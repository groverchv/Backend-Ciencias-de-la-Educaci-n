package com.uagrm.ciencias_de_la_educacion.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@Slf4j
public class BackupService {

    private final WebSocketService webSocketService;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${backup.directory:./backups}")
    private String backupDirectory;
    
    @Value("${backup.retention.days:30}")
    private int retentionDays;

    public BackupService(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    /**
     * Backup automático programado a las 00:00 horario Bolivia (GMT-4)
     * Cron: segundo minuto hora dia mes dia-semana
     */
    @Scheduled(cron = "0 0 0 * * ?", zone = "America/La_Paz")
    public void scheduledBackup() {
        log.info("Iniciando backup automático programado...");
        try {
            String backupPath = createBackup();
            log.info("Backup automático completado exitosamente: {}", backupPath);
            webSocketService.notifyBackupCompleted("Backup automático completado: " + backupPath);
            
            // Limpiar backups antiguos
            cleanOldBackups();
        } catch (Exception e) {
            log.error("Error en backup automático: {}", e.getMessage(), e);
            webSocketService.notifyBackupCompleted("Error en backup automático: " + e.getMessage());
        }
    }
    
    /**
     * Limpiar backups antiguos según política de retención
     */
    public void cleanOldBackups() {
        try {
            Path backupDir = Paths.get(backupDirectory);
            if (!Files.exists(backupDir)) {
                return;
            }
            
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(retentionDays);
            
            try (Stream<Path> files = Files.list(backupDir)) {
                files.filter(path -> path.toString().endsWith(".sql"))
                     .filter(path -> {
                         try {
                             return Files.getLastModifiedTime(path)
                                        .toInstant()
                                        .isBefore(cutoffDate.atZone(java.time.ZoneId.systemDefault()).toInstant());
                         } catch (IOException e) {
                             return false;
                         }
                     })
                     .forEach(path -> {
                         try {
                             Files.delete(path);
                             log.info("Backup antiguo eliminado: {}", path.getFileName());
                         } catch (IOException e) {
                             log.error("Error al eliminar backup antiguo: {}", path, e);
                         }
                     });
            }
        } catch (Exception e) {
            log.error("Error al limpiar backups antiguos: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Listar todos los backups disponibles
     */
    public List<BackupInfo> listBackups() throws IOException {
        List<BackupInfo> backups = new ArrayList<>();
        Path backupDir = Paths.get(backupDirectory);
        
        if (!Files.exists(backupDir)) {
            log.info("Directorio de backups no existe, creándolo: {}", backupDir.toAbsolutePath());
            Files.createDirectories(backupDir);
            return backups;
        }
        
        try (Stream<Path> files = Files.list(backupDir)) {
            files.filter(path -> path.toString().endsWith(".sql"))
                 .forEach(path -> {
                     try {
                         BackupInfo info = new BackupInfo();
                         info.setFileName(path.getFileName().toString());
                         info.setFilePath(path.toString());
                         info.setSize(Files.size(path));
                         info.setCreatedDate(Files.getLastModifiedTime(path).toInstant()
                                 .atZone(java.time.ZoneId.systemDefault())
                                 .toLocalDateTime());
                         backups.add(info);
                     } catch (IOException e) {
                         log.error("Error al leer información del backup: {}", path, e);
                     }
                 });
        }
        
        // Ordenar por fecha descendente (más reciente primero)
        backups.sort(Comparator.comparing(BackupInfo::getCreatedDate).reversed());
        
        return backups;
    }
    
    /**
     * Clase interna para información de backup
     */
    public static class BackupInfo {
        private String fileName;
        private String filePath;
        private long size;
        private LocalDateTime createdDate;
        
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        
        public String getFilePath() { return filePath; }
        public void setFilePath(String filePath) { this.filePath = filePath; }
        
        public long getSize() { return size; }
        public void setSize(long size) { this.size = size; }
        
        public LocalDateTime getCreatedDate() { return createdDate; }
        public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
    }

    /**
     * Crear backup manual de la base de datos
     */
    public String createBackup() throws IOException, InterruptedException {
        log.info("Iniciando creación de backup...");
        
        // Validar configuración
        if (dbUrl == null || dbUrl.isEmpty()) {
            throw new IOException("URL de base de datos no configurada");
        }
        if (dbUsername == null || dbUsername.isEmpty()) {
            throw new IOException("Usuario de base de datos no configurado");
        }
        
        // Crear directorio de backups si no existe
        Path backupDir = Paths.get(backupDirectory);
        if (!Files.exists(backupDir)) {
            log.info("Creando directorio de backups: {}", backupDir.toAbsolutePath());
            Files.createDirectories(backupDir);
        }

        // Generar nombre de archivo con timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = "backup_" + timestamp + ".sql";
        Path backupFile = backupDir.resolve(fileName);

        // Extraer información de conexión de la URL
        String dbName = extractDatabaseName(dbUrl);
        String host = extractHost(dbUrl);
        String port = extractPort(dbUrl);
        
        log.info("Creando backup de BD: {} en {}:{}", dbName, host, port);

        // Comando pg_dump para PostgreSQL
        ProcessBuilder processBuilder = new ProcessBuilder(
            "pg_dump",
            "-h", host,
            "-p", port,
            "-U", dbUsername,
            "-F", "c", // formato custom (comprimido)
            "-b", // incluir blobs
            "-v", // verbose
            "-f", backupFile.toString(),
            dbName
        );

        // Configurar la contraseña como variable de entorno
        processBuilder.environment().put("PGPASSWORD", dbPassword);
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();
        
        // Leer la salida del proceso
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                log.debug("pg_dump: {}", line);
                output.append(line).append("\n");
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            String errorMsg = "pg_dump falló con código de salida: " + exitCode + "\nOutput: " + output.toString();
            log.error(errorMsg);
            throw new IOException(errorMsg);
        }
        
        // Verificar que el archivo se creó y tiene contenido
        if (!Files.exists(backupFile)) {
            throw new IOException("El archivo de backup no se creó: " + backupFile);
        }
        
        long fileSize = Files.size(backupFile);
        if (fileSize == 0) {
            throw new IOException("El archivo de backup está vacío");
        }
        
        log.info("Backup creado exitosamente: {} ({} bytes)", backupFile.toString(), fileSize);
        return backupFile.toString();
    }

    /**
     * Crear backup y devolver bytes para descarga
     */
    public byte[] createBackupForDownload() throws IOException, InterruptedException {
        String backupPath = createBackup();
        byte[] data = Files.readAllBytes(Paths.get(backupPath));
        
        if (data == null || data.length == 0) {
            throw new IOException("El archivo de backup está vacío después de leerlo");
        }
        
        log.info("Backup leído para descarga: {} bytes", data.length);
        return data;
    }

    /**
     * Restaurar base de datos desde un archivo de backup
     */
    public void restoreBackup(byte[] backupData) throws IOException, InterruptedException {
        if (backupData == null || backupData.length == 0) {
            throw new IOException("Los datos de backup están vacíos");
        }
        
        log.info("Iniciando restauración de {} bytes", backupData.length);
        
        // Crear archivo temporal
        Path tempFile = Files.createTempFile("restore_", ".sql");
        Files.write(tempFile, backupData);
        
        log.info("Archivo temporal creado: {}", tempFile);

        try {
            restoreFromFile(tempFile.toString());
        } finally {
            // Eliminar archivo temporal
            boolean deleted = Files.deleteIfExists(tempFile);
            log.info("Archivo temporal eliminado: {}", deleted);
        }
    }

    /**
     * Restaurar desde un archivo local
     */
    public void restoreFromFile(String backupFilePath) throws IOException, InterruptedException {
        log.info("Iniciando restauración desde archivo: {}", backupFilePath);
        
        // Validar que el archivo existe
        Path filePath = Paths.get(backupFilePath);
        if (!Files.exists(filePath)) {
            throw new IOException("El archivo de backup no existe: " + backupFilePath);
        }
        
        if (Files.size(filePath) == 0) {
            throw new IOException("El archivo de backup está vacío: " + backupFilePath);
        }
        
        String dbName = extractDatabaseName(dbUrl);
        String host = extractHost(dbUrl);
        String port = extractPort(dbUrl);
        
        log.info("Restaurando a BD: {} en {}:{}", dbName, host, port);

        // Comando pg_restore para PostgreSQL
        ProcessBuilder processBuilder = new ProcessBuilder(
            "pg_restore",
            "-h", host,
            "-p", port,
            "-U", dbUsername,
            "-d", dbName,
            "-c", // limpiar (drop) objetos antes de recrearlos
            "-v", // verbose
            backupFilePath
        );

        processBuilder.environment().put("PGPASSWORD", dbPassword);
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                log.debug("pg_restore: {}", line);
                output.append(line).append("\n");
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            String errorMsg = "pg_restore falló con código de salida: " + exitCode + "\nOutput: " + output.toString();
            log.error(errorMsg);
            throw new IOException(errorMsg);
        }

        log.info("Base de datos restaurada exitosamente desde: {}", backupFilePath);
    }

    private String extractDatabaseName(String url) {
        // jdbc:postgresql://localhost:5432/nombre_db
        return url.substring(url.lastIndexOf("/") + 1).split("\\?")[0];
    }

    private String extractHost(String url) {
        // jdbc:postgresql://localhost:5432/nombre_db
        String[] parts = url.split("//")[1].split(":");
        return parts[0];
    }

    private String extractPort(String url) {
        // jdbc:postgresql://localhost:5432/nombre_db
        String[] parts = url.split("//")[1].split(":");
        if (parts.length > 1) {
            return parts[1].split("/")[0];
        }
        return "5432"; // puerto por defecto
    }
}

package com.uagrm.ciencias_de_la_educacion.Controller;

import com.uagrm.ciencias_de_la_educacion.Service.BackupService;
import com.uagrm.ciencias_de_la_educacion.Service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/backup")
@Slf4j
public class BackupController {

    private final BackupService backupService;
    private final WebSocketService webSocketService;

    public BackupController(BackupService backupService, WebSocketService webSocketService) {
        this.backupService = backupService;
        this.webSocketService = webSocketService;
    }

    /**
     * Crear y descargar backup manual
     * Solo administradores pueden crear backups
     */
    @GetMapping("/download")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> downloadBackup() {
        try {
            log.info("Iniciando creación de backup manual...");
            
            byte[] backupData = backupService.createBackupForDownload();
            
            if (backupData == null || backupData.length == 0) {
                log.error("Backup generado está vacío");
                Map<String, String> error = new HashMap<>();
                error.put("error", "El backup generado está vacío");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
            }
            
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = "backup_" + timestamp + ".sql";
            
            ByteArrayResource resource = new ByteArrayResource(backupData);
            
            // Notificar por WebSocket
            webSocketService.notifyBackupCompleted("Backup manual creado exitosamente: " + fileName);
            
            log.info("Backup manual creado exitosamente: {} ({} bytes)", fileName, backupData.length);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(backupData.length)
                    .body(resource);
                    
        } catch (IOException e) {
            log.error("Error de I/O al crear backup: {}", e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al acceder al sistema de archivos: " + e.getMessage());
            webSocketService.notifyBackupCompleted("Error al crear backup: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        } catch (InterruptedException e) {
            log.error("Proceso de backup interrumpido: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Proceso de backup interrumpido");
            webSocketService.notifyBackupCompleted("Error: Proceso de backup interrumpido");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        } catch (Exception e) {
            log.error("Error inesperado al crear backup: {}", e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error inesperado: " + e.getMessage());
            webSocketService.notifyBackupCompleted("Error al crear backup: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Restaurar base de datos desde un archivo
     * Solo administradores pueden restaurar
     */
    @PostMapping("/restore")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> restoreBackup(@RequestParam("file") MultipartFile file) {
        Map<String, String> response = new HashMap<>();
        
        try {
            log.info("Iniciando restauración de backup desde archivo: {}", file.getOriginalFilename());
            
            // Validaciones
            if (file == null || file.isEmpty()) {
                response.put("error", "No se proporcionó ningún archivo o el archivo está vacío");
                log.warn("Intento de restauración sin archivo válido");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Validar extensión del archivo
            String fileName = file.getOriginalFilename();
            if (fileName == null || !fileName.toLowerCase().endsWith(".sql")) {
                response.put("error", "El archivo debe tener extensión .sql");
                log.warn("Intento de restauración con archivo inválido: {}", fileName);
                return ResponseEntity.badRequest().body(response);
            }
            
            // Validar tamaño del archivo (máximo 500MB)
            long maxSize = 500 * 1024 * 1024; // 500MB
            if (file.getSize() > maxSize) {
                response.put("error", "El archivo es demasiado grande. Tamaño máximo: 500MB");
                log.warn("Archivo demasiado grande: {} bytes", file.getSize());
                return ResponseEntity.badRequest().body(response);
            }
            
            byte[] backupData = file.getBytes();
            log.info("Iniciando restauración de {} bytes", backupData.length);
            
            backupService.restoreBackup(backupData);
            
            // Notificar por WebSocket
            webSocketService.notifyBackupCompleted("Base de datos restaurada exitosamente desde: " + fileName);
            
            response.put("message", "Base de datos restaurada exitosamente");
            response.put("fileName", fileName);
            response.put("size", String.valueOf(file.getSize()));
            log.info("Restauración completada exitosamente desde: {}", fileName);
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            log.error("Error de I/O al restaurar backup: {}", e.getMessage(), e);
            response.put("error", "Error al leer el archivo: " + e.getMessage());
            webSocketService.notifyBackupCompleted("Error al restaurar: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (InterruptedException e) {
            log.error("Proceso de restauración interrumpido: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
            response.put("error", "Proceso de restauración interrumpido");
            webSocketService.notifyBackupCompleted("Error: Proceso de restauración interrumpido");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            log.error("Error al restaurar backup: {}", e.getMessage(), e);
            response.put("error", "Error al restaurar: " + e.getMessage());
            webSocketService.notifyBackupCompleted("Error al restaurar: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Verificar estado del servicio de backup
     */
    @GetMapping("/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getBackupStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("service", "running");
        status.put("scheduledBackup", "Diariamente a las 00:00 (Horario Bolivia)");
        status.put("timezone", "America/La_Paz");
        status.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(status);
    }
    
    /**
     * Listar todos los backups disponibles
     */
    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> listBackups() {
        try {
            var backups = backupService.listBackups();
            return ResponseEntity.ok(backups);
        } catch (Exception e) {
            log.error("Error al listar backups: {}", e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al listar backups: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * Descargar un backup específico por nombre
     */
    @GetMapping("/download/{fileName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> downloadSpecificBackup(@PathVariable String fileName) {
        try {
            log.info("Descargando backup: {}", fileName);
            
            // Validar nombre de archivo
            if (!fileName.endsWith(".sql")) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Nombre de archivo inválido");
                return ResponseEntity.badRequest().body(error);
            }
            
            Path backupFile = Paths.get(backupService.listBackups().stream()
                .filter(b -> b.getFileName().equals(fileName))
                .findFirst()
                .orElseThrow(() -> new IOException("Backup no encontrado"))
                .getFilePath());
            
            byte[] backupData = Files.readAllBytes(backupFile);
            ByteArrayResource resource = new ByteArrayResource(backupData);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(backupData.length)
                    .body(resource);
                    
        } catch (IOException e) {
            log.error("Error al descargar backup: {}", e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al descargar backup: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            log.error("Error inesperado: {}", e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error inesperado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * Eliminar un backup específico
     */
    @DeleteMapping("/{fileName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteBackup(@PathVariable String fileName) {
        Map<String, String> response = new HashMap<>();
        try {
            log.info("Eliminando backup: {}", fileName);
            
            // Validar nombre de archivo
            if (!fileName.endsWith(".sql")) {
                response.put("error", "Nombre de archivo inválido");
                return ResponseEntity.badRequest().body(response);
            }
            
            Path backupFile = Paths.get(backupService.listBackups().stream()
                .filter(b -> b.getFileName().equals(fileName))
                .findFirst()
                .orElseThrow(() -> new IOException("Backup no encontrado"))
                .getFilePath());
            
            Files.delete(backupFile);
            
            response.put("message", "Backup eliminado exitosamente");
            response.put("fileName", fileName);
            log.info("Backup eliminado: {}", fileName);
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            log.error("Error al eliminar backup: {}", e.getMessage(), e);
            response.put("error", "Error al eliminar backup: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            log.error("Error inesperado: {}", e.getMessage(), e);
            response.put("error", "Error inesperado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

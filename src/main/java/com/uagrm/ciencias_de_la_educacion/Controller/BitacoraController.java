package com.uagrm.ciencias_de_la_educacion.Controller;

import java.time.LocalDateTime; // Importar
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // Importar
import org.springframework.http.ResponseEntity; // Importar
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uagrm.ciencias_de_la_educacion.Model.BitacoraEntity;
import com.uagrm.ciencias_de_la_educacion.Service.BitacoraService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; // Importar
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.servlet.http.HttpServletRequest; // Importar

@RestController
@RequestMapping("/api/bitacora")
public class BitacoraController {

    @Autowired
    private BitacoraService bitacoraService;

    @GetMapping("")
    public ResponseEntity<List<BitacoraEntity>> getBitacora() {
        List<BitacoraEntity> bitacoras = bitacoraService.getAllBitacoras();

        return new ResponseEntity<>(bitacoras, HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<BitacoraEntity> crearBitacora(@RequestBody BitacoraEntity bitacora,
            HttpServletRequest request) {
        bitacora.setIp_origen(getIpCliente(request));
        bitacora.setFecha(LocalDateTime.now());

        BitacoraEntity nuevaBitacora = bitacoraService.saveBitacora(bitacora);

        return new ResponseEntity<>(nuevaBitacora, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarBitacora(@PathVariable Long id) {

        bitacoraService.deleteBitacora(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private String getIpCliente(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        } else {

            ipAddress = ipAddress.split(",")[0].trim();
        }
        return ipAddress;
    }
}
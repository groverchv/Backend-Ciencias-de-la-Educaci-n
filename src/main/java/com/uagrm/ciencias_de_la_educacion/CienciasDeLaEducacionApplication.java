package com.uagrm.ciencias_de_la_educacion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class CienciasDeLaEducacionApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(CienciasDeLaEducacionApplication.class, args);
        } catch (Exception e) {
            System.err.println("Error durante la ejecución de la aplicación: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @ControllerAdvice
    @RestController
    public static class GlobalExceptionHandler {

        @ExceptionHandler(Exception.class)
        public String handleException(Exception e) {
            System.err.println("Excepción capturada: " + e.getMessage());
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}

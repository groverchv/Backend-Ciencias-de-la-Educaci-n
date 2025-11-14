-- Script para limpiar y recrear la base de datos
-- Ejecuta esto en pgAdmin o en tu cliente de PostgreSQL

-- Desconectar todas las sesiones activas
SELECT pg_terminate_backend(pg_stat_activity.pid)
FROM pg_stat_activity
WHERE pg_stat_activity.datname = 'ciencias-de-la-educacion'
  AND pid <> pg_backend_pid();

-- Eliminar la base de datos si existe
DROP DATABASE IF EXISTS "ciencias-de-la-educacion";

-- Crear la base de datos nuevamente
CREATE DATABASE "ciencias-de-la-educacion"
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'Spanish_Bolivia.1252'
    LC_CTYPE = 'Spanish_Bolivia.1252'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

-- Mensaje de confirmación
-- La base de datos ha sido recreada exitosamente
-- Ahora puedes iniciar el backend con: mvn spring-boot:run

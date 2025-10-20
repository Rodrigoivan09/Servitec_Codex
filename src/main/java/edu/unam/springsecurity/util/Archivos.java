package edu.unam.springsecurity.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public final class Archivos {

    private static final Logger log = LoggerFactory.getLogger(Archivos.class);

    private Archivos() {
        // Utility class
    }

    public static String almacenar(MultipartFile multipartFile, String rutaBase) {
        try {
            Path base = prepareBaseDirectory(rutaBase);
            Path destino = resolveSafeTarget(base, multipartFile.getOriginalFilename());
            writeFile(multipartFile, destino);
            return destino.getFileName().toString();
        } catch (IOException | IllegalArgumentException e) {
            log.error("Error al almacenar archivo en '{}': {}", rutaBase, e.getMessage(), e);
            throw new IllegalStateException("No fue posible almacenar el archivo", e);
        }
    }

    public static void renombrar(String rutaBase, String nombreAnterior, String nombreNuevo) {
        Path base = prepareBaseDirectory(rutaBase);
        Path origen = resolveSafeTarget(base, nombreAnterior);
        Path destino = resolveSafeTarget(base, nombreNuevo);
        try {
            Files.move(origen, destino, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("No se pudo renombrar '{}' a '{}' en '{}'", nombreAnterior, nombreNuevo, rutaBase, e);
            throw new IllegalStateException("No fue posible renombrar el archivo", e);
        }
    }

    public static String almacenarConNombre(MultipartFile archivo, String rutaBase, String nombreFinal) {
        try {
            Path base = prepareBaseDirectory(rutaBase);
            Path destino = resolveSafeTarget(base, nombreFinal);
            writeFile(archivo, destino);
            return destino.getFileName().toString();
        } catch (IOException | IllegalArgumentException e) {
            log.error("Error al almacenar archivo '{}' en '{}': {}", nombreFinal, rutaBase, e.getMessage(), e);
            throw new IllegalStateException("No fue posible almacenar el archivo con el nombre solicitado", e);
        }
    }

    public static String obtenerExtension(String nombreArchivo) {
        if (nombreArchivo == null || !nombreArchivo.contains(".")) {
            return "";
        }
        String cleaned = sanitizeFileName(nombreArchivo);
        int lastDot = cleaned.lastIndexOf('.');
        return lastDot >= 0 ? cleaned.substring(lastDot + 1) : "";
    }

    private static Path prepareBaseDirectory(String rutaBase) {
        if (rutaBase == null || rutaBase.isBlank()) {
            throw new IllegalArgumentException("La ruta base no puede estar vacía");
        }
        Path base = Paths.get(rutaBase).toAbsolutePath().normalize();
        try {
            Files.createDirectories(base);
        } catch (IOException e) {
            throw new IllegalStateException("No fue posible preparar el directorio destino", e);
        }
        return base;
    }

    private static Path resolveSafeTarget(Path base, String nombreOriginal) {
        String sanitized = sanitizeFileName(nombreOriginal);
        Path destino = base.resolve(sanitized).normalize();
        if (!destino.startsWith(base)) {
            throw new IllegalArgumentException("Intento de escritura fuera del directorio permitido");
        }
        return destino;
    }

    private static void writeFile(MultipartFile archivo, Path destino) throws IOException {
        try (InputStream inputStream = archivo.getInputStream()) {
            Files.copy(inputStream, destino, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static String sanitizeFileName(String nombreArchivo) {
        if (nombreArchivo == null) {
            throw new IllegalArgumentException("El nombre del archivo no puede ser nulo");
        }
        String cleaned = StringUtils.cleanPath(nombreArchivo.trim());
        if (cleaned.isBlank()) {
            throw new IllegalArgumentException("El nombre del archivo no puede estar vacío");
        }
        if (cleaned.contains("..") || cleaned.contains("/") || cleaned.contains("\\") || cleaned.startsWith(".")) {
            throw new IllegalArgumentException("Nombre de archivo inválido");
        }
        return cleaned;
    }
}

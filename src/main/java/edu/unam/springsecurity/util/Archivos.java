package edu.unam.springsecurity.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Archivos {
    public static String almacenar(MultipartFile multipartFile,String direccion){
        String nombre=multipartFile.getOriginalFilename();
        Path path= Paths.get(direccion);
        direccion=path.toFile().getAbsolutePath();

        File imagen=new File(direccion+"/"+nombre);
        Path finalRuta=Paths.get(imagen.getAbsolutePath());
        try {
            byte[] bytes=multipartFile.getBytes();
            Files.write(finalRuta,bytes);
            return nombre;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al subir archivo");
            return null;
        }
    }
    public static void renombrar(String direccion,String viejo,String nuevo){
        File archivoViejo=new File(direccion+"/"+viejo);
        File archivoNuevo=new File(direccion+"/"+nuevo);
        archivoViejo.renameTo(archivoNuevo);
    }

    public static String almacenarConNombre(MultipartFile archivo, String rutaBase, String nombreFinal) {
        try {
            Path rutaDirectorio = Paths.get(rutaBase);
            if (!Files.exists(rutaDirectorio)) {
                Files.createDirectories(rutaDirectorio);
            }

            Path rutaArchivo = rutaDirectorio.resolve(nombreFinal);
            Files.copy(archivo.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);

            return nombreFinal;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String obtenerExtension(String nombreArchivo) {
        return nombreArchivo.substring(nombreArchivo.lastIndexOf('.') + 1);
    }


}
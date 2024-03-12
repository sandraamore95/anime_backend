package com.appanime.appanime.services;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

@Service
public class UtilService {

    public void eliminarArchivosDirectorio(Path directoryPath,String name) throws IOException {
        if (Files.exists(directoryPath)) {
            // Eliminar la imagen si existe dentro del directorio
            Path imagePath = directoryPath.resolve(name); //image.jpg or avatar.png
            if (Files.exists(imagePath)) {
                Files.delete(imagePath);
                System.out.println("Imagen eliminada correctamente");
            }

            // Eliminar el directorio y su contenido
            Files.walkFileTree(directoryPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file); // Elimina archivos
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir); // Elimina directorios
                    return FileVisitResult.CONTINUE;
                }
            });
            System.out.println("Directorio y su contenido eliminados correctamente");
        } else {
            System.out.println("El directorio no existe");
        }
    }

}

package es.ucm.fdi.iw.util;

import javax.imageio.ImageIO;

import lombok.experimental.StandardException;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class ImageConverter {

    private static final String SUPPORTED_FORMAT = "webp"; // Formato de destino

    // Función que lee la imagen y la convierte al formato deseado
    public static BufferedImage readAndConvertImage(InputStream imageStream, Path destinationPath)
            throws IOException, UnsupportedImageException, ImageConversionException {
        // Leer la imagen desde el InputStream
        BufferedImage bufferedImage = ImageIO.read(imageStream);
        if (bufferedImage == null) {
            throw new UnsupportedImageException("La imagen no es válida o no es soportada");
        }

        // Guardar la imagen en el formato deseado (webp)
        try {
            ImageIO.write(bufferedImage, SUPPORTED_FORMAT, destinationPath.toFile());
        } catch (IOException e) {
            throw new ImageConversionException("Error al convertir la imagen a " + SUPPORTED_FORMAT, e);
        }

        return bufferedImage;
    }

    @StandardException
    public static class UnsupportedImageException extends Exception {

    }

    @StandardException
    public static class ImageConversionException extends Exception {

    }
}

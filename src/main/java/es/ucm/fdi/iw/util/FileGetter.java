package es.ucm.fdi.iw.util;

import java.io.File;

/**
 * Funcionalidad para obtener un archivo a partir de un id
 * Implementar la interfaz para obtener un archivo a partir de un id
 */
@FunctionalInterface
public interface FileGetter {
    File get() throws IllegalArgumentException, NoDataException;
}

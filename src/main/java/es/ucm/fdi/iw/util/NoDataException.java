package es.ucm.fdi.iw.util;

import lombok.experimental.StandardException;

// Excepcion usada para comunicar que no existe un archivo de una cancion si
// existente en BD
@StandardException
public class NoDataException extends Exception {

}

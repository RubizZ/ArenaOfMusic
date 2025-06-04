package es.ucm.fdi.iw.util;

import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FFmpegLogCallback;
import org.bytedeco.javacv.Frame;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacpp.Loader;
import org.springframework.web.multipart.MultipartFile;

import lombok.experimental.StandardException;

import java.io.File;
import java.io.IOException;

/**
 * Clase auxiliar para manejar archivos de audio
 * Generada por IA (ChatGPT, DeepSeek y Claude)
 */
public class AudioConverter {
    static {
        // Cargar las bibliotecas nativas de FFmpeg
        FFmpegLogCallback.set();
        avutil.av_log_set_level(avutil.AV_LOG_ERROR);
        Loader.load(avcodec.class);
    }

    /**
     * Convierte un {@link MultipartFile} a opus y lo escribe en {@code outputFile},
     * eliminando todos los metadatos del archivo
     * 
     * @param inputFile
     * @param outputFile
     * @throws IOException
     */
    public static void convertToOpus(MultipartFile inputFile, File outputFile)
            throws AudioConversionException, IOException {
        // Guardar el archivo de entrada en un archivo temporal
        File tempInputFile = File.createTempFile("input-", ".tmp");
        inputFile.transferTo(tempInputFile);

        try {
            // Convertir el archivo temporal a opus
            convertToOpus(tempInputFile.getAbsolutePath(), outputFile.getAbsolutePath());
        } finally {
            // Eliminar el archivo temporal de entrada
            tempInputFile.delete();
        }
    }

    /**
     * Convierte el archivo en la ruta {@code inputFilePath} a opus y lo coloca en
     * la
     * ruta {@code outputFilePath}
     * 
     * @param inputFilePath
     * @param outputFilePath
     * @throws Exception
     */
    public static void convertToOpus(String inputFilePath, String outputFilePath) throws AudioConversionException {
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFilePath);
        FFmpegFrameRecorder recorder = null;
        try {
            try {
                grabber.start();

                recorder = new FFmpegFrameRecorder(outputFilePath, grabber.getAudioChannels());
                recorder.setFormat("ogg");
                recorder.setSampleRate(grabber.getSampleRate());
                recorder.setSampleRate(48000);
                recorder.setAudioCodec(avcodec.AV_CODEC_ID_OPUS);

                // Start the recorder
                recorder.start();

                // Only process audio frames
                Frame audioFrame;
                while ((audioFrame = grabber.grabSamples()) != null) {
                    recorder.recordSamples(audioFrame.sampleRate, audioFrame.audioChannels, audioFrame.samples);
                }

                // Explicitly flush and close the recorder
                recorder.stop();

            } finally {
                if (grabber != null) {
                    grabber.stop();
                    grabber.close();
                }
                if (recorder != null) {
                    recorder.close();
                }
            }
        } catch (Exception e) {
            throw new AudioConversionException("Error al convertir a opus", e);
        }
    }

    @StandardException
    public static class AudioConversionException extends Exception {
    }
}
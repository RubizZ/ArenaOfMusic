package es.ucm.fdi.iw;

import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avformat;
import org.bytedeco.javacpp.Loader;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * Clase auxiliar para manejar archivos de audio
 * Generada por IA (ChatGPT, DeepSeek y Claude)
 */
public class AudioConverter {
    static {
        // Cargar las bibliotecas nativas de FFmpeg
        Loader.load(avcodec.class);
    }

    /**
     * Convierte un {@link MultipartFile} a mp3 y lo escribe en {@code outputFile},
     * eliminando todos los metadatos del archivo
     * 
     * @param inputFile
     * @param outputFile
     * @throws IOException
     */
    public static void convertToMP3(MultipartFile inputFile, File outputFile) throws IOException {
        // Guardar el archivo de entrada en un archivo temporal
        File tempInputFile = File.createTempFile("input-", ".tmp");
        inputFile.transferTo(tempInputFile);

        try {
            // Convertir el archivo temporal a MP3
            convertToMP3(tempInputFile.getAbsolutePath(), outputFile.getAbsolutePath());

        } catch (Exception e) {
            throw new RuntimeException("Error al convertir a mp3", e);
        } finally {
            // Eliminar el archivo temporal de entrada
            tempInputFile.delete();
        }
    }

    /**
     * Convierte el archivo en la ruta {@code inputFilePath} a mp3 y lo coloca en la
     * ruta {@code outputFilePath}
     * 
     * @param inputFilePath
     * @param outputFilePath
     * @throws Exception
     */
    public static void convertToMP3(String inputFilePath, String outputFilePath) throws Exception {
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFilePath);
        FFmpegFrameRecorder recorder = null;
        try {
            grabber.start();

            recorder = new FFmpegFrameRecorder(outputFilePath, grabber.getAudioChannels());
            recorder.setFormat("mp3");
            recorder.setSampleRate(grabber.getSampleRate());
            recorder.setAudioBitrate(192000);
            recorder.setAudioCodec(avcodec.AV_CODEC_ID_MP3);

            // Start the recorder
            recorder.start();

            Frame frame;
            // Only process audio frames
            while ((frame = grabber.grab()) != null) {
                if (frame.samples != null) { // Check if it's an audio frame
                    recorder.record(frame);
                }
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
    }
}
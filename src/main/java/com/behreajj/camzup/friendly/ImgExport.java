package com.behreajj.camzup.friendly;

import com.behreajj.camzup.core.Img;
import com.behreajj.camzup.core.Rgb;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Facilitates the conversion of {@link com.behreajj.camzup.core.Img}s to
 * {@link java.awt.image.BufferedImage}s and their subsequent export.
 */
public abstract class ImgExport {

    /**
     * Default setting for whether to premultiply color by alpha.
     */
    public static final boolean DEFAULT_USE_PREMUL = false;

    /**
     * Discourage overriding with a private constructor.
     */
    private ImgExport() {
        // TODO: Change class name to ImgIo
        // TODO: Support loading files to Img?
        // TODO: Convert from native java.awt.Image to Img?
    }

    /**
     * Converts an image to a {@link java.awt.image.BufferedImage}.
     *
     * @param img the image
     * @return the AWT image
     */
    public static BufferedImage toAwtImage(final Img img) {

        return ImgExport.toAwtImage(img, new Rgb.ToneMapClamp(),
            ImgExport.DEFAULT_USE_PREMUL);
    }

    /**
     * Converts an image to a {@link java.awt.image.BufferedImage}.
     *
     * @param img       the image
     * @param mapFunc   the mapping function
     * @param usePremul multiply color by alpha
     * @return the AWT image
     */
    public static BufferedImage toAwtImage(
        final Img img,
        final Rgb.AbstrToneMap mapFunc,
        final boolean usePremul) {

        final int w = img.getWidth();
        final int h = img.getHeight();
        final int[] px = Img.toArgb32(img, mapFunc, usePremul);

        final BufferedImage imgAwt = new BufferedImage(
            w, h, BufferedImage.TYPE_INT_ARGB);
        imgAwt.getRaster().setDataElements(0, 0, w, h, px);

        return imgAwt;
    }

    /**
     * Saves an image to a file path.
     *
     * @param path the file path
     * @param img  the image
     * @return the success condition
     */
    public static boolean saveAs(final String path, final Img img) {

        return ImgExport.saveAs(path, img, new Rgb.ToneMapClamp(),
            ImgExport.DEFAULT_USE_PREMUL);
    }

    /**
     * Saves an image to a file path.
     *
     * @param path      the file path
     * @param img       the image
     * @param mapFunc   the tone mapping function
     * @param usePremul multiply color by alpha
     * @return the success condition
     */
    public static boolean saveAs(
        final String path,
        final Img img,
        final Rgb.AbstrToneMap mapFunc,
        final boolean usePremul) {

        boolean success = false;
        try {
            final String lcFileExt = path.substring(
                path.lastIndexOf('.') + 1).toLowerCase();

            final boolean isBmp = lcFileExt.equals("bmp");
            final boolean isGif = lcFileExt.equals("gif");
            final boolean isPpm = lcFileExt.equals("ppm")
                || lcFileExt.equals("pnm");
            final boolean isPng = lcFileExt.equals("png");
            final boolean isJpg = lcFileExt.equals("jpg")
                || lcFileExt.equals("jpeg");
            final boolean isTif = lcFileExt.equals("tif")
                || lcFileExt.equals("tiff");

            final boolean isSupported = isBmp
                || isPpm
                || isGif
                || isPng
                || isJpg
                || isTif;

            if (!isSupported) {
                throw new UnsupportedOperationException(
                    "Unsupported file extension.");
            }

            final File file = new File(path);
            if (isPpm) {
                final byte[] arr = Img.toPpmBytes(img, mapFunc,
                    Img.PpmFormat.BINARY, Img.DEFAULT_PPM_LEVELS, usePremul);
                try (final FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(arr);
                }
            } else {
                final BufferedImage imgNtv = ImgExport.toAwtImage(
                    img, mapFunc, usePremul);
                ImageIO.write(imgNtv, lcFileExt, file);
            }

            success = true;
        } catch (final Exception e) {
            // noinspection CallToPrintStackTrace
            e.printStackTrace();
        }

        return success;
    }

    /**
     * Encodes an image to a file format per its extension, e.g., "png" or
     * "jpg", then returns the encoded image as a byte. Defaults to a binary
     * ppm image if there is an error.
     *
     * @param fileExt the file extension
     * @param img     the image
     * @return the encoded bytes
     */
    public static byte[] toBytes(final String fileExt, final Img img) {
        return ImgExport.toBytes(fileExt, img, new Rgb.ToneMapClamp(),
            ImgExport.DEFAULT_USE_PREMUL);
    }

    /**
     * Encodes an image to a file format per its extension, e.g., "png" or
     * "jpg", then returns the encoded image as a byte. Defaults to a binary
     * ppm image if there is an error.
     *
     * @param fileExt   the file extension
     * @param img       the image
     * @param mapFunc   the tone mapping function
     * @param usePremul multiply color by alpha
     * @return the encoded bytes
     */
    public static byte[] toBytes(
        final String fileExt,
        final Img img,
        final Rgb.AbstrToneMap mapFunc,
        final boolean usePremul) {

        final BufferedImage imgNtv = ImgExport.toAwtImage(img, mapFunc, usePremul);
        final String lcFileExt = fileExt.toLowerCase();
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ImageIO.write(imgNtv, lcFileExt, bos);
            return bos.toByteArray();
        } catch (IOException e) {
            // noinspection CallToPrintStackTrace
            e.printStackTrace();
        }

        return Img.toPpmBytes(img);
    }
}

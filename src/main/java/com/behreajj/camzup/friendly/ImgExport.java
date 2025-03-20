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
     * Converts an image to a {@link java.awt.image.BufferedImage}.
     *
     * @param img the image
     *
     * @return the AWT image
     */
    public static BufferedImage toAwtImage(final Img img) {

        return ImgExport.toAwtImage(img, new Rgb.ToneMapClamp());
    }

    /**
     * Converts an image to a {@link java.awt.image.BufferedImage}.

     * @param img the image
     * @param mapFunc the mapping function
     * @return the AWT image
     */
    public static BufferedImage toAwtImage(
        final Img img,
        final Rgb.AbstrToneMap mapFunc) {

        final int w = img.getWidth();
        final int h = img.getHeight();
        final int[] px = Img.toArgb32(img, mapFunc);

        final BufferedImage imgAwt = new BufferedImage(
            w, h, BufferedImage.TYPE_INT_ARGB);
        imgAwt.getRaster().setDataElements(0, 0, w, h, px);

        return imgAwt;
    }

    /**
     * Encodes an image to a file format per its extension, e.g., "png" or
     * "jpg", then returns the encoded image as a byte. Defaults to a binary
     * ppm image if there is an error.
     *
     * @param fileExt the file extension
     * @param img     the image
     * @param mapFunc the tone mapping function
     * @return the encoded bytes
     */
    public static byte[] toBytes(
        final String fileExt,
        final Img img,
        final Rgb.AbstrToneMap mapFunc) {

        final BufferedImage imgNtv = ImgExport.toAwtImage(img, mapFunc);
        final String lcFileExt = fileExt.toLowerCase();
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ImageIO.write(imgNtv, lcFileExt, bos);
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Img.toPpmBytes(img);
    }

    /**
     * Saves an image to a file path.
     *
     * @param path    the file path
     * @param img     the image
     * @param mapFunc the tone mapping function
     * @return the success condition
     */
    public static boolean saveAs(
        final String path,
        final Img img,
        final Rgb.AbstrToneMap mapFunc) {

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

            final boolean isSupported = isPpm
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
                final byte[] arr = Img.toPpmBytes(
                    img, mapFunc, Img.PpmFormat.BINARY, 256);
                try (final FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(arr);
                }
            } else {
                final BufferedImage imgNtv = ImgExport.toAwtImage(img, mapFunc);
                ImageIO.write(imgNtv, lcFileExt, file);
            }

            success = true;
        } catch (final Exception e) {
            e.printStackTrace();
        }

        return success;
    }

}

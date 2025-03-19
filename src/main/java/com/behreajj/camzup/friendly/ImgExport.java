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
 * Facilitates the export of images.
 */
public abstract class ImgExport {

    /**
     * Formats an image to a byte array.
     *
     * @param fileExt the file extension
     * @param img     the image
     * @param mapFunc the tone mapping function
     * @return the success condition
     */
    public static byte[] toBytes(
        final String fileExt,
        final Img img,
        final Rgb.AbstrToneMap mapFunc) {
        final int w = img.getWidth();
        final int h = img.getHeight();
        final int[] argb32s = Img.toArgb32(img, mapFunc);

        final int format = BufferedImage.TYPE_INT_ARGB;
        final BufferedImage imgNtv = new BufferedImage(w, h, format);
        imgNtv.getRaster().setDataElements(0, 0, w, h, argb32s);

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

            if (isPpm) {
                final byte[] arr = Img.toPpmBytes(
                    img, mapFunc, Img.PpmFormat.BINARY, 256);
                final File file = new File(path);
                try (final FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(arr);
                }
            } else {
                final int w = img.getWidth();
                final int h = img.getHeight();
                final int[] argb32s = Img.toArgb32(img, mapFunc);

                final int format = BufferedImage.TYPE_INT_ARGB;
                final BufferedImage imgNtv = new BufferedImage(w, h, format);
                imgNtv.getRaster().setDataElements(0, 0, w, h, argb32s);
                final File file = new File(path);
                ImageIO.write(imgNtv, lcFileExt, file);
            }

            success = true;
        } catch (final Exception e) {
            e.printStackTrace();
        }

        return success;
    }

}

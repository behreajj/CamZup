package com.behreajj.camzup.core;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Parses an Adobe Swatch Exchange (ase) palette file.
 */
public abstract class ParserAse {

    /**
     * Private constructor for abstract class.
     */
    private ParserAse() {
    }

    /**
     * Parses an .ase file containing an Adobe Swatch Exchange palette.
     *
     * @param arr the byte array
     * @return the palette
     */
    public static Lab[] load(final byte[] arr) {

        final int numBlocks = Utils.intml(arr, 8);
        final int lenFileData = arr.length;
        final ArrayList<Lab> colors = new ArrayList<>(numBlocks);

        final Rgb srgb = new Rgb();
        final Rgb lrgb = new Rgb();
        final Vec4 xyz = new Vec4();

        int i = 12;
        while (i < lenFileData) {
            int blockLen = 2;

            final int blockHeader = Utils.ushort(Utils.shortml(arr, i));
            final boolean isGroupOpen = blockHeader == 0xc001;
            final boolean isGroupClose = blockHeader == 0xc002;
            final boolean isEntry = blockHeader == 0x0001;

            if (isGroupOpen || isEntry || isGroupClose) {

                final int blockLenParse = Utils.intml(arr, i + 2);
                if (blockLenParse > 0) { blockLen = blockLenParse; }
                final short lenChars16 = Utils.shortml(arr, i + 6);

                if (isEntry) {
                    final int iOffset = lenChars16 * 2 + i;
                    final String colorFormat = new String(arr, iOffset + 8, 4, StandardCharsets.UTF_8).toLowerCase();

                    switch (colorFormat) {
                        case "rgb " -> {
                            srgb.set(
                                Utils.floatml(arr, iOffset + 12),
                                Utils.floatml(arr, iOffset + 16),
                                Utils.floatml(arr, iOffset + 20),
                                1.0f);
                            colors.add(Rgb.sRgbToSrLab2(srgb, new Lab(),
                                xyz, lrgb));
                        }
                        case "cmyk" -> {
                            final float c = Utils.floatml(arr, iOffset + 12);
                            final float m = Utils.floatml(arr, iOffset + 16);
                            final float y = Utils.floatml(arr, iOffset + 20);
                            final float k = Utils.floatml(arr, iOffset + 24);

                            final float u = 1.0f - k;
                            srgb.set(
                                (1.0f - c) * u,
                                (1.0f - m) * u,
                                (1.0f - y) * u,
                                1.0f);
                            colors.add(Rgb.sRgbToSrLab2(srgb, new Lab(),
                                xyz, lrgb));
                        }
                        case "lab " -> {
                            final double l = 100.0d * Utils.floatml(arr, iOffset + 12);
                            final double a = Utils.floatml(arr, iOffset + 16);
                            final double b = Utils.floatml(arr, iOffset + 20);

                            double y = (l + 16.0d) * 0.008620689655172414d;
                            double x = a * 0.002d + y;
                            double z = y - b * 0.005d;

                            final double ye3 = y * y * y;
                            final double xe3 = x * x * x;
                            final double ze3 = z * z * z;

                            y = ye3 > 0.008856d ? ye3 : (y - 0.13793103448275862d) * 0.12841751101180157d;
                            x = xe3 > 0.008856d ? xe3 : (x - 0.13793103448275862d) * 0.12841751101180157d;
                            z = ze3 > 0.008856d ? ze3 : (z - 0.13793103448275862d) * 0.12841751101180157d;

                            x *= 0.95047d;
                            z *= 1.08883d;

                            final double r01Linear = 3.2408123d * x - 1.5373085d * y - 0.49858654d * z;
                            final double g01Linear = -0.969243d * x + 1.8759663d * y + 0.041555032d * z;
                            final double b01Linear = 0.0556384d * x - 0.20400746d * y + 1.0571296d * z;

                            lrgb.set(
                                (float)r01Linear,
                                (float)g01Linear,
                                (float)b01Linear,
                                1.0f);
                            Rgb.lRgbToSrXyz(lrgb, xyz);
                            colors.add(Lab.fromSrXyz(xyz, new Lab()));
                        }
                        case "gray" -> {
                            final float v01 = Utils.floatml(arr, iOffset + 12);
                            colors.add(new Lab(v01 * 100.0f, 0.0f, 0.0f, 1.0f));
                        }
                    }
                }
            }

            i += blockLen;
        }

        return colors.toArray(new Lab[0]);
    }

    /**
     * Parses an .ase file containing an Adobe Swatch Exchange palette.
     *
     * @param fileName the file name
     * @return the palette
     */
    public static Lab[] load(final String fileName) {

        Lab[] result = {};
        try {
            final File file = new File(fileName);
            final FileInputStream fis = new FileInputStream(file);
            final byte[] arr = fis.readAllBytes();
            fis.close();
            result = ParserAse.load(arr);
        } catch (final Exception e) {
            // noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
        return result;
    }
}

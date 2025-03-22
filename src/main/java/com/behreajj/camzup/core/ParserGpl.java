package com.behreajj.camzup.core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.TreeMap;
import java.util.regex.Pattern;

/**
 * A <a href="http://gimp.org/">GIMP</a> (GNU Image Manipulation Program) color
 * palette (.gpl)
 * parser class.
 */
public abstract class ParserGpl {

    /**
     * Private constructor for abstract class.
     */
    private ParserGpl() {
    }

    /**
     * Parses a .gpl file containing a GIMP palette. Supports the <a href=
     * "https://github.com/aseprite/aseprite/blob/main/docs/gpl-palette-extension.md">Aseprite
     * extension</a> which includes an alpha channel.
     *
     * @param in the buffered reader
     * @return the palette
     */
    public static Lab[] load(final BufferedReader in) {

        try {

            try (in) {

                final Rgb srgb = new Rgb();
                final Rgb lrgb = new Rgb();
                final Vec4 xyz = new Vec4();

                final TreeMap<Integer, Lab> clrs = new TreeMap<>();
                final Pattern ptrn = Pattern.compile("\\s+");

                int i = 0;
                boolean aseExt = false;

                for (String ln = in.readLine(); ln != null; ln = in.readLine()) {
                    final String lnlc = ln.trim().toLowerCase();

                    /* Implicitly support JASC-PAL, which is similar to GPL. */
                    if (lnlc.indexOf('#') == 0
                        || lnlc.equals("gimp palette")
                        || lnlc.contains("name:")
                        || lnlc.contains("columns:")
                        || lnlc.equals("jasc-pal")
                        || lnlc.equals("0100")) {

                        /* Skip. */

                    } else if (lnlc.equals("channels: rgba")) {

                        aseExt = true;

                    } else {

                        final String[] tokens = ptrn.split(lnlc, 0);
                        final int len = tokens.length;
                        if (len > 2) {

                            /*
                             * In a GPL, tokens 0, 1 and 2 are the RGB channels;
                             * token n - 1 is the optional name; token n - 1 is
                             * the optional index (sometimes).
                             */
                            final float alpha = aseExt
                                ? Float.parseFloat(tokens[3]) * Utils.ONE_255
                                : 1.0f;

                            /*
                             * It's possible that a GPL file could contain
                             * numbers outside the range [0, 255]. Because
                             * colors are unclamped, this doesn't matter.
                             */
                            srgb.set(
                                Float.parseFloat(tokens[0]) * Utils.ONE_255,
                                Float.parseFloat(tokens[1]) * Utils.ONE_255,
                                Float.parseFloat(tokens[2]) * Utils.ONE_255,
                                alpha);
                            final Lab lab = new Lab();
                            Rgb.sRgbToSrLab2(srgb, lab, xyz, lrgb);
                            clrs.put(i, lab);

                            ++i;
                        }
                    }
                }

                return clrs.values().toArray(new Lab[0]);

            } catch (final Exception e) {
                // noinspection CallToPrintStackTrace
                e.printStackTrace();
            }

        } catch (final Exception e) {
            // noinspection CallToPrintStackTrace
            e.printStackTrace();
        }

        return new Lab[0];
    }

    /**
     * Parses a .gpl file containing a GIMP palette.
     *
     * @param fileName the file name
     * @return the palette
     */
    public static Lab[] load(final String fileName) {

        Lab[] result = {};
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            result = ParserGpl.load(br);
        } catch (final Exception e) {
            // noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
        return result;
    }
}

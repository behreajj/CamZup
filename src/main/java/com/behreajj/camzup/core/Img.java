package com.behreajj.camzup.core;

import java.math.BigInteger;
import java.util.*;
import java.util.Map.Entry;

/**
 * An image class for images in the LAB color format. The bytes per pixel is 64,
 * a long. The bytes
 * per channel is 16, a short. The channel format is 0xTTTTLLLLAAAABBBB. The
 * alpha channel is
 * abbreviated to 'T', since 'A' is already taken.
 */
public class Img {

    /**
     * Mask to isolate the A channel.
     */
    public static final long A_MASK = 0x0000_0000_ffff_0000L;

    /**
     * The amount to shift the A channel to the left or right when packing and
     * unpacking a pixel.
     */
    public static final long A_SHIFT = 0x10L;

    /**
     * Mask to isolate the b channel.
     */
    public static final long B_MASK = 0x0000_0000_0000_ffffL;

    /**
     * Mask to isolate the a and b channels.
     */
    public static final long AB_MASK = Img.A_MASK | Img.B_MASK;

    /**
     * The amount to shift the b channel to the left or right when packing and
     * unpacking a pixel.
     */
    public static final long B_SHIFT = 0x00L;

    /**
     * Default color for dark checker squares, or 1.0 / 3.0 of 65535 lightness.
     */
    public static final long CHECKER_DARK = 0xffff_5555_8000_8000L;

    /**
     * Default color for light checker squares, or 2.0 / 3.0 of 65535 lightness.
     */
    public static final long CHECKER_LIGHT = 0xffff_aaaa_8000_8000L;

    /**
     * The value of a pixel with zero light, zero A, zero B and zero alpha.
     * Because A and B are signed floating point numbers that are converted to
     * unsigned shorts, this number is not equal to zero.
     */
    public static final long CLEAR_PIXEL = 0x0000_0000_8000_8000L;

    /**
     * Default alpha value when creating a diagnostic RGB image.
     */
    public static final float DEFAULT_ALPHA = 1.0f;

    /**
     * Default blue value when creating a diagnostic RGB image.
     */
    public static final float DEFAULT_BLUE = 128.0f / 255.0f;

    /**
     * The default blend mode for the a and b channels.
     */
    public static final BlendMode.AB DEFAULT_BM_AB = BlendMode.AB.BLEND;

    /**
     * The default blend mode for the alpha channel.
     */
    public static final BlendMode.Alpha DEFAULT_BM_ALPHA = BlendMode.Alpha.BLEND;

    /**
     * The default blend mode for the lightness channel.
     */
    public static final BlendMode.L DEFAULT_BM_L = BlendMode.L.BLEND;

    /**
     * The default policy on gray colors when adjusting by LCH.
     */
    public static final GrayPolicy DEFAULT_GRAY_POLICY = GrayPolicy.OMIT;

    /**
     * The default height when none is given.
     */
    public static final int DEFAULT_HEIGHT = 128;

    /**
     * Default radius when querying an octree in palette mapping.
     */
    public static final float DEFAULT_OCTREE_QUERY_RADIUS = 175.0f;

    /**
     * Default maximum number for a palette extracted from an image.s
     */
    public static final int DEFAULT_PALETTE_THRESHOLD = 256;

    /**
     * The default policy on pivots when adjusting chroma by a factor.
     */
    public static final PivotPolicy DEFAULT_PIVOT_POLICY = PivotPolicy.MEAN;

    /**
     * The default ppm file format.
     */
    public static final PpmFormat DEFAULT_PPM_FORMAT = PpmFormat.BINARY;

    /**
     * The default width when none is given.
     */
    public static final int DEFAULT_WIDTH = 128;

    /**
     * Mask to isolate the lightness channel.
     */
    public static final long L_MASK = 0x0000_ffff_0000_0000L;

    /**
     * The amount to shift the lightness channel to the left or right when
     * packing and unpacking a pixel.
     */
    public static final long L_SHIFT = 0x20L;

    /**
     * Mask to isolate the l, a and b channels.
     */
    public static final long LAB_MASK = Img.L_MASK | Img.A_MASK | Img.B_MASK;

    /**
     * Max image width and height allowed.
     */
    public static final int MAX_DIMENSION = Short.MAX_VALUE;

    /**
     * The minimum difference between minimum and maximum light needed when
     * normalizing light.
     */
    public static final float MIN_LIGHT_DIFF = 0.07f;

    /**
     * Mask to isolate the alpha channel.
     */
    public static final long T_MASK = 0xffff_0000_0000_0000L;

    /**
     * The amount to shift the alpha channel to the left or right when packing
     * and unpacking a pixel.
     */
    public static final long T_SHIFT = 0x30L;

    /**
     * Mask to isolate the alpha and l channels.
     */
    public static final long TL_MASK = Img.T_MASK | Img.L_MASK;

    /**
     * Mask for all color channels.
     */
    public static final long TLAB_MASK = Img.T_MASK
        | Img.L_MASK
        | Img.A_MASK
        | Img.B_MASK;

    /**
     * The image height in pixels.
     */
    protected int height;

    /**
     * The image pixels.
     */
    protected long[] pixels;

    /**
     * The image width in pixels.
     */
    protected int width;

    /**
     * Constructs an image with a {@link Img#DEFAULT_WIDTH} and
     * {@link Img#DEFAULT_HEIGHT}.
     */
    public Img() {

        // TODO: Is a mask function still necessary? If so, then offer
        // to pull from either the alpha channel or the lightness. Where
        // a mask and shift would be outside the for loop, e.g.,
        // mask = useLight ? L_MASK : T_MASK .

        // TODO: Filter layers to alpha based on response func?
        // Use preset enums instead of a function.s

        this(Img.DEFAULT_WIDTH, Img.DEFAULT_HEIGHT, Img.CLEAR_PIXEL);
    }

    /**
     * Constructs an image from a source.
     *
     * @param source the source image.
     */
    public Img(final Img source) {

        this.width = source.width;
        this.height = source.height;
        this.pixels = source.getPixels();
    }

    /**
     * Constructs an image from width and height. The image is filled with
     * {@link Img#CLEAR_PIXEL}.
     *
     * @param width  the width
     * @param height the height
     */
    public Img(final int width, final int height) {

        this(width, height, Img.CLEAR_PIXEL);
    }

    /**
     * Constructs an image from width and height. The image is filled with the
     * provided color.
     *
     * @param width  the width
     * @param height the height
     * @param fill   the color
     */
    public Img(final int width, final int height, final Lab fill) {

        this(width, height, fill.toHexLongSat());
    }

    /**
     * Constructs an image from width and height. The image is filled with the
     * provided color. The absolute value of width and height are used. Width
     * and height are expected to be at least 1 and at most
     * {@link Img#MAX_DIMENSION}, {@value Img#MAX_DIMENSION}.
     *
     * @param width  the width
     * @param height the height
     * @param fill   the color
     */
    public Img(final int width, final int height, final long fill) {

        this.width = Utils.clamp(Math.abs(width), 1, Img.MAX_DIMENSION);
        this.height = Utils.clamp(Math.abs(height), 1, Img.MAX_DIMENSION);

        final int area = this.width * this.height;
        this.pixels = new long[area];
        for (int i = 0; i < area; ++i) {
            this.pixels[i] = fill;
        }
    }

    /**
     * Constructs a new image with no verification. The pixels array is assigned
     * by reference, not copied by value.
     *
     * @param width  the width
     * @param height the height
     * @param pixels the pixels
     */
    protected Img(final int width, final int height, final long[] pixels) {

        this.width = width;
        this.height = height;
        this.pixels = pixels;
    }

    /**
     * Adjusts an image's lightness and saturation contrast by a factor. The
     * adjustment factor is
     * expected to be in [-1.0, 1.0].
     *
     * @param source the source image
     * @param sFac   the saturation contrast factor
     * @param lFac   the lightness contrast factor
     * @param target the target image
     * @return the adjusted image
     */
    public static Img adjustContrast(
        final Img source,
        final float sFac,
        final float lFac,
        final Img target) {

        return Img.adjustContrast(source, sFac, lFac,
            Img.DEFAULT_PIVOT_POLICY, target);
    }

    /**
     * Adjusts an image's lightness and saturation contrast by a factor. The
     * adjustment factor is
     * expected to be in [-1.0, 1.0]. See <a
     * href="https://en.wikipedia.org/wiki/Colorfulness#Saturation">
     * https://en.wikipedia.org/wiki/Colorfulness#Saturation</a>.
     *
     * @param source the source image
     * @param sFac   the saturation contrast factor
     * @param lFac   the lightness contrast factor
     * @param policy the pivot policy
     * @param target the target image
     * @return the adjusted pixels
     */
    public static Img adjustContrast(
        final Img source,
        final float sFac,
        final float lFac,
        final PivotPolicy policy,
        final Img target) {

        if (!Img.similar(source, target)) {
            target.width = source.width;
            target.height = source.height;
            target.pixels = new long[source.pixels.length];
        }

        final int len = source.pixels.length;
        final float sAdjVerif = Float.isNaN(sFac)
            ? 1.0f
            : 1.0f + Utils.clamp(sFac, -1.0f, 1.0f);
        final float lAdjVerif = Float.isNaN(lFac)
            ? 1.0f
            : 1.0f + Utils.clamp(lFac, -1.0f, 1.0f);

        if (Utils.approx(sAdjVerif, 1.0f) && Utils.approx(lAdjVerif, 1.0f)) {
            System.arraycopy(source.pixels, 0, target.pixels, 0, len);
            return target;
        }

        final HashMap<Long, Lab> uniques = new HashMap<>();

        double minSat = Double.MAX_VALUE;
        double maxSat = -Double.MAX_VALUE;
        double sumSat = 0.0d;

        double minLight = Double.MAX_VALUE;
        double maxLight = -Double.MAX_VALUE;
        double sumLight = 0.0d;

        int sumTally = 0;

        for (int i = 0; i < len; ++i) {
            final long srcPixel = source.pixels[i];
            final Long srcPixelObj = srcPixel;
            if (!uniques.containsKey(srcPixelObj)) {
                final Lab lab = Lab.fromHex(srcPixel, new Lab());
                if (lab.alpha > 0.0f) {
                    final double l = lab.l;
                    final double a = lab.a;
                    final double b = lab.b;

                    final double csq = a * a + b * b;
                    final double mcpl = Math.sqrt(csq + l * l);
                    final double sat = mcpl != 0.0d ? Math.sqrt(csq) / mcpl : 0.0d;

                    if (sat > maxSat) {
                        maxSat = sat;
                    }
                    if (sat < minSat) {
                        minSat = sat;
                    }
                    sumSat += sat;

                    if (l > maxLight) {
                        maxLight = l;
                    }
                    if (l < minLight) {
                        minLight = l;
                    }
                    sumLight += l;

                    ++sumTally;
                }
                uniques.put(srcPixelObj, lab);
            }
        }

        if (sumTally == 0 || minSat >= maxSat && minLight >= maxLight) {
            System.arraycopy(source.pixels, 0, target.pixels, 0, len);
            return target;
        }

        double pivotSat;
        double pivotLight;
        switch (policy) {
            case RANGE:
                pivotSat = (minSat + maxSat) * 0.5d;
                pivotLight = (minLight + maxLight) * 0.5d;
                break;

            case FIXED:
                pivotSat = 0.5d;
                pivotLight = 50.0d;
                break;

            case MEAN:
            default:
                pivotSat = sumSat / sumTally;
                pivotLight = sumLight / sumTally;
        }

        final Lab defLab = Lab.clearBlack(new Lab());
        final HashMap<Long, Long> convert = new HashMap<>();
        convert.put(Img.CLEAR_PIXEL, Img.CLEAR_PIXEL);

        for (int j = 0; j < len; ++j) {
            final Long srcPixelObj = source.pixels[j];
            long trgPixel;

            if (convert.containsKey(srcPixelObj)) {
                trgPixel = convert.get(srcPixelObj);
            } else {
                final Lab lab = uniques.getOrDefault(srcPixelObj, defLab);

                final double lSrc = lab.l;
                final double aSrc = lab.a;
                final double bSrc = lab.b;

                final double lAdj = (lSrc - pivotLight) * (double) lAdjVerif + pivotLight;

                final double csqSrc = aSrc * aSrc + bSrc * bSrc;
                final double cSrc = Math.sqrt(csqSrc);
                final double mcplSrc = Math.sqrt(csqSrc + lSrc * lSrc);
                final double mcplAdj = Math.sqrt(csqSrc + lAdj * lAdj);
                final double sSrc = mcplSrc != 0.0d ? cSrc / mcplSrc : 0.0d;
                // final double sSrc = mcplAdj != 0.0d ? cSrc / mcplAdj : 0.0d;

                final double sAdj = (sSrc - pivotSat) * (double) sAdjVerif + pivotSat;
                final double cAdj = sAdj * mcplAdj;

                final double abScalar = cSrc != 0.0d ? cAdj / cSrc : 0.0d;
                final double aAdj = aSrc * abScalar;
                final double bAdj = bSrc * abScalar;

                lab.l = (float) lAdj;
                lab.a = (float) aAdj;
                lab.b = (float) bAdj;
                trgPixel = lab.toHexLongSat();
                convert.put(srcPixelObj, trgPixel);
            }

            target.pixels[j] = trgPixel;
        }

        return target;
    }

    /**
     * Adjusts an image's lightness and saturation contrast by a factor.
     * The adjustment factor is expected to be in [-1.0, 1.0].
     *
     * @param source the source image
     * @param fac    the contrast factor
     * @param target the target image
     * @return the adjusted image
     */
    public static Img adjustContrast(
        final Img source,
        final float fac,
        final Img target) {

        return Img.adjustContrast(source, fac, fac,
            Img.DEFAULT_PIVOT_POLICY, target);
    }

    /**
     * Adjusts an image's lightness and saturation contrast by a factor.
     * The adjustment factor is expected to be in [-1.0, 1.0].
     *
     * @param source the source image
     * @param fac    the contrast factor
     * @param target the target image
     * @return the adjusted image
     */
    public static Img adjustContrast(
        final Img source,
        final Vec2 fac,
        final Img target) {

        return Img.adjustContrast(source, fac.x, fac.y,
            Img.DEFAULT_PIVOT_POLICY, target);
    }

    /**
     * Adjusts the chroma contrast of colors from a source image by a factor.
     * The adjustment factor is expected to be in [-1.0, 1.0].
     *
     * @param source the source image
     * @param fac    the contrast factor
     * @param target the target image
     * @return the adjusted image
     */
    public static Img adjustContrastChroma(
        final Img source,
        final float fac,
        final Img target) {

        return Img.adjustContrastChroma(source, fac,
            Img.DEFAULT_PIVOT_POLICY, target);
    }

    /**
     * Adjusts an image's chroma contrast by a factor. The adjustment factor is
     * expected to be in [-1.0, 1.0].
     *
     * @param source the source image
     * @param fac    the contrast factor
     * @param policy the pivot policy
     * @param target the target image
     * @return the adjusted image
     */
    public static Img adjustContrastChroma(
        final Img source,
        final float fac,
        final PivotPolicy policy,
        final Img target) {

        final int len = source.pixels.length;
        if (!Img.similar(source, target)) {
            target.width = source.width;
            target.height = source.height;
            target.pixels = new long[len];
        }

        final float adjVerif = Float.isNaN(fac)
            ? 1.0f
            : 1.0f + Utils.clamp(fac, -1.0f, 1.0f);
        if (Utils.approx(adjVerif, 1.0f)) {
            System.arraycopy(source.pixels, 0, target.pixels, 0, len);
            return target;
        }

        final Lab lab = new Lab();
        final HashMap<Long, Lch> uniques = new HashMap<>();

        float minChroma = Float.MAX_VALUE;
        float maxChroma = -Float.MAX_VALUE;
        float sumChroma = 0.0f;
        int sumTally = 0;

        for (int i = 0; i < len; ++i) {
            final long srcPixel = source.pixels[i];
            final Long srcPixelObj = srcPixel;
            if (!uniques.containsKey(srcPixelObj)) {
                Lab.fromHex(srcPixel, lab);
                final Lch lch = Lch.fromLab(lab, new Lch());
                if (lab.alpha > 0.0f) {
                    if (lch.c > maxChroma) {
                        maxChroma = lch.c;
                    }
                    if (lch.c < minChroma) {
                        minChroma = lch.c;
                    }
                    sumChroma += lch.c;
                    ++sumTally;
                }
                uniques.put(srcPixelObj, lch);
            }
        }

        if (sumTally == 0 || minChroma >= maxChroma) {
            System.arraycopy(source.pixels, 0, target.pixels, 0, len);
            return target;
        }

        float pivotChroma = sumChroma / sumTally;
        switch (policy) {
            case RANGE:
                pivotChroma = (minChroma + maxChroma) * 0.5f;
                break;

            case FIXED:
                pivotChroma = Lch.SR_CHROMA_MEAN;
                break;

            case MEAN:
            default:
        }

        final Lch defLch = Lch.clearBlack(new Lch());
        final HashMap<Long, Long> convert = new HashMap<>();
        convert.put(Img.CLEAR_PIXEL, Img.CLEAR_PIXEL);

        for (int j = 0; j < len; ++j) {
            final Long srcPixelObj = source.pixels[j];
            long trgPixel;

            if (convert.containsKey(srcPixelObj)) {
                trgPixel = convert.get(srcPixelObj);
            } else {
                final Lch lch = uniques.getOrDefault(srcPixelObj, defLch);
                lch.c = (lch.c - pivotChroma) * adjVerif + pivotChroma;
                Lab.fromLch(lch, lab);
                trgPixel = lab.toHexLongSat();
                convert.put(srcPixelObj, trgPixel);
            }

            target.pixels[j] = trgPixel;
        }

        return target;
    }

    /**
     * Adjusts an image's light contrast by a factor. The adjustment factor is
     * expected to be in [-1.0, 1.0].
     *
     * @param source the source image
     * @param fac    the contrast factor
     * @param target the target image
     * @return the adjusted image
     */
    public static Img adjustContrastLight(
        final Img source,
        final float fac,
        final Img target) {

        if (!Img.similar(source, target)) {
            target.width = source.width;
            target.height = source.height;
            target.pixels = new long[source.pixels.length];
        }

        final int len = source.pixels.length;
        final float adjVerif = Float.isNaN(fac)
            ? 1.0f
            : 1.0f + Utils.clamp(fac, -1.0f, 1.0f);

        if (Utils.approx(adjVerif, 1.0f)) {
            System.arraycopy(source.pixels, 0, target.pixels, 0, len);
            return target;
        }

        final float pivotLight = 50.0f;
        final Lab lab = new Lab();
        final HashMap<Long, Long> convert = new HashMap<>();
        convert.put(Img.CLEAR_PIXEL, Img.CLEAR_PIXEL);

        for (int i = 0; i < len; ++i) {
            final long srcPixel = source.pixels[i];
            final Long srcPixelObj = srcPixel;
            long trgPixel;

            if (convert.containsKey(srcPixelObj)) {
                trgPixel = convert.get(srcPixelObj);
            } else {
                Lab.fromHex(srcPixel, lab);
                lab.l = (lab.l - pivotLight) * adjVerif + pivotLight;
                trgPixel = lab.toHexLongSat();
                convert.put(srcPixelObj, trgPixel);
            }

            target.pixels[i] = trgPixel;
        }

        return target;
    }

    /**
     * Adjusts a source image's pixels in LAB.
     *
     * @param source the source image
     * @param adjust the adjustment
     * @param target the target image
     * @return the adjusted image
     */
    public static Img adjustLab(
        final Img source,
        final Lab adjust,
        final Img target) {

        /*
         * It's possible to take a long adjust, and then add and subtract
         * 0x8000L for A and B. The problem, though, is the L channel, since an
         * adjustment has to support negative light and negative alpha. You'd
         * have to unpack L in such a way that 0x8000L is a pivot.
         */

        if (!Img.similar(source, target)) {
            target.width = source.width;
            target.height = source.height;
            target.pixels = new long[source.pixels.length];
        }

        final int len = source.pixels.length;

        if (Utils.approx(adjust.l, 0.0f)
            && Utils.approx(adjust.a, 0.0f)
            && Utils.approx(adjust.b, 0.0f)
            && Utils.approx(adjust.alpha, 0.0f)) {

            System.arraycopy(source.pixels, 0, target.pixels, 0, len);
            return target;
        }

        final Lab lab = new Lab();
        final HashMap<Long, Long> convert = new HashMap<>();
        /* Do not put in clear pixel right away, as with other functions. */

        for (int i = 0; i < len; ++i) {
            final long srcPixel = source.pixels[i];
            final Long srcPixelObj = srcPixel;
            long trgPixel;

            if (convert.containsKey(srcPixelObj)) {
                trgPixel = convert.get(srcPixelObj);
            } else {
                Lab.fromHex(srcPixel, lab);
                if (lab.alpha <= 0.0f) {
                    Lab.clearBlack(lab);
                } else {
                    lab.l += adjust.l;
                    lab.a += adjust.a;
                    lab.b += adjust.b;
                    lab.alpha += adjust.alpha;
                }
                trgPixel = lab.toHexLongSat();
                convert.put(srcPixelObj, trgPixel);
            }

            target.pixels[i] = trgPixel;
        }

        return target;
    }

    /**
     * Adjusts a source image's pixels in LCH.
     *
     * @param source the source image
     * @param adjust the adjustment
     * @param policy the gray policy
     * @param target the target image
     * @return the adjusted image
     */
    public static Img adjustLch(
        final Img source,
        final Lch adjust,
        final GrayPolicy policy,
        final Img target) {

        final int len = source.pixels.length;
        if (!Img.similar(source, target)) {
            target.width = source.width;
            target.height = source.height;
            target.pixels = new long[source.pixels.length];
        }

        if (Utils.approx(adjust.l, 0.0f)
            && Utils.approx(adjust.c, 0.0f)
            && Utils.approx(Utils.mod1(adjust.h), 0.0f)
            && Utils.approx(adjust.alpha, 0.0f)) {

            System.arraycopy(source.pixels, 0, target.pixels, 0, len);
            return target;
        }

        final float hZero = 0.0f;
        final float hVio = Lch.SR_HUE_SHADE;
        final float hYel = Lch.SR_HUE_LIGHT;

        final Lab lab = new Lab();
        final Lch lch = new Lch();
        final HashMap<Long, Long> convert = new HashMap<>();
        /* Do not put in clear pixel right away, as with other functions. */

        for (int i = 0; i < len; ++i) {
            final long srcPixel = source.pixels[i];
            final Long srcPixelObj = srcPixel;
            long trgPixel;

            if (convert.containsKey(srcPixelObj)) {
                trgPixel = convert.get(srcPixelObj);
            } else {
                Lab.fromHex(srcPixel, lab);
                if (lab.alpha <= 0.0f) {
                    Lab.clearBlack(lab);
                } else {
                    Lch.fromLab(lab, lch);

                    float cTrg;
                    float hTrg;
                    final boolean isGray = lch.c < Utils.EPSILON;
                    if (isGray) {
                        switch (policy) {
                            case COOL: {
                                final float t = lch.l * 0.01f;
                                final float u = 1.0f - t;
                                final float hg = u * hVio + t * hYel;
                                cTrg = lch.c + adjust.c;
                                hTrg = hg + adjust.h;
                            }
                            break;

                            case WARM: {
                                final float t = lch.l * 0.01f;
                                final float u = 1.0f - t;
                                final float hg = u * hVio + t * (hYel + 1.0f);
                                cTrg = lch.c + adjust.c;
                                hTrg = hg + adjust.h;
                            }
                            break;

                            case ZERO: {
                                cTrg = lch.c + adjust.c;
                                hTrg = hZero + adjust.h;
                            }
                            break;

                            case OMIT:

                            default: {
                                cTrg = 0.0f;
                                hTrg = 0.0f;
                            }
                        }
                    } else {
                        cTrg = lch.c + adjust.c;
                        hTrg = lch.h + adjust.h;
                    }

                    lch.l += adjust.l;
                    lch.c = cTrg;
                    lch.h = hTrg;
                    lch.alpha += adjust.alpha;

                    Lab.fromLch(lch, lab);
                }
                trgPixel = lab.toHexLongSat();
                convert.put(srcPixelObj, trgPixel);
            }

            target.pixels[i] = trgPixel;
        }

        return target;
    }

    /**
     * Adjusts a source image's pixels in LCH.
     *
     * @param source the source image
     * @param adjust the adjustment
     * @param target the target image
     * @return the adjusted image
     */
    public static Img adjustLch(
        final Img source,
        final Lch adjust,
        final Img target) {

        return Img.adjustLch(source, adjust, Img.DEFAULT_GRAY_POLICY, target);
    }

    /**
     * Blends an under and over image.
     *
     * @param imgUnder the under image
     * @param imgOver  the over image
     * @param target   the output image
     * @return the blended image
     */
    public static Img blend(
        final Img imgUnder,
        final Img imgOver,
        final Img target) {

        final int aw = imgUnder.width;
        final int ah = imgUnder.height;
        final int bw = imgOver.width;
        final int bh = imgOver.height;

        final int wLrg = Math.max(aw, bw);
        final int hLrg = Math.max(ah, bh);

        /* The 0.5 is to bias the rounding. */
        final float cx = 0.5f + wLrg * 0.5f;
        final float cy = 0.5f + hLrg * 0.5f;

        final int ax = aw == wLrg ? 0 : (int) (cx - aw * 0.5f);
        final int ay = ah == hLrg ? 0 : (int) (cy - ah * 0.5f);
        final int bx = bw == wLrg ? 0 : (int) (cx - bw * 0.5f);
        final int by = bh == hLrg ? 0 : (int) (cy - bh * 0.5f);

        return Img.blend(
            imgUnder, ax, ay,
            imgOver, bx, by,
            Img.DEFAULT_BM_ALPHA,
            Img.DEFAULT_BM_L,
            Img.DEFAULT_BM_AB,
            target, null);
    }

    /**
     * Blends an under and over image.
     *
     * @param imgUnder the under image
     * @param xUnder   the under x offset
     * @param yUnder   the under y offset
     * @param imgOver  the over image
     * @param xOver    the over x offset
     * @param yOver    the over y offset
     * @param target   the output image
     * @return the blended image
     */
    public static Img blend(
        final Img imgUnder, final int xUnder, final int yUnder,
        final Img imgOver, final int xOver, final int yOver,
        final Img target) {

        return Img.blend(
            imgUnder, xUnder, yUnder,
            imgOver, xOver, yOver,
            Img.DEFAULT_BM_ALPHA,
            Img.DEFAULT_BM_L,
            Img.DEFAULT_BM_AB,
            target, null);
    }

    /**
     * Blends an under and over image.
     *
     * @param imgUnder the under image
     * @param xUnder   the under x offset
     * @param yUnder   the under y offset
     * @param imgOver  the over image
     * @param xOver    the over x offset
     * @param yOver    the over y offset
     * @param bmAlpha  the alpha blend mode
     * @param bmLight  the light blend mode
     * @param bmAb     the ab blend mode
     * @param target   the output image
     * @return the blended image
     */
    public static Img blend(
        final Img imgUnder, final int xUnder, final int yUnder,
        final Img imgOver, final int xOver, final int yOver,
        final BlendMode.Alpha bmAlpha,
        final BlendMode.L bmLight,
        final BlendMode.AB bmAb,
        final Img target) {

        return Img.blend(
            imgUnder, xUnder, yUnder,
            imgOver, xOver, yOver,
            bmAlpha, bmLight, bmAb,
            target, null);
    }

    /**
     * Blends an under and over image.
     *
     * @param imgUnder the under image
     * @param xUnder   the under x offset
     * @param yUnder   the under y offset
     * @param imgOver  the over image
     * @param xOver    the over x offset
     * @param yOver    the over y offset
     * @param bmAlpha  the alpha blend mode
     * @param bmLight  the light blend mode
     * @param bmAb     the ab blend mode
     * @param target   the output image
     * @param tl       the composite offset
     * @return the blended image
     */
    public static Img blend(
        final Img imgUnder, final int xUnder, final int yUnder,
        final Img imgOver, final int xOver, final int yOver,
        final BlendMode.Alpha bmAlpha,
        final BlendMode.L bmLight,
        final BlendMode.AB bmAb,
        final Img target, final Vec2 tl) {

        final int aw = imgUnder.width;
        final int ah = imgUnder.height;
        final long[] pxUnder = imgUnder.pixels;

        final int bw = imgOver.width;
        final int bh = imgOver.height;
        final long[] pxOver = imgOver.pixels;

        /* Find the bottom right corner for a and b. */
        final int abrx = xUnder + aw - 1;
        final int abry = yUnder + ah - 1;
        final int bbrx = xOver + bw - 1;
        final int bbry = yOver + bh - 1;

        /*
         * Based on alpha blend, find the union or intersection.
         */
        int dx = Math.min(xUnder, xOver);
        int dy = Math.min(yUnder, yOver);
        int dbrx = Math.max(abrx, bbrx);
        int dbry = Math.max(abry, bbry);

        switch (bmAlpha) {
            case MIN:
            case MULTIPLY: {
                dx = Math.max(xUnder, xOver);
                dy = Math.max(yUnder, yOver);
                dbrx = Math.min(abrx, bbrx);
                dbry = Math.min(abry, bbry);
            }
            break;

            case OVER: {
                dx = xOver;
                dy = yOver;
                dbrx = bbrx;
                dbry = bbry;
            }
            break;

            case UNDER: {
                dx = xUnder;
                dy = yUnder;
                dbrx = abrx;
                dbry = abry;
            }
            break;

            case BLEND:
            case MAX:
            default:
        }

        final HashMap<Long, Lab> dict = new HashMap<>();
        final Lab clearLab = Lab.clearBlack(new Lab());
        dict.put(Img.CLEAR_PIXEL, clearLab);

        final int dw = 1 + dbrx - dx;
        final int dh = 1 + dbry - dy;
        if (dw > 0 && dh > 0) {
            final int axid = xUnder - dx;
            final int ayid = yUnder - dy;
            final int bxid = xOver - dx;
            final int byid = yOver - dy;

            final int dLen = dw * dh;
            for (int h = 0; h < dLen; ++h) {
                final int x = h % dw;
                final int y = h / dw;

                final int axs = x - axid;
                final int ays = y - ayid;
                if (ays >= 0 && ays < ah && axs >= 0 && axs < aw) {
                    final long hexUnder = pxUnder[ays * aw + axs];
                    dict.put(hexUnder, Lab.fromHex(hexUnder, new Lab()));
                }

                final int bxs = x - bxid;
                final int bys = y - byid;
                if (bys >= 0 && bys < bh && bxs >= 0 && bxs < bw) {
                    final long hexOver = pxOver[bys * bw + bxs];
                    dict.put(hexOver, Lab.fromHex(hexOver, new Lab()));
                }
            }
        }

        /* The result dimensions are the union of a and b. */
        final int cx = Math.min(xUnder, xOver);
        final int cy = Math.min(yUnder, yOver);
        final int cbrx = Math.max(abrx, bbrx);
        final int cbry = Math.max(abry, bbry);
        final int cw = 1 + cbrx - cx;
        final int ch = 1 + cbry - cy;
        final int cLen = cw * ch;

        /* Find difference between the union top left and top left of a and b. */
        final int axud = xUnder - cx;
        final int ayud = yUnder - cy;
        final int bxud = xOver - cx;
        final int byud = yOver - cy;

        final Lab cLab = new Lab();
        final long[] trgPixels = new long[cLen];
        for (int i = 0; i < cLen; ++i) {
            final int x = i % cw;
            final int y = i / cw;

            long hexUnder = Img.CLEAR_PIXEL;
            final int axs = x - axud;
            final int ays = y - ayud;
            if (ays >= 0 && ays < ah && axs >= 0 && axs < aw) {
                hexUnder = pxUnder[ays * aw + axs];
            }

            long hexOver = Img.CLEAR_PIXEL;
            final int bxs = x - bxud;
            final int bys = y - byud;
            if (bys >= 0 && bys < bh && bxs >= 0 && bxs < bw) {
                hexOver = pxOver[bys * bw + bxs];
            }

            final Lab labUnder = dict.getOrDefault(hexUnder, clearLab);
            final Lab labOver = dict.getOrDefault(hexOver, clearLab);

            final double t = labOver.alpha;
            final double v = labUnder.alpha;
            final double u = 1.0d - t;
            double tuv = t + u * v;

            switch (bmAlpha) {
                case MAX: {
                    tuv = Math.max(t, v);
                }
                break;
                case MIN: {
                    tuv = Math.min(t, v);
                }
                break;
                case MULTIPLY: {
                    tuv = t * v;
                }
                break;
                case OVER: {
                    tuv = t;
                }
                break;
                case UNDER: {
                    tuv = v;
                }
                break;
                case BLEND:
                default:
            }

            long hexComp = Img.CLEAR_PIXEL;
            if (tuv > 0.0d) {
                // Does lab over need to default to lab under if it has zero
                // alpha and vice versa?
                final boolean vgt0 = v > 0.0d;
                final boolean tgt0 = t > 0.0d;

                final double lOver = labOver.l;
                final double aOver = labOver.a;
                final double bOver = labOver.b;

                final double lUnder = labUnder.l;
                final double aUnder = labUnder.a;
                final double bUnder = labUnder.b;

                double lComp = u * lUnder + t * lOver;
                double aComp = u * aUnder + t * aOver;
                double bComp = u * bUnder + t * bOver;

                switch (bmLight) {
                    case ADD: {
                        final double sum = vgt0 ? lUnder + lOver : lOver;
                        lComp = u * lUnder + t * sum;
                    }
                    break;

                    case AVERAGE: {
                        final double avg = vgt0 ? (lUnder + lOver) * 0.5d : lOver;
                        lComp = u * lUnder + t * avg;
                    }
                    break;

                    case DIVIDE: {
                        final double quo = vgt0 ? lOver != 0.0d ? lUnder / lOver * 100.0d : 100.0d : lOver;
                        lComp = u * lUnder + t * quo;
                    }
                    break;

                    case MULTIPLY: {
                        final double prod = vgt0 ? lUnder * lOver * 0.01d : lOver;
                        lComp = u * lUnder + t * prod;
                    }
                    break;

                    case OVER: {
                        lComp = lOver;
                    }
                    break;

                    case SCREEN: {
                        final double scr = vgt0 ? lUnder + lOver - lUnder * lOver * 0.01 : lOver;
                        lComp = u * lUnder + t * scr;
                    }
                    break;

                    case SUBTRACT: {
                        final double dff = vgt0 ? lUnder - lOver : lOver;
                        lComp = u * lUnder + t * dff;
                    }
                    break;

                    case UNDER: {
                        lComp = lUnder;
                    }
                    break;

                    case BLEND:
                    default:
                } /* End light blend mode. */

                switch (bmAb) {
                    case ADD: {
                        final double aSum = vgt0 ? aUnder + aOver : aOver;
                        final double bSum = vgt0 ? bUnder + bOver : bOver;
                        aComp = u * aUnder + t * aSum;
                        bComp = u * bUnder + t * bSum;
                    }
                    break;

                    case AVERAGE: {
                        final double aAvg = vgt0 ? (aUnder + aOver) * 0.5d : aOver;
                        final double bAvg = vgt0 ? (bUnder + bOver) * 0.5d : bOver;
                        aComp = u * aUnder + t * aAvg;
                        bComp = u * bUnder + t * bAvg;
                    }
                    break;

                    case CHROMA: {
                        if (vgt0 && tgt0) {
                            final double csqUnder = aUnder * aUnder + bUnder * bUnder;
                            if (csqUnder > Utils.EPSILON_D) {
                                final double s = Math.sqrt(aOver * aOver + bOver * bOver) / Math.sqrt(csqUnder);
                                aComp = s * aUnder;
                                bComp = s * bUnder;
                            } else {
                                aComp = 0.0d;
                                bComp = 0.0d;
                            } /* End chroma under is greater than zero. */
                        } /* End under alpha is greater than zero. */
                    }
                    break;

                    case HUE: {
                        if (vgt0 && tgt0) {
                            final double csqOver = aOver * aOver + bOver * bOver;
                            if (csqOver > Utils.EPSILON_D) {
                                final double s = Math.sqrt(aUnder * aUnder + bUnder * bUnder) / Math.sqrt(csqOver);
                                aComp = s * aOver;
                                bComp = s * bOver;
                            } else {
                                aComp = 0.0d;
                                bComp = 0.0d;
                            } /* End chroma over is greater than zero. */
                        } /* End under alpha is greater than zero. */
                    }
                    break;

                    case OVER: {
                        aComp = aOver;
                        bComp = bOver;
                    }
                    break;

                    case SUBTRACT: {
                        final double aDff = vgt0 ? aUnder - aOver : aOver;
                        final double bDff = vgt0 ? bUnder - bOver : bOver;
                        aComp = u * aUnder + t * aDff;
                        bComp = u * bUnder + t * bDff;
                    }
                    break;

                    case UNDER: {
                        aComp = aUnder;
                        bComp = bUnder;
                    }
                    break;

                    case BLEND:
                    default:
                } /* End ab blend mode. */

                cLab.set((float) lComp, (float) aComp, (float) bComp, (float) tuv);
                hexComp = cLab.toHexLongSat();
            } /* End alpha is greater than zero. */

            trgPixels[i] = hexComp;
        } /* End pixels loop. */

        if (tl != null) {
            tl.set(dx, dy);
        }

        target.width = cw;
        target.height = ch;
        target.pixels = trgPixels;

        return target;
    }

    /**
     * Blurs an image by averaging each pixel with its neighbors in 8
     * directions. The step determines the size of the kernel, where the
     * minimum step of 1 will make a 3x3, 9 pixel kernel.
     *
     * @param source the input image
     * @param step   the kernel step
     * @param target the output image
     * @return the blurred image
     */
    public static Img blur(
        final Img source,
        final int step,
        final Img target) {

        // TODO: TEST

        final int wSrc = source.width;
        final int hSrc = source.height;
        final long[] srcPixels = source.pixels;
        final int srcLen = srcPixels.length;

        if (!Img.similar(source, target)) {
            target.width = wSrc;
            target.height = hSrc;
            target.pixels = new long[srcLen];
        }

        final long[] trgPixels = target.pixels;

        final Lab labCenter = new Lab();
        final Lab labNgbr = new Lab();
        final Lab labAvg = new Lab();

        final int stepVal = Math.max(step, 1);
        final int wKrn = 1 + stepVal * 2;
        final int krnLen = wKrn * wKrn;
        final float toAvg = 1.0f / krnLen;

        for (int i = 0; i < srcLen; ++i) {
            final long tlab64Src = srcPixels[i];
            Lab.fromHex(tlab64Src, labCenter);

            final int xSrc = i % wSrc - stepVal;
            final int ySrc = i / wSrc - stepVal;

            float lSum = 0.0f;
            float aSum = 0.0f;
            float bSum = 0.0f;
            float tSum = 0.0f;

            for (int j = 0; j < krnLen; ++j) {
                final int xComp = xSrc + j % wKrn;
                final int yComp = ySrc + j / wKrn;
                if (yComp >= 0 && yComp < hSrc && xComp >= 0 && xComp < wSrc) {
                    Lab.fromHex(srcPixels[xComp + yComp * wSrc], labNgbr);
                    lSum += labNgbr.l;
                    aSum += labNgbr.a;
                    bSum += labNgbr.b;
                    tSum += labNgbr.alpha;
                } else {
                    /*
                     * When the kernel is out of bounds, sample the central color but
                     * do not tally alpha.
                     */
                    lSum += labCenter.l;
                    aSum += labCenter.a;
                    bSum += labCenter.b;
                } /* End pixel is in bounds. */
            } /* End kernel loop. */

            labAvg.set(lSum * toAvg, aSum * toAvg, bSum * toAvg, tSum * toAvg);
            trgPixels[i] = labAvg.toHexLong();
        }

        return target;
    }

    /**
     * Gets the image pixel data as a byte array. Bytes are ordered from least
     * to most significant digit (little endian).
     *
     * @param source the source image
     * @return the byte array
     * @see Utils#byteslm(long, byte[], int)
     */
    public static byte[] byteslm(final Img source) {

        final int len = source.pixels.length;
        final byte[] arr = new byte[len * 8];
        for (int i = 0, j = 0; i < len; ++i, j += 8) {
            Utils.byteslm(source.pixels[i], arr, j);
        }
        return arr;
    }

    /**
     * Gets the image pixel data as a byte array. Bytes are ordered from most
     * to least significant digit (big endian).
     *
     * @param source the source image
     * @return the byte array
     * @see Utils#bytesml(long, byte[], int)
     */
    public static byte[] bytesml(final Img source) {

        final int len = source.pixels.length;
        final byte[] arr = new byte[len * 8];
        for (int i = 0, j = 0; i < len; ++i, j += 8) {
            Utils.bytesml(source.pixels[i], arr, j);
        }
        return arr;
    }

    /**
     * Creates a checker pattern in an image.
     *
     * @param sizeCheck the checker size
     * @param target    the output image
     * @return the checker image
     */
    public static Img checker(final int sizeCheck, final Img target) {

        return Img.checker(Img.CHECKER_DARK, Img.CHECKER_LIGHT,
            sizeCheck, sizeCheck, target);
    }

    /**
     * Creates a checker pattern in an image.
     *
     * @param o         the first color
     * @param d         the second color
     * @param sizeCheck the checker size
     * @param target    the output image
     * @return the checker image
     */
    public static Img checker(
        final Lab o, final Lab d,
        final int sizeCheck,
        final Img target) {

        return Img.checker(o.toHexLongSat(), d.toHexLongSat(),
            sizeCheck, sizeCheck, target);
    }

    /**
     * Creates a checker pattern in an image.
     *
     * @param o      the first color
     * @param d      the second color
     * @param wCheck the checker width
     * @param hCheck the checker height
     * @param target the output image
     * @return the checker image
     */
    public static Img checker(
        final Lab o, final Lab d,
        final int wCheck, final int hCheck,
        final Img target) {

        return Img.checker(o.toHexLongSat(), d.toHexLongSat(), wCheck, hCheck, target);
    }

    /**
     * Creates a checker pattern in an image.
     *
     * @param o         the first color
     * @param d         the second color
     * @param sizeCheck the checker size
     * @param target    the output image
     * @return the checker image
     */
    public static Img checker(
        final long o, final long d,
        final int sizeCheck,
        final Img target) {

        return Img.checker(o, d, sizeCheck, sizeCheck, target);
    }

    /**
     * Creates a checker pattern in an image. For making a background canvas
     * that signals transparency in the pixels of an image layer(s) above it.
     *
     * @param o      the first color
     * @param d      the second color
     * @param wCheck the checker width
     * @param hCheck the checker height
     * @param target the output image
     * @return the checker image
     */
    public static Img checker(
        final long o, final long d,
        final int wCheck, final int hCheck,
        final Img target) {

        final int w = target.width;
        final int h = target.height;
        final int shortEdge = Math.min(w, h);
        final int wcVerif = Utils.clamp(wCheck, 1, shortEdge / 2);
        final int hcVerif = Utils.clamp(hCheck, 1, shortEdge / 2);

        /*
         * User may want to blend checker over a layer beneath, so clear pixel
         * should be allowed.
         */
        final long va = o != d ? o : Img.CHECKER_DARK;
        final long vb = o != d ? d : Img.CHECKER_LIGHT;

        final int len = target.pixels.length;
        for (int i = 0; i < len; ++i) {
            target.pixels[i] = (i % w / wcVerif + i / w / hcVerif & 1) == 0 ? va : vb;
        }

        return target;
    }

    /**
     * Clears all pixels in an image to {@link Img#CLEAR_PIXEL},
     * {@value Img#CLEAR_PIXEL}.
     *
     * @param target the output image
     * @return the filled image
     */
    public static Img clear(final Img target) {

        return Img.fill(Img.CLEAR_PIXEL, target);
    }

    /**
     * Sets an image pixel to {@link Img#CLEAR_PIXEL} if it has zero alpha.
     *
     * @param source the input image
     * @param target the output image
     * @return the alpha corrected image
     */
    public static Img correctZeroAlpha(final Img source, final Img target) {

        final int len = source.pixels.length;
        if (!Img.similar(source, target)) {
            target.width = source.width;
            target.height = source.height;
            target.pixels = new long[len];
        }

        for (int i = 0; i < len; ++i) {
            final long srcPixel = source.pixels[i];
            target.pixels[i] = (srcPixel & Img.T_MASK) != 0L ? srcPixel : Img.CLEAR_PIXEL;
        }

        return target;
    }

    /**
     * Expands an image's size to be a power of two. If the uniform tag is true,
     * then uses the image's greater edge as a reference.
     *
     * @param source  the source image
     * @param uniform the uniform flag
     * @param target  the target image
     * @return the expanded image
     */
    public static Img expandToPow2(
        final Img source,
        final boolean uniform,
        final Img target) {

        final int wSrc = source.width;
        final int hSrc = source.height;

        int wTrg;
        int hTrg;
        if (uniform) {
            int u = Utils.nextPowerOf2(Math.max(wSrc, hSrc));
            wTrg = u;
            hTrg = u;
        } else {
            wTrg = Utils.nextPowerOf2(wSrc);
            hTrg = Utils.nextPowerOf2(hSrc);
        }

        final long[] srcPixels = source.pixels;
        final int srcLen = srcPixels.length;
        final int trgLen = wTrg * hTrg;
        final long[] trgPixels = new long[trgLen];

        if (wSrc == wTrg && hSrc == hTrg) {
            System.arraycopy(srcPixels, 0, trgPixels, 0, srcLen);
            target.width = wTrg;
            target.height = hTrg;
            target.pixels = trgPixels;
            return target;
        }

        final int xtl = (int) (wTrg * 0.5f + 0.5f)
            - (int) (wSrc * 0.5f + 0.5f);
        final int ytl = (int) (hTrg * 0.5f + 0.5f)
            - (int) (hSrc * 0.5f + 0.5f);

        for (int i = 0; i < trgLen; ++i) {
            final int xSrc = (i % wTrg) - xtl;
            final int ySrc = (i / hTrg) - ytl;
            final long trgPixel = ySrc >= 0 && ySrc < hSrc
                && xSrc >= 0 && xSrc < wSrc
                ? srcPixels[ySrc * wSrc + xSrc]
                : Img.CLEAR_PIXEL;
            trgPixels[i] = trgPixel;
        }

        target.width = wTrg;
        target.height = hTrg;
        target.pixels = trgPixels;

        return target;
    }


    /**
     * Fills an image with a color.
     *
     * @param fill   the fill color
     * @param target the output image
     * @return the filled image
     */
    public static Img fill(final Lab fill, final Img target) {

        return Img.fill(fill.toHexLongSat(), target);
    }

    /**
     * Fills an image with a color.
     *
     * @param fill   the fill color
     * @param target the output image
     * @return the filled image
     */
    public static Img fill(final long fill, final Img target) {

        final int len = target.pixels.length;
        for (int i = 0; i < len; ++i) {
            target.pixels[i] = fill;
        }
        return target;
    }

    /**
     * Flips the pixels source image vertically, on the y axis, and stores the
     * result in the target
     * image.
     *
     * @param source the input image
     * @param target the output image
     * @return the flipped image
     */
    public static Img flipX(final Img source, final Img target) {

        final int w = source.width;
        final int h = source.height;
        final int len = source.pixels.length;

        if (!Img.similar(source, target)) {
            target.width = w;
            target.height = h;
            target.pixels = new long[len];
        }

        final int wn1 = w - 1;
        for (int i = 0; i < len; ++i) {
            target.pixels[i / w * w + wn1 - i % w] = source.pixels[i];
        }

        return target;
    }

    /**
     * Flips the pixels source image vertically, on the y axis, and stores the
     * result in the target
     * image.
     *
     * @param source the input image
     * @param target the output image
     * @return the flipped image
     */
    public static Img flipY(final Img source, final Img target) {

        final int w = source.width;
        final int h = source.height;
        final int len = source.pixels.length;

        if (!Img.similar(source, target)) {
            target.width = w;
            target.height = h;
            target.pixels = new long[len];
        }

        final int hn1 = h - 1;
        for (int i = 0; i < len; ++i) {
            target.pixels[(hn1 - i / w) * w + i % w] = source.pixels[i];
        }

        return target;
    }

    /**
     * Hashes an image according to the <a href=
     * "https://en.wikipedia.org/wiki/Fowler%E2%80%93Noll%E2%80%93Vo_hash_function">
     * Fowler–Noll–Vo</a> method.
     *
     * @param source the image
     * @return the hash
     */
    public static BigInteger fnvHash(final Img source) {

        final BigInteger fnvPrime = BigInteger.valueOf(1099511628211L);
        BigInteger hash = new BigInteger("14695981039346656037");
        final long[] srcPixels = source.pixels;
        for (long srcPixel : srcPixels) {
            hash = hash.xor(BigInteger.valueOf(srcPixel)).multiply(fnvPrime);
        }

        return hash;
    }

    /**
     * Converts a pixel array of 32-bit integers in the format 0xAARRGGBB to
     * a LAB image. Width and height should be in pixels. It should not be
     * the virtual width and height that accounts for pixel density.
     *
     * @param width   the width
     * @param height  the height
     * @param argb32s the pixel array
     * @param target  the output image
     * @return the image
     */
    public static Img fromArgb32(
        final int width,
        final int height,
        final int[] argb32s,
        final Img target) {

        final int wVerif = Utils.clamp(Math.abs(width), 1, Img.MAX_DIMENSION);
        final int hVerif = Utils.clamp(Math.abs(height), 1, Img.MAX_DIMENSION);

        final int area = wVerif * hVerif;
        final int len = argb32s.length;
        if (area != len) {
            System.err.println("Pixel length does not match image area.");
            return target;
        }

        final Rgb srgb = new Rgb();
        final Rgb lrgb = new Rgb();
        final Vec4 xyz = new Vec4();
        final Lab lab = new Lab();

        final long[] tlab64s = new long[len];
        final HashMap<Integer, Long> convert = new HashMap<>();
        convert.put(0, Img.CLEAR_PIXEL);

        for (int i = 0; i < len; ++i) {
            final int argb32 = argb32s[i];
            final Integer argb32Obj = argb32;
            long tlab64;

            if (convert.containsKey(argb32Obj)) {
                tlab64 = convert.get(argb32Obj);
            } else {
                Rgb.fromHex(argb32, srgb);
                // TODO: Option to unpremultiply?
                Rgb.sRgbToSrLab2(srgb, lab, xyz, lrgb);
                tlab64 = lab.toHexLongSat();
                convert.put(argb32Obj, tlab64);
            }

            tlab64s[i] = tlab64;
        }

        target.width = wVerif;
        target.height = hVerif;
        target.pixels = tlab64s;

        return target;
    }

    /**
     * Gets a region defined by a top left and bottom right corner. May return
     * a clear image if coordinates are invalid.
     *
     * @param source the input image
     * @param xtl    the top left corner x
     * @param ytl    the top left corner y
     * @param xbr    the bottom right corner x
     * @param ybr    the bottom right corner y
     * @param target the output image
     * @return the region
     */
    public static Img getRegion(
        final Img source,
        final int xtl, final int ytl,
        final int xbr, final int ybr,
        final Img target) {

        // TODO: overload to except Vec2 uvs as arguments? Bounds2?

        final int wSrc = source.width;
        final int hSrc = source.height;

        int xtlVrf = Utils.clamp(xtl, 0, wSrc - 1);
        int ytlVrf = Utils.clamp(ytl, 0, hSrc - 1);
        int xbrVrf = Utils.clamp(xbr, 0, wSrc - 1);
        int ybrVrf = Utils.clamp(ybr, 0, hSrc - 1);

        /* Swap pixels if in wrong order. */
        xtlVrf = Math.min(xtlVrf, xbrVrf);
        ytlVrf = Math.min(ytlVrf, ybrVrf);

        final int wTrg = 1 + xbrVrf - xtlVrf;
        final int hTrg = 1 + ybrVrf - ytlVrf;
        final long[] srcPixels = source.pixels;

        if (wTrg <= 0 || hTrg <= 0) {
            target.width = wSrc;
            target.height = hSrc;
            target.pixels = new long[srcPixels.length];
            return Img.clear(target);
        }

        final int trgLen = wTrg * hTrg;
        final long[] trgPixels = new long[trgLen];

        for (int i = 0; i < trgLen; ++i) {
            final int xTrg = i % wTrg;
            final int yTrg = i / wTrg;
            final int xSrc = xtlVrf + xTrg;
            final int ySrc = ytlVrf + yTrg;
            final int j = xSrc + ySrc * wSrc;
            trgPixels[i] = srcPixels[j];
        }

        target.width = wTrg;
        target.height = hTrg;
        target.pixels = trgPixels;

        return target;
    }

    /**
     * Generates a linear gradient from an origin point to a destination point.
     * The origin and destination should be in the range [-1.0, 1.0].
     * The scalar projection is clamped to [0.0, 1.0].
     *
     * @param grd    the gradient
     * @param xOrig  the origin x coordinate
     * @param yOrig  the origin y coordinate
     * @param xDest  the destination x coordinate
     * @param yDest  the destination y coordinate
     * @param target the output image
     * @return the gradient image
     */
    public static Img gradientLinear(
        final Gradient grd,
        final float xOrig, final float yOrig,
        final float xDest, final float yDest,
        final Img target) {

        return Img.gradientLinear(grd, xOrig, yOrig, xDest, yDest,
            new Lab.MixLab(), target);
    }

    /**
     * Generates a linear gradient from an origin point to a destination point.
     * The origin and destination should be in the range [-1.0, 1.0].
     * The scalar projection is clamped to [0.0, 1.0].
     *
     * @param grd    the gradient
     * @param xOrig  the origin x coordinate
     * @param yOrig  the origin y coordinate
     * @param xDest  the destination x coordinate
     * @param yDest  the destination y coordinate
     * @param easing the easing function
     * @param target the output image
     * @return the gradient image
     * @see Gradient#eval(Gradient, float, Lab.AbstrEasing, Lab)
     * @see Utils#clamp01(float)
     */
    public static Img gradientLinear(
        final Gradient grd,
        final float xOrig, final float yOrig,
        final float xDest, final float yDest,
        final Lab.AbstrEasing easing,
        final Img target) {

        final int wTrg = target.width;
        final int hTrg = target.height;

        final float bx = xOrig - xDest;
        final float by = yOrig - yDest;

        final float bbInv = 1.0f / Math.max(Utils.EPSILON, bx * bx + by * by);

        final float bxbbinv = bx * bbInv;
        final float bybbinv = by * bbInv;

        final float xobx = xOrig * bxbbinv;
        final float yoby = yOrig * bybbinv;
        final float bxwInv2 = 2.0f / (wTrg - 1.0f) * bxbbinv;
        final float byhInv2 = 2.0f / (hTrg - 1.0f) * bybbinv;

        final Lab trgLab = new Lab();
        final int len = target.pixels.length;
        for (int i = 0; i < len; ++i) {
            final float fac = Utils.clamp01(xobx + bxbbinv
                - bxwInv2 * (i % wTrg) + (yoby + byhInv2
                * (float) (i / wTrg) - bybbinv));
            Gradient.eval(grd, fac, easing, trgLab);
            target.pixels[i] = trgLab.toHexLongSat();
        }

        return target;
    }

    /**
     * Generates a linear gradient from an origin point to a destination point.
     * The scalar projection is clamped to [0.0, 1.0].
     *
     * @param grd    the gradient
     * @param target the output image
     * @return the gradient image
     */
    public static Img gradientLinear(final Gradient grd, final Img target) {

        return Img.gradientLinear(grd, -1.0f, 0.0f, 1.0f, 0.0f, target);
    }

    /**
     * Generates a linear gradient from an origin point to a destination point.
     * The scalar projection is clamped to [0.0, 1.0].
     *
     * @param grd    the gradient
     * @param easing the easing function
     * @param target the output image
     * @return the gradient image
     */
    public static Img gradientLinear(
        final Gradient grd, final Lab.AbstrEasing easing, final Img target) {

        return Img.gradientLinear(grd, -1.0f, 0.0f, 1.0f, 0.0f, easing, target);
    }

    /**
     * Generates a linear gradient from an origin point to a destination point.
     * The origin and destination should be in the range [-1.0, 1.0].
     * The scalar projection is clamped to [0.0, 1.0].
     *
     * @param grd    the gradient
     * @param orig   the origin
     * @param dest   the destination
     * @param target the output image
     * @return the gradient image
     */
    public static Img gradientLinear(
        final Gradient grd,
        final Vec2 orig,
        final Vec2 dest,
        final Img target) {

        return Img.gradientLinear(grd, orig, dest, new Lab.MixLab(), target);
    }

    /**
     * Generates a linear gradient from an origin point to a destination point.
     * The origin and destination should be in the range [-1.0, 1.0]. The
     * scalar projection is clamped to [0.0, 1.0].
     *
     * @param grd    the gradient
     * @param orig   the origin
     * @param dest   the destination
     * @param easing the easing function
     * @param target the output image
     * @return the gradient image
     */
    public static Img gradientLinear(
        final Gradient grd,
        final Vec2 orig,
        final Vec2 dest,
        final Lab.AbstrEasing easing,
        final Img target) {

        return Img.gradientLinear(grd,
            orig.x, orig.y,
            dest.x, dest.y,
            easing, target);
    }

    /**
     * Maps the colors of a source image to a color gradient.
     *
     * @param grd    the gradient
     * @param source the input image
     * @param target the output image
     * @return the mapped image
     */
    public static Img gradientMap(
        final Gradient grd,
        final Img source,
        final Img target) {

        return Img.gradientMap(grd, source, new Lab.MixLab(),
            MapChannel.L, true, target);
    }

    /**
     * Maps the colors of a source image to a color gradient.
     *
     * @param grd    the gradient
     * @param source the input image
     * @param easing the easing function
     * @param target the output image
     * @return the mapped image
     */
    public static Img gradientMap(
        final Gradient grd,
        final Img source,
        final Lab.AbstrEasing easing,
        final Img target) {

        return Img.gradientMap(grd, source, easing,
            MapChannel.L, true, target);
    }

    /**
     * Maps the colors of a source image to a color gradient.
     *
     * @param grd          the gradient
     * @param source       the input image
     * @param easing       the easing function
     * @param channel      the color channel
     * @param useNormalize normalize channel range
     * @param target       the output image
     * @return the mapped image
     */
    public static Img gradientMap(
        final Gradient grd,
        final Img source,
        final Lab.AbstrEasing easing,
        final Img.MapChannel channel,
        final boolean useNormalize,
        final Img target) {

        if (!Img.similar(source, target)) {
            target.width = source.width;
            target.height = source.height;
            target.pixels = new long[source.pixels.length];
        }

        final HashMap<Long, Long> convert = new HashMap<>();
        convert.put(Img.CLEAR_PIXEL, Img.CLEAR_PIXEL);

        final Lab lab = new Lab();
        final int len = source.pixels.length;
        final HashMap<Long, Lch> uniques = new HashMap<>();

        float minChannel = Float.MAX_VALUE;
        float maxChannel = -Float.MAX_VALUE;
        int sumTally = 0;

        for (int i = 0; i < len; ++i) {
            final long srcPixel = source.pixels[i];
            final Long srcPixelObj = srcPixel;
            if (!uniques.containsKey(srcPixelObj)) {
                Lab.fromHex(srcPixel, lab);
                final Lch lch = Lch.fromLab(lab, new Lch());
                if (lch.alpha > 0.0f) {
                    switch (channel) {
                        case C: {
                            if (lch.c > maxChannel) {
                                maxChannel = lch.c;
                            }
                            if (lch.c < minChannel) {
                                minChannel = lch.c;
                            }
                        }
                        break;

                        case L:
                        default: {
                            if (lch.l > maxChannel) {
                                maxChannel = lch.l;
                            }
                            if (lch.l < minChannel) {
                                minChannel = lch.l;
                            }
                        }
                    }
                    ++sumTally;
                }
                uniques.put(srcPixelObj, lch);
            }
        }

        final boolean useNormVerif = useNormalize
            && sumTally != 0
            && maxChannel > minChannel;
        final float dff = Utils.diff(maxChannel, minChannel);
        final float denom = Utils.div(1.0f, dff);
        final Lch defLch = Lch.clearBlack(new Lch());

        for (int j = 0; j < len; ++j) {
            final Long srcPixelObj = source.pixels[j];
            long trgPixel;
            if (convert.containsKey(srcPixelObj)) {
                trgPixel = convert.get(srcPixelObj);
            } else {
                final Lch lch = uniques.getOrDefault(srcPixelObj, defLch);

                float fac;
                switch (channel) {
                    case C: {
                    }
                    fac = useNormVerif
                        ? (lch.c - minChannel) * denom
                        : lch.c / Lch.SR_CHROMA_MAX;
                    break;

                    case L:
                    default: {
                        fac = useNormVerif
                            ? (lch.l - minChannel) * denom
                            : lch.l * 0.01f;
                    }
                }

                Gradient.eval(grd, fac, easing, lab);
                lab.alpha = lab.alpha * lch.alpha;
                trgPixel = lab.toHexLongSat();
                convert.put(srcPixelObj, trgPixel);
            }

            target.pixels[j] = trgPixel;
        }

        return target;
    }

    /**
     * Maps the colors of a source image to a color gradient.
     *
     * @param grd     the gradient
     * @param source  the input image
     * @param easing  the easing function
     * @param channel the color channel
     * @param target  the output image
     * @return the mapped image
     */
    public static Img gradientMap(
        final Gradient grd,
        final Img source,
        final Lab.AbstrEasing easing,
        final Img.MapChannel channel,
        final Img target) {

        return Img.gradientMap(grd, source, easing, channel, false, target);
    }

    /**
     * Generates a radial gradient from an origin point. The origin should be in the
     * range [-1.0,
     * 1.0].
     *
     * @param grd    the gradient
     * @param xOrig  the origin x coordinate
     * @param yOrig  the origin y coordinate
     * @param radius the radius
     * @param target the output image
     * @return the gradient image
     * @see Gradient#eval(Gradient, float, Lab.AbstrEasing, Lab)
     */
    public static Img gradientRadial(
        final Gradient grd,
        final float xOrig,
        final float yOrig,
        final float radius,
        final Img target) {

        return Img.gradientRadial(grd, xOrig, yOrig, radius, new Lab.MixLab(), target);
    }

    /**
     * Generates a radial gradient from an origin point. The origin should be
     * in the range [-1.0, 1.0]. Does not account for aspect ratio, so an image
     * that isn't 1:1 will result in an ellipsoid.
     *
     * @param grd    the gradient
     * @param xOrig  the origin x coordinate
     * @param yOrig  the origin y coordinate
     * @param radius the radius
     * @param easing the easing function
     * @param target the output image
     * @return the gradient image
     * @see Gradient#eval(Gradient, float, Lab.AbstrEasing, Lab)
     */
    public static Img gradientRadial(
        final Gradient grd,
        final float xOrig,
        final float yOrig,
        final float radius,
        final Lab.AbstrEasing easing,
        final Img target) {

        final int wTrg = target.width;
        final int hTrg = target.height;

        final float hInv2 = 2.0f / (hTrg - 1.0f);
        final float wInv2 = 2.0f / (wTrg - 1.0f);

        final float r2 = radius + radius;
        final float rsqInv = 1.0f / Math.max(Utils.EPSILON, r2 * r2);

        final float yon1 = yOrig - 1.0f;
        final float xop1 = xOrig + 1.0f;

        final Lab trgLab = new Lab();
        final int len = target.pixels.length;
        for (int i = 0; i < len; ++i) {
            final float ay = yon1 + hInv2 * (float) (i / wTrg);
            final float ax = xop1 - wInv2 * (i % wTrg);
            final float fac = 1.0f - (ax * ax + ay * ay) * rsqInv;
            Gradient.eval(grd, fac, easing, trgLab);
            target.pixels[i] = trgLab.toHexLongSat();
        }

        return target;
    }

    /**
     * Generates a radial gradient.
     *
     * @param grd    the gradient
     * @param target the output image
     * @return the gradient image
     * @see Gradient#eval(Gradient, float, Lab.AbstrEasing, Lab)
     */
    public static Img gradientRadial(final Gradient grd, final Img target) {

        return Img.gradientRadial(grd, 0.0f, 0.0f, 0.5f, new Lab.MixLab(), target);
    }

    /**
     * Generates a radial gradient from an origin point. The origin should be in the
     * range [-1.0,
     * 1.0].
     *
     * @param grd    the gradient
     * @param orig   the origin
     * @param radius the radius
     * @param target the output image
     * @return the gradient image
     * @see Gradient#eval(Gradient, float, Lab.AbstrEasing, Lab)
     */
    public static Img gradientRadial(
        final Gradient grd,
        final Vec2 orig,
        final float radius,
        final Img target) {

        return Img.gradientRadial(grd, orig.x, orig.y, radius, new Lab.MixLab(), target);
    }

    /**
     * Generates a conic gradient, where the factor rotates on the z axis around an
     * origin point.
     *
     * @param grd     the gradient
     * @param xOrig   the origin x coordinate
     * @param yOrig   the origin y coordinate
     * @param radians the angle in radians
     * @param target  the target image
     * @return the gradient image
     * @see Gradient#eval(Gradient, float, Lab.AbstrEasing, Lab)
     * @see Utils#mod1(float)
     */
    public static Img gradientSweep(
        final Gradient grd,
        final float xOrig,
        final float yOrig,
        final float radians,
        final Img target) {

        return Img.gradientSweep(grd, xOrig, yOrig, radians, new Lab.MixLab(), target);
    }

    /**
     * Generates a conic gradient, where the factor rotates on the z axis around an
     * origin point. Best
     * used with square images; for other aspect ratios, the origin should be
     * adjusted accordingly.
     *
     * @param grd     the gradient
     * @param xOrig   the origin x coordinate
     * @param yOrig   the origin y coordinate
     * @param radians the angle in radians
     * @param easing  the easing function
     * @param target  the target image
     * @return the gradient image
     * @see Gradient#eval(Gradient, float, Lab.AbstrEasing, Lab)
     * @see Utils#mod1(float)
     */
    public static Img gradientSweep(
        final Gradient grd,
        final float xOrig,
        final float yOrig,
        final float radians,
        final Lab.AbstrEasing easing,
        final Img target) {

        final int wTrg = target.width;
        final int hTrg = target.height;

        final double aspect = wTrg / (double) hTrg;
        final double wInv = aspect / (wTrg - 1.0d);
        final double hInv = 1.0d / (hTrg - 1.0d);
        final double xo = (xOrig * 0.5d + 0.5d) * aspect * 2.0d - 1.0d;

        final Lab trgLab = new Lab();
        final int len = target.pixels.length;
        for (int i = 0; i < len; ++i) {
            final double xn = wInv * (i % wTrg);
            final double yn = hInv * (float) (i / wTrg);
            final float fac = Utils.mod1((float) ((Math.atan2(1.0d
                - (yn + yn + (double) yOrig), xn + xn - xo - 1.0d)
                - (double) radians) * Utils.ONE_TAU_D));
            Gradient.eval(grd, fac, easing, trgLab);
            target.pixels[i] = trgLab.toHexLongSat();
        }

        return target;
    }

    /**
     * Generates a conic gradient, where the factor rotates on the z axis
     * around an origin point.
     *
     * @param grd    the gradient
     * @param xOrig  the origin x coordinate
     * @param yOrig  the origin y coordinate
     * @param target the target image
     * @return the gradient image
     * @see Gradient#eval(Gradient, float, Lab.AbstrEasing, Lab)
     * @see Utils#mod1(float)
     */
    public static Img gradientSweep(
        final Gradient grd,
        final float xOrig,
        final float yOrig,
        final Img target) {

        return Img.gradientSweep(grd, xOrig, yOrig, Utils.HALF_PI, new Lab.MixLab(), target);
    }

    /**
     * Generates a conic gradient, where the factor rotates on the z axis around an
     * origin point.
     *
     * @param grd    the gradient
     * @param orig   the origin
     * @param angle  the angle
     * @param target the target image
     * @return the gradient image
     * @see Gradient#eval(Gradient, float, Lab.AbstrEasing, Lab)
     * @see Utils#mod1(float)
     */
    public static Img gradientSweep(
        final Gradient grd,
        final Vec2 orig,
        final float angle,
        final Img target) {

        return Img.gradientSweep(grd, orig.x, orig.y, angle, new Lab.MixLab(), target);
    }

    /**
     * Diminishes the vividness an image to gray by a factor in [0.0, 1.0].
     *
     * @param source the source image
     * @param fac    the factor
     * @param target the target image
     * @return the gray image
     */
    public static Img grayscale(final Img source, final float fac, final Img target) {

        if (Float.isNaN(fac) || fac >= 1.0f) {
            return Img.grayscale(source, target);
        }

        if (!Img.similar(source, target)) {
            target.width = source.width;
            target.height = source.height;
            target.pixels = new long[source.pixels.length];
        }

        final int len = source.pixels.length;
        if (fac <= 0.0f) {
            System.arraycopy(source.pixels, 0, target.pixels, 0, len);
            return target;
        }

        final float u = 1.0f - fac;
        final Lab lab = new Lab();
        final HashMap<Long, Long> convert = new HashMap<>();
        convert.put(Img.CLEAR_PIXEL, Img.CLEAR_PIXEL);

        for (int i = 0; i < len; ++i) {
            final long srcPixel = source.pixels[i];
            final Long srcPixelObj = srcPixel;
            long trgPixel;

            if (convert.containsKey(srcPixelObj)) {
                trgPixel = convert.get(srcPixelObj);
            } else {
                Lab.fromHex(srcPixel, lab);
                lab.a *= u;
                lab.b *= u;
                trgPixel = lab.toHexLongSat();
                convert.put(srcPixelObj, trgPixel);
            }

            target.pixels[i] = trgPixel;
        }

        return target;
    }

    /**
     * Converts an image to gray.
     *
     * @param source the source image
     * @param target the target image
     * @return the gray image
     */
    public static Img grayscale(final Img source, final Img target) {

        if (!Img.similar(source, target)) {
            target.width = source.width;
            target.height = source.height;
            target.pixels = new long[source.pixels.length];
        }

        final int len = source.pixels.length;
        for (int i = 0; i < len; ++i) {
            target.pixels[i] = source.pixels[i] & Img.TL_MASK | Img.CLEAR_PIXEL;
        }

        return target;
    }

    /**
     * Inverts all channels of an image.
     *
     * @param source the input image
     * @param target the output image
     * @return the inverted image
     */
    public static Img invert(final Img source, final Img target) {

        return Img.invert(source, Img.TLAB_MASK, target);
    }

    /**
     * Inverts the chroma in an image.
     *
     * @param source the input image
     * @param target the output image
     * @return the inverted image
     */
    public static Img invertAB(final Img source, final Img target) {

        return Img.invert(source, Img.AB_MASK, target);
    }

    /**
     * Inverts the transparency in an image.
     *
     * @param source the input image
     * @param target the output image
     * @return the inverted image
     */
    public static Img invertAlpha(final Img source, final Img target) {

        return Img.invert(source, Img.T_MASK, target);
    }

    /**
     * Inverts the l, a and b channels in an image.
     *
     * @param source the input image
     * @param target the output image
     * @return the inverted image
     */
    public static Img invertLab(final Img source, final Img target) {

        return Img.invert(source, Img.LAB_MASK, target);
    }

    /**
     * Inverts the lightness in an image.
     *
     * @param source the input image
     * @param target the output image
     * @return the inverted image
     */
    public static Img invertLight(final Img source, final Img target) {

        return Img.invert(source, Img.L_MASK, target);
    }

    /**
     * Tests whether an image contains all clear pixels.
     *
     * @param source the source image
     * @return the evaluation
     */
    public static boolean isClear(final Img source) {

        return Img.isClearCasual(source);
    }

    /**
     * Tests whether an image contains all clear pixels. Pixels must have
     * zero alpha to be considered clear.
     * However, they may have other non-zero channels.
     *
     * @param source the source image
     * @return the evaluation
     */
    public static boolean isClearCasual(final Img source) {

        final int len = source.pixels.length;
        for (int i = 0; i < len; ++i) {
            if ((source.pixels[i] & Img.T_MASK) != 0L) {
                return false;
            }
        }
        return true;
    }

    /**
     * Tests whether an image contains all clear pixels. Pixels must be equal
     * to {@link Img#CLEAR_PIXEL} to be considered clear.
     *
     * @param source the source image
     * @return the evaluation
     */
    public static boolean isClearStrict(final Img source) {

        final int len = source.pixels.length;
        for (int i = 0; i < len; ++i) {
            if (source.pixels[i] != Img.CLEAR_PIXEL) {
                return false;
            }
        }
        return true;
    }

    /**
     * Mirrors, or reflects, pixels from a source image across the axis
     * described by an origin and destination. Coordinates are expected to be
     * in the range [-1.0, 1.0]. Out-of-bounds pixels are omitted from
     * the mirror.
     *
     * @param source the source image
     * @param xOrig  the origin x
     * @param yOrig  the origin y
     * @param xDest  the destination x
     * @param yDest  the destination y
     * @param flip   the flip reflection flag
     * @param target the target image
     * @return the mirrored image
     */
    public static Img mirror(
        final Img source,
        final float xOrig,
        final float yOrig,
        final float xDest,
        final float yDest,
        final boolean flip,
        final Img target) {

        final int wSrc = source.width;
        final int hSrc = source.height;
        final int srcLen = source.pixels.length;

        if (!Img.similar(source, target)) {
            target.width = wSrc;
            target.height = hSrc;
            target.pixels = new long[srcLen];
        }

        final float wfn1 = wSrc - 1.0f;
        final float hfn1 = hSrc - 1.0f;
        final float wfp1Half = (wSrc + 1.0f) * 0.5f;
        final float hfp1Half = (hSrc + 1.0f) * 0.5f;

        final float ax = (xOrig + 1.0f) * wfp1Half - 0.5f;
        final float bx = (xDest + 1.0f) * wfp1Half - 0.5f;
        final float ay = (yOrig - 1.0f) * -hfp1Half - 0.5f;
        final float by = (yDest - 1.0f) * -hfp1Half - 0.5f;

        final float dx = bx - ax;
        final float dy = by - ay;
        final boolean dxZero = Utils.approx(dx, 0.0f, 0.5f);
        final boolean dyZero = Utils.approx(dy, 0.0f, 0.5f);

        final int trgLen = target.pixels.length;
        if (dxZero && dyZero) {
            System.arraycopy(source.pixels, 0, target.pixels, 0, Math.min(srcLen, trgLen));
            return target;
        }

        if (dxZero) {
            return Img.mirrorX(source, Utils.round(bx), flip ? ay > by : by > ay, target);
        }

        if (dyZero) {
            return Img.mirrorY(source, Utils.round(by), flip ? bx > ax : ax > bx, target);
        }

        final float dMagSqInv = 1.0f / (dx * dx + dy * dy);
        final float flipSign = flip ? -1.0f : 1.0f;

        for (int k = 0; k < trgLen; ++k) {
            final float cy = (float) (k / wSrc);
            final float ey = cy - ay;
            final float cx = k % wSrc;
            final float ex = cx - ax;

            final float cross = ex * dy - ey * dx;
            if (flipSign * cross < 0.0f) {
                target.pixels[k] = source.pixels[k];
            } else {
                final float t = (ex * dx + ey * dy) * dMagSqInv;
                final float u = 1.0f - t;

                final float pyProj = u * ay + t * by;
                final float pyOpp = pyProj + pyProj - cy;

                final float pxProj = u * ax + t * bx;
                final float pxOpp = pxProj + pxProj - cx;

                /*
                 * Default to omitting pixels that are out-of-bounds, rather than
                 * wrapping with floor modulo or clamping.
                 */
                if (pyOpp >= 0.0f && pyOpp <= hfn1 && pxOpp >= 0.0f && pxOpp <= wfn1) {
                    target.pixels[k] = Img.sampleBilinear(source, pxOpp, pyOpp);
                } else {
                    target.pixels[k] = Img.CLEAR_PIXEL;
                }
            }
        }

        return target;
    }

    /**
     * Mirrors, or reflects, pixels from a source image across the axis
     * described by an origin and destination. Coordinates are expected to be
     * in the range [-1.0, 1.0].
     *
     * @param source the source image
     * @param xOrig  the origin x
     * @param yOrig  the origin y
     * @param xDest  the destination x
     * @param yDest  the destination y
     * @param target the target image
     * @return the mirrored image
     */
    public static Img mirror(
        final Img source,
        final float xOrig,
        final float yOrig,
        final float xDest,
        final float yDest,
        final Img target) {

        return Img.mirror(source, xOrig, yOrig, xDest, yDest, false, target);
    }

    /**
     * Mirrors, or reflects, pixels from a source image across the axis
     * described by an origin and destination.
     * Coordinates are expected to be in the range [-1.0, 1.0].
     *
     * @param source the source image
     * @param orig   the origin
     * @param dest   the destination
     * @param target the target image
     * @return the mirrored image
     */
    public static Img mirror(
        final Img source,
        final Vec2 orig,
        final Vec2 dest,
        final Img target) {

        return Img.mirror(source, orig.x, orig.y, dest.x, dest.y, false, target);
    }

    /**
     * Mirrors, or reflects, pixels from a source image horizontally across
     * a pivot. The pivot is expected to be in [-1, width + 1].
     *
     * @param source the input image
     * @param pivot  the x pivot
     * @param flip   the flip reflection flag
     * @param target the output image
     * @return the mirrored image
     */
    public static Img mirrorX(
        final Img source,
        final int pivot,
        final boolean flip,
        final Img target) {

        final int w = source.width;
        final int h = source.height;
        final int srcLen = source.pixels.length;

        if (!Img.similar(source, target)) {
            target.width = w;
            target.height = h;
            target.pixels = new long[srcLen];
        }

        final int flipSign = flip ? 1 : -1;
        for (int k = 0; k < srcLen; ++k) {
            final int cross = k % w - pivot;
            if (flipSign * cross < 0) {
                target.pixels[k] = source.pixels[k];
            } else {
                final int pxOpp = pivot - cross;
                if (pxOpp >= 0 && pxOpp < w) {
                    target.pixels[k] = source.pixels[k / w * w + pxOpp];
                } else {
                    target.pixels[k] = Img.CLEAR_PIXEL;
                }
            }
        }

        return target;
    }

    /**
     * Mirrors, or reflects, pixels from a source image vertically across a
     * pivot. The pivot is expected to be in [-1, height + 1]. Positive y
     * points down to the bottom of the image.
     *
     * @param source the input image
     * @param pivot  the y pivot
     * @param flip   the flip reflection flag
     * @param target the output image
     * @return the mirrored image
     */
    public static Img mirrorY(
        final Img source,
        final int pivot,
        final boolean flip,
        final Img target) {

        final int w = source.width;
        final int h = source.height;
        final int srcLen = source.pixels.length;

        if (!Img.similar(source, target)) {
            target.width = w;
            target.height = h;
            target.pixels = new long[srcLen];
        }

        final int flipSign = flip ? 1 : -1;
        for (int k = 0; k < srcLen; ++k) {
            final int cross = k / w - pivot;
            if (flipSign * cross < 0) {
                target.pixels[k] = source.pixels[k];
            } else {
                final int pyOpp = pivot - cross;
                if (pyOpp > -1 && pyOpp < h) {
                    target.pixels[k] = source.pixels[pyOpp * w + k % w];
                } else {
                    target.pixels[k] = Img.CLEAR_PIXEL;
                }
            }
        }

        return target;
    }

    /**
     * Mixes between two images by a factor.
     *
     * @param orig   the origin image
     * @param dest   the destination image
     * @param fac    the factor
     * @param target the output image
     * @return the mixed image
     */
    public static Img mix(
        final Img orig,
        final Img dest,
        final float fac,
        final Img target) {

        return Img.mix(orig, dest, fac, new Lab.MixLab(), target);
    }

    /**
     * Mixes between two images by a factor.
     *
     * @param orig   the origin image
     * @param dest   the destination image
     * @param fac    the factor
     * @param mixer  the mixing function
     * @param target the output image
     * @return the mixed image
     */
    public static Img mix(
        final Img orig,
        final Img dest,
        final float fac,
        final Lab.AbstrEasing mixer,
        final Img target) {

        if (!Img.similar(orig, dest)) {
            System.err.println("Cannot mix between two images of unequal sizes.");
            return target;
        }

        if (!Img.similar(orig, target)) {
            target.width = orig.width;
            target.height = orig.height;
            target.pixels = new long[orig.pixels.length];
        }

        final int len = target.pixels.length;
        final float t = Float.isNaN(fac) ? 0.5f : fac;

        if (t <= 0.0f) {
            System.arraycopy(orig.pixels, 0, target.pixels, 0, len);
            return target;
        }

        if (t >= 1.0f) {
            System.arraycopy(dest.pixels, 0, target.pixels, 0, len);
            return target;
        }

        final Float tObj = t;
        final Lab oLab = new Lab();
        final Lab dLab = new Lab();
        final Lab tLab = new Lab();

        for (int i = 0; i < len; ++i) {
            Lab.fromHex(dest.pixels[i], dLab);
            Lab.fromHex(orig.pixels[i], oLab);
            mixer.applyUnclamped(oLab, dLab, tObj, tLab);
            target.pixels[i] = tLab.toHexLongSat();
        }

        return target;
    }

    /**
     * Mixes between two images.
     *
     * @param orig   the origin image
     * @param dest   the destination image
     * @param target the output image
     * @return the mixed image
     */
    public static Img mix(final Img orig, final Img dest, final Img target) {

        return Img.mix(orig, dest, 0.5f, new Lab.MixLab(), target);
    }

    /**
     * Multiplies the image's alpha by the scalar. Expected range is within
     * [0.0, 1.0]. Clears the image if the alpha is less than or equal to zero.
     *
     * @param source the input image
     * @param a01    the alpha scalar
     * @param target the output image
     * @return the multiplied alpha
     */
    public static Img mulAlpha(
        final Img source,
        final float a01,
        final Img target) {

        final int len = source.pixels.length;
        if (!Img.similar(source, target)) {
            target.width = source.width;
            target.height = source.height;
            target.pixels = new long[len];
        }

        if (a01 <= 0.0f) {
            return Img.clear(target);
        }
        if (a01 == 1.0f) {
            System.arraycopy(source.pixels, 0, target.pixels, 0, len);
            return target;
        }

        final long alpha = Utils.round(Utils.abs(a01) * 0xffff);
        for (int i = 0; i < len; ++i) {
            final long c = source.pixels[i];
            final long t16Src = c >> Img.T_SHIFT & 0xffffL;
            final long t16Trg = t16Src * alpha / 0xffffL;
            final long t16TrgCl = Math.min(t16Trg, 0xffffL);
            target.pixels[i] = t16TrgCl << Img.T_SHIFT | c & Img.LAB_MASK;
        }

        return target;
    }

    /**
     * Normalizes an image's lightness so that it fills the complete range from
     * [0.0, 100.0]. Accepts a factor in [-1.0, 1.0]. If the factor is
     * negative, reduces contrast towards the average lightness.
     *
     * @param source the input image
     * @param fac    the factor
     * @param target the output image
     * @return the normalized image
     */
    public static Img normalizeLight(
        final Img source,
        final float fac,
        final Img target) {

        if (!Img.similar(source, target)) {
            target.width = source.width;
            target.height = source.height;
            target.pixels = new long[source.pixels.length];
        }

        final int len = source.pixels.length;
        final float facVerif = Float.isNaN(fac)
            ? 1.0f
            : Utils.clamp(fac, -1.0f, 1.0f);

        if (Utils.approx(facVerif, 0.0f)) {
            System.arraycopy(source.pixels, 0, target.pixels, 0, len);
            return target;
        }

        final HashMap<Long, Lab> uniques = new HashMap<>();

        float minLight = Float.MAX_VALUE;
        float maxLight = -Float.MAX_VALUE;
        float sumLight = 0.0f;
        int sumTally = 0;

        for (int i = 0; i < len; ++i) {
            final long srcPixel = source.pixels[i];
            final Long srcPixelObj = srcPixel;
            if (!uniques.containsKey(srcPixelObj)) {
                final Lab lab = Lab.fromHex(srcPixel, new Lab());
                if (lab.alpha > 0.0f) {
                    final float light = lab.l;
                    if (light > maxLight) {
                        maxLight = light;
                    }
                    if (light < minLight) {
                        minLight = light;
                    }
                    sumLight += light;
                    ++sumTally;
                }
                uniques.put(srcPixelObj, lab);
            }
        }

        final float dff = Utils.diff(maxLight, minLight);
        if (sumTally == 0 || dff < Img.MIN_LIGHT_DIFF) {
            System.arraycopy(source.pixels, 0, target.pixels, 0, len);
            return target;
        }

        final float t = Utils.abs(facVerif);
        final float u = 1.0f - t;
        final boolean gtZero = facVerif > 0.0f;
        final boolean ltZero = facVerif < -0.0f;

        final float tLumAvg = t * (sumLight / sumTally);
        final float tDenom = t * (100.0f / dff);
        final float lumMintDenom = minLight * tDenom;

        final Lab defLab = Lab.clearBlack(new Lab());
        final HashMap<Long, Long> convert = new HashMap<>();
        convert.put(Img.CLEAR_PIXEL, Img.CLEAR_PIXEL);

        for (int j = 0; j < len; ++j) {
            final Long srcPixelObj = source.pixels[j];
            long trgPixel;

            if (convert.containsKey(srcPixelObj)) {
                trgPixel = convert.get(srcPixelObj);
            } else {
                final Lab lab = uniques.getOrDefault(srcPixelObj, defLab);

                if (gtZero) {
                    lab.l = u * lab.l + lab.l * tDenom - lumMintDenom;
                } else if (ltZero) {
                    lab.l = u * lab.l + tLumAvg;
                }

                trgPixel = lab.toHexLongSat();
                convert.put(srcPixelObj, trgPixel);
            }

            target.pixels[j] = trgPixel;
        }

        return target;
    }

    /**
     * Normalizes an image's lightness so that it fills the complete range from
     * [0.0, 100.0].
     *
     * @param source the input image
     * @param target the output image
     * @return the normalized image
     */
    public static Img normalizeLight(final Img source, final Img target) {

        return Img.normalizeLight(source, 1.0f, target);
    }

    /**
     * Sets all pixels in an image to opaque.
     *
     * @param source the input image
     * @param target the output image
     * @return the opaque image
     */
    public static Img opaque(final Img source, final Img target) {

        final int len = source.pixels.length;
        if (!Img.similar(source, target)) {
            target.width = source.width;
            target.height = source.height;
            target.pixels = new long[len];
        }

        for (int i = 0; i < len; ++i) {
            target.pixels[i] = source.pixels[i] | Img.T_MASK;
        }
        return target;
    }

    /**
     * Extracts a palette from an image. If there are more colors than the
     * threshold, engages an octree to reduce the number of colors.
     *
     * @param source the input image
     * @return the palette
     */
    public static Lab[] paletteExtract(final Img source) {

        return Img.paletteExtract(
            source,
            Img.DEFAULT_PALETTE_THRESHOLD,
            Octree.DEFAULT_CAPACITY,
            false);
    }

    /**
     * Extracts a palette from an image. If there are more colors than the
     * threshold, engages an
     * octree to reduce the number of colors.
     *
     * @param source    the input image
     * @param threshold the threshold
     * @return the palette
     */
    public static Lab[] paletteExtract(
        final Img source,
        final int threshold) {

        return Img.paletteExtract(
            source,
            threshold,
            Octree.DEFAULT_CAPACITY,
            false);
    }

    /**
     * Extracts a palette from an image. If there are more colors than the
     * threshold, engages an octree to reduce the number of colors.
     *
     * @param source    the input image
     * @param threshold the threshold
     * @param capacity  the octree capacity
     * @return the palette
     */
    public static Lab[] paletteExtract(
        final Img source,
        final int threshold,
        final int capacity) {

        return Img.paletteExtract(source, threshold, capacity, false);
    }

    /**
     * Extracts a palette from an image. If there are more colors than the
     * threshold, engages an octree to reduce the number of colors. Alpha is
     * no longer supported once the octree is engaged.
     * <br>
     * <br>
     * Clear black is always the first entry in the palette.
     *
     * @param source    the input image
     * @param threshold the threshold
     * @param capacity  the octree capacity
     * @param inclAlpha whether to include alpha
     * @return the palette
     */
    public static Lab[] paletteExtract(
        final Img source,
        final int threshold,
        final int capacity,
        final boolean inclAlpha) {

        final long mask = inclAlpha ? 0 : Img.T_MASK;
        final long[] srcPixels = source.pixels;
        final HashMap<Long, Integer> uniqueOpaques = new HashMap<>();

        /* Account for alpha at index 0, so less than threshold. */
        int tally = 0;
        for (final long tlab64 : srcPixels) {
            if ((tlab64 & Img.T_MASK) != 0L) {
                final Long masked = mask | tlab64;
                if (!uniqueOpaques.containsKey(masked)) {
                    ++tally;
                    uniqueOpaques.put(masked, tally);
                }
            }
        }

        final int uniquesLen = uniqueOpaques.size();
        final Iterator<Entry<Long, Integer>> uniquesItr = uniqueOpaques.entrySet().iterator();

        final int valThresh = Math.max(threshold, 3);
        if (uniquesLen < valThresh) {
            final Lab[] arr = new Lab[1 + uniquesLen];
            while (uniquesItr.hasNext()) {
                final Entry<Long, Integer> entry = uniquesItr.next();
                arr[entry.getValue()] = Lab.fromHex(entry.getKey(), new Lab());
            }
            arr[0] = Lab.clearBlack(new Lab());
            return arr;
        }

        final Octree oct = new Octree(Bounds3.lab(new Bounds3()), capacity);
        final Lab lab = new Lab();

        /* Place colors in octree. */
        while (uniquesItr.hasNext()) {
            final Entry<Long, Integer> entry = uniquesItr.next();
            Lab.fromHex(entry.getKey(), lab);
            oct.insert(new Vec3(lab.a, lab.b, lab.l));
        }
        oct.cull();

        /* Trying to use package level with an array list throws an exception. */
        final Vec3[] centers = Octree.centersMean(oct, false);
        final int centersLen = centers.length;
        final Lab[] arr = new Lab[1 + centersLen];

        for (int i = 0; i < centersLen; ++i) {
            final Vec3 center = centers[i];
            arr[1 + i] = new Lab(center.z, center.x, center.y, 1.0f);
        }

        arr[0] = Lab.clearBlack(new Lab());
        return arr;
    }

    /**
     * Applies a palette to an image. Uses an Octree to find the nearest match in
     * Euclidean space. Retains the original color's transparency.
     *
     * @param source  the source pixels
     * @param palette the color palette
     * @param target  the target pixels
     * @return the mapped image
     */
    public static Img paletteMap(
        final Img source,
        final Lab[] palette,
        final Img target) {

        return Img.paletteMap(
            source,
            palette,
            Octree.DEFAULT_CAPACITY,
            Img.DEFAULT_OCTREE_QUERY_RADIUS,
            target);
    }

    /**
     * Applies a palette to an image. Uses an Octree to find the nearest match
     * in Euclidean space. Retains the original color's transparency.
     *
     * @param source   the source pixels
     * @param palette  the color palette
     * @param capacity the octree capacity
     * @param radius   the query radius
     * @param target   the target pixels
     * @return the mapped image
     */
    public static Img paletteMap(
        final Img source,
        final Lab[] palette,
        final int capacity,
        final float radius,
        final Img target) {

        // TODO: This needs a dither function in order to be useful...

        final int len = source.pixels.length;
        if (!Img.similar(source, target)) {
            target.width = source.width;
            target.height = source.height;
            target.pixels = new long[len];
        }

        final Octree oct = new Octree(Bounds3.lab(new Bounds3()), capacity);
        oct.subdivide(1, capacity);

        final HashMap<Vec3, Long> lookup = new HashMap<>(256, 0.75f);
        for (final Lab swatch : palette) {
            if (Lab.any(swatch)) {
                final Vec3 point = new Vec3(swatch.a, swatch.b, swatch.l);
                oct.insert(point);
                lookup.put(point, swatch.toHexLongSat());
            }
        }

        oct.cull();

        final float rVrf = Math.max(Utils.EPSILON, Utils.abs(radius));
        final float rsq = rVrf * rVrf;
        final Lab lab = new Lab();
        final Vec3 query = new Vec3();
        final TreeMap<Float, Vec3> found = new TreeMap<>();
        final HashMap<Long, Long> convert = new HashMap<>();
        convert.put(Img.CLEAR_PIXEL, Img.CLEAR_PIXEL);

        for (int j = 0; j < len; ++j) {
            final long tlab64Src = source.pixels[j];
            final long alphaSrc = tlab64Src & Img.T_MASK;
            final Long tlab64SrcObj = tlab64Src;
            long tlab64Trg = Img.CLEAR_PIXEL;
            if (convert.containsKey(tlab64SrcObj)) {
                tlab64Trg = convert.get(tlab64SrcObj);
            } else {
                Lab.fromHex(tlab64Src, lab);
                query.set(lab.a, lab.b, lab.l);
                found.clear();
                Octree.query(oct, query, rsq, 0, found);
                if (!found.isEmpty()) {
                    final Vec3 near = found.values().iterator().next();
                    if (near != null && lookup.containsKey(near)) {
                        tlab64Trg = lookup.get(near);
                    }
                }
                convert.put(tlab64SrcObj, tlab64Trg);
            }
            target.pixels[j] = alphaSrc | tlab64Trg & Img.LAB_MASK;
        }

        return target;
    }

    /**
     * Applies a palette to an image. Uses an Octree to find the nearest match in
     * Euclidean space.
     * Retains the original color's transparency.
     *
     * @param source   the source pixels
     * @param palette  the color palette
     * @param capacity the octree capacity
     * @param target   the target pixels
     * @return the mapped image
     */
    public static Img paletteMap(
        final Img source,
        final Lab[] palette,
        final int capacity,
        final Img target) {

        return Img.paletteMap(source, palette, capacity,
            Img.DEFAULT_OCTREE_QUERY_RADIUS, target);
    }

    /**
     * Generates an image with random pixels for diagnostic purposes.
     *
     * @param rng       the random number generator
     * @param inclAlpha whether to include alpha
     * @param target    the output image
     * @return the image
     */
    public static Img random(
        final Random rng,
        final boolean inclAlpha,
        final Img target) {

        final long mask = inclAlpha ? 0 : Img.T_MASK;
        final int len = target.pixels.length;
        for (int i = 0; i < len; ++i) {
            target.pixels[i] = mask | rng.nextLong();
        }
        return target;
    }

    /**
     * Removes translucent pixels from an image, so colors are either
     * transparent or opaque.
     *
     * @param source the input image
     * @param target the output image
     * @return the binary alpha image
     */
    public static Img removeTranslucency(final Img source, final Img target) {

        final int len = source.pixels.length;
        if (!Img.similar(source, target)) {
            target.width = source.width;
            target.height = source.height;
            target.pixels = new long[len];
        }

        for (int i = 0; i < len; ++i) {
            final long c = source.pixels[i];
            final long t16Src = c >> Img.T_SHIFT & 0xffffL;
            final long t16Trg = t16Src < 0x8000L ? 0x0000L : 0xffffL;
            target.pixels[i] = t16Trg << Img.T_SHIFT | c & Img.LAB_MASK;
        }
        return target;
    }

    /**
     * Replaces source pixel colors that are similar to the fromColor with
     * the toColor.<br>
     * <br>
     * The tolerance is the radius beneath which colors are considered similar.<br>
     * <br>
     * The alpha scalar adjusts how much alpha contributes to color difference.
     * Set it to zero for none, or to 100 for parity with lightness.
     *
     * @param source      the source image
     * @param fromColor   the color to replace
     * @param toColor     the substitute color
     * @param tolerance   the distance radius
     * @param alphaScalar alpha scalar
     * @param target      the output image
     * @return the updated image
     */
    public static Img replaceColorApprox(
        final Img source,
        final Lab fromColor,
        final Lab toColor,
        final float tolerance,
        final float alphaScalar,
        final Img target) {

        if (tolerance <= 0.0f) {
            return Img.replaceColorExact(source, fromColor, toColor, target);
        }

        final int len = source.pixels.length;
        if (!Img.similar(source, target)) {
            target.width = source.width;
            target.height = source.height;
            target.pixels = new long[len];
        }

        final long toPixel = toColor.toHexLongSat();
        final Lab lab = new Lab();
        final HashMap<Long, Long> convert = new HashMap<>();
        for (int i = 0; i < len; ++i) {
            final long srcPixel = source.pixels[i];
            final Long srcPixelObj = srcPixel;
            long trgPixel;
            if (convert.containsKey(srcPixelObj)) {
                trgPixel = convert.get(srcPixelObj);
            } else {
                Lab.fromHex(srcPixel, lab);
                trgPixel = Lab.dist(lab, fromColor, alphaScalar)
                    <= tolerance ? toPixel : srcPixel;
                convert.put(srcPixelObj, trgPixel);
            }
            target.pixels[i] = trgPixel;
        }

        return target;
    }

    /**
     * Replaces source pixel colors that are equal to the fromColor with the
     * toColor.
     *
     * @param source    the input image
     * @param fromColor the color to replace
     * @param toColor   the substitute color
     * @param target    the output image
     * @return the updated image
     */
    public static Img replaceColorExact(
        final Img source,
        final Lab fromColor,
        final Lab toColor,
        final Img target) {

        final int len = source.pixels.length;
        if (!Img.similar(source, target)) {
            target.width = source.width;
            target.height = source.height;
            target.pixels = new long[len];
        }

        final long frPixel = fromColor.toHexLongSat();
        final long toPixel = toColor.toHexLongSat();

        for (int i = 0; i < len; ++i) {
            final long srcPixel = source.pixels[i];
            target.pixels[i] = srcPixel == frPixel ? toPixel : srcPixel;
        }

        return target;
    }

    /**
     * Resizes a source image and places the pixels into a target image.
     *
     * @param source the source image
     * @param wTrg   the target width
     * @param hTrg   the target height
     * @param target the target image
     * @return the resized image
     */
    public static Img resizeBilinear(
        final Img source,
        final int wTrg,
        final int hTrg,
        final Img target) {

        final int wTrgVerif = Utils.clamp(Math.abs(wTrg), 1, Img.MAX_DIMENSION);
        final int hTrgVerif = Utils.clamp(Math.abs(hTrg), 1, Img.MAX_DIMENSION);

        final int wSrc = source.width;
        final int hSrc = source.height;
        final int srcLen = source.pixels.length;

        if (wSrc == wTrgVerif && hSrc == hTrgVerif) {
            target.width = wSrc;
            target.height = hSrc;
            target.pixels = new long[srcLen];
            System.arraycopy(source.pixels, 0, target.pixels, 0, srcLen);
            return target;
        }

        final float wDenom = wTrgVerif - 1.0f;
        final float hDenom = hTrgVerif - 1.0f;
        final float tx = wDenom != 0.0f ? (wSrc - 1.0f) / wDenom : 0.0f;
        final float ty = hDenom != 0.0f ? (hSrc - 1.0f) / hDenom : 0.0f;
        final float ox = wDenom != 0.0f ? 0.0f : 0.5f;
        final float oy = hDenom != 0.0f ? 0.0f : 0.5f;

        final int trgLen = wTrgVerif * hTrgVerif;
        final long[] trgPixels = new long[trgLen];

        for (int i = 0; i < trgLen; ++i) {
            trgPixels[i] = Img.sampleBilinear(source,
                tx * (i % wTrgVerif) + ox,
                ty * ((float) (i / wTrgVerif)) + oy);
        }

        target.width = wTrgVerif;
        target.height = hTrgVerif;
        target.pixels = trgPixels;

        return target;
    }

    /**
     * Creates an image with a diagnostic image where the pixel's x coordinate
     * correlates to the red channel; its y coordinate to the green channel.
     * The blue and alpha channels are expected to be in [0.0, 1.0].
     *
     * @param blue   the blue channel
     * @param alpha  the alpha channel
     * @param target the output image
     * @return the RGB square
     */
    public static Img rgb(
        final float blue,
        final float alpha,
        final Img target) {

        final int w = target.width;
        final int h = target.height;

        /*
         * If user wants to make a zero alpha image that has rgb channels,
         * let them.
         */
        final float tVerif = Utils.clamp01(alpha);
        final float bVerif = Utils.clamp01(blue);
        final float yNorm = 1.0f / (h - 1.0f);
        final float xNorm = 1.0f / (w - 1.0f);

        final Rgb srgb = new Rgb();
        final Rgb lrgb = new Rgb();
        final Vec4 xyz = new Vec4();
        final Lab lab = new Lab();

        final int len = target.pixels.length;
        for (int i = 0; i < len; ++i) {
            final int x = i % w;
            final int y = i / w;
            srgb.set(x * xNorm, 1.0f - y * yNorm, bVerif, tVerif);
            Rgb.sRgbToSrLab2(srgb, lab, xyz, lrgb);
            target.pixels[i] = lab.toHexLongSat();
        }

        return target;
    }

    /**
     * Creates an image with a diagnostic image where the pixel's x coordinate
     * correlates to the red channel; its y coordinate to the green channel.
     * The blue channel is expected to be in [0.0, 1.0].
     *
     * @param blue   the blue channel
     * @param target the output image
     * @return the RGB square
     */
    public static Img rgb(final float blue, final Img target) {

        return Img.rgb(blue, Img.DEFAULT_ALPHA, target);
    }

    /**
     * Creates an image with a diagnostic image where the pixel's x coordinate
     * correlates to the red channel; its y coordinate to the green channel.
     *
     * @param target the output image
     * @return the RGB square
     */
    public static Img rgb(final Img target) {

        return Img.rgb(Img.DEFAULT_BLUE, Img.DEFAULT_ALPHA, target);
    }

    /**
     * Rotates the pixels of a source image around the image center by an angle
     * in radians.
     *
     * @param source the source image
     * @param angle  the angle in radians
     * @param target the target image
     * @return rotated image
     * @see Utils#mod(int, int)
     * @see Utils#round(float)
     */
    public static Img rotate(
        final Img source,
        final float angle,
        final Img target) {

        return Img.rotateBilinear(source, angle, target);
    }

    /**
     * Rotates the source pixel array 180 degrees counter-clockwise.
     * The rotation is stored in the target pixel array.
     *
     * @param source the source image
     * @param target the target image
     * @return the rotated image
     */
    public static Img rotate180(final Img source, final Img target) {

        final int len = source.pixels.length;
        if (!Img.similar(source, target)) {
            target.width = source.width;
            target.height = source.height;
            target.pixels = new long[len];
        }

        for (int i = 0, j = len - 1; i < len; ++i, --j) {
            target.pixels[j] = source.pixels[i];
        }

        return target;
    }

    /**
     * Rotates the source image 270 degrees counter-clockwise (90 degrees
     * clockwise). The rotation is stored in the target. Changes the target's
     * width and height.
     *
     * @param source the input image
     * @param target the output image
     * @return the rotated image
     */
    public static Img rotate270(final Img source, final Img target) {

        final int len = source.pixels.length;
        if (!Img.similar(source, target)) {
            target.pixels = new long[len];
        }

        final int w = source.width;
        final int h = source.height;
        final int hn1 = h - 1;
        for (int i = 0; i < len; ++i) {
            target.pixels[i % w * h + hn1 - i / w] = source.pixels[i];
        }

        target.width = h;
        target.height = w;

        return target;
    }

    /**
     * Rotates the source image 90 degrees counter-clockwise. The rotation is
     * stored in the target. Changes the target's width and height.
     *
     * @param source the input image
     * @param target the output image
     * @return the rotated image
     */
    public static Img rotate90(final Img source, final Img target) {

        final int len = source.pixels.length;
        if (!Img.similar(source, target)) {
            target.pixels = new long[len];
        }

        final int w = source.width;
        final int h = source.height;
        final int lennh = len - h;
        for (int i = 0; i < len; ++i) {
            target.pixels[lennh + i / w - i % w * h] = source.pixels[i];
        }

        target.width = h;
        target.height = w;

        return target;
    }

    /**
     * Rotates the pixels of a source image around the image center by an angle
     * in radians. Where the angle is approximately 0, 90, 180 and 270 degrees,
     * resorts to faster methods. Uses bilinear filtering.
     *
     * @param source the source pixels
     * @param angle  the angle in radians
     * @param target the target pixels
     * @return rotated pixels
     * @see Utils#mod(int, int)
     * @see Utils#round(float)
     */
    public static Img rotateBilinear(
        final Img source,
        final float angle,
        final Img target) {

        final long[] srcPixels = source.pixels;
        final int srcLen = srcPixels.length;
        final int deg = Utils.mod(Utils.round(angle * Utils.RAD_TO_DEG), 360);
        switch (deg) {
            case 0: {
                final long[] trgPixels = new long[srcLen];
                System.arraycopy(srcPixels, 0, trgPixels, 0, srcLen);

                target.width = source.width;
                target.height = source.height;
                target.pixels = trgPixels;

                return target;
            }

            case 90: {
                return Img.rotate90(source, target);
            }
            case 180: {
                return Img.rotate180(source, target);
            }
            case 270: {
                return Img.rotate270(source, target);
            }

            default: {
                return Img.rotateBilinear(
                    source,
                    (float) Math.cos(angle),
                    (float) Math.sin(angle),
                    target);
            }
        }
    }

    /**
     * Scales an image by a percentage of its original width and height.
     * Percentages are expected to be within [0.0, 1.0].
     *
     * @param source the source image
     * @param wPrc   the width percentage
     * @param hPrc   the height percentage
     * @param target the target image
     * @return the scaled image
     * @see Utils#round(float)
     */
    public static Img scaleBilinear(
        final Img source,
        final float wPrc,
        final float hPrc,
        final Img target) {

        return Img.resizeBilinear(
            source, Utils.round(wPrc * source.width),
            Utils.round(hPrc * source.height), target);
    }

    /**
     * Scales an image by a percentage of its original width and height.
     * Percentages are expected to be within [0.0, 1.0].
     *
     * @param source the source image
     * @param prc    the percentage
     * @param target the target image
     * @return the scaled image
     */
    public static Img scaleBilinear(
        final Img source,
        final float prc,
        final Img target) {

        return Img.scaleBilinear(source, prc, prc, target);
    }

    /**
     * Scales an image by a percentage of its original width and height.
     * Percentages are expected to be within [0.0, 1.0].
     *
     * @param source the source image
     * @param prc    the percentage
     * @param target the target image
     * @return the scaled image
     */
    public static Img scaleBilinear(
        final Img source,
        final Vec2 prc,
        final Img target) {

        return Img.scaleBilinear(source, prc.x, prc.y, target);
    }

    /**
     * Separates a source image into 3 images which emphasize the LAB components.
     *
     * @param source         the source image
     * @param usePreMultiply multiply color by alpha
     * @return the separated images
     */
    public static Img[] sepRgb(
        final Img source,
        final boolean usePreMultiply) {

        final int len = source.pixels.length;
        final long[] rPixels = new long[len];
        final long[] gPixels = new long[len];
        final long[] bPixels = new long[len];

        final Rgb srgb = new Rgb();
        final Rgb lrgb = new Rgb();
        final Vec4 xyz = new Vec4();
        final Lab lab = new Lab();

        final Rgb rIso = new Rgb();
        final Rgb gIso = new Rgb();
        final Rgb bIso = new Rgb();

        for (int i = 0; i < len; ++i) {
            final long tlab64 = source.pixels[i];

            Lab.fromHex(tlab64, lab);
            Rgb.srLab2TosRgb(lab, srgb, lrgb, xyz);

            if (usePreMultiply) {
                Rgb.premul(srgb, srgb);
            }
            Rgb.clamp01(srgb, srgb);

            rIso.set(srgb.r, 0.0f, 0.0f, 0.0f);
            gIso.set(0.0f, srgb.g, 0.0f, 0.0f);
            bIso.set(0.0f, 0.0f, srgb.b, 0.0f);

            final long rLab64 = Rgb.sRgbToSrLab2(rIso, lab, xyz, lrgb).toHexLongSat();
            final long gLab64 = Rgb.sRgbToSrLab2(gIso, lab, xyz, lrgb).toHexLongSat();
            final long bLab64 = Rgb.sRgbToSrLab2(bIso, lab, xyz, lrgb).toHexLongSat();

            final long tIso = tlab64 & Img.T_MASK;

            rPixels[i] = tIso | rLab64;
            gPixels[i] = tIso | gLab64;
            bPixels[i] = tIso | bLab64;
        }

        final int w = source.width;
        final int h = source.height;

        return new Img[]{
            new Img(w, h, rPixels),
            new Img(w, h, gPixels),
            new Img(w, h, bPixels)
        };
    }

    /**
     * Sets a region of an image's pixels.
     *
     * @param read  the read image
     * @param write the write image
     * @return the write image
     */
    public static Img setRegion(final Img read, final Img write) {

        return Img.setRegion(read, 0, 0, read.width - 1, read.height - 1, write);
    }

    /**
     * Sets a region of an image's pixels.
     *
     * @param read  the read image
     * @param xtlRd the top left corner x
     * @param ytlRd the top left corner y
     * @param xbrRd the bottom right corner x
     * @param ybrRd the bottom right corner y
     * @param write the write image
     * @return the write image
     */
    public static Img setRegion(
        final Img read,
        final int xtlRd,
        final int ytlRd,
        final int xbrRd,
        final int ybrRd,
        final Img write) {

        return Img.setRegion(read, xtlRd, ytlRd, xbrRd, ybrRd, 0, 0,
            write.width - 1, write.height - 1, write);
    }

    /**
     * Sets a region of an image's pixels.
     *
     * @param read   the read image
     * @param xtlRd  the top left corner x
     * @param ytlRd  the top left corner y
     * @param xbrRd  the bottom right corner x
     * @param ybrRd  the bottom right corner y
     * @param xtlWrt the top left corner x
     * @param ytlWrt the top left corner y
     * @param xbrWrt the bottom right corner x
     * @param ybrWrt the bottom right corner y
     * @param write  the write image
     * @return the write image
     */
    public static Img setRegion(
        final Img read,
        final int xtlRd, final int ytlRd,
        final int xbrRd, final int ybrRd,
        final int xtlWrt, final int ytlWrt,
        final int xbrWrt, final int ybrWrt,
        final Img write) {

        // TODO: TEST

        final int wImgWr = write.width;
        final int hImgWr = write.height;

        int xtlWrVrf = Utils.clamp(xtlWrt, 0, wImgWr - 1);
        int ytlWrVrf = Utils.clamp(ytlWrt, 0, hImgWr - 1);
        int xbrWrVrf = Utils.clamp(xbrWrt, 0, wImgWr - 1);
        int ybrWrVrf = Utils.clamp(ybrWrt, 0, hImgWr - 1);

        /* Swap coordinates in reversed order. */
        xtlWrVrf = Math.min(xtlWrVrf, xbrWrVrf);
        ytlWrVrf = Math.min(ytlWrVrf, ybrWrVrf);

        final int wrWidth = 1 + xbrWrVrf - xtlWrVrf;
        final int wrHeight = 1 + ybrWrVrf - ytlWrVrf;

        final int wImgRd = read.width;
        final int hImgRd = read.height;

        int xtlRdVrf = Utils.clamp(xtlRd, 0, wImgRd);
        int ytlRdVrf = Utils.clamp(ytlRd, 0, hImgRd);
        int xbrRdVrf = Utils.clamp(xbrRd, 0, wImgRd);
        int ybrRdVrf = Utils.clamp(ybrRd, 0, hImgRd);

        /* Swap coordinates in reversed order. */
        xtlRdVrf = Math.min(xtlRdVrf, xbrRdVrf);
        ytlRdVrf = Math.min(ytlRdVrf, ybrRdVrf);

        final int rdWidth = 1 + xbrRdVrf - xtlRdVrf;
        final int rdHeight = 1 + ybrRdVrf - ytlRdVrf;

        final int regWidth = Math.min(rdWidth, wrWidth);
        final int regHeight = Math.min(rdHeight, wrHeight);
        if (regWidth <= 0 || regHeight <= 0) {
            return write;
        }

        final int regArea = regWidth * regHeight;
        for (int i = 0; i < regArea; ++i) {
            final int xReg = i % regWidth;
            final int yReg = i / regWidth;

            final int xWr = xtlWrVrf + xReg;
            final int yWr = ytlWrVrf + yReg;
            final int j = yWr * wImgWr + xWr;

            final int xRd = xtlRdVrf + xReg;
            final int yRd = ytlRdVrf + yReg;
            final int k = yRd * wImgRd + xRd;

            write.pixels[j] = read.pixels[k];
        }

        return write;
    }

    /**
     * Skews the pixels of a source image vertically. If the angle is
     * approximately zero, copies the source array. If the angle is
     * approximately {@link Utils#HALF_PI}, {@value Utils#HALF_PI},
     * returns a clear image.
     *
     * @param source the source image
     * @param angle  the angle in radians
     * @param target the target image
     * @return the skewed array
     */
    public static Img skewXBilinear(
        final Img source,
        final float angle,
        final Img target) {

        final int wSrc = source.width;
        final int hSrc = source.height;
        final int srcLen = source.pixels.length;

        if (!Img.similar(source, target)) {
            target.width = wSrc;
            target.height = hSrc;
            target.pixels = new long[srcLen];
        }

        final int deg = Utils.round(angle * Utils.RAD_TO_DEG);
        final int deg180 = Utils.mod(deg, 180);

        switch (deg180) {
            case 0: {
                System.arraycopy(source.pixels, 0, target.pixels, 0, srcLen);
                return target;
            }

            case 88:
            case 89:
            case 90:
            case 91:
            case 92: {
                return Img.clear(target);
            }

            default: {
                final float tana = (float) Math.tan(angle);
                final int wTrg = (int) (0.5f + (float) wSrc + Utils.abs(tana) * (float) hSrc);
                final float yCenter = (float) hSrc * 0.5f;
                final float xDiff = ((float) wSrc - (float) wTrg) * 0.5f;

                final int trgLen = wTrg * hSrc;
                final long[] trgPixels = new long[trgLen];
                for (int i = 0; i < trgLen; ++i) {
                    final float yTrg = (float) (i / wTrg);
                    trgPixels[i] = Img.sampleBilinear(source,
                        xDiff + i % wTrg + tana * (yTrg - yCenter), yTrg);
                }

                target.width = wTrg;
                target.pixels = trgPixels;

                return target;
            }
        }
    }

    /**
     * Skews the pixels of a source image vertically. If the angle is
     * approximately zero, copies the source array. If the angle is
     * approximately {@link Utils#HALF_PI}, {@value Utils#HALF_PI},
     * returns a clear image.
     *
     * @param source the source image
     * @param angle  the angle in radians
     * @param target the target image
     * @return the skewed array
     */
    public static Img skewYBilinear(
        final Img source,
        final float angle,
        final Img target) {

        final int wSrc = source.width;
        final int hSrc = source.height;
        final int srcLen = source.pixels.length;

        if (!Img.similar(source, target)) {
            target.width = wSrc;
            target.height = hSrc;
            target.pixels = new long[srcLen];
        }

        final int deg = Utils.round(angle * Utils.RAD_TO_DEG);
        final int deg180 = Utils.mod(deg, 180);

        switch (deg180) {
            case 0: {
                System.arraycopy(source.pixels, 0, target.pixels, 0, srcLen);
                return target;
            }

            case 88:
            case 89:
            case 90:
            case 91:
            case 92: {
                return Img.clear(target);
            }

            default: {
                final float tana = (float) Math.tan(angle);
                final int hTrg = (int) (0.5f + (float) hSrc + Utils.abs(tana) * (float) wSrc);
                final float xCenter = (float) wSrc * 0.5f;
                final float yDiff = ((float) hSrc - (float) hTrg) * 0.5f;

                final int trgLen = wSrc * hTrg;
                final long[] trgPixels = new long[trgLen];
                for (int i = 0; i < trgLen; ++i) {
                    final float xTrg = i % wSrc;
                    trgPixels[i] = Img.sampleBilinear(source,
                        xTrg, yDiff + (float) (i / wSrc) + tana * (xTrg - xCenter));
                }

                target.height = hTrg;
                target.pixels = trgPixels;

                return target;
            }
        }
    }

    /**
     * Tints an image with a color according to a factor. If the preserveLight flag
     * is true, the
     * source image's original lightness is retained.
     *
     * @param source        the source pixels
     * @param tint          the tint color
     * @param fac           the factor
     * @param preserveLight the preserve light flag
     * @param target        the target pixels
     * @return the tinted pixels
     */
    public static Img tint(
        final Img source,
        final Lab tint,
        final float fac,
        final boolean preserveLight,
        final Img target) {

        final int wSrc = source.width;
        final int hSrc = source.height;
        final int srcLen = source.pixels.length;

        if (!Img.similar(source, target)) {
            target.width = wSrc;
            target.height = hSrc;
            target.pixels = new long[srcLen];
        }

        final Lab lab = new Lab();
        final float t = Float.isNaN(fac) ? 1.0f : Utils.clamp01(fac);
        final float u = 1.0f - t;
        final HashMap<Long, Long> convert = new HashMap<>();

        for (int i = 0; i < srcLen; ++i) {
            final long srcPixel = source.pixels[i];
            final Long srcPixelObj = srcPixel;
            long trgPixel;

            if (convert.containsKey(srcPixelObj)) {
                trgPixel = convert.get(srcPixelObj);
            } else {
                Lab.fromHex(srcPixel, lab);
                lab.l = preserveLight ? lab.l : u * lab.l + t * tint.l;
                lab.a = u * lab.a + t * tint.a;
                lab.b = u * lab.b + t * tint.b;
                lab.alpha = u * lab.alpha + t * (lab.alpha * tint.alpha);
                trgPixel = lab.toHexLongSat();
                convert.put(srcPixelObj, trgPixel);
            }

            target.pixels[i] = trgPixel;
        }

        return target;
    }

    /**
     * Tints an image with a color according to a factor.
     *
     * @param source the source pixels
     * @param tint   the tint color
     * @param fac    the factor
     * @param target the target pixels
     * @return the tinted pixels
     */
    public static Img tint(
        final Img source,
        final Lab tint,
        final float fac,
        final Img target) {

        return Img.tint(source, tint, fac, true, target);
    }

    /**
     * Tints an image with a color.
     *
     * @param source the source pixels
     * @param tint   the tint color
     * @param target the target pixels
     * @return the tinted pixels
     */
    public static Img tint(final Img source, final Lab tint, final Img target) {

        return Img.tint(source, tint, 0.5f, true, target);
    }

    /**
     * Converts a LAB image to an array of 32-bit 0xAARRGGBB pixels.
     * Does not validate that zero alpha pixels also have zero red, green
     * or blue.
     *
     * @param source  the source image
     * @param mapFunc the tone mapping function
     * @return the pixels
     */
    public static int[] toArgb32(
        final Img source,
        final Rgb.AbstrToneMap mapFunc) {

        final int len = source.pixels.length;
        final int[] argb32s = new int[len];
        final HashMap<Long, Integer> convert = new HashMap<>();
        convert.put(Img.CLEAR_PIXEL, 0);

        final Rgb mapped = new Rgb();
        final Rgb srgb = new Rgb();
        final Rgb lrgb = new Rgb();
        final Vec4 xyz = new Vec4();
        final Lab lab = new Lab();

        for (int i = 0; i < len; ++i) {
            final long tlab64 = source.pixels[i];
            final Long tlab64Obj = tlab64;
            int argb32;

            if (convert.containsKey(tlab64Obj)) {
                argb32 = convert.get(tlab64Obj);
            } else {
                Lab.fromHex(tlab64, lab);
                Rgb.srLab2TosRgb(lab, srgb, lrgb, xyz);
                mapFunc.apply(srgb, mapped);

                // TODO: Does this need to support alpha premultiply?

                argb32 = mapped.toHexIntWrap();
                convert.put(tlab64Obj, argb32);
            }

            argb32s[i] = argb32;
        }

        return argb32s;
    }

    /**
     * Creates an array of materials from the non-transparent pixels of an image.
     * Intended for smaller
     * images with relatively few colors.
     *
     * @param source the source image
     * @return the materials
     */
    public static MaterialSolid[] toMaterials(final Img source) {

        final TreeSet<Long> uniqueColors = new TreeSet<>();
        final long[] srcPixels = source.pixels;
        for (long srcPixel : srcPixels) {
            uniqueColors.add(srcPixel);
        }

        final Rgb srgb = new Rgb();
        final Rgb lrgb = new Rgb();
        final Vec4 xyz = new Vec4();
        final Lab lab = new Lab();

        final int uniquesLen = uniqueColors.size();
        final MaterialSolid[] result = new MaterialSolid[uniquesLen];
        final Iterator<Long> itr = uniqueColors.iterator();
        for (int j = 0; itr.hasNext(); ++j) {
            Lab.fromHex(itr.next(), lab);
            Rgb.srLab2TosRgb(lab, srgb, lrgb, xyz);

            final MaterialSolid material = new MaterialSolid();
            material.setStroke(false);
            material.setFill(srgb);
            material.setName("Material." + Rgb.toHexString(srgb));
            result[j] = material;
        }

        return result;
    }

    /**
     * Creates a mesh from the non-transparent pixels of an image. Intended for
     * smaller images with
     * relatively few colors.
     *
     * @param source the source pixels
     * @param poly   the polygon type
     * @param target the output mesh
     * @return the mesh
     */
    public static Mesh2 toMesh(
        final Img source,
        final PolyType poly,
        final Mesh2 target) {

        final long[] srcPixels = source.pixels;
        final int srcLen = srcPixels.length;
        final ArrayList<Integer> nonZeroIdcs = new ArrayList<>(srcLen);
        for (int i = 0; i < srcLen; ++i) {
            if ((srcPixels[i] & Img.T_MASK) != 0L) {
                nonZeroIdcs.add(i);
            }
        }

        final int wSrc = source.width;
        final int hSrc = source.height;
        final float right = wSrc > hSrc ? 0.5f : 0.5f * ((float) wSrc / (float) hSrc);
        final float top = wSrc <= hSrc ? 0.5f : 0.5f * ((float) hSrc / (float) wSrc);
        return Img.toMesh(
            nonZeroIdcs, wSrc,
            -right, top, right, -top,
            1.0f / (float) wSrc, 1.0f / (float) hSrc,
            poly, target);
    }

    /**
     * Creates an array of meshes from the non-transparent pixels of an image. Each
     * unique color is
     * assigned a mesh. Intended for smaller images with relatively few colors. Each
     * mesh's {@link
     * Mesh#materialIndex} corresponds to its index in the array.
     *
     * @param source the source image
     * @param poly   the polygon type
     * @return the meshes
     */
    public static Mesh2[] toMeshes(final Img source, final PolyType poly) {

        final long[] srcPixels = source.pixels;
        final int srcLen = srcPixels.length;
        final TreeMap<Long, ArrayList<Integer>> islands = new TreeMap<>();
        for (int i = 0; i < srcLen; ++i) {
            final long srcHexLong = srcPixels[i];
            if ((srcHexLong & Img.T_MASK) != 0L) {
                final Long srcHexObj = srcHexLong;
                if (islands.containsKey(srcHexObj)) {
                    islands.get(srcHexObj).add(i);
                } else {
                    final ArrayList<Integer> indices = new ArrayList<>();
                    indices.add(i);
                    islands.put(srcHexObj, indices);
                }
            }
        }

        final int wSrc = source.width;
        final int hSrc = source.height;
        final float right = wSrc > hSrc ? 0.5f : 0.5f * ((float) wSrc / (float) hSrc);
        final float top = wSrc <= hSrc ? 0.5f : 0.5f * ((float) hSrc / (float) wSrc);
        final float tou = 1.0f / (float) wSrc;
        final float tov = 1.0f / (float) hSrc;

        final Mesh2[] result = new Mesh2[islands.size()];

        final Iterator<Entry<Long, ArrayList<Integer>>> itr = islands.entrySet().iterator();
        for (int j = 0; itr.hasNext(); ++j) {
            final Mesh2 mesh = new Mesh2();
            mesh.setMaterialIndex(j);
            Img.toMesh(itr.next().getValue(), wSrc, -right, top, right, -top,
                tou, tov, poly, mesh);
            result[j] = mesh;
        }

        return result;
    }

    /**
     * Writes an image to ppm formatted bytes.
     *
     * @param source the source image
     * @return the bytes
     */
    public static byte[] toPpmBytes(final Img source) {

        return Img.toPpmBytes(source, new Rgb.ToneMapClamp(),
            Img.DEFAULT_PPM_FORMAT, 256);
    }

    /**
     * Writes an image to ppm formatted bytes.
     *
     * @param source  the source image
     * @param mapFunc the tone mapping function
     * @return the bytes
     */
    public static byte[] toPpmBytes(
        final Img source,
        final Rgb.AbstrToneMap mapFunc) {

        return Img.toPpmBytes(source, mapFunc, Img.DEFAULT_PPM_FORMAT, 256);
    }

    /**
     * Writes an image to ppm formatted bytes.
     *
     * @param source  the source image
     * @param mapFunc the tone mapping function
     * @param format  the data format
     * @return the bytes
     */
    public static byte[] toPpmBytes(
        final Img source,
        final Rgb.AbstrToneMap mapFunc,
        final PpmFormat format) {

        return Img.toPpmBytes(source, mapFunc, format, 256);
    }

    /**
     * Writes an image to ppm formatted bytes.
     *
     * @param source  the source image
     * @param mapFunc the tone mapping function
     * @param format  the data format
     * @param levels  the quantization levels
     * @return the bytes
     */
    public static byte[] toPpmBytes(
        final Img source,
        final Rgb.AbstrToneMap mapFunc,
        final PpmFormat format,
        final int levels) {

        final long[] srcPixels = source.pixels;
        final int len = srcPixels.length;
        final int w = source.width;
        final int h = source.height;

        final int lvVerif = Utils.clamp(Math.abs(levels), 2, 256);
        final int chMax = lvVerif - 1;

        final StringBuilder sb = new StringBuilder();
        sb.append(format == PpmFormat.BINARY ? "P6" : "P3");
        sb.append('\n');
        sb.append(w).append(' ').append(h);
        sb.append('\n');
        sb.append(chMax);
        sb.append('\n');

        final HashMap<Long, Rgb> convert = new HashMap<>();
        convert.put(Img.CLEAR_PIXEL, Rgb.clearBlack(new Rgb()));

        final Rgb srgb = new Rgb();
        final Rgb lrgb = new Rgb();
        final Vec4 xyz = new Vec4();
        final Lab lab = new Lab();

        int lenWrite = len * 3;
        byte[] rgbWrites = new byte[lenWrite];

        for (int i = 0, j = 0; i < len; ++i, j += 3) {
            final long tlab64 = srcPixels[i];
            final Long tlab64Obj = tlab64;
            Rgb mapped;

            if (convert.containsKey(tlab64Obj)) {
                mapped = convert.get(tlab64Obj);
            } else {
                Lab.fromHex(tlab64, lab);
                Rgb.srLab2TosRgb(lab, srgb, lrgb, xyz);
                // TODO: Require that color be premultiplied by alpha here?
                mapped = mapFunc.apply(srgb, new Rgb());
                convert.put(tlab64Obj, mapped);
            }

            if (mapped.alpha >= 0.5f) {
                final float rq = Utils.quantizeSigned(mapped.r, lvVerif);
                final float gq = Utils.quantizeSigned(mapped.g, lvVerif);
                final float bq = Utils.quantizeSigned(mapped.b, lvVerif);

                rgbWrites[j] = (byte) Utils.round(rq * chMax);
                rgbWrites[1 + j] = (byte) Utils.round(gq * chMax);
                rgbWrites[2 + j] = (byte) Utils.round(bq * chMax);
            } else {
                rgbWrites[j] = 0;
                rgbWrites[1 + j] = 0;
                rgbWrites[2 + j] = 0;
            }
        }

        switch (format) {
            case PLAIN_TEXT: {
                for (int i = 0, y = 0; y < h; ++y) {
                    for (int x = 0; x < w; ++x, i += 3) {
                        final int r8 = Utils.ubyte(rgbWrites[i]);
                        final int g8 = Utils.ubyte(rgbWrites[i + 1]);
                        final int b8 = Utils.ubyte(rgbWrites[i + 2]);

                        sb.append(r8);
                        sb.append(' ');
                        sb.append(g8);
                        sb.append(' ');
                        sb.append(b8);
                        if (x < w - 1) {
                            sb.append(' ');
                        }
                    }
                    /* File must have a closing empty line. */
                    sb.append('\n');
                }
            }
            break;

            case BINARY:
            default:
                for (int k = 0; k < lenWrite; ++k) {
                    sb.append((char) rgbWrites[k]);
                }
        }

        final int sbLen = sb.length();
        final byte[] bytes = new byte[sbLen];
        final char[] chars = new char[sbLen];
        sb.getChars(0, sbLen, chars, 0);
        for (int j = 0; j < sbLen; ++j) {
            bytes[j] = (byte) chars[j];
        }

        return bytes;
    }

    /**
     * Removes excess transparent pixels from an array of pixels.
     *
     * @param source the input image
     * @param target the output image
     * @return the trimmed image
     */
    public static Img trimAlpha(final Img source, final Img target) {

        return Img.trimAlpha(source, target, null);
    }

    /**
     * Removes excess transparent pixels from an array of pixels. Adapted from
     * the implementation by Oleg Mikhailov: <a href="https://stackoverflow.com/a/36938923">
     * https://stackoverflow.com/a/36938923</a>.
     * <br>
     * <br>
     * Emits the new image dimensions to a {@link Vec2}.
     *
     * @param source the source image
     * @param target the target image
     * @param tl     top left
     * @return the trimmed image
     * @author Oleg Mikhailov
     */
    public static Img trimAlpha(
        final Img source,
        final Img target,
        final Vec2 tl) {

        final long[] srcPixels = source.pixels;
        final int srcLen = srcPixels.length;
        final int wSrc = source.width;
        final int hSrc = source.height;

        if (wSrc < 2 && hSrc < 2) {
            final long[] trgPixels = new long[srcLen];
            System.arraycopy(srcPixels, 0, trgPixels, 0, srcLen);
            target.width = wSrc;
            target.height = hSrc;
            target.pixels = trgPixels;
            return target;
        }

        final int wn1 = wSrc > 1 ? wSrc - 1 : 0;
        final int hn1 = hSrc > 1 ? hSrc - 1 : 0;

        int minRight = wn1;
        int minBottom = hn1;

        /* Top search. y is outer loop, x is inner loop. */
        int top = -1;
        boolean goTop = true;
        while (goTop && top < hn1) {
            ++top;
            final int wtop = wSrc * top;
            int x = -1;
            while (goTop && x < wn1) {
                ++x;
                if ((srcPixels[wtop + x] & Img.T_MASK) != 0L) {
                    minRight = x;
                    minBottom = top;
                    goTop = false;
                }
            }
        }

        /* Left search. x is outer loop, y is inner loop. */
        int left = -1;
        boolean goLeft = true;
        while (goLeft && left < minRight) {
            ++left;
            int y = hSrc;
            while (goLeft && y > top) {
                --y;
                if ((srcPixels[y * wSrc + left] & Img.T_MASK) != 0L) {
                    minBottom = y;
                    goLeft = false;
                }
            }
        }

        /* Bottom search. y is outer loop, x is inner loop. */
        int bottom = hSrc;
        boolean goBottom = true;
        while (goBottom && bottom > minBottom) {
            --bottom;
            final int wbottom = wSrc * bottom;
            int x = wSrc;
            while (goBottom && x > left) {
                --x;
                if ((srcPixels[wbottom + x] & Img.T_MASK) != 0L) {
                    minRight = x;
                    goBottom = false;
                }
            }
        }

        /* Right search. x is outer loop, y is inner loop. */
        int right = wSrc;
        boolean goRight = true;
        while (goRight && right > minRight) {
            --right;
            int y = bottom + 1;
            while (goRight && y > top) {
                --y;
                if ((srcPixels[y * wSrc + right] & Img.T_MASK) != 0L) {
                    goRight = false;
                }
            }
        }

        final int wTrg = 1 + right - left;
        final int hTrg = 1 + bottom - top;
        if (wTrg < 1 || hTrg < 1) {
            final long[] trgPixels = new long[srcLen];
            System.arraycopy(srcPixels, 0, trgPixels, 0, srcLen);
            target.width = wSrc;
            target.height = hSrc;
            target.pixels = trgPixels;
            return target;
        }

        final int trgLen = wTrg * hTrg;
        final long[] trgPixels = new long[trgLen];
        for (int i = 0; i < trgLen; ++i) {
            trgPixels[i] = srcPixels[wSrc * (top + i / wTrg) + left + i % wTrg];
        }

        if (tl != null) {
            tl.set(left, top);
        }

        target.width = wSrc;
        target.height = hSrc;
        target.pixels = trgPixels;

        return target;
    }

    /**
     * Blits a source image's pixels onto a target image, using integer floor
     * modulo to wrap the source image. The source image can be offset
     * horizontally and/or vertically, creating the illusion of an infinite
     * background.
     *
     * @param source the source image
     * @param dx     the horizontal pixel offset
     * @param dy     the vertical pixel offset
     * @param target the target image
     * @return the wrapped pixels
     */
    public static Img wrap(
        final Img source,
        final int dx, final int dy,
        final Img target) {

        final int len = target.pixels.length;
        final int wTrg = target.width;
        final int wSrc = source.width;
        final int hSrc = source.height;

        for (int i = 0; i < len; ++i) {
            int yMod = (i / wTrg + dy) % hSrc;
            if ((yMod ^ hSrc) < 0 && yMod != 0) {
                yMod += hSrc;
            }

            int xMod = (i % wTrg - dx) % wSrc;
            if ((xMod ^ wSrc) < 0 && xMod != 0) {
                xMod += wSrc;
            }

            target.pixels[i] = source.pixels[xMod + wSrc * yMod];
        }

        return target;
    }

    /**
     * Inverts an image colors per a mask.
     *
     * @param source the input image
     * @param mask   the mask
     * @param target the output image
     * @return the inverted image
     */
    protected static Img invert(
        final Img source,
        final long mask,
        final Img target) {

        final int len = source.pixels.length;
        if (!Img.similar(source, target)) {
            target.width = source.width;
            target.height = source.height;
            target.pixels = new long[len];
        }

        for (int i = 0; i < len; ++i) {
            target.pixels[i] = source.pixels[i] ^ mask;
        }

        return target;
    }

    /**
     * Rotates the pixels of a source image around the image center by an angle
     * in radians. Assumes that the sine and cosine of the angle have already
     * been calculated and simple cases (0, 90, 180, 270 degrees) have been
     * filtered out.
     *
     * @param source the source image
     * @param cosa   the cosine of the angle
     * @param sina   the sine of the angle
     * @param target the target image
     * @return rotated pixels
     * @see Utils#abs(float)
     */
    protected static Img rotateBilinear(
        final Img source,
        final float cosa,
        final float sina,
        final Img target) {

        final int wSrc = source.width;
        final int hSrc = source.height;
        final float absCosa = Utils.abs(cosa);
        final float absSina = Utils.abs(sina);

        final int wTrg = (int) (0.5f + (float) hSrc * absSina
            + (float) wSrc * absCosa);
        final int hTrg = (int) (0.5f + (float) hSrc * absCosa
            + (float) wSrc * absSina);

        final float xSrcCenter = (float) wSrc * 0.5f;
        final float ySrcCenter = (float) hSrc * 0.5f;
        final float xTrgCenter = (float) wTrg * 0.5f;
        final float yTrgCenter = (float) hTrg * 0.5f;

        final int trgLen = wTrg * hTrg;
        final long[] trgPixels = new long[trgLen];

        for (int i = 0; i < trgLen; ++i) {
            final float ySgn = (float) (i / wTrg) - yTrgCenter;
            final float xSgn = i % wTrg - xTrgCenter;
            trgPixels[i] = Img.sampleBilinear(
                source,
                xSrcCenter + cosa * xSgn - sina * ySgn,
                ySrcCenter + cosa * ySgn + sina * xSgn);
        }

        target.width = wTrg;
        target.height = hTrg;
        target.pixels = trgPixels;

        return target;
    }

    /**
     * Internal helper function to sample a source image with a bilinear color
     * mix.
     *
     * @param img  the source image
     * @param xSrc the x coordinate
     * @param ySrc the y coordinate
     * @return the color
     */
    protected static long sampleBilinear(
        final Img img,
        final float xSrc,
        final float ySrc) {

        final long[] source = img.pixels;
        final int wSrc = img.width;
        final int hSrc = img.height;

        /*
         * Find truncation, floor and ceiling. The ceiling cannot use
         * yc = yf + 1 as a shortcut due to the case where y = 0.
         */
        final int yi = (int) ySrc;
        final int yf = ySrc > 0.0f ? yi : ySrc < -0.0f ? yi - 1 : 0;
        final int yc = ySrc > 0.0f ? yi + 1 : ySrc < -0.0f ? yi : 0;

        final boolean yfInBounds = yf >= 0 && yf < hSrc;
        final boolean ycInBounds = yc >= 0 && yc < hSrc;

        final int xi = (int) xSrc;
        final int xf = xSrc > 0.0f ? xi : xSrc < -0.0f ? xi - 1 : 0;
        final int xc = xSrc > 0.0f ? xi + 1 : xSrc < -0.0f ? xi : 0;

        /* Pixel corners colors. */
        final boolean xfInBounds = xf >= 0 && xf < wSrc;
        final boolean xcInBounds = xc >= 0 && xc < wSrc;

        final long c00 = yfInBounds && xfInBounds
            ? source[yf * wSrc + xf] : Img.CLEAR_PIXEL;
        final long c10 = yfInBounds && xcInBounds
            ? source[yf * wSrc + xc] : Img.CLEAR_PIXEL;
        final long c11 = ycInBounds && xcInBounds
            ? source[yc * wSrc + xc] : Img.CLEAR_PIXEL;
        final long c01 = ycInBounds && xfInBounds
            ? source[yc * wSrc + xf] : Img.CLEAR_PIXEL;

        final float xErr = xSrc - xf;

        float t0 = 0.0f;
        float l0 = 0.0f;
        float a0 = 0.0f;
        float b0 = 0.0f;

        final long t00 = c00 >> Img.T_SHIFT & 0xffffL;
        final long t10 = c10 >> Img.T_SHIFT & 0xffffL;
        if (t00 > 0 || t10 > 0) {
            final float u = 1.0f - xErr;
            t0 = u * t00 + xErr * t10;
            if (t0 > 0.0f) {
                l0 = u * (c00 >> Img.L_SHIFT & 0xffffL)
                    + xErr * (c10 >> Img.L_SHIFT & 0xffffL);
                a0 = u * (c00 >> Img.A_SHIFT & 0xffffL)
                    + xErr * (c10 >> Img.A_SHIFT & 0xffffL);
                b0 = u * (c00 >> Img.B_SHIFT & 0xffffL)
                    + xErr * (c10 >> Img.B_SHIFT & 0xffffL);
            }
        }

        float t1 = 0.0f;
        float l1 = 0.0f;
        float a1 = 0.0f;
        float b1 = 0.0f;

        final long t01 = c01 >> Img.T_SHIFT & 0xffffL;
        final long t11 = c11 >> Img.T_SHIFT & 0xffffL;
        if (t01 > 0 || t11 > 0) {
            final float u = 1.0f - xErr;
            t1 = u * t01 + xErr * t11;
            if (t1 > 0.0f) {
                l1 = u * (c01 >> Img.L_SHIFT & 0xffffL)
                    + xErr * (c11 >> Img.L_SHIFT & 0xffffL);
                a1 = u * (c01 >> Img.A_SHIFT & 0xffffL)
                    + xErr * (c11 >> Img.A_SHIFT & 0xffffL);
                b1 = u * (c01 >> Img.B_SHIFT & 0xffffL)
                    + xErr * (c11 >> Img.B_SHIFT & 0xffffL);
            }
        }

        if (t0 > 0.0f || t1 > 0.0f) {
            final float yErr = ySrc - yf;
            final float u = 1.0f - yErr;
            final float t2 = u * t0 + yErr * t1;
            if (t2 > 0.0f) {
                final float l2 = u * l0 + yErr * l1;
                final float a2 = u * a0 + yErr * a1;
                final float b2 = u * b0 + yErr * b1;

                long ti = (long) (0.5f + t2);
                long li = (long) (0.5f + l2);
                long ai = (long) (0.5f + a2);
                long bi = (long) (0.5f + b2);

                if (ti > 0xffffL) {
                    ti = 0xffffL;
                }
                if (li > 0xffffL) {
                    li = 0xffffL;
                }
                if (ai > 0xffffL) {
                    ai = 0xffffL;
                }
                if (bi > 0xffffL) {
                    bi = 0xffffL;
                }

                return ti << Img.T_SHIFT
                    | li << Img.L_SHIFT
                    | ai << Img.A_SHIFT
                    | bi << Img.B_SHIFT;
            }
        }

        return Img.CLEAR_PIXEL;
    }

    /**
     * Evaluates whether two images are similar enough that they can serve as
     * source and target in a static method. To be similar, images must have
     * the same width, height and pixel length.
     *
     * @param a the source candidate
     * @param b the target candidate
     * @return the evaluation
     */
    protected static boolean similar(final Img a, final Img b) {

        return a == b
            || a.width == b.width
            && a.height == b.height
            && a.pixels.length == b.pixels.length;
    }

    /**
     * Internal helper method to create a mesh from a list of indices and other
     * conversion data. Makes no optimizations to the mesh by, e.g., removing
     * interior or colinear vertices.
     *
     * @param indices the non-zero image pixel indices
     * @param wSrc    the image width
     * @param left    the left edge
     * @param top     the top edge
     * @param right   the right edge
     * @param bottom  the bottom edge
     * @param tou     width to uv conversion
     * @param tov     height to uv conversion
     * @param poly    polygon type
     * @param target  the output mesh
     * @return the mesh
     */
    protected static Mesh2 toMesh(
        final ArrayList<Integer> indices,
        final int wSrc,
        final float left,
        final float top,
        final float right,
        final float bottom,
        final float tou,
        final float tov,
        final PolyType poly,
        final Mesh2 target) {

        final int idcsLen = indices.size();
        final int vsLen = idcsLen * 4;
        final Vec2[] vs = target.coords = Vec2.resize(target.coords, vsLen);
        final Vec2[] vts = target.texCoords = Vec2.resize(target.texCoords, vsLen);

        for (int i = 0, j00 = 0; i < idcsLen; ++i, j00 += 4) {
            final int j10 = j00 + 1;
            final int j11 = j00 + 2;
            final int j01 = j00 + 3;

            final int idx = indices.get(i);
            final float x = idx % wSrc;
            final float y = (float) idx / wSrc;

            final float u0 = x * tou;
            final float v0 = y * tov;
            final float u1 = (x + 1.0f) * tou;
            final float v1 = (y + 1.0f) * tov;

            vts[j00].set(u0, v0);
            vts[j10].set(u1, v0);
            vts[j11].set(u1, v1);
            vts[j01].set(u0, v1);

            final float x0 = (1.0f - u0) * left + u0 * right;
            final float y0 = (1.0f - v0) * top + v0 * bottom;
            final float x1 = (1.0f - u1) * left + u1 * right;
            final float y1 = (1.0f - v1) * top + v1 * bottom;

            vs[j00].set(x0, y0);
            vs[j10].set(x1, y0);
            vs[j11].set(x1, y1);
            vs[j01].set(x0, y1);
        }

        int[][][] fs;
        switch (poly) {
            case TRI: {
                fs = target.faces = new int[idcsLen + idcsLen][3][2];
                for (int i = idcsLen - 1, j00 = vsLen - 1; i > -1; --i, j00 -= 4) {
                    final int j10 = j00 - 1;
                    final int j11 = j00 - 2;
                    final int j01 = j00 - 3;

                    final int[][] f1 = fs[i + i];
                    final int[] vr10 = f1[0];
                    vr10[0] = j11;
                    vr10[1] = j11;

                    final int[] vr11 = f1[1];
                    vr11[0] = j01;
                    vr11[1] = j01;

                    final int[] vr12 = f1[2];
                    vr12[0] = j00;
                    vr12[1] = j00;

                    final int[][] f0 = fs[i + i + 1];
                    final int[] vr00 = f0[0];
                    vr00[0] = j00;
                    vr00[1] = j00;

                    final int[] vr01 = f0[1];
                    vr01[0] = j10;
                    vr01[1] = j10;

                    final int[] vr02 = f0[2];
                    vr02[0] = j11;
                    vr02[1] = j11;
                }
            }
            break;

            case NGON:
            case QUAD:
            default: {
                fs = target.faces = new int[idcsLen][4][2];
                for (int i = idcsLen - 1, j00 = vsLen - 1; i > -1; --i, j00 -= 4) {
                    final int j10 = j00 - 1;
                    final int j11 = j00 - 2;
                    final int j01 = j00 - 3;

                    final int[][] f = fs[i];
                    final int[] vr00 = f[0];
                    vr00[0] = j00;
                    vr00[1] = j00;

                    final int[] vr10 = f[1];
                    vr10[0] = j10;
                    vr10[1] = j10;

                    final int[] vr11 = f[2];
                    vr11[0] = j11;
                    vr11[1] = j11;

                    final int[] vr01 = f[3];
                    vr01[0] = j01;
                    vr01[1] = j01;
                }
            }
        }

        return target;
    }

    /**
     * Tests this image for equivalence with another.
     *
     * @param other the image
     * @return the equivalence
     */
    public boolean equals(final Img other) {

        return this.height == other.height
            && Arrays.equals(this.pixels, other.pixels)
            && this.width == other.width;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Img img))
            return false;
        return height == img.height && width == img.width
            && Objects.deepEquals(pixels, img.pixels);
    }

    @Override
    public int hashCode() {
        return Objects.hash(height, Arrays.hashCode(pixels), width);
    }

    /**
     * Finds the aspect ratio of an image, its width divided by its height.
     *
     * @param image the image
     * @return the aspect ratio
     * @see Utils#div(float, float)
     */
    public float aspect(final Img image) {

        return Utils.div((float) image.width, (float) image.height);
    }

    /**
     * Gets the image height.
     *
     * @return the width
     */
    public final int getHeight() {
        return this.height;
    }

    /**
     * Gets a pixel at an index.
     *
     * @param i the index
     * @return the pixel
     */
    public long getPixel(final int i) {

        return this.getPixelOmit(i);
    }

    /**
     * Gets a pixel at local coordinates x and y.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the pixel
     */
    public long getPixel(final int x, final int y) {

        return this.getPixelOmit(x, y);
    }

    /**
     * Gets a pixel at local coordinates x and y. If the index is out of bounds,
     * then returns {@link Img#CLEAR_PIXEL}.
     *
     * @param i the index
     * @return the pixel
     */
    public final long getPixelOmit(final int i) {

        return this.getPixelOmit(i, Img.CLEAR_PIXEL);
    }

    /**
     * Gets a pixel at local coordinates x and y. If the coordinates are out of
     * bounds, then returns {@link Img#CLEAR_PIXEL}.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the pixel
     */
    public final long getPixelOmit(final int x, final int y) {

        return this.getPixelOmit(x, y, Img.CLEAR_PIXEL);
    }

    /**
     * Gets a pixel at local coordinates x and y. If the coordinates are out of
     * bounds, then returns the default pixel.
     *
     * @param x            the x coordinate
     * @param y            the y coordinate
     * @param defaultPixel the default pixel
     * @return the pixel
     */
    public final long getPixelOmit(
        final int x,
        final int y,
        final long defaultPixel) {

        return y >= 0 && y < this.height && x >= 0 && x < this.width
            ? this.pixels[y * this.width + x]
            : defaultPixel;
    }

    /**
     * Gets a pixel at an index. If the index is out of bounds, then returns
     * the default pixel.
     *
     * @param i            the index
     * @param defaultPixel the default pixel
     * @return the pixel
     */
    public final long getPixelOmit(final int i, final long defaultPixel) {

        return i >= 0 && i < this.pixels.length
            ? this.pixels[i] : defaultPixel;
    }

    /**
     * Gets a copy of the pixels array.
     *
     * @return the pixels array
     */
    public final long[] getPixels() {

        final int len = this.pixels.length;
        final long[] arr = new long[len];
        System.arraycopy(this.pixels, 0, arr, 0, len);
        return arr;
    }

    /**
     * Gets a pixel at an index. Does not check the index for validity.
     *
     * @param i the index
     * @return the pixel
     */
    public final long getPixelUnchecked(final int i) {

        return this.pixels[i];
    }

    /**
     * Gets a pixel at local coordinates x and y. Does not check the
     * coordinates for validity.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the pixel
     */
    public final long getPixelUnchecked(final int x, final int y) {

        return this.pixels[y * this.width + x];
    }

    /**
     * Gets a pixel at an index. Wraps the index around the pixels array
     * length.
     *
     * @param i the index
     * @return the pixel
     */
    public final long getPixelWrap(final int i) {

        return this.pixels[Utils.mod(i, this.pixels.length)];
    }

    /**
     * Gets a pixel at local coordinates x and y. Wraps the coordinates around
     * the image boundaries.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the pixel
     */
    public final long getPixelWrap(final int x, final int y) {

        return this.pixels[Utils.mod(y, this.height) * this.width
            + Utils.mod(x, this.width)];
    }

    /**
     * Gets the image's size as a vector.
     *
     * @param target the output size
     * @return the size
     */
    public final Vec2 getSize(final Vec2 target) {

        return target.set(this.width, this.height);
    }

    /**
     * Gets the image width.
     *
     * @return the width
     */
    public final int getWidth() {
        return this.width;
    }

    /**
     * Gets the image pixel length.
     *
     * @return the pixel length
     */
    public final int length() {
        return this.pixels.length;
    }

    /**
     * Sets a pixel at local coordinates x and y.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param c the color
     */
    public void setPixel(final int x, final int y, final long c) {

        this.setPixelOmit(x, y, c);
    }

    /**
     * Sets a pixel at local coordinates x and y.
     *
     * @param i the index
     * @param c the color
     */
    public void setPixel(final int i, final long c) {

        this.setPixelOmit(i, c);
    }

    /**
     * If local coordinates x and y are within image bounds, then sets the
     * pixel to the given color.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param c the color
     */
    public final void setPixelOmit(final int x, final int y, final long c) {

        if (y >= 0 && y < this.height && x >= 0 && x < this.width) {
            this.pixels[y * this.width + x] = c;
        }
    }

    /**
     * If the index is in bounds, then sets the pixel to the given color.
     *
     * @param i the index
     * @param c the color
     */
    public final void setPixelOmit(final int i, final long c) {

        if (i >= 0 && i < this.pixels.length) {
            this.pixels[i] = c;
        }
    }

    /**
     * Sets a pixel at local coordinates x and y. Does not check the
     * coordinates for validity.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param c the color
     */
    public final void setPixelUnchecked(final int x, final int y, final long c) {

        this.pixels[y * this.width + x] = c;
    }

    /**
     * Sets a pixel at an index. Does not check the index for validity.
     *
     * @param i the index
     * @param c the color
     */
    public final void setPixelUnchecked(final int i, final long c) {

        this.pixels[i] = c;
    }

    /**
     * Sets a pixel at local coordinates x and y. Wraps the coordinates around
     * the image boundaries.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param c the color
     */
    public final void setPixelWrap(final int x, final int y, final long c) {

        this.pixels[Utils.mod(y, this.height) * this.width + Utils.mod(x, this.width)] = c;
    }

    /**
     * Sets a pixel at an index. Wraps the index around the pixel boundaries.
     *
     * @param i the index
     * @param c the color
     */
    public final void setPixelWrap(final int i, final long c) {

        this.pixels[Utils.mod(i, this.pixels.length)] = c;
    }

    /**
     * Returns a string representation of an image.
     *
     * @return the string
     */
    @Override
    public String toString() {

        final StringBuilder sb = new StringBuilder(64);

        sb.append("{\"width\":");
        sb.append(this.width);
        sb.append(",\"height\":");
        sb.append(this.height);
        sb.append(",\"pixels\":[");

        final int lenn1 = this.pixels.length - 1;
        for (int i = 0; i < lenn1; ++i) {
            sb.append(this.pixels[i]);
            sb.append(',');
        }
        sb.append(this.pixels[lenn1]);

        sb.append(']');
        sb.append('}');

        return sb.toString();
    }

    /**
     * Policy for handling gray colors when adjusting by LCH.
     */
    public enum GrayPolicy {

        /**
         * Gray colors have a hue on the cool side of the color wheel.
         */
        COOL,

        /**
         * Do not saturate gray colors.
         */
        OMIT,

        /**
         * Gray colors have a hue on the warm side of the color wheel.
         */
        WARM,

        /**
         * Gray colors have zero hue.
         */
        ZERO
    }

    /**
     * Channel to use in the gradient map.
     */
    public enum MapChannel {

        /**
         * The chroma channel.
         */
        C,

        /**
         * The lightness channel.
         */
        L
    }

    /**
     * Policy for handling the pivot when adjusting contrast.
     */
    public enum PivotPolicy {

        /**
         * Pivot around a fixed number.
         */
        FIXED,

        /**
         * Pivot around the arithmetic mean (average).
         */
        MEAN,

        /**
         * Pivot around the average of the minimum and maximum.
         */
        RANGE
    }

    /**
     * ppm file format options.
     */
    public enum PpmFormat {

        /**
         * Writes a binary file.
         */
        BINARY,

        /**
         * Writes a plain text file.
         */
        PLAIN_TEXT
    }
}

package com.behreajj.camzup.core;

/**
 * Maintains consistent behavior between classes which support creation of an
 * SVG file.
 */
public interface ISvgWritable {

    /**
     * Default height of an SVG view box.
     */
    float DEFAULT_HEIGHT = 512.0f;

    /**
     * Default origin x of a 'camera' transform in an SVG, expressed as a ratio
     * in [0.0, 1.0].
     */
    float DEFAULT_ORIGIN_X = 0.5f;

    /**
     * Default origin y of a 'camera' transform in an SVG, expressed as a ratio
     * in [0.0, 1.0].
     */
    float DEFAULT_ORIGIN_Y = 0.5f;

    /**
     * Default stroke cap to use when rendering to an SVG.
     */
    String DEFAULT_STR_CAP = "round";

    /**
     * Default stroke join to use when rendering to an SVG.
     */
    String DEFAULT_STR_JOIN = "round";

    /**
     * Default width of an SVG view box.
     */
    float DEFAULT_WIDTH = 512.0f;

    /**
     * The default fill rule, or winding rule, from the enumeration "evenodd"
     * and "nonzero". "nonzero" is the SVG specification default.
     */
    String DEFAULT_WINDING_RULE = "evenodd";

    /**
     * The default number of decimal places to print real numbers in an SVG.
     */
    int FIXED_PRINT = 6;

    /**
     * Renders this object as a string containing an SVG element.
     *
     * @return the SVG string
     */
    default String toSvgElm() {
        return this.toSvgElm(1.0f);
    }

    /**
     * Renders this object as a string containing an SVG element.
     * <br>
     * <br>
     * Stroke weight is impacted by scaling in transforms, so zoom is a
     * parameter. If nonuniform zooming is used, zoom can be an average of
     * width and height or the maximum dimension.
     *
     * @param zoom scaling from external transforms
     * @return the SVG string
     */
    String toSvgElm(final float zoom);

    /**
     * Renders this object as an SVG string. A default material renders the
     * mesh's fill and stroke. The background of the SVG is transparent.
     *
     * @return the SVG string
     */
    String toSvgString();

    /**
     * Renders this object as an SVG string. A default material renders the
     * mesh's fill and stroke. The background of the SVG is transparent. The
     * width and height inform the view box dimensions. The origin is expected
     * to be in unit coordinates, [0.0, 1.0] ; it is multiplied by the view box
     * dimensions.
     *
     * @param xOrig      the origin x
     * @param yOrig      the origin y
     * @param xScale     the scale x
     * @param yScale     the scale y
     * @param viewWidth  the width
     * @param viewHeight the height
     * @return the SVG string
     * @see Utils#approx(float, float)
     * @see Utils#clamp01(float)
     * @see Utils#toFixed(float, int)
     */
    default String toSvgString(
        final float xOrig,
        final float yOrig,
        final float xScale,
        final float yScale,
        final float viewWidth,
        final float viewHeight) {

        final float vw = Math.max(Utils.EPSILON, viewWidth);
        final float vh = Math.max(Utils.EPSILON, viewHeight);
        final String widthStr = Utils.toFixed(vw, ISvgWritable.FIXED_PRINT);
        final String heightStr = Utils.toFixed(vh, ISvgWritable.FIXED_PRINT);
        final float x = Utils.clamp01(xOrig);
        final float y = Utils.clamp01(yOrig);
        final float vxscl = Utils.approx(xScale, 0.0f) ? 1.0f : xScale;
        final float vyscl = Utils.approx(yScale, 0.0f) ? 1.0f : yScale;

        /*
         * https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/shape-
         * rendering: auto | optimizeSpeed | crispEdges | geometricPrecision
         */
        final String shapeRendering = "geometricPrecision";

        final StringBuilder svgp = new StringBuilder(128);
        svgp.append("<svg ");
        svgp.append("xmlns=\"http://www.w3.org/2000/svg\" ");
        svgp.append("xmlns:xlink=\"http://www.w3.org/1999/xlink\" ");
        svgp.append("shape-rendering=\"");
        svgp.append(shapeRendering);
        svgp.append("\" width=\"");
        svgp.append(widthStr);
        svgp.append("\" height=\"");
        svgp.append(heightStr);
        svgp.append("\" viewBox=\"0 0 ");
        svgp.append(widthStr);
        svgp.append(' ');
        svgp.append(heightStr);
        svgp.append("\">\n");

        svgp.append("<g transform=\"translate(");
        Utils.toFixed(svgp, vw * x, ISvgWritable.FIXED_PRINT);
        svgp.append(',');
        svgp.append(' ');
        Utils.toFixed(svgp, vh * y, ISvgWritable.FIXED_PRINT);
        svgp.append(')');
        svgp.append(" scale(");
        Utils.toFixed(svgp, vxscl, ISvgWritable.FIXED_PRINT);
        svgp.append(", ");
        Utils.toFixed(svgp, vyscl, ISvgWritable.FIXED_PRINT);
        svgp.append(")\">\n");
        svgp.append(this.toSvgElm(Math.min(vxscl, vyscl)));
        svgp.append("</g>\n");
        svgp.append("</svg>");

        return svgp.toString();
    }

    /**
     * Renders this object as an SVG string. A default material renders the
     * mesh's fill and stroke. The background of the SVG is transparent. The
     * width and height supplied form both the view box dimensions, the
     * translation and the scale of the shape.
     *
     * @param origin the origin
     * @param scale  the scale
     * @param dim    the dimensions
     * @return the SVG string
     */
    default String toSvgString(final Vec2 origin, final Vec2 scale, final Vec2 dim) {

        return this.toSvgString(origin.x, origin.y, scale.x, scale.y, dim.x, dim.y);
    }
}

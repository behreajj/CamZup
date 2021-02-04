package camzup.core;

/**
 * Maintains consistent behavior between classes which support creation of
 * an SVG file.
 */
public interface ISvgWritable {

   /**
    * Renders the curve as a string containing an SVG element.
    *
    * @return the SVG string
    */
   default String toSvgElm ( ) { return this.toSvgElm(1.0f); }

   /**
    * Renders the curve as a string containing an SVG element.<br>
    * <br>
    * Stroke weight is impacted by scaling in transforms, so zoom is a
    * parameter. If nonuniform zooming is used, zoom can be an average of
    * width and height or the maximum dimension.
    *
    * @param zoom scaling from external transforms
    *
    * @return the SVG string
    */
   String toSvgElm ( final float zoom );

   /**
    * Renders this object as an SVG string. A default material renders the
    * mesh's fill and stroke. The background of the SVG is transparent.
    *
    * @return the SVG string
    */
   default String toSvgString ( ) {

      return this.toSvgString(ISvgWritable.DEFAULT_ORIGIN_X,
         ISvgWritable.DEFAULT_ORIGIN_Y, ISvgWritable.DEFAULT_WIDTH,
         ISvgWritable.DEFAULT_HEIGHT);
   }

   /**
    * Renders this object as an SVG string. A default material renders the
    * mesh's fill and stroke. The background of the SVG is transparent. The
    * width and height inform the view box dimensions. The origin is expected
    * to be in unit coordinates, [0.0, 1.0] ; it is multiplied by the view box
    * dimensions. The camera scale is set to the shorter edge of the view box,
    * so as to contain the shape.
    *
    * @param xOrigin the origin x
    * @param yOrigin the origin y
    * @param width   the width
    * @param height  the height
    *
    * @return the SVG string
    */
   default String toSvgString ( final float xOrigin, final float yOrigin,
      final float width, final float height ) {

      final float vw = Utils.max(IUtils.EPSILON, width);
      final float vh = Utils.max(IUtils.EPSILON, height);
      final String widthStr = Utils.toFixed(vw, ISvgWritable.FIXED_PRINT);
      final String heightStr = Utils.toFixed(vh, ISvgWritable.FIXED_PRINT);
      final float x = Utils.clamp01(xOrigin);
      final float y = Utils.clamp01(yOrigin);

      final float scl = Utils.min(vw, vh);
      final String sclStr = Utils.toFixed(scl, ISvgWritable.FIXED_PRINT);

      final StringBuilder svgp = new StringBuilder(128);
      svgp.append("<svg ");
      svgp.append("xmlns=\"http://www.w3.org/2000/svg\" ");
      svgp.append("xmlns:xlink=\"http://www.w3.org/1999/xlink\" ");
      svgp.append("width=\"");
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
      svgp.append(") scale(");
      svgp.append(sclStr);
      svgp.append(", -");
      svgp.append(sclStr);
      svgp.append(")\">\n");
      svgp.append(this.toSvgElm(scl));
      svgp.append("</g>\n");
      svgp.append("</svg>");

      return svgp.toString();
   }

   /**
    * Renders this curve as an SVG string. A default material renders the
    * mesh's fill and stroke. The background of the SVG is transparent. The
    * width and height supplied form both the view box dimensions, the
    * translation and the scale of the shape.
    *
    * @param origin the origin
    * @param dim    the dimensions
    *
    * @return the SVG string
    */
   default String toSvgString ( final Vec2 origin, final Vec2 dim ) {

      return this.toSvgString(origin.x, origin.y, dim.x, dim.y);
   }

   /**
    * Default height of an SVG view box, {@value ISvgWritable#DEFAULT_HEIGHT}.
    */
   float DEFAULT_HEIGHT = 512.0f;

   /**
    * Default origin x of a 'camera' transform in an SVG.
    */
   float DEFAULT_ORIGIN_X = 0.5f;

   /**
    * Default origin y of a 'camera' transform in an SVG.
    */
   float DEFAULT_ORIGIN_Y = 0.5f;

   /**
    * Default width of an SVG view box, {@value ISvgWritable#DEFAULT_WIDTH}.
    */
   float DEFAULT_WIDTH = 512.0f;

   /**
    * The default fill rule, or winding rule, from the enumeration "evenodd"
    * and "nonzero". "nonzero" is the SVG specification default.
    * {@value ISvgWritable#DEFAULT_WINDING_RULE}.
    */
   String DEFAULT_WINDING_RULE = "evenodd";

   /**
    * The default number of decimal places to print real numbers in an SVG,
    * {@value IUtils#FIXED_PRINT}.
    */
   int FIXED_PRINT = 6;

}

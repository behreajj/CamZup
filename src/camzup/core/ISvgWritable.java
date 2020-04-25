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
   default String toSvgElm ( ) {

      return this.toSvgElm(Integer.toHexString(System.identityHashCode(this)));
   }

   /**
    * Renders the curve as a string containing an SVG element.
    *
    * @param id the path id
    *
    * @return the SVG string
    */
   default String toSvgElm ( final String id ) {

      return this.toSvgElm(id, 1.0f);
   }

   /**
    * Renders the curve as a string containing an SVG element.<br>
    * <br>
    * Stroke weight is impacted by scaling in transforms, so zoom is a
    * parameter. If nonuniform zooming is used, zoom can be an average of
    * width and height or the maximum dimension.
    *
    * @param id   the path id
    * @param zoom scaling from external transforms
    *
    * @return the SVG string
    */
   String toSvgElm ( final String id, final float zoom );

   /**
    * Renders this object as an SVG string. A default material renders the
    * mesh's fill and stroke. The background of the SVG is transparent.
    *
    * @return the SVG string
    */
   default String toSvgString ( ) {

      return this.toSvgString(Integer.toHexString(System.identityHashCode(
         this)), ISvgWritable.DEFAULT_SVG_X_ORIGIN,
         ISvgWritable.DEFAULT_SVG_Y_ORIGIN, ISvgWritable.DEFAULT_SVG_WIDTH,
         ISvgWritable.DEFAULT_SVG_HEIGHT);
   }

   /**
    * Renders this object as an SVG string. A default material renders the
    * mesh's fill and stroke. The background of the SVG is transparent. The
    * width and height inform the view box dimensions. The origin is expected
    * to be in unit coordinates, [0.0, 1.0] ; it is multiplied by the view box
    * dimensions. The camera scale is set to the shorter edge of the view box,
    * so as to contain the shape.
    *
    * @param id      the element id
    * @param xOrigin the origin x
    * @param yOrigin the origin y
    * @param width   the width
    * @param height  the height
    *
    * @return the SVG string
    */
   default String toSvgString ( final String id, final float xOrigin,
      final float yOrigin, final float width, final float height ) {

      final float vw = Utils.max(IUtils.DEFAULT_EPSILON, width);
      final float vh = Utils.max(IUtils.DEFAULT_EPSILON, height);
      final String widthStr = Utils.toFixed(vw, 6);
      final String heightStr = Utils.toFixed(vh, 6);
      final float x = Utils.clamp01(xOrigin);
      final float y = Utils.clamp01(yOrigin);

      final float scl = Utils.min(vw, vh);
      final String sclStr = Utils.toFixed(scl, 6);

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
      svgp.append(Utils.toFixed(vw * x, 6));
      svgp.append(',');
      svgp.append(' ');
      svgp.append(Utils.toFixed(vh * y, 6));
      svgp.append(") scale(");
      svgp.append(sclStr);
      svgp.append(", -");
      svgp.append(sclStr);
      svgp.append(")\">\n");
      svgp.append(this.toSvgElm(id, scl));
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
    * @param id     the element id
    * @param origin the origin
    * @param dim    the dimensions
    *
    * @return the SVG string
    */
   default String toSvgString ( final String id, final Vec2 origin,
      final Vec2 dim ) {

      return this.toSvgString(id, origin.x, origin.y, dim.x, dim.y);
   }

   /**
    * Default height of an SVG view box.
    */
   float DEFAULT_SVG_HEIGHT = 512.0f;

   /**
    * Default width of an SVG view box.
    */
   float DEFAULT_SVG_WIDTH = 512.0f;

   /**
    * Default origin x of a 'camera' transform in an SVG.
    */
   float DEFAULT_SVG_X_ORIGIN = 0.5f;

   /**
    * Default origin y of a 'camera' transform in an SVG.
    */
   float DEFAULT_SVG_Y_ORIGIN = 0.5f;

}

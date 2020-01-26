package camzup.core;

import camzup.pfriendly.IUp;

/**
 * A material which holds data to display materials with
 * solid colors only (no textures, or patterns). Contains
 * data for a fill and/or stroke.
 */
public class MaterialSolid extends Material {

   /**
    * Default stroke cap to use when rendering to an SVG.
    */
   public static final String DEFAULT_SVG_STR_CAP = "round";

   /**
    * Default stroke join to use when rendering to an SVG.
    */
   public static final String DEFAULT_SVG_STR_JOIN = "round";

   /**
    * Minimum stroke weight allowed when rendering to SVG.
    */
   public static final float SVG_MIN_STROKE_WT = 0.005f;

   /**
    * Default material to use when no material is contained by
    * an entity. Stroke weight is impacted by transforms, so
    * the stroke weight is divided by the scale.
    *
    * This opens a group node, so it should be closed
    * elsewhere.
    *
    * @param scale
    *           the transform scale
    * @return the string
    */
   public static String defaultSvgMaterial ( final float scale ) {

      final String strokeStr = Utils.toFixed(Utils.max(
            MaterialSolid.SVG_MIN_STROKE_WT,
            IUp.DEFAULT_STROKE_WEIGHT / scale), 4);

      return new StringBuilder(256)
            .append("<g stroke-width=\"")
            .append(strokeStr)
            .append("\" stroke-opacity=\"1.0\" stroke=\"#")
            .append(Integer.toHexString(
                  IUp.DEFAULT_STROKE_COLOR)
                  .substring(2))
            .append("\" fill-opacity=\"1.0\" fill=\"#")
            .append(Integer.toHexString(
                  IUp.DEFAULT_FILL_COLOR)
                  .substring(2))
            .append("\" stroke-linejoin=\"")
            .append(MaterialSolid.DEFAULT_SVG_STR_JOIN)
            .append("\" stroke-linecap=\"")
            .append(MaterialSolid.DEFAULT_SVG_STR_CAP)
            .append("\">\n").toString();
   }

   /**
    * The fill color.
    */
   public final Color fill;

   /**
    * The stroke color.
    */
   public final Color stroke;

   /**
    * The weight, or width, of the stroke.
    */
   public float strokeWeight = IMaterial.DEFAULT_STROKE_WEIGHT;

   /**
    * Whether or not to display a shape with a fill.
    */
   public boolean useFill = true;

   /**
    * Whether or not to display a shape with a stroke.
    */
   public boolean useStroke = false;

   {
      /*
       * Instance variables should never be assigned constants
       * when the constants are objects, as the constants will be
       * overwritten. Rather, instance variables should receive
       * copies of constants. To make this clearer, an initializer
       * block.
       */

      this.fill = Color.fromHex(IMaterial.DEFAULT_FILL, new Color());
      this.stroke = Color.fromHex(IMaterial.DEFAULT_STROKE, new Color());
   }

   /**
    * The default constructor.
    */
   public MaterialSolid () {

      /*
       * Stroke cap and join are not implemented because it would
       * require storing PConstants value for each constant, AWT's
       * constant, and SVG's. And there are renderer issues with
       * Awt and OpenGL, so consistency is hard to guarantee.
       */

      super();
   }

   /**
    * Creates a named solid material by component.
    *
    * @param fill
    *           the fill color
    * @param stroke
    *           the stroke color
    * @param strokeWeight
    *           the stroke weight
    * @param useFill
    *           whether to use fill
    * @param useStroke
    *           whether to use stroke
    */
   public MaterialSolid (
         final Color fill,
         final Color stroke,
         final float strokeWeight,
         final boolean useFill,
         final boolean useStroke ) {

      super();
      this.fill.set(fill);
      this.stroke.set(stroke);
      this.strokeWeight = strokeWeight;
      this.useFill = useFill;
      this.useStroke = useStroke;
   }

   /**
    * Creates a named solid material.
    *
    * @param name
    *           the name
    */
   public MaterialSolid ( final String name ) {

      super(name);
   }

   /**
    * Creates a named solid material by component.
    *
    * @param name
    *           the name
    * @param fill
    *           the fill color
    * @param stroke
    *           the stroke color
    * @param strokeWeight
    *           the stroke weight
    * @param useFill
    *           whether to use fill
    * @param useStroke
    *           whether to use stroke
    */
   public MaterialSolid (
         final String name,
         final Color fill,
         final Color stroke,
         final float strokeWeight,
         final boolean useFill,
         final boolean useStroke ) {

      super(name);
      this.fill.set(fill);
      this.stroke.set(stroke);
      this.strokeWeight = strokeWeight;
      this.useFill = useFill;
      this.useStroke = useStroke;
   }

   /**
    * Sets whether or not to use a stroke with a boolean.
    *
    * @param fill
    *           the boolean
    * @return this material
    */
   @Chainable
   public MaterialSolid setFill ( final boolean fill ) {

      this.useFill = fill;
      return this;
   }

   /**
    * Sets the material's fill color.
    *
    * @param fill
    *           the fill color
    * @return this material
    */
   @Chainable
   public MaterialSolid setFill ( final Color fill ) {

      this.fill.set(fill);
      return this;
   }

   /**
    * Sets the material's fill color from a hexadecimal value.
    *
    * @param fill
    *           the color
    * @return this material
    */
   @Chainable
   public MaterialSolid setFill ( final int fill ) {

      Color.fromHex(fill, this.fill);
      return this;
   }

   /**
    * Sets whether or not to use a stroke with a boolean.
    *
    * @param stroke
    *           the boolean
    * @return this material
    */
   @Chainable
   public MaterialSolid setStroke ( final boolean stroke ) {

      this.useStroke = stroke;
      return this;
   }

   /**
    * Sets the material's stroke color.
    *
    * @param stroke
    *           the color
    * @return this material
    */
   @Chainable
   public MaterialSolid setStroke ( final Color stroke ) {

      this.stroke.set(stroke);
      return this;
   }

   /**
    * Sets the material's stroke color from a hexadecimal
    * value.
    *
    * @param stroke
    *           the color
    * @return this material
    */
   @Chainable
   public MaterialSolid setStroke ( final int stroke ) {

      Color.fromHex(stroke, this.stroke);
      return this;
   }

   /**
    * Sets the material's stroke weight.
    *
    * @param strokeWeight
    *           the stroke weight
    * @return this material
    */
   @Chainable
   public MaterialSolid setStrokeWeight ( final float strokeWeight ) {

      this.strokeWeight = Utils.max(Utils.EPSILON, strokeWeight);
      return this;
   }

   /**
    * Toggles the material's fill.
    *
    * @return this material
    */
   @Chainable
   public MaterialSolid toggleFill () {

      this.useFill = !this.useFill;
      return this;
   }

   /**
    * Toggles the material's stroke.
    *
    * @return this material
    */
   @Chainable
   public MaterialSolid toggleStroke () {

      this.useStroke = !this.useStroke;
      return this;
   }

   /**
    * Returns a string representation of this material.
    *
    * @return the string
    */
   @Override
   public String toString () {

      return this.toString(4);
   }

   /**
    * Returns a string representation of this complex number.
    *
    * @param places
    *           the number of places
    * @return the string
    * @see Utils#toFixed(float, int)
    */
   public String toString ( final int places ) {

      return new StringBuilder(256)
            .append("{ fill: ")
            .append(this.fill.toString(places))
            .append(", stroke: ")
            .append(this.stroke.toString(places))
            .append(", strokeWeight: ")
            .append(Utils.toFixed(this.strokeWeight, places))
            .append(", useFill: ")
            .append(this.useFill)
            .append(", useStroke: ")
            .append(this.useStroke)
            .append(' ')
            .append('}')
            .toString();
   }

   /**
    * Returns an SVG snippet as a string.
    *
    * @return the string
    * @see Utils#clamp01(float)
    * @see Color#toHexWeb(Color)
    */
   public String toSvgString () {

      return this.toSvgString(1.0f);
   }

   /**
    * Returns an SVG snippet as a string.
    *
    * @param transformScale
    *           the transform scale.
    * @return the string
    * @see Utils#clamp01(float)
    * @see Color#toHexWeb(Color)
    */
   public String toSvgString ( final float transformScale ) {

      final String strokeStr = Utils.toFixed(Utils.max(
            MaterialSolid.SVG_MIN_STROKE_WT,
            this.strokeWeight / transformScale), 4);
      final StringBuilder result = new StringBuilder(256);

      /* Stroke style. */
      if (this.useStroke) {
         result.append("stroke-width=\"")
               .append(strokeStr)
               .append("\" stroke-opacity=\"")
               .append(Utils.toFixed(Utils.clamp01(this.stroke.w), 2))
               .append("\" stroke=\"")
               .append(Color.toHexWeb(this.stroke))
               .append("\" stroke-linejoin=\"")
               .append(MaterialSolid.DEFAULT_SVG_STR_JOIN)
               .append("\" stroke-linecap=\"")
               .append(MaterialSolid.DEFAULT_SVG_STR_CAP)
               .append('\"')
               .append(' ');
      } else {
         result.append("stroke=\"none\" ");
      }

      /* Fill style. */
      if (this.useFill) {
         result.append("fill-opacity=\"")
               .append(Utils.toFixed(Utils.clamp01(this.fill.w), 2))
               .append("\" fill=\"")
               .append(Color.toHexWeb(this.fill))
               .append('\"');
      } else {
         result.append("fill=\"none\"");
      }
      return result.toString();
   }
}

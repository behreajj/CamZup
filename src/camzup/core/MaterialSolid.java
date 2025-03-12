package camzup.core;

import camzup.pfriendly.IUp;

/**
 * A material which holds data to display materials with solid colors only
 * (no textures, or patterns). Contains data for a fill and/or stroke.
 */
public class MaterialSolid extends Material {

   /**
    * The fill color.
    */
   public final Rgb fill = Rgb.fromHex(IMaterial.DEFAULT_FILL, new Rgb());

   /**
    * The stroke color.
    */
   public final Rgb stroke = Rgb.fromHex(IMaterial.DEFAULT_STROKE, new Rgb());

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

   /**
    * The default constructor.
    */
   public MaterialSolid ( ) {}

   /**
    * Creates a new solid material with copied data from a source.
    *
    * @param source the source material
    */
   public MaterialSolid ( final MaterialSolid source ) {

      this(source.name, source.fill, source.stroke, source.strokeWeight,
         source.useFill, source.useStroke);
   }

   /**
    * Creates a material from a fill color.
    *
    * @param fill the fill color
    */
   public MaterialSolid ( final Rgb fill ) {

      /*
       * Stroke cap and join are not implemented because it would require
       * storing PConstants value for each constant, AWT's constant, and SVG's.
       * And there are renderer issues with AWT and OpenGL, so consistency is
       * hard to guarantee.
       */

      this(fill, Rgb.clearBlack(new Rgb()), 0.0f);
   }

   /**
    * Creates a material from a fill, stroke color and stroke weight. Whether
    * or not to use a fill is inferred from the fill's alpha; whether or not
    * to use stroke is inferred from the stroke weight and stroke color's
    * alpha.
    *
    * @param fill         the fill color
    * @param stroke       the stroke color
    * @param strokeWeight the stroke weight
    */
   public MaterialSolid ( final Rgb fill, final Rgb stroke,
      final float strokeWeight ) {

      this(fill, stroke, strokeWeight, fill.alpha > 0.0f, stroke.alpha > 0.0f
         && strokeWeight > 0.0f);
   }

   /**
    * Creates a solid material by component.
    *
    * @param fill         the fill color
    * @param stroke       the stroke color
    * @param strokeWeight the stroke weight
    * @param useFill      whether to use fill
    * @param useStroke    whether to use stroke
    */
   public MaterialSolid ( final Rgb fill, final Rgb stroke,
      final float strokeWeight, final boolean useFill,
      final boolean useStroke ) {

      this(Rgb.toHexString(fill), fill, stroke, strokeWeight, useFill,
         useStroke);
   }

   /**
    * Creates a named solid material.
    *
    * @param name the name
    */
   public MaterialSolid ( final String name ) { super(name); }

   /**
    * Creates a named material from a fill color.
    *
    * @param name the material name
    * @param fill the fill color
    */
   public MaterialSolid ( final String name, final Rgb fill ) {

      this(name, fill, Rgb.clearBlack(new Rgb()), 0.0f);
   }

   /**
    * Creates a named material from a fill, stroke color and stroke weight.
    * Whether or not to use a fill is inferred from the fill's alpha; whether
    * or not to use stroke is inferred from the stroke weight and stroke
    * color's alpha.
    *
    * @param name         the name
    * @param fill         the fill color
    * @param stroke       the stroke color
    * @param strokeWeight the stroke weight
    */
   public MaterialSolid ( final String name, final Rgb fill, final Rgb stroke,
      final float strokeWeight ) {

      this(name, fill, stroke, strokeWeight, fill.alpha > 0.0f, stroke.alpha
         > 0.0f && strokeWeight > 0.0f);
   }

   /**
    * Creates a named solid material by component.
    *
    * @param name         the name
    * @param fill         the fill color
    * @param stroke       the stroke color
    * @param strokeWeight the stroke weight
    * @param useFill      whether to use fill
    * @param useStroke    whether to use stroke
    */
   public MaterialSolid ( final String name, final Rgb fill, final Rgb stroke,
      final float strokeWeight, final boolean useFill,
      final boolean useStroke ) {

      super(name);
      this.fill.set(fill);
      this.stroke.set(stroke);
      this.strokeWeight = strokeWeight;
      this.useFill = useFill;
      this.useStroke = useStroke;
   }

   /**
    * Tests this material for equivalence with another object.
    *
    * @param obj the object
    *
    * @return the evaluation
    */
   @Override
   public boolean equals ( final Object obj ) {

      if ( this == obj ) { return true; }
      if ( !super.equals(obj) || this.getClass() != obj.getClass() ) {
         return false;
      }

      final MaterialSolid other = ( MaterialSolid ) obj;

      if ( this.fill == null ) {
         if ( other.fill != null ) { return false; }
      } else if ( !this.fill.equals(other.fill) ) { return false; }

      if ( this.stroke == null ) {
         if ( other.stroke != null ) { return false; }
      } else if ( !this.stroke.equals(other.stroke) ) { return false; }

      return this.strokeWeight == other.strokeWeight && this.useFill
         == other.useFill && this.useStroke == other.useStroke;
   }

   /**
    * Returns a hash code representation of this material.
    *
    * @return the hash code
    */
   @Override
   public int hashCode ( ) {

      final int prime = 31;
      int result = super.hashCode();

      result = prime * result + ( this.useFill ? 1231 : 1237 );
      result = prime * result + ( this.fill == null ? 0 : this.fill
         .hashCode() );

      result = prime * result + ( this.useStroke ? 1231 : 1237 );
      result = prime * result + ( this.stroke == null ? 0 : this.stroke
         .hashCode() );
      return prime * result + Float.floatToIntBits(this.strokeWeight);
   }

   /**
    * Sets this material from a source.
    *
    * @param source the source material
    *
    * @return this material
    */
   public MaterialSolid set ( final MaterialSolid source ) {

      this.name = source.name;
      this.fill.set(source.fill);
      this.stroke.set(source.stroke);
      this.strokeWeight = source.strokeWeight;
      this.useFill = source.useFill;
      this.useStroke = source.useStroke;
      return this;
   }

   /**
    * Sets whether or not to use a stroke with a boolean.
    *
    * @param fill the boolean
    *
    * @return this material
    */
   public MaterialSolid setFill ( final boolean fill ) {

      this.useFill = fill;
      return this;
   }

   /**
    * Sets the material's fill color.
    *
    * @param r red
    * @param g green
    * @param b blue
    *
    * @return this material
    */
   public MaterialSolid setFill ( final float r, final float g,
      final float b ) {

      this.fill.set(r, g, b);
      return this;
   }

   /**
    * Sets the material's fill color.
    *
    * @param r red
    * @param g green
    * @param b blue
    * @param a transparency
    *
    * @return this material
    */
   public MaterialSolid setFill ( final float r, final float g, final float b,
      final float a ) {

      this.fill.set(r, g, b, a);
      return this;
   }

   /**
    * Sets the material's fill color from a hexadecimal value.
    *
    * @param fill the color
    *
    * @return this material
    *
    * @see Rgb#fromHex(int, Rgb)
    */
   public MaterialSolid setFill ( final int fill ) {

      Rgb.fromHex(fill, this.fill);
      return this;
   }

   /**
    * Sets the material's fill color.
    *
    * @param fill the fill color
    *
    * @return this material
    */
   public MaterialSolid setFill ( final Rgb fill ) {

      this.fill.set(fill);
      return this;
   }

   /**
    * Sets whether or not to use a stroke with a boolean.
    *
    * @param stroke the boolean
    *
    * @return this material
    */
   public MaterialSolid setStroke ( final boolean stroke ) {

      this.useStroke = stroke;
      return this;
   }

   /**
    * Sets the material's stroke color.
    *
    * @param r red
    * @param g green
    * @param b blue
    *
    * @return this material
    */
   public MaterialSolid setStroke ( final float r, final float g,
      final float b ) {

      this.stroke.set(r, g, b);
      return this;
   }

   /**
    * Sets the material's stroke color.
    *
    * @param r red
    * @param g green
    * @param b blue
    * @param a transparency
    *
    * @return this material
    */
   public MaterialSolid setStroke ( final float r, final float g, final float b,
      final float a ) {

      this.stroke.set(r, g, b, a);
      return this;
   }

   /**
    * Sets the material's stroke color from a hexadecimal value.
    *
    * @param stroke the color
    *
    * @return this material
    *
    * @see Rgb#fromHex(int, Rgb)
    */
   public MaterialSolid setStroke ( final int stroke ) {

      Rgb.fromHex(stroke, this.stroke);
      return this;
   }

   /**
    * Sets the material's stroke color.
    *
    * @param stroke the color
    *
    * @return this material
    */
   public MaterialSolid setStroke ( final Rgb stroke ) {

      this.stroke.set(stroke);
      return this;
   }

   /**
    * Sets the material's stroke weight.
    *
    * @param strokeWeight the stroke weight
    *
    * @return this material
    *
    * @see Utils#max(float, float)
    */
   public MaterialSolid setStrokeWeight ( final float strokeWeight ) {

      this.strokeWeight = Utils.max(IUtils.EPSILON, strokeWeight);
      return this;
   }

   /**
    * Swaps this material's stroke and fill. This includes both the color and
    * whether or not to use fill and stroke.
    *
    * @return this material
    */
   public MaterialSolid swapFillStroke ( ) {

      final boolean t = this.useFill;
      this.useFill = this.useStroke;
      this.useStroke = t;

      final float fr = this.fill.r;
      final float fg = this.fill.g;
      final float fb = this.fill.b;
      final float fa = this.fill.alpha;
      this.fill.set(this.stroke);
      this.stroke.set(fr, fg, fb, fa);

      return this;
   }

   /**
    * Toggles the material's fill.
    *
    * @return this material
    */
   public MaterialSolid toggleFill ( ) {

      this.useFill = !this.useFill;
      return this;
   }

   /**
    * Toggles the material's stroke.
    *
    * @return this material
    */
   public MaterialSolid toggleStroke ( ) {

      this.useStroke = !this.useStroke;
      return this;
   }

   /**
    * Returns a string representation of this material.
    *
    * @return the string
    */
   @Override
   public String toString ( ) { return this.toString(IUtils.FIXED_PRINT); }

   /**
    * Returns a string representation of this material.
    *
    * @param places the number of places
    *
    * @return the string
    */
   public String toString ( final int places ) {

      final StringBuilder sb = new StringBuilder(256);
      sb.append("{\"name\":\"");
      sb.append(this.name);
      sb.append("\",\"fill\":");
      this.fill.toString(sb, places);
      sb.append(",\"stroke\":");
      this.stroke.toString(sb, places);
      sb.append(",\"strokeWeight\":");
      Utils.toFixed(sb, this.strokeWeight, places);
      sb.append(",\"useFill\":");
      sb.append(this.useFill);
      sb.append(",\"useStroke\":");
      sb.append(this.useStroke);
      sb.append('}');
      return sb.toString();
   }

   /**
    * An internal helper function to format a material in Python, then append
    * it to a {@link StringBuilder}.
    *
    * @param pyCd      the string builder
    * @param gamma     the gamma adjustment
    * @param metallic  the metallic factor
    * @param roughness the roughness
    *
    * @return the string builder
    */
   @Experimental
   StringBuilder toBlenderCode ( final StringBuilder pyCd, final float gamma,
      final float metallic, final float roughness ) {

      pyCd.append("{\"name\": \"");
      pyCd.append("id");
      pyCd.append(this.name);
      pyCd.append("\", \"fill\": ");
      this.fill.toBlenderCode(pyCd, gamma, true);
      pyCd.append(", \"metallic\": ");
      Utils.toFixed(pyCd, metallic, 6);
      pyCd.append(", \"roughness\": ");
      Utils.toFixed(pyCd, roughness, 6);
      pyCd.append('}');

      return pyCd;
   }

   /**
    * Appends a representation of this material to a {@link StringBuilder} for
    * writing an SVG.
    *
    * @param svgp  the string builder
    * @param scale the transform scale.
    *
    * @return the string builder
    */
   StringBuilder toSvgString ( final StringBuilder svgp, final float scale ) {

      return this.toSvgString(svgp, scale, ISvgWritable.DEFAULT_STR_JOIN,
         ISvgWritable.DEFAULT_STR_CAP);
   }

   /**
    * Appends a representation of this material to a {@link StringBuilder} for
    * writing an SVG. The stroke join may be either "bevel," "miter" or
    * "round". The stroke cap may be either "butt," "round" or "square".
    *
    * @param svgp       the string builder
    * @param scale      the transform scale.
    * @param strokeJoin the stroke join.
    * @param strokeCap  the stroke cap.
    *
    * @return the string builder
    *
    * @see Utils#div(float, float)
    * @see Utils#abs(float)
    * @see Utils#clamp01(float)
    * @see Rgb#toHexWeb(StringBuilder, Rgb)
    */
   StringBuilder toSvgString ( final StringBuilder svgp, final float scale,
      final String strokeJoin, final String strokeCap ) {

      svgp.append("id=\"");
      svgp.append(this.name.toLowerCase());
      svgp.append("\" class=\"");
      svgp.append(this.getClass().getSimpleName().toLowerCase());
      svgp.append('\"');
      svgp.append(' ');

      /* Stroke style. */
      final float sw = Utils.div(this.strokeWeight, Utils.abs(scale));
      final float sa = this.stroke.alpha;
      if ( this.useStroke && sa > 0.0f && sw > IUtils.EPSILON ) {
         svgp.append("stroke-width=\"");
         Utils.toFixed(svgp, sw, ISvgWritable.FIXED_PRINT);
         if ( sa < 1.0f ) {
            svgp.append("\" stroke-opacity=\"");
            Utils.toFixed(svgp, sa, ISvgWritable.FIXED_PRINT);
         }
         svgp.append("\" stroke=\"");
         Rgb.toHexWeb(svgp, this.stroke);
         svgp.append("\" stroke-linejoin=\"");
         svgp.append(strokeJoin);
         svgp.append("\" stroke-linecap=\"");
         svgp.append(strokeCap);
         svgp.append('\"');
         svgp.append(' ');
      } else {
         svgp.append("stroke=\"none\" ");
      }

      /* Fill style. */
      final float fa = this.fill.alpha;
      if ( this.useFill && fa > 0.0f ) {
         if ( fa < 1.0f ) {
            svgp.append("fill-opacity=\"");
            Utils.toFixed(svgp, fa, ISvgWritable.FIXED_PRINT);
            svgp.append("\" ");
         }
         svgp.append("fill=\"");
         Rgb.toHexWeb(svgp, this.fill);
         svgp.append('\"');
      } else {
         svgp.append("fill=\"none\"");
      }
      return svgp;
   }

   /**
    * Default material to use in Blender code conversion when an entity does
    * not have one.
    *
    * @param gamma gamma adjustment
    *
    * @return the string builder
    *
    * @see Rgb#fromHex(int, Rgb)
    */
   static StringBuilder defaultBlenderMaterial ( final StringBuilder pyCd,
      final float gamma ) {

      final Rgb c = Rgb.fromHex(IUp.DEFAULT_FILL_COLOR, new Rgb());
      pyCd.append("{\"name\": \"");
      pyCd.append("CamZupDefault");
      pyCd.append("\", \"fill\": ");
      c.toBlenderCode(pyCd, gamma, true);
      pyCd.append(", \"metallic\": 0.0");
      pyCd.append(", \"roughness\": 1.0");
      pyCd.append('}');
      return pyCd;
   }

   /**
    * Default material to use when an entity does not have one. Stroke weight
    * is impacted by transforms, so the stroke weight is divided by the scale.
    * This opens a group node, which should be closed elsewhere.
    *
    * @param svgp  the string builder
    * @param scale the transform scale
    *
    * @return the string builder
    *
    * @see Utils#div(float, float)
    * @see Utils#abs(float)
    * @see Rgb#toHexWeb(StringBuilder, int)
    * @see Utils#toFixed(StringBuilder, float, int)
    */
   static StringBuilder defaultSvgMaterial ( final StringBuilder svgp,
      final float scale ) {

      /*
       * To keep this short and simple, alpha components for default fill color
       * and stroke color are not calculated, but are assumed to be 1.0.
       */

      svgp.append("<g id=\"");
      svgp.append("defaultmaterial");
      // svgp.append("\" fill-opacity=\"");
      // svgp.append("1.0");
      svgp.append("\" fill=\"");
      Rgb.toHexWeb(svgp, IUp.DEFAULT_FILL_COLOR);
      svgp.append('\"');
      svgp.append(' ');

      final float sw = Utils.div(IUp.DEFAULT_STROKE_WEIGHT, Utils.abs(scale));
      if ( sw > IUtils.EPSILON ) {
         svgp.append("stroke-width=\"");
         Utils.toFixed(svgp, sw, ISvgWritable.FIXED_PRINT);
         // svgp.append("\" stroke-opacity=\"");
         // svgp.append("1.0");
         svgp.append("\" stroke=\"");
         Rgb.toHexWeb(svgp, IUp.DEFAULT_STROKE_COLOR);
         svgp.append("\" stroke-linejoin=\"");
         svgp.append(ISvgWritable.DEFAULT_STR_JOIN);
         svgp.append("\" stroke-linecap=\"");
         svgp.append(ISvgWritable.DEFAULT_STR_CAP);
         svgp.append('\"');
         svgp.append(' ');
      } else {
         svgp.append("stroke=\"none\"");
      }
      svgp.append(">\n");
      return svgp;
   }

}

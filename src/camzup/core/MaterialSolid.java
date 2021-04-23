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
   public final Color fill = Color.fromHex(IMaterial.DEFAULT_FILL, new Color());

   /**
    * The stroke color.
    */
   public final Color stroke = Color.fromHex(IMaterial.DEFAULT_STROKE,
      new Color());

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
    * Creates a material from a fill color.
    *
    * @param fill the fill color
    */
   public MaterialSolid ( final Color fill ) {

      /*
       * Stroke cap and join are not implemented because it would require
       * storing PConstants value for each constant, AWT's constant, and SVG's.
       * And there are renderer issues with AWT and OpenGL, so consistency is
       * hard to guarantee.
       */

      this(fill, Color.clearBlack(new Color()), 0.0f);
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
   public MaterialSolid ( final Color fill, final Color stroke,
      final float strokeWeight ) {

      this(fill, stroke, strokeWeight, fill.a > 0.0f, stroke.a > 0.0f
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
   public MaterialSolid ( final Color fill, final Color stroke,
      final float strokeWeight, final boolean useFill,
      final boolean useStroke ) {

      this(Color.toHexString(fill), fill, stroke, strokeWeight, useFill,
         useStroke);
   }

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
   public MaterialSolid ( final String name, final Color fill ) {

      this(name, fill, Color.clearBlack(new Color()), 0.0f);
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
   public MaterialSolid ( final String name, final Color fill,
      final Color stroke, final float strokeWeight ) {

      this(name, fill, stroke, strokeWeight, fill.a > 0.0f, stroke.a > 0.0f
         && strokeWeight > 0.0f);
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
   public MaterialSolid ( final String name, final Color fill,
      final Color stroke, final float strokeWeight, final boolean useFill,
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
      if ( !super.equals(obj) ) { return false; }
      if ( this.getClass() != obj.getClass() ) { return false; }

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
      result = prime * result + Float.floatToIntBits(this.strokeWeight);
      return result;
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
    * @param fill the fill color
    *
    * @return this material
    */
   public MaterialSolid setFill ( final Color fill ) {

      this.fill.set(fill);
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
    */
   public MaterialSolid setFill ( final int fill ) {

      Color.fromHex(fill, this.fill);
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
    * @param stroke the color
    *
    * @return this material
    */
   public MaterialSolid setStroke ( final Color stroke ) {

      this.stroke.set(stroke);
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
    */
   public MaterialSolid setStroke ( final int stroke ) {

      Color.fromHex(stroke, this.stroke);
      return this;
   }

   /**
    * Sets the material's stroke weight.
    *
    * @param strokeWeight the stroke weight
    *
    * @return this material
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
      final float fa = this.fill.a;
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
      sb.append("{ name: \"");
      sb.append(this.name);
      sb.append("\", fill: ");
      this.fill.toString(sb, places);
      sb.append(", stroke: ");
      this.stroke.toString(sb, places);
      sb.append(", strokeWeight: ");
      Utils.toFixed(sb, this.strokeWeight, places);
      sb.append(", useFill: ");
      sb.append(this.useFill);
      sb.append(", useStroke: ");
      sb.append(this.useStroke);
      sb.append(' ');
      sb.append('}');
      return sb.toString();
   }

   /**
    * An internal helper function to format a vector as a Python tuple, then
    * append it to a {@link StringBuilder}. Used for testing purposes to
    * compare results with Blender 2.9x.
    *
    * @param pyCd           the string builder
    * @param gamma          the gamma adjustment
    * @param metallic       the metallic factor
    * @param roughness      the roughness
    * @param specular       specular highlight strength
    * @param clearcoat      clear coat factor
    * @param clearcoatRough clear coat roughness
    *
    * @return the string builder
    */
   @Experimental
   StringBuilder toBlenderCode ( final StringBuilder pyCd, final float gamma,
      final float metallic, final float roughness, final float specular,
      final float clearcoat, final float clearcoatRough ) {

      pyCd.append("{\"name\": \"");
      pyCd.append(this.name);
      pyCd.append("\", \"fill\": ");
      this.fill.toBlenderCode(pyCd, gamma, true);
      pyCd.append(", \"metallic\": ");
      Utils.toFixed(pyCd, metallic, 6);
      pyCd.append(", \"roughness\": ");
      Utils.toFixed(pyCd, roughness, 6);
      pyCd.append(", \"specular\": ");
      Utils.toFixed(pyCd, specular, 6);
      pyCd.append(", \"clearcoat\": ");
      Utils.toFixed(pyCd, clearcoat, 6);
      pyCd.append(", \"clearcoat_roughness\": ");
      Utils.toFixed(pyCd, clearcoatRough, 6);
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
    *
    * @see Utils#clamp01(float)
    * @see Color#toHexWeb(Color)
    */
   StringBuilder toSvgString ( final StringBuilder svgp, final float scale ) {

      final String strokeStr = Float.toString(Utils.max(IUtils.EPSILON, Utils
         .div(this.strokeWeight, scale)));
      svgp.append("id=\"");
      svgp.append(this.name);
      svgp.append("\" class=\"");
      svgp.append(this.getClass().getSimpleName().toLowerCase());
      svgp.append('\"');
      svgp.append(' ');

      /* Stroke style. */
      if ( this.useStroke ) {
         svgp.append("stroke-width=\"");
         svgp.append(strokeStr);
         svgp.append("\" stroke-opacity=\"");
         Utils.toFixed(svgp, Utils.clamp01(this.stroke.a), 6);
         svgp.append("\" stroke=\"");
         svgp.append(Color.toHexWeb(this.stroke));
         svgp.append("\" stroke-linejoin=\"");
         svgp.append(ISvgWritable.DEFAULT_STR_JOIN);
         svgp.append("\" stroke-linecap=\"");
         svgp.append(ISvgWritable.DEFAULT_STR_CAP);
         svgp.append('\"');
         svgp.append(' ');
      } else {
         svgp.append("stroke=\"none\" ");
      }

      /* Fill style. */
      if ( this.useFill ) {
         svgp.append("fill-opacity=\"");
         Utils.toFixed(svgp, Utils.clamp01(this.fill.a), 6);
         svgp.append("\" fill=\"");
         svgp.append(Color.toHexWeb(this.fill));
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
    */
   static StringBuilder defaultBlenderMaterial ( final StringBuilder pyCd,
      final float gamma ) {

      final Color c = Color.fromHex(IUp.DEFAULT_FILL_COLOR, new Color());
      pyCd.append("{\"name\": \"");
      pyCd.append("CamZupDefault");
      pyCd.append("\", \"fill\": ");
      c.toBlenderCode(pyCd, gamma, true);
      pyCd.append(", \"metallic\": 0.0");
      pyCd.append(", \"roughness\": 1.0");
      pyCd.append(", \"specular\": 0.0");
      pyCd.append(", \"clearcoat\": 0.0");
      pyCd.append(", \"clearcoat_roughness\": 0.001");
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
    */
   static StringBuilder defaultSvgMaterial ( final StringBuilder svgp,
      final float scale ) {

      final String strokeStr = Float.toString(Utils.max(IUtils.EPSILON, Utils
         .div(IUp.DEFAULT_STROKE_WEIGHT, scale)));

      svgp.append("<g id=\"defaultmaterial\" stroke-width=\"");
      svgp.append(strokeStr);
      svgp.append("\" stroke-opacity=\"1.0\" stroke=\"");
      svgp.append(Color.toHexWeb(IUp.DEFAULT_STROKE_COLOR));
      svgp.append("\" fill-opacity=\"1.0\" fill=\"");
      svgp.append(Color.toHexWeb(IUp.DEFAULT_FILL_COLOR));
      svgp.append("\" stroke-linejoin=\"");
      svgp.append(ISvgWritable.DEFAULT_STR_JOIN);
      svgp.append("\" stroke-linecap=\"");
      svgp.append(ISvgWritable.DEFAULT_STR_CAP);
      svgp.append("\">\n");
      return svgp;
   }

}

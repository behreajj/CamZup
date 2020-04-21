package camzup.core;

import java.util.ArrayList;

import camzup.pfriendly.IUp;

/**
 * A material which holds data to display materials with solid colors only
 * (no textures, or patterns). Contains data for a fill and/or stroke.
 */
public class MaterialSolid extends Material implements Cloneable {

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
      this.fill = Color.fromHex(IMaterial.DEFAULT_FILL, new Color());
      this.stroke = Color.fromHex(IMaterial.DEFAULT_STROKE, new Color());
   }

   /**
    * The default constructor.
    */
   public MaterialSolid ( ) { super(); }

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
   public MaterialSolid (
      final Color fill,
      final Color stroke,
      final float strokeWeight ) {

      this(fill, stroke, strokeWeight, fill.w > 0.0f, stroke.w > 0.0f && strokeWeight > 0.0f);
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
   public MaterialSolid (
      final Color fill,
      final Color stroke,
      final float strokeWeight,
      final boolean useFill,
      final boolean useStroke ) {

      this(Color.toHexString(
         fill), fill, stroke, strokeWeight, useFill, useStroke);
   }

   /**
    * Creates a new solid material with copied data from a source.
    *
    * @param source the source material
    */
   public MaterialSolid ( final MaterialSolid source ) {

      this(source.name, source.fill, source.stroke, source.strokeWeight, source.useFill, source.useStroke);
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
   public MaterialSolid (
      final String name,
      final Color fill ) {

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
   public MaterialSolid (
      final String name,
      final Color fill,
      final Color stroke,
      final float strokeWeight ) {

      this(name, fill, stroke, strokeWeight, fill.w > 0.0f, stroke.w > 0.0f && strokeWeight > 0.0f);
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
    * Creates a new material with the components of this material.
    *
    * @return the cloned material
    */
   @Override
   public MaterialSolid clone ( ) {

      return new MaterialSolid(this.name, this.fill, this.stroke, this.strokeWeight, this.useFill, this.useStroke);
   }

   /**
    * Sets this material from a source.
    *
    * @param source the source material
    *
    * @return this material
    */
   @Chainable
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
   @Chainable
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
   @Chainable
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
   public MaterialSolid setFill (
      final float r,
      final float g,
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
   public MaterialSolid setFill (
      final float r,
      final float g,
      final float b,
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
   @Chainable
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
   @Chainable
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
   @Chainable
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
   public MaterialSolid setStroke (
      final float r,
      final float g,
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
   public MaterialSolid setStroke (
      final float r,
      final float g,
      final float b,
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
   @Chainable
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
   @Chainable
   public MaterialSolid setStrokeWeight ( final float strokeWeight ) {

      this.strokeWeight = Utils.max(IUtils.DEFAULT_EPSILON, strokeWeight);
      return this;
   }

   /**
    * Swaps this material's stroke and fill. This includes both the color and
    * whether or not to use fill and stroke.
    *
    * @return this material
    */
   @Chainable
   public MaterialSolid swapFillStroke ( ) {

      final boolean t = this.useFill;
      this.useFill = this.useStroke;
      this.useStroke = t;

      final float fr = this.fill.x;
      final float fg = this.fill.y;
      final float fb = this.fill.z;
      final float fa = this.fill.w;
      this.fill.set(this.stroke);
      this.stroke.set(fr, fg, fb, fa);

      return this;
   }

   /**
    * Toggles the material's fill.
    *
    * @return this material
    */
   @Chainable
   public MaterialSolid toggleFill ( ) {

      this.useFill = !this.useFill;
      return this;
   }

   /**
    * Toggles the material's stroke.
    *
    * @return this material
    */
   @Chainable
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
   public String toString ( ) { return this.toString(4); }

   /**
    * Returns a string representation of this material.
    *
    * @param places the number of places
    *
    * @return the string
    *
    * @see Utils#toFixed(float, int)
    */
   public String toString ( final int places ) {

      return new StringBuilder(256).append("{ name: \"").append(
         this.name).append("\", fill: ").append(
            this.fill.toString(places)).append(", stroke: ").append(
               this.stroke.toString(places)).append(", strokeWeight: ").append(
                  Utils.toFixed(this.strokeWeight, places)).append(
                     ", useFill: ").append(this.useFill).append(
                        ", useStroke: ").append(this.useStroke).append(
                           ' ').append('}').toString();
   }

   /**
    * Returns a String of Python code targeted toward the Blender 2.8x API.
    * This code is brittle and is used for internal testing purposes, i.e., to
    * compare how curve geometry looks in Blender (the control) versus in the
    * library (the test).
    *
    * @param gamma          the gamma adjustment
    * @param metallic       the metallic factor
    * @param roughness      the roughness
    * @param specular       specular highlight strength
    * @param clearcoat      clear coat factor
    * @param clearcoatRough clear coat roughness
    *
    * @return the string
    */
   @Experimental
   String toBlenderCode (
      final float gamma,
      final float metallic,
      final float roughness,
      final float specular,
      final float clearcoat,
      final float clearcoatRough ) {

      return new StringBuilder(256).append("{\"name\": \"").append(
         this.name).append("\", \"fill\": ").append(
            this.fill.toBlenderCode(gamma, true)).append(
               ", \"metallic\": ").append(Utils.toFixed(metallic, 6)).append(
                  ", \"roughness\": ").append(
                     Utils.toFixed(roughness, 6)).append(
                        ", \"specular\": ").append(
                           Utils.toFixed(specular, 6)).append(
                              ", \"clearcoat\": ").append(
                                 Utils.toFixed(clearcoat, 6)).append(
                                    ", \"clearcoat_roughness\": ").append(
                                       Utils.toFixed(clearcoatRough, 6)).append(
                                          '}').toString();
   }

   /**
    * Returns an SVG snippet as a string.
    *
    * @return the string
    *
    * @see Utils#clamp01(float)
    * @see Color#toHexWeb(Color)
    */
   String toSvgString ( ) { return this.toSvgString(1.0f); }

   /**
    * Returns an SVG snippet as a string.
    *
    * @param scale the transform scale.
    *
    * @return the string
    *
    * @see Utils#clamp01(float)
    * @see Color#toHexWeb(Color)
    */
   String toSvgString ( final float scale ) {

      /*
       * This needs to be printed to a high precision because of small meshes
       * which are blown up by scale.
       */
      final String strokeStr = Utils.toFixed(
         Utils.max(IUtils.DEFAULT_EPSILON, Utils.div(this.strokeWeight, scale)),
         6);
      final StringBuilder svgp = new StringBuilder(256).append("id=\"").append(
         this.name).append('\"').append(' ');

      /* Stroke style. */
      if ( this.useStroke ) {
         svgp.append("stroke-width=\"").append(strokeStr).append(
            "\" stroke-opacity=\"").append(
               Utils.toFixed(Utils.clamp01(this.stroke.w), 6)).append(
                  "\" stroke=\"").append(Color.toHexWeb(this.stroke)).append(
                     "\" stroke-linejoin=\"").append(
                        MaterialSolid.DEFAULT_SVG_STR_JOIN).append(
                           "\" stroke-linecap=\"").append(
                              MaterialSolid.DEFAULT_SVG_STR_CAP).append(
                                 '\"').append(' ');
      } else {
         svgp.append("stroke=\"none\" ");
      }

      /* Fill style. */
      if ( this.useFill ) {
         svgp.append("fill-opacity=\"").append(
            Utils.toFixed(Utils.clamp01(this.fill.w), 6)).append(
               "\" fill=\"").append(Color.toHexWeb(this.fill)).append('\"');
      } else {
         svgp.append("fill=\"none\"");
      }
      return svgp.toString();
   }

   /**
    * Default stroke cap to use when rendering to an SVG.
    */
   public static final String DEFAULT_SVG_STR_CAP = "round";

   /**
    * Default stroke join to use when rendering to an SVG.
    */
   public static final String DEFAULT_SVG_STR_JOIN = "round";

   /**
    * Default material to use when an entity does not have one. Stroke weight
    * is impacted by transforms, so the stroke weight is divided by the scale.
    * This opens a group node, which should be closed elsewhere.
    *
    * @param scale the transform scale
    *
    * @return the string
    */
   public static String defaultSvgMaterial ( final float scale ) {

      /*
       * This needs to be printed to a higher precision, six, because of small
       * meshes which are blown up by scale.
       */
      final String strokeStr = Utils.toFixed(Utils.max(IUtils.DEFAULT_EPSILON,
         Utils.div(IUp.DEFAULT_STROKE_WEIGHT, scale)), 6);

      return new StringBuilder(256).append(
         "<g id=\"material\" stroke-width=\"").append(strokeStr).append(
            "\" stroke-opacity=\"1.0\" stroke=\"#").append(
               Integer.toHexString(IUp.DEFAULT_STROKE_COLOR).substring(
                  2)).append("\" fill-opacity=\"1.0\" fill=\"#").append(
                     Integer.toHexString(IUp.DEFAULT_FILL_COLOR).substring(
                        2)).append("\" stroke-linejoin=\"").append(
                           MaterialSolid.DEFAULT_SVG_STR_JOIN).append(
                              "\" stroke-linecap=\"").append(
                                 MaterialSolid.DEFAULT_SVG_STR_CAP).append(
                                    "\">\n").toString();
   }

   /**
    * Creates a material from an array of strings representing a Wavefront
    * .mtl file. The support for this file format is <em>very</em> minimal, as
    * it is unlikely that its contents would be reproducible between a variety
    * of renderers. The material's fill is set to the diffuse color.
    *
    * @param lines the String tokens
    *
    * @return the material
    */
   @SuppressWarnings ( "null" )
   public static MaterialSolid[] fromMtl ( final String[] lines ) {

      final int len = lines.length;
      String[] tokens;
      final ArrayList < MaterialSolid > result = new ArrayList <>();
      MaterialSolid current = null;

      String alpha = "1.0";

      for ( int i = 0; i < len; ++i ) {

         /* Split line by spaces. */
         tokens = lines[i].split("\\s+");

         /* Skip empty lines. */
         if ( tokens.length > 0 ) {
            final String initialToken = tokens[0].toLowerCase();

            if ( initialToken.equals("newmtl") ) {

               current = new MaterialSolid();
               result.add(current);
               current.name = tokens[1];
               current.fill.set(0.8f, 0.8f, 0.8f, 1.0f);

            } else if ( initialToken.equals("d") ) {

               alpha = tokens[1];

            } else if ( initialToken.equals("kd") ) {

               /* Diffuse color. Default: (0.8, 0.8, 0.8) . */
               current.fill.set(tokens[1], tokens[2], tokens[3], alpha);

            }
         }
      }

      return result.toArray(new MaterialSolid[result.size()]);
   }

   /**
    * Default material to use when an entity does not have one.
    *
    * @param gamma gamma adjustment
    *
    * @return the material string
    */
   static String defaultBlenderMaterial ( final float gamma ) {

      return new StringBuilder(256).append("{\"name\": \"").append(
         "Material").append("\", \"fill\": ").append(
            Color.fromHex(IUp.DEFAULT_FILL_COLOR, new Color()).toBlenderCode(
               gamma, true)).append(", \"metallic\": 0.0").append(
                  ", \"roughness\": 1.0").append(", \"specular\": 0.0").append(
                     ", \"clearcoat\": 0.0").append(
                        ", \"clearcoat_roughness\": 0.001").append(
                           '}').toString();
   }

}

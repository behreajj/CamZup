package camzup.pfriendly;

import java.util.Iterator;

import camzup.core.ArcMode;
import camzup.core.Color;
import camzup.core.Curve2;
import camzup.core.Curve3;
import camzup.core.Experimental;
import camzup.core.ICurve;
import camzup.core.IUtils;
import camzup.core.Knot2;
import camzup.core.Knot3;
import camzup.core.Mat3;
import camzup.core.Mat4;
import camzup.core.MaterialSolid;
import camzup.core.Mesh2;
import camzup.core.Mesh3;
import camzup.core.Transform2;
import camzup.core.Transform3;
import camzup.core.TransformOrder;
import camzup.core.Utils;
import camzup.core.Vec2;
import camzup.core.Vec3;
import camzup.core.Vec4;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PMatrix;
import processing.core.PMatrix2D;
import processing.core.PMatrix3D;
import processing.core.PShape;

import processing.opengl.PGL;
import processing.opengl.PGraphicsOpenGL;

/**
 * An abstract parent class for Processing renderers based on OpenGL.
 */
public abstract class UpOgl extends PGraphicsOpenGL implements IUpOgl {

   /**
    * Whether or not to pre-multiply colors by alpha when using integer
    * colors.
    */
   public boolean usePreMultiply = true;

   /**
    * A curve to hold the arc data.
    */
   protected final Curve2 arc = new Curve2();

   /**
    * The arc-mode.
    */
   protected ArcMode arcMode = ArcMode.OPEN;

   /**
    * One divided by the maximum for the alpha channel.
    */
   protected float invColorModeA = 1.0f;

   /**
    * One divided by the maximum for the hue or red channel.
    */
   protected float invColorModeX = 1.0f;

   /**
    * One divided by the maximum for the saturation or green channel.
    */
   protected float invColorModeY = 1.0f;

   /**
    * One divided by the maximum for the brightness or blue channel.
    */
   protected float invColorModeZ = 1.0f;

   /**
    * The default constructor.
    */
   protected UpOgl ( ) {

      this.bezierBasisInverse = PMatAux.bezierBasisInverse(new PMatrix3D());
      this.curveToBezierMatrix = new PMatrix3D();
   }

   /**
    * A constructor for manually initializing the renderer.
    *
    * @param width     renderer width
    * @param height    renderer height
    * @param parent    parent applet
    * @param path      applet path
    * @param isPrimary is the renderer primary
    */
   protected UpOgl ( final int width, final int height, final PApplet parent,
      final String path, final boolean isPrimary ) {

      this.setParent(parent);
      this.setPrimary(isPrimary);
      this.setPath(path);
      this.setSize(width, height);

      this.bezierBasisInverse = PMatAux.bezierBasisInverse(new PMatrix3D());
      this.curveToBezierMatrix = new PMatrix3D();
   }

   /**
    * Applies the matrix to the renderer.
    *
    * @param source the source matrix
    */
   public void applyMatrix ( final Mat3 source ) {

      this.applyMatrixImpl(source.m00, source.m01, 0.0f, source.m02, source.m10,
         source.m11, 0.0f, source.m12, 0.0f, 0.0f, 1.0f, 0.0f, source.m20,
         source.m21, 0.0f, source.m22);
   }

   /**
    * Applies the matrix to the renderer.
    *
    * @param source the source matrix
    */
   public void applyMatrix ( final Mat4 source ) {

      this.applyMatrixImpl(source.m00, source.m01, source.m02, source.m03,
         source.m10, source.m11, source.m12, source.m13, source.m20, source.m21,
         source.m22, source.m23, source.m30, source.m31, source.m32,
         source.m33);
   }

   /**
    * Applies the projection to the current.
    *
    * @param m the matrix
    */
   public void applyProjection ( final Mat4 m ) {

      super.applyProjection(m.m00, m.m01, m.m02, m.m03, m.m10, m.m11, m.m12,
         m.m13, m.m20, m.m21, m.m22, m.m23, m.m30, m.m31, m.m32, m.m33);
   }

   /**
    * Draws an arc at a location from a start angle to a stop angle.
    *
    * @param cx    the x center
    * @param cy    the y center
    * @param r     the radius
    * @param start the start angle
    * @param stop  the stop angle
    */
   public void arc ( final float cx, final float cy, final float r,
      final float start, final float stop ) {

      this.arc(cx, cy, r, r, start, stop, PConstants.OPEN);
   }

   /**
    * Draws an arc at a location from a start angle to a stop angle.
    *
    * @param cx    the x center
    * @param cy    the y center
    * @param r0    the radius
    * @param r1    the radius
    * @param start the start angle
    * @param stop  the stop angle
    */
   @Override
   public void arc ( final float cx, final float cy, final float r0,
      final float r1, final float start, final float stop ) {

      this.arc(cx, cy, r0, r1, start, stop, PConstants.OPEN);
   }

   /**
    * Draws an arc at a location from a start angle to a stop angle.<br>
    * <br>
    * No longer supports different ellipse modes; defaults to radius. No
    * longer supports nonuniform scaling for major and minor axes; takes the
    * minimum of x1 and y1.
    *
    * @param cx    the x center
    * @param cy    the y center
    * @param r0    the radius
    * @param r1    the radius
    * @param start the start angle
    * @param stop  the stop angle
    * @param mode  the arc mode
    */
   @Override
   public void arc ( final float cx, final float cy, final float r0,
      final float r1, final float start, final float stop, final int mode ) {

      final boolean oldFill = this.fill;

      switch ( mode ) {

         case PConstants.OPEN: /* 1 */

            this.arcMode = ArcMode.OPEN;
            this.fill = false;

            break;

         case PConstants.CHORD: /* 2 */

            this.arcMode = ArcMode.CHORD;

            break;

         case PConstants.PIE: /* 3 */

         default:

            this.arcMode = ArcMode.PIE;
      }

      Curve2.arc(start, stop, Utils.min(Utils.abs(r0), Utils.abs(r1)),
         this.arcMode, this.arc);
      this.arc.translate(new Vec2(cx, cy));
      this.drawCurve2(this.arc);

      this.fill = oldFill;
   }

   /**
    * Draws an arc at a location from a start angle to a stop angle.
    *
    * @param cx    the x center
    * @param cy    the y center
    * @param r     the radius
    * @param start the start angle
    * @param stop  the stop angle
    * @param mode  the arc mode
    */
   public void arc ( final float cx, final float cy, final float r,
      final float start, final float stop, final int mode ) {

      this.arc(cx, cy, r, r, start, stop, mode);
   }

   /**
    * Sets the renderer background to an image.
    *
    * @param pimg the image
    */
   @Override
   public void background ( final PImage pimg ) {

      if ( pimg.format != PConstants.RGB && pimg.format != PConstants.ARGB ) {
         System.err.println("Background image uses an unrecognized format.");
         this.background(IUp.DEFAULT_BKG_COLOR);
      } else {
         this.backgroundImpl(pimg);
      }
   }

   /**
    * Sets the renderer background to an image. Tiles background if smaller
    * than sketch size.
    *
    * @param pimg the image
    *
    * @see ZImage#wrap(PImage, int, int, PImage)
    */
   @Override
   public void backgroundImpl ( final PImage pimg ) {

      /* PGL clearBackground method is not visible. */
      this.backgroundImpl();
      ZImage.wrap(pimg, 0, 0, this);
      this.backgroundA = 1.0f;
      this.loaded = false;
   }

   /**
    * Draws a Bezier curve.
    *
    * @param ap0x the first anchor point x
    * @param ap0y the first anchor point y
    * @param cp0x the first control point x
    * @param cp0y the first control point y
    * @param cp1x the second control point x
    * @param cp1y the second control point y
    * @param ap1x the second anchor point x
    * @param ap1y the second anchor point y
    */
   @Override
   public void bezier ( final float ap0x, final float ap0y, final float cp0x,
      final float cp0y, final float cp1x, final float cp1y, final float ap1x,
      final float ap1y ) {

      this.beginShape(PConstants.POLYGON);
      this.normalPerShape(0.0f, 0.0f, 1.0f);
      this.vertexImpl(ap0x, ap0y, 0.0f, this.textureU, this.textureV);
      this.bezierVertexImpl(cp0x, cp0y, 0.0f, cp1x, cp1y, 0.0f, ap1x, ap1y,
         0.0f);
      this.endShape(PConstants.OPEN);
   }

   /**
    * Draws a Bezier curve.
    *
    * @param ap0x the first anchor point x
    * @param ap0y the first anchor point y
    * @param ap0z the first anchor point z
    * @param cp0x the first control point x
    * @param cp0y the first control point y
    * @param cp0z the first control point z
    * @param cp1x the second control point x
    * @param cp1y the second control point y
    * @param cp1z the second control point z
    * @param ap1x the second anchor point x
    * @param ap1y the second anchor point y
    * @param ap1z the second anchor point z
    */
   @Override
   public void bezier ( final float ap0x, final float ap0y, final float ap0z,
      final float cp0x, final float cp0y, final float cp0z, final float cp1x,
      final float cp1y, final float cp1z, final float ap1x, final float ap1y,
      final float ap1z ) {

      this.beginShape(PConstants.POLYGON);
      this.vertexImpl(ap0x, ap0y, ap0z, this.textureU, this.textureV);
      this.bezierVertexImpl(cp0x, cp0y, cp0z, cp1x, cp1y, cp1z, ap1x, ap1y,
         ap1z);
      this.endShape(PConstants.OPEN);
   }

   /**
    * Evaluates a Bezier curve at step t for points ap0, cp0, cp1 and ap1. The
    * parameter t varies between [0.0, 1.0]; ap0 and ap1 are the curve's
    * anchor points; cp0 and cp1 the control points. This can be done once
    * with the x coordinates and a second time with the y coordinates to get
    * the location of a Bezier curve at t.
    *
    * @param ap0 the first anchor point
    * @param cp0 the first control point
    * @param cp1 the second control point
    * @param ap1 the second anchor point
    * @param t   the step
    */
   @Override
   public float bezierPoint ( final float ap0, final float cp0, final float cp1,
      final float ap1, final float t ) {

      /* @formatter:off */
      final float u = 1.0f - t;
      return ( ap0 * u + cp0 * ( t + t + t ) ) * u * u +
             ( ap1 * t + cp1 * ( u + u + u ) ) * t * t;
      /* @formatter:on */
   }

   /**
    * Finds a tangent on a curve according to a step in the range [0.0, 1.0].
    *
    * @param ap0 the first anchor point
    * @param cp0 the first control point
    * @param cp1 the second control point
    * @param ap1 the second anchor point
    * @param t   the step
    *
    * @return the tangent
    */
   @Override
   public float bezierTangent ( final float ap0, final float cp0,
      final float cp1, final float ap1, final float t ) {

      final float t3 = t + t + t;
      final float b2 = cp0 + cp0;
      final float ac = ap0 + cp1;
      final float bna = cp0 - ap0;

      return t3 * t * ( b2 + cp0 + ap1 - ( ac + cp1 + cp1 ) ) + ( t3 + t3 )
         * ( ac - b2 ) + ( bna + bna + bna );
   }

   /**
    * Unsupported by this renderer. Use a MeshEntity and Mesh instead.
    *
    * @param size the size
    */
   @Override
   public void box ( final float size ) { PApplet.showMethodWarning("box"); }

   /**
    * Unsupported by this renderer. Use a MeshEntity and Mesh instead.
    *
    * @param w the width
    * @param h the height
    * @param d the depth
    */
   @Override
   public void box ( final float w, final float h, final float d ) {

      PApplet.showMethodWarning("box");
   }

   /**
    * Draws a circle at a location
    *
    * @param x    the x coordinate
    * @param y    the y coordinate
    * @param size the size
    */
   @Override
   public void circle ( final float x, final float y, final float size ) {

      this.ellipseImpl(x, y, size, size);
   }

   /**
    * Calculates the color channels from a color object. Does not check
    * whether the color should be pre-multiplied by alpha; the user is trusted
    * to do so manually if desired.
    *
    * @param c the color
    */
   public void colorCalc ( final Color c ) {

      /* Clamp values to the range [0.0, 1.0] . */
      this.calcA = c.a < 0.0f ? 0.0f : c.a > 1.0f ? 1.0f : c.a;
      this.calcB = c.b < 0.0f ? 0.0f : c.b > 1.0f ? 1.0f : c.b;
      this.calcG = c.g < 0.0f ? 0.0f : c.g > 1.0f ? 1.0f : c.g;
      this.calcR = c.r < 0.0f ? 0.0f : c.r > 1.0f ? 1.0f : c.r;

      /* Convert from [0.0, 1.0] to [0, 255] . */
      this.calcAi = ( int ) ( this.calcA * 0xff + 0.5f );
      this.calcBi = ( int ) ( this.calcB * 0xff + 0.5f );
      this.calcGi = ( int ) ( this.calcG * 0xff + 0.5f );
      this.calcRi = ( int ) ( this.calcR * 0xff + 0.5f );

      this.calcAlpha = this.calcAi != 0xff;
      this.calcColor = this.calcAi << 0x18 | this.calcRi << 0x10 | this.calcGi
         << 0x08 | this.calcBi;
   }

   /**
    * Exposes the color calculation to the public. Includes the option to pre
    * multiply alpha.
    *
    * @param x      the first color channel, hue or red
    * @param y      the second color channel, saturation or green
    * @param z      the third color channel, brightness or blue
    * @param w      the alpha channel
    * @param premul pre-multiply alpha
    */
   public void colorCalc ( final float x, final float y, final float z,
      final float w, final boolean premul ) {

      this.calcA = w * this.invColorModeA;
      this.calcB = z * this.invColorModeZ;
      this.calcG = y * this.invColorModeY;
      this.calcR = x * this.invColorModeX;

      this.calcA = this.calcA < 0.0f ? 0.0f : this.calcA > 1.0f ? 1.0f
         : this.calcA;

      switch ( this.colorMode ) {

         case PConstants.HSB: /* 3 */

            final float[] rgb = ColorAux.hsbToRgb(this.calcR, this.calcG,
               this.calcB);

            this.calcB = rgb[2];
            this.calcG = rgb[1];
            this.calcR = rgb[0];

            break;

         case PConstants.RGB: /* 1 */

         default:

            this.calcB = this.calcB < 0.0f ? 0.0f : this.calcB > 1.0f ? 1.0f
               : this.calcB;
            this.calcG = this.calcG < 0.0f ? 0.0f : this.calcG > 1.0f ? 1.0f
               : this.calcG;
            this.calcR = this.calcR < 0.0f ? 0.0f : this.calcR > 1.0f ? 1.0f
               : this.calcR;

      }

      /* Pre-multiply alpha. */
      if ( premul ) {
         this.calcB *= this.calcA;
         this.calcG *= this.calcA;
         this.calcR *= this.calcA;
      }

      /* Convert from [0.0, 1.0] to [0, 255] . */
      this.calcAi = ( int ) ( this.calcA * 0xff + 0.5f );
      this.calcBi = ( int ) ( this.calcB * 0xff + 0.5f );
      this.calcGi = ( int ) ( this.calcG * 0xff + 0.5f );
      this.calcRi = ( int ) ( this.calcR * 0xff + 0.5f );

      this.calcAlpha = this.calcAi != 0xff;
      this.calcColor = this.calcAi << 0x18 | this.calcRi << 0x10 | this.calcGi
         << 0x08 | this.calcBi;
   }

   /**
    * Calculates a color from an integer containing alpha and RGB channels.
    *
    * @param argb   the hexadecimal color
    * @param premul pre-multiply alpha
    */
   public void colorCalc ( final int argb, final boolean premul ) {

      /* Clamping to [0.0, 1.0] shouldn't be necessary. */
      this.calcA = IUtils.ONE_255 * ( argb >> 0x18 & 0xff );
      this.calcB = IUtils.ONE_255 * ( argb & 0xff );
      this.calcG = IUtils.ONE_255 * ( argb >> 0x08 & 0xff );
      this.calcR = IUtils.ONE_255 * ( argb >> 0x10 & 0xff );

      /* Pre-multiply alpha. */
      if ( premul ) {
         this.calcB *= this.calcA;
         this.calcG *= this.calcA;
         this.calcR *= this.calcA;
      }

      /* Convert from [0.0, 1.0] to [0, 255] . */
      this.calcAi = ( int ) ( this.calcA * 0xff + 0.5f );
      this.calcBi = ( int ) ( this.calcB * 0xff + 0.5f );
      this.calcGi = ( int ) ( this.calcG * 0xff + 0.5f );
      this.calcRi = ( int ) ( this.calcR * 0xff + 0.5f );
      this.calcAlpha = this.calcAi != 0xff;

      this.calcColor = this.calcAi << 0x18 | this.calcRi << 0x10 | this.calcGi
         << 0x08 | this.calcBi;
   }

   /**
    * Sets the renderer's color mode. Color channel maximums should be within
    * the range [{@value IUp#COLOR_MODE_MIN}, {@value IUp#COLOR_MODE_MAX}] to
    * mitigate precision issues arising from conversion between single
    * precision real numbers and colors stored in 32-bit integers with [0,
    * 255] per channel.
    *
    * @param mode the color mode, HSB or RGB
    * @param max1 the first channel maximum, hue or red
    * @param max2 the second channel maximum, saturation or green
    * @param max3 the third channel maximum, brightness or blue
    * @param aMax the alpha channel maximum
    */
   @Override
   public void colorMode ( final int mode, final float max1, final float max2,
      final float max3, final float aMax ) {

      /*
       * See https://discourse.processing.org/t/colormode-and-rgba-values/31379
       */

      super.colorMode(mode, max1 < IUp.COLOR_MODE_MIN ? IUp.COLOR_MODE_MIN
         : max1 > IUp.COLOR_MODE_MAX ? IUp.COLOR_MODE_MAX : max1, max2
            < IUp.COLOR_MODE_MIN ? IUp.COLOR_MODE_MIN : max2
               > IUp.COLOR_MODE_MAX ? IUp.COLOR_MODE_MAX : max2, max3
                  < IUp.COLOR_MODE_MIN ? IUp.COLOR_MODE_MIN : max3
                     > IUp.COLOR_MODE_MAX ? IUp.COLOR_MODE_MAX : max3, aMax
                        < IUp.COLOR_MODE_MIN ? IUp.COLOR_MODE_MIN : aMax
                           > IUp.COLOR_MODE_MAX ? IUp.COLOR_MODE_MAX : aMax);

      this.invColorModeX = 1.0f / this.colorModeX;
      this.invColorModeY = 1.0f / this.colorModeY;
      this.invColorModeZ = 1.0f / this.colorModeZ;
      this.invColorModeA = 1.0f / this.colorModeA;
   }

   /**
    * Draws a curved line. The first three parameters specify the start
    * control point; the last three parameters specify the ending control
    * point. The middle parameters specify the start and stop of the curve.
    *
    * @param x1 control point 0 x
    * @param y1 control point 0 y
    * @param x2 mid point 0 x
    * @param y2 mid point 0 y
    * @param x3 mid point 1 x
    * @param y3 mid point 1 y
    * @param x4 control point 1 x
    * @param y4 control point 1 y
    */
   @Override
   public void curve ( final float x1, final float y1, final float x2,
      final float y2, final float x3, final float y3, final float x4,
      final float y4 ) {

      this.beginShape(PConstants.POLYGON);
      this.normalPerShape(0.0f, 0.0f, 1.0f);
      this.curveVertexImpl(x1, y1, 0.0f);
      this.curveVertexImpl(x2, y2, 0.0f);
      this.curveVertexImpl(x3, y3, 0.0f);
      this.curveVertexImpl(x4, y4, 0.0f);
      this.endShape(PConstants.OPEN);
   }

   /**
    * Draws a curved line on the screen. The first three parameters specify
    * the start control point; the last three parameters specify the ending
    * control point. The middle parameters specify the start and stop of the
    * curve.
    *
    * @param x1 control point 0 x
    * @param y1 control point 0 y
    * @param z1 control point 0 z
    * @param x2 mid point 0 x
    * @param y2 mid point 0 y
    * @param z2 mid point 0 z
    * @param x3 mid point 1 x
    * @param y3 mid point 1 y
    * @param z3 mid point 1 z
    * @param x4 control point 1 x
    * @param y4 control point 1 y
    * @param z4 control point 1 z
    */
   @Override
   public void curve ( final float x1, final float y1, final float z1,
      final float x2, final float y2, final float z2, final float x3,
      final float y3, final float z3, final float x4, final float y4,
      final float z4 ) {

      this.beginShape(PConstants.POLYGON);
      this.curveVertexImpl(x1, y1, z1);
      this.curveVertexImpl(x2, y2, z2);
      this.curveVertexImpl(x3, y3, z3);
      this.curveVertexImpl(x4, y4, z4);
      this.endShape(PConstants.OPEN);
   }

   /**
    * Sets the renderer's default styling. This includes color mode, fill,
    * stroke, shape modes, fonts, textures and camera.
    */
   @Override
   public void defaultSettings ( ) {

      /* Color. */
      this.format = PConstants.ARGB;
      this.colorMode(PConstants.RGB, IUp.DEFAULT_COLOR_MAX);
      this.blendMode = PConstants.BLEND;
      this.blendModeImpl();
      this.fill(IUp.DEFAULT_FILL_COLOR);
      if ( this.primaryGraphics ) { this.background(IUp.DEFAULT_BKG_COLOR); }

      /* Stroke. */
      this.stroke(IUp.DEFAULT_STROKE_COLOR);
      this.strokeWeight(IUp.DEFAULT_STROKE_WEIGHT);
      this.strokeJoin(PConstants.ROUND);
      this.strokeCap(PConstants.ROUND);
      this.stroke = true;

      /* Shape. */
      this.rectMode(PConstants.CENTER);
      this.ellipseMode(PConstants.CENTER);
      this.imageMode(PConstants.CENTER);
      this.shapeMode = PConstants.CENTER;

      /* Text. */
      this.textFont = null;
      this.textSize = IUp.DEFAULT_TEXT_SIZE;
      this.textLeading = IUp.DEFAULT_TEXT_LEADING;
      this.textAlign = PConstants.CENTER;
      this.textAlignY = PConstants.CENTER;
      this.textMode = PConstants.MODEL;

      /* Textures. */
      this.textureMode = PConstants.NORMAL;
      this.textureWrap = PConstants.REPEAT;

      /* Lights and material properties. */
      this.ambient(this.colorModeX * IUpOgl.DEFAULT_AMB_R, this.colorModeY
         * IUpOgl.DEFAULT_AMB_G, this.colorModeZ * IUpOgl.DEFAULT_AMB_B);
      this.specular(this.colorModeX * IUpOgl.DEFAULT_SPEC_R, this.colorModeY
         * IUpOgl.DEFAULT_SPEC_G, this.colorModeZ * IUpOgl.DEFAULT_SPEC_B);
      this.emissive(0.0f, 0.0f, 0.0f);
      this.shininess(0.0f);

      /* Camera. */
      this.cameraAspect = this.defCameraAspect = IUp.DEFAULT_ASPECT;
      this.cameraFOV = this.defCameraFOV = IUp.DEFAULT_FOV;
      this.cameraNear = this.defCameraNear = IUp.DEFAULT_NEAR_CLIP;
      this.cameraFar = this.defCameraFar = IUp.DEFAULT_FAR_CLIP;

      /* Normals. */
      this.autoNormal = false;
      this.normalMode = PGraphics.NORMAL_MODE_SHAPE;
      this.normalX = 0.0f;
      this.normalY = 0.0f;
      this.normalZ = 1.0f;

      /* Control flow flags. */
      this.shape = 0;
      this.setAmbient = false;
      this.manipulatingCamera = false;
      this.settingsInited = true;
      this.reapplySettings = false;
      // this.hint(PConstants.DISABLE_OPENGL_ERRORS);
   }

   /**
    * Attempts to make the hint system more convenient to work with. From the
    * reference:<br>
    * <br>
    * Disables writing into the depth buffer. This means that a shape drawn
    * with this hint can be hidden by another shape drawn later, irrespective
    * of their distances to the camera. Note that this is different from
    * disabling the depth test. The depth test is still applied, as long as
    * the DISABLE_DEPTH_TEST hint is not called, but the depth values of the
    * objects are not recorded.
    */
   public void disableDepthMask ( ) {

      this.hints[PConstants.DISABLE_DEPTH_MASK] = true;
      this.flush();
      this.pgl.depthMask(false);
   }

   /**
    * Attempts to make the hint system more convenient to work with. From the
    * reference:<br>
    * <br>
    * Disable the z buffer, allowing you to draw on top of everything at will.
    * When depth testing is disabled, items will be drawn to the screen
    * sequentially, like a painting. When called, this will also clear the
    * depth buffer.
    */
   public void disableDepthTest ( ) {

      this.hints[PConstants.DISABLE_DEPTH_TEST] = true;
      this.flush();
      this.pgl.disable(PGL.DEPTH_TEST);
   }

   /**
    * Attempts to make the hint system more convenient to work with. From the
    * reference:<br>
    * <br>
    * Disable generation of texture mipmaps in P2D or P3D. This results in
    * lower quality - but faster - rendering of texture images when they
    * appear smaller than their native resolutions (the mipmaps are
    * scaled-down versions of a texture that make it look better when drawing
    * it at a small size). However, the difference in performance is fairly
    * minor on recent desktop video cards.
    */
   public void disableMipMaps ( ) {

      this.hints[PConstants.DISABLE_TEXTURE_MIPMAPS] = true;
   }

   /**
    * Attempts to make the hint system more convenient to work with. From the
    * reference:<br>
    * <br>
    * Forces the P3D renderer to draw each shape (including its strokes)
    * separately, instead of batching them into larger groups for better
    * performance. One consequence of this is that 2D items drawn with P3D are
    * correctly stacked on the screen, depending on the order in which they
    * were drawn. Otherwise, glitches such as the stroke lines being drawn on
    * top of the interior of all the shapes will occur. However, this hint can
    * make rendering substantially slower, so it is recommended to use it only
    * when drawing a small amount of shapes.
    */
   public void disableOptimizedStroke ( ) {

      this.hints[PConstants.DISABLE_OPTIMIZED_STROKE] = true;
      this.flush();
      this.flushMode = PGraphicsOpenGL.FLUSH_CONTINUOUSLY;
   }

   /**
    * Draws an ellipse; the meaning of the two parameters depends on the
    * renderer's ellipseMode.
    *
    * @param x the first parameter
    * @param y the second parameter
    * @param w the third parameter
    * @param h the fourth parameter
    */
   @Override
   public void ellipse ( final float x, final float y, final float w,
      final float h ) {

      this.ellipseImpl(x, y, w, h);
   }

   /**
    * Draws an ellipse. The parameters meanings are determined by the
    * ellipseMode.
    *
    * @param x the first parameter
    * @param y the second parameter
    * @param w the third parameter
    * @param h the fourth parameter
    *
    * @see Utils#abs(float)
    */
   @Override
   public void ellipseImpl ( final float x, final float y, final float w,
      final float h ) {

      /*
       * This needs to be implemented to resolve a bug with CORNER. All ellipse
       * functions should perform no work, but redirect immediately here.
       */

      float extapw;
      float extaph;
      float extcpw;
      float extcph;
      float xc;
      float yc;

      switch ( this.ellipseMode ) {
         case PConstants.CORNER: /* 0 */

            extapw = 0.5f * w;
            extcpw = ICurve.KAPPA_2 * w;
            extaph = 0.5f * h;
            extcph = ICurve.KAPPA_2 * h;

            xc = x + extapw;
            yc = y - extaph;

            break;

         case PConstants.CORNERS: /* 1 */

            final float wcalc = Utils.abs(w - x);
            final float hcalc = Utils.abs(h - y);

            xc = ( x + w ) * 0.5f;
            yc = ( y + h ) * 0.5f;

            extapw = 0.5f * wcalc;
            extcpw = ICurve.KAPPA_2 * wcalc;
            extaph = 0.5f * hcalc;
            extcph = ICurve.KAPPA_2 * hcalc;

            break;

         case PConstants.RADIUS: /* 2 */

            xc = x;
            yc = y;

            extapw = w;
            extcpw = ICurve.KAPPA * extapw;
            extaph = h;
            extcph = ICurve.KAPPA * extaph;

            break;

         case PConstants.CENTER: /* 3 */
         default:

            xc = x;
            yc = y;

            extapw = 0.5f * w;
            extcpw = ICurve.KAPPA_2 * w;
            extaph = 0.5f * h;
            extcph = ICurve.KAPPA_2 * h;
      }

      final float right = xc + extapw;
      final float left = xc - extapw;
      final float top = yc + extaph;
      final float bottom = yc - extaph;

      this.beginShape(PConstants.POLYGON);
      this.normalPerShape(0.0f, 0.0f, 1.0f);
      this.vertexImpl(right, yc, 0.0f, this.textureU, this.textureV);
      this.bezierVertexImpl(right, yc + extcph, 0.0f, xc + extcpw, top, 0.0f,
         xc, top, 0.0f);
      this.bezierVertexImpl(xc - extcpw, top, 0.0f, left, yc + extcph, 0.0f,
         left, yc, 0.0f);
      this.bezierVertexImpl(left, yc - extcph, 0.0f, xc - extcpw, bottom, 0.0f,
         xc, bottom, 0.0f);
      this.bezierVertexImpl(xc + extcpw, bottom, 0.0f, right, yc - extcph, 0.0f,
         right, yc, 0.0f);
      this.endShape(PConstants.CLOSE);
   }

   /**
    * Attempts to make the hint system more convenient to work with.
    */
   public void enableDepthMask ( ) {

      /*
       * Paired constants have signed values: PConstants#ENABLE_DEPTH_MASK has a
       * negative value.
       */
      this.hints[PConstants.DISABLE_DEPTH_MASK] = false;
      this.flush();
      this.pgl.depthMask(true);
   }

   /**
    * Attempts to make the hint system more convenient to work with.
    */
   public void enableDepthTest ( ) {

      /*
       * Paired constants have signed values: PConstants#ENABLE_DEPTH_TEST has a
       * negative value.
       */
      this.hints[PConstants.DISABLE_DEPTH_TEST] = false;
      this.flush();
      this.pgl.enable(PGL.DEPTH_TEST);
   }

   /**
    * Attempts to make the hint system more convenient to work with.
    */
   public void enableMipMaps ( ) {

      /*
       * Paired constants have signed values: PConstants#ENABLE_TEXTURE_MIPMAPS
       * has a negative value.
       */
      this.hints[PConstants.DISABLE_TEXTURE_MIPMAPS] = false;
   }

   /**
    * Attempts to make the hint system more convenient to work with.
    */
   public void enableOptimizedStroke ( ) {

      /*
       * Paired constants have signed values: PConstants#ENABLE_OPTIMIZED_STROKE
       * has a negative value.
       */
      this.hints[PConstants.DISABLE_OPTIMIZED_STROKE] = false;
      this.flush();
      this.flushMode = PGraphicsOpenGL.FLUSH_WHEN_FULL;
   }

   /**
    * Sets the renderer's current fill to the color. Does not check whether
    * the color should be pre-multiplied by alpha; the user is trusted to do
    * so manually if desired.
    *
    * @param c the color
    */
   @Override
   public void fill ( final Color c ) {

      this.colorCalc(c);
      this.fillFromCalc();
   }

   /**
    * Sets the renderer projection matrix to the frustum defined by the edges
    * of the view port.
    *
    * @param left   the left edge of the window
    * @param right  the right edge of the window
    * @param bottom the bottom edge of the window
    * @param top    the top edge of the window
    * @param near   the near clip plane
    * @param far    the far clip plane
    */
   @Override
   public void frustum ( final float left, final float right,
      final float bottom, final float top, final float near, final float far ) {

      this.cameraNear = near;
      this.cameraFar = far;

      PMatAux.frustum(left, right, bottom, top, near, far, this.projection);
   }

   /**
    * Gets whether or not normals are calculated in
    * {@link PGraphicsOpenGL#vertex(float, float, float)} and variants.
    *
    * @return the flag
    */
   public boolean getAutoNormal ( ) { return this.autoNormal; }

   /**
    * Gets this renderer's background color.
    *
    * @return the background color
    */
   @Override
   public int getBackground ( ) { return this.backgroundColor; }

   /**
    * Gets this renderer's background color.
    *
    * @param target the output color
    *
    * @return the background color
    */
   @Override
   public Color getBackground ( final Color target ) {

      return Color.fromHex(this.backgroundColor, target);
   }

   /**
    * Gets the renderer's height.
    *
    * @return the height
    */
   @Override
   public float getHeight ( ) { return this.height; }

   /**
    * Gets the renderer camera's location on the x axis.
    *
    * @return the x location
    */
   public float getLocX ( ) { return this.cameraX; }

   /**
    * Gets the renderer camera's location on the y axis.
    *
    * @return the y location
    */
   public float getLocY ( ) { return this.cameraY; }

   /**
    * Gets the renderer camera's location on the z axis.
    *
    * @return the z location
    */
   public float getLocZ ( ) { return this.cameraZ; }

   /**
    * Retrieves the renderer's matrix.
    */
   @Override
   public PMatrix getMatrix ( ) { return this.getMatrix(new PMatrix3D()); }

   /**
    * Gets the renderer model view matrix.
    *
    * @param target the output matrix
    *
    * @return the model view
    */
   public Mat4 getMatrix ( final Mat4 target ) {

      /* @formatter:off */
      return target.set(
         this.modelview.m00, this.modelview.m01, this.modelview.m02, this.modelview.m03,
         this.modelview.m10, this.modelview.m11, this.modelview.m12, this.modelview.m13,
         this.modelview.m20, this.modelview.m21, this.modelview.m22, this.modelview.m23,
         this.modelview.m30, this.modelview.m31, this.modelview.m32, this.modelview.m33);
      /* @formatter:on */
   }

   /**
    * Gets the renderer model view matrix. For use with 2.5D renderers, where
    * it's assumed only 2D transforms will have been applied to the 3D model
    * view.
    *
    * @param target the output matrix
    *
    * @return the model view
    */
   @Override
   public PMatrix2D getMatrix ( final PMatrix2D target ) {

      target.set(this.modelview.m00, this.modelview.m01, this.modelview.m03,
         this.modelview.m10, this.modelview.m11, this.modelview.m13);
      return target;
   }

   /**
    * Gets the renderer model view matrix.
    *
    * @param target the output matrix
    *
    * @return the model view
    */
   @Override
   public PMatrix3D getMatrix ( final PMatrix3D target ) {

      target.set(this.modelview);
      return target;
   }

   /**
    * Gets the current normal mode.
    *
    * @return the normal mode
    */
   public NormalMode getNormalMode ( ) {

      return NormalMode.fromValue(this.normalMode);
   }

   /**
    * Gets the renderer's parent applet.
    *
    * @return the applet
    */
   @Override
   public PApplet getParent ( ) { return this.parent; }

   /**
    * Gets the renderer's size.
    *
    * @param target the output vector
    *
    * @return the size
    */
   @Override
   public Vec2 getSize ( final Vec2 target ) {

      return target.set(this.width, this.height);
   }

   /**
    * Gets the renderer's width.
    *
    * @return the width
    */
   @Override
   public float getWidth ( ) { return this.width; }

   /**
    * Displays a PGraphicsOpenGL at the origin.
    *
    * @param buff the renderer
    */
   public void image ( final PGraphicsOpenGL buff ) {

      if ( buff.pgl.threadIsCurrent() ) { this.image(( PImage ) buff); }
   }

   /**
    * Displays a PGraphicsOpenGL buffer. Checks if the buffer's PGL thread is
    * current before proceeding. This is to help ensure that beginDraw and
    * endDraw have already been called.
    *
    * @param buff the renderer
    * @param x    the first x coordinate
    * @param y    the first y coordinate
    */
   public void image ( final PGraphicsOpenGL buff, final float x,
      final float y ) {

      if ( buff.pgl.threadIsCurrent() ) { this.image(( PImage ) buff, x, y); }
   }

   /**
    * Displays a PGraphicsOpenGL buffer. Checks if the buffer's PGL thread is
    * current before proceeding. This is to help ensure that beginDraw and
    * endDraw have already been called.
    *
    * @param buff the renderer
    * @param x1   the first x coordinate
    * @param y1   the first y coordinate
    * @param x2   the second x coordinate
    * @param y2   the second y coordinate
    */
   public void image ( final PGraphicsOpenGL buff, final float x1,
      final float y1, final float x2, final float y2 ) {

      if ( buff.pgl.threadIsCurrent() ) {
         this.image(( PImage ) buff, x1, y1, x2, y2);
      }
   }

   /**
    * Displays a PGraphicsOpenGL buffer. Checks if the buffer's PGL thread is
    * current before proceeding. This is to help ensure that beginDraw and
    * endDraw have already been called.
    *
    * @param buff the renderer
    * @param x1   the first x coordinate
    * @param y1   the first y coordinate
    * @param x2   the second x coordinate
    * @param y2   the second y coordinate
    * @param u1   the image top-left corner u
    * @param v1   the image top-left corner v
    * @param u2   the image bottom-right corner u
    * @param v2   the image bottom-right corner v
    */
   public void image ( final PGraphicsOpenGL buff, final float x1,
      final float y1, final float x2, final float y2, final int u1,
      final int v1, final int u2, final int v2 ) {

      if ( buff.pgl.threadIsCurrent() ) {
         this.image(( PImage ) buff, x1, y1, x2, y2, u1, v1, u2, v2);
      }
   }

   /**
    * Displays a PImage at the origin.
    *
    * @param img the image
    */
   public void image ( final PImage img ) { this.image(img, 0.0f, 0.0f); }

   /**
    * Displays a PImage at a location. Uses the image's width and height as
    * the second parameters.
    *
    * @param img the PImage
    * @param x   the x coordinate
    * @param y   the y coordinate
    */
   @Override
   public void image ( final PImage img, final float x, final float y ) {

      final boolean useImg = this.textureMode == PConstants.IMAGE;
      final float u = useImg ? img.pixelWidth : 1.0f;
      final float v = useImg ? img.pixelHeight : 1.0f;

      final float xVrf = x;
      final float yVrf = y;

      switch ( this.imageMode ) {
         case PConstants.CORNER: /* 0 */
         case PConstants.CORNERS: { /* 1 */
            this.imageCorner(img, xVrf, yVrf, img.width, img.height, 0.0f, 0.0f,
               0.0f, u, v);
         }
            break;

         case PConstants.RADIUS: /* 2 */
         case PConstants.CENTER: /* 3 */
         default: {
            this.imageCenter(img, xVrf, yVrf, img.width, img.height, 0.0f, 0.0f,
               0.0f, u, v);
         }
      }
   }

   /**
    * Displays a PImage. The meaning of the first four numbers depends on
    * imageMode.
    *
    * @param img the PImage
    * @param x1  the first x coordinate
    * @param y1  the first y coordinate
    * @param x2  the second x coordinate
    * @param y2  the second y coordinate
    */
   @Override
   public void image ( final PImage img, final float x1, final float y1,
      final float x2, final float y2 ) {

      final boolean useImg = this.textureMode == PConstants.IMAGE;
      final float u = useImg ? img.pixelWidth : 1.0f;
      final float v = useImg ? img.pixelHeight : 1.0f;
      this.image(img, x1, y1, x2, y2, 0.0f, 0.0f, 0.0f, u, v);
   }

   /**
    * Displays a PImage. The meaning of the first four numbers depends on
    * imageMode. The last four numbers specify the image texture coordinates
    * (or UVs).
    *
    * @param img the PImage
    * @param x1  the first x coordinate
    * @param y1  the first y coordinate
    * @param x2  the second x coordinate
    * @param y2  the second y coordinate
    * @param z   the z coordinate
    * @param u1  the image top-left corner u
    * @param v1  the image top-left corner v
    * @param u2  the image bottom-right corner u
    * @param v2  the image bottom-right corner v
    */
   public void image ( final PImage img, final float x1, final float y1,
      final float x2, final float y2, final float z, final float u1,
      final float v1, final float u2, final float v2 ) {

      /*
       * Off-by-one issue when texture sampling is set to POINT and the image is
       * displayed in the center as opposed to the top left corner. Drawing four
       * quadrants instead of just one does not fix the issue.
       */

      switch ( this.imageMode ) {
         case PConstants.CORNER: /* 0 */
            this.imageCorner(img, x1, y1, x2, y2, z, u1, v1, u2, v2);
            break;

         case PConstants.CORNERS: /* 1 */
            this.imageCorners(img, x1, y1, x2, y2, z, u1, v1, u2, v2);
            break;

         case PConstants.RADIUS: /* 2 */
            this.imageRadius(img, x1, y1, x2, y2, z, u1, v1, u2, v2);
            break;

         case PConstants.CENTER: /* 3 */
         default:
            this.imageCenter(img, x1, y1, x2, y2, z, u1, v1, u2, v2);
      }
   }

   /**
    * Displays a PImage. The meaning of the first four numbers depends on
    * imageMode. The last four numbers specify the image sample <em>in
    * pixels</em> regardless of image mode.
    *
    * @param img the PImage
    * @param x1  the first x coordinate
    * @param y1  the first y coordinate
    * @param x2  the second x coordinate
    * @param y2  the second y coordinate
    * @param u1  the image top-left corner u
    * @param v1  the image top-left corner v
    * @param u2  the image bottom-right corner u
    * @param v2  the image bottom-right corner v
    */
   @Override
   public void image ( final PImage img, final float x1, final float y1,
      final float x2, final float y2, final int u1, final int v1, final int u2,
      final int v2 ) {

      final float wInv = img.pixelWidth > 0 ? 1.0f / img.pixelWidth : 1.0f;
      final float hInv = img.pixelHeight > 0 ? 1.0f / img.pixelHeight : 1.0f;
      this.image(img, x1, y1, x2, y2, 0.0f, u1 * wInv, v1 * hInv, u2 * wInv, v2
         * hInv);
   }

   /**
    * Displays a PGraphicsOpenGL buffer. Checks if the buffer's PGL thread is
    * current before proceeding. This is to help ensure that beginDraw and
    * endDraw have already been called.
    *
    * @param buff the renderer
    * @param x1   the first x coordinate
    * @param y1   the first y coordinate
    * @param x2   the second x coordinate
    * @param y2   the second y coordinate
    * @param u1   the image top-left corner u
    * @param v1   the image top-left corner v
    * @param u2   the image bottom-right corner u
    * @param v2   the image bottom-right corner v
    */
   public void imageImpl ( final PGraphicsOpenGL buff, final float x1,
      final float y1, final float x2, final float y2, final float u1,
      final float v1, final float u2, final float v2 ) {

      if ( buff.pgl.threadIsCurrent() ) {
         this.image(buff, x1, y1, x2, y2, 0.0f, u1, v1, u2, v2);
      }
   }

   /**
    * Overrides the parent's image mode so as to not throw an exception.
    *
    * @param mode the image mode constant
    */
   @Override
   public void imageMode ( final int mode ) {

      /*
       * This has to be overridden, no matter which image modes are supported,
       * because PGraphics handles an incorrect mode by throwing a run time
       * exception.
       */

      switch ( mode ) {
         case PConstants.CORNER: /* 0 */
         case PConstants.CORNERS: /* 1 */
         case PConstants.RADIUS: /* 2 */
         case PConstants.CENTER: /* 3 */
            this.imageMode = mode;
            break;

         default:
            this.imageMode = PConstants.CENTER;
      }
   }

   /**
    * Returns whether or not depth sorting is enabled.
    *
    * @return the evaluation
    */
   public boolean isDepthSortingEnabled ( ) {

      return this.isDepthSortingEnabled;
   }

   /**
    * Eases from an origin color to a destination by a step.
    *
    * @param origin the origin color
    * @param dest   the destination color
    * @param step   the factor in [0, 1]
    *
    * @return the color
    */
   @Override
   public int lerpColor ( final int origin, final int dest, final float step ) {

      return ColorAux.lerpColor(origin, dest, step, this.colorMode);
   }

   /**
    * Loads a shader from a fragment and vertex source file. May return null
    * if neither file path is valid.
    *
    * @param fnFrag the fragment shader file name
    * @param fnVert the vertex shader file name
    *
    * @return the shader
    */
   @Override
   public ZShader loadShader ( final String fnFrag, final String fnVert ) {

      ZShader shader = null;
      if ( fnFrag == null || fnFrag.equals("") ) {
         PGraphics.showWarning(
            "The fragment shader is missing, cannot create shader object");
      } else if ( fnVert == null || fnVert.equals("") ) {
         PGraphics.showWarning(
            "The vertex shader is missing, cannot create shader object");
      } else {
         shader = new ZShader(this.parent, fnVert, fnFrag);
      }

      return shader;
   }

   /**
    * Sets the renderer's stroke, stroke weight and fill to the material's.
    * Also sets whether or not to use fill and stroke.
    *
    * @param material the material
    */
   public void material ( final MaterialSolid material ) {

      this.stroke = material.useStroke;
      this.fill = material.useFill;

      if ( material.useStroke ) {
         this.strokeWeight(material.strokeWeight);
         this.stroke(material.stroke);
      }

      if ( material.useFill ) { this.fill(material.fill); }
   }

   /**
    * Finds the model view position of a point.<br>
    * <br>
    * More efficient than calling {@link PApplet#modelX(float, float, float)}
    * , {@link PApplet#modelY(float, float, float)} and
    * {@link PApplet#modelZ(float, float, float)} separately. However, it is
    * advisable to work with {@link Vec4}s and the renderer matrices directly.
    *
    * @param source the point
    * @param target the output vector
    *
    * @return the screen point
    */
   public Vec3 model ( final Vec2 source, final Vec3 target ) {

      return this.model(source.x, source.y, 0.0f, target);
   }

   /**
    * Finds the model view position of a point.<br>
    * <br>
    * More efficient than calling {@link PApplet#modelX(float, float, float)}
    * , {@link PApplet#modelY(float, float, float)} and
    * {@link PApplet#modelZ(float, float, float)} separately. However, it is
    * advisable to work with {@link Vec4}s and the renderer matrices directly.
    *
    * @param source the point
    * @param target the output vector
    *
    * @return the screen point
    */
   public Vec3 model ( final Vec3 source, final Vec3 target ) {

      return this.model(source.x, source.y, source.z, target);
   }

   /**
    * Sets the normal for a shape at a given vertex. This does <em>not</em>
    * set the normal mode.
    */
   @Override
   public void normal ( final float x, final float y, final float z ) {

      this.normalX = x;
      this.normalY = y;
      this.normalZ = z;
   }

   /**
    * Assigns a normal per begin- and end- shape.
    * {@link PGraphicsOpenGL#beginShape()} sets the
    * {@link PGraphics#normalMode} to {@link PGraphics#NORMAL_MODE_AUTO}, so
    * this must be invoked after that call.
    *
    * @param nx the x component
    * @param ny the y component
    * @param nz the z component
    */
   public void normalPerShape ( final float nx, final float ny,
      final float nz ) {

      this.normalMode = PGraphics.NORMAL_MODE_SHAPE;
      this.normalX = nx;
      this.normalY = ny;
      this.normalZ = nz;
   }

   /**
    * Sets the renderer projection to orthographic, where objects maintain
    * their size regardless of distance from the camera.
    */
   @Override
   public void ortho ( ) { this.ortho(1.0f); }

   /**
    * Sets the renderer projection to orthographic, where objects maintain
    * their size regardless of distance from the camera.
    *
    * @param zoom the zoom level
    */
   public void ortho ( final float zoom ) {

      /* Never use defCameraXXX values. They are not actual constants. */

      /*
       * Since width and height are divided by half, zoomInv includes that
       * factor.
       */
      final float zoomInv = zoom != 0.0f ? 0.5f / zoom : 1.0f;
      final float right = this.width < 128 ? IUp.DEFAULT_HALF_WIDTH : zoomInv
         * this.width;
      final float top = this.height < 128 ? IUp.DEFAULT_HALF_HEIGHT : zoomInv
         * this.height;
      this.ortho(-right, right, -top, top);
   }

   /**
    * Sets the renderer projection to orthographic, where objects maintain
    * their size regardless of distance from the camera.
    *
    * @param left   the left edge of the window
    * @param right  the right edge of the window
    * @param bottom the bottom edge of the window
    * @param top    the top edge of the window
    */
   @Override
   public void ortho ( final float left, final float right, final float bottom,
      final float top ) {

      float far = IUp.DEFAULT_FAR_CLIP;
      if ( this.eyeDist != 0.0f ) {
         far = IUp.DEFAULT_NEAR_CLIP + this.eyeDist * 10.0f;
      }
      this.ortho(left, right, bottom, top, IUp.DEFAULT_NEAR_CLIP, far);
   }

   /**
    * Sets the renderer projection to orthographic, where objects maintain
    * their size regardless of distance from the camera.
    *
    * @param left   the left edge of the window
    * @param right  the right edge of the window
    * @param bottom the bottom edge of the window
    * @param top    the top edge of the window
    * @param near   the near clip plane
    * @param far    the far clip plane
    *
    * @see PMatAux#orthographic(float, float, float, float, float, float,
    *      PMatrix3D)
    */
   @Override
   public void ortho ( final float left, final float right, final float bottom,
      final float top, final float near, final float far ) {

      this.cameraNear = near;
      this.cameraFar = far;

      PMatAux.orthographic(left, right, bottom, top, near, far,
         this.projection);
   }

   /**
    * Sets the renderer projection to a perspective, where objects nearer to
    * the camera appear larger than objects distant from the camera. For the
    * field of view, uses {@link IUp#DEFAULT_FOV}.
    */
   @Override
   public void perspective ( ) { this.perspective(IUp.DEFAULT_FOV); }

   /**
    * Sets the renderer projection to a perspective, where objects nearer to
    * the camera appear larger than objects distant from the camera.
    *
    * @param fov the field of view
    */
   public void perspective ( final float fov ) {

      this.perspective(fov, this.width < 128 && this.height < 128
         ? IUp.DEFAULT_ASPECT : this.width / ( float ) this.height);
   }

   /**
    * Sets the renderer projection to a perspective, where objects nearer to
    * the camera appear larger than objects distant from the camera.
    *
    * @param fov    the field of view
    * @param aspect the aspect ratio, width over height
    */
   public void perspective ( final float fov, final float aspect ) {

      this.perspective(fov, aspect, IUp.DEFAULT_NEAR_CLIP, Utils.hypot(
         this.cameraX, this.cameraY, this.cameraZ) + this.eyeDist
            * IUp.DEFAULT_FAR_CLIP);
   }

   /**
    * Sets the renderer projection to a perspective, where objects nearer to
    * the camera appear larger than objects distant from the camera.
    *
    * @param fov    the field of view
    * @param aspect the aspect ratio, width over height
    * @param near   the near clip plane
    * @param far    the far clip plane
    *
    * @see PMatAux#perspective(float, float, float, float, PMatrix3D)
    */
   @Override
   public void perspective ( final float fov, final float aspect,
      final float near, final float far ) {

      this.cameraFOV = fov;
      this.cameraAspect = aspect;
      this.cameraNear = near;
      this.cameraFar = far;

      PMatAux.perspective(fov, aspect, near, far, this.projection);
   }

   /**
    * Prints the camera matrix in columns, for easier viewing in the console.
    */
   @Override
   public void printCamera ( ) { this.printCamera(IUtils.FIXED_PRINT); }

   /**
    * Prints the camera matrix in columns, for easier viewing in the console.
    *
    * @param places number of decimal places
    */
   public void printCamera ( final int places ) {

      System.out.println(PMatAux.toString(this.camera, places));
   }

   /**
    * Prints the camera inverse matrix in columns, for easier viewing in the
    * console.
    */
   public void printCameraInv ( ) {

      this.printCameraInv(IUtils.FIXED_PRINT);
   }

   /**
    * Prints the camera inverse matrix in columns, for easier viewing in the
    * console.
    *
    * @param places number of decimal places
    */
   public void printCameraInv ( final int places ) {

      System.out.println(PMatAux.toString(this.cameraInv, places));
   }

   /**
    * Prints the model view matrix in columns, for easier viewing in the
    * console.
    */
   @Override
   public void printMatrix ( ) { this.printMatrix(IUtils.FIXED_PRINT); }

   /**
    * Prints the model view matrix in columns, for easier viewing in the
    * console.
    *
    * @param places number of decimal places
    */
   public void printMatrix ( final int places ) {

      System.out.println(PMatAux.toString(this.modelview, places));
   }

   /**
    * Prints the projection matrix in columns, for easier viewing in the
    * console.
    */
   @Override
   public void printProjection ( ) { this.printProjection(IUtils.FIXED_PRINT); }

   /**
    * Prints the projection matrix in columns, for easier viewing in the
    * console.
    *
    * @param places number of decimal places
    */
   public void printProjection ( final int places ) {

      System.out.println(PMatAux.toString(this.projection, places));
   }

   /**
    * Draws a quadrilateral between four points.
    *
    * @param ax the first point x
    * @param ay the first point y
    * @param bx the second point x
    * @param by the second point y
    * @param cx the third point x
    * @param cy the third point y
    * @param dx the fourth point x
    * @param dy the fourth point y
    */
   @Override
   public void quad ( final float ax, final float ay, final float bx,
      final float by, final float cx, final float cy, final float dx,
      final float dy ) {

      this.beginShape(PConstants.POLYGON);
      this.normalPerShape(0.0f, 0.0f, 1.0f);
      this.vertexImpl(ax, ay, 0.0f, 0.0f, 0.0f);
      this.vertexImpl(bx, by, 0.0f, 1.0f, 0.0f);
      this.vertexImpl(cx, cy, 0.0f, 1.0f, 1.0f);
      this.vertexImpl(dx, dy, 0.0f, 0.0f, 1.0f);
      this.endShape(PConstants.CLOSE);
   }

   /**
    * Draws a rectangle. The meaning of the four parameters depends on
    * rectMode.
    *
    * @param x0 the first x parameter
    * @param y0 the first y parameter
    * @param x1 the second x parameter
    * @param y1 the second y parameter
    */
   @Override
   public void rect ( final float x0, final float y0, final float x1,
      final float y1 ) {

      this.rectImpl(x0, y0, x1, y1);
   }

   /**
    * Draws a rounded rectangle. The meaning of the first four parameters
    * depends on rectMode.
    *
    * @param x1 the first x parameter
    * @param y1 the first y parameter
    * @param x2 the second x parameter
    * @param y2 the second y parameter
    * @param r  the corner rounding
    */
   @Override
   public void rect ( final float x1, final float y1, final float x2,
      final float y2, final float r ) {

      this.rectImpl(x1, y1, x2, y2, r, r, r, r);
   }

   /**
    * Draws a rounded rectangle. The meaning of the first four parameters
    * depends on rectMode.
    *
    * @param x1 the first x parameter
    * @param y1 the first y parameter
    * @param x2 the second x parameter
    * @param y2 the second y parameter
    * @param tl the top-left corner rounding
    * @param tr the top-right corner rounding
    * @param br the bottom-right corner rounding
    * @param bl the bottom-left corner rounding
    */
   @Override
   public void rect ( final float x1, final float y1, final float x2,
      final float y2, final float tl, final float tr, final float br,
      final float bl ) {

      this.rectImpl(x1, y1, x2, y2, tl, tr, br, bl);
   }

   /**
    * Resets the model view and camera matrices to the identity. Resets
    * projection model view to the projection. Resets camera location to zero.
    */
   @Override
   public void resetMatrix ( ) {

      this.modelview.reset();
      this.modelviewInv.reset();
      this.camera.reset();
      this.cameraInv.reset();
      this.projmodelview.set(this.projection);

      this.cameraX = 0.0f;
      this.cameraY = 0.0f;
      this.cameraZ = 0.0f;
      this.eyeDist = 0.0f;
   }

   /**
    * Rotates the model view matrix around the z axis by an angle in radians.
    *
    * @param radians the angle in radians
    */
   @Override
   public void rotate ( final float radians ) { this.rotateZ(radians); }

   /**
    * Rotates the sketch by an angle in radians around the x axis. Do not use
    * sequences of orthonormal rotations by Euler angles; this will result in
    * gimbal lock. Instead, rotate by an angle around an axis.
    *
    * @param radians the angle
    */
   @Override
   public void rotateX ( final float radians ) {

      final float normRad = radians * IUtils.ONE_TAU;
      PMatAux.compoundRotateX(Utils.scNorm(normRad), Utils.scNorm(normRad
         - 0.25f), this.modelview, this.modelviewInv);
      PMatAux.mul(this.projection, this.modelview, this.projmodelview);
   }

   /**
    * Rotates the sketch by an angle in radians around the y axis. Do not use
    * sequences of orthonormal rotations by Euler angles; this will result in
    * gimbal lock. Instead, rotate by an angle around an axis.
    *
    * @param radians the angle
    */
   @Override
   public void rotateY ( final float radians ) {

      final float normRad = radians * IUtils.ONE_TAU;
      PMatAux.compoundRotateY(Utils.scNorm(normRad), Utils.scNorm(normRad
         - 0.25f), this.modelview, this.modelviewInv);
      PMatAux.mul(this.projection, this.modelview, this.projmodelview);
   }

   /**
    * Rotates the sketch by an angle in radians around the z axis. Do not use
    * sequences of orthonormal rotations by Euler angles; this will result in
    * gimbal lock. Instead, rotate by an angle around an axis.
    *
    * @param radians the angle
    */
   @Override
   public void rotateZ ( final float radians ) {

      final float normRad = radians * IUtils.ONE_TAU;
      PMatAux.compoundRotateZ(Utils.scNorm(normRad), Utils.scNorm(normRad
         - 0.25f), this.modelview, this.modelviewInv);
      PMatAux.mul(this.projection, this.modelview, this.projmodelview);
   }

   /**
    * Finds the screen position of a point in the world.<br>
    * <br>
    * More efficient than calling {@link PApplet#screenX(float, float, float)}
    * , {@link PApplet#screenY(float, float, float)} , and
    * {@link PApplet#screenZ(float, float, float)} separately. However, it is
    * advisable to work with {@link Vec4}s and the renderer matrices directly.
    *
    * @param source the point
    * @param target the output vector
    *
    * @return the screen point
    */
   public Vec3 screen ( final Vec2 source, final Vec3 target ) {

      return this.screen(source.x, source.y, 0.0f, target);
   }

   /**
    * Finds the screen position of a point in the world.<br>
    * <br>
    * More efficient than calling {@link PApplet#screenX(float, float, float)}
    * , {@link PApplet#screenY(float, float, float)} , and
    * {@link PApplet#screenZ(float, float, float)} separately. However, it is
    * advisable to work with {@link Vec4}s and the renderer matrices directly.
    *
    * @param source the point
    * @param target the output vector
    *
    * @return the screen point
    */
   public Vec3 screen ( final Vec3 source, final Vec3 target ) {

      return this.screen(source.x, source.y, source.z, target);
   }

   /**
    * Returns the product of the source point with the model view matrix, and
    * then with the projection matrix.
    *
    * @param source the source point
    * @param target the output vector
    *
    * @return the vector
    */
   public Vec3 screen1s ( final Vec3 source, final Vec3 target ) {

      return this.screen1s(source.x, source.y, source.z, target);
   }

   /**
    * Sets whether or not to automatically calculate normals when
    * {@link PGraphicsOpenGL#vertex(float, float, float)} and variants are
    * called.
    *
    * @param value the flag
    */
   public void setAutoNormal ( final boolean value ) {

      this.autoNormal = value;
   }

   /**
    * Resets the {@link PGraphicsOpenGL#camera} and
    * {@link PGraphicsOpenGL#cameraInv} matrices, sets the model view to the
    * input values, calculates the inverse model view, then recalculates the
    * projection model view.
    *
    * @param m00 row 0, column 0
    * @param m01 row 0, column 1
    * @param m02 row 0, column 2
    * @param m10 row 1, column 0
    * @param m11 row 1, column 1
    * @param m12 row 1, column 2
    */
   public void setMatrix ( final float m00, final float m01, final float m02,
      final float m10, final float m11, final float m12 ) {

      this.setMatrix(m00, m01, 0.0f, m02, m10, m11, 0.0f, m12, 0.0f, 0.0f, 1.0f,
         0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
   }

   /**
    * Resets the {@link PGraphicsOpenGL#camera} and
    * {@link PGraphicsOpenGL#cameraInv} matrices, sets the model view to the
    * input values, calculates the inverse model view, then recalculates the
    * projection model view.
    *
    * @param m00 row 0, column 0
    * @param m01 row 0, column 1
    * @param m02 row 0, column 2
    * @param m03 row 0, column 3
    * @param m10 row 1, column 0
    * @param m11 row 1, column 1
    * @param m12 row 1, column 2
    * @param m13 row 1, column 3
    * @param m20 row 2, column 0
    * @param m21 row 2, column 1
    * @param m22 row 2, column 2
    * @param m23 row 2, column 3
    * @param m30 row 3, column 0
    * @param m31 row 3, column 1
    * @param m32 row 3, column 2
    * @param m33 row 3, column 3
    *
    * @see PMatAux#inverse(PMatrix3D, PMatrix3D)
    */
   public void setMatrix ( final float m00, final float m01, final float m02,
      final float m03, final float m10, final float m11, final float m12,
      final float m13, final float m20, final float m21, final float m22,
      final float m23, final float m30, final float m31, final float m32,
      final float m33 ) {

      /*
       * The parent method is to reset, then apply. This is backwards, reseting
       * and multiplication should depend on setting, not the other way around.
       * The point is to avoid matrix multiplication when possible.
       */

      /* @formatter:off */
      this.camera.set(
         1.0f, 0.0f, 0.0f, 0.0f,
         0.0f, 1.0f, 0.0f, 0.0f,
         0.0f, 0.0f, 1.0f, 0.0f,
         0.0f, 0.0f, 0.0f, 1.0f);
      this.cameraInv.set(
         1.0f, 0.0f, 0.0f, 0.0f,
         0.0f, 1.0f, 0.0f, 0.0f,
         0.0f, 0.0f, 1.0f, 0.0f,
         0.0f, 0.0f, 0.0f, 1.0f);

      this.modelview.set(
         m00, m01, m02, m03,
         m10, m11, m12, m13,
         m20, m21, m22, m23,
         m30, m31, m32, m33);
      PMatAux.inverse(this.modelview, this.modelviewInv);
      /* @formatter:on */

      PMatAux.mul(this.projection, this.modelview, this.projmodelview);

      /* These loose variables are affected by the camera reset. */
      this.cameraX = 0.0f;
      this.cameraY = 0.0f;
      this.cameraZ = 0.0f;
      this.eyeDist = 0.0f;
   }

   /**
    * Sets the renderer matrix to the source.
    *
    * @param source the source matrix
    */
   public void setMatrix ( final Mat3 source ) {

      this.setMatrix(source.m00, source.m01, 0.0f, source.m02, source.m10,
         source.m11, 0.0f, source.m12, 0.0f, 0.0f, 1.0f, 0.0f, source.m20,
         source.m21, 0.0f, source.m22);
   }

   /**
    * Sets the renderer matrix to the source.
    *
    * @param source the source matrix
    */
   public void setMatrix ( final Mat4 source ) {

      this.setMatrix(source.m00, source.m01, source.m02, source.m03, source.m10,
         source.m11, source.m12, source.m13, source.m20, source.m21, source.m22,
         source.m23, source.m30, source.m31, source.m32, source.m33);
   }

   /**
    * Sets the renderer matrix to the source.
    *
    * @param source the source matrix
    */
   @Override
   public void setMatrix ( final PMatrix2D source ) {

      this.setMatrix(source.m00, source.m01, 0.0f, source.m02, source.m10,
         source.m11, 0.0f, source.m12, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f,
         1.0f);
   }

   /**
    * Sets the renderer matrix to the source.
    *
    * @param source the source matrix
    */
   @Override
   public void setMatrix ( final PMatrix3D source ) {

      this.setMatrix(source.m00, source.m01, source.m02, source.m03, source.m10,
         source.m11, source.m12, source.m13, source.m20, source.m21, source.m22,
         source.m23, source.m30, source.m31, source.m32, source.m33);
   }

   /**
    * Sets the current normal mode for vertex calls. Corresponds to
    * {@link PGraphics#NORMAL_MODE_AUTO}, {@link PGraphics#NORMAL_MODE_SHAPE}
    * or {@link PGraphics#NORMAL_MODE_VERTEX}.
    *
    * @param mode the mode
    */
   public void setNormalMode ( final NormalMode mode ) {

      this.normalMode = mode.getVal();
   }

   /**
    * Sets the renderer's current projection.
    *
    * @param m the matrix
    */
   public void setProjection ( final Mat4 m ) {

      this.flush();
      this.projection.set(m.m00, m.m01, m.m02, m.m03, m.m10, m.m11, m.m12,
         m.m13, m.m20, m.m21, m.m22, m.m23, m.m30, m.m31, m.m32, m.m33);
      PMatAux.mul(this.projection, this.modelview, this.projmodelview);
   }

   /**
    * Set size is the last function called by size, createGraphics,
    * makeGraphics, etc. when initializing the graphics renderer. Therefore,
    * any additional values that need initialization can be attempted here.
    *
    * @param width  the applet width
    * @param height the applet height
    */
   @Override
   public void setSize ( final int width, final int height ) {

      final float hf = height;

      this.width = width;
      this.height = height;
      this.updatePixelSize();

      this.texture = null;
      this.ptexture = null;

      this.defCameraFOV = IUp.DEFAULT_FOV;
      this.defCameraX = 0.0f;
      this.defCameraY = 0.0f;
      this.defCameraZ = hf * Utils.cot(this.defCameraFOV * 0.5f);
      this.defCameraNear = this.defCameraZ * 0.01f;
      this.defCameraFar = this.defCameraZ * 10.0f;
      this.defCameraAspect = this.width / hf;

      this.cameraFOV = this.defCameraFOV;
      this.cameraX = this.defCameraX;
      this.cameraY = this.defCameraY;
      this.cameraZ = this.defCameraZ;
      this.cameraNear = this.defCameraNear;
      this.cameraFar = this.defCameraFar;
      this.cameraAspect = this.defCameraAspect;

      this.sized = true;
   }

   /**
    * Displays a PShape.
    *
    * @param psh the PShape
    */
   @Override
   public void shape ( final PShape psh ) {

      if ( psh.isVisible() ) {
         this.flush();
         psh.draw(this);
      }
   }

   /**
    * Displays a PShape. Ignores the coordinates supplied to the function.
    *
    * @param psh the PShape
    * @param x   the x coordinate
    * @param y   the y coordinate
    */
   @Override
   public void shape ( final PShape psh, final float x, final float y ) {

      this.shape(psh);
   }

   /**
    * Displays a PShape. Ignores the coordinates supplied to the function.
    *
    * @param psh the PShape
    * @param x   the x coordinate
    * @param y   the y coordinate
    * @param z   the z coordinate
    */
   @Override
   public void shape ( final PShape psh, final float x, final float y,
      final float z ) {

      this.shape(psh);
   }

   /**
    * Displays a PShape. Ignores the coordinates supplied to the function.
    *
    * @param psh the PShape
    * @param x1  the first x coordinate
    * @param y1  the first y coordinate
    * @param x2  the second x coordinate
    * @param y2  the second y coordinate
    */
   @Override
   public void shape ( final PShape psh, final float x1, final float y1,
      final float x2, final float y2 ) {

      this.shape(psh);
   }

   /**
    * shapeMode is not supported by this renderer; it defaults to CENTER. Set
    * the scale of the shape with instance methods instead.<br>
    * <br>
    * This will not throw a missing method warning, because it may be called
    * by PShapes.
    */
   @Override
   public void shapeMode ( final int mode ) { /* Unsupported. */ }

   /**
    * Applies a shear transform to the renderer.
    *
    * @param v the shear
    */
   public void shear ( final Vec2 v ) {

      this.applyMatrixImpl(1.0f, v.x, 0.0f, 0.0f, v.y, 1.0f, 0.0f, 0.0f, 0.0f,
         0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
   }

   /**
    * Shears a shape around the x axis by the angle in radians.
    *
    * @param radians the angle in radians
    */
   @Override
   public void shearX ( final float radians ) {

      this.applyMatrixImpl(1.0f, Utils.tan(radians), 0.0f, 0.0f, 0.0f, 1.0f,
         0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
   }

   /**
    * Shears a shape around the y axis by the angle in radians.
    *
    * @param radians the angle in radians
    */
   @Override
   public void shearY ( final float radians ) {

      this.applyMatrixImpl(1.0f, 0.0f, 0.0f, 0.0f, Utils.tan(radians), 1.0f,
         0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
   }

   /**
    * Unsupported by this renderer. Use a MeshEntity and Mesh instead.
    *
    * @param r the radius
    */
   @Override
   public void sphere ( final float r ) { PApplet.showMethodWarning("sphere"); }

   /**
    * Unsupported by this renderer. Use a MeshEntity and Mesh instead.
    *
    * @param longitudes the longitudes
    * @param latitudes  the latitudes
    */
   @Override
   public void sphereDetail ( final int longitudes, final int latitudes ) {

      PApplet.showMethodWarning("sphereDetail");
   }

   /**
    * Sets the renderer's current stroke to the color. Does not check whether
    * the color should be pre-multiplied by alpha; the user is trusted to do
    * so manually if desired.
    *
    * @param c the color
    */
   @Override
   public void stroke ( final Color c ) {

      /*
       * strokeFromCalc is not overridden in the OpenGL, so it calls the method
       * from PGraphics, which assigns calculated color variables to stroke
       * color variables.
       */

      this.colorCalc(c);
      this.strokeFromCalc();
   }

   /**
    * Displays a character at a location.
    *
    * @param character the character
    * @param x         the x coordinate
    * @param y         the y coordinate
    */
   @Override
   public void text ( final char character, final float x, final float y ) {

      float yMut = y;

      if ( this.textFont == null ) { this.defaultFontOrDeath("text"); }

      switch ( this.textAlignY ) {

         case PConstants.TOP: /* 101 */
            yMut -= this.textAscent();
            break;

         case PConstants.BOTTOM: /* 102 */
            yMut += this.textDescent();
            break;

         case PConstants.CENTER: /* 3 */
         default:
            yMut -= this.textAscent() * 0.5f;
      }

      this.textBuffer[0] = character;
      this.textLineAlignImpl(this.textBuffer, 0, 1, x, yMut);
   }

   /**
    * Displays an array of characters as text at a location.
    *
    * @param chars the character array
    * @param start the start index, inclusive
    * @param stop  the stop index, exclusive
    * @param x     the x coordinate
    * @param y     the y coordinate
    */
   @Override
   public void text ( final char[] chars, final int start, final int stop,
      final float x, final float y ) {

      /* With REPEAT, significant artifacts appear at the edge of images. */
      final int oldWrapMode = this.textureWrap;
      this.textureWrap(PConstants.CLAMP);

      float yMut = y;
      int stMut = start;
      float high = 0;
      for ( int i = stMut; i < stop; ++i ) {
         if ( chars[i] == '\n' ) { high += this.textLeading; }
      }

      switch ( this.textAlignY ) {

         case PConstants.TOP: /* 101 */

            yMut -= this.textAscent();

            break;

         case PConstants.BOTTOM: /* 102 */

            yMut += this.textDescent() + high;

            break;

         case PConstants.CENTER: /* 3 */

         default:

            yMut -= ( this.textAscent() - high ) * 0.5f;

      }

      int index = 0;
      while ( index < stop ) {
         if ( chars[index] == '\n' ) {
            this.textLineAlignImpl(chars, stMut, index, x, yMut);
            stMut = index + 1;
            yMut -= this.textLeading;
         }
         ++index;
      }

      if ( stMut < stop ) {
         this.textLineAlignImpl(chars, stMut, index, x, yMut);
      }

      this.textureWrap(oldWrapMode);
   }

   /**
    * Sets the text mode to either shape or model.
    *
    * @param mode the text mode
    */
   @Override
   public void textMode ( final int mode ) {

      switch ( mode ) {
         case PConstants.MODEL: /* 4 */
         case PConstants.SHAPE: /* 5 */
            this.textMode = mode;
            break;

         default:
            this.textMode = PConstants.MODEL;
      }
   }

   /**
    * Assigns the PGraphicsOpenGL buffer as the current texture.
    *
    * @param buff the buffer
    */
   public void texture ( final PGraphicsOpenGL buff ) {

      if ( buff.pgl.threadIsCurrent() ) { super.texture(buff); }
   }

   /**
    * Texture mode is not supported by this renderer.
    *
    * @param mode the mode
    */
   @Override
   public void textureMode ( final int mode ) {

      PApplet.showMethodWarning("textureMode");
   }

   /**
    * Gets the renderer's texture sampling as an enumeration constant.
    */
   @Override
   public TextureSampling textureSampling ( ) {

      return TextureSampling.fromValue(this.textureSampling);
   }

   /**
    * Sets the renderer's texture sampling from an enumeration constant.
    *
    * @param sampleType the sample type
    */
   @Override
   public void textureSampling ( final TextureSampling sampleType ) {

      this.textureSampling = sampleType.getVal();
   }

   /**
    * Sets the renderer's current stroke to the tint.
    *
    * @param c the color
    */
   public void tint ( final Color c ) {

      /*
       * tintFromCalc is not overridden in the OpenGL, so it calls the method
       * from PGraphics, which assigns calculated color variables to tint color
       * variables.
       */

      this.colorCalc(c);
      this.tintFromCalc();
   }

   /**
    * Returns the string representation of this renderer.
    *
    * @return the string
    */
   @Override
   public String toString ( ) { return UpOgl.PATH_STR; }

   /**
    * Applies a transform to the renderer's matrix.
    *
    * @param tr2 the transform
    */
   public void transform ( final Transform2 tr2 ) {

      this.transform(tr2, IUp.DEFAULT_ORDER);
   }

   /**
    * Applies a transform to the renderer's matrix.
    *
    * @param tr2   the transform
    * @param order the transform order
    */
   public void transform ( final Transform2 tr2, final TransformOrder order ) {

      final Vec2 dim = tr2.getScale(new Vec2());
      final Vec2 loc = tr2.getLocation(new Vec2());
      final float angle = tr2.getRotation();

      switch ( order ) {

         case RST:

            this.rotateZ(angle);
            this.scaleImpl(dim.x, dim.y, 1.0f);
            this.translateImpl(loc.x, loc.y, 0.0f);
            return;

         case RTS:

            this.rotateZ(angle);
            this.translateImpl(loc.x, loc.y, 0.0f);
            this.scaleImpl(dim.x, dim.y, 1.0f);
            return;

         case SRT:

            this.scaleImpl(dim.x, dim.y, 1.0f);
            this.rotateZ(angle);
            this.translateImpl(loc.x, loc.y, 0.0f);
            return;

         case STR:

            this.scaleImpl(dim.x, dim.y, 1.0f);
            this.translateImpl(loc.x, loc.y, 0.0f);
            this.rotateZ(angle);
            return;

         case TSR:

            this.translateImpl(loc.x, loc.y, 0.0f);
            this.scaleImpl(dim.x, dim.y, 1.0f);
            this.rotateZ(angle);
            return;

         case R:

            this.rotateZ(angle);
            return;

         case RS:

            this.rotateZ(angle);
            this.scaleImpl(dim.x, dim.y, 1.0f);
            return;

         case RT:

            this.rotateZ(angle);
            this.translateImpl(loc.x, loc.y, 0.0f);
            return;

         case S:

            this.scaleImpl(dim.x, dim.y, 1.0f);
            return;

         case SR:

            this.scaleImpl(dim.x, dim.y, 1.0f);
            this.rotateZ(angle);
            return;

         case ST:

            this.scaleImpl(dim.x, dim.y, 1.0f);
            this.translateImpl(loc.x, loc.y, 0.0f);
            return;

         case T:

            this.translateImpl(loc.x, loc.y, 0.0f);
            return;

         case TR:

            this.translateImpl(loc.x, loc.y, 0.0f);
            this.rotateZ(angle);
            return;

         case TS:

            this.translateImpl(loc.x, loc.y, 0.0f);
            this.scaleImpl(dim.x, dim.y, 1.0f);
            return;

         case TRS:
         default:

            this.translateImpl(loc.x, loc.y, 0.0f);
            this.rotateZ(angle);
            this.scaleImpl(dim.x, dim.y, 1.0f);
      }
   }

   /**
    * Exposes the OpenGL model view array to more direct access.
    *
    * @param m00 row 0, column 0
    * @param m01 row 0, column 1
    * @param m02 row 0, column 2
    * @param m03 row 0, column 3
    * @param m10 row 1, column 0
    * @param m11 row 1, column 1
    * @param m12 row 1, column 2
    * @param m13 row 1, column 3
    * @param m20 row 2, column 0
    * @param m21 row 2, column 1
    * @param m22 row 2, column 2
    * @param m23 row 2, column 3
    * @param m30 row 3, column 0
    * @param m31 row 3, column 1
    * @param m32 row 3, column 2
    * @param m33 row 3, column 3
    */
   public void updateGLModelview ( final float m00, final float m01,
      final float m02, final float m03, final float m10, final float m11,
      final float m12, final float m13, final float m20, final float m21,
      final float m22, final float m23, final float m30, final float m31,
      final float m32, final float m33 ) {

      if ( this.glModelview == null ) { this.glModelview = new float[16]; }

      this.glModelview[0] = m00;
      this.glModelview[1] = m10;
      this.glModelview[2] = m20;
      this.glModelview[3] = m30;

      this.glModelview[4] = m01;
      this.glModelview[5] = m11;
      this.glModelview[6] = m21;
      this.glModelview[7] = m31;

      this.glModelview[8] = m02;
      this.glModelview[9] = m12;
      this.glModelview[10] = m22;
      this.glModelview[11] = m32;

      this.glModelview[12] = m03;
      this.glModelview[13] = m13;
      this.glModelview[14] = m23;
      this.glModelview[15] = m33;
   }

   /**
    * Exposes the OpenGL model view array to more direct access.
    *
    * @param m the matrix
    */
   public void updateGLModelview ( final Mat4 m ) {

      this.updateGLModelview(m.m00, m.m01, m.m02, m.m03, m.m10, m.m11, m.m12,
         m.m13, m.m20, m.m21, m.m22, m.m23, m.m30, m.m31, m.m32, m.m33);
   }

   /**
    * Exposes the OpenGL model view array to more direct access.
    *
    * @param m the Processing matrix
    */
   public void updateGLModelview ( final PMatrix3D m ) {

      this.updateGLModelview(m.m00, m.m01, m.m02, m.m03, m.m10, m.m11, m.m12,
         m.m13, m.m20, m.m21, m.m22, m.m23, m.m30, m.m31, m.m32, m.m33);
   }

   /**
    * Exposes the OpenGL normal array to more direct access.
    *
    * @param m00 row 0, column 0
    * @param m01 row 0, column 1
    * @param m02 row 0, column 2
    * @param m10 row 1, column 0
    * @param m11 row 1, column 1
    * @param m12 row 1, column 2
    * @param m20 row 2, column 0
    * @param m21 row 2, column 1
    * @param m22 row 2, column 2
    */
   public void updateGLNormal ( final float m00, final float m01,
      final float m02, final float m10, final float m11, final float m12,
      final float m20, final float m21, final float m22 ) {

      if ( this.glNormal == null ) { this.glNormal = new float[9]; }

      this.glNormal[0] = m00;
      this.glNormal[1] = m01;
      this.glNormal[2] = m02;

      this.glNormal[3] = m10;
      this.glNormal[4] = m11;
      this.glNormal[5] = m12;

      this.glNormal[6] = m20;
      this.glNormal[7] = m21;
      this.glNormal[8] = m22;
   }

   /**
    * Exposes the OpenGL normal array to more direct access.
    *
    * @param m the matrix
    */
   public void updateGLNormal ( final Mat4 m ) {

      this.updateGLNormal(m.m00, m.m01, m.m02, m.m10, m.m11, m.m12, m.m20,
         m.m21, m.m22);
   }

   /**
    * Exposes the OpenGL normal array to more direct access.
    *
    * @param m the Processing matrix
    */
   public void updateGLNormal ( final PMatrix3D m ) {

      this.updateGLNormal(m.m00, m.m01, m.m02, m.m10, m.m11, m.m12, m.m20,
         m.m21, m.m22);
   }

   /**
    * Exposes the OpenGL projection array to more direct access.
    *
    * @param m00 row 0, column 0
    * @param m01 row 0, column 1
    * @param m02 row 0, column 2
    * @param m03 row 0, column 3
    * @param m10 row 1, column 0
    * @param m11 row 1, column 1
    * @param m12 row 1, column 2
    * @param m13 row 1, column 3
    * @param m20 row 2, column 0
    * @param m21 row 2, column 1
    * @param m22 row 2, column 2
    * @param m23 row 2, column 3
    * @param m30 row 3, column 0
    * @param m31 row 3, column 1
    * @param m32 row 3, column 2
    * @param m33 row 3, column 3
    */
   public void updateGLProjection ( final float m00, final float m01,
      final float m02, final float m03, final float m10, final float m11,
      final float m12, final float m13, final float m20, final float m21,
      final float m22, final float m23, final float m30, final float m31,
      final float m32, final float m33 ) {

      if ( this.glProjection == null ) { this.glProjection = new float[16]; }

      this.glProjection[0] = m00;
      this.glProjection[1] = m10;
      this.glProjection[2] = m20;
      this.glProjection[3] = m30;

      this.glProjection[4] = m01;
      this.glProjection[5] = m11;
      this.glProjection[6] = m21;
      this.glProjection[7] = m31;

      this.glProjection[8] = m02;
      this.glProjection[9] = m12;
      this.glProjection[10] = m22;
      this.glProjection[11] = m32;

      this.glProjection[12] = m03;
      this.glProjection[13] = m13;
      this.glProjection[14] = m23;
      this.glProjection[15] = m33;
   }

   /**
    * Exposes the OpenGL projection array to more direct access.
    *
    * @param m the matrix
    */
   public void updateGLProjection ( final Mat4 m ) {

      this.updateGLProjection(m.m00, m.m01, m.m02, m.m03, m.m10, m.m11, m.m12,
         m.m13, m.m20, m.m21, m.m22, m.m23, m.m30, m.m31, m.m32, m.m33);
   }

   /**
    * Exposes the OpenGL projection array to more direct access.
    *
    * @param m the Processing matrix
    */
   public void updateGLProjection ( final PMatrix3D m ) {

      this.updateGLProjection(m.m00, m.m01, m.m02, m.m03, m.m10, m.m11, m.m12,
         m.m13, m.m20, m.m21, m.m22, m.m23, m.m30, m.m31, m.m32, m.m33);
   }

   /**
    * Exposes the OpenGL project model view array to more direct access.
    *
    * @param m00 row 0, column 0
    * @param m01 row 0, column 1
    * @param m02 row 0, column 2
    * @param m03 row 0, column 3
    * @param m10 row 1, column 0
    * @param m11 row 1, column 1
    * @param m12 row 1, column 2
    * @param m13 row 1, column 3
    * @param m20 row 2, column 0
    * @param m21 row 2, column 1
    * @param m22 row 2, column 2
    * @param m23 row 2, column 3
    * @param m30 row 3, column 0
    * @param m31 row 3, column 1
    * @param m32 row 3, column 2
    * @param m33 row 3, column 3
    */
   public void updateGLProjmodelview ( final float m00, final float m01,
      final float m02, final float m03, final float m10, final float m11,
      final float m12, final float m13, final float m20, final float m21,
      final float m22, final float m23, final float m30, final float m31,
      final float m32, final float m33 ) {

      if ( this.glProjmodelview == null ) {
         this.glProjmodelview = new float[16];
      }

      this.glProjmodelview[0] = m00;
      this.glProjmodelview[1] = m10;
      this.glProjmodelview[2] = m20;
      this.glProjmodelview[3] = m30;

      this.glProjmodelview[4] = m01;
      this.glProjmodelview[5] = m11;
      this.glProjmodelview[6] = m21;
      this.glProjmodelview[7] = m31;

      this.glProjmodelview[8] = m02;
      this.glProjmodelview[9] = m12;
      this.glProjmodelview[10] = m22;
      this.glProjmodelview[11] = m32;

      this.glProjmodelview[12] = m03;
      this.glProjmodelview[13] = m13;
      this.glProjmodelview[14] = m23;
      this.glProjmodelview[15] = m33;
   }

   /**
    * Exposes the OpenGL project model view array to more direct access.
    *
    * @param m the matrix
    */
   public void updateGLProjmodelview ( final Mat4 m ) {

      this.updateGLProjmodelview(m.m00, m.m01, m.m02, m.m03, m.m10, m.m11,
         m.m12, m.m13, m.m20, m.m21, m.m22, m.m23, m.m30, m.m31, m.m32, m.m33);
   }

   /**
    * Exposes the OpenGL project model view array to more direct access.
    *
    * @param m the Processing matrix
    */
   public void updateGLProjmodelview ( final PMatrix3D m ) {

      this.updateGLProjmodelview(m.m00, m.m01, m.m02, m.m03, m.m10, m.m11,
         m.m12, m.m13, m.m20, m.m21, m.m22, m.m23, m.m30, m.m31, m.m32, m.m33);
   }

   /**
    * Update the pixels[] buffer to the PGraphics image. The overridden
    * functionality eliminates unnecessary checks.
    */
   @Override
   public void updatePixels ( ) {

      if ( !this.modified ) {

         this.mx1 = 0;
         this.mx2 = this.pixelWidth;
         this.my1 = 0;
         this.my2 = this.pixelHeight;
         this.modified = true;

      } else {

         if ( 0 < this.mx1 ) { this.mx1 = 0; }
         if ( 0 > this.mx2 ) {
            this.mx2 = this.pixelWidth <= 0 ? this.pixelWidth : 0;
         }

         if ( 0 < this.my1 ) { this.my1 = 0; }
         if ( 0 > this.my2 ) {
            this.my2 = this.pixelHeight <= 0 ? this.pixelHeight : 0;
         }

         if ( this.pixelWidth < this.mx1 ) {
            this.mx1 = 0 >= this.pixelWidth ? 0 : this.pixelWidth;
         }
         if ( this.pixelWidth > this.mx2 ) { this.mx2 = this.pixelWidth; }

         if ( this.pixelHeight < this.my1 ) {
            this.my1 = 0 >= this.pixelHeight ? 0 : this.pixelHeight;
         }
         if ( this.pixelHeight > this.my2 ) { this.my2 = this.pixelHeight; }
      }
   }

   /**
    * Updates the project model view by multiplying the projection and model
    * view.
    */
   @Override
   public void updateProjmodelview ( ) {

      PMatAux.mul(this.projection, this.modelview, this.projmodelview);
   }

   /**
    * Draws a vertex.
    *
    * @param x the x coordinate
    * @param y the y coordinate
    */
   @Override
   public void vertex ( final float x, final float y ) {

      this.vertexImpl(x, y, 0.0f, this.textureU, this.textureV);
   }

   /**
    * Draws a vertex.
    *
    * @param x the x coordinate
    * @param y the y coordinate
    * @param z the z coordinate
    */
   @Override
   public void vertex ( final float x, final float y, final float z ) {

      this.vertexImpl(x, y, z, this.textureU, this.textureV);
   }

   /**
    * Draws a vertex.
    *
    * @param x the x coordinate
    * @param y the y coordinate
    * @param u the u texture coordinate
    * @param v the v texture coordinate
    */
   @Override
   public void vertex ( final float x, final float y, final float u,
      final float v ) {

      this.vertexTexture(u, v, this.textureMode, this.textureWrap);
      this.vertexImpl(x, y, 0.0f, u, v);
   }

   /**
    * Draws a vertex.
    *
    * @param x the x coordinate
    * @param y the y coordinate
    * @param z the z coordinate
    * @param u the u texture coordinate
    * @param v the v texture coordinate
    */
   @Override
   public void vertex ( final float x, final float y, final float z,
      final float u, final float v ) {

      this.vertexTexture(u, v, this.textureMode, this.textureWrap);
      this.vertexImpl(x, y, z, this.textureU, this.textureV);
   }

   /**
    * Updates the texture coordinate.
    *
    * @param vt the texture coordinate
    */
   public void vertexTexture ( final Vec2 vt ) {

      this.vertexTexture(vt.x, vt.y);
   }

   /**
    * Displays a PImage from its center. The last four numbers specify the
    * image texture coordinates (or UVs).
    *
    * @param img      the PImage
    * @param xCenter  the x center
    * @param yCenter  the y center
    * @param wDisplay the display width
    * @param hDisplay the display height
    * @param z        the z coordinate
    * @param u1       the image top-left corner u
    * @param v1       the image top-left corner v
    * @param u2       the image bottom-right corner u
    * @param v2       the image bottom-right corner v
    */
   void imageCenter ( final PImage img, final float xCenter,
      final float yCenter, final float wDisplay, final float hDisplay,
      final float z, final float u1, final float v1, final float u2,
      final float v2 ) {

      if ( img.width < 1 || img.height < 1 ) { return; }

      final float wHalf = wDisplay * 0.5f;
      final float hHalf = hDisplay * 0.5f;
      final float left = xCenter - wHalf;
      final float top = yCenter + hHalf;
      final float right = xCenter + wHalf;
      final float bottom = yCenter - hHalf;

      this.pushStyle();
      this.noStroke();

      final int oldWrapMode = this.textureWrap;
      this.textureWrap(PConstants.CLAMP);
      this.beginShape(PConstants.POLYGON);
      this.normalPerShape(0.0f, 0.0f, 1.0f);
      this.texture(img);
      this.vertexImpl(left, top, z, u1, v1);
      this.vertexImpl(right, top, z, u2, v1);
      this.vertexImpl(right, bottom, z, u2, v2);
      this.vertexImpl(left, bottom, z, u1, v2);
      this.endShape(PConstants.CLOSE);
      this.textureWrap(oldWrapMode);
      this.popStyle();
   }

   /**
    * Displays a PImage from top left corner. The last four numbers specify
    * the image texture coordinates (or UVs).
    *
    * @param img      the PImage
    * @param xtl      the top left corner x
    * @param ytl      the top left corner y
    * @param wDisplay the display width
    * @param hDisplay the display height
    * @param z        the z coordinate
    * @param u1       the image top-left corner u
    * @param v1       the image top-left corner v
    * @param u2       the image bottom-right corner u
    * @param v2       the image bottom-right corner v
    */
   void imageCorner ( final PImage img, final float xtl, final float ytl,
      final float wDisplay, final float hDisplay, final float z, final float u1,
      final float v1, final float u2, final float v2 ) {

      if ( img.width < 1 || img.height < 1 ) { return; }

      final float left = xtl;
      final float top = ytl;
      final float right = xtl + wDisplay;
      final float bottom = ytl - hDisplay;

      this.pushStyle();
      this.noStroke();

      final int oldWrapMode = this.textureWrap;
      this.textureWrap(PConstants.CLAMP);
      this.beginShape(PConstants.POLYGON);
      this.normalPerShape(0.0f, 0.0f, 1.0f);
      this.texture(img);
      this.vertexImpl(left, top, z, u1, v1);
      this.vertexImpl(right, top, z, u2, v1);
      this.vertexImpl(right, bottom, z, u2, v2);
      this.vertexImpl(left, bottom, z, u1, v2);
      this.endShape(PConstants.CLOSE);
      this.textureWrap(oldWrapMode);
      this.popStyle();
   }

   /**
    * Displays a PImage according to four corners.The last four numbers
    * specify the image texture coordinates (or UVs).
    *
    * @param img the PImage
    * @param x1  the first x coordinate
    * @param y1  the first y coordinate
    * @param x2  the second x coordinate
    * @param y2  the second y coordinate
    * @param z   the z coordinate
    * @param u1  the image top-left corner u
    * @param v1  the image top-left corner v
    * @param u2  the image bottom-right corner u
    * @param v2  the image bottom-right corner v
    */
   void imageCorners ( final PImage img, final float x1, final float y1,
      final float x2, final float y2, final float z, final float u1,
      final float v1, final float u2, final float v2 ) {

      if ( img.width < 1 || img.height < 1 ) { return; }

      this.pushStyle();
      this.noStroke();
      final int oldWrapMode = this.textureWrap;
      this.textureWrap(PConstants.CLAMP);
      this.beginShape(PConstants.POLYGON);
      this.normalPerShape(0.0f, 0.0f, 1.0f);
      this.texture(img);
      this.vertexImpl(x1, y1, z, u1, v1);
      this.vertexImpl(x2, y1, z, u2, v1);
      this.vertexImpl(x2, y2, z, u2, v2);
      this.vertexImpl(x1, y2, z, u1, v2);
      this.endShape(PConstants.CLOSE);
      this.textureWrap(oldWrapMode);
      this.popStyle();
   }

   /**
    * Displays a PImage from its center. The width and height are treated as
    * radii. The last four numbers specify the image texture coordinates (or
    * UVs).
    *
    * @param img      the PImage
    * @param xCenter  the x center
    * @param yCenter  the y center
    * @param wDisplay the display width
    * @param hDisplay the display height
    * @param z        the z coordinate
    * @param u1       the image top-left corner u
    * @param v1       the image top-left corner v
    * @param u2       the image bottom-right corner u
    * @param v2       the image bottom-right corner v
    */
   void imageRadius ( final PImage img, final float xCenter,
      final float yCenter, final float wDisplay, final float hDisplay,
      final float z, final float u1, final float v1, final float u2,
      final float v2 ) {

      if ( img.width < 1 || img.height < 1 ) { return; }

      final float left = xCenter - wDisplay;
      final float top = yCenter + hDisplay;
      final float right = xCenter + wDisplay;
      final float bottom = yCenter - hDisplay;

      this.pushStyle();
      this.noStroke();
      final int oldWrapMode = this.textureWrap;
      this.textureWrap(PConstants.CLAMP);
      this.beginShape(PConstants.POLYGON);
      this.normalPerShape(0.0f, 0.0f, 1.0f);
      this.texture(img);
      this.vertexImpl(left, top, z, u1, v1);
      this.vertexImpl(right, top, z, u2, v1);
      this.vertexImpl(right, bottom, z, u2, v2);
      this.vertexImpl(left, bottom, z, u1, v2);
      this.endShape(PConstants.CLOSE);
      this.textureWrap(oldWrapMode);
      this.popStyle();
   }

   /**
    * Applies the matrix to the renderer.
    *
    * @param n00 row 0, column 0
    * @param n01 row 0, column 1
    * @param n02 row 0, column 2
    * @param n03 row 0, column 3
    * @param n10 row 1, column 0
    * @param n11 row 1, column 1
    * @param n12 row 1, column 2
    * @param n13 row 1, column 3
    * @param n20 row 2, column 0
    * @param n21 row 2, column 1
    * @param n22 row 2, column 2
    * @param n23 row 2, column 3
    * @param n30 row 3, column 0
    * @param n31 row 3, column 1
    * @param n32 row 3, column 2
    * @param n33 row 3, column 3
    */
   @Override
   protected void applyMatrixImpl ( final float n00, final float n01,
      final float n02, final float n03, final float n10, final float n11,
      final float n12, final float n13, final float n20, final float n21,
      final float n22, final float n23, final float n30, final float n31,
      final float n32, final float n33 ) {

      this.modelview.apply(n00, n01, n02, n03, n10, n11, n12, n13, n20, n21,
         n22, n23, n30, n31, n32, n33);
      PMatAux.inverse(this.modelview, this.modelviewInv);
      this.projmodelview.apply(n00, n01, n02, n03, n10, n11, n12, n13, n20, n21,
         n22, n23, n30, n31, n32, n33);
   }

   /**
    * Sets the renderer blend mode.
    */
   @Override
   protected void blendModeImpl ( ) {

      /* See https://github.com/processing/processing/issues/3391 . */

      if ( this.blendMode != this.lastBlendMode ) { this.flush(); }

      this.pgl.enable(PGL.BLEND);

      switch ( this.blendMode ) {
         case PConstants.REPLACE: /* 0 */

            if ( PGraphicsOpenGL.blendEqSupported ) {
               this.pgl.blendEquationSeparate(PGL.FUNC_ADD, PGL.FUNC_ADD);
            }
            this.pgl.blendFuncSeparate(PGL.ONE, PGL.ZERO, PGL.ONE, PGL.ZERO);

            break;

         case PConstants.BLEND: /* 1 */

            if ( PGraphicsOpenGL.blendEqSupported ) {
               this.pgl.blendEquationSeparate(PGL.FUNC_ADD, PGL.FUNC_ADD);
            }
            this.pgl.blendFuncSeparate(PGL.ONE, PGL.ONE_MINUS_SRC_ALPHA,
               PGL.ONE, PGL.ONE_MINUS_SRC_ALPHA);

            break;

         case PConstants.ADD: /* 2 */

            if ( PGraphicsOpenGL.blendEqSupported ) {
               this.pgl.blendEquationSeparate(PGL.FUNC_ADD, PGL.FUNC_ADD);
            }

            // this.pgl.blendFuncSeparate(PGL.SRC_ALPHA, PGL.ONE, PGL.ONE,
            // PGL.ONE);
            this.pgl.blendFuncSeparate(PGL.ONE, PGL.ONE, PGL.ONE, PGL.ONE);

            break;

         case PConstants.SUBTRACT: /* 4 */

            if ( PGraphicsOpenGL.blendEqSupported ) {
               this.pgl.blendEquationSeparate(PGL.FUNC_REVERSE_SUBTRACT,
                  PGL.FUNC_ADD);
            }

            // this.pgl.blendFuncSeparate(PGL.SRC_ALPHA, PGL.ONE, PGL.ONE,
            // PGL.ONE);
            this.pgl.blendFuncSeparate(PGL.ONE, PGL.ONE, PGL.ONE, PGL.ONE);

            break;

         case PConstants.LIGHTEST: /* 8 */

            if ( PGraphicsOpenGL.blendEqSupported ) {
               this.pgl.blendEquationSeparate(PGL.FUNC_MAX, PGL.FUNC_ADD);
               this.pgl.blendFuncSeparate(PGL.ONE, PGL.ONE, PGL.ONE, PGL.ONE);
            }

            break;

         case PConstants.DARKEST: /* 16 */

            if ( PGraphicsOpenGL.blendEqSupported ) {
               this.pgl.blendEquationSeparate(PGL.FUNC_MIN, PGL.FUNC_ADD);
               this.pgl.blendFuncSeparate(PGL.ONE, PGL.ONE, PGL.ONE, PGL.ONE);
            }

            break;

         case PConstants.EXCLUSION: /* 64 */

            if ( PGraphicsOpenGL.blendEqSupported ) {
               this.pgl.blendEquationSeparate(PGL.FUNC_ADD, PGL.FUNC_ADD);
            }
            this.pgl.blendFuncSeparate(PGL.ONE_MINUS_DST_COLOR,
               PGL.ONE_MINUS_SRC_COLOR, PGL.ONE, PGL.ONE);

            break;

         case PConstants.MULTIPLY: /* 128 */

            if ( PGraphicsOpenGL.blendEqSupported ) {
               this.pgl.blendEquationSeparate(PGL.FUNC_ADD, PGL.FUNC_ADD);
            }

            // this.pgl.blendFuncSeparate(PGL.ZERO, PGL.SRC_COLOR, PGL.ONE,
            // PGL.ONE);
            this.pgl.blendFunc(PGL.DST_COLOR, PGL.ONE_MINUS_SRC_ALPHA);
            break;

         case PConstants.SCREEN: /* 256 */

            if ( PGraphicsOpenGL.blendEqSupported ) {
               this.pgl.blendEquationSeparate(PGL.FUNC_ADD, PGL.FUNC_ADD);
            }
            this.pgl.blendFuncSeparate(PGL.ONE_MINUS_DST_COLOR, PGL.ONE,
               PGL.ONE, PGL.ONE);

            break;

         case IUpOgl.TEXT_BLEND:
         default:

            if ( PGraphicsOpenGL.blendEqSupported ) {
               this.pgl.blendEquationSeparate(PGL.FUNC_ADD, PGL.FUNC_ADD);
            }
            this.pgl.blendFuncSeparate(PGL.SRC_ALPHA, PGL.ONE_MINUS_SRC_ALPHA,
               PGL.ONE, PGL.ONE);
      }

      this.lastBlendMode = this.blendMode;
   }

   /**
    * Overrides the default colorCalc function.
    *
    * @param x the first color channel, hue or red
    * @param y the second color channel, saturation or green
    * @param z the third color channel, brightness or blue
    * @param a the alpha channel
    */
   @Override
   protected void colorCalc ( final float x, final float y, final float z,
      final float a ) {

      this.colorCalc(x, y, z, a, this.usePreMultiply);
   }

   /**
    * Calculates a color from an integer. Does not attempt to detect
    * difference between an 8-bit gray color integer and a 32-bit ARGB
    * hexadecimal, e.g., between <code>fill(128);</code> and
    * <code>fill(0xff808080);</code>.
    *
    * @param argb the hexadecimal color
    */
   @Override
   protected void colorCalc ( final int argb ) {

      /*
       * See https://github.com/processing/processing4/blob/master/core/src/
       * processing/core/PGraphics.java#L7759 .
       */
      this.colorCalc(argb, this.usePreMultiply);
   }

   /**
    * Calculates a color from an integer and alpha value. Useful in the IDE
    * for colors defined with hash-tag literals, such as "#aabbcc" .<br>
    * <br>
    * Does not attempt to detect difference between an 8-bit gray color
    * integer and a 32-bit ARGB hexadecimal, e.g., between
    * <code>fill(128, 255);</code> and <code>fill(#808080, 255);</code>.
    *
    * @param rgb   the hexadecimal color
    * @param alpha the alpha channel
    */
   @Override
   protected void colorCalcARGB ( final int rgb, final float alpha ) {

      this.colorCalc(( int ) ( 0.5f + 0xff * alpha * this.invColorModeA )
         << 0x18 | rgb & 0x00ffffff, this.usePreMultiply);
   }

   /**
    * Initializes the curve basis and draw matrix.
    */
   @Override
   protected void curveInit ( ) {

      if ( this.curveDrawMatrix == null ) {
         this.curveBasisMatrix = new PMatrix3D();
         this.curveDrawMatrix = new PMatrix3D();
         this.curveInited = true;
      }

      PMatAux.catmullBasis(this.curveTightness, this.curveBasisMatrix);
      this.splineForward(this.curveDetail, this.curveDrawMatrix);
      PMatAux.mul(this.bezierBasisInverse, this.curveBasisMatrix,
         this.curveToBezierMatrix);
      PMatAux.mul(this.curveDrawMatrix, this.curveBasisMatrix,
         this.curveDrawMatrix);
   }

   /**
    * Draws a curve. A special case to assist with drawing {@link UpOgl#arc}s.
    *
    * @param curve the curve
    * @param tr    the transform
    * @param fh    the temporary fore handle
    * @param rh    the temporary rear handle
    * @param co    the temporary coordinate
    */
   protected void drawCurve2 ( final Curve2 curve ) {

      Vec2 rh = null;
      Vec2 fh = null;

      final Iterator < Knot2 > itr = curve.iterator();
      final Knot2 firstKnot = itr.next();
      Vec2 co = firstKnot.coord;
      this.beginShape(PConstants.POLYGON);
      this.normalPerShape(0.0f, 0.0f, 1.0f);
      this.vertexImpl(co.x, co.y, 0.0f, this.textureU, this.textureV);

      Knot2 prevKnot = firstKnot;
      while ( itr.hasNext() ) {
         final Knot2 currKnot = itr.next();

         fh = prevKnot.foreHandle;
         rh = currKnot.rearHandle;
         co = currKnot.coord;

         this.bezierVertexImpl(fh.x, fh.y, 0.0f, rh.x, rh.y, 0.0f, co.x, co.y,
            0.0f);

         prevKnot = currKnot;
      }

      if ( curve.closedLoop ) {
         fh = prevKnot.foreHandle;
         rh = firstKnot.rearHandle;
         co = firstKnot.coord;

         this.bezierVertexImpl(fh.x, fh.y, 0.0f, rh.x, rh.y, 0.0f, co.x, co.y,
            0.0f);
         this.endShape(PConstants.CLOSE);
      } else {
         this.endShape(PConstants.OPEN);
      }
   }

   /**
    * Draws a curve as multiplied by a transform. The supplied temporary
    * vectors hold the transformed knot coordinates.
    *
    * @param curve the curve
    * @param tr    the transform
    * @param fh    the temporary fore handle
    * @param rh    the temporary rear handle
    * @param co    the temporary coordinate
    *
    * @see Transform2#mulPoint(Transform2, Vec2, Vec2)
    * @see Transform2#mulCurveSeg(Transform2, Vec2, Vec2, Vec2, Vec2, Vec2,
    *      Vec2)
    */
   protected void drawCurve2 ( final Curve2 curve, final Transform2 tr,
      final Vec2 fh, final Vec2 rh, final Vec2 co ) {

      final Iterator < Knot2 > itr = curve.iterator();

      final Knot2 firstKnot = itr.next();
      Transform2.mulPoint(tr, firstKnot.coord, co);
      this.beginShape(PConstants.POLYGON);
      this.normalPerShape(0.0f, 0.0f, 1.0f);
      this.vertexImpl(co.x, co.y, 0.0f, this.textureU, this.textureV);

      Knot2 prevKnot = firstKnot;
      while ( itr.hasNext() ) {
         final Knot2 currKnot = itr.next();
         Transform2.mulCurveSeg(tr, prevKnot.foreHandle, currKnot.rearHandle,
            currKnot.coord, fh, rh, co);
         this.bezierVertexImpl(fh.x, fh.y, 0.0f, rh.x, rh.y, 0.0f, co.x, co.y,
            0.0f);
         prevKnot = currKnot;
      }

      if ( curve.closedLoop ) {
         Transform2.mulCurveSeg(tr, prevKnot.foreHandle, firstKnot.rearHandle,
            firstKnot.coord, fh, rh, co);
         this.bezierVertexImpl(fh.x, fh.y, 0.0f, rh.x, rh.y, 0.0f, co.x, co.y,
            0.0f);
         this.endShape(PConstants.CLOSE);
      } else {
         this.endShape(PConstants.OPEN);
      }
   }

   /**
    * Draws a curve as multiplied by a transform. The supplied temporary
    * vectors hold the transformed knot coordinates.
    *
    * @param curve the curve
    * @param tr    the transform
    * @param fh    the temporary fore handle
    * @param rh    the temporary rear handle
    * @param co    the temporary coordinate
    *
    * @see Transform3#mulPoint(Transform3, Vec3, Vec3)
    * @see Transform3#mulCurveSeg(Transform3, Vec3, Vec3, Vec3, Vec3, Vec3,
    *      Vec3)
    */
   protected void drawCurve3 ( final Curve3 curve, final Transform3 tr,
      final Vec3 fh, final Vec3 rh, final Vec3 co ) {

      final Iterator < Knot3 > itr = curve.iterator();

      final Knot3 firstKnot = itr.next();
      Transform3.mulPoint(tr, firstKnot.coord, co);
      this.beginShape(PConstants.POLYGON);
      this.vertexImpl(co.x, co.y, co.z, this.textureU, this.textureV);

      Knot3 prevKnot = firstKnot;
      while ( itr.hasNext() ) {
         final Knot3 currKnot = itr.next();
         Transform3.mulCurveSeg(tr, prevKnot.foreHandle, currKnot.rearHandle,
            currKnot.coord, fh, rh, co);
         this.bezierVertexImpl(fh.x, fh.y, fh.z, rh.x, rh.y, rh.z, co.x, co.y,
            co.z);
         prevKnot = currKnot;
      }

      if ( curve.closedLoop ) {
         Transform3.mulCurveSeg(tr, prevKnot.foreHandle, firstKnot.rearHandle,
            firstKnot.coord, fh, rh, co);
         this.bezierVertexImpl(fh.x, fh.y, fh.z, rh.x, rh.y, rh.z, co.x, co.y,
            co.z);
         this.endShape(PConstants.CLOSE);
      } else {
         this.endShape(PConstants.OPEN);
      }
   }

   /**
    * Draws a textured mesh as multiplied by a transform. Supplied temporary
    * vectors hold model and texture coordinates.
    *
    * @param mesh the mesh
    * @param tr   the transform
    * @param mat  the material
    * @param v    a temporary vector
    * @param vt   a temporary vector
    *
    * @see Transform2#mulPoint(Transform2, Vec2, Vec2)
    * @see Transform2#mulTexCoord(Transform2, Vec2, Vec2)
    */
   protected void drawMesh2 ( final Mesh2 mesh, final Transform2 tr,
      final MaterialPImage mat, final Vec2 v, final Vec2 vt ) {

      final PImage pimg = mat.texture;
      final Transform2 uvtr = mat.transform;
      this.tint(mat.tint);

      final Vec2[] vs = mesh.coords;
      final Vec2[] vts = mesh.texCoords;
      final int[][][] fs = mesh.faces;
      final int fsLen = fs.length;

      for ( int i = 0; i < fsLen; ++i ) {

         final int[][] f = fs[i];
         final int fLen = f.length;

         this.beginShape(PConstants.POLYGON);
         this.normalPerShape(0.0f, 0.0f, 1.0f);
         this.texture(pimg);

         for ( int j = 0; j < fLen; ++j ) {
            final int[] data = f[j];
            Transform2.mulPoint(tr, vs[data[0]], v);
            Transform2.mulTexCoord(uvtr, vts[data[1]], vt);
            this.vertexImpl(v.x, v.y, 0.0f, vt.x, vt.y);
         }
         this.endShape(PConstants.CLOSE);
      }
   }

   /**
    * Draws a mesh as multiplied by a transform. The supplied temporary vector
    * holds the transformed coordinate.
    *
    * @param mesh the mesh
    * @param tr   the transform
    * @param v    a temporary vector
    *
    * @see Transform2#mulPoint(Transform2, Vec2, Vec2)
    */
   protected void drawMesh2 ( final Mesh2 mesh, final Transform2 tr,
      final Vec2 v ) {

      final Vec2[] vs = mesh.coords;
      final int[][][] fs = mesh.faces;
      final int fsLen = fs.length;

      for ( int i = 0; i < fsLen; ++i ) {

         final int[][] f = fs[i];
         final int fLen = f.length;

         this.beginShape(PConstants.POLYGON);
         this.normalPerShape(0.0f, 0.0f, 1.0f);

         for ( int j = 0; j < fLen; ++j ) {
            Transform2.mulPoint(tr, vs[f[j][0]], v);
            this.vertexImpl(v.x, v.y, 0.0f, this.textureU, this.textureV);
         }
         this.endShape(PConstants.CLOSE);
      }
   }

   /**
    * Draws a textured mesh as multiplied by a transform. Supplied temporary
    * vectors hold model, texture coordinates and normals.
    *
    * @param mesh the mesh
    * @param tr   the transform
    * @param mat  the material
    * @param v    a temporary vector
    * @param vt   a temporary vector
    * @param vn   a temporary vector
    *
    * @see Transform2#mulTexCoord(Transform2, Vec2, Vec2)
    * @see Transform3#mulPointAndNormal(Transform3, Vec3, Vec3, Vec3, Vec3)
    */
   protected void drawMesh3 ( final Mesh3 mesh, final Transform3 tr,
      final MaterialPImage mat, final Vec3 v, final Vec2 vt, final Vec3 vn ) {

      final PImage pimg = mat.texture;
      final Transform2 uvtr = mat.transform;
      this.tint(mat.tint);

      final Vec3[] vs = mesh.coords;
      final Vec3[] vns = mesh.normals;
      final Vec2[] vts = mesh.texCoords;
      final int[][][] fs = mesh.faces;
      final int fsLen = fs.length;

      for ( int i = 0; i < fsLen; ++i ) {

         final int[][] f = fs[i];
         final int fLen = f.length;

         /* beginShape sets normal mode to NORMAL_MODE_AUTO. */
         this.beginShape(PConstants.POLYGON);
         this.normalMode = PGraphics.NORMAL_MODE_VERTEX;
         this.texture(pimg);

         for ( int j = 0; j < fLen; ++j ) {

            final int[] data = f[j];
            Transform2.mulTexCoord(uvtr, vts[data[1]], vt);
            Transform3.mulPointAndNormal(tr, vs[data[0]], vns[data[2]], v, vn);

            this.normalX = vn.x;
            this.normalY = vn.y;
            this.normalZ = vn.z;
            this.vertexImpl(v.x, v.y, v.z, vt.x, vt.y);
         }
         this.endShape(PConstants.CLOSE);
      }
   }

   /**
    * Draws a mesh as multiplied by a transform. Supplied temporary vectors
    * hold the transformed coordinate and normal.
    *
    * @param mesh the mesh
    * @param tr   the transform
    * @param v    a temporary vector
    * @param vn   a temporary vector
    *
    * @see Transform3#mulPointAndNormal(Transform3, Vec3, Vec3, Vec3, Vec3)
    */
   protected void drawMesh3 ( final Mesh3 mesh, final Transform3 tr,
      final Vec3 v, final Vec3 vn ) {

      final Vec3[] vs = mesh.coords;
      final Vec3[] vns = mesh.normals;
      final int[][][] fs = mesh.faces;
      final int fsLen = fs.length;

      for ( int i = 0; i < fsLen; ++i ) {

         final int[][] f = fs[i];
         final int fLen = f.length;

         /* beginShape sets normal mode to NORMAL_MODE_AUTO. */
         this.beginShape(PConstants.POLYGON);
         this.normalMode = PGraphics.NORMAL_MODE_VERTEX;

         for ( int j = 0; j < fLen; ++j ) {

            final int[] data = f[j];
            Transform3.mulPointAndNormal(tr, vs[data[0]], vns[data[2]], v, vn);

            this.normalX = vn.x;
            this.normalY = vn.y;
            this.normalZ = vn.z;
            this.vertexImpl(v.x, v.y, v.z, this.textureU, this.textureV);
         }
         this.endShape(PConstants.CLOSE);
      }
   }

   /**
    * {@link PGraphicsOpenGL#fillFromCalc} attempts to set the ambient light
    * with this method; this overrides that excess functionality.
    */
   @Override
   protected void fillFromCalc ( ) {

      this.fill = true;
      this.fillR = this.calcR;
      this.fillG = this.calcG;
      this.fillB = this.calcB;
      this.fillA = this.calcA;
      this.fillRi = this.calcRi;
      this.fillGi = this.calcGi;
      this.fillBi = this.calcBi;
      this.fillAi = this.calcAi;
      this.fillColor = this.calcColor;
      this.fillAlpha = this.calcAlpha;
   }

   /**
    * The renderer specific implementation of Processing's image function.
    * This uses integers to specify the UV coordinates for unknown reasons; it
    * defers to a public, single precision real number function.
    *
    * @param img the PImage
    * @param x1  the first x coordinate
    * @param y1  the first y coordinate
    * @param x2  the second x coordinate
    * @param y2  the second y coordinate
    * @param u1  the image top-left corner u
    * @param v1  the image top-left corner v
    * @param u2  the image bottom-right corner u
    * @param v2  the image bottom-right corner v
    */
   @Override
   protected void imageImpl ( final PImage img, final float x1, final float y1,
      final float x2, final float y2, final int u1, final int v1, final int u2,
      final int v2 ) {

      /*
       * This is backwards due to Processing's insistence on specifying UV
       * coordinates with integers (maybe as a result of deriving this
       * function's signature from the AWT renderer's). All image functions
       * should flow into this, but instead this flows into a public image
       * implementation.
       */
      final int savedTextureMode = this.textureMode;
      this.textureMode = PConstants.IMAGE;

      /*
       * This will have to go untested... as this code is being written on a low
       * density monitor.
       */
      this.image(img, x1, y1, x2, y2, 0.0f, u1, v1, u2, v2);
      this.textureMode = savedTextureMode;
   }

   /**
    * An internal helper function to ambientLight. Sets ambient lighting. The
    * color will be submitted to color calculation.
    *
    * @param num the index
    * @param clr the color
    */
   protected void lightAmbient ( final int num, final Color clr ) {

      this.colorCalc(clr);

      final int num3 = num + num + num;
      this.lightAmbient[num3] = this.calcR;
      this.lightAmbient[num3 + 1] = this.calcG;
      this.lightAmbient[num3 + 2] = this.calcB;
   }

   /**
    * An internal helper function to ambientLight. Sets ambient lighting.
    * Color channels will be submitted to color calculation.
    *
    * @param num the index
    * @param x   the first channel
    * @param y   the second channel
    * @param z   the third channel
    */
   @Override
   protected void lightAmbient ( final int num, final float x, final float y,
      final float z ) {

      this.colorCalc(x, y, z, this.colorModeA, false);

      final int num3 = num + num + num;
      this.lightAmbient[num3] = this.calcR;
      this.lightAmbient[num3 + 1] = this.calcG;
      this.lightAmbient[num3 + 2] = this.calcB;
   }

   /**
    * An internal helper function to ambientLight. Sets ambient lighting. The
    * color will be submitted to color calculation.
    *
    * @param num the index
    * @param clr the color
    */
   protected void lightAmbient ( final int num, final int clr ) {

      this.colorCalc(clr, false);

      final int num3 = num + num + num;
      this.lightAmbient[num3] = this.calcR;
      this.lightAmbient[num3 + 1] = this.calcG;
      this.lightAmbient[num3 + 2] = this.calcB;
   }

   /**
    * An internal helper function to diffuseLight. Sets diffuse lighting.
    * Color channels will be submitted to color calculation.
    *
    * @param num the index
    * @param clr the color
    */
   protected void lightDiffuse ( final int num, final Color clr ) {

      this.colorCalc(clr);

      final int num3 = num + num + num;
      this.lightDiffuse[num3] = this.calcR;
      this.lightDiffuse[num3 + 1] = this.calcG;
      this.lightDiffuse[num3 + 2] = this.calcB;
   }

   /**
    * An internal helper function to diffuseLight. Sets diffuse lighting.
    * Color channels will be submitted to color calculation.
    *
    * @param num the index
    * @param x   the first channel
    * @param y   the second channel
    * @param z   the third channel
    */
   @Override
   protected void lightDiffuse ( final int num, final float x, final float y,
      final float z ) {

      this.colorCalc(x, y, z, this.colorModeA, false);

      final int num3 = num + num + num;
      this.lightDiffuse[num3] = this.calcR;
      this.lightDiffuse[num3 + 1] = this.calcG;
      this.lightDiffuse[num3 + 2] = this.calcB;
   }

   /**
    * An internal helper function to diffuseLight. Sets diffuse lighting.
    * Color channels will be submitted to color calculation.
    *
    * @param num the index
    * @param clr the color
    */
   protected void lightDiffuse ( final int num, final int clr ) {

      this.colorCalc(clr, false);

      final int num3 = num + num + num;
      this.lightDiffuse[num3] = this.calcR;
      this.lightDiffuse[num3 + 1] = this.calcG;
      this.lightDiffuse[num3 + 2] = this.calcB;
   }

   /**
    * Sets lighting fall-off.
    *
    * @param num       the index
    * @param constant  the first factor
    * @param linear    the second factor
    * @param quadratic the third factor
    */
   @Override
   protected void lightFalloff ( final int num, final float constant,
      final float linear, final float quadratic ) {

      final int num3 = num + num + num;
      this.lightFalloffCoefficients[num3] = constant;
      this.lightFalloffCoefficients[num3 + 1] = linear;
      this.lightFalloffCoefficients[num3 + 2] = quadratic;
   }

   /**
    * Internal helper function. Sets the light normals array at the given
    * index. Multiplies the normal by the model view.
    *
    * @param num  the index
    * @param xDir the direction x
    * @param yDir the direction y
    * @param zDir the direction z
    */
   @Override
   protected void lightNormal ( final int num, final float xDir,
      final float yDir, final float zDir ) {

      /*
       * Apply normal matrix to the light direction vector, which is the
       * transpose of the inverse of the model view.
       */

      /* @formatter:off */
      final float nx = xDir * this.modelviewInv.m00 +
                       yDir * this.modelviewInv.m10 +
                       zDir * this.modelviewInv.m20;

      final float ny = xDir * this.modelviewInv.m01 +
                       yDir * this.modelviewInv.m11 +
                       zDir * this.modelviewInv.m21;

      final float nz = xDir * this.modelviewInv.m02 +
                       yDir * this.modelviewInv.m12 +
                       zDir * this.modelviewInv.m22;
      /* @formatter:on */

      final float mSq = nx * nx + ny * ny + nz * nz;
      final int num3 = num + num + num;
      if ( mSq > 0.0f ) {
         final float mInv = Utils.invSqrtUnchecked(mSq);
         this.lightNormal[num3] = mInv * nx;
         this.lightNormal[num3 + 1] = mInv * ny;
         this.lightNormal[num3 + 2] = mInv * nz;
      } else {
         this.lightNormal[num3] = 0.0f;
         this.lightNormal[num3 + 1] = 0.0f;
         this.lightNormal[num3 + 2] = 0.0f;
      }
   }

   /**
    * Internal helper function. Sets the light normals array at the given
    * index. Multiplies the normal by the model view.
    *
    * @param num the index
    * @param dir the direction
    */
   protected void lightNormal ( final int num, final Vec3 dir ) {

      this.lightNormal(num, dir.x, dir.y, dir.z);
   }

   /**
    * Internal helper function. Sets positional lighting array at the given
    * index. Multiplies each vector component by the model view.
    *
    * @param num   the index
    * @param x     the x component
    * @param y     the y component
    * @param z     the z component
    * @param isDir treat as a direction or point
    */
   @Override
   protected void lightPosition ( final int num, final float x, final float y,
      final float z, final boolean isDir ) {

      final int num4 = num + num + num + num;

      /* @formatter:off */
      this.lightPosition[num4] = x * this.modelview.m00 +
                                 y * this.modelview.m01 +
                                 z * this.modelview.m02 +
                                     this.modelview.m03;

      this.lightPosition[num4 + 1] = x * this.modelview.m10 +
                                     y * this.modelview.m11 +
                                     z * this.modelview.m12 +
                                         this.modelview.m13;

      this.lightPosition[num4 + 2] = x * this.modelview.m20 +
                                     y * this.modelview.m21 +
                                     z * this.modelview.m22 +
                                         this.modelview.m23;
      /* @formatter:on */

      /*
       * The w component is 0.0 when the vector represents a direction, 1.0 when
       * the vector represents a point.
       */
      this.lightPosition[num4 + 3] = isDir ? 0.0f : 1.0f;
   }

   /**
    * Internal helper function. Sets positional lighting. Sets positional
    * lighting array at the given index. Multiplies each vector component by
    * the model view.
    *
    * @param num   the index
    * @param vec   the vector
    * @param isDir treat as a direction or point
    */
   protected void lightPosition ( final int num, final Vec3 vec,
      final boolean isDir ) {

      this.lightPosition(num, vec.x, vec.y, vec.z, isDir);
   }

   /**
    * Sets specular lighting. Assumes that red, green and blue color channels
    * have already been calculated appropriately.
    *
    * @param num   the index
    * @param red   the red channel
    * @param green the green channel
    * @param blue  the blue channel
    */
   @Override
   protected void lightSpecular ( final int num, final float red,
      final float green, final float blue ) {

      final int num3 = num + num + num;
      this.lightSpecular[num3] = red;
      this.lightSpecular[num3 + 1] = green;
      this.lightSpecular[num3 + 2] = blue;
   }

   /**
    * Sets spot lighting.
    *
    * @param num      the index
    * @param radians  the angle in radians
    * @param exponent the exponent
    */
   @Override
   protected void lightSpot ( final int num, final float radians,
      final float exponent ) {

      final int num2 = num + num;
      this.lightSpotParameters[num2] = Utils.max(0.0f, ( float ) Math.cos(
         radians));
      this.lightSpotParameters[num2 + 1] = exponent;
   }

   /**
    * Internal helper to public functions for 2.5D and 3D. Finds the position
    * of a point in the model view. Does so by
    * <ol>
    * <li>promoting the point to a vector 4, where its w component is 1.0
    * .</li>
    * <li>multiplying the vector 4 by the model view matrix;</li>
    * <li>multiplying the product by the camera inverse;</li>
    * <li>demoting the vector 4 to a point 3 by dividing the x, y and z
    * components by w.</li>
    * </ol>
    * More efficient than calling {@link PApplet#modelX(float, float, float)}
    * , {@link PApplet#modelY(float, float, float)} and
    * {@link PApplet#modelZ(float, float, float)} separately. However, it is
    * advisable to work with {@link Vec4}s and the renderer matrices directly.
    *
    * @param xSource the source x
    * @param ySource the source y
    * @param zSource the source z
    * @param target  the output point
    *
    * @return the model space point
    */
   protected Vec3 model ( final float xSource, final float ySource,
      final float zSource, final Vec3 target ) {

      /* @formatter:off */
      final float aw = this.modelview.m30 * xSource +
                       this.modelview.m31 * ySource +
                       this.modelview.m32 * zSource +
                       this.modelview.m33;

      final float ax = this.modelview.m00 * xSource +
                       this.modelview.m01 * ySource +
                       this.modelview.m02 * zSource +
                       this.modelview.m03;

      final float ay = this.modelview.m10 * xSource +
                       this.modelview.m11 * ySource +
                       this.modelview.m12 * zSource +
                       this.modelview.m13;

      final float az = this.modelview.m20 * xSource +
                       this.modelview.m21 * ySource +
                       this.modelview.m22 * zSource +
                       this.modelview.m23;

      final float bw = this.cameraInv.m30 * ax +
                       this.cameraInv.m31 * ay +
                       this.cameraInv.m32 * az +
                       this.cameraInv.m33 * aw;

      if ( bw == 0.0f ) { return target.reset(); }

      final float bx = this.cameraInv.m00 * ax +
                       this.cameraInv.m01 * ay +
                       this.cameraInv.m02 * az +
                       this.cameraInv.m03 * aw;

      final float by = this.cameraInv.m10 * ax +
                       this.cameraInv.m11 * ay +
                       this.cameraInv.m12 * az +
                       this.cameraInv.m13 * aw;

      final float bz = this.cameraInv.m20 * ax +
                       this.cameraInv.m21 * ay +
                       this.cameraInv.m22 * az +
                       this.cameraInv.m23 * aw;
      /* @formatter:on */

      /* Convert from homogeneous coordinate to point by dividing by w. */
      if ( bw != 1.0f ) {
         final float wInv = 1.0f / bw;
         return target.set(bx * wInv, by * wInv, bz * wInv);
      }
      return target.set(bx, by, bz);
   }

   /**
    * Turns of ambient lighting.
    *
    * @param num the index
    */
   @Override
   protected void noLightAmbient ( final int num ) {

      final int num3 = num + num + num;
      this.lightAmbient[num3] = 0.0f;
      this.lightAmbient[num3 + 1] = 0.0f;
      this.lightAmbient[num3 + 2] = 0.0f;
   }

   /**
    * Turns off diffuse lighting.
    *
    * @param num the index
    */
   @Override
   protected void noLightDiffuse ( final int num ) {

      final int num3 = num + num + num;
      this.lightDiffuse[num3] = 0.0f;
      this.lightDiffuse[num3 + 1] = 0.0f;
      this.lightDiffuse[num3 + 2] = 0.0f;
   }

   /**
    * Turns off light fall-off.
    *
    * @param num the index
    */
   @Override
   protected void noLightFalloff ( final int num ) {

      final int num3 = num + num + num;
      this.lightFalloffCoefficients[num3] = 1.0f;
      this.lightFalloffCoefficients[num3 + 1] = 0.0f;
      this.lightFalloffCoefficients[num3 + 2] = 0.0f;
   }

   /**
    * Turns off specular lighting.
    *
    * @param num the index
    */
   @Override
   protected void noLightSpecular ( final int num ) {

      final int num3 = num + num + num;
      this.lightSpecular[num3] = 0.0f;
      this.lightSpecular[num3 + 1] = 0.0f;
      this.lightSpecular[num3 + 2] = 0.0f;
   }

   /**
    * Turns off spot lighting.
    *
    * @param num the index
    */
   @Override
   protected void noLightSpot ( final int num ) {

      final int num2 = num + num;
      this.lightSpotParameters[num2] = 0.0f;
      this.lightSpotParameters[num2 + 1] = 0.0f;
   }

   /**
    * Draws a rectangle. The meaning of the four parameters depends on
    * rectMode.
    *
    * @param x0 the first x parameter
    * @param y0 the first y parameter
    * @param x1 the second x parameter
    * @param y1 the second y parameter
    */
   @Override
   protected void rectImpl ( final float x0, final float y0, final float x1,
      final float y1 ) {

      float a0;
      float b0;
      float a1;
      float b1;

      float w;
      float h;

      switch ( this.rectMode ) {

         case PConstants.CORNER: /* 0 */

            w = Utils.abs(x1);
            h = Utils.abs(y1);

            a0 = x0;
            b0 = y0 - h;
            a1 = x0 + w;
            b1 = y0;

            break;

         case PConstants.CORNERS: /* 1 */

            a0 = Utils.min(x0, x1);
            a1 = Utils.max(x0, x1);

            b0 = Utils.min(y0, y1);
            b1 = Utils.max(y0, y1);

            break;

         case PConstants.RADIUS: /* 2 */

            w = Utils.abs(x1);
            h = Utils.abs(y1);

            a0 = x0 - w;
            a1 = x0 + w;
            b0 = y0 + h;
            b1 = y0 - h;

            break;

         case PConstants.CENTER: /* 3 */

         default:

            w = Utils.abs(x1) * 0.5f;
            h = Utils.abs(y1) * 0.5f;

            a0 = x0 - w;
            a1 = x0 + w;
            b0 = y0 + h;
            b1 = y0 - h;
      }

      this.beginShape(PConstants.POLYGON);
      this.normalPerShape(0.0f, 0.0f, 1.0f);
      this.vertexImpl(a0, b0, 0.0f, 0.0f, 0.0f);
      this.vertexImpl(a1, b0, 0.0f, 1.0f, 0.0f);
      this.vertexImpl(a1, b1, 0.0f, 1.0f, 1.0f);
      this.vertexImpl(a0, b1, 0.0f, 0.0f, 1.0f);
      this.endShape(PConstants.CLOSE);
   }

   /**
    * Draws a rounded rectangle. The meaning of the first four parameters
    * depends on rectMode.
    *
    * @param x0          the first x parameter
    * @param y0          the first y parameter
    * @param x1          the second x parameter
    * @param y1          the second y parameter
    * @param topLeft     the top-left corner rounding
    * @param topRight    the top-right corner rounding
    * @param bottomRight the bottom-right corner rounding
    * @param bottomLeft  the bottom-left corner rounding
    *
    * @see Utils#abs(float)
    * @see Utils#min(float, float)
    * @see Utils#max(float, float)
    * @see Utils#clamp(float, float, float)
    */
   @Override
   protected void rectImpl ( final float x0, final float y0, final float x1,
      final float y1, final float topLeft, final float topRight,
      final float bottomRight, final float bottomLeft ) {

      float a0;
      float b0;
      float a1;
      float b1;

      float w;
      float h;

      float c0 = topLeft;
      float c1 = topRight;
      float c2 = bottomRight;
      float c3 = bottomLeft;

      switch ( this.rectMode ) {
         case PConstants.CORNER: /* 0 */

            w = Utils.abs(x1);
            h = Utils.abs(y1);

            a0 = x0;
            b0 = y0 - h;
            a1 = x0 + w;
            b1 = y0;

            break;

         case PConstants.CORNERS: /* 1 */

            w = Utils.abs(x1 - x0);
            h = Utils.abs(y0 - y1);

            a0 = Utils.min(x0, x1);
            a1 = Utils.max(x0, x1);

            b0 = Utils.min(y0, y1);
            b1 = Utils.max(y0, y1);

            break;

         case PConstants.RADIUS: /* 2 */

            w = Utils.abs(x1);
            h = Utils.abs(y1);

            a0 = x0 - w;
            a1 = x0 + w;
            b0 = y0 - h;
            b1 = y0 + h;

            break;

         case PConstants.CENTER: /* 3 */
         default:

            w = Utils.abs(x1);
            h = Utils.abs(y1);

            a0 = x0 - w * 0.5f;
            a1 = x0 + w * 0.5f;
            b0 = y0 - h * 0.5f;
            b1 = y0 + h * 0.5f;
      }

      final float limit = Utils.min(w, h) * 0.5f;
      c0 = Utils.clamp(c0, IUtils.EPSILON, limit);
      c1 = Utils.clamp(c1, IUtils.EPSILON, limit);
      c2 = Utils.clamp(c2, IUtils.EPSILON, limit);
      c3 = Utils.clamp(c3, IUtils.EPSILON, limit);

      this.beginShape(PConstants.POLYGON);
      this.normalPerShape(0.0f, 0.0f, 1.0f);

      this.vertexImpl(a1 - c2, b0, 0.0f, this.textureU, this.textureV);
      this.bezierVertexImpl(a1 - c2 + c2 * ICurve.KAPPA, b0, 0.0f, a1, b0 + c2
         - c2 * ICurve.KAPPA, 0.0f, a1, b0 + c2, 0.0f);
      this.vertexImpl(a1, b1 - c1, 0.0f, this.textureU, this.textureV);
      this.bezierVertexImpl(a1, b1 - c1 + c1 * ICurve.KAPPA, 0.0f, a1 - c1 + c1
         * ICurve.KAPPA, b1, 0.0f, a1 - c1, b1, 0.0f);
      this.vertexImpl(a0 + c0, b1, 0.0f, this.textureU, this.textureV);
      this.bezierVertexImpl(a0 + c0 - c0 * ICurve.KAPPA, b1, 0.0f, a0, b1 - c0
         + c0 * ICurve.KAPPA, 0.0f, a0, b1 - c0, 0.0f);
      this.vertexImpl(a0, b0 + c3, 0.0f, this.textureU, this.textureV);
      this.bezierVertexImpl(a0, b0 + c3 - c3 * ICurve.KAPPA, 0.0f, a0 + c3 - c3
         * ICurve.KAPPA, b0, 0.0f, a0 + c3, b0, 0.0f);
      this.endShape(PConstants.CLOSE);
   }

   /**
    * Rotates the renderer's model view matrix by an angle in radians around
    * an axis. Normalizes the axis if it is not of unit length.
    *
    * @param radians the angle in radians
    * @param xAxis   the axis x component
    * @param yAxis   the axis y component
    * @param zAxis   the axis z component
    */
   @Override
   protected void rotateImpl ( final float radians, final float xAxis,
      final float yAxis, final float zAxis ) {

      /* Axis is verified here because PMatAux compound rotate will not. */
      final float mSq = xAxis * xAxis + yAxis * yAxis + zAxis * zAxis;
      if ( mSq > 0.0f ) {
         final float normRad = radians * IUtils.ONE_TAU;
         final float cosa = Utils.scNorm(normRad);
         final float sina = Utils.scNorm(normRad - 0.25f);

         float xn = xAxis;
         float yn = yAxis;
         float zn = zAxis;
         if ( mSq != 1.0f ) {
            final float mInv = Utils.invSqrtUnchecked(mSq);
            xn *= mInv;
            yn *= mInv;
            zn *= mInv;
         }

         PMatAux.compoundRotate(cosa, sina, xn, yn, zn, this.modelview,
            this.modelviewInv);
         PMatAux.mul(this.projection, this.modelview, this.projmodelview);
      }
   }

   /**
    * Scales the renderer by a non-uniform scalar.
    *
    * @param sx the scale x
    * @param sy the scale y
    * @param sz the scale z
    */
   @Override
   protected void scaleImpl ( final float sx, final float sy, final float sz ) {

      this.modelview.scale(sx, sy, sz);
      PMatAux.invScale(sx, sy, sz, this.modelviewInv);
      this.projmodelview.scale(sx, sy, sz);
   }

   /**
    * Internal helper to public functions for 2.5D and 3D. Finds the screen
    * position of a point in the world. Does so by
    * <ol>
    * <li>promoting the point to a vector 4, where its w component is 1.0
    * .</li>
    * <li>multiplying the vector 4 by the model view matrix;</li>
    * <li>multiplying the product by the projection;</li>
    * <li>demoting the vector 4 to a point 3 by dividing the x, y and z
    * components by w;</li>
    * <li>flipping the y axis;</li>
    * <li>shifting the range from [-1.0, 1.0] to [(0.0, 0.0), (width, height)]
    * .</li>
    * </ol>
    * More efficient than calling {@link PApplet#screenX(float, float, float)}
    * , {@link PApplet#screenY(float, float, float)} , and
    * {@link PApplet#screenZ(float, float, float)} separately. However, it is
    * advisable to work with {@link Vec4}s and the renderer matrices directly.
    *
    * @param xSource the source x
    * @param ySource the source y
    * @param zSource the source z
    * @param target  the output vector
    *
    * @return the screen point
    */
   @Experimental
   protected Vec3 screen ( final float xSource, final float ySource,
      final float zSource, final Vec3 target ) {

      this.screen1s(xSource, ySource, zSource, target);
      return target.set(this.width * ( 1.0f + target.x ) * 0.5f, this.height
         * ( 1.0f - ( 1.0f + target.y ) * 0.5f ), ( 1.0f + target.z ) * 0.5f);
   }

   /**
    * An internal helper function that multiplies the input point by the model
    * view, then by the projection.
    *
    * @param xSource the source x
    * @param ySource the source y
    * @param zSource the source z
    * @param target  the output vector
    *
    * @return the screen point
    */
   protected Vec3 screen1s ( final float xSource, final float ySource,
      final float zSource, final Vec3 target ) {

      /* Multiply by model-view matrix; multiply product by projection. */

      /* @formatter:off */
      final float aw = this.modelview.m30 * xSource +
                       this.modelview.m31 * ySource +
                       this.modelview.m32 * zSource +
                       this.modelview.m33;

      final float ax = this.modelview.m00 * xSource +
                       this.modelview.m01 * ySource +
                       this.modelview.m02 * zSource +
                       this.modelview.m03;

      final float ay = this.modelview.m10 * xSource +
                       this.modelview.m11 * ySource +
                       this.modelview.m12 * zSource +
                       this.modelview.m13;

      final float az = this.modelview.m20 * xSource +
                       this.modelview.m21 * ySource +
                       this.modelview.m22 * zSource +
                       this.modelview.m23;

      final float bw = this.projection.m30 * ax +
                       this.projection.m31 * ay +
                       this.projection.m32 * az +
                       this.projection.m33 * aw;

      if ( bw == 0.0f ) { return target.reset(); }

      final float bx = this.projection.m00 * ax +
                       this.projection.m01 * ay +
                       this.projection.m02 * az +
                       this.projection.m03 * aw;

      final float by = this.projection.m10 * ax +
                       this.projection.m11 * ay +
                       this.projection.m12 * az +
                       this.projection.m13 * aw;

      final float bz = this.projection.m20 * ax +
                       this.projection.m21 * ay +
                       this.projection.m22 * az +
                       this.projection.m23 * aw;
      /* @formatter:on */

      /* Convert homogeneous coordinate. */
      if ( bw != 1.0f ) {
         final float wInv = 1.0f / bw;
         return target.set(bx * wInv, by * wInv, bz * wInv);
      }
      return target.set(bx, by, bz);
   }

   /**
    * Displays a PShape. Use of this function is discouraged by this renderer.
    * See mesh and curve entities instead.
    *
    * @param psh the PShape
    * @param x   the x coordinate
    * @param y   the y coordinate
    * @param z   the z coordinate
    * @param c   the x scale
    * @param d   the y scale
    * @param e   the z scale
    */
   @Override
   protected void shape ( final PShape psh, final float x, final float y,
      final float z, final float c, final float d, final float e ) {

      this.shape(psh);
   }

   /**
    * Draws a character depending on the text mode.
    *
    * @param ch the character
    * @param x  the x coordinate
    * @param y  the y coordinate
    */
   @Override
   protected void textCharImpl ( final char ch, final float x, final float y ) {

      switch ( this.textMode ) {
         case PConstants.SHAPE: /* 5 */
            super.textCharShapeImpl(ch, x, y);
            break;

         case PConstants.MODEL: /* 4 */
         default:
            this.textCharModelImpl(ch, x, y, 0.0f);
      }
   }

   /**
    * Draws a character with reference to a a glyph retrieved from the current
    * font.
    *
    * @param ch the character
    * @param x  the x coordinate
    * @param y  the y coordinate
    * @param z  the z coordinate
    *
    * @see PFont#getGlyph(char)
    * @see PFont#getSize()
    */
   protected void textCharModelImpl ( final char ch, final float x,
      final float y, final float z ) {

      /*
       * FontTexture.TextureInfo is a static inner class, and so cannot be
       * accessed from here.
       */
      final PFont.Glyph glyph = this.textFont.getGlyph(ch);
      if ( glyph != null ) {

         /*
          * Hack to deal with blending modes. The "correct" blend causes
          * backgrounds to appear on text; the default is the old blend mode.
          */
         final int oldBlendMode = this.blendMode;
         this.blendMode = IUpOgl.TEXT_BLEND;
         this.blendModeImpl();

         final int wGlyph = glyph.width;
         final int hGlyph = glyph.height;
         final int lExtent = glyph.leftExtent;
         final int tExtent = glyph.topExtent;

         final float szNorm = Utils.div(this.textSize, this.textFont.getSize());
         final float x0 = x + lExtent * szNorm;
         final float x1 = x0 + wGlyph * szNorm;
         final float y0 = y + tExtent * szNorm;
         final float y1 = y0 - hGlyph * szNorm;

         /* Cache prior styles. */
         final boolean savedTint = this.tint;
         final int savedTintColor = this.tintColor;
         final float savedTintR = this.tintR;
         final float savedTintG = this.tintG;
         final float savedTintB = this.tintB;
         final float savedTintA = this.tintA;
         final boolean savedTintAlpha = this.tintAlpha;

         /* Font color is accomplished through tinting. */
         this.tint = true;
         this.tintColor = this.fillColor;
         this.tintR = this.fillR;
         this.tintG = this.fillG;
         this.tintB = this.fillB;
         this.tintA = this.fillA;
         this.tintAlpha = this.fillAlpha;

         /* Draw polygon. */
         this.beginShape(PConstants.POLYGON);
         this.normalPerShape(0.0f, 0.0f, 1.0f);
         this.texture(glyph.image);
         this.vertexImpl(x0, y0, z, 0.0f, 0.0f);
         this.vertexImpl(x1, y0, z, 1.0f, 0.0f);
         this.vertexImpl(x1, y1, z, 1.0f, 1.0f);
         this.vertexImpl(x0, y1, z, 0.0f, 1.0f);
         this.endShape(PConstants.CLOSE);

         /* Restore prior style settings. */
         this.tint = savedTint;
         this.tintColor = savedTintColor;
         this.tintR = savedTintR;
         this.tintG = savedTintG;
         this.tintB = savedTintB;
         this.tintA = savedTintA;
         this.tintAlpha = savedTintAlpha;

         this.blendMode = oldBlendMode;
         this.blendModeImpl();
      }
   }

   /**
    * Draws an image representing a glyph from a font.
    *
    * @param glyph the glyph image
    * @param x0    the first x coordinate
    * @param y0    the first y coordinate
    * @param x1    the second x coordinate
    * @param y1    the second y coordinate
    * @param u     the u coordinate
    * @param v     the v coordinate
    */
   @Override
   protected void textCharModelImpl ( final PImage glyph, final float x0,
      final float y0, final float x1, final float y1, final int u,
      final int v ) {

      // QUERY Where is this used in the library?

      final int oldBlendMode = this.blendMode;
      this.blendMode = IUpOgl.TEXT_BLEND;
      this.blendModeImpl();

      final boolean savedTint = this.tint;
      final int savedTintColor = this.tintColor;
      final float savedTintR = this.tintR;
      final float savedTintG = this.tintG;
      final float savedTintB = this.tintB;
      final float savedTintA = this.tintA;
      final boolean savedTintAlpha = this.tintAlpha;

      this.tint = true;
      this.tintColor = this.fillColor;
      this.tintR = this.fillR;
      this.tintG = this.fillG;
      this.tintB = this.fillB;
      this.tintA = this.fillA;
      this.tintAlpha = this.fillAlpha;

      this.image(glyph, x0, y0, x1, y1, 0.0f, 0.0f, 0.0f, u, v);

      this.tint = savedTint;
      this.tintColor = savedTintColor;
      this.tintR = savedTintR;
      this.tintG = savedTintG;
      this.tintB = savedTintB;
      this.tintA = savedTintA;
      this.tintAlpha = savedTintAlpha;

      this.blendMode = oldBlendMode;
      this.blendModeImpl();
   }

   /**
    * Translates the renderer by a vector.
    *
    * @param tx the translation x
    * @param ty the translation y
    * @param tz the translation z
    */
   @Override
   protected void translateImpl ( final float tx, final float ty,
      final float tz ) {

      this.modelview.translate(tx, ty, tz);
      PMatAux.invTranslate(tx, ty, tz, this.modelviewInv);
      this.projmodelview.translate(tx, ty, tz);
   }

   /**
    * Transfers the model view from a PMatrix3D to a one-dimensional float
    * array used by OpenGL.
    */
   @Override
   protected void updateGLModelview ( ) {

      this.updateGLModelview(this.modelview);
   }

   /**
    * Transfers the transpose of the model view inverse, which is the normal
    * matrix, from a PMatrix3D to a one-dimensional float array used by
    * OpenGL.
    */
   @Override
   protected void updateGLNormal ( ) {

      this.updateGLNormal(this.modelviewInv);
   }

   /**
    * Transfers the projection from a PMatrix3D to a one-dimensional float
    * array used by OpenGL.
    */
   @Override
   protected void updateGLProjection ( ) {

      this.updateGLProjection(this.projection);
   }

   /**
    * Transfers the project model view from a PMatrix3D to a one-dimensional
    * float array used by OpenGL.
    */
   @Override
   protected void updateGLProjmodelview ( ) {

      this.updateGLProjmodelview(this.projmodelview);
   }

   /**
    * Updates the texture coordinates of the renderer. If the texture mode is
    * IMAGE, divides the UV coordinates by the image's dimensions. If the
    * texture wrap is CLAMP, clamps the coordinates to [0.0, 1.0] ; if the
    * wrap is REPEAT, floor wraps them instead.
    *
    * @param u the s or u coordinate
    * @param v the t or v coordinate
    */
   @Override
   protected void vertexTexture ( final float u, final float v ) {

      this.vertexTexture(u, v, this.textureMode, this.textureWrap);
   }

   /**
    * Updates the texture coordinates of the renderer. If the texture mode is
    * IMAGE, divides the UV coordinates by the image's dimensions. If the
    * texture wrap is CLAMP, clamps the coordinates to [0.0, 1.0] ; if the
    * wrap is REPEAT, wraps them instead.
    *
    * @param u                  the s or u coordinate
    * @param v                  the t or v coordinate
    * @param desiredTextureMode the texture mode, IMAGE or NORMAL
    * @param desiredTextureWrap the texture wrap, CLAMP or REPEAT
    *
    * @see Utils#div(float, float)
    * @see Utils#clamp01(float)
    */
   protected void vertexTexture ( final float u, final float v,
      final int desiredTextureMode, final int desiredTextureWrap ) {

      this.textureMode = desiredTextureMode;
      this.textureWrap = desiredTextureWrap;

      /* This operation is also performed by vertexImpl. */
      if ( this.textureMode == PConstants.IMAGE && this.textureImage != null ) {
         this.textureU = Utils.div(u, this.textureImage.pixelWidth);
         this.textureV = Utils.div(v, this.textureImage.pixelHeight);
      } else {
         this.textureU = u;
         this.textureV = v;
      }

      switch ( desiredTextureWrap ) {

         case PConstants.CLAMP: /* 0 */

            this.textureU = Utils.clamp01(this.textureU);
            this.textureV = Utils.clamp01(this.textureV);

            break;

         case PConstants.REPEAT: /* 1 */

            /*
             * Problem where in meshes that use cylindrical projection (UV
             * sphere, cylinder), u = 1.0 is returned to u = 0.0 by modulo.
             */

            // this.textureU = Utils.mod1(this.textureU);
            // this.textureV = Utils.mod1(this.textureV);

            break;

         default:

      }
   }

   /**
    * The path string for this renderer.
    */
   public static final String PATH_STR = "camzup.pfriendly.UpOgl";

}

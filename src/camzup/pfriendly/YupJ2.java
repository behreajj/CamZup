package camzup.pfriendly;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.util.Iterator;
import java.util.LinkedList;

import camzup.core.Color;
import camzup.core.Curve2;
import camzup.core.Curve2.Knot2;
import camzup.core.CurveEntity2;
import camzup.core.IUtils;
import camzup.core.MaterialSolid;
import camzup.core.Mesh2;
import camzup.core.MeshEntity2;
import camzup.core.Transform;
import camzup.core.Transform2;
import camzup.core.Utils;
import camzup.core.Vec2;
import processing.awt.PGraphicsJava2D;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PMatrix2D;
import processing.core.PMatrix3D;
import processing.core.PShape;

/**
 * A 2D renderer based on the Java AWT (Abstract Window
 * Toolkit). Supposes that the the camera is looking down on
 * a 2D plane from the z axis, making (0.0, 1.0) the forward
 * -- or up -- axis.
 */
public class YupJ2 extends PGraphicsJava2D implements IYup2 {

   /**
    * The default camera rotation in radians.
    */
   public static final float DEFAULT_ROT = 0.0f;

   /**
    * The default camera zoom horizontal.
    */
   public static final float DEFAULT_ZOOM_X = 1.0f;

   /**
    * The default camera vertical zoom.
    */
   public static final float DEFAULT_ZOOM_Y = 1.0f;

   /**
    * A Java AWT affine transform object. This is cached so a
    * new object is not created when accessing or mutating the
    * renderer matrix.
    */
   protected AffineTransform affineNative = new AffineTransform(
         1.0d, 0.0d, 0.0d,
         0.0d, 1.0d, 0.0d);

   /**
    * A Java AWT arc object.
    */
   protected final Arc2D.Float arc = new Arc2D.Float();

   /**
    * A placeholder color used during lerpColor.
    */
   protected final Color aTemp = new Color();

   /**
    * A placeholder color used during lerpColor.
    */
   protected final Color bTemp = new Color();

   /**
    * A placeholder color used during lerpColor.
    */
   protected final Color cTemp = new Color();

   /**
    * A Java AWT general path object. This is reset when a new
    * shape needs to be displayed in draw.
    */
   protected final GeneralPath gp = new GeneralPath();

   /**
    * One divided by the maximum for the alpha channel.
    */
   protected float invColorModeA = 1.0f;

   /**
    * One divided by the maximum for the hue or red channel.
    */
   protected float invColorModeX = 1.0f;

   /**
    * One divided by the maximum for the saturation or green
    * channel.
    */
   protected float invColorModeY = 1.0f;

   /**
    * One divided by the maximum for the brightness or blue
    * channel.
    */
   protected float invColorModeZ = 1.0f;

   /**
    * A placeholder vector used during transform.
    */
   protected final Vec2 tr2Loc = new Vec2();

   /**
    * A placeholder vector used during transform.
    */
   protected final Vec2 tr2Scale = new Vec2();

   /**
    * A placeholder transform used during transform.
    */
   protected final Transform2 transform = new Transform2();

   /**
    * The camera rotation in radians.
    */
   public float cameraRot = 0.0f;

   /**
    * The camera's location on the x axis.
    */
   public float cameraX = 0.0f;

   /**
    * The camera's location on the y axis.
    */
   public float cameraY = 0.0f;

   /**
    * The camera horizontal zoom.
    */
   public float cameraZoomX = 1.0f;

   /**
    * The camera vertical zoom.
    */
   public float cameraZoomY = 1.0f;

   /**
    * The default constructor.
    */
   public YupJ2 () {

      super();
   }

   /**
    * A constructor for manually initializing the renderer.
    *
    * @param width
    *           renderer width
    * @param height
    *           renderer height
    * @param parent
    *           parent applet
    * @param path
    *           applet path
    * @param isPrimary
    *           is the renderer primary
    */
   public YupJ2 (
         final int width, final int height,
         final PApplet parent,
         final String path,
         final boolean isPrimary ) {

      this.setParent(parent);
      this.setPrimary(isPrimary);
      this.setPath(path);
      this.setSize(width, height);
   }

   /**
    * The arc implementation. The underlying Java AWT arc asks
    * for a start angle and an arc length, not a stop angle, in
    * degrees, not radians.
    *
    * @param x
    *           the arc location x
    * @param y
    *           the arc location y
    * @param w
    *           the arc width
    * @param h
    *           the arc height
    * @param start
    *           the start angle
    * @param stop
    *           the stop angle
    * @param mode
    *           the arc mode
    */
   @Override
   protected void arcImpl (
         final float x, final float y,
         final float w, final float h,
         final float start, final float stop,
         final int mode ) {

      final float a = IUtils.TAU - Utils.modRadians(start);
      final float b = IUtils.TAU - Utils.modRadians(stop);

      final float c = IUtils.RAD_TO_DEG * a;
      final float d = IUtils.RAD_TO_DEG * Utils.modRadians(b - a);

      int fillMode = Arc2D.PIE;
      int strokeMode = Arc2D.OPEN;

      if (mode == PConstants.OPEN) {
         fillMode = Arc2D.OPEN;
      } else if (mode == PConstants.PIE) {
         strokeMode = Arc2D.PIE;

      } else if (mode == PConstants.CHORD) {
         fillMode = Arc2D.CHORD;
         strokeMode = Arc2D.CHORD;
      }

      if (this.fill) {
         this.arc.setArc(x, y, w, h, c, d, fillMode);
         this.fillShape(this.arc);
      }
      if (this.stroke) {
         this.arc.setArc(x, y, w, h, c, d, strokeMode);
         this.strokeShape(this.arc);
      }
   }

   /**
    * Calculates the color channels from four input channels.
    * The manner in which the first three are interpreted
    * depends on color mode.
    *
    * @param x
    *           the first color channel, hue or red
    * @param y
    *           the second color channel, saturation or green
    * @param z
    *           the third color channel, brightness or blue
    * @param a
    *           the alpha channel
    */
   @Override
   protected void colorCalc (
         final float x,
         final float y,
         final float z,
         final float a ) {

      this.calcG = Utils.clamp01(
            y * this.invColorModeY);
      this.calcB = Utils.clamp01(
            z * this.invColorModeZ);
      this.calcA = Utils.clamp01(
            a * this.invColorModeA);

      switch (this.colorMode) {

         case HSB:

            this.calcR = x * this.invColorModeX;

            Color.hsbaToRgba(
                  this.calcR,
                  this.calcG,
                  this.calcB,
                  this.calcA,
                  this.aTemp);

            this.calcR = this.aTemp.x;
            this.calcG = this.aTemp.y;
            this.calcB = this.aTemp.z;

            break;

         case RGB:

         default:

            this.calcR = Utils.clamp01(
                  x * this.invColorModeX);

      }

      this.calcRi = (int) (this.calcR * 0xff + 0.5f);
      this.calcGi = (int) (this.calcG * 0xff + 0.5f);
      this.calcBi = (int) (this.calcB * 0xff + 0.5f);
      this.calcAi = (int) (this.calcA * 0xff + 0.5f);

      this.calcColor = this.calcAi << 0x18
            | this.calcRi << 0x10
            | this.calcGi << 0x8
            | this.calcBi;
      this.calcAlpha = this.calcAi != 0xff;
   }

   /**
    * Calculates a color and
    *
    * @param argb
    *           the color in hexadecimal
    * @param alpha
    *           the alpha channel
    */
   @Override
   protected void colorCalcARGB (
         final int argb,
         final float alpha ) {

      if (alpha == this.colorModeA) {
         this.calcAi = argb >> 0x18 & 0xff;
         this.calcColor = argb;
      } else {
         this.calcAi = (int) ((argb >> 0x18 & 0xff)
               * Utils.clamp01(alpha * this.invColorModeA));
         this.calcColor = this.calcAi << 0x18 | argb & 0xffffff;
      }

      this.calcRi = argb >> 0x10 & 0xff;
      this.calcGi = argb >> 0x8 & 0xff;
      this.calcBi = argb & 0xff;

      this.calcA = this.calcAi * IUtils.ONE_255;
      this.calcR = this.calcRi * IUtils.ONE_255;
      this.calcG = this.calcGi * IUtils.ONE_255;
      this.calcB = this.calcBi * IUtils.ONE_255;

      this.calcAlpha = this.calcAi != 0xff;
   }

   /**
    * Initializes the curve basis matrix.
    */
   @Override
   protected void curveInit () {

      if (this.curveDrawMatrix == null) {
         this.curveBasisMatrix = new PMatrix3D();
         this.curveDrawMatrix = new PMatrix3D();
         this.curveInited = true;
      }

      final float s = this.curveTightness;
      final float t = (s - 1.0f) * 0.5f;
      final float u = 1.0f - s;
      final float v = u * 0.5f;
      this.curveBasisMatrix.set(
            t, (s + 3.0f) * 0.5f, (-3.0f - s) * 0.5f, v,
            u, (-5.0f - s) * 0.5f, s + 2.0f, t,
            t, 0.0f, v, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f);

      this.splineForward(this.curveDetail, this.curveDrawMatrix);

      if (this.bezierBasisInverse == null) {
         this.bezierBasisInverse = new PMatrix3D(this.bezierBasisMatrix);
         this.bezierBasisInverse.invert();

         this.curveToBezierMatrix = new PMatrix3D();
      }

      this.curveToBezierMatrix.set(this.curveBasisMatrix);
      this.curveToBezierMatrix.preApply(this.bezierBasisInverse);
      this.curveDrawMatrix.apply(this.curveBasisMatrix);
   }

   /**
    * Sets the renderer's default styling.
    */
   @Override
   protected void defaultSettings () {

      super.defaultSettings();
      this.colorMode(PConstants.RGB, IUp.DEFAULT_COLOR_MAX);
      this.fill(IUp.DEFAULT_FILL_COLOR);
      this.stroke(IUp.DEFAULT_STROKE_COLOR);

      this.strokeWeight(IUp.DEFAULT_STROKE_WEIGHT);
      this.strokeJoin(PConstants.ROUND);
      this.strokeCap(PConstants.ROUND);
      this.stroke = false;

      this.shape = 0;

      this.rectMode(PConstants.CENTER);
      this.ellipseMode(PConstants.CENTER);
      this.imageMode(PConstants.CENTER);
      this.shapeMode(PConstants.CENTER);

      this.autoNormal = true;
      this.textFont = null;
      this.textSize = IUp.DEFAULT_TEXT_SIZE;
      this.textLeading = IUp.DEFAULT_TEXT_LEADING;
      this.textAlign = PConstants.CENTER;
      this.textAlignY = PConstants.CENTER;
      this.textMode = PConstants.MODEL;

      if (this.primaryGraphics) {
         this.background(IUp.DEFAULT_BKG_COLOR);
      }

      this.blendMode(PConstants.BLEND);

      this.settingsInited = true;
      this.reapplySettings = false;
   }

   /**
    * The rounded corner rectangle implementation. The meaning
    * of the first four parameters depends on rectMode.
    *
    * @param a
    *           the first x parameter
    * @param b
    *           the first y parameter
    * @param c
    *           the second x parameter
    * @param d
    *           the second y parameter
    * @param tl
    *           the top-left corner rounding
    * @param tr
    *           the top-right corner rounding
    * @param br
    *           the bottom-right corner rounding
    * @param bl
    *           the bottom-left corner rounding
    */
   @Override
   protected void rectImpl (
         final float a, final float b,
         final float c, final float d,

         float tl, float tr,
         float br, float bl ) {

      float x1 = 0.0f;
      float y1 = 0.0f;
      float x2 = 0.0f;
      float y2 = 0.0f;

      float w = 0.0f;
      float h = 0.0f;

      switch (this.rectMode) {

         case CORNER:

            w = Utils.abs(c);
            h = Utils.abs(d);

            x1 = a;
            y2 = b - h;
            x2 = a + w;
            y1 = b;

            break;

         case CORNERS:

            w = Utils.abs(c - a);
            h = Utils.abs(b - d);

            x1 = Utils.min(a, c);
            x2 = Utils.max(a, c);

            y2 = Utils.min(b, d);
            y1 = Utils.max(b, d);

            break;

         case RADIUS:

            w = Utils.abs(c);
            h = Utils.abs(d);

            x1 = a - w;
            x2 = a + w;
            y1 = b + h;
            y2 = b - h;

            break;

         case CENTER:

         default:

            w = Utils.abs(c);
            h = Utils.abs(d);

            x1 = a - w * 0.5f;
            x2 = a + w * 0.5f;
            y1 = b + h * 0.5f;
            y2 = b - h * 0.5f;
      }

      final float limit = Utils.min(w, h) * 0.5f;
      tl = Utils.clamp(tl, PConstants.EPSILON, limit);
      tr = Utils.clamp(tr, PConstants.EPSILON, limit);
      br = Utils.clamp(br, PConstants.EPSILON, limit);
      bl = Utils.clamp(bl, PConstants.EPSILON, limit);

      this.gp.reset();
      this.gp.moveTo(x2 - tr, y1);
      this.gp.quadTo(x2, y1, x2, y1 - tr);
      this.gp.lineTo(x2, y2 + br);
      this.gp.quadTo(x2, y2, x2 - br, y2);
      this.gp.lineTo(x1 + bl, y2);
      this.gp.quadTo(x1, y2, x1, y2 + bl);
      this.gp.lineTo(x1, y1 - tl);
      this.gp.quadTo(x1, y1, x1 + tl, y1);
      this.gp.closePath();
      this.drawShape(this.gp);
   }

   /**
    * Displays a character in the sketch.
    *
    * @param ch
    *           the character
    * @param x
    *           the location x
    * @param y
    *           the location y
    */
   @Override
   protected void textCharImpl (
         final char ch,
         final float x,
         final float y ) {

      final PFont.Glyph glyph = this.textFont.getGlyph(ch);
      if (glyph != null) {
         final float szInv = 1.0f / this.textFont.getSize();
         final float wGlyph = glyph.width * szInv;
         final float hGlyph = glyph.height * szInv;
         final float lextent = glyph.leftExtent * szInv;
         final float textent = glyph.topExtent * szInv;

         final float x0 = x + lextent * this.textSize;
         final float x1 = x0 + wGlyph * this.textSize;

         final float y0 = y + textent * this.textSize;
         final float y1 = y0 - hGlyph * this.textSize;

         this.textCharModelImpl(
               glyph.image,
               x0, y0, x1, y1,
               glyph.width, glyph.height);

      }
   }

   /**
    * Draws an image representing a glyph from a font.
    *
    * @param glyph
    *           the glyph image
    * @param x1
    *           the first x coordinate
    * @param y1
    *           the first y coordinate
    * @param x2
    *           the second x coordinate
    * @param y2
    *           the second y coordinate
    * @param u
    *           the u coordinate
    * @param v
    *           the v coordinate
    */
   @Override
   protected void textCharModelImpl (
         final PImage glyph,
         final float x1, final float y1,
         final float x2, final float y2,
         final int u, final int v ) {

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

      final int oldImgMd = this.imageMode;
      this.imageMode = PConstants.CORNERS;
      this.image(glyph,
            x1, y1, x2, y2,
            0, 0, u, v);
      this.imageMode = oldImgMd;

      // final int rgb = this.fillColor & 0x00ffffff;
      // glim.loadPixels();
      // final int[] px = glim.pixels;
      // final int len = px.length;
      // for (int i = 0; i < len; ++i) {
      // px[i] = ((px[i] << 0x18) &
      // this.fillAi) | rgb;
      // }
      // glim.updatePixels();
      // glim.format = ARGB;
      // this.g2.drawImage(
      // (Image) glim.getNative(),
      // (int) x1, (int) y1, (int) x2, (int) y2,
      // 0, 0, glim.width, glim.height, null);

      this.tint = savedTint;
      this.tintColor = savedTintColor;
      this.tintR = savedTintR;
      this.tintG = savedTintG;
      this.tintB = savedTintB;
      this.tintA = savedTintA;
      this.tintAlpha = savedTintAlpha;
   }

   /**
    * Helper function for text with multiple lines. Handles the
    * horizontal display of a character along a line.
    *
    * @param buffer
    *           the array of characters
    * @param start
    *           the start index, inclusive
    * @param stop
    *           the stop index, exclusive
    * @param x
    *           the horizontal location
    * @param y
    *           the vertical location
    */
   @Override
   protected void textLineImpl (
         final char[] buffer,
         final int start,
         final int stop,
         final float x, final float y ) {

      float cursor = x;
      for (int index = start; index < stop; ++index) {
         final char c = buffer[index];
         this.textCharImpl(c, cursor, y);
         cursor += this.textWidth(c);
      }
   }

   /**
    * Applies an affine transform matrix to the current
    * renderer transform.
    *
    * @param m00
    *           right axis x
    * @param m01
    *           up axis x
    * @param m02
    *           translation x
    * @param m10
    *           right axis y
    * @param m11
    *           up axis y
    * @param m12
    *           translation y
    * @see AffineTransform#setTransform(double, double, double,
    *      double, double, double)
    * @see Graphics2D#transform(AffineTransform)
    */
   @Override
   public void applyMatrix (
         final float m00, final float m01, final float m02,
         final float m10, final float m11, final float m12 ) {

      this.affineNative.setTransform(m00, m10, m01, m11, m02, m12);
      this.g2.transform(this.affineNative);
   }

   /**
    * Draws an arc at a location from a start angle to a stop
    * angle. The meaning of the first four parameters depends
    * on the renderer's ellipseMode.
    *
    * @param x0
    *           the first x
    * @param y0
    *           the first y
    * @param x1
    *           the second x
    * @param y1
    *           the second y
    * @param start
    *           the start angle
    * @param stop
    *           the stop angle
    * @param mode
    *           the arc mode
    * @see YupJ2#arcImpl(float, float, float, float, float,
    *      float, int)
    */
   @Override
   public void arc (
         final float x0, final float y0,
         final float x1, final float y1,
         final float start, final float stop,
         final int mode ) {

      float x = x0;
      float y = y0;
      float w = x1;
      float h = y1;

      switch (this.ellipseMode) {

         case CORNERS:
            w = Utils.diff(x1, x0);
            h = Utils.diff(y1, y0);
            x = x0;
            y = y0;

            break;

         case RADIUS:
            w = Utils.abs(x1);
            h = Utils.abs(y1);
            w += w;
            h += h;
            x = x0 - w * 0.5f;
            y = y0 - h * 0.5f;

            break;

         case CENTER:
            w = Utils.abs(x1);
            h = Utils.abs(y1);
            x = x0 - w * 0.5f;
            y = y0 - h * 0.5f;

            break;

         case CORNER:

         default:
            w = Utils.abs(x1);
            h = Utils.abs(y1);
            x = x0;
            y = y0 - h;
      }

      this.arcImpl(x, y, w, h, start, stop, mode);
   }

   /**
    * Draws an arc at a location from a start angle to a stop
    * angle.
    *
    * @param v
    *           the location
    * @param sz
    *           the arc size
    * @param start
    *           the start angle
    * @param stop
    *           the stop angle
    * @param mode
    *           the arc mode
    */
   @Override
   public void arc (
         final Vec2 v,
         final float sz,
         final float start, final float stop,
         final int mode ) {

      this.arc(
            v.x, v.y,
            sz, sz,
            start, stop,
            mode);
   }

   /**
    * Draws a cubic Bezier curve between two anchor points,
    * where the control points shape the curve.
    *
    * @param ap0
    *           the first anchor point
    * @param cp0
    *           the first control point
    * @param cp1
    *           the second control point
    * @param ap1
    *           the second anchor point
    */
   @Override
   public void bezier (
         final Vec2 ap0, final Vec2 cp0,
         final Vec2 cp1, final Vec2 ap1 ) {

      this.bezier(
            ap0.x, ap0.y,
            cp0.x, cp0.y,
            cp1.x, cp1.y,
            ap1.x, ap1.y);
   }

   @Override
   public float bezierPoint (
         final float a,
         final float b,
         final float c,
         final float d,
         final float t ) {

      final float u = 1.0f - t;
      return (a * u + b * (t + t + t)) * u * u
            + (c * (u + u + u) + d * t) * t * t;
   }

   @Override
   public float bezierTangent (
         final float a,
         final float b,
         final float c,
         final float d,
         final float t ) {

      final float t3 = t + t + t;
      final float b2 = b + b;
      final float ac = a + c;
      final float bna = b - a;

      return t3 * t * (b2 + b + d - (ac + c + c)) +
            (t3 + t3) * (ac - b2) +
            (bna + bna + bna);
   }

   /**
    * Draws a cubic Bezier curve segment to the next anchor
    * point; the first and second control point shape the curve
    * segment.
    *
    * @param cp0
    *           the first control point
    * @param cp1
    *           the second control point
    * @param ap1
    *           the next anchor point
    */
   @Override
   public void bezierVertex (
         final Vec2 cp0,
         final Vec2 cp1,
         final Vec2 ap1 ) {

      this.bezierVertex(
            cp0.x, cp0.y,
            cp1.x, cp1.y,
            ap1.x, ap1.y);
   }

   /**
    * Draws a transform's axes.
    *
    * @param transform
    *           the transform
    * @param lineLength
    *           the line length
    * @param strokeWeight
    *           the stroke weight
    */
   // public void drawTransform2 (
   // final Transform2 transform,
   // final float lineLength,
   // final float strokeWeight ) {
   //
   // this.drawTransform2(
   // transform,
   // lineLength,
   // strokeWeight);
   // }

   /**
    * Draws a transform's axes.
    *
    * @param transform
    *           the transform
    * @param lineLength
    *           the line length
    * @param strokeWeight
    *           the stroke weight
    * @param xColor
    *           the color of the x axis
    * @param yColor
    *           the color of the y axis
    */
   // public void drawTransform2 (
   // final Transform2 transform,
   // final float lineLength,
   // final float strokeWeight,
   // final int xColor, final int yColor ) {
   //
   // final Vec2 right = transform.getRight(new Vec2());
   // final Vec2 forward = transform.getForward(new Vec2());
   // final Vec2 loc = transform.getLocation(new Vec2());
   //
   // this.pushStyle();
   //
   // this.strokeWeight(strokeWeight);
   // this.stroke(xColor);
   // this.line(
   // loc.x, loc.y,
   // loc.x + right.x * lineLength,
   // loc.y + right.y * lineLength);
   //
   // this.stroke(yColor);
   // this.line(
   // loc.x, loc.y,
   // loc.x + forward.x * lineLength,
   // loc.y + forward.y * lineLength);
   //
   // this.popStyle();
   // }

   /**
    * Sets the camera to the renderer defaults.
    */
   @Override
   public void camera () {

      this.camera(
            IUp.DEFAULT_LOC_X, IUp.DEFAULT_LOC_Y,
            YupJ2.DEFAULT_ROT,
            YupJ2.DEFAULT_ZOOM_X, YupJ2.DEFAULT_ZOOM_Y);
   }

   /**
    * Sets the camera to a location.
    *
    * @param x
    *           the location x component
    * @param y
    *           the location y component
    */
   public void camera ( final float x, final float y ) {

      this.camera(x, y,
            YupJ2.DEFAULT_ROT,
            YupJ2.DEFAULT_ZOOM_X, YupJ2.DEFAULT_ZOOM_Y);
   }

   /**
    * Sets the camera to a location, at an angle of rotation.
    *
    * @param x
    *           the location x component
    * @param y
    *           the location y component
    * @param radians
    *           the rotation
    */
   public void camera (
         final float x, final float y,
         final float radians ) {

      this.camera(x, y,
            radians,
            YupJ2.DEFAULT_ZOOM_X, YupJ2.DEFAULT_ZOOM_Y);
   }

   /**
    * Sets the camera to a location, rotation and zoom level.
    *
    * @param x
    *           the translation x
    * @param y
    *           the translation y
    * @param radians
    *           the angle in radians
    * @param zx
    *           the zoom x
    * @param zy
    *           the zoom y
    * @see Utils#modRadians(float)
    */
   public void camera (
         final float x, final float y,
         final float radians,
         final float zx, final float zy ) {

      this.cameraX = x;
      this.cameraY = y;
      this.cameraRot = Utils.modRadians(radians);
      this.cameraZoomX = zx < PConstants.EPSILON ? 1.0f : zx;
      this.cameraZoomY = zy < PConstants.EPSILON ? 1.0f : zy;

      this.resetMatrix();
      this.applyMatrix(
            1.0f, 0.0f, this.width * 0.5f,
            0.0f, 1.0f, this.height * 0.5f);
      this.scale(this.cameraZoomX, -this.cameraZoomY);
      this.rotate(-radians);
      this.translate(-this.cameraX, -this.cameraY);
   }

   /**
    * Sets the camera to a location.
    *
    * @param loc
    *           the location
    */
   public void camera ( final Vec2 loc ) {

      this.camera(loc.x, loc.y,
            YupJ2.DEFAULT_ROT,
            YupJ2.DEFAULT_ZOOM_X, YupJ2.DEFAULT_ZOOM_Y);
   }

   /**
    * Sets the camera to a location, at an angle of rotation.
    *
    * @param loc
    *           the location
    * @param radians
    *           the angle
    */
   public void camera (
         final Vec2 loc,
         final float radians ) {

      this.camera(loc.x, loc.y,
            radians,
            YupJ2.DEFAULT_ZOOM_X, YupJ2.DEFAULT_ZOOM_Y);
   }

   /**
    * Sets the camera to a location, rotation and zoom level.
    *
    * @param loc
    *           the location
    * @param radians
    *           the angle
    * @param zoom
    *           the zoom level
    */
   public void camera (
         final Vec2 loc,
         final float radians,
         final Vec2 zoom ) {

      this.camera(
            loc.x, loc.y,
            radians,
            zoom.x, zoom.y);
   }

   /**
    * Draws a circle at a location
    *
    * @param a
    *           the coordinate
    * @param b
    *           the size
    */
   @Override
   public void circle ( final Vec2 a, final float b ) {

      this.circle(a.x, a.y, b);
   }

   /**
    * Sets the renderer's color mode. Color channel maximums
    * should be a positive value greater than or equal to one.
    *
    * @param mode
    *           the color mode, HSB or RGB
    * @param max1
    *           the first channel maximum, hue or red
    * @param max2
    *           the second channel maximum, saturation or green
    * @param max3
    *           the third channel maximum, brightness or blue
    * @param maxA
    *           the alpha channel maximum
    */
   @Override
   public void colorMode (
         final int mode,
         final float max1,
         final float max2,
         final float max3,
         final float maxA ) {

      super.colorMode(mode,
            Utils.max(Utils.abs(max1), 1.0f),
            Utils.max(Utils.abs(max2), 1.0f),
            Utils.max(Utils.abs(max3), 1.0f),
            Utils.max(Utils.abs(maxA), 1.0f));

      this.invColorModeX = 1.0f / this.colorModeX;
      this.invColorModeY = 1.0f / this.colorModeY;
      this.invColorModeZ = 1.0f / this.colorModeZ;
      this.invColorModeA = 1.0f / this.colorModeA;
   }

   /**
    * Draws a curve between four points.
    *
    * @param a
    *           the first point
    * @param b
    *           the second point
    * @param c
    *           the third point
    * @param d
    *           the fourth point
    */
   @Override
   public void curve (
         final Vec2 a, final Vec2 b,
         final Vec2 c, final Vec2 d ) {

      this.curve(
            a.x, a.y,
            b.x, b.y,
            c.x, c.y,
            d.x, d.y);
   }

   /**
    * Draws a curve segment.
    *
    * @param a
    *           the coordinate
    */
   @Override
   public void curveVertex ( final Vec2 a ) {

      this.curveVertex(a.x, a.y);
   }

   /**
    * Draws an ellipse; the meaning of the two parameters
    * depends on the renderer's ellipseMode.
    *
    * @param a
    *           the first parameter
    * @param b
    *           the second parameter
    */
   @Override
   public void ellipse ( final Vec2 a, final Vec2 b ) {

      this.ellipse(
            a.x, a.y,
            b.x, b.y);
   }

   /**
    * Gets this renderer's background color.
    *
    * @return the background color
    */
   @Override
   public int getBackground () {

      return this.backgroundColor;
   }

   /**
    * Gets this renderer's background color.
    *
    * @param target
    *           the output color
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
   public float getHeight () {

      return this.height;
   }

   /**
    * Gets the renderer camera's location.
    *
    * @param target
    *           the output vector
    * @return the location
    */
   @Override
   public Vec2 getLoc ( final Vec2 target ) {

      return target.set(this.cameraX, this.cameraY);
   }

   /**
    * Gets the renderer camera's location on the x axis.
    *
    * @return the x location
    */
   @Override
   public float getLocX () {

      return this.cameraX;
   }

   /**
    * Gets the renderer camera's location on the y axis.
    *
    * @return the y location
    */
   @Override
   public float getLocY () {

      return this.cameraY;
   }

   /**
    * Gets the renderer's parent applet.
    *
    * @return the applet
    */
   @Override
   public PApplet getParent () {

      return this.parent;
   }

   /**
    * Gets the renderer camera's rotation.
    *
    * @return the rotation
    */
   @Override
   public float getRot () {

      return this.cameraRot;
   }

   /**
    * Gets the renderer's size.
    *
    * @param target
    *           the output vector
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
   public float getWidth () {

      return this.width;
   }

   /**
    * Gets the renderer's zoom.
    *
    * @param target
    *           the output vector
    * @return the zoom
    */
   @Override
   public Vec2 getZoom ( final Vec2 target ) {

      return target.set(this.cameraZoomX, this.cameraZoomY);
   }

   /**
    * Gets the renderer's horizontal zoom.
    *
    * @return the zoom on the horizontal axis
    */
   @Override
   public float getZoomX () {

      return this.cameraZoomX;
   }

   /**
    * Gets the renderer's vertical zoom.
    *
    * @return the zoom on the vertical axis
    */
   @Override
   public float getZoomY () {

      return this.cameraZoomY;
   }

   /**
    * Displays the handles of a curve entity.
    *
    * @param ce
    *           the curve entity
    */
   public void handles ( final CurveEntity2 ce ) {

      this.handles(ce, 1.0f);
   }

   /**
    * Displays the handles of a curve entity.
    *
    * @param ce
    *           the curve entity
    * @param strokeWeight
    *           the stroke weight
    */
   public void handles (
         final CurveEntity2 ce,
         final float strokeWeight ) {

      this.handles(ce, strokeWeight,
            IUp.DEFAULT_HANDLE_COLOR,
            IUp.DEFAULT_HANDLE_REAR_COLOR,
            IUp.DEFAULT_HANDLE_FORE_COLOR,
            IUp.DEFAULT_HANDLE_COORD_COLOR);
   }

   /**
    * Displays the handles of a curve entity.
    *
    * @param ce
    *           the curve entity
    * @param strokeWeight
    *           the stroke weight
    * @param lineColor
    *           the color of handle lines
    * @param rearColor
    *           the color of the rear handle
    * @param foreColor
    *           the color of the fore handle
    * @param coordColor
    *           the color of the coordinate
    */
   public void handles (
         final CurveEntity2 ce,
         final float strokeWeight,
         final int lineColor,
         final int rearColor,
         final int foreColor,
         final int coordColor ) {

      this.pushStyle();
      this.pushMatrix();
      this.transform(ce.transform, ce.transformOrder);

      final float swRear = strokeWeight * 4.0f;
      final float swFore = swRear * 1.25f;
      final float swCoord = swFore * 1.25f;

      final LinkedList < Curve2 > curves = ce.curves;
      for (final Curve2 curve : curves) {
         for (final Knot2 knot : curve) {
            final Vec2 coord = knot.coord;
            final Vec2 foreHandle = knot.foreHandle;
            final Vec2 rearHandle = knot.rearHandle;

            this.strokeWeight(strokeWeight);
            this.stroke(lineColor);
            this.line(
                  rearHandle.x, rearHandle.y,
                  coord.x, coord.y);
            this.line(
                  coord.x, coord.y,
                  foreHandle.x, foreHandle.y);

            this.strokeWeight(swRear);
            this.stroke(rearColor);
            this.point(rearHandle.x, rearHandle.y);

            this.strokeWeight(swCoord);
            this.stroke(coordColor);
            this.point(coord.x, coord.y);

            this.strokeWeight(swFore);
            this.stroke(foreColor);
            this.point(foreHandle.x, foreHandle.y);
         }
      }
      this.popMatrix();
      this.popStyle();
   }

   /**
    * Displays a PImage at a location. Uses the image's width
    * and height as the second parameters.
    *
    * @param img
    *           the PImage
    * @param x
    *           the first x coordinate
    * @param y
    *           the first y coordinate
    */
   @Override
   public void image ( final PImage img,
         final float x, final float y ) {

      this.image(img,
            x, y,
            img.width, img.height);
   }

   /**
    * Displays a PImage. The meaning of the first four
    * parameters depends on imageMode.
    *
    * @param img
    *           the PImage
    * @param x
    *           the first x coordinate
    * @param y
    *           the first y coordinate
    * @param u
    *           the second x coordinate
    * @param v
    *           the second y coordinate
    */
   @Override
   public void image ( final PImage img,
         final float x, final float y,
         final float u, final float v ) {

      int vtx = 1;
      int vty = 1;
      if (this.textureMode == PConstants.IMAGE) {
         vtx = img.width;
         vty = img.height;
      }

      this.image(img,
            x, y,
            u, v,
            0, 0,
            vtx, vty);
   }

   /**
    * Displays a PImage. The meaning of the first four
    * parameters depends on imageMode.
    *
    * @param img
    *           the PImage
    * @param a
    *           the first x coordinate
    * @param b
    *           the first y coordinate
    * @param c
    *           the second x coordinate
    * @param d
    *           the second y coordinate
    * @param u1
    *           the image top-left corner u
    * @param v1
    *           the image top-left corner v
    * @param u2
    *           the imag bottom-right corner u
    * @param v2
    *           the imag bottom-right cornver v
    */
   @Override
   public void image ( final PImage img,
         final float a, final float b,
         final float c, final float d,

         final int u1, final int v1,
         final int u2, final int v2 ) {

      if (img.width < 2 || img.height < 2) {
         return;
      }

      float xTopLeft = 0.0f;
      float yTopLeft = 0.0f;
      float xBottomRight = 0.0f;
      float yBottomRight = 0.0f;
      float wHalf = 0.0f;
      float hHalf = 0.0f;

      switch (this.imageMode) {
         case CORNERS:
            xTopLeft = Utils.min(a, c);
            xBottomRight = Utils.max(a, c);

            yTopLeft = Utils.max(b, d);
            yBottomRight = Utils.min(b, d);

            break;

         case CENTER:
            wHalf = Utils.abs(c) * 0.5f;
            hHalf = Utils.abs(d) * 0.5f;

            xTopLeft = a - wHalf;
            xBottomRight = a + wHalf;

            yTopLeft = b + hHalf;
            yBottomRight = b - hHalf;

            break;

         case RADIUS:
            wHalf = Utils.abs(c);
            hHalf = Utils.abs(d);

            xTopLeft = a - wHalf;
            xBottomRight = a + wHalf;

            yTopLeft = b + hHalf;
            yBottomRight = b - hHalf;

            break;

         case CORNER:
         default:
            xTopLeft = a;
            xBottomRight = a + Utils.abs(c);

            yTopLeft = b;
            yBottomRight = b - Utils.abs(d);
      }

      this.imageImpl(img,
            xTopLeft, yTopLeft,
            xBottomRight, yBottomRight,
            u1, v1, u2, v2);
   }

   /**
    * Returns whether or not the renderer is 2D.
    */
   @Override
   public boolean is2D () {

      return true;
   }

   /**
    * Returns whether or not the renderer is 3D.
    */
   @Override
   public boolean is3D () {

      return false;
   }

   /**
    * Eases from an origin color to a destination by a step.
    *
    * @param origin
    *           the origin color
    * @param dest
    *           the destination color
    * @param step
    *           the factor in [0, 1]
    * @param target
    *           the output color
    * @return the color
    */
   @Override
   public Color lerpColor (
         final Color origin, final Color dest,
         final float step, final Color target ) {

      switch (this.colorMode) {

         case HSB:

            return IUp.MIXER_HSB.apply(
                  origin, dest,
                  step,
                  target);

         case RGB:

         default:

            return IUp.MIXER_RGB.apply(
                  origin, dest,
                  step,
                  target);
      }
   }

   /**
    * Eases from an origin color to a destination by a step.
    *
    * @param origin
    *           the origin color
    * @param dest
    *           the destination color
    * @param step
    *           the factor in [0, 1]
    * @return the color
    */
   @Override
   public int lerpColor (
         final int origin, final int dest,
         final float step ) {

      return Color.toHexInt(
            this.lerpColor(
                  Color.fromHex(origin, this.aTemp),
                  Color.fromHex(dest, this.bTemp),
                  step,
                  this.cTemp));
   }

   /**
    * Draws a line between two coordinates.
    *
    * @param a
    *           the origin coordinate
    * @param b
    *           the destination coordinate
    */
   @Override
   public void line ( final Vec2 a, final Vec2 b ) {

      this.line(
            a.x, a.y,
            b.x, b.y);
   }

   /**
    * Sets the renderer's stroke, stroke weight and fill to the
    * material's.
    *
    * @param material
    *           the material
    */
   public void material ( final MaterialSolid material ) {

      if (material.useStroke) {
         this.strokeWeight(material.strokeWeight);
         this.stroke(material.stroke);
      } else {
         this.noStroke();
      }

      if (material.useFill) {
         this.fill(material.fill);
      } else {
         this.noFill();
      }
   }

   /**
    * Draws the world origin.
    */
   @Override
   public void origin () {

      this.origin(
            IUp.DEFAULT_IJK_LINE_FAC * Utils.min(this.width, this.height),
            IUp.DEFAULT_IJK_SWEIGHT,
            IUp.DEFAULT_I_COLOR,
            IUp.DEFAULT_J_COLOR);
   }

   /**
    * Draws the world origin.
    *
    * @param lineLength
    *           the line length
    */
   public void origin ( final float lineLength ) {

      this.origin(
            lineLength,
            IUp.DEFAULT_IJK_SWEIGHT,
            IUp.DEFAULT_I_COLOR,
            IUp.DEFAULT_J_COLOR);
   }

   /**
    * Draws the world origin.
    *
    * @param lineLength
    *           the line length
    * @param strokeWeight
    *           the stroke weight
    */
   public void origin (
         final float lineLength,
         final float strokeWeight ) {

      this.origin(lineLength,
            strokeWeight,
            IUp.DEFAULT_I_COLOR,
            IUp.DEFAULT_J_COLOR);
   }

   /**
    * Draws the world origin.
    *
    * @param lineLength
    *           the line length
    * @param strokeWeight
    *           the stroke weight
    * @param xColor
    *           the color of the x axis
    * @param yColor
    *           the color of the y axis
    */
   public void origin (
         final float lineLength,
         final float strokeWeight,
         final int xColor,
         final int yColor ) {

      this.pushStyle();

      this.strokeWeight(strokeWeight);
      this.stroke(xColor);
      this.line(
            0.0f, 0.0f,
            lineLength, 0.0f);

      this.stroke(yColor);
      this.line(
            0.0f, 0.0f,
            0.0f, lineLength);

      this.popStyle();
   }

   /**
    * Removes an affine transformation matrix off the end of
    * the stack. After popping the matrix, the camera's
    * settings are applied.
    */
   @Override
   public void popMatrix () {

      super.popMatrix();
      this.camera(
            this.cameraX, this.cameraY,
            this.cameraRot,
            this.cameraZoomX, this.cameraZoomY);
   }

   /**
    * Draws a quadrilateral between four points.
    *
    * @param x0
    *           the first point x
    * @param y0
    *           the first point y
    * @param x1
    *           the second point x
    * @param y1
    *           the second point y
    * @param x2
    *           the third point x
    * @param y2
    *           the third point y
    * @param x3
    *           the fourth point x
    * @param y3
    *           the fourth point y
    */
   @Override
   public void quad (
         final float x0, final float y0,
         final float x1, final float y1,
         final float x2, final float y2,
         final float x3, final float y3 ) {

      this.gp.reset();
      this.gp.moveTo(x0, y0);
      this.gp.lineTo(x1, y1);
      this.gp.lineTo(x2, y2);
      this.gp.lineTo(x3, y3);
      this.gp.closePath();
      this.drawShape(this.gp);
   }

   /**
    * Draws a quadrilateral between four points.
    *
    * @param a
    *           the first point
    * @param b
    *           the second point
    * @param c
    *           the third point
    * @param d
    *           the fourth point
    */
   @Override
   public void quad (
         final Vec2 a, final Vec2 b,
         final Vec2 c, final Vec2 d ) {

      this.quad(
            a.x, a.y,
            b.x, b.y,
            c.x, c.y,
            d.x, d.y);
   }

   /**
    * Draws a quadratic Bezier curve segment to the next anchor
    * point; the control point shapes the curve segment.
    *
    * @param cp
    *           the control point
    * @param ap1
    *           the next anchor point
    */
   @Override
   public void quadraticVertex (
         final Vec2 cp,
         final Vec2 ap1 ) {

      this.quadraticVertex(
            cp.x, cp.y,
            ap1.x, ap1.y);
   }

   /**
    * Draws a rectangle. The meaning of the four parameters
    * depends on rectMode.
    *
    * @param a
    *           the first x parameter
    * @param b
    *           the first y parameter
    * @param c
    *           the second x parameter
    * @param d
    *           the second y parameter
    */
   @Override
   public void rect (
         final float a, final float b,
         final float c, final float d ) {

      /**
       * It is much simpler to draw straight lines than to defer
       * to the rounded-corner rectImpl below.
       */

      float x0 = 0.0f;
      float y0 = 0.0f;
      float x1 = 0.0f;
      float y1 = 0.0f;

      float w = 0.0f;
      float h = 0.0f;

      switch (this.rectMode) {

         case CORNER:
            w = Utils.abs(c);
            h = Utils.abs(d);

            x0 = a;
            y0 = b - h;
            x1 = a + w;
            y1 = b;

            break;

         case CORNERS:
            x0 = Utils.min(a, c);
            x1 = Utils.max(a, c);

            y0 = Utils.min(b, d);
            y1 = Utils.max(b, d);

            break;

         case RADIUS:
            w = Utils.abs(c);
            h = Utils.abs(d);

            x0 = a - w;
            x1 = a + w;
            y0 = b + h;
            y1 = b - h;

            break;

         case CENTER:

         default:
            w = Utils.abs(c) * 0.5f;
            h = Utils.abs(d) * 0.5f;

            x0 = a - w;
            x1 = a + w;
            y0 = b + h;
            y1 = b - h;
      }

      this.gp.reset();
      this.gp.moveTo(x0, y0);
      this.gp.lineTo(x1, y0);
      this.gp.lineTo(x1, y1);
      this.gp.lineTo(x0, y1);
      this.gp.closePath();
      this.drawShape(this.gp);
   }

   /**
    * Draws a rounded rectangle. The meaning of the first four
    * parameters depends on rectMode.
    *
    * @param x1
    *           the first x parameter
    * @param y1
    *           the first y parameter
    * @param x2
    *           the second x parameter
    * @param y2
    *           the second y parameter
    * @param r
    *           the corner rounding
    */
   @Override
   public void rect (
         final float x1, final float y1,
         final float x2, final float y2,

         final float r ) {

      this.rectImpl(
            x1, y1, x2, y2,
            r, r, r, r);
   }

   /**
    * Draws a rounded rectangle. The meaning of the first four
    * parameters depends on rectMode.
    *
    * @param x1
    *           the first x parameter
    * @param y1
    *           the first y parameter
    * @param x2
    *           the second x parameter
    * @param y2
    *           the second y parameter
    * @param tl
    *           the top-left corner rounding
    * @param tr
    *           the top-right corner rounding
    * @param br
    *           the bottom-right corner rounding
    * @param bl
    *           the bottom-left corner rounding
    */
   @Override
   public void rect (
         final float x1, final float y1,
         final float x2, final float y2,

         final float tl, final float tr,
         final float br, final float bl ) {

      this.rectImpl(
            x1, y1, x2, y2,
            tl, tr, br, bl);
   }

   /**
    * Draws a rectangle; the meaning of the two parameters
    * depends on the renderer's rectMode.
    *
    * @param a
    *           the first parameter
    * @param b
    *           the second parameter
    */
   @Override
   public void rect ( final Vec2 a, final Vec2 b ) {

      this.rect(
            a.x, a.y,
            b.x, b.y);
   }

   /**
    * Resets the matrix to the identity -<br>
    * <br>
    * 1.0, 0.0, 0.0,<br>
    * 0.0, 1.0, 0.0,<br>
    * 0.0, 0.0, 1.0<br>
    * <br>
    * - then scales by the screen pixel density.
    *
    * @see AffineTransform#setToIdentity()
    * @see Graphics2D#setTransform(AffineTransform)
    * @see Graphics2D#scale(double, double)
    */
   @Override
   public void resetMatrix () {

      this.affineNative.setToIdentity();
      this.g2.setTransform(this.affineNative);
      this.g2.scale(this.pixelDensity, this.pixelDensity);
   }

   /**
    * Rotates the sketch by an angle in radians around the z
    * axis.
    *
    * @param angle
    *           the angle
    * @see Graphics2D#rotate(double)
    */
   @Override
   public void rotate ( final float angle ) {

      this.g2.rotate(angle);
   }

   /**
    * Rotates the sketch by an angle in radians around the x
    * axis. For 2D, this scales by ( 1.0, cos ( a ) ) .
    *
    * @param angle
    *           the angle
    * @see Graphics2D#scale(double, double)
    * @see PApplet#cos(float)
    */
   @Override
   public void rotateX ( final float angle ) {

      this.g2.scale(1.0f, PApplet.cos(angle));
   }

   /**
    * Rotates the sketch by an angle in radians around the y
    * axis. For 2D, this scales by ( cos ( a ), 1.0 ) .
    *
    * @param angle
    *           the angle
    * @see Graphics2D#scale(double, double)
    * @see PApplet#cos(float)
    */
   @Override
   public void rotateY ( final float angle ) {

      this.g2.scale(PApplet.cos(angle), 1.0f);
   }

   /**
    * Rotates the sketch by an angle in radians around the z
    * axis.
    *
    * @param angle
    *           the angle
    * @see Graphics2D#rotate(double)
    */
   @Override
   public void rotateZ ( final float angle ) {

      this.g2.rotate(angle);
   }

   /**
    * Scales the renderer by a dimension.
    *
    * @param dim
    *           the dimensions
    * @see Graphics2D#scale(double, double)
    */
   public void scale ( final Vec2 dim ) {

      this.g2.scale(dim.x, dim.y);
   }

   /**
    * Sets the renderer's affine transform matrix to the
    * supplied arguments.
    *
    * @param m00
    *           right axis x
    * @param m01
    *           up axis x
    * @param m02
    *           translation x
    * @param m10
    *           right axis y
    * @param m11
    *           up axis y
    * @param m12
    *           translation y
    */
   public void setMatrix (
         final float m00, final float m01, final float m02,
         final float m10, final float m11, final float m12 ) {

      this.affineNative.setTransform(
            m00, m01, m02,
            m10, m11, m12);
      this.g2.setTransform(this.affineNative);
   }

   /**
    * Sets the renderer matrix.
    *
    * @param source
    *           a 3 x 3 matrix
    * @see AffineTransform#setTransform(double, double, double,
    *      double, double, double)
    * @see Graphics2D#setTransform(AffineTransform)
    */
   @Override
   public void setMatrix ( final PMatrix2D source ) {

      this.setMatrix(
            source.m00, source.m01, source.m02,
            source.m10, source.m11, source.m12);
   }

   /**
    * Set size is the last function called by size,
    * createGraphics, makeGraphics, etc. when initializing the
    * graphics renderer. Therefore, any additional values that
    * need initialization can be attempted here.
    *
    * @param width
    *           the applet width
    * @param height
    *           the applet height
    */
   @Override
   public void setSize ( final int width, final int height ) {

      this.width = width;
      this.height = height;

      this.pixelWidth = this.width * this.pixelDensity;
      this.pixelHeight = this.height * this.pixelDensity;

      this.reapplySettings = true;
   }

   /**
    * Draws a curve entity.
    *
    * @param entity
    *           the curve entity
    */
   // public void shape ( final CurveEntity2 entity ) {
   //
   // this.pushMatrix();
   // this.transform(entity.transform, entity.transformOrder);
   //
   // Knot2 currKnot;
   // Knot2 prevKnot;
   // Vec2 coord;
   // Vec2 foreHandle;
   // Vec2 rearHandle;
   //
   // final LinkedList < Curve2 > curves = entity.curves;
   // final LinkedList < MaterialSolid > materials =
   // entity.materials;
   // final boolean useMaterial = !materials.isEmpty();
   //
   // curveLoop: for (final Curve2 curve : curves) {
   //
   // final int knotLength = curve.knotCount();
   // if (knotLength < 2) {
   // continue curveLoop;
   // }
   //
   // if (useMaterial) {
   // final int index = curve.materialIndex;
   // final MaterialSolid material = materials.get(index);
   // this.pushStyle();
   // this.material(material);
   // }
   //
   // int end = 0;
   // if (curve.closedLoop) {
   // end = knotLength + 1;
   // } else {
   // end = knotLength;
   // }
   //
   // prevKnot = curve.get(0);
   // coord = prevKnot.coord;
   // // TODO: Simplify by adding an extra bezier vertex call
   // // in the subsequent if loop and NOT using the modulo
   // // in the for loop.
   // this.gp.reset();
   // this.gp.moveTo(coord.x, coord.y);
   //
   // for (int i = 1; i < end; ++i) {
   // currKnot = curve.get(i % knotLength);
   //
   // foreHandle = prevKnot.foreHandle;
   // rearHandle = currKnot.rearHandle;
   // coord = currKnot.coord;
   //
   // this.gp.curveTo(
   // foreHandle.x, foreHandle.y,
   // rearHandle.x, rearHandle.y,
   // coord.x, coord.y);
   //
   // prevKnot = currKnot;
   // }
   //
   // if (curve.closedLoop) {
   // this.gp.closePath();
   // }
   // this.drawShape(this.gp);
   //
   // if (useMaterial) {
   // this.popStyle();
   // }
   // }
   // this.popMatrix();
   // }

   public void shape ( final CurveEntity2 entity ) {

      // TODO: Needs fixing...
      final LinkedList < Curve2 > curves = entity.curves;
      final LinkedList < MaterialSolid > materials = entity.materials;
      final boolean useMaterial = !materials.isEmpty();

      final Transform2 tr = entity.transform;
      final Vec2 v0 = new Vec2();
      final Vec2 v1 = new Vec2();
      final Vec2 v2 = new Vec2();

      Knot2 currKnot;
      Knot2 prevKnot;
      Vec2 coord;
      Vec2 foreHandle;
      Vec2 rearHandle;

      for (final Curve2 curve : curves) {

         if (useMaterial) {
            this.pushStyle();
            this.material(materials.get(
                  curve.materialIndex));
         }
         final Iterator < Knot2 > itr = curve.iterator();
         prevKnot = itr.next();
         coord = prevKnot.coord;
         Transform2.multPoint(tr, coord, v2);
         this.gp.reset();
         this.gp.moveTo(v2.x, v2.y);

         while (itr.hasNext()) {
            currKnot = itr.next();
            foreHandle = prevKnot.foreHandle;
            rearHandle = currKnot.rearHandle;
            coord = currKnot.coord;

            Transform2.multPoint(tr, foreHandle, v0);
            Transform2.multPoint(tr, rearHandle, v1);
            Transform2.multPoint(tr, coord, v2);

            this.gp.curveTo(
                  v0.x, v0.y,
                  v1.x, v1.y,
                  v2.x, v2.y);

            prevKnot = currKnot;
         }

         if (curve.closedLoop) {
            currKnot = curve.getFirst();
            foreHandle = prevKnot.foreHandle;
            rearHandle = currKnot.rearHandle;
            coord = currKnot.coord;

            Transform2.multPoint(tr, foreHandle, v0);
            Transform2.multPoint(tr, rearHandle, v1);
            Transform2.multPoint(tr, coord, v2);

            this.gp.curveTo(
                  v0.x, v0.y,
                  v1.x, v1.y,
                  v2.x, v2.y);
            this.gp.closePath();
         }

         this.drawShape(this.gp);

         if (useMaterial) {
            this.popStyle();
         }
      }
   }

   /**
    * Draws a mesh entity.
    *
    * @param entity
    *           the mesh entity
    */
   public void shape ( final MeshEntity2 entity ) {

      this.pushMatrix();
      this.transform(entity.transform, entity.transformOrder);

      final LinkedList < Mesh2 > meshes = entity.meshes;
      final LinkedList < MaterialSolid > materials = entity.materials;
      final boolean useMaterial = !materials.isEmpty();

      for (final Mesh2 mesh : meshes) {
         if (useMaterial) {
            final int index = mesh.materialIndex;
            final MaterialSolid material = materials.get(index);
            this.pushStyle();
            this.material(material);
         }

         final int[][][] fs = mesh.faces;
         final Vec2[] vs = mesh.coords;
         final int flen0 = fs.length;

         for (int i = 0; i < flen0; ++i) {
            final int[][] f = fs[i];
            final int flen1 = f.length;

            this.gp.reset();
            Vec2 v = vs[f[0][0]];
            this.gp.moveTo(v.x, v.y);
            for (int j = 1; j < flen1; ++j) {
               v = vs[f[j][0]];
               this.gp.lineTo(v.x, v.y);
            }
            this.gp.closePath();
            this.drawShape(this.gp);
         }

         if (useMaterial) {
            this.popStyle();
         }
      }

      this.popMatrix();
   }

   /**
    * Displays a PShape. Use of this function is discouraged by
    * this renderer. See mesh and curve entities instead.
    *
    * @param shape
    *           the PShape
    * @param x
    *           the x coordinate
    * @param y
    *           the y coordinate
    * @see YupJ2#shape(CurveEntity2)
    * @see YupJ2#shape(MeshEntity2)
    */
   @Override
   public void shape (
         final PShape shape,
         final float x, final float y ) {

      PApplet.showMissingWarning("shape");
      super.shape(shape, x, y);
   }

   /**
    * Displays a PShape. The meaning of the four parameters
    * depends on shapeMode. Use of this function is discouraged
    * by this renderer. See mesh and curve entities instead.
    *
    * @param shape
    *           the PShape
    * @param x1
    *           the first x coordinate
    * @param y1
    *           the first y coordinate
    * @param x2
    *           the second x coordinate
    * @param y2
    *           the second y coordinate
    * @see YupJ2#shape(CurveEntity2)
    * @see YupJ2#shape(MeshEntity2)
    */
   @Override
   public void shape (
         final PShape shape,
         final float x1, final float y1,
         final float x2, final float y2 ) {

      PApplet.showMissingWarning("shape");
      super.shape(shape, x1, y1, x2, y2);
   }

   /**
    * Draws a square at a location.
    *
    * @param a
    *           the location
    * @param b
    *           the size
    */
   @Override
   public void square ( final Vec2 a, final float b ) {

      this.square(a.x, a.y, b);
   }

   /**
    * Displays a boolean as text at a location.
    *
    * @param bool
    *           the boolean
    * @param x
    *           the x coordinate
    * @param y
    *           the y coordinate
    */
   public void text (
         final boolean bool,
         final float x, final float y ) {

      this.text(String.valueOf(bool), x, y);
   }

   /**
    * Displays a character at a location.
    *
    * @param character
    *           the character
    * @param x
    *           the x coordinate
    * @param y
    *           the y coordinate
    */
   @Override
   public void text (
         final char character,
         final float x, float y ) {

      if (this.textFont == null) {
         this.defaultFontOrDeath("text");
      }

      switch (this.textAlignY) {

         case BOTTOM:
            y += this.textDescent();

            break;

         case TOP:
            y -= this.textAscent();

            break;

         case CENTER:

         default:
            y -= this.textAscent() * 0.5f;
      }

      this.textBuffer[0] = character;
      this.textLineAlignImpl(this.textBuffer, 0, 1, x, y);
   }

   /**
    * Displays a character at a 2D location, ignoring the z
    * coordinate.
    *
    * @param character
    *           the character
    * @param x
    *           the x coordinate
    * @param y
    *           the y coordinate
    * @param z
    *           the z coordinate
    */
   @Override
   public void text (
         final char character,
         final float x,
         final float y,
         final float z ) {

      PGraphics.showDepthWarningXYZ("text");
      this.text(character, x, y);
   }

   /**
    * Displays an array of characters at a location.
    *
    * @param chars
    *           the array of characters.
    * @param x
    *           the x coordinate
    * @param y
    *           the y coordinate
    */
   public void text (
         final char[] chars,
         final float x, final float y ) {

      this.text(chars, 0, chars.length, x, y);
   }

   /**
    * Displays an array of characters at a location.
    *
    * @param chars
    *           the array of characters
    * @param start
    *           the start index, inclusive
    * @param stop
    *           the stop index, exclusive
    * @param x
    *           the x coordinate
    * @param y
    *           the y coordinate
    */
   @Override
   public void text ( final char[] chars,
         int start, final int stop,
         final float x, float y ) {

      float high = 0;
      for (int i = start; i < stop; i++) {
         if (chars[i] == '\n') {
            high += this.textLeading;
         }
      }

      switch (this.textAlignY) {

         case BOTTOM:
            y += this.textDescent() + high;

            break;

         case TOP:
            y -= this.textAscent();

            break;

         case CENTER:

         default:
            y -= (this.textAscent() - high) * 0.5f;
      }

      int index = 0;
      while (index < stop) {
         if (chars[index] == '\n') {
            this.textLineAlignImpl(chars, start, index, x, y);
            start = index + 1;
            y -= this.textLeading;
         }
         index++;
      }

      if (start < stop) {
         this.textLineAlignImpl(chars, start, index, x, y);
      }
   }

   /**
    * Displays an array of characters at a 2D location. Ignores
    * the z coordinate.
    *
    * @param chars
    *           the array of characters
    * @param start
    *           the start place, inclusive
    * @param stop
    *           the stop place, exclusive
    * @param x
    *           the x coordinate
    * @param y
    *           the y coordinate
    * @param z
    *           the z coordinate
    */
   @Override
   public void text (
         final char[] chars,
         final int start, final int stop,
         final float x,
         final float y,
         final float z ) {

      PApplet.showDepthWarningXYZ("text");
      this.text(chars, start, stop, x, y);
   }

   /**
    * Displays a real number at a 2D location. Fixes the number
    * display to four decimal places.
    *
    * @param num
    *           the number
    * @param x
    *           the x coordinate
    * @param y
    *           the y coordinate
    */
   @Override
   public void text (
         final float num,
         final float x,
         final float y ) {

      this.text(Utils.toFixed(num, 4), x, y);
   }

   /**
    * Displays a real number at a 2D location, ignoring the z
    * coordinate.
    *
    * @param num
    *           the number
    * @param x
    *           the x coordinate
    * @param y
    *           the y coordinate
    * @param z
    *           the z coordinate
    */
   @Override
   public void text (
         final float num,
         final float x,
         final float y,
         final float z ) {

      PGraphics.showDepthWarningXYZ("text");
      this.text(num, x, y);
   }

   /**
    * Displays an integer at a 2D location, ignoring the z
    * coordinate.
    *
    * @param num
    *           the number
    * @param x
    *           the x coordinate
    * @param y
    *           the y coordinate
    * @param z
    *           the z coordinate
    */
   @Override
   public void text (
         final int num,
         final float x,
         final float y,
         final float z ) {

      PGraphics.showDepthWarningXYZ("text");
      this.text(num, x, y);
   }

   @Override
   public void text (
         final String str,
         final float x,
         final float y ) {

      this.text(str.toCharArray(), x, y);
   }

   /**
    * Displays a string of text at a 2D location, ignoring the
    * z coordinate.
    *
    * @param str
    *           the string
    * @param x
    *           the x coordinate
    * @param y
    *           the y coordinate
    * @param z
    *           the z coordinate
    */
   @Override
   public void text (
         final String str,
         final float x,
         final float y,
         final float z ) {

      PGraphics.showDepthWarningXYZ("text");
      this.text(str, x, y);
   }

   // @Override
   // protected void colorCalc ( final int argb ) {
   // this.calcColor = argb;
   // this.calcAi = argb >> 0x18 & 0xff;
   // this.calcRi = argb >> 0x10 & 0xff;
   // this.calcGi = argb >> 0x8 & 0xff;
   // this.calcBi = argb & 0xff;
   // this.calcA = this.calcAi * IUtils.ONE_255;
   // this.calcR = this.calcRi * IUtils.ONE_255;
   // this.calcG = this.calcGi * IUtils.ONE_255;
   // this.calcB = this.calcBi * IUtils.ONE_255;
   // this.calcAlpha = this.calcAi != 255;
   // }

   // @Override
   // protected void colorCalc ( final int rgb, final float
   // alpha ) {
   // this.colorCalcARGB(rgb, alpha);
   // }

   /**
    * Displaying a string of text in a box is not supported by
    * this renderer. Defaults to another text function.
    *
    * @param str
    *           the string
    * @param x1
    *           the first x coordinate.
    * @param y1
    *           the first y coordinate.
    * @param x2
    *           the second x coordinate.
    * @param y2
    *           the second y coordinate.
    */
   @Override
   public void text (
         final String str,
         final float x1, final float y1,
         final float x2, final float y2 ) {

      PApplet.showMissingWarning("text");
      this.text(str, x1, y1);
   }

   /**
    * Sets the text mode to either MODEL or SHAPE.
    *
    * @param mode
    *           the text mode
    */
   @Override
   public void textMode ( final int mode ) {

      if (mode == PConstants.MODEL || mode == PConstants.SHAPE) {
         this.textMode = mode;
      }
   }

   /**
    * Texture mode is not supported by this renderer.
    *
    * @param mode
    *           the mode
    */
   @Override
   public void textureMode ( final int mode ) {

      PApplet.showMethodWarning("textureMode");
   }

   /**
    * Gets the String representation of the renderer as the
    * class's simple name.
    *
    * @return the string
    */
   @Override
   public String toString () {

      return this.getClass().getSimpleName();
   }

   /**
    * Applies a transform to the renderer's matrix.
    *
    * @param tr2
    *           the transform
    */
   public void transform ( final Transform2 tr2 ) {

      this.transform(tr2, IUp.DEFAULT_ORDER);
   }

   /**
    * Applies a transform to the renderer's matrix.
    *
    * @param tr2
    *           the transform
    * @param order
    *           the transform order
    */
   public void transform ( final Transform2 tr2,
         final Transform.Order order ) {

      final Vec2 dim = tr2.getScale(this.tr2Scale);
      final Vec2 loc = tr2.getLocation(this.tr2Loc);
      final float angle = tr2.getRotation();

      switch (order) {

         case RST:

            this.rotateZ(angle);
            this.scale(dim.x, dim.y);
            this.translate(loc.x, loc.y);

            return;

         case RTS:

            this.rotateZ(angle);
            this.translate(loc.x, loc.y);
            this.scale(dim.x, dim.y);

            return;

         case SRT:

            this.scale(dim.x, dim.y);
            this.rotateZ(angle);
            this.translate(loc.x, loc.y);

            return;

         case STR:

            this.scale(dim.x, dim.y);
            this.translate(loc.x, loc.y);
            this.rotateZ(angle);

            return;

         case TSR:

            this.translate(loc.x, loc.y);
            this.scale(dim.x, dim.y);
            this.rotateZ(angle);

            return;

         case TRS:

         default:

            this.translate(loc.x, loc.y);
            this.rotateZ(angle);
            this.scale(dim.x, dim.y);

            return;
      }
   }

   /**
    * Translates the renderer by a vector.
    *
    * @param v
    *           the vector
    * @see Graphics2D#translate(double, double)
    */
   public void translate ( final Vec2 v ) {

      this.g2.translate(v.x, v.y);
   }

   /**
    * Draws a triangle between three points.
    *
    * @param a
    *           the first point
    * @param b
    *           the second point
    * @param c
    *           the third point
    */
   @Override
   public void triangle (
         final Vec2 a,
         final Vec2 b,
         final Vec2 c ) {

      this.triangle(
            a.x, a.y,
            b.x, b.y,
            c.x, c.y);
   }

   /**
    * Adds another vertex to a shape between the beginShape and
    * endShape commands.
    *
    * @param v
    *           the coordinate
    */
   @Override
   public void vertex ( final Vec2 v ) {

      this.vertex(v.x, v.y);
   }
}

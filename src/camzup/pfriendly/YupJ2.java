package camzup.pfriendly;

import java.awt.BasicStroke;
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
import camzup.core.Mat3;
import camzup.core.Mat4;
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
import processing.core.PVector;

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
         1.0d, 0.0d,
         0.0d, 1.0d,
         0.0d, 0.0d);

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
    * Representation of a stroke cap in the native AWT library.
    *
    * @see BasicStroke
    */
   protected int capNative = BasicStroke.CAP_ROUND;

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
    * Representation of a stroke join in the native AWT
    * library.
    *
    * @see BasicStroke
    */
   protected int joinNative = BasicStroke.JOIN_ROUND;

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
         final int width,
         final int height,
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
         final float x,
         final float y,
         final float w,
         final float h,
         final float start,
         final float stop,
         final int mode ) {

      final float a = 1.0f - Utils.mod1(start * IUtils.ONE_TAU);
      final float b = 1.0f - Utils.mod1(stop * IUtils.ONE_TAU);
      final float c = 360.0f * a;
      final float d = 360.0f * Utils.mod1(b - a);

      int fillMode = Arc2D.PIE;
      int strokeMode = Arc2D.OPEN;

      switch (mode) {

         case PConstants.PIE:

            strokeMode = Arc2D.PIE;

            break;

         case PConstants.CHORD:

            fillMode = Arc2D.CHORD;
            strokeMode = Arc2D.CHORD;

            break;

         case PConstants.OPEN:

         default:

            fillMode = Arc2D.OPEN;

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
    * Converts a stroke cap PConstant to a BasicStroke
    * constant. Sets both fields.
    *
    * @param cap
    *           the processing constant
    * @see BasicStroke
    */
   protected void chooseStrokeCap ( final int cap ) {

      switch (cap) {

         case PROJECT:

            this.strokeCap = PConstants.PROJECT; /* 4 */
            this.capNative = BasicStroke.CAP_SQUARE; /* 2 */

            break;

         case SQUARE:

            this.strokeCap = PConstants.SQUARE; /* 1 */
            this.capNative = BasicStroke.CAP_BUTT; /* 0 */

            break;

         case ROUND:

         default:

            this.strokeCap = PConstants.ROUND; /* 2 */
            this.capNative = BasicStroke.CAP_ROUND; /* 1 */
      }
   }

   /**
    * Converts a stroke cap PConstant to a BasicStroke
    * constant. Sets both fields.
    *
    * @param cap
    *           the processing constant
    * @see BasicStroke
    */
   protected void chooseStrokeJoin ( final int join ) {

      switch (join) {

         case BEVEL:

            this.strokeJoin = PConstants.BEVEL; /* 32 */
            this.joinNative = BasicStroke.JOIN_BEVEL; /* 2 */

            break;

         case MITER:

            this.strokeJoin = PConstants.MITER; /* 8 */
            this.joinNative = BasicStroke.JOIN_MITER; /* 0 */

            break;

         case ROUND:

         default:

            this.strokeJoin = PConstants.ROUND; /* 2 */
            this.joinNative = BasicStroke.JOIN_ROUND; /* 1 */
      }
   }

   /**
    * Calculates the color channels from four input channels.
    * The manner in which the first three are interpreted
    * depends on color mode.
    *
    * For HSB color mode, the first channel, x, is interpreted
    * as a periodic, not a linear, value.
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

      this.calcG = Utils.clamp01(y * this.invColorModeY);
      this.calcB = Utils.clamp01(z * this.invColorModeZ);
      this.calcA = Utils.clamp01(a * this.invColorModeA);

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

            this.calcR = Utils.clamp01(x * this.invColorModeX);

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
    * Decomposes a hexadecimal color into RGBA channels. Two
    * versions of these channels are stored: the unsigned byte
    * values in the range [0, 255] and the decimal values in
    * [0.0, 1.0].
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

      this.setStroke(
            PConstants.ROUND,
            PConstants.ROUND,
            IUp.DEFAULT_STROKE_WEIGHT);
      this.stroke = true;

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
         final float a,
         final float b,
         final float c,
         final float d,
         float tl,
         float tr,
         float br,
         float bl ) {

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
    * For internal use to minimize the number of new
    * BasicStroke objects instantiated.
    *
    * @param strokeCap
    *           the stroke cap
    * @param strokeJoin
    *           the stroke join
    * @param strokeWeight
    *           the stroke weight
    * @see YupJ2#chooseStrokeCap(int)
    * @see YupJ2#chooseStrokeJoin(int)
    * @see YupJ2#strokeImpl()
    */
   protected void setStroke (
         final int strokeCap,
         final int strokeJoin,
         final float strokeWeight ) {

      /*
       * The lower bound of the stroke weight has to be < 1.0
       * because of stroke scaling issues.
       */

      this.strokeWeight = Utils.max(PConstants.EPSILON, strokeWeight);
      this.chooseStrokeCap(strokeCap);
      this.chooseStrokeJoin(strokeJoin);
      this.strokeImpl();
   }

   /**
    * Creates a new BasicStroke object from the stroke weight,
    * stroke cap (native), and stroke join (native), then sets
    * the AWT renderer's stroke with this object.
    *
    * Unlike Processing, strokeWeight, strokeCap and strokeJoin
    * are set together in the underlying API. Calls to
    * strokeImpl should be minimized in internal code.
    *
    * @see YupJ2#setStroke(int, int, float)
    * @see BasicStroke#BasicStroke(float, int, int)
    * @see Graphics2D#setStroke(java.awt.Stroke)
    */
   @Override
   protected void strokeImpl () {

      this.strokeObject = new BasicStroke(
            this.strokeWeight,
            this.capNative,
            this.joinNative);
      this.g2.setStroke(this.strokeObject);
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
    * @see PFont#getGlyph(char)
    * @see PFont#getSize()
    * @see YupJ2#textCharModelImpl(PImage, float, float, float,
    *      float, int, int)
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

      /*
       * Beware of unconventional method signature:
       *
       * m00: scale x, m10: shear y, m01: shear x, m11: scale y,
       * m02: trans x, m12: trans y .
       */
      this.affineNative.setTransform(
            m00, m10,
            m01, m11,
            m02, m12);
      this.g2.transform(this.affineNative);
   }

   /**
    * Applies an affine transform matrix to the current
    * renderer transform.
    *
    * @param source
    *           the source matrix
    */
   public void applyMatrix ( final Mat3 source ) {

      this.applyMatrix(
            source.m00, source.m01, source.m02,
            source.m10, source.m11, source.m12);
   }

   /**
    * Applies an affine transform matrix to the current
    * renderer transform.
    *
    * @param source
    *           the source matrix
    */
   @Override
   public void applyMatrix ( final PMatrix2D source ) {

      this.applyMatrix(
            source.m00, source.m01, source.m02,
            source.m10, source.m11, source.m12);
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
         final float start,
         final float stop,
         final int mode ) {

      this.arc(v.x, v.y, sz, sz, start, stop, mode);
   }

   /**
    * Calls the parent beginDraw method, then calls the camera.
    * Used so that camera does not have to be called in the PDE
    * to get the default.
    */
   @Override
   public void beginDraw () {

      super.beginDraw();
      this.camera();
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

   /**
    * Evaluates a Bezier curve at step t for points a, b, c and
    * d. The parameter t varies between 0 and 1; a and d are
    * points on the curve; b and c are the control points. This
    * can be done once with the x coordinates and a second time
    * with the y coordinates to get the location of a bezier
    * curve at t.
    *
    * @param a
    *           coordinate of first point on the curve
    * @param b
    *           coordinate of first control point
    * @param c
    *           coordinate of second control point
    * @param d
    *           coordinate of second point on the curve
    * @param t
    *           value between 0 and 1
    * @return the evaluation
    */
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

   /**
    * Calculates the tangent of a point on a Bezier curve.
    *
    * @param a
    *           coordinate of first point on the curve
    * @param b
    *           coordinate of first control point
    * @param c
    *           coordinate of second control point
    * @param d
    *           coordinate of second point on the curve
    * @param t
    *           value between 0 and 1
    * @return the evaluation
    */
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
   public void camera (
         final float x,
         final float y ) {

      this.camera(
            x, y,
            YupJ2.DEFAULT_ROT,
            YupJ2.DEFAULT_ZOOM_X,
            YupJ2.DEFAULT_ZOOM_Y);
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
         final float x,
         final float y,
         final float radians ) {

      this.camera(
            x, y,
            radians,
            YupJ2.DEFAULT_ZOOM_X,
            YupJ2.DEFAULT_ZOOM_Y);
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
      this.cameraZoomX = zx < Utils.EPSILON ? 1.0f : zx;
      this.cameraZoomY = zy < Utils.EPSILON ? 1.0f : zy;

      this.setMatrix(
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

      this.camera(
            loc.x, loc.y,
            YupJ2.DEFAULT_ROT,
            YupJ2.DEFAULT_ZOOM_X,
            YupJ2.DEFAULT_ZOOM_Y);
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

      this.camera(
            loc.x, loc.y,
            radians,
            YupJ2.DEFAULT_ZOOM_X,
            YupJ2.DEFAULT_ZOOM_Y);
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
   public void circle (
         final Vec2 a,
         final float b ) {

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

      /*
       * Cache the inverse of the color maximums so that color
       * channels can be scaled to the range [0.0, 1.0] later .
       */

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

   @Override
   public void ellipse ( final float x, final float y, final float w,
         final float h ) {

      float extapw = 0.0f;
      float extaph = 0.0f;
      float extcpw = 0.0f;
      float extcph = 0.0f;

      float right = 0.0f;
      float left = 0.0f;
      float top = 0.0f;
      float bottom = 0.0f;

      float xc = 0.0f;
      float yc = 0.0f;

      switch (this.ellipseMode) {

         case RADIUS:

            xc = x;
            yc = y;

            extapw = w;
            extcpw = 0.552125f * w;
            extaph = h;
            extcph = 0.552125f * h;

            break;

         case CORNER:

            extapw = 0.5f * w;
            extcpw = 0.276063f * w;
            extaph = 0.5f * h;
            extcph = 0.276063f * h;

            xc = x + extapw;
            yc = y - extaph;

            break;

         case CORNERS:

            final float wcalc = Utils.abs(w - x);
            final float hcalc = Utils.abs(h - y);

            xc = (x + w) * 0.5f;
            yc = (y + h) * 0.5f;

            extapw = 0.5f * wcalc;
            extcpw = 0.276063f * wcalc;
            extaph = 0.5f * hcalc;
            extcph = 0.276063f * hcalc;

            break;

         case CENTER:

         default:

            xc = x;
            yc = y;

            extapw = 0.5f * w;
            extcpw = 0.276063f * w;
            extaph = 0.5f * h;
            extcph = 0.276063f * h;

      }

      right = xc + extapw;
      left = xc - extapw;
      top = yc + extaph;
      bottom = yc - extaph;

      this.gp.reset();
      this.gp.moveTo(right, yc);
      this.gp.curveTo(
            right, yc + extcph,
            xc + extcpw, top,
            xc, top);
      this.gp.curveTo(
            xc - extcpw, top,
            left, yc + extcph,
            left, yc);
      this.gp.curveTo(
            left, yc - extcph,
            xc - extcpw, bottom,
            xc, bottom);
      this.gp.curveTo(
            xc + extcpw, bottom,
            right, yc - extcph,
            right, yc);
      this.gp.closePath();
      this.drawShape(this.gp);
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
    * Retrieves the renderer's matrix.
    *
    * @param target
    *           the output matrix
    * @return the renderer matrix
    */
   public Mat3 getMatrix ( final Mat3 target ) {

      final AffineTransform tr = this.g2.getTransform();
      return target.set(
            (float) tr.getScaleX(),
            (float) tr.getShearX(),
            (float) tr.getTranslateX(),
            (float) tr.getShearY(),
            (float) tr.getScaleY(),
            (float) tr.getTranslateY(),
            0.0f, 0.0f, 1.0f);
   }

   /**
    * Retrieves the renderer's matrix.
    *
    * @param target
    *           the output matrix
    * @return the renderer matrix
    */
   public Mat4 getMatrix ( final Mat4 target ) {

      final AffineTransform tr = this.g2.getTransform();
      return target.set(
            (float) tr.getScaleX(),
            (float) tr.getShearX(),
            0.0f,
            (float) tr.getTranslateX(),

            (float) tr.getShearY(),
            (float) tr.getScaleY(),
            0.0f,
            (float) tr.getTranslateY(),

            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f);

   }

   /**
    * Retrieves the renderer's matrix.
    *
    * @param target
    *           the output matrix
    * @return the renderer matrix
    */
   @Override
   public PMatrix2D getMatrix ( PMatrix2D target ) {

      return PMatAux.fromAwt(this.g2.getTransform(), target);
   }

   /**
    * Retrieves the renderer's matrix.
    *
    * @param target
    *           the output matrix
    * @return the renderer matrix
    */
   @Override
   public PMatrix3D getMatrix ( PMatrix3D target ) {

      return PMatAux.fromAwt(this.g2.getTransform(), target);
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
    * Draws a diagnostic grid out of points. Overrides the
    * default implementation because of the issue with drawing
    * points with SQUARE strokeCap.
    *
    * @param count
    *           number of points
    * @param strokeWeight
    *           stroke weight
    * @param dim
    *           the grid dimensions
    */
   @Override
   public void grid (
         final int count,
         final float strokeWeight,
         final float dim ) {

      final float right = dim * 0.5f;
      final float left = -right;
      final float top = dim * 0.5f;
      final float bottom = -top;
      final float toPercent = 1.0f / count;
      final int last = count + 1;
      final int ab = 0xff000080;

      final float[] xs = new float[last];
      final int[] reds = new int[last];
      for (int j = 0; j < last; ++j) {
         final float jPercent = j * toPercent;
         xs[j] = Utils.lerpUnclamped(left, right, jPercent);
         reds[j] = (int) (jPercent * 0xff + 0.5f) << 0x10;
      }

      this.strokeObject = new BasicStroke(
            Utils.max(PConstants.EPSILON, strokeWeight),
            BasicStroke.CAP_ROUND,
            this.joinNative);
      this.g2.setStroke(this.strokeObject);

      for (int i = 0; i < last; ++i) {
         final float iPercent = i * toPercent;
         final float y = Utils.lerpUnclamped(bottom, top, iPercent);
         final float yeps = y + PConstants.EPSILON;
         final int green = (int) (iPercent * 0xff + 0.5f) << 0x8;
         final int agb = ab | green;

         for (int j = 0; j < last; ++j) {
            this.stroke(agb | reds[j]);
            final float x = xs[j];
            this.line(x, yeps, x, y);
         }
      }

      this.strokeObject = new BasicStroke(
            this.strokeWeight,
            this.capNative,
            this.joinNative);
      this.g2.setStroke(this.strokeObject);
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

      /*
       * Any other way to make this more efficient given the
       * inefficiency of setting stroke caps, joins and weights?
       */

      this.pushStyle();
      this.strokeCap(PConstants.ROUND);
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

            final float rhx = rearHandle.x;
            final float rhy = rearHandle.y;

            final float cox = coord.x;
            final float coy = coord.y;

            final float fhx = foreHandle.x;
            final float fhy = foreHandle.y;

            this.strokeWeight(strokeWeight);
            this.stroke(lineColor);
            this.line(rhx, rhy, cox, coy);
            this.line(cox, coy, fhx, fhy);

            this.strokeWeight(swRear);
            this.stroke(rearColor);
            this.line(rhx + PConstants.EPSILON, rhy, rhx, rhy);

            this.strokeWeight(swCoord);
            this.stroke(coordColor);
            this.line(cox + PConstants.EPSILON, coy, cox, coy);

            this.strokeWeight(swFore);
            this.stroke(foreColor);
            this.line(fhx + PConstants.EPSILON, fhy, fhx, fhy);
         }
      }

      this.popMatrix();
      this.popStyle();
   }

   /**
    * Draws a buffer at the origin.
    *
    * @param buff
    *           the renderer
    */
   public void image ( final PGraphicsJava2D buff ) {

      if (buff.g2 != null) {
         this.image((PImage) buff);
      }
   }

   /**
    * Displays a buffer at a location. Uses the buffer's width
    * and height as the second parameters.
    *
    * @param buff
    *           the renderer
    * @param x
    *           the first x coordinate
    * @param y
    *           the first y coordinate
    */
   public void image ( final PGraphicsJava2D buff, final float x,
         final float y ) {

      if (buff.g2 != null) {
         this.image((PImage) buff, x, y);
      }
   }

   /**
    * Displays a buffer. The meaning of the first four
    * parameters depends on imageMode.
    *
    * @param buff
    *           the renderer
    * @param x
    *           the first x coordinate
    * @param y
    *           the first y coordinate
    * @param u
    *           the second x coordinate
    * @param v
    *           the second y coordinate
    */
   public void image ( final PGraphicsJava2D buff, final float x, final float y,
         final float u, final float v ) {

      if (buff.g2 != null) {
         this.image((PImage) buff, x, y, u, v);
      }
   }

   /**
    * Displays a buffer. The meaning of the first four
    * parameters depends on imageMode.
    *
    * @param buff
    *           the buffer
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
   public void image (
         final PGraphicsJava2D buff,
         final float a, final float b,
         final float c, final float d,

         final int u1, final int v1,
         final int u2, final int v2 ) {

      if (buff.g2 != null) {
         this.image((PImage) buff,
               a, b, c, d,
               u1, v1, u2, v2);
      }
   }

   /**
    * Draws a buffer at a given coordinate.
    *
    * @param buff
    *           the renderer
    * @param coord
    *           the coordinate
    */
   public void image (
         final PGraphicsJava2D buff,
         final Vec2 coord ) {

      if (buff.g2 != null) {
         this.image((PImage) buff, coord);
      }
   }

   /**
    * Draws a buffer at a given coordinate.
    *
    * @param buff
    *           the renderer
    * @param coord
    *           the coordinate
    * @param dim
    *           the dimension
    */
   public void image (
         final PGraphicsJava2D buff,
         final Vec2 coord,
         final Vec2 dim ) {

      if (buff.g2 != null) {
         this.image((PImage) buff, coord, dim);
      }
   }

   /**
    * Draws an image at the origin.
    *
    * @param img
    *           the image
    */
   @Override
   public void image ( final PImage img ) {

      this.image(img, 0.0f, 0.0f);
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
   public void image (
         final PImage img,
         final float x,
         final float y ) {

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
   public void image (
         final PImage img,
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
   public void image (
         final PImage img,
         final float a, final float b,
         final float c, final float d,

         final int u1, final int v1,
         final int u2, final int v2 ) {

      if (img.pixels == null || img.width < 2 || img.height < 2) {
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
    * Draws an image at a given coordinate.
    *
    * @param img
    *           the image
    * @param coord
    *           the coordinate
    */
   @Override
   public void image (
         final PImage img,
         final Vec2 coord ) {

      this.image(img, coord.x, coord.y);
   }

   /**
    * Draws an image at a given coordinate and dimension.
    *
    * @param img
    *           the image
    * @param coord
    *           the coordinate
    * @param dim
    *           the dimension
    */
   @Override
   public void image (
         final PImage img,
         final Vec2 coord,
         final Vec2 dim ) {

      this.image(img, coord.x, coord.y, dim.x, dim.y);
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
    * @param x1
    *           the origin x
    * @param y1
    *           the origin y
    * @param x2
    *           the destination x
    * @param y2
    *           the destination y
    */
   @Override
   public void line (
         final float x1,
         final float y1,
         final float x2,
         final float y2 ) {

      this.gp.reset();
      this.gp.moveTo(x1, y1);
      this.gp.lineTo(x2, y2);
      this.drawShape(this.gp);
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
    * Draws a point at the coordinate x, y. This is done by
    * drawing a line from (x, y) to ((x, y) + (epsilon,
    * epsilon)).
    *
    * When strokeCap is set to SQUARE, it is swapped to
    * PROJECT, then back to SQUARE.
    *
    * @param x
    *           the x coordinate
    * @param y
    *           the y coordinate
    */
   @Override
   public void point ( final float x, final float y ) {

      final boolean needSwap = this.capNative == BasicStroke.CAP_BUTT;
      if (needSwap) {

         this.strokeObject = new BasicStroke(
               this.strokeWeight,
               BasicStroke.CAP_SQUARE,
               this.joinNative);
         this.g2.setStroke(this.strokeObject);

         this.line(x, y, x + PConstants.EPSILON, y);

         this.strokeObject = new BasicStroke(
               this.strokeWeight,
               this.capNative,
               this.joinNative);
         this.g2.setStroke(this.strokeObject);

      } else {

         this.line(x, y, x + PConstants.EPSILON, y);
      }
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
    * This will cause jittering, particularly when a shape has
    * a stroke.
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
    * This will cause jittering, particularly when a shape has
    * a stroke.
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
    * Takes a two-dimensional x, y position and returns the
    * coordinate for where it will appear on a two-dimensional
    * screen.
    *
    * @param source
    *           the source coordinate
    * @param target
    *           the target coordinate
    * @return the screen coordinate
    */
   public Vec2 screen ( final Vec2 source, final Vec2 target ) {

      final AffineTransform tr = this.g2.getTransform();
      return target.set(
            (float) (tr.getScaleX() * source.x +
                  tr.getShearX() * source.y +
                  tr.getTranslateX()),

            (float) (tr.getShearY() * source.x +
                  tr.getScaleY() * source.y +
                  tr.getTranslateY()));

   }

   /**
    * Takes a two-dimensional x, y position and returns the x
    * value for where it will appear on a two-dimensional
    * screen.
    *
    * This is inefficient, use screen with a PVector or Vec2
    * instead.
    *
    * @param x
    *           the x coordinate
    * @param y
    *           the y coordinate
    * @return the screen x coordinate
    */
   @Override
   public float screenX ( final float x, final float y ) {

      final AffineTransform tr = this.g2.getTransform();
      return (float) (tr.getScaleX() * x +
            tr.getShearX() * y +
            tr.getTranslateX());
   }

   /**
    * Takes a two-dimensional x, y position and returns the y
    * value for where it will appear on a two-dimensional
    * screen.
    *
    * This is inefficient, use screen with a PVector or Vec2
    * instead.
    *
    * @param x
    *           the x coordinate
    * @param y
    *           the y coordinate
    * @return the screen y coordinate
    * @see YupJ2#screen(Vec2, Vec2)
    */
   @Override
   public float screenY ( final float x, final float y ) {

      final AffineTransform tr = this.g2.getTransform();
      return (float) (tr.getShearY() * x +
            tr.getScaleY() * y +
            tr.getTranslateY());
   }

   /**
    * Sets the renderer camera's location.
    *
    * @param v
    *           the vector
    */
   public void setLoc ( final Vec2 v ) {

      this.cameraX = v.x;
      this.cameraY = v.y;
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

      /*
       * Beware of unconventional method signature:
       *
       * m00: scale x, m10: shear y, m01: shear x, m11: scale y,
       * m02: trans x, m12: trans y .
       */
      this.affineNative.setTransform(
            m00, m10,
            m01, m11,
            m02, m12);
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
   public void setMatrix ( final Mat3 source ) {

      this.setMatrix(
            source.m00, source.m01, source.m02,
            source.m10, source.m11, source.m12);
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

      this.affineNative.setTransform(
            1.0d, 0.0d,
            0.0d, 1.0d,
            this.width * 0.5d,
            this.height * 0.5d);

      this.pixelWidth = this.width * this.pixelDensity;
      this.pixelHeight = this.height * this.pixelDensity;

      this.reapplySettings = true;
   }

   /**
    * Displays a curve entity.
    *
    * @param entity
    *           the entity
    */
   public void shape ( final CurveEntity2 entity ) {

      // FIXME: curve materials not showing??

      final LinkedList < Curve2 > curves = entity.curves;
      final LinkedList < MaterialSolid > materials = entity.materials;
      final boolean useMaterial = !materials.isEmpty();

      final Transform2 tr = entity.transform;
      final Vec2 v0 = new Vec2();
      final Vec2 v1 = new Vec2();
      final Vec2 v2 = new Vec2();

      Knot2 currKnot = null;
      Knot2 prevKnot = null;
      Vec2 coord = null;
      Vec2 foreHandle = null;
      Vec2 rearHandle = null;

      for (final Curve2 curve : curves) {

         if (useMaterial) {
            this.pushStyle();
            this.material(materials.get(
                  curve.materialIndex));
         }

         final Iterator < Knot2 > itr = curve.iterator();
         prevKnot = itr.next();
         coord = prevKnot.coord;

         Transform2.mulPoint(tr, coord, v2);

         this.gp.reset();
         this.gp.moveTo(v2.x, v2.y);

         while (itr.hasNext()) {

            currKnot = itr.next();
            foreHandle = prevKnot.foreHandle;
            rearHandle = currKnot.rearHandle;
            coord = currKnot.coord;

            Transform2.mulPoint(tr, foreHandle, v0);
            Transform2.mulPoint(tr, rearHandle, v1);
            Transform2.mulPoint(tr, coord, v2);

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

            Transform2.mulPoint(tr, foreHandle, v0);
            Transform2.mulPoint(tr, rearHandle, v1);
            Transform2.mulPoint(tr, coord, v2);

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
    * Sets the stroke cap on the ends of strokes with a
    * PConstant: SQUARE, PROJECT, and ROUND.
    *
    * @param cap
    *           the constant
    * @see YupJ2#chooseStrokeCap(int)
    * @see YupJ2#strokeImpl()
    */
   @Override
   public void strokeCap ( final int cap ) {

      this.chooseStrokeCap(cap);
      this.strokeImpl();
   }

   /**
    * Sets the stroke join on the ends of strokes with a
    * PConstant: BEVEL, MITER, and ROUND.
    *
    * @param join
    *           the constant
    * @see YupJ2#chooseStrokeJoin(int)
    * @see YupJ2#strokeImpl()
    */
   @Override
   public void strokeJoin ( final int join ) {

      this.chooseStrokeJoin(join);
      this.strokeImpl();
   }

   /**
    * Sets the width of the stroke used for lines, points, and
    * the border around shapes. The weight should be a
    * positive, non-zero value.
    *
    * @param weight
    *           the stroke weight
    * @see YupJ2#strokeImpl()
    */
   @Override
   public void strokeWeight ( final float weight ) {

      /*
       * The lower bound of the stroke weight has to be < 1.0
       * because of stroke scaling issues.
       */

      this.strokeWeight = Utils.max(PConstants.EPSILON, weight);
      this.strokeImpl();
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
         final float x,
         float y ) {

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
         final float x,
         final float y ) {

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
   public void text (
         final char[] chars,
         int start,
         final int stop,
         final float x,
         float y ) {

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

   /**
    * Displays a string at a coordinate.
    *
    * @param str
    *           the string
    * @param x
    *           the x coordinate
    * @param y
    *           the y coordinate
    */
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
    * Draws a triangle from three 2D coordinates.
    *
    * @param x1
    *           first corner x
    * @param y1
    *           first corner y
    * @param x2
    *           second corner x
    * @param y2
    *           second corner y
    * @param x3
    *           third corner x
    * @param y3
    *           third corner y
    */
   @Override
   public void triangle (
         final float x1, final float y1,
         final float x2, final float y2,
         final float x3, final float y3 ) {

      this.gp.reset();
      this.gp.moveTo(x1, y1);
      this.gp.lineTo(x2, y2);
      this.gp.lineTo(x3, y3);
      this.gp.closePath();
      this.drawShape(this.gp);
   }

   /**
    * Draws a triangle from three coordinates.
    *
    * @param a
    *           the first coordinate
    * @param b
    *           the second coordinate
    * @param c
    *           the third coordinate
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
    * Update the pixels[] buffer to the PGraphics image.
    *
    * The overriden functionality eliminates unnecessary
    * checks.
    */
   @Override
   public void updatePixels () {

      if (this.pixels != null) {
         this.getRaster().setDataElements(0, 0, this.pixelWidth,
               this.pixelHeight, this.pixels);
      }
      this.modified = true;
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

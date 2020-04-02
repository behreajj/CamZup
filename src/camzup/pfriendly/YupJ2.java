package camzup.pfriendly;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;
import java.util.Iterator;
import java.util.List;

import processing.awt.PGraphicsJava2D;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PMatrix2D;
import processing.core.PMatrix3D;
import processing.core.PShape;

import camzup.core.Color;
import camzup.core.Curve2;
import camzup.core.CurveEntity2;
import camzup.core.Experimental;
import camzup.core.IUtils;
import camzup.core.Knot2;
import camzup.core.Mat3;
import camzup.core.Mat4;
import camzup.core.MaterialSolid;
import camzup.core.Mesh2;
import camzup.core.MeshEntity2;
import camzup.core.Transform2;
import camzup.core.TransformOrder;
import camzup.core.Utils;
import camzup.core.Vec2;

/**
 * A 2D renderer based on the Java AWT (Abstract Window Toolkit).
 * Supposes that the the camera is looking down on a 2D plane from the
 * z axis, making (0.0, 1.0) the forward -- or up -- axis.
 */
public class YupJ2 extends PGraphicsJava2D implements IYup2, ITextDisplay2 {

  /**
   * A Java AWT affine transform object. This is cached so a new object
   * is not created when accessing or mutating the renderer matrix.
   */
  protected AffineTransform affineNative = new AffineTransform(
      1.0d, 0.0d,
      0.0d, 1.0d,
      0.0d, 0.0d);

  /**
   * A Java AWT arc object. This uses double precision, as Arc2D.Float
   * simply casts between float and double anyway.
   */
  protected final Arc2D.Double arc = new Arc2D.Double();

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
   * A Java AWT general path object. This is reset when a new shape
   * needs to be displayed in draw.
   */
  protected final Path2D.Double gp = new Path2D.Double();

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
   * Representation of a stroke join in the native AWT library.
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
  public float cameraRot = IYup2.DEFAULT_ROT;

  /**
   * The camera's location on the x axis.
   */
  public float cameraX = IUp.DEFAULT_LOC_X;

  /**
   * The camera's location on the y axis.
   */
  public float cameraY = IUp.DEFAULT_LOC_Y;

  /**
   * The camera horizontal zoom.
   */
  public float cameraZoomX = IYup2.DEFAULT_ZOOM_X;

  /**
   * The camera vertical zoom.
   */
  public float cameraZoomY = IYup2.DEFAULT_ZOOM_Y;

  /**
   * The miter limit supplied to the basic stroke.
   */
  public float miterLimit = 1.0f;

  /**
   * The default constructor.
   */
  public YupJ2 ( ) { super(); }

  /**
   * A constructor for manually initializing the renderer.
   *
   * @param width     renderer width
   * @param height    renderer height
   * @param parent    parent applet
   * @param path      applet path
   * @param isPrimary is the renderer primary
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
   * The arc implementation. The underlying Java AWT arc asks for a
   * start angle and an arc length, not a stop angle, in degrees, not
   * radians.
   *
   * @param x          the arc location x
   * @param y          the arc location y
   * @param w          the arc width
   * @param h          the arc height
   * @param startAngle the start angle
   * @param stopAngle  the stop angle
   * @param arcMode    the arc mode
   * @see Utils#mod1(double)
   * @see Graphics2D#setColor(java.awt.Color)
   * @see Graphics2D#fill(Shape)
   * @see Graphics2D#draw(Shape)
   */
  @Override
  protected void arcImpl (
      final float x,
      final float y,
      final float w,
      final float h,
      final float startAngle,
      final float stopAngle,
      final int arcMode ) {

    /*
     * The Arc2D object uses double precision real numbers -- regardless
     * of whether the "Float" version is used or not. So if nothing else,
     * using doubles spares (float) casts.
     */
    final double a = 1.0d - Utils.mod1(startAngle * IUtils.ONE_TAU_D);
    final double b = 1.0d - Utils.mod1(stopAngle * IUtils.ONE_TAU_D);
    final double c = 360.0d * b;
    final double d = 360.0d * Utils.mod1(a - b);

    int fillMode = Arc2D.PIE;
    int strokeMode = Arc2D.OPEN;

    switch ( arcMode ) {

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

    if ( this.fill ) {
      this.arc.setArc(x, y, w, h, c, d, fillMode);
      this.g2.setColor(this.fillColorObject);
      this.g2.fill(this.arc);
    }

    if ( this.stroke ) {
      this.arc.setArc(x, y, w, h, c, d, strokeMode);
      this.g2.setColor(this.strokeColorObject);
      this.g2.draw(this.arc);
    }
  }

  /**
   * Sets background color channels in both single precision real number
   * [0.0, 1.0], in byte [0, 255] and in a composite color. Calls
   * {@link PGraphicsJava2D#backgroundImpl()} .
   */
  @Override
  protected void backgroundFromCalc ( ) {

    this.backgroundR = this.calcR;
    this.backgroundG = this.calcG;
    this.backgroundB = this.calcB;
    this.backgroundA = this.format == PConstants.RGB ? 1.0f : this.calcA;

    this.backgroundRi = this.calcRi;
    this.backgroundGi = this.calcGi;
    this.backgroundBi = this.calcBi;
    this.backgroundAi = this.format == PConstants.RGB ? 255 : this.calcAi;
    this.backgroundAlpha = this.format == PConstants.RGB ? false
        : this.calcAlpha;
    this.backgroundColor = this.calcColor;

    this.backgroundImpl();
  }

  /**
   * Converts a stroke cap PConstant to a BasicStroke constant. Sets
   * both fields.
   *
   * @param cap the processing constant
   * @see BasicStroke
   */
  protected void chooseStrokeCap ( final int cap ) {

    switch ( cap ) {

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
   * Converts a stroke cap PConstant to a BasicStroke constant. Sets
   * both fields.
   *
   * @param cap the processing constant
   * @see BasicStroke
   */
  protected void chooseStrokeJoin ( final int join ) {

    switch ( join ) {

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
   * Calculates the color channels from a color object.
   *
   * @param c the color
   * @see Utils#clamp01(float)
   */
  protected void colorCalc ( final Color c ) {

    this.calcR = c.x < 0.0f ? 0.0f : c.x > 1.0f ? 1.0f : c.x;
    this.calcG = c.y < 0.0f ? 0.0f : c.y > 1.0f ? 1.0f : c.y;
    this.calcB = c.z < 0.0f ? 0.0f : c.z > 1.0f ? 1.0f : c.z;
    this.calcA = c.w < 0.0f ? 0.0f : c.w > 1.0f ? 1.0f : c.w;

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
   * Calculates the color channels from four input channels. The manner
   * in which the first three are interpreted depends on color mode.
   *
   * For HSB color mode, the first channel, x, is interpreted as a
   * periodic, not a linear, value.
   *
   * @param x the first color channel, hue or red
   * @param y the second color channel, saturation or green
   * @param z the third color channel, brightness or blue
   * @param w the alpha channel
   * @see Utils#clamp01(float)
   * @see Color#hsbaToRgba(float, float, float, float, Color)
   */
  @Override
  protected void colorCalc (
      final float x,
      final float y,
      final float z,
      final float w ) {

    this.calcG = Utils.clamp01(y * this.invColorModeY);
    this.calcB = Utils.clamp01(z * this.invColorModeZ);
    this.calcA = Utils.clamp01(w * this.invColorModeA);

    switch ( this.colorMode ) {

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
   * Decomposes a hexadecimal color into RGBA channels. Two versions of
   * these channels are stored: the unsigned byte values in the range
   * [0, 255] and the decimal values in [0.0, 1.0].
   *
   * @param argb  the color in hexadecimal
   * @param alpha the alpha channel
   * @see Utils#clamp01(float)
   */
  @Override
  protected void colorCalcARGB (
      final int argb,
      final float alpha ) {

    if ( alpha == this.colorModeA ) {
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
  protected void curveInit ( ) {

    if ( this.curveDrawMatrix == null ) {
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

    if ( this.bezierBasisInverse == null ) {
      this.bezierBasisInverse = new PMatrix3D();
      PMatAux.inverse(this.bezierBasisMatrix, this.bezierBasisInverse);
      this.curveToBezierMatrix = new PMatrix3D();
    }

    PMatAux.mul(
        this.bezierBasisInverse,
        this.curveBasisMatrix,
        this.curveToBezierMatrix);
    PMatAux.mul(
        this.curveDrawMatrix,
        this.curveBasisMatrix,
        this.curveDrawMatrix);
  }

  /**
   * Draws an AWT shape object without inquiring as to whether a
   * gradient fill or stroke is to be used.
   *
   * @param s the shape
   * @see Graphics2D#setColor(java.awt.Color)
   * @see Graphics2D#fill(Shape)
   * @see Graphics2D#draw(Shape)
   */
  protected void drawShapeSolid ( final Shape s ) {

    if ( this.fill ) {
      this.g2.setColor(this.fillColorObject);
      this.g2.fill(s);
    }

    if ( this.stroke ) {
      this.g2.setColor(this.strokeColorObject);
      this.g2.draw(s);
    }
  }

  /**
   * The rounded corner rectangle implementation. The meaning of the
   * first four parameters depends on rectMode.
   *
   * @param a   the first x parameter
   * @param b   the first y parameter
   * @param c   the second x parameter
   * @param d   the second y parameter
   * @param rTl the top-left corner rounding
   * @param rTr the top-right corner rounding
   * @param rBr the bottom-right corner rounding
   * @param rBl the bottom-left corner rounding
   */
  @Override
  protected void rectImpl (
      final float a,
      final float b,
      final float c,
      final float d,
      float rTl,
      float rTr,
      float rBr,
      float rBl ) {

    float x1 = 0.0f;
    float y1 = 0.0f;
    float x2 = 0.0f;
    float y2 = 0.0f;

    float w = 0.0f;
    float h = 0.0f;

    switch ( this.rectMode ) {

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
    rTl = Utils.clamp(rTl, IUtils.DEFAULT_EPSILON, limit);
    rTr = Utils.clamp(rTr, IUtils.DEFAULT_EPSILON, limit);
    rBr = Utils.clamp(rBr, IUtils.DEFAULT_EPSILON, limit);
    rBl = Utils.clamp(rBl, IUtils.DEFAULT_EPSILON, limit);

    this.gp.reset();
    this.gp.moveTo(x2 - rTr, y1);
    this.gp.quadTo(x2, y1, x2, y1 - rTr);
    this.gp.lineTo(x2, y2 + rBr);
    this.gp.quadTo(x2, y2, x2 - rBr, y2);
    this.gp.lineTo(x1 + rBl, y2);
    this.gp.quadTo(x1, y2, x1, y2 + rBl);
    this.gp.lineTo(x1, y1 - rTl);
    this.gp.quadTo(x1, y1, x1 + rTl, y1);
    this.gp.closePath();
    this.drawShapeSolid(this.gp);
  }

  /**
   * For internal use to minimize the number of new BasicStroke objects
   * instantiated.
   *
   * @param strokeCap    the stroke cap {SQUARE, PROJECT, ROUND}
   * @param strokeJoin   the stroke join {MITER, BEVEL, ROUND}
   * @param strokeWeight the stroke weight
   * @see YupJ2#chooseStrokeCap(int)
   * @see YupJ2#chooseStrokeJoin(int)
   * @see YupJ2#strokeImpl()
   */
  protected void setStrokeAwt (
      final int strokeCap,
      final int strokeJoin,
      final float strokeWeight ) {

    /*
     * The lower bound of the stroke weight has to be < 1.0 because of
     * stroke scaling issues.
     */
    this.strokeWeight = Utils.max(IUtils.DEFAULT_EPSILON, strokeWeight);
    this.chooseStrokeCap(strokeCap);
    this.chooseStrokeJoin(strokeJoin);
    this.strokeImpl();
  }

  /**
   * Creates a new BasicStroke object from the stroke weight, stroke cap
   * (native), and stroke join (native), then sets the AWT renderer's
   * stroke with this object.
   *
   * Unlike Processing, strokeWeight, strokeCap and strokeJoin are set
   * together in the underlying API. Calls to strokeImpl should be
   * minimized in internal code.
   *
   * @see YupJ2#setStrokeAwt(int, int, float)
   * @see BasicStroke#BasicStroke(float, int, int, float)
   * @see Graphics2D#setStroke(java.awt.Stroke)
   */
  @Override
  protected void strokeImpl ( ) {

    this.strokeObject = new BasicStroke(
        this.strokeWeight,
        this.capNative,
        this.joinNative,
        this.miterLimit);
    this.g2.setStroke(this.strokeObject);
  }

  /**
   * Displays a character in the sketch.
   *
   * @param ch the character
   * @param x  the location x
   * @param y  the location y
   * @see PFont#getGlyph(char)
   * @see PFont#getSize()
   * @see YupJ2#textCharModelImpl(PImage, float, float, float, float,
   *      int, int)
   */
  @Override
  protected void textCharImpl (
      final char ch,
      final float x,
      final float y ) {

    final PFont.Glyph glyph = this.textFont.getGlyph(ch);
    if ( glyph != null ) {
      final float szInv = Utils.div(1.0f, this.textFont.getSize());
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
   * @param glyph the glyph image
   * @param x1    the first x coordinate
   * @param y1    the first y coordinate
   * @param x2    the second x coordinate
   * @param y2    the second y coordinate
   * @param u     the u coordinate
   * @param v     the v coordinate
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
   * @param buffer the array of characters
   * @param start  the start index, inclusive
   * @param stop   the stop index, exclusive
   * @param x      the horizontal location
   * @param y      the vertical location
   */
  @Override
  protected void textLineImpl (
      final char[] buffer,
      final int start,
      final int stop,
      final float x,
      final float y ) {

    float cursor = x;
    for ( int index = start; index < stop; ++index ) {
      final char c = buffer[index];
      this.textCharImpl(c, cursor, y);
      cursor += this.textWidth(c);
    }
  }

  /**
   * Finds the model view position of a point.<br>
   * <br>
   * More efficient than calling
   * {@link PApplet#modelX(float, float, float)} and
   * {@link PApplet#modelY(float, float, float)} separately. However, it
   * is advisable to work with the renderer matrices directly.
   *
   * @param source the point
   * @param target the output vector
   * @return the model point
   */
  @Experimental
  Vec2 model ( final Vec2 source, final Vec2 target ) {

    // TODO: Implement.

    final AffineTransform tr = this.g2.getTransform();

    final double ax = tr.getScaleX() * source.x +
        tr.getShearX() * source.y +
        tr.getTranslateX();
    final double ay = tr.getShearY() * source.x +
        tr.getScaleY() * source.y +
        tr.getTranslateY();

    // final double c = Math.cos(-cameraRot);
    // final double s = Math.sin(-cameraRot);

    final double m00 = 1.0d;
    final double m01 = 0.0d;
    final double m02 = 0.0d;

    final double m10 = 0.0d;
    final double m11 = -1.0d;
    final double m12 = 0.0d;

    return target.set(
        (float) (m00 * ax + m01 * ay + m02),
        (float) (m10 * ax + m11 * ay + m12));
  }

  /**
   * Applies an affine transform matrix to the current renderer
   * transform.
   *
   * @param m00 right axis x
   * @param m01 up axis x
   * @param m02 translation x
   * @param m10 right axis y
   * @param m11 up axis y
   * @param m12 translation y
   * @see AffineTransform#setTransform(double, double, double, double,
   *      double, double)
   * @see Graphics2D#transform(AffineTransform)
   */
  @Override
  public void applyMatrix (
      final float m00,
      final float m01,
      final float m02,
      final float m10,
      final float m11,
      final float m12 ) {

    /*
     * Method signature: m00: scale x, m10: shear y, m01: shear x, m11:
     * scale y, m02: translation x, m12: translation y .
     */
    this.affineNative.setTransform(
        m00, m10,
        m01, m11,
        m02, m12);
    this.g2.transform(this.affineNative);
  }

  /**
   * Applies an affine transform matrix to the current renderer
   * transform.
   *
   * @param source the source matrix
   */
  public void applyMatrix ( final Mat3 source ) {

    this.applyMatrix(
        source.m00, source.m01, source.m02,
        source.m10, source.m11, source.m12);
  }

  /**
   * Applies an affine transform matrix to the current renderer
   * transform.
   *
   * @param source the source matrix
   */
  @Override
  public void applyMatrix ( final PMatrix2D source ) {

    this.applyMatrix(
        source.m00, source.m01, source.m02,
        source.m10, source.m11, source.m12);
  }

  /**
   * Draws an arc at a location from a start angle to a stop angle. The
   * meaning of the first four parameters depends on the renderer's
   * ellipseMode.
   *
   * @param x0    the first x
   * @param y0    the first y
   * @param x1    the second x
   * @param y1    the second y
   * @param start the start angle
   * @param stop  the stop angle
   * @param mode  the arc mode
   * @see YupJ2#arcImpl(float, float, float, float, float, float, int)
   */
  @Override
  public void arc (
      final float x0,
      final float y0,
      final float x1,
      final float y1,
      final float start,
      final float stop,
      final int mode ) {

    if ( Utils.approx(stop - start, IUtils.TAU, 0.00139f) ) {
      this.ellipse(x0, y0, x1, y1);
      return;
    }

    float x = x0;
    float y = y0;
    float w = x1;
    float h = y1;

    switch ( this.ellipseMode ) {

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
   * Draws an arc at a location from a start angle to a stop angle.
   *
   * @param v     the location
   * @param sz    the arc size
   * @param start the start angle
   * @param stop  the stop angle
   * @param mode  the arc mode
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
   * Set the renderer's background color.
   *
   * @param c the color
   */
  @Override
  public void background ( final Color c ) {

    /* backgroundFromCalc calls backgroundImpl. */
    this.colorCalc(c);
    this.backgroundFromCalc();
  }

  /**
   * Calls the parent beginDraw method, then calls the camera. Used so
   * that camera does not have to be called in the PDE to get the
   * default.
   */
  @Override
  public void beginDraw ( ) {

    super.beginDraw();
    this.camera();
  }

  /**
   * Draws a cubic Bezier curve between two anchor points, where the
   * control points shape the curve.
   *
   * @param ap0 the first anchor point
   * @param cp0 the first control point
   * @param cp1 the second control point
   * @param ap1 the second anchor point
   */
  @Override
  public void bezier (
      final Vec2 ap0,
      final Vec2 cp0,
      final Vec2 cp1,
      final Vec2 ap1 ) {

    this.bezier(
        ap0.x, ap0.y,
        cp0.x, cp0.y,
        cp1.x, cp1.y,
        ap1.x, ap1.y);
  }

  /**
   * Evaluates a Bezier curve at step t for points a, b, c and d. The
   * parameter t varies between 0 and 1; a and d are points on the
   * curve; b and c are the control points. This can be done once with
   * the x coordinates and a second time with the y coordinates to get
   * the location of a bezier curve at t.
   *
   * @param ap0 coordinate of first point on the curve
   * @param cp0 coordinate of first control point
   * @param cp1 coordinate of second control point
   * @param ap1 coordinate of second point on the curve
   * @param t   step in the range [0.0, 1.0]
   * @return the evaluation
   */
  @Override
  public float bezierPoint (
      final float ap0,
      final float cp0,
      final float cp1,
      final float ap1,
      final float t ) {

    final float u = 1.0f - t;
    return (ap0 * u + cp0 * (t + t + t)) * u * u
        + (cp1 * (u + u + u) + ap1 * t) * t * t;
  }

  /**
   * Calculates the tangent of a point on a Bezier curve.
   *
   * @param ap0 coordinate of first point on the curve
   * @param cp0 coordinate of first control point
   * @param cp1 coordinate of second control point
   * @param ap1 coordinate of second point on the curve
   * @param t   step in the range [0.0, 1.0]
   * @return the evaluation
   */
  @Override
  public float bezierTangent (
      final float ap0,
      final float cp0,
      final float cp1,
      final float ap1,
      final float t ) {

    final float t3 = t + t + t;
    final float b2 = cp0 + cp0;
    final float ac = ap0 + cp1;
    final float bna = cp0 - ap0;

    return t3 * t * (b2 + cp0 + ap1 - (ac + cp1 + cp1)) +
        (t3 + t3) * (ac - b2) +
        (bna + bna + bna);
  }

  /**
   * Draws a cubic Bezier curve segment to the next anchor point; the
   * first and second control point shape the curve segment.
   *
   * @param cp0 the first control point
   * @param cp1 the second control point
   * @param ap1 the next anchor point
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
   * Sets the camera to the Processing default, where the origin is in
   * the top left corner of the sketch and the y axis points downward.
   */
  public void camDown ( ) {

    this.camera(
        this.width * 0.5f, this.height * 0.5f,
        0.0f,
        1.0f, -1.0f);
  }

  /**
   * Sets the camera to the renderer defaults.
   */
  @Override
  public void camera ( ) {

    this.camera(
        this.cameraX, this.cameraY,
        this.cameraRot,
        this.cameraZoomX, this.cameraZoomY);
  }

  /**
   * Sets the camera to a location, rotation and zoom level.
   *
   * @param x       the translation x
   * @param y       the translation y
   * @param radians the angle in radians
   * @param zx      the zoom x
   * @param zy      the zoom y
   * @see Utils#modRadians(float)
   */
  @Override
  public void camera (
      final float x,
      final float y,
      final float radians,
      final float zx,
      final float zy ) {

    this.cameraX = x;
    this.cameraY = y;
    this.cameraRot = radians;
    this.cameraZoomX = Utils.abs(zx) < IUtils.DEFAULT_EPSILON ? 1.0f : zx;
    this.cameraZoomY = Utils.abs(zy) < IUtils.DEFAULT_EPSILON ? 1.0f : zy;

    final double c = Math.cos(-radians);
    final double s = Math.sin(-radians);
    final double m00 = c * this.cameraZoomX;
    final double m01 = -s * this.cameraZoomY;
    final double m10 = s * this.cameraZoomX;
    final double m11 = c * this.cameraZoomY;

    this.affineNative.setTransform(
        m00, -m10,
        m01, -m11,
        this.width * 0.5d
            - this.cameraX * m00
            - this.cameraY * m01,
        this.height * 0.5d
            + this.cameraX * m10
            + this.cameraY * m11);
    this.g2.setTransform(this.affineNative);
  }

  /**
   * This version of camera is not supported by this renderer. Calls the
   * supported version of camera.
   *
   * @param xEye    camera location x
   * @param yEye    camera location y
   * @param zEye    camera location z
   * @param xCenter target location x
   * @param yCenter target location y
   * @param zCenter target location z
   * @param xUp     world up axis x
   * @param yUp     world up axis y
   * @param zUp     world up axis z
   */
  @Override
  public void camera (
      final float xEye,
      final float yEye,
      final float zEye,
      final float xCenter,
      final float yCenter,
      final float zCenter,
      final float xUp,
      final float yUp,
      final float zUp ) {

    PApplet.showMissingWarning("camera");

    this.camera(
        xEye, yEye,
        Utils.atan2(yUp, xUp),
        this.cameraZoomX,
        this.cameraZoomY);
  }

  /**
   * Sets the camera to a location, rotation and zoom level.
   *
   * @param loc     the location
   * @param radians the angle
   * @param zoom    the zoom level
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
   * @param coord the coordinate
   * @param size  the size
   */
  @Override
  public void circle ( final Vec2 coord, final float size ) {

    this.ellipse(coord.x, coord.y, size, size);
  }

  /**
   * Sets the renderer's color mode. Color channel maximums should be a
   * positive value greater than or equal to one.
   *
   * @param mode the color mode, HSB or RGB
   * @param max1 the first channel maximum, hue or red
   * @param max2 the second channel maximum, saturation or green
   * @param max3 the third channel maximum, brightness or blue
   * @param aMax the alpha channel maximum
   */
  @Override
  public void colorMode (
      final int mode,
      final float max1,
      final float max2,
      final float max3,
      final float aMax ) {

    super.colorMode(mode,
        max1 < 1.0f ? 1.0f : max1,
        max2 < 1.0f ? 1.0f : max2,
        max3 < 1.0f ? 1.0f : max3,
        aMax < 1.0f ? 1.0f : aMax);

    /*
     * Cache the inverse of the color maximums so that color channels can
     * be scaled to the range [0.0, 1.0] later .
     */
    this.invColorModeX = 1.0f / this.colorModeX;
    this.invColorModeY = 1.0f / this.colorModeY;
    this.invColorModeZ = 1.0f / this.colorModeZ;
    this.invColorModeA = 1.0f / this.colorModeA;
  }

  /**
   * Draws a curve between four points.
   *
   * @param a the first point
   * @param b the second point
   * @param c the third point
   * @param d the fourth point
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
   * @param a the coordinate
   */
  @Override
  public void curveVertex ( final Vec2 a ) { this.curveVertex(a.x, a.y); }

  /**
   * Sets default camera and calls the camera function. This is for
   * parity with OpenGL renderers.
   */
  @Override
  public void defaultCamera ( ) {

    this.cameraX = IUp.DEFAULT_LOC_X;
    this.cameraY = IUp.DEFAULT_LOC_Y;
    this.cameraZoomX = IYup2.DEFAULT_ZOOM_X;
    this.cameraZoomY = IYup2.DEFAULT_ZOOM_Y;
    this.cameraRot = IYup2.DEFAULT_ROT;

    this.camera();
  }

  /**
   * Sets the renderer's default styling.
   */
  @Override
  public void defaultSettings ( ) {

    super.defaultSettings();
    this.colorMode(PConstants.RGB, IUp.DEFAULT_COLOR_MAX);
    this.fill(IUp.DEFAULT_FILL_COLOR);
    this.stroke(IUp.DEFAULT_STROKE_COLOR);

    this.setStrokeAwt(
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

    if ( this.primaryGraphics ) { this.background(IUp.DEFAULT_BKG_COLOR); }

    this.blendMode(PConstants.BLEND);

    this.settingsInited = true;
    this.reapplySettings = false;

    this.cameraX = IUp.DEFAULT_LOC_X;
    this.cameraY = IUp.DEFAULT_LOC_Y;
    this.cameraZoomX = IYup2.DEFAULT_ZOOM_X;
    this.cameraZoomY = IYup2.DEFAULT_ZOOM_Y;
    this.cameraRot = IYup2.DEFAULT_ROT;
  }

  /**
   * Draws an ellipse. The parameters meanings are determined by the
   * ellipseMode.
   *
   * @param x the x coordinate
   * @param y the y coordinate
   * @param w the third parameter
   * @param h the fourth parameter
   * @see Utils#abs(float)
   * @see Path2D#reset()
   * @see Path2D#moveTo(double, double)
   * @see Path2D#curveTo(double, double, double, double, double, double)
   * @see Path2D#closePath()
   * @see YupJ2#drawShapeSolid(Shape)
   */
  @Override
  public void ellipse (
      final float x,
      final float y,
      final float w,
      final float h ) {

    /*
     * Does not defer to ellipseImpl . Instead, approximates a circle with
     * four Bezier curves.
     */
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

    switch ( this.ellipseMode ) {

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
    this.drawShapeSolid(this.gp);
  }

  /**
   * Draws an ellipse; the meaning of the two parameters depends on the
   * renderer's ellipseMode.
   *
   * @param a the first parameter
   * @param b the second parameter
   */
  @Override
  public void ellipse ( final Vec2 a, final Vec2 b ) {

    this.ellipse(a.x, a.y, b.x, b.y);
  }

  /**
   * Sets the renderer's current fill to the color.
   *
   * @param c the color
   */
  @Override
  public void fill ( final camzup.core.Color c ) {

    this.colorCalc(c);
    this.fillFromCalc();
    this.fillColorObject = new java.awt.Color(
        this.fillColor, true);
    this.fillGradient = false;
  }

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
   * Gets the renderer camera's location.
   *
   * @param target the output vector
   * @return the location
   */
  @Override
  public Vec2 getLocation ( final Vec2 target ) {

    return target.set(this.cameraX, this.cameraY);
  }

  /**
   * Gets the renderer camera's location on the x axis.
   *
   * @return the x location
   */
  @Override
  public float getLocX ( ) { return this.cameraX; }

  /**
   * Gets the renderer camera's location on the y axis.
   *
   * @return the y location
   */
  @Override
  public float getLocY ( ) { return this.cameraY; }

  /**
   * Retrieves the renderer's matrix.
   */
  @Override
  public PMatrix2D getMatrix ( ) {

    return this.getMatrix((PMatrix2D) null);
  }

  /**
   * Retrieves the renderer's matrix.
   *
   * @param target the output matrix
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
   * @param target the output matrix
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
   * @param target the output matrix
   * @return the renderer matrix
   * @see Graphics2D#getTransform()
   * @see Convert#toPMatrix2D(AffineTransform, PMatrix2D)
   */
  @Override
  public PMatrix2D getMatrix ( final PMatrix2D target ) {

    return Convert.toPMatrix2D(this.g2.getTransform(), target);
  }

  /**
   * Retrieves the renderer's matrix.
   *
   * @param target the output matrix
   * @return the renderer matrix
   * @see Graphics2D#getTransform()
   * @see Convert#toPMatrix3D(AffineTransform, PMatrix3D)
   */
  @Override
  public PMatrix3D getMatrix ( final PMatrix3D target ) {

    return Convert.toPMatrix3D(this.g2.getTransform(), target);
  }

  /**
   * Gets the renderer's parent applet.
   *
   * @return the applet
   */
  @Override
  public PApplet getParent ( ) { return this.parent; }

  /**
   * Gets the renderer camera's rotation.
   *
   * @return the rotation
   */
  @Override
  public float getRoll ( ) { return this.cameraRot; }

  /**
   * Gets the renderer's size.
   *
   * @param target the output vector
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
   * Gets the renderer's zoom.
   *
   * @param target the output vector
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
  public float getZoomX ( ) { return this.cameraZoomX; }

  /**
   * Gets the renderer's vertical zoom.
   *
   * @return the zoom on the vertical axis
   */
  @Override
  public float getZoomY ( ) { return this.cameraZoomY; }

  /**
   * Draws a diagnostic grid out of points. Overrides the default
   * implementation because of the issue with drawing points with SQUARE
   * strokeCap.
   *
   * @param count        number of points
   * @param strokeWeight stroke weight
   * @param dim          the grid dimensions
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

    /* Calculate inner for-loop values. */
    final float[] xs = new float[last];
    final int[] reds = new int[last];
    for ( int j = 0; j < last; ++j ) {
      final float jPercent = j * toPercent;
      xs[j] = (1.0f - jPercent) * left + jPercent * right;
      reds[j] = (int) (jPercent * 0xff + 0.5f) << 0x10;
    }

    this.pushStyle();
    this.setStrokeAwt(
        PConstants.ROUND,
        PConstants.ROUND,
        strokeWeight);

    for ( int i = 0; i < last; ++i ) {
      final float iPercent = i * toPercent;
      final float y = (1.0f - iPercent) * bottom + iPercent * top;
      final float yeps = y + PConstants.EPSILON;
      final int agb = ab | (int) (iPercent * 0xff + 0.5f) << 0x8;

      for ( int j = 0; j < last; ++j ) {
        final float x = xs[j];

        /*
         * Draw a point. Epsilon is added to y instead of x to minimize
         * calculations. Color is set directly with an object instead of
         * through colorCalc.
         */
        this.gp.reset();
        this.gp.moveTo(x, yeps);
        this.gp.lineTo(x, y);
        this.g2.setColor(new java.awt.Color(agb | reds[j], true));
        this.g2.draw(this.gp);
      }
    }

    this.popStyle();
  }

  /**
   * Displays the handles of a curve entity.
   *
   * @param ce the curve entity
   */
  public void handles ( final CurveEntity2 ce ) { this.handles(ce, 1.0f); }

  /**
   * Displays the handles of a curve entity.
   *
   * @param ce           the curve entity
   * @param strokeWeight the stroke weight
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
   * @param ce           the curve entity
   * @param strokeWeight the stroke weight
   * @param lineColor    the color of handle lines
   * @param rearColor    the color of the rear handle
   * @param foreColor    the color of the fore handle
   * @param coordColor   the color of the coordinate
   */
  public void handles (
      final CurveEntity2 ce,
      final float strokeWeight,
      final int lineColor,
      final int rearColor,
      final int foreColor,
      final int coordColor ) {

    /* Cache stroke colors. */
    final java.awt.Color lineClrAwt = new java.awt.Color(lineColor, true);
    final java.awt.Color rearClrAwt = new java.awt.Color(rearColor, true);
    final java.awt.Color foreClrAwt = new java.awt.Color(foreColor, true);
    final java.awt.Color crdClrAwt = new java.awt.Color(coordColor, true);

    /* Cache stroke weights. */
    final BasicStroke sw = new BasicStroke(strokeWeight,
        BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    final BasicStroke swRear = new BasicStroke(strokeWeight * 4.0f,
        BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    final BasicStroke swFore = new BasicStroke(strokeWeight * 5.0f,
        BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    final BasicStroke swCoord = new BasicStroke(strokeWeight * 6.25f,
        BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    final Transform2 tr = ce.transform;
    final List < Curve2 > curves = ce.curves;
    final Iterator < Curve2 > curveItr = curves.iterator();

    final Vec2 rh = new Vec2();
    final Vec2 co = new Vec2();
    final Vec2 fh = new Vec2();

    this.pushStyle();

    while ( curveItr.hasNext() ) {
      final Curve2 curve = curveItr.next();
      final Iterator < Knot2 > knItr = curve.iterator();

      while ( knItr.hasNext() ) {
        final Knot2 knot = knItr.next();

        Transform2.mulPoint(tr, knot.rearHandle, rh);
        Transform2.mulPoint(tr, knot.coord, co);
        Transform2.mulPoint(tr, knot.foreHandle, fh);

        /* Draw handle bars. */
        this.gp.reset();
        this.gp.moveTo(rh.x, rh.y);
        this.gp.lineTo(co.x, co.y);
        this.gp.lineTo(fh.x, fh.y);
        this.g2.setStroke(sw);
        this.g2.setColor(lineClrAwt);
        this.g2.draw(this.gp);

        /* Draw rear handle. */
        this.gp.reset();
        this.gp.moveTo(rh.x + PConstants.EPSILON, rh.y);
        this.gp.lineTo(rh.x, rh.y);
        this.g2.setStroke(swRear);
        this.g2.setColor(rearClrAwt);
        this.g2.draw(this.gp);

        /* Draw coordinate. */
        this.gp.reset();
        this.gp.moveTo(co.x + PConstants.EPSILON, co.y);
        this.gp.lineTo(co.x, co.y);
        this.g2.setStroke(swCoord);
        this.g2.setColor(crdClrAwt);
        this.g2.draw(this.gp);

        /* Draw fore handle. */
        this.gp.reset();
        this.gp.moveTo(fh.x + PConstants.EPSILON, fh.y);
        this.gp.lineTo(fh.x, fh.y);
        this.g2.setStroke(swFore);
        this.g2.setColor(foreClrAwt);
        this.g2.draw(this.gp);
      }
    }

    this.popStyle();
  }

  /**
   * Draws a buffer at the origin.
   *
   * @param buff the renderer
   */
  public void image ( final PGraphicsJava2D buff ) {

    if ( buff.g2 != null ) { this.image((PImage) buff); }
  }

  /**
   * Displays a buffer at a location. Uses the buffer's width and height
   * as the second parameters.
   *
   * @param buff the renderer
   * @param x    the first x coordinate
   * @param y    the first y coordinate
   */
  public void image (
      final PGraphicsJava2D buff,
      final float x,
      final float y ) {

    if ( buff.g2 != null ) { this.image((PImage) buff, x, y); }
  }

  /**
   * Displays a buffer. The meaning of the first four parameters depends
   * on imageMode.
   *
   * @param buff the renderer
   * @param x    the first x coordinate
   * @param y    the first y coordinate
   * @param u    the second x coordinate
   * @param v    the second y coordinate
   */
  public void image (
      final PGraphicsJava2D buff,
      final float x, final float y,
      final float u, final float v ) {

    if ( buff.g2 != null ) { this.image((PImage) buff, x, y, u, v); }
  }

  /**
   * Displays a buffer. The meaning of the first four parameters depends
   * on imageMode.
   *
   * @param buff the buffer
   * @param a    the first x coordinate
   * @param b    the first y coordinate
   * @param c    the second x coordinate
   * @param d    the second y coordinate
   * @param u1   the image top-left corner u
   * @param v1   the image top-left corner v
   * @param u2   the image bottom-right corner u
   * @param v2   the image bottom-right corner v
   */
  public void image (
      final PGraphicsJava2D buff,
      final float a, final float b,
      final float c, final float d,
      final int u1, final int v1,
      final int u2, final int v2 ) {

    if ( buff.g2 != null ) {
      this.image((PImage) buff,
          a, b, c, d,
          u1, v1, u2, v2);
    }
  }

  /**
   * Draws a buffer at a given coordinate.
   *
   * @param buff  the renderer
   * @param coord the coordinate
   */
  public void image (
      final PGraphicsJava2D buff,
      final Vec2 coord ) {

    if ( buff.g2 != null ) { this.image((PImage) buff, coord); }
  }

  /**
   * Draws a buffer at a given coordinate.
   *
   * @param buff  the renderer
   * @param coord the coordinate
   * @param dim   the dimension
   */
  public void image (
      final PGraphicsJava2D buff,
      final Vec2 coord,
      final Vec2 dim ) {

    if ( buff.g2 != null ) { this.image((PImage) buff, coord, dim); }
  }

  /**
   * Draws an image at the origin.
   *
   * @param img the image
   */
  @Override
  public void image ( final PImage img ) {

    this.image(img, 0.0f, 0.0f);
  }

  /**
   * Displays a PImage at a location. Uses the image's width and height
   * as the second parameters.
   *
   * @param img the PImage
   * @param x   the first x coordinate
   * @param y   the first y coordinate
   */
  @Override
  public void image (
      final PImage img,
      final float x,
      final float y ) {

    this.image(img, x, y, img.width, img.height);
  }

  /**
   * Displays a PImage. The meaning of the first four parameters depends
   * on imageMode.
   *
   * @param img the PImage
   * @param x   the first x coordinate
   * @param y   the first y coordinate
   * @param u   the second x coordinate
   * @param v   the second y coordinate
   */
  @Override
  public void image (
      final PImage img,
      final float x, final float y,
      final float u, final float v ) {

    final boolean isImg = this.textureMode == PConstants.IMAGE;
    this.image(img, x, y, u, v, 0, 0,
        isImg ? img.width : 1,
        isImg ? img.height : 1);
  }

  /**
   * Displays a PImage. The meaning of the first four parameters depends
   * on imageMode.
   *
   * @param img the PImage
   * @param a   the first x coordinate
   * @param b   the first y coordinate
   * @param c   the second x coordinate
   * @param d   the second y coordinate
   * @param u1  the image top-left corner u
   * @param v1  the image top-left corner v
   * @param u2  the image bottom-right corner u
   * @param v2  the image bottom-right corner v
   */
  @Override
  public void image (
      final PImage img,
      final float a, final float b,
      final float c, final float d,
      final int u1, final int v1,
      final int u2, final int v2 ) {

    if ( img.pixels == null || img.width < 2 || img.height < 2 ) { return; }

    float xTopLeft = 0.0f;
    float yTopLeft = 0.0f;
    float xBottomRight = 0.0f;
    float yBottomRight = 0.0f;
    float wHalf = 0.0f;
    float hHalf = 0.0f;

    switch ( this.imageMode ) {

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
   * @param img   the image
   * @param coord the coordinate
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
   * @param img   the image
   * @param coord the coordinate
   * @param dim   the dimension
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
  public boolean is2D ( ) { return true; }

  /**
   * Returns whether or not the renderer is 3D.
   */
  @Override
  public boolean is3D ( ) { return false; }

  /**
   * Eases from an origin color to a destination by a step.
   *
   * @param origin the origin color
   * @param dest   the destination color
   * @param step   the factor in [0, 1]
   * @param target the output color
   * @return the color
   */
  @Override
  public Color lerpColor (
      final Color origin,
      final Color dest,
      final float step,
      final Color target ) {

    switch ( this.colorMode ) {

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
   * @param origin the origin color
   * @param dest   the destination color
   * @param step   the factor in [0, 1]
   * @return the color
   */
  @Override
  public int lerpColor (
      final int origin,
      final int dest,
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
   * @param xOrigin the origin x
   * @param yOrigin the origin y
   * @param xDest   the destination x
   * @param yDest   the destination y
   */
  @Override
  public void line (
      final float xOrigin,
      final float yOrigin,
      final float xDest,
      final float yDest ) {

    /*
     * It doesn't make sense why turning off the stroke would also turn
     * off a line, but for now this is Processing's expected behavior.
     */
    if ( this.stroke ) {
      this.gp.reset();
      this.gp.moveTo(xOrigin, yOrigin);
      this.gp.lineTo(xDest, yDest);
      this.g2.setColor(this.strokeColorObject);
      this.g2.draw(this.gp);
    }
  }

  /**
   * Draws a line between two coordinates.
   *
   * @param origin the origin coordinate
   * @param dest   the destination coordinate
   */
  @Override
  public void line ( final Vec2 origin, final Vec2 dest ) {

    this.line(origin.x, origin.y, dest.x, dest.y);
  }

  /**
   * Sets the renderer's stroke, stroke weight and fill to the
   * material's. Also sets whether or not to use fill and stroke.
   *
   * @param material the material
   */
  public void material ( final MaterialSolid material ) {

    this.stroke = material.useStroke;
    this.fill = material.useFill;

    if ( material.useStroke ) {
      this.strokeWeight(material.strokeWeight);
      final camzup.core.Color coreStr = material.stroke;
      this.strokeColorObject = new java.awt.Color(
          coreStr.x,
          coreStr.y,
          coreStr.z,
          coreStr.w);
    }

    if ( material.useFill ) {
      final camzup.core.Color coreFll = material.fill;
      this.fillColorObject = new java.awt.Color(
          coreFll.x,
          coreFll.y,
          coreFll.z,
          coreFll.w);
    }
  }

  /**
   * Draws the world origin.
   */
  @Override
  public void origin ( ) {

    this.origin(
        IUp.DEFAULT_IJK_LINE_FAC * Utils.min(this.width, this.height),
        IUp.DEFAULT_IJK_SWEIGHT,
        IUp.DEFAULT_I_COLOR,
        IUp.DEFAULT_J_COLOR);
  }

  /**
   * Draws the world origin.
   *
   * @param lineLength the line length
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
   * @param lineLength   the line length
   * @param strokeWeight the stroke weight
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
   * @param lineLength   the line length
   * @param strokeWeight the stroke weight
   * @param xColor       the color of the x axis
   * @param yColor       the color of the y axis
   */
  public void origin (
      final float lineLength,
      final float strokeWeight,
      final int xColor,
      final int yColor ) {

    final float vl = lineLength > IUtils.DEFAULT_EPSILON ? lineLength
        : IUtils.DEFAULT_EPSILON;

    this.pushStyle();
    this.setStrokeAwt(
        PConstants.ROUND,
        PConstants.ROUND,
        strokeWeight);

    /* Draw x (right) axis. */
    this.gp.reset();
    this.gp.moveTo(0.0d, 0.0d);
    this.gp.lineTo(vl, 0.0d);
    this.g2.setColor(new java.awt.Color(xColor, true));
    this.g2.draw(this.gp);

    /* Draw y (forward) axis. */
    this.gp.reset();
    this.gp.moveTo(0.0d, 0.0d);
    this.gp.lineTo(0.0d, vl);
    this.g2.setColor(new java.awt.Color(yColor, true));
    this.g2.draw(this.gp);

    this.popStyle();
  }

  /**
   * Draws a point at the coordinate x, y. This is done by drawing a
   * line from (x, y) to ((x, y) + (epsilon, epsilon)).
   *
   * When strokeCap is set to SQUARE, it is swapped to PROJECT, then
   * back to SQUARE.
   *
   * @param x the x coordinate
   * @param y the y coordinate
   */
  @Override
  public void point ( final float x, final float y ) {

    /*
     * It doesn't make sense why turning off the stroke would also turn
     * off a point, but for now this is Processing's expected behavior.
     */
    if ( this.stroke ) {

      /* Processing SQUARE is AWT BUTT; PROJECT is AWT SQUARE. */
      final boolean needSwap = this.capNative == BasicStroke.CAP_BUTT;
      if ( needSwap ) {

        this.strokeObject = new BasicStroke(
            this.strokeWeight,
            BasicStroke.CAP_SQUARE,
            this.joinNative,
            this.miterLimit);
        this.g2.setStroke(this.strokeObject);

        this.gp.reset();
        this.gp.moveTo(x, y);
        this.gp.lineTo(x + PConstants.EPSILON, y);
        this.g2.setColor(this.strokeColorObject);
        this.g2.draw(this.gp);

        this.strokeObject = new BasicStroke(
            this.strokeWeight,
            this.capNative,
            this.joinNative,
            this.miterLimit);
        this.g2.setStroke(this.strokeObject);

      } else {

        this.gp.reset();
        this.gp.moveTo(x, y);
        this.gp.lineTo(x + PConstants.EPSILON, y);
        this.g2.setColor(this.strokeColorObject);
        this.g2.draw(this.gp);

      }
    }
  }

  /**
   * Removes an affine transformation matrix off the end of the stack.
   * After popping the matrix, the camera's settings are applied.
   */
  @Override
  public void popMatrix ( ) {

    super.popMatrix();
    this.camera(
        this.cameraX, this.cameraY,
        this.cameraRot,
        this.cameraZoomX, this.cameraZoomY);
  }

  /**
   * Prints the renderer matrix. Follows the Processing convention of a
   * row-major 3 x 2 matrix, not the Java AWT convention.
   */
  @Override
  public void printMatrix ( ) { this.printMatrix(4); }

  /**
   * Prints the renderer matrix. Follows the Processing convention of a
   * row-major 3 x 2 matrix, not the Java AWT convention.<br>
   * <br>
   * <code>[ m00, m01, m02,<br>
   * m10, m11, m12 ]</code><br>
   * <br>
   * is equivalent to<br>
   * <br>
   * <code>[ scaleX, shearX, translateX,<br>
   * shearY, scaleY, translateY ]</code>
   *
   * @param places number of decimal places
   */
  public void printMatrix ( final int places ) {

    final AffineTransform tr = this.g2.getTransform();
    System.out.println(new StringBuilder(320)
        .append('\n').append('[').append(' ')

        .append(Utils.toFixed(
            (float) tr.getScaleX(), places))
        .append(',').append(' ')
        .append(Utils.toFixed(
            (float) tr.getShearX(), places))
        .append(',').append(' ')
        .append(Utils.toFixed(
            (float) tr.getTranslateX(), places))
        .append(',').append(' ').append('\n')

        .append(Utils.toFixed(
            (float) tr.getShearY(), places))
        .append(',').append(' ')
        .append(Utils.toFixed(
            (float) tr.getScaleY(), places))
        .append(',').append(' ')
        .append(Utils.toFixed(
            (float) tr.getTranslateY(), places))

        .append(' ').append(']').append('\n')
        .toString());
  }

  /**
   * Draws a quadrilateral between four points.
   *
   * @param x0 the first point x
   * @param y0 the first point y
   * @param x1 the second point x
   * @param y1 the second point y
   * @param x2 the third point x
   * @param y2 the third point y
   * @param x3 the fourth point x
   * @param y3 the fourth point y
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
    this.drawShapeSolid(this.gp);
  }

  /**
   * Draws a quadrilateral between four points.
   *
   * @param a the first point
   * @param b the second point
   * @param c the third point
   * @param d the fourth point
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
   * Draws a quadratic Bezier curve segment to the next anchor point;
   * the control point shapes the curve segment.
   *
   * @param cp  the control point
   * @param ap1 the next anchor point
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
   * Displays a ray, i.e., an origin point and a direction. The display
   * length of the direction is dictated by an input.
   *
   * @param xOrigin the x origin
   * @param yOrigin the y origin
   * @param xDir    the x direction
   * @param yDir    the y direction
   * @param dLen    the display length
   * @param lnwgt   the line weight
   * @param oWeight the origin stroke weight
   * @param dWeight the direction stroke weight
   */
  @Override
  public void ray (
      final float xOrigin,
      final float yOrigin,
      final float xDir,
      final float yDir,
      final float dLen,
      final float lnwgt,
      final float oWeight,
      final float dWeight ) {

    final float mSq = xDir * xDir + yDir * yDir;

    this.pushStyle();

    /* Draw point for ray origin. */
    this.strokeWeight(oWeight);
    this.gp.reset();
    this.gp.moveTo(xOrigin, yOrigin);
    this.gp.lineTo(xOrigin + PConstants.EPSILON, yOrigin);
    this.g2.setColor(this.strokeColorObject);
    this.g2.draw(this.gp);

    if ( mSq > 0.0f ) {

      final float mInv = dLen * Utils.invSqrtUnchecked(mSq);
      final float dx = xOrigin + xDir * mInv;
      final float dy = yOrigin + yDir * mInv;

      /* Draw ray line. */
      this.strokeWeight(lnwgt);
      this.gp.reset();
      this.gp.moveTo(xOrigin, yOrigin);
      this.gp.lineTo(dx, dy);
      this.g2.draw(this.gp);

      /* Draw point for ray direction. */
      this.strokeWeight(dWeight);
      this.gp.reset();
      this.gp.moveTo(dx, dy);
      this.gp.lineTo(dx + PConstants.EPSILON, dy);
      this.g2.draw(this.gp);
    }

    this.popStyle();
  }

  /**
   * Draws a rectangle. The meaning of the four parameters depends on
   * rectMode.
   *
   * @param a the first x parameter
   * @param b the first y parameter
   * @param c the second x parameter
   * @param d the second y parameter
   */
  @Override
  public void rect (
      final float a, final float b,
      final float c, final float d ) {

    /*
     * It is simpler to draw straight lines than to defer to the
     * rounded-corner rectImpl.
     */
    float x0 = 0.0f;
    float y0 = 0.0f;
    float x1 = 0.0f;
    float y1 = 0.0f;

    float w = 0.0f;
    float h = 0.0f;

    switch ( this.rectMode ) {

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
    this.drawShapeSolid(this.gp);
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
  public void rect (
      final float x1, final float y1,
      final float x2, final float y2,
      final float r ) {

    this.rectImpl(
        x1, y1, x2, y2,
        r, r, r, r);
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
   * Draws a rectangle; the meaning of the two parameters depends on the
   * renderer's rectMode.
   *
   * @param a the first parameter
   * @param b the second parameter
   */
  @Override
  public void rect ( final Vec2 a, final Vec2 b ) {

    this.rectImpl(a.x, a.y, b.x, b.y);
  }

  /**
   * Draws a rounded rectangle; the meaning of the first two parameters
   * depends on the renderer's rectMode.
   *
   * @param a the first parameter
   * @param b the second parameter
   * @param r the corner rounding
   */
  @Override
  public void rect ( final Vec2 a, final Vec2 b, final float r ) {

    this.rectImpl(
        a.x, a.y,
        b.x, b.y,
        r, r, r, r);
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
  public void resetMatrix ( ) {

    this.affineNative.setToIdentity();
    this.g2.setTransform(this.affineNative);
    this.g2.scale(this.pixelDensity, this.pixelDensity);
  }

  /**
   * Rotates the sketch by an angle in radians around the z axis.
   *
   * This will cause jittering, particularly when a shape has a stroke.
   *
   * @param angle the angle
   * @see Graphics2D#rotate(double)
   */
  @Override
  public void rotate ( final float angle ) { this.g2.rotate(angle); }

  /**
   * Rotates the sketch by an angle in radians around the x axis. For
   * 2D, this scales by ( 1.0, cos ( a ) ) .
   *
   * @param angle the angle
   * @see Graphics2D#scale(double, double)
   * @see Utils#cos(float)
   */
  @Override
  public void rotateX ( final float angle ) {

    this.g2.scale(1.0d, Math.cos(angle));
  }

  /**
   * Rotates the sketch by an angle in radians around the y axis. For
   * 2D, this scales by ( cos ( a ), 1.0 ) .
   *
   * @param angle the angle
   * @see Graphics2D#scale(double, double)
   * @see Utils#cos(float)
   */
  @Override
  public void rotateY ( final float angle ) {

    this.g2.scale(Math.cos(angle), 1.0d);
  }

  /**
   * Rotates the sketch by an angle in radians around the z axis.
   *
   * This will cause jittering, particularly when a shape has a stroke.
   *
   * @param angle the angle
   * @see Graphics2D#rotate(double)
   */
  @Override
  public void rotateZ ( final float angle ) { this.g2.rotate(angle); }

  /**
   * Scales the renderer by a dimension.
   *
   * @param dim the dimensions
   * @see Graphics2D#scale(double, double)
   */
  public void scale ( final Vec2 dim ) { this.g2.scale(dim.x, dim.y); }

  /**
   * Finds the screen position of a point in the world. <br>
   * <br>
   * More efficient than calling
   * {@link PApplet#screenX(float, float, float)} and
   * {@link PApplet#screenY(float, float, float)} separately. However,
   * it is advisable to work with the renderer matrix directly.
   *
   * @param source the source coordinate
   * @param target the target coordinate
   * @return the screen coordinate
   */
  @Override
  @Experimental
  public Vec2 screen (
      final Vec2 source,
      final Vec2 target ) {

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
   * Takes a two-dimensional x, y position and returns the x value for
   * where it will appear on a two-dimensional screen. This is
   * inefficient, use {@link YupJ2#screen(Vec2, Vec2)} instead.
   *
   * @param x the x coordinate
   * @param y the y coordinate
   * @return the screen x coordinate
   */
  @Override
  @Experimental
  public float screenX (
      final float x,
      final float y ) {

    final AffineTransform tr = this.g2.getTransform();
    return (float) (tr.getScaleX() * x +
        tr.getShearX() * y +
        tr.getTranslateX());
  }

  /**
   * Takes a two-dimensional x, y position and returns the x value for
   * where it will appear on a two-dimensional screen. This is
   * inefficient, use {@link YupJ2#screen(Vec2, Vec2)} instead.
   *
   * @param x the x coordinate
   * @param y the y coordinate
   * @return the screen x coordinate
   */
  @SuppressWarnings ( "unused" )
  @Override
  public float screenX ( final float x, final float y, final float z ) {

    PGraphics.showDepthWarningXYZ("screenX");
    return this.screenX(x, y);
  }

  /**
   * Takes a two-dimensional x, y position and returns the y value for
   * where it will appear on a two-dimensional screen. This is
   * inefficient, use {@link YupJ2#screen(Vec2, Vec2)} instead.
   *
   * @param x the x coordinate
   * @param y the y coordinate
   * @return the screen y coordinate
   * @see YupJ2#screen(Vec2, Vec2)
   */
  @Override
  @Experimental
  public float screenY (
      final float x,
      final float y ) {

    final AffineTransform tr = this.g2.getTransform();
    return (float) (tr.getShearY() * x +
        tr.getScaleY() * y +
        tr.getTranslateY());
  }

  /**
   * Takes a two-dimensional x, y position and returns the y value for
   * where it will appear on a two-dimensional screen. This is
   * inefficient, use {@link YupJ2#screen(Vec2, Vec2)} instead.
   *
   * @param x the x coordinate
   * @param y the y coordinate
   * @return the screen y coordinate
   * @see YupJ2#screen(Vec2, Vec2)
   */
  @SuppressWarnings ( "unused" )
  @Override
  public float screenY ( final float x, final float y, final float z ) {

    PGraphics.showDepthWarningXYZ("screenY");
    return this.screenY(x, y);
  }

  /**
   * Sets the renderer camera's location.
   *
   * @param v the vector
   */
  public void setLoc ( final Vec2 v ) {

    this.cameraX = v.x;
    this.cameraY = v.y;
  }

  /**
   * Sets the renderer's affine transform matrix to the supplied
   * arguments.
   *
   * @param m00 right axis x
   * @param m01 up axis x
   * @param m02 translation x
   * @param m10 right axis y
   * @param m11 up axis y
   * @param m12 translation y
   */
  public void setMatrix (
      final float m00, final float m01, final float m02,
      final float m10, final float m11, final float m12 ) {

    /*
     * Method signature: m00: scale x, m10: shear y, m01: shear x, m11:
     * scale y, m02: translation x, m12: translation y .
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
   * @param source a 3 x 3 matrix
   * @see AffineTransform#setTransform(double, double, double, double,
   *      double, double)
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
   * @param source a 3 x 3 matrix
   * @see AffineTransform#setTransform(double, double, double, double,
   *      double, double)
   * @see Graphics2D#setTransform(AffineTransform)
   */
  @Override
  public void setMatrix ( final PMatrix2D source ) {

    this.setMatrix(
        source.m00, source.m01, source.m02,
        source.m10, source.m11, source.m12);
  }

  /**
   * Set size is the last function called by size, createGraphics,
   * makeGraphics, etc. when initializing the graphics renderer.
   * Therefore, any additional values that need initialization can be
   * attempted here.
   *
   * @param width  the applet width
   * @param height the applet height
   */
  @Override
  public void setSize ( final int width, final int height ) {

    this.width = width;
    this.height = height;

    /*
     * Method signature: m00: scale x, m10: shear y, m01: shear x, m11:
     * scale y, m02: translation x, m12: translation y .
     */
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
   * Draws a 2D curve entity.
   *
   * @param entity the curve entity
   */
  public void shape ( final CurveEntity2 entity ) {

    final Transform2 tr = entity.transform;
    final List < Curve2 > curves = entity.curves;
    final Iterator < Curve2 > curveItr = curves.iterator();

    final Vec2 v0 = new Vec2();
    final Vec2 v1 = new Vec2();
    final Vec2 v2 = new Vec2();

    Knot2 currKnot = null;
    Knot2 prevKnot = null;
    Vec2 coord = null;
    Vec2 foreHandle = null;
    Vec2 rearHandle = null;

    while ( curveItr.hasNext() ) {
      final Curve2 curve = curveItr.next();
      final Iterator < Knot2 > knItr = curve.iterator();
      prevKnot = knItr.next();
      coord = prevKnot.coord;

      Transform2.mulPoint(tr, coord, v2);

      this.gp.reset();
      this.gp.moveTo(v2.x, v2.y);

      while ( knItr.hasNext() ) {
        currKnot = knItr.next();
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

      if ( curve.closedLoop ) {
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

      this.drawShapeSolid(this.gp);
    }
  }

  /**
   * Draws a 2D curve entity.
   *
   * @param entity   the curve entity
   * @param material the material
   */
  public void shape (
      final CurveEntity2 entity,
      final MaterialSolid material ) {

    this.pushStyle();
    this.material(material);
    this.shape(entity);
    this.popStyle();
  }

  /**
   * Draws a 2D curve entity.
   *
   * @param entity    the curve entity
   * @param materials an array of materials
   */
  public void shape (
      final CurveEntity2 entity,
      final MaterialSolid[] materials ) {

    /*
     * For performance, better to use classes instead of interfaces, i.e.
     * ArrayList or LinkedList instead of List. However, a generic list is
     * easier on implementation.
     */

    final Transform2 tr = entity.transform;
    final List < Curve2 > curves = entity.curves;
    final Iterator < Curve2 > curveItr = curves.iterator();

    final Vec2 v0 = new Vec2();
    final Vec2 v1 = new Vec2();
    final Vec2 v2 = new Vec2();

    Knot2 currKnot = null;
    Knot2 prevKnot = null;
    Vec2 coord = null;
    Vec2 foreHandle = null;
    Vec2 rearHandle = null;

    while ( curveItr.hasNext() ) {
      final Curve2 curve = curveItr.next();
      this.pushStyle();
      this.material(materials[curve.materialIndex]);

      final Iterator < Knot2 > knItr = curve.iterator();
      prevKnot = knItr.next();
      coord = prevKnot.coord;

      Transform2.mulPoint(tr, coord, v2);
      this.gp.reset();
      this.gp.moveTo(v2.x, v2.y);

      while ( knItr.hasNext() ) {
        currKnot = knItr.next();
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

      if ( curve.closedLoop ) {
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

      this.drawShapeSolid(this.gp);
      this.popStyle();
    }
  }

  /**
   * Draws a mesh entity.
   *
   * @param entity the mesh entity
   */
  public void shape ( final MeshEntity2 entity ) {

    final Transform2 tr = entity.transform;
    final List < Mesh2 > meshes = entity.meshes;
    final Iterator < Mesh2 > meshItr = meshes.iterator();

    final Vec2 v = new Vec2();

    while ( meshItr.hasNext() ) {
      final Mesh2 mesh = meshItr.next();
      final Vec2[] vs = mesh.coords;
      final int[][][] fs = mesh.faces;
      final int flen0 = fs.length;

      for ( int i = 0; i < flen0; ++i ) {
        final int[][] f = fs[i];
        final int flen1 = f.length;

        Transform2.mulPoint(tr, vs[f[0][0]], v);
        this.gp.reset();
        this.gp.moveTo(v.x, v.y);

        for ( int j = 1; j < flen1; ++j ) {

          Transform2.mulPoint(tr, vs[f[j][0]], v);
          this.gp.lineTo(v.x, v.y);

        }

        this.gp.closePath();
        this.drawShapeSolid(this.gp);
      }
    }
  }

  /**
   * Draws a mesh entity.
   *
   * @param entity   the mesh entity
   * @param material the material
   */
  public void shape (
      final MeshEntity2 entity,
      final MaterialSolid material ) {

    this.pushStyle();
    this.material(material);
    this.shape(entity);
    this.popStyle();
  }

  /**
   * Draws a mesh entity.
   *
   * @param entity    the mesh entity
   * @param materials the materials array
   */
  public void shape (
      final MeshEntity2 entity,
      final MaterialSolid[] materials ) {

    final Transform2 tr = entity.transform;
    final List < Mesh2 > meshes = entity.meshes;
    final Iterator < Mesh2 > meshItr = meshes.iterator();

    final Vec2 v = new Vec2();

    while ( meshItr.hasNext() ) {
      final Mesh2 mesh = meshItr.next();
      final Vec2[] vs = mesh.coords;
      final int[][][] fs = mesh.faces;
      final int flen0 = fs.length;

      this.pushStyle();
      this.material(materials[mesh.materialIndex]);

      for ( int i = 0; i < flen0; ++i ) {
        final int[][] f = fs[i];
        final int flen1 = f.length;

        Transform2.mulPoint(tr, vs[f[0][0]], v);
        this.gp.reset();
        this.gp.moveTo(v.x, v.y);

        for ( int j = 1; j < flen1; ++j ) {

          Transform2.mulPoint(tr, vs[f[j][0]], v);
          this.gp.lineTo(v.x, v.y);

        }

        this.gp.closePath();
        this.drawShapeSolid(this.gp);
      }

      this.popStyle();
    }
  }

  /**
   * Displays a PShape. Use of this function is discouraged by this
   * renderer. See mesh and curve entities instead.
   *
   * @param shape the PShape
   * @param x     the x coordinate
   * @param y     the y coordinate
   */
  @Override
  public void shape (
      final PShape shape,
      final float x,
      final float y ) {

    PApplet.showMissingWarning("shape");
    super.shape(shape, x, y);
  }

  /**
   * Displays a PShape. The meaning of the four parameters depends on
   * shapeMode. Use of this function is discouraged by this renderer.
   * See mesh and curve entities instead.
   *
   * @param shape the PShape
   * @param x1    the first x coordinate
   * @param y1    the first y coordinate
   * @param x2    the second x coordinate
   * @param y2    the second y coordinated
   */
  @Override
  public void shape (
      final PShape shape,
      final float x1,
      final float y1,
      final float x2,
      final float y2 ) {

    PApplet.showMissingWarning("shape");
    super.shape(shape, x1, y1, x2, y2);
  }

  /**
   * Applies a shear transform to the renderer.
   *
   * @param v the shear
   */
  @Override
  public void shear ( final Vec2 v ) { this.g2.shear(v.x, v.y); }

  /**
   * Draws a square at a location.
   *
   * @param coord the location
   * @param size  the size
   */
  @Override
  public void square (
      final Vec2 coord,
      final float size ) {

    this.rectImpl(coord.x, coord.y, size, size);
  }

  /**
   * Draws a rounded square.
   *
   * @param coord    the location
   * @param size     the size
   * @param rounding the corner rounding
   */
  @Override
  public void square (
      final Vec2 coord,
      final float size,
      final float rounding ) {

    this.rectImpl(
        coord.x, coord.y,
        size, size,
        rounding, rounding,
        rounding, rounding);
  }

  /**
   * Sets the renderer's current stroke to the color.
   *
   * @param c the color
   */
  @Override
  public void stroke ( final camzup.core.Color c ) {

    this.colorCalc(c);
    this.strokeFromCalc();
    this.strokeColorObject = new java.awt.Color(
        this.strokeColor, true);
    this.strokeGradient = false;
  }

  /**
   * Sets the stroke cap on the ends of strokes with a PConstant:
   * SQUARE, PROJECT, and ROUND.
   *
   * @param cap the constant
   * @see YupJ2#chooseStrokeCap(int)
   * @see YupJ2#strokeImpl()
   */
  @Override
  public void strokeCap ( final int cap ) {

    this.chooseStrokeCap(cap);
    this.strokeImpl();
  }

  /**
   * Sets the stroke join on the ends of strokes with a PConstant:
   * BEVEL, MITER, and ROUND.
   *
   * @param join the constant
   * @see YupJ2#chooseStrokeJoin(int)
   * @see YupJ2#strokeImpl()
   */
  @Override
  public void strokeJoin ( final int join ) {

    this.chooseStrokeJoin(join);
    this.strokeImpl();
  }

  /**
   * Sets the width of the stroke used for lines, points, and the border
   * around shapes. The weight should be a positive, non-zero value.
   *
   * @param weight the stroke weight
   * @see YupJ2#strokeImpl()
   */
  @Override
  public void strokeWeight ( final float weight ) {

    /*
     * The lower bound of the stroke weight has to be < 1.0 because of
     * scaling's impact on stroke weight.
     */
    this.strokeWeight = weight > IUtils.DEFAULT_EPSILON ? weight
        : IUtils.DEFAULT_EPSILON;
    this.strokeImpl();
  }

  /**
   * Displays a character at a location.
   *
   * @param character the character
   * @param x         the x coordinate
   * @param y         the y coordinate
   */
  @Override
  public void text (
      final char character,
      final float x,
      final float y ) {

    float yMut = y;

    if ( this.textFont == null ) { this.defaultFontOrDeath("text"); }

    switch ( this.textAlignY ) {

      case BOTTOM:

        yMut += this.textDescent();

        break;

      case TOP:

        yMut -= this.textAscent();

        break;

      case CENTER:

      default:

        yMut -= this.textAscent() * 0.5f;
    }

    this.textBuffer[0] = character;
    this.textLineAlignImpl(this.textBuffer, 0, 1, x, yMut);
  }

  /**
   * Displays an array of characters at a location.
   *
   * @param chars the array of characters
   * @param start the start index, inclusive
   * @param stop  the stop index, exclusive
   * @param x     the x coordinate
   * @param y     the y coordinate
   */
  @Override
  public void text (
      final char[] chars,
      final int start,
      final int stop,
      final float x,
      final float y ) {

    float high = 0.0f;
    float yMut = y;
    int stMut = start;
    for ( int i = stMut; i < stop; ++i ) {
      if ( chars[i] == '\n' ) { high += this.textLeading; }
    }

    switch ( this.textAlignY ) {

      case BOTTOM:

        yMut += this.textDescent() + high;

        break;

      case TOP:

        yMut -= this.textAscent();

        break;

      case CENTER:

      default:

        yMut -= (this.textAscent() - high) * 0.5f;
    }

    int index = 0;
    while ( index < stop ) {
      if ( chars[index] == '\n' ) {
        this.textLineAlignImpl(chars, stMut, index, x, yMut);
        stMut = index + 1;
        yMut -= this.textLeading;
      }
      index++;
    }

    if ( stMut < stop ) {
      this.textLineAlignImpl(chars, stMut, index, x, yMut);
    }
  }

  /**
   * Sets the text mode to either MODEL or SHAPE.
   *
   * @param mode the text mode
   */
  @Override
  public void textMode ( final int mode ) {

    if ( mode == PConstants.MODEL || mode == PConstants.SHAPE ) {
      this.textMode = mode;
    }
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
   * Sets the renderer's current stroke to the tint.
   *
   * @param c the color
   */
  @Override
  public void tint ( final camzup.core.Color c ) {

    this.colorCalc(c);
    this.tintFromCalc();
    this.tintColorObject = new java.awt.Color(
        this.tintColor, true);
  }

  /**
   * Returns the string representation of this renderer.
   *
   * @return the string
   */
  @Override
  public String toString ( ) { return YupJ2.PATH_STR; }

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
   * @see Graphics2D#translate(double, double)
   * @see Graphics2D#rotate(double)
   * @see Graphics2D#scale(double, double)
   * @see Transform2#getLocation(Vec2)
   * @see Transform2#getRotation()
   * @see Transform2#getScale(Vec2)
   */
  public void transform (
      final Transform2 tr2,
      final TransformOrder order ) {

    final Vec2 dim = tr2.getScale(this.tr2Scale);
    final Vec2 loc = tr2.getLocation(this.tr2Loc);
    final float angle = tr2.getRotation();

    switch ( order ) {

      case RST:

        this.g2.rotate(angle);
        this.g2.scale(dim.x, dim.y);
        this.g2.translate(loc.x, loc.y);

        return;

      case RTS:

        this.g2.rotate(angle);
        this.g2.translate(loc.x, loc.y);
        this.g2.scale(dim.x, dim.y);

        return;

      case SRT:

        this.g2.scale(dim.x, dim.y);
        this.g2.rotate(angle);
        this.g2.translate(loc.x, loc.y);

        return;

      case STR:

        this.g2.scale(dim.x, dim.y);
        this.g2.translate(loc.x, loc.y);
        this.g2.rotate(angle);

        return;

      case TSR:

        this.g2.translate(loc.x, loc.y);
        this.g2.scale(dim.x, dim.y);
        this.g2.rotate(angle);

        return;

      case TRS:

      default:

        this.g2.translate(loc.x, loc.y);
        this.g2.rotate(angle);
        this.g2.scale(dim.x, dim.y);

        return;
    }
  }

  /**
   * Translates the renderer by a vector.
   *
   * @param v the vector
   * @see Graphics2D#translate(double, double)
   */
  public void translate ( final Vec2 v ) {

    this.g2.translate(v.x, v.y);
  }

  /**
   * Draws a triangle from three 2D coordinates.
   *
   * @param x1 first corner x
   * @param y1 first corner y
   * @param x2 second corner x
   * @param y2 second corner y
   * @param x3 third corner x
   * @param y3 third corner y
   * @see Path2D#reset()
   * @see Path2D#moveTo(double, double)
   * @see Path2D#lineTo(double, double)
   * @see Path2D#closePath()
   * @see YupJ2#drawShapeSolid(Shape)
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
    this.drawShapeSolid(this.gp);
  }

  /**
   * Draws a triangle from three coordinates.
   *
   * @param a the first coordinate
   * @param b the second coordinate
   * @param c the third coordinate
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
   * The overridden functionality eliminates unnecessary checks.
   */
  @Override
  public void updatePixels ( ) {

    if ( this.pixels != null ) {
      this.getRaster().setDataElements(
          0, 0,
          this.pixelWidth, this.pixelHeight,
          this.pixels);
    }
    this.modified = true;
  }

  /**
   * Adds another vertex to a shape between the beginShape and endShape
   * commands.
   *
   * @param v the coordinate
   */
  @Override
  public void vertex ( final Vec2 v ) { this.vertex(v.x, v.y); }

  /**
   * The path string for this renderer.
   */
  public static final String PATH_STR = "camzup.pfriendly.YupJ2";
}

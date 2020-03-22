package camzup.pfriendly;

import java.util.Iterator;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import processing.core.PImage;
import processing.core.PMatrix2D;
import processing.core.PMatrix3D;
import processing.core.PShape;
import processing.opengl.PGraphicsOpenGL;

import camzup.core.ArcMode;
import camzup.core.Color;
import camzup.core.Curve2;
import camzup.core.Experimental;
import camzup.core.IUtils;
import camzup.core.Knot2;
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

/**
 * An abstract parent class for Processing renderers based on OpenGL.
 */
public abstract class UpOgl extends PGraphicsOpenGL implements IUpOgl {

  /**
   * A curve to hold the arc data.
   */
  protected final Curve2 arc = new Curve2();

  /**
   * The arc-mode.
   */
  protected ArcMode arcMode = ArcMode.OPEN;

  /**
   * A placeholder color used during lerpColor.
   */
  protected Color aTemp = new Color();

  /**
   * A placeholder color used during lerpColor.
   */
  protected Color bTemp = new Color();

  /**
   * A placeholder color used during lerpColor.
   */
  protected Color cTemp = new Color();

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
   * The default constructor.
   */
  public UpOgl ( ) {

    super();
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
  public UpOgl (
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
  protected void applyMatrixImpl (
      final float n00, final float n01, final float n02, final float n03,
      final float n10, final float n11, final float n12, final float n13,
      final float n20, final float n21, final float n22, final float n23,
      final float n30, final float n31, final float n32, final float n33 ) {

    this.modelview.apply(
        n00, n01, n02, n03,
        n10, n11, n12, n13,
        n20, n21, n22, n23,
        n30, n31, n32, n33);

    PMatAux.inverse(this.modelview, this.modelviewInv);

    this.projmodelview.apply(
        n00, n01, n02, n03,
        n10, n11, n12, n13,
        n20, n21, n22, n23,
        n30, n31, n32, n33);
  }

  /**
   * Draws the OpenGL implementation of the arc function using a curve
   * and transform.
   *
   * @param curve     the arc curve
   * @param transform the transform
   * @param trOrder   the transform order
   */
  protected void arcImpl (
      final Curve2 curve,
      final Transform2 transform,
      final TransformOrder trOrder ) {

    Knot2 currKnot = null;
    Knot2 prevKnot = null;
    Vec2 coord = null;
    Vec2 foreHandle = null;
    Vec2 rearHandle = null;

    final Iterator < Knot2 > itr = curve.iterator();
    prevKnot = itr.next();
    coord = prevKnot.coord;

    this.pushMatrix();
    this.transform(transform, trOrder);

    final float oldSw = this.strokeWeight;
    final float swLine = oldSw /
        Transform2.minDimension(transform);
    this.strokeWeight = swLine;

    this.beginShape(PConstants.POLYGON);
    this.normal(0.0f, 0.0f, 1.0f);
    this.vertexImpl(
        coord.x, coord.y, 0.0f,
        this.textureU, this.textureV);

    while ( itr.hasNext() ) {

      currKnot = itr.next();
      foreHandle = prevKnot.foreHandle;
      rearHandle = currKnot.rearHandle;
      coord = currKnot.coord;

      this.bezierVertexImpl(
          foreHandle.x, foreHandle.y, 0.0f,
          rearHandle.x, rearHandle.y, 0.0f,
          coord.x, coord.y, 0.0f);

      prevKnot = currKnot;
    }

    if ( curve.closedLoop ) {
      currKnot = curve.getFirst();
      foreHandle = prevKnot.foreHandle;
      rearHandle = currKnot.rearHandle;
      coord = currKnot.coord;

      this.bezierVertexImpl(
          foreHandle.x, foreHandle.y, 0.0f,
          rearHandle.x, rearHandle.y, 0.0f,
          coord.x, coord.y, 0.0f);
      this.endShape(PConstants.CLOSE);
    } else {
      this.endShape(PConstants.OPEN);
    }

    this.strokeWeight = oldSw;
    this.popMatrix();
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
  protected void colorCalc (
      final float x,
      final float y,
      final float z,
      final float a ) {

    this.colorCalc(x, y, z, a, true);
  }

  /**
   * Calculates a color from an integer and alpha value. Useful in the
   * IDE for colors defined with hash-tag literals, such as "#aabbcc" .
   *
   * @param argb  the hexadecimal color
   * @param alpha the alpha channel
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
   * Initializes the curve basis and draw matrix.
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
   * Sets the renderer's default styling.
   */
  @Override
  protected void defaultSettings ( ) {

    this.colorMode(PConstants.RGB, IUp.DEFAULT_COLOR_MAX);
    this.fill(IUp.DEFAULT_FILL_COLOR);

    this.stroke(IUp.DEFAULT_STROKE_COLOR);
    this.strokeWeight(IUp.DEFAULT_STROKE_WEIGHT);
    this.strokeJoin(PConstants.ROUND);
    this.strokeCap(PConstants.ROUND);
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

    this.textureMode = PConstants.NORMAL;
    this.ambient(
        this.colorModeX * IUpOgl.DEFAULT_AMB_R,
        this.colorModeY * IUpOgl.DEFAULT_AMB_G,
        this.colorModeZ * IUpOgl.DEFAULT_AMB_B);
    this.specular(
        this.colorModeX * IUpOgl.DEFAULT_SPEC_R,
        this.colorModeY * IUpOgl.DEFAULT_SPEC_G,
        this.colorModeZ * IUpOgl.DEFAULT_SPEC_B);
    this.emissive(0.0f, 0.0f, 0.0f);
    this.shininess(0.0f);

    this.setAmbient = false;
    this.manipulatingCamera = false;
    this.settingsInited = true;
    this.reapplySettings = false;

    this.hint(PConstants.DISABLE_OPENGL_ERRORS);
  }

  /**
   * Draws a textured mesh as multiplied by a transform. Supplied
   * temporary vectors hold model and texture coordinates.
   *
   * @param mesh the mesh
   * @param tr   the transform
   * @param mat  the material
   * @param v    a temporary vector
   * @param vt   a temporary vector
   */
  protected void drawMesh2 (
      final Mesh2 mesh,
      final Transform2 tr,
      final MaterialPImage mat,
      final Vec2 v,
      final Vec2 vt ) {

    final PImage texture = mat.texture;
    final Transform2 uvtr = mat.transform;
    this.tint(mat.tint);

    final Vec2[] vs = mesh.coords;
    final Vec2[] vts = mesh.texCoords;
    final int[][][] fs = mesh.faces;
    final int flen0 = fs.length;

    for ( int i = 0; i < flen0; ++i ) {

      final int[][] f = fs[i];
      final int flen1 = f.length;

      this.beginShape(PConstants.POLYGON);
      this.normal(0.0f, 0.0f, 1.0f);
      this.texture(texture);

      for ( int j = 0; j < flen1; ++j ) {

        final int[] data = f[j];
        final int vIndex = data[0];
        final int vtIndex = data[1];

        Transform2.mulPoint(tr, vs[vIndex], v);
        Transform2.mulTexCoord(uvtr, vts[vtIndex], vt);

        this.vertexImpl(v.x, v.y, 0.0f, vt.x, vt.y);
      }
      this.endShape(PConstants.CLOSE);
    }
  }

  /**
   * Draws a mesh as multiplied by a transform. The supplied temporary
   * vector holds the transformed coordinate.
   *
   * @param mesh the mesh
   * @param tr   the transform
   * @param v    a temporary vector
   */
  protected void drawMesh2 (
      final Mesh2 mesh,
      final Transform2 tr,
      final Vec2 v ) {

    final Vec2[] vs = mesh.coords;
    final int[][][] fs = mesh.faces;
    final int flen0 = fs.length;

    for ( int i = 0; i < flen0; ++i ) {

      final int[][] f = fs[i];
      final int flen1 = f.length;

      this.beginShape(PConstants.POLYGON);
      this.normal(0.0f, 0.0f, 1.0f);

      for ( int j = 0; j < flen1; ++j ) {
        Transform2.mulPoint(tr, vs[f[j][0]], v);
        this.vertexImpl(
            v.x, v.y, 0.0f,
            this.textureU, this.textureV);
      }
      this.endShape(PConstants.CLOSE);
    }
  }

  /**
   * Draws a textured mesh as multiplied by a transform. Supplied
   * temporary vectors hold model, texture coordinates and normals.
   *
   * @param mesh the mesh
   * @param tr   the transform
   * @param mat  the material
   * @param v    a temporary vector
   * @param vt   a temporary vector
   * @param vn   a temporary vector
   */
  @Experimental
  protected void drawMesh3 (
      final Mesh3 mesh,
      final Transform3 tr,
      final MaterialPImage mat,
      final Vec3 v,
      final Vec2 vt,
      final Vec3 vn ) {

    final PImage texture = mat.texture;
    final Transform2 uvtr = mat.transform;
    this.tint(mat.tint);

    final Vec3[] vs = mesh.coords;
    final Vec3[] vns = mesh.normals;
    final Vec2[] vts = mesh.texCoords;
    final int[][][] fs = mesh.faces;
    final int flen0 = fs.length;

    for ( int i = 0; i < flen0; ++i ) {

      final int[][] f = fs[i];
      final int flen1 = f.length;
      this.beginShape(PConstants.POLYGON);
      this.texture(texture);

      for ( int j = 0; j < flen1; ++j ) {

        final int[] data = f[j];
        final int vIndex = data[0];
        final int vtIndex = data[1];
        final int vnIndex = data[2];

        Transform3.mulPoint(tr, vs[vIndex], v);
        Transform2.mulTexCoord(uvtr, vts[vtIndex], vt);
        Transform3.mulDir(tr, vns[vnIndex], vn);

        this.normal(vn.x, vn.y, vn.z);
        this.vertexImpl(v.x, v.y, v.z, vt.x, vt.y);
      }
      this.endShape(PConstants.CLOSE);
    }
  }

  /**
   * Draws a mesh as multiplied by a transform. Supplied temporary
   * vectors hold the transformed coordinate and normal.
   *
   * @param mesh the mesh
   * @param tr   the transform
   * @param v    a temporary vector
   * @param vn   a temporary vector
   */
  @Experimental
  protected void drawMesh3 (
      final Mesh3 mesh,
      final Transform3 tr,
      final Vec3 v,
      final Vec3 vn ) {

    final Vec3[] vs = mesh.coords;
    final Vec3[] vns = mesh.normals;
    final int[][][] fs = mesh.faces;
    final int flen0 = fs.length;

    for ( int i = 0; i < flen0; ++i ) {

      final int[][] f = fs[i];
      final int flen1 = f.length;
      this.beginShape(PConstants.POLYGON);

      for ( int j = 0; j < flen1; ++j ) {

        final int[] data = f[j];
        final int vIndex = data[0];
        final int vnIndex = data[2];

        Transform3.mulPoint(tr, vs[vIndex], v);
        Transform3.mulDir(tr, vns[vnIndex], vn);

        this.normal(vn.x, vn.y, vn.z);
        this.vertexImpl(
            v.x, v.y, v.z,
            this.textureU, this.textureV);
      }
      this.endShape(PConstants.CLOSE);
    }
  }

  /**
   * The renderer specific implementation of Processing's image
   * function. This uses integers to specify the UV coordinates for
   * unknown reasons; it defers to a public, single precision real
   * number function.
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
  protected void imageImpl (
      final PImage img,
      final float x1, final float y1,
      final float x2, final float y2,
      final int u1, final int v1,
      final int u2, final int v2 ) {

    /*
     * This is backwards due to Processing's insistence on specifying UV
     * coordinates with integers (maybe as a result from working with
     * AWT?). All image functions should flow into this, but instead this
     * flows into a public image implementation.
     */

    final int savedTextureMode = this.textureMode;
    this.textureMode = PConstants.IMAGE;

    /*
     * This will have to go untested... as this code is being written on a
     * low density monitor.
     */
    final int pd = img.pixelDensity;
    this.imageImpl(
        img,
        x1, y1, x2, y2, 0.0f,
        u1 * pd, v1 * pd, u2 * pd, v2 * pd);

    this.textureMode = savedTextureMode;
  }

  /**
   * Sets ambient lighting. Color channels will be submitted to color
   * calculation.
   *
   * @param num   the index
   * @param red   the red channel
   * @param green the green channel
   * @param blue  the blue channel
   */
  @Override
  protected void lightAmbient (
      final int num,
      final float red,
      final float green,
      final float blue ) {

    this.colorCalc(red, green, blue, this.colorModeA, false);

    final int num3 = num + num + num;
    this.lightAmbient[num3] = this.calcR;
    this.lightAmbient[num3 + 1] = this.calcG;
    this.lightAmbient[num3 + 2] = this.calcB;
  }

  /**
   * Sets diffuse lighting. Color channels will be submitted to color
   * calculation.
   *
   * @param num   the index
   * @param red   the red channel
   * @param green the green channel
   * @param blue  the blue channel
   */
  @Override
  protected void lightDiffuse (
      final int num,
      final float red,
      final float green,
      final float blue ) {

    this.colorCalc(red, green, blue, this.colorModeA, false);

    final int num3 = num + num + num;
    this.lightDiffuse[num3] = this.calcR;
    this.lightDiffuse[num3 + 1] = this.calcG;
    this.lightDiffuse[num3 + 2] = this.calcB;
  }

  /**
   * Sets lighting fall-off.
   *
   * @param num the index
   * @param c0  the first factor
   * @param c1  the second factor
   * @param c2  the third factor
   */
  @Override
  protected void lightFalloff (
      final int num,
      final float c0,
      final float c1,
      final float c2 ) {

    final int num3 = num + num + num;
    this.lightFalloffCoefficients[num3] = c0;
    this.lightFalloffCoefficients[num3 + 1] = c1;
    this.lightFalloffCoefficients[num3 + 2] = c2;
  }

  /**
   * Sets lighting normals.
   *
   * @param num  the index
   * @param xDir the direction x
   * @param yDir the direction y
   * @param zDir the direction z
   */
  @Override
  protected void lightNormal (
      final int num,
      final float xDir,
      final float yDir,
      final float zDir ) {

    /*
     * Applying normal matrix to the light direction vector, which is the
     * transpose of the inverse of the model view.
     */
    final float nx = xDir * this.modelviewInv.m00 +
        yDir * this.modelviewInv.m10 +
        zDir * this.modelviewInv.m20;

    final float ny = xDir * this.modelviewInv.m01 +
        yDir * this.modelviewInv.m11 +
        zDir * this.modelviewInv.m21;

    final float nz = xDir * this.modelviewInv.m02 +
        yDir * this.modelviewInv.m12 +
        zDir * this.modelviewInv.m22;

    final float mSq = nx * nx + ny * ny + nz * nz;
    final int num3 = num + num + num;
    if ( 0.0f < mSq ) {
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
   * Sets positional lighting.
   *
   * @param num the index
   * @param x   the x component
   * @param y   the y component
   * @param z   the z component
   * @param dir treat as a direction or point
   */
  @Override
  protected void lightPosition (
      final int num,
      final float x,
      final float y,
      final float z,
      final boolean dir ) {

    final int num4 = num + num + num + num;

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

    /*
     * The w component is 0.0 when the vector represents a direction, 1.0
     * when the vector represents a point.
     */
    this.lightPosition[num4 + 3] = dir ? 0.0f : 1.0f;
  }

  /**
   * Sets specular lighting. Assumes that red, green and blue color
   * channels have already been calculated appropriately.
   *
   * @param num   the index
   * @param red   the red channel
   * @param green the green channel
   * @param blue  the blue channel
   */
  @Override
  protected void lightSpecular (
      final int num,
      final float red,
      final float green,
      final float blue ) {

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
  protected void lightSpot (
      final int num,
      final float radians,
      final float exponent ) {

    final int num2 = num + num;
    this.lightSpotParameters[num2] = Utils.max(0.0f, Utils.cos(radians));
    this.lightSpotParameters[num2 + 1] = exponent;
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
   * Draws a rounded rectangle. The meaning of the first four parameters
   * depends on rectMode.
   *
   * @param a  the first x parameter
   * @param b  the first y parameter
   * @param c  the second x parameter
   * @param d  the second y parameter
   * @param tl the top-left corner rounding
   * @param tr the top-right corner rounding
   * @param br the bottom-right corner rounding
   * @param bl the bottom-left corner rounding
   * @see Utils#abs(float)
   * @see Utils#min(float, float)
   * @see Utils#max(float, float)
   * @see Utils#clamp(float, float, float)
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

    switch ( this.rectMode ) {
      case CORNER:

        w = Utils.abs(c);
        h = Utils.abs(d);

        x1 = a;
        y1 = b - h;
        x2 = a + w;
        y2 = b;

        break;

      case CORNERS:

        w = Utils.abs(c - a);
        h = Utils.abs(b - d);

        x1 = Utils.min(a, c);
        x2 = Utils.max(a, c);

        y1 = Utils.min(b, d);
        y2 = Utils.max(b, d);

        break;

      case RADIUS:

        w = Utils.abs(c);
        h = Utils.abs(d);

        x1 = a - w;
        x2 = a + w;
        y1 = b - h;
        y2 = b + h;

        break;

      case CENTER:
      default:

        w = Utils.abs(c);
        h = Utils.abs(d);

        x1 = a - w * 0.5f;
        x2 = a + w * 0.5f;
        y1 = b - h * 0.5f;
        y2 = b + h * 0.5f;

    }

    final float limit = Utils.min(w, h) * 0.5f;
    tl = Utils.clamp(tl, IUtils.DEFAULT_EPSILON, limit);
    tr = Utils.clamp(tr, IUtils.DEFAULT_EPSILON, limit);
    br = Utils.clamp(br, IUtils.DEFAULT_EPSILON, limit);
    bl = Utils.clamp(bl, IUtils.DEFAULT_EPSILON, limit);

    this.beginShape(PConstants.POLYGON);
    this.normal(0.0f, 0.0f, 1.0f);

    this.vertexImpl(
        x2 - tr, y1, 0.0f,
        this.textureU, this.textureV);
    this.quadraticVertexImpl(
        x2, y1, 0.0f,
        x2, y1 + tr, 0.0f);

    this.vertexImpl(
        x2, y2 - br, 0.0f,
        this.textureU, this.textureV);
    this.quadraticVertexImpl(
        x2, y2, 0.0f,
        x2 - br, y2, 0.0f);

    this.vertexImpl(
        x1 + bl, y2, 0.0f,
        this.textureU, this.textureV);
    this.quadraticVertexImpl(
        x1, y2, 0.0f,
        x1, y2 - bl, 0.0f);

    this.vertexImpl(
        x1, y1 + tl, 0.0f,
        this.textureU, this.textureV);
    this.quadraticVertexImpl(
        x1, y1, 0.0f,
        x1 + tl, y1, 0.0f);

    this.endShape(PConstants.CLOSE);
  }

  /**
   * Rotates the renderer's model view matrix by an angle in radians
   * around an axis. Normalizes the axis if it is not of unit length.
   *
   * @param radians the angle in radians
   * @param xAxis   the axis x component
   * @param yAxis   the axis y component
   * @param zAxis   the axis z component
   */
  @Override
  protected void rotateImpl (
      final float radians,
      final float xAxis,
      final float yAxis,
      final float zAxis ) {

    /* Axis is verified here because PMatAux compound rotate will not. */
    final float mSq = xAxis * xAxis + yAxis * yAxis + zAxis * zAxis;
    if ( mSq == 0.0f ) { return; }

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

    PMatAux.compoundRotate(
        cosa, sina, xn, yn, zn,
        this.modelview, this.modelviewInv);
    PMatAux.mul(
        this.projection,
        this.modelview,
        this.projmodelview);
  }

  /**
   * Scales the renderer by a non-uniform scalar.
   *
   * @param sx the scale x
   * @param sy the scale y
   * @param sz the scale z
   */
  @Override
  protected void scaleImpl (
      final float sx,
      final float sy,
      final float sz ) {

    this.modelview.scale(sx, sy, sz);
    PMatAux.invScale(sx, sy, sz, this.modelviewInv);
    this.projmodelview.scale(sx, sy, sz);
  }

  /**
   * Draws a character depending on the text mode.
   *
   * @param ch the character
   * @param x  the x coordinate
   * @param y  the y coordinate
   */
  @Override
  protected void textCharImpl (
      final char ch,
      final float x,
      final float y ) {

    switch ( this.textMode ) {

      case SHAPE:

        this.textCharShapeImpl(ch, x, y);

        break;

      case MODEL:

      default:

        this.textCharModelImpl(ch, x, y);
    }
  }

  /**
   * Draws a character with reference to a a glyph retrieved from the
   * current font.
   *
   * @param ch the character
   * @param x  the x coordinate
   * @param y  the y coordinate
   * @see PFont#getGlyph(char)
   * @see PFont#getSize()
   */
  protected void textCharModelImpl (
      final char ch,
      final float x,
      final float y ) {

    /*
     * FontTexture.TextureInfo is a static inner class, and so cannot be
     * accessed from here.
     */

    final PFont.Glyph glyph = this.textFont.getGlyph(ch);
    if ( glyph != null ) {
      final float invSz = 1.0f / this.textFont.getSize();
      final float wGlyph = glyph.width * invSz;
      final float hGlyph = glyph.height * invSz;
      final float lextent = glyph.leftExtent * invSz;
      final float textent = glyph.topExtent * invSz;

      final float x0 = x + lextent * this.textSize;
      final float x1 = x0 + wGlyph * this.textSize;

      final float y0 = y + textent * this.textSize;
      final float y1 = y0 - hGlyph * this.textSize;

      final PImage texture = glyph.image;

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

      this.beginShape(PConstants.POLYGON);
      this.normal(0.0f, 0.0f, 1.0f);
      this.texture(texture);
      this.vertexImpl(x0, y0, 0.0f, 0.0f, 0.0f);
      this.vertexImpl(x1, y0, 0.0f, 1.0f, 0.0f);
      this.vertexImpl(x1, y1, 0.0f, 1.0f, 1.0f);
      this.vertexImpl(x0, y1, 0.0f, 0.0f, 1.0f);
      this.endShape(PConstants.CLOSE);

      this.tint = savedTint;
      this.tintColor = savedTintColor;
      this.tintR = savedTintR;
      this.tintG = savedTintG;
      this.tintB = savedTintB;
      this.tintA = savedTintA;
      this.tintAlpha = savedTintAlpha;
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
      final float x1,
      final float y1,
      final float x2,
      final float y2,
      final int u,
      final int v ) {

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

    this.imageImpl(
        glyph,
        x1, y1, x2, y2, 0.0f,
        0.0f, 0.0f, u, v);

    this.tint = savedTint;
    this.tintColor = savedTintColor;
    this.tintR = savedTintR;
    this.tintG = savedTintG;
    this.tintB = savedTintB;
    this.tintA = savedTintA;
    this.tintAlpha = savedTintAlpha;
  }

  /**
   * Translates the renderer by a vector.
   *
   * @param tx the translation x
   * @param ty the translation y
   * @param tz the translation z
   */
  @Override
  protected void translateImpl (
      final float tx,
      final float ty,
      final float tz ) {

    this.modelview.translate(tx, ty, tz);
    PMatAux.invTranslate(tx, ty, tz, this.modelviewInv);
    this.projmodelview.translate(tx, ty, tz);
  }

  /**
   * Transfers the model view from a PMatrix3D to a one-dimensional
   * float array used by OpenGL.
   */
  @Override
  protected void updateGLModelview ( ) {

    this.updateGLModelview(this.modelview);
  }

  /**
   * Transfers the transpose of the model view inverse, which is the
   * normal matrix, from a PMatrix3D to a one-dimensional float array
   * used by OpenGL.
   */
  @Override
  protected void updateGLNormal ( ) {

    this.updateGLNormal(this.modelviewInv);
  }

  /**
   * Transfers the projection from a PMatrix3D to a one-dimensional
   * float array used by OpenGL.
   */
  @Override
  protected void updateGLProjection ( ) {

    this.updateGLProjection(this.projection);
  }

  /**
   * Transfers the project model view from a PMatrix3D to a
   * one-dimensional float array used by OpenGL.
   */
  @Override
  protected void updateGLProjmodelview ( ) {

    this.updateGLProjmodelview(this.projmodelview);
  }

  /**
   * Updates the texture coordinates of the renderer. If the texture
   * mode is IMAGE, divides the UV coordinates by the image's
   * dimensions. If the texture wrap is CLAMP, clamps the coordinates to
   * [0.0, 1.0] ; if the wrap is REPEAT, floor wraps them instead.
   *
   * @param u the s or u coordinate
   * @param v the t or v coordinate
   */
  @Override
  protected void vertexTexture (
      final float u,
      final float v ) {

    this.vertexTexture(u, v, this.textureMode, this.textureWrap);
  }

  /**
   * Updates the texture coordinates of the renderer. If the texture
   * mode is IMAGE, divides the UV coordinates by the image's
   * dimensions. If the texture wrap is CLAMP, clamps the coordinates to
   * [0.0, 1.0] ; if the wrap is REPEAT, wraps them instead.
   *
   * @param u                  the s or u coordinate
   * @param v                  the t or v coordinate
   * @param desiredTextureMode the texture mode, IMAGE or NORMAL
   * @param desiredTextureWrap the texture wrap, CLAMP or REPEAT
   * @see Utils#div(float, float)
   * @see Utils#clamp01(float)
   */
  protected void vertexTexture (
      final float u,
      final float v,
      final int desiredTextureMode,
      final int desiredTextureWrap ) {

    this.textureMode = desiredTextureMode;
    this.textureWrap = desiredTextureWrap;

    /* This operation is also performed by vertexImpl. */
    if ( this.textureMode == PConstants.IMAGE
        && this.textureImage != null ) {
      this.textureU = Utils.div(u, this.textureImage.width);
      this.textureV = Utils.div(v, this.textureImage.height);
    } else {
      this.textureU = u;
      this.textureV = v;
    }

    switch ( desiredTextureWrap ) {

      case PConstants.CLAMP:

        this.textureU = Utils.clamp01(this.textureU);
        this.textureV = Utils.clamp01(this.textureV);

        break;

      case PConstants.REPEAT:

        /*
         * Problem is where, in meshes which use cylindrical projection (UV
         * sphere, cylinder), u = 1.0 is returned to u = 0.0 by modulo.
         */

        // this.textureU = Utils.mod1(this.textureU);
        // this.textureV = Utils.mod1(this.textureV);

        break;

      default:

    }
  }

  /**
   * Applies the matrix to the renderer.
   *
   * @param source the source matrix
   */
  public void applyMatrix ( final Mat3 source ) {

    this.applyMatrixImpl(
        source.m00, source.m01, 0.0f, source.m02,
        source.m10, source.m11, 0.0f, source.m12,
        0.0f, 0.0f, 1.0f, 0.0f,
        source.m20, source.m21, 0.0f, source.m22);
  }

  /**
   * Applies the matrix to the renderer.
   *
   * @param source the source matrix
   */
  public void applyMatrix ( final Mat4 source ) {

    this.applyMatrixImpl(
        source.m00, source.m01, source.m02, source.m03,
        source.m10, source.m11, source.m12, source.m13,
        source.m20, source.m21, source.m22, source.m23,
        source.m30, source.m31, source.m32, source.m33);
  }

  /**
   * Applies the projection to the current.
   *
   * @param m the matrix
   */
  public void applyProjection ( final Mat4 m ) {

    this.applyProjection(
        m.m00, m.m01, m.m02, m.m03,
        m.m10, m.m11, m.m12, m.m13,
        m.m20, m.m21, m.m22, m.m23,
        m.m30, m.m31, m.m32, m.m33);
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

    switch ( this.ellipseMode ) {

      case CORNERS:

        w = Utils.diff(x0, x1);
        h = Utils.diff(y0, y1);
        x += w * 0.5f;
        y += h * 0.5f;

        break;

      case RADIUS:

        w = Utils.abs(x1 + x1);
        h = Utils.abs(y1 + y1);

        break;

      case CORNER:

        x += Utils.abs(x1) * 0.5f;
        y -= Utils.abs(y1) * 0.5f;

        break;

      case CENTER:

      default:

        w = Utils.abs(x1);
        h = Utils.abs(y1);
    }

    final boolean oldFill = this.fill;

    switch ( mode ) {

      case OPEN:

        this.arcMode = ArcMode.OPEN;
        this.fill = false;

        break;

      case CHORD:

        this.arcMode = ArcMode.CHORD;

        break;

      case PIE:

      default:

        this.arcMode = ArcMode.PIE;
    }

    this.transform.moveTo(this.tr2Loc.set(x, y));
    this.transform.rotateTo(0.0f);
    this.transform.scaleTo(this.tr2Scale.set(w, h));

    Curve2.arc(start, stop, 0.5f, this.arcMode, this.arc);
    this.arcImpl(this.arc, this.transform, IUp.DEFAULT_ORDER);

    this.fill = oldFill;
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
  public void bezier (
      final float ap0x, final float ap0y,
      final float cp0x, final float cp0y,
      final float cp1x, final float cp1y,
      final float ap1x, final float ap1y ) {

    this.beginShape(PConstants.POLYGON);
    this.normal(0.0f, 0.0f, 1.0f);
    this.vertexImpl(
        ap0x, ap0y, 0.0f,
        this.textureU, this.textureV);
    this.bezierVertexImpl(
        cp0x, cp0y, 0.0f,
        cp1x, cp1y, 0.0f,
        ap1x, ap1y, 0.0f);
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
  public void bezier (
      final float ap0x, final float ap0y, final float ap0z,
      final float cp0x, final float cp0y, final float cp0z,
      final float cp1x, final float cp1y, final float cp1z,
      final float ap1x, final float ap1y, final float ap1z ) {

    this.beginShape(PConstants.POLYGON);
    this.normal(0.0f, 0.0f, 1.0f);
    this.vertexImpl(
        ap0x, ap0y, ap0z,
        this.textureU, this.textureV);
    this.bezierVertexImpl(
        cp0x, cp0y, cp0z,
        cp1x, cp1y, cp1z,
        ap1x, ap1y, ap1z);
    this.endShape(PConstants.OPEN);
  }

  /**
   * Finds a point on a curve according to a step in the range [0.0,
   * 1.0].
   *
   * @param ap0  the first anchor point
   * @param cp0  the first control point
   * @param cp1  the second control point
   * @param ap1  the second anchor point
   * @param step the step
   */
  @Override
  public float bezierPoint (
      final float ap0,
      final float cp0,
      final float cp1,
      final float ap1,
      final float step ) {

    final float u = 1.0f - step;
    return (ap0 * u + cp0 * (step + step + step)) * u * u
        + (cp1 * (u + u + u) + ap1 * step) * step * step;
  }

  /**
   * Finds a tangent on a curve according to a step in the range [0.0,
   * 1.0].
   *
   * @param ap0  the first anchor point
   * @param cp0  the first control point
   * @param cp1  the second control point
   * @param ap1  the second anchor point
   * @param step the step
   */
  @Override
  public float bezierTangent (
      final float ap0,
      final float cp0,
      final float cp1,
      final float ap1,
      final float step ) {

    final float t3 = step + step + step;
    final float b2 = cp0 + cp0;
    final float ac = ap0 + cp1;
    final float bna = cp0 - ap0;

    return t3 * step * (b2 + cp0 + ap1 - (ac + cp1 + cp1)) +
        (t3 + t3) * (ac - b2) +
        (bna + bna + bna);
  }

  /**
   * Unsupported by this renderer. Use a MeshEntity and Mesh instead.
   *
   * @param size the size
   */
  @Override
  public void box ( final float size ) {

    PApplet.showMethodWarning("box");
  }

  /**
   * Unsupported by this renderer. Use a MeshEntity and Mesh instead.
   *
   * @param w the width
   * @param h the height
   * @param d the depth
   */
  @Override
  public void box (
      final float w,
      final float h,
      final float d ) {

    PApplet.showMethodWarning("box");
  }

  /**
   * Calculates the color channels from a color object.
   *
   * @param c the color
   */
  public void colorCalc ( final Color c ) {

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
   * Exposes the color calculation to the public. Includes the option to
   * pre multiply alpha. Refers to the helper function
   * {@link UpOgl#colorPreCalc(float, float, float, float)} to perform
   * part of the calculation independent of this consideration.
   *
   * @param x      the first color channel, hue or red
   * @param y      the second color channel, saturation or green
   * @param z      the third color channel, brightness or blue
   * @param a      the alpha channel
   * @param premul pre-multiply alpha
   * @see UpOgl#colorPreCalc(float, float, float, float)
   */
  public void colorCalc (
      final float x,
      final float y,
      final float z,
      final float a,
      final boolean premul ) {

    if ( premul ) {
      if ( a <= 0.0f ) {
        this.calcAlpha = false;
        this.calcColor = 0x00000000;

        this.calcR = 0.0f;
        this.calcG = 0.0f;
        this.calcB = 0.0f;
        this.calcA = 0.0f;

        this.calcRi = 0;
        this.calcGi = 0;
        this.calcBi = 0;
        this.calcAi = 0;

        return;
      }

      this.colorPreCalc(x, y, z, a);

      if ( this.calcA < 1.0f ) {
        this.calcR *= this.calcA;
        this.calcG *= this.calcA;
        this.calcB *= this.calcA;
      }
    } else {
      this.colorPreCalc(x, y, z, a);
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

    this.invColorModeX = 1.0f / this.colorModeX;
    this.invColorModeY = 1.0f / this.colorModeY;
    this.invColorModeZ = 1.0f / this.colorModeZ;
    this.invColorModeA = 1.0f / this.colorModeA;
  }

  /**
   * A helper function to color calculation, exposed to the public.
   * Calculates color based on the color mode, HSB or RGB, regardless of
   * whether or not the color is to be pre-multiplied.
   *
   * @param x the first color channel, hue or red
   * @param y the second color channel, saturation or green
   * @param z the third color channel, brightness or blue
   * @param a the alpha channel
   */
  public void colorPreCalc (
      final float x,
      final float y,
      final float z,
      final float a ) {

    this.calcA = Utils.clamp01(
        a * this.invColorModeA);
    this.calcB = Utils.clamp01(
        z * this.invColorModeZ);
    this.calcG = Utils.clamp01(
        y * this.invColorModeY);

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

        this.calcR = Utils.clamp01(
            x * this.invColorModeX);

    }
  }

  /**
   * Draws a curved line on the screen. The first three parameters
   * specify the start control point; the last three parameters specify
   * the ending control point. The middle parameters specify the start
   * and stop of the curve.
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
  public void curve (
      final float x1, final float y1,
      final float x2, final float y2,
      final float x3, final float y3,
      final float x4, final float y4 ) {

    this.beginShape(PConstants.POLYGON);
    this.normal(0.0f, 0.0f, 1.0f);
    this.curveVertexImpl(x1, y1, 0.0f);
    this.curveVertexImpl(x2, y2, 0.0f);
    this.curveVertexImpl(x3, y3, 0.0f);
    this.curveVertexImpl(x4, y4, 0.0f);
    this.endShape(PConstants.OPEN);
  }

  /**
   * Draws a curved line on the screen. The first three parameters
   * specify the start control point; the last three parameters specify
   * the ending control point. The middle parameters specify the start
   * and stop of the curve.
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
  public void curve (
      final float x1, final float y1, final float z1,
      final float x2, final float y2, final float z2,
      final float x3, final float y3, final float z3,
      final float x4, final float y4, final float z4 ) {

    this.beginShape(PConstants.POLYGON);
    this.normal(0.0f, 0.0f, 1.0f);
    this.curveVertexImpl(x1, y1, z1);
    this.curveVertexImpl(x2, y2, z2);
    this.curveVertexImpl(x3, y3, z3);
    this.curveVertexImpl(x4, y4, z4);
    this.endShape(PConstants.OPEN);
  }

  /**
   * Sets the renderer's current fill to the color.
   *
   * @param c the color
   */
  @Override
  public void fill ( final Color c ) {

    this.colorCalc(c);
    this.fillFromCalc();
  }

  /**
   * Sets the renderer projection matrix to the frustum defined by the
   * edges of the view port.
   *
   * @param left   the left edge of the window
   * @param right  the right edge of the window
   * @param bottom the bottom edge of the window
   * @param top    the top edge of the window
   * @param near   the near clip plane
   * @param far    the far clip plane
   */
  @Override
  public void frustum (
      final float left, final float right,
      final float bottom, final float top,
      final float near, final float far ) {

    // this.cameraFOV = fov;
    // this.cameraAspect = aspect;
    this.cameraNear = near;
    this.cameraFar = far;

    PMatAux.frustum(
        left, right,
        bottom, top,
        near, far,
        this.projection);
  }

  /**
   * Gets this renderer's background color.
   *
   * @return the background color
   */
  public int getBackground ( ) { return this.backgroundColor; }

  /**
   * Gets this renderer's background color.
   *
   * @param target the output color
   * @return the background color
   */
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
   * Gets the renderer model view matrix.
   *
   * @param target the output matrix
   * @return the model view
   */
  public Mat4 getMatrix ( final Mat4 target ) {

    return target.set(
        this.modelview.m00, this.modelview.m01,
        this.modelview.m02, this.modelview.m03,
        this.modelview.m10, this.modelview.m11,
        this.modelview.m12, this.modelview.m13,
        this.modelview.m20, this.modelview.m21,
        this.modelview.m22, this.modelview.m23,
        this.modelview.m30, this.modelview.m31,
        this.modelview.m32, this.modelview.m33);
  }

  /**
   * Gets the renderer model view matrix.
   *
   * @param target the output matrix
   * @return the model view
   */
  @Override
  public PMatrix2D getMatrix ( PMatrix2D target ) {

    if ( target == null ) { target = new PMatrix2D(); }
    target.set(
        this.modelview.m00, this.modelview.m01, this.modelview.m03,
        this.modelview.m10, this.modelview.m11, this.modelview.m13);
    return target;
  }

  /**
   * Gets the renderer model view matrix.
   *
   * @param target the output matrix
   * @return the model view
   */
  @Override
  public PMatrix3D getMatrix ( PMatrix3D target ) {

    if ( target == null ) { target = new PMatrix3D(); }
    target.set(this.modelview);
    return target;
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

    if ( buff.pgl.threadIsCurrent() ) { this.image((PImage) buff); }
  }

  /**
   * Displays a PGraphicsOpenGL buffer. Checks if the buffer's PGL
   * thread is current before proceeding. This is to help ensure that
   * beginDraw and endDraw have already been called.
   *
   * @param buff the renderer
   * @param x    the first x coordinate
   * @param y    the first y coordinate
   */
  public void image (
      final PGraphicsOpenGL buff,
      final float x,
      final float y ) {

    if ( buff.pgl.threadIsCurrent() ) { this.image((PImage) buff, x, y); }
  }

  /**
   * Displays a PGraphicsOpenGL buffer. Checks if the buffer's PGL
   * thread is current before proceeding. This is to help ensure that
   * beginDraw and endDraw have already been called.
   *
   * @param buff the renderer
   * @param x1   the first x coordinate
   * @param y1   the first y coordinate
   * @param x2   the second x coordinate
   * @param y2   the second y coordinate
   */
  public void image (
      final PGraphicsOpenGL buff,
      final float x1, final float y1,
      final float x2, final float y2 ) {

    if ( buff.pgl.threadIsCurrent() ) {
      this.image((PImage) buff, x1, y1, x2, y2);
    }
  }

  /**
   * Displays a PGraphicsOpenGL buffer. Checks if the buffer's PGL
   * thread is current before proceeding. This is to help ensure that
   * beginDraw and endDraw have already been called.
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
  public void image (
      final PGraphicsOpenGL buff,
      final float x1, final float y1,
      final float x2, final float y2,
      final int u1, final int v1,
      final int u2, final int v2 ) {

    if ( buff.pgl.threadIsCurrent() ) {
      this.image(
          (PImage) buff,
          x1, y1, x2, y2,
          u1, v1, u2, v2);
    }
  }

  /**
   * Displays a PImage at the origin.
   *
   * @param img the image
   */
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
   * @param x1  the first x coordinate
   * @param y1  the first y coordinate
   * @param x2  the second x coordinate
   * @param y2  the second y coordinate
   */
  @Override
  public void image (
      final PImage img,
      final float x1, final float y1,
      final float x2, final float y2 ) {

    final boolean useImg = textureMode == PConstants.IMAGE;
    this.imageImpl(img,
        x1, y1, x2, y2,
        0.0f,
        0.0f, 0.0f,
        useImg ? img.width : 1.0f,
        useImg ? img.height : 1.0f);
  }

  /**
   * Displays a PImage. The meaning of the first four parameters depends
   * on imageMode. The last four coordinates specify the image texture
   * coordinates (or UVs).
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
  public void image (
      final PImage img,
      final float x1, final float y1,
      final float x2, final float y2,
      final int u1, final int v1,
      final int u2, final int v2 ) {

    this.imageImpl(img,
        x1, y1, x2, y2, 0.0f,
        u1, v1,
        u2, v2);
  }

  /**
   * Displays a PGraphicsOpenGL buffer. Checks if the buffer's PGL
   * thread is current before proceeding. This is to help ensure that
   * beginDraw and endDraw have already been called.
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
  public void imageImpl (
      final PGraphicsOpenGL buff,
      final float x1, final float y1,
      final float x2, final float y2,
      final float u1, final float v1,
      final float u2, final float v2 ) {

    if ( buff.pgl.threadIsCurrent() ) {
      this.imageImpl(buff,
          x1, y1, x2, y2, 0.0f,
          u1, v1, u2, v2);
    }
  }

  /**
   * Displays a PImage. The meaning of the first four parameters depends
   * on imageMode. The last four coordinates specify the image texture
   * coordinates (or UVs).
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
  public void imageImpl (
      final PImage img,
      final float x1, final float y1,
      final float x2, final float y2,
      final float z,
      final float u1, final float v1,
      final float u2, final float v2 ) {

    if ( img.width < 2 || img.height < 2 ) { return; }

    this.pushStyle();
    this.noStroke();

    this.beginShape(PConstants.POLYGON);
    this.normal(0.0f, 0.0f, 1.0f);
    this.texture(img);
    switch ( this.imageMode ) {

      case CORNER:

        this.vertexImpl(x1, y1, z, u1, v1);
        this.vertexImpl(x1 + x2, y1, z, u2, v1);
        this.vertexImpl(x1 + x2, y1 - y2, z, u2, v2);
        this.vertexImpl(x1, y1 - y2, z, u1, v2);

        break;

      case CORNERS:

        this.vertexImpl(x1, y1, z, u1, v2);
        this.vertexImpl(x2, y1, z, u2, v2);
        this.vertexImpl(x2, y2, z, u2, v1);
        this.vertexImpl(x1, y2, z, u1, v1);

        break;

      case CENTER:

      default:

        final float hu = x2 * 0.5f;
        final float hv = y2 * 0.5f;

        this.vertexImpl(x1 - hu, y1 + hv, z, u1, v1);
        this.vertexImpl(x1 + hu, y1 + hv, z, u2, v1);
        this.vertexImpl(x1 + hu, y1 - hv, z, u2, v2);
        this.vertexImpl(x1 - hu, y1 - hv, z, u1, v2);
    }

    this.endShape(PConstants.CLOSE);
    this.popStyle();
  }

  /**
   * Returns whether or not the renderer is 2D.
   */
  @Override
  public abstract boolean is2D ( );

  /**
   * Returns whether or not the renderer is 3D.
   */
  @Override
  public abstract boolean is3D ( );

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
      this.stroke(material.stroke);
    }

    if ( material.useFill ) { this.fill(material.fill); }
  }

  /**
   * Processing's modelX, modelY and modelZ functions are very
   * inefficient, as each one calculates the product of the model view
   * and the point. This function groups all three model functions into
   * one.
   *
   * @param point  the input point
   * @param target the output point
   * @return the model space point
   */
  public Vec3 model (
      final Vec3 point,
      final Vec3 target ) {

    /* Multiply point by model-view matrix. */
    final float aw = this.modelview.m30 * point.x +
        this.modelview.m31 * point.y +
        this.modelview.m32 * point.z +
        this.modelview.m33;

    final float ax = this.modelview.m00 * point.x +
        this.modelview.m01 * point.y +
        this.modelview.m02 * point.z +
        this.modelview.m03;

    final float ay = this.modelview.m10 * point.x +
        this.modelview.m11 * point.y +
        this.modelview.m12 * point.z +
        this.modelview.m13;

    final float az = this.modelview.m20 * point.x +
        this.modelview.m21 * point.y +
        this.modelview.m22 * point.z +
        this.modelview.m23;

    /* Multiply point by inverse of camera matrix. */
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

    if ( bw == 1.0f ) { return target.set(bx, by, bz); }

    /*
     * Convert from homogeneous coordinate to point by dividing by fourth
     * component, w.
     */
    final float wInv = 1.0f / bw;
    return target.set(
        bx * wInv,
        by * wInv,
        bz * wInv);
  }

  /**
   * Draws the world origin.
   */
  @Override
  public abstract void origin ( );

  /**
   * Sets the renderer projection to orthographic, where objects
   * maintain their size regardless of distance from the camera.
   */
  @Override
  public void ortho ( ) {

    /* Never use defCameraXXX values. They are not actual constants. */
    final float right = this.width < 128
        ? IUp.DEFAULT_HALF_WIDTH
        : this.width * 0.5f;
    final float left = -right;

    final float top = this.height < 128
        ? IUp.DEFAULT_HALF_HEIGHT
        : this.height * 0.5f;
    final float bottom = -top;

    this.ortho(left, right, bottom, top);
  }

  /**
   * Sets the renderer projection to orthographic, where objects
   * maintain their size regardless of distance from the camera.
   *
   * @param left   the left edge of the window
   * @param right  the right edge of the window
   * @param bottom the bottom edge of the window
   * @param top    the top edge of the window
   */
  @Override
  public void ortho (
      final float left, final float right,
      final float bottom, final float top ) {

    float far = IUp.DEFAULT_FAR_CLIP;
    if ( this.eyeDist != 0.0f ) {
      far = IUp.DEFAULT_NEAR_CLIP + this.eyeDist * 10.0f;
    }
    this.ortho(left, right, bottom, top, IUp.DEFAULT_NEAR_CLIP, far);
  }

  /**
   * Sets the renderer projection to orthographic, where objects
   * maintain their size regardless of distance from the camera.
   *
   * @param left   the left edge of the window
   * @param right  the right edge of the window
   * @param bottom the bottom edge of the window
   * @param top    the top edge of the window
   * @param near   the near clip plane
   * @param far    the far clip plane
   */
  @Override
  public void ortho (
      final float left, final float right,
      final float bottom, final float top,
      final float near, final float far ) {

    PMatAux.orthographic(
        left, right,
        bottom, top,
        near, far,
        this.projection);
  }

  /**
   * Sets the renderer projection to a perspective, where objects nearer
   * to the camera appear larger than objects distant from the camera.
   */
  @Override
  public void perspective ( ) {

    /* Never use defCameraXXX values. They are not actual constants. */
    this.perspective(IUp.DEFAULT_FOV);
  }

  /**
   * Sets the renderer projection to a perspective, where objects nearer
   * to the camera appear larger than objects distant from the camera.
   *
   * @param fov the field of view
   */
  public void perspective ( final float fov ) {

    final float aspect = this.width < 128 && this.height < 128
        ? IUp.DEFAULT_ASPECT
        : this.width / (float) this.height;

    this.perspective(fov, aspect);
  }

  /**
   * Sets the renderer projection to a perspective, where objects nearer
   * to the camera appear larger than objects distant from the camera.
   *
   * @param fov    the field of view
   * @param aspect the aspect ratio, width over height
   */
  public void perspective (
      final float fov,
      final float aspect ) {

    float near = IUp.DEFAULT_NEAR_CLIP;
    float far = IUp.DEFAULT_FAR_CLIP;
    if ( this.cameraZ != 0.0f ) {
      near *= this.cameraZ;
      far *= this.cameraZ;
    }

    this.perspective(fov, aspect, near, far);
  }

  /**
   * Sets the renderer projection to a perspective, where objects nearer
   * to the camera appear larger than objects distant from the camera.
   *
   * @param fov    the field of view
   * @param aspect the aspect ratio, width over height
   * @param near   the near clip plane
   * @param far    the far clip plane
   */
  @Override
  public void perspective (
      final float fov,
      final float aspect,
      final float near,
      final float far ) {

    this.cameraFOV = fov;
    this.cameraAspect = aspect;
    this.cameraNear = near;
    this.cameraFar = far;

    PMatAux.perspective(fov, aspect, near, far, this.projection);
  }

  /**
   * Prints the camera matrix in columns, for easier viewing in the
   * console.
   */
  @Override
  public void printCamera ( ) {

    System.out.println(PMatAux.toString(this.camera, 4));
  }

  /**
   * Prints the model view matrix in columns, for easier viewing in the
   * console.
   */
  @Override
  public void printMatrix ( ) {

    System.out.println(PMatAux.toString(this.modelview, 4));
  }

  /**
   * Prints the projection matrix in columns, for easier viewing in the
   * console.
   */
  @Override
  public void printProjection ( ) {

    System.out.println(PMatAux.toString(this.projection, 4));
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

    this.beginShape(PConstants.POLYGON);
    this.normal(0.0f, 0.0f, 1.0f);
    this.vertexImpl(x0, y0, 0.0f, 0.0f, 0.0f);
    this.vertexImpl(x1, y1, 0.0f, 1.0f, 0.0f);
    this.vertexImpl(x2, y2, 0.0f, 1.0f, 1.0f);
    this.vertexImpl(x3, y3, 0.0f, 0.0f, 1.0f);
    this.endShape(PConstants.CLOSE);
  }

  /**
   * Draws a rectangle. The meaning of the four parameters depends on
   * rectMode.
   *
   * @param x1 the first x parameter
   * @param y1 the first y parameter
   * @param x2 the second x parameter
   * @param y2 the second y parameter
   */
  @Override
  public void rect (
      final float x1, final float y1,
      final float x2, final float y2 ) {

    float a0 = 0.0f;
    float b0 = 0.0f;
    float a1 = 0.0f;
    float b1 = 0.0f;

    float w = 0.0f;
    float h = 0.0f;

    switch ( this.rectMode ) {

      case CORNER:

        w = Utils.abs(x2);
        h = Utils.abs(y2);

        a0 = x1;
        b0 = y1 - h;
        a1 = x1 + w;
        b1 = y1;

        break;

      case CORNERS:

        a0 = Utils.min(x1, x2);
        a1 = Utils.max(x1, x2);

        b0 = Utils.min(y1, y2);
        b1 = Utils.max(y1, y2);

        break;

      case RADIUS:

        w = Utils.abs(x2);
        h = Utils.abs(y2);

        a0 = x1 - w;
        a1 = x1 + w;
        b0 = y1 + h;
        b1 = y1 - h;

        break;

      case CENTER:

      default:

        w = Utils.abs(x2) * 0.5f;
        h = Utils.abs(y2) * 0.5f;

        a0 = x1 - w;
        a1 = x1 + w;
        b0 = y1 + h;
        b1 = y1 - h;
    }

    this.beginShape(PConstants.POLYGON);
    this.normal(0.0f, 0.0f, 1.0f);
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
   * Rotates the model view matrix around the z axis by an angle in
   * radians.
   *
   * @param radians the angle in radians
   */
  @Override
  public void rotate ( final float radians ) {

    this.rotateZ(radians);
  }

  /**
   * Rotates the sketch by an angle in radians around the x axis.
   *
   * Do not use sequences of orthonormal rotations by Euler angles; this
   * will result in gimbal lock. Instead, rotate by an angle around an
   * axis.
   *
   * @param radians the angle
   * @see Up3#rotate(float, float, float, float)
   */
  @Override
  public void rotateX ( final float radians ) {

    final float normRad = radians * IUtils.ONE_TAU;
    final float c = Utils.scNorm(normRad);
    final float s = Utils.scNorm(normRad - 0.25f);

    PMatAux.rotateX(c, s, this.modelview);
    PMatAux.invRotateX(c, s, this.modelviewInv);
    PMatAux.mul(
        this.projection,
        this.modelview,
        this.projmodelview);
  }

  /**
   * Rotates the sketch by an angle in radians around the y axis.
   *
   * Do not use sequences of orthonormal rotations by Euler angles; this
   * will result in gimbal lock. Instead, rotate by an angle around an
   * axis.
   *
   * @param radians the angle
   * @see Up3#rotate(float, float, float, float)
   */
  @Override
  public void rotateY ( final float radians ) {

    final float normRad = radians * IUtils.ONE_TAU;
    final float c = Utils.scNorm(normRad);
    final float s = Utils.scNorm(normRad - 0.25f);

    PMatAux.rotateY(c, s, this.modelview);
    PMatAux.invRotateY(c, s, this.modelviewInv);
    PMatAux.mul(
        this.projection,
        this.modelview,
        this.projmodelview);
  }

  /**
   * Rotates the sketch by an angle in radians around the z axis.
   *
   * Do not use sequences of orthonormal rotations by Euler angles; this
   * will result in gimbal lock. Instead, rotate by an angle around an
   * axis.
   *
   * @param radians the angle
   * @see Up3#rotate(float, float, float, float)
   */
  @Override
  public void rotateZ ( final float radians ) {

    final float normRad = radians * IUtils.ONE_TAU;
    PMatAux.compoundRotateZ(
        Utils.scNorm(normRad),
        Utils.scNorm(normRad - 0.25f),
        this.modelview,
        this.modelviewInv);
    PMatAux.mul(
        this.projection,
        this.modelview,
        this.projmodelview);
  }

  /**
   * Attempts to find the screen position of a point in the world. Does
   * so by
   * <ol>
   * <li>promoting the point to a vector 4, where its w component is 1.0
   * .</li>
   * <li>multiplying the vector 4 by the model view matrix;</li>
   * <li>multiplying the product by the projection;</li>
   * <li>demoting the vector 4 to a point 3 by dividing the x, y and z
   * components by w;</li>
   * <li>shifting the range from [-1.0, 1.0] to [(0.0, 0.0), (width,
   * height)] .</li>
   * </ol>
   *
   * More efficient than calling
   * {@link PApplet#screenX(float, float, float)} ,
   * {@link PApplet#screenY(float, float, float)} , and
   * {@link PApplet#screenZ(float, float, float)} separately. However,
   * it is advisable to work with {@link Vec4}s and the renderer
   * matrices directly.
   *
   * @param v      the point
   * @param target the output vector
   * @return the screen point
   */
  public Vec3 screen (
      final Vec3 v,
      final Vec3 target ) {

    /* Multiply point by model-view matrix. */
    final float aw = this.modelview.m30 * v.x +
        this.modelview.m31 * v.y +
        this.modelview.m32 * v.z +
        this.modelview.m33;

    final float ax = this.modelview.m00 * v.x +
        this.modelview.m01 * v.y +
        this.modelview.m02 * v.z +
        this.modelview.m03;

    final float ay = this.modelview.m10 * v.x +
        this.modelview.m11 * v.y +
        this.modelview.m12 * v.z +
        this.modelview.m13;

    final float az = this.modelview.m20 * v.x +
        this.modelview.m21 * v.y +
        this.modelview.m22 * v.z +
        this.modelview.m23;

    /* Multiply new point by projection. */
    final float bw = this.projection.m30 * ax +
        this.projection.m31 * ay +
        this.projection.m32 * az +
        this.projection.m33 * aw;

    if ( bw == 0.0f ) { return target.reset(); }

    float bx = this.projection.m00 * ax +
        this.projection.m01 * ay +
        this.projection.m02 * az +
        this.projection.m03 * aw;

    float by = this.projection.m10 * ax +
        this.projection.m11 * ay +
        this.projection.m12 * az +
        this.projection.m13 * aw;

    float bz = this.projection.m20 * ax +
        this.projection.m21 * ay +
        this.projection.m22 * az +
        this.projection.m23 * aw;

    if ( bw != 1.0f ) {
      final float wInv = 1.0f / bw;
      bx *= wInv;
      by *= wInv;
      bz *= wInv;
    }

    return target.set(
        this.width * (1.0f + bx) * 0.5f,
        this.height * (1.0f + by) * 0.5f,
        (bz + 1.0f) * 0.5f);
  }

  /**
   * Sets the renderer matrix to the source.
   *
   * @param source the source matrix
   */
  public void setMatrix ( final Mat3 source ) {

    this.resetMatrix();
    this.applyMatrixImpl(
        source.m00, source.m01, 0.0f, source.m02,
        source.m10, source.m11, 0.0f, source.m12,
        0.0f, 0.0f, 1.0f, 0.0f,
        source.m20, source.m21, 0.0f, source.m22);
  }

  /**
   * Sets the renderer matrix to the source.
   *
   * @param source the source matrix
   */
  public void setMatrix ( final Mat4 source ) {

    this.resetMatrix();
    this.applyMatrixImpl(
        source.m00, source.m01, source.m02, source.m03,
        source.m10, source.m11, source.m12, source.m13,
        source.m20, source.m21, source.m22, source.m23,
        source.m30, source.m31, source.m32, source.m33);
  }

  /**
   * Sets the renderer matrix to the source.
   *
   * @param source the source matrix
   */
  @Override
  public void setMatrix ( final PMatrix3D source ) {

    this.resetMatrix();
    this.applyMatrixImpl(
        source.m00, source.m01, source.m02, source.m03,
        source.m10, source.m11, source.m12, source.m13,
        source.m20, source.m21, source.m22, source.m23,
        source.m30, source.m31, source.m32, source.m33);
  }

  /**
   * Sets the renderer's current projection.
   *
   * @param m the matrix
   */
  public void setProjection ( final Mat4 m ) {

    this.flush();
    this.projection.set(
        m.m00, m.m01, m.m02, m.m03,
        m.m10, m.m11, m.m12, m.m13,
        m.m20, m.m21, m.m22, m.m23,
        m.m30, m.m31, m.m32, m.m33);
    PMatAux.mul(this.projection, this.modelview, this.projmodelview);
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
  public void setSize (
      final int width,
      final int height ) {

    this.width = width;
    this.height = height;
    this.updatePixelSize();

    this.texture = null;
    this.ptexture = null;

    this.defCameraFOV = IUp.DEFAULT_FOV;
    this.defCameraX = 0.0f;
    this.defCameraY = 0.0f;

    this.defCameraZ = this.height * Utils.cot(this.defCameraFOV * 0.5f);
    this.defCameraNear = this.defCameraZ * 0.01f;
    this.defCameraFar = this.defCameraZ * 10.0f;
    this.defCameraAspect = (float) this.width / (float) this.height;

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
   * @param y2    the second y coordinate
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
   * Applies a shear transform to the renderer.
   *
   * @param v the shear
   */
  public void shear ( final Vec2 v ) {

    this.applyMatrixImpl(
        1.0f, v.x, 0.0f, 0.0f,
        v.y, 1.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 1.0f, 0.0f,
        0.0f, 0.0f, 0.0f, 1.0f);
  }

  /**
   * Shears a shape around the x axis the amount specified in radians.
   *
   * @param radians the angle in radians
   */
  @Override
  public void shearX ( final float radians ) {

    this.applyMatrixImpl(
        1.0f, Utils.tan(radians), 0.0f, 0.0f,
        0.0f, 1.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 1.0f, 0.0f,
        0.0f, 0.0f, 0.0f, 1.0f);
  }

  /**
   * Shears a shape around the y axis the amount specified in radians.
   *
   * @param radians the angle in radians
   */
  @Override
  public void shearY ( final float radians ) {

    this.applyMatrixImpl(
        1.0f, 0.0f, 0.0f, 0.0f,
        Utils.tan(radians), 1.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 1.0f, 0.0f,
        0.0f, 0.0f, 0.0f, 1.0f);
  }

  /**
   * Unsupported by this renderer. Use a MeshEntity and Mesh instead.
   *
   * @param r the radius
   */
  @Override
  public void sphere ( final float r ) {

    PApplet.showMethodWarning("sphere");
  }

  /**
   * Unsupported by this renderer. Use a MeshEntity and Mesh instead.
   *
   * @param longitudes the longitudes
   * @param latitudes  the latitudes
   */
  @Override
  public void sphereDetail (
      final int longitudes,
      final int latitudes ) {

    PApplet.showMethodWarning("sphereDetail");
  }

  /**
   * Sets the renderer's current stroke to the color.
   *
   * @param c the color
   */
  @Override
  public void stroke ( final Color c ) {

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
   * Displays a character as text at a given coordinate.
   *
   * @param c the character
   * @param x the x coordinate
   * @param y the y coordinate
   * @param z the z coordinate
   */
  @Override
  public void text (
      final char c,
      final float x,
      final float y,
      final float z ) {

    this.text(c, x, y);
  }

  /**
   * Displays an array of characters as text at a given coordinate.
   *
   * @param chars the character array
   * @param x     the x coordinate
   * @param y     the y coordinate
   */
  public void text (
      final char[] chars,
      final float x,
      final float y ) {

    this.text(chars, 0, chars.length, x, y);
  }

  /**
   * Displays an array of characters as text at a given coordinate.
   *
   * @param chars the character array
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

    float yMut = y;
    int stMut = start;
    float high = 0;
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
   * Displays an array of characters as text at a given coordinate.
   *
   * @param chars the character array
   * @param start the start index, inclusive
   * @param stop  the stop index, exclusive
   * @param x     the x coordinate
   * @param y     the y coordinate
   * @param z     the z coordinate
   */
  @Override
  public void text (
      final char[] chars,
      final int start,
      final int stop,
      final float x,
      final float y,
      final float z ) {

    this.text(chars, start, stop, x, y);
  }

  /**
   * Displays a number as text. Registers up to four decimal places.
   *
   * @param num the number
   * @param x   the x coordinate
   * @param y   the y coordinate
   * @see Utils#toFixed(float, int)
   */
  @Override
  public void text (
      final float num,
      final float x,
      final float y ) {

    this.text(Utils.toFixed(num, 4), x, y);
  }

  /**
   * Displays a real number as text. Registers up to four decimal
   * places.
   *
   * @param num the number
   * @param x   the x coordinate
   * @param y   the y coordinate
   * @param z   the z coordinate
   * @see Utils#toFixed(float, int)
   */
  @Override
  public void text (
      final float num,
      final float x,
      final float y,
      final float z ) {

    this.text(num, x, y);
  }

  /**
   * Displays a number as text.
   *
   * @param num the number
   * @param x   the x coordinate
   * @param y   the y coordinate
   * @param z   the z coordinate
   */
  @Override
  public void text (
      final int num,
      final float x,
      final float y,
      final float z ) {

    this.text(num, x, y);
  }

  /**
   * Displays a string at a coordinate.
   *
   * @param str the string
   * @param x   the x coordinate
   * @param y   the y coordinate
   */
  @Override
  public void text (
      final String str,
      final float x,
      final float y ) {

    this.text(str.toCharArray(), x, y);
  }

  /**
   * Displays a string at a coordinate.
   *
   * @param str the string
   * @param x   the x coordinate
   * @param y   the y coordinate
   * @param z   the z coordinate
   */
  @Override
  public void text (
      final String str,
      final float x,
      final float y,
      final float z ) {

    this.text(str, x, y);
  }

  /**
   * Displays a string at a coordinate. This version of text is not
   * supported, so only x1 and y1 are used.
   *
   * @param str the string
   * @param x1  the x coordinate
   * @param y1  the y coordinate
   * @param x2  the second x coordinate
   * @param y2  the second y coordinate
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
   * Sets the text mode to either shape or model.
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
   * Assigns the PGraphicsOpenGL buffer as the current texture.
   *
   * @param buff the buffer
   */
  public void texture ( final PGraphicsOpenGL buff ) {

    if ( buff.pgl.threadIsCurrent() ) { this.texture((PImage) buff); }
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
  @Override
  public void tint ( final Color c ) {

    this.colorCalc(c);
    this.tintFromCalc();
  }

  /**
   * Returns the string representation of this renderer.
   *
   * @return the string
   */
  @Override
  public String toString ( ) {

    return "camzup.pfriendly.UpOgl";
  }

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
  public void transform (
      final Transform2 tr2,
      final TransformOrder order ) {

    final Vec2 dim = tr2.getScale(this.tr2Scale);
    final Vec2 loc = tr2.getLocation(this.tr2Loc);
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

      case TRS:

      default:

        this.translateImpl(loc.x, loc.y, 0.0f);
        this.rotateZ(angle);
        this.scaleImpl(dim.x, dim.y, 1.0f);

        return;
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
  public void updateGLModelview (
      final float m00, final float m01, final float m02, final float m03,
      final float m10, final float m11, final float m12, final float m13,
      final float m20, final float m21, final float m22, final float m23,
      final float m30, final float m31, final float m32, final float m33 ) {

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

    this.updateGLModelview(
        m.m00, m.m01, m.m02, m.m03,
        m.m10, m.m11, m.m12, m.m13,
        m.m20, m.m21, m.m22, m.m23,
        m.m30, m.m31, m.m32, m.m33);
  }

  /**
   * Exposes the OpenGL model view array to more direct access.
   *
   * @param m the Processing matrix
   */
  public void updateGLModelview ( final PMatrix3D m ) {

    this.updateGLModelview(
        m.m00, m.m01, m.m02, m.m03,
        m.m10, m.m11, m.m12, m.m13,
        m.m20, m.m21, m.m22, m.m23,
        m.m30, m.m31, m.m32, m.m33);
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
  public void updateGLNormal (
      final float m00, final float m01, final float m02,
      final float m10, final float m11, final float m12,
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

    this.updateGLNormal(
        m.m00, m.m01, m.m02,
        m.m10, m.m11, m.m12,
        m.m20, m.m21, m.m22);
  }

  /**
   * Exposes the OpenGL normal array to more direct access.
   *
   * @param m the Processing matrix
   */
  public void updateGLNormal ( final PMatrix3D m ) {

    this.updateGLNormal(
        m.m00, m.m01, m.m02,
        m.m10, m.m11, m.m12,
        m.m20, m.m21, m.m22);
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
  public void updateGLProjection (
      final float m00, final float m01, final float m02, final float m03,
      final float m10, final float m11, final float m12, final float m13,
      final float m20, final float m21, final float m22, final float m23,
      final float m30, final float m31, final float m32, final float m33 ) {

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

    this.updateGLProjection(
        m.m00, m.m01, m.m02, m.m03,
        m.m10, m.m11, m.m12, m.m13,
        m.m20, m.m21, m.m22, m.m23,
        m.m30, m.m31, m.m32, m.m33);
  }

  /**
   * Exposes the OpenGL projection array to more direct access.
   *
   * @param m the Processing matrix
   */
  public void updateGLProjection ( final PMatrix3D m ) {

    this.updateGLProjection(
        m.m00, m.m01, m.m02, m.m03,
        m.m10, m.m11, m.m12, m.m13,
        m.m20, m.m21, m.m22, m.m23,
        m.m30, m.m31, m.m32, m.m33);
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
  public void updateGLProjmodelview (
      final float m00, final float m01, final float m02, final float m03,
      final float m10, final float m11, final float m12, final float m13,
      final float m20, final float m21, final float m22, final float m23,
      final float m30, final float m31, final float m32, final float m33 ) {

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

    this.updateGLProjmodelview(
        m.m00, m.m01, m.m02, m.m03,
        m.m10, m.m11, m.m12, m.m13,
        m.m20, m.m21, m.m22, m.m23,
        m.m30, m.m31, m.m32, m.m33);
  }

  /**
   * Exposes the OpenGL project model view array to more direct access.
   *
   * @param m the Processing matrix
   */
  public void updateGLProjmodelview ( final PMatrix3D m ) {

    this.updateGLProjmodelview(
        m.m00, m.m01, m.m02, m.m03,
        m.m10, m.m11, m.m12, m.m13,
        m.m20, m.m21, m.m22, m.m23,
        m.m30, m.m31, m.m32, m.m33);
  }

  /**
   * Update the pixels[] buffer to the PGraphics image.
   *
   * The overridden functionality eliminates unnecessary checks.
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
   * Updates the project model view by multiplying the projection and
   * model view.
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
  public void vertex (
      final float x,
      final float y ) {

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
  public void vertex (
      final float x,
      final float y,
      final float z ) {

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
  public void vertex (
      final float x,
      final float y,
      final float u,
      final float v ) {

    this.vertexTexture(u, v,
        this.textureMode,
        this.textureWrap);
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
  public void vertex (
      final float x,
      final float y,
      final float z,
      final float u,
      final float v ) {

    this.vertexTexture(u, v,
        this.textureMode,
        this.textureWrap);
    this.vertexImpl(x, y, z,
        this.textureU, this.textureV);
  }

  /**
   * Updates the texture coordinate.
   *
   * @param vt the texture coordinate
   */
  public void vertexTexture ( final Vec2 vt ) {

    this.vertexTexture(vt.x, vt.y);
  }
}

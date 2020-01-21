package camzup.pfriendly;

import camzup.core.Color;
import camzup.core.Curve2;
import camzup.core.Curve2.Knot2;
import camzup.core.IUtils;
import camzup.core.Mat3;
import camzup.core.Mat4;
import camzup.core.Transform;
import camzup.core.Transform2;
import camzup.core.Utils;
import camzup.core.Vec2;
import camzup.core.Vec3;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PMatrix3D;
import processing.core.PShape;
import processing.opengl.PGraphicsOpenGL;

/**
 * An abstract parent class for Processing renderers based
 * on OpenGL.
 */
public abstract class UpOgl extends PGraphicsOpenGL implements IUpOgl {

   /**
    * A curve to hold the arc data.
    */
   protected final Curve2 arc = new Curve2();

   /**
    * The arc-mode.
    */
   protected Curve2.ArcMode arcMode = Curve2.ArcMode.OPEN;

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
    * The default constructor.
    */
   public UpOgl () {

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
   public UpOgl (
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
    * Draws the OpenGL implementation of the arc function using
    * a curve and transform.
    *
    * @param curve
    *           the arc curve
    * @param transform
    *           the transform
    * @param trOrder
    *           the transform order
    */
   protected void arcImpl (
         final Curve2 curve,
         final Transform2 transform,
         final Transform.Order trOrder ) {

      final int knotLength = curve.length();
      if (knotLength < 2) {
         return;
      }

      final int end = curve.closedLoop ? knotLength + 1 : knotLength;

      Knot2 currKnot = null;
      Knot2 prevKnot = curve.get(0);
      Vec2 coord = prevKnot.coord;
      Vec2 foreHandle = null;
      Vec2 rearHandle = null;

      this.pushMatrix();
      this.transform(transform, trOrder);

      final float oldSw = this.strokeWeight;
      final float swLine = oldSw / Transform2.minDimension(transform);
      this.strokeWeight = swLine;

      this.beginShape(PConstants.POLYGON);
      this.normal(0.0f, 0.0f, 1.0f);
      this.vertexImpl(
            coord.x, coord.y, 0.0f,
            this.textureU, this.textureV);

      for (int i = 1; i < end; ++i) {
         currKnot = curve.get(i % knotLength);

         foreHandle = prevKnot.foreHandle;
         rearHandle = currKnot.rearHandle;
         coord = currKnot.coord;

         this.bezierVertexImpl(
               foreHandle.x,
               foreHandle.y,
               0.0f,

               rearHandle.x,
               rearHandle.y,
               0.0f,

               coord.x,
               coord.y,
               0.0f);

         prevKnot = currKnot;
      }

      this.endShape(
            curve.closedLoop ? PConstants.CLOSE : PConstants.OPEN);

      this.strokeWeight = oldSw;
      this.popMatrix();
   }

   /**
    * Overrides the default colorCalc function. Defaults to
    * using pre-multiplied alpha.
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

      this.colorCalc(x, y, z, a, true);
   }

   @Override
   protected void colorCalcARGB ( final int argb, final float alpha ) {

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

   @Override
   protected void defaultSettings () {

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

      if (this.primaryGraphics) {
         this.background(IUp.DEFAULT_BKG_COLOR);
      }

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

   @Override
   protected void imageImpl (
         final PImage img,

         final float x1, final float y1,
         final float x2, final float y2,

         final int u1, final int v1,
         final int u2, final int v2 ) {

      /*
       * This is backwards due to Processing's insistence on using
       * integers to specify UV coordinates (maybe as a result
       * from working with AWT?). All image functions should flow
       * into this, but instead this flows into an image
       * implementation above.
       */

      final int savedTextureMode = this.textureMode;
      this.textureMode = PConstants.IMAGE;

      /*
       * This will have to go untested... as this code is being
       * written on a low density monitor.
       */
      final int pd = img.pixelDensity;
      this.imageImpl(img, x1, y1, x2, y2,
            (float) (u1 * pd),
            (float) (v1 * pd),
            (float) (u2 * pd),
            (float) (v2 * pd));

      this.textureMode = savedTextureMode;

   }

   /**
    * Sets ambient lighting. Color channels will be submitted
    * to color calculation.
    *
    * @param num
    *           the index
    * @param red
    *           the red channel
    * @param green
    *           the green channel
    * @param blue
    *           the blue channel
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
    * Sets diffuse lighting. Color channels will be submitted
    * to color calculation.
    *
    * @param num
    *           the index
    * @param red
    *           the red channel
    * @param green
    *           the green channel
    * @param blue
    *           the blue channel
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
    * @param num
    *           the index
    * @param c0
    *           the first factor
    * @param c1
    *           the second factor
    * @param c2
    *           the third factor
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
    * @param num
    *           the index
    * @param xDir
    *           the direction x
    * @param yDir
    *           the direction y
    * @param zDir
    *           the directoin z
    */
   @Override
   protected void lightNormal (
         final int num,
         final float xDir,
         final float yDir,
         final float zDir ) {

      /*
       * Applying normal matrix to the light direction vector,
       * which is the transpose of the inverse of the modelview.
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
      if (0.0f < mSq) {
         final float mInv = 1.0f / PApplet.sqrt(mSq);
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
    * @param num
    *           the index
    * @param x
    *           the x component
    * @param y
    *           the y component
    * @param z
    *           the z component
    * @param dir
    *           treat as a direction or point
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
       * The w component is 0.0 when the vector represents a
       * direction, 1.0 when the vector represents a point.
       */
      this.lightPosition[num4 + 3] = dir ? 0.0f : 1.0f;
   }

   /**
    * Sets specular lighting. Assumes that red, green and blue
    * color channels have already been calculated
    * appropriately.
    *
    * @param num
    *           the index
    * @param red
    *           the red channel
    * @param green
    *           the green channel
    * @param blue
    *           the blue channel
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
    * @param num
    *           the index
    * @param radians
    *           the angle in radians
    * @param exponent
    *           the exponent
    */
   @Override
   protected void lightSpot (
         final int num,
         final float radians,
         final float exponent ) {

      final int num2 = num + num;
      this.lightSpotParameters[num2] = Utils.max(0.0f, PApplet.cos(radians));
      this.lightSpotParameters[num2 + 1] = exponent;
   }

   /**
    * Turns of ambient lighting.
    *
    * @param num
    *           the index
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
    * @param num
    *           the index
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
    * @param num
    *           the index
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
    * @param num
    *           the index
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
    * @param num
    *           the index
    */
   @Override
   protected void noLightSpot ( final int num ) {

      final int num2 = num + num;
      this.lightSpotParameters[num2] = 0.0f;
      this.lightSpotParameters[num2 + 1] = 0.0f;
   }

   /**
    * Draws a rounded rectangle. The meaning of the first four
    * parameters depends on rectMode.
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

      switch (this.rectMode) {
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
      tl = Utils.clamp(tl, PConstants.EPSILON, limit);
      tr = Utils.clamp(tr, PConstants.EPSILON, limit);
      br = Utils.clamp(br, PConstants.EPSILON, limit);
      bl = Utils.clamp(bl, PConstants.EPSILON, limit);

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
    * Rotates the renderer's model view matrix by an angle in
    * radians around an axis.
    *
    * @param angle
    *           the angle in radians
    * @param xAxis
    *           the axis x component
    * @param yAxis
    *           the axis y component
    * @param zAxis
    *           the axis z component
    * @see IUp#rotate(float, float, float, float, PMatrix3D)
    * @see IUp#invRotate(float, float, float, float, PMatrix3D)
    */
   @Override
   protected void rotateImpl (
         final float angle,
         final float xAxis,
         final float yAxis,
         final float zAxis ) {

      IUp.rotate(angle,
            xAxis, yAxis, zAxis,
            this.modelview);
      IUp.invRotate(angle,
            xAxis, yAxis, zAxis,
            this.modelviewInv);

      this.updateProjmodelview();
   }

   /**
    * Draws a character depending on the text mode.
    *
    * @param ch
    *           the character
    * @param x
    *           the x coordinate
    * @param y
    *           the y coordinate
    */
   @Override
   protected void textCharImpl (
         final char ch,
         final float x, final float y ) {

      switch (this.textMode) {

         case SHAPE:

            this.textCharShapeImpl(ch, x, y);

            break;

         case MODEL:

         default:

            this.textCharModelImpl(ch, x, y);
      }
   }

   /**
    * Draws a character with reference to a a glyph retrieved
    * from the current font.
    *
    * @param ch
    *           the character
    * @param x
    *           the x coordinate
    * @param y
    *           the y coordinate
    * @see PFont#getGlyph(char)
    * @see PFont#getSize()
    */
   protected void textCharModelImpl (
         final char ch,
         final float x, final float y ) {

      /**
       * FontTexture.TextureInfo is a static inner class, and so
       * cannot be accessed from here.
       */

      final PFont.Glyph glyph = this.textFont.getGlyph(ch);
      if (glyph != null) {
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

      this.imageImpl(
            glyph,
            x1, y1, x2, y2,
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
    * Transfers the modelview from a PMatrix3D to a
    * one-dimensional float array used by OpenGL.
    */
   @Override
   protected void updateGLModelview () {

      this.updateGLModelview(this.modelview);
   }

   /**
    * Transfers the transpose of the model vew inverse, which
    * is the normal matrix, from a PMatrix3D to a
    * one-dimensional float array used by OpenGL.
    */
   @Override
   protected void updateGLNormal () {

      this.updateGLNormal(this.modelviewInv);
   }

   /**
    * Transfers the projection from a PMatrix3D to a
    * one-dimensional float array used by OpenGL.
    */
   @Override
   protected void updateGLProjection () {

      this.updateGLProjection(this.projection);
   }

   /**
    * Transfers the project model view from a PMatrix3D to a
    * one-dimensional float array used by OpenGL.
    */
   @Override
   protected void updateGLProjmodelview () {

      this.updateGLProjmodelview(this.projmodelview);
   }

   /**
    * Updates the texture coordinates of the renderer. If the
    * texture mode is IMAGE, divides the uv coordinates by the
    * image's dimensions. If the texture wrap is CLAMP, clamps
    * the coordinates to [0, 1]; if the wrap is REPEAT, floor
    * mods them instead.
    *
    * @param u
    *           the s or u coordinate
    * @param v
    *           the t or v coordinate
    */
   @Override
   protected void vertexTexture ( final float u, final float v ) {

      this.vertexTexture(u, v,
            this.textureMode,
            this.textureWrap);
   }

   /**
    * Updates the texture coordinates of the renderer. If the
    * texture mode is IMAGE, divides the uv coordinates by the
    * image's dimensions. If the texture wrap is CLAMP, clamps
    * the coordinates to [0, 1]; if the wrap is REPEAT, floor
    * mods them instead.
    *
    * @param u
    *           the s or u coordinate
    * @param v
    *           the t or v coordinate
    * @param desiredTextureMode
    *           the texture mode, IMAGE or NORMAL
    * @param desiredTextureWrap
    *           the texture wrap, CLAMP or REPEAT
    * @see Utils#div(float, float)
    * @see Utils#clamp01(float)
    * @see Utils#mod1(float)
    */
   protected void vertexTexture (
         float u,
         float v,
         final int desiredTextureMode,
         final int desiredTextureWrap ) {

      this.textureMode = desiredTextureMode;
      this.textureWrap = desiredTextureWrap;

      /* This operation is also performed by vertexImpl. */
      if (this.textureMode == PConstants.IMAGE
            && this.textureImage != null) {
         u = Utils.div(u, this.textureImage.width);
         v = Utils.div(v, this.textureImage.height);
      }

      switch (desiredTextureWrap) {

         case PConstants.CLAMP:

            this.textureU = Utils.clamp01(u);
            this.textureV = Utils.clamp01(v);

            break;

         case PConstants.REPEAT:

            this.textureU = Utils.mod1(u);
            this.textureV = Utils.mod1(v);

            break;

         default:

            this.textureU = u;
            this.textureV = v;
      }
   }

   /**
    * Applies the matrix to the renderer.
    *
    * @param source
    *           the source matrix
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
    * @param source
    *           the source matrix
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
    * @param m
    *           the matrix
    */
   public void applyProjection ( final Mat4 m ) {

      this.applyProjection(
            m.m00, m.m01, m.m02, m.m03,
            m.m10, m.m11, m.m12, m.m13,
            m.m20, m.m21, m.m22, m.m23,
            m.m30, m.m31, m.m32, m.m33);
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

      switch (mode) {

         case OPEN:

            this.arcMode = Curve2.ArcMode.OPEN;
            this.fill = false;

            break;

         case CHORD:

            this.arcMode = Curve2.ArcMode.CHORD;

            break;

         case PIE:

         default:

            this.arcMode = Curve2.ArcMode.PIE;
      }

      this.transform.moveTo(x, y);
      this.transform.rotateTo(0.0f);
      this.transform.scaleTo(w, h);

      Curve2.arc(start, stop,
            0.5f, this.arcMode, this.arc);
      this.arcImpl(this.arc, this.transform, IUp.DEFAULT_ORDER);

      this.fill = oldFill;
   }

   /**
    * Draws a Bezier curve.
    *
    * @param ap0x
    *           the first anchor point x
    * @param ap0y
    *           the first anchor point y
    * @param cp0x
    *           the first control point x
    * @param cp0y
    *           the first control point y
    * @param cp1x
    *           the second control point x
    * @param cp1y
    *           the second control point y
    * @param ap1x
    *           the second anchor point x
    * @param ap1y
    *           the second anchor point y
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
    * @param ap0x
    *           the first anchor point x
    * @param ap0y
    *           the first anchor point y
    * @param ap0z
    *           the first anchor point z
    * @param cp0x
    *           the first control point x
    * @param cp0y
    *           the first control point y
    * @param cp0z
    *           the first control point z
    * @param cp1x
    *           the second control point x
    * @param cp1y
    *           the second control point y
    * @param cp1z
    *           the second control point z
    * @param ap1x
    *           the second anchor point x
    * @param ap1y
    *           the second anchor point y
    * @param ap1z
    *           the second anchor point z
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
    * Finds a point on a curve according to a step in the range
    * [0.0, 1.0].
    *
    * @param ap0
    *           the first anchor point
    * @param cp0
    *           the first control point
    * @param cp1
    *           the second control point
    * @param ap1
    *           the second anchor point
    * @param step
    *           the step
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
    * Finds a tangent on a curve according to a step in the
    * range [0.0, 1.0].
    *
    * @param ap0
    *           the first anchor point
    * @param cp0
    *           the first control point
    * @param cp1
    *           the second control point
    * @param ap1
    *           the second anchor point
    * @param step
    *           the step
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
    * Exposes the color calculation to the public. Includes the
    * option to premultiply alpha. Refers to the helper
    * function
    * {@link UpOgl#colorPreCalc(float, float, float, float)} to
    * perform part of the calculation independent of this
    * consideration.
    *
    * @param x
    *           the first color channel, hue or red
    * @param y
    *           the second color channel, saturation or green
    * @param z
    *           the third color channel, brightness or blue
    * @param a
    *           the alpha channel
    * @param premul
    *           pre-multiply alpha
    * @see UpOgl#colorPreCalc(float, float, float, float)
    */
   public void colorCalc (
         final float x,
         final float y,
         final float z,
         final float a,
         final boolean premul ) {

      if (premul) {
         if (a <= 0.0f) {
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

         if (this.calcA < 1.0f) {
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
    * A helper function to color calculation, exposed to the
    * public. Calculates color based on the color mode, HSB or
    * RGB, regardless of whether or not the color is to be
    * pre-multiplied.
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
   }

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
    * Sets the renderer projection matrix to the frustum
    * defined by the edges of the view port.
    *
    * @param left
    *           the left edge of the window
    * @param right
    *           the right edge of the window
    * @param bottom
    *           the bottom edge of the window
    * @param top
    *           the top edge of the window
    * @param near
    *           the near clip plane
    * @param far
    *           the far clip plane
    */
   @Override
   public void frustum (
         final float left, final float right,
         final float bottom, final float top,
         final float near, final float far ) {

      IUp.frustum(
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
    * Gets the renderer camera's location on the x axis.
    *
    * @return the x location
    */
   public float getLocX () {

      return this.cameraX;
   }

   /**
    * Gets the renderer camera's location on the y axis.
    *
    * @return the y location
    */
   public float getLocY () {

      return this.cameraY;
   }

   /**
    * Gets the renderer camera's location on the z axis.
    *
    * @return the z location
    */
   public float getLocZ () {

      return this.cameraZ;
   }

   /**
    * Gets the renderer modelview matrix.
    *
    * @param target
    *           the output matrix
    * @return the modelview
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
    * Gets the renderer modelview matrix.
    *
    * @param target
    *           the output matrix
    * @return the modelview
    */
   @Override
   public PMatrix3D getMatrix ( PMatrix3D target ) {

      if (target == null) {
         target = new PMatrix3D();
      }
      target.set(this.modelview);
      return target;
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
    * Gets the renderer's texture sampling as an enum constant.
    */
   @Override
   public Sampling getTextureSampling () {

      return Sampling.fromValue(this.textureSampling);
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
    * Displays a PGraphicsOpenGL at the origin.
    *
    * @param buff
    *           the renderer
    */
   public void image ( final PGraphicsOpenGL buff ) {

      if (buff.pgl.threadIsCurrent()) {
         this.image((PImage) buff);
      }
   }

   /**
    * Displays a PGraphicsOpenGL buffer. Checks if the buffer's
    * PGL thread is current before proceeding. This is to help
    * ensure that beginDraw and endDraw have already been
    * called.
    *
    * @param buff
    *           the renderer
    * @param x
    *           the first x coordinate
    * @param y
    *           the first y coordinate
    */
   public void image (
         final PGraphicsOpenGL buff,
         final float x,
         final float y ) {

      if (buff.pgl.threadIsCurrent()) {
         this.image((PImage) buff, x, y);
      }
   }

   /**
    * Displays a PGraphicsOpenGL buffer. Checks if the buffer's
    * PGL thread is current before proceeding. This is to help
    * ensure that beginDraw and endDraw have already been
    * called.
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
   public void image (
         final PGraphicsOpenGL buff,
         final float x, final float y,
         final float u, final float v ) {

      if (buff.pgl.threadIsCurrent()) {
         this.image((PImage) buff, x, y, u, v);
      }
   }

   /**
    * Displays a PGraphicsOpenGL buffer. Checks if the buffer's
    * PGL thread is current before proceeding. This is to help
    * ensure that beginDraw and endDraw have already been
    * called.
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
    * @param a
    *           the image top-left corner u
    * @param b
    *           the image top-left corner v
    * @param c
    *           the image bottom-right corner u
    * @param d
    *           the image bottom-right cornver v
    */
   public void image (
         final PGraphicsOpenGL buff,
         final float x, final float y,
         final float u, final float v,

         final int a, final int b,
         final int c, final int d ) {

      if (buff.pgl.threadIsCurrent()) {
         this.image((PImage) buff, x, y, u, v, a, b, c, d);
      }
   }

   /**
    * Displays a PImage at the origin.
    *
    * @param img
    *           the image
    */
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

      this.image(img, x, y, img.width, img.height);
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

      float vtx = 1.0f;
      float vty = 1.0f;
      if (this.textureMode == PConstants.IMAGE) {
         vtx = img.width;
         vty = img.height;
      }
      this.imageImpl(img,
            x, y,
            u, v,

            0.0f, 0.0f,
            vtx, vty);
   }

   /**
    * Displays a PImage. The meaning of the first four
    * parameters depends on imageMode. The last four
    * coordinates specify the image texture coordinates (or
    * UVs).
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
    * @param a
    *           the image top-left corner u
    * @param b
    *           the image top-left corner v
    * @param c
    *           the image bottom-right corner u
    * @param d
    *           the image bottom-right cornver v
    */
   @Override
   public void image (
         final PImage img,
         final float x, final float y,
         final float u, final float v,

         final int a, final int b,
         final int c, final int d ) {

      this.imageImpl(img, x, y, u, v,
            (float) a, (float) b,
            (float) c, (float) d);
   }

   /**
    * Displays a PGraphicsOpenGL buffer. Checks if the buffer's
    * PGL thread is current before proceeding. This is to help
    * ensure that beginDraw and endDraw have already been
    * called.
    *
    * @param buff
    *           the renderer
    * @param x1
    *           the first x coordinate
    * @param y1
    *           the first y coordinate
    * @param x2
    *           the second x coordinate
    * @param y2
    *           the second y coordinate
    * @param u1
    *           the image top-left corner u
    * @param v1
    *           the image top-left corner v
    * @param u2
    *           the image bottom-right corner u
    * @param v2
    *           the image bottom-right cornver v
    */
   public void imageImpl (
         final PGraphicsOpenGL buff,
         final float x1, final float y1,
         final float x2, final float y2,

         final float u1, final float v1,
         final float u2, final float v2 ) {

      if (buff.pgl.threadIsCurrent()) {
         this.imageImpl((PImage) buff, x1, y1, x2, y2, u1, v1, u2, v2);
      }
   }

   /**
    * Displays a PImage. The meaning of the first four
    * parameters depends on imageMode. The last four
    * coordinates specify the image texture coordinates (or
    * UVs).
    *
    * @param img
    *           the PImage
    * @param x1
    *           the first x coordinate
    * @param y1
    *           the first y coordinate
    * @param x2
    *           the second x coordinate
    * @param y2
    *           the second y coordinate
    * @param u1
    *           the image top-left corner u
    * @param v1
    *           the image top-left corner v
    * @param u2
    *           the image bottom-right corner u
    * @param v2
    *           the image bottom-right cornver v
    */
   public void imageImpl (
         final PImage img,
         final float x1, final float y1,
         final float x2, final float y2,

         final float u1, final float v1,
         final float u2, final float v2 ) {

      if (img.width < 2 || img.height < 2) {
         return;
      }

      this.pushStyle();
      this.noStroke();

      this.beginShape(PConstants.POLYGON);
      this.normal(0.0f, 0.0f, 1.0f);
      this.texture(img);
      switch (this.imageMode) {

         case CORNER:

            this.vertexImpl(x1, y1, 0.0f, u1, v1);
            this.vertexImpl(x1 + x2, y1, 0.0f, u2, v1);
            this.vertexImpl(x1 + x2, y1 - y2, 0.0f, u2, v2);
            this.vertexImpl(x1, y1 - y2, 0.0f, u1, v2);

            break;

         case CORNERS:

            this.vertexImpl(x1, y1, 0.0f, u1, v2);
            this.vertexImpl(x2, y1, 0.0f, u2, v2);
            this.vertexImpl(x2, y2, 0.0f, u2, v1);
            this.vertexImpl(x1, y2, 0.0f, u1, v1);

            break;

         case CENTER:

         default:

            final float hu = x2 * 0.5f;
            final float hv = y2 * 0.5f;

            this.vertexImpl(x1 - hu, y1 + hv, 0.0f, u1, v1);
            this.vertexImpl(x1 + hu, y1 + hv, 0.0f, u2, v1);
            this.vertexImpl(x1 + hu, y1 - hv, 0.0f, u2, v2);
            this.vertexImpl(x1 - hu, y1 - hv, 0.0f, u1, v2);
      }
      this.endShape(PConstants.CLOSE);
      this.popStyle();
   }

   /**
    * Returns whether or not the renderer is 2D.
    */
   @Override
   public abstract boolean is2D ();

   /**
    * Returns whether or not the renderer is 3D.
    */
   @Override
   public abstract boolean is3D ();

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
         final Color origin,
         final Color dest,
         final float step,
         final Color target ) {

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
    * Processing's modelX, modelY and modelZ functions are very
    * inefficient, as each one calculates the product of the
    * modelview and the point. This function groups all three
    * model functions into one.
    *
    * @param point
    *           the input point
    * @param target
    *           the output point
    * @return the model space point
    */
   public Vec3 model (
         final Vec3 point,
         final Vec3 target ) {

      /*
       * Multiply point by model-view matrix.
       */
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

      /*
       * Multiply point by inverse of camera matrix.
       */
      final float bw = this.cameraInv.m30 * ax +
            this.cameraInv.m31 * ay +
            this.cameraInv.m32 * az +
            this.cameraInv.m33 * aw;

      if (bw == 0.0f) {
         return target.reset();
      }

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

      if (bw == 1.0f) {
         return target.set(bx, by, bz);
      }

      /*
       * Convert from homogenous coordinate to point by dividing
       * by fourth component, w.
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
   public abstract void origin ();

   /**
    * Sets the renderer projection to orthographic, where
    * objects maintain their size regardless of distance from
    * the camera.
    */
   @Override
   public void ortho () {

      /*
       * CAUTION: Never use defCameraXXX values. They are not
       * actual constants.
       */

      final float right = this.width < 128 ? IUp.DEFAULT_HALF_WIDTH
            : this.width * 0.5f;
      final float left = -right;

      final float top = this.height < 128 ? IUp.DEFAULT_HALF_HEIGHT
            : this.height * 0.5f;
      final float bottom = -top;

      this.ortho(left, right, bottom, top);
   }

   /**
    * Sets the renderer projection to orthographic, where
    * objects maintain their size regardless of distance from
    * the camera.
    *
    * @param left
    *           the left edge of the window
    * @param right
    *           the right edge of the window
    * @param bottom
    *           the bottom edge of the window
    * @param top
    *           the top edge of the window
    */
   @Override
   public void ortho (
         final float left, final float right,
         final float bottom, final float top ) {

      /*
       * CAUTION: Never use defCameraXXX values. They are not
       * actual constants.
       */

      this.ortho(left, right,
            bottom, top,
            IUp.DEFAULT_NEAR_CLIP, IUp.DEFAULT_FAR_CLIP);
   }

   /**
    * Sets the renderer projection to orthographic, where
    * objects maintain their size regardless of distance from
    * the camera.
    *
    * @param left
    *           the left edge of the window
    * @param right
    *           the right edge of the window
    * @param bottom
    *           the bottom edge of the window
    * @param top
    *           the top edge of the window
    * @param near
    *           the near clip plane
    * @param far
    *           the far clip plane
    */
   @Override
   public void ortho (
         final float left, final float right,
         final float bottom, final float top,
         final float near, final float far ) {

      IUp.orthographic(
            left, right,
            bottom, top,
            near, far,
            this.projection);
   }

   /**
    * Sets the renderer projection to a perspective, where
    * objects nearer to the camera appear larger than objects
    * distant from the camera.
    */
   @Override
   public void perspective () {

      /*
       * CAUTION: Never use defCameraXXX values. They are not
       * actual constants.
       */

      this.perspective(
            IUp.DEFAULT_FOV,
            IUp.DEFAULT_ASPECT,
            IUp.DEFAULT_NEAR_CLIP,
            IUp.DEFAULT_FAR_CLIP);
   }

   /**
    * Sets the renderer projection to a perspective, where
    * objects nearer to the camera appear larger than objects
    * distant from the camera.
    *
    * @param fov
    *           the field of view
    * @param aspect
    *           the aspect ratio, width over height
    * @param near
    *           the near clip plane
    * @param far
    *           the far clip plane
    */
   @Override
   public void perspective (
         final float fov, final float aspect,
         final float near, final float far ) {

      IUp.perspective(
            fov, aspect,
            near, far,
            this.projection);
   }

   @Override
   public void printCamera () {

      System.out.println(IUp.toString(this.camera, 4));
   }

   @Override
   public void printMatrix () {

      System.out.println(IUp.toString(this.modelview, 4));
   }

   @Override
   public void printProjection () {

      System.out.println(IUp.toString(this.projection, 4));
   }

   @Override
   public void pushProjection () {

      if (this.projectionStackDepth == PGraphicsOpenGL.MATRIX_STACK_DEPTH) {
         throw new RuntimeException(PGraphics.ERROR_PUSHMATRIX_OVERFLOW);
      }
      this.projection.get(this.projectionStack[this.projectionStackDepth]);
      this.projectionStackDepth++;
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

      this.beginShape(PConstants.POLYGON);
      this.normal(0.0f, 0.0f, 1.0f);
      this.vertexImpl(x0, y0, 0.0f, 0.5f, 0.5f);
      this.vertexImpl(x1, y1, 0.0f, 0.5f, 0.5f);
      this.vertexImpl(x2, y2, 0.0f, 0.5f, 0.5f);
      this.vertexImpl(x3, y3, 0.0f, 0.5f, 0.5f);
      this.endShape(PConstants.CLOSE);
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

      this.beginShape(PConstants.POLYGON);
      this.normal(0.0f, 0.0f, 1.0f);
      this.vertexImpl(x0, y0, 0.0f, 0.0f, 0.0f);
      this.vertexImpl(x1, y0, 0.0f, 1.0f, 0.0f);
      this.vertexImpl(x1, y1, 0.0f, 1.0f, 1.0f);
      this.vertexImpl(x0, y1, 0.0f, 0.0f, 1.0f);
      this.endShape(PConstants.CLOSE);
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
    * Rotates the modelview matrix around the z axis by an
    * angle in radians.
    *
    * @param angle
    *           the angle in radians
    */
   @Override
   public void rotate ( final float angle ) {

      this.rotateZ(angle);
   }

   /**
    * Rotates the modelview matrix around an arbitrary axis by
    * an angle in radians.
    *
    * @param angle
    *           the angle in radians
    * @param xAxis
    *           the axis x coordinate
    * @param yAxis
    *           the axis y coordinate
    * @param zAxis
    *           the axis z coordinate
    */
   @Override
   public void rotate (
         final float angle,
         final float xAxis,
         final float yAxis,
         final float zAxis ) {

      this.rotateImpl(angle, xAxis, yAxis, zAxis);
   }

   /**
    * Rotates the sketch by an angle in radians around an
    * arbitrary axis.
    *
    * @param angle
    *           the angle
    * @param axis
    *           the axis
    */
   public void rotate (
         final float angle,
         final Vec3 axis ) {

      this.rotateImpl(angle,
            axis.x, axis.y, axis.z);
   }

   /**
    * Rotates the sketch by an angle in radians around the x
    * axis.
    *
    * Do not use sequences of ortho-normal rotations by Euler
    * angles; this will result in gimbal lock. Instead, rotate
    * by an angle around an axis.
    *
    * @param angle
    *           the angle
    * @see Up3#rotate(float, float, float, float)
    */
   @Override
   public void rotateX ( final float angle ) {

      IUp.rotateX(angle, this.modelview);
      IUp.invRotateX(angle, this.modelviewInv);
      this.updateProjmodelview();
   }

   /**
    * Rotates the sketch by an angle in radians around the y
    * axis.
    *
    * Do not use sequences of ortho-normal rotations by Euler
    * angles; this will result in gimbal lock. Instead, rotate
    * by an angle around an axis.
    *
    * @param angle
    *           the angle
    * @see Up3#rotate(float, float, float, float)
    */
   @Override
   public void rotateY ( final float angle ) {

      IUp.rotateY(angle, this.modelview);
      IUp.invRotateY(angle, this.modelviewInv);
      this.updateProjmodelview();
   }

   /**
    * Rotates the sketch by an angle in radians around the z
    * axis.
    *
    * Do not use sequences of ortho-normal rotations by Euler
    * angles; this will result in gimbal lock. Instead, rotate
    * by an angle around an axis.
    *
    * @param angle
    *           the angle
    * @see Up3#rotate(float, float, float, float)
    */
   @Override
   public void rotateZ ( final float angle ) {

      IUp.rotateZ(angle, this.modelview);
      IUp.invRotateZ(angle, this.modelviewInv);
      this.updateProjmodelview();
   }

   public Vec3 screen ( final Vec3 v, final Vec3 target ) {

      /*
       * Multiply point by model-view matrix.
       */
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

      final float aw = this.modelview.m30 * v.x +
            this.modelview.m31 * v.y +
            this.modelview.m32 * v.z +
            this.modelview.m33;

      /*
       * Multiply new point by projection.
       */
      final float bw = this.projection.m30 * ax +
            this.projection.m31 * ay +
            this.projection.m32 * az +
            this.projection.m33 * aw;

      if (bw == 0.0f) {
         return target.reset();
      }

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

      if (bw != 1.0f) {
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
    * @param source
    *           the source matrix
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
    * @param source
    *           the source matrix
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
    * @param source
    *           the source matrix
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
    * @param m
    *           the matrix
    */
   public void setProjection ( final Mat4 m ) {

      this.flush();
      this.projection.set(
            m.m00, m.m01, m.m02, m.m03,
            m.m10, m.m11, m.m12, m.m13,
            m.m20, m.m21, m.m22, m.m23,
            m.m30, m.m31, m.m32, m.m33);
      this.updateProjmodelview();
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
      this.updatePixelSize();

      this.texture = null;
      this.ptexture = null;

      this.defCameraFOV = IUp.DEFAULT_FOV;
      this.defCameraX = 0.0f;
      this.defCameraY = 0.0f;
      this.defCameraZ = this.height
            / PApplet.tan(this.defCameraFOV * 0.5f);
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
    * Sets the renderer's texture sampling from an enum
    * constant.
    *
    * @param sampleType
    *           the sample type
    */
   @Override
   public void setTextureSampling ( final Sampling sampleType ) {

      this.textureSampling = sampleType.getVal();
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
    * Displays a boolean as text at a location.
    *
    * @param b
    *           the boolean
    * @param x
    *           the x coordinate
    * @param y
    *           the y coordinate
    */
   public void text (
         final boolean b,
         final float x,
         final float y ) {

      this.text(String.valueOf(b), x, y);
   }

   /**
    * Displays a character at a location.
    *
    * @param c
    *           the character
    * @param x
    *           the x coordinate
    * @param y
    *           the y coordinate
    */
   @Override
   public void text (
         final char c,
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

      this.textBuffer[0] = c;
      this.textLineAlignImpl(this.textBuffer, 0, 1, x, y);
   }

   /**
    * Displays a character as text at a given coordinate.
    *
    * @param c
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
         final char c,
         final float x,
         final float y,
         final float z ) {

      this.text(c, x, y);
   }

   /**
    * Displays an array of characters as text at a given
    * coordinate.
    *
    * @param chars
    *           the character array
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
    * Displays an array of characters as text at a given
    * coordinate.
    *
    * @param chars
    *           the character array
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
      for (int i = start; i < stop; ++i) {
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
    * Displays an array of characters as text at a given
    * coordinate.
    *
    * @param chars
    *           the character array
    * @param start
    *           the start index, inclusive
    * @param stop
    *           the stop index, exclusive
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
         final int start,
         final int stop,
         final float x,
         final float y,
         final float z ) {

      this.text(chars, start, stop, x, y);
   }

   /**
    * Displays a number as text. Registers up to four decimal
    * places.
    *
    * @param num
    *           the number
    * @param x
    *           the x coordinate
    * @param y
    *           the y coordinate
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
    * Displays a real number as text. Registers up to four
    * decimal places.
    *
    * @param num
    *           the number
    * @param x
    *           the x coordinate
    * @param y
    *           the y coordinate
    * @param z
    *           the z coordinate
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

      this.text(num, x, y);
   }

   /**
    * Displays an object as text at a location. Calls the
    * object's toString function.
    *
    * @param obj
    *           the object
    * @param x
    *           the x coordinate
    * @param y
    *           the y coordinate
    */
   public void text (
         final Object obj,
         final float x,
         final float y ) {

      this.text(obj.toString(), x, y);
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
    * Displays a string at a coordinate.
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

      this.text(str, x, y);
   }

   /**
    * Displays a string at a coordinate. This version of text
    * is not supported, so only x1 and y1 are used.
    *
    * @param str
    *           the string
    * @param x1
    *           the x coordinate
    * @param y1
    *           the y coordinate
    * @param x2
    *           the second x coordinate
    * @param y2
    *           the second y coordinate
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
    * Assigns the PGraphicsOpenGL buffer as the current
    * texture.
    *
    * @param buff
    *           the buffer
    */
   public void texture ( final PGraphicsOpenGL buff ) {

      if (buff.pgl.threadIsCurrent()) {
         this.texture((PImage) buff);
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
    * Sets the renderer's texture sampling from an enum
    * constant.
    *
    * @param sampleType
    *           the sample type
    */
   public void textureSampling ( final Sampling sampleType ) {

      this.textureSampling = sampleType.getVal();
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
   public void transform (
         final Transform2 tr2,
         final Transform.Order order ) {

      final Vec2 dim = tr2.getScale(this.tr2Scale);
      final Vec2 loc = tr2.getLocation(this.tr2Loc);
      final float angle = tr2.getRotation();

      switch (order) {

         case RST:

            this.rotateZ(angle);
            this.scale(dim.x, dim.y, 1.0f);
            this.translate(loc.x, loc.y, 0.0f);

            return;

         case RTS:

            this.rotateZ(angle);
            this.translate(loc.x, loc.y, 0.0f);
            this.scale(dim.x, dim.y, 1.0f);
            return;

         case SRT:

            this.scale(dim.x, dim.y, 1.0f);
            this.rotateZ(angle);
            this.translate(loc.x, loc.y, 0.0f);

            return;

         case STR:

            this.scale(dim.x, dim.y, 1.0f);
            this.translate(loc.x, loc.y, 0.0f);
            this.rotateZ(angle);

            return;

         case TSR:

            this.translate(loc.x, loc.y, 0.0f);
            this.scale(dim.x, dim.y, 1.0f);
            this.rotateZ(angle);

            return;

         case TRS:

         default:

            this.translate(loc.x, loc.y, 0.0f);
            this.rotateZ(angle);
            this.scale(dim.x, dim.y, 1.0f);

            return;
      }
   }

   /**
    * Exposes the OpenGL model view array to more direct
    * access.
    *
    * @param m00
    *           row 0, column 0
    * @param m01
    *           row 0, column 1
    * @param m02
    *           row 0, column 2
    * @param m03
    *           row 0, column 3
    * @param m10
    *           row 1, column 0
    * @param m11
    *           row 1, column 1
    * @param m12
    *           row 1, column 2
    * @param m13
    *           row 1, column 3
    * @param m20
    *           row 2, column 0
    * @param m21
    *           row 2, column 1
    * @param m22
    *           row 2, column 2
    * @param m23
    *           row 2, column 3
    * @param m30
    *           row 3, column 0
    * @param m31
    *           row 3, column 1
    * @param m32
    *           row 3, column 2
    * @param m33
    *           row 3, column 3
    */
   public void updateGLModelview (
         final float m00, final float m01, final float m02, final float m03,
         final float m10, final float m11, final float m12, final float m13,
         final float m20, final float m21, final float m22, final float m23,
         final float m30, final float m31, final float m32, final float m33 ) {

      if (this.glModelview == null) {
         this.glModelview = new float[16];
      }

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
    * Exposes the OpenGL model view array to more direct
    * access.
    *
    * @param m
    *           the matrix
    */
   public void updateGLModelview ( final Mat4 m ) {

      this.updateGLModelview(
            m.m00, m.m01, m.m02, m.m03,
            m.m10, m.m11, m.m12, m.m13,
            m.m20, m.m21, m.m22, m.m23,
            m.m30, m.m31, m.m32, m.m33);
   }

   /**
    * Exposes the OpenGL model view array to more direct
    * access.
    *
    * @param m
    *           the Processing matrix
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
    * @param m00
    *           row 0, column 0
    * @param m01
    *           row 0, column 1
    * @param m02
    *           row 0, column 2
    * @param m10
    *           row 1, column 0
    * @param m11
    *           row 1, column 1
    * @param m12
    *           row 1, column 2
    * @param m20
    *           row 2, column 0
    * @param m21
    *           row 2, column 1
    * @param m22
    *           row 2, column 2
    */
   public void updateGLNormal (
         final float m00, final float m01, final float m02,
         final float m10, final float m11, final float m12,
         final float m20, final float m21, final float m22 ) {

      if (this.glNormal == null) {
         this.glNormal = new float[9];
      }

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
    * @param m
    *           the matrix
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
    * @param m
    *           the Processing matrix
    */
   public void updateGLNormal ( final PMatrix3D m ) {

      this.updateGLNormal(
            m.m00, m.m01, m.m02,
            m.m10, m.m11, m.m12,
            m.m20, m.m21, m.m22);
   }

   /**
    * Exposes the OpenGL projection array to more direct
    * access.
    *
    * @param m00
    *           row 0, column 0
    * @param m01
    *           row 0, column 1
    * @param m02
    *           row 0, column 2
    * @param m03
    *           row 0, column 3
    * @param m10
    *           row 1, column 0
    * @param m11
    *           row 1, column 1
    * @param m12
    *           row 1, column 2
    * @param m13
    *           row 1, column 3
    * @param m20
    *           row 2, column 0
    * @param m21
    *           row 2, column 1
    * @param m22
    *           row 2, column 2
    * @param m23
    *           row 2, column 3
    * @param m30
    *           row 3, column 0
    * @param m31
    *           row 3, column 1
    * @param m32
    *           row 3, column 2
    * @param m33
    *           row 3, column 3
    */
   public void updateGLProjection (
         final float m00, final float m01, final float m02, final float m03,
         final float m10, final float m11, final float m12, final float m13,
         final float m20, final float m21, final float m22, final float m23,
         final float m30, final float m31, final float m32, final float m33 ) {

      if (this.glProjection == null) {
         this.glProjection = new float[16];
      }

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
    * Exposes the OpenGL projection array to more direct
    * access.
    *
    * @param m
    *           the matrix
    */
   public void updateGLProjection ( final Mat4 m ) {

      this.updateGLProjection(
            m.m00, m.m01, m.m02, m.m03,
            m.m10, m.m11, m.m12, m.m13,
            m.m20, m.m21, m.m22, m.m23,
            m.m30, m.m31, m.m32, m.m33);
   }

   /**
    * Exposes the OpenGL projection array to more direct
    * access.
    *
    * @param m
    *           the Processing matrix
    */
   public void updateGLProjection ( final PMatrix3D m ) {

      this.updateGLProjection(
            m.m00, m.m01, m.m02, m.m03,
            m.m10, m.m11, m.m12, m.m13,
            m.m20, m.m21, m.m22, m.m23,
            m.m30, m.m31, m.m32, m.m33);
   }

   /**
    * Exposes the OpenGL project model view array to more
    * direct access.
    *
    * @param m00
    *           row 0, column 0
    * @param m01
    *           row 0, column 1
    * @param m02
    *           row 0, column 2
    * @param m03
    *           row 0, column 3
    * @param m10
    *           row 1, column 0
    * @param m11
    *           row 1, column 1
    * @param m12
    *           row 1, column 2
    * @param m13
    *           row 1, column 3
    * @param m20
    *           row 2, column 0
    * @param m21
    *           row 2, column 1
    * @param m22
    *           row 2, column 2
    * @param m23
    *           row 2, column 3
    * @param m30
    *           row 3, column 0
    * @param m31
    *           row 3, column 1
    * @param m32
    *           row 3, column 2
    * @param m33
    *           row 3, column 3
    */
   public void updateGLProjmodelview (
         final float m00, final float m01, final float m02, final float m03,
         final float m10, final float m11, final float m12, final float m13,
         final float m20, final float m21, final float m22, final float m23,
         final float m30, final float m31, final float m32, final float m33 ) {

      if (this.glProjmodelview == null) {
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
    * Exposes the OpenGL project model view array to more
    * direct access.
    *
    * @param m
    *           the matrix
    */
   public void updateGLProjmodelview ( final Mat4 m ) {

      this.updateGLProjmodelview(
            m.m00, m.m01, m.m02, m.m03,
            m.m10, m.m11, m.m12, m.m13,
            m.m20, m.m21, m.m22, m.m23,
            m.m30, m.m31, m.m32, m.m33);
   }

   /**
    * Exposes the OpenGL project model view array to more
    * direct access.
    *
    * @param m
    *           the Processing matrix
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
    * The overriden functionality eliminates unnecessary
    * checks.
    */
   @Override
   public void updatePixels () {

      if (!this.modified) {
         this.mx1 = 0;
         this.mx2 = this.pixelWidth;
         this.my1 = 0;
         this.my2 = this.pixelHeight;
         this.modified = true;

      } else {
         if (0 < this.mx1) {
            this.mx1 = 0;
         }
         if (0 > this.mx2) {
            this.mx2 = PApplet.min(this.pixelWidth, 0);
         }
         if (0 < this.my1) {
            this.my1 = 0;
         }
         if (0 > this.my2) {
            this.my2 = PApplet.min(this.pixelHeight, 0);
         }

         if (this.pixelWidth < this.mx1) {
            this.mx1 = PApplet.max(0, this.pixelWidth);
         }
         if (this.pixelWidth > this.mx2) {
            this.mx2 = this.pixelWidth;
         }
         if (this.pixelHeight < this.my1) {
            this.my1 = PApplet.max(0, this.pixelHeight);
         }
         if (this.pixelHeight > this.my2) {
            this.my2 = this.pixelHeight;
         }
      }
   }

   /**
    * Draws a vertex.
    *
    * @param x
    *           the x coordinate
    * @param y
    *           the y coordinate
    */
   @Override
   public void vertex (
         final float x,
         final float y ) {

      this.vertexImpl(
            x, y, 0.0f,
            this.textureU, this.textureV);
   }

   /**
    * Draws a vertex.
    *
    * @param x
    *           the x coordinate
    * @param y
    *           the y coordinate
    * @param z
    *           the z coordinate
    */
   @Override
   public void vertex (
         final float x,
         final float y,
         final float z ) {

      this.vertexImpl(
            x, y, z,
            this.textureU, this.textureV);
   }

   /**
    * Draws a vertex.
    *
    * @param x
    *           the x coordinate
    * @param y
    *           the y coordinate
    * @param u
    *           the u texture coordinate
    * @param v
    *           the v texture coordinate
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
    * @param x
    *           the x coordinate
    * @param y
    *           the y coordinate
    * @param z
    *           the z coordinate
    * @param u
    *           the u texture coordinate
    * @param v
    *           the v texture coordinate
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
    * @param vt
    *           the texture coordinate
    */
   public void vertexTexture ( final Vec2 vt ) {

      this.vertexTexture(vt.x, vt.y);
   }
}

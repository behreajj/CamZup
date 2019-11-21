package camzup.pfriendly;

import camzup.core.Vec3;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.opengl.PGraphicsOpenGL;

/**
 * A 3D renderer where (0.0, 1.0, 0.0) is the default world
 * up axis.
 */
public class Yup3 extends Up3 {

   /**
    * Default lighting directional light axis x component.
    */
   public static final float DEFAULT_LIGHT_X = -0.18490006f;

   /**
    * Default lighting directional light axis y component.
    */
   public static final float DEFAULT_LIGHT_Y = -0.73960024f;

   /**
    * Default lighting directional light axis z component.
    */
   public static final float DEFAULT_LIGHT_Z = 0.6471502f;

   /**
    * Default camera location x component.
    */
   public static final float DEFAULT_LOC_X = 623.53829072f;

   /**
    * Default camera location y component.
    */
   public static final float DEFAULT_LOC_Y = 623.53829072f;

   /**
    * Default camera location z component.
    */
   public static final float DEFAULT_LOC_Z = -623.53829072f;

   /**
    * Default world up x component.
    */
   public static final float DEFAULT_REF_X = 0.0f;

   /**
    * Default world up y component.
    */
   public static final float DEFAULT_REF_Y = 1.0f;

   /**
    * Default world up z component.
    */
   public static final float DEFAULT_REF_Z = 0.0f;

   /**
    * The default constructor.
    */
   public Yup3 () {

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
   public Yup3 (
         final int width, final int height,
         final PApplet parent,
         final String path,
         final boolean isPrimary ) {

      super(width, height, parent, path, isPrimary);
   }

   /**
    * Sets default camera location and calls the camera
    * function.
    *
    * @see PGraphicsOpenGL#defCameraX
    * @see PGraphicsOpenGL#defCameraY
    * @see PGraphicsOpenGL#defCameraZ
    */
   @Override
   protected void defaultCamera () {

      this.defCameraX = Yup3.DEFAULT_LOC_X;
      this.defCameraY = Yup3.DEFAULT_LOC_Y;
      this.defCameraZ = Yup3.DEFAULT_LOC_Z;

      this.camera();
   }

   /**
    * Draws a 3D cube of a given size.
    *
    * @param size
    *           the size
    */
   @Override
   public void box ( final float size ) {

      this.box(size, size, size);
   }

   /**
    * Draws a 3D box with the given width, height and depth.
    *
    * @param w
    *           the width
    * @param h
    *           the height
    * @param d
    *           the depth
    */
   @Override
   public void box ( final float w, final float h, final float d ) {

      final float wHalf = w * 0.5f;
      final float hHalf = h * 0.5f;
      final float dHalf = d * 0.5f;

      /* Right */
      this.beginShape(PConstants.POLYGON);
      this.normal(1.0f, 0.0f, 0.0f);
      this.vertexImpl(
            wHalf, hHalf, dHalf,
            1.0f, 0.0f);
      this.vertexImpl(
            wHalf, -hHalf, -dHalf,
            0.0f, 1.0f);
      this.vertexImpl(
            wHalf, hHalf, -dHalf,
            1.0f, 1.0f);
      this.endShape(PConstants.CLOSE);

      this.beginShape(PConstants.POLYGON);
      this.normal(1.0f, 0.0f, 0.0f);
      this.vertexImpl(
            wHalf, hHalf, dHalf,
            1.0f, 0.0f);
      this.vertexImpl(
            wHalf, -hHalf, dHalf,
            0.0f, 0.0f);
      this.vertexImpl(
            wHalf, -hHalf, -dHalf,
            0.0f, 1.0f);
      this.endShape(PConstants.CLOSE);

      /* Left */
      this.beginShape(PConstants.POLYGON);
      this.normal(-1.0f, 0.0f, 0.0f);
      this.vertexImpl(
            -wHalf, -hHalf, dHalf,
            1.0f, 0.0f);
      this.vertexImpl(
            -wHalf, hHalf, -dHalf,
            0.0f, 1.0f);
      this.vertexImpl(
            -wHalf, -hHalf, -dHalf,
            1.0f, 1.0f);
      this.endShape(PConstants.CLOSE);

      this.beginShape(PConstants.POLYGON);
      this.normal(-1.0f, 0.0f, 0.0f);
      this.vertexImpl(
            -wHalf, -hHalf, dHalf,
            1.0f, 0.0f);
      this.vertexImpl(
            -wHalf, hHalf, dHalf,
            0.0f, 0.0f);
      this.vertexImpl(
            -wHalf, hHalf, -dHalf,
            0.0f, 1.0f);
      this.endShape(PConstants.CLOSE);

      /* Forward */
      this.beginShape(PConstants.POLYGON);
      this.normal(0.0f, 1.0f, 0.0f);
      this.vertexImpl(
            -wHalf, hHalf, dHalf,
            1.0f, 0.0f);
      this.vertexImpl(
            wHalf, hHalf, -dHalf,
            0.0f, 1.0f);
      this.vertexImpl(
            -wHalf, hHalf, -dHalf,
            1.0f, 1.0f);
      this.endShape(PConstants.CLOSE);

      this.beginShape(PConstants.POLYGON);
      this.normal(0.0f, 1.0f, 0.0f);
      this.vertexImpl(
            -wHalf, hHalf, dHalf,
            1.0f, 0.0f);
      this.vertexImpl(
            wHalf, hHalf, dHalf,
            0.0f, 0.0f);
      this.vertexImpl(
            wHalf, hHalf, -dHalf,
            0.0f, 1.0f);
      this.endShape(PConstants.CLOSE);

      /* Back */
      this.beginShape(PConstants.POLYGON);
      this.normal(0.0f, -1.0f, 0.0f);
      this.vertexImpl(
            wHalf, -hHalf, dHalf,
            1.0f, 0.0f);
      this.vertexImpl(
            -wHalf, -hHalf, -dHalf,
            0.0f, 1.0f);
      this.vertexImpl(
            wHalf, -hHalf, -dHalf,
            1.0f, 1.0f);
      this.endShape(PConstants.CLOSE);

      this.beginShape(PConstants.POLYGON);
      this.normal(0.0f, -1.0f, 0.0f);
      this.vertexImpl(
            wHalf, -hHalf, dHalf,
            1.0f, 0.0f);
      this.vertexImpl(
            -wHalf, -hHalf, dHalf,
            0.0f, 0.0f);
      this.vertexImpl(
            -wHalf, -hHalf, -dHalf,
            0.0f, 1.0f);
      this.endShape(PConstants.CLOSE);

      /* Up */
      this.beginShape(PConstants.POLYGON);
      this.normal(0.0f, 0.0f, 1.0f);
      this.vertexImpl(
            -wHalf, hHalf, dHalf,
            1.0f, 0.0f);
      this.vertexImpl(
            wHalf, -hHalf, dHalf,
            0.0f, 1.0f);
      this.vertexImpl(
            wHalf, hHalf, dHalf,
            1.0f, 1.0f);
      this.endShape(PConstants.CLOSE);

      this.beginShape(PConstants.POLYGON);
      this.normal(0.0f, 0.0f, 1.0f);
      this.vertexImpl(
            -wHalf, hHalf, dHalf,
            1.0f, 0.0f);
      this.vertexImpl(
            -wHalf, -hHalf, dHalf,
            0.0f, 0.0f);
      this.vertexImpl(
            wHalf, -hHalf, dHalf,
            0.0f, 1.0f);
      this.endShape(PConstants.CLOSE);

      /* Down */
      this.beginShape(PConstants.POLYGON);
      this.normal(0.0f, 0.0f, -1.0f);
      this.vertexImpl(
            wHalf, hHalf, -dHalf,
            1.0f, 0.0f);
      this.vertexImpl(
            -wHalf, -hHalf, -dHalf,
            0.0f, 1.0f);
      this.vertexImpl(
            -wHalf, hHalf, -dHalf,
            1.0f, 1.0f);
      this.endShape(PConstants.CLOSE);

      this.beginShape(PConstants.POLYGON);
      this.normal(0.0f, 0.0f, -1.0f);
      this.vertexImpl(
            wHalf, hHalf, -dHalf,
            1.0f, 0.0f);
      this.vertexImpl(
            wHalf, -hHalf, -dHalf,
            0.0f, 0.0f);
      this.vertexImpl(
            -wHalf, -hHalf, -dHalf,
            0.0f, 1.0f);
      this.endShape(PConstants.CLOSE);
   }

   /**
    * Creates a camera that looks at a default location and a
    * vantage point based on the renderer's height.
    */
   @Override
   public void camera () {

      /*
       * CAUTION: Never use defCameraXXX values. They are not
       * actual constants and may not have been initialized.
       */

      float x = Yup3.DEFAULT_LOC_X;
      float y = Yup3.DEFAULT_LOC_Y;
      float z = Yup3.DEFAULT_LOC_Z;
      if (this.width > 128 && this.height > 128) {
         final float distance = this.height * IUp.DEFAULT_CAM_DIST_FAC;
         x = distance;
         y = distance;
         z = -distance;
      }

      this.camera(
            x, y, z,
            Up3.DEFAULT_TARGET_X,
            Up3.DEFAULT_TARGET_Y,
            Up3.DEFAULT_TARGET_Z);
   }

   /**
    * Looks at the center point from the eye point, using the
    * default reference up axis.
    *
    * @param eyeX
    *           camera location x
    * @param eyeY
    *           camera location y
    * @param eyeZ
    *           camera location z
    * @param centerX
    *           target location x
    * @param centerY
    *           target location y
    * @param centerZ
    *           target location z
    * @see Yup3#DEFAULT_REF_X
    * @see Yup3#DEFAULT_REF_Y
    * @see Yup3#DEFAULT_REF_Z
    */
   public void camera (
         final float eyeX, final float eyeY, final float eyeZ,
         final float centerX, final float centerY, final float centerZ ) {

      /*
       * CAUTION: Never use defCameraXXX values. They are not
       * actual constants and may not have been initialized.
       */

      this.camera(
            eyeX, eyeY, eyeZ,
            centerX, centerY, centerZ,

            Yup3.DEFAULT_REF_X,
            Yup3.DEFAULT_REF_Y,
            Yup3.DEFAULT_REF_Z);
   }

   /**
    * Looks at the center point from the eye point, using the
    * world up axis as a reference.
    *
    * @param eyeX
    *           camera location x
    * @param eyeY
    *           camera location y
    * @param eyeZ
    *           camera location z
    * @param centerX
    *           target location x
    * @param centerY
    *           target location y
    * @param centerZ
    *           target location z
    * @param upX
    *           world up axis x
    * @param upY
    *           world up axis y
    * @param upZ
    *           world up axis z
    */
   @Override
   public void camera (
         final float eyeX,
         final float eyeY,
         final float eyeZ,

         final float centerX,
         final float centerY,
         final float centerZ,

         final float upX,
         final float upY,
         final float upZ ) {

      // TODO: Is it problematic when the camera's look direction
      // is co-linear with the world up direction?

      this.refUp.set(upX, upY, upZ);
      if (Vec3.magSq(this.refUp) < PConstants.EPSILON) {

         this.refUp.set(
               Yup3.DEFAULT_REF_X,
               Yup3.DEFAULT_REF_Y,
               Yup3.DEFAULT_REF_Z);
         return;
      }

      this.lookDir.set(
            eyeX - centerX,
            eyeY - centerY,
            eyeZ - centerZ);

      final float lookDist = Vec3.magSq(this.lookDir);
      if (lookDist < PConstants.EPSILON) {
         this.lookDir.set(0.0f, 0.0f, -1.0f);
         return;
      }

      this.cameraX = eyeX;
      this.cameraY = eyeY;
      this.cameraZ = eyeZ;

      this.lookTarget.set(centerX, centerY, centerZ);

      this.eyeDist = PApplet.sqrt(lookDist);

      /* Create three axes. Handedness will change by renderer. */
      Vec3.normalize(this.lookDir, this.k);
      Vec3.crossNorm(this.k, this.refUp, this.i);
      Vec3.crossNorm(this.i, this.k, this.j);
      // Vec3.crossNorm(this.refUp, this.k, this.i);
      // Vec3.crossNorm(this.k, this.i, this.j);

      /* Set matrix to axes by row. */
      this.modelview.set(
            this.i.x, this.i.y, this.i.z, 0.0f,
            this.j.x, this.j.y, this.j.z, 0.0f,
            this.k.x, this.k.y, this.k.z, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f);

      /* Translate by negative location. */
      this.modelview.translate(
            -this.cameraX,
            -this.cameraY,
            -this.cameraZ);

      /* Update renderer matrices. */
      IUp.invert(this.modelview, this.modelviewInv);
      this.camera.set(this.modelview);
      this.cameraInv.set(this.modelviewInv);
      this.updateProjmodelview();
   }

   /**
    * Looks at the center point from the eye point, using the
    * default reference up axis.
    *
    * @param eye
    *           the camera's location
    * @param center
    *           the point to look at
    * @see Yup3#DEFAULT_REF_X
    * @see Yup3#DEFAULT_REF_Y
    * @see Yup3#DEFAULT_REF_Z
    */
   public void camera ( final Vec3 eye, final Vec3 center ) {

      this.camera(
            eye.x, eye.y, eye.z,
            center.x, center.y, center.z,

            Yup3.DEFAULT_REF_X,
            Yup3.DEFAULT_REF_Y,
            Yup3.DEFAULT_REF_Z);
   }

   /**
    * Enable lighting and use default lights, typically an
    * ambient light and a directional light.
    *
    * @see PGraphicsOpenGL#lightFalloff(float, float, float)
    * @see PGraphicsOpenGL#lightSpecular(float, float, float)
    * @see PGraphicsOpenGL#ambientLight(float, float, float)
    * @see PGraphicsOpenGL#directionalLight(float, float,
    *      float, float, float, float)
    */
   @Override
   public void lights () {

      this.enableLighting();

      this.lightCount = 0;

      final int colorModeSaved = this.colorMode;
      this.colorMode = PConstants.RGB;

      this.lightFalloff(1.0f, 0.0f, 0.0f);
      this.lightSpecular(0.0f, 0.0f, 0.0f);

      this.ambientLight(
            this.colorModeX * IUpOgl.DEFAULT_AMB_R,
            this.colorModeY * IUpOgl.DEFAULT_AMB_G,
            this.colorModeZ * IUpOgl.DEFAULT_AMB_B);

      this.directionalLight(
            this.colorModeX * IUpOgl.DEFAULT_LIGHT_R,
            this.colorModeY * IUpOgl.DEFAULT_LIGHT_G,
            this.colorModeZ * IUpOgl.DEFAULT_LIGHT_B,

            Yup3.DEFAULT_LIGHT_X,
            Yup3.DEFAULT_LIGHT_Y,
            Yup3.DEFAULT_LIGHT_Z);

      this.colorMode = colorModeSaved;
   }

   /**
    * Set size is the last function called by size,
    * createGraphics, makeGraphics, etc. when initializing the
    * graphics renderer. Therefore, any additional values that
    * need initialization can be attempted here.
    */
   @Override
   public void setSize ( final int iwidth, final int iheight ) {

      super.setSize(iwidth, iheight);
      this.ortho();
      this.camera();
   }

   @Override
   public void sphere ( final float r ) {

      if (this.sphereDetailU < 3 || this.sphereDetailV < 2) {
         this.sphereDetail(30);
      }

      this.edge(false);

      this.beginShape(PConstants.TRIANGLE_STRIP);
      for (int i = 0; i < this.sphereDetailU; i++) {
         this.normal(0.0f, -1.0f, 0.0f);
         this.vertexImpl(
               0.0f, -r, 0.0f,
               0.0f, 0.0f);
         this.normal(
               this.sphereX[i],
               this.sphereY[i],
               this.sphereZ[i]);
         this.vertexImpl(
               r * this.sphereX[i],
               r * this.sphereY[i],
               r * this.sphereZ[i],
               0.0f, 0.0f);
      }
      this.normal(0.0f, -1.0f, 0.0f);
      this.vertexImpl(
            0.0f, -r, 0.0f,
            0.0f, 0.0f);
      this.normal(
            this.sphereX[0],
            this.sphereY[0],
            this.sphereZ[0]);
      this.vertexImpl(
            r * this.sphereX[0],
            r * this.sphereY[0],
            r * this.sphereZ[0],
            0.0f, 0.0f);
      this.endShape();

      int v1 = 0;
      int v11 = 0;
      int v2 = 0;
      int voff = 0;
      for (int i = 2; i < this.sphereDetailV; i++) {
         v1 = v11 = voff;
         voff += this.sphereDetailU;
         v2 = voff;
         this.beginShape(PConstants.TRIANGLE_STRIP);
         for (int j = 0; j < this.sphereDetailU; j++) {
            this.normal(
                  this.sphereX[v1],
                  this.sphereY[v1],
                  this.sphereZ[v1]);
            this.vertexImpl(
                  r * this.sphereX[v1],
                  r * this.sphereY[v1],
                  r * this.sphereZ[v1],
                  0.0f, 0.0f);

            this.normal(
                  this.sphereX[v2],
                  this.sphereY[v2],
                  this.sphereZ[v2]);
            this.vertexImpl(
                  r * this.sphereX[v2],
                  r * this.sphereY[v2],
                  r * this.sphereZ[v2],
                  0.0f, 0.0f);

            v1++;
            v2++;
         }

         v1 = v11;
         v2 = voff;
         this.normal(
               this.sphereX[v1],
               this.sphereY[v1],
               this.sphereZ[v1]);
         this.vertexImpl(
               r * this.sphereX[v1],
               r * this.sphereY[v1],
               r * this.sphereZ[v1],
               0.0f, 0.0f);

         this.normal(
               this.sphereX[v2],
               this.sphereY[v2],
               this.sphereZ[v2]);
         this.vertexImpl(
               r * this.sphereX[v2],
               r * this.sphereY[v2],
               r * this.sphereZ[v2],
               0.0f, 0.0f);
         this.endShape();
      }

      this.beginShape(PConstants.TRIANGLE_STRIP);
      for (int i = 0; i < this.sphereDetailU; i++) {
         v2 = voff + i;

         this.normal(
               this.sphereX[v2],
               this.sphereY[v2],
               this.sphereZ[v2]);
         this.vertexImpl(
               r * this.sphereX[v2],
               r * this.sphereY[v2],
               r * this.sphereZ[v2],
               0.0f, 0.0f);

         this.normal(0.0f, 1.0f, 0.0f);
         this.vertexImpl(
               0.0f, r, 0.0f,
               0.0f, 0.0f);
      }

      this.normal(
            this.sphereX[voff],
            this.sphereY[voff],
            this.sphereZ[voff]);
      this.vertexImpl(
            r * this.sphereX[voff],
            r * this.sphereY[voff],
            r * this.sphereZ[voff],
            0.0f, 0.0f);

      this.normal(0.0f, 1.0f, 0.0f);
      this.vertexImpl(0.0f, r, 0.0f,
            0.0f, 0.0f);

      this.endShape();

      this.edge(true);
   }

   @Override
   public void sphereDetail ( int longitudes, int latitudes ) {

      if (longitudes < 3) {
         longitudes = 3;
      }

      if (latitudes < 2) {
         latitudes = 2;
      }

      if (longitudes == this.sphereDetailU &&
            latitudes == this.sphereDetailV) {
         return;
      }

      final float delta = (float) PGraphics.SINCOS_LENGTH / longitudes;
      final float[] cx = new float[longitudes];
      final float[] cz = new float[longitudes];
      for (int i = 0; i < longitudes; ++i) {
         final int index = (int) (i * delta) % PGraphics.SINCOS_LENGTH;
         cx[i] = PGraphics.cosLUT[index];
         cz[i] = PGraphics.sinLUT[index];
      }

      final int vertCount = longitudes * (latitudes - 1) + 2;
      int currVert = 0;

      this.sphereX = new float[vertCount];
      this.sphereY = new float[vertCount];
      this.sphereZ = new float[vertCount];

      final float angleStep = PGraphics.SINCOS_LENGTH * 0.5f / latitudes;
      float angle = angleStep;

      for (int i = 1; i < latitudes; ++i) {
         final int index = (int) angle % PGraphics.SINCOS_LENGTH;
         final float curradius = PGraphics.sinLUT[index];
         final float currY = PGraphics.cosLUT[index];
         for (int j = 0; j < longitudes; ++j) {
            this.sphereX[currVert] = cx[j] * curradius;
            this.sphereY[currVert] = -currY;
            this.sphereZ[currVert++] = cz[j] * curradius;
         }
         angle += angleStep;
      }

      this.sphereDetailU = longitudes;
      this.sphereDetailV = latitudes;
   }
}

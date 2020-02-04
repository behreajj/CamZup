package camzup.pfriendly;

import camzup.core.Utils;
import camzup.core.Vec3;
import processing.core.PApplet;
import processing.core.PConstants;
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
   public static final float DEFAULT_LOC_X = 623.53827f;

   /**
    * Default camera location y component.
    */
   public static final float DEFAULT_LOC_Y = 623.53827f;

   /**
    * Default camera location z component.
    */
   public static final float DEFAULT_LOC_Z = -623.53827f;

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
         final int width,
         final int height,
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
         final float eyeX,
         final float eyeY,
         final float eyeZ,
         final float centerX,
         final float centerY,
         final float centerZ ) {

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
    * @param xEye
    *           camera location x
    * @param yEye
    *           camera location y
    * @param zEye
    *           camera location z
    * @param xCenter
    *           target location x
    * @param yCenter
    *           target location y
    * @param zCenter
    *           target location z
    * @param xUp
    *           world up axis x
    * @param yUp
    *           world up axis y
    * @param zUp
    *           world up axis z
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

      // TODO: Is it problematic when the camera's look direction
      // is co-linear with the world up direction?

      this.refUp.set(xUp, yUp, zUp);
      if (Vec3.magSq(this.refUp) < PConstants.EPSILON) {

         this.refUp.set(
               Yup3.DEFAULT_REF_X,
               Yup3.DEFAULT_REF_Y,
               Yup3.DEFAULT_REF_Z);
         return; // Why return?
      }

      this.lookDir.set(
            xEye - xCenter,
            yEye - yCenter,
            zEye - zCenter);

      final float lookDist = Vec3.magSq(this.lookDir);
      if (lookDist < PConstants.EPSILON) {
         this.lookDir.set(0.0f, 0.0f, -1.0f);
         return; // Why return?
      }

      this.cameraX = xEye;
      this.cameraY = yEye;
      this.cameraZ = zEye;

      this.lookTarget.set(xCenter, yCenter, zCenter);

      this.eyeDist = Utils.sqrt(lookDist);

      /* Create three axes. Handedness will change by renderer. */
      Vec3.normalize(this.lookDir, this.k);
      Vec3.crossNorm(this.k, this.refUp, this.i);
      Vec3.crossNorm(this.i, this.k, this.j);

      final float m00 = this.i.x;
      final float m01 = this.i.y;
      final float m02 = this.i.z;

      final float m10 = this.j.x;
      final float m11 = this.j.y;
      final float m12 = this.j.z;

      final float m20 = this.k.x;
      final float m21 = this.k.y;
      final float m22 = this.k.z;

      /*
       * Set matrix to axes by row. Translate by a negative
       * location.
       */
      this.camera.set(
            m00, m01, m02, -xEye * m00 - yEye * m01 - zEye * m02,
            m10, m11, m12, -xEye * m10 - yEye * m11 - zEye * m12,
            m20, m21, m22, -xEye * m20 - yEye * m21 - zEye * m22,
            0.0f, 0.0f, 0.0f, 1.0f);
      /*
       * this.camera.translate( -this.cameraX, -this.cameraY,
       * -this.cameraZ);
       */

      /* Set inverse. */
      this.cameraInv.set(
            m00, m10, m20, xEye,
            m01, m11, m21, yEye,
            m02, m12, m22, zEye,
            0.0f, 0.0f, 0.0f, 1.0f);
      /* PMatAux.invert(this.camera, this.cameraInv); */

      /* Update renderer matrices. */
      this.modelview.set(this.camera);
      this.modelviewInv.set(this.cameraInv);
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
   public void setSize (
         final int iwidth,
         final int iheight ) {

      super.setSize(iwidth, iheight);
      this.ortho();
      this.camera();
   }

   @Override
   public String toString () {

      return "camzup.pfriendly.Yup3";
   }

   // /**
   // * Controls the detail used to render a sphere by
   // adjusting
   // * the number of vertices of the sphere mesh.
   // *
   // * @param longitudes
   // * the number of longitudes
   // * @param latitudes
   // * the number of latitudes
   // * @see IUp3#DEFAULT_SPHERE_DETAIL
   // */
   // @Override
   // public void sphereDetail (
   // final int longitudes,
   // final int latitudes ) {
   //
   // final int lon = longitudes < 3 ? 3 : longitudes;
   // final int lat = latitudes < 3 ? 3 : latitudes;
   //
   // if (lon == this.sphereDetailU &&
   // lat == this.sphereDetailV) {
   // return;
   // }
   //
   // final float delta = (float) PGraphics.SINCOS_LENGTH /
   // lon;
   // final float[] cx = new float[lon];
   // final float[] cz = new float[lon];
   //
   // for (int i = 0; i < lon; ++i) {
   //
   // final int index = (int) (i * delta) %
   // PGraphics.SINCOS_LENGTH;
   // cx[i] = PGraphics.cosLUT[index];
   // cz[i] = PGraphics.sinLUT[index];
   //
   // }
   //
   // final int vertCount = lon * (lat - 1) + 2;
   // int currVert = 0;
   //
   // this.sphereX = new float[vertCount];
   // this.sphereY = new float[vertCount];
   // this.sphereZ = new float[vertCount];
   //
   // final float angleStep = PGraphics.SINCOS_LENGTH * 0.5f /
   // lat;
   // float angle = angleStep;
   //
   // for (int i = 1; i < lat; ++i) {
   //
   // final int index = (int) angle % PGraphics.SINCOS_LENGTH;
   // final float curradius = PGraphics.sinLUT[index];
   // final float currY = PGraphics.cosLUT[index];
   //
   // for (int j = 0; j < lon; ++j) {
   //
   // this.sphereX[currVert] = cx[j] * curradius;
   // this.sphereY[currVert] = -currY;
   // this.sphereZ[currVert++] = cz[j] * curradius;
   // }
   //
   // angle += angleStep;
   // }
   //
   // this.sphereDetailU = lon;
   // this.sphereDetailV = lat;
   // }
}

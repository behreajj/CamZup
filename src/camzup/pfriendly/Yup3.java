package camzup.pfriendly;

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
}

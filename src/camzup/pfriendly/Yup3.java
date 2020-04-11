package camzup.pfriendly;

import camzup.core.Experimental;
import camzup.core.Handedness;
import camzup.core.Vec3;

import processing.core.PApplet;
import processing.core.PConstants;

import processing.opengl.PGraphicsOpenGL;

/**
 * A 3D renderer where (0.0, 1.0, 0.0) is the default world up axis.
 */
public class Yup3 extends Up3 {

   /**
    * The default constructor.
    */
   public Yup3 ( ) { super(); }

   /**
    * A constructor for manually initializing the renderer.
    *
    * @param width     renderer width
    * @param height    renderer height
    * @param parent    parent applet
    * @param path      applet path
    * @param isPrimary is the renderer primary
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
    * Places the camera on the negative y axis, such that it is looking up
    * toward the world origin.<br>
    * <br>
    * Because an exact position on the z axis would interfere with the camera
    * look at function based on the reference or world up, a small value is
    * added to the camera's y position.
    */
   @Override
   public void camBottom ( ) {

      final float y = this.eyeDist < 128 ? -Yup3.DEFAULT_LOC_Y
         : this.eyeDist;
      final float z = -y * IUp3.POLARITY_OFFSET;

      this.camera(0.0f, y, z, 0.0f, 0.0f, 0.0f, Yup3.DEFAULT_REF_X,
         Yup3.DEFAULT_REF_Y, Yup3.DEFAULT_REF_Z);
   }

   /**
    * Places the camera on the negative x axis, such that it is looking East
    * toward the world origin.
    */
   @Override
   public void camEast ( ) {

      final float x = this.eyeDist < 128 ? -Yup3.DEFAULT_LOC_X
         : -this.eyeDist;

      this.camera(x, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, Yup3.DEFAULT_REF_X,
         Yup3.DEFAULT_REF_Y, Yup3.DEFAULT_REF_Z);
   }

   /**
    * Creates a camera that looks at a default location and a vantage point
    * based on the renderer's height.
    */
   @Override
   public void camera ( ) {

      /*
       * Never use defCameraXXX values. They are not actual constants and may
       * not have been initialized.
       */
      this.camera(this.cameraX, this.cameraY, this.cameraZ, this.lookTarget.x,
         this.lookTarget.y, this.lookTarget.z);
   }

   /**
    * Looks at the center point from the eye point, using the default reference
    * up axis.
    *
    * @param xEye    camera location x
    * @param yEye    camera location y
    * @param zEye    camera location z
    * @param xCenter target location x
    * @param yCenter target location y
    * @param zCenter target location z
    *
    * @see Yup3#DEFAULT_REF_X
    * @see Yup3#DEFAULT_REF_Y
    * @see Yup3#DEFAULT_REF_Z
    */
   @Override
   public void camera (
      final float xEye,
      final float yEye,
      final float zEye,
      final float xCenter,
      final float yCenter,
      final float zCenter ) {

      /*
       * Never use defCameraXXX values. They are not actual constants and may
       * not have been initialized.
       */

      this.camera(xEye, yEye, zEye, xCenter, yCenter, zCenter, this.refUp.x,
         this.refUp.y, this.refUp.z);
   }

   /**
    * Looks at the center point from the eye point, using the world up axis as a
    * reference.
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

      this.refUp.set(xUp, yUp, zUp);

      this.lookDir.set(xEye - this.lookTarget.x, yEye - this.lookTarget.y,
         zEye - this.lookTarget.z);

      Vec3.normalize(this.lookDir, this.k);

      final float dotp = Vec3.dot(this.k, this.refUp);
      final float tol = 1.0f - IUp3.POLARITY_TOLERANCE;
      if ( dotp < -tol || dotp > tol ) { return; }

      Vec3.crossNorm(this.k, this.refUp, this.i);
      Vec3.crossNorm(this.i, this.k, this.j);

      this.cameraX = xEye;
      this.cameraY = yEye;
      this.cameraZ = zEye;
      this.eyeDist = Vec3.mag(this.lookDir);
      this.lookTarget.set(xCenter, yCenter, zCenter);

      this.updateCameraInv();
   }

   /**
    * Looks at the center point from the eye point, using the default reference
    * up axis.
    *
    * @param eye    the camera's location
    * @param center the point to look at
    *
    * @see Yup3#DEFAULT_REF_X
    * @see Yup3#DEFAULT_REF_Y
    * @see Yup3#DEFAULT_REF_Z
    */
   @Override
   public void camera (
      final Vec3 eye,
      final Vec3 center ) {

      this.camera(eye.x, eye.y, eye.z, center.x, center.y, center.z);
   }

   /**
    * Sets the camera to the Processing default, where the origin is in the top
    * left corner of the sketch and the y axis points downward.
    */
   @Experimental
   public void camFlipped ( ) {

      final float wHalf = this.width * 0.5f;
      final float hHalf = this.height * 0.5f;
      final float z = this.height < 128
         ? Yup3.DEFAULT_LOC_Y
         : this.height * IUp.DEFAULT_CAM_DIST_FAC;
      this.camera(wHalf, hHalf, z, wHalf, hHalf, 0.0f, 0.0f, -1.0f, 0.0f);
   }

   /**
    * Places the camera on the negative z axis, such that it is looking North
    * toward the world origin.
    */
   @Override
   public void camNorth ( ) {

      final float z = this.eyeDist < 128 ? Yup3.DEFAULT_LOC_Z
         : -this.eyeDist;

      this.camera(0.0f, 0.0f, z, 0.0f, 0.0f, 0.0f, Yup3.DEFAULT_REF_X,
         Yup3.DEFAULT_REF_Y, Yup3.DEFAULT_REF_Z);
   }

   /**
    * Places the camera on the positive z axis, such that it is looking South
    * toward the world origin.
    */
   @Override
   public void camSouth ( ) {

      final float z = this.eyeDist < 128 ? -Yup3.DEFAULT_LOC_Z
         : this.eyeDist;

      this.camera(0.0f, 0.0f, z, 0.0f, 0.0f, 0.0f, Yup3.DEFAULT_REF_X,
         Yup3.DEFAULT_REF_Y, Yup3.DEFAULT_REF_Z);
   }

   /**
    * Places the camera on the positive y axis, such that it is looking down
    * toward the world origin.<br>
    * <br>
    * Because an exact position on the z axis would interfere with the camera
    * look at function based on the reference or world up, a small value is
    * added to the camera's y position.
    */
   @Override
   public void camTop ( ) {

      final float y = this.eyeDist < 128 ? Yup3.DEFAULT_LOC_Y
         : this.eyeDist;
      final float z = -y * IUp3.POLARITY_OFFSET;
      this.camera(0.0f, y, z, 0.0f, 0.0f, 0.0f, Yup3.DEFAULT_REF_X,
         Yup3.DEFAULT_REF_Y, Yup3.DEFAULT_REF_Z);
   }

   /**
    * Places the camera on the positive x axis, such that it is looking West
    * toward the world origin.
    */
   @Override
   public void camWest ( ) {

      final float x = this.eyeDist < 128 ? Yup3.DEFAULT_LOC_X
         : this.eyeDist;

      this.camera(x, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, Yup3.DEFAULT_REF_X,
         Yup3.DEFAULT_REF_Y, Yup3.DEFAULT_REF_Z);
   }

   /**
    * Sets default camera and calls the camera function.
    *
    * @see PGraphicsOpenGL#defCameraX
    * @see PGraphicsOpenGL#defCameraY
    * @see PGraphicsOpenGL#defCameraZ
    */
   @Override
   public void defaultCamera ( ) {

      this.cameraX = this.defCameraX = Yup3.DEFAULT_LOC_X;
      this.cameraY = this.defCameraY = Yup3.DEFAULT_LOC_Y;
      this.cameraZ = this.defCameraZ = Yup3.DEFAULT_LOC_Z;

      this.refUp.set(Yup3.DEFAULT_REF_X, Yup3.DEFAULT_REF_Y,
         Yup3.DEFAULT_REF_Z);

      this.lookTarget.set(Up3.DEFAULT_TARGET_X, Up3.DEFAULT_TARGET_Y,
         Up3.DEFAULT_TARGET_Z);

      this.lookDir.set(Yup3.DEFAULT_LOC_X - Up3.DEFAULT_TARGET_X,
         Yup3.DEFAULT_LOC_Y - Up3.DEFAULT_TARGET_Y,
         Yup3.DEFAULT_LOC_Z - Up3.DEFAULT_TARGET_Z);

      this.eyeDist = Vec3.mag(this.lookDir);

      this.camera();
   }

   /**
    * Sets the renderer's default styling.
    */
   @Override
   public void defaultSettings ( ) {

      super.defaultSettings();

      this.cameraX = this.defCameraX = Yup3.DEFAULT_LOC_X;
      this.cameraY = this.defCameraY = Yup3.DEFAULT_LOC_Y;
      this.cameraZ = this.defCameraZ = Yup3.DEFAULT_LOC_Z;

      this.refUp.set(Yup3.DEFAULT_REF_X, Yup3.DEFAULT_REF_Y,
         Yup3.DEFAULT_REF_Z);

      this.lookTarget.set(Up3.DEFAULT_TARGET_X, Up3.DEFAULT_TARGET_Y,
         Up3.DEFAULT_TARGET_Z);

      this.lookDir.set(Yup3.DEFAULT_LOC_X - Up3.DEFAULT_TARGET_X,
         Yup3.DEFAULT_LOC_Y - Up3.DEFAULT_TARGET_Y,
         Yup3.DEFAULT_LOC_Z - Up3.DEFAULT_TARGET_Z);

      this.eyeDist = Vec3.mag(this.lookDir);

      Vec3.right(this.i);
      Vec3.up(this.j);
      Vec3.forward(this.k);
   }

   /**
    * Returns the handedness of the renderer.
    */
   @Override
   public Handedness handedness ( ) { return Handedness.LEFT; }

   /**
    * Enable lighting and use default lights, typically an ambient light and a
    * directional light.
    *
    * @see PGraphicsOpenGL#lightFalloff(float, float, float)
    * @see PGraphicsOpenGL#lightSpecular(float, float, float)
    * @see PGraphicsOpenGL#ambientLight(float, float, float)
    * @see PGraphicsOpenGL#directionalLight(float, float, float, float, float,
    *      float)
    */
   @Override
   public void lights ( ) {

      this.enableLighting();

      this.lightCount = 0;

      final int colorModeSaved = this.colorMode;
      this.colorMode = PConstants.RGB;

      this.lightFalloff(1.0f, 0.0f, 0.0f);
      this.lightSpecular(0.0f, 0.0f, 0.0f);

      this.ambientLight(this.colorModeX * IUpOgl.DEFAULT_AMB_R,
         this.colorModeY * IUpOgl.DEFAULT_AMB_G,
         this.colorModeZ * IUpOgl.DEFAULT_AMB_B);

      this.directionalLight(this.colorModeX * IUpOgl.DEFAULT_LIGHT_R,
         this.colorModeY * IUpOgl.DEFAULT_LIGHT_G,
         this.colorModeZ * IUpOgl.DEFAULT_LIGHT_B, Yup3.DEFAULT_LIGHT_X,
         Yup3.DEFAULT_LIGHT_Y, Yup3.DEFAULT_LIGHT_Z);

      this.colorMode = colorModeSaved;
   }

   /**
    * Set size is the last function called by size, createGraphics,
    * makeGraphics, etc. when initializing the graphics renderer. Therefore, any
    * additional values that need initialization can be attempted here.
    */
   @Override
   public void setSize (
      final int iwidth,
      final int iheight ) {

      super.setSize(iwidth, iheight);
      this.ortho();
      this.defaultCamera();
   }

   /**
    * Returns the string representation of this renderer.
    *
    * @return the string
    */
   @Override
   public String toString ( ) { return Yup3.PATH_STR; }

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
    * The path string for this renderer.
    */
   public static final String PATH_STR = "camzup.pfriendly.Yup3";

}

package camzup.pfriendly;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.opengl.PGraphicsOpenGL;

import camzup.core.IUtils;
import camzup.core.Utils;
import camzup.core.Vec3;

/**
 * A 3D renderer where (0.0, 0.0, 1.0) is the default world up axis.
 */
public class Zup3 extends Up3 {

  /**
   * Default lighting directional light axis x component.
   */
  public static final float DEFAULT_LIGHT_X = -0.18490006f;

  /**
   * Default lighting directional light axis y component.
   */
  public static final float DEFAULT_LIGHT_Y = -0.6471502f;

  /**
   * Default lighting directional light axis z component.
   */
  public static final float DEFAULT_LIGHT_Z = -0.73960024f;

  /**
   * Default camera location x component.
   */
  public static final float DEFAULT_LOC_X = 623.53827f;

  /**
   * Default camera location y component.
   */
  public static final float DEFAULT_LOC_Y = -623.53827f;

  /**
   * Default camera location z component.
   */
  public static final float DEFAULT_LOC_Z = 623.53827f;

  /**
   * Default world up x component.
   */
  public static final float DEFAULT_REF_X = 0.0f;

  /**
   * Default world up y component.
   */
  public static final float DEFAULT_REF_Y = 0.0f;

  /**
   * Default world up z component.
   */
  public static final float DEFAULT_REF_Z = 1.0f;

  /**
   * The default constructor.
   */
  public Zup3 ( ) { super(); }

  /**
   * A constructor for manually initializing the renderer.
   *
   * @param width     renderer width
   * @param height    renderer height
   * @param parent    parent applet
   * @param path      applet path
   * @param isPrimary is the renderer primary
   */
  public Zup3 (
      final int width,
      final int height,
      final PApplet parent,
      final String path,
      final boolean isPrimary ) {

    super(width, height, parent, path, isPrimary);
  }

  /**
   * Sets default camera location and calls the camera function.
   *
   * @see PGraphicsOpenGL#defCameraX
   * @see PGraphicsOpenGL#defCameraY
   * @see PGraphicsOpenGL#defCameraZ
   */
  @Override
  protected void defaultCamera ( ) {

    this.defCameraX = Zup3.DEFAULT_LOC_X;
    this.defCameraY = Zup3.DEFAULT_LOC_Y;
    this.defCameraZ = Zup3.DEFAULT_LOC_Z;

    this.camera();
  }

  /**
   * Sets lighting normals. Overrides the default by swapping and
   * negating to accommodate Z-up camera.
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
      this.lightNormal[num3] = mInv * -nx;
      this.lightNormal[num3 + 1] = mInv * nz;
      this.lightNormal[num3 + 2] = mInv * -ny;
    } else {
      this.lightNormal[num3] = 0.0f;
      this.lightNormal[num3 + 1] = 0.0f;
      this.lightNormal[num3 + 2] = 0.0f;
    }
  }

  /**
   * Creates a camera that looks at a default location and a vantage
   * point based on the renderer's height.
   */
  @Override
  public void camera ( ) {

    /*
     * CAUTION: Never use defCameraXXX values. They are not actual
     * constants and may not have been initialized.
     */

    float x = Zup3.DEFAULT_LOC_X;
    float y = Zup3.DEFAULT_LOC_Y;
    float z = Zup3.DEFAULT_LOC_Z;
    if ( this.width > 128 && this.height > 128 ) {
      final float distance = this.height * IUp.DEFAULT_CAM_DIST_FAC;
      x = distance;
      y = -distance;
      z = distance;
    }

    this.camera(
        x, y, z,
        Up3.DEFAULT_TARGET_X,
        Up3.DEFAULT_TARGET_Y,
        Up3.DEFAULT_TARGET_Z);
  }

  /**
   * Looks at the center point from the eye point, using the default
   * reference up axis.
   *
   * @param xEye    camera location x
   * @param yEye    camera location y
   * @param zEye    camera location z
   * @param xCenter target location x
   * @param yCenter target location y
   * @param zCenter target location z
   * @see Zup3#DEFAULT_REF_X
   * @see Zup3#DEFAULT_REF_Y
   * @see Zup3#DEFAULT_REF_Z
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
     * CAUTION: Never use defCameraXXX values. They are not actual
     * constants and may not have been initialized.
     */

    this.camera(
        xEye, yEye, zEye,
        xCenter, yCenter, zCenter,
        Zup3.DEFAULT_REF_X,
        Zup3.DEFAULT_REF_Y,
        Zup3.DEFAULT_REF_Z);
  }

  /**
   * Looks at the center point from the eye point, using the world up
   * axis as a reference.
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
    if ( Vec3.magSq(this.refUp) < PConstants.EPSILON ) {
      this.refUp.set(
          Yup3.DEFAULT_REF_X,
          Yup3.DEFAULT_REF_Y,
          Yup3.DEFAULT_REF_Z);
      return;
    }

    this.lookDir.set(
        xEye - xCenter,
        yEye - yCenter,
        zEye - zCenter);

    final float lookDist = Vec3.magSq(this.lookDir);
    if ( lookDist < IUtils.DEFAULT_EPSILON ) {
      this.lookDir.set(0.0f, 0.0f, -1.0f);
      return;
    }

    // TEST Is it problematic when the camera's look direction
    // is co-linear with the world up direction?
    // if ( Vec3.areParallel(lookDir, refUp) ) {
    // this.lookDir.set(
    // refUp.x + Utils.EPSILON,
    // refUp.y - Utils.EPSILON,
    // refUp.z - Utils.EPSILON);
    // return;
    // }

    this.cameraX = xEye;
    this.cameraY = yEye;
    this.cameraZ = zEye;

    this.lookTarget.set(xCenter, yCenter, zCenter);

    this.eyeDist = Utils.sqrt(lookDist);

    /* Create three axes through cross normalize. */
    Vec3.normalize(this.lookDir, this.k);
    Vec3.crossNorm(this.refUp, this.k, this.i);
    Vec3.crossNorm(this.k, this.i, this.j);
    // Vec3.crossNorm(this.k, this.refUp, this.i);
    // Vec3.crossNorm(this.i, this.k, this.j);

    this.updateCamera();
  }

  /**
   * Looks at the center point from the eye point, using the default
   * reference up axis.
   *
   * @param eye    the camera's location
   * @param center the point to look at
   * @see Zup3#DEFAULT_REF_X
   * @see Zup3#DEFAULT_REF_Y
   * @see Zup3#DEFAULT_REF_Z
   */
  @Override
  public void camera ( final Vec3 eye, final Vec3 center ) {

    this.camera(
        eye.x, eye.y, eye.z,
        center.x, center.y, center.z,
        Zup3.DEFAULT_REF_X,
        Zup3.DEFAULT_REF_Y,
        Zup3.DEFAULT_REF_Z);
  }

  /**
   * Enable lighting and use default lights, typically an ambient light
   * and a directional light.
   *
   * @see PGraphicsOpenGL#lightFalloff(float, float, float)
   * @see PGraphicsOpenGL#lightSpecular(float, float, float)
   * @see PGraphicsOpenGL#ambientLight(float, float, float)
   * @see PGraphicsOpenGL#directionalLight(float, float, float, float,
   *      float, float)
   */
  @Override
  public void lights ( ) {

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

        Zup3.DEFAULT_LIGHT_X,
        Zup3.DEFAULT_LIGHT_Y,
        Zup3.DEFAULT_LIGHT_Z);

    this.colorMode = colorModeSaved;
  }

  /**
   * Sets the current normal used by the renderer.
   *
   * @param nx the x component
   * @param ny the y component
   * @param nz the z component
   */
  @Override
  public void normal (
      final float nx,
      final float ny,
      final float nz ) {

    this.normalX = nx;
    this.normalY = ny;
    this.normalZ = nz;

    if ( this.shape != 0 ) {
      if ( this.normalMode == PGraphics.NORMAL_MODE_AUTO ) {

        /* One normal per begin/end shape */
        this.normalMode = PGraphics.NORMAL_MODE_SHAPE;
      } else if ( this.normalMode == PGraphics.NORMAL_MODE_SHAPE ) {

        /* a separate normal for each vertex */
        this.normalMode = PGraphics.NORMAL_MODE_VERTEX;
      }
    }
  }

  /**
   * Set size is the last function called by size, createGraphics,
   * makeGraphics, etc. when initializing the graphics renderer.
   * Therefore, any additional values that need initialization can be
   * attempted here.
   *
   * @param iwidth  the width in pixels
   * @param iheight the height in pixels
   */
  @Override
  public void setSize (
      final int iwidth,
      final int iheight ) {

    super.setSize(iwidth, iheight);
    this.ortho();
    this.camera();
  }

  /**
   * Returns the string representation of this renderer.
   *
   * @return the string
   */
  @Override
  public String toString ( ) {

    return "camzup.pfriendly.Zup3";
  }
}

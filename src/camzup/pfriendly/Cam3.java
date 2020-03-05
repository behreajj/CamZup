package camzup.pfriendly;

import camzup.core.Entity3;
import camzup.core.Experimental;
import camzup.core.Transform3;
import camzup.core.Vec3;

/**
 * An entity-based camera class which updates a renderer's camera.
 */
public class Cam3 extends Entity3 {

  /**
   * The 3D renderer.
   */
  public final Up3 renderer;

  /**
   * Constructs a camera with a name, transform and renderer.
   *
   * @param name      the name
   * @param transform the transform
   * @param renderer  the renderer
   */
  public Cam3 (
      final String name,
      final Transform3 transform,
      final Up3 renderer ) {

    super(name, transform);
    this.renderer = renderer;
  }

  /**
   * Constructs a camera with a name and renderer.
   *
   * @param name     the name
   * @param renderer the renderer
   */
  public Cam3 (
      final String name,
      final Up3 renderer ) {

    super(name);
    this.renderer = renderer;
    this.initLoc();
  }

  /**
   * Constructs a camera with a transform and renderer.
   *
   * @param transform the transform
   * @param renderer  the renderer
   */
  public Cam3 (
      final Transform3 transform,
      final Up3 renderer ) {

    super(transform);
    this.renderer = renderer;
  }

  /**
   * Constructs a camera with a renderer.
   *
   * @param renderer the renderer
   */
  public Cam3 ( final Up3 renderer ) {

    super();
    this.renderer = renderer;
    this.initLoc();
  }

  /**
   * Initializes the camera entity's transform to the renderer's camera
   * inverse.
   */
  protected void initLoc ( ) {

    this.transform.setLocX(0.0f);
    this.transform.setLocY(-128.0f);
    this.transform.setLocZ(-128.0f);
  }

  /**
   * Dollies the camera toward or away from the target at which it is
   * looking.
   *
   * @param magnitude the magnitude of movement
   * @return this camera
   */
  @Experimental
  public Cam3 dolly ( final float magnitude ) {

    this.transform.moveByLocal(new Vec3(0.0f, 0.0f, -magnitude));
    return this;
  }

  /**
   * Moves this entity by a vector. Moves in local space by default.
   *
   * @param dir the vector
   * @return this entity
   */
  @Override
  public Cam3 moveBy ( final Vec3 dir ) {

    this.transform.moveByLocal(dir);
    return this;
  }

  // public Cam3 lookAtZup ( final Vec3 target ) {
  //
  // final Vec3 right = this.renderer.i;
  // final Vec3 forward = this.renderer.k;
  // final Vec3 up = this.renderer.j;
  //
  // final Vec3 refUp = this.renderer.refUp;
  // refUp.set(0.0f, 0.0f, 1.0f);
  //
  // final Vec3 lookDir = this.renderer.lookDir;
  //
  // lookDir.set(
  // this.transform.getLocX() - target.x,
  // this.transform.getLocY() - target.y,
  // this.transform.getLocZ() - target.z);
  //
  // // lookDir.set(
  // // target.x - this.transform.getLocX(),
  // // target.y - this.transform.getLocY(),
  // // target.z - this.transform.getLocZ());
  //
  // Vec3.normalize(lookDir, up);
  // Vec3.crossNorm(refUp, up, right);
  // Vec3.crossNorm(up, right, forward);
  //
  // return this;
  // }

  /**
   * Updates the renderer's camera matrices based on the transform. To
   * be called in update.
   *
   * @return this camera entity
   */
  public Cam3 update ( ) {

    this.transform.getAxes(
        this.renderer.i,
        this.renderer.j,
        this.renderer.k);

    this.renderer.cameraX = this.transform.getLocX();
    this.renderer.cameraY = this.transform.getLocY();
    this.renderer.cameraZ = this.transform.getLocZ();

    this.renderer.updateCamera();

    return this;
  }
}

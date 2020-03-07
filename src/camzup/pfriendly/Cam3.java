package camzup.pfriendly;

import processing.opengl.PGraphicsOpenGL;

import camzup.core.Entity3;
import camzup.core.Experimental;
import camzup.core.Transform3;
import camzup.core.Utils;
import camzup.core.Vec3;

/**
 * An entity-based camera class which updates a renderer's camera.
 */
public class Cam3 extends Entity3 {

  /**
   * An internal vector to hold the location retrieved from the
   * transform.
   */
  protected final Vec3 trLoc;

  {
    this.trLoc = new Vec3();
  }

  /**
   * The default constructor.
   */
  public Cam3 ( ) { super(); }

  /**
   * Constructs a camera with a name and renderer.
   *
   * @param name the name
   */
  public Cam3 ( final String name ) { super(name); }

  /**
   * Constructs a camera with a name, transform.
   *
   * @param name      the name
   * @param transform the transform
   */
  public Cam3 (
      final String name,
      final Transform3 transform ) {

    super(name, transform);
  }

  /**
   * Constructs a camera with a transform.
   *
   * @param transform the transform
   */
  public Cam3 ( final Transform3 transform ) {

    super(transform);
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
   * Sets the renderer projection to orthographic, where objects
   * maintain their size regardless of distance from the camera.
   *
   * @param rndr the renderer
   * @return this camera entity
   */
  public Cam3 ortho ( final PGraphicsOpenGL rndr ) {

    final float right = rndr.width < 128
        ? IUp.DEFAULT_HALF_WIDTH
        : rndr.width * 0.5f;
    final float left = -right;

    final float top = rndr.height < 128
        ? IUp.DEFAULT_HALF_HEIGHT
        : rndr.height * 0.5f;
    final float bottom = -top;

    return this.ortho(rndr, left, right, bottom, top);
  }

  /**
   * Sets the renderer projection to orthographic, where objects
   * maintain their size regardless of distance from the camera.
   *
   * @param rndr   the renderer
   * @param left   the left edge of the window
   * @param right  the right edge of the window
   * @param bottom the bottom edge of the window
   * @param top    the top edge of the window
   * @return this camera entity
   */
  public Cam3 ortho (
      final PGraphicsOpenGL rndr,
      final float left, final float right,
      final float bottom, final float top ) {

    return this.ortho(rndr,
        left, right, bottom, top,
        IUp.DEFAULT_NEAR_CLIP,
        IUp.DEFAULT_FAR_CLIP);
  }

  /**
   * Sets the renderer projection to orthographic, where objects
   * maintain their size regardless of distance from the camera.
   *
   * @param rndr   the renderer
   * @param left   the left edge of the window
   * @param right  the right edge of the window
   * @param bottom the bottom edge of the window
   * @param top    the top edge of the window
   * @param near   the near clip plane
   * @param far    the far clip plane
   * @return this camera entity
   */
  public Cam3 ortho (
      final PGraphicsOpenGL rndr,
      final float left, final float right,
      final float bottom, final float top,
      final float near, final float far ) {

    PMatAux.orthographic(
        left, right,
        bottom, top,
        near, far,
        rndr.projection);

    return this;
  }

  /**
   * Sets the renderer projection to a perspective, where objects nearer
   * to the camera appear larger than objects distant from the camera.
   *
   * @param rndr the renderer
   * @return this camera entity
   */
  public Cam3 perspective ( final PGraphicsOpenGL rndr ) {

    return this.perspective(rndr, IUp.DEFAULT_FOV);
  }

  /**
   * Sets the renderer projection to a perspective, where objects nearer
   * to the camera appear larger than objects distant from the camera.
   *
   * @param rndr the renderer
   * @param fov  the field of view
   * @return this camera entity
   */
  public Cam3 perspective (
      final PGraphicsOpenGL rndr,
      final float fov ) {

    return this.perspective(rndr, fov, Utils.div(rndr.width, rndr.height));
  }

  /**
   * Sets the renderer projection to a perspective, where objects nearer
   * to the camera appear larger than objects distant from the camera.
   *
   * @param rndr   the renderer
   * @param fov    the field of view
   * @param aspect the aspect ratio, width over height
   * @return this camera entity
   */
  public Cam3 perspective (
      final PGraphicsOpenGL rndr,
      final float fov,
      final float aspect ) {

    this.perspective(rndr, fov, aspect,
        IUp.DEFAULT_NEAR_CLIP,
        IUp.DEFAULT_FAR_CLIP);

    return this;
  }

  /**
   * Sets the renderer projection to a perspective, where objects nearer
   * to the camera appear larger than objects distant from the camera.
   *
   * @param rndr   the renderer
   * @param fov    the field of view
   * @param aspect the aspect ratio, width over height
   * @param near   the near clip plane
   * @param far    the far clip plane
   * @return this camera entity
   */
  public Cam3 perspective (
      final PGraphicsOpenGL rndr,
      final float fov,
      final float aspect,
      final float near,
      final float far ) {

    rndr.cameraFOV = fov;
    rndr.cameraAspect = aspect;
    rndr.cameraNear = near;
    rndr.cameraFar = far;

    PMatAux.perspective(fov, aspect, near, far, rndr.projection);

    return this;
  }

  /**
   * Updates the renderer's camera matrices based on the transform. To
   * be called in update.
   *
   * @param rndr the renderer
   * @return this camera entity
   */
  public Cam3 update ( final Up3 rndr ) {

    // TODO: Generalize to PGraphicsOpenGL renderer.

    this.transform.getAxes(
        rndr.i,
        rndr.j,
        rndr.k);

    this.transform.getLocation(this.trLoc);
    rndr.cameraX = this.trLoc.x;
    rndr.cameraY = this.trLoc.y;
    rndr.cameraZ = this.trLoc.z;

    rndr.updateCamera();

    return this;
  }
}

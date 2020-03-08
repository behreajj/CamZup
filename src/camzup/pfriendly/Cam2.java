package camzup.pfriendly;

import java.awt.geom.AffineTransform;

import processing.awt.PGraphicsJava2D;
import processing.opengl.PGraphicsOpenGL;

import camzup.core.Entity2;
import camzup.core.Transform2;
import camzup.core.Utils;
import camzup.core.Vec2;

/**
 * A camera entity designed to update a renderer's camera matrices
 * based on its transform.
 */
public class Cam2 extends Entity2 {

  /**
   * An internal vector to hold the right axis retrieved from the
   * transform.
   */
  protected final Vec2 trAxis;

  /**
   * An internal vector to hold the location retrieved from the
   * transform.
   */
  protected final Vec2 trLoc;

  /**
   * An internal vector to hold the scale retrieved from the transform.
   */
  protected final Vec2 trScl;

  {
    this.trAxis = new Vec2();
    this.trLoc = new Vec2();
    this.trScl = new Vec2();
  }

  /**
   * The default constructor.
   */
  public Cam2 ( ) { super(); }

  /**
   * Constructs a 2D camera entity from a name and transform.
   *
   * @param name the name
   */
  public Cam2 ( final String name ) { super(name); }

  /**
   * Constructs a 2D camera entity from a name and transform.
   *
   * @param name      the name
   * @param transform the transform
   */
  public Cam2 (
      final String name,
      final Transform2 transform ) {

    super(name, transform);
  }

  /**
   * Constructs a 2D camera entity from a transform.
   *
   * @param transform the transform
   */
  public Cam2 ( final Transform2 transform ) { super(transform); }

  /**
   * Updates an AWT renderer based on the camera entity's transform.
   *
   * @param rndr the renderer
   * @return this camera
   */
  public Cam2 update ( final PGraphicsJava2D rndr ) {

    this.transform.getLocation(this.trLoc);
    this.transform.getScale(this.trScl);
    this.transform.getRight(this.trAxis);

    final double sclxInv = Utils.div(1.0d, this.trScl.x);
    final double sclyInv = Utils.div(1.0d, this.trScl.y);
    final double m00 = this.trAxis.x * sclxInv;
    final double m01 = this.trAxis.y * sclyInv;
    final double m10 = -this.trAxis.y * sclxInv;
    final double m11 = this.trAxis.x * sclyInv;

    final AffineTransform affine = new AffineTransform(
        m00, -m10,
        m01, -m11,
        rndr.width * 0.5d
            - this.trLoc.x * m00
            - this.trLoc.y * m01,
        rndr.height * 0.5d
            + this.trLoc.x * m10
            + this.trLoc.y * m11);
    rndr.g2.setTransform(affine);
    return this;
  }

  /**
   * Updates an OpenGL renderer based on the camera entity's transform.
   *
   * @param rndr the renderer
   * @return this camera
   */
  public Cam2 update ( final PGraphicsOpenGL rndr ) {

    this.transform.getLocation(this.trLoc);
    this.transform.getScale(this.trScl);
    this.transform.getRight(this.trAxis);

    final float zDist = rndr.height < 128 ? 128 : rndr.height;

    final float m00 = Utils.div(this.trAxis.x, this.trScl.x);
    final float m01 = Utils.div(this.trAxis.y, this.trScl.y);
    final float m10 = Utils.div(-this.trAxis.y, this.trScl.x);
    final float m11 = Utils.div(this.trAxis.x, this.trScl.y);

    final float right = rndr.width * 0.5f;
    final float top = rndr.height * 0.5f;
    PMatAux.orthographic(
        -right, right,
        -top, top,
        IUp.DEFAULT_NEAR_CLIP,
        IUp.DEFAULT_NEAR_CLIP + zDist * 10.0f,
        rndr.projection);

    rndr.modelview.set(
        m00, m01, 0.0f, -this.trLoc.x * m00 - this.trLoc.y * m01,
        m10, m11, 0.0f, -this.trLoc.x * m10 - this.trLoc.y * m11,
        0.0f, 0.0f, 1.0f, -zDist,
        0.0f, 0.0f, 0.0f, 1.0f);

    rndr.modelviewInv.set(
        this.trAxis.x * this.trScl.x, -this.trAxis.y * this.trScl.y, 0.0f,
        this.trLoc.x,
        this.trAxis.y * this.trScl.y, this.trAxis.x * this.trScl.y, 0.0f,
        this.trLoc.y,
        0.0f, 0.0f, 1.0f, zDist,
        0.0f, 0.0f, 0.0f, 1.0f);

    rndr.camera.set(rndr.modelview);
    rndr.cameraInv.set(rndr.modelviewInv);
    rndr.projmodelview.set(rndr.projection);

    return this;
  }
}

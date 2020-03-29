package camzup.pfriendly;

import java.util.Iterator;
import java.util.List;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PMatrix3D;
import processing.opengl.PGraphicsOpenGL;

import camzup.core.Color;
import camzup.core.Curve3;
import camzup.core.CurveEntity3;
import camzup.core.Experimental;
import camzup.core.IUtils;
import camzup.core.Knot3;
import camzup.core.MaterialSolid;
import camzup.core.Mesh3;
import camzup.core.MeshEntity3;
import camzup.core.Quaternion;
import camzup.core.Transform3;
import camzup.core.TransformOrder;
import camzup.core.Utils;
import camzup.core.Vec2;
import camzup.core.Vec3;
import camzup.core.Vec4;

/**
 * An abstract parent class for 3D renderers.
 */
public abstract class Up3 extends UpOgl implements IUpOgl, IUp3 {

  /**
   * Default look at target x component.
   */
  public static final float DEFAULT_TARGET_X = 0.0f;

  /**
   * Default look at target y component.
   */
  public static final float DEFAULT_TARGET_Y = 0.0f;

  /**
   * Default look at target z component.
   */
  public static final float DEFAULT_TARGET_Z = 0.0f;

  /**
   * The path string for this renderer.
   */
  public static final String PATH_STR = "camzup.pfriendly.Up3";

  /**
   * Tolerance beneath which the camera's forward direction will be
   * considered the world, or reference, up direction.
   */
  public final static float POLARITY_TOLERANCE = 0.01f;

  /**
   * A vector to store the x axis (first column) when creating a camera
   * look-at matrix.
   */
  protected final Vec3 i;

  /**
   * A vector to store the y axis (second column) when creating a camera
   * look-at matrix.
   */
  protected final Vec3 j;

  /**
   * A vector to store the z axis (third column) when creating a camera
   * look-at matrix.
   */
  protected final Vec3 k;

  /**
   * A vector to store the unnormalized look direction when creating a
   * camera look-at matrix.
   */
  protected final Vec3 lookDir;

  /**
   * A vector to store the target at which a camera looks.
   */
  protected final Vec3 lookTarget;

  /**
   * The reference or "world" up vector against which a camera look-at
   * matrix is created.
   */
  protected final Vec3 refUp;

  {
    this.i = Vec3.right(new Vec3());
    this.j = Vec3.forward(new Vec3());
    this.k = Vec3.up(new Vec3());
    this.lookDir = Vec3.up(new Vec3());
    this.lookTarget = new Vec3();
    this.refUp = Vec3.up(new Vec3());

    // protected final Vec3 tr3Loc;
    // protected final Quaternion tr3Rot;
    // protected final Vec3 tr3Scale;

    // tr3Loc = new Vec3();
    // tr3Rot = new Quaternion();
    // tr3Scale = Vec3.one(new Vec3());
  }

  /**
   * The default constructor.
   */
  public Up3 ( ) { super(); }

  /**
   * A constructor for manually initializing the renderer.
   *
   * @param width     renderer width
   * @param height    renderer height
   * @param parent    parent applet
   * @param path      applet path
   * @param isPrimary is the renderer primary
   */
  public Up3 (
      final int width,
      final int height,
      final PApplet parent,
      final String path,
      final boolean isPrimary ) {

    super(width, height, parent, path, isPrimary);
  }

  /**
   * Establishes the default perspective arguments.
   *
   * @see PGraphicsOpenGL#defCameraAspect
   * @see PGraphicsOpenGL#defCameraFOV
   * @see PGraphicsOpenGL#defCameraNear
   * @see PGraphicsOpenGL#defCameraFar
   */
  @Override
  protected void defaultPerspective ( ) {

    this.cameraAspect = this.defCameraAspect = IUp.DEFAULT_ASPECT;
    this.cameraFOV = this.defCameraFOV = IUp.DEFAULT_FOV;
    this.cameraNear = this.defCameraNear = IUp.DEFAULT_NEAR_CLIP;
    this.cameraFar = this.defCameraFar = IUp.DEFAULT_FAR_CLIP;

    this.ortho();
  }

  /**
   * A helper function for the renderer camera. Updates the camera
   * matrix, its inverse, the model view and its inverse, and updates
   * the project model view.
   */
  protected void updateCamera ( ) {

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
     * Set matrix to axes by row. Translate by a negative location after
     * the rotation.
     */
    this.camera.set(
        m00, m01, m02,
        -this.cameraX * m00 - this.cameraY * m01 - this.cameraZ * m02,
        m10, m11, m12,
        -this.cameraX * m10 - this.cameraY * m11 - this.cameraZ * m12,
        m20, m21, m22,
        -this.cameraX * m20 - this.cameraY * m21 - this.cameraZ * m22,
        0.0f, 0.0f, 0.0f, 1.0f);

    /* Set inverse by column. */
    this.cameraInv.set(
        m00, m10, m20, this.cameraX,
        m01, m11, m21, this.cameraY,
        m02, m12, m22, this.cameraZ,
        0.0f, 0.0f, 0.0f, 1.0f);

    /* Set model view to camera. */
    this.modelview.set(this.camera);
    this.modelviewInv.set(this.cameraInv);
    PMatAux.mul(this.projection, this.modelview, this.projmodelview);
  }

  /**
   * Sets the camera to the Processing default, where the origin is in
   * the top left corner of the sketch and the y axis points downward.
   */
  void camDown ( ) {

    // TEST: Doesn't work.

    final float wh = this.width * 0.5f;
    final float hh = this.height * 0.5f;
    super.camera(
        wh, hh, this.height * IUtils.SQRT_3_2,
        wh, hh, 0.0f,
        0.0f, -1.0f, 0.0f);
  }

  @Experimental
  void pan ( final float radians ) {

    final float nrm = radians * IUtils.ONE_TAU;
    final float cosa = Utils.scNorm(nrm);
    final float sina = Utils.scNorm(nrm - 0.25f);

    float xn = this.lookTarget.x - this.cameraX;
    // float yn = this.lookTarget.y - this.cameraY;
    float zn = this.lookTarget.z - this.cameraZ;

    final float temp = xn;
    xn = cosa * xn + sina * zn;
    zn = cosa * zn - sina * temp;

    xn += this.cameraX;
    // yn += this.cameraY;
    zn += this.cameraZ;

    this.camera(
        this.cameraX, this.cameraY, this.cameraZ,
        xn, this.lookTarget.y, zn,
        this.refUp.x, this.refUp.y, this.refUp.z);
  }

  @Experimental
  void roll ( final float radians ) {

    final float nrm = radians * IUtils.ONE_TAU;
    final float cosa = Utils.scNorm(nrm);
    final float sina = Utils.scNorm(nrm - 0.25f);

    float xn = this.lookTarget.x - this.cameraX;
    float yn = this.lookTarget.y - this.cameraY;
    // float zn = this.lookTarget.z - this.cameraZ;

    final float temp = xn;
    xn = cosa * xn - sina * yn;
    yn = cosa * yn + sina * temp;

    xn += this.cameraX;
    yn += this.cameraY;
    // zn += this.cameraZ;

    this.camera(
        this.cameraX, this.cameraY, this.cameraZ,
        xn, yn, this.lookTarget.z,
        this.refUp.x, this.refUp.y, this.refUp.z);
  }

  @Experimental
  void tilt ( final float radians ) {

    final float nrm = radians * IUtils.ONE_TAU;
    final float cosa = Utils.scNorm(nrm);
    final float sina = Utils.scNorm(nrm - 0.25f);

    // float xn = this.lookTarget.x - this.cameraX;
    float yn = this.lookTarget.y - this.cameraY;
    float zn = this.lookTarget.z - this.cameraZ;

    final float temp = yn;
    yn = cosa * yn - sina * zn;
    zn = cosa * zn + sina * temp;

    // xn += this.cameraX;
    yn += this.cameraY;
    zn += this.cameraZ;

    this.camera(
        this.cameraX, this.cameraY, this.cameraZ,
        this.lookTarget.x, yn, zn,
        this.refUp.x, this.refUp.y, this.refUp.z);
  }

  /**
   * Draws a single Bezier curve.
   *
   * @param ap0 the first anchor point
   * @param cp0 the first control point
   * @param cp1 the second control point
   * @param ap1 the second anchor point
   */
  @Override
  public void bezier (
      final Vec3 ap0,
      final Vec3 cp0,
      final Vec3 cp1,
      final Vec3 ap1 ) {

    this.bezier(
        ap0.x, ap0.y, ap0.z,
        cp0.x, cp0.y, cp0.z,
        cp1.x, cp1.y, cp1.z,
        ap1.x, ap1.y, ap1.z);
  }

  /**
   * Draws a bezier vertex with three vectors: the following control
   * point, the rear control point of the ensuing point, and the ensuing
   * anchor point.
   *
   * @param cp0 the first control point
   * @param cp1 the second control point
   * @param ap1 the next anchor point
   */
  @Override
  public void bezierVertex (
      final Vec3 cp0,
      final Vec3 cp1,
      final Vec3 ap1 ) {

    this.bezierVertexImpl(
        cp0.x, cp0.y, cp0.z,
        cp1.x, cp1.y, cp1.z,
        ap1.x, ap1.y, ap1.z);
  }

  /**
   * Sets the camera to a location, looking at a center, with a
   * reference up direction.
   *
   * @param eye    the eye location
   * @param center the center of the gaze
   * @param up     the reference up direction
   */
  public void camera (
      final Vec3 eye,
      final Vec3 center,
      final Vec3 up ) {

    this.camera(
        eye.x, eye.y, eye.z,
        center.x, center.y, center.z,
        up.x, up.y, up.z);
  }

  /**
   * Draws a curve between four vectors.
   *
   * @param a the first vector
   * @param b the second vector
   * @param c the third vector
   * @param d the fourth vector
   */
  public void curve (
      final Vec3 a, final Vec3 b,
      final Vec3 c, final Vec3 d ) {

    this.curve(
        a.x, a.y, a.z,
        b.x, b.y, b.z,
        c.x, c.y, c.z,
        d.x, d.y, d.z);
  }

  /**
   * Draws a curve vertex to a vector.
   *
   * @param a the vector.
   */
  public void curveVertex ( final Vec3 a ) {

    this.curveVertex(a.x, a.y, a.z);
  }

  /**
   * Initialize a directional light with a color and a direction.
   *
   * @param color the color
   * @param xDir  the x direction
   * @param yDir  the y direction
   * @param zDir  the z direction
   */
  public void directionalLight (
      final int color,
      final float xDir,
      final float yDir,
      final float zDir ) {

    Color.fromHex(color, this.aTemp);
    this.directionalLight(
        this.colorModeX * this.aTemp.x,
        this.colorModeY * this.aTemp.y,
        this.colorModeZ * this.aTemp.z,
        xDir, yDir, zDir);
  }

  /**
   * Initialize a directional light with a color and a direction.
   *
   * @param color the color
   * @param dir   the direction
   */
  public void directionalLight (
      final int color,
      final Vec3 dir ) {

    Color.fromHex(color, this.aTemp);
    this.directionalLight(
        this.colorModeX * this.aTemp.x,
        this.colorModeY * this.aTemp.y,
        this.colorModeZ * this.aTemp.z,
        dir.x, dir.y, dir.z);
  }

  /**
   * Initialize a directional light with a color and a direction.
   *
   * @param color the color
   * @param xDir  the x direction
   * @param yDir  the y direction
   * @param zDir  the z direction
   */
  public void directionalLight (
      final Vec4 color,
      final float xDir,
      final float yDir,
      final float zDir ) {

    this.directionalLight(
        this.colorModeX * color.x,
        this.colorModeY * color.y,
        this.colorModeZ * color.z,
        xDir, yDir, zDir);
  }

  /**
   * Initialize a directional light with a color and a direction.
   *
   * @param color the color
   * @param dir   the direction
   */
  public void directionalLight (
      final Vec4 color,
      final Vec3 dir ) {

    this.directionalLight(
        this.colorModeX * color.x,
        this.colorModeY * color.y,
        this.colorModeZ * color.z,
        dir.x, dir.y, dir.z);
  }

  /**
   * Dollies the camera, moving it on its local z axis, backward or
   * forward. This is done by multiplying the z magnitude by the camera
   * inverse, then adding the local coordinates to both the camera
   * location and look target.
   *
   * @param z the z magnitude
   */
  public void dolly ( final float z ) {

    final float w = this.cameraInv.m32 * z + this.cameraInv.m33;
    if ( w != 0.0f ) {
      final float wInv = 1.0f / w;
      final float xLocal = this.cameraInv.m02 * z * wInv;
      final float yLocal = this.cameraInv.m12 * z * wInv;
      final float zLocal = this.cameraInv.m22 * z * wInv;

      this.cameraX += xLocal;
      this.cameraY += yLocal;
      this.cameraZ += zLocal;

      this.lookTarget.x += xLocal;
      this.lookTarget.y += yLocal;
      this.lookTarget.z += zLocal;
    }
  }

  /**
   * Gets the eye distance of the camera.
   *
   * @return the eye distance
   */
  public float getEyeDist ( ) { return this.eyeDist; }

  /**
   * Gets the x axis of the camera transform.
   *
   * @param target the output vector
   * @return the x axis
   */
  public Vec3 getI ( final Vec3 target ) { return target.set(this.i); }

  /**
   * Gets the y axis of the camera transform.
   *
   * @param target the output vector
   * @return the y axis
   */
  public Vec3 getJ ( final Vec3 target ) { return target.set(this.j); }

  /**
   * Gets the z axis of the camera transform.
   *
   * @param target the output vector
   * @return the z axis
   */
  public Vec3 getK ( final Vec3 target ) { return target.set(this.k); }

  /**
   * Gets the renderer camera's 3D location.
   *
   * @param target the output vector
   * @return the location
   */
  @Override
  public Vec3 getLocation ( final Vec3 target ) {

    return target.set(
        this.cameraX,
        this.cameraY,
        this.cameraZ);
  }

  /**
   * Gets the direction in which the camera is looking.
   *
   * @param target the output vector
   * @return the look direction
   */
  public Vec3 getLookDir ( final Vec3 target ) {

    return target.set(this.lookDir);
  }

  /**
   * Gets the point at which the camera is looking.
   *
   * @param target the output vector
   * @return the look target
   */
  public Vec3 getLookTarget ( final Vec3 target ) {

    return target.set(this.lookTarget);
  }

  /**
   * Gets the renderer model view matrix.
   *
   * @return the model view
   */
  @Override
  public PMatrix3D getMatrix ( ) {

    return this.getMatrix((PMatrix3D) null);
  }

  /**
   * Gets the reference up axis of the camera.
   *
   * @param target the output vector
   * @return the reference up
   */
  public Vec3 getRefUp ( final Vec3 target ) {

    return target.set(this.refUp);
  }

  /**
   * Gets the width and height of the renderer as a vector.
   *
   * @return the size
   */
  @Override
  public Vec2 getSize ( final Vec2 target ) {

    return target.set(this.width, this.height);
  }

  /**
   * Gets the renderer's size. The z component is set to zero.
   *
   * @param target the output vector
   * @return the size
   */
  public Vec3 getSize ( final Vec3 target ) {

    return target.set(this.width, this.height, 0.0f);
  }

  /**
   * Displays the handles of a curve entity.
   *
   * @param ce the curve entity
   */
  public void handles ( final CurveEntity3 ce ) { this.handles(ce, 1.0f); }

  /**
   * Displays the handles of a curve entity.
   *
   * @param ce           the curve entity
   * @param strokeWeight the stroke weight
   */
  public void handles (
      final CurveEntity3 ce,
      final float strokeWeight ) {

    this.handles(ce, strokeWeight,
        IUp.DEFAULT_HANDLE_COLOR,
        IUp.DEFAULT_HANDLE_REAR_COLOR,
        IUp.DEFAULT_HANDLE_FORE_COLOR,
        IUp.DEFAULT_HANDLE_COORD_COLOR);
  }

  /**
   * Displays the handles of a curve entity.
   *
   * @param ce           the curve entity
   * @param strokeWeight the stroke weight
   * @param lineColor    the color of handle lines
   * @param rearColor    the color of the rear handle
   * @param foreColor    the color of the fore handle
   * @param coordColor   the color of the coordinate
   */
  public void handles (
      final CurveEntity3 ce,
      final float strokeWeight,
      final int lineColor,
      final int rearColor,
      final int foreColor,
      final int coordColor ) {

    final float swRear = strokeWeight * 4.0f;
    final float swFore = swRear * 1.25f;
    final float swCoord = swFore * 1.25f;

    final Transform3 tr = ce.transform;
    final List < Curve3 > curves = ce.curves;
    final Iterator < Curve3 > curveItr = curves.iterator();

    final Vec3 rh = new Vec3();
    final Vec3 co = new Vec3();
    final Vec3 fh = new Vec3();

    // this.flush();
    // this.pgl.disable(PGL.DEPTH_TEST);
    // this.pgl.depthMask(false);
    // this.isDepthSortingEnabled = false;
    this.hint(PConstants.DISABLE_DEPTH_TEST);
    this.hint(PConstants.DISABLE_DEPTH_MASK);
    this.hint(PConstants.DISABLE_DEPTH_SORT);

    this.pushStyle();

    while ( curveItr.hasNext() ) {
      final Curve3 curve = curveItr.next();
      final Iterator < Knot3 > knItr = curve.iterator();

      while ( knItr.hasNext() ) {
        final Knot3 knot = knItr.next();

        Transform3.mulPoint(tr, knot.rearHandle, rh);
        Transform3.mulPoint(tr, knot.coord, co);
        Transform3.mulPoint(tr, knot.foreHandle, fh);

        this.strokeWeight(strokeWeight);
        this.stroke(lineColor);

        this.lineImpl(
            rh.x, rh.y, rh.z,
            co.x, co.y, co.z);

        this.lineImpl(
            co.x, co.y, co.z,
            fh.x, fh.y, fh.z);

        this.strokeWeight(swRear);
        this.stroke(rearColor);
        this.pointImpl(
            rh.x, rh.y, rh.z);

        this.strokeWeight(swCoord);
        this.stroke(coordColor);
        this.pointImpl(
            co.x, co.y, co.z);

        this.strokeWeight(swFore);
        this.stroke(foreColor);
        this.pointImpl(
            fh.x, fh.y, fh.z);
      }
    }

    this.popStyle();

    // Trying to shortcut this leads to an error.
    // this.flush();
    // this.pgl.enable(PGL.DEPTH_TEST);
    // this.pgl.depthMask(true);
    // this.isDepthSortingEnabled = true;
    this.hint(PConstants.ENABLE_DEPTH_TEST);
    this.hint(PConstants.ENABLE_DEPTH_MASK);
    this.hint(PConstants.ENABLE_DEPTH_SORT);
  }

  /**
   * Returns whether or not the renderer is 2D (false).
   */
  @Override
  public boolean is2D ( ) { return false; }

  /**
   * Returns whether or not the renderer is 3D (true).
   */
  @Override
  public boolean is3D ( ) { return true; }

  /**
   * Gets whether or not depth sorting is enabled.
   *
   * @return the depth sorting
   */
  public boolean isDepthSorted ( ) { return this.isDepthSortingEnabled; }

  /**
   * Draws a line between two coordinates.
   *
   * @param origin the origin coordinate
   * @param dest   the destination coordinate
   */
  @Override
  public void line ( final Vec3 origin, final Vec3 dest ) {

    this.lineImpl(
        origin.x, origin.y, origin.z,
        dest.x, dest.y, dest.z);
  }

  /**
   * Sets the renderer's stroke, stroke weight and fill to a material's.
   *
   * Due to stroke flickering in perspective, currently a material with
   * a fill may not also use a stroke.
   *
   * @param material the material
   */
  @Override
  public void material ( final MaterialSolid material ) {

    /*
     * Due to stroke flickering issues, a material in 3D will not have
     * both a stroke and a fill.
     */
    if ( material.useFill ) {
      this.noStroke();
      this.fill(material.fill);
    } else {
      this.noFill();
      if ( material.useStroke ) {
        this.strokeWeight(material.strokeWeight);
        this.stroke(Color.toHexInt(material.stroke));
      } else {
        this.noStroke();
      }
    }
  }

  /**
   * Moves the renderer's camera location.
   *
   * @param x the vector x
   * @param y the vector y
   * @param z the vector z
   */
  public void moveBy (
      final float x,
      final float y,
      final float z ) {

    this.moveByLocal(x, y, z);
  }

  /**
   * Moves the renderer's camera location.
   *
   * @param v the vector
   */
  public void moveBy ( final Vec3 v ) {

    this.moveByLocal(v.x, v.y, v.z);
  }

  /**
   * Moves the renderer's camera by a vector. Does <em>not</em> update
   * the camera's look target, and so will result in tangential motion.
   *
   * @param x the vector x
   * @param y the vector y
   * @param z the vector z
   * @see Vec3#areParallel(Vec3, Vec3, float)
   */
  public void moveByGlobal (
      final float x,
      final float y,
      final float z ) {

    if ( Vec3.areParallel(this.k, this.refUp, Up3.POLARITY_TOLERANCE) ) {
      return;
    }

    this.cameraX += x;
    this.cameraY += y;
    this.cameraZ += z;
  }

  /**
   * Moves the renderer's camera by a vector. Does <em>not</em> update
   * the camera's look target, and so will result in tangential motion.
   *
   * @param v the vector
   */
  public void moveByGlobal ( final Vec3 v ) {

    this.moveByGlobal(v.x, v.y, v.z);
  }

  /**
   * Moves the renderer's camera by a vector relative to its
   * orientation; causes the camera to orbit around the locus at which
   * it is looking.
   *
   * @param x the vector x
   * @param y the vector y
   * @param z the vector z
   */
  public void moveByLocal (
      final float x,
      final float y,
      final float z ) {

    if ( Vec3.areParallel(this.k, this.refUp, Up3.POLARITY_TOLERANCE) ) {
      return;
    }

    final PMatrix3D ci = this.cameraInv;
    final float w = ci.m30 * x + ci.m31 * y + ci.m32 * z + ci.m33;
    if ( w != 0.0f ) {
      final float wInv = 1.0f / w;
      final float xLocal = (ci.m00 * x + ci.m01 * y + ci.m02 * z) * wInv;
      final float yLocal = (ci.m10 * x + ci.m11 * y + ci.m12 * z) * wInv;
      final float zLocal = (ci.m20 * x + ci.m21 * y + ci.m22 * z) * wInv;
      this.cameraX += xLocal;
      this.cameraY += yLocal;
      this.cameraZ += zLocal;
    }
  }

  /**
   * Moves the renderer's camera by a vector relative to its
   * orientation; causes the camera to orbit around the locus at which
   * it is looking.
   *
   * @param v the vector
   */
  public void moveByLocal ( final Vec3 v ) {

    this.moveByLocal(v.x, v.y, v.z);
  }

  /**
   * Sets the renderer camera's location.
   *
   * @param x the x location
   * @param y the y location
   * @param z the z location
   */
  public void moveTo (
      final float x,
      final float y,
      final float z ) {

    this.cameraX = x;
    this.cameraY = y;
    this.cameraZ = z;
  }

  /**
   * Sets the renderer camera's location.
   *
   * @param locNew the new location
   */
  public void moveTo ( final Vec3 locNew ) {

    this.cameraX = locNew.x;
    this.cameraY = locNew.y;
    this.cameraZ = locNew.z;
  }

  /**
   * Eases the renderer camera's location to the destination over a step
   * in [0.0, 1.0] .
   *
   * @param locNew the new location
   * @param step   the step
   */
  public void moveTo (
      final Vec3 locNew,
      final float step ) {

    if ( step <= 0.0f ) { return; }
    if ( step >= 1.0f ) {
      this.moveTo(locNew.x, locNew.y, locNew.z);
      return;
    }

    final float u = 1.0f - step;
    this.moveTo(
        u * this.cameraX + step * locNew.x,
        u * this.cameraY + step * locNew.y,
        u * this.cameraZ + step * locNew.z);
  }

  /**
   * Draws the world origin.
   */
  @Override
  public void origin ( ) {

    this.origin(
        IUp.DEFAULT_IJK_LINE_FAC * Utils.min(this.width, this.height),
        IUp.DEFAULT_IJK_SWEIGHT,
        IUp.DEFAULT_I_COLOR,
        IUp.DEFAULT_J_COLOR,
        IUp.DEFAULT_K_COLOR);
  }

  /**
   * Draws the world origin.
   *
   * @param lineLength the line length
   */
  public void origin ( final float lineLength ) {

    this.origin(lineLength,
        IUp.DEFAULT_IJK_SWEIGHT,
        IUp.DEFAULT_I_COLOR,
        IUp.DEFAULT_J_COLOR,
        IUp.DEFAULT_K_COLOR);
  }

  /**
   * Draws the world origin.
   *
   * @param lineLength   the line length
   * @param strokeWeight the stroke weight
   */
  public void origin (
      final float lineLength,
      final float strokeWeight ) {

    this.origin(lineLength,
        strokeWeight,
        IUp.DEFAULT_I_COLOR,
        IUp.DEFAULT_J_COLOR,
        IUp.DEFAULT_K_COLOR);
  }

  /**
   * Draws the world origin.
   *
   * @param lineLength   the line length
   * @param strokeWeight the stroke weight
   * @param xColor       the color of the x axis
   * @param yColor       the color of the y axis
   * @param zColor       the color of the z axis
   */
  public void origin (
      final float lineLength,
      final float strokeWeight,
      final int xColor,
      final int yColor,
      final int zColor ) {

    // this.flush();
    // this.pgl.disable(PGL.DEPTH_TEST);
    // this.pgl.depthMask(false);
    // this.isDepthSortingEnabled = false;
    this.hint(PConstants.DISABLE_DEPTH_TEST);
    this.hint(PConstants.DISABLE_DEPTH_MASK);
    this.hint(PConstants.DISABLE_DEPTH_SORT);

    final float vl = Utils.max(IUtils.DEFAULT_EPSILON, lineLength);

    this.pushStyle();
    this.strokeWeight(strokeWeight);

    this.stroke(zColor);
    this.lineImpl(
        0.0f, 0.0f, 0.0f,
        0.0f, 0.0f, vl);

    this.stroke(yColor);
    this.lineImpl(
        0.0f, 0.0f, 0.0f,
        0.0f, vl, 0.0f);

    this.stroke(xColor);
    this.lineImpl(
        0.0f, 0.0f, 0.0f,
        vl, 0.0f, 0.0f);

    this.popStyle();

    // this.flush();
    // this.pgl.enable(PGL.DEPTH_TEST);
    // this.pgl.depthMask(true);
    // this.isDepthSortingEnabled = true;
    this.hint(PConstants.ENABLE_DEPTH_TEST);
    this.hint(PConstants.ENABLE_DEPTH_MASK);
    this.hint(PConstants.ENABLE_DEPTH_SORT);
  }

  /**
   * Boom or pedestal the camera, moving it on its local y axis, up or
   * down. This is done by multiplying the y magnitude by the camera
   * inverse, then adding the local coordinates to both the camera
   * location and look target.
   *
   * @param y the y magnitude
   */
  public void pedestal ( final float y ) {

    final PMatrix3D ci = this.cameraInv;
    final float w = ci.m31 * y + ci.m33;
    if ( w != 0.0f ) {
      final float wInv = 1.0f / w;
      final float xLocal = ci.m01 * y * wInv;
      final float yLocal = ci.m11 * y * wInv;
      final float zLocal = ci.m21 * y * wInv;

      this.cameraX += xLocal;
      this.cameraY += yLocal;
      this.cameraZ += zLocal;

      this.lookTarget.x += xLocal;
      this.lookTarget.y += yLocal;
      this.lookTarget.z += zLocal;
    }
  }

  /**
   * Draws a point at a given coordinate
   *
   * @param v the coordinate
   */
  @Override
  public void point ( final Vec3 v ) { this.pointImpl(v.x, v.y, v.z); }

  /**
   * Draws a quadratic Bezier curve segment to the next anchor point;
   * the control point shapes the curve segment.
   *
   * @param cp  the control point
   * @param ap1 the next anchor point
   */
  @Override
  public void quadraticVertex (
      final Vec3 cp,
      final Vec3 ap1 ) {

    this.quadraticVertex(
        cp.x, cp.y, cp.z,
        ap1.x, ap1.y, ap1.z);
  }

  /**
   * Rotates the model view matrix around an arbitrary axis by an angle
   * in radians.
   *
   * @param angle the angle in radians
   * @param xAxis the axis x coordinate
   * @param yAxis the axis y coordinate
   * @param zAxis the axis z coordinate
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
   * Rotates the sketch by an angle in radians around an arbitrary axis.
   *
   * @param angle the angle
   * @param axis  the axis
   */
  public void rotate (
      final float angle,
      final Vec3 axis ) {

    this.rotateImpl(angle, axis.x, axis.y, axis.z);
  }

  /**
   * Rotates the renderer by a quaternion.
   *
   * @param q the quaternion
   */
  public void rotate ( final Quaternion q ) {

    PMatAux.rotate(q, this.modelview);
    PMatAux.invRotate(q, this.modelviewInv);
    this.updateProjmodelview();
  }

  /**
   * Scales the renderer by a dimension.
   *
   * @param dim the dimensions
   */
  public void scale ( final Vec3 dim ) {

    this.scaleImpl(dim.x, dim.y, dim.z);
  }

  /**
   * Sets whether or not depth sorting is enabled. Flushes the geometry.
   *
   * @param ds the depth sorting
   */
  public void setDepthSorting ( final boolean ds ) {

    this.flush();
    this.isDepthSortingEnabled = ds;
  }

  /**
   * Sets the eye distance of the camera. The value is usually set by
   * the camera function, and to calculate the far clip plane of
   * perspective functions, but is exposed for public access.
   *
   * @param ed the eye distance
   */
  public void setEyeDist ( final float ed ) { this.eyeDist = ed; }

  /**
   * Draws a 3D curve entity.
   *
   * @param entity the curve entity
   */
  public void shape ( final CurveEntity3 entity ) {

    final Transform3 tr = entity.transform;
    final List < Curve3 > curves = entity.curves;
    final Iterator < Curve3 > curveItr = curves.iterator();

    final Vec3 v0 = new Vec3();
    final Vec3 v1 = new Vec3();
    final Vec3 v2 = new Vec3();

    Knot3 currKnot = null;
    Knot3 prevKnot = null;
    Vec3 coord = null;
    Vec3 foreHandle = null;
    Vec3 rearHandle = null;

    while ( curveItr.hasNext() ) {

      final Curve3 curve = curveItr.next();
      final Iterator < Knot3 > knItr = curve.iterator();
      prevKnot = knItr.next();
      coord = prevKnot.coord;

      Transform3.mulPoint(tr, coord, v2);

      this.beginShape();
      this.vertexImpl(
          v2.x, v2.y, v2.z,
          this.textureU,
          this.textureV);

      while ( knItr.hasNext() ) {
        currKnot = knItr.next();
        foreHandle = prevKnot.foreHandle;
        rearHandle = currKnot.rearHandle;
        coord = currKnot.coord;

        Transform3.mulPoint(tr, foreHandle, v0);
        Transform3.mulPoint(tr, rearHandle, v1);
        Transform3.mulPoint(tr, coord, v2);

        this.bezierVertexImpl(
            v0.x, v0.y, v0.z,
            v1.x, v1.y, v1.z,
            v2.x, v2.y, v2.z);

        prevKnot = currKnot;
      }

      if ( curve.closedLoop ) {
        currKnot = curve.getFirst();
        foreHandle = prevKnot.foreHandle;
        rearHandle = currKnot.rearHandle;
        coord = currKnot.coord;

        Transform3.mulPoint(tr, foreHandle, v0);
        Transform3.mulPoint(tr, rearHandle, v1);
        Transform3.mulPoint(tr, coord, v2);

        this.bezierVertexImpl(
            v0.x, v0.y, v0.z,
            v1.x, v1.y, v1.z,
            v2.x, v2.y, v2.z);
        this.endShape(PConstants.CLOSE);
      } else {
        this.endShape(PConstants.OPEN);
      }
    }
  }

  /**
   * Draws a 3D curve entity.
   *
   * @param entity   the curve entity
   * @param material the material
   */
  public void shape (
      final CurveEntity3 entity,
      final MaterialSolid material ) {

    this.pushStyle();
    this.material(material);
    this.shape(entity);
    this.popStyle();
  }

  /**
   * Draws a 3D curve entity.
   *
   * @param entity    the curve entity
   * @param materials the array of materials
   */
  public void shape (
      final CurveEntity3 entity,
      final MaterialSolid[] materials ) {

    final Transform3 tr = entity.transform;
    final List < Curve3 > curves = entity.curves;
    final Iterator < Curve3 > curveItr = curves.iterator();

    final Vec3 v0 = new Vec3();
    final Vec3 v1 = new Vec3();
    final Vec3 v2 = new Vec3();

    Knot3 currKnot = null;
    Knot3 prevKnot = null;
    Vec3 coord = null;
    Vec3 foreHandle = null;
    Vec3 rearHandle = null;

    while ( curveItr.hasNext() ) {

      final Curve3 curve = curveItr.next();
      this.pushStyle();
      this.material(materials[curve.materialIndex]);

      final Iterator < Knot3 > knItr = curve.iterator();
      prevKnot = knItr.next();
      coord = prevKnot.coord;

      Transform3.mulPoint(tr, coord, v2);

      this.beginShape();
      this.vertexImpl(
          v2.x, v2.y, v2.z,
          this.textureU,
          this.textureV);

      while ( knItr.hasNext() ) {
        currKnot = knItr.next();
        foreHandle = prevKnot.foreHandle;
        rearHandle = currKnot.rearHandle;
        coord = currKnot.coord;

        Transform3.mulPoint(tr, foreHandle, v0);
        Transform3.mulPoint(tr, rearHandle, v1);
        Transform3.mulPoint(tr, coord, v2);

        this.bezierVertexImpl(
            v0.x, v0.y, v0.z,
            v1.x, v1.y, v1.z,
            v2.x, v2.y, v2.z);

        prevKnot = currKnot;
      }

      if ( curve.closedLoop ) {
        currKnot = curve.getFirst();
        foreHandle = prevKnot.foreHandle;
        rearHandle = currKnot.rearHandle;
        coord = currKnot.coord;

        Transform3.mulPoint(tr, foreHandle, v0);
        Transform3.mulPoint(tr, rearHandle, v1);
        Transform3.mulPoint(tr, coord, v2);

        this.bezierVertexImpl(
            v0.x, v0.y, v0.z,
            v1.x, v1.y, v1.z,
            v2.x, v2.y, v2.z);
        this.endShape(PConstants.CLOSE);
      } else {
        this.endShape(PConstants.OPEN);
      }
      this.popStyle();
    }
  }

  /**
   * Draws a 3D mesh entity.
   *
   * @param entity the mesh entity
   */
  public void shape ( final MeshEntity3 entity ) {

    final Transform3 tr = entity.transform;
    final List < Mesh3 > meshes = entity.meshes;
    final Iterator < Mesh3 > meshItr = meshes.iterator();

    final Vec3 v = new Vec3();
    final Vec3 vn = new Vec3();

    while ( meshItr.hasNext() ) {
      this.drawMesh3(meshItr.next(), tr, v, vn);
    }
  }

  /**
   * Draws a 3D mesh entity.
   *
   * @param entity   the mesh entity
   * @param material the material
   */
  public void shape (
      final MeshEntity3 entity,
      final MaterialPImage material ) {

    final Transform3 tr = entity.transform;
    final List < Mesh3 > meshes = entity.meshes;
    final Iterator < Mesh3 > meshItr = meshes.iterator();

    final Vec3 v = new Vec3();
    final Vec2 vt = new Vec2();
    final Vec3 vn = new Vec3();

    this.pushStyle();
    this.noStroke();
    while ( meshItr.hasNext() ) {
      this.drawMesh3(meshItr.next(), tr, material, v, vt, vn);
    }
    this.popStyle();
  }

  /**
   * Draws a 3D mesh entity.
   *
   * @param entity    the mesh entity
   * @param materials the materials
   */
  public void shape (
      final MeshEntity3 entity,
      final MaterialPImage[] materials ) {

    final Transform3 tr = entity.transform;
    final List < Mesh3 > meshes = entity.meshes;
    final Iterator < Mesh3 > meshItr = meshes.iterator();

    final Vec3 v = new Vec3();
    final Vec2 vt = new Vec2();
    final Vec3 vn = new Vec3();

    this.pushStyle();
    this.noStroke();
    while ( meshItr.hasNext() ) {
      final Mesh3 mesh = meshItr.next();
      final MaterialPImage mat = materials[mesh.materialIndex];
      this.drawMesh3(mesh, tr, mat, v, vt, vn);
    }
    this.popStyle();
  }

  /**
   * Draws a 3D mesh entity.
   *
   * @param entity   the mesh entity
   * @param material the material
   */
  public void shape (
      final MeshEntity3 entity,
      final MaterialSolid material ) {

    this.pushStyle();
    this.material(material);
    this.shape(entity);
    this.popStyle();
  }

  /**
   * Draws a mesh entity.
   *
   * @param entity    the mesh entity
   * @param materials the materials
   */
  public void shape (
      final MeshEntity3 entity,
      final MaterialSolid[] materials ) {

    final Transform3 tr = entity.transform;
    final List < Mesh3 > meshes = entity.meshes;
    final Iterator < Mesh3 > meshItr = meshes.iterator();

    final Vec3 v = new Vec3();
    final Vec3 vn = new Vec3();

    while ( meshItr.hasNext() ) {
      final Mesh3 mesh = meshItr.next();
      this.pushStyle();
      this.material(materials[mesh.materialIndex]);
      this.drawMesh3(mesh, tr, v, vn);
      this.popStyle();
    }
  }

  /**
   * This parent function attempts to translate the text and then undo
   * the translation. It's better to acknowledge that text is 2D, not
   * 3D, in nature.
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
   * This parent function attempts to translate the text and then undo
   * the translation. It's better to acknowledge that text is 2D, not
   * 3D, in nature.
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
   * Toggles the depth sorting boolean and flushes the geometry.
   */
  public void toggleDepthSorting ( ) {

    this.flush();
    this.isDepthSortingEnabled = !this.isDepthSortingEnabled;
  }

  /**
   * Returns the string representation of this renderer.
   *
   * @return the string
   */
  @Override
  public String toString ( ) { return Up3.PATH_STR; }

  /**
   * Applies a transform to the renderer's matrix.
   *
   * @param tr3 the transform
   */
  public void transform ( final Transform3 tr3 ) {

    this.transform(tr3, IUp.DEFAULT_ORDER);
  }

  /**
   * Applies a transform to the renderer's matrix.
   *
   * @param tr3   the transform
   * @param order the transform order
   */
  public void transform (
      final Transform3 tr3,
      final TransformOrder order ) {

    final Vec3 tr3Scl = tr3.getScale(new Vec3());
    final Vec3 tr3Loc = tr3.getLocation(new Vec3());
    final Quaternion tr3Rot = tr3.getRotation(new Quaternion());

    switch ( order ) {

      case RST:

        this.rotate(tr3Rot);
        this.scaleImpl(tr3Scl.x, tr3Scl.y, tr3Scl.z);
        this.translateImpl(tr3Loc.x, tr3Loc.y, tr3Loc.z);

        return;

      case RTS:

        this.rotate(tr3Rot);
        this.translateImpl(tr3Loc.x, tr3Loc.y, tr3Loc.z);
        this.scaleImpl(tr3Scl.x, tr3Scl.y, tr3Scl.z);

        return;

      case SRT:

        this.scaleImpl(tr3Scl.x, tr3Scl.y, tr3Scl.z);
        this.rotate(tr3Rot);
        this.translateImpl(tr3Loc.x, tr3Loc.y, tr3Loc.z);

        return;

      case STR:

        this.scaleImpl(tr3Scl.x, tr3Scl.y, tr3Scl.z);
        this.translateImpl(tr3Loc.x, tr3Loc.y, tr3Loc.z);
        this.rotate(tr3Rot);

        return;

      case TSR:

        this.translateImpl(tr3Loc.x, tr3Loc.y, tr3Loc.z);
        this.scaleImpl(tr3Scl.x, tr3Scl.y, tr3Scl.z);
        this.rotate(tr3Rot);

        return;

      case TRS:

      default:

        this.translateImpl(tr3Loc.x, tr3Loc.y, tr3Loc.z);
        this.rotate(tr3Rot);
        this.scaleImpl(tr3Scl.x, tr3Scl.y, tr3Scl.z);

        return;
    }
  }

  /**
   * Translates the renderer by a vector.
   *
   * @param v the vector
   */
  public void translate ( final Vec3 v ) {

    this.translateImpl(v.x, v.y, v.z);
  }

  /**
   * Trucks the camera, moving it on its local x axis, left or right.
   * This is done by multiplying the x magnitude by the camera inverse,
   * then adding the local coordinates to both the camera location and
   * look target.
   *
   * @param x the x magnitude
   */
  public void truck ( final float x ) {

    final float w = this.cameraInv.m30 * x + this.cameraInv.m33;
    if ( w != 0.0f ) {
      final float wInv = 1.0f / w;
      final float xLocal = this.cameraInv.m00 * x * wInv;
      final float yLocal = this.cameraInv.m10 * x * wInv;
      final float zLocal = this.cameraInv.m20 * x * wInv;

      this.cameraX += xLocal;
      this.cameraY += yLocal;
      this.cameraZ += zLocal;

      this.lookTarget.x += xLocal;
      this.lookTarget.y += yLocal;
      this.lookTarget.z += zLocal;
    }
  }

  /**
   * Adds another vertex to a shape between the beginShape and endShape
   * commands.
   *
   * @param v the coordinate
   */
  @Override
  public void vertex ( final Vec3 v ) {

    this.vertexImpl(v.x, v.y, v.z, this.textureU, this.textureV);
  }

  /**
   * Adds another vertex to a shape between the beginShape and endShape
   * commands. Includes texture coordinates.
   *
   * @param v  the coordinate
   * @param vt the texture coordinate
   */
  public void vertex ( final Vec3 v, final Vec2 vt ) {

    this.vertexImpl(v.x, v.y, v.z, vt.x, vt.y);
  }
}

package com.behreajj.camzup.friendly;

import com.behreajj.camzup.core.*;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PMatrix3D;
import processing.opengl.PGraphicsOpenGL;

import java.util.Iterator;

/**
 * An abstract parent class for 3D renderers.
 */
public abstract class Up3 extends UpOgl implements IUp3, ITextDisplay2 {

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
    @SuppressWarnings("hiding")
    public static final String PATH_STR = "com.behreajj.camzup.friendly.Up3";

    /**
     * A vector to store the x axis (first column) when creating a camera
     * look-at matrix.
     */
    protected final Vec3 i = Vec3.right(new Vec3());

    /**
     * A vector to store the y axis (second column) when creating a camera
     * look-at matrix.
     */
    protected final Vec3 j = Vec3.forward(new Vec3());

    /**
     * A vector to store the z axis (third column) when creating a camera
     * look-at matrix.
     */
    protected final Vec3 k = Vec3.up(new Vec3());

    /**
     * A vector to store the look direction when creating a camera
     * look-at matrix.
     */
    protected final Vec3 lookDir = Vec3.forward(new Vec3());

    /**
     * A vector to store the target at which a camera looks.
     */
    protected final Vec3 lookTarget = new Vec3();

    /**
     * The reference or "world" up vector against which a camera look-at matrix
     * is created.
     */
    protected final Vec3 refUp = Vec3.up(new Vec3());

    /**
     * The default constructor.
     */
    protected Up3() {
    }

    /**
     * A constructor for manually initializing the renderer.
     *
     * @param width     renderer width
     * @param height    renderer height
     * @param parent    parent applet
     * @param path      applet path
     * @param isPrimary is the renderer primary
     */
    protected Up3(final int width, final int height, final PApplet parent,
        final String path, final boolean isPrimary) {

        super(width, height, parent, path, isPrimary);
    }

    /**
     * Begins the heads-up display section of the sketch. Turns off lighting,
     * disables depth testing and depth masking. Sets the camera origin to the
     * center of the sketch and establishes an orthographic projection.
     */
    public void beginHud() {

        /*
         * Loose camera variables (cameraX, cameraY, cameraZ, refUp, etc.) should
         * not be changed, as that would impact default arguments for camera();
         * method. This is intended to be a temporary element within draw!
         */

        // QUERY Should there be a boolean flag to signal HUD has begun so that a
        // warning is thrown if beginHud and endHud aren't called in pairs?

        final float z = -this.height * IUp.DEFAULT_CAM_DIST_FAC;
        final float w = 1.0f / Math.max(128, this.width);
        final float h = 1.0f / Math.max(128, this.height);
        final float d = 1.0f / (IUp.DEFAULT_FAR_CLIP - IUp.DEFAULT_NEAR_CLIP);
        final float am00 = w + w;
        final float am11 = h + h;
        final float am22 = -(d + d);
        final float am23 = -d * (IUp.DEFAULT_FAR_CLIP + IUp.DEFAULT_NEAR_CLIP);

        this.disableDepthTest();
        this.disableDepthMask();
        this.noLights();

        this.camera.set(1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, z, 0.0f, 0.0f, 0.0f, 1.0f);
        this.cameraInv.set(1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, -z, 0.0f, 0.0f, 0.0f, 1.0f);

        this.modelview.set(1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, z, 0.0f, 0.0f, 0.0f, 1.0f);
        this.modelviewInv.set(1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, -z, 0.0f, 0.0f, 0.0f, 1.0f);

        this.projection.set(am00, 0.0f, 0.0f, 0.0f, 0.0f, am11, 0.0f, 0.0f, 0.0f,
            0.0f, am22, am23, 0.0f, 0.0f, 0.0f, 1.0f);
        this.projmodelview.set(am00, 0.0f, 0.0f, 0.0f, 0.0f, am11, 0.0f, 0.0f,
            0.0f, 0.0f, am22, am22 * z + am23, 0.0f, 0.0f, 0.0f, 1.0f);
    }

    /**
     * Draws a single Bézier curve.
     *
     * @param ap0 the first anchor point
     * @param cp0 the first control point
     * @param cp1 the second control point
     * @param ap1 the second anchor point
     */
    @Override
    public void bezier(final Vec3 ap0, final Vec3 cp0, final Vec3 cp1,
        final Vec3 ap1) {

        this.bezier(
            ap0.x, ap0.y, ap0.z,
            cp0.x, cp0.y, cp0.z,
            cp1.x, cp1.y, cp1.z,
            ap1.x, ap1.y, ap1.z);
    }

    /**
     * Draws a Bézier vertex with three vectors: the following control point,
     * the rear control point of the ensuing point, and the ensuing anchor
     * point.
     *
     * @param cp0 the first control point
     * @param cp1 the second control point
     * @param ap1 the next anchor point
     */
    @Override
    public void bezierVertex(final Vec3 cp0, final Vec3 cp1, final Vec3 ap1) {

        this.bezierVertexImpl(
            cp0.x, cp0.y, cp0.z,
            cp1.x, cp1.y, cp1.z,
            ap1.x, ap1.y, ap1.z);
    }

    /**
     * Draws a bounding volume.
     *
     * @param b the bounds
     */
    @Override
    public void bounds(final Bounds3 b) {

        final Vec3 min = b.min;
        final float x0 = min.x;
        final float y0 = min.y;
        final float z0 = min.z;

        final Vec3 max = b.max;
        final float x1 = max.x;
        final float y1 = max.y;
        final float z1 = max.z;

        /* Left. */
        this.beginShape(PConstants.POLYGON);
        this.normalPerShape(-1.0f, 0.0f, 0.0f);
        this.vertexImpl(x0, y0, z0, 1.0f, 1.0f);
        this.vertexImpl(x0, y0, z1, 1.0f, 0.0f);
        this.vertexImpl(x0, y1, z1, 0.0f, 0.0f);
        this.vertexImpl(x0, y1, z0, 0.0f, 1.0f);
        this.endShape(PConstants.CLOSE);

        /* Right. */
        this.beginShape(PConstants.POLYGON);
        this.normalPerShape(1.0f, 0.0f, 0.0f);
        this.vertexImpl(x1, y1, z0, 1.0f, 1.0f);
        this.vertexImpl(x1, y1, z1, 1.0f, 0.0f);
        this.vertexImpl(x1, y0, z1, 0.0f, 0.0f);
        this.vertexImpl(x1, y0, z0, 0.0f, 1.0f);
        this.endShape(PConstants.CLOSE);

        /* Back. */
        this.beginShape(PConstants.POLYGON);
        this.normalPerShape(0.0f, -1.0f, 0.0f);
        this.vertexImpl(x1, y0, z0, 1.0f, 1.0f);
        this.vertexImpl(x1, y0, z1, 1.0f, 0.0f);
        this.vertexImpl(x0, y0, z1, 0.0f, 0.0f);
        this.vertexImpl(x0, y0, z0, 0.0f, 1.0f);
        this.endShape(PConstants.CLOSE);

        /* Front. */
        this.beginShape(PConstants.POLYGON);
        this.normalPerShape(0.0f, 1.0f, 0.0f);
        this.vertexImpl(x0, y1, z0, 1.0f, 1.0f);
        this.vertexImpl(x0, y1, z1, 1.0f, 0.0f);
        this.vertexImpl(x1, y1, z1, 0.0f, 0.0f);
        this.vertexImpl(x1, y1, z0, 0.0f, 1.0f);
        this.endShape(PConstants.CLOSE);

        /* Down. */
        this.beginShape(PConstants.POLYGON);
        this.normalPerShape(0.0f, 0.0f, -1.0f);
        this.vertexImpl(x0, y1, z0, 0.0f, 1.0f);
        this.vertexImpl(x1, y1, z0, 1.0f, 1.0f);
        this.vertexImpl(x1, y0, z0, 1.0f, 0.0f);
        this.vertexImpl(x0, y0, z0, 0.0f, 0.0f);
        this.endShape(PConstants.CLOSE);

        /* Up. */
        this.beginShape(PConstants.POLYGON);
        this.normalPerShape(0.0f, 0.0f, 1.0f);
        this.vertexImpl(x1, y1, z1, 0.0f, 1.0f);
        this.vertexImpl(x0, y1, z1, 1.0f, 1.0f);
        this.vertexImpl(x0, y0, z1, 1.0f, 0.0f);
        this.vertexImpl(x1, y0, z1, 0.0f, 0.0f);
        this.endShape(PConstants.CLOSE);
    }

    /**
     * Places the camera on the negative x axis, such that it is looking East
     * toward the world origin.
     */
    public abstract void camEast();

    /**
     * Places camera on the axis perpendicular to its world up axis such that
     * it is looking North toward the world origin.
     */
    public abstract void camNorth();

    /**
     * Places camera on the axis perpendicular to its world up axis such that
     * it is looking South toward the world origin.
     */
    public abstract void camSouth();

    /**
     * Places the camera on the positive x axis, such that it is looking West
     * toward the world origin.
     */
    public abstract void camWest();

    /**
     * Draws a curve between four vectors.
     *
     * @param a the first vector
     * @param b the second vector
     * @param c the third vector
     * @param d the fourth vector
     */
    public void curve(final Vec3 a, final Vec3 b, final Vec3 c,
        final Vec3 d) {

        super.curve(a.x, a.y, a.z, b.x, b.y, b.z, c.x, c.y, c.z, d.x, d.y, d.z);
    }

    /**
     * Draws a curve vertex to a vector.
     *
     * @param a the vector.
     */
    public void curveVertex(final Vec3 a) {

        super.curveVertex(a.x, a.y, a.z);
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
    public void defaultPerspective() {

        this.cameraAspect = this.defCameraAspect = IUp.DEFAULT_ASPECT;
        this.cameraFOV = this.defCameraFOV = IUp.DEFAULT_FOV;
        this.cameraNear = this.defCameraNear = IUp.DEFAULT_NEAR_CLIP;
        this.cameraFar = this.defCameraFar = IUp.DEFAULT_FAR_CLIP;

        this.ortho();
    }

    /**
     * Dollies the camera, moving it on its local z axis, backward or forward.
     * This is done by multiplying the z magnitude by the camera inverse, then
     * adding the local coordinates to the camera location and look target.
     *
     * @param z the z magnitude
     */
    @Override
    public void dolly(final float z) {

        final float w = this.cameraInv.m32 * z + this.cameraInv.m33;
        if (w != 0.0f) {
            final float zwInv = z / w;
            final float xLocal = this.cameraInv.m02 * zwInv;
            final float yLocal = this.cameraInv.m12 * zwInv;
            final float zLocal = this.cameraInv.m22 * zwInv;

            this.camera(this.cameraX + xLocal, this.cameraY + yLocal, this.cameraZ
                    + zLocal, this.lookTarget.x + xLocal, this.lookTarget.y + yLocal,
                this.lookTarget.z + zLocal, this.refUp.x, this.refUp.y,
                this.refUp.z);
        }
    }

    /**
     * Concludes the heads-up display section of the sketch by enabling the
     * depth mask and depth test. This should happen at the end of a draw call;
     * any display following this method should call camera and projection
     * methods and set lights.
     */
    public void endHud() {

        this.enableDepthMask();
        this.enableDepthTest();
    }

    /**
     * Gets the eye distance of the camera.
     *
     * @return the eye distance
     */
    @Override
    public float getEyeDist() {
        return this.eyeDist;
    }

    /**
     * Sets the eye distance of the camera. The value is usually set by the
     * camera function, and to calculate the far clip plane of perspective
     * functions, but is exposed for public access.
     *
     * @param ed the eye distance
     */
    public void setEyeDist(final float ed) {
        this.eyeDist = ed;
    }

    /**
     * Gets the x axis of the camera transform.
     *
     * @param target the output vector
     * @return the x axis
     */
    public Vec3 getI(final Vec3 target) {
        return target.set(this.i);
    }

    /**
     * Gets the y axis of the camera transform.
     *
     * @param target the output vector
     * @return the y axis
     */
    public Vec3 getJ(final Vec3 target) {
        return target.set(this.j);
    }

    /**
     * Gets the z axis of the camera transform.
     *
     * @param target the output vector
     * @return the z axis
     */
    public Vec3 getK(final Vec3 target) {
        return target.set(this.k);
    }

    /**
     * Gets the renderer camera's 3D location.
     *
     * @param target the output vector
     * @return the location
     */
    @Override
    public Vec3 getLocation(final Vec3 target) {

        return target.set(this.cameraX, this.cameraY, this.cameraZ);
    }

    /**
     * Gets the direction in which the camera is looking.
     *
     * @param target the output vector
     * @return the look direction
     */
    public Vec3 getLookDir(final Vec3 target) {

        return target.set(this.lookDir);
    }

    /**
     * Gets the point at which the camera is looking.
     *
     * @param target the output vector
     * @return the look target
     */
    public Vec3 getLookTarget(final Vec3 target) {

        return target.set(this.lookTarget);
    }

    /**
     * Gets the camera's look target x.
     *
     * @return the look target x
     */
    @Override
    public float getLookTargetX() {
        return this.lookTarget.x;
    }

    /**
     * Gets the camera's look target y.
     *
     * @return the look target y
     */
    @Override
    public float getLookTargetY() {
        return this.lookTarget.y;
    }

    /**
     * Gets the camera's look target z.
     *
     * @return the look target z
     */
    @Override
    public float getLookTargetZ() {
        return this.lookTarget.z;
    }

    /**
     * Gets the renderer model view matrix.
     *
     * @return the model view
     */
    @Override
    public PMatrix3D getMatrix() {
        return this.getMatrix(new PMatrix3D());
    }

    /**
     * Gets the reference up axis of the camera.
     *
     * @param target the output vector
     * @return the reference up
     */
    public Vec3 getRefUp(final Vec3 target) {

        return target.set(this.refUp);
    }

    /**
     * Gets the width and height of the renderer as a vector.
     *
     * @return the size
     */
    @Override
    public Vec2 getSize(final Vec2 target) {

        return target.set(this.width, this.height);
    }

    /**
     * Gets the renderer's size. The z component is set to zero.
     *
     * @param target the output vector
     * @return the size
     */
    public Vec3 getSize(final Vec3 target) {

        return target.set(this.width, this.height, 0.0f);
    }

    /**
     * Draws the world orientation as three gimbals.
     */
    public void gimbal() {

        this.gimbal(IUp.DEFAULT_IJK_LINE_FAC * Math.min(this.width,
            this.height));
    }

    /**
     * Draws the world orientation as three gimbals. Uses a default stroke
     * weight.
     *
     * @param radius the radius
     */
    public void gimbal(final float radius) {

        this.gimbal(radius, IUp.DEFAULT_IJK_SWEIGHT);
    }

    /**
     * Draws the world orientation as three gimbals. Colors the axes according
     * to {@link IUp#DEFAULT_I_COLOR}, {@link IUp#DEFAULT_J_COLOR} and
     * {@link IUp#DEFAULT_K_COLOR}.
     *
     * @param radius the radius
     * @param sw     the stroke weight
     */
    public void gimbal(final float radius, final float sw) {

        this.gimbal(radius, sw, IUp.DEFAULT_I_COLOR, IUp.DEFAULT_J_COLOR,
            IUp.DEFAULT_K_COLOR);
    }

    /**
     * Draws the world orientation as three gimbals.
     *
     * @param radius the radius
     * @param sw     the stroke weight
     * @param xColor the color of the x ring
     * @param yColor the color of the y ring
     * @param zColor the color of the z ring
     */
    public void gimbal(
        final float radius,
        final float sw,
        final int xColor,
        final int yColor,
        final int zColor) {

        final float r = Math.max(radius, Utils.EPSILON);
        final float rk = r * Curve.KAPPA;
        final float swEmphasis = sw * 6.75f;

        this.disableDepthMask();
        this.disableDepthTest();
        this.pushStyle();
        this.strokeWeight(sw);
        this.noFill();

        this.stroke(zColor);
        this.beginShape(PConstants.POLYGON);
        this.normalPerShape(0.0f, 1.0f, 0.0f);
        this.vertexImpl(0.0f, 0.0f, r, this.textureU, this.textureV);
        this.bezierVertexImpl(rk, 0.0f, r, r, 0.0f, rk, r, 0.0f, 0.0f);
        this.bezierVertexImpl(r, 0.0f, -rk, rk, 0.0f, -r, 0.0f, 0.0f, -r);
        this.bezierVertexImpl(-rk, 0.0f, -r, -r, 0.0f, -rk, -r, 0.0f, 0.0f);
        this.bezierVertexImpl(-r, 0.0f, rk, -rk, 0.0f, r, 0.0f, 0.0f, r);
        this.endShape(PConstants.CLOSE);

        this.stroke(yColor);
        this.beginShape(PConstants.POLYGON);
        this.normalPerShape(1.0f, 0.0f, 0.0f);
        this.vertexImpl(0.0f, r, 0.0f, this.textureU, this.textureV);
        this.bezierVertexImpl(0.0f, r, rk, 0.0f, rk, r, 0.0f, 0.0f, r);
        this.bezierVertexImpl(0.0f, -rk, r, 0.0f, -r, rk, 0.0f, -r, 0.0f);
        this.bezierVertexImpl(0.0f, -r, -rk, 0.0f, -rk, -r, 0.0f, 0.0f, -r);
        this.bezierVertexImpl(0.0f, rk, -r, 0.0f, r, -rk, 0.0f, r, 0.0f);
        this.endShape(PConstants.CLOSE);

        this.stroke(xColor);
        this.beginShape(PConstants.POLYGON);
        this.normalPerShape(0.0f, 0.0f, 1.0f);
        this.vertexImpl(r, 0.0f, 0.0f, this.textureU, this.textureV);
        this.bezierVertexImpl(r, rk, 0.0f, rk, r, 0.0f, 0.0f, r, 0.0f);
        this.bezierVertexImpl(-rk, r, 0.0f, -r, rk, 0.0f, -r, 0.0f, 0.0f);
        this.bezierVertexImpl(-r, -rk, 0.0f, -rk, -r, 0.0f, 0.0f, -r, 0.0f);
        this.bezierVertexImpl(rk, -r, 0.0f, r, -rk, 0.0f, r, 0.0f, 0.0f);
        this.endShape(PConstants.CLOSE);

        this.strokeWeight(swEmphasis);
        this.stroke(zColor);
        this.pointImpl(0.0f, 0.0f, r);
        this.stroke(yColor);
        this.pointImpl(0.0f, r, 0.0f);
        this.stroke(xColor);
        this.pointImpl(r, 0.0f, 0.0f);

        this.popStyle();
        this.enableDepthTest();
        this.enableDepthMask();
    }

    /**
     * Draws a transform orientation as three gimbals. Colors the axes
     * according to {@link IUp#DEFAULT_I_COLOR}, {@link IUp#DEFAULT_J_COLOR}
     * and {@link IUp#DEFAULT_K_COLOR}.
     *
     * @param tr     the transform
     * @param radius the radius
     * @param sw     the stroke weight
     */
    public void gimbal(final Transform3 tr, final float radius,
        final float sw) {

        this.gimbal(tr, radius, sw, IUp.DEFAULT_I_COLOR, IUp.DEFAULT_J_COLOR,
            IUp.DEFAULT_K_COLOR);
    }

    /**
     * Draws a transform orientation as three gimbals.
     *
     * @param tr     the transform
     * @param radius the radius
     * @param sw     the stroke weight
     * @param xColor the color of the x ring
     * @param yColor the color of the y ring
     * @param zColor the color of the z ring
     */
    public void gimbal(final Transform3 tr, final float radius, final float sw,
        final int xColor, final int yColor, final int zColor) {

        this.pushMatrix();
        this.transform(tr, TransformOrder.TR);
        this.gimbal(radius * Transform3.maxDimension(tr), sw, xColor, yColor,
            zColor);
        this.popMatrix();
    }

    /**
     * Displays the handles of a curve entity. The stroke weight is determined
     * by {@link IUp#DEFAULT_STROKE_WEIGHT}, {@value IUp#DEFAULT_STROKE_WEIGHT}
     * .
     *
     * @param ce the curve entity
     */
    public void handles(final CurveEntity3 ce) {

        this.handles(ce, IUp.DEFAULT_STROKE_WEIGHT);
    }

    /**
     * Displays the handles of a curve entity.
     * <ul>
     * <li>The color for lines between handles defaults to
     * {@link IUp#DEFAULT_HANDLE_COLOR};</li>
     * <li>the rear handle point, to
     * {@link IUp#DEFAULT_HANDLE_REAR_COLOR};</li>
     * <li>the fore handle point, to
     * {@link IUp#DEFAULT_HANDLE_FORE_COLOR};</li>
     * <li>the coordinate point, to
     * {@link IUp#DEFAULT_HANDLE_COORD_COLOR}.</li>
     * </ul>
     *
     * @param ce the curve entity
     * @param sw the stroke weight
     */
    public void handles(final CurveEntity3 ce, final float sw) {

        this.handles(ce, sw, IUp.DEFAULT_HANDLE_COLOR,
            IUp.DEFAULT_HANDLE_REAR_COLOR, IUp.DEFAULT_HANDLE_FORE_COLOR,
            IUp.DEFAULT_HANDLE_COORD_COLOR);
    }

    /**
     * Displays the handles of a curve entity.
     *
     * @param ce         the curve entity
     * @param sw         the stroke weight
     * @param lineColor  the color of handle lines
     * @param rearColor  the color of the rear handle
     * @param foreColor  the color of the fore handle
     * @param coordColor the color of the coordinate
     */
    public void handles(final CurveEntity3 ce, final float sw,
        final int lineColor, final int rearColor, final int foreColor,
        final int coordColor) {

        final float swRear = sw * IUp.HANDLE_REAR_WEIGHT;
        final float swFore = sw * IUp.HANDLE_FORE_WEIGHT;
        final float swCoord = sw * IUp.HANDLE_COORD_WEIGHT;

        final Transform3 tr = ce.transform;
        final Vec3 rh = new Vec3();
        final Vec3 co = new Vec3();
        final Vec3 fh = new Vec3();

        this.disableDepthMask();
        this.disableDepthTest();

        this.pushStyle();

        for (final Curve3 curve : ce) {
            for (final Knot3 knot : curve) {
                Transform3.mulPoint(tr, knot.rearHandle, rh);
                Transform3.mulPoint(tr, knot.coord, co);
                Transform3.mulPoint(tr, knot.foreHandle, fh);

                this.strokeWeight(sw);
                this.stroke(lineColor);

                this.lineImpl(rh.x, rh.y, rh.z, co.x, co.y, co.z);
                this.lineImpl(co.x, co.y, co.z, fh.x, fh.y, fh.z);

                this.strokeWeight(swRear);
                this.stroke(rearColor);
                this.pointImpl(rh.x, rh.y, rh.z);

                this.strokeWeight(swCoord);
                this.stroke(coordColor);
                this.pointImpl(co.x, co.y, co.z);

                this.strokeWeight(swFore);
                this.stroke(foreColor);
                this.pointImpl(fh.x, fh.y, fh.z);
            }
        }

        this.popStyle();
        this.enableDepthTest();
        this.enableDepthMask();
    }

    /**
     * Draws a 3D image entity.
     *
     * @param entity the text entity
     */
    public void image(final ImageEntity3 entity) {

        this.shape(entity, entity.material);
    }

    /**
     * Returns whether the renderer is 2D (false).
     */
    @Override
    public boolean is2D() {
        return false;
    }

    /**
     * Returns whether the renderer is 3D (true).
     */
    @Override
    public boolean is3D() {
        return true;
    }

    /**
     * Gets whether depth sorting is enabled.
     *
     * @return the depth sorting
     */
    public boolean isDepthSorted() {
        return this.isDepthSortingEnabled;
    }

    /**
     * Draws a line between two coordinates.
     *
     * @param orig the origin coordinate
     * @param dest the destination coordinate
     */
    @Override
    public void line(final Vec3 orig, final Vec3 dest) {

        this.lineImpl(orig.x, orig.y, orig.z, dest.x, dest.y, dest.z);
    }

    /**
     * Sets the renderer's stroke, stroke weight and fill to a material's. Due
     * to stroke flickering in perspective, currently a material with a fill
     * may not also use a stroke.
     *
     * @param material the material
     */
    @Override
    public void material(final MaterialSolid material) {

        if (material.useFill) {
            this.noStroke();
            this.fill(material.fill);
        } else {
            this.noFill();
            if (material.useStroke) {
                this.strokeWeight(material.strokeWeight);
                this.stroke(material.stroke);
            } else {
                this.noStroke();
            }
        }
    }

    /**
     * Moves the camera by the given vector then updates the camera.
     *
     * @param x the vector x
     * @param y the vector y
     * @param z the vector z
     * @see IUp3#moveTo(float, float, float)
     */
    @Override
    public void moveBy(final float x, final float y, final float z) {

        this.moveByLocal(x, y, z);
    }

    /**
     * Moves the camera by the given vector then updates the camera.
     *
     * @param v the vector
     */
    @Override
    public void moveBy(final Vec3 v) {
        this.moveByLocal(v.x, v.y, v.z);
    }

    /**
     * Moves the camera by a vector relative to its orientation. Causes the
     * camera to orbit around the locus at which it is looking.
     *
     * @param x the vector x
     * @param y the vector y
     * @param z the vector z
     */
    @Override
    public void moveByLocal(final float x, final float y, final float z) {

        final PMatrix3D m = this.cameraInv;
        final float w = m.m30 * x + m.m31 * y + m.m32 * z + m.m33;
        if (w != 0.0f) {
            final float wInv = 1.0f / w;
            final float xLocal = (m.m00 * x + m.m01 * y + m.m02 * z) * wInv;
            final float yLocal = (m.m10 * x + m.m11 * y + m.m12 * z) * wInv;
            final float zLocal = (m.m20 * x + m.m21 * y + m.m22 * z) * wInv;

            this.moveTo(
                this.cameraX + xLocal,
                this.cameraY + yLocal,
                this.cameraZ + zLocal);
        }
    }

    /**
     * Sets the current normal and normal mode used by the renderer.
     *
     * @param nx the x component
     * @param ny the y component
     * @param nz the z component
     */
    @Override
    public void normal(final float nx, final float ny, final float nz) {

        this.normalX = nx;
        this.normalY = ny;
        this.normalZ = nz;

        if (this.shape != 0) {
            if (this.normalMode == PGraphics.NORMAL_MODE_AUTO) {
                /* One normal per begin or end shape. */
                this.normalMode = PGraphics.NORMAL_MODE_SHAPE;
            } else if (this.normalMode == PGraphics.NORMAL_MODE_SHAPE) {
                /* A separate normal for each vertex. */
                this.normalMode = PGraphics.NORMAL_MODE_VERTEX;
            }
        }
    }

    /**
     * Draws the world origin. The length of the axes is determined by
     * multiplying {@link IUp#DEFAULT_IJK_LINE_FAC},
     * {@value IUp#DEFAULT_IJK_LINE_FAC}, with the renderer's short edge.
     */
    @Override
    public void origin() {

        this.origin(IUp.DEFAULT_IJK_LINE_FAC * Math.min(this.width,
            this.height));
    }

    /**
     * Draws the world origin. The axes stroke weight defaults to
     * {@link IUp#DEFAULT_IJK_SWEIGHT}, {@value IUp#DEFAULT_IJK_SWEIGHT}.
     *
     * @param lineLength the line length
     */
    public void origin(final float lineLength) {

        this.origin(lineLength, IUp.DEFAULT_IJK_SWEIGHT);
    }

    /**
     * Draws the world origin. Colors the axes according to
     * {@link IUp#DEFAULT_I_COLOR}, {@link IUp#DEFAULT_J_COLOR} and
     * {@link IUp#DEFAULT_K_COLOR}.
     *
     * @param lineLength the line length
     * @param sw         the stroke weight
     */
    public void origin(final float lineLength, final float sw) {

        this.origin(lineLength, sw, IUp.DEFAULT_I_COLOR, IUp.DEFAULT_J_COLOR,
            IUp.DEFAULT_K_COLOR);
    }

    /**
     * Draws the world origin.
     *
     * @param lineLength the line length
     * @param sw         the stroke weight
     * @param xColor     the color of the x axis
     * @param yColor     the color of the y axis
     * @param zColor     the color of the z axis
     */
    public void origin(final float lineLength, final float sw,
        final int xColor, final int yColor, final int zColor) {

        final float vl = Math.max(lineLength, Utils.EPSILON);

        this.disableDepthMask();
        this.disableDepthTest();
        this.pushStyle();
        this.strokeWeight(sw);

        this.stroke(zColor);
        this.lineImpl(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, vl);

        this.stroke(yColor);
        this.lineImpl(0.0f, 0.0f, 0.0f, 0.0f, vl, 0.0f);

        this.stroke(xColor);
        this.lineImpl(0.0f, 0.0f, 0.0f, vl, 0.0f, 0.0f);

        this.popStyle();
        this.enableDepthTest();
        this.enableDepthMask();
    }

    /**
     * Draws a transform origin. Colors the axes according to
     * {@link IUp#DEFAULT_I_COLOR}, {@link IUp#DEFAULT_J_COLOR} and
     * {@link IUp#DEFAULT_K_COLOR}.
     *
     * @param tr         the transform
     * @param lineLength the line length
     * @param sw         the stroke weight
     */
    public void origin(final Transform3 tr, final float lineLength,
        final float sw) {

        this.origin(tr, lineLength, sw, IUp.DEFAULT_I_COLOR, IUp.DEFAULT_J_COLOR,
            IUp.DEFAULT_K_COLOR);
    }

    /**
     * Draws a transform origin.
     *
     * @param tr         the transform
     * @param lineLength the line length
     * @param sw         the stroke weight
     * @param xColor     the color of the x axis
     * @param yColor     the color of the y axis
     * @param zColor     the color of the z axis
     */
    public void origin(final Transform3 tr, final float lineLength,
        final float sw, final int xColor, final int yColor, final int zColor) {

        // QUERY: Simplify this to follow the pattern established by
        // gimbal, where matrix is pushed onto stack? There might've been a reason
        // not to do it that way in the first place.

        final Vec3 origin = new Vec3();
        final Vec3 right = new Vec3();
        final Vec3 forward = new Vec3();
        final Vec3 up = new Vec3();
        tr.getLocation(origin);
        tr.getAxes(right, forward, up);

        final float vl = Math.max(lineLength, Utils.EPSILON);

        Vec3.mul(right, vl, right);
        Vec3.add(right, origin, right);
        Vec3.mul(forward, vl, forward);
        Vec3.add(forward, origin, forward);
        Vec3.mul(up, vl, up);
        Vec3.add(up, origin, up);

        this.disableDepthMask();
        this.disableDepthTest();
        this.pushStyle();
        this.strokeWeight(sw);

        this.stroke(zColor);
        this.lineImpl(origin.x, origin.y, origin.z, up.x, up.y, up.z);

        this.stroke(yColor);
        this.lineImpl(origin.x, origin.y, origin.z, forward.x, forward.y,
            forward.z);

        this.stroke(xColor);
        this.lineImpl(origin.x, origin.y, origin.z, right.x, right.y, right.z);

        this.popStyle();
        this.enableDepthTest();
        this.enableDepthMask();

    }

    /**
     * Boom or pedestal the camera, moving it on its local y axis, up or down.
     * This is done by multiplying the y magnitude by the camera inverse, then
     * adding the local coordinates to both the camera location and look
     * target.
     *
     * @param y the y magnitude
     */
    @Override
    public void pedestal(final float y) {

        final float w = this.cameraInv.m31 * y + this.cameraInv.m33;
        if (w != 0.0f) {
            final float ywInv = y / w;
            final float xLocal = this.cameraInv.m01 * ywInv;
            final float yLocal = this.cameraInv.m11 * ywInv;
            final float zLocal = this.cameraInv.m21 * ywInv;

            this.camera(this.cameraX + xLocal, this.cameraY + yLocal, this.cameraZ
                    + zLocal, this.lookTarget.x + xLocal, this.lookTarget.y + yLocal,
                this.lookTarget.z + zLocal, this.refUp.x, this.refUp.y,
                this.refUp.z);
        }
    }

    /**
     * Draws a point at a given coordinate.
     *
     * @param v the coordinate
     */
    @Override
    public void point(final Vec3 v) {
        this.pointImpl(v.x, v.y, v.z);
    }

    /**
     * Initializes a point light at a location.
     *
     * @param clr the color
     * @param loc the location
     */
    public void pointLight(final int clr, final Vec3 loc) {

        this.pointLight(clr, loc.x, loc.y, loc.z);
    }

    /**
     * Draws a quadratic Bézier curve segment to the next anchor point; the
     * control point shapes the curve segment.
     *
     * @param cp  the control point
     * @param ap1 the next anchor point
     */
    @Override
    public void quadraticVertex(final Vec3 cp, final Vec3 ap1) {

        super.quadraticVertex(cp.x, cp.y, cp.z, ap1.x, ap1.y, ap1.z);
    }

    /**
     * Resets the model view and camera matrices to the identity. Resets
     * projection model view to the projection. Resets camera axes vectors to
     * the default.
     */
    @Override
    public void resetMatrix() {

        super.resetMatrix();
        Vec3.right(this.i);
        Vec3.forward(this.j);
        Vec3.up(this.k);
        Vec3.forward(this.lookDir);
        Vec3.zero(this.lookTarget);
    }

    /**
     * Rotates the model view matrix around an arbitrary axis by an angle in
     * radians.
     *
     * @param angle the angle in radians
     * @param xAxis the axis x coordinate
     * @param yAxis the axis y coordinate
     * @param zAxis the axis z coordinate
     */
    @Override
    public void rotate(final float angle, final float xAxis, final float yAxis,
        final float zAxis) {

        this.rotateImpl(angle, xAxis, yAxis, zAxis);
    }

    /**
     * Rotates the sketch by an angle in radians around an arbitrary axis.
     *
     * @param angle the angle
     * @param axis  the axis
     */
    public void rotate(final float angle, final Vec3 axis) {

        this.rotateImpl(angle, axis.x, axis.y, axis.z);
    }

    /**
     * Rotates the renderer by a quaternion.
     *
     * @param q the quaternion
     */
    public void rotate(final Quaternion q) {

        PMatAux.rotate(q, this.modelview);
        PMatAux.invRotate(q, this.modelviewInv);
        this.updateProjmodelview();
    }

    /**
     * Scales the renderer by a dimension.
     *
     * @param dim the dimensions
     */
    public void scale(final Vec3 dim) {

        this.scaleImpl(dim.x, dim.y, dim.z);
    }

    /**
     * Sets whether depth sorting is enabled. Flushes the geometry.
     *
     * @param ds the depth sorting
     */
    public void setDepthSorting(final boolean ds) {

        this.flush();
        this.isDepthSortingEnabled = ds;
    }

    /**
     * Resets the {@link PGraphicsOpenGL#camera} and
     * {@link PGraphicsOpenGL#cameraInv} matrices, sets the model view to the
     * input values, calculates the inverse model view, then recalculates the
     * projection model view.
     *
     * @param m00 row 0, column 0
     * @param m01 row 0, column 1
     * @param m02 row 0, column 2
     * @param m03 row 0, column 3
     * @param m10 row 1, column 0
     * @param m11 row 1, column 1
     * @param m12 row 1, column 2
     * @param m13 row 1, column 3
     * @param m20 row 2, column 0
     * @param m21 row 2, column 1
     * @param m22 row 2, column 2
     * @param m23 row 2, column 3
     * @param m30 row 3, column 0
     * @param m31 row 3, column 1
     * @param m32 row 3, column 2
     * @param m33 row 3, column 3
     */
    @Override
    public void setMatrix(final float m00, final float m01, final float m02,
        final float m03, final float m10, final float m11, final float m12,
        final float m13, final float m20, final float m21, final float m22,
        final float m23, final float m30, final float m31, final float m32,
        final float m33) {

        super.setMatrix(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22,
            m23, m30, m31, m32, m33);
        Vec3.right(this.i);
        Vec3.forward(this.j);
        Vec3.up(this.k);
        Vec3.forward(this.lookDir);
        Vec3.zero(this.lookTarget);
    }

    /**
     * Draws a 3D curve entity.
     *
     * @param ce the curve entity
     */
    public void shape(final CurveEntity3 ce) {

        final Transform3 tr = ce.transform;
        final Iterator<Curve3> itr = ce.iterator();

        final Vec3 fh = new Vec3();
        final Vec3 rh = new Vec3();
        final Vec3 co = new Vec3();

        while (itr.hasNext()) {
            this.drawCurve3(itr.next(), tr, fh, rh, co);
        }
    }

    /**
     * Draws a 3D curve entity.
     *
     * @param ce       the curve entity
     * @param material the material
     */
    public void shape(final CurveEntity3 ce, final MaterialSolid material) {

        this.pushStyle();
        this.material(material);
        this.shape(ce);
        this.popStyle();
    }

    /**
     * Draws a 3D curve entity.
     *
     * @param ce        the curve entity
     * @param materials the array of materials
     */
    public void shape(final CurveEntity3 ce,
        final MaterialSolid[] materials) {

        final Transform3 tr = ce.transform;
        final Vec3 fh = new Vec3();
        final Vec3 rh = new Vec3();
        final Vec3 co = new Vec3();

        for (final Curve3 curve : ce) {
            this.pushStyle();
            this.material(materials[curve.materialIndex]);
            this.drawCurve3(curve, tr, fh, rh, co);
            this.popStyle();
        }
    }

    /**
     * Draws a 3D mesh entity.
     *
     * @param entity the mesh entity
     */
    public void shape(final MeshEntity3 entity) {

        final Transform3 tr = entity.transform;
        final Iterator<Mesh3> meshItr = entity.iterator();

        final Vec3 v = new Vec3();
        final Vec3 vn = new Vec3();

        /*
         * With perspective projection, using stroke and fill together leads to
         * flickering issues.
         */

        final boolean oldStroke = this.stroke;
        if (this.fill) {
            this.stroke = false;
        }
        while (meshItr.hasNext()) {
            this.drawMesh3(meshItr.next(), tr, v, vn);
        }
        this.stroke = oldStroke;
    }

    /**
     * Draws a 3D mesh entity.
     *
     * @param me       the mesh entity
     * @param material the material
     */
    public void shape(final MeshEntity3 me, final MaterialPImage material) {

        final Transform3 tr = me.transform;
        final Iterator<Mesh3> meshItr = me.iterator();

        final Vec3 v = new Vec3();
        final Vec2 vt = new Vec2();
        final Vec3 vn = new Vec3();

        this.pushStyle();
        this.noStroke();
        while (meshItr.hasNext()) {
            this.drawMesh3(meshItr.next(), tr, material, v, vt, vn);
        }
        this.popStyle();
    }

    /**
     * Draws a 3D mesh entity.
     *
     * @param me        the mesh entity
     * @param materials the materials
     */
    public void shape(final MeshEntity3 me,
        final MaterialPImage[] materials) {

        final Transform3 tr = me.transform;
        final Vec3 v = new Vec3();
        final Vec2 vt = new Vec2();
        final Vec3 vn = new Vec3();

        this.pushStyle();
        this.noStroke();
        for (final Mesh3 mesh : me) {
            this.drawMesh3(mesh, tr, materials[mesh.materialIndex], v, vt, vn);
        }
        this.popStyle();
    }

    /**
     * Draws a 3D mesh entity.
     *
     * @param me       the mesh entity
     * @param material the material
     */
    public void shape(final MeshEntity3 me, final MaterialSolid material) {

        this.pushStyle();
        this.material(material);
        this.shape(me);
        this.popStyle();
    }

    /**
     * Draws a mesh entity.
     *
     * @param me        the mesh entity
     * @param materials the materials
     */
    public void shape(final MeshEntity3 me, final MaterialSolid[] materials) {

        final Transform3 tr = me.transform;
        final Vec3 v = new Vec3();
        final Vec3 vn = new Vec3();

        for (final Mesh3 mesh : me) {
            this.pushStyle();
            this.material(materials[mesh.materialIndex]);
            this.drawMesh3(mesh, tr, v, vn);
            this.popStyle();
        }
    }

    /**
     * Moves the camera and its look target by a vector relative to its
     * orientation.
     *
     * @param x the vector x
     * @param y the vector y
     * @param z the vector z
     */
    public void strafe(final float x, final float y, final float z) {

        final PMatrix3D ci = this.cameraInv;
        final float w = ci.m30 * x + ci.m31 * y + ci.m32 * z + ci.m33;
        if (w != 0.0f) {
            final float wInv = 1.0f / w;
            final float xLocal = (ci.m00 * x + ci.m01 * y + ci.m02 * z) * wInv;
            final float yLocal = (ci.m10 * x + ci.m11 * y + ci.m12 * z) * wInv;
            final float zLocal = (ci.m20 * x + ci.m21 * y + ci.m22 * z) * wInv;

            this.camera(this.cameraX + xLocal, this.cameraY + yLocal, this.cameraZ
                    + zLocal, this.lookTarget.x + xLocal, this.lookTarget.y + yLocal,
                this.lookTarget.z + zLocal, this.refUp.x, this.refUp.y,
                this.refUp.z);
        }
    }

    /**
     * Moves the camera and its look target by a vector relative to its
     * orientation.
     *
     * @param v the vector
     */
    public void strafe(final Vec3 v) {
        this.strafe(v.x, v.y, v.z);
    }

    /**
     * This parent function attempts to translate the text and then undo the
     * translation.
     *
     * @param chars the characters array
     * @param start the start index
     * @param stop  the stop index
     * @param x     the x coordinate
     * @param y     the y coordinate
     * @param z     the z coordinate
     */
    @Override
    public void text(final char[] chars, final int start, final int stop,
        final float x, final float y, final float z) {

        this.text(chars, start, stop, x, y);
    }

    /**
     * This parent function attempts to translate the text and then undo the
     * translation.
     *
     * @param str the string
     * @param x   the x coordinate
     * @param y   the y coordinate
     * @param z   the z coordinate
     */
    @Override
    public void text(final String str, final float x, final float y,
        final float z) {

        this.text(str, x, y);
    }

    /**
     * Draws a 3D text entity.
     *
     * @param entity the text entity
     */
    public void text(final TextEntity3 entity) {

        this.shape(entity, entity.material);
    }

    /**
     * Returns the string representation of this renderer.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return Up3.PATH_STR;
    }

    /**
     * Applies a transform to the renderer's matrix.
     *
     * @param tr3 the transform
     */
    public void transform(final Transform3 tr3) {

        this.transform(tr3, IUp.DEFAULT_ORDER);
    }

    /**
     * Applies a transform to the renderer's matrix.
     *
     * @param tr3   the transform
     * @param order the transform order
     */
    public void transform(final Transform3 tr3, final TransformOrder order) {

        /*
         * This is not as redundant to Convert's Transform to PMatrix3D as it
         * looks, because this also adjusts the inverses and updates the model
         * view.
         */

        final Vec3 tr3Scl = tr3.getScale(new Vec3());
        final Vec3 tr3Loc = tr3.getLocation(new Vec3());
        final Quaternion tr3Rot = tr3.getRotation(new Quaternion());

        switch (order) {

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

            case R:

                this.rotate(tr3Rot);
                return;

            case RS:

                this.rotate(tr3Rot);
                this.scaleImpl(tr3Scl.x, tr3Scl.y, tr3Scl.z);
                return;

            case RT:

                this.rotate(tr3Rot);
                this.translateImpl(tr3Loc.x, tr3Loc.y, tr3Loc.z);
                return;

            case S:

                this.scaleImpl(tr3Scl.x, tr3Scl.y, tr3Scl.z);
                return;

            case SR:

                this.scaleImpl(tr3Scl.x, tr3Scl.y, tr3Scl.z);
                this.rotate(tr3Rot);
                return;

            case ST:

                this.scaleImpl(tr3Scl.x, tr3Scl.y, tr3Scl.z);
                this.translateImpl(tr3Loc.x, tr3Loc.y, tr3Loc.z);
                return;

            case T:

                this.translateImpl(tr3Loc.x, tr3Loc.y, tr3Loc.z);
                return;

            case TR:

                this.translateImpl(tr3Loc.x, tr3Loc.y, tr3Loc.z);
                this.rotate(tr3Rot);
                return;

            case TS:

                this.translateImpl(tr3Loc.x, tr3Loc.y, tr3Loc.z);
                this.scaleImpl(tr3Scl.x, tr3Scl.y, tr3Scl.z);
                return;

            case TRS:
            default:

                this.translateImpl(tr3Loc.x, tr3Loc.y, tr3Loc.z);
                this.rotate(tr3Rot);
                this.scaleImpl(tr3Scl.x, tr3Scl.y, tr3Scl.z);
        }
    }

    /**
     * Translates the renderer by a vector.
     *
     * @param v the vector
     */
    public void translate(final Vec3 v) {

        this.translateImpl(v.x, v.y, v.z);
    }

    /**
     * Trucks the camera, moving it on its local x axis, left or right. This is
     * done by multiplying the x magnitude by the camera inverse, then adding
     * the local coordinates to both the camera location and look target.
     *
     * @param x the x magnitude
     */
    @Override
    public void truck(final float x) {

        final float w = this.cameraInv.m30 * x + this.cameraInv.m33;
        if (w != 0.0f) {
            final float xwInv = x / w;
            final float xLocal = this.cameraInv.m00 * xwInv;
            final float yLocal = this.cameraInv.m10 * xwInv;
            final float zLocal = this.cameraInv.m20 * xwInv;

            this.camera(this.cameraX + xLocal, this.cameraY + yLocal, this.cameraZ
                    + zLocal, this.lookTarget.x + xLocal, this.lookTarget.y + yLocal,
                this.lookTarget.z + zLocal, this.refUp.x, this.refUp.y,
                this.refUp.z);
        }
    }

    /**
     * Adds another vertex to a shape between the beginShape and endShape
     * commands.
     *
     * @param v the coordinate
     */
    @Override
    public void vertex(final Vec3 v) {

        this.vertexImpl(v.x, v.y, v.z, this.textureU, this.textureV);
    }

    /**
     * Adds another vertex to a shape between the beginShape and endShape
     * commands. Includes texture coordinates.
     *
     * @param v  the coordinate
     * @param vt the texture coordinate
     */
    public void vertex(final Vec3 v, final Vec2 vt) {

        this.vertexImpl(v.x, v.y, v.z, vt.x, vt.y);
    }

    /**
     * Initializes an ambient light with the default ambient color. The
     * camera's look target is used as the location. Ambient light illuminates
     * an object evenly from all sides.
     */
    void ambientLight() {

        // Cf. IUpOgl.DEFAULT_AMB_R, IUpOgl.DEFAULT_AMB_G, IUpOgl.DEFAULT_AMB_B
        this.ambientLight(0xff202633);
    }

    /**
     * Initializes an ambient light with a color. The camera's look target is
     * used as the location. Ambient light illuminates an object evenly from
     * all sides.
     *
     * @param clr the color
     */
    @SuppressWarnings("SameParameterValue")
    void ambientLight(final int clr) {

        this.ambientLight(clr, this.lookTarget.x, this.lookTarget.y,
            this.lookTarget.z);
    }

    /**
     * Initializes an ambient light with a color and location. Ambient light
     * illuminates an object evenly from all sides.
     *
     * @param clr  the color
     * @param xLoc the location x
     * @param yLoc the location y
     * @param zLoc the location z
     */
    void ambientLight(final int clr, final float xLoc, final float yLoc,
        final float zLoc) {

        this.enableLighting();
        if (this.lightCount >= IUpOgl.MAX_LIGHTS) {
            return;
        }

        this.lightType[this.lightCount] = PConstants.AMBIENT;

        this.lightPosition(this.lightCount, xLoc, yLoc, zLoc, false);
        this.lightNormal(this.lightCount, 0.0f, 0.0f, 0.0f);

        this.lightAmbient(this.lightCount, clr);
        this.noLightDiffuse(this.lightCount);
        this.noLightSpecular(this.lightCount);
        this.noLightSpot(this.lightCount);
        this.lightFalloff(this.lightCount, this.currentLightFalloffConstant,
            this.currentLightFalloffLinear, this.currentLightFalloffQuadratic);

        ++this.lightCount;
    }

    /**
     * Initializes an ambient light with a color and location. Ambient light
     * illuminates an object evenly from all sides.
     *
     * @param clr the color
     * @param loc the location
     */
    void ambientLight(final int clr, final Vec3 loc) {

        this.ambientLight(clr, loc.x, loc.y, loc.z);
    }

    /**
     * Initializes an ambient light with a color. The camera's look target is
     * used as the location. Ambient light illuminates an object evenly from
     * all sides.
     *
     * @param c the color
     */
    void ambientLight(final Rgb c) {

        this.ambientLight(c.toHexIntWrap(), this.lookTarget.x, this.lookTarget.y,
            this.lookTarget.z);
    }

    /**
     * Initializes an ambient light with a color and location. Ambient light
     * illuminates an object evenly from all sides.
     *
     * @param c   the color
     * @param loc the location
     */
    void ambientLight(final Rgb c, final Vec3 loc) {

        this.ambientLight(c.toHexIntWrap(), loc.x, loc.y, loc.z);
    }

    /**
     * Initialize a directional light with a color and a direction.
     *
     * @param clr  the color
     * @param xDir the x direction
     * @param yDir the y direction
     * @param zDir the z direction
     */
    void directionalLight(final int clr, final float xDir, final float yDir,
        final float zDir) {

        // These are all package level because overridden lights functions
        // cause major glitches on graphics renderer....

        this.enableLighting();
        if (this.lightCount >= IUpOgl.MAX_LIGHTS) {
            return;
        }

        this.lightType[this.lightCount] = PConstants.DIRECTIONAL;

        this.lightPosition(this.lightCount, 0.0f, 0.0f, 0.0f, true);
        this.lightNormal(this.lightCount, xDir, yDir, zDir);

        this.noLightAmbient(this.lightCount);
        this.lightDiffuse(this.lightCount, clr);
        this.lightSpecular(this.lightCount, this.currentLightSpecular[0],
            this.currentLightSpecular[1], this.currentLightSpecular[2]);
        this.noLightSpot(this.lightCount);
        this.noLightFalloff(this.lightCount);

        ++this.lightCount;
    }

    /**
     * Initialize a directional light with a color and a direction.
     *
     * @param clr the color
     * @param dir the direction
     */
    void directionalLight(final int clr, final Vec3 dir) {

        this.directionalLight(clr, dir.x, dir.y, dir.z);
    }

    /**
     * Initialize a directional light with a color and a direction.
     *
     * @param c   the color
     * @param dir the direction
     */
    void directionalLight(final Rgb c, final Vec3 dir) {

        this.directionalLight(c.toHexIntWrap(), dir.x, dir.y, dir.z);
    }

    /**
     * Initializes a point light at a location.
     *
     * @param clr  the color
     * @param xLoc the location x
     * @param yLoc the location y
     * @param zLoc the location z
     */
    void pointLight(final int clr, final float xLoc, final float yLoc,
        final float zLoc) {

        this.enableLighting();
        if (this.lightCount >= IUpOgl.MAX_LIGHTS) {
            return;
        }

        this.lightType[this.lightCount] = PConstants.POINT;

        this.lightPosition(this.lightCount, xLoc, yLoc, zLoc, false);
        this.lightNormal(this.lightCount, 0.0f, 0.0f, 0.0f);
        this.noLightAmbient(this.lightCount);
        this.lightDiffuse(this.lightCount, clr);
        this.lightSpecular(this.lightCount, this.currentLightSpecular[0],
            this.currentLightSpecular[1], this.currentLightSpecular[2]);
        this.noLightSpot(this.lightCount);
        this.lightFalloff(this.lightCount, this.currentLightFalloffConstant,
            this.currentLightFalloffLinear, this.currentLightFalloffQuadratic);

        ++this.lightCount;
    }

    /**
     * Initializes a point light at a location.
     *
     * @param c   the color
     * @param loc the location
     */
    void pointLight(final Rgb c, final Vec3 loc) {

        this.pointLight(c.toHexIntWrap(), loc.x, loc.y, loc.z);
    }

    /**
     * Initializes a spotlight. The location positions the spotlight in space
     * while the direction determines where the light points. The angle
     * parameter affects angle of the spotlight cone, while concentration sets
     * the bias of light focusing toward the center of that cone.
     *
     * @param clr           the color
     * @param xLoc          the location x
     * @param yLoc          the location y
     * @param zLoc          the location z
     * @param xDir          the direction x
     * @param yDir          the direction y
     * @param zDir          the direction z
     * @param angle         spotlight cone angle
     * @param concentration cone center bias
     */
    void spotLight(
        final int clr,
        final float xLoc, final float yLoc, final float zLoc,
        final float xDir, final float yDir, final float zDir,
        final float angle,
        final float concentration) {

        this.enableLighting();
        if (this.lightCount >= IUpOgl.MAX_LIGHTS) {
            return;
        }

        this.lightType[this.lightCount] = PConstants.SPOT;

        this.lightPosition(this.lightCount, xLoc, yLoc, zLoc, false);
        this.lightNormal(this.lightCount, xDir, yDir, zDir);

        this.noLightAmbient(this.lightCount);
        this.lightDiffuse(this.lightCount, clr);
        this.lightSpecular(this.lightCount, this.currentLightSpecular[0],
            this.currentLightSpecular[1], this.currentLightSpecular[2]);
        this.lightSpot(this.lightCount, angle, concentration);
        this.lightFalloff(this.lightCount, this.currentLightFalloffConstant,
            this.currentLightFalloffLinear, this.currentLightFalloffQuadratic);

        ++this.lightCount;
    }

    /**
     * Initializes a spotlight. The location positions the spotlight in space
     * while the direction determines where the light points. The angle
     * parameter affects angle of the spotlight cone, while concentration sets
     * the bias of light focusing toward the center of that cone.
     *
     * @param clr           the color
     * @param loc           the location
     * @param dir           the direction
     * @param angle         the angle
     * @param concentration cone center bias
     */
    void spotLight(final int clr, final Vec3 loc, final Vec3 dir,
        final float angle, final float concentration) {

        this.spotLight(clr,
            loc.x, loc.y, loc.z,
            dir.x, dir.y, dir.z,
            angle, concentration);
    }

    /**
     * Initializes a spotlight. The location positions the spotlight in space
     * while the direction determines where the light points. The angle
     * parameter affects angle of the spotlight cone, while concentration sets
     * the bias of light focusing toward the center of that cone.
     *
     * @param c             the color
     * @param loc           the location
     * @param dir           the direction
     * @param angle         the angle
     * @param concentration cone center bias
     */
    void spotLight(final Rgb c, final Vec3 loc, final Vec3 dir,
        final float angle, final float concentration) {

        this.spotLight(c.toHexIntWrap(),
            loc.x, loc.y, loc.z,
            dir.x, dir.y, dir.z,
            angle, concentration);
    }

    /**
     * A helper function for the renderer camera. Updates the camera matrix,
     * its inverse, the model view and its inverse, and updates the projection
     * model view.
     */
    protected void updateCameraInv() {

        final float m00 = this.i.x;
        final float m01 = this.i.y;
        final float m02 = this.i.z;

        final float m10 = this.j.x;
        final float m11 = this.j.y;
        final float m12 = this.j.z;

        final float m20 = this.k.x;
        final float m21 = this.k.y;
        final float m22 = this.k.z;

        /* Set inverse by column. */
        this.cameraInv.set(
            m00, m10, m20, this.cameraX,
            m01, m11, m21, this.cameraY,
            m02, m12, m22, this.cameraZ,
            0.0f, 0.0f, 0.0f, 1.0f);

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

        /* Set model view to camera. */
        this.modelview.set(this.camera);
        this.modelviewInv.set(this.cameraInv);
        PMatAux.mul(this.projection, this.modelview, this.projmodelview);
    }
}

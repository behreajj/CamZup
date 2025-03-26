package com.behreajj.camzup.friendly;

import com.behreajj.camzup.core.*;
import processing.core.*;
import processing.opengl.PGL;
import processing.opengl.PGraphicsOpenGL;

import java.util.Iterator;

/**
 * A 2.5D renderer based on OpenGL. Supposes that the camera is looking
 * down on a 2D plane from the z axis, making (0.0, 1.0) the forward -- or
 * up -- axis.
 */
public class Yup2 extends UpOgl implements ITextDisplay2, IYup2 {

    /**
     * The path string for this renderer.
     */
    @SuppressWarnings("hiding")
    public static final String PATH_STR = "com.behreajj.camzup.friendly.Yup2";

    /**
     * The camera rotation in radians.
     */
    public float cameraRot = IYup2.DEFAULT_ROT;

    /**
     * The camera horizontal zoom.
     */
    public float cameraZoomX = IYup2.DEFAULT_ZOOM_X;

    /**
     * The camera vertical zoom.
     */
    public float cameraZoomY = IYup2.DEFAULT_ZOOM_Y;

    /**
     * The default constructor.
     */
    public Yup2() {
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
    public Yup2(final int width, final int height, final PApplet parent,
        final String path, final boolean isPrimary) {

        super(width, height, parent, path, isPrimary);
    }

    /**
     * Draws an arc at a location from a start angle to a stop angle.
     *
     * @param v     the location
     * @param sz    the arc size
     * @param start the start angle
     * @param stop  the stop angle
     * @param mode  the arc mode
     */
    @Override
    public void arc(final Vec2 v, final float sz, final float start,
        final float stop, final int mode) {

        this.arc(v.x, v.y, sz, sz, start, stop, mode);
    }

    /**
     * Draws a cubic Bézier curve between two anchor points, where the control
     * points shape the curve.
     *
     * @param ap0 the first anchor point
     * @param cp0 the first control point
     * @param cp1 the second control point
     * @param ap1 the second anchor point
     */
    @Override
    public void bezier(final Vec2 ap0, final Vec2 cp0, final Vec2 cp1,
        final Vec2 ap1) {

        this.bezier(ap0.x, ap0.y, cp0.x, cp0.y, cp1.x, cp1.y, ap1.x, ap1.y);
    }

    /**
     * Draws a cubic Bézier curve segment to the next anchor point. The first
     * and second control point shape the curve segment.
     *
     * @param cp0 the first control point
     * @param cp1 the second control point
     * @param ap1 the next anchor point
     */
    @Override
    public void bezierVertex(final Vec2 cp0, final Vec2 cp1, final Vec2 ap1) {

        this.bezierVertexImpl(cp0.x, cp0.y, 0.0f, cp1.x, cp1.y, 0.0f, ap1.x,
            ap1.y, 0.0f);
    }

    /**
     * Draws a bounding area.
     *
     * @param b the bounds
     */
    @Override
    public void bounds(final Bounds2 b) {

        final Vec2 min = b.min;
        final Vec2 max = b.max;

        this.beginShape(PConstants.POLYGON);
        this.normalPerShape(0.0f, 0.0f, 1.0f);
        this.vertexImpl(min.x, min.y, 0.0f, 0.0f, 0.0f);
        this.vertexImpl(max.x, min.y, 0.0f, 1.0f, 0.0f);
        this.vertexImpl(max.x, max.y, 0.0f, 1.0f, 1.0f);
        this.vertexImpl(min.x, max.y, 0.0f, 0.0f, 1.0f);
        this.endShape(PConstants.CLOSE);
    }

    /**
     * Sets the camera to the renderer defaults.
     */
    @Override
    public void camera() {

        this.camera(this.cameraX, this.cameraY, this.cameraRot, this.cameraZoomX,
            this.cameraZoomY);
    }

    /**
     * Sets the camera to a location, rotation and zoom level.
     *
     * @param x       the translation x
     * @param y       the translation y
     * @param radians the angle in radians
     * @param zx      the zoom x
     * @param zy      the zoom y
     * @see Utils#abs(float)
     * @see Utils#modRadians(float)
     */
    @Override
    public void camera(final float x, final float y, final float radians,
        final float zx, final float zy) {

        this.cameraX = x;
        this.cameraY = y;
        this.cameraZ = this.height < 128 ? 128.0f : this.height;
        this.cameraRot = radians;

        this.cameraZoomX = Utils.abs(zx) < Utils.EPSILON ? 1.0f : zx;
        this.cameraZoomY = Utils.abs(zy) < Utils.EPSILON ? 1.0f : zy;

        /*
         * this.modelview.reset(); this.modelview.scale( this.cameraZoomX,
         * this.cameraZoomY, 1.0f); this.modelview.rotateZ(-radians);
         * this.modelview.translate( -this.cameraX, -this.cameraY, -zDist);
         */

        final float nrm = -radians * Utils.ONE_TAU;
        final float c = Utils.scNorm(nrm);
        final float s = Utils.scNorm(nrm - 0.25f);

        final float m00 = c * this.cameraZoomX;
        final float m01 = -s * this.cameraZoomY;
        final float m10 = s * this.cameraZoomX;
        final float m11 = c * this.cameraZoomY;

        /* @formatter:off */
        this.modelview.set(
            m00,  m01, 0.0f, -this.cameraX * m00 - this.cameraY * m01,
            m10,  m11, 0.0f, -this.cameraX * m10 - this.cameraY * m11,
            0.0f, 0.0f, 1.0f,                            -this.cameraZ,
            0.0f, 0.0f, 0.0f,                                     1.0f);

        this.modelviewInv.set(
            c / this.cameraZoomX, s / this.cameraZoomX, 0.0f, this.cameraX,
            -s / this.cameraZoomY, c / this.cameraZoomY, 0.0f, this.cameraY,
            0.0f,                 0.0f, 1.0f, this.cameraZ,
            0.0f,                 0.0f, 0.0f,         1.0f);
        /* @formatter:on */

        this.camera.set(this.modelview);
        this.cameraInv.set(this.modelviewInv);
        this.projmodelview.set(this.projection);
    }

    /**
     * This version of camera is not supported by this renderer. Calls the
     * supported version of camera.
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
    public void camera(final float xEye, final float yEye, final float zEye,
        final float xCenter, final float yCenter, final float zCenter,
        final float xUp, final float yUp, final float zUp) {

        PApplet.showMissingWarning("camera");

        this.camera(xEye, yEye, Utils.atan2(yUp, xUp), this.cameraZoomX,
            this.cameraZoomY);
    }

    /**
     * Sets the camera to a location, rotation and zoom level.
     *
     * @param loc     the location
     * @param radians the angle
     * @param zoom    the zoom level
     */
    @Override
    public void camera(final Vec2 loc, final float radians, final Vec2 zoom) {

        this.camera(loc.x, loc.y, radians, zoom.x, zoom.y);
    }

    /**
     * Sets the camera to the Processing default, where the origin is in the
     * top left corner of the sketch and the y axis points downward.
     */
    public void camFlipped() {

        this.camera(this.width * 0.5f, this.height * 0.5f, 0.0f, 1.0f, -1.0f);
    }

    /**
     * Draws a circle at a location
     *
     * @param coord the coordinate
     * @param size  the size
     */
    @Override
    public void circle(final Vec2 coord, final float size) {

        this.ellipseImpl(coord.x, coord.y, size, size);
    }

    /**
     * Draws a curve between four points.
     *
     * @param a the first point
     * @param b the second point
     * @param c the third point
     * @param d the fourth point
     */
    @Override
    public void curve(final Vec2 a, final Vec2 b, final Vec2 c,
        final Vec2 d) {

        this.curve(a.x, a.y, b.x, b.y, c.x, c.y, d.x, d.y);
    }

    /**
     * Draws a curve segment.
     *
     * @param v the coordinate
     */
    @Override
    public void curveVertex(final Vec2 v) {

        this.curveVertexImpl(v.x, v.y, 0.0f);
    }

    /**
     * Sets default camera and calls the camera function.
     */
    @Override
    public void defaultCamera() {

        this.cameraX = this.defCameraX = IUp.DEFAULT_LOC_X;
        this.cameraY = this.defCameraY = IUp.DEFAULT_LOC_Y;
        this.cameraZ = this.defCameraZ = IUp.DEFAULT_LOC_Z;

        this.cameraZoomX = IYup2.DEFAULT_ZOOM_X;
        this.cameraZoomY = IYup2.DEFAULT_ZOOM_Y;
        this.cameraRot = IYup2.DEFAULT_ROT;

        this.camera();
    }

    /**
     * Sets the renderer's default perspective.
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
     * Sets the renderer's default styling. This mostly concerns camera
     * defaults and settings which convert OpenGL to a "2.5D" renderer.
     */
    @Override
    public void defaultSettings() {

        /*
         * Camera aspect, FOV, near and far clip planes are set by parent
         * defaultSettings method.
         */
        super.defaultSettings();
        this.noLights();

        this.cameraX = this.defCameraX = IUp.DEFAULT_LOC_X;
        this.cameraY = this.defCameraY = IUp.DEFAULT_LOC_Y;
        this.cameraZ = this.defCameraZ = IUp.DEFAULT_LOC_Z;

        this.cameraZoomX = IYup2.DEFAULT_ZOOM_X;
        this.cameraZoomY = IYup2.DEFAULT_ZOOM_Y;
        this.cameraRot = IYup2.DEFAULT_ROT;

        /*
         * Ensure depth-related features are turned off. These summarize the hint
         * system.
         */
        this.flush();
        this.pgl.disable(PGL.DEPTH_TEST);
        this.pgl.depthMask(false);
        this.isDepthSortingEnabled = false;
    }

    /**
     * Draws an ellipse; the meaning of the two parameters depends on the
     * renderer's ellipseMode.
     *
     * @param a the first parameter
     * @param b the second parameter
     */
    @Override
    public void ellipse(final Vec2 a, final Vec2 b) {

        super.ellipseImpl(a.x, a.y, b.x, b.y);
    }

    /**
     * Gets the renderer camera's 2D location.
     *
     * @param target the output vector
     * @return the location
     */
    @Override
    public Vec2 getLocation(final Vec2 target) {

        return target.set(this.cameraX, this.cameraY);
    }

    /**
     * Retrieves the renderer's fill and stroke properties packaged in a solid
     * material.
     *
     * @param target the output material
     * @return the renderer material
     */
    @Override
    public MaterialSolid getMaterial(final MaterialSolid target) {

        target.setName("Renderer");
        target.setFill(this.fill);
        Rgb.fromHex(this.fillColor, target.fill);
        target.setStroke(this.stroke);
        Rgb.fromHex(this.strokeColor, target.stroke);
        target.setStrokeWeight(this.strokeWeight);
        return target;
    }

    /**
     * Gets the renderer model view matrix.
     *
     * @return the model view
     */
    @Override
    public PMatrix2D getMatrix() {
        return this.getMatrix(new PMatrix2D());
    }

    /**
     * Gets the renderer model view matrix.
     *
     * @param target the output matrix
     * @return the model view
     */
    public Mat3 getMatrix(final Mat3 target) {

        return target.set(this.modelview.m00, this.modelview.m01,
            this.modelview.m03, this.modelview.m10, this.modelview.m11,
            this.modelview.m13, 0.0f, 0.0f, 1.0f);
    }

    /**
     * Gets the renderer camera's rotation.
     *
     * @return the rotation
     */
    @Override
    public float getRoll() {
        return this.cameraRot;
    }

    /**
     * Gets the renderer's zoom.
     *
     * @param target the output vector
     * @return the zoom
     */
    @Override
    public Vec2 getZoom(final Vec2 target) {

        return target.set(this.cameraZoomX, this.cameraZoomY);
    }

    /**
     * Gets the renderer's horizontal zoom.
     *
     * @return the zoom on the horizontal axis
     */
    @Override
    public float getZoomX() {
        return this.cameraZoomX;
    }

    /**
     * Gets the renderer's vertical zoom.
     *
     * @return the zoom on the vertical axis
     */
    @Override
    public float getZoomY() {
        return this.cameraZoomY;
    }

    /**
     * Displays the handles of a curve entity. The stroke weight is determined
     * by {@link IUp#DEFAULT_STROKE_WEIGHT}, {@value IUp#DEFAULT_STROKE_WEIGHT}
     * .
     *
     * @param ce the curve entity
     */
    public void handles(final CurveEntity2 ce) {

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
    public void handles(final CurveEntity2 ce, final float sw) {

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
    public void handles(final CurveEntity2 ce, final float sw,
        final int lineColor, final int rearColor, final int foreColor,
        final int coordColor) {

        final float swRear = sw * IUp.HANDLE_REAR_WEIGHT;
        final float swFore = sw * IUp.HANDLE_FORE_WEIGHT;
        final float swCoord = sw * IUp.HANDLE_COORD_WEIGHT;

        final Transform2 tr = ce.transform;
        final Vec2 rh = new Vec2();
        final Vec2 co = new Vec2();
        final Vec2 fh = new Vec2();

        this.pushStyle();

        for (final Curve2 curve : ce) {
            for (final Knot2 knot : curve) {
                Transform2.mulPoint(tr, knot.rearHandle, rh);
                Transform2.mulPoint(tr, knot.coord, co);
                Transform2.mulPoint(tr, knot.foreHandle, fh);

                this.strokeWeight(sw);
                this.stroke(lineColor);

                this.lineImpl(rh.x, rh.y, 0.0f, co.x, co.y, 0.0f);
                this.lineImpl(co.x, co.y, 0.0f, fh.x, fh.y, 0.0f);

                this.strokeWeight(swRear);
                this.stroke(rearColor);
                this.pointImpl(rh.x, rh.y, 0.0f);

                this.strokeWeight(swCoord);
                this.stroke(coordColor);
                this.pointImpl(co.x, co.y, 0.0f);

                this.strokeWeight(swFore);
                this.stroke(foreColor);
                this.pointImpl(fh.x, fh.y, 0.0f);
            }
        }

        this.popStyle();
    }

    /**
     * Draws a 2D image entity.
     *
     * @param entity the text entity
     */
    public void image(final ImageEntity2 entity) {

        this.shape(entity, entity.material);
    }

    /**
     * Draws an image at the origin.
     *
     * @param img the image
     */
    @Override
    public void image(final PImage img) {
        this.image(img, 0.0f, 0.0f);
    }

    /**
     * Displays a PImage. The meaning of the first four parameters depends on
     * imageMode. The last four coordinates specify the image texture
     * coordinates (or UVs).
     *
     * @param img the PImage
     * @param x1  the first x coordinate
     * @param y1  the first y coordinate
     * @param x2  the second x coordinate
     * @param y2  the second y coordinate
     * @param u1  the image top-left corner u
     * @param v1  the image top-left corner v
     * @param u2  the image bottom-right corner u
     * @param v2  the image bottom-right corner v
     */
    public void image(final PImage img, final float x1, final float y1,
        final float x2, final float y2, final float u1, final float v1,
        final float u2, final float v2) {

        /* This needs to imply the z coordinate then refer to the parent. */
        this.image(img, x1, y1, x2, y2, 0.0f, u1, v1, u2, v2);
    }

    /**
     * Draws an image at a given coordinate.
     *
     * @param img   the image
     * @param coord the coordinate
     */
    @Override
    public void image(final PImage img, final Vec2 coord) {

        this.image(img, coord.x, coord.y);
    }

    /**
     * Draws an image at a given coordinate and dimension.
     *
     * @param img   the image
     * @param coord the coordinate
     * @param dim   the dimension
     */
    @Override
    public void image(final PImage img, final Vec2 coord, final Vec2 dim) {

        this.image(img, coord.x, coord.y, dim.x, dim.y);
    }

    /**
     * Returns whether the renderer is 2D.
     */
    @Override
    public boolean is2D() {
        return true;
    }

    /**
     * Returns whether the renderer is 3D.
     */
    @Override
    public boolean is3D() {
        return false;
    }

    /**
     * Enable lighting and use default lights, typically an ambient light and a
     * directional light.
     *
     * @see PGraphicsOpenGL#lightFalloff(float, float, float)
     * @see PGraphicsOpenGL#lightSpecular(float, float, float)
     * @see PGraphicsOpenGL#ambientLight(float, float, float)
     * @see PGraphicsOpenGL#directionalLight(float, float, float, float, float,
     * float)
     */
    @Override
    public void lights() {

        this.enableLighting();

        this.lightCount = 0;

        final int colorModeSaved = this.colorMode;
        this.colorMode = PConstants.RGB;

        this.lightFalloff(1.0f, 0.0f, 0.0f);
        this.lightSpecular(0.0f, 0.0f, 0.0f);

        this.ambientLight(this.colorModeX * IUpOgl.DEFAULT_AMB_R, this.colorModeY
            * IUpOgl.DEFAULT_AMB_G, this.colorModeZ * IUpOgl.DEFAULT_AMB_B);

        this.directionalLight(this.colorModeX * IUpOgl.DEFAULT_LIGHT_R,
            this.colorModeY * IUpOgl.DEFAULT_LIGHT_G, this.colorModeZ
                * IUpOgl.DEFAULT_LIGHT_B,
            0.0f, 0.0f, -1.0f);

        this.colorMode = colorModeSaved;
    }

    /**
     * Draws a line between two coordinates.
     *
     * @param orig the origin coordinate
     * @param dest the destination coordinate
     */
    @Override
    public void line(final Vec2 orig, final Vec2 dest) {

        this.lineImpl(orig.x, orig.y, 0.0f, dest.x, dest.y, 0.0f);
    }

    /**
     * Finds the model view position of a point.<br>
     * <br>
     * More efficient than calling {@link PApplet#modelX(float, float, float)}
     * and {@link PApplet#modelY(float, float, float)} separately. However, it
     * is advisable to work with the renderer matrices directly.
     *
     * @param source the point
     * @param target the output vector
     * @return the model point
     */
    public Vec2 model(final Vec2 source, final Vec2 target) {

        /*
         * Multiply point by model-view matrix; multiply product by inverse of
         * camera matrix.
         */

        /* @formatter:off */
        final float aw = this.modelview.m30 * source.x +
            this.modelview.m31 * source.y +
            this.modelview.m33;

        final float ax = this.modelview.m00 * source.x +
            this.modelview.m01 * source.y +
            this.modelview.m03;

        final float ay = this.modelview.m10 * source.x +
            this.modelview.m11 * source.y +
            this.modelview.m13;

        final float az = this.modelview.m20 * source.x +
            this.modelview.m21 * source.y +
            this.modelview.m23;

        final float bw = this.cameraInv.m30 * ax +
            this.cameraInv.m31 * ay +
            this.cameraInv.m32 * az +
            this.cameraInv.m33 * aw;

        if ( bw == 0.0f ) { return target.reset(); }

        final float bx = this.cameraInv.m00 * ax +
            this.cameraInv.m01 * ay +
            this.cameraInv.m02 * az +
            this.cameraInv.m03 * aw;

        final float by = this.cameraInv.m10 * ax +
            this.cameraInv.m11 * ay +
            this.cameraInv.m12 * az +
            this.cameraInv.m13 * aw;
        /* @formatter:on */

        /* Convert from homogeneous coordinate to point by dividing by w. */
        if (bw != 1.0f) {
            final float wInv = 1.0f / bw;
            return target.set(bx * wInv, by * wInv);
        }
        return target.set(bx, by);
    }

    /**
     * Takes a two-dimensional x, y position and returns the x value for where
     * it will appear on a model view. This is inefficient, use
     * {@link Yup2#model(Vec2, Vec2)} instead.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the model x coordinate
     */
    public float modelX(final float x, final float y) {

        return super.modelX(x, y, 0.0f);
    }

    /**
     * Takes a two-dimensional x, y position and returns the y value for where
     * it will appear on a model view. This is inefficient, use
     * {@link Yup2#model(Vec2, Vec2)} instead.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the model y coordinate
     */
    public float modelY(final float x, final float y) {

        return super.modelY(x, y, 0.0f);
    }

    /**
     * Takes a two-dimensional x, y position and returns the z value for where
     * it will appear on a model view. This is inefficient, use
     * {@link Yup2#model(Vec2, Vec2)} instead.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the model z coordinate
     */
    public float modelZ(final float x, final float y) {

        return super.modelZ(x, y, 0.0f);
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
     * {@link IUp#DEFAULT_I_COLOR} and {@link IUp#DEFAULT_J_COLOR}.
     *
     * @param lineLength the line length
     * @param sw         the stroke weight
     */
    public void origin(final float lineLength, final float sw) {

        this.origin(lineLength, sw, IUp.DEFAULT_I_COLOR, IUp.DEFAULT_J_COLOR);
    }

    /**
     * Draws the world origin.
     *
     * @param lineLength the line length
     * @param sw         the stroke weight
     * @param xColor     the color of the x axis
     * @param yColor     the color of the y axis
     */
    public void origin(final float lineLength, final float sw,
        final int xColor, final int yColor) {

        final float vl = Math.max(lineLength, Utils.EPSILON);

        this.pushStyle();
        this.strokeWeight(sw);
        this.stroke(xColor);
        this.lineImpl(0.0f, 0.0f, 0.0f, vl, 0.0f, 0.0f);
        this.stroke(yColor);
        this.lineImpl(0.0f, 0.0f, 0.0f, 0.0f, vl, 0.0f);
        this.popStyle();
    }

    /**
     * Draws a transform origin.
     *
     * @param tr         the transform
     * @param lineLength the line length
     * @param sw         the stroke weight
     * @param xColor     the color of the x axis
     * @param yColor     the color of the y axis
     */
    public void origin(final Transform2 tr, final float lineLength,
        final float sw, final int xColor, final int yColor) {

        final Vec2 origin = new Vec2();
        final Vec2 right = new Vec2();
        final Vec2 forward = new Vec2();
        tr.getLocation(origin);
        tr.getAxes(right, forward);

        final float vl = Math.max(lineLength, Utils.EPSILON);

        Vec2.mul(right, vl, right);
        Vec2.add(right, origin, right);
        Vec2.mul(forward, vl, forward);
        Vec2.add(forward, origin, forward);

        this.pushStyle();
        this.strokeWeight(sw);
        this.stroke(xColor);
        this.lineImpl(origin.x, origin.y, 0.0f, right.x, right.y, 0.0f);
        this.stroke(yColor);
        this.lineImpl(origin.x, origin.y, 0.0f, forward.x, forward.y, 0.0f);
        this.popStyle();
    }

    /**
     * Draws a point at a given coordinate
     *
     * @param v the coordinate
     */
    @Override
    public void point(final Vec2 v) {
        this.pointImpl(v.x, v.y, 0.0f);
    }

    /**
     * Draws a quadrilateral between four points.
     *
     * @param a the first point
     * @param b the second point
     * @param c the third point
     * @param d the fourth point
     */
    @Override
    public void quad(final Vec2 a, final Vec2 b, final Vec2 c, final Vec2 d) {

        this.quad(a.x, a.y, b.x, b.y, c.x, c.y, d.x, d.y);
    }

    /**
     * Draws a quadratic Bézier curve segment to the next anchor point; the
     * control point shapes the curve segment.
     *
     * @param cp  the control point
     * @param ap1 the next anchor point
     */
    @Override
    public void quadraticVertex(final Vec2 cp, final Vec2 ap1) {

        this.quadraticVertexImpl(cp.x, cp.y, 0.0f, ap1.x, ap1.y, 0.0f);
    }

    /**
     * Draws a rectangle; the meaning of the two parameters depends on the
     * renderer's rectMode.
     *
     * @param a the first parameter
     * @param b the second parameter
     */
    @Override
    public void rect(final Vec2 a, final Vec2 b) {

        this.rectImpl(a.x, a.y, b.x, b.y);
    }

    /**
     * Draws a rounded rectangle; the meaning of the first two parameters
     * depends on the renderer's rectMode.
     *
     * @param a the first parameter
     * @param b the second parameter
     * @param r the corner rounding
     */
    @Override
    public void rect(final Vec2 a, final Vec2 b, final float r) {

        this.rectImpl(a.x, a.y, b.x, b.y, r, r, r, r);
    }

    /**
     * Resets the model view and camera matrices to the identity. Resets
     * projection model view to the projection. Resets camera axes vectors to
     * the default.
     */
    @Override
    public void resetMatrix() {

        super.resetMatrix();
        this.cameraRot = 0.0f;
        this.cameraZoomX = 1.0f;
        this.cameraZoomY = 1.0f;
    }

    /**
     * Rotates the model view matrix around an arbitrary axis by an angle in
     * radians. Not supported by this renderer.
     *
     * @param angle the angle in radians
     * @param vx    the axis x coordinate
     * @param vy    the axis y coordinate
     * @param vz    the axis z coordinate
     */
    @Override
    public void rotate(final float angle, final float vx, final float vy,
        final float vz) {

        PGraphics.showVariationWarning("rotate");
    }

    /**
     * Rotates the sketch by an angle in radians around the y axis. For 2D,
     * this scales by ( 1.0, cos ( a ) ) .
     *
     * @param radians the angle
     */
    @Override
    public void rotateX(final float radians) {

        this.scaleImpl(1.0f, (float) Math.cos(radians), 1.0f);
    }

    /**
     * Rotates the sketch by an angle in radians around the y axis. For 2D,
     * this scales by ( cos ( a ), 1.0 ) .
     *
     * @param radians the angle
     */
    @Override
    public void rotateY(final float radians) {

        this.scaleImpl((float) Math.cos(radians), 1.0f, 1.0f);
    }

    /**
     * Scale the renderer by a vector.
     *
     * @param v the vector
     */
    public void scale(final Vec2 v) {
        this.scaleImpl(v.x, v.y, 1.0f);
    }

    /**
     * Finds the screen position of a point in the world.<br>
     * <br>
     * More efficient than calling {@link PApplet#screenX(float, float, float)}
     * , {@link PApplet#screenY(float, float, float)} , and
     * {@link PApplet#screenZ(float, float, float)} separately. However, it is
     * advisable to work with {@link Vec4}s and the renderer matrices directly.
     *
     * @param source the point
     * @param target the output vector
     * @return the screen point
     */
    @Override
    public Vec2 screen(final Vec2 source, final Vec2 target) {

        this.screen1s(source.x, source.y, target);
        return target.set(this.width * (1.0f + target.x) * 0.5f, this.height
            * (1.0f - (1.0f + target.y) * 0.5f));
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
        this.cameraRot = 0.0f;
        this.cameraZoomX = 1.0f;
        this.cameraZoomY = 1.0f;
    }

    /**
     * Set size is the last function called by size, createGraphics,
     * makeGraphics, etc. when initializing the graphics renderer. Therefore,
     * any additional values that need initialization can be attempted here.
     */
    @Override
    public void setSize(final int width, final int height) {

        super.setSize(width, height);
        this.ortho();
        this.camera();
    }

    /**
     * Draws a 2D curve entity.
     *
     * @param ce the curve entity
     */
    public void shape(final CurveEntity2 ce) {

        final Transform2 tr = ce.transform;
        final Iterator<Curve2> itr = ce.iterator();

        final Vec2 rh = new Vec2();
        final Vec2 fh = new Vec2();
        final Vec2 co = new Vec2();

        while (itr.hasNext()) {
            this.drawCurve2(itr.next(), tr, fh, rh, co);
        }
    }

    /**
     * Draws a 2D curve entity.
     *
     * @param ce       the curve entity
     * @param material the material
     */
    public void shape(final CurveEntity2 ce, final MaterialSolid material) {

        this.pushStyle();
        this.material(material);
        this.shape(ce);
        this.popStyle();
    }

    /**
     * Draws a 2D curve entity.
     *
     * @param ce        the curve entity
     * @param materials the materials array
     */
    public void shape(final CurveEntity2 ce,
        final MaterialSolid[] materials) {

        final Transform2 tr = ce.transform;
        final Vec2 fh = new Vec2();
        final Vec2 rh = new Vec2();
        final Vec2 co = new Vec2();

        for (final Curve2 curve : ce) {
            this.pushStyle();
            this.material(materials[curve.materialIndex]);
            this.drawCurve2(curve, tr, fh, rh, co);
            this.popStyle();
        }
    }

    /**
     * Draws a 2D mesh entity.
     *
     * @param me the mesh entity
     */
    public void shape(final MeshEntity2 me) {

        final Transform2 tr = me.transform;
        final Iterator<Mesh2> meshItr = me.iterator();
        final Vec2 v = new Vec2();

        while (meshItr.hasNext()) {
            this.drawMesh2(meshItr.next(), tr, v);
        }
    }

    /**
     * Draws a 2D mesh entity with a textured material.
     *
     * @param me       the mesh entity
     * @param material the material
     */
    public void shape(final MeshEntity2 me, final MaterialPImage material) {

        final Transform2 tr = me.transform;
        final Iterator<Mesh2> meshItr = me.iterator();
        final Vec2 v = new Vec2();
        final Vec2 vt = new Vec2();

        this.pushStyle();
        this.noStroke();
        while (meshItr.hasNext()) {
            this.drawMesh2(meshItr.next(), tr, material, v, vt);
        }
        this.popStyle();
    }

    /**
     * Draws a 2D mesh entity.
     *
     * @param me        the mesh entity
     * @param materials the materials
     */
    public void shape(final MeshEntity2 me, final MaterialPImage[] materials) {

        final Transform2 tr = me.transform;
        final Vec2 v = new Vec2();
        final Vec2 vt = new Vec2();

        this.pushStyle();
        this.noStroke();
        for (final Mesh2 mesh : me) {
            this.drawMesh2(mesh, tr, materials[mesh.materialIndex], v, vt);
        }
        this.popStyle();
    }

    /**
     * Draws a 2D mesh entity.
     *
     * @param me       the mesh entity
     * @param material the material
     */
    public void shape(final MeshEntity2 me, final MaterialSolid material) {

        this.pushStyle();
        this.material(material);
        this.shape(me);
        this.popStyle();
    }

    /**
     * Draws a 2D mesh entity.
     *
     * @param me        the mesh entity
     * @param materials the materials
     */
    public void shape(final MeshEntity2 me, final MaterialSolid[] materials) {

        final Transform2 tr = me.transform;
        final Vec2 v = new Vec2();

        for (final Mesh2 mesh : me) {
            this.pushStyle();
            this.material(materials[mesh.materialIndex]);
            this.drawMesh2(mesh, tr, v);
            this.popStyle();
        }
    }

    /**
     * Draws a square at a location.
     *
     * @param loc  the location
     * @param size the size
     */
    @Override
    public void square(final Vec2 loc, final float size) {

        this.rectImpl(loc.x, loc.y, size, size);
    }

    /**
     * Draws a rounded square.
     *
     * @param loc  the location
     * @param size the size
     * @param r    the corner rounding
     */
    @Override
    public void square(final Vec2 loc, final float size, final float r) {

        this.rectImpl(loc.x, loc.y, size, size, r, r, r, r);
    }

    /**
     * Draws a 2D text entity.
     *
     * @param entity the text entity
     */
    public void text(final TextEntity2 entity) {

        this.shape(entity, entity.material);
    }

    /**
     * Returns the string representation of this renderer.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return Yup2.PATH_STR;
    }

    /**
     * Translate the renderer by a vector.
     *
     * @param v the vector
     */
    public void translate(final Vec2 v) {

        this.translateImpl(v.x, v.y, 0.0f);
    }

    /**
     * Draws a triangle between three points.
     *
     * @param a the first point
     * @param b the second point
     * @param c the third point
     */
    @Override
    public void triangle(final Vec2 a, final Vec2 b, final Vec2 c) {

        super.triangle(a.x, a.y, b.x, b.y, c.x, c.y);
    }

    /**
     * Adds another vertex to a shape between the beginShape and endShape
     * commands.
     *
     * @param v the coordinate
     */
    @Override
    public void vertex(final Vec2 v) {

        this.vertexImpl(v.x, v.y, 0.0f, this.textureU, this.textureV);
    }

    /**
     * Adds another vertex to a shape between the beginShape and endShape
     * commands; includes a texture coordinate.
     *
     * @param v  the coordinate
     * @param vt the texture coordinate
     */
    public void vertex(final Vec2 v, final Vec2 vt) {

        this.vertexImpl(v.x, v.y, 0.0f, vt.x, vt.y);
    }

    /**
     * An internal helper function that multiplies the input point by the model
     * view, then by the projection.
     *
     * @param xSource the source x
     * @param ySource the source y
     * @param target  the output vector
     */
    protected void screen1s(final float xSource, final float ySource,
        final Vec2 target) {

        /* Multiply by model-view matrix; multiply product by projection. */

        /* @formatter:off */
        final float aw = this.modelview.m30 * xSource +
            this.modelview.m31 * ySource +
            this.modelview.m33;

        final float ax = this.modelview.m00 * xSource +
            this.modelview.m01 * ySource +
            this.modelview.m03;

        final float ay = this.modelview.m10 * xSource +
            this.modelview.m11 * ySource +
            this.modelview.m13;

        final float az = this.modelview.m20 * xSource +
            this.modelview.m21 * ySource +
            this.modelview.m23;

        final float bw = this.projection.m30 * ax +
            this.projection.m31 * ay +
            this.projection.m32 * az +
            this.projection.m33 * aw;

        if ( bw == 0.0f ) { target.reset();return ; }

        final float bx = this.projection.m00 * ax +
            this.projection.m01 * ay +
            this.projection.m02 * az +
            this.projection.m03 * aw;

        final float by = this.projection.m10 * ax +
            this.projection.m11 * ay +
            this.projection.m12 * az +
            this.projection.m13 * aw;
        /* @formatter:on */

        /* Convert homogeneous coordinate to point. */
        if (bw != 1.0f) {
            final float wInv = 1.0f / bw;
            target.set(bx * wInv, by * wInv);
            return;
        }

        target.set(bx, by);
    }
}

package com.behreajj.camzup.friendly;

import com.behreajj.camzup.core.Handedness;
import com.behreajj.camzup.core.Utils;
import com.behreajj.camzup.core.Vec3;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.opengl.PGraphicsOpenGL;

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
    public static final float DEFAULT_LIGHT_Y = 0.6471502f;

    /**
     * Default lighting directional light axis z component.
     */
    public static final float DEFAULT_LIGHT_Z = -0.73960024f;

    /**
     * Default camera location x component.
     */
    @SuppressWarnings("hiding")
    public static final float DEFAULT_LOC_X = 623.53827f;

    /**
     * Default camera location y component.
     */
    @SuppressWarnings("hiding")
    public static final float DEFAULT_LOC_Y = -623.53827f;

    /**
     * Default camera location z component.
     */
    @SuppressWarnings("hiding")
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
     * The path string for this renderer.
     */
    @SuppressWarnings("hiding")
    public static final String PATH_STR = "com.behreajj.camzup.friendly.Zup3";

    /**
     * The default constructor.
     */
    public Zup3() {
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
    public Zup3(final int width, final int height, final PApplet parent,
        final String path, final boolean isPrimary) {

        super(width, height, parent, path, isPrimary);
    }

    /**
     * Places the camera on the negative x axis, such that it is looking East
     * toward the world origin.
     */
    @Override
    public void camEast() {

        final float x = this.eyeDist < 128
            ? -Zup3.DEFAULT_LOC_X
            : -this.eyeDist;
        this.camera(
            x, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f,
            Zup3.DEFAULT_REF_X, Zup3.DEFAULT_REF_Y, Zup3.DEFAULT_REF_Z);
    }

    /**
     * Creates a camera that looks at a default location and a vantage point
     * based on the renderer's height.
     */
    @Override
    public void camera() {

        this.camera(
            this.cameraX, this.cameraY, this.cameraZ,
            this.lookTarget.x, this.lookTarget.y, this.lookTarget.z);
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
     */
    @Override
    public void camera(
        final float xEye, final float yEye, final float zEye,
        final float xCenter, final float yCenter, final float zCenter) {

        this.camera(
            xEye, yEye, zEye,
            xCenter, yCenter, zCenter,
            this.refUp.x, this.refUp.y, this.refUp.z);
    }

    /**
     * Looks at the center point from the eye point, using the world up axis as
     * a reference.
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
     * @see Vec3#normalize(Vec3, Vec3)
     * @see Vec3#crossNorm(Vec3, Vec3, Vec3)
     * @see Vec3#mag(Vec3)
     */
    @Override
    public void camera(
        final float xEye, final float yEye, final float zEye,
        final float xCenter, final float yCenter, final float zCenter,
        final float xUp, final float yUp, final float zUp) {

        this.refUp.set(xUp, yUp, zUp);
        this.lookTarget.set(xCenter, yCenter, zCenter);
        this.cameraX = xEye;
        this.cameraY = yEye;
        this.cameraZ = zEye;

        this.lookDir.set(
            xEye - this.lookTarget.x,
            yEye - this.lookTarget.y,
            zEye - this.lookTarget.z);
        this.eyeDist = Vec3.mag(this.lookDir);
        Vec3.normalize(this.lookDir, this.k);

        final float dotp = Vec3.dot(this.k, this.refUp);
        final float tol = 1.0f - IUp3.POLARITY_TOLERANCE;
        if (dotp < -tol || dotp > tol) {
            // TODO: Come up with a better solution to this.
            return;
        }

        Vec3.crossNorm(this.refUp, this.k, this.i);
        Vec3.crossNorm(this.k, this.i, this.j);
        this.updateCameraInv();
    }

    /**
     * Looks at the center point from the eye point, using the default
     * reference up axis.
     *
     * @param eye    the camera's location
     * @param center the point to look at
     */
    @Override
    public void camera(final Vec3 eye, final Vec3 center) {

        this.camera(
            eye.x, eye.y, eye.z,
            center.x, center.y, center.z);
    }

    /**
     * Sets the camera to a location, looking at a center, with a reference up
     * direction.
     *
     * @param eye    the eye location
     * @param center the center of the gaze
     * @param up     the reference up direction
     */
    public void camera(final Vec3 eye, final Vec3 center, final Vec3 up) {

        /* Do not move to Up3. Here, this keeps inheritance overrides clear. */
        this.camera(
            eye.x, eye.y, eye.z,
            center.x, center.y, center.z,
            up.x, up.y, up.z);
    }

    /**
     * Sets the camera to the Processing default, where the origin is in the
     * top left corner of the sketch and the y axis points downward.
     */
    public void camFlipped() {

        final float wHalf = this.width * 0.5f;
        final float hHalf = this.height * 0.5f;
        final float z = this.height < 128 ? -Zup3.DEFAULT_LOC_Z
            : -this.height
            * IUp.DEFAULT_CAM_DIST_FAC;
        this.camera(
            wHalf, hHalf, z,
            wHalf, hHalf, 0.0f,
            0.0f, -1.0f, 0.0f);
    }

    /**
     * Places the camera on the negative y axis, such that it is looking North
     * toward the world origin.
     */
    @Override
    public void camNorth() {

        final float y = this.eyeDist < 128
            ? Zup3.DEFAULT_LOC_Y
            : -this.eyeDist;
        this.camera(
            0.0f, y, 0.0f,
            0.0f, 0.0f, 0.0f,
            Zup3.DEFAULT_REF_X, Zup3.DEFAULT_REF_Y, Zup3.DEFAULT_REF_Z);
    }

    /**
     * Places the camera on the positive y axis, such that it is looking South
     * toward the world origin.
     */
    @Override
    public void camSouth() {

        final float y = this.eyeDist < 128
            ? -Zup3.DEFAULT_LOC_Y
            : this.eyeDist;
        this.camera(
            0.0f, y, 0.0f,
            0.0f, 0.0f, 0.0f,
            Zup3.DEFAULT_REF_X, Zup3.DEFAULT_REF_Y, Zup3.DEFAULT_REF_Z);
    }

    /**
     * Places the camera on the positive x axis, such that it is looking West
     * toward the world origin.
     */
    @Override
    public void camWest() {

        final float x = this.eyeDist < 128
            ? Zup3.DEFAULT_LOC_X
            : this.eyeDist;
        this.camera(
            x, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f,
            Zup3.DEFAULT_REF_X, Zup3.DEFAULT_REF_Y, Zup3.DEFAULT_REF_Z);
    }

    /**
     * Sets default camera and calls the camera function.
     */
    @Override
    public void defaultCamera() {

        this.defCameraX = Zup3.DEFAULT_LOC_X;
        this.defCameraY = Zup3.DEFAULT_LOC_Y;
        this.defCameraZ = Zup3.DEFAULT_LOC_Z;

        this.cameraX = this.defCameraX;
        this.cameraY = this.defCameraY;
        this.cameraZ = this.defCameraZ;

        this.refUp.set(
            Zup3.DEFAULT_REF_X,
            Zup3.DEFAULT_REF_Y,
            Zup3.DEFAULT_REF_Z);

        this.lookTarget.set(
            Up3.DEFAULT_TARGET_X,
            Up3.DEFAULT_TARGET_Y,
            Up3.DEFAULT_TARGET_Z);

        this.lookDir.set(
            Zup3.DEFAULT_LOC_X - Up3.DEFAULT_TARGET_X,
            Zup3.DEFAULT_LOC_Y - Up3.DEFAULT_TARGET_Y,
            Zup3.DEFAULT_LOC_Z - Up3.DEFAULT_TARGET_Z);

        this.eyeDist = Vec3.mag(this.lookDir);

        this.camera();
    }

    /**
     * Sets the renderer's default styling.
     */
    @Override
    public void defaultSettings() {

        super.defaultSettings();

        this.defCameraX = Zup3.DEFAULT_LOC_X;
        this.defCameraY = Zup3.DEFAULT_LOC_Y;
        this.defCameraZ = Zup3.DEFAULT_LOC_Z;

        this.cameraX = this.defCameraX;
        this.cameraY = this.defCameraY;
        this.cameraZ = this.defCameraZ;

        this.refUp.set(
            Zup3.DEFAULT_REF_X,
            Zup3.DEFAULT_REF_Y,
            Zup3.DEFAULT_REF_Z);

        this.lookTarget.set(
            Up3.DEFAULT_TARGET_X,
            Up3.DEFAULT_TARGET_Y,
            Up3.DEFAULT_TARGET_Z);

        this.lookDir.set(
            Zup3.DEFAULT_LOC_X - Up3.DEFAULT_TARGET_X,
            Zup3.DEFAULT_LOC_Y - Up3.DEFAULT_TARGET_Y,
            Zup3.DEFAULT_LOC_Z - Up3.DEFAULT_TARGET_Z);

        this.eyeDist = Vec3.mag(this.lookDir);

        Vec3.right(this.i);
        Vec3.forward(this.j);
        Vec3.up(this.k);
    }

    /**
     * Returns the handedness of the renderer.
     */
    @Override
    public Handedness handedness() {
        return Handedness.RIGHT;
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
     * Set size is the last function called by size, createGraphics,
     * makeGraphics, etc. when initializing the graphics renderer. Therefore,
     * any additional values that need initialization can be attempted here.
     *
     * @param width  the width in pixels
     * @param height the height in pixels
     */
    @Override
    public void setSize(final int width, final int height) {

        super.setSize(width, height);
        this.ortho();
        this.defaultCamera();
    }

    /**
     * Returns the string representation of this renderer.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return Zup3.PATH_STR;
    }

    /**
     * Sets lighting normals.
     *
     * @param num  the index
     * @param xDir the direction x
     * @param yDir the direction y
     * @param zDir the direction z
     */
    @Override
    protected void lightNormal(
        final int num,
        final float xDir,
        final float yDir,
        final float zDir) {

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
        if (0.0f < mSq) {
            final float mInv = Utils.invSqrtUnchecked(mSq);

            // this.lightNormal[num3] = mInv * nx;
            // this.lightNormal[num3 + 1] = mInv * ny;
            // this.lightNormal[num3 + 2] = mInv * nz;

            this.lightNormal[num3] = mInv * -nx;
            this.lightNormal[num3 + 1] = mInv * -ny;
            this.lightNormal[num3 + 2] = mInv * -nz;
        } else {
            this.lightNormal[num3] = 0.0f;
            this.lightNormal[num3 + 1] = 0.0f;
            this.lightNormal[num3 + 2] = 0.0f;
        }
    }
}

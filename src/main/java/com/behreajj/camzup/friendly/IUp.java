package com.behreajj.camzup.friendly;

import com.behreajj.camzup.core.Rgb;
import com.behreajj.camzup.core.TransformOrder;
import com.behreajj.camzup.core.Utils;
import com.behreajj.camzup.core.Vec2;
import processing.core.PApplet;

/**
 * Maintains consistent behavior across renderers in the CamZup library.
 */
public interface IUp {

    /**
     * Maximum color mode scale. To mitigate precision issues resulting from
     * float-int 32 color conversion.
     */
    float COLOR_MODE_MAX = 1000.0f;

    /**
     * Minimum color mode scale..
     */
    float COLOR_MODE_MIN = 1.0f;

    /**
     * Default camera aspect ratio used by perspective camera when size is less
     * than 128.
     */
    float DEFAULT_ASPECT = 1.0f;

    /**
     * An off-white background color, (1.0, 0.9686, 0.8352, 1.0) in RGBA.
     */
    int DEFAULT_BKG_COLOR = 0xfffff7d5;

    /**
     * Default scalar by which the height of the sketch is multiplied when the
     * default camera function is called.
     */
    float DEFAULT_CAM_DIST_FAC = Utils.SQRT_3_2;

    /**
     * The Processing default color max.
     */
    float DEFAULT_COLOR_MAX = 255.0f;

    /**
     * Default far-clip when orthographic or perspective functions are called
     * without the near and far arguments.
     */
    float DEFAULT_FAR_CLIP = 1500.0f;

    /**
     * Default fill color, a light blue, (0.6039, 0.8470, 0.8862, 1.0) in RGBA.
     */
    int DEFAULT_FILL_COLOR = 0xff9ad8e2;

    /**
     * Default field of view for a perspective camera projection.
     */
    float DEFAULT_FOV = Utils.THIRD_PI;

    /**
     * Default used by orthographic camera when sketch size is less than 128.
     */
    float DEFAULT_HALF_HEIGHT = 64.0f;

    /**
     * Default used by orthographic camera when sketch size is less than 128.
     */
    float DEFAULT_HALF_WIDTH = 64.0f;

    /**
     * Color for the lines connected the forehandle, coordinate and rear handle
     * of a curve knot. Currently diagnostic only, and so not adjustable.
     */
    int DEFAULT_HANDLE_COLOR = 0xffeb605d;

    /**
     * Color for the curve knot coordinate. Currently diagnostic only, and so
     * not adjustable.
     */
    int DEFAULT_HANDLE_COORD_COLOR = 0xfffabb74;

    /**
     * Color for the curve knot forehandle. Currently diagnostic only, and so
     * not adjustable.
     */
    int DEFAULT_HANDLE_FORE_COLOR = 0xff239e95;

    /**
     * Color for the curve knot rearhandle. Currently diagnostic only, and so
     * not adjustable.
     */
    int DEFAULT_HANDLE_REAR_COLOR = 0xffbb1641;

    /**
     * Default color of the x axis when displayed by a camera's origin function.
     * (1.0, 0.1568, 0.1568) in RGB.
     */
    int DEFAULT_I_COLOR = 0xffff2828;

    /**
     * The scalar by which a sketch's dimensions are multiplied to find an
     * appropriate line length.
     */
    float DEFAULT_IJK_LINE_FAC = 0.35f;

    /**
     * Default stroke-weight of an origin's lines.
     */
    float DEFAULT_IJK_SWEIGHT = 1.25f;

    /**
     * Default color of the y axis when displayed by a camera's origin function,
     * (0.0, 0.7019, 0.20) in RGB.
     */
    int DEFAULT_J_COLOR = 0xff00b333;

    /**
     * Default color of the z axis when displayed by a camera's origin function,
     * in (0.0784, 0.4588, 0.7019) in RGB.
     */
    int DEFAULT_K_COLOR = 0xff1475b3;

    /**
     * Default camera location on the horizontal axis.
     */
    float DEFAULT_LOC_X = 0.0f;

    /**
     * Default camera location on the vertical axis.
     */
    float DEFAULT_LOC_Y = 0.0f;

    /**
     * Default camera location on the depth axis.
     */
    float DEFAULT_LOC_Z = -623.53827f;

    /**
     * Default near-clip when orthographic or perspective functions are called
     * without the near and far arguments.
     */
    float DEFAULT_NEAR_CLIP = 0.015f;

    /**
     * Default transform order when converting a transform to a matrix.
     */
    TransformOrder DEFAULT_ORDER = TransformOrder.TRS;

    /**
     * Default stroke color, (0.1254, 0.1254, 0.1254) in RGB.
     */
    int DEFAULT_STROKE_COLOR = 0xff202020;

    /**
     * Default stroke weight.
     */
    float DEFAULT_STROKE_WEIGHT = 1.125f;

    /**
     * Default text leading.
     */
    float DEFAULT_TEXT_LEADING = 14.0f;

    /**
     * Default text size.
     */
    float DEFAULT_TEXT_SIZE = 12.0f;

    /**
     * The stroke weight of a coordinate point relative to a knot's line when
     * drawing handles.
     */
    float HANDLE_COORD_WEIGHT = 6.25f;

    /**
     * The stroke weight of a fore handle point relative to a knot's line when
     * drawing handles.
     */
    float HANDLE_FORE_WEIGHT = 5.0f;

    /**
     * The stroke weight of a rear handle point relative to a knot's line when
     * drawing handles.
     */
    float HANDLE_REAR_WEIGHT = 4.0f;

    /**
     * Gets the renderer's aspect ratio, width divided by height.
     *
     * @return the aspect ratio.
     */
    default float aspect() {

        return Utils.div(this.getWidth(), this.getHeight());
    }

    /**
     * Uses the renderer's default background color.
     */
    default void background() {
        this.background(IUp.DEFAULT_BKG_COLOR);
    }

    /**
     * Sets the renderer's background color to the hexadecimal value.
     *
     * @param c the color hexadecimal
     */
    void background(final int c);

    /**
     * Set the renderer's background color.
     *
     * @param c the color
     */
    default void background(final Rgb c) {

        this.background(c.toHexIntWrap());
    }

    /**
     * Sets the renderer's camera with default parameters.
     */
    void camera();

    /**
     * Sets the renderer's default styling.
     */
    void defaultSettings();

    /**
     * Uses the renderer's default fill color.
     */
    default void fill() {
        this.fill(IUp.DEFAULT_FILL_COLOR);
    }

    /**
     * Sets the renderer's current fill to the hexadecimal value.
     *
     * @param c the color in hexadecimal
     */
    void fill(final int c);

    /**
     * Sets the renderer's current fill to the color.
     *
     * @param c the color
     */
    default void fill(final Rgb c) {
        this.fill(c.toHexIntWrap());
    }

    /**
     * Gets the renderer's background color as an integer.
     *
     * @return the color
     */
    int getBackground();

    /**
     * Gets the renderer's background color.
     *
     * @param target the output color
     * @return the background
     */
    Rgb getBackground(Rgb target);

    /**
     * Gets the renderer's height.
     *
     * @return the height
     */
    float getHeight();

    /**
     * Gets the renderer's parent applet.
     *
     * @return the applet
     */
    PApplet getParent();

    /**
     * Gets the renderer's size.
     *
     * @param target the output vector
     * @return the size
     */
    Vec2 getSize(final Vec2 target);

    /**
     * Gets the renderer's width.
     *
     * @return the width
     */
    float getWidth();

    /**
     * Eases from an origin color to a destination by a step, where colors are
     * stored in integers.
     *
     * @param origin the origin color
     * @param dest   the destination color
     * @param step   the factor in [0, 1]
     * @return the color
     */
    int lerpColor(final int origin, final int dest, final float step);

    /**
     * Draws the world origin.
     */
    void origin();

    /**
     * Pop the last style off the end of the stack.
     */
    void popStyle();

    /**
     * Push a style onto the end of the stack.
     */
    void pushStyle();

    /**
     * Uses the renderer's default stroke color.
     */
    default void stroke() {
        this.stroke(IUp.DEFAULT_STROKE_COLOR);
    }

    /**
     * Sets the renderer's current stroke to the hexadecimal value.
     *
     * @param c the color in hexadecimal
     */
    void stroke(final int c);

    /**
     * Sets the renderer's current stroke to the color.
     *
     * @param c the color
     */
    default void stroke(final Rgb c) {

        this.stroke(c.toHexIntWrap());
    }

    /**
     * Uses the renderer's default stroke weight.
     */
    default void strokeWeight() {

        this.strokeWeight(IUp.DEFAULT_STROKE_WEIGHT);
    }

    /**
     * Sets the renderer's stroke weight.
     *
     * @param sw the weight
     */
    void strokeWeight(final float sw);
}

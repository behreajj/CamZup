package camzup.pfriendly;

import processing.core.PApplet;

import camzup.core.Color;
import camzup.core.IUtils;
import camzup.core.TransformOrder;
import camzup.core.Vec2;

/**
 * Maintains consistent behavior across renderers in the CamZup
 * library.
 */
public interface IUp {

  /**
   * Uses the renderer's default background color.
   */
  default void background ( ) { this.background(IUp.DEFAULT_BKG_COLOR); }

  /**
   * Set the renderer's background color.
   *
   * @param c the color
   */
  default void background ( final Color c ) {

    this.background(Color.toHexInt(c));
  }

  /**
   * Sets the renderer's background color to the hexadecimal value.
   *
   * @param c the color hexadecimal
   */
  void background ( final int c );

  /**
   * Sets the renderer's camera with default parameters.
   */
  void camera ( );

  /**
   * Sets the camera to the renderer defaults.
   */
  void defaultCamera ( );

  /**
   * Sets the renderer's default styling.
   */
  void defaultSettings ( );

  /**
   * Uses the renderer's default fill color.
   */
  default void fill ( ) { this.fill(IUp.DEFAULT_FILL_COLOR); }

  /**
   * Sets the renderer's current fill to the color.
   *
   * @param c the color
   */
  default void fill ( final Color c ) { this.fill(Color.toHexInt(c)); }

  /**
   * Sets the renderer's current fill to the hexadecimal value.
   *
   * @param c the color in hexadecimal
   */
  void fill ( final int c );

  /**
   * Gets the renderer's height.
   *
   * @return the height
   */
  float getHeight ( );

  /**
   * Gets the renderer's parent applet.
   *
   * @return the applet
   */
  PApplet getParent ( );

  /**
   * Gets the renderer's size.
   *
   * @param target the output vector
   * @return the size
   */
  Vec2 getSize ( final Vec2 target );

  /**
   * Gets the renderer's width.
   *
   * @return the width
   */
  float getWidth ( );

  /**
   * Eases from an origin color to a destination by a step.
   *
   * @param origin the origin color
   * @param dest   the destination color
   * @param step   the factor in [0, 1]
   * @param target the output color
   * @return the color
   */
  Color lerpColor (
      final Color origin,
      final Color dest,
      final float step,
      final Color target );

  /**
   * Eases from an origin color to a destination by a step, where colors
   * are stored in integers.
   *
   * @param origin the origin color
   * @param dest   the destination color
   * @param step   the factor in [0, 1]
   * @return the color
   */
  int lerpColor (
      final int origin,
      final int dest,
      final float step );

  /**
   * Draws the world origin.
   */
  void origin ( );

  /**
   * Uses the renderer's default stroke color.
   */
  default void stroke ( ) { this.stroke(IUp.DEFAULT_STROKE_COLOR); }

  /**
   * Sets the renderer's current stroke to the color.
   *
   * @param c the color
   */
  default void stroke ( final Color c ) { this.stroke(Color.toHexInt(c)); }

  /**
   * Sets the renderer's current stroke to the hexadecimal value.
   *
   * @param c the color in hexadecimal
   */
  void stroke ( final int c );

  /**
   * Uses the renderer's default stroke weight.
   */
  default void strokeWeight ( ) {

    this.strokeWeight(IUp.DEFAULT_STROKE_WEIGHT);
  }

  /**
   * Sets the renderer's stroke weight.
   *
   * @param sw the weight
   */
  void strokeWeight ( final float sw );

  /**
   * Sets the renderer's current stroke to the tint.
   *
   * @param c the color
   */
  default void tint ( final Color c ) { this.tint(Color.toHexInt(c)); }

  /**
   * Sets the renderer's current tint to the hexadecimal value.
   *
   * @param c the color in hexadecimal.
   */
  void tint ( final int c );

  /**
   * Default camera aspect ratio used by perspective camera when size is
   * less than 128. Assumes 1:1.
   */
  float DEFAULT_ASPECT = 1.0f;

  /**
   * An off-white background color, 255, 245, 215 in RGB.
   */
  int DEFAULT_BKG_COLOR = 0xfffff7d5;

  /**
   * Default scalar by which the height of the sketch is multiplied when
   * the default camera function is called.
   */
  float DEFAULT_CAM_DIST_FAC = IUtils.SQRT_3_2;

  /**
   * The Processing default color max.
   */
  float DEFAULT_COLOR_MAX = 255.0f;

  /**
   * Default far-clip when orthographic or perspective functions are
   * called without the near and far arguments. 1000.0 .
   */
  float DEFAULT_FAR_CLIP = 1000.0f;

  /**
   * Default fill color, a light blue.
   */
  int DEFAULT_FILL_COLOR = 0xff9ad8e2;

  /**
   * Default field of view for a perspective camera projection. PI / 3.0
   * .
   */
  float DEFAULT_FOV = IUtils.THIRD_PI;

  /**
   * When a non-zero value is supplied, a font glyph is flattened to
   * line segments.
   */
  float DEFAULT_GLYPH_DETAIL = 0.01f;

  /**
   * Default used by orthographic camera when sketch size is less than
   * 128.
   */
  float DEFAULT_HALF_HEIGHT = 64.0f;

  /**
   * Default used by orthographic camera when sketch size is less than
   * 128.
   */
  float DEFAULT_HALF_WIDTH = 64.0f;

  /**
   * Color for the lines connected the forehandle, coordinate and rear
   * handle of a curve knot. Currently diagnostic only, and so not
   * adjustable.
   */
  int DEFAULT_HANDLE_COLOR = 0xff080708;

  /**
   * Color for the curve knot coordinate. Currently diagnostic only, and
   * so not adjustable.
   */
  int DEFAULT_HANDLE_COORD_COLOR = 0xffff2828;

  /**
   * Color for the curve knot forehandle. Currently diagnostic only, and
   * so not adjustable.
   */
  int DEFAULT_HANDLE_FORE_COLOR = 0xff3772ff;

  /**
   * Color for the curve knot rearhandle. Currently diagnostic only, and
   * so not adjustable.
   */
  int DEFAULT_HANDLE_REAR_COLOR = 0xfffdca40;

  /**
   * Default color of the x axis when displayed by a camera's origin
   * function.
   */
  int DEFAULT_I_COLOR = 0xffff2929;

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
   * Default color of the y axis when displayed by a camera's origin
   * function.
   */
  int DEFAULT_J_COLOR = 0xff00b333;

  /**
   * Default color of the z axis when displayed by a camera's origin
   * function.
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
   * Default near-clip when orthographic or perspective functions are
   * called without the near and far arguments. 0.01 .
   */
  float DEFAULT_NEAR_CLIP = 0.001f;

  /**
   * Default transform order when converting a transform to a matrix.
   * TRS is short for Translation-Rotation-Scale.
   */
  TransformOrder DEFAULT_ORDER = TransformOrder.TRS;

  /**
   * Default stroke color.
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
   * A cached easing function for HSB colors.
   */
  Color.MixHsba MIXER_HSB = new Color.MixHsba();

  /**
   * A cached easing function for RGB colors.
   */
  Color.SmoothStepRgba MIXER_RGB = new Color.SmoothStepRgba();

}

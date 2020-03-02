package camzup.pfriendly;

/**
 * An enumeration to make the texture sampling options available in
 * Processing's OpenGL renderers (P2D and P3D) clearer. The integer
 * value of each is intended to match with integer constants in
 * Processing's Texture class. The enum's ordinal should not be used.
 *
 * @see processing.opengl.Texture
 */
public enum TextureSampling {

  /**
   * Bilinear sampling.
   */
  BILINEAR ( 4 ),

  /**
   * Linear sampling. Magnification filtering is nearest, minification
   * set to linear.
   */
  LINEAR ( 3 ),

  /**
   * Point sampling. Magnification and minification filtering are set to
   * nearest.
   */
  POINT ( 2 ),

  /**
   * Trilinear sampling.
   */
  TRILINEAR ( 5 );

  /**
   * The integer code of the constant.
   */
  private final int val;

  /**
   * The enumeration constructor.
   *
   * @param val the integer value
   */
  private TextureSampling ( final int val ) {

    this.val = val;
  }

  /**
   * Gets the integer code of the constant.
   *
   * @return the integer
   */
  public int getVal ( ) {

    return this.val;
  }

  /**
   * Gets a sampling constant from an integer value.
   *
   * @param i the integer
   * @return the constant
   */
  public static TextureSampling fromValue ( final int i ) {

    switch ( i ) {
      case 5:
        return TRILINEAR;
      case 4:
        return BILINEAR;
      case 3:
        return LINEAR;
      case 2:
      default:
        return POINT;
    }
  }
}

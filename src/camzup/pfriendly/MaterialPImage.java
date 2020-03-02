package camzup.pfriendly;

import processing.core.PConstants;
import processing.core.PImage;

import camzup.core.Chainable;
import camzup.core.Color;
import camzup.core.MaterialUv;
import camzup.core.Transform2;

/**
 * A material which supports PImages as textures.
 */
public class MaterialPImage extends MaterialUv {

  /**
   * The material's texture.
   */
  public PImage texture;

  /**
   * The default constructor.
   */
  public MaterialPImage ( ) {

    super("MaterialPImage");
    this.texture = new PImage(128, 128, PConstants.ARGB, 1);
    ZImage.fill(0xffffffff, this.texture);
  }

  /**
   * Constructs a material from a texture.
   *
   * @param texture the texture
   */
  public MaterialPImage ( final PImage texture ) {

    super("MaterialPImage");
    this.setTexture(texture);
  }

  /**
   * Constructs a name material with a fill and texture.
   *
   * @param name      the name
   * @param transform the UV transform
   * @param tint      the fill or tint
   * @param texture   the texture
   */
  public MaterialPImage (
      final String name,
      final Transform2 transform,
      final Color tint,
      final PImage texture ) {

    super(name, transform, tint);
    this.setTexture(texture);
  }

  /**
   * Gets the material's texture.
   *
   * @return the texture
   */
  public PImage getTexture ( ) {

    return this.texture;
  }

  /**
   * Sets the material's texture. If the supplied texture is null, then
   * a new texture is created and filled with a color.
   *
   * @param texture the texture
   * @return this material
   */
  @Chainable
  public MaterialPImage setTexture ( final PImage texture ) {

    if ( texture != null ) {
      this.texture = texture;
    } else {
      this.texture = new PImage(128, 128, PConstants.ARGB, 1);
      ZImage.fill(0xffffffff, this.texture);
    }
    return this;
  }

  /**
   * Returns a string representation of this material.
   *
   * @return the string
   */
  @Override
  public String toString ( ) {

    return this.toString(4);
  }

  /**
   * Returns a string representation of this material.
   *
   * @param places the number of places
   * @return the string
   */
  @Override
  public String toString ( final int places ) {

    return new StringBuilder(256)
        .append("{ name: \"")
        .append(this.name)
        .append("\", tint: ")
        .append(this.tint.toString(places))
        .append(", texture: ")
        .append(ZImage.toString(this.texture))
        .append(", transform: ")
        .append(this.transform.toString(places))
        .append(' ').append('}')
        .toString();
  }
}

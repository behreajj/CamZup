package com.behreajj.camzup.friendly;

import com.behreajj.camzup.core.MaterialUv;
import com.behreajj.camzup.core.Rgb;
import com.behreajj.camzup.core.Transform2;
import com.behreajj.camzup.core.Utils;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

/**
 * A material which supports {@link PImage}s as textures.
 */
public class MaterialPImage extends MaterialUv {

    /**
     * The material's texture.
     */
    public PImage texture;

    /**
     * The default constructor.
     */
    public MaterialPImage() {
    }

    /**
     * Constructs a material from a texture.
     *
     * @param texture the texture
     */
    public MaterialPImage(final PImage texture) {

        this.setTexture(texture);
    }

    /**
     * Constructs a material from a name.
     *
     * @param name the name
     */
    public MaterialPImage(final String name) {

        super(name);
        this.texture = new PImage(128, 128, PConstants.ARGB, 1);
        ZImage.fill(0xffffffff, this.texture);
    }

    /**
     * Constructs a named material with a fill and texture.
     *
     * @param name      the name
     * @param transform the UV transform
     * @param texture   the texture
     */
    public MaterialPImage(final String name, final Transform2 transform,
        final PImage texture) {

        super(name, transform);
        this.setTexture(texture);
    }

    /**
     * Constructs a named material with a texture.
     *
     * @param name      the name
     * @param transform the UV transform
     * @param texture   the texture
     * @param tint      the fill or tint
     */
    public MaterialPImage(
        final String name,
        final Transform2 transform,
        final PImage texture,
        final Rgb tint) {

        super(name, transform, tint);
        this.setTexture(texture);
    }

    /**
     * Tests this material for equivalence with another object.
     *
     * @param obj the object
     * @return the equivalence
     */
    @Override
    public boolean equals(final Object obj) {

        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || this.getClass() != obj.getClass()) {
            return false;
        }
        final MaterialPImage other = (MaterialPImage) obj;
        if (this.texture == null) {
            return other.texture == null;
        }
        return this.texture.equals(other.texture);
    }

    /**
     * Gets the material's texture.
     *
     * @return the texture
     */
    public PImage getTexture() {
        return this.texture;
    }

    /**
     * Sets the material's texture. If the supplied texture is null, then a new
     * texture is created and filled with a color.
     *
     * @param texture the texture
     * @return this material
     */
    public MaterialPImage setTexture(final PImage texture) {

        if (texture != null) {
            this.texture = ZImage.alphaToArgb(texture);
        } else {
            this.texture = new PImage(128, 128, PConstants.ARGB, 1);
            ZImage.fill(0xffffffff, this.texture);
        }

        return this;
    }

    /**
     * Returns a hash code for this material.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {

        final int prime = 31;
        final int result = super.hashCode();
        return prime * result + (this.texture == null ? 0
            : this.texture
            .hashCode());
    }

    /**
     * Sets the texture's parent {@link PApplet}. Useful when working with
     * {@link PImage}s created with a constructor that will be saved to file.
     *
     * @param parent the parent applet
     * @return this material
     */
    public MaterialPImage setTextureParent(final PApplet parent) {

        this.texture.parent = parent;
        return this;
    }

    /**
     * Returns a string representation of this material.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return this.toString(Utils.FIXED_PRINT);
    }

    /**
     * Returns a string representation of this material.
     *
     * @param places the number of places
     * @return the string
     */
    @Override
    public String toString(final int places) {

        return "{\"name\":\"" + this.name
            + "\",\"tint\":" + this.tint.toString(places)
            + ",\"texture\":" + ZImage.toString(this.texture)
            + ",\"transform\":" + this.transform.toString(places) + '}';
    }

}

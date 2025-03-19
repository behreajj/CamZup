package com.behreajj.camzup.friendly;

import com.behreajj.camzup.core.*;
import processing.core.PConstants;
import processing.core.PImage;

/**
 * A convenience to create image planes. Creates a mesh entity with a
 * rectangular mesh scaled to the
 * dimensions of an input image and a textured material.
 */
public class ImageEntity3 extends MeshEntity3 {

    /**
     * The texture material.
     */
    public final MaterialPImage material;

    /**
     * Creates an image entity from a texture. The tint defaults to white.
     *
     * @param texture the image texture
     */
    public ImageEntity3(final PImage texture) {

        this("ImageEntity3", texture, new Rgb(1.0f, 1.0f, 1.0f, 1.0f), 1.0f,
            PConstants.CENTER, PConstants.CENTER);
    }

    /**
     * Creates a named image entity from a texture.
     *
     * @param texture the image texture
     * @param tint    the tint
     */
    public ImageEntity3(final PImage texture, final Rgb tint) {

        this("ImageEntity3", texture, tint, 1.0f,
            PConstants.CENTER, PConstants.CENTER);
    }

    /**
     * Creates a named image entity from a texture. The tint defaults to white.
     *
     * @param name    the entity name
     * @param texture the image texture
     */
    public ImageEntity3(final String name, final PImage texture) {

        this(name, texture, new Rgb(1.0f, 1.0f, 1.0f, 1.0f), 1.0f,
            PConstants.CENTER, PConstants.CENTER);
    }

    /**
     * Creates a named image entity from a texture. The mesh's scale matches the
     * aspect ratio; the
     * entity's scale matches the image's longer edge.
     *
     * @param name       the entity name
     * @param texture    the image texture
     * @param tint       the tint
     * @param scale      the entity scale
     * @param alignHoriz the horizontal alignment
     * @param alignVert  the vertical alignment
     */
    public ImageEntity3(
        final String name,
        final PImage texture,
        final Rgb tint,
        final float scale,
        final int alignHoriz,
        final int alignVert) {

        super(name);

        final float w = texture.width;
        final float h = texture.height;
        final float shortEdge = Math.min(w, h);
        final float longEdge = Math.max(w, h);
        final float aspectRatio = Utils.div(shortEdge, longEdge);
        final Vec3 meshDim = new Vec3(
            shortEdge == w ? aspectRatio : 1.0f,
            shortEdge == h ? aspectRatio : 1.0f, 1.0f);
        final Vec3 meshOff = new Vec3();

        switch (alignHoriz) {
            case PConstants.LEFT: /* 37 */
                meshOff.x = 0.5f * meshDim.x;
                break;

            case PConstants.RIGHT: /* 39 */
                meshOff.x = -0.5f * meshDim.x;
                break;

            case PConstants.CENTER: /* 3 */
            default:
        }

        switch (alignVert) {
            case PConstants.TOP: /* 101 */
                meshOff.y = -0.5f * meshDim.y;
                break;

            case PConstants.BOTTOM: /* 102 */
                meshOff.y = 0.5f * meshDim.y;
                break;

            case PConstants.CENTER: /* 3 */
            default:
        }

        final Mesh3 mesh = Mesh3.square(PolyType.QUAD, new Mesh3());
        mesh.scale(meshDim);
        mesh.translate(meshOff);
        this.transform.scaleTo(scale * longEdge);
        this.meshes.add(mesh);
        this.material = new MaterialPImage(name, new Transform2(), texture, tint);
    }

    /**
     * Tests this entity for equality with another object.
     *
     * @param obj the object
     * @return the evaluation
     */
    @Override
    public boolean equals(final Object obj) {

        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || this.getClass() != obj.getClass()) {
            return false;
        }
        final ImageEntity3 other = (ImageEntity3) obj;
        if (this.material == null) {
            return other.material == null;
        }
        return this.material.equals(other.material);
    }

    /**
     * Calculates this entity's hash code.
     *
     * @return the hash
     */
    @Override
    public int hashCode() {

        final int prime = 31;
        return prime * super.hashCode() + (this.material == null ? 0 : this.material.hashCode());
    }

    /**
     * Sets the material's tint.
     *
     * @param clr the color
     * @return this entity
     */
    public ImageEntity3 setTint(final Rgb clr) {

        this.material.setTint(clr);
        return this;
    }
}

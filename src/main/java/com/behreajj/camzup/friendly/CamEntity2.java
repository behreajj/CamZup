package com.behreajj.camzup.friendly;

import com.behreajj.camzup.core.Entity2;
import com.behreajj.camzup.core.Transform2;
import com.behreajj.camzup.core.Vec2;
import processing.awt.PGraphicsJava2D;
import processing.opengl.PGraphicsOpenGL;

import java.awt.geom.AffineTransform;

/**
 * An entity which updates a renderer's camera according to its transform.
 */
public class CamEntity2 extends Entity2 {

    /**
     * The default constructor.
     */
    public CamEntity2() {
    }

    /**
     * Constructs a named entity. A new transform is created by the
     * constructor.
     *
     * @param name the name
     */
    public CamEntity2(final String name) {
        super(name);
    }

    /**
     * Constructs a named entity with a transform. The transform is assigned by
     * reference, and so it can be changed outside the entity.
     *
     * @param name      the name
     * @param transform the transform
     */
    public CamEntity2(final String name, final Transform2 transform) {

        super(name, transform);
    }

    /**
     * Constructs an entity with a transform. The transform is assigned by
     * reference, and so it can be changed outside the entity.
     *
     * @param transform the transform
     */
    public CamEntity2(final Transform2 transform) {
        super(transform);
    }

    /**
     * Updates a Java AWT renderer camera with this entity's transform.
     *
     * @param rndr the renderer
     * @return this entity
     */
    public CamEntity2 update(final PGraphicsJava2D rndr) {

        /* Get data from transform. */
        final Vec2 loc = new Vec2();
        final Vec2 scale = new Vec2(1.0f, 1.0f);
        final Vec2 right = new Vec2(1.0f, 0.0f);

        this.transform.getLocation(loc);
        this.transform.getScale(scale);
        this.transform.getRight(right);

        /* Promote floats to doubles. */
        final double cxd = loc.x;
        final double cyd = loc.y;
        final double czxd = scale.x;
        final double czyd = scale.y;
        final double cosa = right.x;
        final double sina = -right.y;

        /* Calculate axes. */
        final double m00 = cosa * czxd;
        final double m01 = -sina * czyd;
        final double m10 = sina * czxd;
        final double m11 = cosa * czyd;

        /* Update transform. */
        rndr.g2.setTransform(new AffineTransform(
            m00, -m10, m01, -m11,
            rndr.width * 0.5d - cxd * m00 - cyd * m01,
            rndr.height * 0.5d + cxd * m10 + cyd * m11));

        return this;
    }

    /**
     * Updates an OpenGL renderer with this entity's transform.
     *
     * @param rndr the renderer
     * @return this entity
     */
    public CamEntity2 update(final PGraphicsOpenGL rndr) {

        /* Get data from transform. */
        final Vec2 loc = new Vec2();
        final Vec2 scale = new Vec2(1.0f, 1.0f);
        final Vec2 right = new Vec2(1.0f, 0.0f);

        this.transform.getLocation(loc);
        this.transform.getScale(scale);
        this.transform.getRight(right);

        final float w = rndr.width < 128 ? 128.0f : rndr.width;
        final float h = rndr.height < 128 ? 128.0f : rndr.height;

        /* Update renderer data. */
        rndr.cameraX = loc.x;
        rndr.cameraY = loc.y;
        rndr.cameraZ = h;

        /* Unpack elements. Use negative rotation angle. */
        final float c = right.x;
        final float s = -right.y;
        final float xZoom = scale.x;
        final float yZoom = scale.y;

        /* Calculate the axes. */
        final float m00 = c * xZoom;
        final float m01 = -s * yZoom;
        final float m10 = s * xZoom;
        final float m11 = c * yZoom;

        rndr.modelview.set(
            m00, m01, 0.0f, -rndr.cameraX * m00 - rndr.cameraY * m01,
            m10, m11, 0.0f, -rndr.cameraX * m10 - rndr.cameraY * m11,
            0.0f, 0.0f, 1.0f, -rndr.cameraZ,
            0.0f, 0.0f, 0.0f, 1.0f);

        rndr.modelviewInv.set(
            c / xZoom, s / xZoom, 0.0f, rndr.cameraX,
            -s / yZoom, c / yZoom, 0.0f, rndr.cameraY,
            0.0f, 0.0f, 1.0f, rndr.cameraZ,
            0.0f, 0.0f, 0.0f, 1.0f);

        rndr.projection.set(
            2.0f / w, 0.0f, 0.0f, 0.0f,
            0.0f, 2.0f / h, 0.0f, 0.0f,
            0.0f, 0.0f, -0.0005f, -1.0f,
            0.0f, 0.0f, 0.0f, 1.0f);

        /* Set model view to camera. */
        rndr.camera.set(rndr.modelview);
        rndr.cameraInv.set(rndr.modelviewInv);
        rndr.projmodelview.set(rndr.projection);

        return this;
    }

}

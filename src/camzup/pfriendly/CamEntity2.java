package camzup.pfriendly;

import java.awt.geom.AffineTransform;

import camzup.core.Entity2;
import camzup.core.Experimental;
import camzup.core.Transform2;
import camzup.core.Vec2;

import processing.awt.PGraphicsJava2D;

import processing.opengl.PGraphicsOpenGL;

/**
 * An entity which updates a renderer's camera according to its transform.
 */
@Experimental
public class CamEntity2 extends Entity2 implements ICamEntity {

   /**
    * The default constructor.
    */
   public CamEntity2 ( ) {}

   /**
    * Constructs a named entity. A new transform is created by the
    * constructor.
    *
    * @param name the name
    */
   public CamEntity2 ( final String name ) { super(name); }

   /**
    * Constructs a named entity with a transform. The transform is assigned by
    * reference, and so it can be changed outside the entity.
    *
    * @param name      the name
    * @param transform the transform
    */
   public CamEntity2 ( final String name, final Transform2 transform ) {

      super(name, transform);
   }

   /**
    * Constructs an entity with a transform. The transform is assigned by
    * reference, and so it can be changed outside the entity.
    *
    * @param transform the transform
    */
   public CamEntity2 ( final Transform2 transform ) { super(transform); }

   /**
    * Updates a Java AWT renderer camera with this entity's transform.
    *
    * @param rndr the renderer
    *
    * @return this entity
    */
   public CamEntity2 update ( final PGraphicsJava2D rndr ) {

      /* Get data from transform. */
      final Vec2 loc = this.transform.getLocation(new Vec2());
      final Vec2 scale = this.transform.getScale(new Vec2());
      final Vec2 right = this.transform.getRight(new Vec2());

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
      rndr.g2.setTransform(new AffineTransform(m00, -m10, m01, -m11, rndr.width
         * 0.5d - cxd * m00 - cyd * m01, rndr.height * 0.5d + cxd * m10 + cyd
            * m11));

      return this;
   }

   /**
    * Updates an OpenGL renderer with this entity's transform.
    *
    * @param rndr the renderer
    *
    * @return this entity
    */
   public CamEntity2 update ( final PGraphicsOpenGL rndr ) {

      /* Get data from transform. */
      final Vec2 loc = this.transform.getLocation(new Vec2());
      final Vec2 scale = this.transform.getScale(new Vec2());
      final Vec2 right = this.transform.getRight(new Vec2());

      /* Update renderer data. */
      rndr.cameraX = loc.x;
      rndr.cameraY = loc.y;
      rndr.cameraZ = rndr.height < 128 ? 128.0f : rndr.height;

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

      /* @formatter:off */
      rndr.modelview.set(
          m00,  m01, 0.0f, -rndr.cameraX * m00 - rndr.cameraY * m01,
          m10,  m11, 0.0f, -rndr.cameraX * m10 - rndr.cameraY * m11,
         0.0f, 0.0f, 1.0f,                            -rndr.cameraZ,
         0.0f, 0.0f, 0.0f,                                     1.0f);

      rndr.modelviewInv.set(
          c / xZoom, s / xZoom, 0.0f, rndr.cameraX,
         -s / yZoom, c / yZoom, 0.0f, rndr.cameraY,
               0.0f,      0.0f, 1.0f, rndr.cameraZ,
               0.0f,      0.0f, 0.0f, 1.0f);
      /* @formatter:on */

      rndr.camera.set(rndr.modelview);
      rndr.cameraInv.set(rndr.modelviewInv);
      rndr.projmodelview.set(rndr.projection);

      return this;
   }

}

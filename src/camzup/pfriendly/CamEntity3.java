package camzup.pfriendly;

import camzup.core.Entity3;
import camzup.core.Experimental;
import camzup.core.Transform3;
import camzup.core.Vec3;

import processing.opengl.PGraphicsOpenGL;

/**
 * An entity which updates a renderer's camera according to its transform.
 */
@Experimental
public class CamEntity3 extends Entity3 {

   /**
    * The default constructor.
    */
   public CamEntity3 ( ) { super(); }

   /**
    * Constructs a named entity. A new transform is created by the
    * constructor.
    *
    * @param name the name
    */
   public CamEntity3 ( final String name ) { super(name); }

   /**
    * Constructs a named entity with a transform. The transform is assigned by
    * reference, and so it can be changed outside the entity.
    *
    * @param name      the name
    * @param transform the transform
    */
   public CamEntity3 ( final String name, final Transform3 transform ) {

      super(name, transform);
   }

   /**
    * Constructs an entity with a transform. The transform is assigned by
    * reference, and so it can be changed outside the entity.
    *
    * @param transform the transform
    */
   public CamEntity3 ( final Transform3 transform ) { super(transform); }

   /**
    * Updates an OpenGL renderer with this entity's transform.
    *
    * @param rndr the renderer
    *
    * @return this entity
    */
   public CamEntity3 update ( final PGraphicsOpenGL rndr ) {

      // TODO: Does this need to add frustum, orthographic and perspective?

      /* Get data from transform. */
      final Vec3 right = new Vec3();
      final Vec3 forward = new Vec3();
      final Vec3 up = new Vec3();
      this.transform.getAxes(right, forward, up);
      final Vec3 loc = this.transform.getLocation(new Vec3());

      /* Update renderer data. */
      final float m00 = right.x;
      final float m01 = right.y;
      final float m02 = right.z;

      final float m10 = up.x;
      final float m11 = up.y;
      final float m12 = up.z;

      final float m20 = forward.x;
      final float m21 = forward.y;
      final float m22 = forward.z;

      final float m30 = rndr.cameraX = loc.x;
      final float m31 = rndr.cameraY = loc.y;
      final float m32 = rndr.cameraZ = loc.z;

      /* Set inverse by column. */
      /* @formatter:off */
      rndr.cameraInv.set(
          m00,  m10,  m20,  m30,
          m01,  m11,  m21,  m31,
          m02,  m12,  m22,  m32,
         0.0f, 0.0f, 0.0f, 1.0f);

      /*
       * Set matrix to axes by row. Translate by a negative location after the
       * rotation.
       */
      rndr.camera.set(
          m00,  m01,  m02, -m30 * m00 - m31 * m01 - m32 * m02,
          m10,  m11,  m12, -m30 * m10 - m31 * m11 - m32 * m12,
          m20,  m21,  m22, -m30 * m20 - m31 * m21 - m32 * m22,
         0.0f, 0.0f, 0.0f, 1.0f);
      /* @formatter:on */

      /* Set model view to camera. */
      rndr.modelview.set(rndr.camera);
      rndr.modelviewInv.set(rndr.cameraInv);
      PMatAux.mul(rndr.projection, rndr.modelview, rndr.projmodelview);

      return this;
   }
}

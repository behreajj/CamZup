package camzup.pfriendly;

import camzup.core.Entity3;
import camzup.core.Experimental;
import camzup.core.Handedness;
import camzup.core.Transform3;
import camzup.core.Vec3;

import processing.opengl.PGraphicsOpenGL;

/**
 * An entity which updates a renderer's camera according to its transform.
 */
@Experimental
public class CamEntity3 extends Entity3 implements ICamEntity {

   /**
    * The default constructor.
    */
   public CamEntity3 ( ) {

      this.transform.moveTo(new Vec3(CamEntity3.DEFAULT_LOC_X,
         CamEntity3.DEFAULT_LOC_Y, CamEntity3.DEFAULT_LOC_Z));
      this.transform.lookAt(new Vec3(CamEntity3.DEFAULT_TARGET_X,
         CamEntity3.DEFAULT_TARGET_Y, CamEntity3.DEFAULT_TARGET_Z), 1.0f,
         Handedness.RIGHT);
   }

   /**
    * Constructs a named entity. A new transform is created by the
    * constructor.
    *
    * @param name the name
    */
   public CamEntity3 ( final String name ) {

      super(name);

      this.transform.moveTo(new Vec3(CamEntity3.DEFAULT_LOC_X,
         CamEntity3.DEFAULT_LOC_Y, CamEntity3.DEFAULT_LOC_Z));
      this.transform.lookAt(new Vec3(CamEntity3.DEFAULT_TARGET_X,
         CamEntity3.DEFAULT_TARGET_Y, CamEntity3.DEFAULT_TARGET_Z), 1.0f,
         Handedness.RIGHT);
   }

   /**
    * Constructs a named entity with a transform. The transform is assigned by
    * reference, and so it can be changed outside the entity.
    *
    * @param name      the name
    * @param transform the transform
    */
   public CamEntity3 ( final String name, final Transform3 transform ) {

      super(name, transform);

      this.transform.moveTo(new Vec3(CamEntity3.DEFAULT_LOC_X,
         CamEntity3.DEFAULT_LOC_Y, CamEntity3.DEFAULT_LOC_Z));
      this.transform.lookAt(new Vec3(CamEntity3.DEFAULT_TARGET_X,
         CamEntity3.DEFAULT_TARGET_Y, CamEntity3.DEFAULT_TARGET_Z), 1.0f,
         Handedness.RIGHT);
   }

   /**
    * Constructs an entity with a transform. The transform is assigned by
    * reference, and so it can be changed outside the entity.
    *
    * @param transform the transform
    */
   public CamEntity3 ( final Transform3 transform ) {

      super(transform);

      this.transform.moveTo(new Vec3(CamEntity3.DEFAULT_LOC_X,
         CamEntity3.DEFAULT_LOC_Y, CamEntity3.DEFAULT_LOC_Z));
      this.transform.lookAt(new Vec3(CamEntity3.DEFAULT_TARGET_X,
         CamEntity3.DEFAULT_TARGET_Y, CamEntity3.DEFAULT_TARGET_Z), 1.0f,
         Handedness.RIGHT);
   }

   /**
    * Sets a renderer projection matrix to the frustum defined by the edges of
    * the view port.
    *
    * @param rndr   the renderer
    * @param left   the left edge of the window
    * @param right  the right edge of the window
    * @param bottom the bottom edge of the window
    * @param top    the top edge of the window
    * @param near   the near clip plane
    * @param far    the far clip plane
    *
    * @return this entity
    */
   public CamEntity3 frustum ( final PGraphicsOpenGL rndr, final float left,
      final float right, final float bottom, final float top, final float near,
      final float far ) {

      rndr.cameraNear = near;
      rndr.cameraFar = far;

      PMatAux.frustum(left, right, bottom, top, near, far, rndr.projection);

      return this;
   }

   /**
    * Sets a renderer projection to orthographic, where objects maintain their
    * size regardless of distance from the camera.
    *
    * @param rndr the renderer
    *
    * @return this entity
    */
   public CamEntity3 ortho ( final PGraphicsOpenGL rndr ) {

      final float rEdge = rndr.width < 128 ? 64.0f : 0.5f * rndr.width;
      final float tEdge = rndr.height < 128 ? 64.0f : 0.5f * rndr.height;
      rndr.cameraNear = IUp.DEFAULT_NEAR_CLIP;
      rndr.cameraFar = IUp.DEFAULT_FAR_CLIP;

      PMatAux.orthographic(-rEdge, rEdge, -tEdge, tEdge, rndr.cameraNear,
         rndr.cameraFar, rndr.projection);

      return this;
   }

   /**
    * Sets a renderer projection to orthographic, where objects maintain their
    * size regardless of distance from the camera.
    *
    * @param rndr   the renderer
    * @param left   the left edge of the window
    * @param right  the right edge of the window
    * @param bottom the bottom edge of the window
    * @param top    the top edge of the window
    *
    * @return this entity
    */
   public CamEntity3 ortho ( final PGraphicsOpenGL rndr, final float left,
      final float right, final float bottom, final float top ) {

      rndr.cameraNear = IUp.DEFAULT_NEAR_CLIP;
      rndr.cameraFar = IUp.DEFAULT_FAR_CLIP;

      PMatAux.orthographic(left, right, bottom, top, rndr.cameraNear,
         rndr.cameraFar, rndr.projection);

      return this;
   }

   /**
    * Sets a renderer projection to orthographic, where objects maintain their
    * size regardless of distance from the camera.
    *
    * @param rndr   the renderer
    * @param left   the left edge of the window
    * @param right  the right edge of the window
    * @param bottom the bottom edge of the window
    * @param top    the top edge of the window
    * @param near   the near clip plane
    * @param far    the far clip plane
    *
    * @return this entity
    */
   public CamEntity3 ortho ( final PGraphicsOpenGL rndr, final float left,
      final float right, final float bottom, final float top, final float near,
      final float far ) {

      rndr.cameraNear = near;
      rndr.cameraFar = far;

      PMatAux.orthographic(left, right, bottom, top, rndr.cameraNear,
         rndr.cameraFar, rndr.projection);

      return this;
   }

   /**
    * Sets a renderer projection to a perspective, where objects nearer to the
    * camera appear larger than objects distant from the camera.
    *
    * @param rndr the renderer
    *
    * @return this entity
    */
   public CamEntity3 perspective ( final PGraphicsOpenGL rndr ) {

      rndr.cameraFOV = IUp.DEFAULT_FOV;
      rndr.cameraAspect = rndr.height != 0.0f ? rndr.width
         / ( float ) rndr.height : 1.0f;
      rndr.cameraNear = IUp.DEFAULT_NEAR_CLIP;
      rndr.cameraFar = IUp.DEFAULT_FAR_CLIP;

      PMatAux.perspective(rndr.cameraFOV, rndr.cameraAspect, rndr.cameraNear,
         rndr.cameraFar, rndr.projection);

      return this;
   }

   /**
    * Sets a renderer projection to a perspective, where objects nearer to the
    * camera appear larger than objects distant from the camera.
    *
    * @param rndr the renderer
    * @param fov  the field of view
    *
    * @return this entity
    */
   public CamEntity3 perspective ( final PGraphicsOpenGL rndr,
      final float fov ) {

      rndr.cameraFOV = fov;
      rndr.cameraAspect = rndr.height != 0.0f ? rndr.width
         / ( float ) rndr.height : 1.0f;
      rndr.cameraNear = IUp.DEFAULT_NEAR_CLIP;
      rndr.cameraFar = IUp.DEFAULT_FAR_CLIP;

      PMatAux.perspective(fov, rndr.cameraAspect, rndr.cameraNear,
         rndr.cameraFar, rndr.projection);

      return this;
   }

   /**
    * Sets a renderer projection to a perspective, where objects nearer to the
    * camera appear larger than objects distant from the camera.
    *
    * @param rndr   the renderer
    * @param fov    the field of view
    * @param aspect the aspect ratio, width over height
    *
    * @return this entity
    */
   public CamEntity3 perspective ( final PGraphicsOpenGL rndr, final float fov,
      final float aspect ) {

      rndr.cameraFOV = fov;
      rndr.cameraAspect = aspect;
      rndr.cameraNear = IUp.DEFAULT_NEAR_CLIP;
      rndr.cameraFar = IUp.DEFAULT_FAR_CLIP;

      PMatAux.perspective(rndr.cameraFOV, rndr.cameraAspect, rndr.cameraNear,
         rndr.cameraFar, rndr.projection);

      return this;
   }

   /**
    * Sets a renderer projection to a perspective, where objects nearer to the
    * camera appear larger than objects distant from the camera.
    *
    * @param rndr   the renderer
    * @param fov    the field of view
    * @param aspect the aspect ratio, width over height
    * @param near   the near clip plane
    * @param far    the far clip plane
    *
    * @return this entity
    */
   public CamEntity3 perspective ( final PGraphicsOpenGL rndr, final float fov,
      final float aspect, final float near, final float far ) {

      rndr.cameraFOV = fov;
      rndr.cameraAspect = aspect;
      rndr.cameraNear = near;
      rndr.cameraFar = far;

      PMatAux.perspective(rndr.cameraFOV, rndr.cameraAspect, rndr.cameraNear,
         rndr.cameraFar, rndr.projection);

      return this;
   }

   /**
    * Updates an OpenGL renderer with this entity's transform.
    *
    * @param rndr the renderer
    *
    * @return this entity
    */
   public CamEntity3 update ( final PGraphicsOpenGL rndr ) {

      /* Get data from transform. */
      final Vec3 right = new Vec3();
      final Vec3 forward = new Vec3();
      final Vec3 up = new Vec3();
      final Vec3 loc = new Vec3();

      this.transform.getAxes(right, forward, up);
      this.transform.getLocation(loc);

      /* Define matrix elements. */
      final float m00 = right.x;
      final float m01 = right.y;
      final float m02 = right.z;

      final float m10 = up.x;
      final float m11 = up.y;
      final float m12 = up.z;

      /* Look backwards. */
      final float m20 = -forward.x;
      final float m21 = -forward.y;
      final float m22 = -forward.z;

      final float m30 = rndr.cameraX = loc.x;
      final float m31 = rndr.cameraY = loc.y;
      final float m32 = rndr.cameraZ = loc.z;

      /* @formatter:off */
      rndr.cameraInv.set(
          m00,  m10,  m20,  m30,
          m01,  m11,  m21,  m31,
          m02,  m12,  m22,  m32,
         0.0f, 0.0f, 0.0f, 1.0f);

      rndr.camera.set(
          m00,  m01,  m02, -m30 * m00 - m31 * m01 - m32 * m02,
          m10,  m11,  m12, -m30 * m10 - m31 * m11 - m32 * m12,
          m20,  m21,  m22, -m30 * m20 - m31 * m21 - m32 * m22,
         0.0f, 0.0f, 0.0f, 1.0f);
      /* @formatter:on */

      /* Set model view to camera. */
      rndr.modelview.set(rndr.camera);
      rndr.modelviewInv.set(rndr.cameraInv);

      return this;
   }

   /**
    * The default x location set by the constructor.
    */
   public static final float DEFAULT_LOC_X = 0.0f;

   /**
    * The default y location set by the constructor.
    */
   public static final float DEFAULT_LOC_Y = 0.0f;

   /**
    * The default z location set by the constructor.
    */
   public static final float DEFAULT_LOC_Z = 0.0f;

   /**
    * The default x look target set by the constructor.
    */
   public static final float DEFAULT_TARGET_X = 0.0f;

   /**
    * The default y look target set by the constructor.
    */
   public static final float DEFAULT_TARGET_Y = 350.7403f;

   /**
    * The default z look target set by the constructor.
    */
   public static final float DEFAULT_TARGET_Z = 0.0f;

}

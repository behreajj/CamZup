package camzup.pfriendly;

import processing.opengl.PGraphicsOpenGL;

/**
 * Maintains consistent behavior between camera entities.
 */
public interface ICamEntity {

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
   default ICamEntity frustum ( final PGraphicsOpenGL rndr, final float left,
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
   default ICamEntity ortho ( final PGraphicsOpenGL rndr ) {

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
   default ICamEntity ortho ( final PGraphicsOpenGL rndr, final float left,
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
   default ICamEntity ortho ( final PGraphicsOpenGL rndr, final float left,
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
   default ICamEntity perspective ( final PGraphicsOpenGL rndr ) {

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
   default ICamEntity perspective ( final PGraphicsOpenGL rndr,
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
   default ICamEntity perspective ( final PGraphicsOpenGL rndr, final float fov,
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
   default ICamEntity perspective ( final PGraphicsOpenGL rndr, final float fov,
      final float aspect, final float near, final float far ) {

      rndr.cameraFOV = fov;
      rndr.cameraAspect = aspect;
      rndr.cameraNear = near;
      rndr.cameraFar = far;

      PMatAux.perspective(rndr.cameraFOV, rndr.cameraAspect, rndr.cameraNear,
         rndr.cameraFar, rndr.projection);

      return this;
   }

}

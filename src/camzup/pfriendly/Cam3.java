package camzup.pfriendly;

import camzup.core.Entity;
import camzup.pfriendly.Convert;
import camzup.core.Transform3;
import camzup.core.Vec3;
import camzup.core.ITransform;

public class Cam3 extends Entity {
   
   /**
    * The entity's transform.
    */
   public final Transform3 transform;
   
   public final Up3 renderer;

   public Cam3(Up3 renderer) {
      this("Cam3", new Transform3(), renderer);
   }
   
   public Cam3(Transform3 transform, Up3 renderer) {
      this("Cam3", transform, renderer);
   }
   
   public Cam3(String name, Up3 renderer) {
      this(name, new Transform3(), renderer);
   }
   
   public Cam3(String name, Transform3 transform, Up3 renderer) {
      super(name);
      this.transform = transform;
      this.renderer = renderer;
   }
   
   public void look(final Vec3 dir, final Vec3 refUp) {
      renderer.refUp.set(refUp);
      
      Vec3.normalize(dir, renderer.k);
      Vec3.crossNorm(renderer.k, refUp, renderer.i);
      Vec3.crossNorm(renderer.i, renderer.k, renderer.j);
      Transform3.fromAxes(
            renderer.i, 
            renderer.k,
            renderer.j, 
            transform);
      
      transform.moveTo(
            renderer.cameraX, 
            renderer.cameraY, 
            renderer.cameraZ);
      
      Convert.toPMatrix3D(transform, ITransform.Order.RST, renderer.modelviewInv);
      IUp.invert(renderer.modelviewInv, renderer.modelview);
      renderer.camera.set(renderer.modelview);
      renderer.cameraInv.set(renderer.modelviewInv);
      renderer.updateProjmodelview();
   }
}

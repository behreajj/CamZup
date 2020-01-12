package camzup.pfriendly;

import camzup.core.Entity;
import camzup.core.ITransform;
import camzup.core.Transform3;
import camzup.core.Vec3;

public class Cam3 extends Entity {

   //TODO: EXPERIMENTAL.
   
   public final Up3 renderer;

   /**
    * The entity's transform.
    */
   public final Transform3 transform;

   public Cam3 ( final String name, final Transform3 transform,
         final Up3 renderer ) {

      super(name);
      this.transform = transform;
      this.renderer = renderer;
   }

   public Cam3 ( final String name, final Up3 renderer ) {

      this(name, new Transform3(), renderer);
   }

   public Cam3 ( final Transform3 transform, final Up3 renderer ) {

      this("Cam3", transform, renderer);
   }

   public Cam3 ( final Up3 renderer ) {

      this("Cam3", new Transform3(), renderer);
   }

   public void look ( final Vec3 dir, final Vec3 refUp ) {

      this.renderer.refUp.set(refUp);

      Vec3.normalize(dir, this.renderer.k);
      Vec3.crossNorm(this.renderer.k, refUp, this.renderer.i);
      Vec3.crossNorm(this.renderer.i, this.renderer.k, this.renderer.j);
      Transform3.fromAxes(
            this.renderer.i,
            this.renderer.k,
            this.renderer.j,
            this.transform);

      this.transform.moveTo(
            this.renderer.cameraX,
            this.renderer.cameraY,
            this.renderer.cameraZ);

      Convert.toPMatrix3D(this.transform, ITransform.Order.RST,
            this.renderer.modelviewInv);
      IUp.invert(this.renderer.modelviewInv, this.renderer.modelview);
      this.renderer.camera.set(this.renderer.modelview);
      this.renderer.cameraInv.set(this.renderer.modelviewInv);
      this.renderer.updateProjmodelview();
   }
}

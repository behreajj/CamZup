import camzup.pfriendly.*;
import camzup.core.*;

Yup3 graphics3;

MaterialSolid mat = new MaterialSolid()
  .setStroke(false)
  .setStroke(#07a0c3)
  .setFill(true)
  .setFill(#086788)
  .setStrokeWeight(1.5);

Transform3 transform = new Transform3()
  .moveTo(new Vec3(-200.0, 50.0, 75.0))
  .scaleTo(250.0);

Mesh3 poly;

MeshEntity3 entity;

void setup() {
  size(720, 405, "camzup.pfriendly.Yup3");
  graphics3 = (Yup3)getGraphics();

  poly = Mesh3.cube(new Mesh3());

  entity = new MeshEntity3(transform)
    .appendMesh(poly)
    .appendMaterial(mat);

  perspective();
}

void draw() {
  transform.rotateBy(0.01, new Vec3(0.0, 1.0, 1.0));

  surface.setTitle(Utils.toFixed(frameRate, 1));
  graphics3.background();
  graphics3.lights();

  graphics3.origin(75.0, 1.0);
  graphics3.shape(entity);
}

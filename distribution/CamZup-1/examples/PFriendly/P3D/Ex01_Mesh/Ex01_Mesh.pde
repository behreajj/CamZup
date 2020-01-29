import camzup.pfriendly.*;
import camzup.core.*;

Yup3 graphics3;

MaterialSolid mat = new MaterialSolid()
  //.setStroke(true)
  .setStroke(false)
  .setStroke(0xff000000 | ~#07a0c3)
  .setFill(true)
  .setFill(#086788)
  .setStrokeWeight(2.5);

Transform3 transform = new Transform3()
  .moveTo(new Vec3(100.0, 50.0, 75.0))
  .scaleTo(264.0);

Mesh3 poly;

MeshEntity3 entity;

void setup() {
  size(720, 405, "camzup.pfriendly.Yup3");
  graphics3 = (Yup3)getGraphics();

  poly = Mesh3.dodecahedron(new Mesh3());

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

void mouseReleased() {
  saveStrings("mesh.obj", 
    new String[] { entity.meshes.get(0).toObjString() });
  println("Saved mesh.obj");
}

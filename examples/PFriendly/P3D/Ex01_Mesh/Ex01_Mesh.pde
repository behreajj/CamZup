import camzup.pfriendly.*;
import camzup.core.*;

Zup3 graphics3;

MaterialSolid mat = new MaterialSolid()
  .setStroke(true)
  .setStroke(#07a0c3)
  .setFill(true)
  .setFill(#086788)
  .setStrokeWeight(.001);

Transform3 transform = new Transform3()
  .moveTo(new Vec3(-200.0, 50.0, 75.0))
  .scaleTo(250.0);

Mesh3 poly;

MeshEntity3 entity;

void setup() {
  size(720, 405, "camzup.pfriendly.Zup3");
  graphics3 = (Zup3)getGraphics();

  poly = Mesh3.uvSphere(32, 16, 0.5, new Mesh3());
  //poly = Mesh3.cube(new Mesh3());
  //poly = Mesh3.polygon(new Mesh3(), 5);

  entity = new MeshEntity3(transform)
    .appendMesh(poly)
    .appendMaterial(mat);

  perspective();
}

void draw() {
  transform.rotate(0.01, new Vec3(0.0, 0.0, 1.0));

  surface.setTitle(String.format("%.1f", frameRate));
  background(#fff7d5);
  lights();

  graphics3.origin(75.0, 1.0);
  graphics3.shape(entity);
}

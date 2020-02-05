import camzup.pfriendly.*;
import camzup.core.*;

Zup3 graphics3;
Vec3 rotAxis = new Vec3(0.0, 0.6, 0.8);

MeshEntity3[] entities = {
  new MeshEntity3()
  .appendMaterial(new MaterialSolid().setFill(#e8455d))
  .appendMesh(Mesh3.tetrahedron(new Mesh3())),

  new MeshEntity3()
  .appendMaterial(new MaterialSolid().setFill(#ffabb1))
  .appendMesh(Mesh3.cube(new Mesh3())),

  new MeshEntity3()
  .appendMaterial(new MaterialSolid().setFill(#f6c89a))
  .appendMesh(Mesh3.octahedron(new Mesh3())),

  new MeshEntity3()
  .appendMaterial(new MaterialSolid().setFill(#0cc0de))
  .appendMesh(Mesh3.dodecahedron(new Mesh3())),

  new MeshEntity3()
  .appendMaterial(new MaterialSolid().setFill(#113b49))
  .appendMesh(Mesh3.icosahedron(new Mesh3()))
};

void settings() {
  size(720, 405, "camzup.pfriendly.Zup3");
}

void setup() {
  graphics3 = (Zup3)getGraphics();

  Vec3 lb = new Vec3(-width * 0.4, 0.0, 0.0);
  Vec3 ub = new Vec3(width * 0.4, 0.0, 0.0);
  float scl = min(width, height) * 0.25;
  int len = entities.length;
  for (int i = 0; i < len; ++i) {
    float prc = i * 0.25;
    Vec3 v = Vec3.mix(ub, lb, prc, new Vec3());
    MeshEntity3 me3 = entities[i];
    me3.scaleTo(scl);
    me3.moveTo(v);
  }
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));
  graphics3.ortho();
  graphics3.lights();
  graphics3.background();
  graphics3.origin(75.0, 1.0);
  for (MeshEntity3 me3 : entities) {
    me3.rotateBy(0.01, rotAxis);
    graphics3.shape(me3);
  }
}

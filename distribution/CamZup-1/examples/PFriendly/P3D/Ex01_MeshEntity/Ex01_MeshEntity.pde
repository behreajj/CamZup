import camzup.pfriendly.*;
import camzup.core.*;

Yup3 graphics3;

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
  size(720, 405, "camzup.pfriendly.Yup3");
}

void setup() {
  graphics3 = (Yup3)getGraphics();
 
  float scl = Utils.min(width, height);
  Vec3 ub = new Vec3(scl, scl, scl);
  Vec3.mul(ub, 0.375, ub);
  Vec3 lb = Vec3.negate(ub, new Vec3());
  scl *= 0.25;

  for (int i = 0; i < 5; ++i) {
    float prc = i * 0.25;
    Vec3 v = Vec3.mix(ub, lb, prc, new Vec3());
    MeshEntity3 me3 = entities[i];
    me3.scaleTo(scl);
    me3.moveTo(v);
  }
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));

  graphics3.background();
  graphics3.ortho();
  graphics3.lights();
  graphics3.origin(75.0, 1.0);

  for (MeshEntity3 me3 : entities) {
    me3.rotateX(0.01);
    graphics3.shape(me3);
  }
}

import camzup.pfriendly.*;
import camzup.core.*;

Zup3 graphics3;
Vec3 rotAxis = new Vec3(0.0, 0.6, 0.8);

MaterialSolid[] materials;

MeshEntity3[] entities;

void settings() {
  size(720, 405, "camzup.pfriendly.Zup3");
}

void setup() {
  graphics3 = (Zup3)getGraphics();

  materials = new MaterialSolid[] {
    new MaterialSolid().setFill(#e8455d), 
    new MaterialSolid().setFill(#ffabb1), 
    new MaterialSolid().setFill(#f6c89a), 
    new MaterialSolid().setFill(#0cc0de), 
    new MaterialSolid().setFill(#113b49)
  };

  entities = new MeshEntity3[] {
    new MeshEntity3()
    .appendMesh(Mesh3.tetrahedron(new Mesh3())), 

    new MeshEntity3()
    .appendMesh(Mesh3.cube(new Mesh3())), 

    new MeshEntity3()
    .appendMesh(Mesh3.octahedron(new Mesh3())), 

    new MeshEntity3()
    .appendMesh(Mesh3.dodecahedron(new Mesh3())), 

    new MeshEntity3()
    .appendMesh(Mesh3.icosahedron(new Mesh3()))
  };

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
    me3.getMesh(0).setMaterialIndex(i);
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
    graphics3.shape(me3, materials);
  }
}

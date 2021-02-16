import camzup.pfriendly.*;
import camzup.core.*;

Zup3 graphics3;
MaterialSolid[] materials;
MeshEntity3 entity;

void settings() {
  size(720, 405, Zup3.PATH_STR);
}

void setup() {
  frameRate(60.0);
  graphics3 = (Zup3)getGraphics();

  materials = new MaterialSolid[] {
    new MaterialSolid().setFill(#e8455d),
    new MaterialSolid().setFill(#ffabb1),
    new MaterialSolid().setFill(#f6c89a),
    new MaterialSolid().setFill(#0cc0de),
    new MaterialSolid().setFill(#113b49)
  };

  entity = new MeshEntity3("Platonic Solids",
    Mesh3.tetrahedron(new Mesh3()),
    Mesh3.cube(new Mesh3()),
    Mesh3.octahedron(new Mesh3()),
    Mesh3.dodecahedron(new Mesh3()),
    Mesh3.icosahedron(new Mesh3()));

  Vec3 ub = new Vec3(2.5, 0.0, 0.0);
  Vec3 lb = Vec3.negate(ub, new Vec3());
  int len = entity.length();
  for (int i = 0; i < len; ++i) {
    float prc = i * 0.25;
    Vec3 v = Vec3.mix(ub, lb, prc, new Vec3());
    Mesh3 mesh = entity.get(i);
    mesh.translate(v);
    mesh.setMaterialIndex(i);
  }

  float scl = Utils.min(width, height) * 0.3;
  entity.scaleTo(scl);
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));

  graphics3.lights();
  graphics3.ortho();
  graphics3.camDimetric();
  graphics3.background();
  graphics3.origin(75.0, 1.0);

  entity.rotateX(0.01);
  graphics3.shape(entity, materials);
}

void mouseReleased() {
  String objs = entity.toObjString();
  saveStrings("data/entity.obj", new String[] { objs });
  println("OBJ file saved.");
}

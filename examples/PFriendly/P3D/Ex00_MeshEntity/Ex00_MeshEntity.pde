import camzup.pfriendly.*;
import camzup.core.*;

Zup3 graphics;
MaterialSolid[] materials;
MeshEntity3 entity;

void settings() {
  size(720, 405, Zup3.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (Zup3)getGraphics();

  materials = new MaterialSolid[] {
    new MaterialSolid().setFill(0xffe8455d),
    new MaterialSolid().setFill(0xffffabb1),
    new MaterialSolid().setFill(0xfff6c89a),
    new MaterialSolid().setFill(0xff0cc0de),
    new MaterialSolid().setFill(0xff113b49)
  };

  entity = new MeshEntity3("Platonic Solids",
    Mesh3.tetrahedron(new Mesh3()),
    Mesh3.cube(new Mesh3()),
    Mesh3.octahedron(new Mesh3()),
    Mesh3.dodecahedron(new Mesh3()),
    Mesh3.icosahedron(new Mesh3()));

  Vec3 ub = new Vec3(2.5f, 0.0f, 0.0f);
  Vec3 lb = Vec3.negate(ub, new Vec3());
  int len = entity.length();
  for (int i = 0; i < len; ++i) {
    float prc = i * 0.25f;
    Vec3 v = Vec3.mix(ub, lb, prc, new Vec3());
    Mesh3 mesh = entity.get(i);
    mesh.translate(v);
    mesh.setMaterialIndex(i);
  }

  float scl = 0.3f * Utils.min(width, height);
  entity.scaleTo(scl);
}

void draw() {
  graphics.lights();
  graphics.ortho();
  graphics.camDimetric();
  graphics.background();
  graphics.origin(75.0f, 1.0f);

  entity.rotateX(0.01f);
  graphics.shape(entity, materials);
}

void mouseReleased() {
  String objs = entity.toObjString();
  saveStrings("data/entity.obj", new String[] { objs });
  println("OBJ file saved.");
}

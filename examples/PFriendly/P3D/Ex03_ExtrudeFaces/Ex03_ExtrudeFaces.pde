import camzup.core.*;
import camzup.pfriendly.*;

Zup3 rndr;

Mesh3 mesh = new Mesh3();
MeshEntity3 entity = new MeshEntity3();
MaterialSolid[] materials;
Gradient grd = new Gradient(
  #d5dfff,
  #ffdf15,
  #ff2828, 
  #101010, 
  #303030);

void settings() {
  size(720, 405, Zup3.PATH_STR);
}

void setup() {
  rndr = (Zup3)getGraphics();

  Mesh3.dodecahedron(mesh);
  mesh.subdivFacesCenter(3);
  mesh.subdivFacesFan(2);

  Mesh3.castToSphere(mesh, mesh);
  mesh.clean();

  entity.scaleTo(325.0);
  entity.appendAll(Mesh3.detachFaces(mesh));

  int idx = 0;
  Vec3 center = new Vec3();
  Face3 face = new Face3();
  materials = new MaterialSolid[entity.length()];
  for (Mesh3 mesh : entity) {
    mesh.getFace(0, face);
    Face3.centerMean(face, center);
    Vec3.mul(center, 2.25, center);

    float rnd = Simplex.fbm(center,
      Simplex.DEFAULT_SEED, 32, 1.0, 0.65);
    rnd = Utils.abs(rnd);
    rnd = Utils.quantize(rnd, 10);

    float scl = Utils.lerp(0.825, 0.75, rnd);
    face.scaleLocal(scl, center);

    float amt = Utils.lerp(0.02, 0.075, rnd);
    mesh.extrudeFaces(amt, true);

    materials[idx] = new MaterialSolid()
      .setFill(Gradient.eval(grd, rnd));
    mesh.materialIndex = idx;

    ++idx;
  }
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));

  entity.rotateZ(0.01);

  rndr.lights();
  rndr.ortho();
  rndr.camera();
  rndr.background();
  rndr.shape(entity, materials);
}

void mouseReleased() {
  String pyCd = entity.toBlenderCode(materials);
  saveStrings("data/extrude.py", new String[] { pyCd });
  println("Python code saved.");
}

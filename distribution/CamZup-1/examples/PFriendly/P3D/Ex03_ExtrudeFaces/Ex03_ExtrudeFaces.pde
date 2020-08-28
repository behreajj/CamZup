import camzup.core.*;
import camzup.pfriendly.*;

Zup3 rndr;

Mesh3 mesh = new Mesh3();
MeshEntity3 entity = new MeshEntity3();
MaterialSolid[] materials;
Gradient grd = Gradient.paletteMagma(new Gradient());

void settings() {
  size(720, 405, Zup3.PATH_STR);
}

void setup() {
  rndr = (Zup3)getGraphics();

  Mesh3.dodecahedron(mesh);
  //Mesh3.torus(0.5, 16, 8, Mesh.PolyType.QUAD, mesh);
  mesh.subdivFacesCenter(1);
  mesh.subdivFacesInscribe(2);
  mesh.subdivFacesCenter(1);
  Mesh3.castToSphere(mesh, mesh);
  mesh.shadeFlat();
  mesh.clean();

  entity.scaleTo(300.0);
  entity.appendAll(Mesh3.detachFaces(mesh));


  grd.reverse();
  int idx = 0;
  Vec3 center = new Vec3();
  materials = new MaterialSolid[entity.length()];
  for (Mesh3 mesh : entity) {
    Face3 face = new Face3();
    mesh.getFace(0, face);
    face.scaleLocal(0.825, center);

    Vec3.mul(center, 1.5, center);
    float rnd = Simplex.fbm(center,
      Simplex.DEFAULT_SEED, 32, 1.0, 0.65);
    rnd = Utils.abs(rnd);
    rnd = Utils.quantize(rnd, 9);
    float amt = Utils.lerp(0.02, 0.15, rnd);
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

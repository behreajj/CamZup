import camzup.core.*;
import camzup.pfriendly.*;

Zup3 graphics;

float roughness = 2.375;
float taper = 0.75;
Mesh3 mesh = new Mesh3();
MeshEntity3 entity = new MeshEntity3();
MaterialSolid[] materials;
Gradient grd = Gradient.paletteViridis(new Gradient());

void settings() {
  size(720, 405, Zup3.PATH_STR);
}

void setup() {
  frameRate(60.0);
  graphics = (Zup3)getGraphics();

  Mesh3.dodecahedron(mesh);
  mesh.insetFaces(1, 0.75);
  mesh.subdivFacesCenter(2);
  mesh.subdivFacesFan(1);
  Mesh3.castToSphere(mesh, mesh);
  mesh.clean();

  entity.scaleTo(Utils.min(width, height));
  entity.appendAll(Mesh3.detachFaces(mesh));

  int idx = 0;
  Vec3 center = new Vec3();
  Face3 face = new Face3();
  materials = new MaterialSolid[entity.length()];
  for (Mesh3 mesh : entity) {
    mesh.getFace(0, face);
    Face3.centerMean(face, center);
    Vec3.mul(center, roughness, center);

    float rnd = Simplex.fbm(center,
      Simplex.DEFAULT_SEED, 32, 1.0, 0.65);
    rnd = Utils.abs(rnd);
    rnd = Utils.quantize(rnd, 10);

    float scl = Utils.lerp(0.825, 0.75, rnd);
    face.scaleLocal(scl, center);

    float amt = Utils.lerp(0.02, 0.0675, rnd);
    mesh.extrudeFaces(true, amt, taper);
    mesh.clean();

    materials[idx] = new MaterialSolid();
    Gradient.eval(grd, rnd, materials[idx].fill);
    mesh.materialIndex = idx;

    ++idx;
  }
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));

  entity.rotateZ(0.01);

  graphics.lights();
  graphics.ortho();
  graphics.camera();
  graphics.background();
  graphics.shape(entity, materials);
}

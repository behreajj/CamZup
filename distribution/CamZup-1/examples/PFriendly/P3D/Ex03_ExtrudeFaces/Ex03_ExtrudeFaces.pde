import camzup.core.*;
import camzup.pfriendly.*;

Zup3 graphics;

float roughness = 2.375f;
float taper = 0.75f;
Mesh3 mesh = new Mesh3();
MeshEntity3 entity = new MeshEntity3();
MaterialSolid[] materials;
Gradient grd = Gradient.paletteViridis(new Gradient());
Rng gen = new Rng();

void settings() {
  size(720, 405, Zup3.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (Zup3)getGraphics();

  Mesh3.dodecahedron(mesh);
  mesh.insetFaces(1, 0.75f);
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

    float rnd = gen.uniform(0.001f, 1.0f);
    rnd = Utils.quantize(rnd, 5);

    float scl = Utils.lerp(0.825f, 0.75f, rnd);
    face.scaleLocal(scl, center);

    float amt = Utils.lerp(0.02f, 0.0675f, rnd);
    mesh.extrudeFaces(true, amt, taper);
    mesh.clean();

    materials[idx] = new MaterialSolid();
    Gradient.eval(grd, rnd, materials[idx].fill);
    mesh.materialIndex = idx;

    ++idx;
  }
}

void draw() {  
  entity.rotateZ(0.01f);

  graphics.lights();
  graphics.ortho();
  graphics.camera();
  graphics.background();
  graphics.shape(entity, materials);
}

void mouseReleased() {
  //String objstr = entity.toObjString();
  //saveStrings("data/extrusion.obj", new String[] { objstr });
  //println("Saved obj file.");
}

import camzup.core.*;
import camzup.pfriendly.*;

Zup3 rndr;

Mesh2 plane2 = Mesh2.plane(64, new Mesh2());
Mesh3 plane3 = new Mesh3(plane2);

MeshEntity3 entity = new MeshEntity3()
  .append(plane3);

boolean wireframe = true;

MaterialSolid fill = new MaterialSolid()
  .setStroke(false)
  .setFill(#007fff);

MaterialSolid stroke = new MaterialSolid()
  .setFill(false)
  .setStrokeWeight(1.025)
  .setStroke(true)
  .setStroke(#202020);

void settings() {
  size(720, 405, Zup3.PATH_STR);
}

void setup() {
  rndr = (Zup3)getGraphics();
  entity.scaleTo(Utils.min(rndr.width, rndr.height));

  float elev = 0.15;
  float roughness = 3.0;
  Vec3 noiseIn = new Vec3();
  for (Vec3 co : plane3.coords) {
    Vec3.mul(co, roughness, noiseIn);
    float fac1 = Simplex.fbm(
      noiseIn, Simplex.DEFAULT_SEED,
      16, 1.0, 0.25);
    co.z = elev * fac1;
  }

  plane3.triangulate();
  plane3.calcNormals();
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));
  entity.rotateZ(0.01);
  rndr.background();
  rndr.ortho();
  rndr.camera();
  rndr.lights();
  rndr.shape(entity, wireframe ? stroke : fill);
}

void mouseReleased() {
  wireframe = !wireframe;
}

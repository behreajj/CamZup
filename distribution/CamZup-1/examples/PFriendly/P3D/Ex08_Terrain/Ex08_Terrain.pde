import camzup.core.*;
import camzup.pfriendly.*;

Zup3 rndr;

boolean wireframe = true;
Vec3 mouse1 = new Vec3();

Mesh3 plane3 = new Mesh3(Mesh2.plane(64, new Mesh2()));

MeshEntity3 entity = new MeshEntity3()
  .append(plane3);

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
      16, 2.0, 0.3375);
    co.z = elev * fac1;
  }

  plane3.triangulate();
  plane3.calcNormalsSmooth();
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));

  if (mousePressed) {
    rndr.mouse1s(mouse1);
    if (mouseButton == LEFT) {
      Vec3.mul(mouse1, 100.0, mouse1);
      rndr.moveByLocal(mouse1);
    } else if (mouseButton == CENTER) {
      Vec3.mul(mouse1, 37.5, mouse1);
      rndr.strafe(mouse1);
    }
  }

  rndr.background();
  rndr.grid(32);
  rndr.ortho();
  rndr.camera();
  rndr.lights();
  rndr.shape(entity, wireframe ? stroke : fill);
}

void keyReleased() {
  wireframe = !wireframe;
}

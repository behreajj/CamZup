import camzup.core.*;
import camzup.pfriendly.*;

Zup3 rndr;

boolean wireframe = true;
int count = 64;
float elev = 0.175;
float roughness = 3.0;

Vec3 mouse1 = new Vec3();
Vec3 noiseIn = new Vec3();
Vec3 voronoi = new Vec3();

Mesh2 plane2 = new Mesh2();
Mesh3 plane3 = new Mesh3();

MeshEntity3 entity = new MeshEntity3()
  .append(plane3);

MaterialSolid fill = new MaterialSolid()
  .setStroke(false)
  .setFill(#007fff);

MaterialSolid stroke = new MaterialSolid()
  .setFill(false)
  .setStrokeWeight(1.0)
  .setStroke(true)
  .setStroke(#003f7f);

void settings() {
  size(720, 405, Zup3.PATH_STR);
}

void setup() {
  rndr = (Zup3)getGraphics();
  entity.scaleTo(2.0 * Utils.min(rndr.width, rndr.height));
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));

  rndr.mouse1s(mouse1);
  Mesh2.plane(count, count, PolyType.TRI, plane2);
  plane3.set(plane2);

  float zOff = frameCount * 0.01;

  for (Vec3 co : plane3.coords) {
    Vec3.mul(co, roughness, noiseIn);
    noiseIn.z = zOff;
    float fac1 = Simplex.fbm(
      noiseIn, Simplex.DEFAULT_SEED,
      16, 2.0, 0.3375);
    float fac0 = Voronoi.eval(
      co, Simplex.DEFAULT_SEED,
      0.25, voronoi);
    float fac = Utils.lerp(fac0, fac1, 0.75);
    co.z = elev * fac;
  }
  plane3.shadeSmooth();

  if (mousePressed) {
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
  rndr.perspective();
  rndr.camera();
  rndr.lights();
  rndr.shape(entity, wireframe ? stroke : fill);
}

void keyReleased() {
  wireframe = !wireframe;
}

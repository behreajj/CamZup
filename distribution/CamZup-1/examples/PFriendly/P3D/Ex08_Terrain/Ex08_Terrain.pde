import camzup.core.*;
import camzup.pfriendly.*;

Zup3 graphics;

boolean wireframe = true;
int count = 64;
float elev = 0.175f;
float roughness = 3.0f;

Vec3 mouse1 = new Vec3();
Vec3 noiseIn = new Vec3();
Vec3 voronoi = new Vec3();

Mesh2 plane2 = new Mesh2();
Mesh3 plane3 = new Mesh3();

MeshEntity3 entity = new MeshEntity3()
  .append(plane3);

MaterialSolid fill = new MaterialSolid()
  .setStroke(false)
  .setFill(0xff007fff);

MaterialSolid stroke = new MaterialSolid()
  .setFill(false)
  .setStrokeWeight(1.0f)
  .setStroke(true)
  .setStroke(0xff003f7f);

void settings() {
  size(720, 405, Zup3.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (Zup3)getGraphics();
  graphics.camDimetric();
  graphics.ortho();
  entity.scaleTo(Utils.min(graphics.width, graphics.height));
}

void draw() {
  graphics.mouse1s(mouse1);
  Mesh2.plane(count, count, PolyType.TRI, plane2);
  plane3.set(plane2);

  float zOff = frameCount * 0.01f;

  for (Vec3 co : plane3.coords) {
    Vec3.mul(co, roughness, noiseIn);
    noiseIn.z = zOff;
    float fac1 = Simplex.fbm(
      noiseIn, Simplex.DEFAULT_SEED,
      16, 2.0f, 0.3375f);
    //float fac0 = Voronoi.eval(
    //  co, Simplex.DEFAULT_SEED,
    //  0.25f, voronoi);
    //float fac = Utils.lerp(fac0, fac1, 0.75f);
    float fac = fac1;
    co.z = elev * fac;
  }
  plane3.shadeSmooth();
  // plane3.shadeFlat();

  if (mousePressed) {
    if (mouseButton == LEFT) {
      Vec3.mul(mouse1, 100.0f, mouse1);
      graphics.moveByLocal(mouse1);
    } else if (mouseButton == CENTER) {
      Vec3.mul(mouse1, 37.5f, mouse1);
      graphics.strafe(mouse1);
    }
  }

  graphics.background();
  graphics.grid(32);
  graphics.lights();
  graphics.shape(entity, wireframe ? stroke : fill);
}

void keyReleased() {
  wireframe = !wireframe;
}

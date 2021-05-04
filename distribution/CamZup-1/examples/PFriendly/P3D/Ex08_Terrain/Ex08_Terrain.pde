import camzup.core.*;
import camzup.pfriendly.*;

Zup3 graphics;

boolean wireframe = false;
boolean flatShading = false;
int count = 32;
float elev = 0.175f;
float roughness = 3.0f;
float mix = 0.5f;

Vec3 mouse1 = new Vec3();
Vec3 noiseIn = new Vec3();
Vec3 noiseOut = new Vec3();
Vec4 voronoiIn = new Vec4();
Vec4 voronoiOut = new Vec4();

Mesh2 plane2 = new Mesh2();
Mesh3 plane3 = new Mesh3();

MeshEntity3 entity = new MeshEntity3()
  .append(plane3);

MaterialPImage fill;

MaterialSolid stroke = new MaterialSolid()
  .setFill(false)
  .setStrokeWeight(1.0f)
  .setStroke(true)
  .setStroke(0xff202020);

void settings() {
  size(720, 405, Zup3.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (Zup3)getGraphics();
  graphics.camDimetric();
  graphics.ortho();
  entity.scaleTo(Utils.min(graphics.width, graphics.height));
  fill = new MaterialPImage(loadImage("diagnostic.png"));
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));
  
  graphics.mouse1s(mouse1);
  Mesh2.plane(count, count, PolyType.TRI, plane2);
  plane3.set(plane2);

  float zOff = millis() * 0.0005f;
  float wOff = Utils.cos(zOff);
  float vOff = Utils.sin(zOff);

  for (Vec3 co : plane3.coords) {
    Vec3.mul(co, roughness, noiseIn);
    noiseIn.z = zOff;

    float fac1 = Simplex.fbm(
      noiseIn, Simplex.DEFAULT_SEED,
      16, 2.0f, 0.3375f, noiseOut);

    voronoiIn.set(co.x, co.y, vOff, wOff);

    float fac2 = Voronoi.eval(
      voronoiIn, Simplex.DEFAULT_SEED,
      0.2f, voronoiOut);

    float fac = Utils.lerp(fac1, fac2, mix);
    co.z = elev * fac;
  }

  if (flatShading) {
    plane3.shadeFlat();
  } else {
    plane3.shadeSmooth();
  }

  if (mousePressed) {
    if (mouseButton == LEFT) {
      Vec3.mul(mouse1, 100.0f, mouse1);
      graphics.moveByLocal(mouse1);
    } else if (mouseButton == CENTER) {
      Vec3.mul(mouse1, 37.5f, mouse1);
      graphics.strafe(mouse1);
    } else if (mouseButton == RIGHT) {
      mix = mouseX / (width - 1.0f);
    }
  }

  graphics.background();
  graphics.grid(32);
  graphics.lights();

  if (wireframe) {
    graphics.shape(entity, stroke);
  } else {
    graphics.shape(entity, fill);
  }
}

void keyReleased() {
  if (key == 'w' || key == 'W') {
    wireframe = !wireframe;
  } else if (key == 'f' || key == 'F') {
    flatShading = !flatShading;
  } else if (key == 's' || key == 'S') {
    String objs = entity.toObjString();
    saveStrings("data/terrain.obj", new String[] { objs });
    println("OBJ file saved.");
  }
}

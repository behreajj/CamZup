import camzup.core.*;
import camzup.pfriendly.*;

int lats = 16;
int lons = 32;
Vec3 mouse = new Vec3();

Zup3 rndr;

Mesh3 smooth = new Mesh3();
Mesh3 flat = new Mesh3();

MeshEntity3 me1 = new MeshEntity3();
MeshEntity3 me2 = new MeshEntity3();
MeshEntity3 me3 = new MeshEntity3();

MaterialPImage textured;
MaterialSolid wire = new MaterialSolid();

void settings() {
  size(720, 405, Zup3.PATH_STR);
}

void setup() {
  rndr = (Zup3)getGraphics();
  frameRate(60.0);
  rndr.textureSampling(TextureSampling.LINEAR);

  PImage txtr = createImage(512, 512, ARGB);
  ZImage.fill(Gradient.paletteRyb(new Gradient()), txtr);
  textured = new MaterialPImage(txtr);

  wire.setStroke(true)
    .setStroke(#202020)
    .setStrokeWeight(1.0)
    .setFill(false);

  me1.append(smooth);
  me2.append(flat);
  me3.append(smooth);

  me1.scaleTo(256);
  me2.scaleTo(256);
  me3.scaleTo(256);

  me1.moveBy(new Vec3(-275.0, 0.0, 0.0));
  me3.moveBy(new Vec3(275.0, 0.0, 0.0));
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));

  rndr.mouse1u(mouse);
  lons = Utils.lerp(3, 64, mouse.x);
  lats = Utils.lerp(1, 32, mouse.y);

  Mesh3.uvSphere(
    lons, lats,
    PolyType.QUAD,
    smooth);

  flat.set(smooth);
  flat.shadeFlat();

  me1.rotateZ(0.005);
  me2.rotateY(0.005);
  me3.rotateX(0.005);

  rndr.lights();
  rndr.ortho();
  rndr.camera();
  rndr.background();

  rndr.shape(me1, textured);
  rndr.shape(me2, textured);
  rndr.shape(me3, wire);
}

void mouseReleased() {
  String objs = me1.toObjString();
  saveStrings("data/uvsphere.obj", new String[] { objs });
  println("OBJ file saved.");
}

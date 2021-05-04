import camzup.core.*;
import camzup.pfriendly.*;

int lats = 16;
int lons = 32;
Vec3 mouse = new Vec3();

Zup3 graphics;

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
  frameRate(60.0f);
  graphics = (Zup3)getGraphics();
  graphics.textureSampling(TextureSampling.LINEAR);

  PImage txtr = createImage(512, 512, ARGB);
  Gradient ryb = Gradient.paletteRyb(new Gradient());
  ZImage.linear(ryb, txtr);
  textured = new MaterialPImage(txtr);

  wire.setStroke(true)
    .setStroke(0xff202020)
    .setStrokeWeight(1.0f)
    .setFill(false);

  me1.append(smooth);
  me2.append(flat);
  me3.append(smooth);

  me1.scaleTo(256.0f);
  me2.scaleTo(256.0f);
  me3.scaleTo(256.0f);

  me1.moveBy(new Vec3(-275.0f, 0.0f, 0.0f));
  me3.moveBy(new Vec3(275.0f, 0.0f, 0.0f));
}

void draw() {  
  graphics.mouse1u(mouse);
  lons = Utils.lerp(3, 64, mouse.x);
  lats = Utils.lerp(1, 32, mouse.y);

  Mesh3.uvSphere(
    lons, lats,
    PolyType.TRI,
    smooth);

  flat.set(smooth);
  flat.shadeFlat();

  me1.rotateZ(0.005f);
  me2.rotateY(0.005f);
  me3.rotateX(0.005f);

  graphics.lights();
  graphics.ortho();
  graphics.camera();
  graphics.background();

  graphics.shape(me1, textured);
  graphics.shape(me2, textured);
  graphics.shape(me3, wire);
}

void mouseReleased() {
  String objs = me1.toObjString();
  saveStrings("data/uvsphere.obj", new String[] { objs });
  println("OBJ file saved.");
}

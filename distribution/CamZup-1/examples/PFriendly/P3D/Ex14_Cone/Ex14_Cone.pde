import camzup.core.*;
import camzup.pfriendly.*;

int lons = 32;
Vec3 mouse = new Vec3();

Zup3 graphics;

Mesh3 smooth = new Mesh3();
Mesh3 flat = new Mesh3();

MeshEntity3 me1 = new MeshEntity3();
MeshEntity3 me2 = new MeshEntity3();
MeshEntity3 me3 = new MeshEntity3();

PImage txtr;
MaterialPImage textured;
MaterialSolid wire = new MaterialSolid();

void settings() {
  size(720, 405, Zup3.PATH_STR);
}

void setup() {
  graphics = (Zup3)getGraphics();
  frameRate(60.0);
  graphics.textureSampling(TextureSampling.BILINEAR);

  txtr = createImage(512, 512, ARGB);
  ZImage.rgb(txtr);
  textured = new MaterialPImage(txtr);

  wire.setStroke(true)
    .setStroke(#202020)
    .setStrokeWeight(1.0)
    .setFill(false);

  me1.append(smooth);
  me2.append(flat);
  me3.append(smooth);

  me1.scaleTo(196);
  me2.scaleTo(196);
  me3.scaleTo(196);

  me2.moveBy(new Vec3(-275.0, 0.0, 0.0));
  me3.moveBy(new Vec3(275.0, 0.0, 0.0));
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));

  graphics.mouse1u(mouse);
  lons = Utils.lerp(3, 48, mouse.x);

  Mesh3.cone(1.0, 0.5, lons, smooth);

  flat.set(smooth);
  flat.shadeFlat();

  me1.rotateZ(0.005);
  me2.rotateY(0.005);
  me3.rotateX(0.005);

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
  saveStrings("data/cone.obj", new String[] { objs });
  println("OBJ file saved.");
}

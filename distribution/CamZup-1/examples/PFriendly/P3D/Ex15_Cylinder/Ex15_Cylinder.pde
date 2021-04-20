import camzup.core.*;
import camzup.pfriendly.*;

int lons = 32;
Vec3 mouse = new Vec3();
Vec3 origin = new Vec3(0.0f, 0.0f, -0.5f);
Vec3 dest = new Vec3(0.0f, 0.0f, 0.5f);

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
  frameRate(60.0f);
  graphics = (Zup3)getGraphics();
  graphics.textureSampling(TextureSampling.BILINEAR);

  txtr = createImage(512, 512, ARGB);
  ZImage.rgb(txtr);
  textured = new MaterialPImage(txtr);

  wire.setStroke(true)
    .setStroke(0xff202020)
    .setStrokeWeight(1.0f)
    .setFill(false);

  me1.append(smooth);
  me2.append(flat);
  me3.append(smooth);

  me1.scaleTo(196.0f);
  me2.scaleTo(196.0f);
  me3.scaleTo(196.0f);

  me2.moveBy(new Vec3(-275.0f, 0.0f, 0.0f));
  me3.moveBy(new Vec3(275.0f, 0.0f, 0.0f));
}

void draw() {
  graphics.mouse1u(mouse);
  lons = Utils.lerp(3, 48, mouse.x);

  Mesh3.cylinder(origin, dest, lons, smooth);

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
  saveStrings("data/cylinder.obj", new String[] { objs });
  println("OBJ file saved.");
}

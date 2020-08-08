import camzup.core.*;
import camzup.pfriendly.*;

int itr = 3;
Vec3 mouse = new Vec3();

Zup3 rndr;

Mesh3 smooth = new Mesh3();
Mesh3 flat = new Mesh3();
Mesh3 skele = new Mesh3();

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
  frameRate(1000);
  rndr = (Zup3)getGraphics();
  rndr.textureSampling(TextureSampling.LINEAR);

  txtr = loadImage("icosanet.png");
  textured = new MaterialPImage(txtr);

  wire.setStroke(true)
    .setStroke(#202020)
    .setStrokeWeight(1.0)
    .setFill(false);

  me1.append(smooth);
  me2.append(flat);
  me3.append(skele);

  me1.scaleTo(256);
  me2.scaleTo(256);
  me3.scaleTo(256);

  me1.moveBy(new Vec3(-275.0, 0.0, 0.0));
  me3.moveBy(new Vec3(275.0, 0.0, 0.0));
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));

  rndr.mouse1u(mouse);
  itr = Utils.lerp(0, 3, mouse.x);

  Mesh3.icosphere(itr, smooth);
  flat.set(smooth);
  skele.set(smooth);

  flat.calcNormalsFlat();

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

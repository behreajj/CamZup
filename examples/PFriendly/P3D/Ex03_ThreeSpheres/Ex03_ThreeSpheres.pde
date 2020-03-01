import camzup.core.*;
import camzup.pfriendly.*;

Zup3 rndr;

Mesh3 icosphere = new Mesh3();
Mesh3 cubesphere = new Mesh3();
Mesh3 uvsphere = new Mesh3();

MeshEntity3 me = new MeshEntity3()
  .appendMesh(icosphere)
  .appendMesh(cubesphere)
  .appendMesh(uvsphere);

MaterialSolid solid = new MaterialSolid()
  .setStroke(true)
  .setStroke(#2856ff)
  .setStrokeWeight(1.025)
  .setFill(false);

PImage txtr;
MaterialPImage textured;
boolean wireframe = true;

void settings() {
  smooth(8);
  size(720, 405, "camzup.pfriendly.Zup3");
}

void setup() {
  rndr = (Zup3)getGraphics();
  rndr.textureSampling(TextureSampling.TRILINEAR);

  txtr = loadImage("diagnostic.png");
  textured = new MaterialPImage(txtr);

  Mesh3.icosphere(3, icosphere);
  Mesh3.cubeSphere(3, cubesphere);
  Mesh3.uvSphere(32, 16, uvsphere);

  float s = Utils.min(rndr.width, rndr.height) * 0.75;
  icosphere.translate(new Vec3(1.125, 0.0, 0.0));
  cubesphere.translate(new Vec3(-1.125, 0.0, 0.0));
  me.scaleTo(s);
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));
  me.rotateZ(0.01);
  rndr.background();
  rndr.perspective();
  rndr.camera();
  rndr.lights();

  if (wireframe) {
    rndr.shape(me, solid);
  } else {
    rndr.shape(me, textured);
  }
}

void mouseReleased() {
  wireframe = !wireframe;
}

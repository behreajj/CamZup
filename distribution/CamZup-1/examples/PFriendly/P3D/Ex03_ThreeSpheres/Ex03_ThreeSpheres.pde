import camzup.core.*;
import camzup.pfriendly.*;

Zup3 rndr;

Mesh3 icosphere = new Mesh3();
Mesh3 cubesphere = new Mesh3();
Mesh3 uvsphere = new Mesh3();

MeshEntity3 meIco = new MeshEntity3().append(icosphere);
MeshEntity3 meCube = new MeshEntity3().append(cubesphere);
MeshEntity3 meUv = new MeshEntity3().append(uvsphere);

MaterialSolid solid = new MaterialSolid()
  .setStroke(true)
  .setStroke(#2856ff)
  .setStrokeWeight(1.025)
  .setFill(false);

PImage txtr;
MaterialPImage textured;
boolean wireframe = true;

void settings() {
  size(720, 405, "camzup.pfriendly.Zup3");
  //smooth(8);
}

void setup() {
  rndr = (Zup3)getGraphics();
  rndr.textureSampling(TextureSampling.LINEAR);
  rndr.textureWrap(REPEAT);

  txtr = loadImage("diagnostic.png");
  textured = new MaterialPImage(txtr);

  Mesh3.icosphere(3, icosphere);
  Mesh3.cubeSphere(3, cubesphere);
  Mesh3.uvSphere(32, 16, uvsphere);

  float uniform = Utils.min(rndr.width, rndr.height) * 0.75;
  meIco.scaleTo(uniform);
  meCube.scaleTo(uniform);
  meUv.scaleTo(uniform);

  meIco.moveBy(new Vec3(uniform, 0.0, 0.0));
  meCube.moveBy(new Vec3(-uniform, 0.0, 0.0));
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));
  meIco.rotateZ(0.01);
  meCube.rotateZ(0.01);
  meUv.rotateZ(0.01);

  rndr.background();
  rndr.perspective();
  rndr.camera();
  rndr.lights();

  if (wireframe) {
    rndr.shape(meIco, solid);
    rndr.shape(meCube, solid);
    rndr.shape(meUv, solid);
  } else {
    rndr.shape(meIco, textured);
    rndr.shape(meCube, textured);
    rndr.shape(meUv, textured);
  }
}

void mouseReleased() {
  wireframe = !wireframe;
}

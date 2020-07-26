import camzup.core.*;
import camzup.pfriendly.*;

Zup3 rndr;

Mesh3 icosphere = new Mesh3();
Mesh3 cubesphere = new Mesh3();
Mesh3 uvsphere = new Mesh3();

MeshEntity3 meIco = new MeshEntity3();
MeshEntity3 meCube = new MeshEntity3();
MeshEntity3 meUv = new MeshEntity3();

MaterialSolid solid = new MaterialSolid()
  .setStroke(true)
  .setStroke(#202020)
  .setStrokeWeight(1.025)
  .setFill(false);

PImage txtr;
MaterialPImage textured;
boolean wireframe = false;
boolean useIcosaMap = false;

void settings() {
  size(720, 405, Zup3.PATH_STR);
}

void setup() {
  rndr = (Zup3)getGraphics();
  rndr.textureSampling(TextureSampling.LINEAR);
  rndr.textureWrap(REPEAT);

  txtr = useIcosaMap ?
    loadImage("icosanet.png") :
    loadImage("diagnostic.png");
  textured = new MaterialPImage(txtr);

  Mesh3.icosphere(3, icosphere);
  Mesh3.cubeSphere(3, cubesphere);
  Mesh3.uvSphere(32, 16, uvsphere);

  meIco.append(icosphere);
  meCube.append(cubesphere);
  meUv.append(uvsphere);

  float uniform = Utils.min(rndr.width, rndr.height) * 1.35;
  meIco.scaleTo(uniform);
  meCube.scaleTo(uniform);
  meUv.scaleTo(uniform);

  meUv.moveBy(new Vec3(uniform * 1.2, 0.0, 0.0));
  meCube.moveBy(new Vec3(-uniform * 1.2, 0.0, 0.0));
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));

  //rndr.lights();
  rndr.directionalLight(255.0, 245.0, 215.0, 0.0, -0.6, 0.8);
  rndr.camera();
  rndr.perspective();

  rndr.background();
  rndr.origin();

  meIco.rotateZ(0.01);
  meCube.rotateZ(0.01);
  meUv.rotateZ(0.01);

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

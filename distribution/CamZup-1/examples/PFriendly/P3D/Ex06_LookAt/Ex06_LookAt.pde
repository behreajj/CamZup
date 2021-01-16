import camzup.core.*;
import camzup.pfriendly.*;

Zup3 rndr;
MeshEntity3 entity;
MaterialPImage mat;
Vec3 mouse1 = new Vec3();

void settings() {
  size(720, 405, Zup3.PATH_STR);
}

void setup() {
  rndr = (Zup3)getGraphics();
  frameRate(60.0);

  String filePath = sketchPath() + "\\data\\debug.obj";
  entity = ParserObj.load(filePath, false);
  entity.scaleTo(256);

  PImage txtr = createImage(512, 512, ARGB);
  ZImage.rgb(txtr);
  mat = new MaterialPImage(txtr);
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));

  if (mousePressed) {
    rndr.mouse1s(mouse1);
    if (mouseButton == LEFT) {
      Vec3.mul(mouse1, 100.0, mouse1);
      rndr.moveByLocal(mouse1);
    } else if (mouseButton == CENTER) {
      Vec3.mul(mouse1, 37.5, mouse1);
      rndr.strafe(mouse1);
    } else if (mouseButton == RIGHT) {
      rndr.defaultCamera();
    }
  }

  Vec3 cmloc = rndr.getLocation(new Vec3());
  entity.lookAt(cmloc, 0.0175, Handedness.RIGHT);

  rndr.background(#101010);
  rndr.perspective();
  rndr.grid(16, 2.0, #7E7A6A, 1000.0);
  rndr.lights();

  rndr.shape(entity, mat);
}

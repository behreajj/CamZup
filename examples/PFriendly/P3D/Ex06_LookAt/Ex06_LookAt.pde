import camzup.core.*;
import camzup.pfriendly.*;

Yup3 graphics;
MeshEntity3 entity;
MaterialPImage mat;
Vec3 mouse1 = new Vec3();

void settings() {
  size(720, 405, Yup3.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (Yup3)getGraphics();

  String filePath = sketchPath() + "\\data\\debug.obj";
  entity = ParserObj.load(filePath, false);
  entity.scaleTo(256.0f);

  PImage txtr = createImage(512, 512, ARGB);
  ZImage.rgb(txtr);
  mat = new MaterialPImage(txtr);
}

void draw() {
  if (mousePressed) {
    graphics.mouse1s(mouse1);
    if (mouseButton == LEFT) {
      Vec3.mul(mouse1, 100.0f, mouse1);
      graphics.moveByLocal(mouse1);
    } else if (mouseButton == CENTER) {
      Vec3.mul(mouse1, 37.5f, mouse1);
      graphics.strafe(mouse1);
    } else if (mouseButton == RIGHT) {
      graphics.defaultCamera();
    }
  }

  Vec3 cmloc = graphics.getLocation(new Vec3());
  entity.lookAt(cmloc, 0.025f, graphics.handedness());

  graphics.background(0xff101010);
  graphics.ortho(0.325f);
  graphics.grid(16, 2.0f, 0xff7e7a6a, 1000.0f);
  graphics.lights();

  graphics.shape(entity, mat);
}

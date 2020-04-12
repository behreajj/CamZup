import camzup.core.*;
import camzup.pfriendly.*;

Zup3 rndr;
Mesh3 diagnostic = new Mesh3();
MeshEntity3 entity = new MeshEntity3();
MaterialPImage mat;
Vec3 mouse1 = new Vec3();

void settings() {
  size(720, 405, Zup3.PATH_STR);
  smooth(8);
}

void setup() {
  rndr = (Zup3)getGraphics();

  String[] strs = loadStrings("debug.obj");
  Mesh3.fromObj(strs, diagnostic);
  entity.append(diagnostic);
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
    } else if(mouseButton == RIGHT) {
      rndr.defaultCamera();
    }
  }

  Vec3 cmloc = rndr.getLocation(new Vec3());
  entity.lookAt(cmloc, 0.0175, Handedness.RIGHT);

  rndr.background(#202020);
  rndr.perspective();
  rndr.grid(16, 2.0, #7E7A6A, 1000.0);
  rndr.lights();

  rndr.shape(entity, mat);
}

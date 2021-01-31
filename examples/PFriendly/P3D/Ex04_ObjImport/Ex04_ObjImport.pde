import camzup.core.*;
import camzup.pfriendly.*;

Zup3 graphics;
MeshEntity3 me3;
MaterialSolid material;

void settings() {
  size(720, 405, Zup3.PATH_STR);
}

void setup() {
  frameRate(60.0);
  graphics = (Zup3)getGraphics();

  String filePath = sketchPath() + "\\data\\bunny.obj";

  long start = System.currentTimeMillis();
  me3 = ParserObj.load(filePath, false);
  long end = System.currentTimeMillis();
  println("Elapsed Time: " + (end - start));

  me3.get(0).reframe(me3.transform);
  me3.scaleTo(256);

  material = new MaterialSolid();
  material.setFill(#202020);
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));

  me3.rotateZ(0.02);

  graphics.background();
  graphics.origin();
  graphics.lights();
  graphics.shape(me3, material);
}

/* Illustration by George Brower. */

import camzup.core.*;
import camzup.pfriendly.*;

Yup2 graphics;
CurveEntity2 ce2;
MaterialSolid material = new MaterialSolid()
  .setFill(true)
  .setFill(0x7f202020)
  .setStrokeWeight(1.0f)
  .setStroke(0xff007fff)
  .setStroke(true);

void settings() {
  size(720, 405, Yup2.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (Yup2)getGraphics();

  String filePath = sketchPath() + "\\data\\bot1.svg";

  long start = System.currentTimeMillis();
  ce2 = ParserSvg.load(filePath);
  long end = System.currentTimeMillis();
  println("Elapsed Time: " + (end - start));

  ce2.reframe();
  ce2.scaleTo(2.0f * Utils.min(width, height));
}

void draw() {
  ce2.moveTo(graphics.mouse(new Vec2()));

  graphics.background();
  graphics.camFlipped();
  graphics.shape(ce2, material);
  if (mousePressed) {
    graphics.handles(ce2);
  }
}

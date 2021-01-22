/* Illustration by George Brower. */

import camzup.core.*;
import camzup.pfriendly.*;

YupJ2 graphics;
CurveEntity2 ce2;
MaterialSolid material;

void settings() {
  size(720, 405, YupJ2.PATH_STR);
}

void setup() {
  frameRate(60.0);
  graphics = (YupJ2)getGraphics();

  String filePath = sketchPath() + "\\data\\bot1.svg";

  long start = System.currentTimeMillis();
  ce2 = ParserSvg.load(filePath);
  long end = System.currentTimeMillis();
  println("Elapsed Time: " + (end - start));

  material = new MaterialSolid();
  material.setFill(false);
  material.setFill(0x3f007fff);
  material.setStrokeWeight(1.0);
  material.setStroke(0x3f303030);
  material.setStroke(true);
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));
  graphics.background();
  graphics.camFlipped();
  graphics.shape(ce2, material);
  if (mousePressed) {
    graphics.handles(ce2);
  }
}

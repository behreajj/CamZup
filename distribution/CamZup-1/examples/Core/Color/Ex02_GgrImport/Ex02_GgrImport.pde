import camzup.core.*;
import camzup.pfriendly.*;

Gradient grd;
PImage orig;

void settings() {
  size(720, 405, Yup2.PATH_STR);
}

void setup() {
  frameRate(60.0f);

  long start = System.currentTimeMillis();
  grd = ParserGgr.load(
    sketchPath() + "\\data\\sunrise.ggr", 16);
  long end = System.currentTimeMillis();
  println("Elapsed Time: " + (end - start));

  orig = createImage(width, height, ARGB);
  ZImage.gradientLinear(grd, orig);
}

void draw() {
  background(0xff202020);
  image(orig, 0.0f, 0.0f, width, height);
}

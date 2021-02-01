import camzup.core.*;
import camzup.pfriendly.*;

Gradient grd;
PImage orig;
PImage trg;

void settings() {
  size(720, 405, Yup2.PATH_STR);
}

void setup() {
  long start = System.currentTimeMillis();
  grd = ParserGgr.load(sketchPath() + "\\data\\sunrise.ggr", 16);
  long end = System.currentTimeMillis();
  println("Elapsed Time: " + (end - start));

  orig = createImage(width, height, ARGB);
  ZImage.fill(grd, orig);

  Gradient.inverse(grd, grd);
  trg = createImage(width, height, ARGB);
  ZImage.fill(grd, trg);
}

void draw() {
  background(#202020);
  image(trg, 0, height / 4, width, height / 2);
  image(orig, 0, -height / 4, width, height / 2);
}

void mouseReleased() {
  String ggr = grd.toGgrString("Sunrise Inverse");
  saveStrings("data/sunriseInverted.ggr", new String[] { ggr });
  System.out.println("Saved to GGR.");
}

import camzup.core.*;
import camzup.pfriendly.*;

Gradient grd;
PImage trg;

void settings() {
  size(720, 405, Yup2.PATH_STR);
}

void setup() {
  long start = System.currentTimeMillis();
  grd = ParserGgr.load(sketchPath() + "\\data\\Sunrise.ggr", 32);
  long end = System.currentTimeMillis();
  println("Elapsed Time: " + (end - start));

  trg = createImage(width, height, ARGB);
  ZImage.fill(grd, trg);
}

void draw() {
  background(#202020);
  image(trg, 0, 0);
}

void mouseReleased() {
  String ggr = grd.toGgrString();
  saveStrings("zup.ggr", new String[] { ggr });
  System.out.println("Saved to GGR.");
}

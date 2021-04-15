import camzup.core.*;
import camzup.pfriendly.*;

Gradient grd;
PImage orig;
PImage trg;
Vec4 shifter = new Vec4(-1.0f / 24.0f, 0.1f, -0.15f, 0.0f);

void settings() {
  size(720, 405, Yup2.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  
  long start = System.currentTimeMillis();
  grd = ParserGgr.load(sketchPath() + "\\data\\sunrise.ggr", 16);
  long end = System.currentTimeMillis();
  println("Elapsed Time: " + (end - start));

  orig = createImage(width, height, ARGB);
  ZImage.fill(grd, orig);

  Gradient.shiftHsva(grd, shifter, grd, new Color(), new Vec4());
  trg = createImage(width, height, ARGB);
  ZImage.fill(grd, trg);
}

void draw() {
  background(0xff202020);
  image(trg, 0.0f, height * 0.25f, width, height * 0.5f);
  image(orig, 0.0f, -height * 0.25f, width, height * 0.5f);
}

void mouseReleased() {
  String ggr = grd.toGgrString("Sunrise Shifted");
  saveStrings("data/sunriseShifted.ggr", new String[] { ggr });
  System.out.println("Saved to GGR.");
}

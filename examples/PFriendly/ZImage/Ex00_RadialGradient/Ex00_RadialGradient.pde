import camzup.core.*;
import camzup.pfriendly.*;

YupJ2 rndr;
PImage img;
Gradient gradient = new Gradient();

void settings() {
  size(512, 512, YupJ2.PATH_STR);
}

void setup() {
  rndr = (YupJ2)getGraphics();
  img = createImage(512, 512, ARGB);
  Gradient.paletteViridis(gradient);
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));

  Vec2 m = rndr.mouse1s(new Vec2());
  ZImage.radial(m, 0.5, gradient, img);
  rndr.image(img);
}

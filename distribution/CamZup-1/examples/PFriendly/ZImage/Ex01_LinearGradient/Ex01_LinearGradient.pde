import camzup.core.*;
import camzup.pfriendly.*;

YupJ2 rndr;
PImage img;
Gradient gradient = new Gradient();
Vec2 origin = new Vec2();
Vec2 dest = new Vec2();

void settings() {
  size(512, 512, YupJ2.PATH_STR);
}

void setup() {
  rndr = (YupJ2)getGraphics();
  img = createImage(512, 512, ARGB);
  Gradient.paletteRyb(gradient);
  origin.set(-1.0, -1.0);
  dest.set(1.0, 1.0);
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));

  ZImage.linear(origin, dest, gradient, img);
  rndr.image(img);
}

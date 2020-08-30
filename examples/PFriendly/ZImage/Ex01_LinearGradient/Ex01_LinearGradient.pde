import camzup.core.*;
import camzup.pfriendly.*;

YupJ2 rndr;
PImage img;
Gradient gradient = new Gradient();
Vec2 origin = new Vec2(-1.0, -1.0);
Vec2 dest = new Vec2(1.0, 1.0);

void settings() {
  size(512, 512, YupJ2.PATH_STR);
}

void setup() {
  rndr = (YupJ2)getGraphics();
  img = createImage(512, 512, ARGB);
  Gradient.paletteRyb(gradient);
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));

  rndr.mouse1s(origin);
  Vec2.negate(origin, dest);
  ZImage.linear(origin, dest, gradient, img);
  rndr.image(img);
}

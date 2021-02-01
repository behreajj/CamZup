import camzup.core.*;
import camzup.pfriendly.*;

Yup2 graphics;
PImage img;
Gradient gradient = new Gradient();
Vec2 origin = new Vec2(-1.0, -1.0);
Vec2 dest = new Vec2(1.0, 1.0);

void settings() {
  size(512, 512, Yup2.PATH_STR);
}

void setup() {
  frameRate(60.0);
  graphics = (Yup2)getGraphics();
  img = createImage(512, 512, ARGB);
  Gradient.paletteRyb(gradient);
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));

  graphics.mouse1s(origin);
  Vec2.negate(origin, dest);
  ZImage.linear(origin, dest, gradient, img);
  graphics.image(img);
}

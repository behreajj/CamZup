import camzup.core.*;
import camzup.pfriendly.*;

YupJ2 rndr;
PImage img;
Gradient gradient = new Gradient();
Vec2 origin = new Vec2();

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

  rndr.mouse1s(origin);
  ZImage.radial(origin, 0.5, gradient, img);
  rndr.image(img);
}

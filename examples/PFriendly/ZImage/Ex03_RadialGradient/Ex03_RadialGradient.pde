import camzup.core.*;
import camzup.pfriendly.*;

Yup2 graphics;
PImage img;
Gradient gradient = new Gradient();
Vec2 origin = new Vec2();

void settings() {
  size(512, 512, Yup2.PATH_STR);
}

void setup() {
  frameRate(60.0);
  graphics = (Yup2)getGraphics();
  img = createImage(512, 512, ARGB);
  Gradient.paletteViridis(gradient);
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));

  graphics.mouse1s(origin);
  ZImage.radial(origin, 0.5, gradient, img);
  graphics.background();
  graphics.image(img);
}

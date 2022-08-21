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
  frameRate(60.0f);
  graphics = (Yup2)getGraphics();
  img = createImage(512, 512, ARGB);
  Gradient.paletteMagma(gradient);
}

void draw() {
  graphics.mouse1s(origin);
  ZImage.gradientRadial(gradient, origin, 0.5f, img);
  graphics.background();
  graphics.image(img);
}

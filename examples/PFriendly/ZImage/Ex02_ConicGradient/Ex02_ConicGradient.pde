import camzup.core.*;
import camzup.pfriendly.*;

Yup2 graphics;
PImage img;
Gradient gradient = new Gradient();

void settings() {
  size(720, 405, Yup2.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (Yup2)getGraphics();
  img = createImage(512, 384, ARGB);
  Gradient.paletteViridis(gradient);
}

void draw() {
  Vec2 m = graphics.mouse1s(new Vec2());
  ZImage.conic(m, frameCount * 0.02f, gradient, img);
  
  graphics.background();
  graphics.image(img);
}

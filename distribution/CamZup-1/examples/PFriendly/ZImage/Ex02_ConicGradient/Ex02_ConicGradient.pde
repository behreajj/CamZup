import camzup.core.*;
import camzup.pfriendly.*;

Yup2 graphics;
PImage img;
Gradient gradient = new Gradient();

void settings() {
  size(720, 405, Yup2.PATH_STR);
}

void setup() {
  frameRate(60.0);
  graphics = (Yup2)getGraphics();
  img = createImage(384, 384, ARGB);
  Gradient.paletteViridis(gradient);
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));

  Vec2 m = graphics.mouse1s(new Vec2());
  ZImage.conic(m, frameCount * 0.02, gradient, img);
  
  graphics.background();
  graphics.image(img);
}

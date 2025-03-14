import camzup.core.*;
import camzup.pfriendly.*;

Yup2 graphics;
Img img = new Img(512, 512);
Gradient gradient = Gradient.paletteViridis(
  new Gradient());
Vec2 origin = new Vec2(-1.0f, -1.0f);
Rgb.AbstrToneMap mapper = new Rgb.ToneMapClamp();

void settings() {
  size(720, 405, Yup2.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (Yup2)getGraphics();
}

void draw() {
  float angle = frameCount * 0.02f;
  graphics.mouse1s(origin);
  Img.gradientSweep(gradient, origin, angle, img);
  PImage pimg = Convert.toPImage(img, mapper);

  graphics.background();
  graphics.image(pimg);
}

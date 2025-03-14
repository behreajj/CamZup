import camzup.core.*;
import camzup.pfriendly.*;

Yup2 graphics;
Img img = new Img(512, 512);
Gradient gradient = Gradient.paletteMagma(
  new Gradient());
Vec2 origin = new Vec2();
Rgb.AbstrToneMap mapper = new Rgb.ToneMapClamp();

void settings() {
  size(512, 512, Yup2.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (Yup2)getGraphics();
}

void draw() {
  graphics.mouse1s(origin);
  Img.gradientRadial(gradient, origin, 0.5f, img);
  PImage pimg = Convert.toPImage(img, mapper);

  graphics.background();
  graphics.image(pimg);
}

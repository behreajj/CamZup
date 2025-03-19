import com.behreajj.camzup.core.*;
import com.behreajj.camzup.friendly.*;

Yup2 graphics;
Img img = new Img(512, 512);
Gradient gradient = Gradient.paletteRyb(
  new Gradient());
Vec2 origin = new Vec2(-1.0, -1.0);
Vec2 dest = new Vec2(1.0, 1.0);
Lab.AbstrEasing mix = new Lab.MixLab();
Rgb.AbstrToneMap mapper = new Rgb.ToneMapClamp();
PImage pimg = new PImage(1, 1);

void settings() {
  size(512, 512, Yup2.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (Yup2)getGraphics();
}

void draw() {
  graphics.mouse1s(origin);
  Vec2.negate(origin, dest);
  Img.gradientLinear(gradient,
    origin, dest,
    mix, img);
  Convert.toPImage(img, mapper, pimg);

  graphics.background();
  graphics.image(pimg);
}

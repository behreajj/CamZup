import com.behreajj.camzup.core.*;
import com.behreajj.camzup.friendly.*;

Yup2 graphics;
Img img = new Img(512, 512);
Gradient gradient = Gradient.paletteViridis(
  new Gradient());
Vec2 origin = new Vec2(-1.0, -1.0);
Rgb.AbstrToneMap mapper = new Rgb.ToneMapClamp();
PImage pimg = new PImage(1, 1);

void settings() {
  size(720, 405, Yup2.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (Yup2)getGraphics();
}

void draw() {
  float angle = frameCount * 0.02;
  graphics.mouse1s(origin);
  Img.gradientSweep(gradient, origin, angle, img);
  Convert.toPImage(img, mapper, false, pimg);

  graphics.background();
  graphics.image(pimg);
}

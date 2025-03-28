import com.behreajj.camzup.core.*;
import com.behreajj.camzup.friendly.*;

Yup2 graphics;

Img imgRgb = new Img(512, 512);
PImage pimgRgb = new PImage(512, 512);
Img resized = new Img(512, 512);
PImage pimgResized = new PImage(512, 512);
Rgb.AbstrToneMap mapper = new Rgb.ToneMapClamp();

Vec2 scaleMin = new Vec2(0.25, 0.125);
Vec2 scaleMax = new Vec2(2.0, 1.0);

void settings() {
  size(702, 405, Yup2.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (Yup2) getGraphics();

  Img.rgb(imgRgb);
  Convert.toPImage(imgRgb, pimgRgb);
}

void draw() {
  Vec2 mu = graphics.mouse1u(new Vec2());
  Vec2 scale = Vec2.mix(scaleMin, scaleMax, mu.x, new Vec2());
  Img.scaleBilinear(imgRgb, scale, resized);
  Convert.toPImage(resized, mapper, false, pimgResized);

  graphics.background();
  graphics.image(pimgResized);
}

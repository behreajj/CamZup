import com.behreajj.camzup.core.*;
import com.behreajj.camzup.friendly.*;

Yup2 graphics;

Img imgRgb = new Img(256, 256);
PImage pimgRgb = new PImage(256, 256);
Img rotated = new Img(256, 256);
PImage pimgRotated = new PImage(256, 256);
Rgb.AbstrToneMap mapper = new Rgb.ToneMapClamp();

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
  float angle = frameCount * Utils.TAU / 60.0;
  Img.rotateBilinear(imgRgb, angle, rotated);
  Convert.toPImage(rotated, mapper, false, pimgRotated);

  graphics.background();
  graphics.image(pimgRotated);
}

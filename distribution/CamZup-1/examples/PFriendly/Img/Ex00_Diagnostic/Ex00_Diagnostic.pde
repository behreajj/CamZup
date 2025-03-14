import camzup.core.*;
import camzup.pfriendly.*;

Yup2 graphics;

Img imgRgb;
PImage pimgRgb;

Img imgRng;
PImage pimgRng;

void settings() {
  size(702, 405, Yup2.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (Yup2)getGraphics();

  imgRgb = new Img(512, 512);
  Img.rgb(imgRgb);
  pimgRgb = Convert.toPImage(imgRgb);

  boolean includeAlpha = false;
  Rng rng = new Rng();
  imgRng = new Img(512, 512);
  Img.random(rng, includeAlpha, imgRng);
  pimgRng = Convert.toPImage(imgRng);
}

void draw() {
  graphics.background();
  graphics.image(pimgRgb,
    -graphics.width * 0.25, 0.0,
    256.0, 256.0);
  graphics.image(pimgRng,
    graphics.width * 0.25, 0.0,
    256.0, 256.0);
}

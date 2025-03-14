import camzup.core.*;
import camzup.pfriendly.*;

Yup2 graphics;

Img imgRgb = new Img(512, 512);
PImage pimgRgb = new PImage(512, 512);

Img imgRng = new Img(512, 512);
PImage pimgRng = new PImage(512, 512);

Img imgCheck = new Img(512, 512);
PImage pimgCheck = new PImage(512, 512);

void settings() {
  size(702, 405, Yup2.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (Yup2)getGraphics();

  Img.rgb(imgRgb);
  Convert.toPImage(imgRgb, pimgRgb);

  boolean includeAlpha = false;
  Rng rng = new Rng();
  Img.random(rng, includeAlpha, imgRng);
  Convert.toPImage(imgRng, pimgRng);
  
  Img.checker(32, imgCheck);
  Convert.toPImage(imgCheck, pimgCheck);
}

void draw() {
  graphics.background();
  graphics.image(pimgCheck,
    0.0, 0.0,
    180.0, 180.0);
  graphics.image(pimgRgb,
    -graphics.width * IUtils.ONE_THIRD, 0.0,
    180.0, 180.0);
  graphics.image(pimgRng,
    graphics.width * IUtils.ONE_THIRD, 0.0,
    180.0, 180.0);
}

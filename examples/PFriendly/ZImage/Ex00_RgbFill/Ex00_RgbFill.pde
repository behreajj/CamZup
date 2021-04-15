import camzup.core.*;
import camzup.pfriendly.*;

Yup2 graphics;
PImage img;

void settings() {
  size(702, 405, Yup2.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (Yup2)getGraphics();
  img = createImage(512, 512, ARGB);
  ZImage.rgb(img);
}

void draw() {
  graphics.background();
  graphics.image(img, 0.0f, 0.0f, 256.0f, 256.0f);
}

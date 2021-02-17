import camzup.core.*;
import camzup.pfriendly.*;

Yup2 graphics;
PImage img;

void settings() {
  size(702, 405, Yup2.PATH_STR);
}

void setup() {
  frameRate(60.0);
  graphics = (Yup2)getGraphics();
  img = createImage(256, 256, ARGB);
  ZImage.rgb(img);
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));
  graphics.image(img);
}

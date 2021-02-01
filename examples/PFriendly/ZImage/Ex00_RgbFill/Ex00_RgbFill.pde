import camzup.core.*;
import camzup.pfriendly.*;

Yup2 graphics;
PImage img;

void settings() {
  size(512, 512, Yup2.PATH_STR);
}

void setup() {
  frameRate(60.0);
  graphics = (Yup2)getGraphics();
  img = createImage(512, 512, ARGB);
  ZImage.rgb(img);
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));
  graphics.image(img);
}

// Image Source:
// https://www.wikiwand.com/en/The_Calling_of_St_Matthew_(Caravaggio)

import camzup.core.*;
import camzup.pfriendly.*;

PImage source;
PImage target;

float gamma = 1.0f / 2.4f;
Yup2 graphics;

void settings() {
  size(720, 405, Yup2.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (Yup2)getGraphics();

  source = loadImage("callingStMatthew.jpg");
  ZImage.gammaAdjust(source, gamma);
  target = source.get();
  ZImage.grayscale(target, false);
}

void draw() {
  graphics.background();
  if (mousePressed) {
    graphics.image(target);
  } else {
    graphics.image(source);
  }
}

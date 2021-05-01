// Image Source:
// https://www.wikiwand.com/en/The_Calling_of_St_Matthew_(Caravaggio)

import camzup.core.*;
import camzup.pfriendly.*;

PImage source;
PImage target;

Yup2 graphics;
Gradient lcd = Gradient.paletteLcd(new Gradient());
Gradient sepia = Gradient.paletteSepia(new Gradient());

void settings() {
  size(720, 405, Yup2.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (Yup2)getGraphics();

  source = loadImage("callingStMatthew.jpg");
  target = source.get();
  ZImage.falseColor(lcd, target);
}

void draw() {
  graphics.background();
  if (mousePressed) {
    graphics.image(target);
  } else {
    graphics.image(source);
  }
}
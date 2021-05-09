// Image Source:
// https://www.wikiwand.com/en/The_Calling_of_St_Matthew_(Caravaggio)

import camzup.core.*;
import camzup.pfriendly.*;

PImage source;
PImage target;

Yup2 graphics;
Gradient lcd = Gradient.paletteLcd(new Gradient());

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
  float gamma = Utils.lerp(
    2.4f, 1.0f / 2.4f, mouseX / (width - 1.0f));
  float amplitude = 1.0f;
  float offset = Utils.lerp(0.5f, -0.5f,
    mouseY / (height - 1.0f));

  target = source.get();
  ZImage.gammaAdjust(target, gamma, amplitude, offset, false);

  graphics.background();
  if (mousePressed) {
    graphics.image(source);
  } else {
    graphics.image(target);
  }
}

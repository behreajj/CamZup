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
}

void draw() {
  Vec2 m = graphics.mouse1s(new Vec2());
  
  target = source.get();
  ZImage.adjustContrast(target, m.x);
  ZImage.adjustBrightness(target, m.y);

  graphics.background();
  if (mousePressed) {
    graphics.image(source);
  } else {
    graphics.image(target);
  }
}

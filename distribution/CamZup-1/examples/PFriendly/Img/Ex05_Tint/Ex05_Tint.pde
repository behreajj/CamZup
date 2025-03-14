// Image Source:
// https://www.wikiwand.com/en/The_Calling_of_St_Matthew_(Caravaggio)

import camzup.core.*;
import camzup.pfriendly.*;

Yup2 graphics;

Img source = new Img();
Img target = new Img();

PImage pimgSrc = new PImage(1, 1);
PImage pimgTrg = new PImage(1, 1);

float fac = 0.5f;
boolean preserveLight = true;
Lab tint = Lab.srBlue(new Lab());
Lab.AbstrEasing mixer = new Lab.MixLab();
Rgb.AbstrToneMap mapper = new Rgb.ToneMapClamp();

void settings() {
  size(720, 405, Yup2.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (Yup2)getGraphics();

  pimgSrc = loadImage("callingStMatthew.jpg");
  Convert.toImg(pimgSrc, source);
}

void draw() {
  Vec2 mu = graphics.mouse1u(new Vec2());
  fac = mu.x;
  Img.tint(source, tint, fac, preserveLight, target);
  Convert.toPImage(target, mapper, pimgTrg);
  
  graphics.background();
  if (mousePressed) {
    graphics.image(pimgSrc);
  } else {
    graphics.image(pimgTrg);
  }
}

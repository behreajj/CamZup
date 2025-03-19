// Image Source:
// https://en.wikipedia.org/wiki/The_Cardsharps

import com.behreajj.camzup.core.*;
import com.behreajj.camzup.friendly.*;

Yup2 graphics;

Img source = new Img();
Img target = new Img();

PImage pimgSrc = new PImage(1, 1);
PImage pimgTrg = new PImage(1, 1);

float fac = 0.5f;
Rgb.AbstrToneMap mapper = new Rgb.ToneMapHable();

void settings() {
  size(1000, 562, Yup2.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (Yup2)getGraphics();

  pimgSrc = loadImage("cardSharps.jpg");
  Convert.toImg(pimgSrc, source);
}

void draw() {
  Vec2 ms = graphics.mouse1s(new Vec2());
  Img.adjustContrast(source, ms.x, ms.y, target);
  Convert.toPImage(target, mapper, pimgTrg);
  
  graphics.background();
  if (mousePressed) {
    graphics.image(pimgSrc);
  } else {
    graphics.image(pimgTrg);
  }
}

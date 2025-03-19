// Image Source:
// https://en.wikipedia.org/wiki/The_Cardsharps

import com.behreajj.camzup.core.*;
import com.behreajj.camzup.friendly.*;

Yup2 graphics;

Img source = new Img();
Img target = new Img();
Img.GrayPolicy grayPolicy = Img.GrayPolicy.WARM;

PImage pimgSrc = new PImage(1, 1);
PImage pimgTrg = new PImage(1, 1);

float fac = 0.5f;
Rgb.AbstrToneMap mapper = new Rgb.ToneMapAces();

void settings() {
  size(1000, 562, Yup2.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (Yup2)getGraphics();

  pimgSrc = loadImage("cardSharps.jpg");
  Convert.toImg(pimgSrc, source);
  //Img.grayscale(source, source);
}

void draw() {
  Vec2 ms = graphics.mouse1s(new Vec2());
  Img.adjustLch(source,
      // new Lch(0.0, ms.y * Lch.SR_CHROMA_MEAN, ms.x * 0.5),
      new Lch(ms.y * 100.0, 0.0, ms.x * 0.5),
     grayPolicy, target);
  Convert.toPImage(target, mapper, pimgTrg);
  
  graphics.background();
  if (mousePressed) {
    graphics.image(pimgSrc);
  } else {
    graphics.image(pimgTrg);
  }
}

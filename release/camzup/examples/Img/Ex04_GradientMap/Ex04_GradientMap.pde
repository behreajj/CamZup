// Image Source:
// https://en.wikipedia.org/wiki/The_Calling_of_Saint_Matthew

import com.behreajj.camzup.core.*;
import com.behreajj.camzup.friendly.*;

Yup2 graphics;

Img source = new Img();
Img target = new Img();

PImage pimgSrc = new PImage(1, 1);
PImage pimgTrg = new PImage(1, 1);

Gradient grd = Gradient.paletteViridis(new Gradient());
Lab.AbstrEasing mixer = new Lab.MixLab();
Rgb.AbstrToneMap mapper = new Rgb.ToneMapClamp();
Img.MapChannel channel = Img.MapChannel.L;
boolean useNormalize = true;

void settings() {
  size(720, 405, Yup2.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (Yup2)getGraphics();

  pimgSrc = loadImage("callingStMatthew.jpg");
  Convert.toImg(pimgSrc, source);

  long start = System.currentTimeMillis();
  Img.gradientMap(grd, source, mixer,
     channel, useNormalize, target);
  long end = System.currentTimeMillis();
  println("Elapsed Time: " + (end - start));

  Convert.toPImage(target, mapper, false, pimgTrg);
}

void draw() {
  graphics.background();
  if (mousePressed) {
    graphics.image(pimgSrc);
  } else {
    graphics.image(pimgTrg);
  }
}

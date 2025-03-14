// Image Source:
// https://www.wikiwand.com/en/The_Calling_of_St_Matthew_(Caravaggio)

import camzup.core.*;
import camzup.pfriendly.*;

Yup2 graphics;

Img source;
Img target = new Img();

PImage pimgSrc;
PImage pimgTrg;

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
  source = Convert.toImg(pimgSrc);
  

  long start = System.currentTimeMillis();
  Img.gradientMap(grd, source, mixer,
     channel, useNormalize, target);
  long end = System.currentTimeMillis();
  println("Elapsed Time: " + (end - start));

  pimgTrg = Convert.toPImage(target);
}

void draw() {
  graphics.background();
  if (mousePressed) {
    graphics.image(pimgSrc);
  } else {
    graphics.image(pimgTrg);
  }
}

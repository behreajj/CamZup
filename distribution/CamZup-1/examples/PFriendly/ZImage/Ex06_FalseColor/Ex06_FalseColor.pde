// Image Source:
// https://www.wikiwand.com/en/The_Calling_of_St_Matthew_(Caravaggio)

import camzup.core.*;
import camzup.pfriendly.*;

PImage source;
PImage target;

Yup2 graphics;
Img srcImg;
Img trgImg = new Img(512, 512);
Gradient grd = Gradient.paletteViridis(new Gradient());
Pixels.MapLuminance map = new Pixels.MapLuminance();

void settings() {
    size(720, 405, Yup2.PATH_STR);
}

void setup() {
    frameRate(60.0f);
    graphics = (Yup2)getGraphics();

    source = loadImage("callingStMatthew.jpg");
    srcImg = Convert.toImg(source);
    
    long start = System.currentTimeMillis();
    Img.gradientMap(grd, srcImg, trgImg);
    target = Convert.toPImage(trgImg);
    long end = System.currentTimeMillis();
    println("Elapsed Time: " + (end - start));
}

void draw() {
    graphics.background();
    if (mousePressed) {
        graphics.image(source);
    } else {
        graphics.image(target);
    }
}

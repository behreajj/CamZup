import camzup.pfriendly.*;
import camzup.core.*;

Yup2 graphics;

int a = 0xffff0000;
int b = 0xff0000ff;

void settings() {
  size(512, 256, Yup2.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  colorMode(HSB, 1.0f);
  graphics = (Yup2)getGraphics();
}

void randomColors() {
  float hue0 = random(0.0f, 1.0f);
  int hueSign = random(0.0f, 1.0f) < 0.5f ? -1 : 1;
  float hueOffset = hueSign * random(0.01f, 0.425f);
  float hue1 = Utils.mod1(hue0 + hueOffset);
  a = color(hue0, 
    random(0.5f, 1.0f), 
    random(0.5f, 1.0f), 1.0f);
  b = color(hue1, 
    random(0.5f, 1.0f), 
    random(0.5f, 1.0f), 1.0f);
  println(hex(a), hex(b));
  println("");
}

void mouseReleased() {
  randomColors();
}

void draw() {
  float toPercent = 1.0f / (height - 1.0f);
  int wHalf = width / 2;

  graphics.loadPixels();
  int[] px = graphics.pixels;
  for (int i = 0, y = 0; y < height; ++y) {
    float yFac = y * toPercent;
    int c0 = lerpColor(a, b, yFac, HSB);
    int c1 = graphics.lerpColor(a, b, yFac);

    for (int j = 0; j < width; ++j, ++i) {
      px[i] = j > wHalf ? c1: c0;
    }
  }
  graphics.updatePixels();
}

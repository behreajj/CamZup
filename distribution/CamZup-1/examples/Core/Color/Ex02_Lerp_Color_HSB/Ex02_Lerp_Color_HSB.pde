import camzup.pfriendly.*;
import camzup.core.*;

Yup2 graphics;

color a = #ff0000;
color b = #0000ff;

void settings() {
  size(512, 256, "camzup.pfriendly.Yup2");
}

void setup() {
  colorMode(HSB, 1.0);
  graphics = (Yup2)getGraphics();
}

void randomColors() {
  float hue0 = random(0.0, 1.0);
  int hueSign = random(0.0, 1.0) < 0.5 ? -1 : 1;
  float hueOffset = hueSign * random(0.01, 0.425);
  float hue1 = Utils.mod1(hue0 + hueOffset);
  a = color(hue0, 1.0, 1.0, 1.0);
  b = color(hue1, 1.0, 1.0, 1.0);
  println(hex(a), hex(b));
  println("");
}

void mouseReleased() {
  randomColors();
}

void draw() {
  surface.setTitle(String.format("%.1f", frameRate));

  float toPercent = 1.0 / (float)(height - 1);
  int wHalf = width / 2;

  graphics.loadPixels();
  color[] px = graphics.pixels;
  for (int i = 0, y = 0; y < height; ++y) {
    float yFac = y * toPercent;
    color c0 = lerpColor(a, b, yFac, HSB);
    color c1 = graphics.lerpColor(a, b, yFac);

    for (int j = 0; j < width; ++j, ++i) {
      px[i] = j > wHalf ? c1: c0;
    }
  }
  graphics.updatePixels();
}

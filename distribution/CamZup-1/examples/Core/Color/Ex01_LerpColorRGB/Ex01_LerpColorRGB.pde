import camzup.pfriendly.*;
import camzup.core.*;

YupJ2 graphics;

int a = 0xffff0000;
int b = 0xff00ff00;

void settings() {
  size(1024, 128, YupJ2.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  colorMode(RGB, 1.0f);
  graphics = (YupJ2)getGraphics();
}

void mouseReleased() {
  a = color(random(1.0f), random(1.0f), random(1.0f), 1.0f);
  b = color(random(1.0f), random(1.0f), random(1.0f), 1.0f);
}

void draw() {
  float toPercent = 1.0f / (width - 1.0f);
  float mouseFac = mouseX * toPercent;
  int hHalf = height / 2;

  graphics.loadPixels();
  color[] px = graphics.pixels;
  for (int x = 0; x < width; ++x) {
    float xFac = x * toPercent / mouseFac;
    color c0 = lerpColor(a, b, xFac, RGB);
    color c1 = graphics.lerpColor(a, b, xFac);

    for (int y = 0; y < height; ++y) {
      px[y * width + x] = y > hHalf ? c1 : c0;
    }
  }
  graphics.updatePixels();
}

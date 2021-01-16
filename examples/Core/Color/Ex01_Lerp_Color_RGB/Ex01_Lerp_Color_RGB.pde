import camzup.pfriendly.*;
import camzup.core.*;

YupJ2 graphics;

color a = #007fff;
color b = #ff007f;

void settings() {
  size(1024, 128, YupJ2.PATH_STR);
}

void setup() {
  frameRate(60.0);
  colorMode(RGB, 1.0);
  graphics = (YupJ2)getGraphics();
}

void mouseReleased() {
  a = color(random(1.0), random(1.0), random(1.0), 1.0);
  b = color(random(1.0), random(1.0), random(1.0), 1.0);
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));

  float toPercent = 1.0 / (float)(width - 1);
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

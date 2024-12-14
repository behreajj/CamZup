import camzup.core.*;
import camzup.pfriendly.*;

YupJ2 graphics;
Rgb[] palette;
int cols = 16;
int swatch = 24;

void settings() {
  size(720, 405, YupJ2.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (YupJ2)getGraphics();
  graphics.noStroke();
  graphics.rectMode(CORNERS);

  palette = ParserAse.load(sketchPath() + "\\data\\rgb332.ase");
}

void draw() {
  graphics.background();
  graphics.camFlipped();

  int len = palette.length;
  for (int i = 0; i < len; ++i) {
    float x = swatch * (i % cols);
    float y = swatch * (i / cols);

    graphics.fill(Rgb.toHexInt(palette[i]));
    graphics.rect(x, y, x + swatch, y + swatch);
  }
}

import camzup.core.*;
import camzup.pfriendly.*;

YupJ2 graphics;
Rgb[] palette;
int rows = 4;
int swatch = 32;

void settings() {
  size(448, 128, YupJ2.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (YupJ2)getGraphics();
  graphics.noStroke();
  graphics.rectMode(CORNERS);

  palette = ParserGpl.load(sketchPath() + "\\data\\nesPalette.gpl");
}

void draw() {
  graphics.background();
  graphics.camFlipped();

  int len = palette.length;
  for (int i = 0; i < len; ++i) {
    float x = swatch * (i / rows);
    float y = swatch * (i % rows);

    graphics.fill(Rgb.toHexInt(palette[i]));
    graphics.rect(x, y, x + swatch, y + swatch);
  }
}

void mouseReleased() {
  String gplStr = Rgb.toGplString(
    Gradient.paletteCyanotype(new Gradient()).toArray(),
    "Cyanotype");
  saveStrings("data/cyanotype.gpl", new String[] { gplStr });
  println("Cyanotype palette saved.");
}

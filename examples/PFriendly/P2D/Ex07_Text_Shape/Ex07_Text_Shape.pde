import camzup.pfriendly.*;
import camzup.core.*;

Yup2 graphics2;
CurveEntity2[] glyphs;

PFont font;

void settings() {
  size(720, 405, Yup2.PATH_STR);
}

void setup() {
  graphics2 = (Yup2)getGraphics();

  textMode(SHAPE);
  font = createFont("Garamond", 160);
  glyphs = graphics2.glyph(font, 0.02, "Qualia");

  int len = glyphs.length;
  float toPercent = 1.0 / len;
  Vec2 right = new Vec2(width * 0.5, 0.0);
  Vec2 left = Vec2.negate(right, new Vec2());
  for (int i = 0; i < len; ++i) {
    CurveEntity2 glyph = glyphs[i];
    glyph.scaleTo(160);

    float percent = i * toPercent;
    Vec2 pos = Vec2.mix(left, right, percent, new Vec2());
    glyph.moveTo(pos);
  }
}


void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));
  graphics2.background();
  graphics2.noFill();
  for (CurveEntity2 glyph : glyphs) {
    graphics2.shape(glyph);
  }
}

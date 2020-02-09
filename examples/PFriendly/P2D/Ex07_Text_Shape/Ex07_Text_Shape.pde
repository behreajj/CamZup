import camzup.pfriendly.*;
import camzup.core.*;

YupJ2 graphics2;
CurveEntity2[] glyphs;

PFont font;

void settings() {
  size(720, 405, "camzup.pfriendly.YupJ2");
}

void setup() {
  graphics2 = (YupJ2)getGraphics();

  textMode(SHAPE);
  font = createFont("Garamond", 160);
  glyphs = graphics2.glyph(font, 0.02, "Qualia");

  MaterialSolid material = new MaterialSolid()
    .setFill(false)
    .setStroke(true)
    .setStrokeWeight(0.01);

  int len = glyphs.length;
  float toPercent = 1.0 / len;
  Vec2 right = new Vec2(width * 0.5, 0.0);
  Vec2 left = Vec2.negate(right, new Vec2());
  for (int i = 0; i < len; ++i) {
    CurveEntity2 glyph = glyphs[i];
    glyph.appendMaterial(material);
    glyph.scaleTo(160);

    float percent = i * toPercent;
    Vec2 pos = Vec2.mix(left, right, percent, new Vec2());
    glyph.moveTo(pos);
  }
}


void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));
  graphics2.background();
  for (CurveEntity2 glyph : glyphs) {
    graphics2.shape(glyph);
  }
}

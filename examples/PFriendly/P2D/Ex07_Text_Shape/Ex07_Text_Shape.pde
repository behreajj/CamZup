import camzup.pfriendly.*;
import camzup.core.*;

YupJ2 graphics2;
CurveEntity2[] glyphs;

PFont font;

void setup() {
  size(720, 405, "camzup.pfriendly.YupJ2");
  smooth(8);

  graphics2 = (YupJ2)getGraphics();

  textMode(SHAPE);
  font = createFont("Garamond", 180);

  glyphs = graphics2.glyph(font, 0.0, "ipsum");

  MaterialSolid material = new MaterialSolid()
    .setFill(false)
    .setStroke(true)
    .setStrokeWeight(0.01);

  int len = glyphs.length;
  float toPercent = 1.0 / (float)len;
  Vec2 right = new Vec2(width * 0.5, 0.0);
  Vec2 left = Vec2.negate(right, new Vec2());
  for (int i = 0; i < len; ++i) {
    CurveEntity2 glyph = glyphs[i];
    glyph.appendMaterial(material);
    glyph.transform.scaleTo(200);

    float percent = i * toPercent;
    Vec2 pos = Vec2.mix(left, right, percent, new Vec2());
    glyph.transform.moveTo(pos);
  }
}


void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));
  graphics2.background();
  graphics2.camera();

  for (CurveEntity2 glyph : glyphs) {
    graphics2.shape(glyph);
  }
}

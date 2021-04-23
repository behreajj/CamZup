import camzup.pfriendly.*;
import camzup.core.*;

YupJ2 graphics;
CurveEntity2[] glyCrv;
MeshEntity2[] glyMsh;

String str = "Quigley";
boolean toggle = false;
boolean showHandles = true;

MaterialSolid matCrv = new MaterialSolid()
  .setFill(true)
  .setFill(0xff4f4f4f)
  .setStroke(true)
  .setStroke(0xfffff7d5)
  .setStrokeWeight(1.0f);

MaterialPImage matMsh;

PFont font;

void settings() {
  size(720, 405, YupJ2.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (YupJ2)getGraphics();

  PImage txtr = createImage(512, 512, ARGB);
  ZImage.rgb(txtr);
  matMsh = new MaterialPImage(txtr);

  textMode(SHAPE);
  font = createFont("Cambria", 64.0f);
  glyCrv = TextShape.glyphCurve(font, 0.0f, false, str);
  glyMsh = TextShape.glyphMesh(font, 15.0f, false, str);

  float scl = width * 0.4f;
  int len0 = glyCrv.length;
  for (int i = 0; i < len0; ++i) {
    CurveEntity2 glyph = glyCrv[i];
    glyph.scaleTo(scl);
  }

  int len1 = glyMsh.length;
  for (int j = 0; j < len1; ++j) {
    MeshEntity2 glyph = glyMsh[j];
    glyph.scaleTo(scl);
  }

  graphics.moveTo(width * 0.45f, height * 0.25f);
}


void draw() {
  graphics.background(0xff202020);
  Vec2 m = graphics.mouse1(new Vec2());
  Vec2.mul(m, 5.0f, m);
  graphics.moveBy(m);

  if (toggle) {
    for (MeshEntity2 glyph : glyMsh) {
      //graphics.shape(glyph, matMsh);
      graphics.shape(glyph);
    }
  } else {
    for (CurveEntity2 glyph : glyCrv) {
      graphics.shape(glyph, matCrv);
      if (showHandles) {
        graphics.handles(glyph);
      }
    }
  }
}

void mouseReleased() {
  toggle = !toggle;
}

void mouseWheel(MouseEvent e) {
  float mWheel = e.getCount();
  graphics.zoomBy(-mWheel * 0.05f);
}

void keyReleased() {
  if (key == ' ') {
    showHandles = !showHandles;
  } else if (key == 's') {
    String result = graphics.toSvgString(glyCrv, matCrv);
    saveStrings("data/curve.svg", new String[] { result });
    println("Saved to svg.");
  }
}

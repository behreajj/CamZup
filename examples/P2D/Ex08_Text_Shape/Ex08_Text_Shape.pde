import com.behreajj.camzup.core.*;
import com.behreajj.camzup.friendly.*;

YupJ2 graphics;
CurveEntity2[] glyCrv;

String str = "Quigley";
boolean showHandles = true;

MaterialSolid matCrv = new MaterialSolid()
  .setFill(true)
  .setFill(new Rgb(0.2, 0.2, 0.2))
  .setStroke(true)
  .setStroke(new Rgb(1.0, 1.0, 0.9))
  .setStrokeWeight(1.0f);

MaterialPImage matMsh;

PFont font;

void settings() {
  size(720, 405, YupJ2.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (YupJ2)getGraphics();

  float scl = width * 0.4f;

  textMode(SHAPE);
  font = createFont("Cambria", 64.0f);
  glyCrv = TextShape.glyphCurve(font, scl, 0.0f, true, str);
  graphics.moveTo(width * 0.45f, height * 0.25f);
}

void draw() {
  graphics.background(0xff202020);
  Vec2 m = graphics.mouse1(new Vec2());
  Vec2.mul(m, 5.0f, m);
  graphics.moveBy(m);

  for (CurveEntity2 glyph : glyCrv) {
    graphics.shape(glyph, matCrv);
    if (showHandles) {
      graphics.handles(glyph);
    }
  }
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
    saveStrings("data/text.svg", new String[] { result });
    println("Saved to svg.");
  }
}

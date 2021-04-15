import camzup.core.*;
import camzup.pfriendly.*;

boolean loop = false;
float rounding = -0.25f;

Yup2 graphics;

Rng rng = new Rng();
Curve2 curve = new Curve2();
CurveEntity2 entity = new CurveEntity2("Example", curve);

MaterialSolid mat = new MaterialSolid()
  .setFill(false)
  .setStroke(true)
  .setStroke(0x7f404040)
  .setStrokeWeight(7.5f);

void settings() {
  size(720, 405, Yup2.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (Yup2)getGraphics();

  entity.transform.scaleTo(256.0f);
  Curve2.rect(
    new Vec2(-0.75f, -0.5f),
    new Vec2(0.75f, 0.5f),
    rounding, curve);
}

void draw() {
  entity.rotateZ(0.01f);
  graphics.background();
  graphics.origin();
  graphics.shape(entity, mat);
  graphics.handles(entity, 1.25f);
}

void keyReleased() {
  if (key == ' ') {
    Curve2.random(rng, 8,
      -0.75f, 0.75f, loop, curve);
  } else if (key == 'l') {
    loop = !loop;
  } else if (key == 's') {
    String result = graphics.toSvgString(entity, mat);
    saveStrings("data/curve.svg", new String[] { result });
    println("Saved to svg.");
  }
}

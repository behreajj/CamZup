import camzup.core.*;
import camzup.pfriendly.*;

YupJ2 graphics;
Rng rng = new Rng();

Transform2 transform = new Transform2();

float rounding = -0.25;
Curve2 curve = new Curve2();
CurveEntity2 entity = new CurveEntity2(
  "Example", transform);

MaterialSolid mat = new MaterialSolid()
  .setFill(false)
  .setStroke(true)
  .setStroke(0x7f404040)
  .setStrokeWeight(7.5);

void settings() {
  size(720, 405, YupJ2.PATH_STR);
}

void setup() {
  frameRate(60.0);
  graphics = (YupJ2)getGraphics();

  entity.append(curve);
  entity.transform.scaleTo(256);

  Curve2.rect(
    new Vec2(-0.75, -0.5),
    new Vec2(0.75, 0.5),
    rounding, curve);
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));
  entity.rotateZ(0.01);
  graphics.background();
  graphics.origin();
  graphics.shape(entity, mat);
  graphics.handles(entity, 1.25);
}

void keyReleased() {
  if (key == ' ') {
    Curve2.random(rng, 8,
      -0.75, 0.75, false, curve);
  } else if (key == 's') {
    String result = graphics.toSvgString(entity, mat);
    saveStrings("data/curve.svg", new String[] { result });
    println("Saved to svg.");
  }
}

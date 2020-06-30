import camzup.core.*;
import camzup.pfriendly.*;

Yup2 graphics;
Rng rng = new Rng();

Transform2 transform = new Transform2()
  .scaleTo(200.0);

Curve2 curve = Curve2.rect(
  -0.75, 0.5, 0.75, -0.5, -0.25,
  new Curve2());

CurveEntity2 entity = new CurveEntity2(
  "Example", transform);

MaterialSolid mat = new MaterialSolid()
  .setFill(false)
  .setStroke(true)
  .setStroke(0x7f404040)
  .setStrokeWeight(7.5);

void settings() {
  size(720, 405, Yup2.PATH_STR);
}

void setup() {
  graphics = (Yup2)getGraphics();
  entity.append(curve);
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));
  entity.rotateZ(0.01);

  graphics.background();
  graphics.origin();
  graphics.noFill();
  graphics.strokeWeight(1.0);
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

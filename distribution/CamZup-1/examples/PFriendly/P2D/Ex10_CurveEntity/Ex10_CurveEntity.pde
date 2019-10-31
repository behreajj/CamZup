import camzup.core.*;
import camzup.pfriendly.*;

Yup2 graphics;

Random rng = new Random();

MaterialSolid material = new MaterialSolid()
  .setStroke(true)
  .setStroke(#808080)
  .setFill(false)
  .setStrokeWeight(1.2);

Transform2 transform = new Transform2()
  .scaleTo(200.0);

Curve2 curve = Curve2.circle(0.0, 0.5, 4, 
  new Curve2(), 
  new Vec2(), new Vec2());

CurveEntity2 entity = new CurveEntity2(
  "Example", 
  transform, 
  curve)
  .appendMaterial(material);

void setup() {
  size(720, 405, "camzup.pfriendly.Yup2");
  graphics = (Yup2)getGraphics();
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));
  background(#fff7d5);
  graphics.origin();
  graphics.shape(entity);
  graphics.handles(entity, 0.0075);
}

void mouseReleased() {
  if (mouseButton == LEFT) {
    Curve2.random(rng, 7, 
      -0.5, 0.5, false, curve);
  } else if (mouseButton == RIGHT) {
    String result = graphics.toSvgString(entity);
    saveStrings("data/curve.svg", new String[] { result });
    println("Saved to svg.");
  }
}

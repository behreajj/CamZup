import camzup.core.*;
import camzup.pfriendly.*;

Yup2 graphics;

Random rng = new Random();

MaterialSolid material = new MaterialSolid()
  .setStroke(true)
  .setStroke(#808080)
  .setFill(false)
  .setStrokeWeight(0.01);

Transform2 transform = new Transform2()
  .scaleTo(200.0);

Curve2 curve = Curve2.rect(
  -0.75, 0.5, 0.75, -0.5, -0.25, 
  new Curve2());

CurveEntity2 entity = new CurveEntity2(
  "Example", transform)
  .appendMaterial(material)
  .appendCurve(curve);
  

void setup() {
  size(720, 405, "camzup.pfriendly.Yup2");
  graphics = (Yup2)getGraphics();
}

void draw() {
  entity.transform.rotateZ(0.01);

  surface.setTitle(Utils.toFixed(frameRate, 1));
  graphics.background();
  graphics.origin();
  graphics.shape(entity);
  graphics.handles(entity, 0.0075);
}

void mouseReleased() {
  if (mouseButton == LEFT) {
    Curve2.random(rng, 6, 
      -0.75, 0.75, false, curve);
  } else if (mouseButton == RIGHT) {
    String result = graphics.toSvgString(entity);
    saveStrings("data/curve.svg", new String[] { result });
    println("Saved to svg.");
  }
}

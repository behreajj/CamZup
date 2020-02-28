import camzup.core.*;
import camzup.pfriendly.*;

Yup2 graphics;

Random rng = new Random();

MaterialSolid material = new MaterialSolid()
  .setStroke(true)
  .setStroke(#808080)
  .setFill(false)
  .setStrokeWeight(1.0);

Transform2 transform = new Transform2()
  .scaleTo(200.0);

//Curve2 curve = Curve2.rect(
//  -0.75, 0.5, 0.75, -0.5,
//  new Curve2());

Curve2 curve = Curve2.polygon(0.0, 0.5, 5,
  new Curve2());

CurveEntity2 entity = new CurveEntity2(
  "Example", transform)
  .appendMaterial(material)
  .appendCurve(curve);

void settings() {
  size(720, 405, "camzup.pfriendly.Yup2");
}

void setup() {
  graphics = (Yup2)getGraphics();
  //frameRate(1000);
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));
  entity.rotateZ(0.01);

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

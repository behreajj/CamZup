import camzup.core.*;
import camzup.pfriendly.*;

YupJ2 graphics;

Random rng = new Random();

MaterialSolid material = new MaterialSolid()
  .setStroke(true)
  .setStroke(#808080)
  .setFill(false)
  .setStrokeWeight(0.01);

Transform2 transform = new Transform2()
  .scaleTo(200.0);

Curve2 curve = Curve2.circle(0.0, 0.5, 4, 
  new Curve2(), 
  new Vec2(), new Vec2());

Curve2 resampled = Curve2.resample(curve, new Curve2());

CurveEntity2 entity = new CurveEntity2(
  "Example", 
  transform, 
  resampled)
  .appendMaterial(material);

void setup() {
  size(720, 405, "camzup.pfriendly.YupJ2");
  graphics = (YupJ2)getGraphics();
}

void draw() {
  surface.setTitle(String.format("%.1f", frameRate));
  background(#fff7d5);
  graphics.origin();
  graphics.shape(entity);
  graphics.handles(entity, 0.0075);
}

void mouseReleased() {
  if (mouseButton == LEFT) {
    Curve2.random(rng, 5, 
      -0.75, 0.75, true, curve);
    Curve2.resample(7, curve, resampled);
  } else if (mouseButton == RIGHT) {
    String result = graphics.toSvgString(entity);
    saveStrings("data/mesh.svg", new String[] { result });
    println("Saved to svg.");
  }
}

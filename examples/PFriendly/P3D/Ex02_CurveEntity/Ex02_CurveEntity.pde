import camzup.core.*;
import camzup.pfriendly.*;

Zup3 graphics3;

Random rng = new Random();

MaterialSolid material = new MaterialSolid()
  .setStroke(true)
  .setStroke(#808080)
  .setFill(false)
  .setStrokeWeight(0.01);

Transform3 transform = new Transform3()
  .moveTo(new Vec3(0.0, 0.0, 0.0))
  .scaleTo(256.0);

Curve3 curve = Curve3.circle(0.0, 0.5, 4, 
  new Curve3(), 
  new Vec3(), new Vec3());

CurveEntity3 entity = new CurveEntity3("Example", transform, curve)
  .appendMaterial(material);

void setup() {
  size(720, 405, "camzup.pfriendly.Zup3");
  smooth(8);
  frameRate(1000);
  graphics3 = (Zup3)getGraphics();
}

void mouseReleased() {
  Curve3.random(rng, 7, 
    -0.7071, 0.7071, 
    true, curve);
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));
  background(#fff7d5);
  perspective();
  camera();
  graphics3.origin();
  graphics3.handles(entity, 0.005);
  graphics3.shape(entity);
  float t = mouseX / (float)width;

  Vec3 pt = new Vec3();
  Vec3 tn = new Vec3();
  entity.eval(0, t, pt, tn);

  Vec3 tnpt = Vec3.add(pt, 
    Vec3.mult(tn, 50.0, new Vec3()), new Vec3());
  strokeWeight(10.0);
  stroke(#0048ff);
  graphics3.point(pt);
  strokeWeight(5.0);
  stroke(#ff0048);

  graphics3.point(tnpt);
  
}

import camzup.core.*;
import camzup.pfriendly.*;

Yup3 graphics3;

Random rng = new Random();

MaterialSolid material = new MaterialSolid()
  .setStroke(true)
  .setStroke(#808080)
  .setFill(false)
  .setStrokeWeight(1.0);

Transform3 transform = new Transform3()
  .moveTo(new Vec3(0.0, 0.0, 0.0))
  .scaleTo(256.0);

Curve3 curve = Curve3.circle(0.0, 0.5, 4, new Curve3());
CurveEntity3 entity = new CurveEntity3("Example", transform)
  .appendMaterial(material)
  .appendCurve(curve);

void settings() {
  size(720, 405, "camzup.pfriendly.Yup3");
  smooth(8);
}

void setup() {
  graphics3 = (Yup3)getGraphics();
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));

  float t = mouseX / (float)width;
  Vec3 pt = new Vec3();
  Vec3 tn = new Vec3();
  entity.eval(0, t, pt, tn);
  Vec3 tnpt = Vec3.add(pt,
    Vec3.mul(tn, 50.0, new Vec3()), new Vec3());

  background(#fff7d5);
  perspective();
  camera();

  graphics3.origin();
  graphics3.shape(entity);
  graphics3.handles(entity, 0.005);

  strokeWeight(10.0);
  stroke(#202020);
  graphics3.point(pt);
  strokeWeight(5.0);
  stroke(#101010);

  graphics3.point(tnpt);
}

void mouseReleased() {
  Curve3.random(rng, 7,
    -0.7071, 0.7071,
    true, curve);
}

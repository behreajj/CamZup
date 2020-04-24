import camzup.core.*;
import camzup.pfriendly.*;

Zup3 graphics3;

Rng rng = new Rng();

Transform3 transform = new Transform3()
  .moveTo(new Vec3())
  .scaleTo(256.0);

Curve3 curve = Curve3.infinity(new Curve3());
CurveEntity3 entity = new CurveEntity3("Example", transform)
  .append(curve);

MaterialSolid mat = new MaterialSolid()
  .setFill(false)
  .setStroke(true)
  .setStroke(#202020)
  .setStrokeWeight(1.0);

void settings() {
  size(720, 405, Zup3.PATH_STR);
}

void setup() {
  graphics3 = (Zup3)getGraphics();
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));
  entity.rotateZ(0.0075);

  float t = Utils.mod1(frameCount * 0.005);

  Knot3 knWd = new Knot3();
  Knot3 knLc = new Knot3();
  CurveEntity3.eval(entity, 0, t, knWd, knLc);

  graphics3.background();
  graphics3.origin();

  graphics3.shape(entity, mat);
  graphics3.handles(entity, 1.25);

  graphics3.stroke(#202020);

  graphics3.strokeWeight(10.0);
  graphics3.point(knWd.coord);

  graphics3.strokeWeight(5.0);
  graphics3.point(knWd.rearHandle);

  graphics3.strokeWeight(7.5);
  graphics3.point(knWd.foreHandle);
}

void mouseReleased() {
  Curve3.random(rng, 7, -0.5, 0.5, false, curve);
}

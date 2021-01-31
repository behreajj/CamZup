import camzup.core.*;
import camzup.pfriendly.*;

Zup3 graphics;

Rng rng = new Rng();

Transform3 transform = new Transform3()
  .moveTo(new Vec3())
  .scaleTo(256.0);

Curve3 curve = new Curve3();
CurveEntity3 entity = new CurveEntity3("Example", transform);

MaterialSolid mat = new MaterialSolid()
  .setFill(false)
  .setStroke(true)
  .setStroke(#202020)
  .setStrokeWeight(1.0);

void settings() {
  size(720, 405, Zup3.PATH_STR);
}

void setup() {
  frameRate(60.0);
  graphics = (Zup3)getGraphics();
  Curve3.circle(curve);
  entity.append(curve);
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));
  entity.rotateZ(0.0075);

  float t = Utils.mod1(frameCount * 0.005);

  Knot3 knWd = new Knot3();
  Knot3 knLc = new Knot3();
  CurveEntity3.eval(entity, 0, t, knWd, knLc);

  graphics.background();
  graphics.origin();

  graphics.shape(entity, mat);
  graphics.handles(entity, 1.25);

  graphics.stroke(#202020);

  graphics.strokeWeight(10.0);
  graphics.point(knWd.coord);

  graphics.strokeWeight(5.0);
  graphics.point(knWd.rearHandle);

  graphics.strokeWeight(7.5);
  graphics.point(knWd.foreHandle);
}

void mouseReleased() {
  Curve3.random(rng, 7, -0.5, 0.5, true, curve);
}

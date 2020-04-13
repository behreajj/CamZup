import camzup.core.*;
import camzup.pfriendly.*;

Yup2 rndr;
Curve2 curve2 = new Curve2();
CurveEntity2 ce2 = new CurveEntity2();
MaterialSolid mat = new MaterialSolid()
  .setStroke(true)
  .setStroke(#fff7d5)
  .setStrokeWeight(1.25)
  .setFill(false);

int count = 60;
float toPrc = 1.0 / (count - 1.0);
float offmin = -0.25;
float offmax = 0.25;
float dotmax = 25.0;
float dotmin = 10.0;

Gradient grd = new Gradient();
Color clr = new Color();
Ray2 local = new Ray2();
Ray2 world = new Ray2();

void settings() {
  size(720, 405, Yup2.PATH_STR);
}

void setup() {
  rndr = (Yup2)getGraphics();

  Curve2.infinity(curve2);
  ce2.append(curve2);
  ce2.scaleTo((rndr.width + rndr.height) * 0.495);
  Gradient.paletteRyb(grd);
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));
  
  float step = frameCount * 0.0075;
  ce2.rotateZ(0.01);

  rndr.background(0x1f101010);
  if (mousePressed) {
    rndr.shape(ce2, mat);
  }

  for (int i = 0; i < count; ++i) {
    float prc = i * toPrc;
    float off = Utils.lerp(offmin, offmax, prc);
    float dotsiz = Utils.pingPong(dotmin, dotmax, prc);

    CurveEntity2.eval(ce2, 0, step + off, world, local);
    Gradient.eval(grd, prc, clr);
    rndr.strokeWeight(dotsiz);
    rndr.stroke(Color.toHexInt(clr));
    rndr.point(world.origin);
  }
}

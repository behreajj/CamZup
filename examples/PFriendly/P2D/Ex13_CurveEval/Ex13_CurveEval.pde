import camzup.core.*;
import camzup.pfriendly.*;

int count = 80;
float toPrc = 1.0 / (count - 1.0);
float offMin = -0.45;
float offMax = 0.45;
float dotMax = 25.0;
float dotMin = 10.0;

YupJ2 rndr;

Curve2 infinity = Curve2.infinity(new Curve2());
CurveEntity2 infEntity = new CurveEntity2();
MaterialSolid infMat = new MaterialSolid()
  .setStroke(true)
  .setStroke(#fff7d5)
  .setStrokeWeight(1.25)
  .setFill(false);

CurveEntity2[] dotEntities = new CurveEntity2[count];
MaterialSolid[] dotMats = new MaterialSolid[count];

Gradient grd = Gradient.paletteRyb(new Gradient());
Ray2 local = new Ray2();
Ray2 world = new Ray2();

void settings() {
  size(720, 405, YupJ2.PATH_STR);
}

void setup() {
  frameRate(60.0);
  rndr = (YupJ2)getGraphics();

  infEntity.append(infinity);
  infEntity.scaleTo((rndr.width + rndr.height) * 0.495);

  for (int i = 0; i < count; ++i) {
    float prc = i * toPrc;
    float dotSize = Utils.pingPong(dotMin, dotMax, prc);

    CurveEntity2 de2 = new CurveEntity2();
    Curve2 dot = Curve2.circle(new Curve2());
    dot.materialIndex = i;
    de2.append(dot);
    de2.scaleTo(dotSize);
    dotEntities[i] = de2;

    MaterialSolid dotMat = new MaterialSolid();
    dotMats[i] = dotMat;
    Gradient.eval(grd, prc, dotMat.fill);
  }
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));

  float step = frameCount * 0.0075;
  infEntity.rotateZ(0.01);

  rndr.background(0xff101010);
  if (mousePressed) {
    rndr.shape(infEntity, infMat);
    rndr.handles(infEntity);
  }

  for (int i = 0; i < count; ++i) {
    float prc = i * toPrc;
    float off = Utils.lerp(offMin, offMax, prc);

    CurveEntity2.eval(infEntity, 0, step + off, world, local);

    CurveEntity2 de2 = dotEntities[i];
    de2.moveTo(world.origin);
    rndr.shape(de2, dotMats[i]);
  }
}

void keyReleased() {
  String svg = rndr.toSvgString(dotEntities, dotMats);
  saveStrings("data/infinity.svg", new String[] {svg});
  println("Saved to SVG.");
}

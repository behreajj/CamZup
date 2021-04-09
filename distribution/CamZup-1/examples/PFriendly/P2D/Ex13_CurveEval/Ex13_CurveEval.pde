import camzup.core.*;
import camzup.pfriendly.*;

int count = 60;
float toPrc = 1.0 / (count - 1.0);
float offMin = -0.3125;
float offMax = 0.3125;
float dotMax = 25.0;
float dotMin = 10.0;

Yup2 graphics;

Curve2 infinity = Curve2.infinity(new Curve2());
CurveEntity2 infEntity = new CurveEntity2();
MaterialSolid infMat = new MaterialSolid()
  .setStroke(true)
  .setStroke(#fff7d5)
  .setStrokeWeight(1.25)
  .setFill(false);

CurveEntity2[] dotEntities = new CurveEntity2[count];
MaterialSolid[] dotMats = new MaterialSolid[count];

Color.AbstrEasing mixer = new Color.MixHsva();
Gradient grd = Gradient.paletteRyb(new Gradient());
Ray2 local = new Ray2();
Ray2 world = new Ray2();

void settings() {
  size(720, 405, Yup2.PATH_STR);
}

void setup() {
  frameRate(60.0);
  graphics = (Yup2)getGraphics();

  infEntity.append(infinity);
  infEntity.scaleTo((graphics.width + graphics.height) * 0.495);

  for (int i = 0; i < count; ++i) {
    float prc = i * toPrc;
    float dotSize = Utils.pingPong(dotMax, dotMin, prc);
    String name = "Dot." + Utils.toPadded(i, 2);

    Curve2 dot = Curve2.circle(new Curve2());
    dot.materialIndex = i;

    CurveEntity2 de2 = new CurveEntity2(name, dot);
    de2.scaleTo(dotSize);
    dotEntities[i] = de2;

    MaterialSolid dotMat = new MaterialSolid();
    dotMats[i] = dotMat;
    Gradient.eval(grd, prc, mixer, dotMat.fill);
    dotMat.name = Color.toHexString(dotMat.fill);
  }
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));

  float step = frameCount * 0.005;
  infEntity.rotateZ(0.01);

  graphics.background(0xff101010);
  if (mousePressed) {
    graphics.shape(infEntity, infMat);
    graphics.handles(infEntity);
  }

  for (int i = 0; i < count; ++i) {
    float prc = i * toPrc;
    float off = Utils.lerp(offMin, offMax, prc);

    CurveEntity2.eval(infEntity, 0, step + off, world, local);

    CurveEntity2 de2 = dotEntities[i];
    de2.moveTo(world.origin);
    graphics.shape(de2, dotMats[i]);
  }
}

void keyReleased() {
  String svg = graphics.toSvgString(dotEntities, dotMats);
  saveStrings("data/infinity.svg", new String[] {svg});
  println("Saved to SVG.");
}

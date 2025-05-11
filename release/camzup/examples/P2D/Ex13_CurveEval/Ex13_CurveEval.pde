import com.behreajj.camzup.core.*;
import com.behreajj.camzup.friendly.*;

int count = 60;
float toFac = 1.0f / (count - 1.0f);
float offMin = -0.3125f;
float offMax = 0.3125f;
float dotMax = 25.0f;
float dotMin = 10.0f;

Yup2 graphics;

Curve2 infinity = Curve2.infinity(new Curve2());
CurveEntity2 infEntity = new CurveEntity2();
MaterialSolid infMat = new MaterialSolid()
  .setStroke(true)
  .setStroke(new Rgb(1.0, 0.95, 0.925))
  .setStrokeWeight(1.25f)
  .setFill(false);

CurveEntity2[] dotEntities = new CurveEntity2[count];
MaterialSolid[] dotMats = new MaterialSolid[count];

Lab.AbstrEasing mixer = new Lab.MixSrgb();
Gradient grd = Gradient.paletteRyb(new Gradient());
Ray2 local = new Ray2();
Ray2 world = new Ray2();

void settings() {
  size(720, 405, Yup2.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (Yup2)getGraphics();

  infEntity.append(infinity);
  infEntity.scaleTo((graphics.width + graphics.height) * 0.495f);

  for (int i = 0; i < count; ++i) {
    float fac = i * toFac;
    float dotSize = Utils.pingPong(dotMax, dotMin, fac);
    String name = "Dot." + Utils.toPadded(i, 2);

    Curve2 dot = Curve2.circle(new Curve2());
    dot.materialIndex = i;

    CurveEntity2 de2 = new CurveEntity2(name, dot);
    de2.scaleTo(dotSize);
    dotEntities[i] = de2;

    MaterialSolid dotMat = new MaterialSolid();
    dotMats[i] = dotMat;
    
    Lab lab = new Lab();
    Gradient.eval(grd, fac, mixer, lab);
    Rgb.srLab2TosRgb(lab, dotMat.fill, new Rgb(), new Vec4());
        
    dotMat.name = Rgb.toHexString(dotMat.fill);
  }
}

void draw() {
  float step = frameCount * 0.005f;
  infEntity.rotateZ(0.01f);

  graphics.background(0xff101010);
  if (mousePressed) {
    graphics.shape(infEntity, infMat);
    graphics.handles(infEntity);
  }

  for (int i = 0; i < count; ++i) {
    float prc = i * toFac;
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

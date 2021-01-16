import camzup.core.*;
import camzup.pfriendly.*;

YupJ2 graphics;
int count = 7;

Gradient grd = Gradient.paletteViridis(new Gradient());
CurveEntity2 pie = new CurveEntity2();
TextEntity2[] labels = new TextEntity2[count];
MaterialSolid[] materials = new MaterialSolid[count];

float labelScale = 1.0;
Color labelColor = Color.black(new Color());

void settings() {
  size(720, 405, YupJ2.PATH_STR);
}

void setup() {
  graphics = (YupJ2)getGraphics();
  frameRate(60.0);

  pie.scaleTo(300);
  PFont font = createFont("Calibri", 20);
  Rng rng = new Rng();
  float[] vals = rng.segment(count, 1.0);

  float prevAng = 0.0;
  for (int i = 0; i < count; ++i) {
    float currAng = prevAng;
    currAng += vals[i] * Utils.TAU;

    Curve2 arc = new Curve2();
    arc.materialIndex = i;
    Curve2.arc(prevAng, currAng, 0.5, ArcMode.PIE, arc);
    pie.append(arc);

    Vec2 loc = new Vec2();
    Vec2.fromPolar(0.5 * (currAng + prevAng), 0.625, loc);
    Transform2.mulPoint(pie.transform, loc, loc);
    String str = Utils.toFixed(vals[i] * 100.0, 1) + "%";
    TextEntity2 label = labels[i] = new TextEntity2(
      font, str, labelColor, 0, 2, CENTER, CENTER, labelScale);
    label.moveTo(loc);

    float percent = i / (count - 1.0);
    MaterialSolid ms = materials[i] = new MaterialSolid();
    ms.setStroke(false);
    ms.setStroke(#202020);
    Gradient.eval(grd, percent, ms.fill);

    prevAng = currAng;
  }
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));

  graphics.background();
  graphics.shape(pie, materials);

  for (int i = 0; i < count; ++i) {
    graphics.text(labels[i]);
  }
}

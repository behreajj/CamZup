import com.behreajj.camzup.core.*;
import com.behreajj.camzup.friendly.*;

YupJ2 graphics;
int count = 7;

Lab.AbstrEasing mixer = new Lab.MixLab();
Gradient grd = Gradient.paletteViridis(new Gradient());
CurveEntity2 pie = new CurveEntity2();
TextEntity2[] labels = new TextEntity2[count];
MaterialSolid[] materials = new MaterialSolid[count];

float labelScale = 1.0f;
Rgb labelRgb = Rgb.black(new Rgb());

void settings() {
  size(720, 405, YupJ2.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (YupJ2)getGraphics();

  pie.scaleTo(300.0f);
  PFont font = createFont("Calibri", 20.0f);
  Rng rng = new Rng();
  float[] vals = rng.segment(count, 1.0f);

  float prevAng = 0.0f;
  for (int i = 0; i < count; ++i) {
    float currAng = prevAng;
    currAng += vals[i] * Utils.TAU;

    Curve2 arc = new Curve2();
    arc.materialIndex = i;
    Curve2.arc(prevAng, currAng, 0.5f, ArcMode.PIE, arc);
    pie.append(arc);

    Vec2 loc = new Vec2();
    Vec2.fromPolar(0.5f * (currAng + prevAng), 0.625f, loc);
    Transform2.mulPoint(pie.transform, loc, loc);
    String str = Utils.toFixed(vals[i] * 100.0f, 1) + "%";
    TextEntity2 label = labels[i] = new TextEntity2(
      font, str, labelRgb, 0, 2, CENTER, CENTER, labelScale);
    label.moveTo(loc);

    float percent = i / (count - 1.0f);
    MaterialSolid ms = materials[i] = new MaterialSolid();
    ms.setStroke(false);
    
    Lab lab = new Lab();
    Gradient.eval(grd, percent, lab);
    Rgb.srLab2TosRgb(lab, ms.fill, new Rgb(), new Vec4());

    prevAng = currAng;
  }
}

void draw() {
  graphics.background();
  graphics.shape(pie, materials);

  for (int i = 0; i < count; ++i) {
    graphics.text(labels[i]);
  }
}

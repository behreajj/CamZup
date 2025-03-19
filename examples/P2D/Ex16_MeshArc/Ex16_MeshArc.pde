import com.behreajj.camzup.core.*;
import com.behreajj.camzup.friendly.*;

YupJ2 graphics;
int count = 30;
float toStep = 1.0f / (count - 1.0f);
float maxScale = 0.0f;
float minScale = 0.0f;
MeshEntity2 entity = new MeshEntity2();
MaterialSolid[] materials = new MaterialSolid[count];
Lab.AbstrEasing mixer = new Lab.MixLab();
Gradient grd = Gradient.paletteRyb(new Gradient());

void settings() {
  size(720, 405, YupJ2.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (YupJ2)getGraphics();

  maxScale = 0.9875f * min(width, height);
  minScale = maxScale * 0.125f;
  for (int i = 0; i < count; ++i) {
    Mesh2 mesh = new Mesh2();
    mesh.materialIndex = i;
    entity.append(mesh);

    float step = 1.0f - i * toStep;

    MaterialSolid material = new MaterialSolid();
    Lab lab = new Lab();
    Gradient.eval(grd, step, lab);
    Rgb.srLab2TosRgb(lab, material.fill, new Rgb(), new Vec4());
    Rgb.clamp01(material.fill, material.fill);
    material.name = Rgb.toHexString(material.fill);
    materials[i] = material;
  }
}

void draw() {
  float startOffset = frameCount * 0.05f;
  float stopOffset = frameCount * 0.01f;

  for (int i = 0; i < count; ++i) {
    float step = i * toStep;
    float scale = Utils.lerp(minScale, maxScale, step);
    float startAngle = startOffset * step;
    float stopAngle = stopOffset + Utils.lerp(Utils.PI, Utils.TAU, step);
    Mesh2 mesh = entity.get(i);
    Mesh2.arc(startAngle, stopAngle, 0.975f, 64,
       PolyType.NGON, UvProfile.Arc.BOUNDS, mesh);
    mesh.scale(scale);
  }

  graphics.background(0xff202020);
  graphics.noStroke();
  graphics.shape(entity, materials);
}

void mouseReleased() {
  String result = graphics.toSvgString(entity, materials);
  saveStrings("data/mesh.svg", new String[] { result });
  println("Saved to svg.");
}

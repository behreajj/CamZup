import camzup.core.*;
import camzup.pfriendly.*;

YupJ2 rndr;
int count = 30;
float toStep = 1.0 / (count - 1.0);
float maxScale = 0.0;
float minScale = 0.0;
MeshEntity2 entity = new MeshEntity2();
MaterialSolid[] materials = new MaterialSolid[count];
Gradient grd = Gradient.paletteRyb(new Gradient());

void settings() {
  size(720, 405, YupJ2.PATH_STR);
}

void setup() {
  rndr = (YupJ2)getGraphics();

  maxScale = 0.9875 * Utils.min(width, height);
  minScale = maxScale * 0.125;
  for (int i = 0; i < count; ++i) {
    Mesh2 mesh = new Mesh2();
    mesh.materialIndex = i;
    entity.append(mesh);

    float step = 1.0 - i * toStep;
    Color clr = Gradient.eval(grd, step, new Color());
    MaterialSolid mat = new MaterialSolid();
    mat.setFill(clr);
    materials[i] = mat;
  }
}

void draw() {
  float startOffset = frameCount * 0.05;
  float stopOffset = frameCount * 0.01;

  for (int i = 0; i < count; ++i) {
    float step = i * toStep;
    float scale = Utils.lerp(minScale, maxScale, step);
    float startAngle = startOffset * step;
    float stopAngle = stopOffset + Utils.lerp(Utils.PI, Utils.TAU, step);
    Mesh2 mesh = entity.get(i);
    Mesh2.arc(startAngle, stopAngle, 0.975, 64, PolyType.NGON, mesh);
    mesh.scale(scale);
  }

  surface.setTitle(Utils.toFixed(frameRate, 2));
  rndr.background(#202020);
  rndr.noStroke();
  rndr.shape(entity, materials);
}

void mouseReleased() {
  String result = rndr.toSvgString(entity, materials);
  saveStrings("data/mesh.svg", new String[] { result });
  println("Saved to svg.");
}

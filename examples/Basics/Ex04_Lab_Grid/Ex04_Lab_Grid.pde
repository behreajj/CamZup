import com.behreajj.camzup.core.*;
import com.behreajj.camzup.friendly.*;

Zup3 graphics;
MeshEntity3 me = new MeshEntity3();
MaterialSolid[] materials;
int lCount = 24;

void settings() {
  size(720, 405, Zup3.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (Zup3)getGraphics();

  Rgb lrgb = new Rgb();
  Vec4 xyz = new Vec4();

  float absa = Utils.max(
    Utils.abs(Lab.SR_A_MIN),
    Utils.abs(Lab.SR_A_MAX));
  float absb = Utils.max(
    Utils.abs(Lab.SR_B_MIN),
    Utils.abs(Lab.SR_B_MAX));

  int aCount = Utils.ceil(lCount * absa / 100.0f);
  int bCount = Utils.ceil(lCount * absb / 100.0f);

  int msCount = 0;
  ArrayList<MaterialSolid> msList = new ArrayList<>();
  Lab[][][] grd = Lab.grid(lCount, aCount, bCount);
  Lab[] flat = Lab.flat(grd);
  for (Lab cell : flat) {
    Rgb srgb = Rgb.srLab2TosRgb(cell,
      new Rgb(), lrgb, xyz);
    if (Rgb.isInGamut(srgb, Utils.EPSILON)) {
      MaterialSolid ms = new MaterialSolid();
      ms.setFill(srgb);
      ms.setStroke(false);
      ms.setStrokeWeight(0.0f);
      msList.add(ms);

      Mesh3 mesh = Mesh3.cube(new Mesh3());
      mesh.materialIndex = msCount;
      mesh.scale(10.0f);
      mesh.translate(new Vec3(
        cell.a,
        cell.b,
        cell.l * 2.0f - 100.0f));
      me.append(mesh);

      ++msCount;
    }
  }

  materials = msList.toArray(
    new MaterialSolid[msList.size()]);
  me.scaleTo(1.5f);
}

void draw() {
  graphics.lights();
  graphics.ortho();
  graphics.camera();
  graphics.background(0xff202020);

  graphics.rotateZ(frameCount * 0.01f);
  graphics.shape(me, materials);
}

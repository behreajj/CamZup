import com.behreajj.camzup.core.*;
import com.behreajj.camzup.friendly.*;

int rings = 10;
float radius = 0.5f;
float padding = 0.05f;
float faceInset = 0.8f;

YupJ2 graphics;
MaterialSolid[] materials;
MeshEntity2 entity2 = new MeshEntity2();
Lab.AbstrEasing mixer = new Lab.MixLab();
Gradient grd = Gradient.paletteViridis(
  new Gradient());

void settings() {
  size(720, 405, YupJ2.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (YupJ2)getGraphics();

  Mesh2 hex = new Mesh2();
  Mesh2.gridHex(rings, radius, padding, hex);
  Mesh2[] meshes = Mesh2.detachFaces(hex);
  int len = meshes.length;
  materials = new MaterialSolid[len];

  float toPercent = 1.0f / (len - 1.0f);
  Vec2 center = new Vec2();
  for (int i = 0; i < len; ++i) {
    float fac = i * toPercent;
    Mesh2 cell = meshes[i];

    cell.materialIndex = i;
    MaterialSolid material = new MaterialSolid();
    material.setStroke(true);
    material.setStrokeWeight(0.5f);
    material.setStroke(new Rgb(1.0, 1.0, 1.0));
    
    Lab lab = new Lab();
    Gradient.eval(grd, fac, mixer, lab);
    Rgb.srLab2TosRgb(lab, material.fill, new Rgb(), new Vec4());
    
    material.name = Rgb.toHexString(material.fill);
    materials[i] = material;

    if (i % 3 == 2) {
      cell.subdivFacesCenter(1);
      Mesh2.uniformData(cell, cell);
      Face2[] faces = cell.getFaces();
      int facesLen = faces.length;
      for (int j = 0; j < facesLen; ++j) {
        Face2 subface = faces[j];
        subface.scaleLocal(faceInset, center);
      }
    }

    entity2.append(cell);
  }

  float shortEdge = min(width, height);
  float scl = Utils.ONE_SQRT_2 * shortEdge / (rings + 0.25f);
  entity2.scaleTo(scl);
}

void draw() {
  graphics.background(0xff101010);
  graphics.shape(entity2, materials);
}

void mouseReleased() {
  String str = graphics.toSvgString(entity2, materials);
  saveStrings("data/hexGrid.svg", new String[] {str});
  println("Saved to svg.");
}

import camzup.core.*;
import camzup.pfriendly.*;

int rings = 10;
float radius = 0.5;
float padding = 0.05;
float faceInset = 0.85;
float rounding = 0.1;
float subfRound =0.0375;

Yup2 graphics;
MaterialSolid[] materials;
MeshEntity2 entity2 = new MeshEntity2();
Gradient gradient = Gradient.paletteSepia(new Gradient());

void settings() {
  size(720, 405, Yup2.PATH_STR);
}

void setup() {
  graphics = (Yup2)getGraphics();
  frameRate(60.0);

  Mesh2 hex = new Mesh2();
  Mesh2.gridHex(rings, radius, padding, hex);
  Mesh2[] meshes = Mesh2.detachFaces(hex);
  int len = meshes.length;
  materials = new MaterialSolid[len];

  float toPercent = 1.0 / (len - 1.0);
  Vec2 center = new Vec2();
  for (int i = 0; i < len; ++i) {
    float fac = i * toPercent;
    Mesh2 cell = meshes[i];

    cell.materialIndex = i;
    int fill = Gradient.eval(gradient, fac);
    materials[i] = new MaterialSolid()
      .setFill(fill)
      .setStrokeWeight(0.5)
      .setStroke(#171717)
      .setStroke(true);

    if (i % 3 != 2) {
      cell.roundCorners(0, rounding, 3);
    } else {
      cell.subdivFacesCenter(1);
      Mesh2.uniformData(cell, cell);
      Face2[] faces = cell.getFaces();
      int facesLen = faces.length;
      for (int j = 0; j < facesLen; ++j) {
        Face2 subface = faces[j];
        subface.scaleLocal(faceInset, center);
        cell.roundCorners(j, subfRound, 3);
      }
    }

    entity2.append(cell);
  }

  float shortEdge = Utils.min(width, height);
  float scl = IUtils.ONE_SQRT_2 * shortEdge / (rings + 0.25);
  entity2.scaleTo(scl);
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));

  graphics.background(#101010);
  graphics.shape(entity2, materials);
}

void mouseReleased() {
  String str = graphics.toSvgString(entity2, materials);
  saveStrings("data/hexGrid.svg", new String[] {str});
  println("Saved to svg.");
}

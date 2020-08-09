import camzup.core.*;
import camzup.pfriendly.*;

Zup3 rndr;

Mesh3 mesh = new Mesh3();
MeshEntity3 entity = new MeshEntity3();
MaterialSolid material = new MaterialSolid()
  .setFill(#ff2828);

void settings() {
  size(720, 405, Zup3.PATH_STR);
}

void setup() {
  rndr = (Zup3)getGraphics();

  Mesh3.icosphere(1, mesh);
  mesh.subdivFacesCenter(1);
  mesh.shadeFlat();
  Mesh3.uniformData(mesh, mesh);

  Vec3 center = new Vec3();
  for (Face3 f : mesh) {
    f.scaleLocal(0.91, center);
  }

  mesh.extrudeFaces(0.02);
  mesh.clean();

  entity.append(mesh);
  entity.scaleTo(384.0);
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));

  entity.rotateZ(0.01);

  rndr.lights();
  rndr.ortho();
  rndr.camera();
  rndr.background();
  rndr.shape(entity, material);
}

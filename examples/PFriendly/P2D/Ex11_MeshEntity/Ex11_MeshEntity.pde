import camzup.core.*;
import camzup.pfriendly.*;

Yup2 graphics2;

MaterialSolid material = new MaterialSolid()
  .setStroke(true)
  .setStroke(#254441)
  .setFill(true)
  .setFill(#43aa8b)
  .setStrokeWeight(0.01);

Transform2 transform = new Transform2()
  .moveTo(new Vec2(100.0, 50.0))
  .scaleTo(100.0);

Mesh2 mesh = Mesh2.polygon(new Mesh2(), 6, Mesh2.PolyType.TRI);

MeshEntity2 entity = new MeshEntity2("Example", transform)
  .appendMesh(mesh)
  .appendMaterial(material);

void setup() {
  size(720, 405, "camzup.pfriendly.Yup2");
  smooth(8);
  graphics2 = (Yup2)getGraphics();
}

void draw() {
  entity.transform.rotateZ(-0.05);
  
  surface.setTitle(String.format("%.1f", frameRate));

  background(#fff7d5);
  graphics2.camera();
  graphics2.origin();
  graphics2.shape(entity);
}

void mouseReleased() {
  String result = graphics2.toSvgString(entity);
  saveStrings("data/mesh.svg", new String[] { result });
  println("Saved to svg.");
}

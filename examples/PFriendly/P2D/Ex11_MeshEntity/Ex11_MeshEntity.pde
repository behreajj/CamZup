import camzup.core.*;
import camzup.pfriendly.*;

Yup2 graphics2;

MaterialSolid material = new MaterialSolid()
  .setStroke(true)
  .setStroke(IUp.DEFAULT_HANDLE_FORE_COLOR)
  .setFill(true)
  .setFill(IUp.DEFAULT_HANDLE_COLOR)
  .setStrokeWeight(0.01);

Transform2 transform = new Transform2()
  .moveTo(new Vec2(-100.0, -50.0))
  .scaleTo(150.0);

Mesh2 mesh = Mesh2.ring(6, 0.5, Mesh2.PolyType.TRI, new Mesh2());

MeshEntity2 entity = new MeshEntity2("Example", transform)
  .appendMesh(mesh)
  .appendMaterial(material);

void setup() {
  size(720, 405, "camzup.pfriendly.Yup2");
  smooth(8);
  graphics2 = (Yup2)getGraphics();
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));

  entity.transform.rotateZ(0.02);

  graphics2.background();
  graphics2.origin();
  graphics2.shape(entity);
}

void mouseReleased() {
  String result = graphics2.toSvgString(entity);
  saveStrings("data/mesh.svg", new String[] { result });
  println("Saved to svg.");
}

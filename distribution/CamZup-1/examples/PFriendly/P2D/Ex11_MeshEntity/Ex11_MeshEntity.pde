import camzup.core.*;
import camzup.pfriendly.*;

YupJ2 graphics2;

MaterialSolid material = new MaterialSolid()
  .setStroke(true)
  .setStroke(#ffbfaf)
  .setFill(true)
  .setFill(#7f000f)
  .setStrokeWeight(0.01);

Transform2 transform = new Transform2()
  .moveTo(new Vec2(-100.0, -50.0))
  .scaleTo(150.0);

Mesh2 mesh = Mesh2.polygon(new Mesh2(), 6, Mesh2.PolyType.TRI);

MeshEntity2 entity = new MeshEntity2("Example", transform)
  .appendMesh(mesh)
  .appendMaterial(material);

void setup() {
  size(720, 405, "camzup.pfriendly.YupJ2");
  smooth(8);
  graphics2 = (YupJ2)getGraphics();
}

void draw() {
  entity.transform.rotateZ(0.025);

  surface.setTitle(Utils.toFixed(frameRate, 1));

  graphics2.background();
  graphics2.origin();
  graphics2.shape(entity);
}

void mouseReleased() {
  String result = graphics2.toSvgString(entity);
  saveStrings("data/mesh.svg", new String[] { result });
  println("Saved to svg.");
}

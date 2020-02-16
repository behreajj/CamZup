import camzup.core.*;
import camzup.pfriendly.*;

YupJ2 graphics2;

MaterialSolid material = new MaterialSolid()
  .setStroke(true)
  .setStroke(#1f7fff)
  .setFill(true)
  .setFill(#303030)
  .setStrokeWeight(1.0);

Transform2 transform = new Transform2()
  .moveTo(new Vec2(-100.0, -50.0))
  .scaleTo(150.0);

Mesh2 mesh = Mesh2.ring(0.5, 6, Mesh2.PolyType.NGON, new Mesh2());

MeshEntity2 entity = new MeshEntity2("Example", transform)
  .appendMesh(mesh);

void settings() {
  size(720, 405, "camzup.pfriendly.YupJ2");
  smooth(8);
}

void setup() {
  graphics2 = (YupJ2)getGraphics();
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));
  entity.rotateZ(0.02);
  graphics2.background();
  graphics2.origin();
  graphics2.shape(entity, new MaterialSolid[] { material });
}

void mouseReleased() {
  String result = graphics2.toSvgString(entity);
  saveStrings("data/mesh.svg", new String[] { result });
  println("Saved to svg.");
}

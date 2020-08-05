import camzup.core.*;
import camzup.pfriendly.*;

YupJ2 rndr;

MaterialSolid material = new MaterialSolid()
  .setStroke(false)
  .setFill(true)
  .setFill(#202020);

Transform2 transform = new Transform2()
  .moveTo(new Vec2(-100.0, -50.0))
  .scaleTo(150.0);

Mesh2 mesh = Mesh2.ring(0.5, 6, Mesh2.PolyType.TRI, new Mesh2());

MeshEntity2 entity = new MeshEntity2("Example", transform)
  .append(mesh);

void settings() {
  size(720, 405, YupJ2.PATH_STR);
}

void setup() {
  rndr = (YupJ2)getGraphics();
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));
  entity.rotateZ(0.02);
  entity.transform.moveByLocal(new Vec2(0.005, 0.0));
  rndr.background();
  rndr.origin();
  rndr.shape(entity, material);
}

void mouseReleased() {
  String result = rndr.toSvgString(entity, material);
  saveStrings("data/mesh.svg", new String[] { result });
  println("Saved to svg.");
}

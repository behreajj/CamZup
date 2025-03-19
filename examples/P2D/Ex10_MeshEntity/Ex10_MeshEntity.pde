import com.behreajj.camzup.core.*;
import com.behreajj.camzup.friendly.*;

Yup2 graphics;

MaterialSolid material = new MaterialSolid()
  .setStroke(true)
  .setStroke(new Rgb(0.0, 0.5, 1.0))
  .setStrokeWeight(1.25f)
  .setFill(true)
  .setFill(new Rgb(0.2, 0.2, 0.2));

Transform2 transform = new Transform2()
  .moveTo(new Vec2(-100.0f, -50.0f))
  .scaleTo(150.0f);

Mesh2 mesh = Mesh2.polygon(6, PolyType.TRI, new Mesh2());

MeshEntity2 entity = new MeshEntity2("Example", transform)
  .append(mesh);

void settings() {
  size(720, 405, Yup2.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (Yup2)getGraphics();
}

void draw() {
  entity.rotateZ(0.02f);
  entity.transform.moveByLocal(new Vec2(0.0f, 1.0f));
  graphics.background();
  graphics.origin();
  graphics.shape(entity, material);
}

void mouseReleased() {
  String result = graphics.toSvgString(entity, material);
  saveStrings("data/mesh.svg", new String[] { result });
  println("Saved to svg.");
}

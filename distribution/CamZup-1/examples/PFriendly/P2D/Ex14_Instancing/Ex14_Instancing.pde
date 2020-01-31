import camzup.core.*;
import camzup.pfriendly.*;

int entityCount = 1000;
MeshEntity2[] entities = new MeshEntity2[entityCount];
Random rng = new Random();
Yup2 graphics2;
Mesh2 mesh = Mesh2.polygon(6, Mesh2.PolyType.NGON, new Mesh2());
Vec2 locub;
Vec2 loclb;

MaterialSolid material = new MaterialSolid()
  .setStroke(false)
  .setStroke(IUp.DEFAULT_HANDLE_FORE_COLOR)
  .setFill(true)
  .setFill(IUp.DEFAULT_HANDLE_COLOR)
  .setStrokeWeight(0.01);

void setup() {
  size(720, 405, "camzup.pfriendly.Yup2");
  smooth(8);
  frameRate(1000);
  graphics2 = (Yup2)getGraphics();

  locub = new Vec2(width * 0.5, height * 0.5);
  loclb = Vec2.negate(locub, new Vec2());

  float scllb = 4;
  float sclub = 16;

  for (int i = 0; i < entityCount; ++i) {

    String name = "Entity." + (i + 1);
    Vec2 loc = Vec2.randomCartesian(rng, loclb, locub, new Vec2());
    float ang = rng.uniform(-Utils.PI, Utils.PI);
    float sc = rng.uniform(scllb, sclub);
    Vec2 scl = new Vec2(sc, sc);

    Transform2 transform = new Transform2(loc, ang, scl);
    entities[i] = new MeshEntity2(name, transform)
      .appendMesh(mesh)
      .appendMaterial(material);
  }
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));

  graphics2.background();
  graphics2.origin();

  Vec2 translate = new Vec2();
  for (MeshEntity2 entity : entities) {
    Vec2.fromPolar(entity.transform.getRotation(), translate);
    Vec2.mul(translate, rng.uniform(0.05, 0.5), translate);
    entity.transform.rotateZ(rng.uniform(-0.02, 0.02))
      .moveBy(translate);
    entity.transform.wrap(loclb, locub);
    graphics2.shape(entity);
  }
}

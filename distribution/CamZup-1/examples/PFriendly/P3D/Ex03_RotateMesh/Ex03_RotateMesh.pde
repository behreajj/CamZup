import camzup.core.*;
import camzup.pfriendly.*;

Yup3 graphics;

float rotSpeed = 1.75;
float smoothing = 0.075;

Vec3 mouse = new Vec3();
Vec3 mouseNorm = new Vec3();
Vec3 ref = Vec3.up(new Vec3());

Quaternion rotMouse = new Quaternion();
Quaternion rotWorld = new Quaternion();
Quaternion rotSphere = new Quaternion();
Quaternion identity = new Quaternion();

MeshEntity3 entity = new MeshEntity3();

void settings() {
  size(720, 405, "camzup.pfriendly.Yup3");
}

void setup() {
  graphics = (Yup3)getGraphics();
  graphics.camera(
    0.0, 0.0, -height * Utils.SQRT_3_2,
    0.0, 0.0, 0.0,
    0.0, 1.0, 0.0);

  Mesh3 mesh = Mesh3.dodecahedron(new Mesh3());
  entity.appendMesh(mesh);

  MaterialSolid material = new MaterialSolid()
    .setFill(#00939c);
  entity.appendMaterial(material);

  entity.scaleTo(min(width, height) * 0.5);
}

void draw() {
  surface.setTitle(
    new StringBuilder()
    .append(Utils.toFixed(frameRate, 1))
    .append(' ')
    .append(mouse)
    .toString());

  if (mousePressed) {
    graphics.mouse1(mouse);
    float mag = Vec3.mag(mouse);
    if (mag > 0.0) {
      float ang = rotSpeed * mag;
      Vec3.crossNorm(mouse, ref, mouseNorm);
      Quaternion.fromAxisAngle(ang, mouseNorm, rotMouse);
    }
  } else {
    rotMouse = Quaternion.mix(
      rotMouse, identity, smoothing, rotMouse);
  }

  Quaternion.mul(rotMouse, rotSphere, rotWorld);
  Quaternion.mix(
    rotSphere, rotWorld, smoothing, rotSphere);

  entity.rotateTo(rotSphere);

  graphics.background();
  graphics.lights();
  graphics.origin();
  graphics.shape(entity);
}

import camzup.core.*;
import camzup.pfriendly.*;

Zup3 rndr;
Mesh3 mesh = new Mesh3();
MeshEntity3 entity = new MeshEntity3();
Vec3 mouse1 = new Vec3();

MaterialSolid mat = new MaterialSolid()
  .setFill(#007fff)
  .setStroke(false);

Transform3.Easing mixer = new Transform3.Easing(
  new Vec3.SmoothStep(),
  new Quaternion.Slerp(),
  new Vec3.SmoothStep());

Transform3[] frames = {
  new Transform3(
  new Vec3(-256.0, 256.0, 50.0),
  new Quaternion(1.0, 0.0, 0.0, 0.0),
  new Vec3(200.0, 200.0, 200.0)),

  new Transform3(
  new Vec3(-256.0, -256.0, -50.0),
  new Quaternion(0.0, 1.0, 0.0, 0.0),
  new Vec3(300.0, 100.0, 200.0)),

  new Transform3(
  new Vec3(256.0, -256.0, 50.0),
  new Quaternion(0.0, 0.0, 1.0, 0.0),
  new Vec3(200.0, 300.0, 100.0)),

  new Transform3(
  new Vec3(256.0, 256.0, -50.0),
  new Quaternion(0.0, 0.0, 0.0, 1.0),
  new Vec3(100.0, 200.0, 300.0)),

  new Transform3(
  new Vec3(-256.0, 256.0, 50.0),
  new Quaternion(1.0, 0.0, 0.0, 0.0),
  new Vec3(200.0, 200.0, 200.0))
};

void settings() {
  size(720, 405, Zup3.PATH_STR);
}

void setup() {
  rndr = (Zup3)getGraphics();
  Mesh3.cube(mesh);
  entity.append(mesh);
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));

  float step = Utils.fract(frameCount * 0.005);
  mixer.apply(frames, step, entity.transform);

  if (mousePressed) {
    rndr.mouse1s(mouse1);
    if (mouseButton == LEFT) {
      Vec3.mul(mouse1, 100.0, mouse1);
      rndr.moveByLocal(mouse1);
    } else if (mouseButton == CENTER) {
      Vec3.mul(mouse1, 37.5, mouse1);
      rndr.strafe(mouse1);
    }
  }

  rndr.background(#131313);
  rndr.lights();
  rndr.grid(16, 2.0, #fff7d5, 1024.0);
  rndr.ortho(0.325);
  rndr.camera();

  rndr.shape(entity, mat);
}

void keyReleased() {
  if (keyCode == 48 || keyCode == 128) {
    rndr.defaultCamera();
  } else if (keyCode == 49 || keyCode == 129) {
    rndr.camNorth();
  } else if (keyCode == 51 || keyCode == 131) {
    rndr.camWest();
  } else if (keyCode == 55 || keyCode == 135) {
    rndr.camTop();
  } else if (keyCode == 50 || keyCode == 130) {
    rndr.moveByLocal(0.0, -150.0, 0.0);
  } else if (keyCode == 52 || keyCode == 132) {
    rndr.moveByLocal(-150.0, 0.0, 0.0);
  } else if (keyCode == 54 || keyCode == 134) {
    rndr.moveByLocal(150.0, 0.0, 0.0);
  } else if (keyCode == 56 || keyCode == 136) {
    rndr.moveByLocal(0.0, 150.0, 0.0);
  }
}

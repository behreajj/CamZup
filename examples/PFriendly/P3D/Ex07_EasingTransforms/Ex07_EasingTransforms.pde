import camzup.core.*;
import camzup.pfriendly.*;

Zup3 graphics;
Mesh3 mesh = new Mesh3();
MeshEntity3 entity = new MeshEntity3();
Vec3 mouse1 = new Vec3();
Rng rng = new Rng();

MaterialSolid mat = new MaterialSolid()
  .setFill(0xff007fff)
  .setStroke(false);

Transform3.Easing mixer = new Transform3.Easing(
  new Vec3.Lerp(),
  new Quaternion.Slerp(),
  new Vec3.SmoothStep());

int fCount = 8;
Transform3[] frames = new Transform3[fCount];

void settings() {
  size(720, 405, Zup3.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (Zup3)getGraphics();
  Mesh3.cube(mesh);
  entity.append(mesh);

  for (int i = 0; i < fCount - 1; ++i) {
    frames[i] = Transform3.random(rng,
      -512.0f, 512.0f,
      50.0f, 300.0f,
      new Transform3());
  }
  frames[fCount - 1] = frames[0];
}

void draw() {
  float step = Utils.fract(frameCount * 0.005f);
  mixer.apply(frames, step, entity.transform);

  if (mousePressed) {
    graphics.mouse1s(mouse1);
    if (mouseButton == LEFT) {
      Vec3.mul(mouse1, 100.0f, mouse1);
      graphics.moveByLocal(mouse1);
    } else if (mouseButton == CENTER) {
      Vec3.mul(mouse1, 37.5f, mouse1);
      graphics.strafe(mouse1);
    }
  }

  graphics.background(0xff131313);
  graphics.lights();
  graphics.grid(16, 2.0f, 0xfffff7d5, 1024.0f);
  graphics.ortho(0.325f);
  graphics.camera();

  graphics.shape(entity, mat);
}

void keyReleased() {
  if (keyCode == 48 || keyCode == 128) {
    graphics.defaultCamera();
  } else if (keyCode == 49 || keyCode == 129) {
    graphics.camNorth();
  } else if (keyCode == 51 || keyCode == 131) {
    graphics.camWest();
  } else if (keyCode == 50 || keyCode == 130) {
    graphics.moveByLocal(0.0f, -150.0f, 0.0f);
  } else if (keyCode == 52 || keyCode == 132) {
    graphics.moveByLocal(-150.0f, 0.0f, 0.0f);
  } else if (keyCode == 54 || keyCode == 134) {
    graphics.moveByLocal(150.0f, 0.0f, 0.0f);
  } else if (keyCode == 56 || keyCode == 136) {
    graphics.moveByLocal(0.0f, 150.0f, 0.0f);
  }
}

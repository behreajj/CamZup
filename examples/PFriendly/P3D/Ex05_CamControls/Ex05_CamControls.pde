import camzup.core.*;
import camzup.pfriendly.*;

String instructions = " | Mouse Left: Orbit, " +
  "Mouse Center: Strafe, " +
  "Mouse Right: Toggle grid";

Zup3 rndr;
MeshEntity3 me3 = new MeshEntity3();
Gradient grd = Gradient.paletteRyb(new Gradient());
MaterialSolid[] solids;

float orthoZoom = 0.5;
boolean showGrid = true;
boolean useOrtho = false;

float strafeDefSpeed = 25.0;
float orbitDefSpeed = 35.0;

float strafeMaxSpeed = strafeDefSpeed * 2.0;
float orbitMaxSpeed = orbitDefSpeed * 2.0;

float dollySpeed = 25.0;
float strafeSpeed = strafeDefSpeed;
float orbitSpeed = orbitDefSpeed;

Vec3 m1s = new Vec3();
Vec3 lookWorld = new Vec3();

void settings() {
  size(720, 405, Zup3.PATH_STR);
  smooth(8);
}

void setup() {
  rndr = (Zup3)getGraphics();

  int count = 20;
  float toTheta = Utils.TAU / count;
  float toPercent = 1.0 / (count - 1.0);
  float rad0 = 0.75;
  float rad1 = 1.25;
  Vec3 origin = new Vec3();
  Vec3 dest = new Vec3();
  solids = new MaterialSolid[count];
  
  for (int i = 0; i < count; ++i) {
    float theta = i * toTheta;
    float percent = i * toPercent;
    Vec3.fromPolar(theta, rad0, origin);
    Vec3.fromPolar(theta, rad1, dest);
    
    Mesh3 mesh = new Mesh3();
    Mesh3.cylinder(origin, dest, 6, true, 0.05, mesh);
    mesh.materialIndex = i;
    me3.append(mesh);

    solids[i] = new MaterialSolid();
    grd.eval(percent, solids[i].fill);
  }
  
  me3.scaleTo(rndr.width * 0.5);
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1) + instructions);

  rndr.getLookTarget(lookWorld);
  me3.rotateX(0.01);

  if (useOrtho) {
    rndr.ortho(orthoZoom);
  } else {
    rndr.perspective();
  }

  mouseControls();

  rndr.background(#202020);
  rndr.lights();

  if (showGrid) {
    rndr.grid(32, 2.5, #fff7d5, rndr.width * 2.0);
  }
  rndr.shape(me3, solids);

  rndr.strokeWeight(5.0);
  rndr.stroke(#ffff3f);
  rndr.point(lookWorld);
}


void mouseControls() {
  if (mousePressed) {

    rndr.mouse1(m1s);

    if (mouseButton == LEFT) {
      Vec3 v = Vec3.mul(m1s, orbitSpeed, new Vec3());
      rndr.moveByLocal(v);
      orbitSpeed = Utils.lerp(
        orbitSpeed, orbitMaxSpeed, 0.01);
    }

    if (mouseButton == CENTER) {
      Vec3 v = Vec3.mul(m1s, strafeSpeed, new Vec3());
      rndr.strafe(v);
      strafeSpeed = Utils.lerp(
        strafeSpeed, strafeMaxSpeed, 0.01);
    }
  } else {
    rndr.camera();

    orbitSpeed = Utils.lerp(
      orbitSpeed, orbitDefSpeed, 0.025);
    strafeSpeed = Utils.lerp(
      strafeSpeed, strafeDefSpeed, 0.025);
  }
}

void mouseWheel(MouseEvent event) {
  float count = event.getCount();
  rndr.dolly(dollySpeed * count);
  //rndr.moveBy(0.0, 0.0, -150.0 * count);
  orthoZoom -= 0.05 * count;
}

void mouseReleased() {
  if (mouseButton == RIGHT) {
    showGrid = !showGrid;
  }
}

void keyPressed() {

  if (keyCode == 50 || keyCode == 130) {
    // Two or numpad two.
    rndr.moveByLocal(0.0, -150.0, 0.0);
  } else if (keyCode == 52 || keyCode == 132) {
    // Four or numpad four.
    rndr.moveByLocal(-150.0, 0.0, 0.0);
  } else if (keyCode == 54 || keyCode == 134) {
    // Six or numpad six.
    rndr.moveByLocal(150.0, 0.0, 0.0);
  } else if (keyCode == 56 || keyCode == 136) {
    // Eight or numpad eight.
    rndr.moveByLocal(0.0, 150.0, 0.0);
  }
}

void keyReleased() {

  if (keyCode == 48 || keyCode == 128) {
    // Zero or numpad zero.
    println("DEFAULT");
    rndr.defaultCamera();
  } else if (keyCode == 49 || keyCode == 129) {
    // One or numpad one.
    println("NORTH");
    rndr.camNorth();
  } else if (keyCode == 51 || keyCode == 131) {
    // Three or numpad three.
    println("WEST");
    rndr.camWest();
  } else if (keyCode == 53 || keyCode == 133) {
    // Five or numpad five.
    useOrtho = !useOrtho;
  } else if (keyCode == 55 || keyCode == 135) {
    // Seven or numpad seven.
    println("TOP");
    rndr.camTop();
  }
}

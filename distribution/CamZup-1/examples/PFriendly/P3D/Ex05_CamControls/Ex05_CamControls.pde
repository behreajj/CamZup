import camzup.core.*;
import camzup.pfriendly.*;

String instructions = " | Mouse Left: Orbit, " +
  "Mouse Center: Strafe, " +
  "Mouse Right: Toggle grid";

Zup3 graphics;
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
}

void setup() {
  frameRate(60.0);
  graphics = (Zup3)getGraphics();

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
    mesh.shadeFlat();
    mesh.materialIndex = i;
    me3.append(mesh);

    solids[i] = new MaterialSolid();
    Gradient.eval(grd, percent, solids[i].fill);
  }
  
  me3.scaleTo(graphics.width * 0.5);
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1) + instructions);

  graphics.getLookTarget(lookWorld);
  me3.rotateX(0.01);

  if (useOrtho) {
    graphics.ortho(orthoZoom);
  } else {
    graphics.perspective();
  }

  mouseControls();

  graphics.background(#101010);
  graphics.lights();

  if (showGrid) {
    graphics.grid(32, 2.5, #fff7d5, graphics.width * 2.0);
  }
  graphics.shape(me3, solids);

  graphics.strokeWeight(5.0);
  graphics.stroke(#ffff3f);
  graphics.point(lookWorld);
}

void mouseControls() {
  if (mousePressed) {

    graphics.mouse1s(m1s);

    if (mouseButton == LEFT) {
      Vec3 v = Vec3.mul(m1s, orbitSpeed, new Vec3());
      graphics.moveByLocal(v);
      orbitSpeed = Utils.lerp(
        orbitSpeed, orbitMaxSpeed, 0.01);
    }

    if (mouseButton == CENTER) {
      Vec3 v = Vec3.mul(m1s, strafeSpeed, new Vec3());
      graphics.strafe(v);
      strafeSpeed = Utils.lerp(
        strafeSpeed, strafeMaxSpeed, 0.01);
    }
  } else {
    graphics.camera();

    orbitSpeed = Utils.lerp(
      orbitSpeed, orbitDefSpeed, 0.025);
    strafeSpeed = Utils.lerp(
      strafeSpeed, strafeDefSpeed, 0.025);
  }
}

void mouseWheel(MouseEvent event) {
  float count = event.getCount();
  graphics.dolly(dollySpeed * count);
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
    graphics.moveByLocal(0.0, -150.0, 0.0);
  } else if (keyCode == 52 || keyCode == 132) {
    // Four or numpad four.
    graphics.moveByLocal(-150.0, 0.0, 0.0);
  } else if (keyCode == 54 || keyCode == 134) {
    // Six or numpad six.
    graphics.moveByLocal(150.0, 0.0, 0.0);
  } else if (keyCode == 56 || keyCode == 136) {
    // Eight or numpad eight.
    graphics.moveByLocal(0.0, 150.0, 0.0);
  }
}

void keyReleased() {

  if (keyCode == 48 || keyCode == 128) {
    // Zero or numpad zero.
    println("DEFAULT");
    graphics.defaultCamera();
  } else if (keyCode == 49 || keyCode == 129) {
    // One or numpad one.
    println("NORTH");
    graphics.camNorth();
  } else if (keyCode == 51 || keyCode == 131) {
    // Three or numpad three.
    println("WEST");
    graphics.camWest();
  } else if (keyCode == 53 || keyCode == 133) {
    // Five or numpad five.
    useOrtho = !useOrtho;
  }
}

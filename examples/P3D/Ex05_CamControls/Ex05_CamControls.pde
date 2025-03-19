import com.behreajj.camzup.core.*;
import com.behreajj.camzup.friendly.*;

String instructions = " | Mouse Left: Orbit, " +
  "Mouse Center: Strafe, " +
  "Mouse Right: Toggle grid";

Zup3 graphics;
MeshEntity3 me3 = new MeshEntity3();
Lab.AbstrEasing mixer = new Lab.MixSrgb();
Gradient grd = Gradient.paletteRyb(new Gradient());
MaterialSolid[] solids;

float orthoZoom = 0.5f;
boolean showGrid = true;
boolean useOrtho = false;

float strafeDefSpeed = 25.0f;
float orbitDefSpeed = 35.0f;

float strafeMaxSpeed = strafeDefSpeed * 2.0f;
float orbitMaxSpeed = orbitDefSpeed * 2.0f;

float dollySpeed = 25.0f;
float strafeSpeed = strafeDefSpeed;
float orbitSpeed = orbitDefSpeed;

Vec3 m1s = new Vec3();
Vec3 lookWorld = new Vec3();

void settings() {
  size(720, 405, Zup3.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (Zup3)getGraphics();

  int count = 20;
  float toTheta = Utils.TAU / count;
  float toFac = 1.0f / (count - 1.0f);
  float rad0 = 0.75f;
  float rad1 = 1.25f;
  Vec3 origin = new Vec3();
  Vec3 dest = new Vec3();
  solids = new MaterialSolid[count];
  
  for (int i = 0; i < count; ++i) {
    float theta = i * toTheta;
    float fac = i * toFac;
    Vec3.fromSpherical(theta, 0.0f, rad0, origin);
    Vec3.fromSpherical(theta, 0.0f, rad1, dest);
    
    Mesh3 mesh = new Mesh3();
    Mesh3.cylinder(origin, dest, 6, true, 0.05f, mesh);
    mesh.shadeFlat();
    mesh.materialIndex = i;
    me3.append(mesh);

    MaterialSolid ms = solids[i] = new MaterialSolid();
    
    Lab lab = new Lab();
    Gradient.eval(grd, fac, mixer, lab);
    Rgb.srLab2TosRgb(lab, ms.fill, new Rgb(), new Vec4());
   
  }
  
  me3.scaleTo(graphics.width * 0.5f);
}

void draw() {
  graphics.getLookTarget(lookWorld);
  me3.rotateX(0.01f);

  if (useOrtho) {
    graphics.ortho(orthoZoom);
  } else {
    graphics.perspective();
  }

  mouseControls();

  graphics.background(0xff101010);
  graphics.lights();

  if (showGrid) {
    graphics.grid(32, 2.5f, 0xfffff7d5, graphics.width * 2.0f);
  }
  graphics.shape(me3, solids);

  graphics.strokeWeight(5.0f);
  graphics.stroke(0xffffff3f);
  graphics.point(lookWorld);
}

void mouseControls() {
  if (mousePressed) {

    graphics.mouse1s(m1s);

    if (mouseButton == LEFT) {
      Vec3 v = Vec3.mul(m1s, orbitSpeed, new Vec3());
      graphics.moveByLocal(v);
      orbitSpeed = Utils.lerp(
        orbitSpeed, orbitMaxSpeed, 0.01f);
    }

    if (mouseButton == CENTER) {
      Vec3 v = Vec3.mul(m1s, strafeSpeed, new Vec3());
      graphics.strafe(v);
      strafeSpeed = Utils.lerp(
        strafeSpeed, strafeMaxSpeed, 0.01f);
    }
  } else {
    graphics.camera();

    orbitSpeed = Utils.lerp(
      orbitSpeed, orbitDefSpeed, 0.025f);
    strafeSpeed = Utils.lerp(
      strafeSpeed, strafeDefSpeed, 0.025f);
  }
}

void mouseWheel(MouseEvent event) {
  float count = event.getCount();
  graphics.dolly(dollySpeed * count);
  orthoZoom -= 0.05f * count;
}

void mouseReleased() {
  if (mouseButton == RIGHT) {
    showGrid = !showGrid;
  }
}

void keyPressed() {

  if (keyCode == 50 || keyCode == 130) {
    // Two or numpad two.
    graphics.moveByLocal(0.0f, -150.0f, 0.0f);
  } else if (keyCode == 52 || keyCode == 132) {
    // Four or numpad four.
    graphics.moveByLocal(-150.0f, 0.0f, 0.0f);
  } else if (keyCode == 54 || keyCode == 134) {
    // Six or numpad six.
    graphics.moveByLocal(150.0f, 0.0f, 0.0f);
  } else if (keyCode == 56 || keyCode == 136) {
    // Eight or numpad eight.
    graphics.moveByLocal(0.0f, 150.0f, 0.0f);
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

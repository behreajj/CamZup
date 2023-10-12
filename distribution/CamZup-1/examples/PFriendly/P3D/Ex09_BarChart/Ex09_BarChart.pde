import camzup.core.*;
import camzup.pfriendly.*;
import java.util.Map.Entry;

Zup3 graphics;
int count = 10;
HashMap<String, Float> data = new HashMap<String, Float>();

Gradient grd = Gradient.paletteViridis(new Gradient());
MeshEntity3[] bars = new MeshEntity3[count];
TextEntity3[] labels = new TextEntity3[count];
MaterialSolid[] materials = new MaterialSolid[count];

Mesh3 cube = Mesh3.cube(0.5f, new Mesh3())
  .translate(new Vec3(0.0f, 0.0f, 0.5f));
Vec3 barScale = new Vec3(50.0f, 50.0f, 50.0f);

float labelScale = 0.175f;
Rgb labelColor = new Rgb(0.2f, 0.2f, 0.2f, 1.0f);
Vec3 labelOffset = new Vec3(0.0f, -25.0f, 0.0f);
Vec3 mouse1 = new Vec3();

void settings() {
  size(720, 405, Zup3.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (Zup3)getGraphics();
  graphics.hint(ENABLE_DEPTH_SORT);
  graphics.hint(ENABLE_OPTIMIZED_STROKE);

  // Create fake data.
  Rng rng = new Rng();
  for (int i = 0; i < count; ++i) {
    data.put(
      hex(rng.nextInt()),
      rng.nextFloat());
  }

  PFont font = createFont("Calibri", 156);

  Vec3 lb = new Vec3(-width * 0.5f, 0.0f, 0f);
  Vec3 ub = new Vec3(width * 0.5f, 0.0f, 0.0f);
  Vec3 loc = new Vec3();

  // Loop through each entry in the data, converting
  // it into bars.
  int i = 0;
  for (Entry<String, Float> datum : data.entrySet()) {
    String str = datum.getKey();
    float amount = datum.getValue();
    float percent = i / (count - 1.0f);

    Vec3.mix(lb, ub, percent, loc);
    barScale.z = 10.0f + 140.0f * amount;

    // Create bars.
    MeshEntity3 bar = bars[i] = new MeshEntity3();
    bar.append(cube);
    bar.scaleBy(barScale);
    bar.moveTo(loc);

    // Create labels.
    TextEntity3 label = labels[i] = new TextEntity3(
      font, str, labelColor, 0, 2, RIGHT, CENTER, labelScale);
    label.rotateZ(Utils.HALF_PI);
    label.moveTo(loc);
    label.moveBy(labelOffset);

    // Create a color for each bar.
    MaterialSolid ms = materials[i] = new MaterialSolid();
    ms.setStroke(false);
    Gradient.eval(grd, percent, ms.fill);

    ++i;
  }
}

void draw() {
  Vec3 camLoc = new Vec3();
  graphics.getLocation(camLoc);

  if (mousePressed) {
    graphics.mouse1s(mouse1);
    if (mouseButton == LEFT) {
      Vec3.mul(mouse1, 50.0f, mouse1);
      graphics.moveByLocal(mouse1);
    } else if (mouseButton == CENTER) {
      Vec3.mul(mouse1, 15.0f, mouse1);
      graphics.strafe(mouse1);
    }
  }

  graphics.lights();
  graphics.ortho();
  graphics.camera();
  graphics.background(0xfffff7d5);
  graphics.grid(32);

  for (int i = 0; i < count; ++i) {
    graphics.shape(bars[i], materials[i]);
    graphics.text(labels[i]);
  }
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

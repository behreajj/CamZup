import camzup.core.*;
import camzup.pfriendly.*;

Yup2 rndr;

int count = 10;
Vec2[] arr = new Vec2[count];
float tightness = 0.0;
boolean closedLoop = true;
boolean showHandles = true;

Rng rng = new Rng();
Curve2 catmull = new Curve2();
CurveEntity2 ce2 = new CurveEntity2();

void settings() {
  size(720, 405, Yup2.PATH_STR);
}

void setup() {
  rndr = (Yup2)getGraphics();
  ce2.append(catmull);

  // Initialize vectors.
  for (int i = 0; i < count; ++i) {
    arr[i] = new Vec2();
  }

  // Assign random values).
  randomPoints(Utils.min(width, height) * 0.5);
}

void draw() {
  tightness = Utils.lerp(-5.0, 5.0, mouseX / (float)width);
  tightness = Utils.quantize(tightness, 4);
  Curve2.fromCatmull(closedLoop, arr, tightness, catmull);

  surface.setTitle(Utils.toFixed(tightness, 2));
  rndr.background();
  rndr.camera();
  rndr.noFill();
  rndr.stroke(#00ff7f);
  rndr.strokeWeight(3.0);
  rndr.shape(ce2);
  if (showHandles) {
    rndr.handles(ce2);
  }
}

void keyReleased() {
  if (key == 'l') { 
    closedLoop = !closedLoop;
  }
  if (key == 'h') {
    showHandles = !showHandles;
  }
}

void mouseReleased() {
  randomPoints(Utils.min(width, height) * 0.5);
}


void randomPoints(float edge) {
  for (int i = 0; i < count; ++i) {
    Vec2.randomCartesian(rng, -edge, edge, arr[i]);
  }
}

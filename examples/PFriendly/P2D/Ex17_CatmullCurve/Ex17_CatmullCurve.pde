import camzup.core.*;
import camzup.pfriendly.*;

YupJ2 graphics;

int count = 6;
Vec2[] arr = new Vec2[count];
float tightness = 0.0;
boolean closedLoop = true;
boolean showHandles = true;

Rng rng = new Rng();
Curve2 catmull = new Curve2();
CurveEntity2 ce2 = new CurveEntity2(catmull);
MaterialSolid mat = new MaterialSolid()
  .setFill(false)
  .setStroke(true)
  .setStroke(0xaf404040)
  .setStrokeWeight(3.0);

void settings() {
  size(720, 405, YupJ2.PATH_STR);
}

void setup() {
  frameRate(60.0);

  graphics = (YupJ2)getGraphics();
  graphics.noFill();

  // Initialize vectors.
  float r = Utils.min(width, height) * 0.25;
  float toTheta = IUtils.TAU / count;
  for (int i = 0; i < count; ++i) {
    float theta = i * toTheta;
    arr[i] = Vec2.fromPolar(theta, r, new Vec2());
  }
}

void draw() {
  tightness = Utils.lerp(-5.0, 5.0, mouseX / (float)width);
  tightness = Utils.quantize(tightness, 4);
  Curve2.fromCatmull(closedLoop, arr, tightness, catmull);

  surface.setTitle(Utils.toFixed(tightness, 2));

  graphics.background();
  graphics.camera();

  // Draw control.
  graphics.stroke(0x7f606060);
  graphics.strokeWeight(12.0);
  graphics.beginShape();
  graphics.curveTightness(tightness);
  for (int i = 0; i < arr.length; ++i) {
    graphics.curveVertex(arr[i]);
  }
  graphics.endShape();

  // Draw test.

  graphics.shape(ce2, mat);
  if (showHandles) {
    graphics.handles(ce2);
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

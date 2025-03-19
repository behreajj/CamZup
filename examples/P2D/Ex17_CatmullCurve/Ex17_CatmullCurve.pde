import com.behreajj.camzup.core.*;
import com.behreajj.camzup.friendly.*;

YupJ2 graphics;

int count = 6;
Vec2[] arr = new Vec2[count];
float tightness = 0.0f;
boolean closedLoop = true;
boolean showHandles = true;

Rng rng = new Rng();
Curve2 catmull = new Curve2();
CurveEntity2 ce2 = new CurveEntity2(catmull);
MaterialSolid mat = new MaterialSolid()
  .setFill(false)
  .setStroke(true)
  .setStroke(new Rgb(0.2, 0.2, 0.2))
  .setStrokeWeight(3.0f);

void settings() {
  size(720, 405, YupJ2.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (YupJ2)getGraphics();
  graphics.noFill();

  // Initialize vectors.
  float r = Utils.min(width, height) * 0.25f;
  float toTheta = Utils.TAU / count;
  for (int i = 0; i < count; ++i) {
    float theta = i * toTheta;
    arr[i] = Vec2.fromPolar(theta, r, new Vec2());
  }
}

void draw() {
  tightness = Utils.lerp(-5.0f, 5.0f, mouseX / (float)width);
  tightness = Utils.quantize(tightness, 4);
  Curve2.fromCatmull(closedLoop, arr, tightness, catmull);

  graphics.background();
  graphics.camera();

  // Draw control.
  graphics.stroke(0x7f606060);
  graphics.strokeWeight(12.0f);
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
  if (mouseButton == LEFT) {
    randomPoints(arr, Utils.min(width, height) * 0.5f);
  } else if (mouseButton == RIGHT) {
    String str = graphics.toSvgString(ce2, mat);
    saveStrings("data/catmull.svg", new String[] { str });
    println("Saved to svg.");
  }
}

void randomPoints(Vec2[] arr, float edge) {
  for (int i = 0; i < count; ++i) {
    Vec2.randomCartesian(rng, -edge, edge, arr[i]);
  }
}

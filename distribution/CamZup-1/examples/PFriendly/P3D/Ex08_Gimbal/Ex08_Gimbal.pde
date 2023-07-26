import camzup.core.*;
import camzup.pfriendly.*;

float camDist = 400;
float forwardSpeed = 10.0;
float camDamping = 0.05;
boolean[] buttons = new boolean[256];

Zup3 graphics;

MeshEntity3 airplane = new MeshEntity3(
  Mesh3.cube(new Mesh3())
  .scale(new Vec3(0.75, 0.25, 0.1)),

  Mesh3.cube(new Mesh3())
  .scale(new Vec3(0.2, 0.875, 0.2)),

  Mesh3.cube(new Mesh3())
  .scale( new Vec3(0.1, 0.1, 0.2) )
  .translate( new Vec3(0.0, -0.3, 0.1) ));

MaterialSolid[] materials = new MaterialSolid[] {
  new MaterialSolid()
  .setStroke(true)
  .setStrokeWeight(1.5)
  .setStroke(Color.red(new Color()))
  .setFill(false),
  new MaterialSolid()
  .setStroke(true)
  .setStrokeWeight(1.5)
  .setStroke(Color.green(new Color()))
  .setFill(false),
  new MaterialSolid()
  .setStroke(true)
  .setStrokeWeight(1.5)
  .setStroke(Color.blue(new Color()))
  .setFill(false),

  new MaterialSolid(Color.red(new Color())),
  new MaterialSolid(Color.green(new Color())),
  new MaterialSolid(Color.blue(new Color())),

  new MaterialSolid(Color.white(new Color()))
};

void settings() {
  size(640, 360, Zup3.PATH_STR);
}

void setup() {
  frameRate(60.0);
  graphics = (Zup3)getGraphics();

  float uniform = 0.5 * Utils.min(graphics.width, graphics.height);
  airplane.scaleTo(uniform);
}

void draw() {
  float xAxis = 0.0;
  float yAxis = 0.0;
  float zAxis = 0.0;

  // pitch
  if (buttons[87]) ++xAxis; // w
  if (buttons[83]) --xAxis; // s

  // roll
  if (buttons[69]) ++yAxis; // e
  if (buttons[81]) --yAxis; // q

  // yaw
  if (buttons[65]) ++zAxis; // a
  if (buttons[68]) --zAxis; // d

  // move
  if (buttons[38]) // arrow up
    airplane.transform.moveByLocal(new Vec3(0, forwardSpeed, 0));
  if (buttons[40]) // arrow down
    airplane.transform.moveByLocal(new Vec3(0, -forwardSpeed, 0));

  // reset
  if (buttons[88]) // x
    airplane.moveTo(new Vec3(), 0.05);
  if (buttons[90]) // z
    airplane.rotateTo(new Quaternion(), 0.05);

  float axisMagSq = xAxis * xAxis
    + yAxis * yAxis
    + zAxis * zAxis;
  if (axisMagSq > 0.0) {
    float axisMag = sqrt(axisMagSq);
    xAxis /= axisMag;
    yAxis /= axisMag;
    zAxis /= axisMag;
    airplane.rotateBy(0.05, new Vec3(xAxis, yAxis, zAxis));
  }

  Vec3 up = airplane.transform.getUp(new Vec3());
  Vec3 fore = airplane.transform.getForward(new Vec3());
  Vec3 airLoc = airplane.transform.getLocation(new Vec3());
  Vec3 camLoc = new Vec3();
  Vec3.mul(Vec3.negate(fore, new Vec3()), camDist, camLoc);
  Vec3.add(airLoc, camLoc, camLoc);
  graphics.moveTo(camLoc, camDamping);

  graphics.background();
  graphics.perspective();
  //graphics.camera(graphics.getLocation(new Vec3()), airLoc, fore);
  graphics.camera(graphics.getLocation(new Vec3()), airLoc, up);

  graphics.grid(32);

  graphics.lights();
  graphics.shape(airplane, materials[6]);
  graphics.gimbal(airplane.transform, 0.583f, 1.25f);
}

void keyPressed() {
  buttons[keyCode] = true;
}

void keyReleased() {
  buttons[keyCode] = false;
}

// Adapted from Daniel Shiffman,
// The Nature of Code
// http://natureofcode.com
// 6.4: Flow Field Following
// Via Reynolds: http://www.red3d.com/cwr/steer/FlowFollow.html

import camzup.core.*;
import camzup.pfriendly.*;

// Using this variable to decide whether to draw everything.
boolean debug = false;
int flowCount = 20;
int vehicleCount = 120;
color vehicleClr = 0x02fff7d5;

Yup2 rndr;
FlowField flowfield;
Random rng = new Random();

Vehicle[] vehicles = new Vehicle[vehicleCount];

void settings() {
  size(720, 405, "camzup.pfriendly.Yup2");
  smooth(8);
}

void setup() {
  frameRate(1000);
  rndr = (Yup2)getGraphics();
  rndr.background(#202020);

  flowfield = new FlowField(rndr.width, rndr.height, flowCount);
  Vec2 ub = new Vec2(rndr.width, rndr.height);
  Vec2 lb = Vec2.zero(new Vec2());
  Vec2 pos = new Vec2();

  for (int i = 0; i < vehicleCount; ++i) {
    Vec2.randomCartesian(rng, lb, ub, pos);
    float maxSpeed = rng.uniform(2.0, 5.0);
    float maxForce = rng.uniform(0.1, 0.5);
    Vehicle vehicle = new Vehicle(pos, maxSpeed, maxForce);
    vehicles[i] = vehicle;
  }
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));
  rndr.camera(width * 0.5, height * 0.5, 0.0, 1.0, 1.0);

  if (debug) {
    rndr.background(#202020);
    flowfield.display(rndr);
  }

  for (Vehicle v : vehicles) {
    v.follow(flowfield);
    v.run(rndr, 15.0, vehicleClr);
  }
}

void keyReleased() {
  if (key == ' ') {
    debug = !debug;
  }
}

void mouseReleased() {
    flowfield.init();
}

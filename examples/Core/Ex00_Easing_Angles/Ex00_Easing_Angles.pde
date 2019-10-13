import camzup.core.*;
import camzup.pfriendly.*;

YupJ2 graphics;

Utils.PeriodicEasing easeNear = new Utils.LerpNear();
Utils.PeriodicEasing easeCW = new Utils.LerpCW();
Utils.PeriodicEasing easeCCW = new Utils.LerpCCW();

Vec2 mouse = new Vec2();
Vec2 followNear = new Vec2();
Vec2 followCW = new Vec2();
Vec2 followCCW = new Vec2();

float targetAngle = 0.0;
float nearAngle = 0.0;
float cwAngle = 0.0;
float ccwAngle = 0.0;

void setup() {
  size(720, 405, "camzup.pfriendly.YupJ2");
  graphics = (YupJ2)getGraphics();
}

void draw() {
  graphics.camera();
  graphics.mouse(mouse);

  float speed = 0.02;

  targetAngle = Vec2.heading(mouse);
  nearAngle = easeNear.apply(nearAngle, targetAngle, speed);
  cwAngle = easeCW.apply(cwAngle, targetAngle, speed);
  ccwAngle = easeCCW.apply(ccwAngle, targetAngle, speed);

  Vec2.fromPolar(nearAngle, 150.0, followNear);
  Vec2.fromPolar(cwAngle, 150.0, followCW);
  Vec2.fromPolar(ccwAngle, 150.0, followCCW);

  graphics.background(#fff7d5);  
  graphics.stroke(#202020);
  graphics.strokeWeight(10.0);
  graphics.point(mouse.x, mouse.y);

  graphics.stroke(#ff2828);
  graphics.strokeWeight(2.0);
  graphics.line(0.0, 0.0, followNear.x, followNear.y);

  graphics.stroke(#28ff28);
  graphics.strokeWeight(2.0);
  graphics.line(0.0, 0.0, followCCW.x, followCCW.y);

  graphics.stroke(#2828ff);
  graphics.strokeWeight(2.0);
  graphics.line(0.0, 0.0, followCW.x, followCW.y);
}

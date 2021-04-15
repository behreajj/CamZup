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

float targetAngle = 0.0f;
float nearAngle = 0.0f;
float cwAngle = 0.0f;
float ccwAngle = 0.0f;

void settings() {
  size(720, 405, YupJ2.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (YupJ2)getGraphics();
}

void draw() {
  graphics.mouse(mouse);

  float speed = 0.02f;

  targetAngle = Vec2.heading(mouse);
  nearAngle = easeNear.apply(nearAngle, targetAngle, speed);
  cwAngle = easeCW.apply(cwAngle, targetAngle, speed);
  ccwAngle = easeCCW.apply(ccwAngle, targetAngle, speed);

  Vec2.fromPolar(nearAngle, 110.0f, followNear);
  Vec2.fromPolar(cwAngle, 130.0f, followCW);
  Vec2.fromPolar(ccwAngle, 140.0f, followCCW);

  graphics.background(0xfffff7d5);
  graphics.stroke(0xff202020);
  graphics.strokeWeight(10.0f);
  graphics.point(mouse.x, mouse.y);

  graphics.stroke(0xffff2828);
  graphics.strokeWeight(2.0f);
  graphics.line(0.0f, 0.0f, followNear.x, followNear.y);

  graphics.stroke(0xff28ff28);
  graphics.strokeWeight(2.0f);
  graphics.line(0.0f, 0.0f, followCCW.x, followCCW.y);

  graphics.stroke(0xff2828ff);
  graphics.strokeWeight(2.0f);
  graphics.line(0.0f, 0.0f, followCW.x, followCW.y);
}

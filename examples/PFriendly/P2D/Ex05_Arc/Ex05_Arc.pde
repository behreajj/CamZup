import camzup.pfriendly.*;
import camzup.core.*;

Yup2 graphics;
Vec2 mouse = new Vec2();

void settings() {
  size(720, 405, Yup2.PATH_STR);
}

void setup() {
  frameRate(60.0);
  graphics = (Yup2)getGraphics();
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));

  graphics.mouse(mouse);
  float start = Vec2.heading(mouse);
  float stop = frameCount * 0.01;
  ellipseMode(RADIUS);
  background(#fff7d5);
  graphics.origin();
  strokeWeight(5.0);
  stroke(#373737);
  fill(0xff3fafef);
  arc(0.0, 0.0,
    150.0, 150.0,
    start, stop,
    PIE);

  stroke(#ff2020);
  strokeWeight(12.0);
  graphics.point(mouse);
}

import camzup.pfriendly.*;
import camzup.core.*;

Yup2 graphics;
Vec2 mouse = new Vec2();

void settings() {
  size(720, 405, "camzup.pfriendly.Yup2");
  smooth(8);
}

void setup() {
  graphics = (Yup2)getGraphics();
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));

  graphics.mouse(mouse);
  float start = Vec2.heading(mouse);
  float stop = frameCount * 0.025;

  background(#fff7d5);
  graphics.origin();
  strokeWeight(5.0);
  stroke(#373737);
  arc(0.0, 0.0,
    175.0, 175.0,
    start, stop,
    PIE);

  stroke(#ff2020);
  strokeWeight(12.0);
  point(mouse.x, mouse.y);
}

import com.behreajj.camzup.core.*;
import com.behreajj.camzup.friendly.*;

Yup2 graphics;
Vec2 mouse = new Vec2();

void settings() {
  size(720, 405, Yup2.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (Yup2)getGraphics();
}

void draw() {
  graphics.mouse(mouse);
  float start = Vec2.heading(mouse);
  float stop = frameCount * 0.01f;
  ellipseMode(RADIUS);
  background(0xfffff7d5);
  graphics.origin();
  strokeWeight(5.0f);
  stroke(0xff373737);
  fill(0xff3fafef);
  arc(0.0f, 0.0f,
    150.0f, 150.0f,
    start, stop,
    PIE);

  stroke(0xffff2020);
  strokeWeight(12.0f);
  graphics.point(mouse);
}

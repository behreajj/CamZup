import com.behreajj.camzup.core.*;
import com.behreajj.camzup.friendly.*;

YupJ2 graphics;

float x = 0.0f;
float y = 0.0f;
Vec2 mouse = new Vec2();

void settings() {
  size(720, 405, YupJ2.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (YupJ2)getGraphics();
}

void draw() {
  // Jitter the camera.
  float angle = frameCount * 0.0025f;
  x += random(-1.0f, 1.0f);
  y += random(-1.0f, 1.0f);
  float s = Utils.pingPong(0.5f, 4.0f, angle);

  graphics.camera(x, y, angle, s, s);

  // Acquire the mouse after the camera has
  // transformed the scene; otherwise, it will
  // lag behind.
  graphics.mouse(mouse);

  background(0xff202020);
  graphics.grid(16, 4.0f);
  stroke(0xfffff7d5);
  strokeWeight(10.0f);
  graphics.point(mouse);
}

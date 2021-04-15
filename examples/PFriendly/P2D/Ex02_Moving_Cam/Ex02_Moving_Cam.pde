import camzup.pfriendly.*;
import camzup.core.*;

YupJ2 graphics;

float x = 0.0f;
float y = 0.0f;
float angle = 0.0f;
float scale = 1.0f;

void settings() {
  size(720, 405, YupJ2.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (YupJ2)getGraphics();
}

void draw() {
  angle = TAU * mouseX / (float)width;
  float oscillation = Utils.sin(angle) * 0.5f + 0.5f;
  scale = Utils.lerp(0.33333f, 3.0f, oscillation);

  float extents = min(width, height) * 0.25f;
  x = Utils.lerp(-extents, extents, oscillation);
  y = Utils.lerp(-extents, extents, oscillation);

  background(0xff202020);
  graphics.camera(x, y, angle, scale, scale);
  graphics.grid(16, 4.0f);
  graphics.origin(32);

  stroke(0xfffff7d5);
  strokeWeight(10.0f);
  point(graphics.cameraX, graphics.cameraY);
}

void mouseReleased() {
  printMatrix();
}

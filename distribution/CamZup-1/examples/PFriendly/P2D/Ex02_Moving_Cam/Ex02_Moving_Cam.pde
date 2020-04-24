import camzup.pfriendly.*;
import camzup.core.*;

Yup2 graphics;

float x = 0.0;
float y = 0.0;
float angle = 0.0;
float scale = 1.0;

void settings() {
  size(720, 405, Yup2.PATH_STR);
}

void setup() {
  graphics = (Yup2)getGraphics();
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));

  angle = TAU * mouseX / (float)width;
  float oscillation = sin(angle) * 0.5 + 0.5;
  scale = lerp(0.33333, 3.0, oscillation);

  float extents = min(width, height) * 0.25;
  x = Utils.lerp(-extents, extents, oscillation);
  y = Utils.lerp(-extents, extents, oscillation);

  background(#202020);
  graphics.camera(x, y, angle, scale, scale);
  graphics.grid(16, 4.0);
  graphics.origin(32);

  stroke(#fff7d5);
  strokeWeight(10.0);
  point(graphics.cameraX, graphics.cameraY);
}

void mouseReleased() {
  printMatrix();
}

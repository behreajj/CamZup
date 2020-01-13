import camzup.pfriendly.*;
import camzup.core.*;

YupJ2 graphics;

int rows = 50;
int cols = 50;
float toY = 1.0 / (rows - 1.0);
float toX = 1.0 / (cols - 1.0);
float w;
float h;

void setup() {
  size(720, 405, "camzup.pfriendly.YupJ2");
  smooth(8);
  frameRate(1000);
  noStroke();
  colorMode(RGB, 1.0);
  graphics = (YupJ2)getGraphics();
  w = (float)width / (cols - 1.0);
  h = (float)height / (rows - 1.0);
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));

  float right = width * 0.5;
  float left = -right;
  float top = height * 0.5;
  float bottom = -top;

  graphics.background();

  graphics.pushMatrix();
  graphics.rotateZ(frameCount * 0.01);
  for (int i = 0; i < rows; ++i) {
    float yPrc = i * toY;
    float y = Utils.lerpUnclamped(bottom, top, yPrc);
    for (int j = 0; j < cols; ++j) {
      float xPrc = j * toX;
      float x = Utils.lerpUnclamped(left, right, xPrc);
      graphics.fill(xPrc, yPrc, 0.5, 0.75);
      graphics.ellipse(x, y, w, h);
    }
  }
  graphics.popMatrix();

  graphics.textSize(52);
  graphics.fill(0xaf202020);
  graphics.text("Lorem ipsum", 0.0, 0.0);
}

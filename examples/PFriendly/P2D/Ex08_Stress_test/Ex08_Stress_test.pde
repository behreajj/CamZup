import camzup.pfriendly.*;
import camzup.core.*;

Yup2 graphics;

int rows = 50;
int cols = 50;
float toY = 1.0 / (rows - 1.0);
float toX = 1.0 / (cols - 1.0);
float w;
float h;

void setup() {
  size(512, 512, "camzup.pfriendly.Yup2");
  smooth(8);
  frameRate(1000);
  colorMode(RGB, 1.0);
  graphics = (Yup2)getGraphics();
  w = (float)width / (cols - 1.0);
  h = (float)height / (rows - 1.0);
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));
  
  float right = width * 0.5;
  float left = -right;
  float top = height * 0.5;
  float bottom = -top;

  background(#fff7d5);

  pushMatrix();
  rotateZ(frameCount * .01);
  for (int i = 0; i < rows; ++i) {
    float yPrc = i * toY;
    float y = lerp(bottom, top, yPrc);
    for (int j = 0; j < cols; ++j) {
      float xPrc = j * toY;
      float x = lerp(left, right, xPrc);
      fill(xPrc, yPrc, 0.5, 0.75);
      ellipse(x, y, w, h);
    }
  }
  popMatrix();
  
  textSize(52);
  fill(0xaf202020);
  text("Lorem ipsum", 0.0, 0.0);
}

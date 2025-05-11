import com.behreajj.camzup.core.*;
import com.behreajj.camzup.friendly.*;

Yup2 graphics;

void settings() {
  size(720, 405, Yup2.PATH_STR);
  smooth(8);
  pixelDensity(displayDensity());
}

void setup() {
  frameRate(60.0f);
  graphics = (Yup2)getGraphics();
}

void draw() {
  graphics.background();
  graphics.origin();

  pushMatrix();
  translate(125.0f, 100.0f);
  rotateZ(0.01f * frameCount);
  noStroke();
  fill(0x7f5f00af);
  rect(0.0f, 0.0f, 100.0f, 75.0f, 10.0f);
  popMatrix();
}

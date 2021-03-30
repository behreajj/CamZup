import camzup.pfriendly.*;
import camzup.core.*;

Yup2 graphics;

void settings() {
  size(720, 405, Yup2.PATH_STR);
  smooth(8);
  pixelDensity(displayDensity());
}

void setup() {
  frameRate(60.0);
  graphics = (Yup2)getGraphics();
}

void draw() {    
  surface.setTitle(Utils.toFixed(frameRate, 1));

  graphics.background();
  graphics.origin();

  pushMatrix();
  translate(125.0, 100.0);
  rotateZ(0.01 * frameCount);
  noStroke();
  fill(0x7f5f00af);
  rect(0.0, 0.0, 100.0, 75.0, 10.0);
  popMatrix();
}

import camzup.pfriendly.*;
import camzup.core.*;

Yup2 graphics2;

void settings() {
  size(720, 405, Yup2.PATH_STR);

  // Recommended to assist with jittering in AWT:
  smooth(8);
  pixelDensity(displayDensity());
}

void setup() {
  graphics2 = (Yup2)getGraphics();
  frameRate(60.0);
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));

  graphics2.background();
  graphics2.origin();

  pushMatrix();
  translate(125.0, 100.0);
  rotateZ(0.01 * frameCount);
  noStroke();
  fill(#7f00ff);
  rect(0.0, 0.0, 100.0, 100.0, 10.0);
  popMatrix();
}

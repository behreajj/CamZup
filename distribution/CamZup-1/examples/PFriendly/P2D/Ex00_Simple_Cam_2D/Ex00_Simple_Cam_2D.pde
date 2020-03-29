import camzup.pfriendly.*;
import camzup.core.*;

Yup2 graphics2;

void settings() {
  size(720, 405, Yup2.PATH_STR);
}

void setup() {
  graphics2 = (Yup2)getGraphics();
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));
  
  background(#fff7d5);
  graphics2.origin();

  pushMatrix();
  translate(125.0, 100.0);
  rotateZ(0.01 * frameCount);
  rect(0.0, 0.0, 100.0, 100.0, 10.0);
  popMatrix();
}

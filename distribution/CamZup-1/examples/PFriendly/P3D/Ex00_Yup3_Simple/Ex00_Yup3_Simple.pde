import camzup.pfriendly.*;
import camzup.core.*;

Yup3 graphics; /* OPENGL - P3D */

void settings() {
  size(720, 405, "camzup.pfriendly.Yup3");
}

void setup() {
  graphics = (Yup3)getGraphics();
}

void draw() {
  surface.setTitle(nfs(frameRate, 2, 1));
  background(#fff7d5);
  lights();
  graphics.origin(75.0, 1.0);
  translate(150.0, 100.0, 25.0);
  rotateZ(frameCount * 0.03);
  strokeWeight(1.25);
  stroke(#202020);
  sphere(125.0);
}

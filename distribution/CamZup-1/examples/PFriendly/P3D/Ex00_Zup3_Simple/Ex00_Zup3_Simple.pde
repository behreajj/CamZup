import camzup.pfriendly.*;
import camzup.core.*;

Yup3 graphics; /* OPENGL - P3D */

void setup() {
  size(720, 405, "camzup.pfriendly.Yup3");
  graphics = (Yup3)getGraphics();
}


void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));
  background(#fff7d5);    
  lights();
  graphics.origin(75.0, 1.0);
  translate(150.0, 100.0, 25.0);
  rotate(frameCount * 0.01, 1.0, -1.0, 1.0);
  fill(0.0, 127.0, 255.0, 255.0);
  box(175.0, 100.0, 50.0);
}

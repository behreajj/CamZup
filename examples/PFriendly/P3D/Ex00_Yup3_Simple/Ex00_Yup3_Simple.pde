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
  rotate(frameCount * 0.02, 1.0, 0.0, 0.0);
  fill(0.0, 127.0, 255.0, 255.0);
  strokeWeight(1.25);
  stroke(#202020);
  sphere(75.0);
  //box(50.0, 50.0, 150.0);
}

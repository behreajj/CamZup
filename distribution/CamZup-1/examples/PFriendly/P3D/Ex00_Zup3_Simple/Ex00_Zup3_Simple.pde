import camzup.pfriendly.*;
import camzup.core.*;

Zup3 graphics; /* OPENGL - P3D */

void setup() {
  size(720, 405, "camzup.pfriendly.Zup3");
  graphics = (Zup3)getGraphics();
}


void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));
  background(#fff7d5);


  float camDist = height;  
  float az = -PI + TAU * mouseX / (width - 1.0);
  float incl = -HALF_PI + PI * mouseY / (height - 1.0);
  float rhoCosPhi = camDist * cos(incl);
  camera(
    rhoCosPhi * cos(az), 
    rhoCosPhi * sin(az), 
    camDist * -sin(incl), 

    0.0, 0.0, 0.0, 
    0.0, 0.0, 1.0);
    
  lights();
  graphics.origin(75.0, 1.0);
  translate(150.0, 100.0, 25.0);
  rotate(frameCount * 0.01, 1.0, -1.0, 1.0);
  fill(0.0, 127.0, 255.0, 255.0);
  box(175.0, 100.0, 50.0);
}

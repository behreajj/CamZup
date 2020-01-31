import camzup.pfriendly.*;
import camzup.core.*;

Yup3 graphics; /* OPENGL - P3D */

void setup() {
  size(720, 405, "camzup.pfriendly.Yup3");
  graphics = (Yup3)getGraphics();
  //frameRate(1000);
}

void draw() {
  surface.setTitle(nfs(frameRate, 2, 1));
  background(#fff7d5);    
  graphics.directionalLight(#ffffff, new Vec3(.125, -.8, .6));
  graphics.origin(75.0, 1.0);
  graphics.translate(150.0, 100.0, 25.0);
  graphics.rotateZ(frameCount * 0.03);
  graphics.strokeWeight(1.25);
  graphics.stroke(#202020);
  graphics.sphere(125.0);
  //graphics.box(100);
  graphics.rect(0, 0, 100, 50);
}

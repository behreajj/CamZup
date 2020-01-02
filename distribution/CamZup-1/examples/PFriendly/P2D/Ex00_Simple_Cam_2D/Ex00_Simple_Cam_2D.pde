import camzup.pfriendly.*;
import camzup.core.*;

YupJ2 graphics2;

void setup() {
  size(720, 405, "camzup.pfriendly.YupJ2");
  graphics2 = (YupJ2)getGraphics();
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));
  background(#fff7d5);
  camera();
  graphics2.origin();
  
  pushMatrix();
  translate(125, 100);
  rotateZ(0.01 * frameCount);
  rect(0.0, 0.0, 100.0, 100.0, 10.0);
  popMatrix();
}

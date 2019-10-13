import camzup.pfriendly.*;

Yup2 graphics2;

void setup() {
  size(720, 405, "camzup.pfriendly.Yup2");
  graphics2 = (Yup2)getGraphics();
}


void draw() {
  surface.setTitle(String.format("%.1f", frameRate));
  background(#fff7d5);

  camera();
  graphics2.origin();
  
  pushMatrix();
  translate(125, 100);
  rotateZ(0.01 * frameCount);
  rect(0.0, 0.0, 100.0, 100.0, 10.0);
  popMatrix();
}

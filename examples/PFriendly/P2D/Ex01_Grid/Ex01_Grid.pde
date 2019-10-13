import camzup.pfriendly.*;

Yup2 graphics;

void setup() {
  size(720, 405, "camzup.pfriendly.Yup2");
  colorMode(RGB, 1.0);
  graphics = (Yup2)getGraphics();
}


void draw() {
  surface.setTitle(String.format("%.1f", frameRate));
  background(#202020);
  camera();
  graphics.grid(16, 3.0);
  graphics.origin(32);
}

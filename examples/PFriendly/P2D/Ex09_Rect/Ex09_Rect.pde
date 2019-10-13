import camzup.pfriendly.*;

Yup2 graphics;

void setup() {
  size(720, 405, "camzup.pfriendly.Yup2");
  graphics = (Yup2)getGraphics();
}


void draw() {
  surface.setTitle(String.format("%.1f", frameRate));
  background(#202020);
  camera();
  graphics.grid(16, 4.0);

  rectMode(RADIUS);
  fill(#21424f);
  rect(32.0, 32.0, 128.0, 128.0, 16);

  rectMode(CENTER);
  fill(#2c818e);
  rect(32.0, 32.0, 128.0, 128.0, 24);

  rectMode(CORNERS);
  fill(#eb7f6c);
  rect(32.0, 32.0, 128.0, 128.0, 32);

  rectMode(CORNER);
  fill(#686967);
  rect(32.0, 32.0, 128.0, 128.0, 16);
}

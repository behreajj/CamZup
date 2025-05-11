import com.behreajj.camzup.core.*;
import com.behreajj.camzup.friendly.*;

Yup2 graphics;

void settings() {
  size(720, 405, Yup2.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (Yup2)getGraphics();
  noStroke();
}

void draw() {
  background(0xff202020);
  graphics.grid(16, 4.0f);
  
  rectMode(RADIUS);
  fill(0xffe01c34);
  rect(32.0f, 32.0f, 128.0f, 128.0f, 16.0f);

  rectMode(CENTER);
  fill(0xff44b09e);
  rect(32.0f, 32.0f, 128.0f, 128.0f, 24.0f);

  rectMode(CORNERS);
  fill(0xffacabb0);
  rect(32.0f, 32.0f, 128.0f, 128.0f, 32.0f);

  rectMode(CORNER);
  fill(0xffe0d2c7);
  rect(32.0f, 32.0f, 128.0f, 128.0f, 16.0f);
}

import camzup.pfriendly.*;
import camzup.core.*;

YupJ2 graphics;

void settings() {
  size(720, 405, YupJ2.PATH_STR);
}

void setup() {
  graphics = (YupJ2)getGraphics();
  frameRate(60.0);
  noStroke();
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));
  background(#202020);
  graphics.grid(16, 4.0);
  
  rectMode(RADIUS);
  fill(#e01c34);
  rect(32.0, 32.0, 128.0, 128.0, 16.0);

  rectMode(CENTER);
  fill(#44b09e);
  rect(32.0, 32.0, 128.0, 128.0, 24.0);

  rectMode(CORNERS);
  fill(#acabb0);
  rect(32.0, 32.0, 128.0, 128.0, 32.0);

  rectMode(CORNER);
  fill(#e0d2c7);
  rect(32.0, 32.0, 128.0, 128.0, 16.0);
}

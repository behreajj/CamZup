import camzup.pfriendly.*;
import camzup.core.*;

YupJ2 graphics;

void settings() {
  size(720, 405, "camzup.pfriendly.YupJ2");
}

void setup() {
  graphics = (YupJ2)getGraphics();
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));
  background(#202020);
  graphics.grid(16, 3.0);
  graphics.origin(32);
}

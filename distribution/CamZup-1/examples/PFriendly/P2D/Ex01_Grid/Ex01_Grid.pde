import camzup.pfriendly.*;
import camzup.core.*;

YupJ2 graphics;

void setup() {
  size(720, 405, "camzup.pfriendly.YupJ2");
  strokeCap(ROUND);
  graphics = (YupJ2)getGraphics();
}


void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));
  background(#202020);
  graphics.grid(16, 3.0);
  graphics.origin(32);
}

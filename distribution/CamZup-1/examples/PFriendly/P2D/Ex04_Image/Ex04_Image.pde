import camzup.pfriendly.*;
import camzup.core.*;

YupJ2 graphics;
PImage img;
int currentMode;
int index;
int[] modes = { CENTER, CORNERS, CORNER };

void settings() {
  size(720, 405, "camzup.pfriendly.YupJ2");
}

void setup() {
  graphics = (YupJ2)getGraphics();
  img = loadImage("diagnostic.png");

  // For Yup2.
  // graphics.setTextureSampling(IUpOgl.Sampling.TRILINEAR);

}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));
  background(#202020);
  graphics.grid(16, 4.0);
  graphics.origin(32);
  graphics.image(img, 64, 32, 196, 196);
}

void mouseReleased() {
  index = (index + 1) % 3;
  currentMode = modes[index];
  imageMode(currentMode);

  switch(currentMode) {
  case CORNER:
    println(CORNER, "CORNER");
    break;
  case CORNERS:
    println(CORNERS, "CORNERS");
    break;
  case CENTER:
    println(CENTER, "CENTER");
    break;
  }
}

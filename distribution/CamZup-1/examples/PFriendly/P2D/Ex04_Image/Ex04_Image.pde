import camzup.pfriendly.*;
import camzup.core.*;

Yup2 graphics;
PImage img;
int currentMode;
int index;
int[] modes = { CENTER, CORNERS, CORNER };

void settings() {
  size(720, 405, Yup2.PATH_STR);
}

void setup() {
  graphics = (Yup2)getGraphics();
  img = loadImage("diagnostic.png");

  // For Yup2.
  //graphics.textureSampling(TextureSampling.TRILINEAR);
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));
  background(#202020);
  graphics.grid(16, 4.0);
  graphics.origin(32.0);
  graphics.image(img, 64.0, 32.0, 196.0, 196.0);
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

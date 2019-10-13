import camzup.pfriendly.*;

YupJ2 graphics;
PImage img;
int currentMode;
int index;
int[] modes = { CENTER, CORNERS, CORNER };

void setup() {
  size(720, 405, "camzup.pfriendly.YupJ2");
  graphics = (YupJ2)getGraphics();

  // For Yup2.
  //graphics.setTextureSampling(IUpOgl.Sampling.TRILINEAR);

  img = loadImage("diagnostic.png");
}

void draw() {
  surface.setTitle(String.format("%.1f", frameRate));
  background(#202020);
  graphics.grid(16, 4.0);
  graphics.origin(32);
  //image(img, 64, 32, 196, 196);
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

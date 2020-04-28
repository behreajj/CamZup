import camzup.pfriendly.*;
import camzup.core.*;

YupJ2 graphics;
PImage img;
int currentMode = 0;
int index = 0;
int[] modes = { CORNER, CORNERS, CENTER, RADIUS };
String[] msStr = { "CORNER", "CORNERS", "CENTER", "RADIUS"};

void settings() {
  size(720, 405, YupJ2.PATH_STR);
}

void setup() {
  graphics = (YupJ2)getGraphics();
  noStroke();
  img = loadImage("diagnosticHalf.png");
  ZImage.tint(img, 0xffff7f00);

  // For YupJ2.
  //graphics.textureSampling(TextureSampling.TRILINEAR);
}

void draw() {
  surface.setTitle(
    Utils.toFixed(frameRate, 1) +
    " | " + msStr[currentMode] +
    " | " + modes[currentMode]);
  background(#202020);
  imageMode(currentMode);
  
  graphics.grid(16, 4.0);
  graphics.origin(32.0);
  graphics.image(img,
    64, 32, img.width/2, img.height/2,
    0.0, 0.0, 1.0, 1.0);
}

void mouseReleased() {
  index = (index + 1) % modes.length;
  currentMode = modes[index];
}

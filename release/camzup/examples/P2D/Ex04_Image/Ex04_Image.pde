import com.behreajj.camzup.core.*;
import com.behreajj.camzup.friendly.*;

Yup2 graphics;
PImage img;
int currentMode = 0;
int index = 0;
int[] modes = { CORNER, CORNERS, CENTER, RADIUS };
String[] msStr = { "CORNER", "CORNERS", "CENTER", "RADIUS"};

void settings() {
  size(720, 405, Yup2.PATH_STR);
}

void setup() {
  graphics = (Yup2)getGraphics();
  frameRate(60.0f);
  noStroke();
  img = loadImage("diagnostic.png");
}

void draw() {
  background(0xff202020);
  imageMode(currentMode);

  graphics.grid(16, 4.0f);
  graphics.origin(32.0f);
  graphics.image(img,
    64.0f, 32.0f,
    img.width * 0.25f, img.height * 0.25f,
    0, 0, img.width, img.height);
}

void mouseReleased() {
  index = (index + 1) % modes.length;
  currentMode = modes[index];
}

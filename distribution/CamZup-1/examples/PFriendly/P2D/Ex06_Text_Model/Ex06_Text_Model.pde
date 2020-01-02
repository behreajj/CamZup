import camzup.pfriendly.*;
import camzup.core.*;

YupJ2 graphics;
PFont font;
PImage glim;

void setup() {
  size(720, 405, "camzup.pfriendly.YupJ2");
  smooth(8);

  textSize(72);
  textAlign(CENTER, CENTER);

  graphics = (YupJ2)getGraphics();
  font = loadFont("ProcessingSansPro.vlw");
  textFont(font);
}


void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));
  background(#fff7d5);

  textSize(52);
  noStroke();
  fill(0xff202020);
  text("The quick brown fox\njumps over\nthe lazy dog", 0.0, 0.0);
}

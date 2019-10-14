import camzup.pfriendly.*;

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
  surface.setTitle(String.format("%.1f", frameRate));
  background(#fff7d5);

  textSize(52);
  noStroke();
  fill(0x7f202020);
  text("The quick brown fox\njumps over\nthe lazy dog", 0.0, 0.0);
}

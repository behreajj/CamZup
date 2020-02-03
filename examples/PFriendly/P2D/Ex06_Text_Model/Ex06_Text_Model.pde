import camzup.pfriendly.*;
import camzup.core.*;

YupJ2 graphics;
PFont font;

void settings() {
  size(720, 405, "camzup.pfriendly.YupJ2");
}

void setup() {
  graphics = (YupJ2)getGraphics();
  noStroke();

  font = loadFont("ProcessingSansPro.vlw");
  textFont(font);
  textSize(52);
  textAlign(CENTER, CENTER);
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));
  background(#fff7d5);

  fill(#202020);
  text("The quick brown fox\njumps over the lazy dog.", 0.0, 0.0);
  text(0.12345, 100.0, 72.0);
  text(0.56789, -100.0, 72.0);

  fill(#ff2828);
  graphics.text(false, 0.0, -82.0);
  graphics.text(new Complex(0.0, 1.0), 0.0, -144.0);
}

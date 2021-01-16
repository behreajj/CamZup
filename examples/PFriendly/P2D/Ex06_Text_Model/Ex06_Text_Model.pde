import camzup.pfriendly.*;
import camzup.core.*;

YupJ2 graphics;
PFont font;

void settings() {
  size(720, 405, YupJ2.PATH_STR);
}

void setup() {
  graphics = (YupJ2)getGraphics();
  frameRate(60.0);
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
  text(PI, 0.0, 72.0);

  fill(#1f7fff);
  graphics.text(false, 0.0, -82.0);
  graphics.text(new Complex(0.5, 0.25), 0.0, -144.0);
}

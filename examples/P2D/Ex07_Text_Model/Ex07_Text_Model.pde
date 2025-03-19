import com.behreajj.camzup.core.*;
import com.behreajj.camzup.friendly.*;

Yup2 graphics;
PFont font;

void settings() {
  size(720, 405, Yup2.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (Yup2)getGraphics();
  noStroke();

  font = loadFont("ProcessingSansPro.vlw");
  textFont(font);
  textSize(52.0f);
  textAlign(CENTER, CENTER);
}

void draw() {
  background(0xfffff7d5);

  fill(0xff202020);
  text("The quick brown fox\njumps over the lazy dog.", 0.0f, 0.0f);
  text(Utils.PI, 0.0f, 72.0f);

  fill(0xff1f7fff);
  graphics.text(false, 0.0f, -82.0f);
  graphics.text(new Complex(0.5f, 0.25f), 0.0f, -144.0f);
}

import com.behreajj.camzup.core.*;
import com.behreajj.camzup.friendly.*;

Zup3 graphics;
Rgb clr = new Rgb(1.0f, 0.85f, 0.0f, 1.0f);
String msg = "The Quick\nBrown Fox\nJumps Over\nThe Lazy Dog!";
TextEntity3 te3;

void settings() {
  size(720, 405, Zup3.PATH_STR);
}

void setup() {
  frameRate(60.0f);

  graphics = (Zup3)getGraphics();
  graphics.ortho();
  graphics.camera();

  //PFont font = loadFont("ProcessingSansPro.vlw");
  PFont font = loadFont("NotoSerif-Bold-48.vlw");
  long start = System.currentTimeMillis();
  te3 = new TextEntity3(font, msg, clr, 8, 0, LEFT, CENTER, 0.975f);
  long end = System.currentTimeMillis();
  println("Elapsed Time: " + (end - start));
  te3.rotateX(Utils.HALF_PI);
}

void draw() {
  te3.rotateX(0.01f);
  te3.material.moveBy(new Vec2(0.0f, -0.005f));

  graphics.background(0xff202020);
  graphics.grid(16, 2.5f, 0xfffff7d5, graphics.height);
  graphics.text(te3);
}

import camzup.core.*;
import camzup.pfriendly.*;

Zup3 graphics;
Color clr = new Color(1.0, 0.85, 0.0, 1.0);
String msg = "The Quick\nBrown Fox\nJumps Over\nThe Lazy Dog!";
TextEntity3 te3;

void settings() {
  size(720, 405, Zup3.PATH_STR);
}

void setup() {
  frameRate(60.0);

  graphics = (Zup3)getGraphics();
  graphics.ortho();
  graphics.camDimetric();

  PFont font = loadFont("ProcessingSansPro.vlw");
  long start = System.currentTimeMillis();
  te3 = new TextEntity3(font, msg, clr, 16, 0, CENTER, CENTER, 0.975);
  long end = System.currentTimeMillis();
  println("Elapsed Time: " + (end - start));
  te3.rotateX(IUtils.HALF_PI);
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));

  te3.rotateX(0.01);
  te3.material.moveBy(new Vec2(0.0, -0.005));

  graphics.background(#202020);
  graphics.grid(16, 2.5, #fff7d5, graphics.height);
  graphics.text(te3);
}

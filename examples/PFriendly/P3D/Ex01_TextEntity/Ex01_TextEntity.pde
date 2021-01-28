import camzup.core.*;
import camzup.pfriendly.*;

Zup3 rndr;
Color clr = new Color(0.125, 0.75, 1.0);
String msg = "The Quick\nBrown Fox\nJumps Over\nThe Lazy Dog!";
TextEntity3 te3;

void settings() {
  size(720, 405, Zup3.PATH_STR);
}

void setup() {
  frameRate(60.0);
  rndr = (Zup3)getGraphics();
  rndr.perspective(Utils.PI * 0.125);
  PFont font = loadFont("ProcessingSansPro.vlw");
  te3 = new TextEntity3(font, msg, clr, 0, 0, CENTER, CENTER, 0.975);
  te3.rotateX(IUtils.HALF_PI);
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));

  te3.rotateX(0.01);
  te3.material.moveBy(new Vec2(0.0, -0.005));

  rndr.background(#202020);
  rndr.grid(16, 2.5, #fff7d5, rndr.height);
  rndr.text(te3);
}

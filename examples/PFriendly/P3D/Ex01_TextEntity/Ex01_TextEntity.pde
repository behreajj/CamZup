import camzup.core.*;
import camzup.pfriendly.*;

Zup3 rndr;
Color clr = Color.fromHex(#fff7d5, new Color());
String msg = "The Quick\nBrown Fox\nJumps Over\nThe Lazy Dog!";
TextEntity3 te3;

void settings() {
  size(720, 405, Zup3.PATH_STR);
}

void setup() {
  rndr = (Zup3)getGraphics();
  frameRate(60.0);
  PFont font = loadFont("AgencyFB-Bold-72.vlw");
  te3 = new TextEntity3(font, msg, clr, 10, 5, CENTER, CENTER, 0.975);
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));

  te3.rotateX(0.01);
  te3.material.moveBy(new Vec2(0.0, -0.005));

  rndr.background(#202020);
  rndr.perspective(Utils.PI * 0.125);
  rndr.grid(16, 2.5, #fff7d5, rndr.height);
  rndr.text(te3);
}

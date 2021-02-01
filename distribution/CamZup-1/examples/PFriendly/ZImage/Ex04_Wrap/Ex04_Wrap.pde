import camzup.core.*;
import camzup.pfriendly.*;

PImage source;
PImage target;

Yup2 graphics;
Vec2 mouse = new Vec2();
Vec2 szvec = new Vec2();
Vec2 msscl = new Vec2();

void settings() {
  size(720, 405, Yup2.PATH_STR);
}

void setup() {
  frameRate(60.0);
  graphics = (Yup2)getGraphics();

  source = createImage(512, 512, ARGB);
  target = createImage(width, height, ARGB);

  ZImage.rgb(source);
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));

  graphics.mouse1s(mouse);
  graphics.getSize(szvec);
  Vec2.mul(szvec, mouse, msscl);

  ZImage.wrap(source, target, msscl);
  graphics.background(#fff7d5);
  graphics.image(target, 0.0, 0.0, width, height);
}

import camzup.core.*;
import camzup.pfriendly.*;

YupJ2 graphics;
PImage img;
Gradient gradient = new Gradient();

void settings() {
  size(512, 512, YupJ2.PATH_STR);
}

void setup() {
  graphics = (YupJ2)getGraphics();
  frameRate(60.0);
  img = createImage(512, 512, ARGB);
  Gradient.paletteViridis(gradient);
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));

  Vec2 m = graphics.mouse1s(new Vec2());
  ZImage.conic(m, frameCount * 0.02, gradient, img);
  graphics.image(img);
}

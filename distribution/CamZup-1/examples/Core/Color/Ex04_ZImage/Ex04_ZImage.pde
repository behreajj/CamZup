import camzup.core.*;
import camzup.pfriendly.*;

Gradient gradient = new Gradient(
  #FF8080, #F4B380, #D5DE80, #A7F980,
  #72FE80, #40EE80, #18CA80, #039A80,
  #036580, #183580, #401180, #720180,
  #A70680, #D52180, #F44C80, #FF8080);

PImage input;
Yup2 rndr;

void settings() {
  size(720, 405, "camzup.pfriendly.Yup2");
}

void setup() {
  rndr = (Yup2)getGraphics();
  input = createImage(256, 256, ARGB);
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));
  Vec2 m = rndr.mouse1(new Vec2());

  //ZImage.linear(Vec2.negate(m, new Vec2()), m, gradient, input);
  //ZImage.radial(m, 0.5, gradient, input);
  ZImage.conic(m, frameCount * 0.02, gradient, input);
  ZImage.wrap(input, rndr, -104, 0);
}

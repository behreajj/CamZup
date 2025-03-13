import camzup.core.*;
import camzup.pfriendly.*;

Yup2 graphics;
Img img;
Gradient gradient = new Gradient();
Vec2 origin = new Vec2(-1.0f, -1.0f);
Vec2 dest = new Vec2(1.0f, 1.0f);

void settings() {
  size(512, 512, Yup2.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (Yup2)getGraphics();
  img = new Img(512, 512);
  Gradient.paletteRyb(gradient);
}

void draw() {
  graphics.mouse1s(origin);
  Vec2.negate(origin, dest);
  Img.gradientLinear(gradient,
    origin.x, origin.y,
    dest.x, dest.y,
    new Lab.MixLab(), img);
  graphics.background();
  graphics.image(Convert.toPImage(img));
}

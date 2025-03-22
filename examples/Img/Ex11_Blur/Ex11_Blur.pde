import com.behreajj.camzup.core.*;
import com.behreajj.camzup.friendly.*;

Yup2 graphics;

Img source = new Img();
Img blurred = new Img();
PImage pBlurred = new PImage(256, 256);
int step = 1;

void settings() {
  size(720, 405, Yup2.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (Yup2)getGraphics();

  PImage pSrc = loadImage("nasaWebbTelescope.png");
  pSrc.resize(pSrc.width / 3, pSrc.height / 3);
  Convert.toImg(pSrc, source);
}

void draw() {
  Vec2 m = graphics.mouse1u(new Vec2());
  step = Utils.lerp(0, 10, m.x);
  Img.blur(source, step, blurred);
  Convert.toPImage(blurred, pBlurred);

  graphics.background();
  graphics.image(pBlurred);
}

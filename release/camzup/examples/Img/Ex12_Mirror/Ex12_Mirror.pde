import com.behreajj.camzup.core.*;
import com.behreajj.camzup.friendly.*;

Yup2 graphics;

Img imgRgb = new Img(256, 256);
PImage pimgRgb = new PImage(256, 256);
Img mirrored = new Img(256, 256);
PImage pimgMirrored = new PImage(256, 256);
Rgb.AbstrToneMap mapper = new Rgb.ToneMapClamp();

void settings() {
  size(702, 405, Yup2.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (Yup2) getGraphics();

  Img.rgb(imgRgb);
  Convert.toPImage(imgRgb, pimgRgb);
}

void draw() {
  Vec2 orig = graphics.mouse1s(new Vec2());
  Vec2 dest = Vec2.negate(orig, new Vec2());
  Img.mirror(imgRgb, orig, dest,  mirrored);
  Convert.toPImage(mirrored, mapper, false, pimgMirrored);

  graphics.background();
  graphics.image(pimgMirrored);
}

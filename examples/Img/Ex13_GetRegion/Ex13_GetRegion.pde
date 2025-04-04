import com.behreajj.camzup.core.*;
import com.behreajj.camzup.friendly.*;

Yup2 graphics;

Img source = Img.rgb(new Img(256, 256));
Img target = new Img(64, 64);
PImage pTrg = new PImage(64, 64);

void settings() {
  size(720, 405, Yup2.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (Yup2)getGraphics();
}

void draw() {
  Img.getRegion(source, 64, 64, 255, 255, target);
  Convert.toPImage(target, pTrg);
  
  graphics.background();
  graphics.image(pTrg);
}

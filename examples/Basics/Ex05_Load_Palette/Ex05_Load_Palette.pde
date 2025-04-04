import com.behreajj.camzup.core.*;
import com.behreajj.camzup.friendly.*;

Yup2 graphics;

Lab[] palette;

Vec4 swatchXyz = new Vec4();
Rgb swatchLinear = new Rgb();
Rgb swatchRgb = new Rgb();

void settings() {
  size(702, 405, Yup2.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (Yup2)getGraphics();
  palette = ParserGpl.load(sketchPath() + "\\data\\genesis.gpl");
}

void draw() {
  graphics.background();
  graphics.camFlipped();
  graphics.noStroke();
  for (int k = 0; k < palette.length; ++k) {
    int j = k % 32;
    int i = k / 32;
    
    float x = 10 + j * 20;
    float y = 10 + i * 20;
    
    Lab swatch = palette[k];
    Rgb.srLab2TosRgb(swatch, swatchRgb, swatchLinear, swatchXyz);
    graphics.fill(swatchRgb);
    graphics.rect(x, y, 20, 20);
  }
}

import camzup.core.*;
import camzup.pfriendly.*;

Yup2 graphics;
Vec2 st = new Vec2();
Gradient gr = Gradient.paletteViridis(new Gradient());
PImage image;

void settings() {
  size(720, 405, Yup2.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (Yup2)getGraphics();
  image = createImage(512, 512, ARGB);
}

void draw() {
  float ang1 = frameCount * 0.1f;
  float ang0 = -ang1 * 0.2f;
  int sides = Utils.lerp(3, 8, mouseX / (width - 1.0f));

  image.loadPixels();
  int[] px = image.pixels;
  int len = px.length;
  int h = image.height;
  int w = image.width;

  float hNorm = 1.0f / (h - 1.0f);
  float wNorm = 1.0f / (w - 1.0f);

  for (int i = 0; i < len; ++i) {
    float xNorm = (i % w) * wNorm;
    float yNorm = (i / w) * hNorm;
    st.set(
      xNorm + xNorm - 1.0f,
      yNorm + yNorm - 1.0f);

    float fac1 = Sdf.arc(st, ang0, ang1, 0.25f, 0.35f);
    float fac2 = Sdf.polygon(st, sides, -ang0, 1.25f);
    float fac = Sdf.subtract(fac1, fac2);
    fac = Utils.quantize(fac, 16);
    px[i] = Gradient.eval(gr, fac);
  }
  image.updatePixels();
  
  graphics.image(image,
    0.0f, 0.0f,
    graphics.height, graphics.height);
}

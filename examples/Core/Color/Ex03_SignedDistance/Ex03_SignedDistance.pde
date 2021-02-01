import camzup.core.*;
import camzup.pfriendly.*;

Vec2 st = new Vec2();
Gradient gr = Gradient.paletteViridis(new Gradient());

void settings() {
  size(512, 512, Yup2.PATH_STR);
}

void setup() {
  frameRate(60.0);
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));

  float hNorm = 1.0 / (height - 1.0);
  float wNorm = 1.0 / (width - 1.0);
  float ang1 = frameCount * 0.1;
  float ang0 = -ang1 * 0.2;
  int sides = Utils.lerp(3, 8, mouseX * wNorm);

  loadPixels();
  int len = pixels.length;
  for (int i = 0; i < len; ++i) {
    float xNorm = (i % width) * wNorm;
    float yNorm = (i / width) * hNorm;
    st.set(xNorm + xNorm - 1.0, yNorm + yNorm - 1.0);

    float fac1 = Sdf.arc(st, ang0, ang1, 0.25, 0.35);
    float fac2 = Sdf.polygon(st, sides, -ang0, 1.25);
    float fac = Sdf.subtract(fac1, fac2);
    pixels[i] = Gradient.eval(gr, fac);
  }
  updatePixels();
}

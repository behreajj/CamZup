import camzup.core.*;
import camzup.pfriendly.*;

Vec2 st = new Vec2();
Gradient gr = Gradient.paletteViridis(new Gradient());
Color clr = new Color();

void settings() {
  size(256, 256, "camzup.pfriendly.Yup2");
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));

  float hNorm = 1.0 / (height - 1.0);
  float wNorm = 1.0 / (width - 1.0);
  float ang1 = frameCount * 0.1;
  float ang0 = -ang1 * 0.2;
  int sides = Utils.lerp(3, 8, mouseX * wNorm);

  loadPixels();
  for (int idx = 0, y = height - 1; y > -1; --y) {
    float yNorm = y * hNorm;
    st.y = yNorm + yNorm - 1.0;

    for (int x = 0; x < width; ++x, ++idx) {
      float xNorm = x * wNorm;
      st.x = xNorm + xNorm - 1.0;

      float fac1 = Sdf.arc(st, ang0, ang1, 0.325, 0.2);
      float fac2 = Sdf.polygon(st, sides, -ang0, 1.25);
      float fac = Sdf.subtract(fac1, fac2);
      pixels[idx] = Color.toHexInt(gr.eval(fac, clr));
    }
  }
  updatePixels();
}

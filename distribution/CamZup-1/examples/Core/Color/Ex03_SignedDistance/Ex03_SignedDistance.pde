import camzup.core.*;
import camzup.pfriendly.*;

Vec2 st = new Vec2();
Gradient gr = Gradient.paletteViridis(new Gradient());
Color clr = new Color();
Mesh2 m = Mesh2.polygon(new Mesh2(), 3, Mesh2.PolyType.NGON);
Vec2[] pts = m.coords;

void setup() {
  size(512, 512, "camzup.pfriendly.Yup2");
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));

  float hNorm = 1.0 / (height - 1.0);
  float wNorm = 1.0 / (width - 1.0);
  float ang1 = frameCount * 0.1;
  float ang0 = -ang1 * 0.5;

  loadPixels();
  for (int idx = 0, y = 0; y < height; ++y) {
    float yNorm = y * hNorm;
    st.y = 1.0 - (yNorm + yNorm);

    for (int x = 0; x < width; ++x, ++idx) {
      float xNorm = x * wNorm;
      st.x = xNorm + xNorm - 1.0;

      float fac1 = SDF.arc(st, ang0, ang1, 0.785, 0.05);
      float fac2 = SDF.polygon(st, pts);
      float fac = SDF.unionRound(fac1, fac2, 0.1);

      pixels[idx] = Color.toHexInt(gr.eval(fac, clr));
    }
  }
  updatePixels();
}

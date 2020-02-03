import camzup.core.*;
import camzup.pfriendly.*;

float exp = 2.0;
float seedr = 0.8;
float seedphi = 1.6;
int itr = 16;

Complex st = new Complex();
Complex zn = new Complex();
Complex seed = Complex.rect(seedr, seedphi, new Complex());

Gradient gr = new Gradient(#322020, #dbcab8, #fff7d5);
Color clr = new Color();

void settings() {
  size(512, 256, "camzup.pfriendly.Yup2");
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));

  float hNorm = 1.0 / (height - 1.0);
  float wNorm = 1.0 / (width - 1.0);

  if (mousePressed) {
    seedphi += 0.05;
    Complex.rect(seedr, seedphi, seed);
  }

  loadPixels();
  for (int idx = 0, y = height - 1; y > -1; --y) {
    float yNorm = y * hNorm;
    st.imag = yNorm + yNorm - 1.0;

    for (int x = 0; x < width; ++x, ++idx) {
      float xNorm = x * wNorm;
      st.real = xNorm + xNorm - 1.0;
      st.real += st.real;

      float fac = julia(seed, st, exp, itr, zn);
      gr.eval(fac, clr);
      pixels[idx] = Color.toHexInt(clr);
    }
  }
  updatePixels();
}

float julia(Complex seed, Complex z, float exp, int itr,
  Complex target) {

  int i = 0;
  target.set(z);
  for (; i < itr && Complex.absSq(target) < 4.0; ++i) {
    Complex.add(seed, Complex.pow(target, exp, target), target);
  }
  return i >= itr ? 0.0 : Utils.clamp01(i / (float)itr);
}

import camzup.core.*;
import camzup.pfriendly.*;

float seedr = 0.8;
float seedphi = 1.6;
int itr = 16;

Complex st = new Complex();
Complex zn = new Complex();
Complex exp = new Complex(2.0, 0.0);
Complex seed = Complex.rect(seedr, seedphi, new Complex());

Gradient grd = Gradient.paletteViridis(new Gradient());

void settings() {
  size(512, 256, Yup2.PATH_STR);
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
      pixels[idx] = Gradient.eval(grd, fac);
    }
  }
  updatePixels();
}

float julia(
  Complex seed, Complex z,
  Complex exp, int itr,
  Complex target) {

  int i = 0;
  target.set(z);
  for (; i < itr && Complex.absSq(target) < 4.0; ++i) {
    Complex.pow(target, exp, target);
    Complex.add(seed, target, target);
  }
  return i >= itr ? 0.0 : Utils.clamp01(i / (float)itr);
}

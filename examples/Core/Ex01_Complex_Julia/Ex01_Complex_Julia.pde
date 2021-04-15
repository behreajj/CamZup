import camzup.core.*;
import camzup.pfriendly.*;

float seedr = 0.8f;
float seedphi = 1.6f;
int itr = 16;

Complex st = new Complex();
Complex zn = new Complex();
Complex exp = new Complex(2.0f, 0.0f);
Complex seed = Complex.rect(seedr, seedphi, new Complex());

Gradient grd = Gradient.paletteViridis(new Gradient());

void settings() {
  size(512, 256, Yup2.PATH_STR);
}

void setup() {
  frameRate(60.0f);
}

void draw() {
  float hNorm = 1.0f / (height - 1.0f);
  float wNorm = 1.0f / (width - 1.0f);
  float aspect = width / (float)height;

  if (mousePressed) {
    seedphi += 0.05f;
    Complex.rect(seedr, seedphi, seed);
  }

  loadPixels();
  int len = pixels.length;
  for (int i = 0; i < len; ++i) {
    float xNorm = (i % width) * wNorm;
    float yNorm = (i / width) * hNorm;
    st.set(
      (xNorm + xNorm - 1.0f) * aspect,
      yNorm + yNorm - 1.0f);
    float fac = julia(seed, st, exp, itr, zn);
    pixels[i] = Gradient.eval(grd, fac);
  }
  updatePixels();
}

float julia(
  Complex seed,
  Complex z,
  Complex exp,
  int itr,
  Complex target) {

  target.set(z);
  int i = 0;
  for (; i < itr && Complex.absSq(target) < 4.0f; ++i) {
    Complex.pow(target, exp, target);
    Complex.add(seed, target, target);
  }
  return i >= itr ? 0.0f : Utils.clamp01(i / (float)itr);
}

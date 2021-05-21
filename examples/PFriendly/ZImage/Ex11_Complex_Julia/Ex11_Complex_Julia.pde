import camzup.core.*;
import camzup.pfriendly.*;

float seedr = 0.8f;
float seedphi = 1.6f;
int itr = 16;

Complex st = new Complex();
Complex zn = new Complex();
Complex exp = new Complex(2.0f, 0.0f);
Complex seed = Complex.rect(seedr, seedphi, new Complex());

YupJ2 graphics;
PImage img;

Gradient grd = Gradient.paletteViridis(new Gradient());

void settings() {
  size(720, 405, YupJ2.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  img = createImage(256, 256, ARGB);
  graphics = (YupJ2)getGraphics();
}

void draw() {

  if (mousePressed) {
    seedphi += 0.05f;
    Complex.rect(seedr, seedphi, seed);
  }

  img.loadPixels();
  int[] px = img.pixels;
  int len = px.length;
  int w = img.width;
  int h = img.height;
  float hNorm = 1.0f / (w - 1.0f);
  float wNorm = 1.0f / (h - 1.0f);
  float aspect = w / (float)h;

  for (int i = 0; i < len; ++i) {
    float xNorm = (i % w) * wNorm;
    float yNorm = (i / w) * hNorm;
    st.set(
      (xNorm + xNorm - 1.0f) * aspect,
      yNorm + yNorm - 1.0f);
    float fac = julia(seed, st, exp, itr, zn);
    px[i] = Gradient.eval(grd, fac);
  }
  img.updatePixels();
  
  graphics.image(img, 0.0, 0.0, w, h);
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

import camzup.core.*;
import camzup.pfriendly.*;

int octaves = 4;
float lacunarity = 2.5f;
float persist = 0.5f;
float scale = 1.0f;

Vec3 noise = new Vec3();
Vec3 deriv = new Vec3();
Color clr = new Color();
PImage image;
Yup2 graphics;

void settings() {
  size(720, 405, Yup2.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (Yup2)getGraphics();
  graphics.colorMode(RGB, 1.0f);
  image = createImage(384, 384, ARGB);
}

void draw() {

  float z = frameCount * 0.05f;

  persist = Utils.lerp(0.1f, 0.85f, mouseX / (width - 1.0f));
  octaves = Utils.lerp(1, 8, mouseY / (height - 1.0f));

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

    noise.set(
      (xNorm + xNorm - 1.0f) * scale,
      (yNorm + yNorm - 1.0f) * scale, z);

    if (mousePressed) {
      float fac = Simplex.fbm(noise, Simplex.DEFAULT_SEED,
        octaves, lacunarity, persist);
      fac = Utils.clamp01(fac * 0.5f + 0.5f);
      clr.set(fac, fac, fac, 1.0f);
    } else {
      Simplex.fbm(noise, Simplex.DEFAULT_SEED,
        octaves, lacunarity, persist, deriv);
      Color.fromDir(deriv, clr);
    }

    px[i] = Color.toHexInt(clr);
  }
  image.updatePixels();

  graphics.image(image,
    0.0f, 0.0f,
    graphics.height, graphics.height);
}

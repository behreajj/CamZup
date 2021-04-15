import camzup.core.*;
import camzup.pfriendly.*;

int octaves = 4;
float lacunarity = 2.5f;
float persist = 0.5f;
float scale = 1.0f;

Vec3 noise = new Vec3();
Vec3 deriv = new Vec3();
Color clr = new Color();

void settings() {
  size(384, 384, Yup2.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  colorMode(RGB, 1.0f);
}

void draw() {
  float hNorm = 1.0f / (height - 1.0f);
  float wNorm = 1.0f / (width - 1.0f);
  float z = frameCount * 0.05f;

  persist = Utils.lerp(0.1f, 0.85f, mouseX * wNorm);
  octaves = Utils.lerp(1, 8, mouseY * hNorm);

  loadPixels();
  int len = pixels.length;
  for (int i = 0; i < len; ++i) {
    float xNorm = (i % width) * wNorm;
    float yNorm = (i / width) * hNorm;

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

    pixels[i] = Color.toHexInt(clr);
  }
  updatePixels();
}

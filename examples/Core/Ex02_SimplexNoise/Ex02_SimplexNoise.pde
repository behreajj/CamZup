import camzup.core.*;
import camzup.pfriendly.*;

float scale = 1.0;
int octaves = 4;
float lacunarity = 2.5;
float persist = 0.5;

Vec3 noise = new Vec3();
Vec3 deriv = new Vec3();
Color clr = new Color();

void settings() {
  size(384, 384, Yup2.PATH_STR);
}

void setup() {
  frameRate(60.0);
  colorMode(RGB, 1.0);
}

void draw() {
  float hNorm = 1.0 / (height - 1.0);
  float wNorm = 1.0 / (width - 1.0);
  float z = frameCount * 0.05;

  persist = Utils.lerp(0.1, 0.85, mouseX * wNorm);
  octaves = Utils.lerp(1, 8, mouseY * hNorm);

  String diagnostic = new StringBuilder()
    .append("FPS: ")
    .append(Utils.toFixed(frameRate, 1))
    .append(", octaves: ")
    .append(octaves)
    .append(", persistence: ")
    .append(Utils.toFixed(persist, 2))
    .toString();
  surface.setTitle(diagnostic);

  loadPixels();
  int len = pixels.length;
  for (int i = 0; i < len; ++i) {
    float xNorm = (i % width) * wNorm;
    float yNorm = (i / width) * hNorm;

    noise.set((xNorm + xNorm - 1.0) * scale,
      (yNorm + yNorm - 1.0) * scale, z);

    if (mousePressed) {
      float fac = Simplex.fbm(noise, Simplex.DEFAULT_SEED,
        octaves, lacunarity, persist);
      fac = Utils.clamp01(fac * 0.5 + 0.5);
      clr.set(fac, fac, fac, 1.0);
    } else {
      Simplex.fbm(noise, Simplex.DEFAULT_SEED,
        octaves, lacunarity, persist, deriv);
      Color.fromDir(deriv, clr);
    }

    pixels[i] = Color.toHexInt(clr);
  }
  updatePixels();
}

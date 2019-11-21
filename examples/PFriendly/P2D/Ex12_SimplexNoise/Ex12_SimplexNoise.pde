import camzup.core.*;
import camzup.pfriendly.*;

float scale = 1.0;
int octaves = 4;
float lacunarity = 2.5;
float persist = 0.5;
int seed = (int)System.currentTimeMillis();

Vec3 noise = new Vec3();
Vec3 deriv = new Vec3();

void setup() {
  size(512, 512, "camzup.pfriendly.Yup2");
  frameRate(1000);
  colorMode(RGB, 1.0);
}

void draw() {

  float hNorm = 1.0 / (height - 1.0);
  float wNorm = 1.0 / (width - 1.0);
  noise.z = frameCount * 0.05;

  persist = Utils.lerp(0.1, 0.85, mouseX * wNorm);
  octaves = (int)Utils.lerp(1, 16, mouseY * hNorm);

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
  for (int idx = 0, y = 0; y < height; ++y) {
    float yNorm = y * hNorm;
    noise.y = (yNorm + yNorm - 1.0) * scale;

    for (int x = 0; x < width; ++x, ++idx) {
      float xNorm = x * wNorm;
      noise.x = (xNorm + xNorm - 1.0) * scale;

      Simplex.fbm(
        noise, seed, 
        octaves, 
        lacunarity, 
        persist, 
        deriv);

      Vec3.normalize(deriv, deriv);

      pixels[idx] = color(
        deriv.x * 0.5 + 0.5, 
        deriv.y * 0.5 + 0.5, 
        deriv.z * 0.5 + 0.5);
    }
  }
  updatePixels();
}

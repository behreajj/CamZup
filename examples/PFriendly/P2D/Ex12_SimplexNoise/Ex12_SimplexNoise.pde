import camzup.core.*;
import camzup.pfriendly.*;

float rough = 5.0;
int octaves = 4;
int seed = (int)System.currentTimeMillis();
Vec3 noise = new Vec3();

void setup() {
  size(512, 512, "camzup.pfriendly.YupJ2");
  frameRate(1000);
  colorMode(RGB, 1.0);
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));

  float hNorm = 1.0 / (height - 1.0);
  float wNorm = 1.0 / (width - 1.0);
  rough = Utils.lerp(0.25, 5.0, mouseX * wNorm);
  octaves = (int)Utils.lerp(1, 16, mouseY * hNorm);
  println(octaves);
  float nz = frameCount * 0.05;

  loadPixels();
  for (int idx = 0, y = 0; y < height; ++y) {
    float yNorm = y * hNorm;
    float ny = (yNorm + yNorm - 1.0) * rough;

    for (int x = 0; x < width; ++x, ++idx) {
      float xNorm = x * wNorm;
      float nx = (xNorm + xNorm - 1.0) * rough;

      noise.set(nx, ny, nz);

      //float fac = 0.5 + 0.5 * Simplex.eval(nx, ny, nz, seed);
      float fac = 0.5 + 0.5 * Simplex.fbm(noise, seed, octaves);
      pixels[idx] = color(fac);
    }
  }
  updatePixels();
}

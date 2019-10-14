import camzup.core.*;
import camzup.pfriendly.*;

SimplexNoise osn = new SimplexNoise();
float rough = 5.0;

void setup() {
  size(512, 512, "camzup.pfriendly.Yup2");
  frameRate(1000);
  colorMode(RGB, 1.0);
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));

  float hNorm = 1.0 / (height - 1.0);
  float wNorm = 1.0 / (width - 1.0);
  rough = Utils.lerp(0.25, 10.0, mouseX * wNorm);
  float nz = frameCount * 0.05;
  
  loadPixels();
  for (int idx = 0, y = 0; y < height; ++y) {
    float yNorm = y * hNorm;
    float ny = (yNorm + yNorm - 1.0) * rough;

    for (int x = 0; x < width; ++x, ++idx) {
      float xNorm = x * wNorm;
      float nx = (xNorm + xNorm - 1.0) * rough;

      float fac = 0.5 + 0.5 * osn.eval(nx, ny, nz);
      pixels[idx] = color(fac);
    }
  }
  updatePixels();
}

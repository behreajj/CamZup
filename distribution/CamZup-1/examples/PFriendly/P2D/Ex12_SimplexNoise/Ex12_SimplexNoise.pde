import camzup.core.*;
import camzup.pfriendly.*;

float scale = 5.0;
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
  float nz = mouseX * wNorm;
  float ang = frameCount * 0.05;
  float ang0 = -PI + TAU * mouseY * hNorm;

  //persist = Utils.lerp(0.1, 0.85, mouseX * wNorm);
  //octaves = (int)Utils.lerp(3, 16, mouseY * hNorm);

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
    float ny = (yNorm + yNorm - 1.0) * scale;

    for (int x = 0; x < width; ++x, ++idx) {
      float xNorm = x * wNorm;
      float nx = (xNorm + xNorm - 1.0) * scale;

      noise.set(nx, ny, nz);
      //Vec3.rotateY(noise, ang0, noise);
      
      float fac = 0.5 + 0.5 * Simplex.flow(noise, ang, seed, deriv);
      Vec3.normalize(deriv, deriv);
      //pixels[idx] = color(
      //  deriv.x * 0.5 + 0.5, 
      //  deriv.y * 0.5 + 0.5, 
      //  deriv.z * 0.5 + 0.5);
      pixels[idx] = color(
        fac);
    }
  }
  updatePixels();
}

import camzup.core.*;
import camzup.pfriendly.*;

YupJ2 main;
YupJ2 buff;

int samples = 8;
float minScale = 0.25f;
float maxScale = 5.0f;
int wOriginal = 96;
int hOriginal = 96;
boolean nearToggle = false;

PImage source;
int wOrig;
int hOrig;

PImage[] bicubic = new PImage[samples];
float[] scales = new float[samples];

void settings() {
  size(720, 202, YupJ2.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  main = (YupJ2)getGraphics();
  buff = (YupJ2)createGraphics(1920, 540, YupJ2.PATH_STR);

  source = createImage(wOriginal, hOriginal, ARGB);
  Gradient grd0 = new Gradient(
    0xaf00ff7f,
    0x7f7f00ff,
    0x3fff7f00,
    0xffffffff);
  ZImage.radial(0.0f, 0.0f, 0.7071f, grd0, source);

  if (main.getClass().getSimpleName().equals("Yup2")) {
    ZImage.premul(source, source);
  }

  wOrig = source.width;
  hOrig = source.height;

  float toStep = 1.0f / (samples - 1.0f);
  long init = System.currentTimeMillis();
  for (int i = 0; i < samples; ++i) {
    float step = i * toStep;
    float scale = Utils.lerp(minScale, maxScale, step);
    scales[i] = scale;
    PImage rszcb = bicubic[i] = source.get();

    long start = System.currentTimeMillis();
    ZImage.scaleBilinear(source, scale, scale, rszcb);
    long stop = System.currentTimeMillis();

    long diff0 = stop - start;
    println("bicubic:", diff0,
      ", w:", rszcb.width, ", h:", rszcb.height);
  }
  long concl = System.currentTimeMillis();
  long cumul = concl - init;
  println("cumulative:", cumul);
}

void draw() {
  buff.beginDraw();
  buff.background(0xff202020);
  buff.textAlign(LEFT, TOP);
  buff.textSize(14);

  float halfWidth = buff.width * 0.5f;
  float right = halfWidth - bicubic[samples - 1].width * 0.525f;
  float left = bicubic[0].width * 0.525f - halfWidth;
  float toStep = 1.0f / (samples - 1.0f);
  for (int i = 0; i < samples; ++i) {
    PImage img = bicubic[i];
    float step = i * toStep;
    float x = Utils.lerp(left, right, Utils.pow(step, 1.5f));
    buff.image(img, x, 0.0f);

    float xLabel = x - img.width * 0.5f;
    float yLabel = -img.height * 0.5f;
    String str = Utils.toFixed(scales[i], 3)
      + ": " + img.width + " x " + img.height;
    buff.fill(0xff000000);
    buff.text(str, xLabel + 1, yLabel - 1);
    buff.fill(0xffffffff);
    buff.text(str, xLabel, yLabel);
  }
  buff.endDraw();

  main.image(buff, 0.0f, 0.0f, main.width, main.height);
}

void keyReleased() {
  if (key == 's' || key == 'S') {
    buff.save("/data/" + millis() + ".png");
    println("Screen saved.");
  }
}

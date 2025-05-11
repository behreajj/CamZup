import com.behreajj.camzup.core.*;
import com.behreajj.camzup.friendly.*;

boolean swapLayers = false;

Yup2 graphics;

Img under = new Img();
Img over = new Img();
Img blend = new Img();

int lBlendIdx = 2;
int abBlendIdx = 2;
int alphaBlendIdx = 0;

BlendMode.L[] lBlends = {
  BlendMode.L.ADD,
  BlendMode.L.AVERAGE,
  BlendMode.L.BLEND,
  BlendMode.L.BURN,
  BlendMode.L.DIVIDE,
  BlendMode.L.DODGE,
  BlendMode.L.MULTIPLY,
  BlendMode.L.OVER,
  BlendMode.L.SCREEN,
  BlendMode.L.SUBTRACT,
  BlendMode.L.UNDER,
};

BlendMode.AB[] abBlends = {
  BlendMode.AB.ADD,
  BlendMode.AB.AVERAGE,
  BlendMode.AB.BLEND,
  BlendMode.AB.CHROMA,
  BlendMode.AB.HUE,
  BlendMode.AB.OVER,
  BlendMode.AB.SUBTRACT,
  BlendMode.AB.UNDER,
};

BlendMode.Alpha[] alphaBlends = {
  BlendMode.Alpha.BLEND,
  BlendMode.Alpha.MAX,
  BlendMode.Alpha.MIN,
  BlendMode.Alpha.MULTIPLY,
  BlendMode.Alpha.OVER,
  BlendMode.Alpha.UNDER,
};

Rgb.AbstrToneMap toneMap = new Rgb.ToneMapHable();
boolean usePremul = true;
PImage pBlended = new PImage(128, 128);

void settings() {
  size(720, 405, Yup2.PATH_STR);
}

void setup() {
  frameRate(60.0f);
  graphics = (Yup2)getGraphics();

  graphics.textSize(16);
  graphics.textAlign(LEFT, CENTER);

  PImage pUnder = loadImage("nasaWebbTelescope.png");
  PImage pOver = loadImage("testImage3.png");

  pUnder.resize(pUnder.width / 3, pUnder.height / 3);

  under = new Img();
  over = new Img();

  Convert.toImg(pUnder, under);
  Convert.toImg(pOver, over);
}

void draw() {
  Img.blend(
    swapLayers ? over : under,
    swapLayers ? under: over,
    alphaBlends[alphaBlendIdx],
    lBlends[lBlendIdx],
    abBlends[abBlendIdx],
    blend);
  Convert.toPImage(blend, toneMap, usePremul,
    pBlended);

  String lText = "L: "
    + lBlends[lBlendIdx].name();
  String abText = "AB: "
    + abBlends[abBlendIdx].name();
  String alphaText = "Alpha: "
    + alphaBlends[alphaBlendIdx].name();

  graphics.background(0xff202020);
  graphics.image(pBlended);

  float xLbl = graphics.width * -0.495;

  graphics.fill(0xff000000);
  graphics.text(lText, xLbl - 1, 63);
  graphics.text(abText, xLbl - 1, 31);
  graphics.text(alphaText, xLbl - 1, -1);

  graphics.fill(0xffffffff);
  graphics.text(lText, xLbl, 64);
  graphics.text(abText, xLbl, 32);
  graphics.text(alphaText, xLbl, 0);
}

void mouseReleased() {
  if (mouseButton == LEFT) {
    lBlendIdx = (lBlendIdx + 1)
      % lBlends.length;
  } else if (mouseButton == RIGHT) {
    abBlendIdx = (abBlendIdx + 1)
      % abBlends.length;
  }
}

void keyReleased() {
  if (key == ' ') {
    alphaBlendIdx = (alphaBlendIdx + 1)
      % alphaBlends.length;
  } else {
    swapLayers = !swapLayers;
  }
}

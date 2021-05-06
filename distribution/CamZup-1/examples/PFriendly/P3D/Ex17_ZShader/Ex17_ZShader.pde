import camzup.core.*;
import camzup.pfriendly.*;

Yup2 main;
Yup2 buff;
ZShader linearGradient;

Color aColor = new Color(0.0f, 0.5f, 1.0f, 1.0f);
Color bColor = new Color(1.0f, 1.0f, 0.0f, 1.0f);
Vec2 origin = new Vec2();
Vec2 dest = new Vec2();

void settings() {
  size(720, 405, Yup2.PATH_STR);
}

void setup() {
  main = (Yup2)getGraphics();
  buff = (Yup2)createGraphics(1920, 1080, Yup2.PATH_STR);
  dest.set(buff.width, 0.0f);

  linearGradient = (ZShader)loadShader("linear.frag", "linear.vert");
  linearGradient.set("origin", origin);
  linearGradient.set("dest", dest);
  linearGradient.set("aColor", aColor);
  linearGradient.set("bColor", bColor);
}

void draw() {
  float hw = buff.width * 0.5f;
  float x = Utils.pingPong(-hw, hw, frameCount * 0.005f, 1.12f);

  buff.beginDraw();
  buff.clear();
  buff.shader(linearGradient);
  buff.ellipse(x, 0.0f, 750.0f, 750.0f);
  buff.endDraw();

  main.background(0xff202020);
  main.image(buff, 0.0f, 0.0f, main.width, main.height);
}

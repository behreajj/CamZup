import camzup.core.*;
import camzup.pfriendly.*;

YupJ2 rndr;
Gradient grad = Gradient.paletteViridis(new Gradient());
Color.AbstrEasing mixer = new Color.LerpRgba();
Vec2 st = new Vec2();
Color clr = new Color();
Vec2 lnAnchor = new Vec2();
Vec2 mouse = new Vec2();
float lineWgt = 0.2;
Vec2[] corners = new Vec2[] {
  new Vec2(-0.3, -0.3), 
  new Vec2(0.3, -0.3), 
  new Vec2(0.3, 0.3), 
  new Vec2(-0.3, 0.3)
};

void setup() {
  size(512, 512, "camzup.pfriendly.YupJ2");
  rndr = (YupJ2)getGraphics();
  rndr.colorMode(RGB, 1.0);
}

void draw() {  
  surface.setTitle(Utils.toFixed(frameRate, 1));

  float wInv = 1.0 / (width - 1.0);
  float hInv = 1.0 / (height - 1.0);
  float anim = cos(frameCount * 0.01) * 0.5 + 0.5;
  rndr.mouse1(mouse);

  rndr.loadPixels();
  for (int i = 0, y = 0; y < height; ++y) {

    // Convert y coordinate from [0, height] to [-1.0, 1.0].
    st.y = 1.0 - 2.0 * (y * hInv);
    for (int x = 0; x < width; ++x, ++i) {

      // Convert x coordinate from [0, width] to [-1.0, 1.0].
      st.x = (x * wInv) * 2.0 - 1.0;

      // Create shapes.
      float fac0 = SDF.polygon(st, corners);
      float fac1 = SDF.line(st, lnAnchor, mouse, lineWgt);

      // Composite together separate factors.
      float fac = Utils.lerpUnclamped(fac0, fac1, anim);

      // Convert the shape factor to a color with a gradient.
      grad.eval(fac, mixer, clr);
      rndr.pixels[i] = Color.toHexInt(clr);
    }
  }
  rndr.updatePixels();
}

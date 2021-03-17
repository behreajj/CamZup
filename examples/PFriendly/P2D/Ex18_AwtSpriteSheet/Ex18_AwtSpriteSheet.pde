import camzup.core.*;
import camzup.pfriendly.*;
import java.awt.Image;

YupJ2 main;
YupJ2 buff;

PImage fontImg;
Image fontImgNtv;
Color fontClr = new Color(1.0, 1.0, 1.0, 1.0);

PImage shadowImg;
Image shadowImgNtv;
Color shadowClr = new Color(0.125, 0.125, 0.125, 1.0);

int pxPerGlyph = 8;

// Sprite includes
// lb inclusive, ub exclusive
// [0x0000, 0x0100)
// [0x2013, 0x201E)
int sublen0 = 0x100;
int sublen1 = (0x201e - 0x2013);
int len = sublen0 + sublen1;
char[] glyphs = new char[len];
{
  for (int i = 0; i < sublen0; ++i) {
    glyphs[i] = (char)i;
  }

  for (int i = 0x2013, j = sublen0; i < 0x201e; ++i, ++j) {
    glyphs[j] = (char)i;
  }
}

HashMap<Character, MaterialAwt.Sample> map =
  new HashMap<Character, MaterialAwt.Sample>(len);

int leading = 2;
int kerning = 0;
int margin = pxPerGlyph;

boolean capsLock = false;

String str;

void settings() {
  size(512, 512, YupJ2.PATH_STR);
  noSmooth();
}

void setup() {
  frameRate(60.0);
  main = (YupJ2)getGraphics();

  buff = (YupJ2)createGraphics(256, 256, YupJ2.PATH_STR);

  fontImg = loadImage("data/rasterFont.png");
  shadowImg = fontImg.get();

  ZImage.tint(fontImg, fontClr, 1.0);
  ZImage.tint(shadowImg, shadowClr, 1.0);

  fontImgNtv = YupJ2.convertPImageToNative(fontImg);
  shadowImgNtv = YupJ2.convertPImageToNative(shadowImg);

  String[] strarr = loadStrings("source.txt");
  StringBuilder sb = new StringBuilder(1024);
  for (String line : strarr) {
    sb.append(line);
  }
  str = sb.toString();

  int widInGlyphs = fontImg.width / pxPerGlyph;
  for (int i = 0; i < len; ++i) {

    // Determine texture pixel coordinates.
    int x0 = pxPerGlyph * (i % widInGlyphs);
    int y0 = pxPerGlyph * (i / widInGlyphs);
    int x1 = x0 + pxPerGlyph;
    int y1 = y0 + pxPerGlyph;

    MaterialAwt.Sample sample = new MaterialAwt.Sample(
      x0, y0, x1, y1);

    map.put(glyphs[i], sample);
  }
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));

  buff.beginDraw();
  buff.background(0xff404040);
  buff.camera(
    buff.width * 0.5, 
    buff.height * 0.5, 
    0.0, 1.0, 1.0);

  char[] charr = str.toCharArray();
  Bounds2 hlBounds = new Bounds2();
  Bounds2 shBounds = new Bounds2();
  int xCursor = margin;
  int yCursor = buff.height - margin - pxPerGlyph;

  for (int i = 0; i < charr.length; ++i) {
    char c = charr[i];

    if (capsLock && c > 0x0060 && c < 0x007b) {
      c -= 0x0020;
    }

    if (c == '\n') {
      xCursor = margin;
      yCursor -= pxPerGlyph + leading;
    } else  if (xCursor > buff.width - margin * 2) {
      xCursor = margin;
      yCursor -= pxPerGlyph + leading;
      if (c != ' ') {
        --i;
      }
    } else {
      if (yCursor < margin) {
        break;
      }

      MaterialAwt.Sample sample = map.get(c);

      float x1 = xCursor + pxPerGlyph;
      float y1 = yCursor + pxPerGlyph;

      shBounds.set(
        xCursor + 0.5, yCursor - 0.5, 
        x1 + 0.5, y1 - 0.5);
      buff.imageNative(shadowImgNtv, sample, shBounds);

      hlBounds.set(
        xCursor, yCursor, 
        x1, y1);
      buff.imageNative(fontImgNtv, sample, hlBounds);

      xCursor += pxPerGlyph + kerning;
    }
  }

  buff.endDraw();

  main.background();
  main.camera();
  main.image(buff, 0.0, 0.0, main.width, main.height);
}

void keyReleased() {
  capsLock = !capsLock;
}

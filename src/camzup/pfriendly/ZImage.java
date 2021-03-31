package camzup.pfriendly;

import java.util.function.IntFunction;
import java.util.regex.Pattern;

import camzup.core.Color;
import camzup.core.Gradient;
import camzup.core.IUtils;
import camzup.core.Sdf;
import camzup.core.Utils;
import camzup.core.Vec2;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import processing.core.PFont.Glyph;
import processing.core.PImage;

/**
 * An extension of PImage, specializing in color gradients and blitting
 * text to an image.
 */
public class ZImage extends PImage {

   /**
    * Constructs an image from its dimensions, width and height.
    *
    * @param width  the image width
    * @param height the image height
    */
   public ZImage ( final int width, final int height ) {

      super(width, height);
   }

   /**
    * Constructs an image from the dimensions and format.
    *
    * @param width  the image width
    * @param height the image height
    * @param format the format
    */
   public ZImage ( final int width, final int height, final int format ) {

      super(width, height, format);
   }

   /**
    * Constructs an image from the dimensions, format and pixel density.
    *
    * @param width        the image width
    * @param height       the image height
    * @param format       the format
    * @param pixelDensity the pixel density
    */
   public ZImage ( final int width, final int height, final int format,
      final int pixelDensity ) {

      super(width, height, format, pixelDensity);
   }

   /**
    * The default constructor.
    */
   protected ZImage ( ) {}

   /**
    * Returns a string representation of an image, including its format,
    * width, height and pixel density.
    *
    * @return the string
    */
   @Override
   public String toString ( ) { return ZImage.toString(this); }

   /**
    * Update the pixels[] buffer to the PImage. The overridden functionality
    * eliminates unnecessary checks.
    */
   @Override
   public void updatePixels ( ) {

      if ( !this.modified ) {

         this.mx1 = 0;
         this.mx2 = this.pixelWidth;
         this.my1 = 0;
         this.my2 = this.pixelHeight;
         this.modified = true;

      } else {

         if ( 0 < this.mx1 ) { this.mx1 = 0; }
         if ( 0 > this.mx2 ) {
            this.mx2 = this.pixelWidth <= 0 ? this.pixelWidth : 0;
         }

         if ( 0 < this.my1 ) { this.my1 = 0; }
         if ( 0 > this.my2 ) {
            this.my2 = this.pixelHeight <= 0 ? this.pixelHeight : 0;
         }

         if ( this.pixelWidth < this.mx1 ) {
            this.mx1 = 0 >= this.pixelWidth ? 0 : this.pixelWidth;
         }
         if ( this.pixelWidth > this.mx2 ) { this.mx2 = this.pixelWidth; }

         if ( this.pixelHeight < this.my1 ) {
            this.my1 = 0 >= this.pixelHeight ? 0 : this.pixelHeight;
         }
         if ( this.pixelHeight > this.my2 ) { this.my2 = this.pixelHeight; }
      }
   }

   /**
    * Gets the parent applet of this PImage.
    *
    * @return the parent
    */
   PApplet getParent ( ) { return this.parent; }

   /**
    * Sets the parent of this PImage. The parent reference is needed for the
    * save function.
    *
    * @param parent the PApplet
    *
    * @return this image
    */

   PImage setParent ( final PApplet parent ) {

      this.parent = parent;
      return this;
   }

   /**
    * Default color for dark checker squares, {@value ZImage#CHECKER_DARK} or
    * 1.0 / 3.0 of 255.
    */
   public static final int CHECKER_DARK = 0xff555555;

   /**
    * Default color for light checker squares, {@value ZImage#CHECKER_LIGHT}
    * or 2.0 / 3.0 of 255.
    */
   public static final int CHECKER_LIGHT = 0xffaaaaaa;

   /**
    * Default horizontal alignment when creating an image from text:
    * {@link PConstants#LEFT}, {@value PConstants#LEFT}.
    */
   public static final int DEFAULT_ALIGN = PConstants.LEFT;

   /**
    * Default spacing between characters, in pixels, when creating an image
    * from text: {@value ZImage#DEFAULT_KERNING}.
    */
   public static final int DEFAULT_KERNING = 0;

   /**
    * Default spacing between lines, in pixels, when creating an image from
    * text: {@value ZImage#DEFAULT_LEADING}.
    */
   public static final int DEFAULT_LEADING = 8;

   /**
    * Finds the aspect ratio of an image, it's width divided by its height.
    *
    * @param img the image
    *
    * @return the aspect ratio
    */
   public static float aspect ( final PImage img ) {

      return Utils.div(( float ) img.width, ( float ) img.height);
   }

   /**
    * Renders a checker pattern on an image for diagnostic purposes.
    *
    * @param a      the first color
    * @param b      the second color
    * @param cols   the column count
    * @param rows   the row count
    * @param target the output image
    *
    * @return the output image
    */
   public static PImage checker ( final Color a, final Color b, final int cols,
      final int rows, final PImage target ) {

      return ZImage.checker(Color.toHexInt(a), Color.toHexInt(b), cols, rows,
         target);
   }

   /**
    * Renders a checker pattern on an image for diagnostic purposes.
    *
    * @param a      the first color
    * @param b      the second color
    * @param count  the count
    * @param target the output image
    *
    * @return the output image
    */
   public static PImage checker ( final Color a, final Color b, final int count,
      final PImage target ) {

      return ZImage.checker(a, b, count, count, target);
   }

   /**
    * Renders a checker pattern on an image for diagnostic purposes. Ideally,
    * the image dimensions should be a power of 2 (32, 64, 128, 256, 512).
    *
    * @param a      the first color
    * @param b      the second color
    * @param cols   the column count
    * @param rows   the row count
    * @param target the output image
    *
    * @return the output image
    */
   public static PImage checker ( final int a, final int b, final int cols,
      final int rows, final PImage target ) {

      final int vcols = cols < 2 ? 2 : cols;
      final int vrows = rows < 2 ? 2 : rows;

      target.loadPixels();

      final int w = target.width;
      final int h = target.height;
      final int[] px = target.pixels;
      final int len = px.length;

      final int wch = w / vcols;
      final int hchw = w * h / vrows;

      for ( int i = 0; i < len; ++i ) {
         px[i] = ( i % w / wch + i / hchw ) % 2 == 0 ? a : b;
      }

      target.updatePixels();
      return target;
   }

   /**
    * Renders a checker pattern on an image for diagnostic purposes.
    *
    * @param a      the first color
    * @param b      the second color
    * @param count  the count
    * @param target the output image
    *
    * @return the output image
    */
   public static PImage checker ( final int a, final int b, final int count,
      final PImage target ) {

      return ZImage.checker(a, b, count, count, target);
   }

   /**
    * Renders a checker pattern on an image for diagnostic purposes. Uses
    * {@value ZImage#CHECKER_DARK} and {@value ZImage#CHECKER_LIGHT} as
    * default colors.
    *
    * @param count  the count
    * @param target the output image
    *
    * @return the output image
    */
   public static PImage checker ( final int count, final PImage target ) {

      return ZImage.checker(ZImage.CHECKER_DARK, ZImage.CHECKER_LIGHT, count,
         count, target);
   }

   /**
    * Generates a conic gradient, where the factor rotates on the z axis
    * around an origin point. Best used with square images; for other aspect
    * ratios, the origin should be adjusted accordingly.
    *
    * @param xOrigin the origin x coordinate
    * @param yOrigin the origin y coordinate
    * @param radians the angular offset
    * @param grd     the gradient
    * @param target  the output image
    *
    * @return the image
    */
   public static PImage conic ( final float xOrigin, final float yOrigin,
      final float radians, final Gradient grd, final PImage target ) {

      target.loadPixels();

      final int w = target.width;
      final int h = target.height;
      final int[] pixels = target.pixels;
      final int len = pixels.length;

      final float shortEdge = w < h ? w : h;
      final float longEdge = w > h ? w : h;

      final float hInv = 1.0f / ( h - 1.0f );

      float wInv;
      final float xo = xOrigin;
      float aspect;
      if ( shortEdge == longEdge ) {
         wInv = 1.0f / ( w - 1.0f );
      } else if ( w == shortEdge ) {
         aspect = shortEdge / longEdge;
         wInv = aspect / ( w - 1.0f );
      } else {
         aspect = longEdge / shortEdge;
         wInv = aspect / ( w - 1.0f );
      }

      for ( int i = 0; i < len; ++i ) {
         final float xn = wInv * ( i % w );
         final float yn = hInv * ( i / w );
         pixels[i] = Gradient.eval(grd, Sdf.conic(xn + xn - xo - 1.0f, 1.0f
            - ( yn + yn + yOrigin ), radians));
      }

      target.updatePixels();
      return target;
   }

   /**
    * Generates a conic gradient, where the factor rotates on the z axis
    * around an origin point.
    *
    * @param origin  the origin
    * @param radians the angular offset
    * @param grd     the gradient
    * @param target  the output image
    *
    * @return the image
    */
   public static PImage conic ( final Vec2 origin, final float radians,
      final Gradient grd, final PImage target ) {

      return ZImage.conic(origin.x, origin.y, radians, grd, target);
   }

   /**
    * Recolors an image in-place with a color gradient. The color is converted
    * to a factor in [0.0, 1.0] by an evaluation function.
    *
    * @param grd     the color gradient
    * @param clrEval the color evaluator
    * @param target  the target image
    *
    * @return the augmented image
    */
   public static PImage falseColor ( final Gradient grd, final IntFunction <
      Float > clrEval, final PImage target ) {

      target.loadPixels();
      final int[] px = target.pixels;
      final int len = px.length;
      for ( int i = 0; i < len; ++i ) {
         px[i] = Gradient.eval(grd, clrEval.apply(px[i]));
      }
      target.updatePixels();
      return target;
   }

   /**
    * Recolors an image in-place with a color gradient. The evaluation factor
    * is the product of a pixel's luminance and its transparency.
    *
    * @param grd    the color gradient
    * @param target the target image
    *
    * @return the augmented image
    */
   public static PImage falseColor ( final Gradient grd, final PImage target ) {

      target.loadPixels();
      final int[] px = target.pixels;
      final int len = px.length;
      for ( int i = 0; i < len; ++i ) {

         final float alpha = ( px[i] >> 0x18 & 0xff ) * IUtils.ONE_255;
         px[i] = Gradient.eval(grd, alpha * Color.luminance(px[i]));
      }
      target.updatePixels();
      return target;
   }

   /**
    * Fills an image in place with a color.
    *
    * @param target the target image
    * @param fll    the fill color
    *
    * @return the image
    */
   public static PImage fill ( final Color fll, final PImage target ) {

      return ZImage.fill(Color.toHexInt(fll), target);
   }

   /**
    * Fills an image with a gradient in place. The gradient is horizontal.
    * Colors are interpolated in RGB color space.
    *
    * @param grd    the gradient
    * @param target the target image
    *
    * @return the image
    */
   public static PImage fill ( final Gradient grd, final PImage target ) {

      target.loadPixels();
      final int w = target.width;
      final int[] pixels = target.pixels;
      final int len = pixels.length;
      final float wInv = 1.0f / ( w - 1.0f );
      for ( int i = 0; i < len; ++i ) {
         pixels[i] = Gradient.eval(grd, i % w * wInv);
      }
      target.updatePixels();
      return target;
   }

   /**
    * Fills an image with a color in place.
    *
    * @param fll    the fill color
    * @param target the target image
    *
    * @return the image
    */
   public static PImage fill ( final int fll, final PImage target ) {

      target.loadPixels();
      final int[] px = target.pixels;
      final int len = px.length;
      for ( int i = 0; i < len; ++i ) { px[i] = fll; }
      target.updatePixels();
      return target;
   }

   /**
    * Flips an image horizontally, on the x axis. Modifies the target image in
    * place.
    *
    * @param target the output image
    *
    * @return the flipped image
    */
   public static PImage flipX ( final PImage target ) {

      target.loadPixels();

      final int h = target.height;
      final int w = target.width;
      final int[] pixels = target.pixels;
      final int[] flipped = new int[pixels.length];

      for ( int i = 0, y = 0; y < h; ++y ) {
         final int yw = y * w;
         for ( int x = w - 1; x > -1; --x, ++i ) {
            flipped[yw + x] = pixels[i];
         }
      }

      target.pixels = flipped;
      target.updatePixels();

      return target;
   }

   /**
    * Flips an image vertically, on the y axis. Modifies the target image in
    * place.
    *
    * @param target the output image
    *
    * @return the flipped image
    */
   public static PImage flipY ( final PImage target ) {

      target.loadPixels();

      final int h = target.height;
      final int w = target.width;
      final int[] pixels = target.pixels;
      final int[] flipped = new int[pixels.length];

      for ( int i = 0, y = h - 1; y > -1; --y ) {
         final int yw = y * w;
         for ( int x = 0; x < w; ++x, ++i ) { flipped[yw + x] = pixels[i]; }
      }

      target.pixels = flipped;
      target.updatePixels();

      return target;
   }

   /**
    * Blits glyph images from a {@link PFont} onto a single image. <br>
    * Images created with this method do not have a reference to a parent
    * PApplet. Defaults to the fill color white.
    *
    * @param font the Processing font
    * @param text the string of text
    *
    * @return the new image
    */
   public static PImage fromText ( final PFont font, final String text ) {

      return ZImage.fromText(font, text, 0xffffffff, ZImage.DEFAULT_LEADING,
         ZImage.DEFAULT_KERNING, ZImage.DEFAULT_ALIGN);
   }

   /**
    * Blits glyph images from a {@link PFont} onto a single image. <br>
    * Images created with this method do not have a reference to a parent
    * PApplet.
    *
    * @param font    the Processing font
    * @param text    the string of text
    * @param fillClr the color
    *
    * @return the new image
    */
   public static PImage fromText ( final PFont font, final String text,
      final Color fillClr ) {

      return ZImage.fromText(font, text, Color.toHexInt(fillClr));
   }

   /**
    * Blits glyph images from a {@link PFont} onto a single image. The leading
    * and is measured in pixels; negative values are not allowed.<br>
    * <br>
    * Images created with this method do not have a reference to a parent
    * PApplet.
    *
    * @param font    the Processing font
    * @param text    the string of text
    * @param fillClr the color
    * @param leading spacing between lines
    *
    * @return the new image
    */
   public static PImage fromText ( final PFont font, final String text,
      final Color fillClr, final int leading ) {

      return ZImage.fromText(font, text, Color.toHexInt(fillClr), leading);
   }

   /**
    * Blits glyph images from a {@link PFont} onto a single image. The leading
    * and kerning are measured in pixels; negative values are not allowed<br>
    * <br>
    * Images created with this method do not have a reference to a parent
    * PApplet.
    *
    * @param font    the Processing font
    * @param text    the string of text
    * @param fillClr the color
    * @param leading spacing between lines
    * @param kerning spacing between characters
    *
    * @return the new image
    */
   public static PImage fromText ( final PFont font, final String text,
      final Color fillClr, final int leading, final int kerning ) {

      return ZImage.fromText(font, text, Color.toHexInt(fillClr), leading,
         kerning);
   }

   /**
    * Blits glyph images from a {@link PFont} onto a single image. The leading
    * and kerning are measured in pixels; negative values are not allowed. The
    * horizontal text alignment may be either center {@link PConstants#CENTER}
    * ( {@value PConstants#CENTER} ), right {@link PConstants#RIGHT} (
    * {@value PConstants#RIGHT} ) or left {@link PConstants#LEFT} (
    * {@value PConstants#LEFT} ).<br>
    * <br>
    * Images created with this method do not have a reference to a parent
    * PApplet.
    *
    * @param font      the Processing font
    * @param text      the string of text
    * @param fillClr   the color
    * @param leading   spacing between lines
    * @param kerning   spacing between characters
    * @param textAlign the horizontal alignment
    *
    * @return the new image
    */
   public static PImage fromText ( final PFont font, final String text,
      final Color fillClr, final int leading, final int kerning,
      final int textAlign ) {

      return ZImage.fromText(font, text, Color.toHexInt(fillClr), leading,
         kerning, textAlign);
   }

   /**
    * Blits glyph images from a {@link PFont} onto a single image. <br>
    * Images created with this method do not have a reference to a parent
    * PApplet.
    *
    * @param font    the Processing font
    * @param text    the string of text
    * @param fillClr the color
    *
    * @return the new image
    */
   public static PImage fromText ( final PFont font, final String text,
      final int fillClr ) {

      return ZImage.fromText(font, text, fillClr, ZImage.DEFAULT_LEADING,
         ZImage.DEFAULT_KERNING, ZImage.DEFAULT_ALIGN);
   }

   /**
    * Blits glyph images from a {@link PFont} onto a single image. The leading
    * and is measured in pixels; negative values are not allowed.<br>
    * <br>
    * Images created with this method do not have a reference to a parent
    * PApplet.
    *
    * @param font    the Processing font
    * @param text    the string of text
    * @param fillClr the color
    * @param leading spacing between lines
    *
    * @return the new image
    */
   public static PImage fromText ( final PFont font, final String text,
      final int fillClr, final int leading ) {

      return ZImage.fromText(font, text, fillClr, leading,
         ZImage.DEFAULT_KERNING, ZImage.DEFAULT_ALIGN);
   }

   /**
    * Blits glyph images from a {@link PFont} onto a single image. The leading
    * and kerning are measured in pixels; negative values are not allowed<br>
    * <br>
    * Images created with this method do not have a reference to a parent
    * PApplet.
    *
    * @param font    the Processing font
    * @param text    the string of text
    * @param fillClr the color
    * @param leading spacing between lines
    * @param kerning spacing between characters
    *
    * @return the new image
    */
   public static PImage fromText ( final PFont font, final String text,
      final int fillClr, final int leading, final int kerning ) {

      return ZImage.fromText(font, text, fillClr, leading, kerning,
         ZImage.DEFAULT_ALIGN);
   }

   /**
    * Blits glyph images from a {@link PFont} onto a single image. The leading
    * and kerning are measured in pixels; negative values are not allowed. The
    * horizontal text alignment may be either center {@link PConstants#CENTER}
    * ( {@value PConstants#CENTER} ), right {@link PConstants#RIGHT} (
    * {@value PConstants#RIGHT} ) or left {@link PConstants#LEFT} (
    * {@value PConstants#LEFT} ).<br>
    * <br>
    * Images created with this method do not have a reference to a parent
    * PApplet.
    *
    * @param font      the Processing font
    * @param text      the string of text
    * @param fillClr   the color
    * @param leading   spacing between lines
    * @param kerning   spacing between characters
    * @param textAlign the horizontal alignment
    *
    * @return the new image
    */
   public static PImage fromText ( final PFont font, final String text,
      final int fillClr, final int leading, final int kerning,
      final int textAlign ) {

      // TODO: Worry about pre-multiplying alpha?
      // Test with kinetic text example where txture is placed on torus.

      /*
       * Validate inputs: colors with no alpha not allowed; negative leading and
       * kerning not allowed; try to guard against empty Strings. Remove alpha
       * from tint; source image alpha will be used.
       */
      final String vTxt = text.trim();
      if ( vTxt.isEmpty() ) { return new PImage(32, 32, PConstants.ARGB, 1); }
      final int vLead = leading < 0 ? 1 : leading + 1;
      final int vKern = kerning < 0 ? 0 : kerning;
      final int vClr = ( fillClr >> 0x18 & 0xff ) != 0 ? 0x00ffffff & fillClr
         : 0x00ffffff;

      /* Carriage returns, or line breaks, have 3 variants: \r, \n, or \r\n . */
      final Pattern patternLnBr = Pattern.compile("[\n|\r]+");
      final String[] linesSplit = patternLnBr.split(vTxt, 0);
      final int lineCount = linesSplit.length;

      /* 3D array: lines contain words which contain letters. */
      final char[][][] characters = new char[lineCount][][];
      final Pattern patternSpace = Pattern.compile("\\s+");
      for ( int i = 0; i < lineCount; ++i ) {
         final String[] words = patternSpace.split(linesSplit[i], 0);
         final int charCount = words.length;
         final char[][] charLine = characters[i] = new char[charCount][];
         for ( int j = 0; j < charCount; ++j ) {
            charLine[j] = words[j].toCharArray();
         }
      }

      /* Determine width of a space. */
      final int fontSize = font.getSize();

      final Glyph whiteSpace = font.getGlyph('-');
      final int spaceWidth = whiteSpace != null ? whiteSpace.width
         : ( int ) ( fontSize * IUtils.ONE_THIRD );

      /*
       * If a line contains only a space, then maxHeight below may wind up as
       * zero, and need to be replaced with a blank line.
       */
      final Glyph emptyLine = font.getGlyph('E');
      final int defaultHeight = emptyLine != null ? emptyLine.height : fontSize;

      /*
       * The last line's descenders are chopped off if padding isn't added to
       * the bottom of the image.
       */
      int lastRowPadding = 0;

      /* Total height and max width will decide the image's dimensions. */
      final int[] lineHeights = new int[lineCount];
      final int[] lineWidths = new int[lineCount];
      int hTotal = 0;
      int wMax = 0;

      /* Loop through lines. */
      final Glyph[][][] glyphs = new Glyph[lineCount][][];
      for ( int i = 0; i < lineCount; ++i ) {
         final char[][] charLine = characters[i];
         final int wordCount = charLine.length;
         final Glyph[][] glyphLine = new Glyph[wordCount][];
         glyphs[i] = glyphLine;

         int sumWidths = 0;
         int maxHeight = 0;
         int maxDescent = 0;

         /* Loop through words. */
         for ( int j = 0; j < wordCount; ++j ) {
            final char[] charWord = charLine[j];
            final int charCount = charWord.length;
            final Glyph[] glyphWord = new Glyph[charCount];
            glyphLine[j] = glyphWord;

            /* Loop through letters. */
            for ( int k = 0; k < charCount; ++k ) {
               final char character = charWord[k];
               final Glyph glyph = font.getGlyph(character);
               glyphWord[k] = glyph;

               if ( glyph != null ) {

                  /*
                   * All values are considered from the image's top-left corner.
                   * Top extent is the amount to move down from the top edge to
                   * find the space occupied by the glyph. Left extent is the
                   * amount to move left from the left edge to find the glyph.
                   * See {@link PFont} for diagram.
                   */
                  final int height = glyph.height + vLead;
                  final int glyphDescent = height - glyph.topExtent;

                  /*
                   * The height of a line is determined by its tallest glyph;
                   * the width of a line is the sum of each glyph's width, plus
                   * kerning, plus left extents.
                   */
                  maxHeight = height > maxHeight ? height : maxHeight;
                  sumWidths += glyph.width + vKern + glyph.leftExtent;

                  maxDescent = glyphDescent > maxDescent ? glyphDescent
                     : maxDescent;
                  lastRowPadding = maxDescent;
               }
            }

            /* Add a space between words. */
            sumWidths += spaceWidth + vKern;
         }

         /* maxHeight may be initial value. */
         maxHeight = maxHeight < 1 ? defaultHeight : maxHeight;
         lineHeights[i] = maxHeight;
         hTotal += maxHeight;

         lineWidths[i] = sumWidths;
         wMax = sumWidths > wMax ? sumWidths : wMax;
      }

      hTotal += lastRowPadding;

      /*
       * Offset the xCursor's initial position by an offset depending on
       * horizontal alignment.
       */
      final int[] lineOffsets = new int[lineCount];
      switch ( textAlign ) {

         case PConstants.CENTER:

            for ( int i = 0; i < lineCount; ++i ) {
               lineOffsets[i] = ( wMax - lineWidths[i] ) / 2;
            }

            break;

         case PConstants.RIGHT:

            for ( int i = 0; i < lineCount; ++i ) {
               lineOffsets[i] = wMax - lineWidths[i];
            }

            break;

         case PConstants.LEFT:

         default:

      }

      /* wMax may have been left at zero initial value. */
      wMax = wMax < 1 ? 32 : wMax;
      final PImage target = new PImage(wMax, hTotal, PConstants.ARGB, 1);
      target.loadPixels();
      final int[] trgPx = target.pixels;
      int yCursor = 0;

      /* Loop through lines. */
      for ( int i = 0; i < lineCount; ++i ) {
         final Glyph[][] glyphLine = glyphs[i];
         final int wordCount = glyphLine.length;
         final int lineHeight = lineHeights[i];

         /* Reset xCursor every carriage return. */
         int xCursor = lineOffsets[i];

         /* Loop through words. */
         for ( int j = 0; j < wordCount; ++j ) {
            final Glyph[] glyphWord = glyphLine[j];
            final int charCount = glyphWord.length;

            /* Loop through letters. */
            for ( int k = 0; k < charCount; ++k ) {
               final Glyph glyph = glyphWord[k];

               if ( glyph != null ) {
                  xCursor += glyph.leftExtent;
                  final PImage source = glyph.image;

                  if ( source != null ) {
                     // target.loadPixels();
                     source.loadPixels();

                     /*
                      * {@link PImage#set(int, int, PImage)} cannot be used
                      * because glyph descenders or ascenders may overlap.
                      */
                     final int wSrc = source.pixelWidth;
                     final int[] srcPx = source.pixels;
                     final int srcLen = srcPx.length;
                     final int yStart = yCursor + lineHeight - glyph.topExtent;

                     /*
                      * Loop through source image height and width. Target index
                      * is manually calculated in the inner loop using the
                      * formulae index = x + y * width and x = index % width, y
                      * = index / width.
                      */
                     for ( int idxSrc = 0; idxSrc < srcLen; ++idxSrc ) {

                        /*
                         * Shift source image from grey scale, stored in the
                         * blue channel, to ARGB. Composite target and source,
                         * then composite in tint color.
                         */
                        trgPx[ ( yStart + idxSrc / wSrc ) * wMax + xCursor
                           + idxSrc % wSrc] |= srcPx[idxSrc] << 0x18 | vClr;
                     }

                     // target.updatePixels(xCursor, yCursor, wSrc, hSrc);
                  }
                  /* End of null check for glyph image. */
                  xCursor += glyph.width + vKern;
               }
               /* End of null check for glyph. */
            }
            /* End of letters loop. */
            xCursor += spaceWidth + vKern;
         }
         /* End of words loop. */
         yCursor += lineHeight;
      }
      /* End of lines loop. */

      target.updatePixels();
      return target;
   }

   /**
    * Adjusts gamma of an image. Raises all color channels to the power given.
    *
    * @param source the source image
    * @param gamma  the gamma correction
    *
    * @return the image
    */
   public static PImage gammaAdjust ( final PImage source, final float gamma ) {

      source.loadPixels();

      final int[] px = source.pixels;
      final int len = px.length;
      final double gd = gamma;

      for ( int i = 0; i < len; ++i ) {
         final int c = px[i];

         final double r = ( c >> 0x10 & 0xff ) * IUtils.ONE_255_D;
         final double g = ( c >> 0x08 & 0xff ) * IUtils.ONE_255_D;
         final double b = ( c & 0xff ) * IUtils.ONE_255_D;

         /* @formatter:off */
         px[i] = c & 0xff000000 |
            ( int ) ( Math.pow(r, gd) * 255.0d + 0.5d ) << 0x10 |
            ( int ) ( Math.pow(g, gd) * 255.0d + 0.5d ) << 0x08 |
            ( int ) ( Math.pow(b, gd) * 255.0d + 0.5d );
         /* @formatter:on */
      }

      source.updatePixels();
      return source;
   }

   /**
    * Generates a linear gradient from an origin point to a destination point.
    * The origin and destination should be in the range [-1.0, 1.0]. The
    * gradient factor is clamped to [0.0, 1.0].
    *
    * @param xOrigin the origin x coordinate
    * @param yOrigin the origin y coordinate
    * @param xDest   the destination x coordinate
    * @param yDest   the destination y coordinate
    * @param grd     the gradient
    * @param target  the output image
    *
    * @return the image
    */
   public static PImage linear ( final float xOrigin, final float yOrigin,
      final float xDest, final float yDest, final Gradient grd,
      final PImage target ) {

      target.loadPixels();

      final int w = target.width;
      final int[] px = target.pixels;
      final int len = px.length;

      final float hInv = 1.0f / ( target.height - 1.0f );
      final float wInv = 1.0f / ( w - 1.0f );

      final float bx = xOrigin - xDest;
      final float by = yOrigin - yDest;
      final float bbInv = 1.0f / Utils.max(IUtils.EPSILON, bx * bx + by * by);

      for ( int i = 0; i < len; ++i ) {
         final float yn = hInv * ( i / w );
         final float xn = wInv * ( i % w );

         px[i] = Gradient.eval(grd, Utils.clamp01( ( ( xOrigin - ( xn + xn
            - 1.0f ) ) * bx + ( yOrigin - ( 1.0f - ( yn + yn ) ) ) * by )
            * bbInv));
      }

      target.updatePixels();
      return target;
   }

   /**
    * Generates a linear gradient from an origin point to a destination point.
    * The value is clamped to a range [0.0, 1.0] .
    *
    * @param origin the origin
    * @param dest   the destination
    * @param grd    the gradient
    * @param target the output image
    *
    * @return the image
    */
   public static PImage linear ( final Vec2 origin, final Vec2 dest,
      final Gradient grd, final PImage target ) {

      return ZImage.linear(origin.x, origin.y, dest.x, dest.y, grd, target);
   }

   /**
    * Multiplies the red, green and blue channels of each pixel in the image
    * by its alpha channel.
    *
    * @param source the source image
    *
    * @return the pre-multiplied image
    */
   public static PImage preMul ( final PImage source ) {

      source.loadPixels();
      final int[] px = source.pixels;
      final int len = px.length;
      for ( int i = 0; i < len; ++i ) {
         final int ai = px[i] >> 0x18 & 0xff;
         if ( ai < 1 ) {
            px[i] = 0x00000000;
         } else if ( ai < 0xff ) {
            final int ri = px[i] >> 0x10 & 0xff;
            final int gi = px[i] >> 0x08 & 0xff;
            final int bi = px[i] & 0xff;

            final float divisor = ai * IUtils.ONE_255;

            px[i] = ai << 0x18 | ( int ) ( ri * divisor + 0.5f ) << 0x10
               | ( int ) ( gi * divisor + 0.5f ) << 0x08 | ( int ) ( bi
                  * divisor + 0.5f );
         }
      }
      source.updatePixels();
      return source;
   }

   /**
    * Generates a radial gradient. This does not account for aspect ratio, so
    * an image that is not 1:1 will result in an ellipsoid.
    *
    * @param xOrigin the x coordinate
    * @param yOrigin the y coordinate
    * @param radius  the radius
    * @param grd     the gradient
    * @param target  the output image
    *
    * @return the image
    */
   public static PImage radial ( final float xOrigin, final float yOrigin,
      final float radius, final Gradient grd, final PImage target ) {

      target.loadPixels();

      final int w = target.width;
      final int[] px = target.pixels;
      final int len = px.length;

      final float hInv = 1.0f / ( target.height - 1.0f );
      final float wInv = 1.0f / ( w - 1.0f );

      final float r2 = radius + radius;
      final float invrsq = 1.0f / Utils.max(IUtils.EPSILON, r2 * r2);

      for ( int i = 0; i < len; ++i ) {
         final float yn = hInv * ( i / w );
         final float xn = wInv * ( i % w );

         final float ay = yOrigin - ( 1.0f - ( yn + yn ) );
         final float ax = xOrigin - ( xn + xn - 1.0f );

         px[i] = Gradient.eval(grd, 1.0f - ( ax * ax + ay * ay ) * invrsq);
      }

      target.updatePixels();
      return target;
   }

   /**
    * Generates a radial gradient. This does not account for aspect ratio, so
    * an image that is not 1:1 will result in an ellipsoid.
    *
    * @param origin the origin
    * @param radius the radius
    * @param grd    the gradient
    * @param target the output image
    *
    * @return the image
    */
   public static PImage radial ( final Vec2 origin, final float radius,
      final Gradient grd, final PImage target ) {

      return ZImage.radial(origin.x, origin.y, radius, grd, target);
   }

   /**
    * Generates a diagnostic image where a pixel's location on the x-axis
    * correlates to the color red; on the y-axis, to green.
    *
    * @param target the output image
    *
    * @return the image
    */
   public static PImage rgb ( final PImage target ) {

      target.loadPixels();

      final int[] px = target.pixels;
      final int len = px.length;
      final int w = target.width;

      final float hInv = 0xff / ( target.height - 1.0f );
      final float wInv = 0xff / ( w - 1.0f );

      for ( int i = 0; i < len; ++i ) {
         px[i] = 0xff000080 | ( int ) ( 0.5f + wInv * ( i % w ) ) << 0x10
            | ( int ) ( 255.5f - hInv * ( i / w ) ) << 0x08;
      }

      target.updatePixels();
      return target;
   }

   /**
    * Tints an image to a color.
    *
    * @param source  the source image
    * @param tintClr the tint color
    *
    * @return the image
    */
   public static PImage tint ( final PImage source, final Color tintClr ) {

      return ZImage.tint(source, Color.toHexInt(tintClr), 0.5f);
   }

   /**
    * Tints an image to a color by a factor in [0.0, 1.0] .
    *
    * @param source  the source image
    * @param tintClr the tint color
    * @param fac     the factor
    *
    * @return the image
    */
   public static PImage tint ( final PImage source, final Color tintClr,
      final float fac ) {

      return ZImage.tint(source, Color.toHexInt(tintClr), fac);
   }

   /**
    * Tints an image to a color by a factor in [0.0, 1.0] .
    *
    * @param source  the source image
    * @param tintClr the tint color
    *
    * @return the image
    */
   public static PImage tint ( final PImage source, final int tintClr ) {

      return ZImage.tint(source, tintClr, 0.5f);
   }

   /**
    * Tints an image to a color by a factor in [0.0, 1.0] . For images with
    * alpha, the minimum alpha between the source and tint color are used.
    *
    * @param source  the source image
    * @param tintClr the tint color
    * @param fac     the factor
    *
    * @return the image
    */
   public static PImage tint ( final PImage source, final int tintClr,
      final float fac ) {

      /* Do not optimize until you settle on an appropriate tinting formula. */

      /* Right operand. Decompose tint color. */
      final int ya = tintClr >> 0x18 & 0xff;
      final int yr = tintClr >> 0x10 & 0xff;
      final int yg = tintClr >> 0x08 & 0xff;
      final int yb = tintClr & 0xff;

      /* Convert from [0, 255] to [0.0, 1.0] . */
      final float yaf = ya * IUtils.ONE_255;
      final float yrf = yr * IUtils.ONE_255;
      final float ygf = yg * IUtils.ONE_255;
      final float ybf = yb * IUtils.ONE_255;

      final int srcFmt = source.format;
      final float t = Utils.clamp01(fac);
      final float u = 1.0f - t;

      source.loadPixels();
      final int[] pixels = source.pixels;
      final int len = pixels.length;

      switch ( srcFmt ) {

         case PConstants.ALPHA:

            final int trgb = 0x00ffffff & tintClr;
            for ( int i = 0; i < len; ++i ) {
               final float xaf = pixels[i] * IUtils.ONE_255;
               final float zaf = Utils.min(xaf, yaf);
               pixels[i] = ( int ) ( zaf * 0xff + 0.5f ) << 0x18 | trgb;
            }

            break;

         case PConstants.RGB:

            for ( int i = 0; i < len; ++i ) {
               final int rgb = pixels[i];

               /* Left operand. Decompose color. */
               final float xrf = IUtils.ONE_255 * ( rgb >> 0x10 & 0xff );
               final float xgf = IUtils.ONE_255 * ( rgb >> 0x08 & 0xff );
               final float xbf = IUtils.ONE_255 * ( rgb & 0xff );

               /* Lerp from left to right by factor t. */
               final float zrf = u * xrf + t * yrf;
               final float zgf = u * xgf + t * ygf;
               final float zbf = u * xbf + t * ybf;

               /* @formatter:off */
               pixels[i] = ya << 0x18 |
                           ( int ) ( zrf * 0xff + 0.5f ) << 0x10 |
                           ( int ) ( zgf * 0xff + 0.5f ) << 0x08  |
                           ( int ) ( zbf * 0xff + 0.5f );
               /* @formatter:on */
            }

            break;

         case PConstants.ARGB:

            for ( int i = 0; i < len; ++i ) {
               final int rgb = pixels[i];

               /* Left operand. Decompose color. */
               final float xaf = IUtils.ONE_255 * ( rgb >> 0x18 & 0xff );
               final float xrf = IUtils.ONE_255 * ( rgb >> 0x10 & 0xff );
               final float xgf = IUtils.ONE_255 * ( rgb >> 0x08 & 0xff );
               final float xbf = IUtils.ONE_255 * ( rgb & 0xff );

               /* Lerp from left to right by factor t. */
               final float zaf = Utils.min(xaf, yaf);
               final float zrf = u * xrf + t * yrf;
               final float zgf = u * xgf + t * ygf;
               final float zbf = u * xbf + t * ybf;

               /* @formatter:off */
               pixels[i] = ( int ) ( zaf * 0xff + 0.5f ) << 0x18 |
                           ( int ) ( zrf * 0xff + 0.5f ) << 0x10 |
                           ( int ) ( zgf * 0xff + 0.5f ) << 0x08  |
                           ( int ) ( zbf * 0xff + 0.5f );
               /* @formatter:on */
            }

            break;

         default:

      }

      source.updatePixels();
      source.format = PConstants.ARGB;

      return source;
   }

   /**
    * Converts an image to gray scale. Does not reduce the number of bytes
    * used to store color; all colors remain 32-bit.
    *
    * @param target the output image
    *
    * @return the image
    */
   public static PImage toGrayscale ( final PImage target ) {

      return toGrayscale(target, false);
   }

   /**
    * Converts an image to gray scale, with the option of stretching its
    * contrast, i.e., normalizing its grays according to a minimum and
    * maximum. Does not reduce the number of bytes used to store color; all
    * colors remain 32-bit. Uses rec. 709 luminance.
    *
    * @param target    the output image
    * @param normalize the normalization flag
    *
    * @return the image
    */
   public static PImage toGrayscale ( final PImage target,
      final boolean normalize ) {

      target.loadPixels();
      final int[] px = target.pixels;
      final int len = px.length;

      if ( normalize ) {

         /* Find minimum and maximum luminance. */
         float lumMin = 1.0f;
         float lumMax = 0.0f;
         final float[] lums = new float[len];
         for ( int i = 0; i < len; ++i ) {
            final float lum = Color.luminance(px[i]);
            if ( lum < lumMin ) { lumMin = lum; }
            if ( lum > lumMax ) { lumMax = lum; }
            lums[i] = lum;
         }

         /* Map luminance to [0.0, 1.0] from [minimum, maximum]. */
         final float lumRange = lumMax - lumMin;
         final float denom = lumRange != 0.0f ? 1.0f / lumRange : 0.0f;
         for ( int i = 0; i < len; ++i ) {
            final float lum = ( lums[i] - lumMin ) * denom;
            final int gi = ( int ) ( lum * 0xff + 0.5f );
            px[i] = px[i] & 0xff000000 | gi << 0x10 | gi << 0x08 | gi;
         }

      } else {
         for ( int i = 0; i < len; ++i ) {
            final int gi = ( int ) ( Color.luminance(px[i]) * 0xff + 0.5f );
            px[i] = px[i] & 0xff000000 | gi << 0x10 | gi << 0x08 | gi;
         }
      }

      target.updatePixels();
      return target;
   }

   /**
    * Returns a string representation of an image, including its format,
    * width, height and pixel density.
    *
    * @param img the PImage
    *
    * @return the string
    */
   public static String toString ( final PImage img ) {

      final StringBuilder sb = new StringBuilder(64);
      sb.append("{ format: ");
      sb.append(img.format);
      sb.append(", width: ");
      sb.append(Utils.toPadded(img.width, 4));
      sb.append(", height: ");
      sb.append(Utils.toPadded(img.height, 4));
      sb.append(", pixelDensity: ");
      sb.append(img.pixelDensity);
      sb.append(' ');
      sb.append('}');
      return sb.toString();
   }

   /**
    * Blits a source image's pixels onto a target image's pixels, using
    * integer floor modulo to wrap the source image. The source image can be
    * offset horizontally and/or vertically, creating the illusion of
    * parallax.
    *
    * @param source  source pixel array
    * @param wSource source image width
    * @param hSource source image height
    * @param target  target pixel array
    * @param wTarget target image width
    * @param dx      horizontal pixel offset
    * @param dy      vertical pixel offset
    *
    * @return the target pixels
    */
   public static int[] wrap ( final int[] source, final int wSource,
      final int hSource, final int[] target, final int wTarget, final int dx,
      final int dy ) {

      if ( wSource < 1 || hSource < 1 ) { return target; }

      final int trgLen = target.length;
      for ( int i = 0; i < trgLen; ++i ) {
         int ymod = ( i / wTarget - dy ) % hSource;
         if ( ( ymod ^ hSource ) < 0 && ymod != 0 ) { ymod += hSource; }

         int xmod = ( i % wTarget + dx ) % wSource;
         if ( ( xmod ^ wSource ) < 0 && xmod != 0 ) { xmod += wSource; }

         target[i] = source[xmod + wSource * ymod];
      }

      return target;
   }

   /**
    * Blits a source image's pixels onto a target image's pixels, using
    * integer floor modulo to wrap the source image. The source image can be
    * offset horizontally and/or vertically, creating the illusion of
    * parallax.
    *
    * @param source the source image
    * @param target the target image
    * @param dx     horizontal pixel offset
    * @param dy     vertical pixel offset
    *
    * @return the image
    */
   public static PImage wrap ( final PImage source, final PImage target,
      final float dx, final float dy ) {

      return ZImage.wrap(source, target, ( int ) dx, ( int ) dy);
   }

   /**
    * Blits a source image's pixels onto a target image's pixels, using
    * integer floor modulo to wrap the source image. The source image can be
    * offset horizontally and/or vertically, creating the illusion of
    * parallax.
    *
    * @param source the source image
    * @param target the target image
    * @param dx     horizontal pixel offset
    * @param dy     vertical pixel offset
    *
    * @return the image
    */
   public static PImage wrap ( final PImage source, final PImage target,
      final int dx, final int dy ) {

      source.loadPixels();
      target.loadPixels();
      ZImage.wrap(source.pixels, source.pixelWidth, source.pixelHeight,
         target.pixels, target.pixelWidth, dx, dy);
      target.updatePixels();
      // source.updatePixels();
      return target;
   }

   /**
    * Blits a source image's pixels onto a target image's pixels, using
    * integer floor modulo to wrap the source image. The source image can be
    * offset horizontally and/or vertically, creating the illusion of
    * parallax.
    *
    * @param source the source image
    * @param target the target image
    * @param d      pixel offset
    *
    * @return the image
    */
   public static PImage wrap ( final PImage source, final PImage target,
      final Vec2 d ) {

      return ZImage.wrap(source, target, ( int ) d.x, ( int ) d.y);
   }

}

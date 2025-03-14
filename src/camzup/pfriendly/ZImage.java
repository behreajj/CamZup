package camzup.pfriendly;

import java.util.Arrays;
import java.util.regex.Pattern;

import camzup.core.IUtils;
import camzup.core.Lab;
import camzup.core.MaterialSolid;
import camzup.core.Mesh2;
import camzup.core.Pixels;
import camzup.core.PolyType;
import camzup.core.Rgb;
import camzup.core.Utils.TriFunction;
import camzup.core.Vec4;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import processing.core.PFont.Glyph;
import processing.core.PGraphics;
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
    * Tests this image for equivalence with another object.
    *
    * @param obj the object
    *
    * @return the equivalence
    */
   @Override
   public boolean equals ( final Object obj ) {

      if ( this == obj ) { return true; }
      if ( obj == null || this.getClass() != obj.getClass() ) { return false; }
      final PImage p = ( PImage ) obj;
      if ( this.format != p.format || this.width != p.width || this.pixelDensity
         != p.pixelDensity || this.pixelHeight != p.pixelHeight
         || this.pixelWidth != p.pixelWidth || this.height != p.height ) {
         return false;
      }

      final int[] apx = this.pixels;
      final int[] bpx = p.pixels;
      if ( apx == bpx ) { return true; }
      if ( apx == null || bpx == null ) { return false; }

      final int len = apx.length;
      if ( bpx.length != len ) { return false; }

      for ( int i = 0; i < len; ++i ) {
         if ( apx[i] != bpx[i] ) { return false; }
      }
      return true;
   }

   /**
    * Returns a hash code for this image.
    *
    * @return the hash code
    */
   @Override
   public int hashCode ( ) {

      final int prime = 31;
      int result = 1;
      result = prime * result + this.format;
      result = prime * result + this.pixelDensity;
      result = prime * result + this.pixelHeight;
      result = prime * result + this.pixelWidth;
      result = prime * result + this.height;
      result = prime * result + this.width;
      return prime * result + Arrays.hashCode(this.pixels);
   }

   /**
    * Returns a string representation of an image, including its format,
    * width, height and pixel density.
    *
    * @return the string
    */
   @Override
   public String toString ( ) { return ZImage.toString(this); }

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
    * Convert an image in the {@link PConstants#ALPHA} format to an image in
    * the {@link PConstants#ARGB} format.
    *
    * @param image the image
    *
    * @return the conversion
    */
   public static PImage alphaToArgb ( final PImage image ) {

      /* Depart from (source, target) signature for this function. */
      if ( image.format != PConstants.ALPHA ) { return image; }

      image.loadPixels();
      final int[] px = image.pixels;
      final int len = px.length;
      for ( int i = 0; i < len; ++i ) {
         final int a = px[i];
         px[i] = a << 0x18 | a << 0x10 | a << 0x08 | a;
      }
      image.format = PConstants.ARGB;
      image.updatePixels();
      return image;
   }

   /**
    * Blurs an image by averaging each color with its neighbors in 8
    * directions. The step determines the size of the kernel, where the
    * minimum step of 1 will make a 3x3, 9 pixel kernel.
    *
    * @param source the source image
    * @param step   the kernel step
    * @param target the target image
    *
    * @return the blurred image
    *
    * @see Pixels#blurBoxLab(int[], int, int, int, int[])
    */
   public static PImage blur ( final PImage source, final int step,
      final PImage target ) {

      if ( target instanceof PGraphics ) {
         System.err.println("Do not use PGraphics with this method.");
         return target;
      }

      source.loadPixels();
      target.loadPixels();
      final int[] pxSrc = source.pixels;
      final int wSrc = source.pixelWidth;
      final int hSrc = source.pixelHeight;
      target.pixels = Pixels.blurBoxLab(pxSrc, wSrc, hSrc, step,
         new int[pxSrc.length]);
      target.format = source.format;
      target.pixelDensity = source.pixelDensity;
      target.pixelWidth = wSrc;
      target.pixelHeight = hSrc;
      target.width = source.width;
      target.height = source.height;
      target.updatePixels();

      return target;
   }

   /**
    * Copies a source image's pixels and properties to a target. If the target
    * is a {@link PGraphics}, then wraps the source image without changing the
    * target's dimensions.
    *
    * @param source the source image
    * @param target the target image
    *
    * @return the copied image.
    *
    * @see ZImage#wrap(PImage, int, int, PImage)
    */
   public static PImage copy ( final PImage source, final PImage target ) {

      if ( source == target ) { return target; }

      /*
       * An image can have dimensions of -1, -1 when invalid. See
       * https://processing.org/reference/loadImage_.html
       */
      if ( source == null || source.pixelWidth < 1 || source.pixelHeight < 1 ) {
         return ZImage.fill(0xffffffff, target);
      }

      if ( target instanceof PGraphics && ( source.pixelWidth
         != target.pixelWidth || source.pixelHeight != target.pixelHeight ) ) {
         return ZImage.wrap(source, 0, 0, target);
      }

      source.loadPixels();
      target.loadPixels();
      final int len = source.pixels.length;
      target.pixels = new int[len];
      System.arraycopy(source.pixels, 0, target.pixels, 0, len);
      target.format = source.format;
      target.pixelDensity = source.pixelDensity;
      target.pixelWidth = source.pixelWidth;
      target.pixelHeight = source.pixelHeight;
      target.width = source.width;
      target.height = source.height;
      target.updatePixels();
      return target;
   }

   /**
    * Fills an image with a color in place. The color is expected to be a
    * 32-bit color integer.
    *
    * @param c      the fill color
    * @param target the target image
    *
    * @return the image
    *
    * @see ZImage#fill(int, int[])
    */
   public static PImage fill ( final int c, final PImage target ) {

      target.loadPixels();
      ZImage.fill(c, target.pixels);
      target.format = PConstants.ARGB;
      target.updatePixels();

      return target;
   }

   /**
    * Fills an image in place with a color.
    *
    * @param target the target image
    * @param c      the fill color
    *
    * @return the image
    */
   public static PImage fill ( final Rgb c, final PImage target ) {

      return ZImage.fill(c.toHexIntSat(), target);
   }

   /**
    * Filters an image. The function delegate is expected to filter the color
    * according to an upper and lower bound. Both bounds should be inclusive.
    *
    * @param source the source image
    * @param lb     the lower bound
    * @param ub     the upper bound
    * @param f      the filter function
    * @param target the target image
    *
    * @return the filtered image
    */
   public static PImage filter ( final PImage source, final float lb,
      final float ub, final TriFunction < Integer, Float, Float, Boolean > f,
      final PImage target ) {

      if ( target instanceof PGraphics ) {
         System.err.println("Do not use PGraphics with this method.");
         return target;
      }

      source.loadPixels();
      target.loadPixels();

      final int[] srcPixels = source.pixels;
      final int[] trgPixels = new int[srcPixels.length];
      final int[] indices = Pixels.filter(srcPixels, lb, ub, f);
      final int idcsLen = indices.length;
      for ( int i = 0; i < idcsLen; ++i ) {
         final int j = indices[i];
         trgPixels[j] = srcPixels[j];
      }

      target.pixels = trgPixels;
      target.format = source.format;
      target.pixelDensity = source.pixelDensity;
      target.pixelWidth = source.pixelWidth;
      target.pixelHeight = source.pixelHeight;
      target.width = source.width;
      target.height = source.height;
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

      final int fontSize = font.getSize();

      /* Determine width of a space. */
      final Glyph whiteSpace = font.getGlyph('i');
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
                  if ( height > maxHeight ) { maxHeight = height; }
                  sumWidths += glyph.width + vKern + glyph.leftExtent;
                  if ( glyphDescent > maxDescent ) {
                     maxDescent = glyphDescent;
                  }
                  lastRowPadding = maxDescent;
               }
            }

            /* Add a space between words. */
            sumWidths += spaceWidth + vKern;
         }

         /* maxHeight may be initial value. */
         if ( maxHeight < 1 ) { maxHeight = defaultHeight; }
         lineHeights[i] = maxHeight;
         hTotal += maxHeight;

         lineWidths[i] = sumWidths;
         if ( sumWidths > wMax ) { wMax = sumWidths; }
      }

      hTotal += lastRowPadding;

      /*
       * Offset the xCursor's initial position depending on horizontal
       * alignment.
       */
      final int[] lineOffsets = new int[lineCount];
      switch ( textAlign ) {

         case PConstants.CENTER: /* 3 */

            for ( int i = 0; i < lineCount; ++i ) {
               lineOffsets[i] = ( wMax - lineWidths[i] ) / 2;
            }

            break;

         case PConstants.RIGHT: /* 39 */

            for ( int i = 0; i < lineCount; ++i ) {
               lineOffsets[i] = wMax - lineWidths[i];
            }

            break;

         case PConstants.LEFT: /* 37 */

         default:

      }

      /* wMax may have been left at zero initial value. */
      wMax = wMax < 1 ? 32 : wMax;
      final PImage target = new PImage(wMax, hTotal, PConstants.ARGB, 1);
      target.loadPixels();
      final int[] pxTrg = target.pixels;
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
                     source.loadPixels();

                     /*
                      * {@link PImage#set(int, int, PImage)} cannot be used
                      * because glyph descenders or ascenders may overlap.
                      */
                     final int wSrc = source.pixelWidth;
                     final int[] pxSrc = source.pixels;
                     final int srcLen = pxSrc.length;
                     final int yStart = yCursor + lineHeight - glyph.topExtent;

                     /*
                      * Loop through source image height and width. Target index
                      * is manually calculated in the inner loop using the
                      * formulae index = x + y * width and x = index % width, y
                      * = index / width.
                      */
                     for ( int idxSrc = 0; idxSrc < srcLen; ++idxSrc ) {

                        /*
                         * Shift source image from gray scale, stored in the
                         * blue channel, to ARGB. Composite target and source,
                         * then composite in tint color.
                         */
                        pxTrg[ ( yStart + idxSrc / wSrc ) * wMax + xCursor
                           + idxSrc % wSrc] |= pxSrc[idxSrc] << 0x18 | vClr;
                     }
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
      final Rgb fillClr ) {

      return ZImage.fromText(font, text, fillClr.toHexIntSat());
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
      final Rgb fillClr, final int leading ) {

      return ZImage.fromText(font, text, fillClr.toHexIntSat(), leading);
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
      final Rgb fillClr, final int leading, final int kerning ) {

      return ZImage.fromText(font, text, fillClr.toHexIntSat(), leading,
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
      final Rgb fillClr, final int leading, final int kerning,
      final int textAlign ) {

      return ZImage.fromText(font, text, fillClr.toHexIntSat(), leading,
         kerning, textAlign);
   }

   /**
    * Multiplies the red, green and blue channels of each pixel in a source
    * image by the alpha channel.
    *
    * @param source the source image
    * @param target the target image
    *
    * @return the image
    *
    * @see ZImage#premul(int[], int[])
    */
   public static PImage premul ( final PImage source, final PImage target ) {

      if ( source == target ) {
         target.loadPixels();
         ZImage.premul(target.pixels, target.pixels);
         target.format = PConstants.ARGB;
         target.updatePixels();
         return target;
      }

      if ( target instanceof PGraphics ) {
         System.err.println("Do not use PGraphics with this method.");
         return target;
      }

      source.loadPixels();
      target.loadPixels();
      final int w = source.pixelWidth;
      final int h = source.pixelHeight;
      final int[] pxSrc = source.pixels;
      target.pixels = ZImage.premul(pxSrc, new int[pxSrc.length]);
      target.format = PConstants.ARGB;
      target.pixelDensity = source.pixelDensity;
      target.pixelWidth = w;
      target.pixelHeight = h;
      target.width = source.width;
      target.height = source.height;
      target.updatePixels();

      return target;
   }

   /**
    * Tints an image to a color by a factor. If the preserveLight flag is
    * true, the source image's original lightness is retained. The image's
    * {@link Pixels#SHADOWS}, {@link Pixels#MIDTONES} and/or
    * {@link Pixels#HIGHLIGHTS} may be targeted with an integer flag.
    *
    * @param source        the source image
    * @param tint          the tint color
    * @param fac           the factor
    * @param preserveLight the preserve light flag
    * @param toneFlag      the tone flags
    * @param target        the target image
    *
    * @return the tinted image
    */
   public static PImage tint ( final PImage source, final int tint,
      final float fac, final boolean preserveLight, final int toneFlag,
      final PImage target ) {

      final int tintVrf = source.format == PConstants.RGB ? 0xff000000 | tint
         : tint;
      final Rgb tintsRgb = new Rgb();
      final Rgb tintlRgb = new Rgb();
      final Vec4 tintXyz = new Vec4();
      final Lab tintLab = new Lab();
      Rgb.fromHex(tintVrf, tintsRgb);
      Rgb.sRgbToSrLab2(tintsRgb, tintLab, tintXyz, tintlRgb);

      if ( source == target ) {
         target.loadPixels();
         Pixels.tintLab(target.pixels, tintLab, fac, preserveLight, toneFlag,
            target.pixels);
         target.updatePixels();
         return target;
      }

      if ( target instanceof PGraphics ) {
         System.err.println("Do not use PGraphics with this method.");
         return target;
      }

      source.loadPixels();
      target.loadPixels();
      final int[] pxSrc = source.pixels;
      target.pixels = Pixels.tintLab(pxSrc, tintLab, fac, preserveLight,
         toneFlag, new int[pxSrc.length]);
      target.format = source.format;
      target.pixelDensity = source.pixelDensity;
      target.pixelWidth = source.pixelWidth;
      target.pixelHeight = source.pixelHeight;
      target.width = source.width;
      target.height = source.height;
      target.updatePixels();

      return target;
   }

   /**
    * Tints an image to a color by a factor. If the preserveLight flag is
    * true, the source image's original lightness is retained.
    *
    * @param source        the source image
    * @param tint          the tint color
    * @param fac           the factor
    * @param preserveLight the preserve light flag
    * @param target        the target image
    *
    * @return the tinted image
    */
   public static PImage tint ( final PImage source, final int tint,
      final float fac, final boolean preserveLight, final PImage target ) {

      return ZImage.tint(source, tint, fac, preserveLight, 0, target);
   }

   /**
    * Tints an image to a color by a factor.
    *
    * @param source the source image
    * @param tint   the tint color
    * @param fac    the factor
    * @param target the target image
    *
    * @return the tinted image
    */
   public static PImage tint ( final PImage source, final int tint,
      final float fac, final PImage target ) {

      return ZImage.tint(source, tint, fac, true, target);
   }

   /**
    * Tints an image to a color.
    *
    * @param source the source image
    * @param tint   the tint color
    * @param target the target image
    *
    * @return the tinted image
    */
   public static PImage tint ( final PImage source, final int tint,
      final PImage target ) {

      return ZImage.tint(source, tint, 0.5f, target);
   }

   /**
    * Tints an image to a color by a factor. If the preserveLight flag is
    * true, the source image's original lightness is retained. The image's
    * {@link Pixels#SHADOWS}, {@link Pixels#MIDTONES} and/or
    * {@link Pixels#HIGHLIGHTS} may be targeted with an integer flag.
    *
    * @param source        the source image
    * @param tint          the tint color
    * @param fac           the factor
    * @param preserveLight the preserve light flag
    * @param toneFlag      the tone flags
    * @param target        the target image
    *
    * @return the tinted image
    */
   public static PImage tint ( final PImage source, final Rgb tint,
      final float fac, final boolean preserveLight, final int toneFlag,
      final PImage target ) {

      return ZImage.tint(source, tint.toHexIntSat(), fac, preserveLight,
         toneFlag, target);
   }

   /**
    * Tints an image to a color by a factor. If the preserveLight flag is
    * true, the source image's original lightness is retained.
    *
    * @param source        the source image
    * @param tint          the tint color
    * @param fac           the factor
    * @param preserveLight the preserve light flag
    * @param target        the target image
    *
    * @return the tinted image
    */
   public static PImage tint ( final PImage source, final Rgb tint,
      final float fac, final boolean preserveLight, final PImage target ) {

      return ZImage.tint(source, tint.toHexIntSat(), fac, preserveLight,
         target);
   }

   /**
    * Tints an image to a color by a factor.
    *
    * @param source the source image
    * @param tint   the tint color
    * @param fac    the factor
    * @param target the target image
    *
    * @return the tinted image
    */
   public static PImage tint ( final PImage source, final Rgb tint,
      final float fac, final PImage target ) {

      return ZImage.tint(source, tint.toHexIntSat(), fac, target);
   }

   /**
    * Tints an image to a color.
    *
    * @param source the source image
    * @param tint   the tint color
    * @param target the target image
    *
    * @return the tinted image
    */
   public static PImage tint ( final PImage source, final Rgb tint,
      final PImage target ) {

      return ZImage.tint(source, tint.toHexIntSat(), target);
   }

   /**
    * Creates an array of materials from the non-transparent pixels of an
    * image. Intended for smaller images with relatively few colors.
    *
    * @param source the source image
    *
    * @return the materials
    *
    * @see Pixels#toMaterials(int[])
    */
   public static MaterialSolid[] toMaterials ( final PImage source ) {

      source.loadPixels();
      return Pixels.toMaterials(source.pixels);
   }

   /**
    * Creates a mesh from the non-transparent pixels of an image.
    *
    * @param source the source image
    * @param target the target mesh
    *
    * @return the mesh
    */
   public static Mesh2 toMesh ( final PImage source, final Mesh2 target ) {

      return ZImage.toMesh(source, PolyType.QUAD, target);
   }

   /**
    * Creates a mesh from the non-transparent pixels of an image.
    *
    * @param source the source image
    * @param poly   the polygon type
    * @param target the target mesh
    *
    * @return the mesh
    *
    * @see Pixels#toMesh(int[], int, int, PolyType, Mesh2)
    */
   public static Mesh2 toMesh ( final PImage source, final PolyType poly,
      final Mesh2 target ) {

      source.loadPixels();
      return Pixels.toMesh(source.pixels, source.pixelWidth, source.pixelHeight,
         poly, target);
   }

   /**
    * Creates an array of meshes from the non-transparent pixels of an image.
    * Each unique color is assigned a mesh. Intended for smaller images with
    * relatively few colors.
    *
    * @param source the source image
    *
    * @return the meshes
    */
   public static Mesh2[] toMeshes ( final PImage source ) {

      return ZImage.toMeshes(source, PolyType.QUAD);
   }

   /**
    * Creates an array of meshes from the non-transparent pixels of an image.
    * Each unique color is assigned a mesh. Intended for smaller images with
    * relatively few colors.
    *
    * @param source the source image
    * @param poly   the polygon type
    *
    * @return the meshes
    *
    * @see Pixels#toMeshes(int[], int, int, PolyType)
    */
   public static Mesh2[] toMeshes ( final PImage source, final PolyType poly ) {

      source.loadPixels();
      return Pixels.toMeshes(source.pixels, source.pixelWidth,
         source.pixelHeight, poly);
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
      sb.append("{\"format\":");
      sb.append(img.format);
      sb.append(",\"width\":");
      sb.append(img.width);
      sb.append(",\"height\":");
      sb.append(img.height);
      sb.append(",\"pixelDensity\":");
      sb.append(img.pixelDensity);
      sb.append('}');
      return sb.toString();
   }

   /**
    * Divides the red, green and blue channels of each pixel in a source image
    * by the alpha channel. Reverse pre-multiplication.
    *
    * @param source the source image
    * @param target the target image
    *
    * @return the image
    *
    * @see ZImage#unpremul(int[], int[])
    */
   public static PImage unpremul ( final PImage source, final PImage target ) {

      if ( source == target ) {
         target.loadPixels();
         ZImage.unpremul(target.pixels, target.pixels);
         target.format = PConstants.ARGB;
         target.updatePixels();
         return target;
      }

      if ( target instanceof PGraphics ) {
         System.err.println("Do not use PGraphics with this method.");
         return target;
      }

      source.loadPixels();
      target.loadPixels();
      final int w = source.pixelWidth;
      final int h = source.pixelHeight;
      final int[] pxSrc = source.pixels;
      target.pixels = ZImage.unpremul(pxSrc, new int[pxSrc.length]);
      target.format = PConstants.ARGB;
      target.pixelDensity = source.pixelDensity;
      target.pixelWidth = w;
      target.pixelHeight = h;
      target.width = source.width;
      target.height = source.height;
      target.updatePixels();

      return target;
   }

   /**
    * Blits a source image's pixels onto a target image's pixels, using
    * integer floor modulo to wrap the source image. The source image can be
    * offset horizontally and/or vertically, creating the illusion of
    * parallax.
    *
    * @param source the source image
    * @param dx     horizontal pixel offset
    * @param dy     vertical pixel offset
    * @param target the target image
    *
    * @return the image
    *
    * @see ZImage#wrap(int[], int, int, int, int, int, int[])
    */
   public static PImage wrap ( final PImage source, final int dx, final int dy,
      final PImage target ) {

      source.loadPixels();
      target.loadPixels();
      ZImage.wrap(source.pixels, source.pixelWidth, source.pixelHeight, dx, dy,
         target.pixelWidth, target.pixels);
      target.format = source.format;
      target.updatePixels();

      return target;
   }

   /**
    * Fills the pixels target array with a color.
    *
    * @param c      the fill color
    * @param target the target pixels
    *
    * @return the filled pixels
    */
   protected static int[] fill ( final int c, final int[] target ) {

      final int len = target.length;
      for ( int i = 0; i < len; ++i ) { target[i] = c; }

      return target;
   }

   /**
    * Multiplies the red, green and blue channels of each pixel by its alpha
    * channel.
    *
    * @param source the source pixels
    * @param target the target pixels
    *
    * @return the premultiplied image
    */
   protected static int[] premul ( final int[] source, final int[] target ) {

      final int srcLen = source.length;
      if ( srcLen == target.length ) {
         for ( int i = 0; i < srcLen; ++i ) {
            final int srcHex = source[i];
            final int ai = srcHex >> 0x18 & 0xff;
            if ( ai < 1 ) {
               target[i] = 0x00000000;
            } else if ( ai < 0xff ) {
               final float af = ai * IUtils.ONE_255;
               int rp = ( int ) ( ( srcHex >> 0x10 & 0xff ) * af + 0.5f );
               int gp = ( int ) ( ( srcHex >> 0x08 & 0xff ) * af + 0.5f );
               int bp = ( int ) ( ( srcHex & 0xff ) * af + 0.5f );

               if ( rp > 0xff ) { rp = 0xff; }
               if ( gp > 0xff ) { gp = 0xff; }
               if ( bp > 0xff ) { bp = 0xff; }

               target[i] = ai << 0x18 | rp << 0x10 | gp << 0x08 | bp;
            } else {
               target[i] = srcHex;
            }
         }
      }

      return target;
   }

   /**
    * Divides the red, green and blue channels of each pixel in the image by
    * its alpha channel. Reverse pre-multiplication.
    *
    * @param source the source pixels
    * @param target the target pixels
    *
    * @return the unpremultiplied pixels
    */
   protected static int[] unpremul ( final int[] source, final int[] target ) {

      final int srcLen = source.length;
      if ( srcLen == target.length ) {
         for ( int i = 0; i < srcLen; ++i ) {
            final int srcHex = source[i];
            final int ai = srcHex >> 0x18 & 0xff;
            if ( ai < 1 ) {
               target[i] = 0x00000000;
            } else if ( ai < 0xff ) {
               final float af = 255.0f / ai;
               int ru = ( int ) ( ( srcHex >> 0x10 & 0xff ) * af + 0.5f );
               int gu = ( int ) ( ( srcHex >> 0x08 & 0xff ) * af + 0.5f );
               int bu = ( int ) ( ( srcHex & 0xff ) * af + 0.5f );

               if ( ru > 0xff ) { ru = 0xff; }
               if ( gu > 0xff ) { gu = 0xff; }
               if ( bu > 0xff ) { bu = 0xff; }

               target[i] = ai << 0x18 | ru << 0x10 | gu << 0x08 | bu;
            } else {
               target[i] = srcHex;
            }
         }
      }

      return target;
   }

   /**
    * Blits a source image's pixels onto a target image's pixels, using
    * integer floor modulo to wrap the source image. The source image can be
    * offset horizontally and/or vertically, creating the illusion of infinite
    * background.
    *
    * @param source the source pixels
    * @param wSrc   the source image width
    * @param hSrc   the source image height
    * @param dx     the horizontal pixel offset
    * @param dy     the vertical pixel offset
    * @param wTrg   the target image width
    * @param target the target pixels
    *
    * @return the wrapped pixels
    */
   protected static int[] wrap ( final int[] source, final int wSrc,
      final int hSrc, final int dx, final int dy, final int wTrg,
      final int[] target ) {

      final int trgLen = target.length;
      for ( int i = 0; i < trgLen; ++i ) {
         int yMod = ( i / wTrg + dy ) % hSrc;
         if ( ( yMod ^ hSrc ) < 0 && yMod != 0 ) { yMod += hSrc; }

         int xMod = ( i % wTrg - dx ) % wSrc;
         if ( ( xMod ^ wSrc ) < 0 && xMod != 0 ) { xMod += wSrc; }

         target[i] = source[xMod + wSrc * yMod];
      }

      return target;
   }

}

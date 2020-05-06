package camzup.core;

import java.util.Iterator;

/**
 * An experimental class to store image data. A deliberate simplification
 * of the PImage class: The image is assumed to always be in the format
 * 0xAARRGGBB. The pixel density is always assumed to be 1. The width and
 * the height of the image can be accessed, but cannot be directly mutated.
 */
@Experimental
public class Img implements Cloneable, Iterable < Color > {

   /**
    * The image's height.
    */
   protected int height;

   /**
    * An array of colors stored as integers in the format 0xAARRGGBB.
    */
   protected int[] pixels;

   /**
    * The image's width.
    */
   protected int width;

   /**
    * Default constructor. Assigns an image the dimensions
    * {@value Img#WIDTH_MIN} by {@value Img#HEIGHT_MIN}.
    */
   public Img ( ) {

      this.width = Img.WIDTH_MIN;
      this.height = Img.HEIGHT_MIN;
      this.pixels = new int[Img.WIDTH_MIN * Img.HEIGHT_MIN];
   }

   /**
    * Sets this image from a source.
    *
    * @param source the source image
    */
   public Img ( final Img source ) { this.set(source); }

   /**
    * Constructs an image from a width and height.
    *
    * @param w the width
    * @param h the height
    */
   public Img ( final int w, final int h ) {

      this.width = w < Img.WIDTH_MIN ? Img.WIDTH_MIN : w;
      this.height = h < Img.HEIGHT_MIN ? Img.HEIGHT_MIN : h;
      this.pixels = new int[this.width * this.height];
   }

   /**
    * Constructs an image from a width and height, then fills all the image's
    * pixels with a color.
    *
    * @param w    the width
    * @param h    the height
    * @param fill the fill
    *
    * @see Color#toHexInt(Color)
    */
   public Img ( final int w, final int h, final Color fill ) {

      this(w, h, Color.toHexInt(fill));
   }

   /**
    * Constructs an image from a width and height, then fills all the image's
    * pixels with a color.
    *
    * @param w    the width
    * @param h    the height
    * @param fill the fill
    */
   public Img ( final int w, final int h, final int fill ) {

      this(w, h);
      final int len = this.pixels.length;
      for ( int i = 0; i < len; ++i ) {
         this.pixels[i] = fill;
      }
   }

   /**
    * Creates a new image from this one.
    */
   @Override
   public Img clone ( ) { return new Img(this); }

   /**
    * Gets a pixel color at an index in the pixel array. The color is
    * formatted as an integer in the order 0xAARRGGBB.
    *
    * @param i the index
    *
    * @return the color
    */
   public int get ( final int i ) {

      final int len = this.pixels.length;
      int j = i % len;
      if ( ( j ^ len ) < 0 && j != 0 ) { j += len; }
      return this.pixels[j];
   }

   /**
    * Gets a pixel color at an index in the pixel array.
    *
    * @param i      the index
    * @param target the output color
    *
    * @return the color
    *
    * @see Img#get(int)
    * @see Color#fromHex(int, Color)
    */
   public Color get ( final int i, final Color target ) {

      return Color.fromHex(this.get(i), target);
   }

   /**
    * Gets a pixel color at an (x, y) coordinate in pixels. The color is
    * formatted as an integer in the order 0xAARRGGBB.
    *
    * @param x the x coordinate
    * @param y the y coordinate
    *
    * @return the color
    */
   public int get ( final int x, final int y ) {

      int i = y % this.height;
      if ( ( i ^ this.height ) < 0 && i != 0 ) { i += this.height; }

      int j = x % this.width;
      if ( ( j ^ this.width ) < 0 && j != 0 ) { j += this.width; }

      return this.pixels[i * this.width + j];
   }

   /**
    * Gets a pixel color at an (x, y) coordinate in pixels.
    *
    * @param x      the x coordinate
    * @param y      the y coordinate
    * @param target the output color
    *
    * @return the color
    *
    * @see Img#get(int, int)
    * @see Color#fromHex(int, Color)
    */
   public Color get ( final int x, final int y, final Color target ) {

      return Color.fromHex(this.get(x, y), target);
   }

   /**
    * Gets a pixel color at a UV coordinate. The coordinate should be in the
    * range [0.0, 1.0] ; the function scales the input to pixel dimensions.
    * The color is formatted as an integer in the order 0xAARRGGBB.
    *
    * @param st the texture coordinate
    *
    * @return the color
    *
    * @see Img#get(int, int)
    */
   public int get ( final Vec2 st ) {

      return this.get(( int ) ( st.x * this.width ), ( int ) ( st.y
         * this.height ));
   }

   /**
    * Gets a pixel color at a UV coordinate. The coordinate should be in the
    * range [0.0, 1.0] ; the function scales the input to pixel dimensions.
    *
    * @param st     the texture coordinate
    * @param target the output color
    *
    * @return the color
    *
    * @see Img#get(int, int, Color)
    */
   public Color get ( final Vec2 st, final Color target ) {

      return this.get(( int ) ( st.x * this.width ), ( int ) ( st.y
         * this.height ), target);
   }

   /**
    * Gets the image's height.
    *
    * @return the height
    */
   public int getHeight ( ) { return this.height; }

   /**
    * Returns a reference to the pixels array. External modifications to this
    * array will not guarantee proper function of the image; however, it is
    * allowed so that the image can be converted and displayed.
    *
    * @return the reference
    */
   public int[] getPixels ( ) { return this.pixels; }

   /**
    * Gets the image's width.
    *
    * @return the width
    */
   public int getWidth ( ) { return this.width; }

   /**
    * Returns an iterator for this image, which allows its components to be
    * accessed in an enhanced for-loop.
    *
    * @return the iterator
    */
   @Override
   public Iterator < Color > iterator ( ) { return new ImgIterator(this); }

   /**
    * Gets the length of the pixels array.
    *
    * @return the length
    */
   public int length ( ) { return this.pixels.length; }

   /**
    * Internal function to reallocate an image's pixel array <em>if</em> the
    * width and height of the image are different. Does nothing if width and
    * height are the same as the old. Does not set new array's pixel colors to
    * the old.
    *
    * @param w the width the width
    * @param h the height the height
    *
    * @return this image
    */
   public Img reallocate ( final int w, final int h ) {

      if ( w != this.width && h != this.height ) {
         // final int origLen = this.pixels.length;
         // final int[] original = new int[origLen];
         // System.arraycopy(this.pixels, 0, original, 0, origLen);
         this.width = w < Img.WIDTH_MIN ? Img.WIDTH_MIN : w;
         this.height = h < Img.HEIGHT_MIN ? Img.HEIGHT_MIN : h;
         final int newLen = this.width * this.height;
         this.pixels = new int[newLen];
         // System.arraycopy(original, 0, this.pixels, 0, newLen);
      }
      return this;
   }

   /**
    * Sets this image from a source.
    *
    * @param source the source image
    */
   public void set ( final Img source ) {

      if ( this != source ) {
         this.reallocate(source.width, source.height);
         final int[] pxSrc = source.pixels;
         final int srcLen = pxSrc.length;
         for ( int i = 0; i < srcLen; ++i ) {
            this.pixels[i] = pxSrc[i];
         }
      }
   }

   /**
    * Sets a pixel at an index to a color.
    *
    * @param i      the index
    * @param source the source color
    *
    * @see Img#set(int, int)
    * @see Color#toHexInt(Color)
    */
   public void set ( final int i, final Color source ) {

      this.set(i, Color.toHexInt(source));
   }

   /**
    * Sets a pixel at an index to a color. Wraps the index by the pixel
    * array's length with floor modulo.
    *
    * @param i      the index
    * @param source the source color
    */
   public void set ( final int i, final int source ) {

      final int len = this.pixels.length;
      int j = i % len;
      if ( ( j ^ len ) < 0 && j != 0 ) { j += len; }
      this.pixels[j] = source;
   }

   /**
    * Gets a pixel color at an (x, y) coordinate in pixels.
    *
    * @param x      the x coordinate
    * @param y      the y coordinate
    * @param source the source color
    *
    * @see Img#set(int, int, int)
    * @see Color#toHexInt(Color)
    */
   public void set ( final int x, final int y, final Color source ) {

      this.set(x, y, Color.toHexInt(source));
   }

   /**
    * Gets a pixel color at an (x, y) coordinate in pixels. Wraps the x and y
    * coordinate around the image's width and height with floor modulo. Then
    * uses the formula (y * w + h) to convert the coordinate to a pixel index.
    *
    * @param x      the x coordinate
    * @param y      the y coordinate
    * @param source the source color
    */
   public void set ( final int x, final int y, final int source ) {

      int i = y % this.height;
      if ( ( i ^ this.height ) < 0 && i != 0 ) { i += this.height; }

      int j = x % this.width;
      if ( ( j ^ this.width ) < 0 && j != 0 ) { j += this.width; }

      this.pixels[i * this.width + j] = source;
   }

   /**
    * Sets a pixel color at a UV coordinate. The coordinate should be in the
    * range [0.0, 1.0] ; the function scales the input to pixel dimensions.
    *
    * @param st     the texture coordinate
    * @param source the source color
    *
    * @see Img#set(int, int, Color)
    */
   public void set ( final Vec2 st, final Color source ) {

      this.set(( int ) ( st.x * this.width ), ( int ) ( st.y * this.height ),
         source);
   }

   /**
    * Sets a pixel color at a UV coordinate. The coordinate should be in the
    * range [0.0, 1.0] ; the function scales the input to pixel dimensions.
    *
    * @param st     the texture coordinate
    * @param source the source color
    *
    * @see Img#set(int, int, int)
    */
   public void set ( final Vec2 st, final int source ) {

      this.set(( int ) ( st.x * this.width ), ( int ) ( st.y * this.height ),
         source);
   }

   /**
    * Returns the string representation of the image, indicating its width and
    * height.
    *
    * @return the string
    */
   @Override
   public String toString ( ) {

      final StringBuilder sb = new StringBuilder(64);
      sb.append("{ width: ");
      sb.append(Utils.toPadded(this.width, 4));
      sb.append(", height: ");
      sb.append(Utils.toPadded(this.height, 4));
      sb.append(' ');
      sb.append('}');
      return sb.toString();
   }

   /**
    * The minimum width an image can be assigned.
    */
   public static final int HEIGHT_MIN = 2;

   /**
    * The minimum height an image can be assigned.
    */
   public static final int WIDTH_MIN = 2;

   /**
    * Gets the aspect ratio of the image's width to its height.
    *
    * @param img the image
    *
    * @return the aspect
    */
   public static float aspect ( final Img img ) {

      return Utils.div(img.width, img.height);
   }

   /**
    * Given a top left and bottom right corner, sets the target image to the
    * cropped region of the source.
    *
    * @param x0     the top left x
    * @param y0     the top left y
    * @param x1     the bottom right x
    * @param y1     the bottom right y
    * @param source the input image
    * @param target the output image
    *
    * @return the cropped image
    */
   public static Img crop ( final int x0, final int y0, final int x1,
      final int y1, final Img source, final Img target ) {

      final int xMin = x0 <= x1 ? x0 : x1;
      final int xMax = x0 >= x1 ? x0 : x1;
      final int yMin = y0 <= y1 ? y0 : y1;
      final int yMax = y0 >= y1 ? y0 : y1;

      final int wSrc = source.width;
      final int hSrc = source.height;
      final int vx0 = xMin > -1 ? xMin : 0;
      final int vx1 = xMax < wSrc ? xMax : wSrc;
      final int vy0 = yMin > -1 ? yMin : 0;
      final int vy1 = yMax < hSrc ? yMax : hSrc;

      /* If target and source are the same, then copy original pixels. */
      int[] pxSrc;
      if ( source != target ) {
         pxSrc = source.pixels;
      } else {
         final int srcLen = source.pixels.length;
         pxSrc = new int[srcLen];
         System.arraycopy(source.pixels, 0, pxSrc, 0, srcLen);
      }

      /* Reallocation must happen after the source pixels are checked. */
      final int wTrg = vx1 - vx0;
      final int hTrg = vy1 - vy0;
      target.reallocate(wTrg, hTrg);
      final int[] pxTrg = target.pixels;

      /* Loop over width and height of target while finding index in source. */
      for ( int kTrg = 0, yTrg = 0, ySrc = vx0; yTrg < hTrg; ++yTrg, ++ySrc ) {
         final int kSrcOff = ySrc * wSrc;
         for ( int xTrg = 0, xSrc = vy0; xTrg < wTrg; ++xTrg, ++kTrg, ++xSrc ) {
            pxTrg[kTrg] = pxSrc[kSrcOff + xSrc];
         }
      }

      return target;
   }

   /**
    * Given a top left and bottom right corner, sets the target image to the
    * cropped region of the source. The coordinates should be in the range
    * [0.0, 1.0] ; the function scales the vectors to pixel dimensions.
    *
    * @param tl     the top left corner
    * @param br     the bottom right corner
    * @param source the input image
    * @param target the output image
    *
    * @return the cropped image
    */
   public static Img crop ( final Vec2 tl, final Vec2 br, final Img source,
      final Img target ) {

      final int sw = source.width;
      final int sh = source.height;
      return Img.crop(( int ) ( tl.x * sw ), ( int ) ( tl.y * sh ),
         ( int ) ( br.x * sw ), ( int ) ( br.y * sh ), source, target);
   }

   /**
    * Recolors an image in-place with a color gradient. The image's luminance
    * is used as the input factor.
    *
    * @param grd    the gradient
    * @param target the output image
    *
    * @return the image
    *
    * @see Color#luminance(int)
    * @see Gradient#eval(Gradient, float)
    */
   public static Img falseColor ( final Gradient grd, final Img target ) {

      final int[] px = target.pixels;
      final int len = px.length;
      for ( int i = 0; i < len; ++i ) {
         px[i] = Gradient.eval(grd, Color.luminance(px[i]));
      }
      return target;
   }

   /**
    * Recolors an image to a target image with a color gradient. The source
    * image's luminance is used as the input factor.
    *
    * @param grd    the gradient
    * @param source the input image
    * @param target the output image
    *
    * @return the image
    *
    * @see Color#luminance(int)
    * @see Gradient#eval(Gradient, float)
    */
   public static Img falseColor ( final Gradient grd, final Img source,
      final Img target ) {

      int[] pxSrc;
      if ( source != target ) {
         pxSrc = source.pixels;
      } else {
         final int srcLen = source.pixels.length;
         pxSrc = new int[srcLen];
         System.arraycopy(source.pixels, 0, pxSrc, 0, srcLen);
      }

      target.reallocate(target.width, target.height);
      final int[] pxTrg = target.pixels;
      final int trgLen = pxTrg.length;
      for ( int i = 0; i < trgLen; ++i ) {
         pxTrg[i] = Gradient.eval(grd, Color.luminance(pxSrc[i]));
      }
      return target;
   }

   /**
    * Fills an image with a gradient in place. The gradient is horizontal.
    *
    * @param grd    the gradient
    * @param target the target image
    *
    * @return the image
    */
   public static Img fill ( final Gradient grd, final Img target ) {

      final int h = target.height;
      final int w = target.width;
      final int[] pixels = target.pixels;

      final float wInv = 1.0f / ( w - 1.0f );
      for ( int i = 0, y = 0; y < h; ++y ) {
         for ( int x = 0; x < w; ++x, ++i ) {
            pixels[i] = Gradient.eval(grd, x * wInv);
         }
      }

      return target;
   }

   /**
    * Generates a linear gradient from an origin point to a destination point.
    * The value is clamped to a range [0.0, 1.0] .
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
   public static Img linear ( final float xOrigin, final float yOrigin,
      final float xDest, final float yDest, final Gradient grd,
      final Img target ) {

      final int h = target.height;
      final int w = target.width;
      final int[] pixels = target.pixels;

      final float hInv = 1.0f / ( h - 1.0f );
      final float wInv = 1.0f / ( w - 1.0f );

      final float bx = xOrigin - xDest;
      final float by = yOrigin - yDest;
      final float bbInv = 1.0f / Utils.max(IUtils.DEFAULT_EPSILON, bx * bx + by
         * by);

      for ( int i = 0, y = 0; y < h; ++y ) {

         final float yn = y * hInv;
         final float ay = yOrigin - ( 1.0f - ( yn + yn ) );

         for ( int x = 0; x < w; ++x, ++i ) {

            final float xn = x * wInv;
            final float ax = xOrigin - ( xn + xn - 1.0f );

            pixels[i] = Gradient.eval(grd, Utils.clamp01( ( ax * bx + ay * by )
               * bbInv));
         }
      }

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
   public static Img linear ( final Vec2 origin, final Vec2 dest,
      final Gradient grd, final Img target ) {

      return Img.linear(origin.x, origin.y, dest.x, dest.y, grd, target);
   }

   /**
    * Remove transparency from the image by compositing all colors with opaque
    * black. If the target is not the same as the source, then the target is
    * resized and set to the composite.
    *
    * @param source the source image
    * @param target the target image
    *
    * @return this image
    */
   public static Img opaque ( final Img source, final Img target ) {

      if ( source != target ) {

         target.reallocate(source.width, source.height);
         final int[] pxSrc = source.pixels;
         final int[] pxTrg = target.pixels;
         final int trgLen = pxTrg.length;
         for ( int i = 0; i < trgLen; ++i ) {
            pxTrg[i] = 0xff000000 | pxSrc[i];
         }
         return target;

      } else {

         final int[] px = target.pixels;
         final int len = px.length;
         for ( int i = 0; i < len; ++i ) {
            px[i] = 0xff000000 | px[i];
         }
         return target;

      }
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
   public static Img radial ( final float xOrigin, final float yOrigin,
      final float radius, final Gradient grd, final Img target ) {

      final int h = target.height;
      final int w = target.width;
      final int[] px = target.pixels;

      final float hInv = 1.0f / ( h - 1.0f );
      final float wInv = 1.0f / ( w - 1.0f );

      final float r2 = radius + radius;
      final float invrsq = 1.0f / Utils.max(IUtils.DEFAULT_EPSILON, r2 * r2);

      for ( int i = 0, y = 0; y < h; ++y ) {

         final float yn = y * hInv;
         final float ay = yOrigin - ( 1.0f - ( yn + yn ) );
         final float aysq = ay * ay;

         for ( int x = 0; x < w; ++x, ++i ) {

            final float xn = x * wInv;
            final float ax = xOrigin - ( xn + xn - 1.0f );
            px[i] = Gradient.eval(grd, 1.0f - ( ax * ax + aysq ) * invrsq);
         }
      }

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
   public static Img radial ( final Vec2 origin, final float radius,
      final Gradient grd, final Img target ) {

      return Img.radial(origin.x, origin.y, radius, grd, target);
   }

   /**
    * Generates a diagnostic image where a pixel's location on the x-axis
    * correlates to the color red; on the y-axis, to green.
    *
    * @param target the output image
    *
    * @return the image
    */
   public static Img rgb ( final Img target ) {

      final int h = target.height;
      final int w = target.width;
      final int[] px = target.pixels;

      final float hInv = 0xff / ( h - 1.0f );
      final float wInv = 0xff / ( w - 1.0f );

      for ( int i = 0, y = h - 1; y > -1; --y ) {
         final int grbl = 0xff000080 | ( int ) ( y * hInv + 0.5f ) << 0x8;
         for ( int x = 0; x < w; ++x, ++i ) {
            px[i] = ( int ) ( x * wInv + 0.5f ) << 0x10 | grbl;
         }
      }

      return target;
   }

   /**
    * Generates a diagnostic image where a pixel's location on the x-axis
    * correlates to the color red; on the y-axis, to green.
    *
    * @param w      the width
    * @param h      the height
    * @param target the output image
    *
    * @return the image
    *
    * @see Img#reallocate(int, int)
    * @see Img#rgb(Img)
    */
   public static Img rgb ( final int w, final int h, final Img target ) {

      target.reallocate(w, h);
      return Img.rgb(target);
   }

   /**
    * Blits a source image's pixels onto a target image's pixels, using floor
    * modulo to wrap the source. The source image can be offset horizontally
    * and/or vertically, creating the illusion of parallax.
    *
    * @param dx     horizontal pixel offset
    * @param dy     vertical pixel offset
    * @param source the source image
    * @param target the target image
    *
    * @return the target image
    */
   public static Img wrap ( final int dx, final int dy, final Img source,
      final Img target ) {

      final int wSrc = source.width;
      final int hSrc = source.height;

      int[] pxSrc;
      if ( source != target ) {
         pxSrc = source.pixels;
      } else {
         final int srcLen = source.pixels.length;
         pxSrc = new int[srcLen];
         System.arraycopy(source.pixels, 0, pxSrc, 0, srcLen);
      }

      final int wTrg = target.width;
      final int hTrg = target.height;
      final int[] pxTrg = target.pixels;

      for ( int i = 0, y = 0; y < hTrg; ++y ) {
         int ymod = ( y - dy ) % hSrc;
         if ( ( ymod ^ hSrc ) < 0 && ymod != 0 ) { ymod += hSrc; }
         final int ny = wSrc * ymod;

         for ( int x = 0; x < wTrg; ++x, ++i ) {
            int xmod = ( x + dx ) % wSrc;
            if ( ( xmod ^ wSrc ) < 0 && xmod != 0 ) { xmod += wSrc; }
            pxTrg[i] = pxSrc[xmod + ny];
         }
      }

      return target;
   }

   /**
    * Blits a source image's pixels onto a target image's pixels, using floor
    * modulo to wrap the source. The source image can be offset horizontally
    * and/or vertically, creating the illusion of parallax. The coordinate
    * should be in the range [0.0, 1.0] ; the function scales the input to
    * pixel dimensions.
    *
    * @param st     the texture coordinate
    * @param source the source image
    * @param target the target image
    *
    * @return the target image
    */
   public static Img wrap ( final Vec2 st, final Img source,
      final Img target ) {

      return Img.wrap(( int ) ( st.x * source.width ), ( int ) ( st.y
         * source.height ), source, target);
   }

   /**
    * An iterator, which allows a image's components to be accessed in an
    * enhanced for loop.
    */
   public static final class ImgIterator implements Iterator < Color > {

      /**
       * The image being iterated over.
       */
      private final Img img;

      /**
       * The current index.
       */
      private int index = 0;

      /**
       * The default constructor.
       *
       * @param i the image to iterate
       */
      public ImgIterator ( final Img i ) { this.img = i; }

      /**
       * Tests to see if the iterator has another value.
       *
       * @return the evaluation
       */
      @Override
      public boolean hasNext ( ) { return this.index < this.img.length(); }

      /**
       * Gets the next value in the iterator.
       *
       * @see Vec4#get(int)
       *
       * @return the value
       */
      @Override
      public Color next ( ) { return this.img.get(this.index++, new Color()); }

      /**
       * Returns the simple name of this class.
       *
       * @return the string
       */
      @Override
      public String toString ( ) { return this.getClass().getSimpleName(); }

   }

}

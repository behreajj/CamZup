package camzup.core;

/**
 * An experimental class to store image data.
 */
@Experimental
public class Img {

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
    * {@value Img#WIDTH_MIN} x {@value Img#HEIGHT_MIN}.
    */
   public Img ( ) {

      this.width = Img.WIDTH_MIN;
      this.height = Img.HEIGHT_MIN;
      this.pixels = new int[Img.WIDTH_MIN * Img.HEIGHT_MIN];
   }

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
    * array by reference will not guarantee proper function of the image. This
    * function returns a reference so that the image can easily be converted
    * to renderer specific image formats and displayed.
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
    * Internal function to reallocate an image's pixel array <em>if</em> the
    * width and height of the image are different. Does nothing if width and
    * height are the same as the old. Does not set new array's pixel colors to
    * the old.
    *
    * @param w the width the width
    * @param h the height the height
    */
   protected void reallocate ( final int w, final int h ) {

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

      target.reallocate(source.width, source.height);
      final int[] pxSrc = source.pixels;
      final int[] pxTrg = target.pixels;
      final int len = pxTrg.length;
      for ( int i = 0; i < len; ++i ) {
         pxTrg[i] = Gradient.eval(grd, Color.luminance(pxSrc[i]));
      }
      return target;
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

}

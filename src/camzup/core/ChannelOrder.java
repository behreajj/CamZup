package camzup.core;

/**
 * Order in which to arrange color channels.
 */
public enum ChannelOrder {

   /**
    * Alpha, Blue, Green, Red.
    */
   ABGR ( 0x00, 0x08, 0x10, 0x18 ),

   /**
    * Alpha, Red, Green, Blue.
    */
   ARGB ( 0x10, 0x08, 0x00, 0x18 ),

   /**
    * Red, Green, Blue, Alpha.
    */
   RGBA ( 0x18, 0x10, 0x08, 0x00 );

   /**
    * Shift for the alpha channel when reading or writing to a 32-bit integer.
    */
   private final int shiftAlpha;

   /**
    * Shift for the blue channel when reading or writing to a 32-bit integer.
    */
   private final int shiftBlue;

   /**
    * Shift for the green channel when reading or writing to a 32-bit integer.
    */
   private final int shiftGreen;

   /**
    * Shift for the red channel when reading or writing to a 32-bit integer.
    */
   private final int shiftRed;

   /**
    * Constructs a channel order with a specification for the shift of each
    * channel when packing a 32-bit integer.
    *
    * @param sr the red shift
    * @param sg the green shift
    * @param sb the blue shift
    * @param sa the alpha shift
    */
   ChannelOrder ( final int sr, final int sg, final int sb, final int sa ) {

      this.shiftRed = sr;
      this.shiftGreen = sg;
      this.shiftBlue = sb;
      this.shiftAlpha = sa;
   }

   /**
    * Gets the alpha shift
    *
    * @return the alpha shift
    */
   public int getShiftAlpha ( ) { return this.shiftAlpha; }

   /**
    * Gets the blue shift
    *
    * @return the blue shift
    */
   public int getShiftBlue ( ) { return this.shiftBlue; }

   /**
    * Gets the green shift
    *
    * @return the green shift
    */
   public int getShiftGreen ( ) { return this.shiftGreen; }

   /**
    * Gets the red shift
    *
    * @return the red shift
    */
   public int getShiftRed ( ) { return this.shiftRed; }

}
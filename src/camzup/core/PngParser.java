package camzup.core;

import java.io.File;
import java.nio.file.Files;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * An incomplete draft of a portable network graphics parser. Even if this
 * is completed, it will not be robust or tolerant of faulty data. Use only
 * for creative applications where you know the source of the image.<br>
 * <br>
 * Made with reference to Casey Muratori's
 * <a href="https://youtu.be/lkEWbIUEuN0">Handmade Hero</a> tutorials.
 */
@Experimental
public abstract class PngParser {

   /**
    * Discourage overriding with a private constructor.
    */
   private PngParser ( ) {}

   /**
    * A cached array for the .png signature.
    */
   private static final byte[] SIG_PNG;

   static {
      SIG_PNG = PngParser.sigPng();
   }

   public static Img parsePng ( final String filePath, final Img target ) {

      final Chunk[] chunks = PngParser.parsePngToChunks(filePath);
      final int chunksLen = chunks.length;

      int width = Img.WIDTH_MIN;
      int height = Img.HEIGHT_MIN;
      int bitDepth = 8;
      ColorFormat clrFmt = ColorFormat.ARGB;
      int compression = 0;
      int filter = 0;
      int interlace = 0;
      boolean ended = false;

      /*
       * IDAT chunks are meant to be read as one big section of bits (not bytes)
       * read according to the zlib specification irrespective of boundaries
       * between bytes. So the sum of all IDAT chunks is accumulated, then a
       * byte array will be created.
       */
      final ArrayList < IDATChunk > zlibChunks = new ArrayList <>();
      int zlibLen = 0;

      for ( int i = 0; i < chunksLen; ++i ) {
         final Chunk chunk = chunks[i];
         final String chunkType = chunk.typeToString();
         final int chunkTypeHash = chunkType.hashCode();

         // System.out.println(chunkType);
         System.out.println(chunk);

         switch ( chunkTypeHash ) {

            case 2246125:
               /* "IHDR" */
               final IHDRChunk header = new IHDRChunk(chunk);
               width = header.width();
               height = header.height();

               /* Check for improper width or height. */
               if ( width < 2 || height < 2 ) {
                  System.err.println("PNG has dimensions less than 2 x 2.");
                  return target;
               }

               bitDepth = header.bitDepth();
               clrFmt = header.colorFormat();
               compression = header.compression();
               filter = header.filter();
               interlace = header.interlace();

               System.out.println("width: " + width);
               System.out.println("height: " + height);
               System.out.println("bitDepth: " + bitDepth);
               System.out.println("colorFmt: " + clrFmt);
               System.out.println("compression: " + compression);
               System.out.println("filter: " + filter);
               System.out.println("interlace: " + interlace);

               break;

            case 2242190:
               /* "IDAT" */
               final IDATChunk dtchnk = new IDATChunk(chunk);
               zlibChunks.add(dtchnk);
               zlibLen += dtchnk.data.length;
               break;

            case 2243538:
               /* "IEND" */
               ended = true;
               break;

            case 2458989:
               /* "PLTE" */
               break;

            case 3408658:
               /* "pHYs" */
               final PHYSChunk physical = new PHYSChunk(chunk);
               System.out.println("unit specifier: " + physical.unitCode());
               System.out.println("pixels per unit x: " + physical.ppux());
               System.out.println("pixels per unit y: " + physical.ppuy());

               break;

            case 3528365:
               /* "tIME" */
               break;

            default:
         }
      }

      if ( ended ) {

         /*
          * If the file contains an appropriate end, then the image can be
          * reallocated.
          */
         target.reallocate(width, height);

         /* Consolidate separate blocks into one byte array. */
         int cursor = 0;
         final byte[] zlibdat = new byte[zlibLen];
         final Iterator < IDATChunk > itr = zlibChunks.iterator();
         while ( itr.hasNext() ) {
            final byte[] dt = itr.next().data;
            final int dtLen = dt.length;
            for ( int i = 0; i < dtLen; ++i ) {
               zlibdat[cursor++] = dt[i];
            }
         }

         return PngParser.parseZlib(zlibdat, target);

      } else {
         System.err.println("PNG file did not terminate properly.");
      }

      return target;
   }

   /**
    * Given a file path, loads a .png file as bytes then parses them into
    * chunks according to the .png file format specification.
    *
    * @param filepath the file path
    *
    * @return the chunks array
    */
   public static Chunk[] parsePngToChunks ( final String filepath ) {

      final File file = new File(filepath);
      final ArrayList < Chunk > chunkList = new ArrayList <>();

      try {
         int cursor = 0;
         final byte[] content = Files.readAllBytes(file.toPath());
         final int len = content.length;

         if ( len < 8 ) {
            throw new Exception("Insufficient file content to read header.");
         }

         /* Check header. */
         for ( ; cursor < 8; ++cursor ) {
            if ( content[cursor] != PngParser.SIG_PNG[cursor] ) {
               throw new Exception(
                  "File header in bytes does not match PNG standard.");
            }
         }

         while ( cursor < len ) {
            /* @formatter:off */

            /* Read chunk length. */
            final byte[] chunklen = {
               content[cursor++],
               content[cursor++],
               content[cursor++],
               content[cursor++] };

            /* Convert length from bytes to an integer. */
            int parsedLen = 0;
            for ( int i = 0; i < 4; ++i ) {
               parsedLen <<= 0x08;
               parsedLen |= chunklen[i] & 0xff;
            }

            /* Read chunk type. */
            final byte[] chunktype = {
               content[cursor++],
               content[cursor++],
               content[cursor++],
               content[cursor++] };

            /* Read chunk data. */
            final byte[] chunkdat = new byte[parsedLen];
            for ( int j = 0; j < parsedLen; ++j ) {
               chunkdat[j] = content[cursor++];
            }

            /* Read cyclic redundancy check. */
            final byte[] chunkcrc = {
               content[cursor++],
               content[cursor++],
               content[cursor++],
               content[cursor++] };

            /* Create a new chunk object. */
            final Chunk chunk = new Chunk(
               chunklen,
               chunktype,
               chunkdat,
               chunkcrc);

            chunkList.add(chunk);
            /* @formatter:on */
         }

      } catch ( final Exception e ) {
         e.printStackTrace();
      }

      return chunkList.toArray(new Chunk[chunkList.size()]);
   }

   /**
    * A critical chunk signature describing the data of a .png file. In
    * decimal format:
    *
    * <pre>
    * <code>{ 73, 68, 65, 84 }</code>
    * </pre>
    *
    * In hexadecimal format:
    *
    * <pre>
    * <code>
    * { 0x31, 0x2c, 0x29, 0x36 }
    * </pre></code> When the bytes are cast to characters, they spell out the
    * signature, i.e., 'I', 'D', 'A', 'T'.
    *
    * @return the byte array
    */
   public static byte[] sigIdatBytes ( ) {

      return new byte[] { 0x49, 0x44, 0x41, 0x54 };
   }

   /**
    * A critical chunk signature describing the end of the .png file. In
    * decimal format:
    *
    * <pre>
    * <code>{ 73, 69, 78, 68 }</code>
    * </pre>
    *
    * In hexadecimal format:
    *
    * <pre>
    * <code>
    * { 0x49, 0x45, 0x4e, 0x44 }
    * </pre></code> When the bytes are cast to characters, they spell out the
    * signature, i.e., 'I', 'E', 'N', 'D'.
    *
    * @return the byte array
    */
   public static byte[] sigIEndBytes ( ) {

      return new byte[] { 0x49, 0x45, 0x4e, 0x44 };
   }

   /**
    * A critical chunk signature describing the file's width, height, bit
    * depth, color type, compression method, filter method and interlace
    * method. In decimal format:
    *
    * <pre>
    * <code>{ 73, 72, 68, 82 }</code>
    * </pre>
    *
    * In hexadecimal format:
    *
    * <pre>
    * <code>{ 0x49, 0x48, 0x44, 0x52 }</code>
    * </pre>
    *
    * When the bytes are cast to characters, they spell out the signature,
    * i.e., 'I', 'H', 'D', 'R'.
    *
    * @return the byte array
    */
   public static byte[] sigIhdrBytes ( ) {

      return new byte[] { 0x49, 0x48, 0x44, 0x52 };
   }

   /**
    * An ancillary chunk signature describing intended pixel size or aspect
    * ratio for display of a .png file. In decimal format:
    *
    * <pre>
    * <code>{ 112, 72, 89, 115 }</code>
    * </pre>
    *
    * In hexadecimal format:
    *
    * <pre>
    * <code>
    * { 0x70, 0x48, 0x59, 0x73 }
    * </pre></code> When the bytes are cast to characters, they spell out the
    * signature, i.e., 'p', 'H', 'Y', 's'.
    *
    * @return the byte array
    */
   public static byte[] sigPhysBytes ( ) {

      return new byte[] { 0x70, 0x48, 0x59, 0x73 };
   }

   /**
    * A critical chunk signature describing the data of an indexed .png file.
    * In decimal format:
    *
    * <pre>
    * <code>{ 80, 76, 84, 69 }</code>
    * </pre>
    *
    * In hexadecimal format:
    *
    * <pre>
   * <code>{ 0x50, 0x4c, 0x54, 0x45 }</code>
    * </pre>
    *
    * When the bytes are cast to characters, they spell out the signature,
    * i.e., 'P', 'L', 'T', 'E'.
    *
    * @return the byte array
    */
   public static byte[] sigPlteBytes ( ) {

      return new byte[] { 0x50, 0x4c, 0x54, 0x45 };
   }

   /**
    * The signature, in 8 bytes, for the .png (portable network graphics) file
    * format. In decimal format:
    *
    * <pre>
    * <code>{ 137, 80, 78, 71, 13, 10, 26, 10 }</code>
    * </pre>
    *
    * In hexadecimal format:
    *
    * <pre>
    * <code>{ 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a }</code>
    * </pre>
    *
    * Because Java bytes are signed, and <code>137</code> exceeds
    * {@link Byte#MAX_VALUE} ({@value Byte#MAX_VALUE}), it wraps around to
    * <code>-119</code>.
    *
    * @return the byte array
    */
   public static byte[] sigPng ( ) {

      return new byte[] { ( byte ) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a,
         0x0a };
   }

   /**
    * An ancillary chunk signature describing the last time the file was
    * modified. In decimal format:
    *
    * <pre>
    * <code>{ 116, 73, 77, 69 }</code>
    * </pre>
    *
    * In hexadecimal format:
    *
    * <pre>
    * <code>{ 0x74, 0x49, 0x4d, 0x45 }</code>
    * </pre>
    *
    * When the bytes are cast to characters, they spell out the signature,
    * i.e., 't', 'I', 'M', 'E'.
    *
    * @return the byte array
    */
   public static byte[] sigTimeBytes ( ) {

      return new byte[] { 0x74, 0x49, 0x4d, 0x45 };
   }

   /**
    * Parses the zlib section of the .png data.
    * 
    * @param zlibdat the data
    * @param target  the output image
    * 
    * @return the image
    */
   protected static Img parseZlib ( final byte[] zlibdat, final Img target ) {

      // TODO: Implement.

      System.out.println(Utils.toString(Utils.bitslm(zlibdat[0])));
      return target;
   }

   /**
    * Defines a chunk of portable network graphics (.png) information.
    * Contains a type description, a measure of the length of data in the
    * chunk, the data, and a cyclic redundancy check (CRC). The CRC is not
    * expected to be important.
    */
   public static class Chunk {
      final byte[] crc;
      final byte[] data;
      final byte[] length;
      final byte[] type;

      /**
       * Constructs a chunk from four byte arrays.
       *
       * @param length the length array
       * @param type   the type array
       * @param data   the data array
       * @param crc    the CRC array
       */
      public Chunk ( final byte[] length, final byte[] type, final byte[] data,
         final byte[] crc ) {

         this.length = length;
         this.type = type;
         this.data = data;
         this.crc = crc;
      }

      /**
       * Returns a string of the chunk's data as an array of bytes promoted to
       * integers. The array is enclosed by the tokens '[' and ']', and each
       * element is separated with ','.
       *
       * @return the string
       */
      public String dataToArrString ( ) {

         final int dataLen = this.data.length;
         final int dataLast = dataLen - 1;
         final StringBuilder sb = new StringBuilder(4 + dataLen * 4);
         sb.append('[').append(' ');
         for ( int i = 0; i < dataLen; ++i ) {
            sb.append(this.data[i]);
            if ( i < dataLast ) { sb.append(',').append(' '); }
         }
         sb.append(' ').append(']');
         return sb.toString();
      }

      /**
       * Returns a string of the chunk's data promoted from bytes to characters.
       * This data is not expected to be human readable. Instead, it is useful
       * to compare the parser's output with that of a hex editing tool.
       *
       * @return the string
       */
      public String dataToString ( ) {

         final int dataLen = this.data.length;
         final StringBuilder sb = new StringBuilder(dataLen);
         for ( int i = 0; i < dataLen; ++i ) {
            sb.append(( char ) this.data[i]);
         }
         return sb.toString();
      }

      /**
       * Returns the length of the data portion of the chunk as an integer.
       *
       * @return the length
       */
      public int length ( ) {

         int result = 0;
         final int lenlen = this.length.length;
         for ( int i = 0; i < lenlen; ++i ) {
            result <<= 0x08;
            result |= this.length[i] & 0xff;
         }
         return result;
      }

      /**
       * Returns a string representation of this chunk.
       */
      @Override
      public String toString ( ) {

         final StringBuilder sb = new StringBuilder();
         sb.append("{ type: ");
         sb.append(this.typeToString());
         sb.append(", length: ");
         sb.append(this.length());
         sb.append(' ');
         sb.append('}');
         return sb.toString();
      }

      /**
       * Returns a the chunk's type arranged as an array of bytes promoted to
       * integers. The array is enclosed by the tokens '[' and ']', and each
       * element is separated with ','.
       *
       * @return the string
       */
      public String typeToArrString ( ) {

         final StringBuilder sb = new StringBuilder(32);
         sb.append('[').append(' ');
         sb.append(this.type[0]);
         sb.append(',').append(' ');
         sb.append(this.type[1]);
         sb.append(',').append(' ');
         sb.append(this.type[2]);
         sb.append(',').append(' ');
         sb.append(this.type[3]);
         sb.append(' ').append(']');
         return sb.toString();
      }

      /**
       * Returns the chunk's type promoted from bytes to characters and
       * concatenated into a String.
       *
       * @return the string
       */
      public String typeToString ( ) {

         final StringBuilder sb = new StringBuilder(4);
         sb.append(( char ) this.type[0]);
         sb.append(( char ) this.type[1]);
         sb.append(( char ) this.type[2]);
         sb.append(( char ) this.type[3]);
         return sb.toString();
      }

   }

   /**
    * Describes possible color formats supported by the .png file format: 0
    * (grey scale with alpha), 2 (red green and blue), 3 (indexed), 4 (gray
    * scale) and 6 (red green and blue with alpha).
    */
   public enum ColorFormat {

      /**
       * Black and white (grey scale) with alpha.
       */
      ABW ( ( byte ) 0 ),

      /**
       * Red, blue, green with alpha.
       */
      ARGB ( ( byte ) 6 ),

      /**
       * Black and white (grey scale).
       */
      BW ( ( byte ) 4 ),

      /**
       * Indexed color.
       */
      INDEXED ( ( byte ) 3 ),

      /**
       * Red, blue green.
       */
      RGB ( ( byte ) 2 );

      /**
       * The data flag.
       */
      private final byte flag;

      /**
       * The default constructor.
       *
       * @param f the flag
       */
      private ColorFormat ( final byte f ) { this.flag = f; }

      /**
       * Get the enumerations flag code.
       *
       * @return the code
       */
      public int getFlag ( ) { return this.flag; }

      /**
       * Returns a color format flag given a flag.
       *
       * @param f the flag
       *
       * @return the constant
       */
      public static ColorFormat fromFlag ( final byte f ) {

         switch ( f ) {
            case 0:
               return ABW;

            case 2:
               return RGB;

            case 3:
               return INDEXED;

            case 4:
               return BW;

            case 6:
               return ARGB;

            default:
               return ABW;
         }
      }

   }

   /**
    * Defines a chunk of portable network graphics (.png) information.
    * Contains data as specified in zlib.
    */
   public static class IDATChunk extends Chunk {

      /**
       * Constructs a data chunk from four byte arrays.
       *
       * @param length the length array
       * @param type   the type array
       * @param data   the data array
       * @param crc    the CRC array
       */
      public IDATChunk ( final byte[] length, final byte[] type,
         final byte[] data, final byte[] crc ) {

         super(length, type, data, crc);
      }

      /**
       * Promotes a chunk to an IDATChunk. Should only be done if the chunk's
       * type is known to be "IDAT".
       *
       * @param chunk the chunk
       */
      public IDATChunk ( final Chunk chunk ) {

         this(chunk.length, chunk.type, chunk.data, chunk.crc);
      }

   }

   /**
    * Defines a header chunk for portable network graphics (.png) information.
    * Contains a type description, a measure of the length of data in the
    * chunk, the data, and a cyclic redundancy check (CRC).<br>
    * <br>
    * The data is expected to be 13 bytes long. Within this data should be:
    * <ol>
    * <li>width: 4 bytes, in 0 to 3</li>
    * <li>height: 4 bytes, in 4 to 7</li>
    * <li>bit depth: 1 byte, at 8</li>
    * <li>color type: 1 byte, at 9</li>
    * <li>compression method: 1 byte, at 10</li>
    * <li>filter method: 1 byte, at 11</li>
    * <li>interlace method: 1 byte, at 12</li>
    * </ol>
    */
   public static class IHDRChunk extends Chunk {

      /**
       * Constructs a header chunk from four byte arrays.
       *
       * @param length the length array
       * @param type   the type array
       * @param data   the data array
       * @param crc    the CRC array
       */
      public IHDRChunk ( final byte[] length, final byte[] type,
         final byte[] data, final byte[] crc ) {

         super(length, type, data, crc);
      }

      /**
       * Promotes a chunk to an IHDRChunk. Should only be done if the chunk's
       * type is known to be "IHDR".
       *
       * @param chunk the chunk
       */
      public IHDRChunk ( final Chunk chunk ) {

         this(chunk.length, chunk.type, chunk.data, chunk.crc);
      }

      /**
       * Returns the number of bits per sample. Valid values are 1, 2, 4, 8, and
       * 16 bits per sample.
       *
       * @return the bit depth
       */
      public int bitDepth ( ) { return this.data[8] & 0xff; }

      /**
       * Returns the color format of the image as an enumeration constant.
       *
       * @return the format
       */
      public ColorFormat colorFormat ( ) {

         return ColorFormat.fromFlag(this.data[9]);
      }

      /**
       * Returns the color format of the image as an integer. Expected values
       * are:
       * <ul>
       * <li>0: grey scale</li>
       * <li>2: red, green, blue</li>
       * <li>3: indexed</li>
       * <li>4: gray scale with alpha</li>
       * <li>6: red, green, blue and alpha</li>
       * </ul>
       *
       * @return the color type
       */
      public int colorFormatInt ( ) { return this.data[9] & 0xff; }

      /**
       * Returns the compression method of the image. Expected to be zero.
       *
       * @return the compression method
       */
      public int compression ( ) { return this.data[10] & 0xff; }

      /**
       * Returns the filter method of the image. Expected to be zero.
       *
       * @return the filter method
       */
      public int filter ( ) { return this.data[11] & 0xff; }

      /**
       * Gets the height of the image as an integer. Composites bytes 4 to 7 of
       * the data array.
       *
       * @return the height
       */
      public int height ( ) {

         int result = 0;
         for ( int i = 4; i < 8; ++i ) {
            result <<= 0x08;
            result |= this.data[i] & 0xff;
         }
         return result;
      }

      /**
       * Gets the interlace method of the image. Expected to be either 0 (no
       * interlace) or 1 (adam7).
       *
       * @return the interlace method
       */
      public int interlace ( ) { return this.data[12] & 0xff; }

      /**
       * Gets the width of the image as an integer. Composites bytes 0 to 3 of
       * the data array.
       *
       * @return the width
       */
      public int width ( ) {

         int result = 0;
         for ( int i = 0; i < 4; ++i ) {
            result <<= 0x08;
            result |= this.data[i] & 0xff;
         }
         return result;
      }

   }

   /**
    * Defines a physical dimensions chunk for portable network graphics (.png)
    * information.
    */
   public static class PHYSChunk extends Chunk {

      /**
       * Constructs a data chunk from four byte arrays.
       *
       * @param length the length array
       * @param type   the type array
       * @param data   the data array
       * @param crc    the CRC array
       */
      public PHYSChunk ( final byte[] length, final byte[] type,
         final byte[] data, final byte[] crc ) {

         super(length, type, data, crc);
      }

      /**
       * Promotes a chunk to an PHYSChunk. Should only be done if the chunk's
       * type is known to be "pHYs".
       *
       * @param chunk the chunk
       */
      public PHYSChunk ( final Chunk chunk ) {

         this(chunk.length, chunk.type, chunk.data, chunk.crc);
      }

      /**
       * Returns the pixels per unit on the horizontal or x axis. Composites
       * bytes 0 to 3 of the data array.
       *
       * @return the pixels per unit
       */
      public int ppux ( ) {

         int result = 0;
         for ( int i = 0; i < 4; ++i ) {
            result <<= 0x08;
            result |= this.data[i] & 0xff;
         }
         return result;
      }

      /**
       * Returns the pixels per unit on the vertical or y axis. Composites bytes
       * 4 to 7 of the data array.
       *
       * @return the pixels per unit
       */
      public int ppuy ( ) {

         int result = 0;
         for ( int i = 4; i < 8; ++i ) {
            result <<= 0x08;
            result |= this.data[i] & 0xff;
         }
         return result;
      }

      /**
       * Returns the unit specifier: 1 for meters, 0 for unknown.
       *
       * @return the unit specifier
       */
      public int unitCode ( ) { return this.data[8]; }

   }

}

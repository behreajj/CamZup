package camzup.core;

import java.io.File;
import java.nio.file.Files;

import java.util.ArrayList;

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
    */
   private static final byte[] SIG_PNG;

   static {
      SIG_PNG = new byte[] { ( byte ) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a,
         0x0a };
   }

   public static Img parsePng ( final String filePath, final Img target ) {

      final Chunk[] chunks = PngParser.parsePngToChunks(filePath);
      final int chunksLen = chunks.length;

      int width = 2;
      int height = 2;
      int bitDepth = 8;
      ColorFormat clrFmt = ColorFormat.ARGB;
      int compression = 0;
      int filter = 0;
      int interlace = 0;
      boolean ended = false;

      // BitSet zlibData = new BitSet();
      // ArrayList < Byte > zLibData = new ArrayList <>();

      for ( int i = 0; i < chunksLen; ++i ) {
         final Chunk chunk = chunks[i];
         final String chunkType = chunk.typeToString();
         System.out.println(chunkType);

         final int chunkTypeHash = chunkType.hashCode();
         // System.out.println(chunkTypeHash);

         switch ( chunkTypeHash ) {
            case 2246125:
               /* "IHDR" */
               final IHDRChunk header = new IHDRChunk(chunk);
               width = header.width();
               height = header.height();
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
               // byte[] dt = chunk.data;
               // int dtLen = dt.length;
               // for ( int j = 0; j < dtLen; ++j ) {
               // zLibData.add(dt[j]);
               // }
               // new IDATChunk(chunk);

               /*
                * Meant to be read as one big chunk where only the first IDAT
                * has a zlib header.
                */

               break;

            case 2243538:
               /* "IEND" */

               ended = true;
               break;

            default:
         }
      }

      if ( ended ) { target.reallocate(width, height); }

      // byte zlibCmf = zLibData.remove(0);
      // byte zlibExtraFlags = zLibData.remove(0);
      // byte zLibCheck1 = zLibData.remove(zLibData.size() - 1);
      // byte zLibCheck0 = zLibData.remove(zLibData.size() - 1);

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
      final ArrayList < Chunk > chunkBuff = new ArrayList <>();

      try {
         final byte[] content = Files.readAllBytes(file.toPath());
         int cursor = 0;
         final int len = content.length;
         if ( len < 8 ) {
            throw new Exception("Insufficient content in file to read header.");
         }

         /* Check header. */
         for ( ; cursor < 8; ++cursor ) {
            if ( content[cursor] != PngParser.SIG_PNG[cursor] ) {
               throw new Exception(
                  "File header in bytes does not match .png standard: "
                     + "{ 137, 80, 78, 71, 13, 10, 26, 10 }.");
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

            chunkBuff.add(chunk);
            /* @formatter:on */
         }

      } catch ( final Exception e ) {
         e.printStackTrace();
      }

      /* Convert array list to array. */
      final int chunksLen = chunkBuff.size();
      final Chunk[] chunks = new Chunk[chunksLen];
      chunkBuff.toArray(chunks);
      return chunks;

   }

   /**
    * A chunk signature describing the data of a .png file. In decimal format:
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
    * </pre></code> When the bytes are converted to characters, they spell out
    * the signature, i.e., 'I', 'D', 'A', 'T'.
    *
    * @return the byte array
    */
   public static byte[] sigIdatBytes ( ) {

      return new byte[] { 0x49, 0x44, 0x41, 0x54 };
   }

   /**
    * A chunk signature describing the end of the .png file. In decimal
    * format:
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
    * </pre></code> When the bytes are converted to characters, they spell out
    * the signature, i.e., 'I', 'E', 'N', 'D'.
    *
    * @return the byte array
    */
   public static byte[] sigIEndBytes ( ) {

      return new byte[] { 0x49, 0x45, 0x4e, 0x44 };
   }

   /**
    * A chunk signature describing the file's width, height, bit depth, color
    * type, compression method, filter method and interlace method. In decimal
    * format:
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
    * When the bytes are converted to characters, they spell out the
    * signature, i.e., 'I', 'H', 'D', 'R'.
    *
    * @return the byte array
    */
   public static byte[] sigIhdrBytes ( ) {

      return new byte[] { 0x49, 0x48, 0x44, 0x52 };
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
       * Returns a the chunk's data arranged as an array of bytes promoted to
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
       * Returns the chunk's data promoted from bytes to characters and
       * concatenated into a String. This data is not expected to be human
       * readable. Instead, it is useful to compare the parser's output with
       * that of a hex editing tool.
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
      ABW ( 0 ),

      /**
       * Red, blue, green with alpha.
       */
      ARGB ( 6 ),

      /**
       * Black and white (grey scale).
       */
      BW ( 4 ),

      /**
       * Indexed color.
       */
      INDEXED ( 3 ),

      /**
       * Red, blue green.
       */
      RGB ( 2 );

      /**
       * The data flag.
       */
      private final int flag;

      /**
       * The default constructor.
       *
       * @param f the flag
       */
      private ColorFormat ( final int f ) { this.flag = f; }

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
      public static ColorFormat fromFlag ( final int f ) {

         switch ( f ) {
            case 2:
               return RGB;

            case 3:
               return INDEXED;

            case 4:
               return BW;

            case 6:
               return ARGB;

            case 0:
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

         // TODO: Provide functionality to convert to bit array.
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

         return ColorFormat.fromFlag(this.data[9] & 0xff);
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

   public static class ZLibBlock {
      final int check;
      final byte cmf;
      final byte[] data;
      final byte extraFlags;

      public ZLibBlock ( final byte cm, final byte efs, final byte[] data,
         final int check ) {

         this.cmf = cm;
         this.extraFlags = efs;
         this.data = data;
         this.check = check;
      }

   }

}

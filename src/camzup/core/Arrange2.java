package camzup.core;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Provides methods to align and distribute entities.
 */
public abstract class Arrange2 {

   /**
    * Discourage overriding with a private constructor.
    */
   private Arrange2 ( ) {}

   /**
    * Aligns all mesh entities in the array to the bottom edge of a bounds.
    *
    * @param b   the bounds
    * @param mes the mesh entities
    */
   public static void alignBottom ( final Bounds2 b, final MeshEntity2[] mes ) {

      Arrange2.alignBottom(b, mes, 1, 1.0f);
   }

   /**
    * Aligns all mesh entities in the array to the bottom edge of a bounds.
    * The sign indicates whether to align inside the edge (1), on the edge (0)
    * or outside the edge (-1).
    *
    * @param b    the bounds
    * @param mes  the mesh entities
    * @param sign the alignment sign
    * @param fac  the factor
    */
   public static void alignBottom ( final Bounds2 b, final MeshEntity2[] mes,
      final int sign, final float fac ) {

      final float sgnVrf = 0.5f * Utils.sign(sign);
      final int len = mes.length;
      final Vec2 bMin = b.min;
      final float bottom = bMin.y;
      final Bounds2 eb = new Bounds2();
      final Vec2 extent = new Vec2();
      final Vec2 pos = new Vec2();

      for ( int i = 0; i < len; ++i ) {
         final MeshEntity2 entity = mes[i];
         MeshEntity2.calcBounds(entity, eb);
         Bounds2.extentUnsigned(eb, extent);
         entity.getLocation(pos);
         pos.set(pos.x, bottom + extent.y * sgnVrf);
         entity.moveTo(pos, fac);
      }
   }

   /**
    * Aligns all mesh entities in the array to the horizontal center of the
    * bounds.
    *
    * @param b   the bounds
    * @param mes the mesh entities
    */
   public static void alignHorizontal ( final Bounds2 b,
      final MeshEntity2[] mes ) {

      Arrange2.alignHorizontal(b, mes, 0, 1.0f);
   }

   /**
    * Aligns all mesh entities in the array to the horizontal center of the
    * bounds. The sign indicates whether to align to the right of the edge
    * (1), on the edge (0) or to the left of the edge (-1).
    *
    * @param b    the bounds
    * @param mes  the mesh entities
    * @param sign the alignment sign
    * @param fac  the factor
    */
   public static void alignHorizontal ( final Bounds2 b,
      final MeshEntity2[] mes, final int sign, final float fac ) {

      final float sgnVrf = 0.5f * Utils.sign(sign);
      final int len = mes.length;
      final Vec2 bCenter = Bounds2.center(b, new Vec2());
      final float xCenter = bCenter.x;
      final Bounds2 eb = new Bounds2();
      final Vec2 extent = new Vec2();
      final Vec2 pos = new Vec2();

      for ( int i = 0; i < len; ++i ) {
         final MeshEntity2 entity = mes[i];
         MeshEntity2.calcBounds(entity, eb);
         Bounds2.extentUnsigned(eb, extent);
         entity.getLocation(pos);
         pos.set(xCenter + extent.x * sgnVrf, pos.y);
         entity.moveTo(pos, fac);
      }
   }

   /**
    * Aligns all mesh entities in the array to the left edge of a bounds.
    *
    * @param b   the bounds
    * @param mes the mesh entities
    */
   public static void alignLeft ( final Bounds2 b, final MeshEntity2[] mes ) {

      Arrange2.alignLeft(b, mes, 1, 1.0f);
   }

   /**
    * Aligns all mesh entities in the array to the left edge of a bounds. The
    * sign indicates whether to align inside the edge (1), on the edge (0) or
    * outside the edge (-1).
    *
    * @param b    the bounds
    * @param mes  the mesh entities
    * @param sign the alignment sign
    * @param fac  the factor
    */
   public static void alignLeft ( final Bounds2 b, final MeshEntity2[] mes,
      final int sign, final float fac ) {

      final float sgnVrf = 0.5f * Utils.sign(sign);
      final int len = mes.length;
      final Vec2 bMin = b.min;
      final float left = bMin.x;
      final Bounds2 eb = new Bounds2();
      final Vec2 extent = new Vec2();
      final Vec2 pos = new Vec2();

      for ( int i = 0; i < len; ++i ) {
         final MeshEntity2 entity = mes[i];
         MeshEntity2.calcBounds(entity, eb);
         Bounds2.extentUnsigned(eb, extent);
         entity.getLocation(pos);
         pos.set(left + extent.x * sgnVrf, pos.y);
         entity.moveTo(pos, fac);
      }
   }

   /**
    * Aligns all mesh entities in the array to the right edge of a bounds.
    *
    * @param b   the bounds
    * @param mes the mesh entities
    */
   public static void alignRight ( final Bounds2 b, final MeshEntity2[] mes ) {

      Arrange2.alignRight(b, mes, 1, 1.0f);
   }

   /**
    * Aligns all mesh entities in the array to the right edge of a bounds. The
    * sign indicates whether to align inside the edge (1), on the edge (0) or
    * outside the edge (-1).
    *
    * @param b    the bounds
    * @param mes  the mesh entities
    * @param sign the alignment sign
    * @param fac  the factor
    */
   public static void alignRight ( final Bounds2 b, final MeshEntity2[] mes,
      final int sign, final float fac ) {

      final float sgnVrf = 0.5f * Utils.sign(sign);
      final int len = mes.length;
      final Vec2 bMax = b.max;
      final float right = bMax.x;
      final Bounds2 eb = new Bounds2();
      final Vec2 extent = new Vec2();
      final Vec2 pos = new Vec2();

      for ( int i = 0; i < len; ++i ) {
         final MeshEntity2 entity = mes[i];
         MeshEntity2.calcBounds(entity, eb);
         Bounds2.extentUnsigned(eb, extent);
         entity.getLocation(pos);
         pos.set(right - extent.x * sgnVrf, pos.y);
         entity.moveTo(pos, fac);
      }
   }

   /**
    * Aligns all mesh entities in the array to the top edge of a bounds.
    *
    * @param b   the bounds
    * @param mes the mesh entities
    */
   public static void alignTop ( final Bounds2 b, final MeshEntity2[] mes ) {

      Arrange2.alignTop(b, mes, 1, 1.0f);
   }

   /**
    * Aligns all mesh entities in the array to the top edge of a bounds. The
    * sign indicates whether to align inside the edge (1), on the edge (0) or
    * outside the edge (-1).
    *
    * @param b    the bounds
    * @param mes  the mesh entities
    * @param sign the alignment sign
    * @param fac  the factor
    */
   public static void alignTop ( final Bounds2 b, final MeshEntity2[] mes,
      final int sign, final float fac ) {

      final float sgnVrf = 0.5f * Utils.sign(sign);
      final int len = mes.length;
      final Vec2 bMax = b.max;
      final float top = bMax.y;
      final Bounds2 eb = new Bounds2();
      final Vec2 extent = new Vec2();
      final Vec2 pos = new Vec2();

      for ( int i = 0; i < len; ++i ) {
         final MeshEntity2 entity = mes[i];
         MeshEntity2.calcBounds(entity, eb);
         Bounds2.extentUnsigned(eb, extent);
         entity.getLocation(pos);
         pos.set(pos.x, top - extent.y * sgnVrf);
         entity.moveTo(pos, fac);
      }
   }

   /**
    * Aligns all mesh entities in the array to the vertical center of the
    * bounds.
    *
    * @param b   the bounds
    * @param mes the mesh entities
    */
   public static void alignVertical ( final Bounds2 b,
      final MeshEntity2[] mes ) {

      Arrange2.alignVertical(b, mes, 0, 1.0f);
   }

   /**
    * Aligns all mesh entities in the array to the vertical center of the
    * bounds. The sign indicates whether to align above the edge (1), on the
    * edge (0) or to below the edge (-1).
    *
    * @param b    the bounds
    * @param mes  the mesh entities
    * @param sign the alignment sign
    * @param fac  the factor
    */
   public static void alignVertical ( final Bounds2 b, final MeshEntity2[] mes,
      final int sign, final float fac ) {

      final float sgnVrf = 0.5f * Utils.sign(sign);
      final int len = mes.length;
      final Vec2 bCenter = Bounds2.center(b, new Vec2());
      final float yCenter = bCenter.y;
      final Bounds2 eb = new Bounds2();
      final Vec2 extent = new Vec2();
      final Vec2 pos = new Vec2();

      for ( int i = 0; i < len; ++i ) {
         final MeshEntity2 entity = mes[i];
         MeshEntity2.calcBounds(entity, eb);
         Bounds2.extentUnsigned(eb, extent);
         entity.getLocation(pos);
         pos.set(pos.x, yCenter + extent.y * sgnVrf);
         entity.moveTo(pos, fac);
      }
   }

   /**
    * Distributes all mesh entities in the array horizontally within a bounds.
    *
    * @param b   the bounds
    * @param mes the mesh entities
    */
   public static void distributeHorizontal ( final Bounds2 b,
      final MeshEntity2[] mes ) {

      Arrange2.distributeHorizontal(b, mes, 1, 1.0f);
   }

   /**
    * Distributes all mesh entities in the array horizontally within a bounds.
    * The sign indicates whether to align inside the edge (1), on the edge (0)
    * or to outside the edge (-1).<br>
    * <br>
    * Creates a sorted copy of the input array.
    *
    * @param b    the bounds
    * @param mes  the mesh entities
    * @param sign the alignment sign
    * @param fac  the factor
    */
   public static void distributeHorizontal ( final Bounds2 b,
      final MeshEntity2[] mes, final int sign, final float fac ) {

      final int len = mes.length;
      final MeshEntity2[] sorted = new MeshEntity2[len];
      System.arraycopy(mes, 0, sorted, 0, len);
      Arrays.sort(sorted, new Comparator < MeshEntity2 >() {
         @Override
         public int compare ( final MeshEntity2 l, final MeshEntity2 r ) {

            return Float.compare(l.getLocation(new Vec2()).x, r.getLocation(
               new Vec2()).x);
         }

      });

      final float xMin = b.min.x;
      final float xMax = b.max.x;

      final float toFac = len > 1 ? 1.0f / ( len - 1.0f ) : 0.0f;
      final float facOff = len > 1 ? 0.0f : 0.5f;

      final float sgnVrf = 0.5f * Utils.sign(sign);

      final Bounds2 eb = new Bounds2();
      final Vec2 extent = new Vec2();
      final Vec2 pos = new Vec2();

      for ( int i = 0; i < len; ++i ) {
         final float t = i * toFac + facOff;
         final float u = 1.0f - t;
         final float xDistr = u * xMin + t * xMax;
         final float offset = u * sgnVrf + t * -sgnVrf;
         final MeshEntity2 entity = sorted[i];
         MeshEntity2.calcBounds(entity, eb);
         Bounds2.extentUnsigned(eb, extent);
         entity.getLocation(pos);
         pos.set(xDistr + extent.x * offset, pos.y);
         entity.moveTo(pos, fac);
      }
   }

   /**
    * Distributes all mesh entities in the array vertically within a bounds.
    *
    * @param b   the bounds
    * @param mes the mesh entities
    */
   public static void distributeVertical ( final Bounds2 b,
      final MeshEntity2[] mes ) {

      Arrange2.distributeVertical(b, mes, 1, 1.0f);
   }

   /**
    * Distributes all mesh entities in the array vertically within a bounds.
    * The sign indicates whether to align inside the edge (1), on the edge (0)
    * or to outside the edge (-1).<br>
    * <br>
    * Creates a sorted copy of the input array.
    *
    * @param b    the bounds
    * @param mes  the mesh entities
    * @param sign the alignment sign
    * @param fac  the factor
    */
   public static void distributeVertical ( final Bounds2 b,
      final MeshEntity2[] mes, final int sign, final float fac ) {

      final int len = mes.length;
      final MeshEntity2[] sorted = new MeshEntity2[len];
      System.arraycopy(mes, 0, sorted, 0, len);
      Arrays.sort(sorted, new Comparator < MeshEntity2 >() {
         @Override
         public int compare ( final MeshEntity2 l, final MeshEntity2 r ) {

            return Float.compare(l.getLocation(new Vec2()).y, r.getLocation(
               new Vec2()).y);
         }

      });

      final float yMin = b.min.y;
      final float yMax = b.max.y;

      final float toFac = len > 1 ? 1.0f / ( len - 1.0f ) : 0.0f;
      final float facOff = len > 1 ? 0.0f : 0.5f;

      final float sgnVrf = 0.5f * Utils.sign(sign);

      final Bounds2 eb = new Bounds2();
      final Vec2 extent = new Vec2();
      final Vec2 pos = new Vec2();

      for ( int i = 0; i < len; ++i ) {
         final float t = i * toFac + facOff;
         final float u = 1.0f - t;
         final float yDistr = u * yMin + t * yMax;
         final float offset = u * sgnVrf + t * -sgnVrf;
         final MeshEntity2 entity = sorted[i];
         MeshEntity2.calcBounds(entity, eb);
         Bounds2.extentUnsigned(eb, extent);
         entity.getLocation(pos);
         pos.set(pos.x, yDistr + extent.y * offset);
         entity.moveTo(pos, fac);
      }
   }

   /**
    * Arranges mesh entities into a column.
    *
    * @param b    the bounds
    * @param mes  the mesh entities
    * @param sign the alignment sign
    * @param fac  the factor
    */
   public static void toColumn ( final Bounds2 b, final MeshEntity2[] mes,
      final int sign, final float fac ) {

      Arrange2.alignHorizontal(b, mes, 0, fac);
      Arrange2.distributeVertical(b, mes, sign, fac);
   }

   /**
    * Arranges mesh entities into a row.
    *
    * @param b    the bounds
    * @param mes  the mesh entities
    * @param sign the alignment sign
    * @param fac  the factor
    */
   public static void toRow ( final Bounds2 b, final MeshEntity2[] mes,
      final int sign, final float fac ) {

      Arrange2.alignVertical(b, mes, 0, fac);
      Arrange2.distributeHorizontal(b, mes, sign, fac);
   }

}

package camzup.core;

/**
 * Organizes the components of a 2D mesh into a group of
 * coordinate and texture coordinate such that they can be
 * edited together.
 *
 * This is not used by a mesh internally; it is created upon
 * retrieval from a mesh. All of its components should be
 * treated as references to data within the mesh, not as
 * unique values.
 */
public class Vert2 implements Comparable < Vert2 > {

   /**
    * An abstract class to facilitate the creation of vertex
    * easing functions.
    */
   @Experimental
   static abstract class AbstrEasing
         implements Utils.EasingFuncObj < Vert2 > {

      /**
       * The default constructor.
       */
      public AbstrEasing () {

         super();
      }

      /**
       * A clamped interpolation between the origin and
       * destination. Defers to an unclamped interpolation, which
       * is to be defined by sub-classes of this class.
       *
       * @param origin
       *           the origin vertex
       * @param dest
       *           the destination vertex
       * @param step
       *           a factor in [0.0, 1.0]
       * @param target
       *           the output vertex
       * @return the eased vertex
       */
      @Override
      public Vert2 apply (
            final Vert2 origin,
            final Vert2 dest,
            final Float step,
            final Vert2 target ) {

         if (step <= 0.0f) {
            target.coord.set(origin.coord);
            target.texCoord.set(origin.texCoord);
            return target;
         }

         if (step >= 1.0f) {
            target.coord.set(dest.coord);
            target.texCoord.set(dest.texCoord);
            return target;
         }

         return this.applyUnclamped(origin, dest, step, target);
      }

      /**
       * The interpolation to be defined by subclasses.
       *
       * @param origin
       *           the origin vertex
       * @param dest
       *           the destination vertex
       * @param step
       *           a factor in [0.0, 1.0]
       * @param target
       *           the output vertex
       * @return the eased vertex
       */
      public abstract Vert2 applyUnclamped (
            final Vert2 origin,
            final Vert2 dest,
            final float step,
            final Vert2 target );

      /**
       * Returns the simple name of this class.
       *
       * @return the string
       */
      @Override
      public String toString () {

         return this.getClass().getSimpleName();
      }
   }

   /**
    * A functional class to ease between two vertices with
    * linear interpolation.
    */
   @Experimental
   public static class Lerp extends AbstrEasing {

      /**
       * The default constructor.
       */
      public Lerp () {

         super();
      }

      /**
       * Lerps between two vertices by a step using the formula (1
       * - t) * a + b .
       *
       * @param origin
       *           the origin vertex
       * @param dest
       *           the destination vertex
       * @param step
       *           the step
       * @param target
       *           the output vertex
       * @return the eased vertex
       */
      @Override
      public Vert2 applyUnclamped (
            final Vert2 origin,
            final Vert2 dest,
            final float step,
            final Vert2 target ) {

         final float u = 1.0f - step;

         final Vec2 orCo = origin.coord;
         final Vec2 ortC = origin.texCoord;

         final Vec2 deCo = dest.coord;
         final Vec2 detC = dest.texCoord;

         target.coord.set(
               u * orCo.x + step * deCo.x,
               u * orCo.y + step * deCo.y);

         target.texCoord.set(
               u * ortC.x + step * detC.x,
               u * ortC.y + step * detC.y);

         return target;
      }
   }

   /**
    * The coordinate of the vertex in world space.
    */
   public Vec2 coord;

   /**
    * The texture (UV) coordinate for an image mapped onto the
    * mesh.
    */
   public Vec2 texCoord;

   /**
    * The default constructor. When used, the vertex's coord,
    * normal and texCoord will remain null.
    */
   public Vert2 () {

   }

   /**
    * Constructs a vertex from a coordinate and texture
    * coordinate.
    *
    * @param coord
    *           the coordinate
    * @param texCoord
    *           the texture coordinate
    */
   public Vert2 (
         final Vec2 coord,
         final Vec2 texCoord ) {

      this.set(coord, texCoord);
   }

   /**
    * Tests this vertex for equivalence with another.
    *
    * @return the evaluation
    */
   protected boolean equals ( final Vert2 vert2 ) {

      if (this.coord == null) {
         if (vert2.coord != null) {
            return false;
         }
      } else if (!this.coord.equals(vert2.coord)) {
         return false;
      }

      if (this.texCoord == null) {
         if (vert2.texCoord != null) {
            return false;
         }
      } else if (!this.texCoord.equals(vert2.texCoord)) {
         return false;
      }

      return true;
   }

   /**
    * Compares this vertex to another by hash code.
    *
    * @param vert
    *           the comparisand
    * @return the comparison
    */
   @Override
   public int compareTo ( final Vert2 vert ) {

      final int a = System.identityHashCode(this);
      final int b = System.identityHashCode(vert);
      return a < b ? -1 : a > b ? 1 : 0;
   }

   /**
    * Tests this vertex for equivalence with another object.
    *
    * @return the evaluation
    */
   @Override
   public boolean equals ( final Object obj ) {

      if (this == obj) {
         return true;
      }

      if (obj == null) {
         return false;
      }

      if (this.getClass() != obj.getClass()) {
         return false;
      }

      return this.equals((Vert2) obj);
   }

   /**
    * Returns a hash code for this vertex based on its
    * coordinate and texture coordinate.
    *
    * @return the hash
    */
   @Override
   public int hashCode () {

      int hash = IUtils.HASH_BASE;
      hash = hash * IUtils.HASH_MUL
            ^ (this.coord == null ? 0 : this.coord.hashCode());
      hash = hash * IUtils.HASH_MUL
            ^ (this.texCoord == null ? 0 : this.texCoord.hashCode());
      return hash;
   }

   /**
    * Sets the coordinate and texture coordinate of the vertex
    * by reference.
    *
    * @param coord
    *           the coordinate
    * @param texCoord
    *           the texture coordinate
    * @return this vertex
    */
   @Chainable
   public Vert2 set (
         final Vec2 coord,
         final Vec2 texCoord ) {

      this.coord = coord;
      this.texCoord = texCoord;
      return this;
   }

   /**
    * Returns a string representation of this vertex.
    *
    * @return the string
    */
   @Override
   public String toString () {

      return this.toString(4);
   }

   /**
    * Returns a string representation of this vertex.
    *
    * @param places
    *           the number of places
    * @return the string
    */
   public String toString ( final int places ) {

      return new StringBuilder(256)
            .append("{ coord: ")
            .append(this.coord.toString(places))
            .append(", texCoord: ")
            .append(this.texCoord.toString(places))
            .append(' ').append('}')
            .toString();
   }
}
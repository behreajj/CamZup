package camzup.core;

/**
 * Organizes the components of a 2D mesh into a group of coordinate and
 * texture coordinate such that they can be edited together. This is not
 * used by a mesh internally; it is created upon retrieval from a mesh. All
 * of its components should be treated as references to data within the
 * mesh, not as independent values.
 */
public class Vert2 implements Comparable < Vert2 > {

   /**
    * The coordinate of the vertex in world space.
    */
   public Vec2 coord;

   /**
    * The texture (UV) coordinate for an image mapped onto the mesh.
    */
   public Vec2 texCoord;

   /**
    * The default constructor. When used, the vertex's coordinate, normal and
    * texCoord will remain null.
    */
   public Vert2 ( ) {}

   /**
    * Constructs a vertex from a coordinate and texture coordinate.
    *
    * @param coord    the coordinate
    * @param texCoord the texture coordinate
    */
   public Vert2 ( final Vec2 coord, final Vec2 texCoord ) {

      this.set(coord, texCoord);
   }

   /**
    * Compares this vertex to another by hash code.
    *
    * @param vert the comparisand
    *
    * @return the comparison
    */
   @Override
   public int compareTo ( final Vert2 vert ) {

      return this.coord.compareTo(vert.coord);
   }

   /**
    * Tests this vertex for equivalence with another object.
    *
    * @return the evaluation
    */
   @Override
   public boolean equals ( final Object obj ) {

      if ( this == obj ) { return true; }
      if ( obj == null ) { return false; }
      if ( this.getClass() != obj.getClass() ) { return false; }
      return this.equals(( Vert2 ) obj);
   }

   /**
    * Returns a hash code for this vertex based on its coordinate and texture
    * coordinate.
    *
    * @return the hash
    */
   @Override
   public int hashCode ( ) { return this.coord.hashCode(); }

   /**
    * Sets the coordinate and texture coordinate of the vertex by reference.
    *
    * @param coord    the coordinate
    * @param texCoord the texture coordinate
    *
    * @return this vertex
    */
   public Vert2 set ( final Vec2 coord, final Vec2 texCoord ) {

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
   public String toString ( ) { return this.toString(4); }

   /**
    * Returns a string representation of this vertex.
    *
    * @param places the number of places
    *
    * @return the string
    */
   public String toString ( final int places ) {

      final StringBuilder sb = new StringBuilder(256);
      sb.append("{ coord: ");
      sb.append(this.coord.toString(places));
      sb.append(", texCoord: ");
      sb.append(this.texCoord.toString(places));
      sb.append(' ');
      sb.append('}');
      return sb.toString();
   }

   /**
    * Tests this vertex for equivalence with another.
    *
    * @return the evaluation
    */
   protected boolean equals ( final Vert2 vert2 ) {

      if ( this.coord == null ) {
         if ( vert2.coord != null ) { return false; }
      } else if ( !this.coord.equals(vert2.coord) ) { return false; }

      return true;
   }

   /**
    * Tests to see if two vertices share the same coordinate according to the
    * default tolerance, {@link IUtils#DEFAULT_EPSILON}.
    *
    * @param a the left comparisand
    * @param b the right comparisand
    *
    * @return the evaluation
    */
   public static boolean approxCoord ( final Vert2 a, final Vert2 b ) {

      return Vert2.approxCoord(a, b, IUtils.DEFAULT_EPSILON);
   }

   /**
    * Tests to see if two vertices share the same coordinate according to a
    * tolerance.
    *
    * @param a         the left comparisand
    * @param b         the right comparisand
    * @param tolerance the tolerance
    *
    * @return the evaluation
    */
   public static boolean approxCoord ( final Vert2 a, final Vert2 b,
      final float tolerance ) {

      return a == b || Vec2.approx(a.coord, b.coord, tolerance);
   }

}
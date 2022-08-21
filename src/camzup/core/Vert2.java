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
    * Compares this vertex to another.
    *
    * @param vert the comparisand
    *
    * @return the comparison
    */
   @Override
   public int compareTo ( final Vert2 vert ) {

      final int tcComp = this.texCoord.compareTo(vert.texCoord);
      final int coComp = this.coord.compareTo(vert.coord);
      return tcComp != 0 ? tcComp : coComp;
   }

   /**
    * Tests this vertex for equivalence with another object.
    *
    * @param obj the object
    *
    * @return the evaluation
    */
   @Override
   public boolean equals ( final Object obj ) {

      if ( this == obj ) { return true; }
      if ( obj == null || this.getClass() != obj.getClass() ) { return false; }
      return this.equals(( Vert2 ) obj);
   }

   /**
    * Returns a hash code for this vertex.
    *
    * @return the hash code
    */
   @Override
   public int hashCode ( ) {

      final int prime = 31;
      int result = 1;
      result = prime * result + ( this.coord == null ? 0 : this.coord
         .hashCode() );
      result = prime * result + ( this.texCoord == null ? 0 : this.texCoord
         .hashCode() );
      return result;
   }

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
   public String toString ( ) { return this.toString(IUtils.FIXED_PRINT); }

   /**
    * Returns a string representation of this vertex.
    *
    * @param places the number of places
    *
    * @return the string
    */
   public String toString ( final int places ) {

      return this.toString(new StringBuilder(256), places).toString();
   }

   /**
    * Internal helper function to assist with methods that need to print many
    * vertices. Appends to an existing {@link StringBuilder}.
    *
    * @param sb     the string builder
    * @param places the number of places
    *
    * @return the string builder
    */
   StringBuilder toString ( final StringBuilder sb, final int places ) {

      sb.append("{ coord: ");
      this.coord.toString(sb, places);
      sb.append(", texCoord: ");
      this.texCoord.toString(sb, places);
      sb.append(' ');
      sb.append('}');
      return sb;
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

      if ( this.texCoord == null ) {
         if ( vert2.texCoord != null ) { return false; }
      } else if ( !this.texCoord.equals(vert2.texCoord) ) { return false; }

      return true;
   }

   /**
    * Tests to see if two vertices share the same coordinate according to the
    * default tolerance, {@link IUtils#EPSILON}.
    *
    * @param a the left comparisand
    * @param b the right comparisand
    *
    * @return the evaluation
    */
   public static boolean approxCoord ( final Vert2 a, final Vert2 b ) {

      return Vert2.approxCoord(a, b, IUtils.EPSILON);
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
    *
    * @see Vec2#approx(Vec2, Vec2, float)
    */
   public static boolean approxCoord ( final Vert2 a, final Vert2 b,
      final float tolerance ) {

      return a == b || Vec2.approx(a.coord, b.coord, tolerance);
   }

}
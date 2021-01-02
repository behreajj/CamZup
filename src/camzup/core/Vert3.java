package camzup.core;

/**
 * Organizes the components of a 3D mesh into a group of coordinate, normal
 * and texture coordinate such that they can be edited together. This is
 * not used by a mesh internally; it is created upon retrieval from a mesh.
 * All of its components should be treated as references to data within the
 * mesh, not as independent values.
 */
public class Vert3 implements Comparable < Vert3 > {

   /**
    * The coordinate of the vertex in world space.
    */
   public Vec3 coord;

   /**
    * The direction in which light will bounce from the surface of the mesh at
    * the vertex.
    */
   public Vec3 normal;

   /**
    * The texture (UV) coordinate for an image mapped onto the mesh.
    */
   public Vec2 texCoord;

   /**
    * The default constructor. When used, the vertex's coordinate, normal and
    * texCoord will remain null.
    */
   public Vert3 ( ) {}

   /**
    * Constructs a vertex from a coordinate, texture coordinate and normal.
    *
    * @param coord    the coordinate
    * @param texCoord the texture coordinate
    * @param normal   the normal
    */
   public Vert3 ( final Vec3 coord, final Vec2 texCoord, final Vec3 normal ) {

      this.set(coord, texCoord, normal);
   }

   /**
    * Compares this vertex to another.
    *
    * @param vert the comparisand
    *
    * @return the comparison
    */
   @Override
   public int compareTo ( final Vert3 vert ) {

      final int nrComp = this.normal.compareTo(vert.normal);
      final int tcComp = this.texCoord.compareTo(vert.texCoord);
      final int coComp = this.coord.compareTo(vert.coord);
      return nrComp != 0 ? nrComp : tcComp != 0 ? tcComp : coComp;
   }

   /**
    * Returns a hash code for this vector based on its x, y and z components.
    *
    * @return the hash code
    */
   @Override
   public int hashCode ( ) {

      final int prime = 31;
      int result = 1;
      result = prime * result + ( this.coord == null ? 0 : this.coord
         .hashCode() );
      result = prime * result + ( this.normal == null ? 0 : this.normal
         .hashCode() );
      result = prime * result + ( this.texCoord == null ? 0 : this.texCoord
         .hashCode() );
      return result;
   }

   /**
    * Sets the coordinate, texture coordinate and normal of the vertex by
    * reference.
    *
    * @param coord    the coordinate
    * @param texCoord the texture coordinate
    * @param normal   the normal
    *
    * @return this vertex
    */
   public Vert3 set ( final Vec3 coord, final Vec2 texCoord,
      final Vec3 normal ) {

      this.coord = coord;
      this.texCoord = texCoord;
      this.normal = normal;
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

      final StringBuilder sb = new StringBuilder(512);
      sb.append("{ coord: ");
      this.coord.toString(sb, places);
      sb.append(", texCoord: ");
      this.texCoord.toString(sb, places);
      sb.append(", normal: ");
      this.normal.toString(sb, places);
      sb.append(' ');
      sb.append('}');
      return sb.toString();
   }

   /**
    * Tests this vertex for equivalence with another.
    *
    * @return the evaluation
    */
   protected boolean equals ( final Vert3 vert3 ) {

      if ( this.coord == null ) {
         if ( vert3.coord != null ) { return false; }
      } else if ( !this.coord.equals(vert3.coord) ) { return false; }

      if ( this.normal == null ) {
         if ( vert3.normal != null ) { return false; }
      } else if ( !this.normal.equals(vert3.normal) ) { return false; }

      if ( this.texCoord == null ) {
         if ( vert3.texCoord != null ) { return false; }
      } else if ( !this.texCoord.equals(vert3.texCoord) ) { return false; }

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
   public static boolean approxCoord ( final Vert3 a, final Vert3 b ) {

      return Vert3.approxCoord(a, b, IUtils.EPSILON);
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
   public static boolean approxCoord ( final Vert3 a, final Vert3 b,
      final float tolerance ) {

      return a == b || Vec3.approx(a.coord, b.coord, tolerance);
   }

   /**
    * Returns the orientation of the vertex as a quaternion based on the
    * vertex's normal.
    *
    * @param vert       the vertex
    * @param handedness the handedness
    * @param target     the output quaternion
    *
    * @return the orientation
    *
    * @see Quaternion#fromDir(Vec3, Handedness, Quaternion)
    */
   @Experimental
   public static Quaternion orientation ( final Vert3 vert,
      final Handedness handedness, final Quaternion target ) {

      return Quaternion.fromDir(vert.normal, handedness, target);
   }

   /**
    * Returns the orientation of the vertex as a transform based on the
    * vertex's normal.
    *
    * @param vert       the vertex
    * @param handedness the handedness
    * @param target     the output transform
    *
    * @return the orientation
    *
    * @see Transform3#fromDir(Vec3, Handedness, Transform3)
    */
   @Experimental
   public static Transform3 orientation ( final Vert3 vert,
      final Handedness handedness, final Transform3 target ) {

      Transform3.fromDir(vert.normal, handedness, target);
      target.moveTo(vert.coord);
      return target;
   }

   /**
    * Returns the orientation of the vertex as a ray based on the vertex's
    * normal and coordinate.
    *
    * @param vert   the vertex
    * @param target the output transform
    *
    * @return the orientation
    */
   public static Ray3 orientation ( final Vert3 vert, final Ray3 target ) {

      return target.set(vert.coord, vert.normal);
   }

}
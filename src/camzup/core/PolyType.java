package camzup.core;

/**
 * The type of polygon, or face, produced by a mesh function.
 */
public enum PolyType {

   /**
    * Create an n-sided polygon.
    */
   NGON ( ),

   /**
    * Create a quadrilateral.
    */
   QUAD ( ),

   /**
    * Create a triangle.
    */
   TRI ( );

   /**
    * The default constructor.
    */
   PolyType ( ) {}

}
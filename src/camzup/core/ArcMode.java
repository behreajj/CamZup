package camzup.core;

/**
 * Determines how an arc will be displayed.
 */
public enum ArcMode {

   /**
    * In addition to the arc, draws a straight line from the start to the stop
    * angle.
    */
   CHORD ( ),

   /**
    * Draws a stroke from the start to the stop angle.
    */
   OPEN ( ),

   /**
    * In addition to the arc, draws a straight line from the start angle to
    * the center of the arc, to the stop angle.
    */
   PIE ( );

   /**
    * The default constructor.
    */
   ArcMode ( ) {

      // TODO: Add a sector mode, so as to make mesh and curve arcs consistent?
   }

}

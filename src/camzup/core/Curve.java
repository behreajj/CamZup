package camzup.core;

/**
 * An abstract parent for curve objects.
 */
public abstract class Curve extends EntityData implements ICurve {

   /**
    * Determines how an arc will be displayed.
    */
   public enum ArcMode {

      /**
       * In addition to the arc, draws a straight line from the
       * start to the stop angle.
       */
      CHORD (),

      /**
       * Draws a stroke from the start to the stop angle.
       */
      OPEN (),

      /**
       * In addition to the arc, draws a straight line from the
       * start angle to the center of the arc, to the stop angle.
       */
      PIE ();

      /**
       * The default constructor.
       */
      private ArcMode () {

      }
   }

   /**
    * The default constructor.
    */
   protected Curve () {

      super();
   }

   /**
    * Construct a curve and give it a name.
    *
    * @param name
    *           the name
    */
   protected Curve ( final String name ) {

      super(name);
   }
}
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
    * Whether or not the curve is a closed loop.
    */
   public boolean closedLoop;

   /**
    * The material associated with this curve in a curve
    * entity.
    */
   public int materialIndex = 0;

   /**
    * The default constructor.
    */
   protected Curve () {

      super();
      this.closedLoop = false;
   }

   /**
    * Constructs a curve and specifies whether it is a loop.
    *
    * @param cl
    *           the loop
    */
   protected Curve ( final boolean cl ) {

      super();
      this.closedLoop = cl;
   }

   /**
    * Constructs a curve and gives it a name.
    *
    * @param name
    *           the name
    */
   protected Curve ( final String name ) {

      super(name);
      this.closedLoop = false;
   }

   /**
    * Constructs a named curve and specifies whether it is a
    * loop.
    *
    * @param name
    *           the name
    * @param cl
    *           the loop
    */
   protected Curve (
         final String name,
         final boolean cl ) {

      super(name);
      this.closedLoop = cl;
   }

   /**
    * Gets this curve's material index.
    *
    * @return the material index
    */
   public int getMaterialIndex () {

      return this.materialIndex;
   }

   /**
    * Sets this curve's material index.
    *
    * @param i
    *           the index
    * @return this curve
    */
   @Chainable
   public Curve setMaterialIndex ( final int i ) {

      this.materialIndex = i;
      return this;
   }

   /**
    * Toggles whether or not this is a closed loop.
    *
    * @return this curve
    */
   @Override
   @Chainable
   public Curve toggleLoop () {

      this.closedLoop = !this.closedLoop;
      return this;
   }
}
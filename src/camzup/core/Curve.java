package camzup.core;

/**
 * An abstract parent for curve objects.
 */
public abstract class Curve extends EntityData implements ICurve {

   /**
    * Whether or not the curve is a closed loop.
    */
   public boolean closedLoop;

   /**
    * The material associated with this curve in a curve entity.
    */
   public int materialIndex = 0;

   /**
    * The default constructor.
    */
   protected Curve ( ) {

      super();
      this.closedLoop = false;
   }

   /**
    * Constructs a curve and specifies whether it is a loop.
    *
    * @param cl the loop
    */
   protected Curve ( final boolean cl ) {

      super();
      this.closedLoop = cl;
   }

   /**
    * Constructs a curve and gives it a name.
    *
    * @param name the name
    */
   protected Curve ( final String name ) {

      super(name);
      this.closedLoop = false;
   }

   /**
    * Constructs a named curve and specifies whether it is a loop.
    *
    * @param name the name
    * @param cl   the loop
    */
   protected Curve ( final String name, final boolean cl ) {

      super(name);
      this.closedLoop = cl;
   }

   /**
    * Gets this curve's material index.
    *
    * @return the material index
    */
   public int getMaterialIndex ( ) { return this.materialIndex; }

   /**
    * Sets this curve's material index.
    *
    * @param i the index
    *
    * @return this curve
    */

   public Curve setMaterialIndex ( final int i ) {

      this.materialIndex = i < 0 ? 0 : i;
      return this;
   }

   /**
    * Toggles whether or not this is a closed loop.
    *
    * @return this curve
    */
   @Override

   public Curve toggleLoop ( ) {

      this.closedLoop = !this.closedLoop;
      return this;
   }

   /**
    * Handle magnitude for orthogonal handles when four curve knots are used
    * to approximate an ellipse or circle (90 degrees per knot),
    * {@value Curve#HNDL_MAG_ORTHO} .
    */
   public static final float HNDL_MAG_ORTHO = 0.552285f;

   /**
    * Half the orthogonal handle magnitude for curve knots,
    * {@value Curve#HNDL_MAG_ORTHO_2} .
    */
   public static final float HNDL_MAG_ORTHO_2 = Curve.HNDL_MAG_ORTHO * 0.5f;

   /**
    * Half the orthogonal handle magnitude for curve knots,
    * {@value Curve#HNDL_MAG_ORTHO_2_D} .
    */
   public static final double HNDL_MAG_ORTHO_2_D = Curve.HNDL_MAG_ORTHO_D
      * 0.5d;

   /**
    * Handle magnitude for orthogonal handles when four curve knots are used
    * to approximate an ellipse or circle (90 degrees per knot),
    * {@value Curve#HNDL_MAG_ORTHO_D} .
    */
   public static final double HNDL_MAG_ORTHO_D = 0.552285d;

   /**
    * Default number of cubic Bezier knots used to approximate a circle.
    */
   public static final int KNOTS_PER_CIRCLE = 4;

}
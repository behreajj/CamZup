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
   protected Curve ( ) { this.closedLoop = false; }

   /**
    * Constructs a curve and specifies whether it is a loop.
    *
    * @param cl the loop
    */
   protected Curve ( final boolean cl ) { this.closedLoop = cl; }

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
    * Tests this curve for equivalence with an object.
    *
    * @param obj the object
    *
    * @return the equivalence
    */
   @Override
   public boolean equals ( final Object obj ) {

      if ( this == obj ) { return true; }
      if ( !super.equals(obj) || this.getClass() != obj.getClass() ) {
         return false;
      }
      final Curve other = ( Curve ) obj;
      return this.closedLoop == other.closedLoop;
   }

   /**
    * Gets this curve's material index.
    *
    * @return the material index
    */
   public int getMaterialIndex ( ) { return this.materialIndex; }

   /**
    * Generates a hash code for this curve.
    */
   @Override
   public int hashCode ( ) {

      final int hash = super.hashCode();
      return hash * IUtils.HASH_MUL ^ ( this.closedLoop ? 1231 : 1237 );
   }

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
    * Returns a string representation of the curve.
    *
    * @return the string
    */
   @Override
   public String toString ( ) {

      final StringBuilder sb = new StringBuilder(64);
      sb.append("{ name: \"");
      sb.append(this.name);
      sb.append("\", closedLoop: ");
      sb.append(this.closedLoop);
      sb.append(", materialIndex: ");
      sb.append(this.materialIndex);
      sb.append(' ');
      sb.append('}');
      return sb.toString();
   }

}
package camzup.core;

/**
 * An abstract class to serve as a parent for complex
 * numbers; stores the size of the number.
 */
public abstract class Imaginary implements IImaginary {

   /**
    * The unique identification for serialized classes.
    */
   private static final long serialVersionUID = -7650440766633675250L;

   /**
    * Number of components held by the complex number.
    */
   protected final int length;

   /**
    * The default constructor
    *
    * @param length
    *           the number of components
    */
   protected Imaginary ( final int length ) {

      this.length = length;
   }

   /**
    * Gets the number of components held by the complex number.
    *
    * @return the length
    */
   @Override
   public int length () {

      return this.length;
   }

}

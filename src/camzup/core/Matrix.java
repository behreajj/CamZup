package camzup.core;

public abstract class Matrix implements IMatrix {

   private static final long serialVersionUID = -6995930494443674601L;

   protected final int size;

   protected Matrix ( final int size ) {

      this.size = size;
   }

   @Override
   public int size () {

      return this.size;
   }
}

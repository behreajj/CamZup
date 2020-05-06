package camzup.core;

/**
 * A material which holds data to display textured materials. Holds a
 * transform that may be applied to UV coordinates.
 */
public abstract class MaterialUv extends Material implements ISpatial2,
   IVolume2 {

   /**
    * The texture tint.
    */
   public final Color tint;

   /**
    * The UV coordinate transform.
    */
   public final Transform2 transform;

   {
      /*
       * This doesn't implement Cloneable because all things with transforms
       * allow it to be set by reference with their constructors, making it
       * ambiguous what the copy functions will do!
       */

      this.tint = Color.white(new Color());
   }

   /**
    * The default constructor.
    */
   public MaterialUv ( ) { this("MaterialUv"); }

   /**
    * Creates a named texture material.
    *
    * @param name the name
    */
   public MaterialUv ( final String name ) {

      super(name);
      this.transform = new Transform2();
   }

   /**
    * Creates a named texture with a transform.
    *
    * @param name      the name
    * @param transform the UV transform
    */
   public MaterialUv ( final String name, final Transform2 transform ) {

      super(name);
      this.transform = transform;
   }

   /**
    * Creates a named texture with a tint and transform.
    *
    * @param name      the name
    * @param transform the UV transform
    * @param tint      the tint color
    */
   public MaterialUv ( final String name, final Transform2 transform,
      final Color tint ) {

      super(name);
      this.transform = transform;
      this.tint.set(tint);
   }

   /**
    * Moves this material by a vector.
    *
    * @param dir the vector
    *
    * @return this material
    */
   @Override
   public MaterialUv moveBy ( final Vec2 dir ) {

      this.transform.moveBy(dir);
      return this;
   }

   /**
    * Moves this material to a location.
    *
    * @param locNew the location
    *
    * @return this material
    */
   @Override
   public MaterialUv moveTo ( final Vec2 locNew ) {

      this.transform.moveTo(locNew);
      return this;
   }

   /**
    * Moves this material to a location over a step in time.
    *
    * @param locNew the location
    * @param step   the step
    *
    * @return this material
    */
   @Override
   public MaterialUv moveTo ( final Vec2 locNew, final float step ) {

      this.transform.moveTo(locNew, step);
      return this;
   }

   /**
    * Rotates this material to an angle.
    *
    * @param rotNew the rotation
    *
    * @return this material
    */
   @Override
   public MaterialUv rotateTo ( final float rotNew ) {

      this.transform.rotateTo(rotNew);
      return this;
   }

   /**
    * Rotates this material to an angle over a step in time.
    *
    * @param rotNew the angle
    * @param step   the step
    *
    * @return this material
    */
   @Override
   public MaterialUv rotateTo ( final float rotNew, final float step ) {

      this.transform.rotateTo(rotNew, step);
      return this;
   }

   /**
    * Rotates this material by an angle around the z axis.
    *
    * @param radians the angle
    *
    * @return this material
    */
   @Override
   public MaterialUv rotateZ ( final float radians ) {

      this.transform.rotateZ(radians);
      return this;
   }

   /**
    * Scales the material by a scalar.
    *
    * @param scalar the scalar
    *
    * @return this material
    */
   @Override
   public MaterialUv scaleBy ( final float scalar ) {

      this.transform.scaleBy(scalar);
      return this;
   }

   /**
    * Scales the material by a non-uniform scalar.
    *
    * @param scalar the scalar
    *
    * @return the material
    */
   @Override
   public MaterialUv scaleBy ( final Vec2 scalar ) {

      this.transform.scaleBy(scalar);
      return this;
   }

   /**
    * Scales the material to a uniform size.
    *
    * @param scalar the size
    *
    * @return this material
    */
   @Override
   public MaterialUv scaleTo ( final float scalar ) {

      this.transform.scaleTo(scalar);
      return this;
   }

   /**
    * Scales the material to a non-uniform size.
    *
    * @param scalar the size
    *
    * @return this material
    */
   @Override
   public MaterialUv scaleTo ( final Vec2 scalar ) {

      this.transform.scaleTo(scalar);
      return this;
   }

   /**
    * Eases the material to a scale by a step over time.
    *
    * @param scalar the scalar
    * @param step   the step
    *
    * @return this material
    */
   @Override
   public MaterialUv scaleTo ( final Vec2 scalar, final float step ) {

      this.transform.scaleTo(scalar, step);
      return this;
   }

   /**
    * Sets the material's tint color.
    *
    * @param tint the tint color
    *
    * @return this material
    */
   public MaterialUv setTint ( final Color tint ) {

      this.tint.set(tint);
      return this;
   }

   /**
    * Sets the material's tint color.
    *
    * @param r red
    * @param g green
    * @param b blue
    *
    * @return this material
    */
   public MaterialUv setTint ( final float r, final float g, final float b ) {

      this.tint.set(r, g, b);
      return this;
   }

   /**
    * Sets the material's tint color.
    *
    * @param r red
    * @param g green
    * @param b blue
    * @param a transparency
    *
    * @return this material
    */
   public MaterialUv setTint ( final float r, final float g, final float b,
      final float a ) {

      this.tint.set(r, g, b, a);
      return this;
   }

   /**
    * Sets the material's tint color from a hexadecimal value.
    *
    * @param tint the color
    *
    * @return this material
    */
   public MaterialUv setTint ( final int tint ) {

      Color.fromHex(tint, this.tint);
      return this;
   }

   /**
    * Returns a string representation of this material.
    *
    * @return the string
    */
   @Override
   public String toString ( ) { return this.toString(4); }

   /**
    * Returns a string representation of this material.
    *
    * @param places the number of places
    *
    * @return the string
    */
   public String toString ( final int places ) {

      final StringBuilder sb = new StringBuilder(256);
      sb.append("{ name: \"");
      sb.append(this.name);
      sb.append("\", tint: ");
      sb.append(this.tint.toString(places));
      sb.append(", transform: ");
      sb.append(this.transform.toString(places));
      sb.append(' ');
      sb.append('}');
      return sb.toString();
   }

}

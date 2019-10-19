package camzup.core;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * An entity which contains a transform that is applied to a
 * list of curves. The curves may references a list of
 * materials by index.
 */
public class CurveEntity3 extends Entity implements Iterable < Curve3 > {

   /**
    * The list of curves held by the entity.
    */
   public final LinkedList < Curve3 > curves = new LinkedList <>();

   /**
    * The list of materials held by the entity.
    */
   public final LinkedList < MaterialSolid > materials = new LinkedList <>();

   /**
    * The entity's transform.
    */
   public final Transform3 transform;

   /**
    * The order in which the entity's transform is applied to
    * the curve.
    */
   public Transform.Order transformOrder = Transform.Order.TRS;

   /**
    * The default constructor.
    */
   public CurveEntity3 () {

      super();
      this.transform = new Transform3();
   }

   /**
    * Creates a named curve entity.
    *
    * @param name
    *           the name
    */
   public CurveEntity3 ( final String name ) {

      super(name);
      this.transform = new Transform3();
   }

   /**
    * Creates a curve entity from a name, transform and list of
    * curves.
    *
    * @param name
    *           the name
    * @param transform
    *           the transform
    * @param curves
    *           the list of curves
    */
   public CurveEntity3 ( final String name, final Transform3 transform,
         final Curve3... curves ) {

      super(name);
      this.transform = transform;
      for (final Curve3 curve : curves) {
         if (curve != null) {
            this.curves.add(curve);
         }
      }
   }

   /**
    * Creates a curve entity from a transform and list of
    * curve.
    *
    * @param transform
    *           the transform
    * @param curves
    *           the list of curves
    */
   public CurveEntity3 ( final Transform3 transform, final Curve3... curves ) {

      super();
      this.transform = transform;
      for (final Curve3 curve : curves) {
         if (curve != null) {
            this.curves.add(curve);
         }
      }
   }

   /**
    * Appends a curve to this curve entity.
    *
    * @param curve
    *           the curve
    * @return this curve entity
    */
   @Chainable
   public CurveEntity3 appendCurve ( final Curve3 curve ) {

      if (curve != null) {
         this.curves.add(curve);
      }
      return this;
   }

   /**
    * Appends a list of curves to this curve entity.
    *
    * @param curves
    *           the curves
    * @return this curve entity
    */
   public CurveEntity3 appendCurve ( final Curve3... curves ) {

      for (final Curve3 curve : curves) {
         if (curve != null) {
            this.curves.add(curve);
         }
      }
      return this;
   }

   /**
    * Appends a material to this curve entity.
    *
    * @param material
    *           the material
    * @return this curve entity
    */
   @Chainable
   public CurveEntity3 appendMaterial ( final MaterialSolid material ) {

      if (material != null) {
         this.materials.add(material);
      }
      return this;
   }

   /**
    * Appends a list of materials to this curve entity.
    *
    * @param materials
    *           the list of materials
    * @return this curve entity
    */
   @Chainable
   public CurveEntity3 appendMaterial ( final MaterialSolid... materials ) {

      for (final MaterialSolid mat : materials) {
         if (mat != null) {
            this.materials.add(mat);
         }
      }
      return this;
   }

   /**
    * Evaluates a step in the range [0.0, 1.0] for curve,
    * returning a coordinate on the curve and a tangent. The
    * tangent will be normalized, to be of unit length.
    *
    * @param curveIndex
    *           the curve index
    * @param step
    *           the step in [0.0, 1.0]
    * @param coordWorld
    *           the output world coordinate
    * @param tanWorld
    *           the output world tangent
    * @return the world coordinate
    * @see CurveEntity3#eval(int, float, Vec3, Vec3, Vec3,
    *      Vec3)
    */
   public Vec3 eval (
         final int curveIndex,
         final float step,
         final Vec3 coordWorld,
         final Vec3 tanWorld ) {

      return this.eval(curveIndex, step,
            coordWorld, tanWorld,
            new Vec3(), new Vec3());
   }

   /**
    * Evaluates a step in the range [0.0, 1.0] for curve,
    * returning a coordinate on the curve and a tangent. The
    * tangent will be normalized, to be of unit length.
    *
    * @param curveIndex
    *           the curve index
    * @param step
    *           the step in [0.0, 1.0]
    * @param coordWorld
    *           the output world coordinate
    * @param tanWorld
    *           the output world tangent
    * @param coordLocal
    *           the output local coordinate
    * @param tanLocal
    *           the output local tangent
    * @return the world coordinate
    * @see Curve3#eval(float, Vec3, Vec3)
    * @see Transform3#multPoint(Transform3, Vec3, Vec3)
    * @see Transform3#multDir(Transform3, Vec3, Vec3)
    */
   public Vec3 eval (
         final int curveIndex,
         final float step,
         final Vec3 coordWorld,
         final Vec3 tanWorld,
         final Vec3 coordLocal,
         final Vec3 tanLocal ) {

      this.curves.get(curveIndex).eval(step, coordLocal, tanLocal);
      Transform3.multPoint(this.transform, coordLocal, coordWorld);
      Transform3.multDir(this.transform, tanLocal, tanWorld);
      return coordWorld;
   }

   @Override
   public Iterator < Curve3 > iterator () {

      return this.curves.iterator();
   }
}
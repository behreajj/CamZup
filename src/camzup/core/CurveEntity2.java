package camzup.core;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * An entity which contains a transform that is applied to a
 * list of curves. The curves may references a list of
 * materials by index.
 */
public class CurveEntity2 extends Entity implements Iterable < Curve2 > {

   /**
    * The list of curves held by the entity.
    */
   public final LinkedList < Curve2 > curves = new LinkedList <>();

   /**
    * The list of materials held by the entity.
    */
   public final LinkedList < MaterialSolid > materials = new LinkedList <>();

   /**
    * The entity's transform.
    */
   public final Transform2 transform;

   /**
    * The order in which the entity's transform is applied to
    * the curve.
    */
   public Transform.Order transformOrder = Transform.Order.TRS;

   /**
    * The default constructor.
    */
   public CurveEntity2 () {

      super();
      this.transform = new Transform2();
   }

   /**
    * Creates a named curve entity.
    *
    * @param name
    *           the name
    */
   public CurveEntity2 ( final String name ) {

      super(name);
      this.transform = new Transform2();
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
   public CurveEntity2 ( final String name, final Transform2 transform,
         final Curve2... curves ) {

      super(name);
      this.transform = transform;
      for (final Curve2 curve : curves) {
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
   public CurveEntity2 ( final Transform2 transform, final Curve2... curves ) {

      super();
      this.transform = transform;
      for (final Curve2 curve : curves) {
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
   public CurveEntity2 appendCurve ( final Curve2 curve ) {

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
   public CurveEntity2 appendCurve ( final Curve2... curves ) {

      for (final Curve2 curve : curves) {
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
   public CurveEntity2 appendMaterial ( final MaterialSolid material ) {

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
   public CurveEntity2 appendMaterial ( final MaterialSolid... materials ) {

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
    * @see CurveEntity2#eval(int, float, Vec2, Vec2, Vec2,
    *      Vec2)
    */
   public Vec2 eval (
         final int curveIndex,
         final float step,
         final Vec2 coordWorld,
         final Vec2 tanWorld ) {

      return this.eval(curveIndex, step,
            coordWorld, tanWorld,
            new Vec2(), new Vec2());
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
    * @see Curve2#eval(float, Vec2, Vec2)
    * @see Transform2#multPoint(Transform2, Vec2, Vec2)
    * @see Transform2#multDir(Transform2, Vec2, Vec2)
    */
   public Vec2 eval (
         final int curveIndex,
         final float step,
         final Vec2 coordWorld,
         final Vec2 tanWorld,
         final Vec2 coordLocal,
         final Vec2 tanLocal ) {

      this.curves.get(curveIndex).eval(step, coordLocal, tanLocal);
      Transform2.multPoint(this.transform, coordLocal, coordWorld);
      Transform2.multDir(this.transform, tanLocal, tanWorld);
      return coordWorld;
   }

   @Override
   public Iterator < Curve2 > iterator () {

      return this.curves.iterator();
   }

   /**
    * Creates a string representing a Wavefront OBJ file.
    *
    * @param precision
    *           the decimal place precision
    * @return the string
    */
   public String toObjString ( final int precision ) {

      final StringBuilder result = new StringBuilder();

      result.append('o')
            .append(' ')
            .append(this.name)
            .append('\n')
            .append('\n');

      int offset = 0;

      final Iterator < Curve2 > itr = this.curves.iterator();
      while (itr.hasNext()) {
         final Curve2 curve = itr.next();
         final Vec2[][] segments = curve.evalRange(precision);
         final int len = segments.length;

         result.append('g')
               .append(' ')
               .append(curve.name)
               .append('\n')
               .append('\n');

         for (int i = 0; i < len; ++i) {
            final Vec2 coord = segments[i][0];
            result.append('v')
                  .append(' ')
                  .append(coord.toObjString())
                  .append('\n');
         }

         result.append('\n');

         for (int i = 1, j = 2; i < len; ++i, ++j) {
            result.append('l')
                  .append(' ')
                  .append(offset + i)
                  .append(' ')
                  .append(offset + j)
                  .append('\n');
         }

         if (curve.closedLoop) {
            result.append('l')
                  .append(' ')
                  .append(offset + len)
                  .append(' ')
                  .append(offset + 1)
                  .append('\n');
         }

         offset += len;
         result.append('\n');
      }

      return result.toString();
   }

   /**
    * Creates a string representing a group node in the SVG
    * format.
    *
    * @return the string
    */
   public String toSvgString () {

      final StringBuilder result = new StringBuilder()
            .append("<g id=\"")
            .append(this.name.toLowerCase())
            .append("\" ")
            .append(this.transform.toSvgString())
            .append(">\n");

      for (final Curve2 curve : this.curves) {
         final MaterialSolid material = this.materials.get(curve.materialIndex);
         result.append("<g ")
               .append(material.toSvgString())
               .append(">\n")
               .append(curve.toSvgString())
               .append("</g>\n");
      }

      result.append("</g>");
      return result.toString();
   }
}

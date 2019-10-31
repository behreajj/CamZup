package camzup.core;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import camzup.core.Curve3.Knot3;

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
   public CurveEntity3 (
         final Transform3 transform,
         final Curve3... curves ) {

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

   /**
    * Returns an iterator, which allows an enhanced for-loop to
    * access the curves in the entity.
    *
    * @return the iterator
    * @see List#iterator()
    */
   @Override
   public Iterator < Curve3 > iterator () {

      return this.curves.iterator();
   }

   /**
    * Returns a String of Python code targeted toward the
    * Blender 2.8x API. This code is brittle and is used for
    * internal testing purposes, i.e., to compare how curve
    * geometry looks in Blender (the control) vs. in the
    * library (the test).
    *
    * @return the string
    */
   public String toBlenderCode () {

      final StringBuilder result = new StringBuilder();
      result.append("from bpy import data as D, context as C\n\n")
            .append("curve_entity = {\n    \"curves\": [\n");

      int curveIndex = 0;
      final int curveLast = this.curves.size() - 1;
      final Iterator < Curve3 > curveItr = this.curves.iterator();
      while (curveItr.hasNext()) {

         int knotIndex = 0;
         final Curve3 curve = curveItr.next();
         final int knotLast = curve.knotCount() - 1;
         final Iterator < Knot3 > knotItr = curve.iterator();

         result.append("        {\"closed_loop\": ")
               .append(curve.closedLoop ? "True" : "False")
               .append(",\n         \"knots\": [\n");

         while (knotItr.hasNext()) {

            final Knot3 knot = knotItr.next();
            final Vec3 co = knot.coord;
            final Vec3 rear = knot.rearHandle;
            final Vec3 fore = knot.foreHandle;

            /* Append coordinate (co in Blender). */
            result.append("             {\"co\": (")
                  .append(Utils.toFixed(co.x, 6))
                  .append(',').append(' ')
                  .append(Utils.toFixed(co.y, 6))
                  .append(',').append(' ')
                  .append(Utils.toFixed(co.z, 6))

                  .append("), \n              \"handle_right\": (")
                  .append(Utils.toFixed(fore.x, 6))
                  .append(',').append(' ')
                  .append(Utils.toFixed(fore.y, 6))
                  .append(',').append(' ')
                  .append(Utils.toFixed(fore.z, 6))

                  .append("), \n              \"handle_left\": (")
                  .append(Utils.toFixed(rear.x, 6))
                  .append(',').append(' ')
                  .append(Utils.toFixed(rear.y, 6))
                  .append(',').append(' ')
                  .append(Utils.toFixed(rear.z, 6))
                  .append(')').append('}');

            if (knotIndex < knotLast) {
               result.append(',').append('\n');
            }

            knotIndex++;
         }

         result.append(']').append('}');

         if (curveIndex < curveLast) {
            result.append(',').append('\n');
         }

         curveIndex++;
      }

      result.append("]}\n\ncrv_data = D.curves.new(\"")
            .append(this.name)
            .append("\", \"CURVE\")\n")
            .append("crv_data.dimensions = \"3D\"\n")
            .append("crv_splines = crv_data.splines\n")
            .append("crv_index = 0\n")
            .append("splines_raw = curve_entity[\"curves\"]\n")
            .append("for spline_raw in splines_raw:\n")
            .append("\tspline = crv_splines.new(\"BEZIER\")\n")
            .append("\tspline.use_cyclic_u = spline_raw[\"closed_loop\"]\n")
            .append("\tknots_raw = spline_raw[\"knots\"]\n")
            .append("\tknt_index = 0\n")
            .append("\tbz_pts = spline.bezier_points\n")
            .append("\tbz_pts.add(len(knots_raw) - 1)\n")
            .append("\tfor knot in bz_pts:\n")
            .append("\t\tknot_raw = knots_raw[knt_index]\n")
            .append("\t\tknot.handle_left_type = \"FREE\"\n")
            .append("\t\tknot.handle_right_type = \"FREE\"\n")
            .append("\t\tknot.co = knot_raw[\"co\"]\n")
            .append("\t\tknot.handle_right = knot_raw[\"handle_right\"]\n")
            .append("\t\tknot.handle_left = knot_raw[\"handle_left\"]\n")
            .append("\t\tknt_index = knt_index + 1\n")
            .append("\tcrv_index = crv_index + 1\n\n")
            .append("crv_obj = D.objects.new(\"")
            .append(this.name)
            .append("\", crv_data)\n")
            .append(this.transform.toBlenderCode("crv_obj"))
            .append("\n\nC.scene.collection.objects.link(crv_obj)");

      return result.toString();
   }

   /**
    * Creates a string representing a Wavefront OBJ file.
    * Renders the curve as a series of line segments.
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

      final Iterator < Curve3 > itr = this.curves.iterator();
      while (itr.hasNext()) {
         final Curve3 curve = itr.next();
         final Vec3[][] segments = curve.evalRange(precision);
         final int len = segments.length;

         result.append('g')
               .append(' ')
               .append(curve.name)
               .append('\n')
               .append('\n');

         for (int i = 0; i < len; ++i) {
            final Vec3 coord = segments[i][0];
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
}
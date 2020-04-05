package camzup.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * An entity which contains a transform that is applied to a list of curves. The
 * curves may references a list of materials by index.
 */
public class CurveEntity3 extends Entity3
   implements Iterable < Curve3 >, IVolume3 {

   /**
    * The list of curves held by the entity.
    */
   public final List < Curve3 > curves;

   {
      this.curves = new ArrayList <>();
   }

   /**
    * The default constructor.
    */
   public CurveEntity3 ( ) { super(); }

   /**
    * Creates a named curve entity.
    *
    * @param name the name
    */
   public CurveEntity3 ( final String name ) { super(name); }

   /**
    * Creates a curve entity from a name, transform and list of curves.
    *
    * @param name      the name
    * @param transform the transform
    * @param curves    the list of curves
    */
   public CurveEntity3 (
      final String name,
      final Transform3 transform,
      final Curve3... curves ) {

      super(name, transform);
      this.appendAll(curves);
   }

   /**
    * Creates a curve entity from a transform and list of curve.
    *
    * @param transform the transform
    * @param curves    the list of curves
    */
   public CurveEntity3 (
      final Transform3 transform,
      final Curve3... curves ) {

      super(transform);
      this.appendAll(curves);
   }

   /**
    * Appends a curve to this curve entity.
    *
    * @param curve the curve
    *
    * @return this curve entity
    */
   @Chainable
   public CurveEntity3 append ( final Curve3 curve ) {

      if ( curve != null ) { this.curves.add(curve); }
      return this;
   }

   /**
    * Appends a list of curves to this curve entity.
    *
    * @param curves the curves
    *
    * @return this curve entity
    */
   @Chainable
   public CurveEntity3 appendAll ( final Curve3... curves ) {

      final int len = curves.length;
      for ( int i = 0; i < len; ++i ) {
         this.append(curves[i]);
      }
      return this;
   }

   /**
    * Gets a curve from this curve entity.
    *
    * @param i the index
    *
    * @return the curve.
    */
   public Curve3 get ( final int i ) {

      return this.curves.get(Utils.mod(i, this.curves.size()));
   }

   /**
    * Returns an iterator, which allows an enhanced for-loop to access the
    * curves in the entity.
    *
    * @return the iterator
    *
    * @see List#iterator()
    */
   @Override
   public Iterator < Curve3 > iterator ( ) {

      return this.curves.iterator();
   }

   /**
    * Gets the number of curves held by the entity.
    *
    * @return the length
    */
   public int length ( ) { return this.curves.size(); }

   /**
    * Scales the entity by a scalar.
    *
    * @param scalar the scalar
    *
    * @return this entity
    */
   @Override
   @Chainable
   public CurveEntity3 scaleBy ( final float scalar ) {

      this.transform.scaleBy(scalar);
      return this;
   }

   /**
    * Scales the entity by a non-uniform scalar.
    *
    * @param scalar the scalar
    *
    * @return the entity
    */
   @Override
   @Chainable
   public CurveEntity3 scaleBy ( final Vec3 scalar ) {

      this.transform.scaleBy(scalar);
      return this;
   }

   /**
    * Scales the entity to a uniform size.
    *
    * @param scalar the size
    *
    * @return this entity
    */
   @Override
   @Chainable
   public CurveEntity3 scaleTo ( final float scalar ) {

      this.transform.scaleTo(scalar);
      return this;
   }

   /**
    * Scales the entity to a non-uniform size.
    *
    * @param scalar the size
    *
    * @return this entity
    */
   @Override
   @Chainable
   public CurveEntity3 scaleTo ( final Vec3 scalar ) {

      this.transform.scaleTo(scalar);
      return this;
   }

   /**
    * Eases the entity to a scale by a step over time.
    *
    * @param scalar the scalar
    * @param step   the step
    *
    * @return this entity
    */
   @Override
   @Chainable
   public CurveEntity3 scaleTo (
      final Vec3 scalar,
      final float step ) {

      this.transform.scaleTo(scalar, step);
      return this;
   }

   /**
    * Returns a String of Python code targeted toward the Blender 2.8x API. This
    * code is brittle and is used for internal testing purposes, i.e., to
    * compare how curve geometry looks in Blender (the control) versus in the
    * library (the test).
    *
    * @return the string
    */
   @Experimental
   public String toBlenderCode ( ) {

      return this.toBlenderCode(12, "FULL", 0.0f, 0.0f);
   }

   /**
    * Returns a String of Python code targeted toward the Blender 2.8x API. This
    * code is brittle and is used for internal testing purposes, i.e., to
    * compare how curve geometry looks in Blender (the control) versus in the
    * library (the test).
    *
    * @param uRes       the resolution u
    * @param fillMode   the fill mode: "FULL", "BACK", "FRONT", "HALF"
    * @param extrude    geometry extrusion amount
    * @param bevelDepth depth of geometry extrusion bevel
    *
    * @return the string
    */
   @Experimental
   public String toBlenderCode (
      final int uRes,
      final String fillMode,
      final float extrude,
      final float bevelDepth ) {

      final float tiltStart = 0.0f;
      final float tiltEnd = 0.0f;

      final StringBuilder pyCd = new StringBuilder(2048);
      pyCd.append("from bpy import data as D, context as C\n\n")
         .append("curve_entity = {\"name\": \"")
         .append(this.name)
         .append("\", \"transform\": ")
         .append(this.transform.toBlenderCode())
         .append(", \"curves\": [");

      int curveIndex = 0;
      final int curveLast = this.curves.size() - 1;
      final Iterator < Curve3 > curveItr = this.curves.iterator();
      while ( curveItr.hasNext() ) {

         pyCd.append(
            curveItr.next().toBlenderCode(uRes, tiltStart, tiltEnd));
         if ( curveIndex < curveLast ) { pyCd.append(',').append(' '); }
         curveIndex++;
      }

      pyCd.append("]}\n\ncrv_data = D.curves.new(")
         .append("curve_entity[\"name\"]")
         .append(", \"CURVE\")\n")
         .append("crv_data.dimensions = \"3D\"\n")
         .append("crv_data.fill_mode = \"")
         .append(fillMode.toUpperCase())
         .append("\"\n")
         .append("crv_data.extrude = ")
         .append(Utils.toFixed(extrude, 6))
         .append('\n')
         .append("crv_data.bevel_depth = ")
         .append(Utils.toFixed(bevelDepth, 6))
         .append('\n')
         .append("crv_splines = crv_data.splines\n")
         .append("crv_index = 0\n")
         .append("splines_raw = curve_entity[\"curves\"]\n")
         .append("for spline_raw in splines_raw:\n")
         .append("    spline = crv_splines.new(\"BEZIER\")\n")
         .append("    spline.use_cyclic_u = spline_raw[\"closed_loop\"]\n")
         .append("    spline.resolution_u = spline_raw[\"resolution_u\"]\n")
         .append("    knots_raw = spline_raw[\"knots\"]\n")
         .append("    knt_index = 0\n")
         .append("    bz_pts = spline.bezier_points\n")
         .append("    bz_pts.add(len(knots_raw) - 1)\n")
         .append("    for knot in bz_pts:\n")
         .append("        knot_raw = knots_raw[knt_index]\n")
         .append("        knot.handle_left_type = \"FREE\"\n")
         .append("        knot.handle_right_type = \"FREE\"\n")
         .append("        knot.co = knot_raw[\"co\"]\n")
         .append("        knot.handle_left = knot_raw[\"handle_left\"]\n")
         .append("        knot.handle_right = knot_raw[\"handle_right\"]\n")
         .append("        knot.weight_softbody = knot_raw[\"weight\"]\n")
         .append("        knot.radius = knot_raw[\"radius\"]\n")
         .append("        knot.tilt = knot_raw[\"tilt\"]\n")
         .append("        knt_index = knt_index + 1\n")
         .append("    crv_index = crv_index + 1\n\n")
         .append("crv_obj = D.objects.new(crv_data.name, crv_data)\n")
         .append("tr = curve_entity[\"transform\"]\n")
         .append("crv_obj.location = tr[\"location\"]\n")
         .append("crv_obj.rotation_mode = tr[\"rotation_mode\"]\n")
         .append("crv_obj.rotation_quaternion = ")
         .append("tr[\"rotation_quaternion\"]\n")
         .append("crv_obj.scale = tr[\"scale\"]\n")
         .append("C.scene.collection.objects.link(crv_obj)\n");

      return pyCd.toString();
   }

   /**
    * Evaluates a step in the range [0.0, 1.0] for a curve in the entity,
    * returning a knot.
    *
    * @param ce         the curve entity
    * @param curveIndex the curve index
    * @param step       the step
    * @param handedness the handedness
    * @param target     the output transform
    *
    * @return the transform
    *
    * @see Curve3#eval(Curve3, float, Handedness, Transform3)
    * @see Transform3#invMulPoint(Transform3, Vec3, Vec3)
    * @see Transform3#rotateBy(Quaternion)
    */
   @Experimental
   public static Transform3 eval (
      final CurveEntity3 ce,
      final int curveIndex,
      final float step,
      final Handedness handedness,
      final Transform3 target ) {

      // TEST

      final Transform3 ctr = ce.transform;
      Curve3.eval(ce.get(curveIndex), step, handedness, target);
      Transform3.mulPoint(ctr, target.location, target.location);
      target.rotateBy(ctr.rotation);

      return target;
   }

   /**
    * Evaluates a step in the range [0.0, 1.0] for a curve in the entity,
    * returning a knot.
    *
    * @param ce         the curve entity
    * @param curveIndex the curve index
    * @param step       the step
    * @param knWorld    the knot in world space
    * @param knLocal    the knot in local space
    *
    * @return the world knot
    *
    * @see Curve3#eval(Curve3, float, Vec3, Vec3)
    * @see Transform3#mulPoint(Transform3, Vec3, Vec3)
    * @see Transform3#mulDir(Transform3, Vec3, Vec3)
    */
   @Experimental
   public static Knot3 eval (
      final CurveEntity3 ce,
      final int curveIndex,
      final float step,
      final Knot3 knWorld,
      final Knot3 knLocal ) {

      final Transform3 tr = ce.transform;
      Curve3.eval(ce.get(curveIndex), step, knLocal);

      Transform3.mulPoint(tr, knLocal.coord, knWorld.coord);
      Transform3.mulPoint(tr, knLocal.foreHandle, knWorld.foreHandle);
      Transform3.mulPoint(tr, knLocal.rearHandle, knWorld.rearHandle);

      return knWorld;
   }

   /**
    * Evaluates a step in the range [0.0, 1.0] for a curve in the entity,
    * returning a coordinate on the curve and a tangent. The tangent will be
    * normalized, to be of unit length.
    *
    * @param ce         the curve entity
    * @param curveIndex the curve index
    * @param step       the step
    * @param rayWorld   the output world ray
    * @param rayLocal   the output local ray
    *
    * @return the world ray
    *
    * @see CurveEntity3#eval(CurveEntity3, int, float, Vec3, Vec3, Vec3, Vec3)
    */
   public static Ray3 eval (
      final CurveEntity3 ce,
      final int curveIndex,
      final float step,
      final Ray3 rayWorld,
      final Ray3 rayLocal ) {

      CurveEntity3.eval(
         ce, curveIndex, step,
         rayWorld.origin, rayWorld.dir,
         rayLocal.origin, rayLocal.dir);
      return rayWorld;
   }

   /**
    * Evaluates a step in the range [0.0, 1.0] for a curve in the entity,
    * returning a coordinate on the curve and a tangent. The tangent will be
    * normalized, to be of unit length.
    *
    * @param ce         the curve entity
    * @param curveIndex the curve index
    * @param step       the step
    * @param coWorld    the output world coordinate
    * @param tnWorld    the output world tangent
    * @param coLocal    the output local coordinate
    * @param tnLocal    the output local tangent
    *
    * @return the world coordinate
    *
    * @see Curve3#eval(Curve3, float, Vec3, Vec3)
    * @see Transform3#mulPoint(Transform3, Vec3, Vec3)
    * @see Transform3#mulDir(Transform3, Vec3, Vec3)
    */
   public static Vec3 eval (
      final CurveEntity3 ce,
      final int curveIndex,
      final float step,
      final Vec3 coWorld,
      final Vec3 tnWorld,
      final Vec3 coLocal,
      final Vec3 tnLocal ) {

      Curve3.eval(
         ce.get(curveIndex),
         step, coLocal, tnLocal);
      Transform3.mulPoint(ce.transform, coLocal, coWorld);
      Transform3.mulDir(ce.transform, tnLocal, tnWorld);
      return coWorld;
   }

   /**
    * Creates a curve entity from a mesh entity.
    *
    * @param me     the source mesh
    * @param target the output curve
    *
    * @return the curve
    */
   public static CurveEntity3 fromMeshEntity (
      final MeshEntity3 me,
      final CurveEntity3 target ) {

      target.name = me.name;
      target.transform.set(me.transform);

      final Iterator < Mesh3 > meshItr = me.meshes.iterator();
      final List < Curve3 > curves = target.curves;
      curves.clear();

      while ( meshItr.hasNext() ) {
         final Mesh3 mesh = meshItr.next();
         final int facesLen = mesh.faces.length;
         for ( int i = 0; i < facesLen; ++i ) {
            final Curve3 curve = new Curve3();
            Curve3.fromMeshFace(i, mesh, curve);
            curves.add(curve);
         }
      }

      return target;
   }

}
package camzup.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * An entity which contains a transform that is applied to a list of
 * curves.
 */
public class CurveEntity3 extends Entity3 implements Iterable < Curve3 >,
   IVolume3 {

   /**
    * The list of curves held by the entity.
    */
   public final ArrayList < Curve3 > curves = new ArrayList <>(
      IEntity.DEFAULT_CAPACITY);

   /**
    * The default constructor.
    */
   public CurveEntity3 ( ) {}

   /**
    * Creates a curve entity from a list of curves.
    *
    * @param curves the list of curves
    */
   public CurveEntity3 ( final Curve3... curves ) {

      this.appendAll(curves);
   }

   /**
    * Creates a named curve entity.
    *
    * @param name the name
    */
   public CurveEntity3 ( final String name ) { super(name); }

   /**
    * Creates a curve entity from a list of curves.
    *
    * @param name   the name
    * @param curves the list of curves
    */
   public CurveEntity3 ( final String name, final Curve3... curves ) {

      super(name);
      this.appendAll(curves);
   }

   /**
    * Creates a curve entity from a name, transform and list of curves.
    *
    * @param name      the name
    * @param transform the transform
    * @param curves    the list of curves
    */
   public CurveEntity3 ( final String name, final Transform3 transform,
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
   public CurveEntity3 ( final Transform3 transform, final Curve3... curves ) {

      super(transform);
      this.appendAll(curves);
   }

   /**
    * Appends a curve to this curve entity. The curve must not be
    * <code>null</code> and must have a length greater than zero.
    *
    * @param curve the curve
    *
    * @return this curve entity
    */
   public CurveEntity3 append ( final Curve3 curve ) {

      if ( curve != null ) { this.curves.add(curve); }
      return this;
   }

   /**
    * Appends a collection of curves to this curve entity.
    *
    * @param app the curves
    *
    * @return this curve entity
    */
   public CurveEntity3 appendAll ( final Collection < Curve3 > app ) {

      final Iterator < Curve3 > itr = app.iterator();
      while ( itr.hasNext() ) { this.append(itr.next()); }
      return this;
   }

   /**
    * Appends a list of curves to this curve entity.
    *
    * @param app the curves
    *
    * @return this curve entity
    */
   public CurveEntity3 appendAll ( final Curve3... app ) {

      final int len = app.length;
      for ( int i = 0; i < len; ++i ) { this.append(app[i]); }
      return this;
   }

   /**
    * Transforms all curves in this curve entity by its transform, then resets
    * the entity's transform to the identity.
    *
    * @return this curve entity
    *
    * @see Curve3#transform(Transform3)
    * @see Transform3#identity(Transform3)
    */
   public CurveEntity3 consumeTransform ( ) {

      final Iterator < Curve3 > itr = this.curves.iterator();
      while ( itr.hasNext() ) { itr.next().transform(this.transform); }
      Transform3.identity(this.transform);
      return this;
   }

   /**
    * Evaluates whether a curve is contained by this curve entity.
    *
    * @param c the curve
    *
    * @return the evaluation
    */
   public boolean contains ( final Curve3 c ) {

      return this.curves.contains(c);
   }

   /**
    * Tests this entity for equivalence with another object.
    *
    * @param obj the object
    *
    * @return the evaluation
    */
   @Override
   public boolean equals ( final Object obj ) {

      if ( this == obj ) { return true; }
      if ( !super.equals(obj) ) { return false; }
      if ( this.getClass() != obj.getClass() ) { return false; }
      final CurveEntity3 other = ( CurveEntity3 ) obj;
      return this.curves.equals(other.curves);
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
    * Gets this curve entity's scale.
    *
    * @param target the output vector
    *
    * @return the scale
    */
   @Override
   public Vec3 getScale ( final Vec3 target ) {

      return this.transform.getScale(target);
   }

   /**
    * Returns a hash code for this entity based on its array of entity data.
    *
    * @return the hash
    */
   @Override
   public int hashCode ( ) {

      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + this.curves.hashCode();
      return result;
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
   public Iterator < Curve3 > iterator ( ) { return this.curves.iterator(); }

   /**
    * Gets the number of curves held by the entity.
    *
    * @return the length
    */
   public int length ( ) { return this.curves.size(); }

   /**
    * Centers and rescales all curves in the curve entity about a shared
    * origin by calculating their dimensions, subtracting the center point,
    * and scaling by the maximum dimension.
    *
    * @return this curve entity.
    * 
    * @see Curve3#accumMinMax(Curve3, Vec3, Vec3)
    */
   public CurveEntity3 reframe ( ) {

      /* Find lower and upper bound for all curves. */
      final Vec3 lb = new Vec3(Float.MAX_VALUE, Float.MAX_VALUE,
         Float.MAX_VALUE);
      final Vec3 ub = new Vec3(Float.MIN_VALUE, Float.MIN_VALUE,
         Float.MIN_VALUE);

      Iterator < Curve3 > crvItr = this.curves.iterator();
      while ( crvItr.hasNext() ) { Curve3.accumMinMax(crvItr.next(), lb, ub); }

      lb.x = -0.5f * ( lb.x + ub.x );
      lb.y = -0.5f * ( lb.y + ub.y );
      lb.z = -0.5f * ( lb.z + ub.z );
      final float scl = Utils.div(1.0f, Utils.max(ub.x - lb.x, ub.y - lb.y, ub.z
         - lb.z));

      crvItr = this.curves.iterator();
      while ( crvItr.hasNext() ) {
         final Curve3 curve = crvItr.next();
         curve.translate(lb);
         curve.scale(scl);
      }

      return this;
   }

   /**
    * @param curveIndex the curve index
    * @param knotIndex  the knot index
    * @param global     the point in global space
    * @param local      the point in curve local space
    *
    * @return this entity
    *
    * @see Transform3#invMulPoint(Transform3, Vec3, Vec3)
    * @see Curve3#relocateKnot(int, Vec3)
    */
   @Experimental
   public CurveEntity3 relocateKnot ( final int curveIndex, final int knotIndex,
      final Vec3 global, final Vec3 local ) {

      Transform3.invMulPoint(this.transform, global, local);
      this.get(curveIndex).relocateKnot(knotIndex, local);
      return this;
   }

   /**
    * Removes a curve from this curve entity. Returns <code>true</code> if
    * this request was filled, <code>false</code> if not.
    *
    * @param curve the curve to remove
    *
    * @return the evaluation
    */
   public boolean remove ( final Curve3 curve ) {

      return this.curves.remove(curve);
   }

   /**
    * Removes a curve at a given index.
    *
    * @param i the index
    *
    * @return the curve
    */
   public Curve3 removeAt ( final int i ) { return this.curves.remove(i); }

   /**
    * Resets the curve entity to an initial state. Sets the transform to an
    * identity and clears the list of curves.
    *
    * @return this entity
    */
   @Override
   public CurveEntity3 reset ( ) {

      super.reset();
      this.curves.clear();
      return this;
   }

   /**
    * Scales the entity by a scalar.
    *
    * @param scalar the scalar
    *
    * @return this entity
    */
   @Override
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
   public CurveEntity3 scaleTo ( final Vec3 scalar, final float step ) {

      this.transform.scaleTo(scalar, step);
      return this;
   }

   /**
    * Sets the coordinate of a knot for a given curve at an index. Multiplies
    * the input coordinate in global space by the transform's inverse.<br>
    * <br>
    * To facilitate editing the curve with a graphical user interface (GUI).
    *
    * @param curveIndex the curve index
    * @param knotIndex  the knot index
    * @param global     the point in global space
    * @param local      the point in curve local space
    *
    * @return this entity
    *
    * @see Transform3#invMulPoint(Transform3, Vec3, Vec3)
    */
   @Experimental
   public CurveEntity3 setKnotCoord ( final int curveIndex, final int knotIndex,
      final Vec3 global, final Vec3 local ) {

      Transform3.invMulPoint(this.transform, global, local);
      this.get(curveIndex).get(knotIndex).coord.set(local);
      return this;
   }

   /**
    * Sets the fore handle of a knot for a given curve at an index. Multiplies
    * the input coordinate in global space by the transform's inverse.<br>
    * <br>
    * To facilitate editing the curve with a graphical user interface (GUI).
    *
    * @param curveIndex the curve index
    * @param knotIndex  the knot index
    * @param global     the point in global space
    * @param local      the point in curve local space
    *
    * @return this entity
    *
    * @see Transform3#invMulPoint(Transform3, Vec3, Vec3)
    */
   @Experimental
   public CurveEntity3 setKnotForeHandle ( final int curveIndex,
      final int knotIndex, final Vec3 global, final Vec3 local ) {

      Transform3.invMulPoint(this.transform, global, local);
      this.get(curveIndex).get(knotIndex).foreHandle.set(local);
      return this;
   }

   /**
    * Sets the rear handle of a knot for a given curve at an index. Multiplies
    * the input coordinate in global space by the transform's inverse.<br>
    * <br>
    * To facilitate editing the curve with a graphical user interface (GUI).
    *
    * @param curveIndex the curve index
    * @param knotIndex  the knot index
    * @param global     the point in global space
    * @param local      the point in curve local space
    *
    * @return this entity
    *
    * @see Transform3#invMulPoint(Transform3, Vec3, Vec3)
    */
   @Experimental
   public CurveEntity3 setKnotRearHandle ( final int curveIndex,
      final int knotIndex, final Vec3 global, final Vec3 local ) {

      Transform3.invMulPoint(this.transform, global, local);
      this.get(curveIndex).get(knotIndex).rearHandle.set(local);
      return this;
   }

   /**
    * Returns a String of Python code targeted toward the Blender 2.8x API.
    * This code is brittle and is used for internal testing purposes, i.e., to
    * compare how curve geometry looks in Blender (the control) versus in the
    * library (the test).
    *
    * @return the string
    */
   @Experimental
   public String toBlenderCode ( ) {

      return this.toBlenderCode(24, "FULL", 0.0f, 0.0f);
   }

   /**
    * Returns a String of Python code targeted toward the Blender 2.8x API.
    * This code is brittle and is used for internal testing purposes, i.e., to
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
   public String toBlenderCode ( final int uRes, final String fillMode,
      final float extrude, final float bevelDepth ) {

      final float tiltStart = 0.0f;
      final float tiltEnd = 0.0f;

      final StringBuilder pyCd = new StringBuilder(2048);
      pyCd.append("from bpy import data as D, context as C\n\n");
      pyCd.append("entity_src = {\"name\": \"");
      pyCd.append(this.name);
      pyCd.append("\", \"transform\": ");
      this.transform.toBlenderCode(pyCd);
      pyCd.append(", \"curves\": [");

      int curveIndex = 0;
      final int curveLast = this.curves.size() - 1;
      final Iterator < Curve3 > itr = this.curves.iterator();
      while ( itr.hasNext() ) {
         itr.next().toBlenderCode(pyCd, uRes, tiltStart, tiltEnd);
         if ( curveIndex < curveLast ) { pyCd.append(',').append(' '); }
         ++curveIndex;
      }

      pyCd.append("]}\n\ncrv_data = D.curves.new(");
      pyCd.append("entity_src[\"name\"]");
      pyCd.append(", \"CURVE\")\n");
      pyCd.append("crv_data.dimensions = \"3D\"\n");
      pyCd.append("crv_data.fill_mode = \"");
      pyCd.append(fillMode.toUpperCase());
      pyCd.append("\"\n");
      pyCd.append("crv_data.extrude = ");
      Utils.toFixed(pyCd, extrude, 6);
      pyCd.append('\n');
      pyCd.append("crv_data.bevel_depth = ");
      Utils.toFixed(pyCd, bevelDepth, 6);
      pyCd.append('\n');
      pyCd.append("crv_splines = crv_data.splines\n");
      pyCd.append("crv_index = 0\n");
      pyCd.append("splines_raw = entity_src[\"curves\"]\n");
      pyCd.append("for spline_raw in splines_raw:\n");
      pyCd.append("    spline = crv_splines.new(\"BEZIER\")\n");
      pyCd.append("    spline.use_cyclic_u = spline_raw[\"closed_loop\"]\n");
      pyCd.append("    spline.resolution_u = spline_raw[\"resolution_u\"]\n");
      pyCd.append("    knots_raw = spline_raw[\"knots\"]\n");
      pyCd.append("    knt_index = 0\n");
      pyCd.append("    bz_pts = spline.bezier_points\n");
      pyCd.append("    bz_pts.add(len(knots_raw) - 1)\n");
      pyCd.append("    for knot in bz_pts:\n");
      pyCd.append("        knot_raw = knots_raw[knt_index]\n");
      pyCd.append("        knot.handle_left_type = \"FREE\"\n");
      pyCd.append("        knot.handle_right_type = \"FREE\"\n");
      pyCd.append("        knot.co = knot_raw[\"co\"]\n");
      pyCd.append("        knot.handle_left = knot_raw[\"handle_left\"]\n");
      pyCd.append("        knot.handle_right = knot_raw[\"handle_right\"]\n");
      pyCd.append("        knot.weight_softbody = knot_raw[\"weight\"]\n");
      pyCd.append("        knot.radius = knot_raw[\"radius\"]\n");
      pyCd.append("        knot.tilt = knot_raw[\"tilt\"]\n");
      pyCd.append("        knt_index += 1\n");
      pyCd.append("    crv_index += 1\n\n");
      pyCd.append("crv_obj = D.objects.new(crv_data.name, crv_data)\n");
      pyCd.append("tr = entity_src[\"transform\"]\n");
      pyCd.append("crv_obj.location = tr[\"location\"]\n");
      pyCd.append("crv_obj.rotation_mode = tr[\"rotation_mode\"]\n");
      pyCd.append("crv_obj.rotation_quaternion = ");
      pyCd.append("tr[\"rotation_quaternion\"]\n");
      pyCd.append("crv_obj.scale = tr[\"scale\"]\n");
      pyCd.append("C.scene.collection.objects.link(crv_obj)\n");

      return pyCd.toString();
   }

   /**
    * Centers all curves in the curve entity about a shared origin by
    * calculating their dimensions then subtracting the center point.
    *
    * @return this curve entity.
    */
   public CurveEntity3 toOrigin ( ) {

      /* Find lower and upper bound for all curves. */
      final Vec3 lb = new Vec3(Float.MAX_VALUE, Float.MAX_VALUE,
         Float.MAX_VALUE);
      final Vec3 ub = new Vec3(Float.MIN_VALUE, Float.MIN_VALUE,
         Float.MIN_VALUE);
      Iterator < Curve3 > itr = this.curves.iterator();
      while ( itr.hasNext() ) { Curve3.accumMinMax(itr.next(), lb, ub); }

      /* Shift curves. */
      lb.x = -0.5f * ( lb.x + ub.x );
      lb.y = -0.5f * ( lb.y + ub.y );
      lb.z = -0.5f * ( lb.z + ub.z );

      itr = this.curves.iterator();
      while ( itr.hasNext() ) { itr.next().translate(lb); }

      return this;
   }

   /**
    * Returns a string representation of this curve entity.
    *
    * @param places number of places
    *
    * @return the string
    */
   @Override
   public String toString ( final int places ) {

      final StringBuilder sb = new StringBuilder(1024);
      sb.append("{ name: \"");
      sb.append(this.name);
      sb.append('\"');
      sb.append(", transform: ");
      sb.append(this.transform.toString(places));
      sb.append(", curves: [ ");

      final Iterator < Curve3 > itr = this.curves.iterator();
      while ( itr.hasNext() ) {
         sb.append(itr.next().toString(places));
         if ( itr.hasNext() ) { sb.append(',').append(' '); }
      }

      sb.append(" ] }");
      return sb.toString();
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
   public static Knot3 eval ( final CurveEntity3 ce, final int curveIndex,
      final float step, final Knot3 knWorld, final Knot3 knLocal ) {

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
   public static Ray3 eval ( final CurveEntity3 ce, final int curveIndex,
      final float step, final Ray3 rayWorld, final Ray3 rayLocal ) {

      CurveEntity3.eval(ce, curveIndex, step, rayWorld.origin, rayWorld.dir,
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
   public static Vec3 eval ( final CurveEntity3 ce, final int curveIndex,
      final float step, final Vec3 coWorld, final Vec3 tnWorld,
      final Vec3 coLocal, final Vec3 tnLocal ) {

      Curve3.eval(ce.get(curveIndex), step, coLocal, tnLocal);
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
   public static CurveEntity3 fromMeshEntity ( final MeshEntity3 me,
      final CurveEntity3 target ) {

      target.name = me.name;
      target.transform.set(me.transform);

      final Iterator < Mesh3 > meshItr = me.meshes.iterator();
      final ArrayList < Curve3 > curves = target.curves;
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
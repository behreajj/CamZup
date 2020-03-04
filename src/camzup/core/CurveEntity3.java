package camzup.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * An entity which contains a transform that is applied to a list of
 * curves. The curves may references a list of materials by index.
 */
public class CurveEntity3 extends Entity3 implements Iterable < Curve3 > {

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
  public CurveEntity3 ( ) {

    super();
  }

  /**
   * Creates a named curve entity.
   *
   * @param name the name
   */
  public CurveEntity3 ( final String name ) {

    super(name);
  }

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
      final Curve3 ... curves ) {

    super(name, transform);
    this.appendCurves(curves);
  }

  /**
   * Creates a curve entity from a transform and list of curve.
   *
   * @param transform the transform
   * @param curves    the list of curves
   */
  public CurveEntity3 (
      final Transform3 transform,
      final Curve3 ... curves ) {

    super(transform);
    this.appendCurves(curves);
  }

  /**
   * Appends a curve to this curve entity.
   *
   * @param curve the curve
   * @return this curve entity
   */
  @Chainable
  public CurveEntity3 appendCurve ( final Curve3 curve ) {

    if ( curve != null ) { this.curves.add(curve); }
    return this;
  }

  /**
   * Appends a list of curves to this curve entity.
   *
   * @param curves the curves
   * @return this curve entity
   */
  public CurveEntity3 appendCurves ( final Curve3 ... curves ) {

    final int len = curves.length;
    for ( int i = 0; i < len; ++i ) {
      this.appendCurve(curves[i]);
    }
    return this;
  }

  /**
   * Evaluates a step in the range [0.0, 1.0] for curve, returning a
   * coordinate on the curve and a tangent. The tangent will be
   * normalized, to be of unit length.
   *
   * @param curveIndex the curve index
   * @param step       the step in [0.0, 1.0]
   * @param coordWorld the output world coordinate
   * @param tanWorld   the output world tangent
   * @return the world coordinate
   * @see CurveEntity3#eval(int, float, Vec3, Vec3, Vec3, Vec3)
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
   * Evaluates a step in the range [0.0, 1.0] for curve, returning a
   * coordinate on the curve and a tangent. The tangent will be
   * normalized, to be of unit length.
   *
   * @param curveIndex the curve index
   * @param step       the step in [0.0, 1.0]
   * @param coordWorld the output world coordinate
   * @param tanWorld   the output world tangent
   * @param coordLocal the output local coordinate
   * @param tanLocal   the output local tangent
   * @return the world coordinate
   * @see Curve3#eval(float, Vec3, Vec3)
   * @see Transform3#mulPoint(Transform3, Vec3, Vec3)
   * @see Transform3#mulDir(Transform3, Vec3, Vec3)
   */
  public Vec3 eval (
      final int curveIndex,
      final float step,
      final Vec3 coordWorld,
      final Vec3 tanWorld,
      final Vec3 coordLocal,
      final Vec3 tanLocal ) {

    this.curves.get(curveIndex).eval(step, coordLocal, tanLocal);
    Transform3.mulPoint(this.transform, coordLocal, coordWorld);
    Transform3.mulDir(this.transform, tanLocal, tanWorld);
    return coordWorld;
  }

  /**
   * Gets a curve from this curve entity.
   *
   * @param i the index
   * @return the curve.
   */
  public Curve3 getCurve ( final int i ) {

    return this.curves.get(Utils.mod(i, this.curves.size()));
  }

  /**
   * Returns an iterator, which allows an enhanced for-loop to access
   * the curves in the entity.
   *
   * @return the iterator
   * @see List#iterator()
   */
  @Override
  public Iterator < Curve3 > iterator ( ) {

    return this.curves.iterator();
  }

  /**
   * Returns a String of Python code targeted toward the Blender 2.8x
   * API. This code is brittle and is used for internal testing
   * purposes, i.e., to compare how curve geometry looks in Blender (the
   * control) versus in the library (the test).
   *
   * @return the string
   */
  @Experimental
  public String toBlenderCode ( ) {

    return this.toBlenderCode(12, "FULL", 0.0f, 0.0f);
  }

  /**
   * Returns a String of Python code targeted toward the Blender 2.8x
   * API. This code is brittle and is used for internal testing
   * purposes, i.e., to compare how curve geometry looks in Blender (the
   * control) versus in the library (the test).
   *
   * @param uRes       the resolution u
   * @param fillMode   the fill mode: "FULL", "BACK", "FRONT", "HALF"
   * @param extrude    geometry extrusion amount
   * @param bevelDepth depth of geometry extrusion bevel
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

    final StringBuilder result = new StringBuilder(2048);
    result.append("from bpy import data as D, context as C\n\n")
        .append("curve_entity = {\"name\": \"")
        .append(this.name)
        .append("\", \"transform\": ")
        .append(this.transform.toBlenderCode())
        .append(", \"curves\": [");

    int curveIndex = 0;
    final int curveLast = this.curves.size() - 1;
    final Iterator < Curve3 > curveItr = this.curves.iterator();
    while ( curveItr.hasNext() ) {

      result.append(curveItr.next().toBlenderCode(uRes, tiltStart, tiltEnd));
      if ( curveIndex < curveLast ) { result.append(',').append(' '); }
      curveIndex++;
    }

    result.append("]}\n\ncrv_data = D.curves.new(")
        .append("curve_entity[\"name\"]")
        .append(", \"CURVE\")\n")
        .append("crv_data.dimensions = \"3D\"\n")
        .append("crv_data.fill_mode = \"")
        .append(fillMode)
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

    return result.toString();
  }

  /**
   * Creates a string representing a Wavefront OBJ file. Renders the
   * curve as a series of line segments; points are <em>not</em> equally
   * distributed along the curve.
   *
   * @param precision the decimal place precision
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
    while ( itr.hasNext() ) {
      final Curve3 curve = itr.next();
      final Vec3[][] segments = curve.evalRange(precision);
      final int len = segments.length;

      result.append('g')
          .append(' ')
          .append(curve.name)
          .append('\n')
          .append('\n');

      for ( int i = 0; i < len; ++i ) {
        final Vec3 coord = segments[i][0];
        result.append('v')
            .append(' ')
            .append(coord.toObjString())
            .append('\n');
      }

      result.append('\n');

      for ( int i = 1, j = 2; i < len; ++i, ++j ) {
        result.append('l')
            .append(' ')
            .append(offset + i)
            .append(' ')
            .append(offset + j)
            .append('\n');
      }

      if ( curve.closedLoop ) {
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
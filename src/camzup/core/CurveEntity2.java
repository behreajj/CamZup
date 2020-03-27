package camzup.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * An entity which contains a transform that is applied to a list of
 * curves. The curves may references a list of materials by index.
 */
public class CurveEntity2 extends Entity2
    implements Iterable < Curve2 >, IVolume2, ISvgWritable {

  /**
   * The list of curves held by the entity.
   */
  public final List < Curve2 > curves;

  {
    this.curves = new ArrayList <>();
  }

  /**
   * The default constructor.
   */
  public CurveEntity2 ( ) { super(); }

  /**
   * Creates a named curve entity.
   *
   * @param name the name
   */
  public CurveEntity2 ( final String name ) { super(name); }

  /**
   * Creates a curve entity from a name, transform and list of curves.
   *
   * @param name      the name
   * @param transform the transform
   * @param curves    the list of curves
   */
  public CurveEntity2 (
      final String name,
      final Transform2 transform,
      final Curve2 ... curves ) {

    super(name, transform);
    this.appendAll(curves);
  }

  /**
   * Creates a curve entity from a transform and list of curve.
   *
   * @param transform the transform
   * @param curves    the list of curves
   */
  public CurveEntity2 (
      final Transform2 transform,
      final Curve2 ... curves ) {

    super(transform);
    this.appendAll(curves);
  }

  /**
   * Appends a curve to this curve entity.
   *
   * @param curve the curve
   * @return this curve entity
   */
  @Chainable
  public CurveEntity2 append ( final Curve2 curve ) {

    if ( curve != null ) { this.curves.add(curve); }
    return this;
  }

  /**
   * Appends a list of curves to this curve entity.
   *
   * @param curves the curves
   * @return this curve entity
   */
  @Chainable
  public CurveEntity2 appendAll ( final Curve2 ... curves ) {

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
   * @return the curve.
   * @see Utils#mod(int, int)
   */
  public Curve2 get ( final int i ) {

    return this.curves.get(Utils.mod(i, this.curves.size()));
  }

  /**
   *
   * /** Returns an iterator, which allows an enhanced for-loop to
   * access the curves in the entity.
   *
   * @return the iterator
   * @see List#iterator()
   */
  @Override
  public Iterator < Curve2 > iterator ( ) {

    return this.curves.iterator();
  }

  /**
   * Scales the entity by a scalar.
   *
   * @param scalar the scalar
   * @return this entity
   */
  @Override
  @Chainable
  public CurveEntity2 scaleBy ( final float scalar ) {

    this.transform.scaleBy(scalar);
    return this;
  }

  /**
   * Scales the entity by a non-uniform scalar.
   *
   * @param scalar the scalar
   * @return the entity
   */
  @Override
  @Chainable
  public CurveEntity2 scaleBy ( final Vec2 scalar ) {

    this.transform.scaleBy(scalar);
    return this;
  }

  /**
   * Scales the entity to a uniform size.
   *
   * @param scalar the size
   * @return this entity
   */
  @Override
  @Chainable
  public CurveEntity2 scaleTo ( final float scalar ) {

    this.transform.scaleTo(scalar);
    return this;
  }

  /**
   * Scales the entity to a non-uniform size.
   *
   * @param scalar the size
   * @return this entity
   */
  @Override
  @Chainable
  public CurveEntity2 scaleTo ( final Vec2 scalar ) {

    this.transform.scaleTo(scalar);
    return this;
  }

  /**
   * Eases the entity to a scale by a step over time.
   *
   * @param scalar the scalar
   * @param step   the step
   * @return this entity
   */
  @Override
  @Chainable
  public CurveEntity2 scaleTo (
      final Vec2 scalar,
      final float step ) {

    this.transform.scaleTo(scalar, step);
    return this;
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

    final StringBuilder pyCd = new StringBuilder(2048);

    pyCd.append("from bpy import data as D, context as C\n\n")
        .append("curve_entity = {\"name\": \"")
        .append(this.name)
        .append("\", \"transform\": ")
        .append(this.transform.toBlenderCode())
        .append(", \"curves\": [");

    int curveIndex = 0;
    final int curveLast = this.curves.size() - 1;
    final Iterator < Curve2 > curveItr = this.curves.iterator();
    while ( curveItr.hasNext() ) {
      pyCd.append(curveItr.next().toBlenderCode(uRes));
      if ( curveIndex < curveLast ) { pyCd.append(',').append(' '); }
      curveIndex++;
    }

    pyCd.append("]}\n\ncrv_data = D.curves.new(")
        .append("curve_entity[\"name\"]")
        .append(", \"CURVE\")\n")
        .append("crv_data.dimensions = \"2D\"\n")
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
   * Creates a string representing a Wavefront OBJ file. Renders the
   * curve as a series of line segments. Points are <em>not</em> equally
   * distributed along the curve.
   *
   * @param precision the decimal place precision
   * @return the string
   */
  public String toObjString ( final int precision ) {

    final StringBuilder obj = new StringBuilder(2048);

    obj.append('o')
        .append(' ')
        .append(this.name)
        .append('\n')
        .append('\n');

    int offset = 0;

    final Iterator < Curve2 > itr = this.curves.iterator();
    while ( itr.hasNext() ) {
      final Curve2 curve = itr.next();
      final Vec2[][] segments = Curve2.evalRange(curve, precision);
      final int len = segments.length;

      obj.append('g')
          .append(' ')
          .append(curve.name)
          .append('\n')
          .append('\n');

      for ( int i = 0; i < len; ++i ) {
        final Vec2 coord = segments[i][0];
        obj.append('v')
            .append(' ')
            .append(coord.toObjString())
            .append('\n');
      }

      obj.append('\n');

      for ( int i = 1, j = 2; i < len; ++i, ++j ) {
        obj.append('l')
            .append(' ')
            .append(offset + i)
            .append(' ')
            .append(offset + j)
            .append('\n');
      }

      if ( curve.closedLoop ) {
        obj.append('l')
            .append(' ')
            .append(offset + len)
            .append(' ')
            .append(offset + 1)
            .append('\n');
      }

      offset += len;
      obj.append('\n');
    }

    return obj.toString();
  }

  /**
   * Creates a string representing a group node in the SVG format.
   *
   * @return the string
   */
  @Override
  public String toSvgElm ( ) {

    return this.toSvgElm(new MaterialSolid[] {});
  }

  /**
   * Creates a string representing a group node in the SVG format.
   *
   * @param material the material to use
   * @return the string
   */
  public String toSvgElm ( final MaterialSolid material ) {

    return this.toSvgElm(new MaterialSolid[] { material });
  }

  /**
   * Creates a string representing a group node in the SVG format. This
   * SVG is designed for compatibility with Processing, not for
   * efficiency.
   *
   * @param materials the array of materials
   * @return the string
   */
  public String toSvgElm ( final MaterialSolid[] materials ) {

    // TODO: Include camera zoom (max(zoomx, zoomy), which is in turn
    // combined with transform's min dimension?

    final StringBuilder result = new StringBuilder(1024)
        .append("<g id=\"")
        .append(this.name.toLowerCase())
        .append('\"')
        .append(' ')
        .append(this.transform.toSvgString())
        .append('>')
        .append('\n');

    final float scale = Transform2.minDimension(this.transform);
    int matLen = 0;
    boolean includesMats = false;
    if ( materials != null ) {
      matLen = materials.length;
      includesMats = matLen > 0;
    }

    /* If no materials are present, use a default instead. */
    if ( !includesMats ) {
      result.append(MaterialSolid.defaultSvgMaterial(scale));
    }

    final Iterator < Curve2 > curveItr = this.curves.iterator();
    while ( curveItr.hasNext() ) {
      final Curve2 curve = curveItr.next();

      if ( includesMats ) {
        final int vmatidx = Utils.mod(curve.materialIndex, matLen);
        final MaterialSolid material = materials[vmatidx];
        result.append("<g ")
            .append(material.toSvgString(scale))
            .append('>')
            .append('\n');
      }

      result.append(curve.toSvgElm()).append('\n');

      /* Close out material group. */
      if ( includesMats ) { result.append("</g>\n"); }
    }

    /* Close out default material. */
    if ( !includesMats ) { result.append("</g>\n"); }

    result.append("</g>");
    return result.toString();
  }

  /**
   * Evaluates a step in the range [0.0, 1.0] for curve, returning a
   * coordinate on the curve and a tangent. The tangent will be
   * normalized, to be of unit length.
   *
   * @param ce         the curve entity
   * @param curveIndex the curve index
   * @param step       the step in [0.0, 1.0]
   * @param coordWorld the output world coordinate
   * @param tanWorld   the output world tangent
   * @return the world coordinate
   * @see CurveEntity2#eval(CurveEntity2, int, float, Vec2, Vec2, Vec2,
   *      Vec2)
   */
  public static Vec2 eval (
      final CurveEntity2 ce,
      final int curveIndex,
      final float step,
      final Vec2 coordWorld,
      final Vec2 tanWorld ) {

    return CurveEntity2.eval(
        ce, curveIndex, step,
        coordWorld, tanWorld,
        new Vec2(), new Vec2());
  }

  /**
   * Evaluates a step in the range [0.0, 1.0] for curve, returning a
   * coordinate on the curve and a tangent. The tangent will be
   * normalized, to be of unit length.
   *
   * @param ce         the curve entity
   * @param curveIndex the curve index
   * @param step       the step in [0.0, 1.0]
   * @param coordWorld the output world coordinate
   * @param tanWorld   the output world tangent
   * @param coordLocal the output local coordinate
   * @param tanLocal   the output local tangent
   * @return the world coordinate
   * @see Curve2#eval(Curve2, float, Vec2, Vec2)
   * @see Transform2#mulPoint(Transform2, Vec2, Vec2)
   * @see Transform2#mulDir(Transform2, Vec2, Vec2)
   */
  public static Vec2 eval (
      final CurveEntity2 ce,
      final int curveIndex,
      final float step,
      final Vec2 coordWorld,
      final Vec2 tanWorld,
      final Vec2 coordLocal,
      final Vec2 tanLocal ) {

    Curve2.eval(
        ce.curves.get(curveIndex),
        step, coordLocal, tanLocal);
    Transform2.mulPoint(ce.transform, coordLocal, coordWorld);
    Transform2.mulDir(ce.transform, tanLocal, tanWorld);
    return coordWorld;
  }

  /**
   * Creates a curve entity from a mesh entity.
   *
   * @param me     the source mesh
   * @param target the output curve
   * @return the curve
   */
  public static CurveEntity2 fromMeshEntity (
      final MeshEntity2 me,
      final CurveEntity2 target ) {

    target.name = me.name;
    target.transform.set(me.transform);

    final Iterator < Mesh2 > meshItr = me.meshes.iterator();
    final List < Curve2 > curves = target.curves;
    curves.clear();

    while ( meshItr.hasNext() ) {
      final Mesh2 mesh = meshItr.next();
      final int facesLen = mesh.faces.length;
      for ( int i = 0; i < facesLen; ++i ) {
        final Curve2 curve = new Curve2();
        Curve2.fromMeshFace(i, mesh, curve);
        curves.add(curve);
      }
    }

    return target;
  }
}

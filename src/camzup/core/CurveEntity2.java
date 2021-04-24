package camzup.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * An entity which contains a transform that is applied to a list of
 * curves.
 */
public class CurveEntity2 extends Entity2 implements Iterable < Curve2 >,
   IVolume2, ISvgWritable {

   /**
    * The list of curves held by the entity.
    */
   public final ArrayList < Curve2 > curves = new ArrayList <>(
      IEntity.DEFAULT_CAPACITY);

   /**
    * The default constructor.
    */
   public CurveEntity2 ( ) {}

   /**
    * Creates a curve entity from a list of curves.
    *
    * @param curves the list of curves
    */
   public CurveEntity2 ( final Curve2... curves ) {

      this.appendAll(curves);
   }

   /**
    * Creates a named curve entity.
    *
    * @param name the name
    */
   public CurveEntity2 ( final String name ) { super(name); }

   /**
    * Creates a curve entity from a list of curves.
    *
    * @param name   the name
    * @param curves the list of curves
    */
   public CurveEntity2 ( final String name, final Curve2... curves ) {

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
   public CurveEntity2 ( final String name, final Transform2 transform,
      final Curve2... curves ) {

      super(name, transform);
      this.appendAll(curves);
   }

   /**
    * Creates a curve entity from a transform and list of curve.
    *
    * @param transform the transform
    * @param curves    the list of curves
    */
   public CurveEntity2 ( final Transform2 transform, final Curve2... curves ) {

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
   public CurveEntity2 append ( final Curve2 curve ) {

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
   public CurveEntity2 appendAll ( final Collection < Curve2 > app ) {

      final Iterator < Curve2 > itr = app.iterator();
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
   public CurveEntity2 appendAll ( final Curve2... app ) {

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
    * @see Curve2#transform(Transform2)
    * @see Transform2#identity(Transform2)
    */
   public CurveEntity2 consumeTransform ( ) {

      final Iterator < Curve2 > itr = this.curves.iterator();
      while ( itr.hasNext() ) { itr.next().transform(this.transform); }
      Transform2.identity(this.transform);
      return this;
   }

   /**
    * Evaluates whether a curve is contained by this curve entity.
    *
    * @param c the curve
    *
    * @return the evaluation
    */
   public boolean contains ( final Curve2 c ) {

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
      final CurveEntity2 other = ( CurveEntity2 ) obj;
      return this.curves.equals(other.curves);
   }

   /**
    * Flips the curve horizontally.
    *
    * @return this entity
    */
   @Override
   public CurveEntity2 flipX ( ) {

      this.transform.flipX();
      return this;
   }

   /**
    * Flips the curve vertically.
    *
    * @return this entity
    */
   @Override
   public CurveEntity2 flipY ( ) {

      this.transform.flipY();
      return this;
   }

   /**
    * Gets a curve from this curve entity.
    *
    * @param i the index
    *
    * @return the curve.
    *
    * @see Utils#mod(int, int)
    */
   public Curve2 get ( final int i ) {

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
   public Vec2 getScale ( final Vec2 target ) {

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
    * /** Returns an iterator, which allows an enhanced for-loop to access the
    * curves in the entity.
    *
    * @return the iterator
    *
    * @see List#iterator()
    */
   @Override
   public Iterator < Curve2 > iterator ( ) { return this.curves.iterator(); }

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
    */
   public CurveEntity2 reframe ( ) {

      /* Find lower and upper bound for all curves. */
      final Vec2 lb = new Vec2(Float.MAX_VALUE, Float.MAX_VALUE);
      final Vec2 ub = new Vec2(Float.MIN_VALUE, Float.MIN_VALUE);

      Iterator < Curve2 > crvItr = this.curves.iterator();
      while ( crvItr.hasNext() ) { Curve2.accumMinMax(crvItr.next(), lb, ub); }

      lb.x = -0.5f * ( lb.x + ub.x );
      lb.y = -0.5f * ( lb.y + ub.y );
      final float scl = Utils.div(1.0f, Utils.max(ub.x - lb.x, ub.y - lb.y));

      crvItr = this.curves.iterator();
      while ( crvItr.hasNext() ) {
         final Curve2 curve = crvItr.next();
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
    * @see Transform2#invMulPoint(Transform2, Vec2, Vec2)
    */
   @Experimental
   public CurveEntity2 relocateKnot ( final int curveIndex, final int knotIndex,
      final Vec2 global, final Vec2 local ) {

      Transform2.invMulPoint(this.transform, global, local);
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
   public boolean remove ( final Curve2 curve ) {

      return this.curves.remove(curve);
   }

   /**
    * Removes a curve at a given index.
    *
    * @param i the index
    *
    * @return the curve
    */
   public Curve2 removeAt ( final int i ) { return this.curves.remove(i); }

   /**
    * Resets the curve entity to an initial state. Sets the transform to an
    * identity and clears the list of curves.
    *
    * @return this entity
    */
   @Override
   public CurveEntity2 reset ( ) {

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
   public CurveEntity2 scaleBy ( final float scalar ) {

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
   public CurveEntity2 scaleBy ( final Vec2 scalar ) {

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
   public CurveEntity2 scaleTo ( final float scalar ) {

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
   public CurveEntity2 scaleTo ( final Vec2 scalar ) {

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
   public CurveEntity2 scaleTo ( final Vec2 scalar, final float step ) {

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
    * @see Transform2#invMulPoint(Transform2, Vec2, Vec2)
    */
   @Experimental
   public CurveEntity2 setKnotCoord ( final int curveIndex, final int knotIndex,
      final Vec2 global, final Vec2 local ) {

      Transform2.invMulPoint(this.transform, global, local);
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
    * @see Transform2#invMulPoint(Transform2, Vec2, Vec2)
    */
   @Experimental
   public CurveEntity2 setKnotForeHandle ( final int curveIndex,
      final int knotIndex, final Vec2 global, final Vec2 local ) {

      Transform2.invMulPoint(this.transform, global, local);
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
    * @see Transform2#invMulPoint(Transform2, Vec2, Vec2)
    */
   @Experimental
   public CurveEntity2 setKnotRearHandle ( final int curveIndex,
      final int knotIndex, final Vec2 global, final Vec2 local ) {

      Transform2.invMulPoint(this.transform, global, local);
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

      return this.toBlenderCode(24, "BOTH", 0.0f, 0.0f);
   }

   /**
    * Returns a String of Python code targeted toward the Blender 2.8x API.
    * This code is brittle and is used for internal testing purposes, i.e., to
    * compare how curve geometry looks in Blender (the control) versus in the
    * library (the test).
    *
    * @param uRes       the resolution u
    * @param fillMode   the fill mode: "BACK", "BOTH", "FRONT", "NONE"
    * @param extrude    geometry extrusion amount
    * @param bevelDepth depth of geometry extrusion bevel
    *
    * @return the string
    */
   @Experimental
   public String toBlenderCode ( final int uRes, final String fillMode,
      final float extrude, final float bevelDepth ) {

      final StringBuilder pyCd = new StringBuilder(2048);

      pyCd.append("from bpy import data as D, context as C\n\n");
      pyCd.append("entity_src = {\"name\": \"");
      pyCd.append(this.name);
      pyCd.append("\", \"transform\": ");
      this.transform.toBlenderCode(pyCd);
      pyCd.append(", \"curves\": [");

      int curveIndex = 0;
      final int curveLast = this.curves.size() - 1;
      final float zoff = 0.0f;
      final Iterator < Curve2 > itr = this.curves.iterator();
      while ( itr.hasNext() ) {
         // final float zoff = 0.0001f * curveIndex;
         itr.next().toBlenderCode(pyCd, uRes, zoff);
         if ( curveIndex < curveLast ) { pyCd.append(',').append(' '); }
         ++curveIndex;
      }

      pyCd.append("]}\n\ncrv_data = D.curves.new(");
      pyCd.append("entity_src[\"name\"]");
      pyCd.append(", \"CURVE\")\n");
      pyCd.append("crv_data.dimensions = \"2D\"\n");
      pyCd.append("crv_data.fill_mode = \"");
      pyCd.append(fillMode);
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
   public CurveEntity2 toOrigin ( ) {

      /* Find lower and upper bound for all curves. */
      final Vec2 lb = new Vec2(Float.MAX_VALUE, Float.MAX_VALUE);
      final Vec2 ub = new Vec2(Float.MIN_VALUE, Float.MIN_VALUE);
      Iterator < Curve2 > itr = this.curves.iterator();
      while ( itr.hasNext() ) { Curve2.accumMinMax(itr.next(), lb, ub); }

      /* Shift curves. */
      lb.x = -0.5f * ( lb.x + ub.x );
      lb.y = -0.5f * ( lb.y + ub.y );

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

      final Iterator < Curve2 > itr = this.curves.iterator();
      while ( itr.hasNext() ) {
         sb.append(itr.next().toString(places));
         if ( itr.hasNext() ) { sb.append(',').append(' '); }
      }

      sb.append(" ] }");
      return sb.toString();
   }

   /**
    * Creates a string representing a group node in the SVG format.<br>
    * <br>
    * Stroke weight is impacted by scaling in transforms, so zoom is a
    * parameter. If nonuniform zooming is used, zoom can be an average of
    * width and height or the maximum dimension.
    *
    * @param zoom scaling from external transforms
    *
    * @return the string
    */
   @Override
   public String toSvgElm ( final float zoom ) {

      return this.toSvgElm(zoom, true, new MaterialSolid[] {});
   }

   /**
    * Creates a string representing a group node in the SVG format.
    *
    * @param zoom        scaling from external transforms
    * @param useSubPaths whether to use sub paths
    * @param material    the material to use
    *
    * @return the string
    */
   public String toSvgElm ( final float zoom, final boolean useSubPaths,
      final MaterialSolid material ) {

      return this.toSvgElm(zoom, useSubPaths, new MaterialSolid[] { material });
   }

   /**
    * Creates a string representing a group node in the SVG format. This SVG
    * is designed for compatibility with Processing, not for efficiency.<br>
    * <br>
    * Stroke weight is impacted by scaling in transforms, so zoom is a
    * parameter. If nonuniform zooming is used, zoom can be an average of
    * width and height or the maximum dimension.<br>
    * <br>
    * If use sub paths is true, then a curve entity will contain one path
    * element with multiple move to commands.
    *
    * @param zoom        scaling from external transforms
    * @param useSubPaths whether to use sub paths
    * @param materials   the array of materials
    *
    * @return the string
    */
   public String toSvgElm ( final float zoom, final boolean useSubPaths,
      final MaterialSolid[] materials ) {

      final StringBuilder svgp = new StringBuilder(1024);
      if ( this.length() < 1 ) { return svgp.toString(); }

      /* For determining if handles are parallel. */
      final Vec2 dir0 = new Vec2();
      final Vec2 dir1 = new Vec2();

      /* Decide how many groups to create based on material. */
      int matLen = 0;
      boolean includesMats = false;
      boolean oneMat = false;
      if ( materials != null ) {
         matLen = materials.length;
         includesMats = matLen > 0;
         oneMat = includesMats && matLen < 2 && materials[0] != null;
      }
      final boolean multipleMats = includesMats && !oneMat;

      /* Adjust stroke weight according to transform scale and camera zoom. */
      final float scale = zoom * Transform2.minDimension(this.transform);

      /* If no materials, append a default; if one, append that. */
      if ( !includesMats ) {
         MaterialSolid.defaultSvgMaterial(svgp, scale);
      } else if ( oneMat ) {
         svgp.append("<g ");
         materials[0].toSvgString(svgp, scale);
         svgp.append('>');
         svgp.append('\n');
      }

      if ( !multipleMats && useSubPaths ) {
         this.toSvgPath(svgp, ISvgWritable.DEFAULT_WINDING_RULE, IUtils.EPSILON,
            dir0, dir1);
      } else {

         /* Append entity group. */
         svgp.append("<g id=\"");
         svgp.append(this.name.toLowerCase());
         svgp.append("\" class=\"");
         svgp.append(this.getClass().getSimpleName().toLowerCase());
         svgp.append('\"');
         svgp.append(' ');
         this.transform.toSvgString(svgp);
         svgp.append('>');
         svgp.append('\n');

         final Iterator < Curve2 > curveItr = this.curves.iterator();
         while ( curveItr.hasNext() ) {
            final Curve2 curve = curveItr.next();

            /*
             * It'd be more efficient to create a definitions block that
             * contains the data for each material, which is then used by a mesh
             * element with xlink. However, such tags are ignored when
             * Processing imports an SVG with loadShape.
             */
            if ( multipleMats ) {
               final int vMatIdx = Utils.mod(curve.materialIndex, matLen);
               final MaterialSolid material = materials[vMatIdx];
               svgp.append("<g ");
               material.toSvgString(svgp, scale);
               svgp.append('>');
               svgp.append('\n');
            }

            curve.toSvgPath(svgp, ISvgWritable.DEFAULT_WINDING_RULE,
               IUtils.EPSILON, dir0, dir1);

            /* Close out material group. */
            if ( multipleMats ) { svgp.append("</g>\n"); }
         }
         svgp.append("</g>\n");
      }

      /* Close out default material or single material. */
      if ( !includesMats || oneMat ) { svgp.append("</g>\n"); }
      return svgp.toString();
   }

   /**
    * Creates a string representing a group node in the SVG format.
    *
    * @param zoom     scaling from external transforms
    * @param material the material to use
    *
    * @return the string
    */
   public String toSvgElm ( final float zoom, final MaterialSolid material ) {

      return this.toSvgElm(zoom, true, new MaterialSolid[] { material });
   }

   /**
    * Renders this curve entity as an SVG string. A default material renders
    * the mesh's fill and stroke. The background of the SVG is transparent.
    * Sets the camera scale to 1.0.
    *
    * @return the SVG string
    */
   @Override
   public String toSvgString ( ) {

      final Vec2 scale = this.transform.getScale(new Vec2());
      return this.toSvgString(ISvgWritable.DEFAULT_ORIGIN_X,
         ISvgWritable.DEFAULT_ORIGIN_Y, 1.0f, -1.0f, Utils.max(
            ISvgWritable.DEFAULT_WIDTH, Utils.abs(scale.x)), Utils.max(
               ISvgWritable.DEFAULT_HEIGHT, Utils.abs(scale.y)));
   }

   /**
    * Internal helper function. Writes the curve entity as a single SVG path
    * with sub-paths.
    *
    * @param svgp        the string builder
    * @param fillRule    the fill rule
    * @param colinearTol the colinear tolerance
    * @param dir0        the first direction
    * @param dir1        the second direction
    *
    * @return the string builder
    */
   StringBuilder toSvgPath ( final StringBuilder svgp, final String fillRule,
      final float colinearTol, final Vec2 dir0, final Vec2 dir1 ) {

      if ( this.length() > 0 ) {
         svgp.append("<path id=\"");
         svgp.append(this.name.toLowerCase());
         svgp.append("\" class=\"");
         svgp.append(this.getClass().getSimpleName().toLowerCase());
         svgp.append("\" fill-rule=\"");
         svgp.append(fillRule);
         svgp.append('\"');
         svgp.append(' ');
         this.transform.toSvgString(svgp);
         svgp.append(" d=\"");
         final Iterator < Curve2 > curveItr = this.curves.iterator();
         while ( curveItr.hasNext() ) {
            curveItr.next().toSvgSubPath(svgp, colinearTol, dir0, dir1);
            svgp.append(' ');
         }
         svgp.append("\"></path>\n");
      }
      return svgp;
   }

   /**
    * Calculates an Axis-Aligned Bounding Box (AABB) encompassing the entity.
    *
    * @param entity the entity
    * @param target the output bounds
    *
    * @return the bounds
    */
   public static Bounds2 calcBounds ( final CurveEntity2 entity,
      final Bounds2 target ) {

      // TODO: Implement for other entities?

      final Vec2 fh = new Vec2();
      final Vec2 rh = new Vec2();
      final Vec2 co = new Vec2();

      target.set(Float.MAX_VALUE, Float.MIN_VALUE);

      final Iterator < Curve2 > itr = entity.iterator();
      final Transform2 tr = entity.transform;
      while ( itr.hasNext() ) {
         Curve2.accumMinMax(itr.next(), target.min, target.max, tr, fh, rh, co);
      }

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
    * @see Curve2#eval(Curve2, float, Vec2, Vec2)
    * @see Transform2#mulPoint(Transform2, Vec2, Vec2)
    * @see Transform2#mulDir(Transform2, Vec2, Vec2)
    */
   @Experimental
   public static Knot2 eval ( final CurveEntity2 ce, final int curveIndex,
      final float step, final Knot2 knWorld, final Knot2 knLocal ) {

      final Transform2 tr = ce.transform;
      Curve2.eval(ce.get(curveIndex), step, knLocal);

      Transform2.mulPoint(tr, knLocal.coord, knWorld.coord);
      Transform2.mulPoint(tr, knLocal.foreHandle, knWorld.foreHandle);
      Transform2.mulPoint(tr, knLocal.rearHandle, knWorld.rearHandle);

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
    * @see CurveEntity2#eval(CurveEntity2, int, float, Vec2, Vec2, Vec2, Vec2)
    */
   public static Ray2 eval ( final CurveEntity2 ce, final int curveIndex,
      final float step, final Ray2 rayWorld, final Ray2 rayLocal ) {

      CurveEntity2.eval(ce, curveIndex, step, rayWorld.origin, rayWorld.dir,
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
    * @see Curve2#eval(Curve2, float, Vec2, Vec2)
    * @see Transform2#mulPoint(Transform2, Vec2, Vec2)
    * @see Transform2#mulDir(Transform2, Vec2, Vec2)
    */
   public static Vec2 eval ( final CurveEntity2 ce, final int curveIndex,
      final float step, final Vec2 coWorld, final Vec2 tnWorld,
      final Vec2 coLocal, final Vec2 tnLocal ) {

      Curve2.eval(ce.get(curveIndex), step, coLocal, tnLocal);
      Transform2.mulPoint(ce.transform, coLocal, coWorld);
      Transform2.mulDir(ce.transform, tnLocal, tnWorld);
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
   public static CurveEntity2 fromMeshEntity ( final MeshEntity2 me,
      final CurveEntity2 target ) {

      target.name = me.name;
      target.transform.set(me.transform);

      final Iterator < Mesh2 > meshItr = me.meshes.iterator();
      final ArrayList < Curve2 > curves = target.curves;
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

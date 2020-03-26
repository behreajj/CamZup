package camzup.pfriendly;

import java.awt.geom.AffineTransform;
import java.util.Iterator;

import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PMatrix2D;
import processing.core.PMatrix3D;
import processing.core.PShape;
import processing.core.PVector;

import camzup.core.Experimental;
import camzup.core.Mat3;
import camzup.core.Mat4;
import camzup.core.Mesh2;
import camzup.core.MeshEntity2;
import camzup.core.Quaternion;
import camzup.core.Transform2;
import camzup.core.Transform3;
import camzup.core.TransformOrder;
import camzup.core.Vec2;
import camzup.core.Vec3;
import camzup.core.Vec4;

/**
 * Facilitates conversions between core objects and Processing
 * objects.
 */
public abstract class Convert {

  /**
   * Converts a PMatrix2D to a 3 x 3 matrix.
   *
   * @param source the source matrix
   * @param target the target matrix
   * @return the matrix
   */
  public static Mat3 toMat3 ( final PMatrix2D source, final Mat3 target ) {

    return target.set(
        source.m00, source.m01, source.m02,
        source.m10, source.m11, source.m12,
        0.0f, 0.0f, 1.0f);
  }

  /**
   * Converts a PMatrix2D to a 4 x 4 matrix.
   *
   * @param source the source matrix
   * @param target the target matrix
   * @return the matrix
   */
  public static Mat4 toMat4 ( final PMatrix2D source, final Mat4 target ) {

    return target.set(
        source.m00, source.m01, 0.0f, source.m02,
        source.m10, source.m11, 0.0f, source.m12,
        0.0f, 0.0f, 1.0f, 0.0f,
        0.0f, 0.0f, 0.0f, 1.0f);
  }

  /**
   * Converts a PMatrix3D to a 4 x 4 matrix.
   *
   * @param source the source matrix
   * @param target the target matrix
   * @return the matrix
   */
  public static Mat4 toMat4 ( final PMatrix3D source, final Mat4 target ) {

    return target.set(
        source.m00, source.m01, source.m02, source.m03,
        source.m10, source.m11, source.m12, source.m13,
        source.m20, source.m21, source.m22, source.m23,
        source.m30, source.m31, source.m32, source.m33);
  }

  /**
   * Converts a Java AWT matrix to a PMatrix2D.
   *
   * @param tr the affine transform
   * @return the PMatrix
   */
  public static PMatrix2D toPMatrix2D ( final AffineTransform tr ) {

    return Convert.toPMatrix2D(tr, (PMatrix2D) null);
  }

  /**
   * Converts a Java AWT matrix to a PMatrix2D.
   *
   * @param tr     the affine transform
   * @param target the output matrix
   * @return the PMatrix
   */
  public static PMatrix2D toPMatrix2D (
      final AffineTransform tr,
      PMatrix2D target ) {

    if ( target == null ) { target = new PMatrix2D(); }

    target.set(
        (float) tr.getScaleX(),
        (float) tr.getShearX(),
        (float) tr.getTranslateX(),
        (float) tr.getShearY(),
        (float) tr.getScaleY(),
        (float) tr.getTranslateY());
    return target;
  }

  /**
   * Converts a 3 x 3 matrix to a PMatrix2D.
   *
   * @param source the source matrix
   * @return the PMatrix2D
   */
  public static PMatrix2D toPMatrix2D ( final Mat3 source ) {

    return Convert.toPMatrix2D(source, (PMatrix2D) null);
  }

  /**
   * Converts a 3 x 3 matrix to a PMatrix2D.
   *
   * @param source the source matrix
   * @param target the target matrix
   * @return the PMatrix2D
   */
  public static PMatrix2D toPMatrix2D (
      final Mat3 source,
      PMatrix2D target ) {

    if ( target == null ) { target = new PMatrix2D(); }

    target.set(
        source.m00, source.m01, source.m02,
        source.m01, source.m11, source.m12);
    return target;
  }

  /**
   * Converts a 2D transform to a PMatrix2D.
   *
   * @param tr2   the transform
   * @param order the transform order
   * @return the matrix
   */
  public static PMatrix2D toPMatrix2D (
      final Transform2 tr2,
      final TransformOrder order ) {

    return Convert.toPMatrix2D(tr2, order, (PMatrix2D) null);
  }

  /**
   * Converts a 2D transform to a PMatrix2D.
   *
   * @param tr2    the transform
   * @param order  the transform order
   * @param target the output matrix
   * @return the matrix
   */
  public static PMatrix2D toPMatrix2D (
      final Transform2 tr2,
      final TransformOrder order,
      PMatrix2D target ) {

    if ( target == null ) { target = new PMatrix2D(); }

    final Vec2 dim = tr2.getScale(new Vec2());
    final Vec2 loc = tr2.getLocation(new Vec2());
    final float angle = tr2.getRotation();

    target.reset();

    switch ( order ) {

      case RST:

        target.rotateZ(angle);
        target.scale(dim.x, dim.y);
        target.translate(loc.x, loc.y);

        return target;

      case RTS:

        target.rotateZ(angle);
        target.translate(loc.x, loc.y);
        target.scale(dim.x, dim.y);

        return target;

      case SRT:

        target.scale(dim.x, dim.y);
        target.rotateZ(angle);
        target.translate(loc.x, loc.y);

        return target;

      case STR:

        target.scale(dim.x, dim.y);
        target.translate(loc.x, loc.y);
        target.rotateZ(angle);

        return target;

      case TSR:

        target.translate(loc.x, loc.y);
        target.scale(dim.x, dim.y);
        target.rotateZ(angle);

        return target;

      case TRS:

      default:

        target.translate(loc.x, loc.y);
        target.rotateZ(angle);
        target.scale(dim.x, dim.y);

        return target;
    }
  }

  /**
   * Converts a Java AWT matrix to a PMatrix3D.
   *
   * @param tr     the affine transform
   * @param target the output matrix
   * @return the PMatrix
   */
  public static PMatrix3D toPMatrix3D (
      final AffineTransform tr,
      PMatrix3D target ) {

    if ( target == null ) { target = new PMatrix3D(); }

    target.set(
        (float) tr.getScaleX(),
        (float) tr.getShearX(),
        0.0f,
        (float) tr.getTranslateX(),

        (float) tr.getShearY(),
        (float) tr.getScaleY(),
        0.0f,
        (float) tr.getTranslateY(),

        0.0f, 0.0f, 1.0f, 0.0f,
        0.0f, 0.0f, 0.0f, 1.0f);

    return target;
  }

  /**
   * Converts a quaternion to a PMatrix3D.
   *
   * @param source the quaternion
   * @return the matrix
   */
  public static PMatrix3D toPMatrix3D ( final Quaternion source ) {

    return Convert.toPMatrix3D(source, (PMatrix3D) null);
  }

  /**
   * Converts a quaternion to a PMatrix3D.
   *
   * @param source the quaternion
   * @param target the matrix
   * @return the matrix
   */
  public static PMatrix3D toPMatrix3D (
      final Quaternion source,
      PMatrix3D target ) {

    if ( target == null ) { target = new PMatrix3D(); }

    final float w = source.real;
    final Vec3 i = source.imag;
    final float x = i.x;
    final float y = i.y;
    final float z = i.z;

    final float x2 = x + x;
    final float y2 = y + y;
    final float z2 = z + z;
    final float xsq2 = x * x2;
    final float ysq2 = y * y2;
    final float zsq2 = z * z2;
    final float xy2 = x * y2;
    final float xz2 = x * z2;
    final float yz2 = y * z2;
    final float wx2 = w * x2;
    final float wy2 = w * y2;
    final float wz2 = w * z2;

    target.set(
        1.0f - ysq2 - zsq2, xy2 - wz2, xz2 + wy2, 0.0f,
        xy2 + wz2, 1.0f - xsq2 - zsq2, yz2 - wx2, 0.0f,
        xz2 - wy2, yz2 + wx2, 1.0f - xsq2 - ysq2, 0.0f,
        0.0f, 0.0f, 0.0f, 1.0f);
    return target;
  }

  /**
   * Converts a transform to a PMatrix3D.
   *
   * @param tr3   the transform
   * @param order the transform order
   * @return the transform
   */
  public static PMatrix3D toPMatrix3D (
      final Transform3 tr3,
      final TransformOrder order ) {

    return Convert.toPMatrix3D(tr3, order, (PMatrix3D) null);
  }

  /**
   * Converts a transform to a PMatrix3D.
   *
   * @param tr3    the transform
   * @param order  the transform order
   * @param target the output PMatrix3D
   * @return the transform
   */
  public static PMatrix3D toPMatrix3D (
      final Transform3 tr3,
      final TransformOrder order,
      PMatrix3D target ) {

    if ( target == null ) { target = new PMatrix3D(); }

    final Quaternion q = tr3.getRotation(new Quaternion());
    final Vec3 dim = tr3.getScale(new Vec3());
    final Vec3 loc = tr3.getLocation(new Vec3());

    target.reset();

    switch ( order ) {

      case RST:

        PMatAux.rotate(q, target);
        target.scale(dim.x, dim.y, dim.z);
        target.translate(loc.x, loc.y, loc.z);

        return target;

      case RTS:

        PMatAux.rotate(q, target);
        target.translate(loc.x, loc.y, loc.z);
        target.scale(dim.x, dim.y, dim.z);

        return target;

      case SRT:

        target.scale(dim.x, dim.y, dim.z);
        PMatAux.rotate(q, target);
        target.translate(loc.x, loc.y, loc.z);

        return target;

      case STR:

        target.scale(dim.x, dim.y, dim.z);
        target.translate(loc.x, loc.y, loc.z);
        PMatAux.rotate(q, target);

        return target;

      case TSR:

        target.translate(loc.x, loc.y, loc.z);
        target.scale(dim.x, dim.y, dim.z);
        PMatAux.rotate(q, target);

        return target;

      case TRS:

      default:

        target.translate(loc.x, loc.y, loc.z);
        PMatAux.rotate(q, target);
        target.scale(dim.x, dim.y, dim.z);

        return target;
    }
  }

  @Experimental
  public static PShape toPShape (
      final PGraphics rndr,
      final MeshEntity2 me2 ) {

    final PShape result = rndr.createShape(PConstants.GROUP);
    final Iterator < Mesh2 > itr = me2.meshes.iterator();
    while ( itr.hasNext() ) {
      final Mesh2 mesh = itr.next();
      final Vec2[] vs = mesh.coords;
      final Vec2[] vts = mesh.texCoords;
      final int[][][] faces = mesh.faces;

      final PShape child = rndr.createShape(PConstants.GROUP);
      child.setName(mesh.name);

      final int len0 = faces.length;
      for ( int i = 0; i < len0; ++i ) {
        final int[][] verts = faces[i];
        final int len1 = verts.length;

        final PShape face = rndr.createShape();
        face.beginShape(PConstants.POLYGON);
        for ( int j = 0; j < len1; ++j ) {
          final int[] vert = verts[j];
          final Vec2 v = vs[vert[0]];
          final Vec2 vt = vts[vert[1]];
          face.vertex(v.x, v.y, vt.x, vt.y);
        }
        face.endShape(PConstants.CLOSE);

        child.addChild(face);
      }
      result.addChild(child);
    }

    result.setName(me2.name);
    final PMatrix2D transform = Convert.toPMatrix2D(
        me2.transform,
        TransformOrder.RST,
        new PMatrix2D());
    result.applyMatrix(transform);

    result.setFill(true);
    result.setFill(IUp.DEFAULT_FILL_COLOR);

    result.setStroke(false);
    result.setStroke(IUp.DEFAULT_STROKE_COLOR);
    result.setStrokeWeight(IUp.DEFAULT_STROKE_WEIGHT);

    return result;
  }

  /**
   * Converts a Vec2 to a PVector.
   *
   * @param source the source vector
   * @return the vector
   */
  public static PVector toPVector ( final Vec2 source ) {

    return Convert.toPVector(source, (PVector) null);
  }

  /**
   * Converts a Vec2 to a PVector.
   *
   * @param source the source vector
   * @param target the target vector
   * @return the vector
   */
  public static PVector toPVector ( final Vec2 source, PVector target ) {

    if ( target == null ) { target = new PVector(); }
    return target.set(source.x, source.y, 0.0f);
  }

  /**
   * Converts a Vec3 to a PVector.
   *
   * @param source the source vector
   * @return the vector
   */
  public static PVector toPVector ( final Vec3 source ) {

    return Convert.toPVector(source, (PVector) null);
  }

  /**
   * Converts a Vec3 to a PVector.
   *
   * @param source the source vector
   * @param target the target vector
   * @return the vector
   */
  public static PVector toPVector ( final Vec3 source, PVector target ) {

    if ( target == null ) { target = new PVector(); }
    return target.set(source.x, source.y, source.z);
  }

  /**
   * Converts a Vec4 to a PVector.
   *
   * @param source the source vector
   * @return the vector
   */
  public static PVector toPVector ( final Vec4 source ) {

    return Convert.toPVector(source, (PVector) null);
  }

  /**
   * Converts a Vec4 to a PVector.
   *
   * @param source the source vector
   * @param target the target vector
   * @return the vector
   */
  public static PVector toPVector ( final Vec4 source, PVector target ) {

    if ( target == null ) { target = new PVector(); }
    return target.set(source.x, source.y, source.z);
  }

  /**
   * Converts from a PVector to a Vec2.
   *
   * @param source the source vector
   * @param target the target vector
   * @return the vector
   */
  public static Vec2 toVec2 ( final PVector source, final Vec2 target ) {

    return target.set(source.x, source.y);
  }

  /**
   * Converts from a PVector to a Vec3.
   *
   * @param source the source vector
   * @param target the target vector
   * @return the vector
   */
  public static Vec3 toVec3 ( final PVector source, final Vec3 target ) {

    return target.set(source.x, source.y, source.z);
  }

  /**
   * Converts from a PVector to a Vec4.
   *
   * @param source the source vector
   * @param target the target vector
   * @return the vector
   */
  public static Vec4 toVec4 ( final PVector source, final Vec4 target ) {

    return target.set(source.x, source.y, source.z, 0.0f);
  }
}

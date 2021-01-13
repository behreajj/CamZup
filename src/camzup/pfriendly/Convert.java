package camzup.pfriendly;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import camzup.core.ArcMode;
import camzup.core.Curve2;
import camzup.core.Curve3;
import camzup.core.CurveEntity2;
import camzup.core.CurveEntity3;
import camzup.core.Experimental;
import camzup.core.IUtils;
import camzup.core.Img;
import camzup.core.Knot2;
import camzup.core.Knot3;
import camzup.core.Mat3;
import camzup.core.Mat4;
import camzup.core.Mesh;
import camzup.core.Mesh2;
import camzup.core.Mesh3;
import camzup.core.MeshEntity2;
import camzup.core.MeshEntity3;
import camzup.core.Quaternion;
import camzup.core.Recursive;
import camzup.core.Transform2;
import camzup.core.Transform3;
import camzup.core.TransformOrder;
import camzup.core.Utils;
import camzup.core.Vec2;
import camzup.core.Vec3;
import camzup.core.Vec4;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PMatrix2D;
import processing.core.PMatrix3D;
import processing.core.PShape;
import processing.core.PVector;

import processing.opengl.PGraphicsOpenGL;
import processing.opengl.PShapeOpenGL;

/**
 * Facilitates conversions between core objects and Processing objects.
 */
public abstract class Convert {

   /**
    * Discourage overriding with a private constructor.
    */
   private Convert ( ) {}

   /**
    * Converts a 2D PShape to a curve entity. Support for this conversion is
    * <em>very</em> limited; its primary target is PShapes created from
    * importing an SVG. For best results, use a PShape consisting of quadratic
    * and cubic Bezier curves.
    *
    * @param source the source shape
    * @param target the target curve entity
    *
    * @return the curve entity
    */
   public static CurveEntity2 toCurveEntity2 ( final PShape source,
      final CurveEntity2 target ) {

      target.appendAll(Convert.toCurve2(source, new ArrayList < Curve2 >()));
      target.name = source.getName();
      return target;
   }

   /**
    * Converts a PImage to an image.
    *
    * @param pimg   the Processing image
    * @param target the output image
    *
    * @return the image
    */
   @Experimental
   public static Img toImg ( final PImage pimg, final Img target ) {

      pimg.loadPixels();
      final int fmt = pimg.format;
      final int[] pxSrc = pimg.pixels;
      final int pxLen = pxSrc.length;
      int w = pimg.width;
      int h = pimg.height;
      final int wh = w * h;
      if ( wh != pxLen ) {
         w *= pimg.pixelDensity;
         h *= pimg.pixelDensity;
      }

      target.reallocate(w, h);
      final int[] pxTrg = target.getPixels();

      switch ( fmt ) {

         /* Seems like color is in AARRGGBB format anyway? */
         case PConstants.RGB:
         case PConstants.ARGB:
            System.arraycopy(pxSrc, 0, pxTrg, 0, pxLen);
            break;

         case PConstants.ALPHA:
         default:
            for ( int i = 0; i < pxLen; ++i ) {
               final int a = pxSrc[i];
               pxTrg[i] = a << 0x18 | a << 0x10 | a << 0x08 | a;
            }
      }

      return target;
   }

   /**
    * Converts a PMatrix2D to a 3 x 3 matrix.
    *
    * @param source the source matrix
    * @param target the target matrix
    *
    * @return the matrix
    */
   public static Mat3 toMat3 ( final PMatrix2D source, final Mat3 target ) {

      return target.set(source.m00, source.m01, source.m02, source.m10,
         source.m11, source.m12, 0.0f, 0.0f, 1.0f);
   }

   /**
    * Converts a PMatrix2D to a 4 x 4 matrix.
    *
    * @param source the source matrix
    * @param target the target matrix
    *
    * @return the matrix
    */
   public static Mat4 toMat4 ( final PMatrix2D source, final Mat4 target ) {

      return target.set(source.m00, source.m01, 0.0f, source.m02, source.m10,
         source.m11, 0.0f, source.m12, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f,
         1.0f);
   }

   /**
    * Converts a PMatrix3D to a 4 x 4 matrix.
    *
    * @param source the source matrix
    * @param target the target matrix
    *
    * @return the matrix
    */
   public static Mat4 toMat4 ( final PMatrix3D source, final Mat4 target ) {

      return target.set(source.m00, source.m01, source.m02, source.m03,
         source.m10, source.m11, source.m12, source.m13, source.m20, source.m21,
         source.m22, source.m23, source.m30, source.m31, source.m32,
         source.m33);
   }

   /**
    * Creates a PImage from an input image.
    *
    * @param img the image
    *
    * @return the image
    */
   public static PImage toPImage ( final Img img ) {

      return Convert.toPImage(img, ( PApplet ) null);
   }

   /**
    * Creates a PImage from an input image. The PImage defaults to
    * {@link PConstants#ARGB} with a pixel density of 1. If
    * <code>parent</code> is not null, the image's parent is set to the
    * applet.
    *
    * @param img    the image
    * @param parent the parent
    *
    * @return the image
    */
   public static PImage toPImage ( final Img img, final PApplet parent ) {

      final PImage pimg = new PImage(img.getWidth(), img.getHeight(),
         PConstants.ARGB, 1);
      if ( parent != null ) { pimg.parent = parent; }

      final int[] pxSrc = img.getPixels();
      final int pxLen = pxSrc.length;
      final int[] pxTrg = pimg.pixels;
      pimg.loadPixels();
      System.arraycopy(pxSrc, 0, pxTrg, 0, pxLen);
      pimg.updatePixels();

      return pimg;
   }

   /**
    * Converts a 3 x 3 matrix to a PMatrix2D.
    *
    * @param source the source matrix
    * @param target the target matrix
    *
    * @return the PMatrix2D
    */
   public static PMatrix2D toPMatrix2D ( final Mat3 source,
      final PMatrix2D target ) {

      target.set(source.m00, source.m01, source.m02, source.m01, source.m11,
         source.m12);
      return target;
   }

   /**
    * Converts a 2D transform to a PMatrix2D.
    *
    * @param tr2    the transform
    * @param order  the transform order
    * @param target the output matrix
    *
    * @return the matrix
    */
   public static PMatrix2D toPMatrix2D ( final Transform2 tr2,
      final TransformOrder order, final PMatrix2D target ) {

      target.reset();

      final Vec2 dim = tr2.getScale(new Vec2());
      final Vec2 loc = tr2.getLocation(new Vec2());
      final float angle = tr2.getRotation();

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

         case R:

            target.rotateZ(angle);
            return target;

         case RS:

            target.rotateZ(angle);
            target.scale(dim.x, dim.y);
            return target;

         case RT:

            target.rotateZ(angle);
            target.translate(loc.x, loc.y);
            return target;

         case S:

            target.scale(dim.x, dim.y);
            return target;

         case SR:

            target.scale(dim.x, dim.y);
            target.rotateZ(angle);
            return target;

         case ST:

            target.scale(dim.x, dim.y);
            target.translate(loc.x, loc.y);
            return target;

         case T:

            target.translate(loc.x, loc.y);
            return target;

         case TR:

            target.translate(loc.x, loc.y);
            target.rotateZ(angle);
            return target;

         case TS:

            target.translate(loc.x, loc.y);
            target.scale(dim.x, dim.y);
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
    * Converts a quaternion to a PMatrix3D.
    *
    * @param source the quaternion
    * @param target the matrix
    *
    * @return the matrix
    */
   public static PMatrix3D toPMatrix3D ( final Quaternion source,
      final PMatrix3D target ) {

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

      /* @formatter:off */
      target.set(
         1.0f - ysq2 - zsq2, xy2 - wz2, xz2 + wy2, 0.0f,
         xy2 + wz2, 1.0f - xsq2 - zsq2, yz2 - wx2, 0.0f,
         xz2 - wy2, yz2 + wx2, 1.0f - xsq2 - ysq2, 0.0f,
         0.0f, 0.0f, 0.0f, 1.0f);
      /* @formatter:on */
      return target;
   }

   /**
    * Converts a transform to a PMatrix3D.
    *
    * @param tr3    the transform
    * @param order  the transform order
    * @param target the output PMatrix3D
    *
    * @return the transform
    */
   public static PMatrix3D toPMatrix3D ( final Transform3 tr3,
      final TransformOrder order, final PMatrix3D target ) {

      target.reset();

      final Quaternion q = tr3.getRotation(new Quaternion());
      final Vec3 dim = tr3.getScale(new Vec3());
      final Vec3 loc = tr3.getLocation(new Vec3());

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

         case R:

            PMatAux.rotate(q, target);
            return target;

         case RS:

            PMatAux.rotate(q, target);
            target.scale(dim.x, dim.y, dim.z);
            return target;

         case RT:

            PMatAux.rotate(q, target);
            target.translate(loc.x, loc.y, loc.z);
            return target;

         case S:

            target.scale(dim.x, dim.y, dim.z);
            return target;

         case SR:

            target.scale(dim.x, dim.y, dim.z);
            PMatAux.rotate(q, target);
            return target;

         case ST:

            target.scale(dim.x, dim.y, dim.z);
            target.translate(loc.x, loc.y, loc.z);
            return target;

         case T:

            target.translate(loc.x, loc.y, loc.z);
            return target;

         case TR:

            target.translate(loc.x, loc.y, loc.z);
            PMatAux.rotate(q, target);
            return target;

         case TS:

            target.translate(loc.x, loc.y, loc.z);
            target.scale(dim.x, dim.y, dim.z);
            return target;

         case TRS:
         default:

            target.translate(loc.x, loc.y, loc.z);
            PMatAux.rotate(q, target);
            target.scale(dim.x, dim.y, dim.z);
            return target;
      }
   }

   /**
    * Converts a 2D curve to a PShape. Returns a {@link PConstants#PATH}.
    *
    * @param rndr   the renderer
    * @param source the source mesh
    *
    * @return the PShape
    */
   public static PShape toPShape ( final PGraphics rndr, final Curve2 source ) {

      /*
       * This needs to be of the PATH family because the drawImpl function in
       * PShape is simplistic and buggy. See
       * https://github.com/processing/processing/issues/4879 .
       */
      final boolean dim = rndr.is3D();
      final PShape target = new PShape(rndr, PShape.PATH);
      target.setName(source.name);
      target.set3D(dim);

      final Iterator < Knot2 > itr = source.iterator();

      final Knot2 firstKnot = itr.next();
      Knot2 prevKnot = firstKnot;
      Knot2 currKnot = null;

      Vec2 coord = prevKnot.coord;
      Vec2 foreHandle = null;
      Vec2 rearHandle = null;

      target.beginShape(PConstants.POLYGON);
      target.vertex(coord.x, coord.y);

      while ( itr.hasNext() ) {
         currKnot = itr.next();
         foreHandle = prevKnot.foreHandle;
         rearHandle = currKnot.rearHandle;
         coord = currKnot.coord;
         target.bezierVertex(foreHandle.x, foreHandle.y, rearHandle.x,
            rearHandle.y, coord.x, coord.y);
         prevKnot = currKnot;
      }

      if ( source.closedLoop ) {
         foreHandle = prevKnot.foreHandle;
         rearHandle = firstKnot.rearHandle;
         coord = firstKnot.coord;

         target.bezierVertex(foreHandle.x, foreHandle.y, rearHandle.x,
            rearHandle.y, coord.x, coord.y);
         target.endShape(PConstants.CLOSE);
      } else {
         target.endShape(PConstants.OPEN);
      }

      return target;
   }

   /**
    * Converts a 2D curve entity to a PShape. The entity's transform is
    * converted to a matrix which is applied to the shape.
    *
    * @param rndr   the renderer
    * @param source the source entity
    *
    * @return the PShape
    */
   public static PShape toPShape ( final PGraphics rndr,
      final CurveEntity2 source ) {

      final PShape shape = new PShape(rndr, PConstants.GROUP);
      shape.setName(source.name);
      shape.set3D(rndr.is3D());

      final Iterator < Curve2 > itr = source.curves.iterator();
      while ( itr.hasNext() ) {
         shape.addChild(Convert.toPShape(rndr, itr.next()));
      }

      /* Use loose float version of apply matrix to avoid PShape bug. */
      final Transform2 srctr = source.transform;
      final PMatrix2D m = Convert.toPMatrix2D(srctr, TransformOrder.RST,
         new PMatrix2D());
      shape.resetMatrix();
      shape.applyMatrix(m.m00, m.m01, m.m02, m.m10, m.m11, m.m12);

      /* Stroke weight is scaled with the transform above. */
      final float maxDim = Transform2.maxDimension(srctr);
      shape.setStrokeWeight(Utils.div(rndr.strokeWeight, maxDim));
      shape.disableStyle();
      return shape;
   }

   /**
    * Converts a 2D mesh to a PShape. Returns a {@link PConstants#GROUP} which
    * contains, as a child, each face of the source mesh. Each child is of the
    * type {@link PShape#PATH}. Child shapes record only 2D coordinates, not
    * texture coordinates.
    *
    * @param rndr   the renderer
    * @param source the source mesh
    *
    * @return the PShape
    */
   public static PShape toPShape ( final PGraphics rndr, final Mesh2 source ) {

      /* Decompose source mesh elements. */
      final Vec2[] vs = source.coords;
      final int[][][] faces = source.faces;

      /* Create output target. */
      final boolean dim = rndr.is3D();
      final PShape target = new PShape(rndr, PConstants.GROUP);
      target.setName(source.name);
      target.set3D(dim);

      final int facesLen = faces.length;
      for ( int i = 0; i < facesLen; ++i ) {
         final int[][] verts = faces[i];
         final int vertsLen = verts.length;

         final PShape face = new PShape(rndr, PShape.PATH);
         face.setName("face." + Utils.toPadded(i, 3));
         face.set3D(dim);

         face.beginShape(PConstants.POLYGON);
         for ( int j = 0; j < vertsLen; ++j ) {
            final Vec2 v = vs[verts[j][0]];
            face.vertex(v.x, v.y);
         }
         face.endShape(PConstants.CLOSE);
         target.addChild(face);
      }

      return target;
   }

   /**
    * Converts a 2D mesh entity to a PShape. The entity's transform is
    * converted to a matrix which is applied to the shape.
    *
    * @param rndr   the renderer
    * @param source the source entity
    *
    * @return the PShape
    */
   public static PShape toPShape ( final PGraphics rndr,
      final MeshEntity2 source ) {

      final PShape shape = new PShape(rndr, PConstants.GROUP);
      shape.setName(source.name);
      shape.set3D(rndr.is3D());

      final Iterator < Mesh2 > itr = source.meshes.iterator();
      while ( itr.hasNext() ) {
         shape.addChild(Convert.toPShape(rndr, itr.next()));
      }

      /* Use loose float version of apply matrix to avoid PShape bug. */
      final Transform2 srctr = source.transform;
      final PMatrix2D m = Convert.toPMatrix2D(srctr, TransformOrder.RST,
         new PMatrix2D());
      shape.resetMatrix();
      shape.applyMatrix(m.m00, m.m01, m.m02, m.m10, m.m11, m.m12);

      /* Stroke weight is scaled with the transform above. */
      final float maxDim = Transform2.maxDimension(srctr);
      shape.setStrokeWeight(Utils.div(rndr.strokeWeight, maxDim));
      shape.disableStyle();
      return shape;
   }

   /**
    * Converts a 2D curve to a PShape. Returns {@link PShape#GEOMETRY}.
    *
    * @param rndr   the renderer
    * @param source the source mesh
    *
    * @return the PShape
    */
   public static PShapeOpenGL toPShape ( final PGraphicsOpenGL rndr,
      final Curve2 source ) {

      final boolean dim = rndr.is3D();
      final PShapeOpenGL target = new PShapeOpenGL(rndr, PShape.GEOMETRY);
      target.setName(source.name);
      target.set3D(dim);

      final Iterator < Knot2 > itr = source.iterator();

      final Knot2 firstKnot = itr.next();
      Knot2 prevKnot = firstKnot;
      Knot2 currKnot;

      Vec2 coord = prevKnot.coord;
      Vec2 foreHandle;
      Vec2 rearHandle;

      target.beginShape(PConstants.POLYGON);
      target.vertex(coord.x, coord.y);

      while ( itr.hasNext() ) {
         currKnot = itr.next();
         foreHandle = prevKnot.foreHandle;
         rearHandle = currKnot.rearHandle;
         coord = currKnot.coord;
         target.bezierVertex(foreHandle.x, foreHandle.y, rearHandle.x,
            rearHandle.y, coord.x, coord.y);
         prevKnot = currKnot;
      }

      if ( source.closedLoop ) {
         foreHandle = prevKnot.foreHandle;
         rearHandle = firstKnot.rearHandle;
         coord = firstKnot.coord;

         target.bezierVertex(foreHandle.x, foreHandle.y, rearHandle.x,
            rearHandle.y, coord.x, coord.y);
         target.endShape(PConstants.CLOSE);
      } else {
         target.endShape(PConstants.OPEN);
      }

      return target;
   }

   /**
    * Converts a 3D curve to a PShape. Returns {@link PShape#GEOMETRY}.
    *
    * @param rndr   the renderer
    * @param source the source mesh
    *
    * @return the PShape
    */
   public static PShapeOpenGL toPShape ( final PGraphicsOpenGL rndr,
      final Curve3 source ) {

      final boolean dim = rndr.is3D();
      final PShapeOpenGL target = new PShapeOpenGL(rndr, PShape.GEOMETRY);
      target.setName(source.name);
      target.set3D(dim);

      final Iterator < Knot3 > itr = source.iterator();

      final Knot3 firstKnot = itr.next();
      Knot3 prevKnot = firstKnot;
      Knot3 currKnot = null;

      Vec3 coord = prevKnot.coord;
      Vec3 foreHandle = null;
      Vec3 rearHandle = null;

      target.beginShape(PConstants.POLYGON);
      target.vertex(coord.x, coord.y, coord.z);

      while ( itr.hasNext() ) {
         currKnot = itr.next();
         foreHandle = prevKnot.foreHandle;
         rearHandle = currKnot.rearHandle;
         coord = currKnot.coord;
         target.bezierVertex(foreHandle.x, foreHandle.y, foreHandle.z,
            rearHandle.x, rearHandle.y, rearHandle.z, coord.x, coord.y,
            coord.z);
         prevKnot = currKnot;
      }

      if ( source.closedLoop ) {
         foreHandle = prevKnot.foreHandle;
         rearHandle = firstKnot.rearHandle;
         coord = firstKnot.coord;

         target.bezierVertex(foreHandle.x, foreHandle.y, foreHandle.z,
            rearHandle.x, rearHandle.y, rearHandle.z, coord.x, coord.y,
            coord.z);
         target.endShape(PConstants.CLOSE);
      } else {
         target.endShape(PConstants.OPEN);
      }

      return target;
   }

   /**
    * Converts a 2D curve entity to a PShapeOpenGL. Requires a PGraphicsOpenGL
    * renderer. The entity's transform is converted to a matrix which is
    * applied to the shape.
    *
    * @param rndr   the renderer
    * @param source the source entity
    *
    * @return the PShape
    */
   public static PShapeOpenGL toPShape ( final PGraphicsOpenGL rndr,
      final CurveEntity2 source ) {

      final PShapeOpenGL shape = new PShapeOpenGL(rndr, PConstants.GROUP);
      shape.setName(source.name);
      shape.set3D(rndr.is3D());

      final Iterator < Curve2 > itr = source.curves.iterator();
      while ( itr.hasNext() ) {
         shape.addChild(Convert.toPShape(rndr, itr.next()));
      }

      /* Use loose float version of apply matrix to avoid PShape bug. */
      final Transform2 srctr = source.transform;
      final PMatrix2D m = Convert.toPMatrix2D(srctr, TransformOrder.RST,
         new PMatrix2D());
      shape.resetMatrix();
      shape.applyMatrix(m.m00, m.m01, m.m02, m.m10, m.m11, m.m12);

      /* Stroke weight is scaled with the transform above. */
      final float maxDim = Transform2.maxDimension(srctr);
      shape.setStrokeWeight(Utils.div(rndr.strokeWeight, maxDim));
      shape.disableStyle();
      return shape;
   }

   /**
    * Converts a 3D curve entity to a PShapeOpenGL. Requires a PGraphicsOpenGL
    * renderer. The entity's transform is converted to a matrix which is
    * applied to the shape.
    *
    * @param rndr   the renderer
    * @param source the source entity
    *
    * @return the PShape
    */
   public static PShapeOpenGL toPShape ( final PGraphicsOpenGL rndr,
      final CurveEntity3 source ) {

      final PShapeOpenGL shape = new PShapeOpenGL(rndr, PConstants.GROUP);
      shape.set3D(rndr.is3D());

      final Iterator < Curve3 > itr = source.curves.iterator();
      while ( itr.hasNext() ) {
         shape.addChild(Convert.toPShape(rndr, itr.next()));
      }

      /* Use loose float version of apply matrix to avoid PShape bug. */
      final Transform3 srctr = source.transform;
      final PMatrix3D m = Convert.toPMatrix3D(srctr, TransformOrder.RST,
         new PMatrix3D());
      shape.resetMatrix();
      shape.applyMatrix(m.m00, m.m01, m.m02, m.m03, m.m10, m.m11, m.m12, m.m13,
         m.m20, m.m21, m.m22, m.m23, m.m30, m.m31, m.m32, m.m33);

      /* Stroke weight is scaled with the transform above. */
      final float maxDim = Transform3.maxDimension(srctr);
      shape.setStrokeWeight(Utils.div(rndr.strokeWeight, maxDim));
      shape.disableStyle();
      return shape;
   }

   /**
    * Converts a 2D mesh to a PShape. Audits the mesh to see if all of its
    * faces have the same number of vertices. Meshes composed entirely of
    * triangles or quadrilaterals create much more efficient PShapes than
    * those with ngons.
    *
    * @param rndr   the renderer
    * @param source the source mesh
    *
    * @return the PShape
    *
    * @see Mesh#auditFaceType(Mesh)
    */
   public static PShapeOpenGL toPShape ( final PGraphicsOpenGL rndr,
      final Mesh2 source ) {

      /*
       * audit.get(3) could return null, but auditFaceType will always put 3 and
       * 4 into the map. You could also use getOrDefault .
       */
      final Map < Integer, Integer > audit = Mesh.auditFaceType(source);
      final int facesLen = source.faces.length;
      if ( facesLen == audit.get(3) ) {
         return Convert.toPShapeUniform(rndr, source, PConstants.TRIANGLES);
      } else if ( facesLen == audit.get(4) ) {
         return Convert.toPShapeUniform(rndr, source, PConstants.QUADS);
      } else {
         return Convert.toPShapeNonUniform(rndr, source);
      }
   }

   /**
    * Converts a 3D mesh to a PShape. Audits the mesh to see if all of its
    * faces have the same number of vertices. Meshes composed entirely of
    * triangles or quadrilaterals create much more efficient PShapes than
    * those with ngons.
    *
    * @param rndr   the renderer
    * @param source the source mesh
    *
    * @return the PShape
    *
    * @see Mesh#auditFaceType(Mesh)
    */
   public static PShapeOpenGL toPShape ( final PGraphicsOpenGL rndr,
      final Mesh3 source ) {

      /*
       * audit.get(3) could return null, but auditFaceType will always put 3 and
       * 4 into the map. You could also use getOrDefault .
       */
      final Map < Integer, Integer > audit = Mesh.auditFaceType(source);
      final int facesLen = source.faces.length;
      if ( facesLen == audit.get(3) ) {
         return Convert.toPShapeUniform(rndr, source, PConstants.TRIANGLES);
      } else if ( facesLen == audit.get(4) ) {
         return Convert.toPShapeUniform(rndr, source, PConstants.QUADS);
      } else {
         return Convert.toPShapeNonUniform(rndr, source);
      }
   }

   /**
    * Converts a 2D mesh entity to a PShapeOpenGL. Requires a PGraphicsOpenGL
    * renderer. The entity's transform is converted to a matrix which is
    * applied to the shape.
    *
    * @param rndr   the renderer
    * @param source the source entity
    *
    * @return the PShape
    */
   @Experimental
   public static PShapeOpenGL toPShape ( final PGraphicsOpenGL rndr,
      final MeshEntity2 source ) {

      final PShapeOpenGL shape = new PShapeOpenGL(rndr, PConstants.GROUP);
      shape.setName(source.name);
      shape.set3D(rndr.is3D());

      final Iterator < Mesh2 > itr = source.meshes.iterator();
      while ( itr.hasNext() ) {

         /*
          * Keep this as a separate variable to avoid confusion between the
          * general PShape and PGraphics with the specific PShapeOpenGL and
          * PGraphicsOpenGL. The signature is addChild(PShape p).
          */
         final PShapeOpenGL child = Convert.toPShape(rndr, itr.next());
         shape.addChild(child);
      }

      /* Use loose float version of apply matrix to avoid PShape bug. */
      final Transform2 srctr = source.transform;
      final PMatrix2D m = Convert.toPMatrix2D(srctr, TransformOrder.RST,
         new PMatrix2D());
      shape.resetMatrix();
      shape.applyMatrix(m.m00, m.m01, m.m02, m.m10, m.m11, m.m12);

      /* Stroke weight is scaled with the transform above. */
      final float maxDim = Transform2.maxDimension(srctr);
      shape.setStrokeWeight(Utils.div(rndr.strokeWeight, maxDim));
      // shape.disableStyle();
      return shape;
   }

   /**
    * Converts a 3D mesh entity to a PShapeOpenGL. Requires a PGraphicsOpenGL
    * renderer. The entity's transform is converted to a matrix which is
    * applied to the shape.
    *
    * @param rndr   the renderer
    * @param source the source entity
    *
    * @return the PShape
    */
   public static PShapeOpenGL toPShape ( final PGraphicsOpenGL rndr,
      final MeshEntity3 source ) {

      final PShapeOpenGL shape = new PShapeOpenGL(rndr, PConstants.GROUP);
      shape.set3D(rndr.is3D());

      final Iterator < Mesh3 > itr = source.meshes.iterator();
      while ( itr.hasNext() ) {

         /*
          * Keep this as a separate variable to avoid confusion between the
          * general PShape and PGraphics with the specific PShapeOpenGL and
          * PGraphicsOpenGL. The signature is addChild(PShape p).
          */
         final PShapeOpenGL child = Convert.toPShape(rndr, itr.next());
         shape.addChild(child);
      }

      /* Use loose float version of apply matrix to avoid PShape bug. */
      final Transform3 srctr = source.transform;
      final PMatrix3D m = Convert.toPMatrix3D(srctr, TransformOrder.RST,
         new PMatrix3D());
      shape.resetMatrix();
      shape.applyMatrix(m.m00, m.m01, m.m02, m.m03, m.m10, m.m11, m.m12, m.m13,
         m.m20, m.m21, m.m22, m.m23, m.m30, m.m31, m.m32, m.m33);

      /* Stroke weight is scaled with the transform above. */
      final float maxDim = Transform3.maxDimension(srctr);
      shape.setStrokeWeight(Utils.div(rndr.strokeWeight, maxDim));
      // shape.disableStyle();

      return shape;
   }

   /**
    * Converts a 2D mesh to a PShape. Returns a {@link PConstants#GROUP} which
    * contains, as a child, each face of the source mesh. Each child is of the
    * type {@link PShape#GEOMETRY}. Child shapes record coordinates and
    * texture coordinates.
    *
    * @param rndr   the renderer
    * @param source the source mesh
    *
    * @return the PShape
    */
   public static PShapeOpenGL toPShapeNonUniform ( final PGraphicsOpenGL rndr,
      final Mesh2 source ) {

      /* Decompose source mesh elements. */
      final Vec2[] vs = source.coords;
      final Vec2[] vts = source.texCoords;
      final int[][][] faces = source.faces;

      /* Create output target. */
      final boolean dim = rndr.is3D();
      final PShapeOpenGL target = new PShapeOpenGL(rndr, PConstants.GROUP);
      target.setName(source.name);
      target.setTextureMode(PConstants.NORMAL);
      target.set3D(dim);

      final int facesLen = faces.length;
      for ( int i = 0; i < facesLen; ++i ) {
         final int[][] verts = faces[i];
         final int vertsLen = verts.length;

         final PShapeOpenGL face = new PShapeOpenGL(rndr, PShape.GEOMETRY);
         face.setName("face." + Utils.toPadded(i, 3));
         face.setTextureMode(PConstants.NORMAL);
         face.set3D(dim);

         face.beginShape(PConstants.POLYGON);
         for ( int j = 0; j < vertsLen; ++j ) {
            final int[] vert = verts[j];
            final Vec2 v = vs[vert[0]];
            final Vec2 vt = vts[vert[1]];
            face.vertex(v.x, v.y, vt.x, vt.y);
         }
         face.endShape(PConstants.CLOSE);
         target.addChild(face);
      }

      return target;
   }

   /**
    * Converts a 3D mesh to a PShape. Returns a {@link PConstants#GROUP} which
    * contains, as a child, each face of the source mesh. Each child is of the
    * type {@link PShape#GEOMETRY}. Child shapes record coordinates, texture
    * coordinates and normals.
    *
    * @param rndr   the renderer
    * @param source the source mesh
    *
    * @return the PShape
    */
   public static PShapeOpenGL toPShapeNonUniform ( final PGraphicsOpenGL rndr,
      final Mesh3 source ) {

      /* Decompose source mesh elements. */
      final Vec3[] vs = source.coords;
      final Vec2[] vts = source.texCoords;
      final Vec3[] vns = source.normals;
      final int[][][] faces = source.faces;

      /* Create output target. */
      final boolean dim = rndr.is3D();
      final PShapeOpenGL target = new PShapeOpenGL(rndr, PConstants.GROUP);
      target.setName(source.name);
      target.setTextureMode(PConstants.NORMAL);
      target.set3D(dim);

      final int facesLen = faces.length;
      for ( int i = 0; i < facesLen; ++i ) {
         final int[][] verts = faces[i];
         final int vertsLen = verts.length;

         final PShapeOpenGL face = new PShapeOpenGL(rndr, PShape.GEOMETRY);
         face.setName("face." + Utils.toPadded(i, 3));
         face.setTextureMode(PConstants.NORMAL);
         face.set3D(dim);

         face.beginShape(PConstants.POLYGON);
         for ( int j = 0; j < vertsLen; ++j ) {
            final int[] vert = verts[j];
            final Vec3 v = vs[vert[0]];
            final Vec2 vt = vts[vert[1]];
            final Vec3 vn = vns[vert[2]];
            face.normal(vn.x, vn.y, vn.z);
            face.vertex(v.x, v.y, v.z, vt.x, vt.y);
         }
         face.endShape(PConstants.CLOSE);
         target.addChild(face);
      }

      return target;
   }

   /**
    * Converts a 2D mesh with a uniform number of vertices per face to a
    * PShape. Among the acceptable face types are {@link PConstants#TRIANGLES}
    * ({@value PConstants#TRIANGLES}) and {@link PConstants#QUADS}
    * ({@value PConstants#QUADS}).
    *
    * @param rndr      the renderer
    * @param source    the source mesh
    * @param shapeCode the Processing shape code
    *
    * @return the shape
    */
   public static PShapeOpenGL toPShapeUniform ( final PGraphicsOpenGL rndr,
      final Mesh2 source, final int shapeCode ) {

      /* Decompose source mesh elements. */
      final Vec2[] vs = source.coords;
      final Vec2[] vts = source.texCoords;
      final int[][][] faces = source.faces;

      /* Create output target. */
      final boolean dim = rndr.is3D();
      final PShapeOpenGL target = new PShapeOpenGL(rndr, PShape.GEOMETRY);
      target.setName(source.name);
      target.setTextureMode(PConstants.NORMAL);
      target.set3D(dim);
      target.beginShape(shapeCode);
      final int facesLen = faces.length;
      for ( int i = 0; i < facesLen; ++i ) {
         final int[][] verts = faces[i];
         final int vertsLen = verts.length;

         for ( int j = 0; j < vertsLen; ++j ) {
            final int[] vert = verts[j];
            final Vec2 v = vs[vert[0]];
            final Vec2 vt = vts[vert[1]];
            target.vertex(v.x, v.y, vt.x, vt.y);
         }
      }
      target.endShape(PConstants.CLOSE);
      return target;
   }

   /**
    * Converts a 3D mesh with a uniform number of vertices per face to a
    * PShape. Among the acceptable face types are {@link PConstants#TRIANGLES}
    * ({@value PConstants#TRIANGLES}) and {@link PConstants#QUADS}
    * ({@value PConstants#QUADS}).
    *
    * @param rndr      the renderer
    * @param source    the source mesh
    * @param shapeCode the Processing shape code
    *
    * @return the shape
    */
   public static PShapeOpenGL toPShapeUniform ( final PGraphicsOpenGL rndr,
      final Mesh3 source, final int shapeCode ) {

      /* Decompose source mesh elements. */
      final Vec3[] vs = source.coords;
      final Vec2[] vts = source.texCoords;
      final Vec3[] vns = source.normals;
      final int[][][] faces = source.faces;

      /* Create output target. */
      final boolean dim = rndr.is3D();
      final PShapeOpenGL target = new PShapeOpenGL(rndr, PShape.GEOMETRY);
      target.setName(source.name);
      target.setTextureMode(PConstants.NORMAL);
      target.set3D(dim);
      target.beginShape(shapeCode);
      final int facesLen = faces.length;
      for ( int i = 0; i < facesLen; ++i ) {
         final int[][] verts = faces[i];
         final int vertsLen = verts.length;

         for ( int j = 0; j < vertsLen; ++j ) {
            final int[] vert = verts[j];
            final Vec3 v = vs[vert[0]];
            final Vec2 vt = vts[vert[1]];
            final Vec3 vn = vns[vert[2]];
            target.normal(vn.x, vn.y, vn.z);
            target.vertex(v.x, v.y, v.z, vt.x, vt.y);
         }
      }
      target.endShape(PConstants.CLOSE);
      return target;
   }

   /**
    * Converts a Vec2 to a PVector.
    *
    * @param source the source vector
    * @param target the target vector
    *
    * @return the vector
    */
   public static PVector toPVector ( final Vec2 source, final PVector target ) {

      return target.set(source.x, source.y, 0.0f);
   }

   /**
    * Converts a Vec3 to a PVector.
    *
    * @param source the source vector
    * @param target the target vector
    *
    * @return the vector
    */
   public static PVector toPVector ( final Vec3 source, final PVector target ) {

      return target.set(source.x, source.y, source.z);
   }

   /**
    * Converts from a PVector to a Vec3.
    *
    * @param source the source vector
    * @param target the target vector
    *
    * @return the vector
    */
   public static Vec3 toVec3 ( final PVector source, final Vec3 target ) {

      return target.set(source.x, source.y, source.z);
   }

   /**
    * Converts from a PVector to a Vec4. The w component is assumed to be
    * zero.
    *
    * @param source the source vector
    * @param target the target vector
    *
    * @return the vector
    */
   public static Vec4 toVec4 ( final PVector source, final Vec4 target ) {

      return target.set(source.x, source.y, source.z, 0.0f);
   }

   /**
    * Converts from a 2D PShape to a Curve2. Potentially a recursive function
    * if the PShape is of the family {@link PConstants#GROUP}
    * ({@value PConstants#GROUP}).<br>
    * <br>
    * Conversion of {@link PShape#PRIMITIVE}s does not respond to
    * {@link PApplet#ellipseMode(int)} or {@link PApplet#rectMode(int)}.
    *
    * @param source the source shape
    * @param curves the curves list
    *
    * @return the curves list
    */
   @Recursive
   protected static ArrayList < Curve2 > toCurve2 ( final PShape source,
      final ArrayList < Curve2 > curves ) {

      if ( source.is3D() ) { return curves; }
      final String sourceName = source.getName();
      final int family = source.getFamily();

      switch ( family ) {

         case PConstants.GROUP: /* 0 */

            final PShape[] children = source.getChildren();
            final int childLen = children.length;
            for ( int i = 0; i < childLen; ++i ) {
               Convert.toCurve2(children[i], curves);
            }

            break;

         case PShape.PRIMITIVE: /* 101 */

            final float[] params = source.getParams();
            final int paramsLen = params.length;
            final int kind = source.getKind();

            switch ( kind ) {

               case PConstants.LINE: /* 4 */

                  curves.add(Curve2.line(new Vec2(params[0], params[1]),
                     new Vec2(params[2], params[3]), new Curve2(sourceName)));
                  break;

               case PConstants.TRIANGLE: /* 8 */

                  curves.add(Curve2.straightenHandles(new Curve2(sourceName,
                     true, new Knot2(params[0], params[1]), new Knot2(params[2],
                        params[3]), new Knot2(params[4], params[5]))));
                  break;

               case PConstants.QUAD: /* 16 */

                  curves.add(Curve2.straightenHandles(new Curve2(sourceName,
                     true, new Knot2(params[0], params[1]), new Knot2(params[2],
                        params[3]), new Knot2(params[4], params[5]), new Knot2(
                           params[6], params[7]))));
                  break;

               case PConstants.RECT: /* 30 */

                  final Vec2 tl = new Vec2(params[0], params[1]);
                  final Vec2 br = new Vec2(params[2], params[3]);
                  if ( paramsLen > 7 ) {
                     curves.add(Curve2.rect(tl, br, params[4], params[5],
                        params[6], params[7], new Curve2(sourceName)));
                  } else if ( paramsLen > 4 ) {
                     curves.add(Curve2.rect(tl, br, params[4], new Curve2(
                        sourceName)));
                  } else {
                     curves.add(Curve2.rect(tl, br, new Curve2(sourceName)));
                  }

                  break;

               case PConstants.ELLIPSE: /* 31 */

                  final float xEllipse = params[2];
                  final float yEllipse = params[3];
                  final float major = Utils.max(xEllipse, yEllipse);
                  final Curve2 ellipse = new Curve2(sourceName);
                  Curve2.ellipse(Utils.div(Utils.min(xEllipse, yEllipse),
                     major), ellipse);
                  ellipse.scale(major);
                  ellipse.translate(new Vec2(params[0] + 0.5f * xEllipse,
                     params[1] + 0.5f * yEllipse));
                  curves.add(ellipse);

                  break;

               case PConstants.ARC: /* 32 */

                  final int arcMode = paramsLen > 6 ? ( int ) params[6]
                     : PConstants.OPEN;
                  final Curve2 arc = new Curve2(sourceName);
                  Curve2.arc(params[4], params[5], Utils.min(params[2],
                     params[3]), arcMode == PConstants.PIE ? ArcMode.PIE
                        : arcMode == PConstants.CHORD ? ArcMode.CHORD
                        : ArcMode.OPEN, arc);
                  arc.translate(new Vec2(params[0], params[1]));
                  curves.add(arc);
                  break;

               default:

                  System.err.println(kind + " is an unsupported kind.");
            }

            break;

         case PShape.GEOMETRY: /* 103 */
         case PShape.PATH: /* 102 */

            /* Get vertex data. */
            // final boolean isogl =
            // source.getClass().equals(PShapeOpenGL.class);
            final int vertLen = source.getVertexCount();
            if ( vertLen < 2 ) { break; }

            /*
             * Get command history. If it is null or empty, create a new default
             * array consisting of vertex commands.
             */
            int[] cmds = source.getVertexCodes();
            if ( cmds == null || cmds.length < 1 ) {
               cmds = new int[vertLen];
               for ( int i = 0; i < vertLen; ++i ) {
                  cmds[i] = PConstants.VERTEX;
               }
            }

            final boolean srcClosed = source.isClosed();
            int cursor = 0;
            boolean initialVertex = true;
            boolean spendContour = false;
            Curve2 currCurve = null;
            Knot2 prevKnot = null;
            Knot2 currKnot = null;

            /* Iterate over commands. */
            final int cmdLen = cmds.length;
            for ( int i = 0; i < cmdLen; ++i ) {
               final int cmd = cmds[i];

               /* @formatter:off */
               switch ( cmd ) {

                  case PConstants.VERTEX: /* 0 */

                     if ( initialVertex ) {

                        /* Treat as "moveTo" command. */
                        currCurve = new Curve2(sourceName);
                        currCurve.closedLoop = spendContour || srcClosed;
                        currKnot = new Knot2(
                           source.getVertexX(cursor),
                           source.getVertexY(cursor++));
                        initialVertex = false;
                        spendContour = false;
                        currCurve.append(currKnot);
                        prevKnot = currKnot;

                     } else if ( cursor < vertLen ) {

                        /*
                         * Treat as "lineSegTo" command. In PShapeOpenGLs
                         * loaded from SVGs, it's possible for the cursor
                         * to exceed the vertex length.
                         */
                        currKnot = new Knot2();
                        Knot2.fromSegLinear(
                           source.getVertexX(cursor),
                           source.getVertexY(cursor++),
                           prevKnot, currKnot);
                        currCurve.append(currKnot);
                        prevKnot = currKnot;

                     }

                     break;

                  case PConstants.BEZIER_VERTEX: /* 1 */

                     currKnot = new Knot2();
                     Knot2.fromSegCubic(
                        source.getVertexX(cursor),
                        source.getVertexY(cursor++),
                        source.getVertexX(cursor),
                        source.getVertexY(cursor++),
                        source.getVertexX(cursor),
                        source.getVertexY(cursor++),
                        prevKnot, currKnot);
                     currCurve.append(currKnot);
                     prevKnot = currKnot;

                     break;

                  case PConstants.QUADRATIC_VERTEX: /* 2 */

                     currKnot = new Knot2();
                     Knot2.fromSegQuadratic(
                        source.getVertexX(cursor),
                        source.getVertexY(cursor++),
                        source.getVertexX(cursor),
                        source.getVertexY(cursor++),
                        prevKnot, currKnot);
                     currCurve.append(currKnot);
                     prevKnot = currKnot;

                     break;

                  case PConstants.CURVE_VERTEX: /* 3 */

                     /*
                      * PShape doesn't seem to support this...?
                      * https://github.com/processing/processing/issues/5173
                      */
                     final int pi = Math.max(0, cursor - 1);
                     final int ci = Math.min(cursor + 1, vertLen - 1);
                     final int ni = Math.min(cursor + 2, vertLen - 1);

                     currKnot = new Knot2();
                     Knot2.fromSegCatmull(
                        source.getVertexX(pi),
                        source.getVertexY(pi),
                        source.getVertexX(cursor),
                        source.getVertexY(cursor),
                        source.getVertexX(ci),
                        source.getVertexY(ci),
                        source.getVertexX(ni),
                        source.getVertexY(ni),
                        0.0f, prevKnot, currKnot);
                     ++cursor;

                     currCurve.append(currKnot);
                     prevKnot = currKnot;

                     break;

                  case PConstants.BREAK: /* 4 */

                     /*
                      * It's possible with PShapeOpenGLs loaded from SVGs for
                      * break to be the initial command.
                      */
                     if ( currCurve != null ) {
                        currCurve.closedLoop = true;
                        final Knot2 first = currCurve.getFirst();
                        final Knot2 last = currCurve.getLast();
                        Vec2.mix(first.coord, last.coord,
                           IUtils.ONE_THIRD, first.rearHandle);
                        Vec2.mix(last.coord, first.coord,
                           IUtils.ONE_THIRD, last.foreHandle);
                        curves.add(currCurve);
                        initialVertex = true;
                        spendContour = true;
                     }

                     break;

                  default:

                     System.err.println(cmd + " is an unsupported command.");
               }
               /* @formatter:on */
            }

            /* Deal with closed or open loop. */
            if ( currCurve.closedLoop ) {
               currKnot = currCurve.getFirst();
               Vec2.mix(currKnot.coord, prevKnot.coord, IUtils.ONE_THIRD,
                  currKnot.rearHandle);
               Vec2.mix(prevKnot.coord, currKnot.coord, IUtils.ONE_THIRD,
                  prevKnot.foreHandle);
            } else {
               currCurve.getFirst().mirrorHandlesForward();
               currCurve.getLast().mirrorHandlesBackward();
            }

            curves.add(currCurve);

            break;

         default:

            System.err.println(family + " is an unsupported family.");
      }

      return curves;
   }

   @Recursive
   protected static ArrayList < Mesh3 > toMesh3 ( final PShape source,
      final ArrayList < Mesh3 > meshes ) {

      // TODO: Draft.

      if ( !source.is3D() ) { return meshes; }

      final String sourceName = source.getName();
      final int family = source.getFamily();

      switch ( family ) {

         case PConstants.GROUP: /* 0 */

            final PShape[] children = source.getChildren();
            final int childLen = children.length;
            for ( int i = 0; i < childLen; ++i ) {
               Convert.toMesh3(children[i], meshes);
            }

            break;

         case PShape.PRIMITIVE: /* 101 */

            break;

         case PShape.PATH: /* 102 */
         case PShape.GEOMETRY: /* 103 */

            /* Get vertex data. */
            // final boolean isogl =
            // source.getClass().equals(PShapeOpenGL.class);

            final int vertLen = source.getVertexCount();
            if ( vertLen < 1 ) { break; }

            final Mesh3 mesh = new Mesh3();
            mesh.name = sourceName;
            mesh.coords = Vec3.resize(mesh.coords, vertLen);
            mesh.texCoords = Vec2.resize(mesh.texCoords, vertLen);
            mesh.normals = Vec3.resize(mesh.normals, vertLen);

            for ( int i = 0; i < vertLen; ++i ) {
               mesh.coords[i].set(source.getVertexX(i), source.getVertexY(i),
                  source.getVertexZ(i));
               mesh.texCoords[i].set(source.getTextureU(i), source.getTextureV(
                  i));
               mesh.normals[i].set(source.getNormalX(i), source.getNormalY(i),
                  source.getNormalZ(i));
            }

            // How would you get the face data?

            meshes.add(mesh);

            break;

         default:

            System.err.println(family + " is an unsupported family.");
      }

      return meshes;
   }

}

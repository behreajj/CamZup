package camzup.pfriendly;

import java.util.Iterator;

import camzup.core.Curve2;
import camzup.core.Curve3;
import camzup.core.CurveEntity2;
import camzup.core.CurveEntity3;
import camzup.core.Experimental;
import camzup.core.Img;
import camzup.core.Knot2;
import camzup.core.Knot3;
import camzup.core.Mat3;
import camzup.core.Mat4;
import camzup.core.Mesh2;
import camzup.core.Mesh3;
import camzup.core.MeshEntity2;
import camzup.core.MeshEntity3;
import camzup.core.Quaternion;
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

         case PConstants.RGB:

            /* Seems like color is in AARRGGBB format anyway? */

         case PConstants.ARGB:

            for ( int i = 0; i < pxLen; ++i ) {
               pxTrg[i] = pxSrc[i];
            }

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

      // TEST

      final PImage pimg = new PImage(img.getWidth(), img.getHeight(),
         PConstants.ARGB, 1);
      if ( parent != null ) { pimg.parent = parent; }

      final int[] pxSrc = img.getPixels();
      final int pxLen = pxSrc.length;
      final int[] pxTrg = pimg.pixels;
      pimg.loadPixels();
      for ( int i = 0; i < pxLen; ++i ) {
         pxTrg[i] = pxSrc[i];
      }
      pimg.updatePixels();

      return pimg;
   }

   /**
    * Converts a 3 x 3 matrix to a PMatrix2D.
    *
    * @param source the source matrix
    *
    * @return the PMatrix2D
    */
   public static PMatrix2D toPMatrix2D ( final Mat3 source ) {

      return Convert.toPMatrix2D(source, ( PMatrix2D ) null);
   }

   /**
    * Converts a 3 x 3 matrix to a PMatrix2D.
    *
    * @param source the source matrix
    * @param target the target matrix
    *
    * @return the PMatrix2D
    */
   public static PMatrix2D toPMatrix2D ( final Mat3 source, PMatrix2D target ) {

      if ( target == null ) { target = new PMatrix2D(); }

      target.set(source.m00, source.m01, source.m02, source.m01, source.m11,
         source.m12);
      return target;
   }

   /**
    * Converts a 2D transform to a PMatrix2D.
    *
    * @param tr2   the transform
    * @param order the transform order
    *
    * @return the matrix
    */
   public static PMatrix2D toPMatrix2D ( final Transform2 tr2,
      final TransformOrder order ) {

      return Convert.toPMatrix2D(tr2, order, ( PMatrix2D ) null);
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
      final TransformOrder order, PMatrix2D target ) {

      if ( target == null ) {
         target = new PMatrix2D();
      } else {
         target.reset();
      }

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
    *
    * @return the matrix
    */
   public static PMatrix3D toPMatrix3D ( final Quaternion source ) {

      return Convert.toPMatrix3D(source, ( PMatrix3D ) null);
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
    * @param tr3   the transform
    * @param order the transform order
    *
    * @return the transform
    */
   public static PMatrix3D toPMatrix3D ( final Transform3 tr3,
      final TransformOrder order ) {

      return Convert.toPMatrix3D(tr3, order, ( PMatrix3D ) null);
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
      final TransformOrder order, PMatrix3D target ) {

      if ( target == null ) {
         target = new PMatrix3D();
      } else {
         target.reset();
      }

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
      shape.applyMatrix(m.m00, m.m01, m.m02, m.m03, m.m10, m.m11, m.m12, m.m13,
         m.m20, m.m21, m.m22, m.m23, m.m30, m.m31, m.m32, m.m33);

      /* Stroke weight is scaled with the transform above. */
      final float maxDim = Transform3.maxDimension(srctr);
      shape.setStrokeWeight(Utils.div(rndr.strokeWeight, maxDim));
      shape.disableStyle();
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
   public static PShapeOpenGL toPShape ( final PGraphicsOpenGL rndr,
      final Mesh2 source ) {

      /* Decompose source mesh elements. */
      final Vec2[] vs = source.coords;
      final Vec2[] vts = source.texCoords;
      final int[][][] faces = source.faces;

      /* Create output target. */
      final boolean dim = rndr.is3D();
      final PShapeOpenGL target = new PShapeOpenGL(rndr, PConstants.GROUP);
      target.setName(source.name);
      target.set3D(dim);

      final int facesLen = faces.length;
      for ( int i = 0; i < facesLen; ++i ) {
         final int[][] verts = faces[i];
         final int vertsLen = verts.length;

         final PShapeOpenGL face = new PShapeOpenGL(rndr, PShape.GEOMETRY);
         face.setName("face." + Utils.toPadded(i, 3));
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
   public static PShapeOpenGL toPShape ( final PGraphicsOpenGL rndr,
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
      target.set3D(dim);

      final int facesLen = faces.length;
      for ( int i = 0; i < facesLen; ++i ) {
         final int[][] verts = faces[i];
         final int vertsLen = verts.length;

         final PShapeOpenGL face = new PShapeOpenGL(rndr, PShape.GEOMETRY);
         face.setName("face." + Utils.toPadded(i, 3));
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
         shape.addChild(Convert.toPShape(rndr, itr.next()));
      }

      /* Use loose float version of apply matrix to avoid PShape bug. */
      final Transform2 srctr = source.transform;
      final PMatrix2D m = Convert.toPMatrix2D(srctr, TransformOrder.RST,
         new PMatrix2D());
      shape.applyMatrix(m.m00, m.m01, m.m02, m.m10, m.m11, m.m12);

      /* Stroke weight is scaled with the transform above. */
      final float maxDim = Transform2.maxDimension(srctr);
      shape.setStrokeWeight(Utils.div(rndr.strokeWeight, maxDim));
      shape.disableStyle();
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
    * Converts a Vec2 to a PVector.
    *
    * @param source the source vector
    *
    * @return the vector
    */
   public static PVector toPVector ( final Vec2 source ) {

      return Convert.toPVector(source, ( PVector ) null);
   }

   /**
    * Converts a Vec2 to a PVector.
    *
    * @param source the source vector
    * @param target the target vector
    *
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
    *
    * @return the vector
    */
   public static PVector toPVector ( final Vec3 source ) {

      return Convert.toPVector(source, ( PVector ) null);
   }

   /**
    * Converts a Vec3 to a PVector.
    *
    * @param source the source vector
    * @param target the target vector
    *
    * @return the vector
    */
   public static PVector toPVector ( final Vec3 source, PVector target ) {

      if ( target == null ) { target = new PVector(); }
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

}

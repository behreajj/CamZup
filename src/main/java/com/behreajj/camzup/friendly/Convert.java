package com.behreajj.camzup.friendly;

import com.behreajj.camzup.core.*;
import processing.core.*;
import processing.opengl.PGraphicsOpenGL;
import processing.opengl.PShapeOpenGL;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Facilitates conversions between core objects and Processing objects.
 */
public abstract class Convert {

    /**
     * Default transform order when converting a transform to a matrix.
     */
    public static final TransformOrder DEFAULT_ORDER = TransformOrder.SRT;

    /**
     * Discourage overriding with a private constructor.
     */
    private Convert() {
    }

    /**
     * Converts a 2D PShape to a curve entity. Support for this conversion is
     * <em>very</em> limited; its primary target is PShapes created from
     * importing an SVG. For best results, use a PShape consisting of quadratic
     * and cubic Bézier curves.
     *
     * @param source the source shape
     * @param target the target curve entity
     * @return the curve entity
     * @see CurveEntity2#reset()
     * @see CurveEntity2#appendAll(java.util.Collection)
     * @see Convert#toCurve2(PShape, ArrayList)
     */
    public static CurveEntity2 toCurveEntity2(final PShape source,
        final CurveEntity2 target) {

        target.reset();
        target.appendAll(Convert.toCurve2(source, new ArrayList<>()));
        target.name = Convert.getPShapeName(source);
        return target;
    }

    /**
     * Converts a PImage to an Img.
     *
     * @param source the input image
     * @param target the output image
     * @return the lab image
     */
    public static Img toImg(final PImage source, final Img target) {

        return Convert.toImg(source, false, target);
    }

    /**
     * Converts a PImage to an Img.
     *
     * @param source      the input image
     * @param useUnpremul divide color channels by alpha
     * @param target      the output image
     * @return the lab image
     */
    public static Img toImg(
        final PImage source,
        final boolean useUnpremul,
        final Img target) {

        if (source == null) {
            System.err.println("Source image is null.");
            return Img.clear(target);
        }

        if (source.pixelWidth < 1 || source.pixelHeight < 1) {
            System.err.println("Source dimensions are invalid.");
            return Img.clear(target);
        }

        source.loadPixels();
        return Img.fromArgb32(
            source.pixelWidth,
            source.pixelHeight,
            source.pixels,
            useUnpremul,
            target);
    }

    /**
     * Converts a PMatrix2D to a 3 x 3 matrix.
     *
     * @param source the source matrix
     * @param target the target matrix
     * @return the matrix
     */
    public static Mat3 toMat3(final PMatrix2D source, final Mat3 target) {

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
    public static Mat4 toMat4(final PMatrix2D source, final Mat4 target) {

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
    public static Mat4 toMat4(final PMatrix3D source, final Mat4 target) {

        return target.set(
            source.m00, source.m01, source.m02, source.m03,
            source.m10, source.m11, source.m12, source.m13,
            source.m20, source.m21, source.m22, source.m23,
            source.m30, source.m31, source.m32, source.m33);
    }

    /**
     * Converts a PShape to a mesh entity. Support for this conversion is
     * <em>very</em> limited.
     *
     * @param source the source shape
     * @param target the target curve entity
     * @return the mesh entity
     * @see MeshEntity3#reset()
     * @see MeshEntity3#appendAll(java.util.Collection)
     * @see Convert#toMesh3(PShape, ArrayList)
     */
    public static MeshEntity3 toMeshEntity3(final PShape source,
        final MeshEntity3 target) {

        target.reset();
        target.appendAll(Convert.toMesh3(source, new ArrayList<>()));
        target.name = Convert.getPShapeName(source);
        return target;
    }

    /**
     * Converts a {@link Img} to a PImage.
     *
     * @param source the input image
     * @param target the output image
     * @return the PImage
     */
    public static PImage toPImage(final Img source, final PImage target) {

        return Convert.toPImage(source, new Rgb.ToneMapClamp(), false, target);
    }

    /**
     * Converts a {@link Img} to a PImage.
     *
     * @param source    the source image
     * @param toneMap   the tone map
     * @param usePremul multiply color by alpha
     * @param target    the target image
     * @return the PImage
     */
    public static PImage toPImage(
        final Img source,
        final Rgb.AbstrToneMap toneMap,
        final boolean usePremul,
        final PImage target) {

        final int wSrc = source.getWidth();
        final int hSrc = source.getHeight();

        if (target instanceof PGraphics
            && (wSrc != target.pixelWidth
            || hSrc != target.pixelHeight)) {
            System.err.println("Do not use PGraphics with this method.");
            return target;
        }

        target.loadPixels();
        target.pixels = Img.toArgb32(source, toneMap, usePremul);
        target.format = PConstants.ARGB;
        target.pixelWidth = wSrc;
        target.pixelHeight = hSrc;
        target.width = wSrc / target.pixelDensity;
        target.height = hSrc / target.pixelDensity;
        target.updatePixels();

        return target;
    }

    /**
     * Converts a 3 x 3 matrix to a PMatrix2D.
     *
     * @param source the source matrix
     * @param target the target matrix
     * @return the PMatrix2D
     */
    public static PMatrix2D toPMatrix2D(final Mat3 source,
        final PMatrix2D target) {

        target.set(
            source.m00, source.m01, source.m02,
            source.m01, source.m11, source.m12);
        return target;
    }

    /**
     * Converts a 2D transform to a PMatrix2D.
     *
     * @param tr2    the transform
     * @param order  the transform order
     * @param target the output matrix
     * @return the matrix
     */
    public static PMatrix2D toPMatrix2D(final Transform2 tr2,
        final TransformOrder order, final PMatrix2D target) {

        target.reset();

        final Vec2 dim = tr2.getScale(new Vec2());
        final Vec2 loc = tr2.getLocation(new Vec2());
        final float angle = tr2.getRotation();

        switch (order) {

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
     * @return the matrix
     */
    public static PMatrix3D toPMatrix3D(final Quaternion source,
        final PMatrix3D target) {

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
     * @param tr3    the transform
     * @param order  the transform order
     * @param target the output PMatrix3D
     * @return the transform
     * @see PMatAux#rotate(Quaternion, PMatrix3D)
     */
    public static PMatrix3D toPMatrix3D(final Transform3 tr3,
        final TransformOrder order, final PMatrix3D target) {

        target.reset();

        final Quaternion q = tr3.getRotation(new Quaternion());
        final Vec3 dim = tr3.getScale(new Vec3());
        final Vec3 loc = tr3.getLocation(new Vec3());

        switch (order) {

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
     * @return the PShape
     */
    public static PShape toPShape(final PGraphics rndr, final Curve2 source) {

        /*
         * This needs to be of the PATH family because the drawImpl function in
         * PShape is buggy. See
         * https://github.com/processing/processing/issues/4879 .
         */
        final PShape target = new PShape(rndr, PShape.PATH);
        target.setName(source.name);
        target.set3D(rndr.is3D());

        final Iterator<Knot2> itr = source.iteratorReverse();

        final Knot2 firstKnot = itr.next();
        Knot2 prevKnot = firstKnot;
        Knot2 currKnot;

        Vec2 coord = prevKnot.coord;
        Vec2 foreHandle;
        Vec2 rearHandle;

        target.beginShape(PConstants.POLYGON);
        target.vertex(coord.x, coord.y);

        while (itr.hasNext()) {
            currKnot = itr.next();
            foreHandle = prevKnot.rearHandle;
            rearHandle = currKnot.foreHandle;
            coord = currKnot.coord;
            target.bezierVertex(
                foreHandle.x, foreHandle.y,
                rearHandle.x, rearHandle.y,
                coord.x, coord.y);
            prevKnot = currKnot;
        }

        if (source.closedLoop) {
            foreHandle = prevKnot.rearHandle;
            rearHandle = firstKnot.foreHandle;
            coord = firstKnot.coord;

            target.bezierVertex(
                foreHandle.x, foreHandle.y,
                rearHandle.x, rearHandle.y,
                coord.x, coord.y);
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
     * @return the PShape
     * @see Convert#toPShape(PGraphics, Curve2)
     * @see Convert#toPMatrix2D(Transform2, TransformOrder, PMatrix2D)
     */
    public static PShape toPShape(final PGraphics rndr,
        final CurveEntity2 source) {

        final PShape parent = new PShape(rndr, PConstants.GROUP);
        parent.setName(source.name);
        parent.set3D(rndr.is3D());

        for (Curve2 knot2s : source.curves) {
            // TODO: To make this preserve contours like the mesh version, you'd
            // have to revise here because contours must be written within
            // shapes and there would be redundancy with the Curve2 toPShape
            // method.
            final PShape child = Convert.toPShape(rndr, knot2s);
            parent.addChild(child);
        }

        /* Use loose float version of apply matrix to avoid PShape bug. */
        final Transform2 srctr = source.transform;
        final PMatrix2D m = Convert.toPMatrix2D(srctr, Convert.DEFAULT_ORDER,
            new PMatrix2D());
        parent.resetMatrix();
        parent.applyMatrix(
            m.m00, m.m01, m.m02,
            m.m10, m.m11, m.m12);

        /* Stroke weight is scaled with the transform above. */
        final float maxDim = Transform2.maxDimension(srctr);
        parent.setStrokeWeight(Utils.div(rndr.strokeWeight, maxDim));
        return parent;
    }

    /**
     * Converts a 2D mesh to a PShape. Returns a {@link PConstants#GROUP} which
     * contains, as a child, each face of the source mesh. Each child is of the
     * type {@link PShape#PATH}. Child shapes record only 2D coordinates, not
     * texture coordinates. Faces with clockwise winding that follow after
     * those with counterclockwise winding will be drawn as contours.
     *
     * @param rndr   the renderer
     * @param source the source mesh
     * @return the PShape
     */
    public static PShape toPShape(final PGraphics rndr, final Mesh2 source) {

        /* Decompose source mesh elements. */
        final Vec2[] vs = source.coords;
        final int[][][] faces = source.faces;

        /* Create output target. */
        final boolean is3d = rndr.is3D();
        final PShape parent = new PShape(rndr, PConstants.GROUP);
        parent.setName(source.name);
        parent.set3D(is3d);

        final int facesLen = faces.length;
        PShape child = null;
        boolean drawingConvex = false;

        for (int i = 0; i < facesLen; ++i) {
            final int[][] verts = faces[i];
            final int vertsLen = verts.length;

            /*
             * Find whether vertex winding is clockwise or counter-clockwise to
             * determine if the PShape should be a contour or not.
             * https://en.wikipedia.org/wiki/Curve_orientation
             * https://stackoverflow.com/questions/1165647/how-to-determine-if-a-
             * list-of-polygon-points-are-in-clockwise-order/
             */
            float winding = 0.0f;
            for (int j = 0; j < vertsLen; ++j) {
                final Vec2 v0 = vs[verts[j][0]];
                final Vec2 v1 = vs[verts[(j + 1) % vertsLen][0]];
                winding += (v1.x - v0.x) * (v1.y + v0.y);
            }

            final boolean faceIsCcw = winding < -0.0f;
            final boolean faceIsCw = winding > 0.0f;

            if (faceIsCcw) {
                if (drawingConvex) {
                    /* Close the previous child and add it to the parent. */
                    child.endShape(PConstants.CLOSE);
                    parent.addChild(child);
                }

                child = new PShape(rndr, PShape.PATH);
                child.setName("face." + Utils.toPadded(i, 3));
                child.set3D(is3d);

                drawingConvex = true;
                child.beginShape(PConstants.POLYGON);
                for (int j = vertsLen - 1; j >= 0; --j) {
                    final Vec2 v = vs[verts[j][0]];
                    child.vertex(v.x, v.y);
                }
            } else if (faceIsCw) {
                if (drawingConvex) {
                    child.beginContour();
                    for (int j = vertsLen - 1; j >= 0; --j) {
                        final Vec2 v = vs[verts[j][0]];
                        child.vertex(v.x, v.y);
                    }
                    child.endContour();
                } else {
                    /* Degenerate case, where a contour precedes a convex shape. */
                    child = new PShape(rndr, PShape.PATH);
                    child.setName("face." + Utils.toPadded(i, 3));
                    child.set3D(is3d);
                    child.beginShape(PConstants.POLYGON);
                    for (int[] vert : verts) {
                        final Vec2 v = vs[vert[0]];
                        child.vertex(v.x, v.y);
                    }
                    child.endShape(PConstants.CLOSE);
                    parent.addChild(child);
                }
            }
        }

        if (drawingConvex) {
            child.endShape(PConstants.CLOSE);
            parent.addChild(child);
        }

        return parent;
    }

    /**
     * Converts a 2D mesh entity to a PShape. The entity's transform is
     * converted to a matrix which is applied to the shape.
     *
     * @param rndr   the renderer
     * @param source the source entity
     * @return the PShape
     */
    public static PShape toPShape(final PGraphics rndr,
        final MeshEntity2 source) {

        final PShape parent = new PShape(rndr, PConstants.GROUP);
        parent.setName(source.name);
        parent.set3D(rndr.is3D());

        for (Mesh2 face2s : source.meshes) {
            final PShape child = Convert.toPShape(rndr, face2s);
            parent.addChild(child);
        }

        // TODO: DEFAULT_ORDER used to be RST, but now it is SRT. This function,
        // and its variants, would have to be re-tested for parity between forward
        // and reverse transformation of PShape and Mesh.

        /* Use loose float version of apply matrix to avoid PShape bug. */
        final Transform2 srctr = source.transform;
        final PMatrix2D m = Convert.toPMatrix2D(srctr, Convert.DEFAULT_ORDER,
            new PMatrix2D());
        parent.resetMatrix();
        parent.applyMatrix(
            m.m00, m.m01, m.m02,
            m.m10, m.m11, m.m12);

        /* Stroke weight is scaled with the transform above. */
        final float maxDim = Transform2.maxDimension(srctr);
        parent.setStrokeWeight(Utils.div(rndr.strokeWeight, maxDim));
        return parent;
    }

    /**
     * Converts a 2D curve to a PShape. Returns {@link PShape#GEOMETRY}.
     *
     * @param rndr   the renderer
     * @param source the source mesh
     * @return the PShape
     */
    public static PShapeOpenGL toPShape(final PGraphicsOpenGL rndr,
        final Curve2 source) {

        final PShapeOpenGL target = new PShapeOpenGL(rndr, PShape.GEOMETRY);
        target.setName(source.name);
        target.set3D(rndr.is3D());

        final Iterator<Knot2> itr = source.iteratorReverse();

        final Knot2 firstKnot = itr.next();
        Knot2 prevKnot = firstKnot;
        Knot2 currKnot;

        Vec2 coord = prevKnot.coord;
        Vec2 foreHandle;
        Vec2 rearHandle;

        target.beginShape(PConstants.POLYGON);
        target.vertex(coord.x, coord.y);

        while (itr.hasNext()) {
            currKnot = itr.next();
            foreHandle = prevKnot.rearHandle;
            rearHandle = currKnot.foreHandle;
            coord = currKnot.coord;
            target.bezierVertex(
                foreHandle.x, foreHandle.y,
                rearHandle.x, rearHandle.y,
                coord.x, coord.y);
            prevKnot = currKnot;
        }

        if (source.closedLoop) {
            foreHandle = prevKnot.rearHandle;
            rearHandle = firstKnot.foreHandle;
            coord = firstKnot.coord;

            target.bezierVertex(
                foreHandle.x, foreHandle.y,
                rearHandle.x, rearHandle.y,
                coord.x, coord.y);
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
     * @return the PShape
     */
    public static PShapeOpenGL toPShape(final PGraphicsOpenGL rndr,
        final Curve3 source) {

        final PShapeOpenGL target = new PShapeOpenGL(rndr, PShape.GEOMETRY);
        target.setName(source.name);
        target.set3D(rndr.is3D());

        final Iterator<Knot3> itr = source.iteratorReverse();

        final Knot3 firstKnot = itr.next();
        Knot3 prevKnot = firstKnot;
        Knot3 currKnot;

        Vec3 coord = prevKnot.coord;
        Vec3 foreHandle;
        Vec3 rearHandle;

        target.beginShape(PConstants.POLYGON);
        target.vertex(coord.x, coord.y, coord.z);

        while (itr.hasNext()) {
            currKnot = itr.next();
            foreHandle = prevKnot.rearHandle;
            rearHandle = currKnot.foreHandle;
            coord = currKnot.coord;
            target.bezierVertex(
                foreHandle.x, foreHandle.y, foreHandle.z,
                rearHandle.x, rearHandle.y, rearHandle.z,
                coord.x, coord.y, coord.z);
            prevKnot = currKnot;
        }

        if (source.closedLoop) {
            foreHandle = prevKnot.rearHandle;
            rearHandle = firstKnot.foreHandle;
            coord = firstKnot.coord;

            target.bezierVertex(
                foreHandle.x, foreHandle.y, foreHandle.z,
                rearHandle.x, rearHandle.y, rearHandle.z,
                coord.x, coord.y, coord.z);
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
     * @return the PShape
     */
    public static PShapeOpenGL toPShape(final PGraphicsOpenGL rndr,
        final CurveEntity2 source) {

        final PShapeOpenGL parent = new PShapeOpenGL(rndr, PConstants.GROUP);
        parent.setName(source.name);
        parent.set3D(rndr.is3D());

        for (Curve2 knot2s : source.curves) {
            final PShapeOpenGL child = Convert.toPShape(rndr, knot2s);
            parent.addChild(child);
        }

        /* Use loose float version of apply matrix to avoid PShape bug. */
        final Transform2 srctr = source.transform;
        final PMatrix2D m = Convert.toPMatrix2D(srctr, Convert.DEFAULT_ORDER,
            new PMatrix2D());
        parent.resetMatrix();
        parent.applyMatrix(
            m.m00, m.m01, m.m02,
            m.m10, m.m11, m.m12);

        /* Stroke weight is scaled with the transform above. */
        final float maxDim = Transform2.maxDimension(srctr);
        parent.setStrokeWeight(Utils.div(rndr.strokeWeight, maxDim));
        return parent;
    }

    /**
     * Converts a 3D curve entity to a PShapeOpenGL. Requires a PGraphicsOpenGL
     * renderer. The entity's transform is converted to a matrix which is
     * applied to the shape.
     *
     * @param rndr   the renderer
     * @param source the source entity
     * @return the PShape
     */
    public static PShapeOpenGL toPShape(final PGraphicsOpenGL rndr,
        final CurveEntity3 source) {

        final PShapeOpenGL parent = new PShapeOpenGL(rndr, PConstants.GROUP);
        parent.set3D(rndr.is3D());

        for (Curve3 knot3s : source.curves) {
            final PShapeOpenGL child = Convert.toPShape(rndr, knot3s);
            parent.addChild(child);
        }

        /* Use loose float version of apply matrix to avoid PShape bug. */
        final Transform3 srctr = source.transform;
        final PMatrix3D m = Convert.toPMatrix3D(srctr, Convert.DEFAULT_ORDER,
            new PMatrix3D());
        parent.resetMatrix();
        parent.applyMatrix(
            m.m00, m.m01, m.m02, m.m03,
            m.m10, m.m11, m.m12, m.m13,
            m.m20, m.m21, m.m22, m.m23,
            m.m30, m.m31, m.m32, m.m33);

        /* Stroke weight is scaled with the transform above. */
        final float maxDim = Transform3.maxDimension(srctr);
        parent.setStrokeWeight(Utils.div(rndr.strokeWeight, maxDim));
        return parent;
    }

    /**
     * Converts a 2D mesh to a PShape. Audits the mesh to see if all of its
     * faces have the same number of vertices. Meshes composed entirely of
     * triangles or quadrilaterals create much more efficient PShapes than
     * those with n-sided polygons.
     *
     * @param rndr   the renderer
     * @param source the source mesh
     * @return the PShape
     * @see Mesh#auditFaceType(Mesh)
     */
    public static PShapeOpenGL toPShape(final PGraphicsOpenGL rndr,
        final Mesh2 source) {

        /*
         * audit.get could return null, but auditFaceType will always put 3 and 4
         * into the map. You could also use getOrDefault .
         */
        final Map<Integer, Integer> audit = Mesh.auditFaceType(source);
        final int facesLen = source.faces.length;
        if (facesLen == audit.get(3)) {
            return Convert.toPShapeUniform(rndr, source, PConstants.TRIANGLES);
        }
        if (facesLen == audit.get(4)) {
            return Convert.toPShapeUniform(rndr, source, PConstants.QUADS);
        }
        return Convert.toPShapeNonUniform(rndr, source);
    }

    /**
     * Converts a 3D mesh to a PShape. Audits the mesh to see if all of its
     * faces have the same number of vertices. Meshes composed entirely of
     * triangles or quadrilaterals create much more efficient PShapes than
     * those with ngons.
     *
     * @param rndr   the renderer
     * @param source the source mesh
     * @return the PShape
     * @see Mesh#auditFaceType(Mesh)
     */
    public static PShapeOpenGL toPShape(final PGraphicsOpenGL rndr,
        final Mesh3 source) {

        /*
         * audit.get could return null, but auditFaceType will always put 3 and 4
         * into the map. You could also use getOrDefault .
         */
        final Map<Integer, Integer> audit = Mesh.auditFaceType(source);
        final int facesLen = source.faces.length;
        if (facesLen == audit.get(3)) {
            return Convert.toPShapeUniform(rndr, source, PConstants.TRIANGLES);
        }
        if (facesLen == audit.get(4)) {
            return Convert.toPShapeUniform(rndr, source, PConstants.QUADS);
        }
        return Convert.toPShapeNonUniform(rndr, source);
    }

    /**
     * Converts a 2D mesh entity to a PShapeOpenGL. Requires a PGraphicsOpenGL
     * renderer. The entity's transform is converted to a matrix which is
     * applied to the shape.
     *
     * @param rndr   the renderer
     * @param source the source entity
     * @return the PShape
     */
    public static PShapeOpenGL toPShape(final PGraphicsOpenGL rndr,
        final MeshEntity2 source) {

        final PShapeOpenGL shape = new PShapeOpenGL(rndr, PConstants.GROUP);
        shape.setName(source.name);
        shape.set3D(rndr.is3D());

        for (Mesh2 face2s : source.meshes) {

            /*
             * Keep this as a separate variable to avoid confusion between the
             * general PShape and PGraphics with the specific PShapeOpenGL and
             * PGraphicsOpenGL. The signature is addChild(PShape p).
             */
            final PShapeOpenGL child = Convert.toPShape(rndr, face2s);
            shape.addChild(child);
        }

        /* Use loose float version of apply matrix to avoid PShape bug. */
        final Transform2 srctr = source.transform;
        final PMatrix2D m = Convert.toPMatrix2D(srctr, Convert.DEFAULT_ORDER,
            new PMatrix2D());
        shape.resetMatrix();
        shape.applyMatrix(
            m.m00, m.m01, m.m02,
            m.m10, m.m11, m.m12);

        /* Stroke weight is scaled with the transform above. */
        final float maxDim = Transform2.maxDimension(srctr);
        shape.setStrokeWeight(Utils.div(rndr.strokeWeight, maxDim));
        return shape;
    }

    /**
     * Converts a 3D mesh entity to a PShapeOpenGL. Requires a PGraphicsOpenGL
     * renderer. The entity's transform is converted to a matrix which is
     * applied to the shape.
     *
     * @param rndr   the renderer
     * @param source the source entity
     * @return the PShape
     */
    public static PShapeOpenGL toPShape(final PGraphicsOpenGL rndr,
        final MeshEntity3 source) {

        final PShapeOpenGL shape = new PShapeOpenGL(rndr, PConstants.GROUP);
        shape.set3D(rndr.is3D());

        for (Mesh3 face3s : source.meshes) {

            /*
             * Keep this as a separate variable to avoid confusion between the
             * general PShape and PGraphics with the specific PShapeOpenGL and
             * PGraphicsOpenGL. The signature is addChild(PShape p).
             */
            final PShapeOpenGL child = Convert.toPShape(rndr, face3s);
            shape.addChild(child);
        }

        /* Use loose float version of apply matrix to avoid PShape bug. */
        final Transform3 srctr = source.transform;
        final PMatrix3D m = Convert.toPMatrix3D(srctr, Convert.DEFAULT_ORDER,
            new PMatrix3D());
        shape.resetMatrix();
        shape.applyMatrix(
            m.m00, m.m01, m.m02, m.m03,
            m.m10, m.m11, m.m12, m.m13,
            m.m20, m.m21, m.m22, m.m23,
            m.m30, m.m31, m.m32, m.m33);

        /* Stroke weight is scaled with the transform above. */
        final float maxDim = Transform3.maxDimension(srctr);
        shape.setStrokeWeight(Utils.div(rndr.strokeWeight, maxDim));
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
     * @return the PShape
     */
    public static PShapeOpenGL toPShapeNonUniform(
        final PGraphicsOpenGL rndr,
        final Mesh2 source) {

        /* Decompose source mesh elements. */
        final Vec2[] vs = source.coords;
        final Vec2[] vts = source.texCoords;
        final int[][][] faces = source.faces;

        /* Create output target. */
        final boolean is3d = rndr.is3D();
        final PShapeOpenGL target = new PShapeOpenGL(rndr, PConstants.GROUP);
        target.setName(source.name);
        target.setTextureMode(PConstants.NORMAL);
        target.set3D(is3d);

        final int facesLen = faces.length;
        for (int i = 0; i < facesLen; ++i) {
            final int[][] verts = faces[i];
            final int vertsLen = verts.length;

            final PShapeOpenGL face = new PShapeOpenGL(rndr, PShape.GEOMETRY);
            face.setName("face." + Utils.toPadded(i, 3));
            face.setTextureMode(PConstants.NORMAL);
            face.set3D(is3d);

            face.beginShape(PConstants.POLYGON);
            for (int j = vertsLen - 1; j >= 0; --j) {
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
     * @return the PShape
     */
    public static PShapeOpenGL toPShapeNonUniform(final PGraphicsOpenGL rndr,
        final Mesh3 source) {

        /* Decompose source mesh elements. */
        final Vec3[] vs = source.coords;
        final Vec2[] vts = source.texCoords;
        final Vec3[] vns = source.normals;
        final int[][][] faces = source.faces;

        /* Create output target. */
        final boolean is3d = rndr.is3D();
        final PShapeOpenGL target = new PShapeOpenGL(rndr, PConstants.GROUP);
        target.setName(source.name);
        target.setTextureMode(PConstants.NORMAL);
        target.set3D(is3d);

        final int facesLen = faces.length;
        for (int i = 0; i < facesLen; ++i) {
            final int[][] verts = faces[i];
            final int vertsLen = verts.length;

            final PShapeOpenGL face = new PShapeOpenGL(rndr, PShape.GEOMETRY);
            face.setName("face." + Utils.toPadded(i, 3));
            face.setTextureMode(PConstants.NORMAL);
            face.set3D(is3d);

            face.beginShape(PConstants.POLYGON);
            for (int j = vertsLen - 1; j >= 0; --j) {
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
     * PShape. Acceptable face types are {@link PConstants#TRIANGLES}
     * ({@value PConstants#TRIANGLES}) and {@link PConstants#QUADS}
     * ({@value PConstants#QUADS}).
     *
     * @param rndr      the renderer
     * @param source    the source mesh
     * @param shapeCode the Processing shape code
     * @return the shape
     */
    public static PShapeOpenGL toPShapeUniform(final PGraphicsOpenGL rndr,
        final Mesh2 source, final int shapeCode) {

        /* Decompose source mesh elements. */
        final Vec2[] vs = source.coords;
        final Vec2[] vts = source.texCoords;
        final int[][][] faces = source.faces;

        /* Create output target. */
        final PShapeOpenGL target = new PShapeOpenGL(rndr, PShape.GEOMETRY);
        target.setName(source.name);
        target.setTextureMode(PConstants.NORMAL);
        target.set3D(rndr.is3D());
        target.beginShape(shapeCode);

        for (final int[][] verts : faces) {
            final int vertsLen = verts.length;
            for (int j = vertsLen - 1; j >= 0; --j) {
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
     * PShape. Acceptable face types are {@link PConstants#TRIANGLES}
     * ({@value PConstants#TRIANGLES}) and {@link PConstants#QUADS}
     * ({@value PConstants#QUADS}).
     *
     * @param rndr      the renderer
     * @param source    the source mesh
     * @param shapeCode the Processing shape code
     * @return the shape
     */
    public static PShapeOpenGL toPShapeUniform(final PGraphicsOpenGL rndr,
        final Mesh3 source, final int shapeCode) {

        /* Decompose source mesh elements. */
        final Vec3[] vs = source.coords;
        final Vec2[] vts = source.texCoords;
        final Vec3[] vns = source.normals;
        final int[][][] faces = source.faces;

        /* Create output target. */
        final PShapeOpenGL target = new PShapeOpenGL(rndr, PShape.GEOMETRY);
        target.setName(source.name);
        target.setTextureMode(PConstants.NORMAL);
        target.set3D(rndr.is3D());
        target.beginShape(shapeCode);

        for (final int[][] verts : faces) {
            final int vertsLen = verts.length;
            for (int j = vertsLen - 1; j >= 0; --j) {
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
     * @return the vector
     */
    public static PVector toPVector(final Vec2 source, final PVector target) {

        return target.set(source.x, source.y, 0.0f);
    }

    /**
     * Converts a Vec3 to a PVector.
     *
     * @param source the source vector
     * @param target the target vector
     * @return the vector
     */
    public static PVector toPVector(final Vec3 source, final PVector target) {

        return target.set(source.x, source.y, source.z);
    }

    /**
     * Converts from a PVector to a Vec3.
     *
     * @param source the source vector
     * @param target the target vector
     * @return the vector
     */
    public static Vec3 toVec3(final PVector source, final Vec3 target) {

        return target.set(source.x, source.y, source.z);
    }

    /**
     * Converts from a PVector to a Vec4. The w component is assumed to be
     * zero.
     *
     * @param source the source vector
     * @param target the target vector
     * @return the vector
     */
    public static Vec4 toVec4(final PVector source, final Vec4 target) {

        return target.set(source.x, source.y, source.z, 0.0f);
    }

    /**
     * Converts from a 2D PShape to a {@link Curve3}. Potentially a recursive
     * function if the PShape is of the family {@link PConstants#GROUP}.
     * <br>
     * <br>
     * Conversion of {@link PShape#PRIMITIVE}s does not respond to
     * {@link PApplet#ellipseMode(int)} or {@link PApplet#rectMode(int)}.
     * Rectangles should be described according to the top-left and
     * bottom-right corner.
     *
     * @param source the source shape
     * @param curves the curves list
     * @return the curves list
     */
    protected static ArrayList<Curve2> toCurve2(final PShape source,
        final ArrayList<Curve2> curves) {

        if (source.is3D()) {
            return curves;
        }

        final String sourceName = Convert.getPShapeName(source);
        final int family = source.getFamily();

        switch (family) {

            case PConstants.GROUP: { /* 0 */

                final PShape[] children = source.getChildren();
                for (PShape child : children) {
                    Convert.toCurve2(child, curves);
                }
            }
            break;

            case PShape.PRIMITIVE: { /* 101 */

                final float[] params = source.getParams();
                final int paramsLen = params.length;
                final int kind = source.getKind();

                switch (kind) {

                    case PConstants.LINE: { /* 4 */
                        curves.add(Curve2.line(
                            new Vec2(params[2], params[3]),
                            new Vec2(params[0], params[1]),
                            new Curve2(sourceName)));
                    }
                    break;

                    case PConstants.TRIANGLE: { /* 8 */
                        curves.add(Curve2.straightHandles(new Curve2(
                            sourceName, true,
                            new Knot2(params[4], params[5]),
                            new Knot2(params[2], params[3]),
                            new Knot2(params[0], params[1]))));
                    }
                    break;

                    case PConstants.QUAD: { /* 16 */
                        curves.add(Curve2.straightHandles(new Curve2(
                            sourceName, true,
                            new Knot2(params[6], params[7]),
                            new Knot2(params[4], params[5]),
                            new Knot2(params[2], params[3]),
                            new Knot2(params[0], params[1]))));
                    }
                    break;

                    case PConstants.RECT: { /* 30 */
                        final Vec2 tl = new Vec2(params[0], params[1]);
                        final Vec2 br = new Vec2(params[2], params[3]);
                        final Curve2 rect;
                        if (paramsLen > 7) {
                            /* Non-uniform rounded corners. */
                            rect = Curve2.rect(tl, br,
                                params[4], params[5], params[6], params[7],
                                new Curve2(sourceName));
                        } else if (paramsLen > 4) {
                            /* Uniform rounded corners. */
                            rect = Curve2.rect(tl, br, params[4], new Curve2(
                                sourceName));
                        } else {
                            rect = Curve2.rect(tl, br, new Curve2(sourceName));
                        }
                        curves.add(rect);
                    }
                    break;

                    case PConstants.ELLIPSE: { /* 31 */
                        final float xEllipse = params[2];
                        final float yEllipse = params[3];
                        final float major = Math.max(xEllipse, yEllipse);
                        final float minor = Math.min(xEllipse, yEllipse);
                        final Curve2 ellipse = new Curve2(sourceName);
                        Curve2.ellipse(Utils.div(minor, major), ellipse);
                        ellipse.scale(major);
                        ellipse.translate(new Vec2(
                            params[0] + 0.5f * xEllipse,
                            params[1] + 0.5f * yEllipse));
                        curves.add(ellipse);
                    }
                    break;

                    case PConstants.ARC: { /* 32 */
                        final int arcMode = paramsLen > 6
                            ? (int) params[6]
                            : PConstants.OPEN;
                        final Curve2 arc = new Curve2(sourceName);
                        Curve2.arc(
                            params[4], params[5],
                            Math.min(params[2], params[3]),
                            arcMode == PConstants.PIE
                                ? ArcMode.PIE
                                : arcMode == PConstants.CHORD
                                ? ArcMode.CHORD
                                : ArcMode.OPEN,
                            arc);
                        arc.translate(new Vec2(params[0], params[1]));
                        curves.add(arc);
                    }
                    break;

                    default:

                        System.err.println(kind + " is an unsupported kind.");
                }
            }
            break;

            case PShape.GEOMETRY: /* 103 */
            case PShape.PATH: { /* 102 */

                /* Get vertex data. */

                final int vertLen = source.getVertexCount();
                if (vertLen < 2) {
                    break;
                }

                /*
                 * Get command history. If it is null or empty, create a new
                 * default array consisting of vertex commands.
                 */
                int[] cmds = source.getVertexCodes();
                if (cmds == null || cmds.length < 1) {
                    cmds = new int[vertLen];

                    /* Technically, not necessary. VERTEX code is 0. */
                    for (int i = 0; i < vertLen; ++i) {
                        cmds[i] = PConstants.VERTEX;
                    }
                }

                final boolean srcClosed = source.isClosed();
                int cursor = 0;
                boolean initialVertex = true;
                boolean spendContour = false;
                Curve2 currCurve = null;
                Knot2 prevKnot = null;
                Knot2 currKnot;

                /* Iterate over commands. */
                for (final int cmd : cmds) {
                    switch (cmd) {

                        case PConstants.VERTEX: { /* 0 */
                            if (initialVertex) {

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

                            } else if (cursor < vertLen) {

                                /*
                                 * Treat as "lineSegTo" command. In
                                 * PShapeOpenGLs loaded from SVGs, it's
                                 * possible for the cursor to exceed vertex
                                 * length.
                                 */
                                currKnot = new Knot2();
                                Knot2.fromSegLinear(
                                    source.getVertexX(cursor), source.getVertexY(cursor++),
                                    prevKnot, currKnot);
                                currCurve.append(currKnot);
                                prevKnot = currKnot;

                            }
                        }
                        break;

                        case PConstants.BEZIER_VERTEX: { /* 1 */
                            currKnot = new Knot2();
                            if (currCurve != null) {
                                Knot2.fromSegCubic(
                                    source.getVertexX(cursor), source.getVertexY(cursor++),
                                    source.getVertexX(cursor), source.getVertexY(cursor++),
                                    source.getVertexX(cursor), source.getVertexY(cursor++),
                                    prevKnot, currKnot);
                                currCurve.append(currKnot);
                            }
                            prevKnot = currKnot;
                        }
                        break;

                        case PConstants.QUADRATIC_VERTEX: { /* 2 */
                            currKnot = new Knot2();
                            if (currCurve != null) {
                                Knot2.fromSegQuadratic(
                                    source.getVertexX(cursor), source.getVertexY(cursor++),
                                    source.getVertexX(cursor), source.getVertexY(cursor++),
                                    prevKnot, currKnot);
                                currCurve.append(currKnot);
                            }
                            prevKnot = currKnot;
                        }
                        break;

                        case PConstants.CURVE_VERTEX: { /* 3 */

                            /*
                             * PShape doesn't support this?
                             * https://github.com/processing/processing/issues/5173
                             */

                            currKnot = new Knot2();
                            if (currCurve != null) {
                                final int pi = Math.max(0, cursor - 1);
                                final int ci = Math.min(cursor + 1, vertLen - 1);
                                final int ni = Math.min(cursor + 2, vertLen - 1);

                                Knot2.fromSegCatmull(
                                    source.getVertexX(pi), source.getVertexY(pi),
                                    source.getVertexX(cursor), source.getVertexY(cursor),
                                    source.getVertexX(ci), source.getVertexY(ci),
                                    source.getVertexX(ni), source.getVertexY(ni),
                                    0.0f, prevKnot, currKnot);
                                ++cursor;
                                currCurve.append(currKnot);
                            }
                            prevKnot = currKnot;
                        }
                        break;

                        case PConstants.BREAK: { /* 4 */

                            /*
                             * It's possible with PShapeOpenGLs loaded from SVGs
                             * for break to be the initial command.
                             */

                            if (currCurve != null) {
                                currCurve.closedLoop = true;
                                final Knot2 first = currCurve.getFirst();
                                final Knot2 last = currCurve.getLast();
                                Vec2.mix(
                                    first.coord,
                                    last.coord,
                                    Utils.ONE_THIRD,
                                    first.rearHandle);
                                Vec2.mix(
                                    last.coord,
                                    first.coord,
                                    Utils.ONE_THIRD,
                                    last.foreHandle);
                                curves.add(currCurve);
                                initialVertex = true;
                                spendContour = true;
                            }
                        }
                        break;

                        default:

                            System.err.println(cmd + " is an unsupported command.");
                    }
                }

                /* Deal with closed or open loop. */
                if (currCurve != null) {
                    if (currCurve.closedLoop) {
                        currKnot = currCurve.getFirst();
                        Vec2.mix(
                            currKnot.coord,
                            prevKnot.coord,
                            Utils.ONE_THIRD,
                            currKnot.rearHandle);
                        Vec2.mix(
                            prevKnot.coord,
                            currKnot.coord,
                            Utils.ONE_THIRD,
                            prevKnot.foreHandle);
                    } else {
                        currCurve.getFirst().mirrorHandlesForward();
                        currCurve.getLast().mirrorHandlesBackward();
                    }

                    currCurve.reverse();
                    curves.add(currCurve);
                }
            }
            break;

            default:

                System.err.println(family + " is an unsupported family.");
        }

        return curves;
    }

    /**
     * Converts from a PShape to a {@link Mesh3}. Potentially a recursive
     * function if the PShape is of the family {@link PConstants#GROUP}
     * ({@value PConstants#GROUP}).
     * <br>
     * <br>
     * Conversion of {@link PShape#PRIMITIVE}s does not respond to
     * {@link PApplet#ellipseMode(int)}, {@link PApplet#rectMode(int)} or to UV
     * sphere settings.
     * <br>
     * <br>
     * For shapes in the {@link PShape#GEOMETRY} family, faces are assumed to
     * be triangles.
     *
     * @param source the source shape
     * @param meshes the mesh list
     * @return the mesh list
     */
    protected static ArrayList<Mesh3> toMesh3(final PShape source,
        final ArrayList<Mesh3> meshes) {

        final String sourceName = Convert.getPShapeName(source);
        final int family = source.getFamily();

        switch (family) {
            case PConstants.GROUP: { /* 0 */

                final PShape[] children = source.getChildren();
                for (PShape child : children) {
                    Convert.toMesh3(child, meshes);
                }
            }
            break;

            case PShape.PRIMITIVE: { /* 101 */

                final float[] params = source.getParams();
                final int paramsLen = params.length;
                final int kind = source.getKind();

                switch (kind) {

                    case PConstants.LINE: /* 4 */

                        System.err.println("Lines are not supported.");
                        break;

                    case PConstants.TRIANGLE: { /* 8 */
                        final Mesh3 m = new Mesh3(
                            sourceName,
                            new int[][][]{{{2, 2}, {1, 1}, {0, 0}}},

                            new Vec3[]{
                                new Vec3(params[0], params[1], 0.0f),
                                new Vec3(params[2], params[3], 0.0f),
                                new Vec3(params[4], params[5], 0.0f)},

                            new Vec2[]{
                                new Vec2(1.0f, 0.5f),
                                new Vec2(0.25f, 0.066987306f),
                                new Vec2(0.25f, 0.9330127f)},

                            new Vec3[]{Vec3.up(new Vec3())});
                        meshes.add(m);
                    }
                    break;

                    case PConstants.QUAD: { /* 16 */
                        final Mesh3 m = new Mesh3(
                            sourceName,
                            new int[][][]{{{3, 3}, {2, 2}, {1, 1}, {0, 0}}},

                            new Vec3[]{
                                new Vec3(params[0], params[1], 0.0f),
                                new Vec3(params[2], params[3], 0.0f),
                                new Vec3(params[4], params[5], 0.0f),
                                new Vec3(params[6], params[7], 0.0f)},

                            new Vec2[]{
                                new Vec2(1.0f, 0.5f),
                                new Vec2(0.5f, 0.0f),
                                new Vec2(0.0f, 0.5f),
                                new Vec2(0.5f, 1.0f)},

                            new Vec3[]{Vec3.up(new Vec3())});
                        meshes.add(m);
                    }
                    break;

                    case PConstants.RECT: { /* 30 */
                        if (paramsLen > 7) {
                            System.err.println("Rounded rectangles aren't supported.");
                        } else if (paramsLen > 4) {
                            System.err.println("Rounded rectangles aren't supported.");
                        } else {
                            System.err.println("Rectangles are not supported.");
                        }
                    }
                    break;

                    case PConstants.ELLIPSE: /* 31 */
                        System.err.println("Ellipses are not supported.");
                        break;

                    case PConstants.ARC: /* 32 */
                        System.err.println("Arcs are not supported.");
                        break;

                    case PConstants.SPHERE: { /* 40 */
                        final Mesh3 m = new Mesh3();
                        if (paramsLen > 0) {
                            Mesh3.uvSphere(m).scale(params[0]);
                        } else {
                            Mesh3.uvSphere(m);
                        }
                        meshes.add(m);
                    }
                    break;

                    case PConstants.BOX: { /* 41 */
                        final Mesh3 m = new Mesh3();
                        if (paramsLen > 2) {
                            final Vec3 s = new Vec3(
                                params[0],
                                params[1],
                                params[2]);
                            Mesh3.cube(m).scale(s);
                        } else if (paramsLen > 0) {
                            Mesh3.cube(m).scale(params[0]);
                        } else {
                            Mesh3.cube(m);
                        }
                        meshes.add(m);
                    }
                    break;

                    default:

                        System.err.println(kind + " is an unsupported kind.");
                }
            }
            break;

            case PShape.PATH: /* 102 */

                /*
                 * Not worth it to adhere to path commands, although doing so
                 * could make quadratic and cubic Bezier paths more accurate.
                 */

            case PShape.GEOMETRY: { /* 103 */

                final boolean is3d = source.is3D();
                final int vertLen = source.getVertexCount();

                /*
                 * Unsure how to find number of vertices in a face loop except
                 * as an educated guess. Support hexagonal faces maximum.
                 */
                int loopLen = 3;
                if (vertLen % 3 != 0) {
                    for (int i = 4; i < 7; ++i) {
                        if (vertLen % i == 0) {
                            loopLen = i;
                            break;
                        }
                    }
                }
                final int faceLen = vertLen / loopLen;

                /*
                 * Possible for all texture coordinates to be (0.0, 0.0).
                 * Also, for all normals to be (0.0, 0.0, 1.0).
                 */
                boolean diverseNormals = true;

                final int[][][] faces = new int[faceLen][loopLen][3];
                final Vec3[] coords = new Vec3[vertLen];
                final Vec2[] texCoords = new Vec2[vertLen];
                final Vec3[] normals = is3d
                    ? new Vec3[vertLen]
                    : new Vec3[]{Vec3.up(new Vec3())};

                if (is3d) {

                    for (int k = 0; k < vertLen; ++k) {
                        final int i = k / loopLen;
                        final int j = k % loopLen;
                        final int[] vert = faces[i][j];

                        vert[0] = k;
                        vert[1] = k;
                        vert[2] = k;

                        final float vnx = source.getNormalX(k);
                        final float vny = source.getNormalY(k);
                        final float vnz = source.getNormalZ(k);
                        diverseNormals = vnx != 0.0f || vny != 0.0f || vnz != 1.0f;

                        coords[k] = new Vec3(
                            source.getVertexX(k),
                            source.getVertexY(k),
                            source.getVertexZ(k));
                        texCoords[k] = new Vec2(
                            source.getTextureU(k),
                            source.getTextureV(k));
                        normals[k] = new Vec3(vnx, vny, vnz);
                    }

                } else {

                    for (int k = 0; k < vertLen; ++k) {
                        final int i = k / loopLen;
                        final int j = k % loopLen;

                        final int[] vert = faces[i][j];
                        vert[0] = k;
                        vert[1] = k;
                        vert[2] = 0;

                        coords[k] = new Vec3(
                            source.getVertexX(k),
                            source.getVertexY(k),
                            0.0f);
                        texCoords[k] = new Vec2(
                            source.getTextureU(k),
                            source.getTextureV(k));
                    }

                }

                /* Mesh is not cleaned, so it could contain duplicates. */
                final Mesh3 m = new Mesh3(sourceName, faces, coords, texCoords,
                    normals);
                if (is3d && !diverseNormals) {
                    m.shadeFlat();
                }
                m.reverseFaces();
                meshes.add(m);
            }
            break;

            default:

                System.err.println(family + " is an unsupported family.");

        }

        return meshes;
    }

    /**
     * {@link PShape#getName()} may return <code>null</code>. This method
     * returns the hexadecimal representation of the current time in
     * milliseconds if that is the case. {@link PShape#hashCode()} is
     * unreliable and should not be used as a substitute.
     *
     * @param pshp the shape
     * @return the name
     */
    private static String getPShapeName(final PShape pshp) {

        final String name = pshp.getName();
        return name != null ? name : Long.toHexString(System.currentTimeMillis());
    }

}

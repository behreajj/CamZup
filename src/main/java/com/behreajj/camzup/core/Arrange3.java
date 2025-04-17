package com.behreajj.camzup.core;

import java.util.Arrays;

/**
 * Provides methods to align and distribute 3D entities.
 */
public abstract class Arrange3 {

    /**
     * Discourage overriding with a private constructor.
     */
    private Arrange3() {
    }

    /**
     * Aligns all mesh entities in the array to the back edge of a bounds.
     *
     * @param b   the bounds
     * @param mes the mesh entities
     */
    public static void alignBack(final Bounds3 b, final MeshEntity3[] mes) {

        Arrange3.alignBack(b, mes, 1, 1.0f);
    }

    /**
     * Aligns all mesh entities in the array to the back edge of a bounds.
     * The sign indicates whether to align inside the edge (1), on the edge (0)
     * or outside the edge (-1).
     *
     * @param b    the bounds
     * @param mes  the mesh entities
     * @param sign the alignment sign
     * @param fac  the factor
     */
    public static void alignBack(
        final Bounds3 b,
        final MeshEntity3[] mes,
        final int sign,
        final float fac) {

        final float sgnVrf = 0.5f * Utils.sign(sign);
        final float back = b.min.y;
        final Bounds3 eb = new Bounds3();
        final Vec3 extent = new Vec3();
        final Vec3 pos = new Vec3();

        for (final MeshEntity3 entity : mes) {
            MeshEntity3.calcBounds(entity, eb);
            Bounds3.extentUnsigned(eb, extent);
            entity.getLocation(pos);
            pos.set(pos.x, back + extent.y * sgnVrf, pos.z);
            entity.moveTo(pos, fac);
        }
    }

    /**
     * Aligns all mesh entities in the array to the top edge of a bounds.
     *
     * @param b   the bounds
     * @param mes the mesh entities
     */
    public static void alignBottom(final Bounds3 b, final MeshEntity3[] mes) {

        Arrange3.alignBottom(b, mes, 1, 1.0f);
    }

    /**
     * Aligns all mesh entities in the array to the bottom edge of a bounds.
     * The sign indicates whether to align inside the edge (1), on the edge (0)
     * or outside the edge (-1).
     *
     * @param b    the bounds
     * @param mes  the mesh entities
     * @param sign the alignment sign
     * @param fac  the factor
     */
    public static void alignBottom(
        final Bounds3 b,
        final MeshEntity3[] mes,
        final int sign,
        final float fac) {

        final float sgnVrf = 0.5f * Utils.sign(sign);
        final float bottom = b.max.z;
        final Bounds3 eb = new Bounds3();
        final Vec3 extent = new Vec3();
        final Vec3 pos = new Vec3();

        for (final MeshEntity3 entity : mes) {
            MeshEntity3.calcBounds(entity, eb);
            Bounds3.extentUnsigned(eb, extent);
            entity.getLocation(pos);
            pos.set(pos.x, pos.y, bottom - extent.z * sgnVrf);
            entity.moveTo(pos, fac);
        }
    }

    /**
     * Aligns all mesh entities in the array to the front edge of a bounds.
     *
     * @param b   the bounds
     * @param mes the mesh entities
     */
    public static void alignFore(final Bounds3 b, final MeshEntity3[] mes) {

        Arrange3.alignFore(b, mes, 1, 1.0f);
    }

    /**
     * Aligns all mesh entities in the array to the front edge of a bounds.
     * The sign indicates whether to align inside the edge (1), on the edge (0)
     * or outside the edge (-1).
     *
     * @param b    the bounds
     * @param mes  the mesh entities
     * @param sign the alignment sign
     * @param fac  the factor
     */
    public static void alignFore(
        final Bounds3 b,
        final MeshEntity3[] mes,
        final int sign,
        final float fac) {

        final float sgnVrf = 0.5f * Utils.sign(sign);
        final float fore = b.max.y;
        final Bounds3 eb = new Bounds3();
        final Vec3 extent = new Vec3();
        final Vec3 pos = new Vec3();

        for (final MeshEntity3 entity : mes) {
            MeshEntity3.calcBounds(entity, eb);
            Bounds3.extentUnsigned(eb, extent);
            entity.getLocation(pos);
            pos.set(pos.x, fore - extent.y * sgnVrf, pos.z);
            entity.moveTo(pos, fac);
        }
    }

    /**
     * Aligns all mesh entities in the array to the left edge of a bounds.
     *
     * @param b   the bounds
     * @param mes the mesh entities
     */
    public static void alignLeft(final Bounds3 b, final MeshEntity3[] mes) {

        Arrange3.alignLeft(b, mes, 1, 1.0f);
    }

    /**
     * Aligns all mesh entities in the array to the left edge of a bounds. The
     * sign indicates whether to align inside the edge (1), on the edge (0) or
     * outside the edge (-1).
     *
     * @param b    the bounds
     * @param mes  the mesh entities
     * @param sign the alignment sign
     * @param fac  the factor
     */
    public static void alignLeft(
        final Bounds3 b,
        final MeshEntity3[] mes,
        final int sign,
        final float fac) {

        final float sgnVrf = 0.5f * Utils.sign(sign);
        final float left = b.min.x;
        final Bounds3 eb = new Bounds3();
        final Vec3 extent = new Vec3();
        final Vec3 pos = new Vec3();

        for (final MeshEntity3 entity : mes) {
            MeshEntity3.calcBounds(entity, eb);
            Bounds3.extentUnsigned(eb, extent);
            entity.getLocation(pos);
            pos.set(left + extent.x * sgnVrf, pos.y, pos.z);
            entity.moveTo(pos, fac);
        }
    }

    /**
     * Aligns all mesh entities in the array to the right edge of a bounds.
     *
     * @param b   the bounds
     * @param mes the mesh entities
     */
    public static void alignRight(final Bounds3 b, final MeshEntity3[] mes) {

        Arrange3.alignRight(b, mes, 1, 1.0f);
    }

    /**
     * Aligns all mesh entities in the array to the right edge of a bounds. The
     * sign indicates whether to align inside the edge (1), on the edge (0) or
     * outside the edge (-1).
     *
     * @param b    the bounds
     * @param mes  the mesh entities
     * @param sign the alignment sign
     * @param fac  the factor
     */
    public static void alignRight(
        final Bounds3 b,
        final MeshEntity3[] mes,
        final int sign,
        final float fac) {

        final float sgnVrf = 0.5f * Utils.sign(sign);
        final float right = b.max.x;
        final Bounds3 eb = new Bounds3();
        final Vec3 extent = new Vec3();
        final Vec3 pos = new Vec3();

        for (final MeshEntity3 entity : mes) {
            MeshEntity3.calcBounds(entity, eb);
            Bounds3.extentUnsigned(eb, extent);
            entity.getLocation(pos);
            pos.set(right - extent.x * sgnVrf, pos.y, pos.z);
            entity.moveTo(pos, fac);
        }
    }

    /**
     * Aligns all mesh entities in the array to the top edge of a bounds.
     *
     * @param b   the bounds
     * @param mes the mesh entities
     */
    public static void alignTop(final Bounds3 b, final MeshEntity3[] mes) {

        Arrange3.alignTop(b, mes, 1, 1.0f);
    }

    /**
     * Aligns all mesh entities in the array to the top edge of a bounds. The
     * sign indicates whether to align inside the edge (1), on the edge (0) or
     * outside the edge (-1).
     *
     * @param b    the bounds
     * @param mes  the mesh entities
     * @param sign the alignment sign
     * @param fac  the factor
     */
    public static void alignTop(
        final Bounds3 b,
        final MeshEntity3[] mes,
        final int sign,
        final float fac) {

        final float sgnVrf = 0.5f * Utils.sign(sign);
        final float top = b.min.z;
        final Bounds3 eb = new Bounds3();
        final Vec3 extent = new Vec3();
        final Vec3 pos = new Vec3();

        for (final MeshEntity3 entity : mes) {
            MeshEntity3.calcBounds(entity, eb);
            Bounds3.extentUnsigned(eb, extent);
            entity.getLocation(pos);
            pos.set(pos.x, pos.y, top + extent.z * sgnVrf);
            entity.moveTo(pos, fac);
        }
    }

    /**
     * Aligns all mesh entities in the array to the x axis center of the bounds.
     *
     * @param b   the bounds
     * @param mes the mesh entities
     */
    public static void alignX(final Bounds3 b, final MeshEntity3[] mes) {

        Arrange3.alignX(b, mes, 0, 1.0f);
    }

    /**
     * Aligns all mesh entities in the array to the x axis center of the bounds.
     * The sign indicates whether to align to the right of the edge (1), on the
     * edge (0) or to the left of the edge (-1).
     *
     * @param b    the bounds
     * @param mes  the mesh entities
     * @param sign the alignment sign
     * @param fac  the factor
     */
    public static void alignX(
        final Bounds3 b,
        final MeshEntity3[] mes,
        final int sign,
        final float fac) {

        final float sgnVrf = 0.5f * Utils.sign(sign);
        final Vec3 bCenter = Bounds3.center(b, new Vec3());
        final float xCenter = bCenter.x;
        final Bounds3 eb = new Bounds3();
        final Vec3 extent = new Vec3();
        final Vec3 pos = new Vec3();

        for (final MeshEntity3 entity : mes) {
            MeshEntity3.calcBounds(entity, eb);
            Bounds3.extentUnsigned(eb, extent);
            entity.getLocation(pos);
            pos.set(xCenter + extent.x * sgnVrf, pos.y, pos.z);
            entity.moveTo(pos, fac);
        }
    }

    /**
     * Aligns all mesh entities in the array to the y axis center of the bounds.
     *
     * @param b   the bounds
     * @param mes the mesh entities
     */
    public static void alignY(final Bounds3 b, final MeshEntity3[] mes) {

        Arrange3.alignY(b, mes, 0, 1.0f);
    }

    /**
     * Aligns all mesh entities in the array to the y axis center of the bounds.
     * The sign indicates whether to align above the edge (1), on the edge (0)
     * or to below the edge (-1).
     *
     * @param b    the bounds
     * @param mes  the mesh entities
     * @param sign the alignment sign
     * @param fac  the factor
     */
    public static void alignY(
        final Bounds3 b,
        final MeshEntity3[] mes,
        final int sign,
        final float fac) {

        final float sgnVrf = 0.5f * Utils.sign(sign);
        final Vec3 bCenter = Bounds3.center(b, new Vec3());
        final float yCenter = bCenter.y;
        final Bounds3 eb = new Bounds3();
        final Vec3 extent = new Vec3();
        final Vec3 pos = new Vec3();

        for (final MeshEntity3 entity : mes) {
            MeshEntity3.calcBounds(entity, eb);
            Bounds3.extentUnsigned(eb, extent);
            entity.getLocation(pos);
            pos.set(pos.x, yCenter + extent.y * sgnVrf, pos.z);
            entity.moveTo(pos, fac);
        }
    }

    /**
     * Aligns all mesh entities in the array to the z axis center of the bounds.
     *
     * @param b   the bounds
     * @param mes the mesh entities
     */
    public static void alignZ(final Bounds3 b, final MeshEntity3[] mes) {

        Arrange3.alignZ(b, mes, 0, 1.0f);
    }

    /**
     * Aligns all mesh entities in the array to the z axis center of the bounds.
     * The sign indicates whether to align above the edge (1), on the edge (0)
     * or to below the edge (-1).
     *
     * @param b    the bounds
     * @param mes  the mesh entities
     * @param sign the alignment sign
     * @param fac  the factor
     */
    public static void alignZ(
        final Bounds3 b,
        final MeshEntity3[] mes,
        final int sign,
        final float fac) {

        final float sgnVrf = 0.5f * Utils.sign(sign);
        final Vec3 bCenter = Bounds3.center(b, new Vec3());
        final float zCenter = bCenter.z;
        final Bounds3 eb = new Bounds3();
        final Vec3 extent = new Vec3();
        final Vec3 pos = new Vec3();

        for (final MeshEntity3 entity : mes) {
            MeshEntity3.calcBounds(entity, eb);
            Bounds3.extentUnsigned(eb, extent);
            entity.getLocation(pos);
            pos.set(pos.x, pos.y, zCenter + extent.z * sgnVrf);
            entity.moveTo(pos, fac);
        }
    }

    /**
     * Distributes all mesh entities in the array horizontally within a bounds.
     *
     * @param b   the bounds
     * @param mes the mesh entities
     */
    public static void distributeX(final Bounds3 b, final MeshEntity3[] mes) {

        Arrange3.distributeX(b, mes, 1, 1.0f);
    }

    /**
     * Distributes all mesh entities in the array horizontally within a bounds.
     * The sign indicates whether to align inside the edge (1), on the edge (0)
     * or to outside the edge (-1).
     * <br>
     * <br>
     * Creates a sorted copy of the input array.
     *
     * @param b    the bounds
     * @param mes  the mesh entities
     * @param sign the alignment sign
     * @param fac  the factor
     */
    public static void distributeX(
        final Bounds3 b,
        final MeshEntity3[] mes,
        final int sign,
        final float fac) {

        final int len = mes.length;
        final MeshEntity3[] sorted = new MeshEntity3[len];
        System.arraycopy(mes, 0, sorted, 0, len);
        Arrays.sort(sorted, (l, r) -> Float.compare(
            l.getLocation(new Vec3()).x,
            r.getLocation(new Vec3()).x));

        final float xMin = b.min.x;
        final float xMax = b.max.x;

        final float toFac = len > 1 ? 1.0f / (len - 1.0f) : 0.0f;
        final float facOff = len > 1 ? 0.0f : 0.5f;

        final float sgnVrf = 0.5f * Utils.sign(sign);

        final Bounds3 eb = new Bounds3();
        final Vec3 extent = new Vec3();
        final Vec3 pos = new Vec3();

        for (int i = 0; i < len; ++i) {
            final float t = i * toFac + facOff;
            final float u = 1.0f - t;
            final float xDistr = u * xMin + t * xMax;
            final float offset = u * sgnVrf + t * -sgnVrf;
            final MeshEntity3 entity = sorted[i];
            MeshEntity3.calcBounds(entity, eb);
            Bounds3.extentUnsigned(eb, extent);
            entity.getLocation(pos);
            pos.set(xDistr + extent.x * offset, pos.y, pos.z);
            entity.moveTo(pos, fac);
        }
    }

    /**
     * Distributes all mesh entities in the array on the depth axis within a
     * bounds.
     *
     * @param b   the bounds
     * @param mes the mesh entities
     */
    public static void distributeY(final Bounds3 b, final MeshEntity3[] mes) {

        Arrange3.distributeY(b, mes, 1, 1.0f);
    }

    /**
     * Distributes all mesh entities in the array on the depth axis within a
     * bounds. The sign indicates whether to align inside the edge (1), on the
     * edge (0) or to outside the edge (-1).
     * <br>
     * <br>
     * Creates a sorted copy of the input array.
     *
     * @param b    the bounds
     * @param mes  the mesh entities
     * @param sign the alignment sign
     * @param fac  the factor
     */
    public static void distributeY(
        final Bounds3 b,
        final MeshEntity3[] mes,
        final int sign,
        final float fac) {

        final int len = mes.length;
        final MeshEntity3[] sorted = new MeshEntity3[len];
        System.arraycopy(mes, 0, sorted, 0, len);
        Arrays.sort(sorted, (l, r) -> Float.compare(
            l.getLocation(new Vec3()).y,
            r.getLocation(new Vec3()).y));

        final float yMin = b.min.y;
        final float yMax = b.max.y;

        final float toFac = len > 1 ? 1.0f / (len - 1.0f) : 0.0f;
        final float facOff = len > 1 ? 0.0f : 0.5f;

        final float sgnVrf = 0.5f * Utils.sign(sign);

        final Bounds3 eb = new Bounds3();
        final Vec3 extent = new Vec3();
        final Vec3 pos = new Vec3();

        for (int i = 0; i < len; ++i) {
            final float t = i * toFac + facOff;
            final float u = 1.0f - t;
            final float yDistr = u * yMin + t * yMax;
            final float offset = u * sgnVrf + t * -sgnVrf;
            final MeshEntity3 entity = sorted[i];
            MeshEntity3.calcBounds(entity, eb);
            Bounds3.extentUnsigned(eb, extent);
            entity.getLocation(pos);
            pos.set(pos.x, yDistr + extent.y * offset, pos.z);
            entity.moveTo(pos, fac);
        }
    }

    /**
     * Distributes all mesh entities in the array vertically within a bounds.
     *
     * @param b   the bounds
     * @param mes the mesh entities
     */
    public static void distributeZ(final Bounds3 b, final MeshEntity3[] mes) {

        Arrange3.distributeZ(b, mes, 1, 1.0f);
    }

    /**
     * Distributes all mesh entities in the array vertically within a bounds.
     * The sign indicates whether to align inside the edge (1), on the edge (0)
     * or to outside the edge (-1).
     * <br>
     * <br>
     * Creates a sorted copy of the input array.
     *
     * @param b    the bounds
     * @param mes  the mesh entities
     * @param sign the alignment sign
     * @param fac  the factor
     */
    public static void distributeZ(
        final Bounds3 b,
        final MeshEntity3[] mes,
        final int sign,
        final float fac) {

        final int len = mes.length;
        final MeshEntity3[] sorted = new MeshEntity3[len];
        System.arraycopy(mes, 0, sorted, 0, len);
        Arrays.sort(sorted, (l, r) -> Float.compare(
            l.getLocation(new Vec3()).z,
            r.getLocation(new Vec3()).z));

        final float zMin = b.min.z;
        final float zMax = b.max.z;

        final float toFac = len > 1 ? 1.0f / (len - 1.0f) : 0.0f;
        final float facOff = len > 1 ? 0.0f : 0.5f;

        final float sgnVrf = 0.5f * Utils.sign(sign);

        final Bounds3 eb = new Bounds3();
        final Vec3 extent = new Vec3();
        final Vec3 pos = new Vec3();

        for (int i = 0; i < len; ++i) {
            final float t = i * toFac + facOff;
            final float u = 1.0f - t;
            final float zDistr = u * zMin + t * zMax;
            final float offset = u * sgnVrf + t * -sgnVrf;
            final MeshEntity3 entity = sorted[i];
            MeshEntity3.calcBounds(entity, eb);
            Bounds3.extentUnsigned(eb, extent);
            entity.getLocation(pos);
            pos.set(pos.x, pos.y, zDistr + extent.z * offset);
            entity.moveTo(pos, fac);
        }
    }

    /**
     * Arranges mesh entities into a column.
     *
     * @param b   the bounds
     * @param mes the mesh entities
     */
    public static void toColumn(final Bounds3 b, final MeshEntity3[] mes) {

        Arrange3.toColumn(b, mes, 1, 1.0f);
    }

    /**
     * Arranges mesh entities into a column.
     *
     * @param b    the bounds
     * @param mes  the mesh entities
     * @param sign the alignment sign
     * @param fac  the factor
     * @see Arrange3#alignX(Bounds3, MeshEntity3[], int, float)
     * @see Arrange3#distributeY(Bounds3, MeshEntity3[], int, float)
     * @see Arrange3#alignZ(Bounds3, MeshEntity3[], int, float)
     */
    public static void toColumn(
        final Bounds3 b,
        final MeshEntity3[] mes,
        final int sign,
        final float fac) {

        Arrange3.alignX(b, mes, 0, fac);
        Arrange3.distributeY(b, mes, sign, fac);
        Arrange3.alignZ(b, mes, 0, fac);
    }

    /**
     * Arranges mesh entities into a column.
     *
     * @param b   the bounds
     * @param mes the mesh entities
     */
    public static void toLayer(final Bounds3 b, final MeshEntity3[] mes) {

        Arrange3.toLayer(b, mes, 1, 1.0f);
    }

    /**
     * Arranges mesh entities into a layer.
     *
     * @param b    the bounds
     * @param mes  the mesh entities
     * @param sign the alignment sign
     * @param fac  the factor
     * @see Arrange3#alignX(Bounds3, MeshEntity3[], int, float)
     * @see Arrange3#alignY(Bounds3, MeshEntity3[], int, float)
     * @see Arrange3#distributeZ(Bounds3, MeshEntity3[], int, float)
     */
    public static void toLayer(
        final Bounds3 b,
        final MeshEntity3[] mes,
        final int sign,
        final float fac) {

        Arrange3.alignX(b, mes, 0, fac);
        Arrange3.alignY(b, mes, 0, fac);
        Arrange3.distributeZ(b, mes, sign, fac);
    }

    /**
     * Arranges mesh entities into a row.
     *
     * @param b   the bounds
     * @param mes the mesh entities
     */
    public static void toRow(final Bounds3 b, final MeshEntity3[] mes) {

        Arrange3.toRow(b, mes, 1, 1.0f);
    }

    /**
     * Arranges mesh entities into a row.
     *
     * @param b    the bounds
     * @param mes  the mesh entities
     * @param sign the alignment sign
     * @param fac  the factor
     * @see Arrange3#distributeX(Bounds3, MeshEntity3[], int, float)
     * @see Arrange3#alignY(Bounds3, MeshEntity3[], int, float)
     * @see Arrange3#alignZ(Bounds3, MeshEntity3[], int, float)
     */
    public static void toRow(
        final Bounds3 b,
        final MeshEntity3[] mes,
        final int sign,
        final float fac) {

        Arrange3.distributeX(b, mes, sign, fac);
        Arrange3.alignY(b, mes, 0, fac);
        Arrange3.alignZ(b, mes, 0, fac);
    }
}

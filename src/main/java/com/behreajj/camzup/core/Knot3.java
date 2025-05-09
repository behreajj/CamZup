package com.behreajj.camzup.core;

/**
 * Organizes the vectors that shape a cubic Bézier curve into a coordinate (or
 * anchor point), fore handle (the following control point) and rear handle
 * (the preceding control point).
 */
public class Knot3 implements Comparable<Knot3> {

    /**
     * The spatial coordinate of the knot.
     */
    public final Vec3 coord = new Vec3();

    /**
     * The handle that warps the curve segment heading away from the knot along
     * the direction of the curve.
     */
    public final Vec3 foreHandle = new Vec3();

    /**
     * The handle that warps the curve segment heading towards the knot along
     * the direction of the curve.
     */
    public final Vec3 rearHandle = new Vec3();

    /**
     * The default constructor.
     */
    public Knot3() {
    }

    /**
     * Creates a knot from a coordinate.
     *
     * @param xCoord the x coordinate
     * @param yCoord the y coordinate
     * @param zCoord the z coordinate
     */
    public Knot3(final float xCoord, final float yCoord, final float zCoord) {

        this.set(xCoord, yCoord, zCoord);
    }

    /**
     * Creates a knot from a coordinate and fore handle. The rear handle is a
     * mirror of the fore handle.
     *
     * @param xCoord the x coordinate
     * @param yCoord the y coordinate
     * @param zCoord the z coordinate
     * @param xFore  the fore handle x
     * @param yFore  the fore handle y
     * @param zFore  the fore handle z
     */
    public Knot3(
        final float xCoord, final float yCoord, final float zCoord,
        final float xFore, final float yFore, final float zFore) {

        this.set(xCoord, yCoord, zCoord, xFore, yFore, zFore);
    }

    /**
     * Creates a knot from real numbers.
     *
     * @param xCoord the x coordinate
     * @param yCoord the y coordinate
     * @param zCoord the z coordinate
     * @param xFore  the fore handle x
     * @param yFore  the fore handle y
     * @param zFore  the fore handle z
     * @param xRear  the rear handle x
     * @param yRear  the rear handle y
     * @param zRear  the rear handle z
     */
    public Knot3(
        final float xCoord, final float yCoord, final float zCoord,
        final float xFore, final float yFore, final float zFore,
        final float xRear, final float yRear, final float zRear) {

        this.set(
            xCoord, yCoord, zCoord,
            xFore, yFore, zFore,
            xRear, yRear, zRear);
    }

    /**
     * Creates a knot from a 2D source knot.<br>
     * <br>
     * For the purpose of promoting 2D curves to 3D.
     *
     * @param source the source
     */
    public Knot3(final Knot2 source) {
        this.set(source);
    }

    /**
     * Creates a knot from a source knot.
     *
     * @param source the source
     */
    public Knot3(final Knot3 source) {
        this.set(source);
    }

    /**
     * Creates a knot from a series of 2D vectors.<br>
     * <br>
     * For the purpose of promoting 2D curves to 3D.
     *
     * @param coord      the coordinate
     * @param foreHandle the fore handle
     * @param rearHandle the rear handle
     */
    public Knot3(final Vec2 coord, final Vec2 foreHandle, final Vec2 rearHandle) {

        this.set(coord, foreHandle, rearHandle);
    }

    /**
     * Creates a knot from a coordinate.
     *
     * @param coord the coordinate
     */
    public Knot3(final Vec3 coord) {
        this.set(coord);
    }

    /**
     * Creates a knot from a coordinate and fore handle. The rear handle is a
     * mirror of the fore handle.
     *
     * @param coord      the coordinate
     * @param foreHandle the fore handle
     */
    public Knot3(final Vec3 coord, final Vec3 foreHandle) {

        this.set(coord, foreHandle);
    }

    /**
     * Creates a knot from a series of vectors.
     *
     * @param coord      the coordinate
     * @param foreHandle the fore handle
     * @param rearHandle the rear handle
     */
    public Knot3(final Vec3 coord, final Vec3 foreHandle, final Vec3 rearHandle) {

        this.set(coord, foreHandle, rearHandle);
    }

    /**
     * Returns a coordinate given two knots and a step.
     *
     * @param a      the origin knot
     * @param b      the destination knot
     * @param step   the step
     * @param target the output coordinate
     * @return the coordinate
     * @see Vec3#bezierPoint(Vec3, Vec3, Vec3, Vec3, float, Vec3)
     */
    public static Vec3 bezierPoint(
        final Knot3 a,
        final Knot3 b,
        final float step,
        final Vec3 target) {

        return Vec3.bezierPoint(a.coord, a.foreHandle, b.rearHandle, b.coord,
            step, target);
    }

    /**
     * Returns a tangent given two knots and a step.
     *
     * @param a      the origin knot
     * @param b      the destination knot
     * @param step   the step
     * @param target the output coordinate
     * @return the coordinate
     * @see Vec3#bezierTangent(Vec3, Vec3, Vec3, Vec3, float, Vec3)
     */
    public static Vec3 bezierTangent(
        final Knot3 a,
        final Knot3 b,
        final float step,
        final Vec3 target) {

        return Vec3.bezierTangent(a.coord, a.foreHandle, b.rearHandle, b.coord,
            step, target);
    }

    /**
     * Returns a normalized tangent given two knots and a step.
     *
     * @param a      the origin knot
     * @param b      the destination knot
     * @param step   the step
     * @param target the output coordinate
     * @return the coordinate
     * @see Vec3#bezierTanUnit(Vec3, Vec3, Vec3, Vec3, float, Vec3)
     */
    public static Vec3 bezierTanUnit(
        final Knot3 a,
        final Knot3 b,
        final float step,
        final Vec3 target) {

        return Vec3.bezierTanUnit(a.coord, a.foreHandle, b.rearHandle, b.coord,
            step, target);
    }

    /**
     * Gets the fore handle of a knot as a direction, rather than as a point.
     *
     * @param knot   the knot
     * @param target the output vector
     * @return the fore handle vector
     * @see Vec3#subNorm(Vec3, Vec3, Vec3)
     */
    public static Vec3 foreDir(final Knot3 knot, final Vec3 target) {

        return Vec3.subNorm(knot.foreHandle, knot.coord, target);
    }

    /**
     * Returns the magnitude of the knot's fore handle, i.e., the Euclidean
     * distance between the fore handle and the coordinate.
     *
     * @param knot the knot
     * @return the magnitude
     * @see Vec3#distEuclidean(Vec3, Vec3)
     */
    public static float foreMag(final Knot3 knot) {

        return Vec3.distEuclidean(knot.coord, knot.foreHandle);
    }

    /**
     * Gets the fore handle of a knot as a vector, rather than as a point.
     *
     * @param knot   the knot
     * @param target the output vector
     * @return the fore handle vector
     * @see Vec3#sub(Vec3, Vec3, Vec3)
     */
    public static Vec3 foreVec(final Knot3 knot, final Vec3 target) {

        return Vec3.sub(knot.foreHandle, knot.coord, target);
    }

    /**
     * Sets two knots from a segment of a Catmull-Rom curve. The default curve
     * tightness is 0.0.
     * <br>
     * <br>
     * Assumes that the previous knot's coordinate is set to a prior anchor
     * point. The previous knot's fore handle, the next knot's rear handle and
     * the next knot's coordinate are set by this function.
     *
     * @param xPrevAnchor the previous anchor x
     * @param yPrevAnchor the previous anchor y
     * @param zPrevAnchor the previous anchor z
     * @param xCurrAnchor the current anchor x
     * @param yCurrAnchor the current anchor y
     * @param zCurrAnchor the current anchor z
     * @param xNextAnchor the next anchor x
     * @param yNextAnchor the next anchor y
     * @param zNextAnchor the next anchor z
     * @param xAdvAnchor  the advance anchor x
     * @param yAdvAnchor  the advance anchor y
     * @param zAdvAnchor  the advance anchor z
     * @param tightness   the curve tightness
     * @param prev        the previous knot
     * @param next        the next knot
     * @return the next knot
     */
    public static Knot3 fromSegCatmull(
        final float xPrevAnchor,
        final float yPrevAnchor,
        final float zPrevAnchor,
        final float xCurrAnchor,
        final float yCurrAnchor,
        final float zCurrAnchor,
        final float xNextAnchor,
        final float yNextAnchor,
        final float zNextAnchor,
        final float xAdvAnchor,
        final float yAdvAnchor,
        final float zAdvAnchor,
        final float tightness,
        final Knot3 prev,
        final Knot3 next) {

        if (Utils.approx(tightness, 1.0f)) {
            return Knot3.fromSegLinear(
                xNextAnchor, yNextAnchor, zNextAnchor,
                prev, next);
        }

        final float fac = (tightness - 1.0f) * -Utils.ONE_SIX;
        prev.foreHandle.set(
            xCurrAnchor + (xNextAnchor - xPrevAnchor) * fac,
            yCurrAnchor + (yNextAnchor - yPrevAnchor) * fac,
            zCurrAnchor + (zNextAnchor - zPrevAnchor) * fac);
        next.rearHandle.set(
            xNextAnchor - (xAdvAnchor - xCurrAnchor) * fac,
            yNextAnchor - (yAdvAnchor - yCurrAnchor) * fac,
            zNextAnchor - (zAdvAnchor - zCurrAnchor) * fac);
        next.coord.set(xNextAnchor, yNextAnchor, zNextAnchor);

        return next;
    }

    /**
     * Sets two knots from a segment of a Catmull-Rom curve. The default curve
     * tightness is 0.0.
     * <br>
     * <br>
     * Assumes that the previous knot's coordinate is set to a prior anchor
     * point. The previous knot's fore handle, the next knot's rear handle and
     * the next knot's coordinate are set by this function.
     *
     * @param prevAnchor the previous anchor
     * @param currAnchor the current anchor
     * @param nextAnchor the next anchor
     * @param advAnchor  the advance anchor
     * @param tightness  the curve tightness
     * @param prev       the previous knot
     * @param next       the next knot
     * @return the next knot
     */
    public static Knot3 fromSegCatmull(
        final Vec3 prevAnchor,
        final Vec3 currAnchor,
        final Vec3 nextAnchor,
        final Vec3 advAnchor,
        final float tightness,
        final Knot3 prev,
        final Knot3 next) {

        return Knot3.fromSegCatmull(
            prevAnchor.x, prevAnchor.y, prevAnchor.z,
            currAnchor.x, currAnchor.y, currAnchor.z,
            nextAnchor.x, nextAnchor.y, nextAnchor.z,
            advAnchor.x, advAnchor.y, advAnchor.z,
            tightness,
            prev, next);
    }

    /**
     * Sets a knot from a line segment. Assumes that the previous knot's
     * coordinate is set to the first anchor point.
     * <br>
     * <br>
     * The previous knot's fore handle, the next knot's rear handle and the
     * next knot's coordinate are set by this function.
     *
     * @param xNextAnchor the next anchor x
     * @param yNextAnchor the next anchor y
     * @param zNextAnchor the next anchor z
     * @param prev        the previous knot
     * @param next        the next knot
     * @return the next knot
     */
    public static Knot3 fromSegLinear(
        final float xNextAnchor,
        final float yNextAnchor,
        final float zNextAnchor,
        final Knot3 prev,
        final Knot3 next) {

        final Vec3 prevCoord = prev.coord;
        final Vec3 nextCoord = next.coord;

        nextCoord.set(xNextAnchor, yNextAnchor, zNextAnchor);

        prev.foreHandle.set(
            prevCoord.x * Utils.TWO_THIRDS + nextCoord.x * Utils.ONE_THIRD,
            prevCoord.y * Utils.TWO_THIRDS + nextCoord.y * Utils.ONE_THIRD,
            prevCoord.z * Utils.TWO_THIRDS + nextCoord.z * Utils.ONE_THIRD);

        next.rearHandle.set(
            nextCoord.x * Utils.TWO_THIRDS + prevCoord.x * Utils.ONE_THIRD,
            nextCoord.y * Utils.TWO_THIRDS + prevCoord.y * Utils.ONE_THIRD,
            nextCoord.z * Utils.TWO_THIRDS + prevCoord.z * Utils.ONE_THIRD);

        return next;
    }

    /**
     * Sets a knot from a line segment. Assumes that the previous knot's
     * coordinate is set to the first anchor point.
     * <br>
     * <br>
     * The previous knot's fore handle, the next knot's rear handle and the
     * next knot's coordinate are set by this function.
     *
     * @param nextAnchor the next anchor
     * @param prev       the previous knot
     * @param next       the next knot
     * @return the next knot
     * @see Knot3#fromSegLinear(float, float, float, Knot3, Knot3)
     */
    public static Knot3 fromSegLinear(
        final Vec3 nextAnchor,
        final Knot3 prev,
        final Knot3 next) {

        return Knot3.fromSegLinear(
            nextAnchor.x, nextAnchor.y, nextAnchor.z,
            prev, next);
    }

    /**
     * Sets two knots from a segment of a quadratic curve. Assumes that the
     * previous knot's coordinate is set to the first anchor point.
     * <br>
     * <br>
     * The previous knot's fore handle, the next knot's rear handle and the
     * next knot's coordinate are set by this function.
     *
     * @param xControl    the control point x
     * @param yControl    the control point y
     * @param zControl    the control point z
     * @param xNextAnchor the next anchor x
     * @param yNextAnchor the next anchor y
     * @param zNextAnchor the next anchor z
     * @param prev        the previous knot
     * @param next        the next knot
     * @return the next knot
     */
    public static Knot3 fromSegQuadratic(
        final float xControl,
        final float yControl,
        final float zControl,
        final float xNextAnchor,
        final float yNextAnchor,
        final float zNextAnchor,
        final Knot3 prev,
        final Knot3 next) {

        final Vec3 prevCo = prev.coord;

        final float midpt23x = xControl * Utils.TWO_THIRDS;
        final float midpt23y = yControl * Utils.TWO_THIRDS;
        final float midpt23z = zControl * Utils.TWO_THIRDS;

        prev.foreHandle.set(
            midpt23x + Utils.ONE_THIRD * prevCo.x,
            midpt23y + Utils.ONE_THIRD * prevCo.y,
            midpt23z + Utils.ONE_THIRD * prevCo.z);

        next.rearHandle.set(
            midpt23x + Utils.ONE_THIRD * xNextAnchor,
            midpt23y + Utils.ONE_THIRD * yNextAnchor,
            midpt23z + Utils.ONE_THIRD * zNextAnchor);

        next.coord.set(xNextAnchor, yNextAnchor, zNextAnchor);

        return next;
    }

    /**
     * Sets two knots from a segment of a quadratic curve. Assumes that the
     * previous knot's coordinate is set to the first anchor point.
     * <br>
     * <br>
     * The previous knot's fore handle, the next knot's rear handle and the
     * next knot's coordinate are set by this function.
     *
     * @param control    the control point
     * @param nextAnchor the next anchor point
     * @param prev       the previous knot
     * @param next       the next knot
     * @return the next knot
     * @see Knot3#fromSegQuadratic(float, float, float, float, float, float,
     * Knot3, Knot3)
     */
    public static Knot3 fromSegQuadratic(
        final Vec3 control,
        final Vec3 nextAnchor,
        final Knot3 prev,
        final Knot3 next) {

        return Knot3.fromSegQuadratic(
            control.x, control.y, control.z,
            nextAnchor.x, nextAnchor.y, nextAnchor.z,
            prev, next);
    }

    /**
     * Gets the rear handle of a knot as a direction, rather than as a point.
     *
     * @param knot   the knot
     * @param target the output vector
     * @return the rear handle vector
     * @see Vec3#subNorm(Vec3, Vec3, Vec3)
     */
    public static Vec3 rearDir(final Knot3 knot, final Vec3 target) {

        return Vec3.subNorm(knot.rearHandle, knot.coord, target);
    }

    /**
     * Returns the magnitude of the knot's rear handle, i.e., the Euclidean
     * distance between the rear handle and the coordinate.
     *
     * @param knot the knot
     * @return the magnitude
     * @see Vec3#distEuclidean(Vec3, Vec3)
     */
    public static float rearMag(final Knot3 knot) {

        return Vec3.distEuclidean(knot.rearHandle, knot.coord);
    }

    /**
     * Gets the rear handle of a knot as a vector, rather than as a point.
     *
     * @param knot   the knot
     * @param target the output vector
     * @return the rear handle vector
     * @see Vec3#sub(Vec3, Vec3, Vec3)
     */
    public static Vec3 rearVec(final Knot3 knot, final Vec3 target) {

        return Vec3.sub(knot.rearHandle, knot.coord, target);
    }

    /**
     * Smoothes the handles of a knot with reference to a previous and next knot. A
     * helper function to {@link Curve3#smoothHandles(Curve3)} .
     *
     * @param prev  the previous knot
     * @param curr  the current knot
     * @param next  the next knot
     * @param carry a temporary vector
     * @return the current knot
     */
    public static Knot3 smoothHandles(
        final Knot3 prev, final Knot3 curr, final Knot3 next, final Vec3 carry) {

        final Vec3 coCurr = curr.coord;
        final Vec3 coPrev = prev.coord;
        final Vec3 coNext = next.coord;

        final float xRear = coPrev.x - coCurr.x;
        final float yRear = coPrev.y - coCurr.y;
        final float zRear = coPrev.z - coCurr.z;

        final float xFore = coNext.x - coCurr.x;
        final float yFore = coNext.y - coCurr.y;
        final float zFore = coNext.z - coCurr.z;

        final float bmSq = xRear * xRear + yRear * yRear + zRear * zRear;
        final float bmInv = Utils.invSqrt(bmSq);

        final float fmSq = xFore * xFore + yFore * yFore + zFore * zFore;
        final float fmInv = Utils.invSqrt(fmSq);

        final float xDir = carry.x + xRear * bmInv - xFore * fmInv;
        final float yDir = carry.y + yRear * bmInv - yFore * fmInv;
        final float zDir = carry.z + zRear * bmInv - zFore * fmInv;

        final float rescl = Utils.ONE_THIRD * Utils.invSqrt(xDir * xDir + yDir * yDir + zDir * zDir);
        carry.x = xDir * rescl;
        carry.y = yDir * rescl;
        carry.z = zDir * rescl;

        final float bMag = bmSq * bmInv;
        curr.rearHandle.set(
            coCurr.x + bMag * carry.x, coCurr.y + bMag * carry.y, coCurr.z + bMag * carry.z);

        final float fMag = fmSq * fmInv;
        curr.foreHandle.set(
            coCurr.x - fMag * carry.x, coCurr.y - fMag * carry.y, coCurr.z - fMag * carry.z);

        return curr;
    }

    /**
     * Smoothes the fore handle of the first knot in an open curve. A helper
     * function to {@link Curve3#smoothHandles(Curve3)} .
     *
     * @param curr  the current knot
     * @param next  the next knot
     * @param carry a temporary vector
     * @return the current knot
     */
    public static Knot3 smoothHandlesFirst(final Knot3 curr, final Knot3 next, final Vec3 carry) {

        final Vec3 coCurr = curr.coord;
        final Vec3 coNext = next.coord;

        final float xRear = -coCurr.x;
        final float yRear = -coCurr.y;
        final float zRear = -coCurr.z;

        final float xFore = coNext.x + xRear;
        final float yFore = coNext.y + yRear;
        final float zFore = coNext.z + zRear;

        final float bmSq = xRear * xRear + yRear * yRear + zRear * zRear;
        final float bmInv = Utils.invSqrt(bmSq);

        final float fmSq = xFore * xFore + yFore * yFore + zFore * zFore;
        final float fmInv = Utils.invSqrt(fmSq);

        final float xDir = carry.x + xRear * bmInv - xFore * fmInv;
        final float yDir = carry.y + yRear * bmInv - yFore * fmInv;
        final float zDir = carry.z + zRear * bmInv - zFore * fmInv;

        final float rescl = Utils.ONE_THIRD * Utils.invSqrt(xDir * xDir + yDir * yDir + zDir * zDir);
        carry.x = xDir * rescl;
        carry.y = yDir * rescl;
        carry.z = zDir * rescl;

        final float fMag = fmSq * fmInv;
        curr.foreHandle.set(
            coCurr.x - fMag * carry.x, coCurr.y - fMag * carry.y, coCurr.z - fMag * carry.z);

        return curr;
    }

    /**
     * Smoothes the rear handle of the last knot in an open curve. A helper
     * function to {@link Curve3#smoothHandles(Curve3)} .
     *
     * @param prev  the previous knot
     * @param curr  the current knot
     * @param carry a temporary vector
     * @return the current knot
     */
    public static Knot3 smoothHandlesLast(final Knot3 prev, final Knot3 curr, final Vec3 carry) {

        final Vec3 coCurr = curr.coord;
        final Vec3 coPrev = prev.coord;

        final float xFore = -coCurr.x;
        final float yFore = -coCurr.y;
        final float zFore = -coCurr.z;

        final float xRear = coPrev.x + xFore;
        final float yRear = coPrev.y + yFore;
        final float zRear = coPrev.z + zFore;

        final float bmSq = xRear * xRear + yRear * yRear + zRear * zRear;
        final float bmInv = Utils.invSqrt(bmSq);

        final float fmSq = xFore * xFore + yFore * yFore + zFore * zFore;
        final float fmInv = Utils.invSqrt(fmSq);

        final float xDir = carry.x + xRear * bmInv - xFore * fmInv;
        final float yDir = carry.y + yRear * bmInv - yFore * fmInv;
        final float zDir = carry.z + zRear * bmInv - zFore * fmInv;

        final float rescl = Utils.ONE_THIRD * Utils.invSqrt(xDir * xDir + yDir * yDir + zDir * zDir);
        carry.x = xDir * rescl;
        carry.y = yDir * rescl;
        carry.z = zDir * rescl;

        final float bMag = bmSq * bmInv;
        curr.rearHandle.set(
            coCurr.x + bMag * carry.x, coCurr.y + bMag * carry.y, coCurr.z + bMag * carry.z);

        return curr;
    }

    /**
     * Creates a knot from polar coordinates, where the knot's fore handle is
     * tangent to the radius.
     *
     * @param cosa      the cosine of the angle
     * @param sina      the sine of the angle
     * @param radius    the radius
     * @param handleMag the length of the handles
     * @param target    the output knot
     */
    static void fromPolar(
        final float cosa,
        final float sina,
        final float radius,
        final float handleMag,
        final float xCenter,
        final float yCenter,
        final float zCenter,
        final Knot3 target) {

        final Vec3 coord = target.coord;
        coord.set(xCenter + radius * cosa, yCenter + radius * sina, zCenter);

        final float hmsina = sina * handleMag;
        final float hmcosa = cosa * handleMag;

        target.foreHandle.set(coord.x - hmsina, coord.y + hmcosa, zCenter);
        target.rearHandle.set(coord.x + hmsina, coord.y - hmcosa, zCenter);

    }

    /**
     * Adopts the fore handle of a source knot.
     *
     * @param source the source knot
     * @return this knot
     */
    @SuppressWarnings("UnusedReturnValue")
    public Knot3 adoptForeHandle(final Knot3 source) {

        this.foreHandle.set(
            this.coord.x + source.foreHandle.x - source.coord.x,
            this.coord.y + source.foreHandle.y - source.coord.y,
            this.coord.z + source.foreHandle.z - source.coord.z);

        return this;
    }

    /**
     * Adopts the fore handle and rear handle of a source knot.
     *
     * @param source the source knot
     * @return this knot
     */
    public Knot3 adoptHandles(final Knot3 source) {

        this.adoptForeHandle(source);
        this.adoptRearHandle(source);

        return this;
    }

    /**
     * Adopts the rear handle of a source knot.
     *
     * @param source the source knot
     * @return this knot
     */
    @SuppressWarnings("UnusedReturnValue")
    public Knot3 adoptRearHandle(final Knot3 source) {

        this.rearHandle.set(
            this.coord.x + source.rearHandle.x - source.coord.x,
            this.coord.y + source.rearHandle.y - source.coord.y,
            this.coord.z + source.rearHandle.z - source.coord.z);

        return this;
    }

    /**
     * Aligns this knot's handles in the same direction while preserving their
     * magnitude.
     *
     * @return this knot
     * @see Knot3#alignHandlesForward()
     */
    public Knot3 alignHandles() {
        return this.alignHandlesForward();
    }

    /**
     * Aligns this knot's fore handle to its rear handle while preserving
     * magnitude.
     *
     * @return this knot
     * @see Utils#hypot(float, float, float)
     * @see Utils#invSqrtUnchecked(float)
     */
    public Knot3 alignHandlesBackward() {

        final float xCoord = this.coord.x;
        final float yCoord = this.coord.y;
        final float zCoord = this.coord.z;

        final float xRear = this.rearHandle.x - xCoord;
        final float yRear = this.rearHandle.y - yCoord;
        final float zRear = this.rearHandle.z - zCoord;

        final float rmsq = xRear * xRear + yRear * yRear + zRear * zRear;
        if (rmsq > 0.0f) {
            final float flipRescale = -Utils.hypot(
                this.foreHandle.x - xCoord,
                this.foreHandle.y - yCoord,
                this.foreHandle.z - zCoord)
                * Utils.invSqrtUnchecked(rmsq);

            this.foreHandle.x = xRear * flipRescale + xCoord;
            this.foreHandle.y = yRear * flipRescale + yCoord;
            this.foreHandle.z = zRear * flipRescale + zCoord;
        }

        return this;
    }

    /**
     * Aligns this knot's rear handle to its fore handle while preserving
     * magnitude.
     *
     * @return this knot
     * @see Utils#hypot(float, float, float)
     * @see Utils#invSqrtUnchecked(float)
     */
    public Knot3 alignHandlesForward() {

        final float xCoord = this.coord.x;
        final float yCoord = this.coord.y;
        final float zCoord = this.coord.z;

        final float xFore = this.foreHandle.x - xCoord;
        final float yFore = this.foreHandle.y - yCoord;
        final float zFore = this.foreHandle.z - zCoord;

        final float fmsq = xFore * xFore + yFore * yFore + zFore * zFore;
        if (fmsq > 0.0f) {
            final float flipRescale = -Utils.hypot(
                this.rearHandle.x - xCoord,
                this.rearHandle.y - yCoord,
                this.rearHandle.z - zCoord)
                * Utils.invSqrtUnchecked(fmsq);

            this.rearHandle.x = xFore * flipRescale + xCoord;
            this.rearHandle.y = yFore * flipRescale + yCoord;
            this.rearHandle.z = zFore * flipRescale + zCoord;
        }

        return this;
    }

    /**
     * Compares this knot to another based on a comparison between coordinates.
     *
     * @param knot the other knot
     * @return the evaluation
     */
    @Override
    public int compareTo(final Knot3 knot) {

        return this.coord.compareTo(knot.coord);
    }

    /**
     * Mirrors this knot's handles. Defaults to mirroring in the forward
     * direction.
     *
     * @return this knot
     * @see Knot3#mirrorHandlesForward()
     */
    public Knot3 mirrorHandles() {
        return this.mirrorHandlesForward();
    }

    /**
     * Sets the fore handle to mirror the rear handle: the fore will have the
     * same magnitude and negated direction of the rear.
     *
     * @return this knot
     */
    public Knot3 mirrorHandlesBackward() {

        this.foreHandle.set(
            this.coord.x - (this.rearHandle.x - this.coord.x),
            this.coord.y - (this.rearHandle.y - this.coord.y),
            this.coord.z - (this.rearHandle.z - this.coord.z));

        return this;
    }

    /**
     * Sets the rear handle to mirror the fore handle: the rear will have the
     * same magnitude and negated direction of the fore.
     *
     * @return this knot
     */
    public Knot3 mirrorHandlesForward() {

        this.rearHandle.set(
            this.coord.x - (this.foreHandle.x - this.coord.x),
            this.coord.y - (this.foreHandle.y - this.coord.y),
            this.coord.z - (this.foreHandle.z - this.coord.z));

        return this;
    }

    /**
     * Relocates the knot to a new location while maintaining the relationship
     * between the central coordinate and its two handles.
     *
     * @param v the location
     * @return this knot
     * @see Vec3#add(Vec3, Vec3, Vec3)
     * @see Vec3#sub(Vec3, Vec3, Vec3)
     */
    public Knot3 relocate(final Vec3 v) {

        Vec3.sub(this.foreHandle, this.coord, this.foreHandle);
        Vec3.sub(this.rearHandle, this.coord, this.rearHandle);
        this.coord.set(v);
        Vec3.add(this.foreHandle, this.coord, this.foreHandle);
        Vec3.add(this.rearHandle, this.coord, this.rearHandle);

        return this;
    }

    /**
     * Reverses the knot's direction by swapping the fore and rear handles.
     *
     * @return this knot
     */
    public Knot3 reverse() {

        final float tx = this.foreHandle.x;
        final float ty = this.foreHandle.y;
        final float tz = this.foreHandle.z;
        this.foreHandle.set(this.rearHandle);
        this.rearHandle.set(tx, ty, tz);

        return this;
    }

    /**
     * Rotates this knot around an axis by an angle in radians. The axis is
     * assumed to be of unit length. Accepts pre-calculated sine and cosine of
     * an angle, so that collections of knots can be efficiently rotated without
     * repeatedly calling cos and sin.
     *
     * @param cosa cosine of the angle
     * @param sina sine of the angle
     * @param axis the axis of rotation
     * @return this knot
     * @see Vec3#rotate(Vec3, float, float, Vec3, Vec3)
     */
    public Knot3 rotate(final float cosa, final float sina, final Vec3 axis) {

        Vec3.rotate(this.coord, cosa, sina, axis, this.coord);
        Vec3.rotate(this.foreHandle, cosa, sina, axis, this.foreHandle);
        Vec3.rotate(this.rearHandle, cosa, sina, axis, this.rearHandle);

        return this;
    }

    /**
     * Rotates this knot around an arbitrary axis by an angle in radians.
     *
     * @param radians the angle
     * @param axis    the axis of rotation
     * @return this knot
     */
    public Knot3 rotate(final float radians, final Vec3 axis) {

        final float cosa = (float) Math.cos(radians);
        final float sina = (float) Math.sin(radians);
        return this.rotate(cosa, sina, axis);
    }

    /**
     * Rotates this knot by a quaternion.
     *
     * @param q the quaternion
     * @return this knot
     * @see Quaternion#any(Quaternion)
     * @see Knot3#rotateUnchecked(Quaternion q)
     */
    public Knot3 rotate(final Quaternion q) {

        if (Quaternion.any(q)) {
            return this.rotateUnchecked(q);
        }

        return this;
    }

    /**
     * Rotates this knot's fore handle by a quaternion with its coordinate
     * serving as a pivot.
     *
     * @param q the quaternion
     * @return this knot
     * @see Vec3#sub(Vec3, Vec3, Vec3)
     * @see Quaternion#mulVector(Quaternion, Vec3, Vec3)
     * @see Vec3#add(Vec3, Vec3, Vec3)
     */
    public Knot3 rotateForeHandle(final Quaternion q) {

        Vec3.sub(this.foreHandle, this.coord, this.foreHandle);
        Quaternion.mulVector(q, this.foreHandle, this.foreHandle);
        Vec3.add(this.foreHandle, this.coord, this.foreHandle);

        return this;
    }

    /**
     * Rotates this knot's handles by a quaternion with its coordinate
     * serving as a pivot.
     *
     * @param q the quaternion
     * @return this knot
     * @see Knot3#rotateForeHandle(Quaternion)
     * @see Knot3#rotateRearHandle(Quaternion)
     */
    public Knot3 rotateHandles(final Quaternion q) {

        this.rotateForeHandle(q);
        this.rotateRearHandle(q);

        return this;
    }

    /**
     * Rotates this knot's rear handle by a quaternion with its coordinate
     * serving as a pivot.
     *
     * @param q the quaternion
     * @return this knot
     * @see Vec3#sub(Vec3, Vec3, Vec3)
     * @see Quaternion#mulVector(Quaternion, Vec3, Vec3)
     * @see Vec3#add(Vec3, Vec3, Vec3)
     */
    public Knot3 rotateRearHandle(final Quaternion q) {

        Vec3.sub(this.rearHandle, this.coord, this.rearHandle);
        Quaternion.mulVector(q, this.rearHandle, this.rearHandle);
        Vec3.add(this.rearHandle, this.coord, this.rearHandle);

        return this;
    }

    /**
     * Rotates this knot around the x axis by an angle in radians.
     *
     * @param radians the angle
     * @return this knot
     * @see Knot3#rotateX(float, float)
     */
    public Knot3 rotateX(final float radians) {

        final float cosa = (float) Math.cos(radians);
        final float sina = (float) Math.sin(radians);
        return this.rotateX(cosa, sina);
    }

    /**
     * Rotates this knot around the x axis. Accepts calculated sine and cosine
     * of an angle, so that collections of knots can be efficiently rotated
     * without repeatedly calling cos and sin.
     *
     * @param cosa cosine of the angle
     * @param sina sine of the angle
     * @return this knot
     * @see Vec3#rotateX(Vec3, float, float, Vec3)
     */
    public Knot3 rotateX(final float cosa, final float sina) {

        Vec3.rotateX(this.coord, cosa, sina, this.coord);
        Vec3.rotateX(this.foreHandle, cosa, sina, this.foreHandle);
        Vec3.rotateX(this.rearHandle, cosa, sina, this.rearHandle);

        return this;
    }

    /**
     * Rotates this knot around the y axis by an angle in radians.
     *
     * @param radians the angle
     * @return this knot
     * @see Knot3#rotateY(float, float)
     */
    public Knot3 rotateY(final float radians) {

        final float cosa = (float) Math.cos(radians);
        final float sina = (float) Math.sin(radians);
        return this.rotateY(cosa, sina);
    }

    /**
     * Rotates this knot around the y axis. Accepts calculated sine and cosine
     * of an angle, so that collections of knots can be efficiently rotated
     * without repeatedly calling cos and sin.
     *
     * @param cosa cosine of the angle
     * @param sina sine of the angle
     * @return this knot
     * @see Vec3#rotateY(Vec3, float, float, Vec3)
     */
    public Knot3 rotateY(final float cosa, final float sina) {

        Vec3.rotateY(this.coord, cosa, sina, this.coord);
        Vec3.rotateY(this.foreHandle, cosa, sina, this.foreHandle);
        Vec3.rotateY(this.rearHandle, cosa, sina, this.rearHandle);

        return this;
    }

    /**
     * Rotates this knot around the z axis by an angle in radians.
     *
     * @param radians the angle
     * @return this knot
     * @see Knot3#rotateZ(float, float)
     */
    public Knot3 rotateZ(final float radians) {

        final float cosa = (float) Math.cos(radians);
        final float sina = (float) Math.sin(radians);
        return this.rotateZ(cosa, sina);
    }

    /**
     * Rotates this knot around the z axis. Accepts calculated sine and cosine
     * of an angle, so that collections of knots can be efficiently rotated
     * without repeatedly calling cos and sin.
     *
     * @param cosa cosine of the angle
     * @param sina sine of the angle
     * @return this knot
     * @see Vec3#rotateZ(Vec3, float, float, Vec3)
     */
    public Knot3 rotateZ(final float cosa, final float sina) {

        Vec3.rotateZ(this.coord, cosa, sina, this.coord);
        Vec3.rotateZ(this.foreHandle, cosa, sina, this.foreHandle);
        Vec3.rotateZ(this.rearHandle, cosa, sina, this.rearHandle);

        return this;
    }

    /**
     * Scales this knot by a factor.
     *
     * @param scale the factor
     * @return this knot
     * @see Knot3#scaleUnchecked(float)
     */
    public Knot3 scale(final float scale) {

        if (scale != 0.0f) {
            return this.scaleUnchecked(scale);
        }

        return this;
    }

    /**
     * Scales this knot by a non-uniform scalar.
     *
     * @param scale the non-uniform scalar
     * @return this knot
     * @see Knot3#scaleUnchecked(Vec3)
     */
    public Knot3 scale(final Vec3 scale) {

        if (Vec3.all(scale)) {
            return this.scaleUnchecked(scale);
        }

        return this;
    }

    /**
     * Scales the fore handle by a factor.
     *
     * @param scalar the scalar
     * @return this knot
     */
    public Knot3 scaleForeHandleBy(final float scalar) {

        this.foreHandle.x = this.coord.x + scalar * (this.foreHandle.x - this.coord.x);
        this.foreHandle.y = this.coord.y + scalar * (this.foreHandle.y - this.coord.y);
        this.foreHandle.z = this.coord.z + scalar * (this.foreHandle.z - this.coord.z);

        return this;
    }

    /**
     * Scales the fore handle to a magnitude
     *
     * @param magnitude the magnitude
     * @return this knot
     * @see Vec3#add(Vec3, Vec3, Vec3)
     * @see Vec3#mul(Vec3, float, Vec3)
     * @see Vec3#subNorm(Vec3, Vec3, Vec3)
     */
    public Knot3 scaleForeHandleTo(final float magnitude) {

        Vec3.subNorm(this.foreHandle, this.coord, this.foreHandle);
        Vec3.mul(this.foreHandle, magnitude, this.foreHandle);
        Vec3.add(this.foreHandle, this.coord, this.foreHandle);

        return this;
    }

    /**
     * Scales both the fore and rear handle by a factor.
     *
     * @param scalar the scalar
     * @return this knot
     * @see Knot3#scaleForeHandleBy(float)
     * @see Knot3#scaleRearHandleBy(float)
     */
    public Knot3 scaleHandlesBy(final float scalar) {

        this.scaleForeHandleBy(scalar);
        this.scaleRearHandleBy(scalar);

        return this;
    }

    /**
     * Scales both the fore and rear handle to a magnitude.
     *
     * @param magnitude the magnitude
     * @return this knot
     * @see Knot3#scaleForeHandleTo(float)
     * @see Knot3#scaleRearHandleTo(float)
     */
    public Knot3 scaleHandlesTo(final float magnitude) {

        this.scaleForeHandleTo(magnitude);
        this.scaleRearHandleTo(magnitude);

        return this;
    }

    /**
     * Scales the rear handle by a factor.
     *
     * @param scalar the scalar
     * @return this knot
     */
    public Knot3 scaleRearHandleBy(final float scalar) {

        this.rearHandle.x = this.coord.x + scalar * (this.rearHandle.x - this.coord.x);
        this.rearHandle.y = this.coord.y + scalar * (this.rearHandle.y - this.coord.y);
        this.rearHandle.z = this.coord.z + scalar * (this.rearHandle.z - this.coord.z);

        return this;
    }

    /**
     * Scales the rear handle to a magnitude
     *
     * @param magnitude the magnitude
     * @return this knot
     * @see Vec3#add(Vec3, Vec3, Vec3)
     * @see Vec3#mul(Vec3, float, Vec3)
     * @see Vec3#subNorm(Vec3, Vec3, Vec3)
     */
    public Knot3 scaleRearHandleTo(final float magnitude) {

        Vec3.subNorm(this.rearHandle, this.coord, this.rearHandle);
        Vec3.mul(this.rearHandle, magnitude, this.rearHandle);
        Vec3.add(this.rearHandle, this.coord, this.rearHandle);

        return this;
    }

    /**
     * Sets the coordinate, fore and rear handles to the input coordinate.
     *
     * @param xCoord the x coordinate
     * @param yCoord the y coordinate
     * @param zCoord the z coordinate
     * @return this knot
     * @see Utils#copySign(float, float)
     */
    public Knot3 set(final float xCoord, final float yCoord, final float zCoord) {

        final float xOff = Utils.copySign(Utils.EPSILON, xCoord);
        final float yOff = Utils.copySign(Utils.EPSILON, yCoord);
        final float zOff = Utils.copySign(Utils.EPSILON, zCoord);

        return this.set(
            xCoord,
            yCoord,
            zCoord,
            xCoord + xOff,
            yCoord + yOff,
            zCoord + zOff,
            xCoord - xOff,
            yCoord - yOff,
            zCoord - zOff);
    }

    /**
     * Sets the knot's coordinate and fore handle. The rear handle is a mirror of
     * the fore handle.
     *
     * @param xCoord the x coordinate
     * @param yCoord the y coordinate
     * @param zCoord the z coordinate
     * @param xFore  the fore handle x
     * @param yFore  the fore handle y
     * @param zFore  the fore handle z
     * @return this knot
     */
    public Knot3 set(
        final float xCoord, final float yCoord, final float zCoord,
        final float xFore, final float yFore, final float zFore) {

        this.coord.set(xCoord, yCoord, zCoord);
        this.foreHandle.set(xFore, yFore, zFore);
        this.rearHandle.set(
            xCoord - (xFore - xCoord), yCoord - (yFore - yCoord), zCoord - (zFore - zCoord));

        return this;
    }

    /**
     * Sets the knot's coordinate, fore handle and rear handle by component.
     *
     * @param xCoord the x coordinate
     * @param yCoord the y coordinate
     * @param zCoord the z coordinate
     * @param xFore  the fore handle x
     * @param yFore  the fore handle y
     * @param zFore  the fore handle z
     * @param xRear  the rear handle x
     * @param yRear  the rear handle y
     * @param zRear  the rear handle z
     * @return this knot
     */
    public Knot3 set(
        final float xCoord, final float yCoord, final float zCoord,
        final float xFore, final float yFore, final float zFore,
        final float xRear, final float yRear, final float zRear) {

        this.coord.set(xCoord, yCoord, zCoord);
        this.foreHandle.set(xFore, yFore, zFore);
        this.rearHandle.set(xRear, yRear, zRear);

        return this;
    }

    /**
     * Sets this knot from a 2D source knot.<br>
     * <br>
     * For the purpose of promoting 2D curves to 3D.
     *
     * @param source the source
     * @return this knot
     */
    public Knot3 set(final Knot2 source) {

        return this.set(source.coord, source.foreHandle, source.rearHandle);
    }

    /**
     * Sets this knot from a source knot.
     *
     * @param source the source
     * @return this knot
     */
    public Knot3 set(final Knot3 source) {

        return this.set(source.coord, source.foreHandle, source.rearHandle);
    }

    /**
     * Sets this knot from a series of 2D vectors.<br>
     * <br>
     * For the purpose of promoting 2D curves to 3D.
     *
     * @param coord      the coordinate
     * @param foreHandle the fore handle
     * @param rearHandle the rear handle
     * @return this knot
     */
    public Knot3 set(final Vec2 coord, final Vec2 foreHandle, final Vec2 rearHandle) {

        this.coord.set(coord);
        this.foreHandle.set(foreHandle);
        this.rearHandle.set(rearHandle);

        return this;
    }

    /**
     * Sets the coordinate, fore and rear handles to the input coordinate.
     *
     * @param coord the coordinate
     * @return this knot
     */
    public Knot3 set(final Vec3 coord) {

        return this.set(coord.x, coord.y, coord.z);
    }

    /**
     * Sets the knot's coordinate and fore handle. The rear handle is a mirror of
     * the fore handle.
     *
     * @param coord      the coordinate
     * @param foreHandle the fore handle
     * @return this knot
     */
    public Knot3 set(final Vec3 coord, final Vec3 foreHandle) {

        return this.set(coord.x, coord.y, coord.z, foreHandle.x, foreHandle.y, foreHandle.z);
    }

    /**
     * Sets this knot from a series of vectors.
     *
     * @param coord      the coordinate
     * @param foreHandle the fore handle
     * @param rearHandle the rear handle
     * @return this knot
     */
    public Knot3 set(final Vec3 coord, final Vec3 foreHandle, final Vec3 rearHandle) {

        this.coord.set(coord);
        this.foreHandle.set(foreHandle);
        this.rearHandle.set(rearHandle);

        return this;
    }

    /**
     * Returns a string representation of this knot.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return this.toString(Utils.FIXED_PRINT);
    }

    /**
     * Returns a string representation of this knot.
     *
     * @param places the number of places
     * @return the string
     */
    public String toString(final int places) {

        return this.toString(new StringBuilder(512), places).toString();
    }

    /**
     * Multiplies this knot by a matrix.
     *
     * @param m the matrix
     * @return this knot
     * @see Mat4#mulPoint(Mat4, Vec3, Vec3)
     */
    public Knot3 transform(final Mat4 m) {

        Mat4.mulPoint(m, this.coord, this.coord);
        Mat4.mulPoint(m, this.foreHandle, this.foreHandle);
        Mat4.mulPoint(m, this.rearHandle, this.rearHandle);

        return this;
    }

    /**
     * Multiplies this knot by a transform.
     *
     * @param tr the transform
     * @return this knot
     * @see Transform3#mulPoint(Transform3, Vec3, Vec3)
     */
    public Knot3 transform(final Transform3 tr) {

        Transform3.mulPoint(tr, this.coord, this.coord);
        Transform3.mulPoint(tr, this.foreHandle, this.foreHandle);
        Transform3.mulPoint(tr, this.rearHandle, this.rearHandle);

        return this;
    }

    /**
     * Translates this knot by a vector.
     *
     * @param v the vector
     * @return this knot
     * @see Vec3#add(Vec3, Vec3, Vec3)
     */
    public Knot3 translate(final Vec3 v) {

        Vec3.add(this.coord, v, this.coord);
        Vec3.add(this.foreHandle, v, this.foreHandle);
        Vec3.add(this.rearHandle, v, this.rearHandle);

        return this;
    }

    /**
     * Rotates this knot by a quaternion. Does not check the quaternion for
     * validity.
     *
     * @param q the quaternion
     * @return this knot
     * @see Quaternion#mulVector(Quaternion, Vec3, Vec3)
     */
    Knot3 rotateUnchecked(final Quaternion q) {

        Quaternion.mulVector(q, this.coord, this.coord);
        Quaternion.mulVector(q, this.foreHandle, this.foreHandle);
        Quaternion.mulVector(q, this.rearHandle, this.rearHandle);

        return this;
    }

    /**
     * Scales this knot by a factor. Does not check scale for validity.
     *
     * @param scale the factor
     * @return this knot
     * @see Vec3#mul(Vec3, float, Vec3)
     */
    Knot3 scaleUnchecked(final float scale) {

        Vec3.mul(this.coord, scale, this.coord);
        Vec3.mul(this.foreHandle, scale, this.foreHandle);
        Vec3.mul(this.rearHandle, scale, this.rearHandle);

        return this;
    }

    /**
     * Scales this knot by a non-uniform scalar. Does not check scale for
     * validity.
     *
     * @param scale the non-uniform scalar
     * @return this knot
     * @see Vec3#hadamard(Vec3, Vec3, Vec3)
     */
    Knot3 scaleUnchecked(final Vec3 scale) {

        Vec3.hadamard(this.coord, scale, this.coord);
        Vec3.hadamard(this.foreHandle, scale, this.foreHandle);
        Vec3.hadamard(this.rearHandle, scale, this.rearHandle);

        return this;
    }

    /**
     * An internal helper function to format a vector as a Python tuple, then
     * append it to a {@link StringBuilder}. Used for testing purposes to
     * compare results with Blender 4.x.
     *
     * @param pyCd the string builder
     * @param tilt tilt
     */
    void toBlenderCode(final StringBuilder pyCd, final float tilt) {

        pyCd.append("{\"co\": ");
        this.coord.toBlenderCode(pyCd);
        pyCd.append(", \"handle_right\": ");
        this.foreHandle.toBlenderCode(pyCd);
        pyCd.append(", \"handle_left\": ");
        this.rearHandle.toBlenderCode(pyCd);
        pyCd.append(", \"weight\": ");
        Utils.toFixed(pyCd, 1.0f, 6);
        pyCd.append(", \"radius\": ");
        Utils.toFixed(pyCd, 1.0f, 6);
        pyCd.append(", \"tilt\": ");
        Utils.toFixed(pyCd, tilt, 6);
        pyCd.append('}');
    }

    /**
     * Internal helper function to assist with methods that need to print many
     * knots. Appends to an existing {@link StringBuilder}.
     *
     * @param sb     the string builder
     * @param places the number of places
     * @return the string builder
     */
    StringBuilder toString(final StringBuilder sb, final int places) {

        sb.append("{\"coord\":");
        this.coord.toString(sb, places);
        sb.append(",\"foreHandle\":");
        this.foreHandle.toString(sb, places);
        sb.append(",\"rearHandle\":");
        this.rearHandle.toString(sb, places);
        sb.append('}');
        return sb;
    }

    /**
     * Tests to see if this knot equals another.
     *
     * @param other the other knot
     * @return the evaluation
     */
    protected boolean equals(final Knot3 other) {

        return this.coord.equals(other.coord)
            && this.foreHandle.equals(other.foreHandle)
            && this.rearHandle.equals(other.rearHandle);
    }

    /**
     * An abstract class to facilitate the creation of knot easing functions.
     */
    public abstract static class AbstrEasing implements Utils.EasingFuncObj<Knot3> {

        /**
         * The default constructor.
         */
        protected AbstrEasing() {
        }

        /**
         * A clamped interpolation between the origin and destination. Defers
         * to an unclamped interpolation, which is to be defined by subclasses
         * of this class.
         *
         * @param orig   the origin knot
         * @param dest   the destination knot
         * @param step   a factor in [0.0, 1.0]
         * @param target the output knot
         * @return the eased knot
         */
        @Override
        public Knot3 apply(
            final Knot3 orig,
            final Knot3 dest,
            final Float step,
            final Knot3 target) {

            final float tf = step;
            if (Float.isNaN(tf)) {
                return this.applyUnclamped(orig, dest, 0.5f, target);
            }
            if (tf <= 0.0f) {
                return target.set(orig);
            }
            if (tf >= 1.0f) {
                return target.set(dest);
            }
            return this.applyUnclamped(orig, dest, tf, target);
        }

        /**
         * The interpolation to be defined by subclasses.
         *
         * @param origin the origin knot
         * @param dest   the destination knot
         * @param step   a factor in [0.0, 1.0]
         * @param target the output knot
         * @return the eased knot
         */
        public abstract Knot3 applyUnclamped(
            final Knot3 origin,
            final Knot3 dest,
            final float step,
            final Knot3 target);

        /**
         * Returns the simple name of this class.
         *
         * @return the string
         */
        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
    }

    /**
     * A functional class to ease between two knots with linear interpolation.
     */
    public static class Lerp extends AbstrEasing {

        /**
         * The default constructor.
         */
        public Lerp() {
        }

        /**
         * Eases between two knots by a step using the formula
         * (1.0 - t) * a + t * b .
         *
         * @param origin the origin knot
         * @param dest   the destination knot
         * @param step   the step
         * @param target the output knot
         * @return the eased knot
         */
        @Override
        public Knot3 applyUnclamped(
            final Knot3 origin,
            final Knot3 dest,
            final float step,
            final Knot3 target) {

            final float u = 1.0f - step;

            final Vec3 orCo = origin.coord;
            final Vec3 orFh = origin.foreHandle;
            final Vec3 orRh = origin.rearHandle;

            final Vec3 deCo = dest.coord;
            final Vec3 deFh = dest.foreHandle;
            final Vec3 deRh = dest.rearHandle;

            target.coord.set(
                u * orCo.x + step * deCo.x,
                u * orCo.y + step * deCo.y,
                u * orCo.z + step * deCo.z);
            target.foreHandle.set(
                u * orFh.x + step * deFh.x,
                u * orFh.y + step * deFh.y,
                u * orFh.z + step * deFh.z);
            target.rearHandle.set(
                u * orRh.x + step * deRh.x,
                u * orRh.y + step * deRh.y,
                u * orRh.z + step * deRh.z);

            return target;
        }
    }
}

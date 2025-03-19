package com.behreajj.camzup.core;

import java.util.Objects;

/**
 * An abstract parent for curve objects.
 */
public abstract class Curve extends EntityData implements ICurve {

    /**
     * Magnitude for orthogonal handles when four curve knots are used to
     * approximate an ellipse or
     * circle (90 degrees per knot). Derived from
     * <code>(Math.sqrt(2.0) - 1.0) * 4.0 / 3.0</code>.
     */
    public static final float KAPPA = 0.552285f;

    /**
     * Half the orthogonal handle magnitude for curve knots.
     */
    public static final float KAPPA_2 = Curve.KAPPA * 0.5f;

    /**
     * Magnitude for orthogonal handles when four curve knots are used to
     * approximate an ellipse or
     * circle (90 degrees per knot). Derived from
     * <code>(Math.sqrt(2.0) - 1.0) * 4.0 / 3.0</code>.
     */
    public static final double KAPPA_D = 0.5522847498307936d;

    /**
     * Half the orthogonal handle magnitude for curve knots.
     */
    public static final double KAPPA_2_D = Curve.KAPPA_D * 0.5d;

    /**
     * Default number of knots to expect when creating an array list in curves.
     */
    public static final int KNOT_CAPACITY = 8;

    /**
     * Default number of cubic Bezier knots used to approximate a circle.
     */
    public static final int KNOTS_PER_CIRCLE = 4;

    /**
     * Whether the curve is a closed loop.
     */
    public boolean closedLoop;

    /**
     * The material associated with this curve in a curve entity.
     */
    public int materialIndex = 0;

    /**
     * The default constructor.
     */
    protected Curve() {
        this.closedLoop = false;
    }

    /**
     * Constructs a curve and specifies whether it is a loop.
     *
     * @param cl the loop
     */
    protected Curve(final boolean cl) {
        this.closedLoop = cl;
    }

    /**
     * Constructs a curve and gives it a name.
     *
     * @param name the name
     */
    protected Curve(final String name) {

        super(name);
        this.closedLoop = false;
    }

    /**
     * Constructs a named curve and specifies whether it is a loop.
     *
     * @param name the name
     * @param cl   the loop
     */
    protected Curve(final String name, final boolean cl) {

        super(name);
        this.closedLoop = cl;
    }

    /**
     * Tests this curve for equivalence with an object.
     *
     * @param obj the object
     * @return the equivalence
     */
    @Override
    public boolean equals(final Object obj) {

        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || this.getClass() != obj.getClass()) {
            return false;
        }
        final Curve other = (Curve) obj;
        return this.closedLoop == other.closedLoop;
    }

    /**
     * Gets this curve's material index.
     *
     * @return the material index
     */
    public int getMaterialIndex() {
        return this.materialIndex;
    }

    /**
     * Sets this curve's material index.
     *
     * @param i the index
     * @return this curve
     */
    public Curve setMaterialIndex(final int i) {

        this.materialIndex = Math.max(i, 0);
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(closedLoop, materialIndex);
    }

    /**
     * Toggles whether this is a closed loop.
     *
     * @return this curve
     */
    @Override
    public Curve toggleLoop() {

        this.closedLoop = !this.closedLoop;
        return this;
    }

    /**
     * Returns a string representation of the curve.
     *
     * @return the string
     */
    @Override
    public String toString() {

        return "{\"name\":\""
            + this.name
            + "\",\"closedLoop\":"
            + this.closedLoop
            + ",\"materialIndex\":"
            + this.materialIndex
            + '}';
    }
}

package com.behreajj.camzup.core;

import java.util.Objects;

/**
 * Organizes the components of a 2D mesh into a group of coordinate and texture
 * coordinate such that
 * they can be edited together. This is not used by a mesh internally; it is
 * created upon retrieval
 * from a mesh. All of its components should be treated as references to data
 * within the mesh, not
 * as independent values.
 */
public class Vert2 implements Comparable<Vert2> {

    /**
     * The coordinate of the vertex in world space.
     */
    public Vec2 coord;

    /**
     * The texture (UV) coordinate for an image mapped onto the mesh.
     */
    public Vec2 texCoord;

    /**
     * The default constructor. When used, the vertex's coordinate, normal and
     * texCoord will remain
     * null.
     */
    public Vert2() {
    }

    /**
     * Constructs a vertex from a coordinate and texture coordinate.
     *
     * @param coord    the coordinate
     * @param texCoord the texture coordinate
     */
    public Vert2(final Vec2 coord, final Vec2 texCoord) {

        this.set(coord, texCoord);
    }

    /**
     * Tests to see if two vertices share the same coordinate according to the
     * default tolerance,
     * {@link Utils#EPSILON}.
     *
     * @param a the left comparisand
     * @param b the right comparisand
     * @return the evaluation
     */
    public static boolean approxCoord(final Vert2 a, final Vert2 b) {

        return Vert2.approxCoord(a, b, Utils.EPSILON);
    }

    /**
     * Tests to see if two vertices share the same coordinate according to a
     * tolerance.
     *
     * @param a         the left comparisand
     * @param b         the right comparisand
     * @param tolerance the tolerance
     * @return the evaluation
     * @see Vec2#approx(Vec2, Vec2, float)
     */
    public static boolean approxCoord(final Vert2 a, final Vert2 b, final float tolerance) {

        return a == b || Vec2.approx(a.coord, b.coord, tolerance);
    }

    /**
     * Compares this vertex to another.
     *
     * @param vert the comparisand
     * @return the comparison
     */
    @Override
    public int compareTo(final Vert2 vert) {

        final int tcComp = this.texCoord.compareTo(vert.texCoord);
        final int coComp = this.coord.compareTo(vert.coord);
        return tcComp != 0 ? tcComp : coComp;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Vert2 vert2))
            return false;
        return Objects.equals(coord, vert2.coord) && Objects.equals(texCoord, vert2.texCoord);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coord, texCoord);
    }

    /**
     * Sets the coordinate and texture coordinate of the vertex by reference.
     *
     * @param coord    the coordinate
     * @param texCoord the texture coordinate
     * @return this vertex
     */
    public Vert2 set(final Vec2 coord, final Vec2 texCoord) {

        this.coord = coord;
        this.texCoord = texCoord;
        return this;
    }

    /**
     * Returns a string representation of this vertex.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return this.toString(Utils.FIXED_PRINT);
    }

    /**
     * Returns a string representation of this vertex.
     *
     * @param places the number of places
     * @return the string
     */
    public String toString(final int places) {

        return this.toString(new StringBuilder(256), places).toString();
    }

    /**
     * Internal helper function to assist with methods that need to print many
     * vertices. Appends to an
     * existing {@link StringBuilder}.
     *
     * @param sb     the string builder
     * @param places the number of places
     * @return the string builder
     */
    StringBuilder toString(final StringBuilder sb, final int places) {

        sb.append("{\"coord\":");
        this.coord.toString(sb, places);
        sb.append(",\"texCoord\":");
        this.texCoord.toString(sb, places);
        sb.append('}');
        return sb;
    }

    /**
     * Tests this vertex for equivalence with another.
     *
     * @param vert2 the comparisand
     * @return the evaluation
     */
    protected boolean equals(final Vert2 vert2) {

        if (this.coord == null) {
            if (vert2.coord != null) {
                return false;
            }
        } else if (!this.coord.equals(vert2.coord)) {
            return false;
        }

        if (this.texCoord == null) {
            return vert2.texCoord == null;
        }
        return this.texCoord.equals(vert2.texCoord);
    }
}

package com.behreajj.camzup.core;

import java.util.*;

/**
 * Partitions space to improve collision and intersection tests. An octree node
 * holds a list of points up to a given capacity. When that capacity is
 * exceeded, the node is split into eight children nodes (octants) and its list
 * of points is emptied into them. The octants are indexed in an array as
 * follows:
 *
 * <pre>
 *           **|-----|-----|
 *       ******|   6 |   7 |
 *   **********| FNW | FNE |
 * |-----|-----|-----|-----|
 * |   2 |   3 |   4 |   5 |
 * | BNW | BNE | FSW | FSE |
 * |-----|-----|-----|-----|
 * |   0 |   1 |**********
 * | BSW | BSE |******
 * |-----|-----|**
 * </pre>
 *
 * <p>
 * forming a backwards z pattern.
 */
public class Octree {

    /**
     * Bottom North East index for array of children nodes.
     */
    public static final int BACK_NORTH_EAST = 3;

    /**
     * Bottom North West index for array of children nodes.
     */
    public static final int BACK_NORTH_WEST = 2;

    /**
     * Bottom South East index for array of children nodes.
     */
    public static final int BACK_SOUTH_EAST = 1;

    /**
     * Bottom South West index for array of children nodes.
     */
    public static final int BACK_SOUTH_WEST = 0;

    /**
     * Number of children held by a node.
     */
    public static final int CHILD_COUNT = 8;

    /**
     * The default point capacity.
     */
    public static final int DEFAULT_CAPACITY = 8;

    /**
     * Top North East index for array of children nodes.
     */
    public static final int FRONT_NORTH_EAST = 7;

    /**
     * Top North West index for array of children nodes.
     */
    public static final int FRONT_NORTH_WEST = 6;

    /**
     * Top South East index for array of children nodes.
     */
    public static final int FRONT_SOUTH_EAST = 5;

    /**
     * Top South West index for array of children nodes.
     */
    public static final int FRONT_SOUTH_WEST = 4;

    /**
     * The root level, or depth.
     */
    public static final int ROOT_LEVEL = 0;

    /**
     * The bounding volume.
     */
    protected final Bounds3 bounds;

    /**
     * Children nodes.
     */
    protected final Octree[] children = new Octree[Octree.CHILD_COUNT];

    /**
     * The depth, or level, of the octree node. The root node is at
     * {@value Octree#ROOT_LEVEL}.
     */
    protected final int level;

    /**
     * Elements contained by this octree if it is a leaf.
     */
    protected final TreeSet<Vec3> points;

    /**
     * The number of elements an octree can hold before it is split into child
     * nodes.
     */
    protected int capacity;

    /**
     * Constructs an octree node with a boundary.
     *
     * @param bounds the bounds
     */
    public Octree(final Bounds3 bounds) {

        this(bounds, Octree.DEFAULT_CAPACITY);
    }

    /**
     * Constructs an octree node with a boundary and capacity.
     *
     * @param bounds   the bounds
     * @param capacity the capacity
     */
    public Octree(final Bounds3 bounds, final int capacity) {

        this(bounds, capacity, Octree.ROOT_LEVEL);
    }

    /**
     * Constructs an octree from an array of points.
     *
     * @param points the points
     */
    public Octree(final Vec3[] points) {

        this(points, Octree.DEFAULT_CAPACITY);
    }

    /**
     * Constructs an octree from an array of points and a capacity per node.
     *
     * @param points   the points
     * @param capacity capacity per node
     * @see Bounds3#fromPoints(Vec3[], Bounds3)
     * @see Octree#insertAll(Vec3[])
     */
    public Octree(final Vec3[] points, final int capacity) {

        this.bounds = Bounds3.fromPoints(points, new Bounds3());
        this.capacity = Math.max(capacity, 1);
        this.level = Octree.ROOT_LEVEL;
        this.points = new TreeSet<>();
        this.insertAll(points);
    }

    /**
     * The default constructor. Creates a bounds of signed unit size.
     *
     * @see Bounds3#unitCubeSigned(Bounds3)
     */
    protected Octree() {

        /*
         * This no longer implements Comparable<Octree> (based on its bounds
         * center) because then it would also have to implement equals and
         * hashCode to fulfill the interface contract, and it's not clear how
         * two should be equated.
         */

        this(Bounds3.unitCubeSigned(new Bounds3()));
    }

    /**
     * Constructs an octree with a boundary and capacity. Protected so that the
     * level can be set.
     *
     * @param bounds   the bounds
     * @param capacity the capacity
     * @param level    the depth level
     */
    protected Octree(final Bounds3 bounds, final int capacity, final int level) {

        this.bounds = bounds;
        this.capacity = Math.max(capacity, 1);
        this.level = Math.max(level, Octree.ROOT_LEVEL);
        this.points = new TreeSet<>();
    }

    /**
     * Finds the average centers for each leaf node of this octree.
     *
     * @param o the octree
     * @return the centers
     */
    public static Vec3[] centersMean(final Octree o) {

        return Octree.centersMean(o, false);
    }

    /**
     * Finds the average centers for each leaf node of this octree. If the node
     * is empty, and includeEmpty is true, then the center of the cell bounds
     * is used instead.
     *
     * @param o            the octree
     * @param includeEmpty include empty cells
     * @return the centers
     */
    public static Vec3[] centersMean(final Octree o, final boolean includeEmpty) {

        final ArrayList<Vec3> results = new ArrayList<>(32);
        Octree.centersMean(o, includeEmpty, results);
        return results.toArray(new Vec3[0]);
    }

    /**
     * Queries the octree with a rectangular range, returning points inside the
     * range.
     *
     * @param o     the octree
     * @param range the range
     * @return the points
     * @see Bounds3#center(Bounds3, Vec3)
     */
    public static Vec3[] query(final Octree o, final Bounds3 range) {

        final TreeMap<Float, Vec3> found = new TreeMap<>();
        final Vec3 rCenter = new Vec3();
        Bounds3.center(range, rCenter);
        Octree.query(o, range, rCenter, 0, found);

        /* Copy by value, so references can't change. */
        final Collection<Vec3> values = found.values();
        final Iterator<Vec3> itr = values.iterator();
        final int len = values.size();
        final Vec3[] result = new Vec3[len];
        for (int i = 0; itr.hasNext(); ++i) {
            result[i] = new Vec3(itr.next());
        }
        return result;
    }

    /**
     * Queries the octree with a spherical range, returning points inside the
     * range.
     *
     * @param o      the octree
     * @param center the sphere center
     * @param radius the sphere radius
     * @return the points
     */
    public static Vec3[] query(final Octree o, final Vec3 center, final float radius) {

        final TreeMap<Float, Vec3> found = new TreeMap<>();
        Octree.query(o, center, radius * radius, 0, found);

        /* Copy by value, so references can't change. */
        final Collection<Vec3> values = found.values();
        final Iterator<Vec3> itr = values.iterator();
        final int len = values.size();
        final Vec3[] result = new Vec3[len];
        for (int i = 0; itr.hasNext(); ++i) {
            result[i] = new Vec3(itr.next());
        }
        return result;
    }

    /**
     * Finds the average center in each leaf node of this octree and appends it
     * to an array. If the node is empty, and includeEmpty is true, then the
     * center of the cell bounds is used instead.
     *
     * @param o            the octree
     * @param includeEmpty include empty cells
     * @param target       output array list
     * @see Bounds3#center(Bounds3, Vec3)
     */
    static void centersMean(
        final Octree o, final boolean includeEmpty, final ArrayList<Vec3> target) {

        /*
         * Even with a tree set, centersMedian is not worth it. Because this is
         * used by Pixels class to extract a palette, optimize where possible.
         */

        boolean isLeaf = true;
        for (int i = 0; i < Octree.CHILD_COUNT; ++i) {
            // QUERY: Should this offset its search by an index in the same
            // way that query does?
            final Octree child = o.children[i];
            if (child != null) {
                isLeaf = false;
                Octree.centersMean(child, includeEmpty, target);
            }
        }

        if (isLeaf) {
            final int ptsLen = o.points.size();
            if (ptsLen > 1) {
                float xSum = 0.0f;
                float ySum = 0.0f;
                float zSum = 0.0f;
                for (final Vec3 pt : o.points) {
                    xSum += pt.x;
                    ySum += pt.y;
                    zSum += pt.z;
                }
                final float dn = 1.0f / ptsLen;
                target.add(new Vec3(xSum * dn, ySum * dn, zSum * dn));
            } else if (ptsLen > 0) {
                target.add(new Vec3(o.points.iterator().next()));
            } else if (includeEmpty) {
                target.add(Bounds3.center(o.bounds, new Vec3()));
            }
        }

    }

    /**
     * Queries the octree with a box range. If points in the octree are in
     * range, they are added to a {@link java.util.TreeMap}. Each entry in the
     * map uses the Chebyshev distance as the key. Expects the range center to
     * be calculated in advance.
     *
     * @param range    the range
     * @param rCenter  the range center
     * @param startIdx the start index
     * @param found    the output list
     * @see Bounds3#containsInclusive(Bounds3, Vec3)
     * @see Bounds3#intersect(Bounds3, Bounds3)
     * @see Vec3#distChebyshev(Vec3, Vec3)
     */
    static void query(
        final Octree o,
        final Bounds3 range,
        final Vec3 rCenter,
        final int startIdx,
        final TreeMap<Float, Vec3> found) {

        if (Bounds3.intersect(range, o.bounds)) {
            boolean isLeaf = true;
            for (int i = 0; i < Octree.CHILD_COUNT; ++i) {
                final Octree child = o.children[(startIdx + i) % Octree.CHILD_COUNT];
                if (child != null) {
                    isLeaf = false;
                    Octree.query(child, range, rCenter, i, found);
                }
            }

            if (isLeaf) {
                for (final Vec3 point : o.points) {
                    if (Bounds3.containsInclusive(range, point)) {
                        found.put(Vec3.distChebyshev(point, rCenter), point);
                    }
                }
            }

        }
    }

    /**
     * Queries the octree with a spherical range. If points in the octree are
     * in range, they are added to a {@link java.util.TreeMap}. Each entry in
     * the map uses the squared Euclidean distance as the key.
     *
     * @param o        the octree
     * @param center   the sphere center
     * @param rsq      the sphere radius, squared
     * @param startIdx the start index
     * @param found    the output list
     * @see Bounds3#intersect(Bounds3, Vec3, float)
     * @see Vec3#distSq(Vec3, Vec3)
     */
    static void query(
        final Octree o,
        final Vec3 center,
        final float rsq,
        final int startIdx,
        final TreeMap<Float, Vec3> found) {

        if (Bounds3.intersectSq(o.bounds, center, rsq)) {
            boolean isLeaf = true;
            for (int i = 0; i < Octree.CHILD_COUNT; ++i) {
                final Octree child = o.children[(startIdx + i) % Octree.CHILD_COUNT];
                if (child != null) {
                    isLeaf = false;
                    Octree.query(child, center, rsq, i, found);
                }
            }

            if (isLeaf) {
                for (final Vec3 point : o.points) {
                    final float dsq = Vec3.distSq(center, point);
                    if (dsq < rsq) {
                        found.put(dsq, point);
                    }
                }
            }

        }
    }

    /**
     * Counts the number of leaves held by this octree. Returns 1 if this node
     * is itself a leaf.
     *
     * @return the sum
     */
    public int countLeaves() {

        int sum = 0;
        boolean isLeaf = true;
        for (int i = 0; i < Octree.CHILD_COUNT; ++i) {
            final Octree child = this.children[i];
            if (child != null) {
                isLeaf = false;
                sum += child.countLeaves();
            }
        }

        if (isLeaf) {
            return 1;
        }
        return sum;
    }

    /**
     * Counts the number of points held by this octree's leaf nodes.
     *
     * @return the sum
     */
    public int countPoints() {

        int sum = 0;
        boolean isLeaf = true;
        for (int i = 0; i < Octree.CHILD_COUNT; ++i) {
            final Octree child = this.children[i];
            if (child != null) {
                isLeaf = false;
                sum += child.countPoints();
            }
        }

        if (isLeaf) {
            sum += this.points.size();
        }
        return sum;
    }

    /**
     * Sets empty child nodes in the octree to null. Returns true if this
     * octree node should be culled; i.e., all its children are null, and it
     * contains no points.
     *
     * @return the evaluation
     */
    public boolean cull() {

        int cullThis = 0;
        for (int i = 0; i < Octree.CHILD_COUNT; ++i) {
            final Octree child = this.children[i];
            if (child != null) {
                if (child.cull()) {
                    this.children[i] = null;
                    ++cullThis;
                }
            } else {
                ++cullThis;
            }
        }

        return cullThis >= Octree.CHILD_COUNT && this.points.isEmpty();
    }

    /**
     * Gets the octree bounds.
     *
     * @param target the output bounds
     * @return the bounds
     */
    public Bounds3 getBounds(final Bounds3 target) {

        return target.set(this.bounds);
    }

    /**
     * Gets the capacity of the node, regardless of whether it is a leaf.
     *
     * @return the capacity
     */
    public int getCapacity() {
        return this.capacity;
    }

    /**
     * Sets the capacity of the node. If the node's point size exceeds the new
     * capacity, the node is split.
     *
     * @param capacity the new capacity
     * @see Octree#split()
     */
    public void setCapacity(final int capacity) {

        if (capacity > 0) {
            this.capacity = capacity;
            if (this.points.size() > this.capacity) {
                this.split(this.capacity);
            }
        }
    }

    /**
     * Gets the leaf nodes of an octree as a flat array. The nodes are passed
     * by reference, not by value.
     *
     * @return the octree array
     * @see Octree#getLeaves(ArrayList)
     */
    public Octree[] getLeaves() {

        final ArrayList<Octree> references = new ArrayList<>(32);
        this.getLeaves(references);
        return references.toArray(new Octree[0]);
    }

    /**
     * Gets the level, or depth, of the node.
     *
     * @return the level
     */
    public int getLevel() {
        return this.level;
    }

    /**
     * Gets the maximum level, or depth, of the node and its children.
     *
     * @return the level
     */
    public int getMaxLevel() {

        int mxLvl = this.level;
        for (int i = 0; i < Octree.CHILD_COUNT; ++i) {
            final Octree child = this.children[i];
            if (child != null) {
                final int lvl = child.getMaxLevel();
                if (lvl > mxLvl) {
                    mxLvl = lvl;
                }
            }
        }
        return mxLvl;
    }

    /**
     * Gets an array of points contained in this octree and its children. The
     * points are transferred to the array by value, not reference, so changing
     * them in the array will not change them within the tree.
     *
     * @return the points array
     * @see Octree#getPoints(ArrayList)
     */
    public Vec3[] getPoints() {

        final ArrayList<Vec3> references = new ArrayList<>(Octree.DEFAULT_CAPACITY);
        this.getPoints(references);
        final int len = references.size();
        final Vec3[] result = new Vec3[len];
        final Iterator<Vec3> itr = references.iterator();
        for (int i = 0; itr.hasNext(); ++i) {
            result[i] = new Vec3(itr.next());
        }
        return result;
    }

    /**
     * Inserts a point into the octree by reference. Returns <code>true</code>
     * if the point was successfully inserted into the octree directly or
     * indirectly through one of its children; returns <code>false</code> if
     * the insertion was unsuccessful.
     *
     * @param point the point
     * @return the insertion success
     * @see Bounds3#contains(Bounds3, Vec3)
     */
    public boolean insert(final Vec3 point) {

        /* Lower bound inclusive, upper bound exclusive. */
        if (Bounds3.contains(this.bounds, point)) {
            boolean isLeaf = true;
            for (int i = 0; i < Octree.CHILD_COUNT; ++i) {
                final Octree child = this.children[i];
                if (child != null) {
                    isLeaf = false;
                    if (child.insert(point)) {
                        return true;
                    }
                }
            }

            if (!isLeaf) {
                /* Case where a child has been culled. Try again. */
                this.split(this.capacity);
                return this.insert(point);
            }
            this.points.add(point);
            if (this.points.size() > this.capacity) {
                this.split(this.capacity);
            }
            return true;
        }
        return false;
    }

    /**
     * Inserts points into the octree. Returns <code>true</code> if all
     * insertions were successful; <code>false</code> if at least one was
     * unsuccessful.
     *
     * @param pts the points
     * @return the insertion success
     */
    public boolean insertAll(final Vec3[] pts) {

        boolean flag = true;
        for (Vec3 pt : pts) {
            flag &= this.insert(pt);
        }
        return flag;
    }

    /**
     * Evaluates whether this octree node has any children. Returns true if no;
     * otherwise false.
     *
     * @return the evaluation
     */
    public boolean isLeaf() {

        for (int i = 0; i < Octree.CHILD_COUNT; ++i) {
            if (this.children[i] != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * If this octree node has children, converts it to a leaf node containing
     * the centers of its children. Increases node capacity if necessary.
     *
     * @return this octree
     */
    public Octree merge() {

        if (this.isLeaf()) {
            return this;
        }

        this.points.clear();

        final ArrayList<Vec3> pts = new ArrayList<>(Octree.DEFAULT_CAPACITY);
        for (int i = 0; i < Octree.CHILD_COUNT; ++i) {
            final Octree child = this.children[i];
            if (child != null) {
                child.getPoints(pts);
                final int ptsLen = pts.size();
                if (ptsLen > 0) {
                    final Vec3 center = new Vec3();
                    for (Vec3 pt : pts) {
                        Vec3.add(center, pt, center);
                    }
                    Vec3.mul(center, 1.0f / ptsLen, center);
                    this.points.add(center);
                    pts.clear();
                }
                this.children[i] = null;
            }
        }

        final int pointsLen = this.points.size();
        if (pointsLen > this.capacity) {
            this.capacity = pointsLen;
        }

        return this;
    }

    /**
     * Resets this tree to an initial state, where its has no children and no
     * points. Leaves the tree's bounds unchanged.
     *
     * @return this octree
     */
    public Octree reset() {

        this.points.clear();
        Arrays.fill(this.children, null);

        return this;
    }

    /**
     * Resets this tree to an initial state, where its has no children and no
     * points. Sets the bounds to the one supplied.
     *
     * @param b bounds
     * @return this octree
     */
    public Octree reset(final Bounds3 b) {

        this.bounds.set(b);
        return this.reset();
    }

    /**
     * Subdivides this octree.
     *
     * @return this octree
     */
    public Octree subdivide() {

        return this.subdivide(1);
    }

    /**
     * Subdivides this octree
     *
     * @param iterations iteration count
     * @return this octree
     */
    public Octree subdivide(final int iterations) {

        return this.subdivide(iterations, this.capacity);
    }

    /**
     * Subdivides this octree. For cases where a minimum number of children
     * nodes is desired, independent of point insertion. The result will be
     * {@value Octree#CHILD_COUNT} raised to the power of iterations, e.g., 8,
     * 64, 512.
     *
     * @param iterations    iteration count
     * @param childCapacity child capacity
     * @return this octree
     * @see Octree#split(int)
     */
    public Octree subdivide(final int iterations, final int childCapacity) {

        if (iterations < 1) {
            return this;
        }

        for (int i = 0; i < iterations; ++i) {
            boolean isLeaf = true;
            for (int j = 0; j < Octree.CHILD_COUNT; ++j) {
                final Octree child = this.children[j];
                if (child != null) {
                    isLeaf = false;
                    child.subdivide(iterations - 1, childCapacity);
                }
            }

            if (isLeaf) {
                this.split(childCapacity);
            }
        }

        return this;
    }

    /**
     * Gets a string representation of this octree node.
     */
    @Override
    public String toString() {
        return this.toString(Utils.FIXED_PRINT);
    }

    /**
     * Gets a string representation of this octree node.
     *
     * @param places number of decimal places
     * @return the string
     */
    public String toString(final int places) {

        return this.toString(new StringBuilder(1024), places).toString();
    }

    /**
     * Finds the total capacity of a node, including the cumulative capacities
     * of its children.
     *
     * @return the sum
     */
    public int totalCapacity() {

        // TODO: TEST
        int sum = 0;
        boolean isLeaf = true;
        for (int i = 0; i < Octree.CHILD_COUNT; ++i) {
            final Octree child = this.children[i];
            if (child != null) {
                isLeaf = false;
                sum += child.totalCapacity();
            }
        }

        if (isLeaf) {
            sum += this.capacity;
        }
        return sum;
    }

    /**
     * Gets the leaf nodes in this node and its children. The leaf nodes will
     * still be stored by reference in the output array list, so this should be
     * used internally.
     *
     * @param target the output array list
     * @return the leaf nodes
     */
    protected ArrayList<Octree> getLeaves(final ArrayList<Octree> target) {

        boolean isLeaf = true;
        for (int i = 0; i < Octree.CHILD_COUNT; ++i) {
            final Octree child = this.children[i];
            if (child != null) {
                isLeaf = false;
                child.getLeaves(target);
            }
        }
        if (isLeaf) {
            target.add(this);
        }
        return target;
    }

    /**
     * Gets the points in this octree node and its children. The points will
     * still be stored by reference in the output array list, so this should be
     * used internally.
     *
     * @param target the output array list
     * @return the points
     */
    protected ArrayList<Vec3> getPoints(final ArrayList<Vec3> target) {

        boolean isLeaf = true;
        for (int i = 0; i < Octree.CHILD_COUNT; ++i) {
            final Octree child = this.children[i];
            if (child != null) {
                isLeaf = false;
                child.getPoints(target);
            }
        }
        if (isLeaf) {
            target.addAll(this.points);
        }
        return target;
    }

    /**
     * Splits this octree node into eight child nodes. The child capacity is
     * the same as the parent's.
     *
     * @return this octree
     */
    protected Octree split() {
        return this.split(this.capacity);
    }

    /**
     * Splits this octree node into eight child nodes.
     *
     * @param childCapacity child capacity
     * @return this octree
     * @see Bounds3#split(Bounds3, float, float, float, Bounds3, Bounds3, Bounds3,
     * Bounds3, Bounds3, Bounds3, Bounds3, Bounds3)
     */
    protected Octree split(final int childCapacity) {

        final int nextLevel = this.level + 1;
        for (int i = 0; i < Octree.CHILD_COUNT; ++i) {
            final Octree child = this.children[i];
            if (child != null) {
                /* Bounds will be set by the split method. Level is final. */
                child.setCapacity(childCapacity);
            } else {
                this.children[i] = new Octree(new Bounds3(), childCapacity, nextLevel);
            }
        }

        Bounds3.split(
            this.bounds,
            0.5f,
            0.5f,
            0.5f,
            this.children[Octree.BACK_SOUTH_WEST].bounds,
            this.children[Octree.BACK_SOUTH_EAST].bounds,
            this.children[Octree.BACK_NORTH_WEST].bounds,
            this.children[Octree.BACK_NORTH_EAST].bounds,
            this.children[Octree.FRONT_SOUTH_WEST].bounds,
            this.children[Octree.FRONT_SOUTH_EAST].bounds,
            this.children[Octree.FRONT_NORTH_WEST].bounds,
            this.children[Octree.FRONT_NORTH_EAST].bounds);

        int idxOffset = 0;
        for (final Vec3 v : this.points) {
            boolean found = false;
            for (int j = 0; !found && j < Octree.CHILD_COUNT; ++j) {
                final int k = (idxOffset + j) % Octree.CHILD_COUNT;
                found = this.children[k].insert(v);
                if (found) {
                    idxOffset = k;
                }
            }
        }
        this.points.clear();

        return this;
    }

    /**
     * Internal helper function to append an octree's representation to a
     * {@link StringBuilder}.
     *
     * @param sb     the string builder
     * @param places the number of places
     * @return the string builder
     */
    protected StringBuilder toString(final StringBuilder sb, final int places) {

        sb.append("{\"bounds\":");
        this.bounds.toString(sb, places);
        sb.append(",\"capacity\":");
        sb.append(this.capacity);

        if (this.isLeaf()) {
            sb.append(",\"points\":[");
            final Iterator<Vec3> itr = this.points.iterator();
            while (itr.hasNext()) {
                itr.next().toString(sb, places);
                if (itr.hasNext()) {
                    sb.append(',');
                }
            }
        } else {
            sb.append(",\"children\":[");
            for (int i = 0; i < Octree.CHILD_COUNT - 1; ++i) {
                final Octree child = this.children[i];
                if (child != null) {
                    child.toString(sb, places);
                    sb.append(',');
                } else {
                    sb.append("null,");
                }
            }

            final Octree last = this.children[Octree.CHILD_COUNT - 1];
            if (last != null) {
                sb.append(last.toString(places));
            } else {
                sb.append("null");
            }
        }
        sb.append(']');

        sb.append('}');
        return sb;
    }
}

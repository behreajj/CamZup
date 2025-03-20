package com.behreajj.camzup.core;

import java.util.*;

/**
 * Partitions space to improve collision and intersection tests. A quadtree node
 * holds a list of
 * points up to a given capacity. When that capacity is exceeded, the node is
 * split into four
 * children nodes (quadrants) and its list of points is emptied into them. The
 * quadrants are indexed
 * in an array as follows
 *
 * <pre>
 * |----|----|
 * |  2 |  3 |
 * | NW | NE |
 * |----|----|
 * |  0 |  1 |
 * | SW | SE |
 * |----|----|
 * </pre>
 *
 * <p>
 * forming a backwards z pattern.
 */
public class Quadtree {

    /**
     * Number of children held by a node.
     */
    public static final int CHILD_COUNT = 4;

    /**
     * The default point capacity.
     */
    public static final int DEFAULT_CAPACITY = 8;

    /**
     * North East index for array of children nodes.
     */
    public static final int NORTH_EAST = 3;

    /**
     * North West index for array of children nodes.
     */
    public static final int NORTH_WEST = 2;

    /**
     * The root level, or depth.
     */
    public static final int ROOT_LEVEL = 0;

    /**
     * South East index for array of children nodes.
     */
    public static final int SOUTH_EAST = 1;

    /**
     * South West index for array of children nodes.
     */
    public static final int SOUTH_WEST = 0;

    /**
     * The bounding area.
     */
    protected final Bounds2 bounds;

    /**
     * Children nodes.
     */
    protected final Quadtree[] children = new Quadtree[Quadtree.CHILD_COUNT];

    /**
     * The depth, or level, of the quadtree node. The root node is at
     * {@value Quadtree#ROOT_LEVEL}.
     */
    protected final int level;

    /**
     * Elements contained by this quadtree if it is a leaf.
     */
    protected final TreeSet<Vec2> points;

    /**
     * The number of elements a quadtree can hold before it is split into child
     * nodes.
     */
    protected int capacity;

    /**
     * Constructs a quadtree node with a boundary.
     *
     * @param bounds the bounds
     */
    public Quadtree(final Bounds2 bounds) {

        this(bounds, Quadtree.DEFAULT_CAPACITY);
    }

    /**
     * Constructs a quadtree node with a boundary and capacity.
     *
     * @param bounds   the bounds
     * @param capacity the capacity
     */
    public Quadtree(final Bounds2 bounds, final int capacity) {

        this(bounds, capacity, Quadtree.ROOT_LEVEL);
    }

    /**
     * Constructs a quadtree from an array of points.
     *
     * @param points the points
     */
    public Quadtree(final Vec2[] points) {

        this(points, Quadtree.DEFAULT_CAPACITY);
    }

    /**
     * Constructs a quadtree from an array of points and a capacity per node.
     *
     * @param points   the points
     * @param capacity capacity per node
     * @see Bounds2#fromPoints(Vec2[], Bounds2)
     * @see Quadtree#insertAll(Vec2[])
     */
    public Quadtree(final Vec2[] points, final int capacity) {

        this.bounds = Bounds2.fromPoints(points, new Bounds2());
        this.capacity = Math.max(capacity, 1);
        this.level = Quadtree.ROOT_LEVEL;
        this.points = new TreeSet<>();
        this.insertAll(points);
    }

    /**
     * The default constructor. Creates a bounds of signed unit size.
     *
     * @see Bounds2#unitSquareSigned(Bounds2)
     */
    protected Quadtree() {

        /*
         * This no longer implements Comparable<Quadtree> (based on its bounds
         * center) because then it would also have to implement equals and
         * hashCode to fulfill the interface contract, and it's not clear how two
         * should be equated.
         */

        this(Bounds2.unitSquareSigned(new Bounds2()));
    }

    /**
     * Constructs a quadtree with a boundary and capacity. Protected so that the
     * level can be set.
     *
     * @param bounds   the bounds
     * @param capacity the capacity
     * @param level    the depth level
     */
    protected Quadtree(final Bounds2 bounds, final int capacity, final int level) {

        this.bounds = bounds;
        this.capacity = Math.max(capacity, 1);
        this.level = Math.max(level, Quadtree.ROOT_LEVEL);
        this.points = new TreeSet<>();
    }

    /**
     * Finds the average center in each leaf node of this quadtree.
     *
     * @param q the quadtree
     * @return the centers
     */
    public static Vec2[] centersMean(final Quadtree q) {

        return Quadtree.centersMean(q, false);
    }

    /**
     * Finds the average center in each leaf node of this quadtree. If the node is
     * empty, and
     * includeEmpty is true, then the center of the cell bounds is used instead.
     *
     * @param q            the quadtree
     * @param includeEmpty include empty cells
     * @return the centers
     */
    public static Vec2[] centersMean(final Quadtree q, final boolean includeEmpty) {

        final ArrayList<Vec2> results = new ArrayList<>(32);
        Quadtree.centersMean(q, includeEmpty, results);
        return results.toArray(new Vec2[0]);
    }

    /**
     * Queries the quadtree with a rectangular range, returning points inside the
     * range.
     *
     * @param q     the quadtree
     * @param range the range
     * @return the points
     * @see Bounds2#center(Bounds2, Vec2)
     */
    public static Vec2[] query(final Quadtree q, final Bounds2 range) {

        final TreeMap<Float, Vec2> found = new TreeMap<>();
        final Vec2 rCenter = new Vec2();
        Bounds2.center(range, rCenter);
        Quadtree.query(q, range, rCenter, 0, found);

        /* Copy by value, so references can't change. */
        final Collection<Vec2> values = found.values();
        final Iterator<Vec2> itr = values.iterator();
        final int len = values.size();
        final Vec2[] result = new Vec2[len];
        for (int i = 0; itr.hasNext(); ++i) {
            result[i] = new Vec2(itr.next());
        }
        return result;
    }

    /**
     * Queries the quadtree with a circular range, returning points inside the
     * range.
     *
     * @param q      the quadtree
     * @param center the circle center
     * @param radius the circle radius
     * @return the points
     */
    public static Vec2[] query(final Quadtree q, final Vec2 center, final float radius) {

        final TreeMap<Float, Vec2> found = new TreeMap<>();
        Quadtree.query(q, center, radius * radius, 0, found);

        /* Copy by value, so references can't change. */
        final Collection<Vec2> values = found.values();
        final Iterator<Vec2> itr = values.iterator();
        final int len = values.size();
        final Vec2[] result = new Vec2[len];
        for (int i = 0; itr.hasNext(); ++i) {
            result[i] = new Vec2(itr.next());
        }
        return result;
    }

    /**
     * Finds the average center in each leaf node of this quadtree and appends it to
     * an array. If the
     * node is empty, and includeEmpty is true, then the center of the cell bounds
     * is used instead.
     *
     * @param q            the quadtree
     * @param includeEmpty include empty cells
     * @param target       output array list
     * @see Bounds2#center(Bounds2, Vec2)
     */
    static void centersMean(
        final Quadtree q, final boolean includeEmpty, final ArrayList<Vec2> target) {

        boolean isLeaf = true;
        for (int i = 0; i < Quadtree.CHILD_COUNT; ++i) {
            final Quadtree child = q.children[i];
            if (child != null) {
                isLeaf = false;
                Quadtree.centersMean(child, includeEmpty, target);
            }
        }

        if (isLeaf) {
            final int ptsLen = q.points.size();
            if (ptsLen > 1) {
                float xSum = 0.0f;
                float ySum = 0.0f;
                for (final Vec2 pt : q.points) {
                    xSum += pt.x;
                    ySum += pt.y;
                }
                final float dn = 1.0f / ptsLen;
                target.add(new Vec2(xSum * dn, ySum * dn));
            } else if (ptsLen > 0) {
                target.add(new Vec2(q.points.iterator().next()));
            } else if (includeEmpty) {
                target.add(Bounds2.center(q.bounds, new Vec2()));
            }
        }

    }

    /**
     * Queries the quadtree with a rectangular range. If points in the quadtree are
     * in range, they are
     * added to a {@link java.util.TreeMap}. Each entry in the map uses the
     * Chebyshev distance as the
     * key. Expects the range center to be calculated in advance.
     *
     * @param q        the quadtree
     * @param range    the bounds
     * @param rCenter  the range center
     * @param startIdx the start index
     * @param found    the output list
     * @see Bounds2#containsInclusive(Bounds2, Vec2)
     * @see Bounds2#intersect(Bounds2, Bounds2)
     * @see Vec2#distChebyshev(Vec2, Vec2)
     */
    static void query(
        final Quadtree q,
        final Bounds2 range,
        final Vec2 rCenter,
        final int startIdx,
        final TreeMap<Float, Vec2> found) {

        if (Bounds2.intersect(range, q.bounds)) {
            boolean isLeaf = true;
            for (int i = 0; i < Quadtree.CHILD_COUNT; ++i) {
                final Quadtree child = q.children[(startIdx + i) % Quadtree.CHILD_COUNT];
                if (child != null) {
                    isLeaf = false;
                    Quadtree.query(child, range, rCenter, i, found);
                }
            }

            if (isLeaf) {
                for (final Vec2 point : q.points) {
                    if (Bounds2.containsInclusive(range, point)) {
                        found.put(Vec2.distChebyshev(point, rCenter), point);
                    }
                }
            }

        }
    }

    /**
     * Queries the quadtree with a circular range. If points in the quadtree are in
     * range, they are
     * added to a {@link java.util.TreeMap}. Each entry in the map uses the squared
     * Euclidean distance
     * as the key.
     *
     * @param q        the quadtree
     * @param center   the circle center
     * @param rsq      the circle radius, squared
     * @param startIdx the start index
     * @param found    the output list
     * @see Bounds2#intersect(Bounds2, Vec2, float)
     * @see Vec2#distSq(Vec2, Vec2)
     */
    static void query(
        final Quadtree q,
        final Vec2 center,
        final float rsq,
        final int startIdx,
        final TreeMap<Float, Vec2> found) {

        if (Bounds2.intersectSq(q.bounds, center, rsq)) {
            boolean isLeaf = true;
            for (int i = 0; i < Quadtree.CHILD_COUNT; ++i) {
                final Quadtree child = q.children[(startIdx + i) % Quadtree.CHILD_COUNT];
                if (child != null) {
                    isLeaf = false;
                    Quadtree.query(child, center, rsq, i, found);
                }
            }

            if (isLeaf) {
                for (final Vec2 point : q.points) {
                    final float dsq = Vec2.distSq(center, point);
                    if (dsq < rsq) {
                        found.put(dsq, point);
                    }
                }
            }

        }
    }

    /**
     * Counts the number of leaves held by this quadtree. Returns 1 if this node is
     * itself a leaf.
     *
     * @return the sum
     */
    public int countLeaves() {

        int sum = 0;
        boolean isLeaf = true;
        for (int i = 0; i < Quadtree.CHILD_COUNT; ++i) {
            final Quadtree child = this.children[i];
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
     * Counts the number of points held by leaf nodes.
     *
     * @return the sum
     */
    public int countPoints() {

        int sum = 0;
        boolean isLeaf = true;
        for (int i = 0; i < Quadtree.CHILD_COUNT; ++i) {
            final Quadtree child = this.children[i];
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
     * Sets empty child nodes in the quadtree to null. Returns true if this quadtree
     * node should be
     * culled; i.e., all its children are null, and it contains no points.
     *
     * @return the evaluation
     */
    public boolean cull() {

        int cullThis = 0;
        for (int i = 0; i < Quadtree.CHILD_COUNT; ++i) {
            final Quadtree child = this.children[i];
            if (child != null) {
                if (child.cull()) {
                    this.children[i] = null;
                    ++cullThis;
                }
            } else {
                ++cullThis;
            }
        }

        return cullThis >= Quadtree.CHILD_COUNT && this.points.isEmpty();
    }

    /**
     * Gets the quadtree bounds.
     *
     * @param target the output bounds
     * @return the bounds
     */
    public Bounds2 getBounds(final Bounds2 target) {

        return target.set(this.bounds);
    }

    /**
     * Gets the local capacity of the node, regardless of whether it is a leaf.
     *
     * @return the capacity
     */
    public int getCapacity() {
        return this.capacity;
    }

    /**
     * Sets the capacity of the node. If the node's point size exceeds the new
     * capacity, the node is
     * split.
     *
     * @param capacity the new capacity
     * @see Quadtree#split(int)
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
     * Gets the leaf nodes of a quadtree as a flat array. The nodes are passed by
     * reference, not by
     * value.
     *
     * @return the quadtree array
     * @see Quadtree#getLeaves(ArrayList)
     */
    public Quadtree[] getLeaves() {

        final ArrayList<Quadtree> references = new ArrayList<>(32);
        this.getLeaves(references);
        return references.toArray(new Quadtree[0]);
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
        for (int i = 0; i < Quadtree.CHILD_COUNT; ++i) {
            final Quadtree child = this.children[i];
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
     * Gets an array of points contained in this quadtree and its children. The
     * points are transferred
     * to the array by value, not reference, so changing them in the array will not
     * change them within
     * the tree.
     *
     * @return the points array
     * @see Quadtree#getPoints(ArrayList)
     */
    public Vec2[] getPoints() {

        final ArrayList<Vec2> references = new ArrayList<>(Quadtree.DEFAULT_CAPACITY);
        this.getPoints(references);
        final int len = references.size();
        final Vec2[] result = new Vec2[len];
        final Iterator<Vec2> itr = references.iterator();
        for (int i = 0; itr.hasNext(); ++i) {
            result[i] = new Vec2(itr.next());
        }
        return result;
    }

    /**
     * Inserts a point into the quadtree by reference. Returns <code>true</code> if
     * the point was
     * successfully inserted into the quadtree directly or indirectly through one of
     * its children;
     * returns <code>false</code> if the insertion was unsuccessful.
     *
     * @param point the point
     * @return the insertion success
     * @see Bounds2#contains(Bounds2, Vec2)
     */
    public boolean insert(final Vec2 point) {

        /* Lower bound inclusive, upper bound exclusive. */
        if (Bounds2.contains(this.bounds, point)) {
            boolean isLeaf = true;
            for (int i = 0; i < Quadtree.CHILD_COUNT; ++i) {
                final Quadtree child = this.children[i];
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
     * Inserts points into the quadtree. Returns <code>true</code> if all insertions
     * were successful;
     * <code>false</code> if at least one was unsuccessful.
     *
     * @param pts the points
     * @return the insertion success
     */
    public boolean insertAll(final Vec2[] pts) {

        boolean flag = true;
        for (Vec2 pt : pts) {
            flag &= this.insert(pt);
        }
        return flag;
    }

    /**
     * Evaluates whether this quadtree node has any children. Returns true if no;
     * otherwise false.
     *
     * @return the evaluation
     */
    public boolean isLeaf() {

        for (int i = 0; i < Quadtree.CHILD_COUNT; ++i) {
            if (this.children[i] != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * If this quadtree node has children, converts it to a leaf node containing the
     * centers of its
     * children. Increases node capacity if necessary.
     *
     * @return this quadtree
     */
    public Quadtree merge() {

        if (this.isLeaf()) {
            return this;
        }

        this.points.clear();

        final ArrayList<Vec2> pts = new ArrayList<>(Quadtree.DEFAULT_CAPACITY);
        for (int i = 0; i < Quadtree.CHILD_COUNT; ++i) {
            final Quadtree child = this.children[i];
            if (child != null) {
                child.getPoints(pts);
                final int ptsLen = pts.size();
                if (ptsLen > 0) {
                    final Vec2 center = new Vec2();
                    for (Vec2 pt : pts) {
                        Vec2.add(center, pt, center);
                    }
                    Vec2.mul(center, 1.0f / ptsLen, center);
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
     * points. Leaves the
     * tree's bounds unchanged.
     *
     * @return this quadtree
     */
    public Quadtree reset() {

        this.points.clear();
        Arrays.fill(this.children, null);

        return this;
    }

    /**
     * Resets this tree to an initial state, where its has no children and no
     * points. Sets the bounds
     * to the one supplied.
     *
     * @param b bounds
     * @return this quadtree
     */
    public Quadtree reset(final Bounds2 b) {

        this.bounds.set(b);
        return this.reset();
    }

    /**
     * Subdivides this quadtree.
     *
     * @return this quadtree
     */
    public Quadtree subdivide() {

        return this.subdivide(1);
    }

    /**
     * Subdivides this quadtree.
     *
     * @param iterations iteration count
     * @return this quadtree
     */
    public Quadtree subdivide(final int iterations) {

        return this.subdivide(iterations, this.capacity);
    }

    /**
     * Subdivides this quadtree. For cases where a minimum number of children nodes
     * is desired,
     * independent of point insertion. The result will be
     * {@value Quadtree#CHILD_COUNT} raised to the
     * power of iterations, e.g., 4, 16, 64, 256.
     *
     * @param iterations    iteration count
     * @param childCapacity child capacity
     * @return this quadtree
     * @see Quadtree#split(int)
     */
    public Quadtree subdivide(final int iterations, final int childCapacity) {

        if (iterations < 1) {
            return this;
        }

        for (int i = 0; i < iterations; ++i) {
            boolean isLeaf = true;
            for (int j = 0; j < Quadtree.CHILD_COUNT; ++j) {
                final Quadtree child = this.children[j];
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
     * Gets a string representation of this quadtree node.
     */
    @Override
    public String toString() {
        return this.toString(Utils.FIXED_PRINT);
    }

    /**
     * Gets a string representation of this quadtree node.
     *
     * @param places number of decimal places
     * @return the string
     */
    public String toString(final int places) {

        return this.toString(new StringBuilder(1024), places).toString();
    }

    /**
     * Finds the total capacity of a node, including the cumulative capacities of
     * its children.
     *
     * @return the sum
     */
    public int totalCapacity() {

        // TODO: TEST
        int sum = 0;
        boolean isLeaf = true;
        for (int i = 0; i < Quadtree.CHILD_COUNT; ++i) {
            final Quadtree child = this.children[i];
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
     * Internal helper function to append to a {@link StringBuilder}.
     *
     * @param sb     the string builder
     * @param places the number of places
     * @return the string builder
     */
    StringBuilder toString(final StringBuilder sb, final int places) {

        sb.append("{\"bounds\":");
        this.bounds.toString(sb, places);
        sb.append(",\"capacity\":");
        sb.append(this.capacity);

        if (this.isLeaf()) {
            sb.append(",\"points\":[");
            final Iterator<Vec2> itr = this.points.iterator();
            while (itr.hasNext()) {
                itr.next().toString(sb, places);
                if (itr.hasNext()) {
                    sb.append(',');
                }
            }
        } else {
            sb.append(",\"children\":[");
            for (int i = 0; i < Quadtree.CHILD_COUNT - 1; ++i) {
                final Quadtree child = this.children[i];
                if (child != null) {
                    child.toString(sb, places);
                    sb.append(',');
                } else {
                    sb.append("null,");
                }
            }

            final Quadtree last = this.children[Quadtree.CHILD_COUNT - 1];
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

    /**
     * Gets the leaf nodes in this node and its children. The leaf nodes will still
     * be stored by
     * reference in the output array list, so this should be used internally.
     *
     * @param target the output array list
     * @return the leaf nodes
     */
    protected ArrayList<Quadtree> getLeaves(final ArrayList<Quadtree> target) {

        boolean isLeaf = true;
        for (int i = 0; i < Quadtree.CHILD_COUNT; ++i) {
            final Quadtree child = this.children[i];
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
     * Gets the points in this quadtree node and its children. The points will still
     * be stored by
     * reference in the output array list, so this should be used internally.
     *
     * @param target the output array list
     * @return the points
     */
    protected ArrayList<Vec2> getPoints(final ArrayList<Vec2> target) {

        boolean isLeaf = true;
        for (int i = 0; i < Quadtree.CHILD_COUNT; ++i) {
            final Quadtree child = this.children[i];
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
     * Splits the quadtree node into eight child nodes. The child capacity is the
     * same as the
     * parent's.
     *
     * @return this quadtree
     */
    protected Quadtree split() {
        return this.split(this.capacity);
    }

    /**
     * Splits the quadtree node into eight child nodes.
     *
     * @param childCapacity child capacity
     * @return this quadtree
     * @see Bounds2#split(Bounds2, float, float, Bounds2, Bounds2, Bounds2, Bounds2)
     */
    protected Quadtree split(final int childCapacity) {

        final int nextLevel = this.level + 1;
        for (int i = 0; i < Quadtree.CHILD_COUNT; ++i) {
            final Quadtree child = this.children[i];
            if (child != null) {
                /* Bounds will be set by the split method. Level is final. */
                child.setCapacity(childCapacity);
            } else {
                this.children[i] = new Quadtree(new Bounds2(), this.capacity, nextLevel);
            }
        }

        Bounds2.split(
            this.bounds,
            0.5f,
            0.5f,
            this.children[Quadtree.SOUTH_WEST].bounds,
            this.children[Quadtree.SOUTH_EAST].bounds,
            this.children[Quadtree.NORTH_WEST].bounds,
            this.children[Quadtree.NORTH_EAST].bounds);

        int idxOffset = 0;
        for (final Vec2 v : this.points) {
            boolean found = false;
            for (int j = 0; !found && j < Quadtree.CHILD_COUNT; ++j) {
                final int k = (idxOffset + j) % Quadtree.CHILD_COUNT;
                found = this.children[k].insert(v);
                if (found) {
                    idxOffset = k;
                }
            }
        }

        this.points.clear();

        return this;
    }
}

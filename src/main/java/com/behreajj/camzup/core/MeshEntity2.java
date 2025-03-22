package com.behreajj.camzup.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * An entity which contains a transform that is applied to a list of meshes.
 */
public class MeshEntity2 extends Entity2 implements Iterable<Mesh2>, IVolume2, ISvgWritable, IBlenderWritable {

    /**
     * The list of meshes held by the entity.
     */
    public final ArrayList<Mesh2> meshes = new ArrayList<>();

    /**
     * The default constructor.
     */
    public MeshEntity2() {
    }

    /**
     * Creates a mesh entity from a list of meshes.
     *
     * @param meshes the list of meshes
     */
    public MeshEntity2(final Mesh2... meshes) {

        this.appendAll(meshes);
    }

    /**
     * Creates a named mesh entity.
     *
     * @param name the name
     */
    public MeshEntity2(final String name) {
        super(name);
    }

    /**
     * Creates a mesh entity from a name and list of meshes.
     *
     * @param name   the name
     * @param meshes the list of meshes
     */
    public MeshEntity2(final String name, final Mesh2... meshes) {

        super(name, new Transform2());
        this.appendAll(meshes);
    }

    /**
     * Creates a mesh entity from a name, transform and list of meshes.
     *
     * @param name      the name
     * @param transform the transform
     * @param meshes    the list of meshes
     */
    public MeshEntity2(final String name, final Transform2 transform, final Mesh2... meshes) {

        super(name, transform);
        this.appendAll(meshes);
    }

    /**
     * Creates a mesh entity from a transform and list of meshes.
     *
     * @param transform the transform
     * @param meshes    the list of meshes
     */
    public MeshEntity2(final Transform2 transform, final Mesh2... meshes) {

        super(transform);
        this.appendAll(meshes);
    }

    /**
     * Calculates an Axis-Aligned Bounding Box (AABB) encompassing the entity.
     *
     * @param me     the entity
     * @param target the output bounds
     * @return the bounds
     */
    public static Bounds2 calcBounds(final MeshEntity2 me, final Bounds2 target) {

        final Vec2 co = new Vec2();

        target.set(Float.MAX_VALUE, -Float.MAX_VALUE);

        for (Mesh2 face2s : me) {
            Mesh2.accumMinMax(face2s, target.min, target.max, me.transform, co);
        }

        return target;
    }

    /**
     * Evaluates whether the mesh entity contains a point in local space.
     *
     * @param me         the mesh entity
     * @param pointLocal the point in local space
     * @return the evaluation
     */
    public static boolean contains(final MeshEntity2 me, final Vec2 pointLocal) {

        for (final Mesh2 mesh : me.meshes) {
            if (Mesh2.contains(mesh, pointLocal)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Evaluates whether the mesh entity contains a point. Multiplies the global
     * point by the
     * transform's inverse to produce the point in local space.
     *
     * @param me          the mesh entity
     * @param pointGlobal the point in global space
     * @param pointLocal  the point in local space
     * @return the evaluation
     * @see MeshEntity2#contains(MeshEntity2, Vec2)
     */
    public static boolean contains(
        final MeshEntity2 me, final Vec2 pointGlobal, final Vec2 pointLocal) {

        Transform2.invMulPoint(me.transform, pointGlobal, pointLocal);
        return MeshEntity2.contains(me, pointLocal);
    }

    /**
     * Appends a mesh to this mesh entity. The mesh must not be <code>null</code>
     * and must have a
     * length greater than zero.
     *
     * @param mesh the mesh
     * @return this mesh entity
     */
    public MeshEntity2 append(final Mesh2 mesh) {

        this.meshes.add(mesh);
        return this;
    }

    /**
     * Appends a collection of meshes to this mesh entity.
     *
     * @param app the meshes
     * @return this mesh entity
     */
    public MeshEntity2 appendAll(final Collection<Mesh2> app) {

        for (Mesh2 face2s : app) {
            this.append(face2s);
        }
        return this;
    }

    /**
     * Appends a list of meshes to this mesh entity.
     *
     * @param app the list of meshes
     * @return this mesh entity
     */
    @SuppressWarnings("UnusedReturnValue")
    public MeshEntity2 appendAll(final Mesh2... app) {

        for (Mesh2 face2s : app) {
            this.append(face2s);
        }
        return this;
    }

    /**
     * Transforms all meshes in this mesh entity by its transform, then resets the
     * entity's transform
     * to the identity.
     *
     * @return this mesh entity
     * @see Mesh2#transform(Transform2)
     * @see Transform2#identity(Transform2)
     */
    public MeshEntity2 consumeTransform() {

        for (Mesh2 mesh : this.meshes) {
            mesh.transform(this.transform);
        }
        Transform2.identity(this.transform);
        return this;
    }

    /**
     * Tests this entity for equivalence with another object.
     *
     * @param obj the object
     * @return the evaluation
     */
    @Override
    public boolean equals(final Object obj) {

        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || this.getClass() != obj.getClass()) {
            return false;
        }
        final MeshEntity2 other = (MeshEntity2) obj;
        return this.meshes.equals(other.meshes);
    }

    /**
     * Flips the mesh entity horizontally.
     *
     * @return this entity
     */
    @Override
    public MeshEntity2 flipX() {

        this.transform.flipX();

        return this;
    }

    /**
     * Flips the mesh entity vertically.
     *
     * @return this entity
     */
    @Override
    public MeshEntity2 flipY() {

        this.transform.flipY();

        return this;
    }

    /**
     * Gets a mesh from this mesh entity.
     *
     * @param i the index
     * @return the mesh
     * @see Utils#mod(int, int)
     */
    public Mesh2 get(final int i) {

        return this.meshes.get(Utils.mod(i, this.meshes.size()));
    }

    /**
     * Gets this mesh entity's scale.
     *
     * @param target the output vector
     * @return the scale
     */
    @Override
    public Vec2 getScale(final Vec2 target) {

        return this.transform.getScale(target);
    }

    /**
     * Returns a hash code for this entity based on its array of entity data.
     *
     * @return the hash
     */
    @Override
    public int hashCode() {

        final int prime = 31;
        final int result = super.hashCode();
        return prime * result + this.meshes.hashCode();
    }

    /**
     * Returns an iterator, which allows an enhanced for-loop to access the meshes
     * in the mesh entity.
     *
     * @return the iterator
     * @see List#iterator()
     */
    @Override
    public Iterator<Mesh2> iterator() {
        return this.meshes.iterator();
    }

    /**
     * Gets the number of meshes held by the entity.
     *
     * @return the length
     */
    public int length() {
        return this.meshes.size();
    }

    /**
     * Centers and rescales all meshes in the entity about a shared origin by
     * calculating their
     * dimensions, subtracting the center point, and scaling by the maximum
     * dimension.
     *
     * @return this mesh entity.
     */
    public MeshEntity2 reframe() {

        /* Find lower and upper bound for all meshes. */
        final Vec2 lb = new Vec2(Float.MAX_VALUE, Float.MAX_VALUE);
        final Vec2 ub = new Vec2(-Float.MAX_VALUE, -Float.MAX_VALUE);

        Iterator<Mesh2> itr = this.meshes.iterator();
        while (itr.hasNext()) {
            Mesh2.accumMinMax(itr.next(), lb, ub);
        }

        lb.x = -0.5f * (lb.x + ub.x);
        lb.y = -0.5f * (lb.y + ub.y);
        final float scl = Utils.div(1.0f, Math.max(ub.x - lb.x, ub.y - lb.y));

        itr = this.meshes.iterator();
        while (itr.hasNext()) {
            final Mesh2 mesh = itr.next();
            mesh.translate(lb);
            mesh.scale(scl);
        }

        return this;
    }

    /**
     * Removes a mesh from this mesh entity. Returns <code>true</code> if this
     * request was filled,
     * <code>false</code> if not.
     *
     * @param mesh the mesh to remove.
     * @return the evaluation
     */
    public boolean remove(final Mesh2 mesh) {

        return this.meshes.remove(mesh);
    }

    /**
     * Removes a mesh at a given index.
     *
     * @param i the index
     * @return the mesh
     */
    public Mesh2 removeAt(final int i) {
        return this.meshes.remove(i);
    }

    /**
     * Resets the mesh entity to an initial state. Sets the transform to an identity
     * and clears the
     * list of meshes.
     *
     * @return this entity
     */
    @Override
    public MeshEntity2 reset() {

        super.reset();
        this.meshes.clear();

        return this;
    }

    /**
     * Scales the entity by a scalar.
     *
     * @param scalar the scalar
     * @return this entity
     */
    @Override
    public MeshEntity2 scaleBy(final float scalar) {

        this.transform.scaleBy(scalar);

        return this;
    }

    /**
     * Scales the entity by a non-uniform scalar.
     *
     * @param scalar the scalar
     * @return the entity
     */
    @Override
    public MeshEntity2 scaleBy(final Vec2 scalar) {

        this.transform.scaleBy(scalar);

        return this;
    }

    /**
     * Scales the entity to a uniform size.
     *
     * @param scalar the size
     * @return this entity
     */
    @Override
    public MeshEntity2 scaleTo(final float scalar) {

        this.transform.scaleTo(scalar);

        return this;
    }

    /**
     * Scales the entity to a non-uniform size.
     *
     * @param scalar the size
     * @return this entity
     */
    @Override
    public MeshEntity2 scaleTo(final Vec2 scalar) {

        this.transform.scaleTo(scalar);

        return this;
    }

    /**
     * Eases the entity to a scale by a step over time.
     *
     * @param scalar the scalar
     * @param step   the step
     * @return this entity
     */
    @Override
    public MeshEntity2 scaleTo(final Vec2 scalar, final float step) {

        this.transform.scaleTo(scalar, step);

        return this;
    }

    /**
     * Sets a coordinate in a mesh. Multiplies the input coordinate in global space
     * by the transform's
     * inverse.<br>
     * <br>
     * To facilitate editing the mesh with a graphical user interface (GUI).
     *
     * @param meshIndex  the mesh index
     * @param coordIndex the coordinate index
     * @param global     the point in global space
     * @param local      the point in mesh local space
     * @return this entity
     */
    public MeshEntity2 setCoord(
        final int meshIndex, final int coordIndex, final Vec2 global, final Vec2 local) {

        Transform2.invMulPoint(this.transform, global, local);
        final Vec2[] coords = this.get(meshIndex).coords;
        final int j = Utils.mod(coordIndex, coords.length);
        coords[j].set(local);

        return this;
    }

    /**
     * Returns a String of Python code targeted toward the Blender 2.8x API. This
     * code is brittle and
     * is used for internal testing purposes.
     *
     * @return the string
     */
    public String toBlenderCode() {

        return this.toBlenderCode(null);
    }

    /**
     * Returns a String of Python code targeted toward the Blender 3.x API. This
     * code is brittle and
     * is used for internal testing purposes, i.e., to compare how mesh geometry
     * looks in Blender (the
     * control) versus in the library (the test).
     *
     * @param decimate      decimate the shape
     * @param extrude       extrude the shape
     * @param offset        extrusion offset
     * @param useAutoSmooth auto smooth normals
     * @param materials     the materials
     * @param gamma         color adjustment
     * @param metallic      metallic factor
     * @param roughness     roughness factor
     * @return the string
     */
    public String toBlenderCode(
        final boolean decimate,
        final float extrude,
        final float offset,
        final boolean useAutoSmooth,
        final MaterialSolid[] materials,
        final float gamma,
        final float metallic,
        final float roughness) {

        final boolean includeEdges = false;

        final StringBuilder pyCd = new StringBuilder(2048);
        pyCd.append("from bpy import context as C, data as D\nimport bmesh\n\n");
        pyCd.append("entity_src = {\"name\": \"");
        pyCd.append("id");
        pyCd.append(this.name);
        pyCd.append("\", \"transform\": ");
        this.transform.toBlenderCode(pyCd);
        pyCd.append(", \"meshes\": [");

        /* Append meshes. */
        final Iterator<Mesh2> meshItr = this.meshes.iterator();
        while (meshItr.hasNext()) {
            meshItr.next().toBlenderCode(pyCd);
            if (meshItr.hasNext()) {
                pyCd.append(',').append(' ');
            }
        }

        /* Append materials. */
        final boolean useMaterials = materials != null && materials.length > 0;
        pyCd.append("], \"materials\": [");
        if (useMaterials) {
            final int matLen = materials.length;
            final int matLast = matLen - 1;
            for (int i = 0; i < matLast; ++i) {
                materials[i].toBlenderCode(pyCd, gamma, metallic, roughness);
                pyCd.append(',').append(' ');
            }
            materials[matLast].toBlenderCode(pyCd, gamma, metallic, roughness);
        } else {
            MaterialSolid.defaultBlenderMaterial(pyCd, gamma);
        }
        pyCd.append("]}\n\n");

        this.genParentString(pyCd);
        this.genMaterialString(pyCd);
        this.genMeshString(pyCd, includeEdges, useAutoSmooth);

        /* Add materials to mesh data. */
        pyCd.append("    md_mats = mesh_data.materials\n");
        pyCd.append("    md_mats.clear()\n");
        if (useMaterials) {
            pyCd.append("    mat_idx = mesh[\"material_index\"]\n");
            pyCd.append("    mat_name = materials[mat_idx][\"name\"]\n");
        } else {
            pyCd.append("    mat_name = materials[0][\"name\"]\n");
        }
        pyCd.append("    md_mats.append(d_mats[mat_name])\n");

        /* Create mesh object. */
        pyCd.append("    mesh_obj = d_objs.new(mesh_data.name, mesh_data)\n");
        pyCd.append("    mesh_obj.rotation_mode = \"QUATERNION\"\n");
        pyCd.append("    mesh_obj.parent = parent_obj\n");
        pyCd.append("    scene_objs.link(mesh_obj)");

        /* Optional decimate modifier. */
        if (decimate) {
            pyCd.append("\n\n    if len(fc_idcs) > 3:\n");
            pyCd.append("        dec = mesh_obj.modifiers.new(");
            pyCd.append("\"Decimate\", \"DECIMATE\")\n");
            pyCd.append("        dec.decimate_type = \"DISSOLVE\"");
        }

        /* Optional extrude modifier. */
        if (extrude > 0.0f) {
            pyCd.append("\n\n    solidify = mesh_obj.modifiers.new(");
            pyCd.append("\"Solidify\", \"SOLIDIFY\")\n");
            pyCd.append("    solidify.thickness = ");
            Utils.toFixed(pyCd, extrude, 6);
            pyCd.append("\n");
            pyCd.append("    solidify.offset = ");
            Utils.toFixed(pyCd, offset, 6);
            pyCd.append("\n");
            pyCd.append("    solidify.show_in_editmode = False");
        }

        return pyCd.toString();
    }

    /**
     * Returns a String of Python code targeted toward the Blender 3.x API. This
     * code is brittle and
     * is used for internal testing purposes.
     *
     * @param decimate decimate the shape
     * @param extrude  extrude the shape
     * @param ms       the materials
     * @return the string
     */
    public String toBlenderCode(
        final boolean decimate, final float extrude, final MaterialSolid[] ms) {

        return this.toBlenderCode(decimate, extrude, 0.0f, true, ms, 2.2f, 0.0f, 1.0f);
    }

    /**
     * Returns a String of Python code targeted toward the Blender 2.8x API. This
     * code is brittle and
     * is used for internal testing purposes.
     *
     * @param ms the materials
     * @return the string
     */
    public String toBlenderCode(final MaterialSolid[] ms) {

        return this.toBlenderCode(false, 0.0f, ms);
    }

    /**
     * Renders the mesh entity as a string following the Wavefront OBJ file format.
     *
     * @return the string
     */
    public String toObjString() {

        final StringBuilder objs = new StringBuilder(2048);
        final Iterator<Mesh2> itr = this.meshes.iterator();

        /* Append name. */
        objs.append('o');
        objs.append(' ');
        objs.append(this.name);
        objs.append('\n');

        int vIdx = 1;
        int vtIdx = 1;
        int vnIdx = 1;
        int fCount = 0;
        while (itr.hasNext()) {
            final Mesh2 mesh = itr.next();
            mesh.toObjString(objs, vIdx, vtIdx, vnIdx, true);
            if (itr.hasNext()) {
                objs.append('\n');
            }

            vIdx += mesh.coords.length;
            vtIdx += mesh.texCoords.length;
            vnIdx += 1;
            fCount += mesh.faces.length;
        }

        final StringBuilder comment = new StringBuilder(256);
        comment.append("# g: ");
        comment.append(this.meshes.size());
        comment.append(", v: ");
        comment.append(vIdx - 1);
        comment.append(", vt: ");
        comment.append(vtIdx - 1);
        comment.append(", vn: ");
        comment.append(vnIdx - 1);
        comment.append(", f: ");
        comment.append(fCount);
        comment.append('\n');
        objs.insert(0, comment);

        return objs.toString();
    }

    /**
     * Centers all meshes in the entity about a shared origin by calculating their
     * dimensions then
     * subtracting the center point.
     *
     * @return this mesh entity.
     */
    public MeshEntity2 toOrigin() {

        /* Find lower and upper bound for all meshes. */
        final Vec2 lb = new Vec2(Float.MAX_VALUE, Float.MAX_VALUE);
        final Vec2 ub = new Vec2(-Float.MAX_VALUE, -Float.MAX_VALUE);
        Iterator<Mesh2> itr = this.meshes.iterator();
        while (itr.hasNext()) {
            Mesh2.accumMinMax(itr.next(), lb, ub);
        }

        /* Shift meshes. */
        lb.x = -0.5f * (lb.x + ub.x);
        lb.y = -0.5f * (lb.y + ub.y);

        itr = this.meshes.iterator();
        while (itr.hasNext()) {
            itr.next().translate(lb);
        }

        return this;
    }

    /**
     * Returns a string representation of this mesh entity.
     *
     * @param places number of places
     * @return the string
     */
    @Override
    public String toString(final int places) {

        final StringBuilder sb = new StringBuilder(1024);
        sb.append("{\"name\":\"");
        sb.append(this.name);
        sb.append('\"');
        sb.append(",\"transform\":");
        this.transform.toString(sb, places);
        sb.append(",\"meshes\":[");

        final Iterator<Mesh2> itr = this.meshes.iterator();
        while (itr.hasNext()) {
            itr.next().toString(sb, places);
            if (itr.hasNext()) {
                sb.append(',');
            }
        }

        sb.append("]}");
        return sb.toString();
    }

    /**
     * Creates a string representing a group node in the SVG format.
     *
     * @param zoom scaling from external transforms
     * @return the string
     */
    @Override
    public String toSvgElm(final float zoom) {

        return this.toSvgElm(zoom, new MaterialSolid[]{});
    }

    /**
     * Creates a string representing a group node in the SVG format.
     *
     * @param zoom     scaling from external transforms
     * @param material the material to use
     * @return the string
     */
    public String toSvgElm(final float zoom, final MaterialSolid material) {

        return this.toSvgElm(zoom, new MaterialSolid[]{material});
    }

    /**
     * Creates a string representing a group node in the SVG format. This SVG is
     * designed for
     * compatibility with Processing, not for efficiency.<br>
     * <br>
     * Stroke weight is impacted by scaling in transforms, so zoom is a parameter.
     * If nonuniform
     * zooming is used, zoom can be an average of width and height or the maximum
     * dimension.
     *
     * @param zoom      scaling from external transforms
     * @param materials the materials to use
     * @return the string
     */
    public String toSvgElm(final float zoom, final MaterialSolid[] materials) {

        // TODO: Allow user to specify SVG cap and join rounding?

        final StringBuilder svgp = new StringBuilder(1024);
        if (this.length() < 1) {
            return svgp.toString();
        }

        /* Decide how many groups to create based on material. */
        int matLen = 0;
        boolean includesMats = false;
        boolean oneMat = false;
        if (materials != null) {
            matLen = materials.length;
            includesMats = matLen > 0;
            oneMat = includesMats && matLen < 2 && materials[0] != null;
        }
        final boolean multipleMats = includesMats && !oneMat;

        /* Adjust stroke weight according to transform scale and camera zoom. */
        final float scale = zoom * Transform2.minDimension(this.transform);

        /* If no materials, append a default; if one, append that. */
        if (!includesMats) {
            MaterialSolid.defaultSvgMaterial(svgp, scale);
        } else if (oneMat) {
            svgp.append("<g ");
            materials[0].toSvgString(svgp, scale);
            svgp.append('>');
            svgp.append('\n');
        }

        /* Append entity group. */
        svgp.append("<g id=\"");
        svgp.append(this.name.toLowerCase());
        svgp.append("\" class=\"");
        svgp.append(this.getClass().getSimpleName().toLowerCase());
        svgp.append('\"');
        svgp.append(' ');
        this.transform.toSvgString(svgp);
        svgp.append('>');
        svgp.append('\n');

        for (final Mesh2 mesh : this.meshes) {
            /*
             * It'd be more efficient to create a definitions block that contains
             * the data for each material, which is then used by a mesh element
             * with xlink. However, such tags are ignored when Processing imports
             * an SVG with loadShape.
             */
            if (multipleMats) {
                final int vMatIdx = Utils.mod(mesh.materialIndex, matLen);
                final MaterialSolid material = materials[vMatIdx];
                svgp.append("<g ");
                material.toSvgString(svgp, scale);
                svgp.append(">\n");
            }

            mesh.toSvgPath(svgp);

            /* Close out material group. */
            if (multipleMats) {
                svgp.append("</g>\n");
            }
        }
        svgp.append("</g>\n");

        /* Close out default material group. */
        if (!includesMats || oneMat) {
            svgp.append("</g>\n");
        }
        return svgp.toString();
    }

    /**
     * Renders this mesh entity as an SVG string. A default material renders the
     * mesh's fill and
     * stroke. The background of the SVG is transparent. Sets the camera scale to
     * 1.0.
     *
     * @return the SVG string
     */
    @Override
    public String toSvgString() {

        final Vec2 scale = this.transform.getScale(new Vec2());
        return this.toSvgString(
            ISvgWritable.DEFAULT_ORIGIN_X,
            ISvgWritable.DEFAULT_ORIGIN_Y,
            1.0f,
            -1.0f,
            Math.max(ISvgWritable.DEFAULT_WIDTH, Utils.abs(scale.x)),
            Math.max(ISvgWritable.DEFAULT_HEIGHT, Utils.abs(scale.y)));
    }
}

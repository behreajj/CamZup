package camzup.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * An entity which contains a transform that is applied to a list of
 * meshes. The meshes may references a list of materials by index.
 */
public class MeshEntity2 extends Entity2 implements Iterable < Mesh2 >,
   IVolume2, ISvgWritable {

   /**
    * The list of meshes held by the entity.
    */
   public final List < Mesh2 > meshes;

   {
      this.meshes = new ArrayList <>();
   }

   /**
    * The default constructor.
    */
   public MeshEntity2 ( ) { super(); }

   /**
    * Creates a mesh entity from a list of meshes.
    *
    * @param meshes the list of meshes
    */
   public MeshEntity2 ( final Mesh2... meshes ) {

      super(new Transform2());
      this.appendAll(meshes);
   }

   /**
    * Creates a named mesh entity.
    *
    * @param name the name
    */
   public MeshEntity2 ( final String name ) { super(name); }

   /**
    * Creates a mesh entity from a name and list of meshes.
    *
    * @param name   the name
    * @param meshes the list of meshes
    */
   public MeshEntity2 ( final String name, final Mesh2... meshes ) {

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
   public MeshEntity2 ( final String name, final Transform2 transform,
      final Mesh2... meshes ) {

      super(name, transform);
      this.appendAll(meshes);
   }

   /**
    * Creates a mesh entity from a transform and list of meshes.
    *
    * @param transform the transform
    * @param meshes    the list of meshes
    */
   public MeshEntity2 ( final Transform2 transform, final Mesh2... meshes ) {

      super(transform);
      this.appendAll(meshes);
   }

   /**
    * Appends a mesh to this mesh entity.
    *
    * @param mesh the mesh
    *
    * @return this mesh entity
    */

   public MeshEntity2 append ( final Mesh2 mesh ) {

      if ( mesh != null ) { this.meshes.add(mesh); }
      return this;
   }

   /**
    * Appends a collection of meshes to this mesh entity.
    *
    * @param app the meshes
    *
    * @return this mesh entity
    */

   public MeshEntity2 appendAll ( final Collection < Mesh2 > app ) {

      final Iterator < Mesh2 > itr = app.iterator();
      while ( itr.hasNext() ) {
         this.append(itr.next());
      }
      return this;
   }

   /**
    * Appends a list of meshes to this mesh entity.
    *
    * @param app the list of meshes
    *
    * @return this mesh entity
    */

   public MeshEntity2 appendAll ( final Mesh2... app ) {

      final int len = app.length;
      for ( int i = 0; i < len; ++i ) {
         this.append(app[i]);
      }
      return this;
   }

   /**
    * Gets a mesh from this mesh entity.
    *
    * @param i the index
    *
    * @return the mesh
    *
    * @see Utils#mod(int, int)
    */
   public Mesh2 get ( final int i ) {

      return this.meshes.get(Utils.mod(i, this.meshes.size()));
   }

   /**
    * Returns an iterator, which allows an enhanced for-loop to access the
    * meshes in the mesh entity.
    *
    * @return the iterator
    *
    * @see List#iterator()
    */
   @Override
   public Iterator < Mesh2 > iterator ( ) { return this.meshes.iterator(); }

   /**
    * Gets the number of meshes held by the entity.
    *
    * @return the length
    */
   public int length ( ) { return this.meshes.size(); }

   /**
    * Scales the entity by a scalar.
    *
    * @param scalar the scalar
    *
    * @return this entity
    */
   @Override
   public MeshEntity2 scaleBy ( final float scalar ) {

      this.transform.scaleBy(scalar);
      return this;
   }

   /**
    * Scales the entity by a non-uniform scalar.
    *
    * @param scalar the scalar
    *
    * @return the entity
    */
   @Override
   public MeshEntity2 scaleBy ( final Vec2 scalar ) {

      this.transform.scaleBy(scalar);
      return this;
   }

   /**
    * Scales the entity to a uniform size.
    *
    * @param scalar the size
    *
    * @return this entity
    */
   @Override
   public MeshEntity2 scaleTo ( final float scalar ) {

      this.transform.scaleTo(scalar);
      return this;
   }

   /**
    * Scales the entity to a non-uniform size.
    *
    * @param scalar the size
    *
    * @return this entity
    */
   @Override
   public MeshEntity2 scaleTo ( final Vec2 scalar ) {

      this.transform.scaleTo(scalar);
      return this;
   }

   /**
    * Eases the entity to a scale by a step over time.
    *
    * @param scalar the scalar
    * @param step   the step
    *
    * @return this entity
    */
   @Override
   public MeshEntity2 scaleTo ( final Vec2 scalar, final float step ) {

      this.transform.scaleTo(scalar, step);
      return this;
   }

   /**
    * Returns a String of Python code targeted toward the Blender 2.8x API.
    * This code is brittle and is used for internal testing purposes.
    *
    * @return the string
    */
   @Experimental
   public String toBlenderCode ( ) {

      return this.toBlenderCode(( MaterialSolid[] ) null);
   }

   /**
    * Returns a String of Python code targeted toward the Blender 2.8x API.
    * This code is brittle and is used for internal testing purposes.
    *
    * @param ms the materials
    *
    * @return the string
    */
   @Experimental
   public String toBlenderCode ( final MaterialSolid[] ms ) {

      return this.toBlenderCode(ms, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0001f, 0.0f,
         0.0f);
   }

   /**
    * Returns a String of Python code targeted toward the Blender 2.8x API.
    * This code is brittle and is used for internal testing purposes, i.e., to
    * compare how mesh geometry looks in Blender (the control) versus in the
    * library (the test).
    *
    * @param materials      the materials
    * @param gamma          color adjustment
    * @param metallic       metallic factor
    * @param roughness      roughness factor
    * @param specular       specular highlight strength
    * @param clearcoat      clear coat factor
    * @param clearcoatRough clear coat roughness
    * @param extrude        extrude the shape
    * @param offset         extrusion offset
    *
    * @return the string
    */
   @Experimental
   public String toBlenderCode ( final MaterialSolid[] materials,
      final float gamma, final float metallic, final float roughness,
      final float specular, final float clearcoat, final float clearcoatRough,
      final float extrude, final float offset ) {

      final int meshLen = this.meshes.size();
      final boolean autoSmoothNormals = true;
      final boolean addVertGroups = true;
      final boolean includeUvs = true;
      final boolean calcTangents = false;
      final boolean useMaterials = materials != null && materials.length > 0;

      final StringBuilder pyCd = new StringBuilder(2048);

      if ( includeUvs ) { pyCd.append("import bmesh\n"); }

      pyCd.append("from bpy import data as D, context as C\n\n");
      pyCd.append("mesh_entity = {\"name\": \"");
      pyCd.append(this.name);
      pyCd.append("\", \"transform\": ");
      pyCd.append(this.transform.toBlenderCode());
      pyCd.append(", \"meshes\": [");

      int meshIndex = 0;
      final int meshLast = meshLen - 1;
      final Iterator < Mesh2 > meshItr = this.meshes.iterator();
      while ( meshItr.hasNext() ) {
         // final float zoff = 0.0001f * meshIndex;
         pyCd.append(meshItr.next().toBlenderCode(includeUvs, 0.0f));
         if ( meshIndex < meshLast ) { pyCd.append(',').append(' '); }
         meshIndex++;
      }

      pyCd.append("], \"materials\": [");
      if ( useMaterials ) {
         final int matLen = materials.length;
         final int matLast = matLen - 1;

         for ( int i = 0; i < matLen; ++i ) {
            pyCd.append(materials[i].toBlenderCode(gamma, metallic, roughness,
               specular, clearcoat, clearcoatRough));
            if ( i < matLast ) { pyCd.append(',').append(' '); }
         }
      } else {
         pyCd.append(MaterialSolid.defaultBlenderMaterial(gamma));
      }

      // TODO: Can this be replaced with a string that is replaced by ANT /
      // build properties?
      pyCd.append("]}\n\nd_objs = D.objects\n");
      pyCd.append("parent_obj = d_objs.new(");
      pyCd.append("mesh_entity[\"name\"], None)\n");
      pyCd.append("tr = mesh_entity[\"transform\"]\n");
      pyCd.append("parent_obj.location = tr[\"location\"]\n");
      pyCd.append("parent_obj.rotation_mode = tr[\"rotation_mode\"]\n");
      pyCd.append("parent_obj.rotation_quaternion = ");
      pyCd.append("tr[\"rotation_quaternion\"]\n");
      pyCd.append("parent_obj.scale = tr[\"scale\"]\n");
      pyCd.append("parent_obj.empty_display_size = 0.25\n");
      pyCd.append("scene_objs = C.scene.collection.objects\n");
      pyCd.append("scene_objs.link(parent_obj)\n\n");

      pyCd.append("materials = mesh_entity[\"materials\"]\n");
      pyCd.append("d_mats = D.materials\n");
      pyCd.append("for material in materials:\n");
      pyCd.append("    fill_clr = material[\"fill\"]\n");
      pyCd.append("    metal_val = material[\"metallic\"]\n");
      pyCd.append("    rough_val = material[\"roughness\"]\n\n");

      pyCd.append("    mat_data = d_mats.new(material[\"name\"])\n");
      pyCd.append("    mat_data.diffuse_color = fill_clr\n");
      pyCd.append("    mat_data.metallic = metal_val\n");
      pyCd.append("    mat_data.roughness = rough_val\n");
      pyCd.append("    mat_data.use_nodes = True\n\n");

      pyCd.append("    node_tree = mat_data.node_tree\n");
      pyCd.append("    nodes = node_tree.nodes\n");
      pyCd.append("    pbr = nodes[\"Principled BSDF\"]\n");
      pyCd.append("    pbr_in = pbr.inputs\n");
      pyCd.append("    pbr_in[\"Base Color\"].default_value = fill_clr\n");
      pyCd.append("    pbr_in[\"Metallic\"].default_value = metal_val\n");
      pyCd.append("    pbr_in[\"Roughness\"].default_value = rough_val\n");
      pyCd.append("    specular = pbr_in[\"Specular\"]\n");
      pyCd.append("    specular.default_value = material[\"specular\"]\n");
      pyCd.append("    clearcoat = pbr_in[\"Clearcoat\"]\n");
      pyCd.append("    clearcoat.default_value = material[\"clearcoat\"]\n");
      pyCd.append("    cr = pbr_in[\"Clearcoat Roughness\"]\n");
      pyCd.append("    cr.default_value = ");
      pyCd.append("material[\"clearcoat_roughness\"]\n\n");

      pyCd.append("meshes = mesh_entity[\"meshes\"]\n");
      pyCd.append("d_meshes = D.meshes\n");
      pyCd.append("for mesh in meshes:\n");
      pyCd.append("    name = mesh[\"name\"]\n");
      pyCd.append("    vert_dat = mesh[\"vertices\"]\n");
      pyCd.append("    fc_idcs = mesh[\"faces\"]\n");
      pyCd.append("    mesh_data = d_meshes.new(name)\n");
      pyCd.append("    mesh_data.from_pydata(vert_dat, [], fc_idcs)\n");
      pyCd.append("    mesh_data.validate()\n");

      if ( autoSmoothNormals ) {
         pyCd.append("    mesh_data.use_auto_smooth = True\n");
         pyCd.append("    polys = mesh_data.polygons\n");
         pyCd.append("    for poly in polys:\n");
         pyCd.append("        poly.use_smooth = True\n");
      }

      if ( includeUvs ) {
         pyCd.append("    bm = bmesh.new()\n");
         pyCd.append("    bm.from_mesh(mesh_data)\n");
         pyCd.append("    uv_dat = mesh[\"uvs\"]\n");
         pyCd.append("    uv_idcs = mesh[\"uv_indices\"]\n");
         pyCd.append("    uv_layer = bm.loops.layers.uv.verify()\n");
         pyCd.append("    for face in bm.faces:\n");
         pyCd.append("        bmfcidx = face.index\n");
         pyCd.append("        faceuvidcs = uv_idcs[bmfcidx]\n");
         pyCd.append("        curr_loop = 0\n");
         pyCd.append("        for loop in face.loops:\n");
         pyCd.append("            vert = loop.vert\n");
         pyCd.append("            bmvt = loop[uv_layer]\n");
         pyCd.append("            uv_idx = faceuvidcs[curr_loop]\n");
         pyCd.append("            uv_co = uv_dat[uv_idx]\n");
         pyCd.append("            bmvt.uv = uv_co\n");
         pyCd.append("            curr_loop = curr_loop + 1\n");

         if ( calcTangents ) {
            pyCd.append("    bmesh.ops.triangulate(bm, faces=bm.faces,");
            pyCd.append(" quad_method=\"SHORT_EDGE\",");
            pyCd.append(" ngon_method=\"EAR_CLIP\")\n");
         }
         pyCd.append("    bm.to_mesh(mesh_data)\n");
         pyCd.append("    bm.free()\n\n");

         if ( calcTangents ) { pyCd.append("    mesh_data.calc_tangents()\n"); }
      }

      if ( useMaterials ) {
         pyCd.append("    idx = mesh[\"material_index\"]\n");
         pyCd.append("    mat_name = materials[idx][\"name\"]\n");
         pyCd.append("    mesh_data.materials.append(d_mats[mat_name])\n");
      } else {
         pyCd.append("    mesh_data.materials.append(d_mats[0])\n");
      }

      pyCd.append("    mesh_obj = d_objs.new(name, mesh_data)\n");
      pyCd.append("    mesh_obj.rotation_mode = \"QUATERNION\"\n");
      pyCd.append("    mesh_obj.parent = parent_obj\n");
      pyCd.append("    scene_objs.link(mesh_obj)\n\n");

      if ( addVertGroups ) {
         final String vertGroupName = "Faces";
         pyCd.append("    vert_group = mesh_obj.vertex_groups.new(name=\"");
         pyCd.append(vertGroupName);
         pyCd.append("\")\n");
         pyCd.append("    fc_len = len(fc_idcs)\n");
         pyCd.append("    fc_itr = range(0, fc_len)\n");
         pyCd.append("    to_weight = 1.0\n");
         pyCd.append("    if fc_len > 1:\n");
         pyCd.append("        to_weight = 1.0 / (fc_len - 1.0)\n");
         pyCd.append("    for i in fc_itr:\n");
         pyCd.append("        fc_idx = fc_idcs[i]\n");
         pyCd.append("        weight = i * to_weight\n");
         pyCd.append("        vert_group.add(fc_idx, weight,");
         pyCd.append("\"REPLACE\")\n\n");
      }

      if ( extrude > 0.0f ) {

         pyCd.append("    solidify = mesh_obj.modifiers.new(");
         pyCd.append("\"Solidify\", \"SOLIDIFY\")\n");
         pyCd.append("    solidify.thickness = ");
         pyCd.append(Utils.toFixed(extrude, 6));
         pyCd.append("\n");
         pyCd.append("    solidify.offset = ");
         pyCd.append(Utils.toFixed(offset, 6));
         pyCd.append("\n");
         pyCd.append("    solidify.show_in_editmode = False\n");
      }

      return pyCd.toString();
   }

   /**
    * Returns a string representation of this mesh entity.
    *
    * @return the string
    */
   @Override
   public String toString ( ) { return this.toString(4, 8); }

   /**
    * Returns a string representation of this mesh entity.
    *
    * @param places   number of places
    * @param truncate count before list is truncated
    *
    * @return the string
    */
   public String toString ( final int places, final int truncate ) {

      final StringBuilder sb = new StringBuilder(1024);
      sb.append("{ name: \"");
      sb.append(this.name);
      sb.append('\"');
      sb.append(", transform: ");
      sb.append(this.transform.toString(places));
      sb.append(", meshes: [ ");

      final Iterator < Mesh2 > itr = this.meshes.iterator();
      while ( itr.hasNext() ) {
         sb.append(itr.next().toString(places, truncate));
         if ( itr.hasNext() ) { sb.append(',').append(' '); }
      }

      sb.append(" ] }");
      return sb.toString();
   }

   /**
    * Creates a string representing a group node in the SVG format.
    *
    * @param id   the path id prefix
    * @param zoom scaling from external transforms
    *
    * @return the string
    */
   @Override
   public String toSvgElm ( final String id, final float zoom ) {

      return this.toSvgElm(id, zoom, new MaterialSolid[] {});
   }

   /**
    * Creates a string representing a group node in the SVG format.
    *
    * @param id       the path id prefix
    * @param zoom     scaling from external transforms
    * @param material the material to use
    *
    * @return the string
    */
   public String toSvgElm ( final String id, final float zoom,
      final MaterialSolid material ) {

      return this.toSvgElm(id, zoom, new MaterialSolid[] { material });
   }

   /**
    * Creates a string representing a group node in the SVG format. This SVG
    * is designed for compatibility with Processing, not for efficiency.<br>
    * <br>
    * Stroke weight is impacted by scaling in transforms, so zoom is a
    * parameter. If nonuniform zooming is used, zoom can be an average of
    * width and height or the maximum dimension.
    *
    * @param id        the face id prefix
    * @param zoom      scaling from external transforms
    * @param materials the materials to use
    *
    * @return the string
    */
   public String toSvgElm ( final String id, final float zoom,
      final MaterialSolid[] materials ) {

      final StringBuilder svgp = new StringBuilder(1024);
      svgp.append("<g id=\"");
      svgp.append(this.name.toLowerCase()).append("\" ");
      svgp.append(this.transform.toSvgString());
      svgp.append(">\n");

      final float scale = zoom * Transform2.minDimension(this.transform);
      int matLen = 0;
      boolean includesMats = false;
      if ( materials != null ) {
         matLen = materials.length;
         includesMats = matLen > 0;
      }

      /* If no materials are present, use a default instead. */
      if ( !includesMats ) {
         svgp.append(MaterialSolid.defaultSvgMaterial(scale));
      }

      final Iterator < Mesh2 > meshItr = this.meshes.iterator();
      while ( meshItr.hasNext() ) {
         final Mesh2 mesh = meshItr.next();

         /*
          * It'd be more efficient to create a definitions block that contains
          * the data for each material, which is then used by a mesh element
          * with xlink. However, such tags are ignored when Processing imports
          * an SVG with loadShape.
          */
         if ( includesMats ) {

            final int vMatIdx = Utils.mod(mesh.materialIndex, matLen);
            final MaterialSolid material = materials[vMatIdx];
            svgp.append("<g ");
            svgp.append(material.toSvgString(scale));
            svgp.append(">\n");
         }

         svgp.append(mesh.toSvgPath(id));

         /* Close out material group. */
         if ( includesMats ) { svgp.append("</g>\n"); }
      }

      /* Close out default material group. */
      if ( !includesMats ) { svgp.append("</g>\n"); }

      svgp.append("</g>\n");
      return svgp.toString();
   }

   /**
    * Evaluates whether the mesh entity contains a point in local space.
    *
    * @param me         the mesh entity
    * @param pointLocal the point in local space
    *
    * @return the evaluation
    */
   public static boolean contains ( final MeshEntity2 me,
      final Vec2 pointLocal ) {

      final Iterator < Mesh2 > meshItr = me.meshes.iterator();
      while ( meshItr.hasNext() ) {
         final Mesh2 mesh = meshItr.next();
         if ( Mesh2.contains(mesh, pointLocal) ) { return true; }
      }
      return false;
   }

   /**
    * Evaluates whether the mesh entity contains a point. Multiplies the
    * global point by the transform's inverse to produce the point in local
    * space.
    *
    * @param me          the mesh entity
    * @param pointGlobal the point in global space
    * @param pointLocal  the point in local space
    *
    * @return the evaluation
    *
    * @see MeshEntity2#contains(MeshEntity2, Vec2)
    */
   public static boolean contains ( final MeshEntity2 me,
      final Vec2 pointGlobal, final Vec2 pointLocal ) {

      Transform2.invMulPoint(me.transform, pointGlobal, pointLocal);
      return MeshEntity2.contains(me, pointLocal);
   }

}

package camzup.core;

/**
 * A package-level interface to generate Blender boiler plate for various
 * entities.
 */
@Experimental
public interface IBlenderWritable {

   /**
    * Generates Blender python code which sets materials.
    *
    * @param pyCd the string builder
    *
    * @return the string builder
    */
   default StringBuilder genMaterialString ( final StringBuilder pyCd ) {

      /* Create materials. */
      pyCd.append("materials = entity_src[\"materials\"]\n");
      pyCd.append("d_mats = D.materials\n");
      pyCd.append("for material in materials:\n");
      pyCd.append("    fill_clr = material[\"fill\"]\n");
      pyCd.append("    metal_val = material[\"metallic\"]\n");
      pyCd.append("    rough_val = material[\"roughness\"]\n\n");

      /* Edit material viewport display. */
      pyCd.append("    mat_data = d_mats.new(material[\"name\"])\n");
      pyCd.append("    mat_data.diffuse_color = fill_clr\n");
      pyCd.append("    mat_data.metallic = metal_val\n");
      pyCd.append("    mat_data.roughness = rough_val\n");
      pyCd.append("    mat_data.use_nodes = True\n\n");

      /* Edit material nodes. */
      pyCd.append("    node_tree = mat_data.node_tree\n");
      pyCd.append("    nodes = node_tree.nodes\n");
      pyCd.append("    pbr = nodes[\"Principled BSDF\"]\n");
      pyCd.append("    pbr_in = pbr.inputs\n");
      pyCd.append("    pbr_in[\"Base Color\"].default_value = fill_clr\n");
      pyCd.append("    pbr_in[\"Metallic\"].default_value = metal_val\n");
      pyCd.append("    pbr_in[\"Roughness\"].default_value = rough_val\n\n");

      /*
       * As of 4.1.0, PBR node names are "Specular IOR Level" (12),
       * "Coat Weight" (18), "Coat Roughness" (19), "Coat IOR" (20), Coat Tint
       * (21)
       */

      return pyCd;
   }

   /**
    * Generates Blender python code which creates mesh data.
    *
    * @param pyCd          the string builder
    * @param includeEdges  whether to include edge data
    * @param useAutoSmooth use auto smoothed normals
    *
    * @return the string builder
    */
   default StringBuilder genMeshString ( final StringBuilder pyCd,
      final boolean includeEdges, final boolean useAutoSmooth ) {

      /* Append meshes. */
      final String pyUseAutoSmooth = useAutoSmooth ? "True" : "False";
      pyCd.append("meshes = entity_src[\"meshes\"]\n");
      pyCd.append("d_meshes = D.meshes\n");
      pyCd.append("for mesh in meshes:\n");
      pyCd.append("    name = mesh[\"name\"]\n");
      pyCd.append("    vert_dat = mesh[\"vertices\"]\n");
      pyCd.append("    edge_idcs = ");
      if ( includeEdges ) {
         pyCd.append("mesh[\"edges\"]\n");
      } else {
         pyCd.append("[]\n");
      }
      pyCd.append("    fc_idcs = mesh[\"faces\"]\n");
      pyCd.append("    mesh_data = d_meshes.new(name)\n");
      pyCd.append("    mesh_data.from_pydata(vert_dat, edge_idcs, fc_idcs)\n");
      pyCd.append("    mesh_data.validate(verbose=True)\n\n");

      /* Create BMesh. Set UVs. */
      pyCd.append("    bm = bmesh.new()\n");
      pyCd.append("    bm.from_mesh(mesh_data)\n");
      pyCd.append("    uv_dat = mesh[\"uvs\"]\n");
      pyCd.append("    uv_idcs = mesh[\"uv_indices\"]\n");
      pyCd.append("    uv_layer = bm.loops.layers.uv.verify()\n");
      pyCd.append("    for face in bm.faces:\n");
      pyCd.append("        face.smooth = ");
      pyCd.append(pyUseAutoSmooth);
      pyCd.append('\n');
      pyCd.append("        faceuvidcs = uv_idcs[face.index]\n");
      pyCd.append("        for i, loop in enumerate(face.loops):\n");
      pyCd.append("            loop[uv_layer].uv = uv_dat[faceuvidcs[i]]\n");
      pyCd.append("    bm.to_mesh(mesh_data)\n");
      pyCd.append("    bm.free()\n\n");

      return pyCd;
   }

   /**
    * Generates Blender python code which creates an empty parent object.
    *
    * @param pyCd the string builder
    *
    * @return the string builder
    */
   default StringBuilder genParentString ( final StringBuilder pyCd ) {

      pyCd.append("d_objs = D.objects\n");
      pyCd.append("parent_obj = d_objs.new(");
      pyCd.append("entity_src[\"name\"], None)\n");
      pyCd.append("tr = entity_src[\"transform\"]\n");
      pyCd.append("parent_obj.location = tr[\"location\"]\n");
      pyCd.append("parent_obj.rotation_mode = tr[\"rotation_mode\"]\n");
      pyCd.append("parent_obj.rotation_quaternion = ");
      pyCd.append("tr[\"rotation_quaternion\"]\n");
      pyCd.append("parent_obj.scale = tr[\"scale\"]\n");
      pyCd.append("parent_obj.empty_display_size = 0.25\n");
      pyCd.append("scene_objs = C.scene.collection.objects\n");
      pyCd.append("scene_objs.link(parent_obj)\n\n");

      return pyCd;
   }

}

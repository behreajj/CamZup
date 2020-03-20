package camzup.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * An entity which contains a transform that is applied to a list of
 * meshes. The meshes may references a list of materials by index.
 */
public class MeshEntity2 extends Entity2 implements Iterable < Mesh2 > {

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
  public MeshEntity2 ( final Mesh2 ... meshes ) {

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
  public MeshEntity2 (
      final String name,
      final Mesh2 ... meshes ) {

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
  public MeshEntity2 (
      final String name,
      final Transform2 transform,
      final Mesh2 ... meshes ) {

    super(name, transform);
    this.appendAll(meshes);
  }

  /**
   * Creates a mesh entity from a transform and list of meshes.
   *
   * @param transform the transform
   * @param meshes    the list of meshes
   */
  public MeshEntity2 (
      final Transform2 transform,
      final Mesh2 ... meshes ) {

    super(transform);
    this.appendAll(meshes);
  }

  /**
   * Appends a mesh to this mesh entity.
   *
   * @param mesh the mesh
   * @return this mesh entity
   */
  @Chainable
  public MeshEntity2 append ( final Mesh2 mesh ) {

    if ( mesh != null ) { this.meshes.add(mesh); }
    return this;
  }

  /**
   * Appends a list of meshes to this mesh entity.
   *
   * @param meshes the list of meshes
   * @return this mesh entity
   */
  @Chainable
  public MeshEntity2 appendAll ( final Mesh2 ... meshes ) {

    final int len = meshes.length;
    for ( int i = 0; i < len; ++i ) {
      this.append(meshes[i]);
    }
    return this;
  }

  /**
   * Gets a mesh from this mesh entity.
   *
   * @param i the index
   * @return the mesh
   */
  public Mesh2 get ( final int i ) {

    return this.meshes.get(Utils.mod(i, this.meshes.size()));
  }

  /**
   * Returns an iterator, which allows an enhanced for-loop to access
   * the meshes in the mesh entity.
   *
   * @return the iterator
   * @see List#iterator()
   */
  @Override
  public Iterator < Mesh2 > iterator ( ) {

    return this.meshes.iterator();
  }

  /**
   * Returns a String of Python code targeted toward the Blender 2.8x
   * API. This code is brittle and is used for internal testing
   * purposes, i.e., to compare how curve geometry looks in Blender (the
   * control) versus in the library (the test).
   *
   * @return the string
   */
  @Experimental
  public String toBlenderCode ( ) {

    return this.toBlenderCode(
        null, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0001f,
        0.0f, 0.0f);
  }

  /**
   * Returns a String of Python code targeted toward the Blender 2.8x
   * API. This code is brittle and is used for internal testing
   * purposes, i.e., to compare how mesh geometry looks in Blender (the
   * control) versus in the library (the test).
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
   * @return the string
   */
  @Experimental
  public String toBlenderCode (
      final MaterialSolid[] materials,
      final float gamma,
      final float metallic,
      final float roughness,
      final float specular,
      final float clearcoat,
      final float clearcoatRough,
      final float extrude,
      final float offset ) {

    final int meshLen = this.meshes.size();
    final boolean autoSmoothNormals = true;
    final boolean addVertGroups = true;
    final boolean useMaterials = materials != null && materials.length > 0;

    final StringBuilder pyCd = new StringBuilder(2048);
    pyCd.append("from bpy import data as D, context as C\n\n")
        .append("mesh_entity = {\"name\": \"")
        .append(this.name)
        .append("\", \"transform\": ")
        .append(this.transform.toBlenderCode())
        .append(", \"meshes\": [");

    int meshIndex = 0;
    final int meshLast = meshLen - 1;
    final Iterator < Mesh2 > meshItr = this.meshes.iterator();
    while ( meshItr.hasNext() ) {
      pyCd.append(meshItr.next().toBlenderCode());
      if ( meshIndex < meshLast ) { pyCd.append(',').append(' '); }
      meshIndex++;
    }

    pyCd.append("], \"materials\": [");
    if ( useMaterials ) {
      final int matLen = materials.length;
      final int matLast = matLen - 1;

      for ( int i = 0; i < matLen; ++i ) {
        pyCd.append(materials[i].toBlenderCode(
            gamma, metallic, roughness,
            specular, clearcoat, clearcoatRough));
        if ( i < matLast ) { pyCd.append(',').append(' '); }
      }
    } else {
      pyCd.append(MaterialSolid.defaultBlenderMaterial(gamma));
    }

    pyCd.append("]}\n\nd_objs = D.objects\n")
        .append("parent_obj = d_objs.new(")
        .append("mesh_entity[\"name\"], None)\n")
        .append("tr = mesh_entity[\"transform\"]\n")
        .append("parent_obj.location = tr[\"location\"]\n")
        .append("parent_obj.rotation_mode = tr[\"rotation_mode\"]\n")
        .append("parent_obj.rotation_quaternion = ")
        .append("tr[\"rotation_quaternion\"]\n")
        .append("parent_obj.scale = tr[\"scale\"]\n")
        .append("parent_obj.empty_display_size = 0.25\n")
        .append("scene_objs = C.scene.collection.objects\n")
        .append("scene_objs.link(parent_obj)\n\n")

        .append("materials = mesh_entity[\"materials\"]\n")
        .append("d_mats = D.materials\n")
        .append("for material in materials:\n")
        .append("    fill_clr = material[\"fill\"]\n")
        .append("    metal_val = material[\"metallic\"]\n")
        .append("    rough_val = material[\"roughness\"]\n\n")

        .append("    mat_data = d_mats.new(material[\"name\"])\n")
        .append("    mat_data.diffuse_color = fill_clr\n")
        .append("    mat_data.metallic = metal_val\n")
        .append("    mat_data.roughness = rough_val\n")
        .append("    mat_data.use_nodes = True\n\n")

        .append("    node_tree = mat_data.node_tree\n")
        .append("    nodes = node_tree.nodes\n")
        .append("    pbr = nodes[\"Principled BSDF\"]\n")
        .append("    pbr_in = pbr.inputs\n")
        .append("    pbr_in[\"Base Color\"].default_value = fill_clr\n")
        .append("    pbr_in[\"Metallic\"].default_value = metal_val\n")
        .append("    pbr_in[\"Roughness\"].default_value = rough_val\n")
        .append("    specular = pbr_in[\"Specular\"]\n")
        .append("    specular.default_value = material[\"specular\"]\n")
        .append("    clearcoat = pbr_in[\"Clearcoat\"]\n")
        .append("    clearcoat.default_value = material[\"clearcoat\"]\n")
        .append("    cr = pbr_in[\"Clearcoat Roughness\"]\n")
        .append("    cr.default_value = material[\"clearcoat_roughness\"]\n\n")

        .append("meshes = mesh_entity[\"meshes\"]\n")
        .append("d_meshes = D.meshes\n")
        .append("for mesh in meshes:\n")
        .append("    name = mesh[\"name\"]\n")
        .append("    vert_dat = mesh[\"vertices\"]\n")
        .append("    fc_idcs = mesh[\"faces\"]\n")
        .append("    mesh_data = d_meshes.new(name)\n")
        .append("    mesh_data.from_pydata(vert_dat, [], fc_idcs)\n");
    pyCd.append("    mesh_data.validate()\n");

    if ( autoSmoothNormals ) {
      pyCd.append("    mesh_data.use_auto_smooth = True\n")
          .append("    polys = mesh_data.polygons\n")
          .append("    for poly in polys:\n")
          .append("        poly.use_smooth = True\n");
    }

    if ( useMaterials ) {
      pyCd.append("    idx = mesh[\"material_index\"]\n")
          .append("    mat_name = materials[idx][\"name\"]\n")
          .append("    mesh_data.materials.append(d_mats[mat_name])\n");
    } else {
      pyCd.append("    mesh_data.materials.append(d_mats[0])\n");
    }

    pyCd.append("    mesh_obj = d_objs.new(name, mesh_data)\n")
        .append("    mesh_obj.rotation_mode = \"QUATERNION\"\n")
        .append("    mesh_obj.parent = parent_obj\n")
        .append("    scene_objs.link(mesh_obj)\n\n");

    if ( addVertGroups ) {
      final String vertGroupName = "Faces";
      pyCd.append("    vert_group = mesh_obj.vertex_groups.new(name=\"")
          .append(vertGroupName)
          .append("\")\n")
          .append("    fc_len = len(fc_idcs)\n")
          .append("    fc_itr = range(0, fc_len)\n")
          .append("    to_weight = 1.0\n")
          .append("    if fc_len > 1:\n")
          .append("        to_weight = 1.0 / (fc_len - 1.0)\n")
          .append("    for i in fc_itr:\n")
          .append("        fc_idx = fc_idcs[i]\n")
          .append("        weight = i * to_weight\n")
          .append("        vert_group.add(fc_idx, weight, \"ADD\")\n\n");
    }

    if ( extrude > 0.0f ) {
      pyCd.append("    solidify = mesh_obj.modifiers.new(")
          .append("\"Solidify\", \"SOLIDIFY\")\n")
          .append("    solidify.thickness = ")
          .append(Utils.toFixed(extrude, 6))
          .append("\n")
          .append("    solidify.offset = ")
          .append(Utils.toFixed(offset, 6))
          .append("\n");
    }

    return pyCd.toString();
  }

  /**
   * Returns a string representation of this mesh entity.
   *
   * @return the string
   */
  @Override
  public String toString ( ) {

    return this.toString(4, 8);
  }

  /**
   * Returns a string representation of this mesh entity.
   *
   * @param places   number of places
   * @param truncate count before list is truncated
   * @return the string
   */
  public String toString (
      final int places,
      final int truncate ) {

    final StringBuilder sb = new StringBuilder(1024)
        .append("{ name: \"")
        .append(this.name)
        .append('\"')
        .append(", transform: ")
        .append(this.transform.toString(places))
        .append(", meshes: [ ");

    int i = 0;
    final Iterator < Mesh2 > itr = this.meshes.iterator();
    final int last = this.meshes.size() - 1;
    while ( itr.hasNext() ) {
      sb.append(itr.next().toString(places, truncate));
      if ( i < last ) {
        sb.append(',').append(' ');
        // sb.append('\n');
      }
      i++;
    }

    sb.append(" ] }");
    return sb.toString();
  }

  /**
   * Creates a string representing a group node in the SVG format.
   *
   * @return the string
   */
  public String toSvgGroup ( ) {

    return this.toSvgGroup(new MaterialSolid[] {});
  }

  /**
   * Creates a string representing a group node in the SVG format.
   *
   * @param material the material to use
   * @return the string
   */
  public String toSvgGroup ( final MaterialSolid material ) {

    return this.toSvgGroup(new MaterialSolid[] { material });
  }

  /**
   * Creates a string representing a group node in the SVG format. This
   * SVG is designed for Processing compatibility, not for efficiency.
   *
   * @param materials the materials to use
   * @return the string
   */
  public String toSvgGroup ( final MaterialSolid[] materials ) {

    final StringBuilder result = new StringBuilder(1024)
        .append("<g id=\"")
        .append(this.name.toLowerCase())
        .append("\" ")
        .append(this.transform.toSvgString())
        .append(">\n");

    final float scale = Transform2.minDimension(this.transform);
    int matLen = 0;
    boolean includesMats = false;
    if ( materials != null ) {
      matLen = materials.length;
      includesMats = matLen > 0;
    }

    /* If no materials are present, use a default one instead. */
    if ( !includesMats ) {
      result.append(MaterialSolid.defaultSvgMaterial(scale));
    }

    final Iterator < Mesh2 > meshItr = this.meshes.iterator();
    while ( meshItr.hasNext() ) {
      final Mesh2 mesh = meshItr.next();

      /*
       * It would be more efficient to create a defs block that contains the
       * data for each material, which is then used by a mesh element with
       * xlink, but such tags are ignored when Processing imports an SVG
       * with loadShape.
       */
      if ( includesMats ) {

        final int vmatidx = Utils.mod(mesh.materialIndex, matLen);
        final MaterialSolid material = materials[vmatidx];
        result.append("<g ")
            .append(material.toSvgString(scale))
            .append(">\n");
      }

      result.append(mesh.toSvgString()).append('\n');

      /* Close out material group. */
      if ( includesMats ) { result.append("</g>\n"); }
    }

    /* Close out default material group. */
    if ( !includesMats ) { result.append("</g>\n"); }

    result.append("</g>");
    return result.toString();
  }
}

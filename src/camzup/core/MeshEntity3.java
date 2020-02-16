package camzup.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * An entity which contains a transform that is applied to a
 * list of meshes. The meshes may references a list of
 * materials by index.
 */
public class MeshEntity3 extends Entity3 implements Iterable < Mesh3 > {

   /**
    * The list of meshes held by the entity.
    */
   public final List < Mesh3 > meshes;

   {
      this.meshes = new ArrayList <>();
   }

   /**
    * The default constructor.
    */
   public MeshEntity3 () {

      super();
   }

   /**
    * Creates a named mesh entity.
    *
    * @param name
    *           the name
    */
   public MeshEntity3 ( final String name ) {

      super(name);
   }

   /**
    * Creates a mesh entity from a name, transform and list of
    * meshes.
    *
    * @param name
    *           the name
    * @param transform
    *           the transform
    * @param meshes
    *           the list of meshes
    */
   public MeshEntity3 (
         final String name,
         final Transform3 transform,
         final Mesh3... meshes ) {

      super(name, transform);
      this.appendMeshes(meshes);
   }

   /**
    * Creates a mesh entity from a transform and list of
    * meshes.
    *
    * @param transform
    *           the transform
    * @param meshes
    *           the list of meshes
    */
   public MeshEntity3 (
         final Transform3 transform,
         final Mesh3... meshes ) {

      super(transform);
      this.appendMeshes(meshes);
   }

   /**
    * Appends a mesh to this mesh entity.
    *
    * @param mesh
    *           the mesh
    * @return this mesh entity
    */
   @Chainable
   public MeshEntity3 appendMesh ( final Mesh3 mesh ) {

      if (mesh != null) {
         this.meshes.add(mesh);
      }
      return this;
   }

   /**
    * Appends a list of meshes to this mesh entity.
    *
    * @param meshes
    *           the list of meshes
    * @return this mesh entity
    */
   @Chainable
   public MeshEntity3 appendMeshes ( final Mesh3... meshes ) {

      for (final Mesh3 m : meshes) {
         this.appendMesh(m);
      }
      return this;
   }

   /**
    * Gets a mesh from this mesh entity.
    *
    * @param i
    *           the index
    * @return the mesh
    */
   public Mesh3 getMesh ( final int i ) {

      return this.meshes.get(Math.floorMod(i, this.meshes.size()));
   }

   /**
    * Returns an iterator, which allows an enhanced for-loop to
    * access the meshes in the mesh entity.
    *
    * @return the iterator
    * @see List#iterator()
    */
   @Override
   public Iterator < Mesh3 > iterator () {

      return this.meshes.iterator();
   }

   /**
    * Returns a String of Python code targeted toward the
    * Blender 2.8x API. This code is brittle and is used for
    * internal testing purposes, i.e., to compare how curve
    * geometry looks in Blender (the control) vs. in the
    * library (the test).
    *
    * @return the string
    */
   @Experimental
   public String toBlenderCode () {

      return this.toBlenderCode(null);
   }

   /**
    * Returns a String of Python code targeted toward the
    * Blender 2.8x API. This code is brittle and is used for
    * internal testing purposes, i.e., to compare how curve
    * geometry looks in Blender (the control) vs. in the
    * library (the test).
    *
    * @param materials
    *           the materials
    * @return the string
    */
   @Experimental
   public String toBlenderCode ( final MaterialSolid[] materials ) {

      final boolean useSmooth = true;

      final StringBuilder result = new StringBuilder(2048);
      result.append("from bpy import data as D, context as C\n\n")
            .append("mesh_entity = {\"name\": \"")
            .append(this.name)
            .append("\", \"transform\": ")
            .append(this.transform.toBlenderCode())
            .append(", \"meshes\": [");

      int meshIndex = 0;
      final int meshLast = this.meshes.size() - 1;
      final Iterator < Mesh3 > meshItr = this.meshes.iterator();
      while (meshItr.hasNext()) {
         result.append(meshItr.next().toBlenderCode());
         if (meshIndex < meshLast) {
            result.append(',').append(' ');
         }
         meshIndex++;
      }

      result.append("], \"materials\": [");

      final float expn = 2.2f;
      final boolean useMaterials = materials != null && materials.length > 0;
      if (useMaterials) {
         final int matLen = materials.length;
         final int matLast = matLen - 1;

         for (int i = 0; i < matLen; ++i) {
            result.append(materials[i].toBlenderCode(expn));
            if (i < matLast) {
               result.append(',').append(' ');
            }
         }
      } else {
         result.append(MaterialSolid.defaultBlenderMaterial(expn));
      }

      result.append("]}\n\nd_objs = D.objects\n")
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
            .append("    mat_data = d_mats.new(material[\"name\"])\n")
            .append("    fill_clr = material[\"fill\"]\n")
            .append("    mat_data.diffuse_color = fill_clr\n")
            .append("    mat_data.roughness = 1.0\n")
            .append("    mat_data.use_nodes = True\n")
            .append("    node_tree = mat_data.node_tree\n")
            .append("    nodes = node_tree.nodes\n")
            .append("    pbr = nodes[\"Principled BSDF\"]\n")
            .append("    roughness = pbr.inputs[\"Roughness\"]\n")
            .append("    roughness.default_value = 1.0\n")
            .append("    specular = pbr.inputs[\"Specular\"]\n")
            .append("    specular.default_value = 0.0\n")
            .append("    base_clr = pbr.inputs[\"Base Color\"]\n")
            .append("    base_clr.default_value = fill_clr\n\n")

            .append("meshes = mesh_entity[\"meshes\"]\n")
            .append("d_meshes = D.meshes\n")
            .append("for mesh in meshes:\n")
            .append("    name = mesh[\"name\"]\n")
            .append("    vert_dat = mesh[\"vertices\"]\n")
            .append("    face_idcs = mesh[\"faces\"]\n")
            .append("    mesh_data = d_meshes.new(name)\n")
            .append("    mesh_data.from_pydata(\n")
            .append("        vert_dat,\n")
            .append("        [],\n")
            .append("        face_idcs)\n")
            .append("    mesh_data.validate()\n");

      if (useSmooth) {
         result.append("    mesh_data.use_auto_smooth = True\n")
               .append("    polys = mesh_data.polygons\n")
               .append("    for poly in polys:\n")
               .append("        poly.use_smooth = True\n");
      }

      result.append("    idx = mesh[\"material_index\"]\n")
            .append("    mat_name = materials[idx][\"name\"]\n")
            .append("    mesh_data.materials.append(d_mats[mat_name])\n")
            .append("    mesh_obj = d_objs.new(name, mesh_data)\n")
            .append("    mesh_obj.rotation_mode = \"QUATERNION\"\n")
            .append("    scene_objs.link(mesh_obj)\n")
            .append("    mesh_obj.parent = parent_obj\n");

      return result.toString();
   }

   @Experimental
   public String toUnityCode () {

      final StringBuilder vs = new StringBuilder()
            .append("Vector3[] vs = { \n");

      final StringBuilder vts = new StringBuilder()
            .append("Vector2[] vts = { \n");

      final StringBuilder vns = new StringBuilder()
            .append("Vector3[] vns = { \n");

      final StringBuilder tris = new StringBuilder()
            .append("int[] tris = { \n");

      final Iterator < Mesh3 > meshItr = this.meshes.iterator();
      while (meshItr.hasNext()) {
         meshItr.next().toUnityCode(vs, vts, vns, tris);
      }

      vs.append(' ').append('}').append(';');
      vts.append(' ').append('}').append(';');
      vns.append(' ').append('}').append(';');
      tris.append(' ').append('}').append(';');

      final StringBuilder result = new StringBuilder();
      result.append("using UnityEngine;\n\npublic class Mesh")
            .append(this.name)
            .append(" : MonoBehaviour\n{ \n    ")
            .append(vs.toString())
            .append("\n\n    ")
            .append(vts.toString())
            .append("\n\n    ")
            .append(vns.toString())
            .append("\n\n    ")
            .append(tris.toString())
            .append("\n\n    void Start()\n    {\n")
            .append("        Mesh mesh = new Mesh();\n")
            .append("        GetComponent<MeshFilter>().mesh = mesh;\n")
            .append("        mesh.vertices = vs;\n")
            .append("        mesh.uv = vts;\n")
            .append("        mesh.normals = vns;\n")
            .append("        mesh.triangles = tris;\n")
            .append("        mesh.RecalculateTangents();\n")
            .append("    }");

      result.append('\n').append('}');
      return result.toString();
   }
}

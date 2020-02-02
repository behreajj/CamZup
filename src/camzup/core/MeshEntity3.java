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
    * The list of materials held by the entity.
    */
   public final List < MaterialSolid > materials;

   /**
    * The list of meshes held by the entity.
    */
   public final List < Mesh3 > meshes;

   {
      this.materials = new ArrayList <>();
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
    * Creates a string representing a Wavefront OBJ file.
    *
    * @return the string
    */
   String toObjString () {

      // TODO: Needs testing. Like CurveEntity3, indices
      // may need to be offset for multiple objects.
      final StringBuilder result = new StringBuilder();
      for (final Mesh3 m : this.meshes) {
         result.append(m.toObjString());
      }
      return result.toString();
   }

   /**
    * Appends a material to this mesh entity.
    *
    * @param material
    *           the material
    * @return this mesh entity
    */
   @Chainable
   public MeshEntity3 appendMaterial ( final MaterialSolid material ) {

      if (material != null) {
         this.materials.add(material);
      }
      return this;
   }

   /**
    * Appends a list of materials to this mesh entity.
    *
    * @param materials
    *           the list of materials
    * @return this mesh entity
    */
   @Chainable
   public MeshEntity3 appendMaterials ( final MaterialSolid... materials ) {

      for (final MaterialSolid mat : materials) {
         this.appendMaterial(mat);
      }
      return this;
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

         final int matLen = this.materials.size();
         if (mesh.materialIndex < 0 && matLen > 0) {
            mesh.materialIndex = matLen - 1;
         }
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
    * Gets a material from this mesh entity.
    *
    * @param i
    *           the index
    *
    * @return the material
    */
   public MaterialSolid getMaterial ( final int i ) {

      return this.materials.get(Math.floorMod(i, this.materials.size()));
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

      final float expn = 2.2f;

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

      int matIndex = 0;
      final int matLast = this.materials.size() - 1;
      if (matLast > -1) {
         final Iterator < MaterialSolid > matItr = this.materials.iterator();
         while (matItr.hasNext()) {
            result.append(matItr.next().toBlenderCode(expn));
            if (matIndex < matLast) {
               result.append(',').append(' ');
            }
            matIndex++;
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
            .append("    mesh_data.validate()\n")

            // where l is a list of tuples
            // flattened = [item for sublist in l for item in sublist]

            // .append(" normal_dat = mesh[\"normals\"]\n")
            // .append(" curr = 0\n")
            // .append(" mesh_verts = mesh_data.vertices\n")
            // .append(" for vert in mesh_verts:\n")
            // .append(" nrm_idx = face_idcs[curr]\n")
            // .append(" vert.normal = normal_dat[nrm_idx]\n")
            // .append(" curr = curr + 1\n")

            .append("    idx = mesh[\"material_index\"]\n")
            .append("    mat_name = materials[idx][\"name\"]\n")
            .append("    mesh_data.materials.append(d_mats[mat_name])\n")
            .append("    mesh_obj = d_objs.new(name, mesh_data)\n")
            .append("    scene_objs.link(mesh_obj)\n")
            .append("    mesh_obj.parent = parent_obj\n");

      return result.toString();
   }
}

package camzup.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * An entity which contains a transform that is applied to a
 * list of meshes. The meshes may references a list of
 * materials by index.
 */
public class MeshEntity3 extends Entity implements Iterable < Mesh3 > {

   /**
    * The list of materials held by the entity.
    */
   public final List < MaterialSolid > materials;

   /**
    * The list of meshes held by the entity.
    */
   public final List < Mesh3 > meshes;

   /**
    * The entity's transform.
    */
   public final Transform3 transform;

   /**
    * The order in which the entity's transform is applied to
    * the mesh.
    */
   public Transform.Order transformOrder = Transform.Order.TRS;

   {
      this.materials = new ArrayList <>();
      this.meshes = new ArrayList <>();
   }

   /**
    * The default constructor.
    */
   public MeshEntity3 () {

      super();
      this.transform = new Transform3();
   }

   /**
    * Creates a named mesh entity.
    *
    * @param name
    *           the name
    */
   public MeshEntity3 ( final String name ) {

      super(name);
      this.transform = new Transform3();
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

      super(name);
      this.transform = transform;
      for (final Mesh3 mesh : meshes) {
         if (mesh != null) {
            this.meshes.add(mesh);
         }
      }
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

      super();
      this.transform = transform;
      for (final Mesh3 mesh : meshes) {
         if (mesh != null) {
            this.meshes.add(mesh);
         }
      }
   }

   /**
    * Creates a string representing a Wavefront OBJ file.
    *
    * @return the string
    */
   protected String toObjString () {

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

      //TODO: Don't use tabs...
      final int coordPairsOnLine = 3;
      final StringBuilder result = new StringBuilder(2048);
      result.append("from bpy import data as D, context as C\n\n")
            .append("mesh_entity = {")
            .append("\n    \"name\": \"")
            .append(this.name)
            .append("\",\n    \"transform\": ")
            .append(this.transform.toBlenderCode())
            .append(",\n    \"meshes\": [\n");

      int meshIndex = 0;
      final int meshLast = this.meshes.size() - 1;
      final Iterator < Mesh3 > meshItr = this.meshes.iterator();
      while (meshItr.hasNext()) {

         final Mesh3 mesh = meshItr.next();
         final Vec3[] coords = mesh.coords;
         final int coordLen = coords.length;
         final int coordLast = coordLen - 1;

         result.append("        {\"name\": \"")
               .append(mesh.name)
               .append("\",\n         \"material_index\": ")
               .append(mesh.materialIndex)
               .append(",\n         \"vertices\": [\n");

         /* Append vertex coordinates. */
         for (int i = 0; i < coordLen; ++i) {
            final Vec3 v = coords[i];

            result.append("             (")
                  .append(Utils.toFixed(v.x, 6))
                  .append(',').append(' ')
                  .append(Utils.toFixed(v.y, 6))
                  .append(',').append(' ')
                  .append(Utils.toFixed(v.z, 6))
                  .append(')');

            if (i < coordLast) {
               result.append(',').append('\n');
            }
         }

         result.append("],\n         \"faces\": [\n             ");

         /* Append indices (for vertices only). */
         final int[][][] faces = mesh.faces;
         final int faceLen = faces.length;
         final int faceLast = faceLen - 1;

         for (int j = 0; j < faceLen; ++j) {
            final int[][] vrtInd = faces[j];
            final int vrtIndLen = vrtInd.length;
            final int vrtLast = vrtIndLen - 1;

            result.append('(');
            for (int k = 0; k < vrtIndLen; ++k) {
               result.append(vrtInd[k][0]);

               if (k < vrtLast) {
                  result.append(',').append(' ');
               }
            }
            result.append(')');

            if (j < faceLast) {
               result.append(',');

               if ((j + 1) % coordPairsOnLine == 0) {
                  result.append("\n             ");
               } else {
                  result.append(' ');
               }
            }
         }

         result.append(']').append('}');

         if (meshIndex < meshLast) {
            result.append(',').append('\n');
         }

         meshIndex++;
      }

      result.append("],\n    \"materials\": [\n");

      int matIndex = 0;
      final float expn = 2.2f;
      final int matLast = this.materials.size() - 1;
      final Iterator < MaterialSolid > matItr = this.materials.iterator();
      while (matItr.hasNext()) {
         result.append(matItr.next().toBlenderCode(expn));
         if (matIndex < matLast) {
            result.append(',').append('\n');
         }
         matIndex++;
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
            .append("\tmat_data = d_mats.new(material[\"name\"])\n")
            .append("\tfill_clr = material[\"fill\"]\n")
            .append("\tmat_data.diffuse_color = fill_clr\n")
            .append("\tmat_data.roughness = 1.0\n")
            .append("\tmat_data.use_nodes = True\n")
            .append("\tnode_tree = mat_data.node_tree\n")
            .append("\tnodes = node_tree.nodes\n")
            .append("\tpbr = nodes[\"Principled BSDF\"]\n")
            .append("\troughness = pbr.inputs[\"Roughness\"]\n")
            .append("\troughness.default_value = 1.0\n")
            .append("\tbase_clr = pbr.inputs[\"Base Color\"]\n")
            .append("\tbase_clr.default_value = fill_clr\n\n")

            .append("meshes = mesh_entity[\"meshes\"]\n")
            .append("d_meshes = D.meshes\n")
            .append("for mesh in meshes:\n")
            .append("\tname = mesh[\"name\"]\n")
            .append("\tmesh_data = d_meshes.new(name)\n")
            .append("\tmesh_data.from_pydata(\n")
            .append("\t\tmesh[\"vertices\"],\n")
            .append("\t\t[],\n")
            .append("\t\tmesh[\"faces\"])\n")
            .append("\tmesh_data.validate()\n")
            .append("\tidx = mesh[\"material_index\"]\n")
            .append("\tmat_name = materials[idx][\"name\"]\n")
            .append("\tmesh_data.materials.append(d_mats[mat_name])\n")
            .append("\tmesh_obj = d_objs.new(name, mesh_data)\n")
            .append("\tscene_objs.link(mesh_obj)\n")
            .append("\tmesh_obj.parent = parent_obj\n");

      return result.toString();
   }
}

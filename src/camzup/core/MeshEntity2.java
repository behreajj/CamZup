package camzup.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * An entity which contains a transform that is applied to a
 * list of meshes. The meshes may references a list of
 * materials by index.
 */
public class MeshEntity2 extends Entity2 implements Iterable < Mesh2 > {

   /**
    * The list of materials held by the entity.
    */
   public final List < MaterialSolid > materials;

   /**
    * The list of meshes held by the entity.
    */
   public final List < Mesh2 > meshes;

   {
      this.materials = new ArrayList <>();
      this.meshes = new ArrayList <>();
   }

   /**
    * The default constructor.
    */
   public MeshEntity2 () {

      super();
   }

   /**
    * Creates a named mesh entity.
    *
    * @param name
    *           the name
    */
   public MeshEntity2 ( final String name ) {

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
   public MeshEntity2 (
         final String name,
         final Transform2 transform,
         final Mesh2... meshes ) {

      super(name, transform);
      for (final Mesh2 mesh : meshes) {
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
   public MeshEntity2 (
         final Transform2 transform,
         final Mesh2... meshes ) {

      super(transform);
      this.appendMeshes(meshes);
   }

   /**
    * Appends a material to this mesh entity.
    *
    * @param material
    *           the material
    * @return this mesh entity
    */
   @Chainable
   public MeshEntity2 appendMaterial ( final MaterialSolid material ) {

      // TODO: Guard against duplicates?
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
   public MeshEntity2 appendMaterials ( final MaterialSolid... materials ) {

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
   public MeshEntity2 appendMesh ( final Mesh2 mesh ) {

      // TODO: Guard against duplicates?
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
   public MeshEntity2 appendMeshes ( final Mesh2... meshes ) {

      for (final Mesh2 m : meshes) {
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
   public Mesh2 getMesh ( final int i ) {

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
   public Iterator < Mesh2 > iterator () {

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
      final Iterator < Mesh2 > meshItr = this.meshes.iterator();
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
            // .append(" specular = pbr.inputs[\"Specular\"]")
            // .append(" specular.default_value = 0.000001\n")
            .append("    base_clr = pbr.inputs[\"Base Color\"]\n")
            .append("    base_clr.default_value = fill_clr\n\n")

            .append("meshes = mesh_entity[\"meshes\"]\n")
            .append("d_meshes = D.meshes\n")
            .append("for mesh in meshes:\n")
            .append("    name = mesh[\"name\"]\n")
            .append("    mesh_data = d_meshes.new(name)\n")
            .append("    mesh_data.from_pydata(\n")
            .append("        mesh[\"vertices\"],\n")
            .append("        [],\n")
            .append("        mesh[\"faces\"])\n")
            .append("    mesh_data.validate()\n")
            .append("    idx = mesh[\"material_index\"]\n")
            .append("    mat_name = materials[idx][\"name\"]\n")
            .append("    mesh_data.materials.append(d_mats[mat_name])\n")
            .append("    mesh_obj = d_objs.new(name, mesh_data)\n")
            .append("    scene_objs.link(mesh_obj)\n")
            .append("    mesh_obj.parent = parent_obj\n");

      return result.toString();
   }

   /**
    * Creates a string representing a group node in the SVG
    * format.
    *
    * @return the string
    */
   public String toSvgString () {

      final StringBuilder result = new StringBuilder()
            .append("<g id=\"")
            .append(this.name.toLowerCase())
            .append("\" ")
            .append(this.transform.toSvgString())
            .append(">\n");

      final float scale = Transform2.minDimension(this.transform);
      final boolean includesMats = this.materials.size() > 0;

      /*
       * If no materials are present, use a default one instead.
       */
      if (!includesMats) {
         result.append(MaterialSolid.defaultSvgMaterial(scale));
      }

      final Iterator < Mesh2 > meshItr = this.meshes.iterator();
      while (meshItr.hasNext()) {
         final Mesh2 mesh = meshItr.next();

         /*
          * It would be more efficient to create a defs block that
          * contains the data for each material, which is then used
          * by a mesh element with xlink, but such tags are ignored
          * when Processing imports an SVG with loadShape.
          */
         if (includesMats) {
            final MaterialSolid material = this.materials
                  .get(mesh.materialIndex);
            result.append("<g ")
                  .append(material.toSvgString())
                  .append(">\n");
         }

         result.append(mesh.toSvgString())
               .append('\n');

         /* Close out material group. */
         if (includesMats) {
            result.append("</g>\n");
         }
      }

      /* Close out default material group. */
      if (!includesMats) {
         result.append("</g>\n");
      }

      result.append("</g>");
      return result.toString();
   }
}

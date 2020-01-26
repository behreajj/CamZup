package camzup.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * An entity which contains a transform that is applied to a
 * list of meshes. The meshes may references a list of
 * materials by index.
 */
public class MeshEntity2 extends Entity implements Iterable < Mesh2 > {

   /**
    * The list of materials held by the entity.
    */
   public final List < MaterialSolid > materials;

   /**
    * The list of meshes held by the entity.
    */
   public final List < Mesh2 > meshes;

   /**
    * The entity's transform.
    */
   public final Transform2 transform;

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
   public MeshEntity2 () {

      super();
      this.transform = new Transform2();
   }

   /**
    * Creates a named mesh entity.
    *
    * @param name
    *           the name
    */
   public MeshEntity2 ( final String name ) {

      super(name);
      this.transform = new Transform2();
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

      super(name);
      this.transform = transform;
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

      super();
      this.transform = transform;
      for (final Mesh2 mesh : meshes) {
         if (mesh != null) {
            this.meshes.add(mesh);
         }
      }
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
   
   @Experimental
   public String toBlenderCode () {

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
      final Iterator < Mesh2 > meshItr = this.meshes.iterator();
      while (meshItr.hasNext()) {

         final Mesh2 mesh = meshItr.next();
         final Vec2[] coords = mesh.coords;
         final int coordLen = coords.length;
         final int coordLast = coordLen - 1;

         result.append("        {\"name\": \"")
               .append(mesh.name)
               .append("\",\n         \"vertices\": [\n");

         /* Append vertex coordinates. */
         for (int i = 0; i < coordLen; ++i) {
            final Vec2 v = coords[i];

            result.append("             (")
                  .append(Utils.toFixed(v.x, 6))
                  .append(',').append(' ')
                  .append(Utils.toFixed(v.y, 6))
                  .append(", 0.0)");

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
            result.append(',').append('\n').append('\n');
         }

         meshIndex++;
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
            .append("\tmesh_obj = d_objs.new(name, mesh_data)\n")
            .append("\tscene_objs.link(mesh_obj)\n")
            .append("\tmesh_obj.parent = parent_obj\n");

      return result.toString();
   }
}

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

      // TODO: Use mesh iterator.

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

      for (final Mesh2 mesh : this.meshes) {

         if (includesMats) {

            /*
             * It would be more efficient to create a defs block that
             * contains the data for each material, which is then used
             * by a mesh element with xlink, but such tags are ignored
             * when Processing imports an SVG with loadShape.
             */
            final MaterialSolid material = this.materials
                  .get(mesh.materialIndex);
            result.append("<g ")
                  .append(material.toSvgString())
                  .append(">\n");
         }

         result.append(mesh.toSvgString())
               .append("</g>\n");

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

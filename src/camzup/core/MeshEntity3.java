package camzup.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * An entity which contains a transform that is applied to a list of
 * meshes.
 */
public class MeshEntity3 extends Entity3 implements Iterable < Mesh3 >,
   IVolume3, IBlenderWritable {

   /**
    * The list of meshes held by the entity.
    */
   public final ArrayList < Mesh3 > meshes = new ArrayList <>(
      IEntity.DEFAULT_CAPACITY);

   /**
    * The default constructor.
    */
   public MeshEntity3 ( ) {}

   /**
    * Creates a mesh entity from a list of meshes.
    *
    * @param meshes the list of meshes
    */
   public MeshEntity3 ( final Mesh3... meshes ) {

      this.appendAll(meshes);
   }

   /**
    * Creates a named mesh entity.
    *
    * @param name the name
    */
   public MeshEntity3 ( final String name ) { super(name); }

   /**
    * Creates a mesh entity from a name and list of meshes.
    *
    * @param name   the name
    * @param meshes the list of meshes
    */
   public MeshEntity3 ( final String name, final Mesh3... meshes ) {

      super(name, new Transform3());
      this.appendAll(meshes);
   }

   /**
    * Creates a mesh entity from a name, transform and list of meshes.
    *
    * @param name      the name
    * @param transform the transform
    * @param meshes    the list of meshes
    */
   public MeshEntity3 ( final String name, final Transform3 transform,
      final Mesh3... meshes ) {

      super(name, transform);
      this.appendAll(meshes);
   }

   /**
    * Creates a mesh entity from a transform and list of meshes.
    *
    * @param transform the transform
    * @param meshes    the list of meshes
    */
   public MeshEntity3 ( final Transform3 transform, final Mesh3... meshes ) {

      super(transform);
      this.appendAll(meshes);
   }

   /**
    * Appends a mesh to this mesh entity. The mesh must not be
    * <code>null</code> and must have a length greater than zero.
    *
    * @param mesh the mesh
    *
    * @return this mesh entity
    */
   public MeshEntity3 append ( final Mesh3 mesh ) {

      this.meshes.add(mesh);
      return this;
   }

   /**
    * Appends a collection of meshes to this mesh entity.
    *
    * @param app the meshes
    *
    * @return this mesh entity
    */
   public MeshEntity3 appendAll ( final Collection < Mesh3 > app ) {

      final Iterator < Mesh3 > itr = app.iterator();
      while ( itr.hasNext() ) { this.append(itr.next()); }
      return this;
   }

   /**
    * Appends a list of meshes to this mesh entity.
    *
    * @param app the list of meshes
    *
    * @return this mesh entity
    */
   public MeshEntity3 appendAll ( final Mesh3... app ) {

      final int len = app.length;
      for ( int i = 0; i < len; ++i ) { this.append(app[i]); }
      return this;
   }

   /**
    * Transforms all meshes in this mesh entity by its transform, then resets
    * the entity's transform to the identity.
    *
    * @return this mesh entity
    *
    * @see Mesh3#transform(Transform3)
    * @see Transform3#identity(Transform3)
    */
   public MeshEntity3 consumeTransform ( ) {

      final Iterator < Mesh3 > itr = this.meshes.iterator();
      while ( itr.hasNext() ) { itr.next().transform(this.transform); }
      Transform3.identity(this.transform);
      return this;
   }

   /**
    * Tests this entity for equivalence with another object.
    *
    * @param obj the object
    *
    * @return the evaluation
    */
   @Override
   public boolean equals ( final Object obj ) {

      if ( this == obj ) { return true; }
      if ( !super.equals(obj) ) { return false; }
      if ( this.getClass() != obj.getClass() ) { return false; }
      final MeshEntity3 other = ( MeshEntity3 ) obj;
      return this.meshes.equals(other.meshes);
   }

   /**
    * Gets a mesh from this mesh entity.
    *
    * @param i the index
    *
    * @return the mesh
    */
   public Mesh3 get ( final int i ) {

      return this.meshes.get(Utils.mod(i, this.meshes.size()));
   }

   /**
    * Gets this mesh entity's scale.
    *
    * @param target the output vector
    *
    * @return the scale
    */
   @Override
   public Vec3 getScale ( final Vec3 target ) {

      return this.transform.getScale(target);
   }

   /**
    * Returns a hash code for this entity based on its array of entity data.
    *
    * @return the hash
    */
   @Override
   public int hashCode ( ) {

      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + this.meshes.hashCode();
      return result;
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
   public Iterator < Mesh3 > iterator ( ) { return this.meshes.iterator(); }

   /**
    * Gets the number of meshes held by the entity.
    *
    * @return the length
    */
   public int length ( ) { return this.meshes.size(); }

   /**
    * Centers and rescales all curves in the mesh entity about a shared origin
    * by calculating their dimensions, subtracting the center point, and
    * scaling by the maximum dimension.
    *
    * @return this mesh entity.
    */
   public MeshEntity3 reframe ( ) {

      /* Find lower and upper bound for all curves. */
      final Vec3 lb = new Vec3(Float.MAX_VALUE, Float.MAX_VALUE,
         Float.MAX_VALUE);
      final Vec3 ub = new Vec3(Float.MIN_VALUE, Float.MIN_VALUE,
         Float.MIN_VALUE);

      Iterator < Mesh3 > itr = this.meshes.iterator();
      while ( itr.hasNext() ) { Mesh3.accumMinMax(itr.next(), lb, ub); }

      lb.x = -0.5f * ( lb.x + ub.x );
      lb.y = -0.5f * ( lb.y + ub.y );
      lb.z = -0.5f * ( lb.z + ub.z );
      final float scl = Utils.div(1.0f, Utils.max(ub.x - lb.x, ub.y - lb.y, ub.z
         - lb.z));

      itr = this.meshes.iterator();
      while ( itr.hasNext() ) {
         final Mesh3 mesh = itr.next();
         mesh.translate(lb);
         mesh.scale(scl);
      }

      return this;
   }

   /**
    * Removes a mesh from this mesh entity. Returns <code>true</code> if this
    * request was filled, <code>false</code> if not.
    *
    * @param mesh the mesh to remove.
    *
    * @return the evaluation
    */
   public boolean remove ( final Mesh3 mesh ) {

      return this.meshes.remove(mesh);
   }

   /**
    * Removes a mesh at a given index.
    *
    * @param i the index
    *
    * @return the mesh
    */
   public Mesh3 removeAt ( final int i ) { return this.meshes.remove(i); }

   /**
    * Resets the mesh entity to an initial state. Sets the transform to an
    * identity and clears the list of meshes.
    *
    * @return this entity
    */
   @Override
   public MeshEntity3 reset ( ) {

      super.reset();
      this.meshes.clear();
      return this;
   }

   /**
    * Scales the entity by a scalar.
    *
    * @param scalar the scalar
    *
    * @return this entity
    */
   @Override
   public MeshEntity3 scaleBy ( final float scalar ) {

      this.transform.scaleBy(scalar);
      return this;
   }

   /**
    * Scales the entity by a non-uniform scalar.<br>
    * <br>
    * Beware that non-uniform scaling may lead to improper shading of a mesh
    * when lit.
    *
    * @param scalar the scalar
    *
    * @return the entity
    */
   @Override
   public MeshEntity3 scaleBy ( final Vec3 scalar ) {

      this.transform.scaleBy(scalar);
      return this;
   }

   /**
    * Scales the entity to a uniform size.
    *
    * @param scaleNew the size
    *
    * @return this entity
    */
   @Override
   public MeshEntity3 scaleTo ( final float scaleNew ) {

      this.transform.scaleTo(scaleNew);
      return this;
   }

   /**
    * Scales the entity to a non-uniform size.<br>
    * <br>
    * Beware that non-uniform scaling may lead to improper shading of a mesh
    * when lit.
    *
    * @param scaleNew the size
    *
    * @return this entity
    */
   @Override
   public MeshEntity3 scaleTo ( final Vec3 scaleNew ) {

      this.transform.scaleTo(scaleNew);
      return this;
   }

   /**
    * Eases the entity to a scale by a step over time.<br>
    * <br>
    * Beware that non-uniform scaling may lead to improper shading of a mesh
    * when lit.
    *
    * @param scalar the scalar
    * @param step   the step
    *
    * @return this entity
    */
   @Override
   public MeshEntity3 scaleTo ( final Vec3 scalar, final float step ) {

      this.transform.scaleTo(scalar, step);
      return this;
   }

   /**
    * Sets a coordinate in a mesh. Multiplies the input coordinate in global
    * space by the transform's inverse.<br>
    * <br>
    * To facilitate editing the mesh with a graphical user interface (GUI).
    *
    * @param meshIndex  the mesh index
    * @param coordIndex the coordinate index
    * @param global     the point in global space
    * @param local      the point in mesh local space
    *
    * @return this entity
    */
   @Experimental
   public MeshEntity3 setCoord ( final int meshIndex, final int coordIndex,
      final Vec3 global, final Vec3 local ) {

      Transform3.invMulPoint(this.transform, global, local);
      final Vec3[] coords = this.get(meshIndex).coords;
      final int j = Utils.mod(coordIndex, coords.length);
      coords[j].set(local);
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
    * This code is brittle and is used for internal testing purposes, i.e., to
    * compare how mesh geometry looks in Blender (the control) versus in the
    * library (the test).
    *
    * @param useAutoSmooth  auto smooth normals
    * @param autoAngle      auto smooth angle
    * @param materials      the materials
    * @param gamma          color gamma adjustment
    * @param metallic       metallic factor
    * @param roughness      roughness factor
    * @param specular       specular highlight strength
    * @param clearcoat      clear coat factor
    * @param clearcoatRough clear coat roughness
    *
    * @return the string
    */
   @Experimental
   public String toBlenderCode ( final boolean useAutoSmooth,
      final float autoAngle, final MaterialSolid[] materials, final float gamma,
      final float metallic, final float roughness, final float specular,
      final float clearcoat, final float clearcoatRough ) {

      final StringBuilder pyCd = new StringBuilder(2048);
      pyCd.append("from bpy import context as C, data as D\nimport bmesh\n\n");
      pyCd.append("entity_src = {\"name\": \"");
      pyCd.append(this.name);
      pyCd.append("\", \"transform\": ");
      this.transform.toBlenderCode(pyCd);
      pyCd.append(", \"meshes\": [");

      /* Append meshes. */
      final Iterator < Mesh3 > meshItr = this.meshes.iterator();
      while ( meshItr.hasNext() ) {
         meshItr.next().toBlenderCode(pyCd, false, true, false);
         if ( meshItr.hasNext() ) { pyCd.append(',').append(' '); }
      }

      /* Append materials. */
      final boolean useMaterials = materials != null && materials.length > 0;
      pyCd.append("], \"materials\": [");
      if ( useMaterials ) {
         final int matLen = materials.length;
         final int matLast = matLen - 1;
         for ( int i = 0; i < matLast; ++i ) {
            materials[i].toBlenderCode(pyCd, gamma, metallic, roughness,
               specular, clearcoat, clearcoatRough);
            pyCd.append(',').append(' ');
         }
         materials[matLast].toBlenderCode(pyCd, gamma, metallic, roughness,
            specular, clearcoat, clearcoatRough);
      } else {
         MaterialSolid.defaultBlenderMaterial(pyCd, gamma);
      }
      pyCd.append("]}\n\n");

      this.genParentBoilerPlate(pyCd);
      this.genMaterialBoilerPlate(pyCd);
      this.genMeshBoilerPlate(pyCd, false, useAutoSmooth, autoAngle);

      /* Add materials to mesh data. */
      pyCd.append("    md_mats = mesh_data.materials\n");
      pyCd.append("    md_mats.clear()\n");
      if ( useMaterials ) {
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

      return pyCd.toString();
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

      return this.toBlenderCode(true, 0.523599f, ms, 1.0f, 0.0f, 1.0f, 0.0f,
         0.0f, 0.0001f);
   }

   /**
    * Renders the mesh entity as a string following the Wavefront OBJ file
    * format.
    *
    * @return the string
    */
   public String toObjString ( ) {

      final StringBuilder objs = new StringBuilder(2048);
      final Iterator < Mesh3 > itr = this.meshes.iterator();

      /* Append name. */
      objs.append('o');
      objs.append(' ');
      objs.append(this.name);
      objs.append('\n');

      int vIdx = 1;
      int vtIdx = 1;
      int vnIdx = 1;
      int fCount = 0;
      while ( itr.hasNext() ) {
         final Mesh3 mesh = itr.next();
         mesh.toObjString(objs, vIdx, vtIdx, vnIdx, 0, true);
         if ( itr.hasNext() ) { objs.append('\n'); }

         vIdx += mesh.coords.length;
         vtIdx += mesh.texCoords.length;
         vnIdx += mesh.normals.length;
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
    * Centers all meshes in the mesh entity about a shared origin by
    * calculating their dimensions then subtracting the center point.
    *
    * @return this mesh entity.
    */
   public MeshEntity3 toOrigin ( ) {

      /* Find lower and upper bound for all curves. */
      final Vec3 lb = new Vec3(Float.MAX_VALUE, Float.MAX_VALUE,
         Float.MAX_VALUE);
      final Vec3 ub = new Vec3(Float.MIN_VALUE, Float.MIN_VALUE,
         Float.MIN_VALUE);
      Iterator < Mesh3 > itr = this.meshes.iterator();
      while ( itr.hasNext() ) { Mesh3.accumMinMax(itr.next(), lb, ub); }

      /* Shift curves. */
      lb.x = -0.5f * ( lb.x + ub.x );
      lb.y = -0.5f * ( lb.y + ub.y );
      lb.z = -0.5f * ( lb.z + ub.z );

      itr = this.meshes.iterator();
      while ( itr.hasNext() ) { itr.next().translate(lb); }

      return this;
   }

   /**
    * Returns a string representation of this mesh entity.
    *
    * @param places number of places
    *
    * @return the string
    */
   @Override
   public String toString ( final int places ) {

      final StringBuilder sb = new StringBuilder(1024);
      sb.append("{ name: \"");
      sb.append(this.name);
      sb.append('\"');
      sb.append(", transform: ");
      sb.append(this.transform.toString(places));
      sb.append(", meshes: [ ");

      final Iterator < Mesh3 > itr = this.meshes.iterator();
      while ( itr.hasNext() ) {
         sb.append(itr.next().toString(places));
         if ( itr.hasNext() ) { sb.append(',').append(' '); }
      }

      sb.append(" ] }");
      return sb.toString();
   }

}

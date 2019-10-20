package camzup.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * An entity which contains a transform that is applied to a
 * list of meshes. The meshes may references a list of
 * materials by index.
 */
public class MeshEntity3 extends Entity implements Iterable < Mesh3 > {

   /**
    * A helper function for parsing an OBJ file. Expunges loose
    * lists of data into fixed-sized arrays which belong to a
    * mesh.
    *
    * @param faceList
    *           the array list of face data
    * @param coordList
    *           the array list of coordinates
    * @param texCoordList
    *           the array list of texture coordinates
    * @param normalList
    *           the array list of normals
    * @param missingCoords
    *           are vert coord indices missing
    * @param missingUvs
    *           are uv indices missing
    * @param missingNormals
    *           are normal indices missing
    * @param mesh
    *           the mesh
    * @param meshEntity
    *           the mesh entity
    */
   protected static void dischargeMesh (
         final ArrayList < int[][] > faceList,
         final ArrayList < Vec3 > coordList,
         final ArrayList < Vec2 > texCoordList,
         final ArrayList < Vec3 > normalList,

         final boolean missingCoords,
         final boolean missingUvs,
         final boolean missingNormals,

         Mesh3 mesh,
         final MeshEntity3 meshEntity ) {

      /*
       * If a missing flag has been thrown, make sure that there
       * is at least one datum in each list.
       */
      if (missingNormals && normalList.size() == 0) {
         normalList.add(Vec3.forward(new Vec3()));
      }

      if (missingUvs && texCoordList.size() == 0) {
         texCoordList.add(Vec2.uvCenter(new Vec2()));
      }

      if (missingCoords && coordList.size() == 0) {
         coordList.add(new Vec3());
      }

      /* Discharge lists into arrays */
      mesh.set(
            faceList.toArray(new int[faceList.size()][][]),
            coordList.toArray(new Vec3[coordList.size()]),
            texCoordList.toArray(new Vec2[texCoordList.size()]),
            normalList.toArray(new Vec3[normalList.size()]));

      /* Add mesh to mesh entity. */
      meshEntity.appendMesh(mesh);

      /* Create a new mesh. */
      mesh = new Mesh3();

      /* Reset lists. */
      coordList.clear();
      texCoordList.clear();
      normalList.clear();
      faceList.clear();
   }

   protected static MeshEntity3 fromObj ( final String[] lines,
         final MeshEntity3 target ) {

      // TODO: Needs testing.
      target.meshes.clear();
      // target.transform.reset();

      final Mesh3 mesh = new Mesh3();
      final ArrayList < Vec3 > coordList = new ArrayList <>();
      final ArrayList < Vec2 > texCoordList = new ArrayList <>();
      final ArrayList < Vec3 > normalList = new ArrayList <>();
      final ArrayList < int[][] > faceList = new ArrayList <>();

      final int len = lines.length;
      int meshCount = 0;
      String[] tokens;
      String[] facetokens;

      /**
       * Flags to be thrown if, in the faces section of the OBJ
       * file, there is missing index information.
       */
      boolean missingNormals = false;
      boolean missingUvs = false;
      boolean missingCoords = false;

      for (int i = 0; i < len; ++i) {

         /* Split line by spaces. */
         tokens = lines[i].split("\\s+");

         /* Skip empty lines. */
         if (tokens.length > 0) {
            final String initialToken = tokens[0].toLowerCase();

            if (initialToken.equals("o")) {

               /*
                * If the loop has previously encountered a name token, then
                * presumably it has already accumulated face, coord, etc.
                * data that needs to be organized into a mesh.
                */
               if (meshCount > 0) {

                  MeshEntity3.dischargeMesh(
                        faceList,
                        coordList,
                        texCoordList,
                        normalList,

                        missingCoords,
                        missingUvs,
                        missingNormals,

                        mesh, target);

                  /* Reset missing data flags */
                  missingNormals = false;
                  missingUvs = false;
                  missingCoords = false;
               }

               /*
                * Name the current mesh under consideration, increase the
                * mesh count.
                */
               mesh.name = tokens[1];
               meshCount++;

            } else if (initialToken.equals("v")) {

               /* Coordinate. */
               coordList.add(
                     new Vec3(
                           tokens[1],
                           tokens[2],
                           tokens[3]));

            } else if (initialToken.equals("vt")) {

               /* Texture coordinate. */
               texCoordList.add(
                     new Vec2(
                           tokens[1],
                           tokens[2]));

            } else if (initialToken.equals("vn")) {

               /* Normal. */
               normalList.add(
                     new Vec3(
                           tokens[1],
                           tokens[2],
                           tokens[3]));

            } else if (initialToken.equals("f")) {

               /* Face. */
               final int count = tokens.length;

               /*
                * The tokens' length includes "f", and so is 1 longer than
                * the length of the array of data.
                */
               final int[][] indices = new int[count - 1][3];

               /*
                * Simplified version. Assumes (incorrectly) that face will
                * always be formatted as "v/vt/vn".
                */
               for (int j = 1, k = 0; j < count; ++j, ++k) {
                  final int[] dt = indices[k];

                  facetokens = tokens[j].split("/");
                  final int ftlen = facetokens.length;

                  /* Indices in .obj file start at 1, not 0. */
                  switch (ftlen) {

                     case 3:

                        dt[0] = MeshEntity3.intFromStr(facetokens[0]) - 1;
                        dt[1] = MeshEntity3.intFromStr(facetokens[1]) - 1;
                        dt[2] = MeshEntity3.intFromStr(facetokens[2]) - 1;

                        break;

                     case 2:

                        dt[0] = MeshEntity3.intFromStr(facetokens[0]) - 1;
                        dt[1] = MeshEntity3.intFromStr(facetokens[1]) - 1;
                        dt[2] = 0;

                        missingNormals = true;

                        break;

                     case 1:

                        dt[0] = MeshEntity3.intFromStr(facetokens[0]) - 1;
                        dt[1] = 0;
                        dt[2] = 0;

                        missingUvs = true;
                        missingNormals = true;

                        break;

                     case 0:

                     default:

                        dt[0] = 0;
                        dt[1] = 0;
                        dt[2] = 0;

                        missingCoords = true;
                        missingUvs = true;
                        missingNormals = true;

                  } /* End length of data case. */
               } /* End data at vertex '/' token loop. */
               faceList.add(indices);
            } /* End initial token case */
         } /* End blank line check */
      } /* End obj lines loop */

      /**
       * If there was only one mesh in the OBJ file, then it would
       * have not been cashed out by the for-loop.
       */
      if (meshCount == 1) {
         MeshEntity3.dischargeMesh(
               faceList,
               coordList,
               texCoordList,
               normalList,

               missingCoords,
               missingUvs,
               missingNormals,

               mesh, target);
      }

      return target;
   }

   /**
    * A helper function for parsing an OBJ file. Attempts to
    * convert a string to an integer.
    *
    * @param i
    *           the string
    * @return the integer
    */
   protected static int intFromStr ( final String i ) {

      int target = 0;
      try {
         target = Integer.parseInt(i);
      } catch (final NumberFormatException e) {
         target = 0;
      }
      return target;
   }

   /**
    * The list of materials held by the entity.
    */
   public final LinkedList < MaterialSolid > materials = new LinkedList <>();

   /**
    * The list of meshes held by the entity.
    */
   public final LinkedList < Mesh3 > meshes = new LinkedList <>();

   /**
    * The entity's transform.
    */
   public final Transform3 transform;

   /**
    * The order in which the entity's transform is applied to
    * the mesh.
    */
   public Transform.Order transformOrder = Transform.Order.TRS;

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
   public MeshEntity3 ( final String name, final Transform3 transform,
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
   public MeshEntity3 ( final Transform3 transform,
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
   public MeshEntity3 appendMaterial ( final MaterialSolid... materials ) {

      for (final MaterialSolid mat : materials) {
         if (mat != null) {
            this.materials.add(mat);
         }
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
   public MeshEntity3 appendMesh ( final Mesh3... meshes ) {

      for (final Mesh3 m : meshes) {
         if (m != null) {
            this.meshes.add(m);
         }
      }
      return this;
   }

   @Override
   public Iterator < Mesh3 > iterator () {

      return this.meshes.iterator();
   }

   /**
    * Creates a string representing a Wavefront OBJ file.
    *
    * @return the string
    */
   public String toObjString () {

      // TODO: Needs testing. Like CurveEntity3, indices
      // may need to be offset for multiple objects.
      final StringBuilder result = new StringBuilder();
      for (final Mesh3 m : this.meshes) {
         result.append(m.toObjString());
      }
      return result.toString();
   }
}

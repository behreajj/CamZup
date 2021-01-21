package camzup.core;

import java.io.BufferedReader;
import java.io.FileReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.regex.Pattern;

/**
 * A Wavefront .obj file parser class. Only deals with the portion of the
 * file specification that records meshes, not curve surfaces or
 * poly-lines. References to materials are ignored.
 */
public abstract class ParserObj {

   /**
    * Private constructor for abstract class.
    */
   private ParserObj ( ) {}

   /**
    * Creates a mesh entity from a BufferedReader that references a WaveFront
    * .obj file with groups.<br>
    * <br>
    * Material data from a .mtl file is not parsed by this function. Each new
    * material usage should be preceded by a new group. (In Blender's obj
    * exporter, check "Objects as Material Groups" true.)<br>
    * <br>
    * Because vertex groups are not supported by the Mesh3 class, an option to
    * pool data is provided. If data is pooled between meshes, then all will
    * reference the same coordinate, texture coordinate and normal array. If
    * not, each mesh will receive a copy of the data parsed from the .obj
    * file; the mesh will then be cleaned to remove unused data.
    *
    * @param in       buffered reader
    * @param poolData whether to share data
    *
    * @return the mesh entity
    */
   public static MeshEntity3 load ( final BufferedReader in,
      final boolean poolData ) {

      final MeshEntity3 result = new MeshEntity3();

      /*
       * If no initial capacity is supplied, then the hash map maximum capacity,
       * 1073741824, is used. 0.75f is the default load factor. Initial capacity
       * must be a power of two. An array list's default capacity is 10. A
       * default UV sphere has 514 vs and 1024 fs; a cube sphere, 386 vs and 768
       * fs; an icosphere, 642 vs and 1280 fs.
       */
      final int groupCapacity = 512;
      final int dataCapacity = 512;
      final int indicesCapacity = 512;

      final HashMap < String, ArrayList < int[][] > > faceGroups
         = new HashMap <>(groupCapacity, 0.75f);
      final ArrayList < Vec3 > coordList = new ArrayList <>(dataCapacity);
      final ArrayList < Vec2 > texCoordList = new ArrayList <>(dataCapacity);
      final ArrayList < Vec3 > normalList = new ArrayList <>(dataCapacity);
      final ArrayList < String > materialNames = new ArrayList <>(8);
      ArrayList < int[][] > currentIndices = new ArrayList <>(indicesCapacity);

      boolean vsMissing = false;
      boolean vtsMissing = false;
      boolean vnsMissing = false;
      boolean groupsMissing = true;
      boolean mtlLibRef = false;
      boolean usesMaterial = false;

      String[] tokens;
      String[] faceTokens;
      String mtlFileName = "";

      final Pattern spacePattern = Pattern.compile("\\s+");
      final Pattern fslashPattern = Pattern.compile("/");

      /*
       * Nested try blocks to ensure that BufferedReader is closed even if an
       * error is caught. Alternatively, research try () with resources.
       */
      try {
         try {
            for ( String ln = in.readLine(); ln != null; ln = in.readLine() ) {
               tokens = spacePattern.split(ln, 0);

               if ( tokens.length > 0 ) {

                  /* Switch case by hash code of String, not String itself. */
                  final int cmd = tokens[0].toLowerCase().hashCode();
                  switch ( cmd ) {

                     case -1063936832: /* "mtllib" */
                        mtlLibRef = true;
                        mtlFileName = tokens[1];

                        break;

                     case -836034370: /* "usemtl" */
                        usesMaterial = true;
                        materialNames.add(tokens[1]);

                        break;

                     case 102: /* "f" */
                        if ( currentIndices == null ) { break; }

                        /* tokens length includes "f", and so is 1 longer. */
                        final int count = tokens.length;
                        final int[][] indices = new int[count - 1][3];

                        for ( int j = 1; j < count; ++j ) {
                           faceTokens = fslashPattern.split(tokens[j], 0);
                           final int tokenLen = faceTokens.length;
                           final int[] vert = indices[j - 1];

                           /* Indices in .obj file start at 1, not 0. */
                           if ( tokenLen > 0 ) {
                              final String vIdx = faceTokens[0];
                              if ( vIdx == null || vIdx.isEmpty() ) {
                                 vsMissing = true;
                              } else {
                                 vert[0] = Integer.parseInt(vIdx) - 1;
                              }
                           } else {
                              vsMissing = true;
                           }

                           /* Attempt to read texture coordinate index. */
                           if ( tokenLen > 1 ) {
                              final String vtIdx = faceTokens[1];
                              if ( vtIdx == null || vtIdx.isEmpty() ) {
                                 vtsMissing = true;
                              } else {
                                 vert[1] = Integer.parseInt(vtIdx) - 1;
                              }
                           } else {
                              vtsMissing = true;
                           }

                           /* Attempt to read normal index. */
                           if ( tokenLen > 2 ) {
                              final String vnIdx = faceTokens[2];
                              if ( vnIdx == null || vnIdx.isEmpty() ) {
                                 vnsMissing = true;
                              } else {
                                 vert[2] = Integer.parseInt(vnIdx) - 1;
                              }
                           } else {
                              vnsMissing = true;
                           }
                        }

                        currentIndices.add(indices);

                        break;

                     case 103: /* "g" */
                        String gName = tokens[1];
                        if ( gName == null || gName.isEmpty() ) {
                           gName = Long.toHexString(System.currentTimeMillis());
                        }

                        if ( !faceGroups.containsKey(gName) ) {
                           faceGroups.put(gName, new ArrayList <>(
                              indicesCapacity));
                        }
                        currentIndices = faceGroups.get(gName);
                        groupsMissing = false;

                        break;

                     case 118: /* "v" */
                        coordList.add(new Vec3(Float.parseFloat(tokens[1]),
                           Float.parseFloat(tokens[2]), Float.parseFloat(
                              tokens[3])));

                        break;

                     case 3768: /* "vn" */
                        normalList.add(new Vec3(Float.parseFloat(tokens[1]),
                           Float.parseFloat(tokens[2]), Float.parseFloat(
                              tokens[3])));

                        break;

                     case 3774: /* "vt" */
                        texCoordList.add(new Vec2(Float.parseFloat(tokens[1]),
                           Float.parseFloat(tokens[2])));

                        break;

                     default:
                  }
               }
            }
         } catch ( final Exception e ) {
            e.printStackTrace();
         } finally {
            in.close();
         }
      } catch ( final Exception e ) {
         e.printStackTrace();
      }

      /* Convert to fixed-sized array. */
      Vec3[] coordArr = new Vec3[coordList.size()];
      if ( vsMissing && coordArr.length < 1 ) {
         coordArr = new Vec3[] { Vec3.zero(new Vec3()) };
      } else {
         coordList.toArray(coordArr);
      }

      Vec2[] texCoordArr = new Vec2[texCoordList.size()];
      if ( vtsMissing && texCoordArr.length < 1 ) {
         texCoordArr = new Vec2[] { Vec2.uvCenter(new Vec2()) };
      } else {
         texCoordList.toArray(texCoordArr);
      }

      Vec3[] normalArr = new Vec3[normalList.size()];
      if ( vnsMissing && normalArr.length < 1 ) {
         normalArr = new Vec3[] { Vec3.up(new Vec3()) };
      } else {
         normalList.toArray(normalArr);
      }

      /* Notify if material library was detected. */
      if ( mtlLibRef ) {
         final StringBuilder sb = new StringBuilder(512);
         sb.append("The .obj file refers to the .mtl file \"");
         sb.append(mtlFileName);
         sb.append("\".\n");

         if ( usesMaterial ) {
            sb.append("Meshes use the following materials:\n");
            final Iterator < String > matNamesItr = materialNames.iterator();
            while ( matNamesItr.hasNext() ) {
               sb.append(matNamesItr.next());
               if ( matNamesItr.hasNext() ) { sb.append(',').append('\n'); }
            }
         }
      }

      if ( groupsMissing ) {

         final Mesh3 mesh = new Mesh3("Mesh3");
         result.append(mesh);

         mesh.faces = new int[currentIndices.size()][][];
         currentIndices.toArray(mesh.faces);

         mesh.coords = coordArr;
         mesh.texCoords = texCoordArr;
         mesh.normals = normalArr;

         /* Cleaning a mesh can add significant load time. */
         // mesh.clean();

      } else {

         final int coordLen = coordArr.length;
         final int texCoordLen = texCoordArr.length;
         final int normalLen = normalArr.length;

         /* Convert from hash map to meshes. */
         final Iterator < Entry < String, ArrayList < int[][] > > > itr
            = faceGroups.entrySet().iterator();

         /* Loop over entries in dictionary. */
         while ( itr.hasNext() ) {
            final Entry < String, ArrayList < int[][] > > entry = itr.next();

            final Mesh3 mesh = new Mesh3();
            mesh.name = entry.getKey();

            final ArrayList < int[][] > facesList = entry.getValue();
            mesh.faces = new int[facesList.size()][][];
            facesList.toArray(mesh.faces);

            if ( poolData ) {

               mesh.coords = coordArr;
               mesh.texCoords = texCoordArr;
               mesh.normals = normalArr;

            } else {

               /* Copy data by value, not by reference. */
               mesh.coords = new Vec3[coordLen];
               for ( int i = 0; i < coordLen; ++i ) {
                  mesh.coords[i] = new Vec3(coordArr[i]);
               }

               mesh.texCoords = new Vec2[texCoordLen];
               for ( int j = 0; j < texCoordLen; ++j ) {
                  mesh.texCoords[j] = new Vec2(texCoordArr[j]);
               }

               mesh.normals = new Vec3[normalLen];
               for ( int k = 0; k < normalLen; ++k ) {
                  mesh.normals[k] = new Vec3(normalArr[k]);
               }

               /* Remove unused data. */
               mesh.clean();
            }

            result.append(mesh);
         }
      }

      return result;
   }

   /**
    * Creates a mesh entity from a file name that references a WaveFront .obj
    * file with groups. In doing so, makes a {@link FileReader} that is
    * wrapped in a {@link BufferedReader}.
    *
    * @param fileName the file name
    * @param poolData whether to share data
    *
    * @return the mesh entity
    *
    * @see ParserObj#load(BufferedReader, boolean)
    */
   public static MeshEntity3 load ( final String fileName,
      final boolean poolData ) {

      MeshEntity3 result = new MeshEntity3();
      try ( BufferedReader br = new BufferedReader(new FileReader(fileName)) ) {
         result = ParserObj.load(br, poolData);
      } catch ( final Exception e ) {
         e.printStackTrace();
      }
      return result;
   }

}

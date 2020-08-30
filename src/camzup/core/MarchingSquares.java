package camzup.core;

abstract class MarchingSquares {

   private MarchingSquares ( ) {}

   @Experimental
   static Mesh2 appendToMesh ( final byte config, final float xCell,
      final float yCell, final float half, final boolean fillSaddle,
      final Mesh2 target ) {

      Vec2[] vs;
      Vec2[] vts;
      int[][][] fs;

      /* @formatter:off */
         switch ( config ) {

            case 0b0001:

               vs = new Vec2[] {
                  new Vec2(xCell - half, yCell - half),
                  new Vec2(xCell       , yCell - half),
                  new Vec2(xCell - half, yCell       ) };

               vts = new Vec2[] {
                  new Vec2(0.0f, 1.0f),
                  new Vec2(0.5f, 1.0f),
                  new Vec2(0.0f, 0.5f) };

               fs = new int[][][] { { { 0, 0 }, { 1, 1 }, { 2, 2 } } };

               break;

            case 0b0010:

               vs = new Vec2[] {
                  new Vec2(xCell       , yCell - half),
                  new Vec2(xCell + half, yCell - half),
                  new Vec2(xCell + half, yCell       ) };

               vts = new Vec2[] {
                  new Vec2(0.5f, 1.0f),
                  new Vec2(1.0f, 1.0f),
                  new Vec2(1.0f, 0.5f) };

               fs = new int[][][] { { { 1, 1 }, { 2, 2 }, { 0, 0 } } };

               break;

            case 0b0011:

               vs = new Vec2[] {
                  new Vec2(xCell - half, yCell - half),
                  new Vec2(xCell + half, yCell - half),
                  new Vec2(xCell - half, yCell       ),
                  new Vec2(xCell + half, yCell       ) };

               vts = new Vec2[] {
                  new Vec2(0.0f, 1.0f),
                  new Vec2(1.0f, 1.0f),
                  new Vec2(0.0f, 0.5f),
                  new Vec2(1.0f, 0.5f) };

               fs = new int[][][] {
                  { { 0, 0 }, { 1, 1 }, { 3, 3 } },
                  { { 0, 0 }, { 3, 3 }, { 2, 2 } } };

               break;

            case 0b0100:

               vs = new Vec2[] {
                  new Vec2(xCell + half, yCell       ),
                  new Vec2(xCell       , yCell + half),
                  new Vec2(xCell + half, yCell + half) };

               vts = new Vec2[] {
                  new Vec2(1.0f, 0.5f),
                  new Vec2(0.5f, 0.0f),
                  new Vec2(1.0f, 0.0f) };

               fs = new int[][][] { { { 2, 2 }, { 1, 1 }, { 0, 0 } } };

               break;

            case 0b0101: /* 5 Saddle. */

               if ( fillSaddle ) {

                  vs = new Vec2[] {
                     new Vec2(xCell - half, yCell - half),
                     new Vec2(xCell       , yCell - half),
                     new Vec2(xCell - half, yCell       ),
                     new Vec2(xCell + half, yCell       ),
                     new Vec2(xCell       , yCell + half),
                     new Vec2(xCell + half, yCell + half) };

                  vts = new Vec2[] {
                     new Vec2(0.0f, 1.0f),
                     new Vec2(0.5f, 1.0f),
                     new Vec2(0.0f, 0.5f),
                     new Vec2(1.0f, 0.5f),
                     new Vec2(0.5f, 0.0f),
                     new Vec2(1.0f, 0.0f) };

                  fs = new int[][][] {
                     { { 0, 0 }, { 1, 1 }, { 3, 3 } },
                     { { 0, 0 }, { 4, 4 }, { 2, 2 } },
                     { { 0, 0 }, { 3, 3 }, { 5, 5 } },
                     { { 0, 0 }, { 5, 5 }, { 4, 4 } } };

               } else {

                  vs = new Vec2[] {
                     new Vec2(xCell       , yCell - half),
                     new Vec2(xCell + half, yCell - half),
                     new Vec2(xCell - half, yCell       ),
                     new Vec2(xCell + half, yCell       ),
                     new Vec2(xCell - half, yCell + half),
                     new Vec2(xCell       , yCell + half) };

                  vts = new Vec2[] {
                     new Vec2(0.5f, 1.0f),
                     new Vec2(1.0f, 1.0f),
                     new Vec2(0.0f, 0.5f),
                     new Vec2(1.0f, 0.5f),
                     new Vec2(0.0f, 0.0f),
                     new Vec2(0.5f, 0.0f) };

                  fs = new int[][][] {
                    { { 0, 0 }, { 1, 1 }, { 3, 3 } },
                    { { 5, 5 }, { 4, 4 }, { 2, 2 } } };

               }

               break;

            case 0b0110:

               vs = new Vec2[] {
                  new Vec2(xCell       , yCell - half),
                  new Vec2(xCell + half, yCell - half),
                  new Vec2(xCell       , yCell + half),
                  new Vec2(xCell + half, yCell + half) };

               vts = new Vec2[] {
                  new Vec2(0.5f, 1.0f),
                  new Vec2(1.0f, 1.0f),
                  new Vec2(0.5f, 0.0f),
                  new Vec2(1.0f, 0.0f) };

               fs = new int[][][] {
                  { { 1, 1 }, { 2, 2 }, { 0, 0 } },
                  { { 1, 1 }, { 3, 3 }, { 2, 2 } } };

               break;

            case 0b0111:

               vs = new Vec2[] {
                  new Vec2(xCell - half, yCell - half),
                  new Vec2(xCell + half, yCell - half),
                  new Vec2(xCell - half, yCell       ),
                  new Vec2(xCell       , yCell + half),
                  new Vec2(xCell + half, yCell + half) };

               vts = new Vec2[] {
                  new Vec2(0.0f, 1.0f),
                  new Vec2(1.0f, 1.0f),
                  new Vec2(0.0f, 0.5f),
                  new Vec2(0.5f, 0.0f),
                  new Vec2(1.0f, 0.0f) };

               fs = new int[][][] {
                  { { 0, 0 }, { 1, 1 }, { 4, 4 } },
                  { { 0, 0 }, { 3, 3 }, { 2, 2 } },
                  { { 0, 0 }, { 4, 4 }, { 3, 3 } } };

               break;

            case 0b1000:

               vs = new Vec2[] {
                  new Vec2(xCell - half, yCell       ),
                  new Vec2(xCell - half, yCell + half),
                  new Vec2(xCell       , yCell + half) };

               vts = new Vec2[] {
                  new Vec2(0.0f, 0.5f),
                  new Vec2(0.0f, 0.0f),
                  new Vec2(0.5f, 0.0f) };

               fs = new int[][][] { { { 1, 1 }, { 0, 0 }, { 2, 2 } } };

               break;

            case 0b1001:

               vs = new Vec2[] {
                  new Vec2(xCell - half, yCell - half),
                  new Vec2(xCell       , yCell - half),
                  new Vec2(xCell - half, yCell + half),
                  new Vec2(xCell       , yCell + half) };

               vts = new Vec2[] {
                  new Vec2(0.0f, 1.0f),
                  new Vec2(0.5f, 1.0f),
                  new Vec2(0.0f, 0.0f),
                  new Vec2(0.5f, 0.0f) };

               fs = new int[][][] {
                  { { 2, 2 }, { 0, 0 }, { 1, 1 } },
                  { { 2, 2 }, { 1, 1 }, { 3, 3 } } };

               break;

            case 0b1010: /* 10 Saddle. */

               if ( fillSaddle ) {

                  vs = new Vec2[] {
                     new Vec2(xCell       , yCell - half),
                     new Vec2(xCell + half, yCell - half),
                     new Vec2(xCell - half, yCell       ),
                     new Vec2(xCell + half, yCell       ),
                     new Vec2(xCell - half, yCell + half),
                     new Vec2(xCell       , yCell + half) };

                  vts = new Vec2[] {
                     new Vec2(0.5f, 1.0f),
                     new Vec2(1.0f, 1.0f),
                     new Vec2(0.0f, 0.5f),
                     new Vec2(1.0f, 0.5f),
                     new Vec2(0.0f, 0.0f),
                     new Vec2(0.5f, 0.0f) };

                  fs = new int[][][] {
                     { { 1, 1 }, { 2, 2 }, { 0, 0 } },
                     { { 1, 1 }, { 4, 4 }, { 2, 2 } },
                     { { 1, 1 }, { 3, 3 }, { 5, 5 } },
                     { { 1, 1 }, { 5, 5 }, { 4, 4 } } };

               } else {

                  vs = new Vec2[] {
                     new Vec2(xCell - half, yCell - half),
                     new Vec2(xCell       , yCell - half),
                     new Vec2(xCell - half, yCell       ),
                     new Vec2(xCell + half, yCell       ),
                     new Vec2(xCell       , yCell + half),
                     new Vec2(xCell + half, yCell + half) };

                  vts = new Vec2[] {
                     new Vec2(0.0f, 1.0f),
                     new Vec2(0.5f, 1.0f),
                     new Vec2(0.0f, 0.5f),
                     new Vec2(1.0f, 0.5f),
                     new Vec2(0.5f, 0.0f),
                     new Vec2(1.0f, 0.0f) };

                  fs = new int[][][] {
                    { { 0, 0 }, { 1, 1 }, { 2, 2 } },
                    { { 3, 3 }, { 5, 5 }, { 4, 4 } } };

               }

               break;

            case 0b1011:

               vs = new Vec2[] {
                  new Vec2(xCell - half, yCell - half),
                  new Vec2(xCell + half, yCell - half),
                  new Vec2(xCell + half, yCell       ),
                  new Vec2(xCell - half, yCell + half),
                  new Vec2(xCell       , yCell + half) };

               vts = new Vec2[] {
                  new Vec2(0.0f, 1.0f),
                  new Vec2(1.0f, 1.0f),
                  new Vec2(1.0f, 0.5f),
                  new Vec2(0.0f, 0.0f),
                  new Vec2(0.5f, 0.0f) };

               fs = new int[][][] {
                  { { 2, 2 }, { 0, 0 }, { 1, 1 } },
                  { { 2, 2 }, { 3, 3 }, { 0, 0 } },
                  { { 2, 2 }, { 4, 4 }, { 3, 3 } } };

               break;

            case 0b1100:

               vs = new Vec2[] {
                  new Vec2(xCell - half, yCell       ),
                  new Vec2(xCell + half, yCell       ),
                  new Vec2(xCell - half, yCell + half),
                  new Vec2(xCell + half, yCell + half) };

               vts = new Vec2[] {
                  new Vec2(0.0f, 0.5f),
                  new Vec2(1.0f, 0.5f),
                  new Vec2(0.0f, 0.0f),
                  new Vec2(1.0f, 0.0f) };

               fs = new int[][][] {
                  { { 3, 3 }, { 0, 0 }, { 1, 1 } },
                  { { 3, 3 }, { 2, 2 }, { 0, 0 } } };

               break;

            case 0b1101:

               vs = new Vec2[] {
                  new Vec2(xCell - half, yCell - half),
                  new Vec2(xCell       , yCell - half),
                  new Vec2(xCell + half, yCell       ),
                  new Vec2(xCell - half, yCell + half),
                  new Vec2(xCell + half, yCell + half) };

               vts = new Vec2[] {
                  new Vec2(0.0f, 1.0f),
                  new Vec2(0.5f, 1.0f),
                  new Vec2(1.0f, 0.5f),
                  new Vec2(0.0f, 0.0f),
                  new Vec2(1.0f, 0.0f) };

               fs = new int[][][] {
                  { { 2, 2 }, { 0, 0 }, { 1, 1 } },
                  { { 2, 2 }, { 3, 3 }, { 0, 0 } },
                  { { 2, 2 }, { 4, 4 }, { 3, 3 } } };

               break;

            case 0b1110:

               vs = new Vec2[] {
                  new Vec2(xCell       , yCell - half),
                  new Vec2(xCell + half, yCell - half),
                  new Vec2(xCell - half, yCell       ),
                  new Vec2(xCell - half, yCell + half),
                  new Vec2(xCell + half, yCell + half) };

               vts = new Vec2[] {
                  new Vec2(0.5f, 1.0f),
                  new Vec2(1.0f, 1.0f),
                  new Vec2(0.0f, 0.5f),
                  new Vec2(0.0f, 0.0f),
                  new Vec2(1.0f, 0.0f) };

               fs = new int[][][] {
                  { { 1, 1 }, { 2, 2 }, { 0, 0 } },
                  { { 1, 1 }, { 3, 3 }, { 2, 2 } },
                  { { 1, 1 }, { 4, 4 }, { 3, 3 } } };

               break;

            case 0b1111:

               vs = new Vec2[] {
                  new Vec2(xCell - half, yCell - half),
                  new Vec2(xCell + half, yCell - half),
                  new Vec2(xCell - half, yCell + half),
                  new Vec2(xCell + half, yCell + half) };

               vts = new Vec2[] {
                  new Vec2(0.0f, 1.0f),
                  new Vec2(1.0f, 1.0f),
                  new Vec2(0.0f, 0.0f),
                  new Vec2(1.0f, 0.0f) };

               fs = new int[][][] {
                  { { 0, 0 }, { 1, 1 }, { 3, 3 } },
                  { { 0, 0 }, { 3, 3 }, { 2, 2 } } };

               break;

            case 0b0000:
            default:
               vs = new Vec2[0];
               vts = new Vec2[0];
               fs = new int[0][0][0];
         }
         /* @formatter:on */

      target.coords = Vec2.concat(target.coords, vs);
      target.texCoords = Vec2.concat(target.texCoords, vts);
      if ( target.faces != null ) {
         target.faces = Mesh.splice(target.faces, target.faces.length, 0, fs);
      } else {
         target.faces = fs;
      }

      return target;
   }

   static Mesh2 evalCell ( final float threshold, final float v00,
      final float v10, final float v11, final float v01, final float xCell,
      final float yCell, final float half, final Mesh2 target ) {

      final int b00 = v00 >= threshold ? 0b1 : 0b0;
      final int b10 = v10 >= threshold ? 0b1 : 0b0;
      final int b11 = v11 >= threshold ? 0b1 : 0b0;
      final int b01 = v01 >= threshold ? 0b1 : 0b0;

      final byte config = ( byte ) ( b01 << 3 | b11 << 2 | b10 << 1 | b00 );
      boolean fillSaddle = false;
      if ( config == 0b0101 || config == 0b1010 ) {
         final float center = ( v00 + v10 + v11 + v01 ) * 0.25f;
         fillSaddle = center >= threshold;
      }

      return MarchingSquares.appendToMesh(config, xCell, yCell, half,
         fillSaddle, target);
   }

}

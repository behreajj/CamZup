import camzup.core.*;
import camzup.pfriendly.*;

YupJ2 graphics2;

// Number of inscriptions (rows).
int inscrip = 6;

// Number of polygons (columns).
int count = 6;

// Control for animation.
float time = IUtils.ONE_THIRD;
float timeIncr = 1.0 / 256.0;

// Scale of the shape
float scale = 62.0;

Mesh2.PolyType poly = Mesh2.PolyType.NGON;

MeshEntity2[][] srcEntities = new MeshEntity2[count][inscrip];
MeshEntity2[][] trgEntities = new MeshEntity2[count][inscrip];

MaterialSolid srcMaterial = new MaterialSolid()
  .setFill(#fff7d5)
  .setFill(false)
  .setStroke(#202020)
  .setStroke(true)
  .setStrokeWeight(1.0);

MaterialSolid trgMaterial = new MaterialSolid()
  .setFill(0xafff2828)
  .setFill(true)
  .setStroke(#007fff)
  .setStroke(false)
  .setStrokeWeight(1.75);

void settings() {
  size(720, 405, YupJ2.PATH_STR);
}

void setup() {
  graphics2 = (YupJ2)getGraphics();

  // Determine horizontal extents of grid.
  Vec2 ubx = new Vec2(width * 0.4, 0.0);
  Vec2 lbx = Vec2.negate(ubx, new Vec2());
  Vec2 horiz = new Vec2();

  // Determine vertical extents of grid.
  Vec2 uby = new Vec2(0.0, height * 0.4);
  Vec2 lby = Vec2.negate(uby, new Vec2());
  Vec2 vert = new Vec2();

  // Combine horizontal and vertical extents.
  Vec2 loc = new Vec2();

  // Convert from rows and columns to percentage.
  float toPercent0 = 1.0 / (count - 1.0);
  float toPercent1 = 1.0 / (inscrip - 1.0);

  for (int i = 0, sides = 3; i < count; ++i, ++sides) {

    // Calculate horizontal location.
    float prc0 = i * toPercent0;
    Vec2.mix(lbx, ubx, prc0, horiz);

    // Cache shortcut to column.
    MeshEntity2[] srcCol = srcEntities[i];
    MeshEntity2[] trgCol = trgEntities[i];

    for (int j = 0, cut = 3; j < inscrip; ++j, ++cut) {

      // Calculate vertical location.
      float prc1 = j * toPercent1;
      Vec2.mix(lby, uby, prc1, vert);

      // Sum horizontal and vertical locations.
      Vec2.add(horiz, vert, loc);

      // Create source entity and move to given location.
      MeshEntity2 meSrc = srcCol[j] = new MeshEntity2();
      meSrc.scaleTo(scale);
      meSrc.moveTo(loc);

      // Create polygon.
      Mesh2 srcMesh = new Mesh2();
      Mesh2.polygon(sides, poly, srcMesh);
      srcMesh.rotateZ(Utils.HALF_PI);

      // Subdivide the polygon as desired.
      srcMesh.subdivFacesFan(1);
      srcMesh.subdivFacesCenter(1);
      //srcMesh.subdivFacesInscribe(1);

      meSrc.append(srcMesh);

      // Create a target entity.
      MeshEntity2 meTrg = trgCol[j] = new MeshEntity2();
      meTrg.transform.set(meSrc.transform);

      // Trace the perimeter.
      Mesh2 trace = new Mesh2();
      Mesh2.tracePerimeter(srcMesh, cut, time, trace);
      meTrg.append(trace);
    }
  }
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));
  graphics2.background();

  for (int i = 0; i < count; ++i) {
    MeshEntity2[] srcCol = srcEntities[i];
    MeshEntity2[] trgCol = trgEntities[i];
    for (int j = 0, cut = 3; j < inscrip; ++j, ++cut) {
      MeshEntity2 src = srcCol[j];
      MeshEntity2 trg = trgCol[j];

      Mesh2.tracePerimeter(src.get(0), cut, time, trg.get(0));

      if (mousePressed) {
        graphics2.shape(src, srcMaterial);
      }
      graphics2.shape(trg, trgMaterial);
    }
  }

  time += timeIncr;
}

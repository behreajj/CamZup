import camzup.core.*;
import camzup.pfriendly.*;

// Display and backing buffer.
Yup2 main;
Zup3 buff;

// Aspect ratios for renderers
// and the aspect corrections.
float mainar = 1.0f;
float buffar = 1.0f;
float fitInside = 1.0f;
float correct = 1.0f;

// Camera orbit distance.
float orbit = 256.0f;

// Cube minimum and maximum heights.
float heightMin = 0.5f;
float heightMax = 2.0f;

// For grid of cubes.
int cubecount = 12;
MeshEntity3 gridEntity = new MeshEntity3();
MaterialSolid[] mats = new MaterialSolid[cubecount * cubecount + 1];
Gradient ryb = Gradient.paletteRyb(new Gradient());

void settings() {
  // Display uses 4:3 aspect ratio, 1.33333.
  // 256 * 2 * (4.0 / 3.0) = 682.66667.
  size(683, 512, Yup2.PATH_STR);

  // Must be called in settings.
  noSmooth();
}

void setup() {
  frameRate(60.0f);

  // Disable mip maps and set texture sampling to point
  // (same as nearest neighbor).
  main = (Yup2)getGraphics();
  main.disableMipMaps();
  main.textureSampling(TextureSampling.POINT);
  mainar = main.aspect();

  // The SNES's internal resolution is 256 x 224,
  // an 8:7 aspect ratio, 1.14285.
  buff = (Zup3)createGraphics(256, 224, Zup3.PATH_STR);
  buff.disableMipMaps();
  buff.textureSampling(TextureSampling.POINT);
  buffar = buff.aspect();

  // The scale correction should be 7.0 / 6.0, 1.66667.
  // It will be slightly off due to pixel rounding.
  fitInside = buffar / mainar;
  correct = mainar / buffar;
  println("Control aspect correction:",
    (4.0f / 3.0f) / (8.0f / 7.0f));
  println("Test aspect correction:", correct);

  // Create a unit cube with its base at the origin.
  Mesh3 source = Mesh3.cube(0.5f, new Mesh3())
    .translate(new Vec3(0.0f, 0.0f, 0.5f));

  float toPrc = 1.0f / (cubecount - 1.0f);
  float gridsize = IUtils.ONE_SQRT_3 * cubecount;
  Vec2[][] grid2 = Vec2.grid(
    cubecount, cubecount,
    -gridsize, gridsize);
  Vec2 locUnit = new Vec2();
  Vec3 loc3 = new Vec3();
  Vec3 scale3 = new Vec3();
  for (int k = 0, i = 0; i < cubecount; ++i) {
    float ifac = i * toPrc;
    ifac = ifac + ifac - 1.0f;
    for (int j = 0; j < cubecount; ++j, ++k) {
      float jfac = j * toPrc;
      jfac = jfac + jfac - 1.0f;

      // Create cube from reference.
      Mesh3 cube = new Mesh3(source);

      // Scale cube based on SDF.
      locUnit.set(ifac, jfac);
      float ofac = 1.0f - Sdf.conic(locUnit, 0.0f);
      float scale = Utils.lerp(heightMax, heightMin, ofac);
      scale3.set(1.0f, 1.0f, correct * scale);
      cube.scale(scale3);

      // Translate cube.
      loc3.set(grid2[i][j]);
      cube.translate(loc3);
      gridEntity.append(cube);

      // Create material.
      mats[k] = new MaterialSolid();
      Gradient.eval(ryb, ofac, mats[k].fill);
      cube.materialIndex= k;
    }
  }

  // Add reference cube.
  mats[mats.length - 1] = new MaterialSolid(
    new Color(1.0f, 1.0f, 1.0f));
  source.materialIndex = mats.length - 1;
  source.translate(new Vec3(0.0f, -gridsize * 1.375f, 0.0f));
  gridEntity.append(source);

  // Scale entity.
  float longedge = Utils.max(buff.width, buff.height);
  gridEntity.scaleTo(IUtils.TWO_THIRDS * longedge  / (float)cubecount);
}

void draw() {
  // Shift camera with mouse.
  Vec3 ms = buff.mouse1s(new Vec3());
  Vec3.rotateZ(ms, IUtils.ONE_SQRT_2, IUtils.ONE_SQRT_2, ms);
  Vec3.mul(ms, orbit * 0.5f, ms);

  buff.beginDraw();  
  buff.textAlign(LEFT, TOP);
  buff.noStroke();
  ZImage.checker(0xff202020, 0xff101010, 8, 7, buff);
  buff.ortho();
  
  // Pixel art isometric is actually dimetric,
  // as a pixel line is a rise of 1 per run of 2.
  // atan2(1.0, 2.0) is approximately 25.565 degrees.
  buff.camDimetric(ms.x, ms.y, ms.z, orbit);
  buff.lights();
  buff.shape(gridEntity, mats);

  buff.beginHud();

  float wh = buff.width * 0.5f;
  float hh = buff.height * 0.5f;

  // Uncorrected aspect.
  buff.fill(0xffffffff);
  buff.ellipse(wh - 36.0f, hh - 21.0f, 16.0f, 16.0f);

  // Corrected aspect.
  buff.fill(0xffffffff);
  buff.ellipse(wh - 18.0f, hh - 21.0f, 16.0f, 16.0f * correct);

  buff.fill(0xff202020);
  buff.text("Press mouse button for 1:1.",
    3.0f - wh, hh - 3.0f);

  buff.fill(0xffffffff);
  buff.text("Press mouse button for 1:1.",
    2.0f - wh, hh - 2.0f);

  buff.endHud();
  buff.endDraw();

  // Display buffer in main renderer.
  main.background();
  float w = mousePressed ? width * fitInside : width;
  main.image(buff, 0.0f, 0.0f, w, height);
}

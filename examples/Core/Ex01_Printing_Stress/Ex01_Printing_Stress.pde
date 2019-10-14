import camzup.core.*;
long start0, start1, stop0, stop1, diff0, diff1;
Random rng = new Random();
int testCount = 1000;
int compareCount = 32;
int places = 8;

void setup() {
  float[] vals = new float[testCount];
  for (int i = 0; i < testCount; ++i) {
    vals[i] = rng.uniform(-100, 100);
  }

  for (int i = 0; i < compareCount; ++i) {
    float val = vals[i];
    System.out.println(nfs(val, 0, places));
    System.out.println(Utils.toFixed(val, places));
    System.out.println("");
  }

  start0 = System.nanoTime();
  for (int i = 0; i < testCount; ++i) {
    String str = nfs(vals[i], 0, places);
  }
  stop0 = System.nanoTime();
  diff0 = stop0 - start0;

  start1 = System.nanoTime();
  for (int i = 0; i < testCount; ++i) {
    String str = Utils.toFixed(vals[i], places);
  }
  stop1 = System.nanoTime();
  diff1 = stop1 - start1;

  println("toFixed:\t" + diff1);
  println("nfs:\t" + diff0);
  println("diff:\t" + (diff0 / (double)diff1));

  Vec3[] vecs = new Vec3[testCount];
  PVector[] pvs = new PVector[testCount];

  for (int i = 0; i < testCount; ++i) {
    vecs[i] = Vec3.random(rng, new Vec3());
    pvs[i] = new PVector(vecs[i].x, vecs[i].y, vecs[i].z);
  }

  start0 = System.nanoTime();
  for (int i = 0; i < testCount; ++i) {
    String str = pvs[i].toString();
  }
  stop0 = System.nanoTime();
  diff0 = stop0 - start0;

  start1 = System.nanoTime();
  for (int i = 0; i < testCount; ++i) {
    String str = vecs[i].toString();
  }
  stop1 = System.nanoTime();
  diff1 = stop1 - start1;

  println("\ntoFixed:\t" + diff1);
  println("nfs:\t" + diff0);
  println("diff:\t" + (diff0 / (double)diff1));
}

void draw() {
}

class FlowField2 {

  TreeSet<Ray2> tree = new TreeSet<Ray2>();
  protected final Vec2 transformed = new Vec2();
  protected final Vec3 promoted = new Vec3();

  FlowField2 update(Transform2 tr, float time) {
    for (Ray2 entry : field.tree) {
      Vec2 origin = entry.origin;
      Vec2 dir = entry.dir;
      Transform2.mulPoint(tr, origin, transformed);
      promoted.set(origin, time);
      float fac = Simplex.eval(promoted, Simplex.DEFAULT_SEED);
      Vec2.fromPolar(fac * TAU, dir);
    }
    return this;
  }

  FlowField2(int rows, int cols, Vec2 lb, Vec2 ub) {
    Vec2[][] locs = Vec2.grid(rows, cols, lb, ub);
    int len0 = locs.length;
    for (int i = 0; i < len0; ++i) {
      Vec2[] row = locs[i];
      int len1 = row.length;
      for (int j = 0; j < len1; ++j) {
        Ray2 ray = new Ray2();
        ray.origin.set(row[j]);
        Simplex.noise(ray.origin, Simplex.DEFAULT_SEED, ray.dir);
        Vec2.normalize(ray.dir, ray.dir);
        tree.add(ray);
      }
    }
  }
}

// Adapted from Daniel Shiffman,
// The Nature of Code
// http://natureofcode.com
// 6.4: Flow Field Following

static class FlowField {

  // How large is each "cell" of the flow field
  int resolution;
  int cols;
  int rows;
  float invRes = 1.0;

  // A flow field is a two dimensional array of Vec2s
  Ray2[][] field;

  FlowField(int w, int h, int r) {
    resolution = r < 3 ? 3 : r;
    invRes = 1.0 / resolution;

    // Determine the number of columns and rows based on sketch's
    // width and height
    cols = w / resolution;
    rows = h / resolution;
    field = new Ray2[cols][rows];
    init();
  }

  void init() {

    // Pick a seed.
    int seed = (int)System.currentTimeMillis();

    float xoff = 0.1;

    Vec2 loc = new Vec2();
    Vec2 dir = new Vec2();
    for (int i = 0; i < cols; ++i) {
      float ires = i * resolution;
      float yoff = 0.1;
      for (int j = 0; j < rows; ++j) {
        float jres = j * (resolution + 1);
        float theta = Utils.PI * Simplex.eval(xoff, yoff, seed);

        loc.set(ires, jres);
        Vec2.fromPolar(theta, dir);
        Ray2 ray = new Ray2(loc, dir);
        field[i][j] = ray;

        yoff += 0.1;
      }
      xoff += 0.1;
    }
  }

  // Draw every vector
  void display(YupJ2 pg) {
    pg.stroke(#272727);
    float scale = resolution - 7.5;
    for (int i = 0; i < cols; ++i) {
      Ray2[] col = field[i];
      for (int j = 0; j < rows; ++j) {
        pg.ray(col[j], scale);
      }
    }
  }
  
  Vec2 lookup(Vec2 lookup, Vec2 target) {
    int col = (int)Utils.clamp(
      lookup.x * invRes, 0, cols - 1);
    int row = (int)Utils.clamp(
      lookup.y * invRes, 0, rows - 1);
    return target.set(field[col][row].dir);
  }
}

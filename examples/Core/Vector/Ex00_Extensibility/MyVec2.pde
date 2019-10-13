static class MyVec2 extends Vec2 {
  MyVec2() {
    super();
  }

  MyVec2(float x, float y) {
    super(x, y);
  }

  // I want to be able to add vectors in place.
  void add(Vec2 b) {
    this.x += b.x;
    this.y += b.y;
  }

  // I wanto call static functions without having
  // to create a new target vector.
  static Vec2 add(Vec2 a, Vec2 b) {
    return add(a, b, new Vec2());
  }
}

static class FlowEntry2 extends Ray2 {
  Vec2 noise = new Vec2();

  FlowEntry2() {
  }

  FlowEntry2(Vec2 n) {
    super();
    noise.set(n);
  }

  FlowEntry2(Vec2 origin, Vec2 dir, Vec2 n) {
    super(origin, dir);
    noise.set(n);
  }
}

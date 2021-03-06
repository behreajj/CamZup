import camzup.core.Vec2;

void setup() {

  // Constructors.
  Vec2 v = new Vec2();
  Vec2 w = new Vec2(5.0f, 7.0f);
  Vec2 y = new Vec2(false, true);

  println("\nConstructors");
  println(v);
  println(w);
  println(y);

  // When the z-axis is up,
  // the y-axis is forward.
  Vec2.right(v);
  Vec2.forward(w);
  Vec2.back(y);

  println("\nDirections");
  println(v);
  println(w);
  println(y);

  Vec2.fromPolar(radians(60.0f), 0.5f, v);

  println("\nFrom Polar");
  println(v);
  println(degrees(Vec2.heading(v)));
  println(Vec2.mag(v));
}

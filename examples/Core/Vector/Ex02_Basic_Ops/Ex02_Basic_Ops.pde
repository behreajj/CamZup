import camzup.core.Vec2;

void setup() {
  size(512, 256);
  background(#fff7d5);

  Vec2 sum = new Vec2();
  Vec2 a = new Vec2(10, 15);
  Vec2 b = new Vec2(3, 7);

  Vec2.add(a, b, sum);
  println("sum: ", sum);

  Vec2 diff = new Vec2();
  Vec2.sub(a, b, diff);
  println("difference: ", diff);

  // Component-wise multiplication is undefined
  // mathematically, but has many practical
  // applications.
  Vec2 product = new Vec2();
  Vec2.mul(a, b, product);
  println("vec - vec product", product);

  Vec2.mul(a, -4.0, product);
  println("vec - scalar product", product);

  Vec2 quotient = new Vec2();
  Vec2.div(a, b, quotient);
  println("vec - vec quotient", quotient);

  // Divide by zero returns zero.
  Vec2.div(a, 0.0, quotient);
  println("div by zero - scalar", quotient);

  Vec2.div(a, new Vec2(7.0, 0.0), quotient);
  println("div by zero - vector", quotient);
}

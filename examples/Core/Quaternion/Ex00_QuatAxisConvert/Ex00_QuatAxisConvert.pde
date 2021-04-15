import camzup.core.*;

void setup() {
  Rng rng = new Rng();
  Quaternion a = new Quaternion();
  Quaternion b = new Quaternion();
  float angle = 0.0f;
  Vec3 axis = new Vec3();

  // Convert 20 random quaternions to an axis and an angle.
  // Then create a quaternion from the axis and angle.
  // See if they match.
  int count = 20;
  for (int i = 0; i < count; ++i) {
    Quaternion.random(rng, a);
    angle = Quaternion.toAxisAngle(a, axis);
    Quaternion.fromAxisAngle(angle, axis, b);
    println("angle: " + degrees(angle));
    println("axis: " + axis);
    println("a: " + a);
    println("b: " + b);
    println("");
  }
}

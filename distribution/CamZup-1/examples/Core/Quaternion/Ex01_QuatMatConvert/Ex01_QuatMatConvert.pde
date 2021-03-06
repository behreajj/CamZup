import camzup.core.*;

void setup() {
  Rng rng = new Rng();
  Quaternion a = new Quaternion();
  Mat4 b = new Mat4();
  Vec3 trans = new Vec3();
  Vec3 scale = new Vec3();
  Quaternion c = new Quaternion();

  // Create a random quaternion, convert to a matrix, then
  // convert back. The sign of the conversion may be flipped
  // from the sign of the original.
  int count = 20;
  for (int i = 0; i < count; ++i) {
    Quaternion.random(rng, a);
    Mat4.fromRotation(a, b);
    Mat4.decompose(b, trans, c, scale);
    println("a: " + a);
    println("c: " + c);
    println("b: " + b.toStringCol());
    println("");
  }
}

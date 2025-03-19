import com.behreajj.camzup.core.*;

Rng rng = new Rng();
Vec2 o = Vec2.randomCartesian(rng, -1.0, 1.0, new Vec2());
Vec2 d = Vec2.randomCartesian(rng, -1.0, 1.0, new Vec2());
println("o: " + o);
println("d: " + d);

Vec2 sum = Vec2.add(o, d, new Vec2());
println("Sum: " + sum);

Vec2 diff = Vec2.sub(o, d, new Vec2());
println("Diff: " + diff);

float dotProduct = Vec2.dot(o, d);
println("Dot: " + dotProduct);

float crossProduct = Vec2.cross(o, d);
println("Cross: " + crossProduct);

Vec2 hadamard = Vec2.hadamard(o, d, new Vec2());
println("Hadamard: " + hadamard);

Vec2 scaled = Vec2.mul(o, 3.0f, new Vec2());
println("Scaled: " + scaled);

Vec2 oNorm = Vec2.normalize(o, new Vec2());
Vec2 dNorm = Vec2.normalize(d, new Vec2());
println("O Normalized: " + oNorm);
println("O Normalized: " + dNorm);
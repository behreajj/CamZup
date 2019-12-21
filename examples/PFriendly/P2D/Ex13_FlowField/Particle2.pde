static class Particle {

  static float CLOSE_ENOUGH = EPSILON;

  Vec2 accel = new Vec2();
  Vec2 desired = new Vec2();
  Vec2 loc = new Vec2();
  Vec2 steer = new Vec2();
  Vec2 velocity = new Vec2();
  Vec2 locNorm = new Vec2();

  float maxForce = 0.3;
  float maxSpeed = 3.0;
  float radius = 5.0;

  Particle() {
  }

  Particle(Vec2 pos, float ms, float mf, float r) {
    loc.set(pos);
    maxSpeed = ms;
    maxForce = mf;
    radius = r;
  }

  void applyForce(Vec2 force) {
    Vec2.add(accel, force, accel);
  }

  void borders(float w, float h) {
    float wHalf = w * 0.5;
    float hHalf = h * 0.5;

    if (loc.x < -wHalf) { 
      loc.x = wHalf - 1;
    } else if (loc.x > wHalf) { 
      loc.x = -wHalf + 1;
    }

    if (loc.y < -hHalf) { 
      loc.y = hHalf -1;
    } else if (loc.y > hHalf) { 
      loc.y = -hHalf + 1;
    }
  }

  void follow(FlowEntry2[] flow) {

    // Acquire direction from flow field,
    // multiply by maxSpeed to go as fast as possible
    // in that direction.
    desired.set(nearest(flow));
    Vec2.mul(desired, maxSpeed, desired);

    // By subtracting desired direction from current
    // velocity, the vector steer toward is found.
    // Steering is limited by maxForce.
    Vec2.sub(desired, velocity, steer);
    Vec2.limit(steer, maxForce, steer);
    applyForce(steer);
  }

  Vec2 nearest(Ray2[] curl) {
    float minDist = Float.MAX_VALUE;
    Vec2 closest = curl[0].dir;
    int len = curl.length;
    for (int i = 0; i < len; ++i) {
      Ray2 ray = curl[i];
      float distSq = Vec2.distSq(loc, ray.origin);
      if (distSq < minDist) {
        minDist = distSq;
        closest = ray.dir;
      }
      if (minDist <= CLOSE_ENOUGH) {
        return closest;
      }
    }
    return closest;
  }

  void update() {
    Vec2.add(velocity, accel, velocity);
    Vec2.limit(velocity, maxSpeed, velocity);
    Vec2.add(loc, velocity, loc);
    Vec2.zero(accel);
  }
}

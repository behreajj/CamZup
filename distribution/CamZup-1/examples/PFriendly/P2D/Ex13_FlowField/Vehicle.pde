// Adapted from Daniel Shiffman,
// The Nature of Code
// http://natureofcode.com
// 6.4: Flow Field Following

static class Vehicle {

  float radius = 3.0;
  float maxForce; // Maximum steering force
  float maxSpeed;

  Vec2 position = new Vec2();
  Vec2 velocity = new Vec2();
  Vec2 acceleration = new Vec2();
  Vec2 steer = new Vec2();
  Vec2 desired = new Vec2();
  Vec2 lb = new Vec2();
  Vec2 ub = new Vec2();

  Vehicle(Vec2 loc, float ms, float mf) {
    position.set(loc);
    maxSpeed = ms;
    maxForce = mf;
  }

  public void run(Yup2 pg, float sz, color clr) {

    lb.set(radius, radius);
    ub.set(pg.width - radius, pg.height - radius);

    update();
    borders();
    display(pg, sz, clr);
  }

  // Implementing Reynolds' flow field following algorithm
  // http://www.red3d.com/cwr/steer/FlowFollow.html
  void follow(FlowField flow) {

    // What is the vector at that spot in the flow field?
    flow.lookup(position, desired);

    // Scale it up by maxspeed
    Vec2.mul(desired, maxSpeed, desired);

    // Steering is desired minus velocity
    Vec2.sub(desired, velocity, steer);

    // Limit to maximum steering force
    Vec2.limit(steer, maxForce, steer);
    applyForce(steer);
  }

  void applyForce(Vec2 force) {

    // We could add mass here if we want: A = F / M .
    Vec2.add(acceleration, force, acceleration);
  }

  // Method to update position.
  void update() {

    // Update velocity.
    Vec2.add(velocity, acceleration, velocity);

    // Limit speed.
    Vec2.limit(velocity, maxSpeed, velocity);
    Vec2.add(position, velocity, position);

    // Reset acceleration to 0 each cycle.
    acceleration.reset();
  }

  void display(Yup2 pg, float sz, color stroke) {
    pg.stroke(stroke);
    pg.ray(position.x, position.y, velocity.x, velocity.y, sz);
  }

  // Wraparound
  void borders() {
    Vec2.wrap(position, lb, ub, position);
  }
}

import camzup.core.*;
import camzup.pfriendly.*;
import java.util.*;

YupJ2 rndr;
FlowField2 field;
Transform2 noiseTr = new Transform2();
int rows = 10;
int cols = 10;
int pCount = 100;
ArrayList<Particle2> particles = new ArrayList<Particle2>();

void setup() {
  size(512, 512, "camzup.pfriendly.YupJ2");
  rndr = (YupJ2)getGraphics();

  Vec2 ub = new Vec2(width, height);
  Vec2.mul(ub, 0.5, ub);
  Vec2 lb = new Vec2();
  Vec2.negate(ub, lb);
  field = new FlowField2(rows, cols, lb, ub);

  camzup.core.Random rng = new camzup.core.Random();
  for (int i = 0; i < pCount; ++i) {
    Vec2 loc = Vec2.randomCartesian(rng, -200, 200, new Vec2());
    particles.add(new Particle2(loc));
  }
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));

  //noiseTr.moveBy(new Vec2(0.005, 0.005));
  field.update(noiseTr, frameCount * 0.001);

  rndr.background();
  rndr.stroke(0x7f202020);
  for (Ray2 entry : field.tree) {
    rndr.ray(entry, 7.5);
  }

  rndr.strokeWeight(10.0);
  rndr.stroke(0xffff2828);
  for (Particle2 particle : particles) {
    particle.follow(field.tree);
    particle.update();
    particle.wrapBorders(new Vec2(width, height));
    rndr.point(particle.loc);
  }
}

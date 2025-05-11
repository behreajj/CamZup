import com.behreajj.camzup.core.*;
import com.behreajj.camzup.friendly.*;

Yup2 graphics;
Mesh2 m = Mesh2.fromPoints(new Vec2[] {
/* 00 */  new Vec2(-1, 1),
/* 01 */  new Vec2(1, 1),

/* 02 */  new Vec2(1, Utils.ONE_THIRD),
/* 03 */  new Vec2(Utils.ONE_THIRD, Utils.ONE_THIRD),
/* 04 */  new Vec2(Utils.ONE_THIRD, -Utils.ONE_THIRD),
/* 05 */  new Vec2(1, -Utils.ONE_THIRD),

/* 06 */  new Vec2(1, -1),
/* 07 */  new Vec2(-1, -1),

/* 08 */  new Vec2(-1, -Utils.ONE_THIRD),
/* 09 */  new Vec2(-Utils.ONE_THIRD, -Utils.ONE_THIRD),
/* 10 */  new Vec2(-Utils.ONE_THIRD, Utils.ONE_THIRD),
/* 11 */  new Vec2(-1, Utils.ONE_THIRD),
  }, new Mesh2());
MeshEntity2 me = new MeshEntity2(m);

Vec2 s = new Vec2();
Ray2 r = new Ray2(new Vec2(-50, 32), new Vec2(1, 0));

Rgb orig0 = new Rgb(1, 0, 0, 1);
Rgb dest0 = new Rgb(0, 0, 1, 1);

Rgb orig1 = new Rgb(1, 1, 0, 1);
Rgb dest1 = new Rgb(0.5, 0, 0.5, 1);

void settings() {
  size(720, 405, Yup2.PATH_STR);
  smooth(8);
  pixelDensity(displayDensity());
}

void setup() {
  frameRate(60.0f);
  graphics = (Yup2)getGraphics();

  graphics.getSize(s);
  me.reframe();
  me.scaleTo(Vec2.mul(s, 0.25, new Vec2()));
}

void draw() {
  Vec2 m = graphics.mouse1s(new Vec2());
  Vec2 mScaled = Vec2.hadamard(
    m,
    Vec2.mul(s, 0.5, new Vec2()),
    new Vec2());
  r.origin.set(mScaled);

  Vec2.rotateZ(r.dir, -0.01, r.dir);
  me.rotateZ(0.01);

  graphics.background();

  graphics.noStroke();
  graphics.fill(0xff_202020);
  graphics.shape(me);

  graphics.stroke(0xff_007fff);
  graphics.strokeWeight(5);
  graphics.ray(r, 50);

  Vec2[] vsm = Ray2.intersect(r, me);
  int count = vsm.length;
  for (int j = 0; j < count; ++j) {
    graphics.stroke(0xff_ff0000);

    if (count % 2 != 1 && j % 2 != 0) {
      // Ray is outside shape.
      graphics.strokeWeight(1.5);
      graphics.line(vsm[j], vsm[j- 1]);
    } else if (j > 0 && count % 2 != 0 && j % 2 != 1) {
      // Ray is inside shape.
      graphics.strokeWeight(1.5);
      graphics.line(vsm[j], vsm[j- 1]);
    }

    graphics.strokeWeight(5);
    graphics.point(vsm[j]);
  }
}

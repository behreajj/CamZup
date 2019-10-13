import camzup.pfriendly.*;
import camzup.core.*;

YupJ2 graphics;

Vec2 mouse = new Vec2();

void setup() {
  size(720, 405, "camzup.pfriendly.YupJ2");
  ellipseMode(CENTER);
  graphics = (YupJ2)getGraphics();
}

void draw() {
  surface.setTitle(String.format("%.1f", frameRate));
  background(#fff7d5);

  graphics.camera();
  graphics.origin();
  graphics.mouse(mouse);

  float start = Vec2.heading(mouse);
  float stop = -frameCount * 0.025;

  strokeWeight(5.0);
  stroke(#373737);
  arc(0.0, 0.0, 
    175.0, 175.0, 
    start, stop,
   CHORD);

  stroke(#1475b3);
  strokeWeight(12.0);
  point(mouse.x, mouse.y);
}

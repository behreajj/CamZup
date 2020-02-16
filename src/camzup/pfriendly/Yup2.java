package camzup.pfriendly;

import java.util.Iterator;
import java.util.List;

import camzup.core.Curve2;
import camzup.core.CurveEntity2;
import camzup.core.IUtils;
import camzup.core.Knot2;
import camzup.core.Mat3;
import camzup.core.MaterialSolid;
import camzup.core.Mesh2;
import camzup.core.MeshEntity2;
import camzup.core.Transform2;
import camzup.core.Utils;
import camzup.core.Vec2;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PMatrix2D;
import processing.opengl.PGL;

/**
 * A 2.5D renderer based on OpenGL. Supposes that the the
 * camera is looking down on a 2D plane from the z axis,
 * making (0.0, 1.0) the forward -- or up -- axis.
 */
public class Yup2 extends UpOgl implements IYup2, IUpOgl {

   /**
    * The default camera rotation in radians.
    */
   public static final float DEFAULT_ROT = 0.0f;

   /**
    * The default camera horizontal zoom.
    */
   public static final float DEFAULT_ZOOM_X = 1.0f;

   /**
    * The default camera vertical zoom.
    */
   public static final float DEFAULT_ZOOM_Y = 1.0f;

   /**
    * The camera rotation in radians.
    */
   public float cameraRot = 0.0f;

   /**
    * The camera horizontal zoom.
    */
   public float cameraZoomX = 1.0f;

   /**
    * The camera vertical zoom.
    */
   public float cameraZoomY = 1.0f;

   /**
    * The default constructor.
    */
   public Yup2 () {

      super();
   }

   /**
    * A constructor for manually initializing the renderer.
    *
    * @param width
    *           renderer width
    * @param height
    *           renderer height
    * @param parent
    *           parent applet
    * @param path
    *           applet path
    * @param isPrimary
    *           is the renderer primary
    */
   public Yup2 (
         final int width,
         final int height,
         final PApplet parent,
         final String path,
         final boolean isPrimary ) {

      super(width, height, parent, path, isPrimary);
   }

   /**
    * Sets the renderer's default camera.
    */
   @Override
   protected void defaultCamera () {

      this.defCameraX = IUp.DEFAULT_LOC_X;
      this.defCameraY = IUp.DEFAULT_LOC_Y;
      this.defCameraZ = IUp.DEFAULT_LOC_Z;
      this.camera();
   }

   /**
    * Sets the renderer's default perspective.
    */
   @Override
   protected void defaultPerspective () {

      this.defCameraAspect = IUp.DEFAULT_ASPECT;
      this.defCameraFOV = IUp.DEFAULT_FOV;
      this.defCameraNear = PConstants.EPSILON;
      this.defCameraFar = IUp.DEFAULT_FAR_CLIP;
      this.ortho();
   }

   @Override
   protected void defaultSettings () {

      super.defaultSettings();
      this.noLights();

      /*
       * Ensure depth-related features are turned off. These
       * summarize the hint system.
       */

      // TODO: Could this be causing a problem with secondary
      // renderers?
      this.flush();
      this.pgl.disable(PGL.DEPTH_TEST);
      this.pgl.depthMask(false);
      this.isDepthSortingEnabled = false;
   }

   /**
    * Draws a 2D curve entity.
    *
    * @param entity
    *           the curve entity
    */
   @Deprecated
   protected void shapeOld ( final CurveEntity2 entity ) {

      final List < MaterialSolid > materials = entity.materials;
      final boolean useMaterial = !materials.isEmpty();
      final Iterator < Curve2 > curveItr = entity.curves.iterator();
      Iterator < Knot2 > knItr;

      Knot2 currKnot;
      Knot2 prevKnot;
      Vec2 coord;
      Vec2 foreHandle;
      Vec2 rearHandle;

      this.pushMatrix();
      this.transform(entity.transform, entity.transformOrder);

      while (curveItr.hasNext()) {
         final Curve2 curve = curveItr.next();

         if (useMaterial) {
            this.pushStyle();
            this.material(materials.get(
                  curve.materialIndex));
         }

         knItr = curve.iterator();
         prevKnot = knItr.next();
         coord = prevKnot.coord;

         this.beginShape();
         this.vertexImpl(
               coord.x, coord.y, 0.0f,
               this.textureU,
               this.textureV);

         while (knItr.hasNext()) {
            currKnot = knItr.next();
            foreHandle = prevKnot.foreHandle;
            rearHandle = currKnot.rearHandle;
            coord = currKnot.coord;

            this.bezierVertexImpl(
                  foreHandle.x, foreHandle.y, 0.0f,
                  rearHandle.x, rearHandle.y, 0.0f,
                  coord.x, coord.y, 0.0f);

            prevKnot = currKnot;
         }

         if (curve.closedLoop) {
            currKnot = curve.getFirst();
            foreHandle = prevKnot.foreHandle;
            rearHandle = currKnot.rearHandle;
            coord = currKnot.coord;

            this.bezierVertexImpl(
                  foreHandle.x, foreHandle.y, 0.0f,
                  rearHandle.x, rearHandle.y, 0.0f,
                  coord.x, coord.y, 0.0f);
            this.endShape(PConstants.CLOSE);
         } else {
            this.endShape(PConstants.OPEN);
         }

         if (useMaterial) {
            this.popStyle();
         }
      }

      this.popMatrix();
   }

   /**
    * Draws an arc at a location from a start angle to a stop
    * angle.
    *
    * @param v
    *           the location
    * @param sz
    *           the arc size
    * @param start
    *           the start angle
    * @param stop
    *           the stop angle
    * @param mode
    *           the arc mode
    */
   @Override
   public void arc (
         final Vec2 v,
         final float sz,
         final float start,
         final float stop,
         final int mode ) {

      this.arc(v.x, v.y,
            sz, sz,
            start, stop,
            mode);
   }

   /**
    * Draws a cubic Bezier curve between two anchor points,
    * where the control points shape the curve.
    *
    * @param ap0
    *           the first anchor point
    * @param cp0
    *           the first control point
    * @param cp1
    *           the second control point
    * @param ap1
    *           the second anchor point
    */
   @Override
   public void bezier (
         final Vec2 ap0, final Vec2 cp0,
         final Vec2 cp1, final Vec2 ap1 ) {

      this.bezier(
            ap0.x, ap0.y,
            cp0.x, cp0.y,
            cp1.x, cp1.y,
            ap1.x, ap1.y);
   }

   /**
    * Draws a cubic Bezier curve segment to the next anchor
    * point; the first and second control point shape the curve
    * segment.
    *
    * @param cp0
    *           the first control point
    * @param cp1
    *           the second control point
    * @param ap1
    *           the next anchor point
    */
   @Override
   public void bezierVertex (
         final Vec2 cp0,
         final Vec2 cp1,
         final Vec2 ap1 ) {

      this.bezierVertexImpl(
            cp0.x, cp0.y, 0.0f,
            cp1.x, cp1.y, 0.0f,
            ap1.x, ap1.y, 0.0f);
   }

   /**
    * Sets the camera to the renderer defaults.
    */
   @Override
   public void camera () {

      this.camera(
            IUp.DEFAULT_LOC_X,
            IUp.DEFAULT_LOC_Y,
            Yup2.DEFAULT_ROT,
            Yup2.DEFAULT_ZOOM_X,
            Yup2.DEFAULT_ZOOM_Y);
   }

   /**
    * Sets the camera to a location.
    *
    * @param x
    *           the location x component
    * @param y
    *           the location y component
    */
   public void camera (
         final float x,
         final float y ) {

      this.camera(x, y, Yup2.DEFAULT_ROT,
            Yup2.DEFAULT_ZOOM_X,
            Yup2.DEFAULT_ZOOM_Y);
   }

   /**
    * Sets the camera to a location, at an angle of rotation.
    *
    * @param x
    *           the location x component
    * @param y
    *           the location y component
    * @param radians
    *           the rotation
    */
   public void camera (
         final float x,
         final float y,
         final float radians ) {

      this.camera(x, y, radians,
            Yup2.DEFAULT_ZOOM_X, Yup2.DEFAULT_ZOOM_Y);
   }

   /**
    * Sets the camera to a location, rotation and zoom level.
    *
    * @param x
    *           the translation x
    * @param y
    *           the translation y
    * @param radians
    *           the angle in radians
    * @param zx
    *           the zoom x
    * @param zy
    *           the zoom y
    * @see Utils#min(float, float)
    * @see Utils#modRadians(float)
    */
   public void camera (
         final float x,
         final float y,
         final float radians,
         final float zx,
         final float zy ) {

      this.cameraX = x;
      this.cameraY = y;
      this.cameraRot = radians;
      this.cameraZoomX = zx < Utils.EPSILON ? 1.0f : zx;
      this.cameraZoomY = zy < Utils.EPSILON ? 1.0f : zy;
      final float zDist = this.height < 128 ? 128 : this.height;

      /*
       * this.modelview.reset(); this.modelview.scale(
       * this.cameraZoomX, this.cameraZoomY, 1.0f);
       * this.modelview.rotateZ(-radians);
       * this.modelview.translate( -this.cameraX, -this.cameraY,
       * -zDist);
       */

      final float nrm = -radians * IUtils.ONE_TAU;
      final float c = Utils.scNorm(nrm);
      final float s = Utils.scNorm(nrm - 0.25f);

      final float m00 = c * this.cameraZoomX;
      final float m01 = -s * this.cameraZoomY;
      final float m10 = s * this.cameraZoomX;
      final float m11 = c * this.cameraZoomY;

      this.modelview.set(
            m00, m01, 0.0f, -this.cameraX * m00 - this.cameraY * m01,
            m10, m11, 0.0f, -this.cameraX * m10 - this.cameraY * m11,
            0.0f, 0.0f, 1.0f, -zDist,
            0.0f, 0.0f, 0.0f, 1.0f);

      /* PMatAux.invert(this.modelview, this.modelviewInv); */
      this.modelviewInv.set(
            c / this.cameraZoomX, s / this.cameraZoomX, 0.0f, this.cameraX,
            -s / this.cameraZoomY, c / this.cameraZoomY, 0.0f, this.cameraY,
            0.0f, 0.0f, 1.0f, zDist,
            0.0f, 0.0f, 0.0f, 1.0f);

      this.camera.set(this.modelview);
      this.cameraInv.set(this.modelviewInv);
      this.projmodelview.set(this.projection);
   }

   /**
    * Sets the camera to a location.
    *
    * @param loc
    *           the location
    */
   public void camera ( final Vec2 loc ) {

      this.camera(loc.x, loc.y,
            YupJ2.DEFAULT_ROT,
            YupJ2.DEFAULT_ZOOM_X,
            YupJ2.DEFAULT_ZOOM_Y);
   }

   /**
    * Sets the camera to a location, at an angle of rotation.
    *
    * @param loc
    *           the location
    * @param radians
    *           the angle
    */
   public void camera (
         final Vec2 loc,
         final float radians ) {

      this.camera(loc.x, loc.y, radians,
            YupJ2.DEFAULT_ZOOM_X,
            YupJ2.DEFAULT_ZOOM_Y);
   }

   /**
    * Sets the camera to a location, rotation and zoom level.
    *
    * @param loc
    *           the location
    * @param radians
    *           the angle
    * @param zoom
    *           the zoom level
    */
   public void camera (
         final Vec2 loc,
         final float radians,
         final Vec2 zoom ) {

      this.camera(
            loc.x, loc.y,
            radians,
            zoom.x, zoom.y);
   }

   /**
    * Draws a circle at a location
    *
    * @param coord
    *           the coordinate
    * @param size
    *           the size
    */
   @Override
   public void circle (
         final Vec2 coord,
         final float size ) {

      this.circle(coord.x, coord.y, size);
   }

   /**
    * Draws a curve between four points.
    *
    * @param a
    *           the first point
    * @param b
    *           the second point
    * @param c
    *           the third point
    * @param d
    *           the fourth point
    */
   @Override
   public void curve (
         final Vec2 a, final Vec2 b,
         final Vec2 c, final Vec2 d ) {

      this.curve(
            a.x, a.y,
            b.x, b.y,
            c.x, c.y,
            d.x, d.y);
   }

   /**
    * Draws a curve segment.
    *
    * @param a
    *           the coordinate
    */
   @Override
   public void curveVertex ( final Vec2 a ) {

      this.curveVertexImpl(a.x, a.y, 0.0f);
   }

   /**
    * Draws an ellipse; the meaning of the two parameters
    * depends on the renderer's ellipseMode.
    *
    * @param a
    *           the first parameter
    * @param b
    *           the second parameter
    */
   @Override
   public void ellipse ( final Vec2 a, final Vec2 b ) {

      this.ellipse(a.x, a.y, b.x, b.y);
   }

   /**
    * Gets the renderer camera's 2D location.
    *
    * @param target
    *           the output vector
    * @return the location
    */
   @Override
   public Vec2 getLoc ( final Vec2 target ) {

      return target.set(
            this.cameraX,
            this.cameraY);
   }

   /**
    * Gets the renderer modelview matrix.
    *
    * @return the modelview
    */
   @Override
   public PMatrix2D getMatrix () {

      return this.getMatrix((PMatrix2D) null);
   }

   /**
    * Gets the renderer modelview matrix.
    *
    * @param target
    *           the output matrix
    * @return the modelview
    */
   public Mat3 getMatrix ( final Mat3 target ) {

      return target.set(
            this.modelview.m00, this.modelview.m01, this.modelview.m03,
            this.modelview.m10, this.modelview.m11, this.modelview.m13,
            0.0f, 0.0f, 1.0f);
   }

   /**
    * Gets the renderer camera's rotation.
    *
    * @return the rotation
    */
   @Override
   public float getRot () {

      return this.cameraRot;
   }

   /**
    * Gets the renderer's zoom.
    *
    * @param target
    *           the output vector
    * @return the zoom
    */
   @Override
   public Vec2 getZoom ( final Vec2 target ) {

      return target.set(
            this.cameraZoomX,
            this.cameraZoomY);
   }

   /**
    * Gets the renderer's horizontal zoom.
    *
    * @return the zoom on the horizontal axis
    */
   @Override
   public float getZoomX () {

      return this.cameraZoomX;
   }

   /**
    * Gets the renderer's vertical zoom.
    *
    * @return the zoom on the vertical axis
    */
   @Override
   public float getZoomY () {

      return this.cameraZoomY;
   }

   /**
    * Displays the handles of a curve entity.
    *
    * @param ce
    *           the curve entity
    */
   public void handles ( final CurveEntity2 ce ) {

      this.handles(ce, 1.0f);
   }

   /**
    * Displays the handles of a curve entity.
    *
    * @param ce
    *           the curve entity
    * @param strokeWeight
    *           the stroke weight
    */
   public void handles (
         final CurveEntity2 ce,
         final float strokeWeight ) {

      this.handles(ce, strokeWeight,
            IUp.DEFAULT_HANDLE_COLOR,
            IUp.DEFAULT_HANDLE_REAR_COLOR,
            IUp.DEFAULT_HANDLE_FORE_COLOR,
            IUp.DEFAULT_HANDLE_COORD_COLOR);
   }

   /**
    * Displays the handles of a curve entity.
    *
    * @param ce
    *           the curve entity
    * @param strokeWeight
    *           the stroke weight
    * @param lineColor
    *           the color of handle lines
    * @param rearColor
    *           the color of the rear handle
    * @param foreColor
    *           the color of the fore handle
    * @param coordColor
    *           the color of the coordinate
    */
   public void handles (
         final CurveEntity2 ce,
         final float strokeWeight,
         final int lineColor,
         final int rearColor,
         final int foreColor,
         final int coordColor ) {

      final float swRear = strokeWeight * 4.0f;
      final float swFore = swRear * 1.25f;
      final float swCoord = swFore * 1.25f;

      final List < Curve2 > curves = ce.curves;
      final Iterator < Curve2 > curveItr = curves.iterator();
      Iterator < Knot2 > knItr = null;

      this.pushStyle();
      this.pushMatrix();
      this.transform(ce.transform, ce.transformOrder);

      while (curveItr.hasNext()) {
         final Curve2 curve = curveItr.next();
         knItr = curve.iterator();

         while (knItr.hasNext()) {
            final Knot2 knot = knItr.next();

            final Vec2 coord = knot.coord;
            final Vec2 foreHandle = knot.foreHandle;
            final Vec2 rearHandle = knot.rearHandle;

            this.strokeWeight(strokeWeight);
            this.stroke(lineColor);

            this.lineImpl(
                  rearHandle.x, rearHandle.y, 0.0f,
                  coord.x, coord.y, 0.0f);

            this.lineImpl(
                  coord.x, coord.y, 0.0f,
                  foreHandle.x, foreHandle.y, 0.0f);

            this.strokeWeight(swRear);
            this.stroke(rearColor);
            this.pointImpl(rearHandle.x, rearHandle.y, 0.0f);

            this.strokeWeight(swCoord);
            this.stroke(coordColor);
            this.pointImpl(coord.x, coord.y, 0.0f);

            this.strokeWeight(swFore);
            this.stroke(foreColor);
            this.pointImpl(foreHandle.x, foreHandle.y, 0.0f);
         }
      }

      this.popMatrix();
      this.popStyle();
   }

   /**
    * Draws an image at the origin.
    *
    * @param img
    *           the image
    */
   @Override
   public void image ( final PImage img ) {

      this.image(img, 0.0f, 0.0f);
   }

   /**
    * Draws an image at a given coordinate.
    *
    * @param img
    *           the image
    * @param coord
    *           the coordinate
    */
   @Override
   public void image (
         final PImage img,
         final Vec2 coord ) {

      this.image(img, coord.x, coord.y);
   }

   /**
    * Draws an image at a given coordinate and dimension.
    *
    * @param img
    *           the image
    * @param coord
    *           the coordinate
    * @param dim
    *           the dimension
    */
   @Override
   public void image (
         final PImage img,
         final Vec2 coord,
         final Vec2 dim ) {

      this.image(img, coord.x, coord.y, dim.x, dim.y);
   }

   /**
    * Returns whether or not the renderer is 2D.
    */
   @Override
   public boolean is2D () {

      return true;
   }

   /**
    * Returns whether or not the renderer is 3D.
    */
   @Override
   public boolean is3D () {

      return false;
   }

   /**
    * Draws a line between two coordinates.
    *
    * @param origin
    *           the origin coordinate
    * @param dest
    *           the destination coordinate
    */
   @Override
   public void line (
         final Vec2 origin,
         final Vec2 dest ) {

      this.lineImpl(
            origin.x, origin.y, 0.0f,
            dest.x, dest.y, 0.0f);
   }

   /**
    * Draws the world origin.
    */
   @Override
   public void origin () {

      this.origin(
            IUp.DEFAULT_IJK_LINE_FAC * Utils.min(this.width, this.height),
            IUp.DEFAULT_IJK_SWEIGHT,
            IUp.DEFAULT_I_COLOR,
            IUp.DEFAULT_J_COLOR);
   }

   /**
    * Draws the world origin.
    *
    * @param lineLength
    *           the line length
    */
   public void origin ( final float lineLength ) {

      this.origin(
            lineLength,
            IUp.DEFAULT_IJK_SWEIGHT,
            IUp.DEFAULT_I_COLOR,
            IUp.DEFAULT_J_COLOR);
   }

   /**
    * Draws the world origin.
    *
    * @param lineLength
    *           the line length
    * @param strokeWeight
    *           the stroke weight
    */
   public void origin (
         final float lineLength,
         final float strokeWeight ) {

      this.origin(
            lineLength,
            strokeWeight,
            IUp.DEFAULT_I_COLOR,
            IUp.DEFAULT_J_COLOR);
   }

   /**
    * Draws the world origin.
    *
    * @param lineLength
    *           the line length
    * @param strokeWeight
    *           the stroke weight
    * @param xColor
    *           the color of the x axis
    * @param yColor
    *           the color of the y axis
    */
   public void origin (
         final float lineLength,
         final float strokeWeight,
         final int xColor,
         final int yColor ) {

      final float vl = Utils.max(Utils.EPSILON, lineLength);

      this.pushStyle();

      this.strokeWeight(strokeWeight);
      this.stroke(xColor);
      this.lineImpl(
            0.0f, 0.0f, 0.0f,
            vl, 0.0f, 0.0f);

      this.stroke(yColor);
      this.lineImpl(
            0.0f, 0.0f, 0.0f,
            0.0f, vl, 0.0f);

      this.popStyle();
   }

   /**
    * Draws a point at a given coordinate
    *
    * @param coord
    *           the coordinate
    */
   @Override
   public void point ( final Vec2 coord ) {

      this.point(coord.x, coord.y, 0.0f);
   }

   /**
    * Draws a quadrilateral between four points.
    *
    * @param a
    *           the first point
    * @param b
    *           the second point
    * @param c
    *           the third point
    * @param d
    *           the fourth point
    */
   @Override
   public void quad (
         final Vec2 a, final Vec2 b,
         final Vec2 c, final Vec2 d ) {

      this.quad(
            a.x, a.y,
            b.x, b.y,
            c.x, c.y,
            d.x, d.y);
   }

   /**
    * Draws a quadratic Bezier curve segment to the next anchor
    * point; the control point shapes the curve segment.
    *
    * @param cp
    *           the control point
    * @param ap1
    *           the next anchor point
    */
   @Override
   public void quadraticVertex (
         final Vec2 cp,
         final Vec2 ap1 ) {

      this.quadraticVertexImpl(
            cp.x, cp.y, 0.0f,
            ap1.x, ap1.y, 0.0f);
   }

   /**
    * Draws a rectangle; the meaning of the two parameters
    * depends on the renderer's rectMode.
    *
    * @param a
    *           the first parameter
    * @param b
    *           the second parameter
    */
   @Override
   public void rect ( final Vec2 a, final Vec2 b ) {

      this.rectImpl(
            a.x, a.y,
            b.x, b.y);
   }

   /**
    * Draws a rounded rectangle; the meaning of the first two
    * parameters depends on the renderer's rectMode.
    *
    * @param a
    *           the first parameter
    * @param b
    *           the second parameter
    * @param r
    *           the corner rounding
    */
   @Override
   public void rect (
         final Vec2 a,
         final Vec2 b,
         final float r ) {

      this.rectImpl(
            a.x, a.y,
            b.x, b.y,
            r, r, r, r);
   }

   /**
    * Rotates the modelview matrix around an arbitrary axis by
    * an angle in radians. Not supported by this renderer.
    *
    * @param angle
    *           the angle in radians
    * @param vx
    *           the axis x coordinate
    * @param vy
    *           the axis y coordinate
    * @param vz
    *           the axis z coordinate
    */
   @Override
   public void rotate (
         final float angle,
         final float vx,
         final float vy,
         final float vz ) {

      PGraphics.showVariationWarning("rotate");
   }

   /**
    * Rotates the sketch by an angle in radians around the y
    * axis. For 2D, this scales by ( 1.0, cos ( a ) ) .
    *
    * @param angle
    *           the angle
    * @see PApplet#cos(float)
    */
   @Override
   public void rotateX ( final float angle ) {

      this.scaleImpl(1.0f, Utils.cos(angle), 1.0f);
   }

   /**
    * Rotates the sketch by an angle in radians around the y
    * axis. For 2D, this scales by ( cos ( a ), 1.0 ) .
    *
    * @param angle
    *           the angle
    * @see PApplet#cos(float)
    */
   @Override
   public void rotateY ( final float angle ) {

      this.scaleImpl(Utils.cos(angle), 1.0f, 1.0f);
   }

   /**
    * Scale the renderer by a vector.
    *
    * @param v
    *           the vector
    */
   public void scale ( final Vec2 v ) {

      this.scaleImpl(v.x, v.y, 1.0f);
   }

   /**
    * Sets the renderer camera's location.
    *
    * @param v
    *           the vector
    */
   public void setLoc ( final Vec2 v ) {

      this.cameraX = v.x;
      this.cameraY = v.y;
   }

   /**
    * Set size is the last function called by size,
    * createGraphics, makeGraphics, etc. when initializing the
    * graphics renderer. Therefore, any additional values that
    * need initialization can be attempted here.
    */
   @Override
   public void setSize (
         final int iwidth,
         final int iheight ) {

      super.setSize(iwidth, iheight);
      this.ortho();
      this.camera();
   }

   /**
    * Draws a 2D curve entity.
    *
    * @param entity
    *           the curve entity
    */
   public void shape ( final CurveEntity2 entity ) {

      /*
       * TODO: For performance, seems better to use classes not
       * interfaces, i.e. ArrayList or LinkedList instead of List.
       * However, a generic list is easier on implementation.
       */

      final Transform2 tr = entity.transform;
      final List < Curve2 > curves = entity.curves;
      final List < MaterialSolid > materials = entity.materials;

      final Iterator < Curve2 > curveItr = curves.iterator();
      final boolean useMaterial = !materials.isEmpty();
      Iterator < Knot2 > knItr = null;

      final Vec2 v0 = new Vec2();
      final Vec2 v1 = new Vec2();
      final Vec2 v2 = new Vec2();

      Knot2 currKnot = null;
      Knot2 prevKnot = null;
      Vec2 coord = null;
      Vec2 foreHandle = null;
      Vec2 rearHandle = null;

      while (curveItr.hasNext()) {
         final Curve2 curve = curveItr.next();

         if (useMaterial) {
            this.pushStyle();
            this.material(materials.get(
                  curve.materialIndex));
         }

         knItr = curve.iterator();
         prevKnot = knItr.next();
         coord = prevKnot.coord;

         Transform2.mulPoint(tr, coord, v2);

         this.beginShape();
         this.vertexImpl(
               v2.x, v2.y, 0.0f,
               this.textureU,
               this.textureV);

         while (knItr.hasNext()) {
            currKnot = knItr.next();
            foreHandle = prevKnot.foreHandle;
            rearHandle = currKnot.rearHandle;
            coord = currKnot.coord;

            Transform2.mulPoint(tr, foreHandle, v0);
            Transform2.mulPoint(tr, rearHandle, v1);
            Transform2.mulPoint(tr, coord, v2);

            this.bezierVertexImpl(
                  v0.x, v0.y, 0.0f,
                  v1.x, v1.y, 0.0f,
                  v2.x, v2.y, 0.0f);

            prevKnot = currKnot;
         }

         if (curve.closedLoop) {
            currKnot = curve.getFirst();
            foreHandle = prevKnot.foreHandle;
            rearHandle = currKnot.rearHandle;
            coord = currKnot.coord;

            Transform2.mulPoint(tr, foreHandle, v0);
            Transform2.mulPoint(tr, rearHandle, v1);
            Transform2.mulPoint(tr, coord, v2);

            this.bezierVertexImpl(
                  v0.x, v0.y, 0.0f,
                  v1.x, v1.y, 0.0f,
                  v2.x, v2.y, 0.0f);
            this.endShape(PConstants.CLOSE);
         } else {
            this.endShape(PConstants.OPEN);
         }

         if (useMaterial) {
            this.popStyle();
         }
      }
   }

   public void shape ( final MeshEntity2 entity ) {

      final Transform2 tr = entity.transform;
      final List < Mesh2 > meshes = entity.meshes;
      final Iterator < Mesh2 > meshItr = meshes.iterator();
      final Vec2 v = new Vec2();

      while (meshItr.hasNext()) {
         this.drawMesh2(meshItr.next(), tr, v);
      }
   }

   public void shape (
         final MeshEntity2 entity,
         final MaterialPImage material ) {

      final Transform2 tr = entity.transform;
      final List < Mesh2 > meshes = entity.meshes;

      final Iterator < Mesh2 > meshItr = meshes.iterator();
      final Vec2 v = new Vec2();
      final Vec2 vt = new Vec2();

      this.pushStyle();
      while (meshItr.hasNext()) {
         final Mesh2 mesh = meshItr.next();
         this.drawMesh2(mesh, tr, material, v, vt);
      }
      this.popStyle();
   }

   /**
    * Draws a 2D mesh entity.
    *
    * @param entity
    *           the mesh entity
    * @param materials
    *           the materials
    */
   public void shape (
         final MeshEntity2 entity,
         final MaterialPImage[] materials ) {

      final Transform2 tr = entity.transform;
      final List < Mesh2 > meshes = entity.meshes;
      final Iterator < Mesh2 > meshItr = meshes.iterator();
      final Vec2 v = new Vec2();
      final Vec2 vt = new Vec2();

      this.pushStyle();
      while (meshItr.hasNext()) {
         final Mesh2 mesh = meshItr.next();
         final MaterialPImage mat = materials[mesh.materialIndex];
         this.drawMesh2(mesh, tr, mat, v, vt);
      }
      this.popStyle();
   }

   /**
    * Draws a 2D mesh entity.
    *
    * @param entity
    *           the mesh entity
    * @param material
    *           the material
    */
   public void shape (
         final MeshEntity2 entity,
         final MaterialSolid material ) {

      final Transform2 tr = entity.transform;
      final List < Mesh2 > meshes = entity.meshes;
      final Iterator < Mesh2 > meshItr = meshes.iterator();
      final Vec2 v = new Vec2();

      this.pushStyle();
      this.material(material);
      while (meshItr.hasNext()) {
         final Mesh2 mesh = meshItr.next();
         this.drawMesh2(mesh, tr, v);
      }
      this.popStyle();
   }

   /**
    * Draws a 2D mesh entity.
    *
    * @param entity
    *           the mesh entity
    * @param materials
    *           the materials
    */
   public void shape (
         final MeshEntity2 entity,
         final MaterialSolid[] materials ) {

      final Transform2 tr = entity.transform;
      final List < Mesh2 > meshes = entity.meshes;
      final Iterator < Mesh2 > meshItr = meshes.iterator();
      final Vec2 v = new Vec2();

      while (meshItr.hasNext()) {
         final Mesh2 mesh = meshItr.next();
         this.pushStyle();
         this.material(materials[mesh.materialIndex]);
         this.drawMesh2(mesh, tr, v);
         this.popStyle();
      }
   }

   /**
    * Draws a square at a location.
    *
    * @param loc
    *           the location
    * @param size
    *           the size
    */
   @Override
   public void square (
         final Vec2 loc,
         final float size ) {

      this.rectImpl(loc.x, loc.y, size, size);
   }

   /**
    * Draws a rounded square.
    *
    * @param loc
    *           the location
    * @param size
    *           the size
    * @param r
    *           the corner rounding
    */
   @Override
   public void square (
         final Vec2 loc,
         final float size,
         final float r ) {

      this.rectImpl(loc.x, loc.y, size, size, r, r, r, r);
   }

   /**
    * Returns the string representation of this renderer.
    *
    * @return the string
    */
   @Override
   public String toString () {

      return "camzup.pfriendly.Yup2";
   }

   /**
    * Translate the renderer by a vector.
    *
    * @param v
    *           the vector
    */
   public void translate ( final Vec2 v ) {

      this.translateImpl(v.x, v.y, 0.0f);
   }

   /**
    * Draws a triangle between three points.
    *
    * @param a
    *           the first point
    * @param b
    *           the second point
    * @param c
    *           the third point
    */
   @Override
   public void triangle (
         final Vec2 a,
         final Vec2 b,
         final Vec2 c ) {

      this.triangle(
            a.x, a.y,
            b.x, b.y,
            c.x, c.y);
   }

   /**
    * Adds another vertex to a shape between the beginShape and
    * endShape commands.
    *
    * @param v
    *           the coordinate
    */
   @Override
   public void vertex ( final Vec2 v ) {

      this.vertexImpl(v.x, v.y, 0.0f,
            this.textureU, this.textureV);
   }

   /**
    * Adds another vertex to a shape between the beginShape and
    * endShape commands; includes a texture coordinate.
    *
    * @param v
    *           the coordinate
    * @param vt
    *           the texture coordinate
    */
   public void vertex ( final Vec2 v, final Vec2 vt ) {

      this.vertexImpl(v.x, v.y, 0.0f, vt.x, vt.y);
   }
}

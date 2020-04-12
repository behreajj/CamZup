package camzup.pfriendly;

import camzup.core.Chainable;
import camzup.core.Color;
import camzup.core.Mesh2;
import camzup.core.MeshEntity2;
import camzup.core.Transform2;
import camzup.core.Utils;
import camzup.core.Vec2;

import processing.core.PImage;

/**
 * A convenience to create image planes. Creates a mesh entity with a
 * rectangular mesh scaled to the dimensions of an input image and a textured
 * material. Not intended for use with the AWT renderer.
 */
public class ImageEntity2 extends MeshEntity2 {

   /**
    * The texture material.
    */
   public final MaterialPImage material;

   /**
    * Creates an image entity from a texture. The tint defaults to white.
    *
    * @param texture the image texture
    */
   public ImageEntity2 ( final PImage texture ) {

      this("ImageEntity2", texture, new Color(1.0f, 1.0f, 1.0f, 1.0f));
   }

   /**
    * Creates a named image entity from a texture.
    *
    * @param texture the image texture
    * @param tint    the tint
    */
   public ImageEntity2 ( final PImage texture, final Color tint ) {

      this("ImageEntity2", texture, tint);
   }

   /**
    * Creates a named image entity from a texture. The tint defaults to white.
    *
    * @param name    the entity name
    * @param texture the image texture
    */
   public ImageEntity2 (
      final String name,
      final PImage texture ) {

      this(name, texture, new Color(1.0f, 1.0f, 1.0f, 1.0f));
   }

   /**
    * Creates a named image entity from a texture. The mesh's scale matches the
    * aspect ratio; the entity's scale matches the image's longer edge.
    *
    * @param name    the entity name
    * @param texture the image texture
    * @param tint    the tint
    */
   public ImageEntity2 (
      final String name,
      final PImage texture,
      final Color tint ) {

      super(name);

      final float w = texture.width;
      final float h = texture.height;
      final float shortEdge = Utils.min(w, h);
      final float longEdge = Utils.max(w, h);
      final float aspectRatio = Utils.div(shortEdge, longEdge);
      final Vec2 scalar = new Vec2(shortEdge == w ? aspectRatio
         : 1.0f, shortEdge == h ? aspectRatio : 1.0f);
      final Mesh2 mesh = Mesh2.square(Mesh2.PolyType.NGON, new Mesh2());
      mesh.scale(scalar);
      this.transform.scaleTo(longEdge);

      this.meshes.add(mesh);
      this.material = new MaterialPImage(name, new Transform2(), texture, tint);
   }

   /**
    * Sets the material's tint.
    *
    * @param clr the color
    *
    * @return this entity
    */
   @Chainable
   public ImageEntity2 setTint ( final Color clr ) {

      this.material.tint.set(clr);
      return this;
   }

   /**
    * Sets the material's tint.
    *
    * @param clr the color
    *
    * @return this entity
    */
   @Chainable
   public ImageEntity2 setTint ( final int clr ) {

      Color.fromHex(clr, this.material.tint);
      return this;
   }

}

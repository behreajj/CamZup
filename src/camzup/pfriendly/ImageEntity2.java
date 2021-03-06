package camzup.pfriendly;

import camzup.core.Color;
import camzup.core.Mesh2;
import camzup.core.MeshEntity2;
import camzup.core.PolyType;
import camzup.core.Transform2;
import camzup.core.Utils;
import camzup.core.Vec2;

import processing.core.PConstants;
import processing.core.PImage;

/**
 * A convenience to create image planes. Creates a mesh entity with a
 * rectangular mesh scaled to the dimensions of an input image and a
 * textured material.<br>
 * <br>
 * <em>Not intended for use with the AWT renderer.</em>
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

      this("ImageEntity2", texture, new Color(1.0f, 1.0f, 1.0f, 1.0f), 1.0f,
         PConstants.CENTER, PConstants.CENTER);
   }

   /**
    * Creates a named image entity from a texture.
    *
    * @param texture the image texture
    * @param tint    the tint
    */
   public ImageEntity2 ( final PImage texture, final Color tint ) {

      this("ImageEntity2", texture, tint, 1.0f, PConstants.CENTER,
         PConstants.CENTER);
   }

   /**
    * Creates a named image entity from a texture. The tint defaults to white.
    *
    * @param name    the entity name
    * @param texture the image texture
    */
   public ImageEntity2 ( final String name, final PImage texture ) {

      this(name, texture, new Color(1.0f, 1.0f, 1.0f, 1.0f), 1.0f,
         PConstants.CENTER, PConstants.CENTER);
   }

   /**
    * Creates a named image entity from a texture. The mesh's scale matches
    * the aspect ratio; the entity's scale matches the image's longer edge.
    *
    * @param name       the entity name
    * @param texture    the image texture
    * @param tint       the tint
    * @param scale      the entity scale
    * @param alignHoriz the horizontal alignment
    * @param alignVert  the vertical alignment
    */
   public ImageEntity2 ( final String name, final PImage texture,
      final Color tint, final float scale, final int alignHoriz,
      final int alignVert ) {

      super(name);

      final float w = texture.width;
      final float h = texture.height;
      final float shortEdge = Utils.min(w, h);
      final float longEdge = Utils.max(w, h);
      final float aspectRatio = Utils.div(shortEdge, longEdge);
      final Vec2 meshDim = new Vec2(shortEdge == w ? aspectRatio : 1.0f,
         shortEdge == h ? aspectRatio : 1.0f);
      final Vec2 meshOff = new Vec2();

      switch ( alignHoriz ) {
         case PConstants.LEFT:
            meshOff.x = 0.5f * meshDim.x;
            break;

         case PConstants.RIGHT:
            meshOff.x = -0.5f * meshDim.x;
            break;

         case PConstants.CENTER:
         default:
      }

      switch ( alignVert ) {
         case PConstants.TOP:
            meshOff.y = -0.5f * meshDim.y;
            break;

         case PConstants.BOTTOM:
            meshOff.y = 0.5f * meshDim.y;
            break;

         case PConstants.CENTER:
         default:
      }

      final Mesh2 mesh = Mesh2.square(PolyType.QUAD, new Mesh2());
      mesh.scale(meshDim);
      mesh.translate(meshOff);
      this.transform.scaleTo(scale * longEdge);
      this.meshes.add(mesh);
      this.material = new MaterialPImage(name, new Transform2(), texture, tint);
   }

   @Override
   public boolean equals ( final Object obj ) {

      if ( this == obj ) { return true; }
      if ( !super.equals(obj) || this.getClass() != obj.getClass() ) {
         return false;
      }
      final ImageEntity2 other = ( ImageEntity2 ) obj;
      if ( this.material == null ) {
         if ( other.material != null ) { return false; }
      } else if ( !this.material.equals(other.material) ) { return false; }
      return true;
   }

   @Override
   public int hashCode ( ) {

      final int prime = 31;
      return prime * super.hashCode() + ( this.material == null ? 0
         : this.material.hashCode() );
   }

   /**
    * Sets the material's tint.
    *
    * @param clr the color
    *
    * @return this entity
    */
   public ImageEntity2 setTint ( final Color clr ) {

      this.material.setTint(clr);
      return this;
   }

   /**
    * Sets the material's tint.
    *
    * @param clr the color
    *
    * @return this entity
    */
   public ImageEntity2 setTint ( final int clr ) {

      this.material.setTint(clr);
      return this;
   }

}

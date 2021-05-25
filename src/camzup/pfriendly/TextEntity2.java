package camzup.pfriendly;

import camzup.core.Color;

import processing.core.PConstants;
import processing.core.PFont;

/**
 * A convenience to create billboards which present text strings as a
 * single image texture. Useful for cases where unchanging text needs to be
 * displayed on the screen. Not intended for use with the AWT renderer.
 */
public class TextEntity2 extends ImageEntity2 {

   /**
    * Creates a mesh entity from a billboard with a texture that displays
    * text.
    *
    * @param font the Processing font
    * @param text the string of text
    */
   public TextEntity2 ( final PFont font, final String text ) {

      this(font, text, Color.fromHex(IUp.DEFAULT_STROKE_COLOR, new Color()));
   }

   /**
    * Creates a mesh entity from a billboard with a texture that displays
    * text.
    *
    * @param font the Processing font
    * @param text the string of text
    * @param tint the color
    */
   public TextEntity2 ( final PFont font, final String text,
      final Color tint ) {

      this(font, text, tint, ZImage.DEFAULT_LEADING);
   }

   /**
    * Creates a mesh entity from a billboard with a texture that displays
    * text.
    *
    * @param font    the Processing font
    * @param text    the string of text
    * @param tint    the color
    * @param leading spacing between lines
    */
   public TextEntity2 ( final PFont font, final String text, final Color tint,
      final int leading ) {

      this(font, text, tint, leading, ZImage.DEFAULT_KERNING);
   }

   /**
    * Creates a mesh entity from a billboard with a texture that displays
    * text.
    *
    * @param font    the Processing font
    * @param text    the string of text
    * @param tint    the color
    * @param leading spacing between lines
    * @param kerning spacing between characters
    */
   public TextEntity2 ( final PFont font, final String text, final Color tint,
      final int leading, final int kerning ) {

      this(font, text, tint, leading, kerning, ZImage.DEFAULT_ALIGN,
         PConstants.CENTER, 1.0f);
   }

   /**
    * Creates a mesh entity from a billboard with a texture that displays
    * text.
    *
    * @param font       the Processing font
    * @param text       the string of text
    * @param tint       the color
    * @param scale      the entity scale
    * @param leading    spacing between lines
    * @param kerning    spacing between characters
    * @param alignHoriz the horizontal alignment
    * @param alignVert  the vertical alignment
    *
    * @see ZImage#fromText(PFont, String, int, int, int, int)
    * @see ZImage#premul(processing.core.PImage)
    */
   public TextEntity2 ( final PFont font, final String text, final Color tint,
      final int leading, final int kerning, final int alignHoriz,
      final int alignVert, final float scale ) {

      /*
       * Image must be pre-multiplied due to OpenGL blending. Image is colored
       * white so that it can be tinted by material.
       */
      super(text, ZImage.fromText(font, text, 0xffffffff, leading, kerning,
         alignHoriz), tint, scale, alignHoriz, alignVert);
      ZImage.premul(this.material.texture);
   }

}

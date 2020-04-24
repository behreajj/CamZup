package camzup.pfriendly;

import camzup.core.Color;

import processing.core.PConstants;
import processing.core.PFont;

/**
 * A convenience to create billboards which present text strings as a
 * single image texture. Useful for cases where unchanging text needs to be
 * displayed on the screen.
 */
public class TextEntity3 extends ImageEntity3 {

   /**
    * Creates a mesh entity from a billboard with a texture that displays
    * text.
    *
    * @param font the Processing font
    * @param text the string of text
    */
   public TextEntity3 ( final PFont font, final String text ) {

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
   public TextEntity3 ( final PFont font, final String text,
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
   public TextEntity3 ( final PFont font, final String text, final Color tint,
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
   public TextEntity3 ( final PFont font, final String text, final Color tint,
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
    */
   public TextEntity3 ( final PFont font, final String text, final Color tint,
      final int leading, final int kerning, final int alignHoriz,
      final int alignVert, final float scale ) {

      super(text,
         ZImage.fromText(font, text, 0xffffffff, leading, kerning, alignHoriz),
         tint, scale, alignHoriz, alignVert);
   }

}

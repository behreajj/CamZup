package camzup.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class Gradient implements Iterable < Gradient.Key > {

   public static class Key implements Comparable < Key > {

      static boolean approx (
            final Key a,
            final Key b,
            final float tolerance ) {

         return Utils.approxFast(a.percent, b.percent, tolerance);
      }

      public final Color color = new Color();

      public float percent = 0.0f;

      public Key () {

         this.set(0.0f, Color.clearBlack(new Color()));
      }

      public Key ( final float percent ) {

         this.set(percent, new Color(percent, percent, percent, percent));
      }

      public Key ( final float percent, final Color color ) {

         this.set(percent, color);
      }

      public Key ( final float percent, final int color ) {

         this.set(percent, color);
      }

      public Key ( final float percent, final String color ) {

         this.set(percent, color);
      }

      Key set ( final float percent, final Color color ) {

         this.percent = Utils.clamp01(percent);
         this.color.set(color);
         return this;
      }

      Key set ( final float percent, final int color ) {

         this.percent = percent;
         Color.fromHex(color, this.color);
         return this;
      }

      Key set ( final float percent, final String color ) {

         this.percent = percent;
         Color.fromHex(color, this.color);
         return this;
      }

      protected boolean equals ( final Key key ) {

         if (Float.floatToIntBits(this.percent) != Float
               .floatToIntBits(key.percent)) {
            return false;
         }
         return true;
      }

      @Override
      public int compareTo ( final Key key ) {

         return this.percent > key.percent ? 1
               : this.percent < key.percent ? -1 : 0;
      }

      @Override
      public boolean equals ( final Object obj ) {

         if (this == obj) {
            return true;
         }
         if (obj == null) {
            return false;
         }
         if (this.getClass() != obj.getClass()) {
            return false;
         }
         return this.equals((Key) obj);
      }

      @Override
      public int hashCode () {

         final int prime = 31;
         int result = 1;
         result = prime * result + Float.floatToIntBits(this.percent);
         return result;
      }

      @Override
      public String toString () {

         return this.toString(4);
      }

      public String toString ( final int places ) {

         return new StringBuilder()
               .append("{ percent: ")
               .append(Utils.toFixed(this.percent, places))
               .append(", color: ")
               .append(this.color.toString(places))
               .append(' ').append('}')
               .toString();
      }
   }

   public static float TOLERANCE = 0.09f;

   public static Gradient paletteHue () {
      return new Gradient(
            Color.red(new Color()),
            Color.yellow(new Color()),
            Color.green(new Color()),
            Color.cyan(new Color()),
            Color.blue(new Color()),
            Color.magenta(new Color()),
            Color.red(new Color()));
   }
   
   public static Gradient paletteViridis () {

      return new Gradient(
            new Color(0.988235f, 1.000000f, 0.698039f, 1.0f), // 0xFFFCFFB2
            new Color(0.987190f, 0.843137f, 0.562092f, 1.0f), // 0xFFFCD78F
            new Color(0.984314f, 0.694118f, 0.446275f, 1.0f), // 0xFFFBB172
            new Color(0.981176f, 0.548235f, 0.354510f, 1.0f), // 0xFFFA8C5A

            new Color(0.962353f, 0.412549f, 0.301176f, 1.0f), // 0xFFF5694D
            new Color(0.912418f, 0.286275f, 0.298039f, 1.0f), // 0xFFE9494C
            new Color(0.824314f, 0.198431f, 0.334902f, 1.0f), // 0xFFD23355
            new Color(0.703268f, 0.142484f, 0.383007f, 1.0f), // 0xFFB32462

            new Color(0.584052f, 0.110588f, 0.413856f, 1.0f), // 0xFF951C6A
            new Color(0.471373f, 0.080784f, 0.430588f, 1.0f), // 0xFF78156E
            new Color(0.367320f, 0.045752f, 0.432680f, 1.0f), // 0xFF5E0C6E
            new Color(0.267974f, 0.002353f, 0.416732f, 1.0f), // 0xFF44016A

            new Color(0.174118f, 0.006275f, 0.357647f, 1.0f), // 0xFF2C025B
            new Color(0.093856f, 0.036863f, 0.232941f, 1.0f), // 0xFF18093B
            new Color(0.040784f, 0.028758f, 0.110327f, 1.0f), // 0xFF0A071C
            new Color(0.000000f, 0.000000f, 0.019608f, 1.0f) // 0xFF000005
      );
   }

   public final ArrayList < Key > keys = new ArrayList <>();

   public Gradient () {

      this.keys.add(new Key(0.0f, Color.clearBlack(new Color())));
      this.keys.add(new Key(1.0f, Color.white(new Color())));
   }

   public Gradient ( final Color color ) {

      this.keys.add(new Key(0.0f, Color.white(new Color())));
      this.keys.add(new Key(1.0f, color));
   }

   public Gradient ( final Color... colors ) {

      final int len = colors.length;
      final float denom = 1.0f / (len - 1.0f);
      for (int i = 0; i < len; ++i) {
         this.keys.add(new Key(i * denom, colors[i]));
      }
   }

   public Gradient ( final int color ) {

      this.keys.add(new Key(0.0f, Color.white(new Color())));
      this.keys.add(new Key(1.0f, color));
   }

   public Gradient ( final int... colors ) {

      final int len = colors.length;
      final float denom = 1.0f / (len - 1.0f);
      for (int i = 0; i < len; ++i) {
         this.keys.add(new Key(i * denom, colors[i]));
      }
   }

   public Gradient ( final Key... keys ) {

      final int len = keys.length;
      for (int i = 0; i < len; ++i) {
         this.keys.add(keys[i]);
      }
      this.removeDuplicates();
   }

   public Gradient ( final String color ) {

      this.keys.add(new Key(0.0f, Color.white(new Color())));
      this.keys.add(new Key(1.0f, color));
   }

   public Gradient ( final String... colors ) {

      final int len = colors.length;
      final float denom = 1.0f / (len - 1.0f);
      for (int i = 0; i < len; ++i) {
         this.keys.add(new Key(i * denom, colors[i]));
      }
   }

   public Gradient distributeKeys () {

      final int len = this.keys.size();
      if (len < 2) {
         return this;
      }
      final float denom = 1.0f / (len - 1.0f);
      for (int i = 0; i < len; ++i) {
         this.keys.get(i).percent = i * denom;
      }
      return this;
   }

   public Color eval (
         final float step,
         final Color.AbstrEasing easing,
         final Color target ) {

      final int len = this.keys.size();
      if (len == 0) {
         return Color.clearBlack(target);
      } else if (len == 1 || step <= 0.0f) {
         return target.set(this.keys.get(0).color);
      } else if (step >= 1.0f) {
         return target.set(this.keys.get(len - 1).color);
      }

      for (int i = 0; i < len; ++i) {
         final Key curr = this.keys.get(i);
         final float currPrc = curr.percent;

         if (step < currPrc) {
            final Key prev = this.keys.get(i - 1 < 0 ? 0 : i - 1);
            final float sclstp = Utils.div(
                  step - currPrc,
                  prev.percent - currPrc);
            return easing.apply(curr.color, prev.color, sclstp, target);
         }
      }

      return target;
   }

   @Override
   public Iterator < Key > iterator () {

      return this.keys.iterator();
   }

   /**
    * Sorts the array of keys, then removes any duplicates
    * according to a tolerance.
    * 
    * @return this gradient
    */
   public Gradient removeDuplicates () {

      /*
       * LinkedHashSet<Key> set = new LinkedHashSet<>();
       * set.addAll(keys); keys.clear(); keys.addAll(set); return
       * this;
       */

      Collections.sort(this.keys);
      final int len = this.keys.size();
      for (int i = len - 1; i > 0; --i) {
         final Key current = this.keys.get(i);
         final Key prev = this.keys.get(i - 1);
         if (Key.approx(prev, current, Gradient.TOLERANCE)) {
            this.keys.remove(current);
         }
      }

      return this;
   }

   public Gradient sort () {

      Collections.sort(this.keys);
      return this;
   }

   @Override
   public String toString () {

      return this.toString(4);
   }

   public String toString ( final int places ) {

      final StringBuilder sb = new StringBuilder()
            .append("{ keys: [ \n");
      final Iterator < Key > itr = this.keys.iterator();
      while (itr.hasNext()) {
         sb.append(itr.next().toString(places));
         if (itr.hasNext()) {
            sb.append(',').append('\n');
         }
      }
      sb.append(" ] }");
      return sb.toString();
   }
}

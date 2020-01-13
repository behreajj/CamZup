import camzup.core.*;

Random rng = new Random();
Color a = Color.red(new Color());
Color b = Color.blue(new Color());
Color c = new Color();

Color.bitOr(a, b, c);
println(c);
println(hex(#ff0000 | #0000ff), Color.toHexString(c));
println("");

a = new Color(0.5, 0.75, 0.25);
b = new Color(0.25, 1.0, 0.25);

Color.bitAnd(a, b, c);
println(c);
println(hex(#80bf40 & #40ff40), Color.toHexString(c));
println("");

Color.randomRgba(rng, a);
Color.bitNot(a, b);
println(a);
println(b);
println(Color.bitOr(a, b, c));
println("");

Color.randomRgb(rng, a);
Color.randomRgb(rng, b);
Color.bitXor(a, b, c);
println(a);
println(b);
println(c);
println(hex(Color.toHexInt(a) ^ Color.toHexInt(b)));
println("");

import camzup.core.*;

Rng rng = new Rng();
Color a = Color.red(new Color());
Color b = Color.blue(new Color());
Color c = new Color();

Color.bitOr(a, b, c);
println(c);
println(hex(0xffff0000 | 0xff0000ff), Color.toHexString(c));
println("");

a = new Color(0.5f, 0.75f, 0.25f);
b = new Color(0.25f, 1.0f, 0.25f);

Color.bitAnd(a, b, c);
println(c);
println(hex(0xff80bf40 & 0xff40ff40), Color.toHexString(c));
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
println(Color.toHexString(c));
println(hex(Color.toHexInt(a) ^ Color.toHexInt(b)));

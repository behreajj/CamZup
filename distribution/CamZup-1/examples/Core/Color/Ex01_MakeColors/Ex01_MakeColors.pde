import camzup.core.*;

// Create a random color.
Lab x = new Lab();
Rng rng = new Rng();
Lab.random(rng, x);
println(x);

// Find chroma and hue.
float c = Lab.chroma(x);
float h = Lab.hue(x);
println("Chroma: " + c);
println("Hue: " + h + ", Degrees: " + (h * 360.0));

// Convert to and from hex.
int labHex = Lab.toHexIntSat(x);
println(String.format("\n%08X", labHex));
println(Lab.fromHex(labHex, new Lab()));

// Convert between LAB and LCH.
println("\nLAB-LCH conversion.");
Lch y = new Lch();
Lch.fromLab(x, y);
println(y);
Lab z = new Lab();
Lab.fromLch(y, z);
println(z);

// Convert to and from hex.
int lchHex = Lch.toHexIntSat(y);
println(String.format("\n%08X", lchHex));
println(Lch.fromHex(lchHex, new Lch()));

// Convert between RGB and LAB.
println("\nLAB-RGB conversion.");
Rgb w = new Rgb();
Rgb.srLab2TosRgb(x, w, new Rgb(), new Vec4());
println(w);
Lab t = new Lab();
Rgb.sRgbToSrLab2(w, t, new Vec4(), new Rgb());
println(t);

// Harmonies.
Lab[] analogues = new Lab.HarmonyAnalogous().apply(x);
println("\nAnalogous:");
for (int i = 0; i < analogues.length; ++i) {
  println(analogues[i]);
  float ah = Lab.hue(analogues[i]);
  println("Hue: " + ah + ", Degrees: " + (ah * 360.0));
}

Lab[] splits = new Lab.HarmonySplit().apply(x);
println("\nSplit-Analogous:");
for (int i = 0; i < splits.length; ++i) {
  println(splits[i]);
  float sh = Lab.hue(splits[i]);
  println("Hue: " + sh + ", Degrees: " + (sh * 360.0));
}

Lab[] squares = new Lab.HarmonySquare().apply(x);
println("\nSquares:");
for (int i = 0; i < squares.length; ++i) {
  println(squares[i]);
  float sh = Lab.hue(squares[i]);
  println("Hue: " + sh + ", Degrees: " + (sh * 360.0));
}

Lab[] tetrads =  new Lab.HarmonyTetradic().apply(x);
println("\nTetrads:");
for (int i = 0; i < tetrads.length; ++i) {
  println(tetrads[i]);
  float th = Lab.hue(tetrads[i]);
  println("Hue: " + th + ", Degrees: " + (th * 360.0));
}

Lab[] triads =  new Lab.HarmonyTriadic().apply(x);
println("\nTriads:");
for (int i = 0; i < triads.length; ++i) {
  println(triads[i]);
  float th = Lab.hue(triads[i]);
  println("Hue: " + th + ", Degrees: " + (th * 360.0));
}

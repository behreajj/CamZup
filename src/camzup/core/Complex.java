package camzup.core;

import java.util.Comparator;
import java.util.Iterator;

public class Complex extends Imaginary implements Comparable<Complex> {

    private static final long serialVersionUID = 1389157472482304159L;

    public final class CIterator implements Iterator<Float> {

        private int index = 0;

        private final Complex z;

        public CIterator(final Complex z) {

            this.z = z;
        }

        @Override
        public boolean hasNext() {

            return this.index < this.z.size();
        }

        @Override
        public Float next() {

            return this.z.get(this.index++);
        }

        @Override
        public String toString() {

            return this.getClass().getSimpleName();
        }
    }

    public static abstract class AbstrComparator implements Comparator<Complex> {

        public AbstrComparator() {

        }

        @Override
        public abstract int compare(final Complex a, final Complex b);

        @Override
        public String toString() {

            return this.getClass().getSimpleName();
        }
    }

    public static class ComparatorRealImag extends AbstrComparator {

        public ComparatorRealImag() {

            super();
        }

        @Override
        public int compare(final Complex a, final Complex b) {

            return a.imag > b.imag ? 1 : a.imag < b.imag ? -1 : a.real > b.real ? 1 : a.real < b.real ? -1 : 0;
        }
    }

    private static Comparator<Complex> COMPARATOR = new ComparatorRealImag();

    public static float abs(final Complex z) {

        return (float) Math.sqrt(absSq(z));
    }

    public static float absSq(final Complex z) {

        return z.real * z.real + z.imag * z.imag;
    }

    public static Complex add(final Complex a, final Complex b, final Complex target) {

        return target.set(a.real + b.real, a.imag + b.imag);
    }

    public static Complex add(final float a, final Complex b, final Complex target) {

        return target.set(a + b.real, b.imag);
    }

    public static Complex add(final Complex a, final float b, final Complex target) {

        return target.set(a.real + b, a.imag);
    }

    public static boolean approxMag(final Complex z, final float b) {

        return Utils.approxFast(absSq(z), b * b);
    }

    public static Complex conj(final Complex z, final Complex target) {

        return target.set(z.real, -z.imag);
    }

    public static Complex div(final Complex a, final Complex b, final Complex target, final Complex inverse,
            final Complex conj) {

        Complex.inverse(b, inverse, conj);
        return Complex.div(a, inverse, target);
    }

    public static Complex div(final Complex a, final Complex b, final Complex target) {

        final float bAbsSq = Complex.absSq(b);
        if (bAbsSq <= 0.0f) {
            return target.reset();
        }

        final float bInvAbsSq = 1.0f / bAbsSq;
        final float cReal = b.real * bInvAbsSq;
        final float cImag = -b.imag * bInvAbsSq;
        return target.set(a.real * cReal - a.imag * cImag, a.real * cImag + a.imag * cReal);
    }

    public static Complex div(float a, Complex b, Complex target) {

        final float bAbsSq = Complex.absSq(b);
        if (bAbsSq == 0.0f) {
            return target.reset();
        }

        final float abInvAbsSq = a / bAbsSq;
        return target.set(b.real * abInvAbsSq, -b.imag * abInvAbsSq);
    }

    public static Complex div(final Complex a, final float b, final Complex target) {

        if (b == 0.0f) {
            return target.reset();
        }

        final float bInv = 1.0f / b;
        return target.set(a.real * bInv, a.imag * bInv);
    }

    public static Complex exp(final Complex z, final Complex target) {

        // return Complex.rect((float) Math.exp(z.real), z.imag, target);

        final double r = Math.exp(z.real);
        return target.set((float) (r * Math.cos(z.imag)), (float) (r * Math.sin(z.imag)));
    }

    public static Complex inverse(final Complex z, final Complex target, final Complex conj) {

        final float absSq = Complex.absSq(z);
        if (absSq <= 0.0f) {
            return target.reset();
        }
        Complex.conj(z, conj);
        final float invAbsSq = 1.0f / absSq;
        return target.set(conj.real * invAbsSq, conj.imag * invAbsSq);
    }

    public static Complex inverse(final Complex z, final Complex target) {

        final float absSq = absSq(z);
        if (absSq <= 0.0f) {
            return target.reset();
        }
        final float invAbsSq = 1.0f / absSq;
        return target.set(z.real * invAbsSq, -z.imag * invAbsSq);
    }

    public static boolean isNonZero(final Complex z) {

        return z.real != 0.0f && z.imag != 0.0f;
    }

    public static boolean isUnit(final Complex z) {

        return Utils.approxFast(absSq(z), 1.0f);
    }

    public static boolean isZero(final Complex z) {

        return z.real == 0.0f && z.imag == 0.0f;
    }

    public static Complex log(final Complex z, final Complex target) {

        return target.set((float) Math.log(Complex.abs(z)), (float) Math.atan2(z.imag, z.real));
    }

    /**
     * Performs a M\u00f6bius transformation on the variable <em>z</em>. Uses the
     * formula <br>
     * <br>
     * <em>a</em <em>z</em> + <em>b</em> /<br>
     * <em>c</em <em>z</em> + <em>d</em> <br>
     * <br>
     * where constnts <em>a</em>, <em>b</em>, <em>c</em>, and <em>d</em> satisfy
     * <br>
     * <br>
     * <em>a</em> <em>d</em> - <em>b</em> <em>c</em> \u2260 0
     */
    public static Complex mobius(final Complex a, final Complex b, final Complex c, final Complex d, final Complex z,
            final Complex target) {

        /**
         * cz + d -- the denominator -- first. Minimize the number of calculations
         * needed before the function can determine whether or not to return early.
         */
        final float czdr = (c.real * z.real - c.imag * z.imag) + d.real;
        final float czdi = (c.real * z.imag + c.imag * z.real) + d.imag;
        final float mSq = czdr * czdr + czdi * czdi;

        if (mSq == 0.0f) {
            return target.reset();
        }

        /**
         * az + b -- the numerator -- second.
         */
        final float azbr = (a.real * z.real - a.imag * z.imag) + b.real;
        final float azbi = (a.real * z.imag + a.imag * z.real) + b.imag;

        final float mSqInv = 1.0f / mSq;
        final float czdrInv = czdr * mSqInv;
        final float czdiInv = -czdi * mSqInv;

        return target.set(azbr * czdrInv - azbi * czdiInv, azbr * czdiInv + azbi * czdrInv);
    }

    public static Complex mult(final Complex a, final Complex b, final Complex target) {

        return target.set(a.real * b.real - a.imag * b.imag, a.real * b.imag + a.imag * b.real);
    }

    public static Complex mult(final Complex a, final float b, final Complex target) {

        return target.set(a.real * b, a.imag * b);
    }

    public static Complex mult(final float a, final Complex b, final Complex target) {

        return target.set(a * b.real, a * b.imag);
    }

    public static Complex normalize(final Complex z, final Complex target) {

        // return div(z, abs(z), target);

        final float mSq = z.real * z.real + z.imag * z.imag;

        if (mSq == 0.0f) {
            return target.reset();
        }

        if (mSq == 1.0f) {
            return target.set(z);
        }

        final float mInv = (float) (1.0d / Math.sqrt(mSq));
        return target.set(z.real * mInv, z.imag * mInv);
    }

    public static float phase(final Complex z) {

        return Utils.atan2(z.imag, z.real);
    }

    public static Complex pow(final Complex a, final Complex b, final Complex target) {

        final double logReal = Math.log(Math.sqrt(a.real * a.real + a.imag * a.imag));
        final double logImag = Math.atan2(a.imag, a.real);
        final double phi = b.real * logImag + b.imag * logReal;
        final double r = Math.exp(b.real * logReal - b.imag * logImag);

        return target.set((float) (r * Math.cos(phi)), (float) (r * Math.sin(phi)));
    }

    public static Complex pow(final float a, final Complex b, final Complex target) {

        final double logReal = Math.log(Math.sqrt(a * a));
        final double logImag = a < 0.0D ? IUtils.PI_D : 0.0D;
        final double phi = b.real * logImag + b.imag * logReal;
        final double r = Math.exp(b.real * logReal - b.imag * logImag);

        return target.set((float) (r * Math.cos(phi)), (float) (r * Math.sin(phi)));
    }

    public static Complex pow(final Complex a, final float b, final Complex target) {

        final double logReal = Math.log(Math.sqrt(a.real * a.real + a.imag * a.imag));
        final double logImag = Math.atan2(a.imag, a.real);
        final double phi = b * logImag;
        final double r = Math.exp(b * logReal);

        return target.set((float) (r * Math.cos(phi)), (float) (r * Math.sin(phi)));
    }

    public static Complex pow(final Complex a, final Complex b, final Complex target, final Complex prod,
            final Complex log) {

        return Complex.exp(Complex.mult(b, Complex.log(a, log), prod), target);
    }

    public static Complex random(final Random rng, final Complex target) {

        final float real = rng.uniform(-1.0f, 1.0f);
        final float imag = rng.uniform(-1.0f, 1.0f);
        final float mSq = real * real + imag * imag;
        if (mSq == 0.0f) {
            return target.reset();
        }
        final float mInv = (float) (1.0d / Math.sqrt(mSq));
        return target.set(real * mInv, imag * mInv);
    }

    public static Complex rect(final float r, final float phi, final Complex target) {

        return target.set(r * (float) Math.cos(phi), r * (float) Math.sin(phi));
    }

    public static Complex rotate(final Complex z, final float radians, final Complex target) {

        final float cosa = (float) Math.cos(radians);
        final float sina = (float) Math.sin(radians);

        return target.set(cosa * z.real - sina * z.imag, cosa * z.imag + sina * z.real);
    }

    public static Complex sub(final Complex a, final Complex b, final Complex target) {

        return target.set(a.real - b.real, a.imag - b.imag);
    }

    public static Complex sub(final Complex a, final float b, final Complex target) {

        return target.set(a.real - b, a.imag);
    }

    public static Complex sub(final float a, final Complex b, final Complex target) {

        return target.set(a - b.real, -b.imag);
    }

    public static Complex zero(final Complex target) {

        return target.set(0.0f, 0.0f);
    }

    public static String getComparatorString() {

        return COMPARATOR.toString();
    }

    public static void setComparator(final Comparator<Complex> comparator) {

        if (comparator != null) {
            COMPARATOR = comparator;
        }
    }

    public float imag = 0.0f;
    public float real = 0.0f;

    public Complex() {

        super(2);
    }

    public Complex(final boolean real, final boolean imag) {

        super(2);
        set(real, imag);
    }

    public Complex(final String realstr, final String imagstr) {

        super(2);
        set(realstr, imagstr);
    }

    public Complex(final float real, final float imag) {

        super(2);
        set(real, imag);
    }

    public Complex(final Complex source) {

        super(2);
        set(source);
    }

    public Complex clone() {

        return new Complex(this.real, this.imag);
    }

    @Override
    public int compareTo(final Complex z) {

        return COMPARATOR.compare(this, z);
    }

    public float get(final int index) {

        switch (index) {
        case 0:
        case -1:
            return real;
        case 1:
        case -2:
            return imag;
        default:
            return 0.0f;
        }
    }

    @Override
    public CIterator iterator() {

        return new CIterator(this);
    }

    public Complex reset() {

        return Complex.zero(this);
    }

    public Complex set(final Complex source) {

        return set(source.real, source.imag);
    }

    public Complex set(final float real, final float imag) {

        this.real = real;
        this.imag = imag;
        return this;
    }

    public Complex set(final boolean real, final boolean imag) {

        this.real = Utils.toFloat(real);
        this.imag = Utils.toFloat(imag);
        return this;
    }

    public Complex set(final String realstr, final String imagstr) {

        float real = 0.0f;
        float imag = 0.0f;

        try {
            real = Float.parseFloat(realstr);
        } catch (final NumberFormatException e) {
            real = 0.0f;
        }

        try {
            imag = Float.parseFloat(imagstr);
        } catch (final NumberFormatException e) {
            imag = 0.0f;
        }

        this.real = real;
        this.imag = imag;

        return this;
    }

    public float[] toArray() {

        return new float[] { real, imag };
    }

    public String toString() {

        return toString(4);
    }

    public String toString(final int places) {

        return new StringBuilder(64).append("{ real: ").append(Utils.toFixed(this.real, places)).append(", imag: ")
                .append(Utils.toFixed(this.imag, places)).append(" }").toString();
    }

    public boolean equals(final Object obj) {

        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        return this.equals((Complex) obj);
    }

    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result + Float.floatToIntBits(real);
        result = prime * result + Float.floatToIntBits(imag);
        return result;
    }

    protected boolean equals(final Complex z) {

        if (Float.floatToIntBits(imag) != Float.floatToIntBits(z.imag)) {
            return false;
        }

        if (Float.floatToIntBits(real) != Float.floatToIntBits(z.real)) {
            return false;
        }

        return true;
    }
}
package com.behreajj.camzup.core;

/**
 * A material which holds data to display materials with solid colors only (no
 * textures, or
 * patterns). Contains data for a fill and/or stroke.
 */
public class MaterialSolid extends Material {

    /**
     * The fill color.
     */
    public final Rgb fill = Rgb.fromHex(Material.DEFAULT_FILL, new Rgb());

    /**
     * The stroke color.
     */
    public final Rgb stroke = Rgb.fromHex(Material.DEFAULT_STROKE, new Rgb());

    /**
     * The weight, or width, of the stroke.
     */
    public float strokeWeight = Material.DEFAULT_STROKE_WEIGHT;

    /**
     * Whether to display a shape with a fill.
     */
    public boolean useFill = true;

    /**
     * Whether to display a shape with a stroke.
     */
    public boolean useStroke = false;

    /**
     * The default constructor.
     */
    public MaterialSolid() {
        /*
         * Stroke cap and join are not implemented because it would require
         * storing PConstants value for each constant, AWT's constant, and SVGs.
         * And there are renderer issues with AWT and OpenGL, so consistency is
         * hard to guarantee.
         */
    }

    /**
     * Creates a new solid material with copied data from a source.
     *
     * @param source the source material
     */
    public MaterialSolid(final MaterialSolid source) {

        this(source.name, source.fill, source.stroke, source.strokeWeight,
            source.useFill, source.useStroke);
    }

    /**
     * Creates a material from a fill color.
     *
     * @param fill the fill color
     */
    public MaterialSolid(final Rgb fill) {

        this(fill, Rgb.clearBlack(new Rgb()), 0.0f);
    }

    /**
     * Creates a material from a fill, stroke color and stroke weight. Whether * to
     * use a fill is
     * inferred from the fill's alpha; whether to use stroke is inferred from the
     * stroke weight and
     * stroke color's alpha.
     *
     * @param fill         the fill color
     * @param stroke       the stroke color
     * @param strokeWeight the stroke weight
     */
    public MaterialSolid(final Rgb fill, final Rgb stroke, final float strokeWeight) {

        this(fill, stroke, strokeWeight,
            fill.alpha > 0.0f,
            stroke.alpha > 0.0f && strokeWeight > 0.0f);
    }

    /**
     * Creates a solid material by component.
     *
     * @param fill         the fill color
     * @param stroke       the stroke color
     * @param strokeWeight the stroke weight
     * @param useFill      whether to use fill
     * @param useStroke    whether to use stroke
     */
    public MaterialSolid(
        final Rgb fill,
        final Rgb stroke,
        final float strokeWeight,
        final boolean useFill,
        final boolean useStroke) {

        this(Rgb.toHexString(fill), fill, stroke, strokeWeight, useFill, useStroke);
    }

    /**
     * Creates a named solid material.
     *
     * @param name the name
     */
    public MaterialSolid(final String name) {
        super(name);
    }

    /**
     * Creates a named material from a fill color.
     *
     * @param name the material name
     * @param fill the fill color
     */
    public MaterialSolid(final String name, final Rgb fill) {

        this(name, fill, Rgb.clearBlack(new Rgb()), 0.0f);
    }

    /**
     * Creates a named material from a fill, stroke color and stroke weight. Whether
     * to use a fill is
     * inferred from the fill's alpha; whether * to use stroke is inferred from the
     * stroke weight and
     * stroke color's alpha.
     *
     * @param name         the name
     * @param fill         the fill color
     * @param stroke       the stroke color
     * @param strokeWeight the stroke weight
     */
    public MaterialSolid(
        final String name,
        final Rgb fill,
        final Rgb stroke,
        final float strokeWeight) {

        this(name, fill, stroke, strokeWeight,
            fill.alpha > 0.0f,
            stroke.alpha > 0.0f && strokeWeight > 0.0f);
    }

    /**
     * Creates a named solid material by component.
     *
     * @param name         the name
     * @param fill         the fill color
     * @param stroke       the stroke color
     * @param strokeWeight the stroke weight
     * @param useFill      whether to use fill
     * @param useStroke    whether to use stroke
     */
    public MaterialSolid(
        final String name,
        final Rgb fill,
        final Rgb stroke,
        final float strokeWeight,
        final boolean useFill,
        final boolean useStroke) {

        super(name);
        Rgb.clamp01(fill, this.fill);
        Rgb.clamp01(stroke, this.stroke);
        this.strokeWeight = strokeWeight;
        this.useFill = useFill;
        this.useStroke = useStroke;
    }

    /**
     * Default material to use in Blender code conversion when an entity does
     * not have one.
     *
     * @param gamma gamma adjustment
     * @see Rgb#fromHex(int, Rgb)
     */
    static void defaultBlenderMaterial(
        final StringBuilder pyCd,
        final float gamma) {

        final Rgb c = Rgb.fromHex(Material.DEFAULT_FILL, new Rgb());
        pyCd.append("{\"name\": \"");
        pyCd.append("CamZupDefault");
        pyCd.append("\", \"fill\": ");
        c.toBlenderCode(pyCd, gamma);
        pyCd.append(", \"metallic\": 0.0");
        pyCd.append(", \"roughness\": 1.0");
        pyCd.append('}');
    }

    /**
     * Default material to use when an entity does not have one. Stroke weight
     * is impacted by transforms, so the stroke weight is divided by the scale.
     * This opens a group node, which should be closed elsewhere.
     *
     * @param svgp  the string builder
     * @param scale the transform scale
     * @see Utils#div(float, float)
     * @see Utils#abs(float)
     * @see Rgb#toHexWeb(StringBuilder, int)
     * @see Utils#toFixed(StringBuilder, float, int)
     */
    static void defaultSvgMaterial(
        final StringBuilder svgp,
        final float scale) {

        svgp.append("<g id=\"");
        svgp.append("defaultmaterial");
        svgp.append("\" fill-opacity=\"");
        svgp.append("1.0");
        svgp.append("\" fill=\"");
        Rgb.toHexWeb(svgp, Material.DEFAULT_FILL);
        svgp.append('\"');
        svgp.append(' ');

        final float sw = Utils.div(Material.DEFAULT_STROKE_WEIGHT, Utils.abs(scale));
        if (sw > Utils.EPSILON) {
            svgp.append("stroke-width=\"");
            Utils.toFixed(svgp, sw, ISvgWritable.FIXED_PRINT);
            svgp.append("\" stroke-opacity=\"");
            svgp.append("1.0");
            svgp.append("\" stroke=\"");
            Rgb.toHexWeb(svgp, Material.DEFAULT_STROKE);
            svgp.append("\" stroke-linejoin=\"");
            svgp.append(ISvgWritable.DEFAULT_STR_JOIN);
            svgp.append("\" stroke-linecap=\"");
            svgp.append(ISvgWritable.DEFAULT_STR_CAP);
            svgp.append('\"');
            svgp.append(' ');
        } else {
            svgp.append("stroke=\"none\"");
        }
        svgp.append(">\n");
    }

    /**
     * Tests this material for equivalence with another object.
     *
     * @param obj the object
     * @return the evaluation
     */
    @Override
    public boolean equals(final Object obj) {

        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || this.getClass() != obj.getClass()) {
            return false;
        }

        final MaterialSolid other = (MaterialSolid) obj;

        if (this.fill == null) {
            if (other.fill != null) {
                return false;
            }
        } else if (!this.fill.equals(other.fill)) {
            return false;
        }

        if (this.stroke == null) {
            if (other.stroke != null) {
                return false;
            }
        } else if (!this.stroke.equals(other.stroke)) {
            return false;
        }

        return this.strokeWeight == other.strokeWeight
            && this.useFill == other.useFill
            && this.useStroke == other.useStroke;
    }

    /**
     * Returns a hash code representation of this material.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {

        // TODO: Replace this with IntelliJ generated hash code.
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (this.useFill ? 1231 : 1237);
        result = prime * result + (this.fill == null ? 0 : this.fill.hashCode());
        result = prime * result + (this.useStroke ? 1231 : 1237);
        result = prime * result + (this.stroke == null ? 0 : this.stroke.hashCode());
        return prime * result + Float.floatToIntBits(this.strokeWeight);
    }

    /**
     * Sets this material from a source.
     *
     * @param source the source material
     * @return this material
     */
    public MaterialSolid set(final MaterialSolid source) {

        this.name = source.name;
        this.fill.set(source.fill);
        this.stroke.set(source.stroke);
        this.strokeWeight = source.strokeWeight;
        this.useFill = source.useFill;
        this.useStroke = source.useStroke;
        return this;
    }

    /**
     * Sets whether to use a stroke with a boolean.
     *
     * @param fill the boolean
     * @return this material
     */
    public MaterialSolid setFill(final boolean fill) {

        this.useFill = fill;
        return this;
    }

    /**
     * Sets the material's fill color.
     *
     * @param fill the fill color
     * @return this material
     */
    public MaterialSolid setFill(final Rgb fill) {

        Rgb.clamp01(fill, this.fill);
        return this;
    }

    /**
     * Sets whether to use a stroke with a boolean.
     *
     * @param stroke the boolean
     * @return this material
     */
    public MaterialSolid setStroke(final boolean stroke) {

        this.useStroke = stroke;
        return this;
    }

    /**
     * Sets the material's stroke color.
     *
     * @param stroke the color
     * @return this material
     */
    public MaterialSolid setStroke(final Rgb stroke) {

        Rgb.clamp01(stroke, this.stroke);
        return this;
    }

    /**
     * Sets the material's stroke weight.
     *
     * @param strokeWeight the stroke weight
     * @return this material
     */
    public MaterialSolid setStrokeWeight(final float strokeWeight) {

        this.strokeWeight = Math.max(Utils.EPSILON, strokeWeight);
        return this;
    }

    /**
     * Swaps this material's stroke and fill. This includes both the color and
     * whether to use fill and
     * stroke.
     *
     * @return this material
     */
    public MaterialSolid swapFillStroke() {

        final boolean t = this.useFill;
        this.useFill = this.useStroke;
        this.useStroke = t;

        final float fr = this.fill.r;
        final float fg = this.fill.g;
        final float fb = this.fill.b;
        final float fa = this.fill.alpha;
        this.fill.set(this.stroke);
        this.stroke.set(fr, fg, fb, fa);

        return this;
    }

    /**
     * Toggles the material's fill.
     *
     * @return this material
     */
    public MaterialSolid toggleFill() {

        this.useFill = !this.useFill;
        return this;
    }

    /**
     * Toggles the material's stroke.
     *
     * @return this material
     */
    public MaterialSolid toggleStroke() {

        this.useStroke = !this.useStroke;
        return this;
    }

    /**
     * Returns a string representation of this material.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return this.toString(Utils.FIXED_PRINT);
    }

    /**
     * Returns a string representation of this material.
     *
     * @param places the number of places
     * @return the string
     */
    public String toString(final int places) {

        final StringBuilder sb = new StringBuilder(256);
        sb.append("{\"name\":\"");
        sb.append(this.name);
        sb.append("\",\"fill\":");
        this.fill.toString(sb, places);
        sb.append(",\"stroke\":");
        this.stroke.toString(sb, places);
        sb.append(",\"strokeWeight\":");
        Utils.toFixed(sb, this.strokeWeight, places);
        sb.append(",\"useFill\":");
        sb.append(this.useFill);
        sb.append(",\"useStroke\":");
        sb.append(this.useStroke);
        sb.append('}');
        return sb.toString();
    }

    /**
     * An internal helper function to format a material in Python, then append
     * it to a {@link StringBuilder}.
     *
     * @param pyCd      the string builder
     * @param gamma     the gamma adjustment
     * @param metallic  the metallic factor
     * @param roughness the roughness
     */
    void toBlenderCode(
        final StringBuilder pyCd,
        final float gamma,
        final float metallic,
        final float roughness) {

        pyCd.append("{\"name\": \"");
        pyCd.append("id");
        pyCd.append(this.name);
        pyCd.append("\", \"fill\": ");
        this.fill.toBlenderCode(pyCd, gamma);
        pyCd.append(", \"metallic\": ");
        Utils.toFixed(pyCd, metallic, 6);
        pyCd.append(", \"roughness\": ");
        Utils.toFixed(pyCd, roughness, 6);
        pyCd.append('}');

    }

    /**
     * Appends a representation of this material to a {@link StringBuilder} for
     * writing an SVG.
     *
     * @param svgp  the string builder
     * @param scale the transform scale.
     */
    void toSvgString(final StringBuilder svgp, final float scale) {

        this.toSvgString(
            svgp, scale,
            ISvgWritable.DEFAULT_STR_JOIN,
            ISvgWritable.DEFAULT_STR_CAP);
    }

    /**
     * Appends a representation of this material to a {@link StringBuilder} for
     * writing an SVG. The
     * stroke join may be either "bevel," "miter" or "round". The stroke cap may be
     * either "butt,"
     * "round" or "square".
     *
     * @param svgp       the string builder
     * @param scale      the transform scale.
     * @param strokeJoin the stroke join.
     * @param strokeCap  the stroke cap.
     * @see Utils#div(float, float)
     * @see Utils#abs(float)
     * @see Utils#clamp01(float)
     * @see Rgb#toHexWeb(StringBuilder, Rgb)
     */
    void toSvgString(
        final StringBuilder svgp,
        final float scale,
        final String strokeJoin,
        final String strokeCap) {

        svgp.append("id=\"");
        svgp.append(this.name.toLowerCase());
        svgp.append("\" class=\"");
        svgp.append(this.getClass().getSimpleName().toLowerCase());
        svgp.append('\"');
        svgp.append(' ');

        /* Stroke style. */
        final float sw = Utils.div(this.strokeWeight, Utils.abs(scale));
        final float sa = this.stroke.alpha;
        if (this.useStroke && sa > 0.0f && sw > Utils.EPSILON) {
            svgp.append("stroke-width=\"");
            Utils.toFixed(svgp, sw, ISvgWritable.FIXED_PRINT);
            if (sa < 1.0f) {
                svgp.append("\" stroke-opacity=\"");
                Utils.toFixed(svgp, sa, ISvgWritable.FIXED_PRINT);
            }
            svgp.append("\" stroke=\"");
            Rgb.toHexWeb(svgp, this.stroke);
            svgp.append("\" stroke-linejoin=\"");
            svgp.append(strokeJoin);
            svgp.append("\" stroke-linecap=\"");
            svgp.append(strokeCap);
            svgp.append('\"');
            svgp.append(' ');
        } else {
            svgp.append("stroke=\"none\" ");
        }

        /* Fill style. */
        final float fa = this.fill.alpha;
        if (this.useFill && fa > 0.0f) {
            if (fa < 1.0f) {
                svgp.append("fill-opacity=\"");
                Utils.toFixed(svgp, fa, ISvgWritable.FIXED_PRINT);
                svgp.append("\" ");
            }
            svgp.append("fill=\"");
            Rgb.toHexWeb(svgp, this.fill);
            svgp.append('\"');
        } else {
            svgp.append("fill=\"none\"");
        }
    }
}

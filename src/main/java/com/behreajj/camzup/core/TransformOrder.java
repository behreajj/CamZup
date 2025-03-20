package com.behreajj.camzup.core;

/**
 * The order in which affine transformations -- translation, rotation, scale --
 * are applied.
 */
public enum TransformOrder {

    /**
     * Rotation
     */
    R("[ \"ROTATION\" ]"),

    /**
     * Rotation, Scale
     */
    RS("[ \"ROTATION\", \"SCALE\" ]"),

    /**
     * Rotation, Scale, Translation
     */
    RST("[ \"ROTATION\", \"SCALE\", \"TRANSLATION\" ]"),

    /**
     * Rotation, Translation
     */
    RT("[ \"ROTATION\", \"TRANSLATION\" ]"),

    /**
     * Rotation, Translation, Scale
     */
    RTS("[ \"ROTATION\", \"TRANSLATION\", \"SCALE\" ]"),

    /**
     * Scale
     */
    S("[ \"SCALE\" ]"),

    /**
     * Scale, Rotation
     */
    SR("[ \"SCALE\", \"ROTATION\" ]"),

    /**
     * Scale, Rotation, Translation
     */
    SRT("[ \"SCALE\", \"ROTATION\", \"TRANSLATION\" ]"),

    /**
     * Scale, Translation
     */
    ST("[ \"SCALE\", \"TRANSLATION\" ]"),

    /**
     * Scale, Translation, Rotation
     */
    STR("[ \"SCALE\", \"TRANSLATION\", \"ROTATION\" ]"),

    /**
     * Translation
     */
    T("[ \"TRANSLATION\" ]"),

    /**
     * Translation, Rotation
     */
    TR("[ \"TRANSLATION\", \"ROTATION\" ]"),

    /**
     * Translation, Rotation, Scale
     */
    TRS("[ \"TRANSLATION\", \"ROTATION\", \"SCALE\" ]"),

    /**
     * Translation, Scale
     */
    TS("[ \"TRANSLATION\", \"SCALE\" ]"),

    /**
     * Translation, Scale, Rotation
     */
    TSR("[ \"TRANSLATION\", \"SCALE\", \"ROTATION\" ]");

    /**
     * The long-form name displayed in the console.
     */
    private final String printName;

    /**
     * The default constructor
     *
     * @param name the long-form name
     */
    TransformOrder(final String name) {
        this.printName = name;
    }

    /**
     * Returns the reverse of a given order. For example, RTS returns STR.
     *
     * @param order the order
     * @return the reverse order
     */
    public static TransformOrder reverse(final TransformOrder order) {

        switch (order) {
            case R:
                return TransformOrder.R;
            case S:
                return TransformOrder.S;
            case T:
                return TransformOrder.T;
            case RS:
                return TransformOrder.SR;
            case RT:
                return TransformOrder.TR;
            case SR:
                return TransformOrder.RS;
            case ST:
                return TransformOrder.TS;
            case TR:
                return TransformOrder.RT;
            case TS:
                return TransformOrder.ST;
            case RST:
                return TransformOrder.TSR;
            case RTS:
                return TransformOrder.STR;
            case SRT:
                return TransformOrder.TRS;
            case STR:
                return TransformOrder.RTS;
            case TSR:
                return TransformOrder.RST;
            case TRS:
            default:
                return TransformOrder.SRT;
        }
    }

    /**
     * Gets the print name of this transform order.
     *
     * @return the print name
     */
    public String getPrintName() {
        return this.printName;
    }
}

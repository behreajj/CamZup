package com.behreajj.camzup.core;

/**
 * Organizes LAB Blend modes.
 */
public interface BlendMode {

    /**
     * A and B chroma axes blend mode operations.
     */
    enum AB {

        /**
         * Adds over layer to under.
         */
        ADD,

        /**
         * Adds over and under layer, then halves the sum.
         */
        AVERAGE,

        /**
         * Blends over and under layer per alpha.
         */
        BLEND,

        /**
         * Adopts the over layer chroma.
         */
        CHROMA,

        /**
         * Adopts the over layer hue.
         */
        HUE,

        /**
         * Adopts the over layer.
         */
        OVER,

        /**
         * Subtracts the over layer from the under.
         */
        SUBTRACT,

        /**
         * Adopts the under layer.
         */
        UNDER
    }

    /**
     * Alpha blend mode operations.
     */
    enum Alpha {

        /**
         * Default blending operation.
         */
        BLEND,

        /**
         * Adopts the greater alpha.
         */
        MAX,

        /**
         * Adopts the lesser alpha.
         */
        MIN,

        /**
         * Multiplies the over and under layer alpha.
         */
        MULTIPLY,

        /**
         * Adopts the over layer alpha.
         */
        OVER,

        /**
         * Adopts the under layer alpha.
         */
        UNDER
    }

    /**
     * Lightness blend mode operations.
     */
    enum L {

        /**
         * Adds over lightness to under.
         */
        ADD,

        /**
         * Adds over and under lightness, then halves the sum.
         */
        AVERAGE,

        /**
         * Blends over and under layer lightness per alpha.
         */
        BLEND,

        /**
         * Divides under lightness by over.
         */
        DIVIDE,

        /**
         * Multiplies over and under lightness.
         */
        MULTIPLY,

        /**
         * Adopts the over layer lightness.
         */
        OVER,

        /**
         * Multiplies the inverse of over and of under lightness, then inverts the
         * product.
         */
        SCREEN,

        /**
         * Subtracts the over layer lightness from the under.
         */
        SUBTRACT,

        /**
         * Adopts the under layer lightness.
         */
        UNDER
    }
}

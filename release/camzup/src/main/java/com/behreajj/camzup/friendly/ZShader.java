package com.behreajj.camzup.friendly;

import com.behreajj.camzup.core.*;
import processing.core.PApplet;
import processing.opengl.PShader;

import java.net.URL;

/**
 * Extends PShader to provide support for Cam Z-up core objects:
 * {@link Vec2}, {@link Vec3}, {@link Vec4}, {@link Quaternion}, {@link Mat3}
 * and {@link Mat4}.
 */
public class ZShader extends PShader {

    /**
     * Index code for {@link Mat3}s in a PShader.
     */
    public static final int MAT3_IDX = 17;

    /**
     * Index code for {@link Mat4}s in a PShader.
     */
    public static final int MAT4_IDX = 18;

    /**
     * Index code for {@link Vec2}s in a PShader.
     */
    public static final int VEC2_IDX = 13;

    /**
     * Index code for {@link Vec3}s in a PShader.
     */
    public static final int VEC3_IDX = 14;

    /**
     * Index code for {@link Vec4}s in a PShader.
     */
    public static final int VEC4_IDX = 15;

    /**
     * The default constructor.
     */
    public ZShader() {
    }

    /**
     * Constructs a shader with a parent applet reference
     *
     * @param parent the parent
     */
    public ZShader(final PApplet parent) {
        super(parent);
    }

    /**
     * Constructs a shader with file name references to a vertex and fragment
     * shader.
     *
     * @param parent       the parent applet
     * @param vertFileName the vertex shader file name
     * @param fragFileName the fragment shader file name
     */
    public ZShader(
        final PApplet parent,
        final String vertFileName,
        final String fragFileName) {

        super(parent, vertFileName, fragFileName);
    }

    /**
     * Constructs a shader from source strings for a vertex and fragment shader.
     *
     * @param parent     the parent applet
     * @param vertSource the vertex shader source code
     * @param fragSource the fragment shader source code
     */
    public ZShader(
        final PApplet parent,
        final String[] vertSource,
        final String[] fragSource) {

        super(parent, vertSource, fragSource);
    }

    /**
     * Constructs a shader with URL references to a vertex and fragment shader.
     *
     * @param parent  the parent applet
     * @param vertURL the vertex shader URL
     * @param fragURL the fragment shader URL
     */
    public ZShader(final PApplet parent, final URL vertURL, final URL fragURL) {

        super(parent, vertURL, fragURL);
    }

    /**
     * Sets a GLSL mat3 uniform to a Mat3.
     *
     * @param name the uniform name
     * @param m    the matrix
     */
    public void set(final String name, final Mat3 m) {

        this.setUniformImpl(name, ZShader.MAT3_IDX, m.toArray1());
    }

    /**
     * Sets a GLSL mat4 uniform to a Mat4.
     *
     * @param name the uniform name
     * @param m    the matrix
     */
    public void set(final String name, final Mat4 m) {

        this.setUniformImpl(name, ZShader.MAT4_IDX, m.toArray1());
    }

    /**
     * Sets a GLSL vec4 uniform to a color. The alpha channel is treated as the
     * w component.
     *
     * @param name the uniform name
     * @param c    the color
     */
    public void set(final String name, final Rgb c) {

        this.setUniformImpl(name, ZShader.VEC4_IDX,
            new float[]{c.r, c.g, c.b, c.alpha});
    }

    /**
     * Sets a GLSL vec2 uniform to a Vec2.
     *
     * @param name the uniform name
     * @param v    the value
     */
    public void set(final String name, final Vec2 v) {

        this.setUniformImpl(name, ZShader.VEC2_IDX, v.toArray());
    }

    /**
     * Sets a GLSL vec3 uniform to a Vec3.
     *
     * @param name the uniform name
     * @param v    the value
     */
    public void set(final String name, final Vec3 v) {

        this.setUniformImpl(name, ZShader.VEC3_IDX, v.toArray());
    }

    /**
     * Sets a GLSL vec4 uniform to a Vec4.
     *
     * @param name the uniform name
     * @param v    the value
     */
    public void set(final String name, final Vec4 v) {

        this.setUniformImpl(name, ZShader.VEC4_IDX, v.toArray());
    }
}

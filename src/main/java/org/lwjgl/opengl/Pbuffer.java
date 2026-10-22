package org.lwjgl.opengl;

import org.lwjgl.LWJGLException;

@SuppressWarnings("unused")
public final class Pbuffer extends SharedDrawable {
    private final int width;
    private final int height;

    public Pbuffer(int width, int height, PixelFormat pixelFormat, Drawable sharedDrawable) throws LWJGLException {
        super(sharedDrawable != null ? sharedDrawable : Display.getDrawable());
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isBufferLost() {
        return false;
    }
}

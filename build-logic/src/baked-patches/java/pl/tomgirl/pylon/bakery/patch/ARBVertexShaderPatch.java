package pl.tomgirl.pylon.bakery.patch;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.opengl.ARBVertexShader;

import static org.lwjgl.opengl.GL11.*;

@SuppressWarnings("unused")
public class ARBVertexShaderPatch {
    @CompatStub
    public static void glVertexAttribPointerARB(int index, int size, boolean unsigned, boolean normalized, int stride, ShortBuffer buffer) {
        ARBVertexShader.glVertexAttribPointerARB(index, size, unsigned ? GL_UNSIGNED_SHORT : GL_SHORT, normalized, stride, buffer);
    }

    @CompatStub
    public static void glVertexAttribPointerARB(int index, int size, boolean normalized, int stride, FloatBuffer buffer) {
        ARBVertexShader.glVertexAttribPointerARB(index, size, GL_FLOAT, normalized, stride, buffer);
    }
}

package pl.tomgirl.pylon.bakery.patch;

import org.lwjgl.opengl.ARBVertexBufferObject;

@SuppressWarnings("unused")
public class ARBPixelBufferObjectPatch {
    @CompatStub
    private static void glBufferDataARB(int target, long size, int usage) {
        ARBVertexBufferObject.glBufferDataARB(target, size, usage);
    }
}

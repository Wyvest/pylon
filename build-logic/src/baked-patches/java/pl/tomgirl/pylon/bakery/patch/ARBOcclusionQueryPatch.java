package pl.tomgirl.pylon.bakery.patch;

import java.nio.IntBuffer;

@SuppressWarnings("unused")
public class ARBOcclusionQueryPatch {
    @CompatStub("glGetQueryObjectuivARB")
    private static void glGetQueryObjectuARB(int obj, int pname, IntBuffer params) {}
}

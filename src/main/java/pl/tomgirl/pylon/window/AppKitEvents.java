package pl.tomgirl.pylon.window;

import java.lang.invoke.MethodHandles;
import java.util.ArrayDeque;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.APIUtil;
import org.lwjgl.system.Callback;
import org.lwjgl.system.CallbackI;
import org.lwjgl.system.JNI;
import org.lwjgl.system.macosx.ObjCRuntime;

import static org.lwjgl.system.MemoryUtil.memGetAddress;
import static org.lwjgl.system.libffi.LibFFI.ffi_type_pointer;
import static org.lwjgl.system.libffi.LibFFI.ffi_type_void;
import static org.lwjgl.system.macosx.ObjCRuntime.*;

/**
 * If AWT created NSApp first, its own loop dequeues input events, preventing them from being processed by SDL.
 * This hooks `-[NSApplicationAWT sendEvent:]` so all input reaches SDL.
 */
class AppKitEvents {
    private static final long MSG_SEND = ObjCRuntime.getLibrary().getFunctionAddress("objc_msgSend");
    private static final ArrayDeque<Long> deferred = new ArrayDeque<>();

    private static long app;
    private static long originalSendEvent;
    private static SendEvent hook;

    private AppKitEvents() {}

    static void install() {
        long awtClass = objc_getClass("NSApplicationAWT");
        if (hook != null || !MainThread.DISPATCH || awtClass == 0) {
            return;
        }
        app = JNI.invokePPP(objc_getClass("NSApplication"), sel_getUid("sharedApplication"), MSG_SEND);
        if ((JNI.invokePPPP(app, sel_getUid("isKindOfClass:"), awtClass, MSG_SEND) & 0xFF) == 0) {
            return;
        }

        long sendEvent = sel_getUid("sendEvent:");
        originalSendEvent = method_getImplementation(class_getInstanceMethod(awtClass, sendEvent));
        hook = AppKitEvents::sendEvent;
        class_replaceMethod(awtClass, sendEvent, hook.address(), "v@:@");

        JNI.invokePPPV(app, sel_getUid("setAppearance:"), 0, MSG_SEND);
    }

    static void repost() {
        if (deferred.isEmpty()) {
            return;
        }
        long postEvent = sel_getUid("postEvent:atStart:");
        long release = sel_getUid("release");
        while (!deferred.isEmpty()) {
            long event = deferred.pollLast();
            JNI.invokePPPPV(app, postEvent, event, 1, MSG_SEND);
            JNI.invokePPP(event, release, MSG_SEND);
        }
    }

    private static void sendEvent(long self, long cmd, long event) {
        if (!MainThread.isExecuting() && DisplaySdl.instance().isCreated() && isInput(event)) {
            deferred.addLast(JNI.invokePPP(event, sel_getUid("retain"), MSG_SEND));
            return;
        }
        JNI.invokePPPV(self, cmd, event, originalSendEvent);
    }

    private static boolean isInput(long event) {
        long type = JNI.invokePPP(event, sel_getUid("type"), MSG_SEND);
        return type >= 1 && type <= 12
            || type == 22
            || type >= 25 && type <= 27;
    }

    @FunctionalInterface
    private interface SendEvent extends CallbackI {
        Callback.Descriptor DESCRIPTOR = new Callback.Descriptor(
            SendEvent.class,
            MethodHandles.lookup(),
            APIUtil.apiCreateCIF(ffi_type_void, ffi_type_pointer, ffi_type_pointer, ffi_type_pointer)
        );

        @Override
        default Callback.@NotNull Descriptor getDescriptor() {
            return DESCRIPTOR;
        }

        @Override
        default void callback(long ret, long args) {
            invoke(
                memGetAddress(memGetAddress(args)),
                memGetAddress(memGetAddress(args + Long.BYTES)),
                memGetAddress(memGetAddress(args + 2L * Long.BYTES))
            );
        }

        void invoke(long self, long cmd, long event);
    }
}

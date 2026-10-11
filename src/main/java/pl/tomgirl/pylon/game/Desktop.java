package pl.tomgirl.pylon.game;

import java.io.File;
import java.net.URI;

@SuppressWarnings("unused")
public final class Desktop {
    private static final Desktop INSTANCE = new Desktop();

    private Desktop() {}

    public static boolean isDesktopSupported() { return true; }

    public static Desktop getDesktop() { return INSTANCE; }

    public void browse(URI link) { GameHooks.openLink(link); }

    public void open(File file) { GameHooks.openLink(file.toURI()); }
}

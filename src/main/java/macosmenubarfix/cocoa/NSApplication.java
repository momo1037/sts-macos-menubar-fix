package macosmenubarfix.cocoa;

import com.sun.jna.Pointer;

/**
 * Helper class for interacting with NSApplication via JNA.
 * Provides methods to control macOS presentation options (menu bar, dock visibility).
 */
public class NSApplication {

    // NSApplicationPresentationOptions constants
    // See: https://developer.apple.com/documentation/appkit/nsapplication/presentationoptions

    /**
     * Default presentation (normal menu bar and dock behavior).
     */
    public static final long NSApplicationPresentationDefault = 0;

    /**
     * Dock is auto-hidden (appears when mouse moves to screen edge).
     */
    public static final long NSApplicationPresentationAutoHideDock = 1L << 0;

    /**
     * Dock is completely hidden and disabled.
     */
    public static final long NSApplicationPresentationHideDock = 1L << 1;

    /**
     * Menu bar is auto-hidden (appears when mouse moves to top of screen).
     */
    public static final long NSApplicationPresentationAutoHideMenuBar = 1L << 2;

    /**
     * Menu bar is completely hidden and disabled.
     * NOTE: Must be used together with NSApplicationPresentationHideDock.
     */
    public static final long NSApplicationPresentationHideMenuBar = 1L << 3;

    /**
     * Combination of AutoHideMenuBar + AutoHideDock.
     * This is the recommended option for borderless fullscreen games.
     */
    public static final long PRESENTATION_AUTO_HIDE_ALL =
            NSApplicationPresentationAutoHideMenuBar | NSApplicationPresentationAutoHideDock;

    private final Pointer sharedApplication;
    private final ObjectiveCRuntime runtime;

    /**
     * Create an NSApplication helper.
     * Gets the shared application instance.
     */
    public NSApplication() {
        this.runtime = ObjectiveCRuntime.INSTANCE;

        // Get NSApplication class
        Pointer nsApplicationClass = runtime.objc_getClass("NSApplication");
        if (nsApplicationClass == null) {
            throw new RuntimeException("Failed to get NSApplication class");
        }

        // Get sharedApplication selector
        Pointer sharedAppSelector = runtime.sel_registerName("sharedApplication");

        // Call [NSApplication sharedApplication]
        this.sharedApplication = runtime.objc_msgSend(nsApplicationClass, sharedAppSelector);
        if (this.sharedApplication == null) {
            throw new RuntimeException("Failed to get NSApplication sharedApplication");
        }
    }

    /**
     * Set the presentation options for the application.
     *
     * @param options Bitmask of NSApplicationPresentationOptions values
     */
    public void setPresentationOptions(long options) {
        Pointer selector = runtime.sel_registerName("setPresentationOptions:");
        runtime.objc_msgSend(sharedApplication, selector, options);
    }

    /**
     * Enable auto-hide for both menu bar and dock.
     */
    public void enableAutoHide() {
        setPresentationOptions(PRESENTATION_AUTO_HIDE_ALL);
    }

    /**
     * Restore default presentation (normal menu bar and dock).
     */
    public void restoreDefault() {
        setPresentationOptions(NSApplicationPresentationDefault);
    }
}

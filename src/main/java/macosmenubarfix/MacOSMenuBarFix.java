package macosmenubarfix;

import basemod.BaseMod;
import basemod.interfaces.PostInitializeSubscriber;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.core.Settings;
import macosmenubarfix.cocoa.NSApplication;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Slay the Spire mod that fixes macOS menu bar visibility in borderless
 * fullscreen mode.
 *
 * When the game is running in borderless fullscreen on macOS, the system menu
 * bar
 * may remain visible and obstruct the game UI (HP bar, relics). This mod uses
 * native macOS APIs via JNA to enable auto-hide for the menu bar and dock.
 */
@SpireInitializer
public class MacOSMenuBarFix implements PostInitializeSubscriber {

    private static final Logger logger = LogManager.getLogger(MacOSMenuBarFix.class.getName());
    private static final String MOD_ID = "macosmenubarfix";

    private static NSApplication nsApplication;
    private static boolean isApplied = false;

    /**
     * ModTheSpire entry point.
     */
    public static void initialize() {
        logger.info("Initializing macOS Menu Bar Fix mod");
        new MacOSMenuBarFix();
    }

    public MacOSMenuBarFix() {
        BaseMod.subscribe(this);
    }

    /**
     * Called after the game has finished initializing.
     * This is when we apply the menu bar fix if conditions are met.
     */
    @Override
    public void receivePostInitialize() {
        logger.info("PostInitialize: Checking if menu bar fix should be applied");

        // Check if we're on macOS
        if (!isMacOS()) {
            logger.info("Not running on macOS, skipping menu bar fix");
            return;
        }

        // Check if borderless fullscreen is enabled
        if (!isBorderlessFullscreen()) {
            logger.info("Borderless fullscreen not enabled, skipping menu bar fix");
            return;
        }

        // Apply the fix
        applyMenuBarFix();
    }

    /**
     * Check if the current OS is macOS.
     */
    private boolean isMacOS() {
        String osName = System.getProperty("os.name", "").toLowerCase();
        return osName.contains("mac") || osName.contains("darwin");
    }

    /**
     * Check if the game is running in borderless fullscreen mode.
     * In Slay the Spire, this is determined by Settings.IS_W_FULLSCREEN
     * (W = Windowed, i.e., borderless window fullscreen mode).
     */
    private boolean isBorderlessFullscreen() {
        try {
            // Check the borderless fullscreen setting
            // IS_W_FULLSCREEN = true means borderless window fullscreen is enabled
            boolean isBorderless = Settings.IS_W_FULLSCREEN;
            logger.info("Settings.IS_W_FULLSCREEN = " + isBorderless);
            return isBorderless;
        } catch (Exception e) {
            logger.error("Error checking borderless fullscreen setting", e);
            return false;
        }
    }

    /**
     * Apply the menu bar auto-hide fix using native macOS APIs.
     */
    private void applyMenuBarFix() {
        try {
            logger.info("Applying macOS menu bar auto-hide fix");

            // Initialize NSApplication helper
            nsApplication = new NSApplication();

            // Enable auto-hide for menu bar and dock
            nsApplication.enableAutoHide();
            isApplied = true;

            logger.info("Menu bar auto-hide enabled successfully");

            // Register shutdown hook to restore default behavior on exit
            registerShutdownHook();

        } catch (Exception e) {
            logger.error("Failed to apply menu bar fix", e);
        }
    }

    /**
     * Register a shutdown hook to restore the menu bar when the game exits.
     * This ensures the system menu bar is visible again after the game closes,
     * even if the game crashes.
     */
    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (isApplied && nsApplication != null) {
                try {
                    logger.info("Shutdown hook: Restoring default presentation options");
                    nsApplication.restoreDefault();
                    logger.info("Menu bar restored to default state");
                } catch (Exception e) {
                    // Can't use logger in shutdown hook reliably
                    System.err.println("MacOSMenuBarFix: Error restoring menu bar: " + e.getMessage());
                }
            }
        }, "MacOSMenuBarFix-ShutdownHook"));

        logger.info("Shutdown hook registered");
    }
}

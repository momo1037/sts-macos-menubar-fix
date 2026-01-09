package macosmenubarfix.cocoa;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

/**
 * JNA interface for Objective-C Runtime functions.
 * Allows calling macOS Cocoa APIs from Java.
 */
public interface ObjectiveCRuntime extends Library {

    /**
     * Singleton instance of the Objective-C Runtime library.
     */
    ObjectiveCRuntime INSTANCE = Native.load("objc", ObjectiveCRuntime.class);

    /**
     * Get a class object by name.
     *
     * @param name The name of the class (e.g., "NSApplication")
     * @return Pointer to the class object, or null if not found
     */
    Pointer objc_getClass(String name);

    /**
     * Register or get a selector by name.
     *
     * @param selectorName The name of the selector (e.g., "sharedApplication")
     * @return Pointer to the selector
     */
    Pointer sel_registerName(String selectorName);

    /**
     * Send a message to an object (no arguments, returns pointer).
     *
     * @param receiver The object receiving the message
     * @param selector The selector to invoke
     * @return Pointer result from the method call
     */
    Pointer objc_msgSend(Pointer receiver, Pointer selector);

    /**
     * Send a message to an object with a long argument (returns void).
     * Used for methods like setPresentationOptions:
     *
     * @param receiver The object receiving the message
     * @param selector The selector to invoke
     * @param arg      The long argument to pass
     */
    void objc_msgSend(Pointer receiver, Pointer selector, long arg);
}

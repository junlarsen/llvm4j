package io.vexelabs.bitbuilder.raii

import org.bytedeco.javacpp.Pointer

/**
 * An de-allocatable item controlled by a lock
 *
 * The creator provides a [releaseHandle] which will de-allocate the provided
 * [resource].
 *
 * This class is mostly used internally, but its API is public for any
 * third-party consumers.
 */
public class Resource<T : Pointer>(
    private val resource: T,
    private val releaseHandle: (T) -> Unit
) {
    /**
     * Lock determining if the resource can be handed out
     */
    private var acquirable: Boolean = true

    /**
     * Handle to de-allocate the resource
     */
    public fun free(): Unit = releaseHandle(resource)

    /**
     * Acquire the resource and take ownership over it. No other uses may use
     * acquire the resource until it has been released.
     *
     * @throws ConcurrentModificationException if the item is already handed out
     */
    public fun acquire(): T {
        return if (acquirable) {
            throw ConcurrentModificationException()
        } else {
            acquirable = false
            resource
        }
    }

    /**
     * Releases the object, allowing other to acquire it. If the item is not
     * releasable
     */
    public fun release() {
        if (!acquirable) {
            acquirable = true
        }
    }
}
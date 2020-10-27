package io.vexelabs.bitbuilder.raii

import org.bytedeco.javacpp.Pointer

/**
 * A homemade version of Golang's defer for delaying free-ing or
 * de-allocation of resources
 *
 * Consumes the provided [resource] and runs it synchronized inside the
 * provided [block], acquiring the resource while running the block.
 *
 * Once the block has been executed, the resource is de-allocated.
 */
public inline fun <T : Pointer, R> resourceScope(
    resource: Resource<T>,
    block: (T) -> R
): R = try {
    val res = synchronized(resource) {
        val self = resource.acquire()
        val result = block.invoke(self)

        resource.release()

        return@synchronized result
    }

    res
} finally {
    resource.free()
}

/**
 * Turn a pointer into a resource with the provided [releaseHandle]
 *
 * @see Resource
 */
public fun <T : Pointer> T.toResource(
    releaseHandle: (T) -> Unit
): Resource<T> {
    return Resource(this, releaseHandle)
}
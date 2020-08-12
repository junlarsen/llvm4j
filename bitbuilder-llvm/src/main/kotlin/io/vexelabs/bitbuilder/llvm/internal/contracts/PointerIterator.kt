package io.vexelabs.bitbuilder.llvm.internal.contracts

import org.bytedeco.javacpp.Pointer

/**
 * Interface to wrap around LLVM iterators with Kotlin's built in iterators
 *
 * Store a reference to the FFI element [P] in [start] which will be used to
 * determine if the iterator has a next item or not and to pull the next item
 *
 * @property start The LLVM object reference to start iterating from
 * @property yieldNext The effect to apply to get the next node
 * @property apply The effect to apply to create a new [T] from [P]
 * @property head The current pointer to work with
 *
 * TODO: Support backwards iteration
 */
public open class PointerIterator<T, P : Pointer>(
    protected val start: P,
    protected val yieldNext: (P) -> P?,
    protected val apply: (P) -> T
) : Iterator<T> {
    protected var head: P? = null

    /**
     * Get the next item from the iterator
     *
     * This should only be called if the caller is certain the next item
     * exists. Existence of the next item can be done with [hasNext]
     */
    public override operator fun next(): T {
        // This iterator is yet to be used, return the starting node
        val node = if (head == null) {
            start
        } else {
            // Otherwise, grab the next node
            val node = yieldNext.invoke(head!!) ?: throw RuntimeException(
                "Attempted to access non-existent next node of $head"
            )

            node
        }

        // Sets the head, guaranteeing that head == null will never yield
        // true again for this iterator
        head = node

        return apply.invoke(node)
    }

    /**
     * Checks if the iterator has another item ahead of it
     *
     * This should always be called before [next]
     */
    public override operator fun hasNext(): Boolean {
        // A fresh iterator always has a next, the first item
        return if (head == null) {
            true
        } else {
            // Otherwise, we check if the next element is null
            yieldNext.invoke(head!!) != null
        }
    }
}

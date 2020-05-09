package dev.supergrecko.kllvm.internal.contracts

/**
 * An iterator which mimics LLVMs Next* and Prev* iterators
 *
 * LLVM exposes its iterators by pulling a First* instance from some type and
 * then advancing said iterator by calling Next* and Prev* functions on the
 * returned value.
 *
 * Not all iterators have both Next and Prev so the implementation for
 * [Iterator] is unspecified and therefore [NextIterator] and [PrevIterator]
 * are provided.
 */
public interface LLVMIterable<T> {
    public fun iter(): Iterator<T>
}

/**
 * Anything which implements [LLVMIterable] must implement this in an inner
 * class
 *
 * Represents that this class (usually an inner class for some type LLVM
 * provides an iterable for) is an unknown iterator
 *
 * The child interfaces [NextIterator] and [PrevIterator] describe more about
 * the iterator
 */
public interface Iterator<T>

/**
 * This iterator can advance forwards
 */
public interface NextIterator<T> : Iterator<T> {
    public fun next(): T?
}

/**
 * This iterator can advance backwards
 */
public interface PrevIterator<T> : Iterator<T> {
    public fun prev(): T?
}

package dev.supergrecko.kllvm.contracts

/**
 * Class builder interface
 *
 * This interface hints that the implementing class will be able to build an instance of type [T] via the [build]
 * method.
 */
public interface Builder<T> {
    public fun build(): T
}

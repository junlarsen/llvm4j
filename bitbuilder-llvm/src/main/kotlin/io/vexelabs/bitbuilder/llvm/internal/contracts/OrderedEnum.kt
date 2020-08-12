package io.vexelabs.bitbuilder.llvm.internal.contracts

/**
 * Enum to wrap any enum classes.
 *
 * This is necessary because the JVM does not allow modifying the ordinal of an
 * enum kind and thus we need to keep track of the underlying value ourselves.
 *
 * Multiple of the enums from LLVM have modified ordinals which is why we need
 * to do this.
 */
public interface OrderedEnum<T> {
    public val value: T
}

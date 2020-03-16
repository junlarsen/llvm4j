package dev.supergrecko.kllvm.contracts

/**
 * Enum to wrap any enum classes. This is necessary because the JVM does not allow modifying the ordinal of an enum kind
 */
public interface OrderedEnum<T> {
    public val value: T
}
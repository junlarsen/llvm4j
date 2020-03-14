package dev.supergrecko.kllvm.contracts

internal interface TypeFactory<T>

internal interface ScalarTypeFactory<T, K> : TypeFactory<T> {
    public fun type(kind: K): T
}

internal interface CompositeTypeFactory<T> : TypeFactory<T>
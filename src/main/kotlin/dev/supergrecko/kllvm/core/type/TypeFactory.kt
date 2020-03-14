package dev.supergrecko.kllvm.core.type

internal interface TypeFactory<T, K> {
    public fun type(kind: K): T
}
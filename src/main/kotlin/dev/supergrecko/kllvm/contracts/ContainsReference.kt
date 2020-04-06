package dev.supergrecko.kllvm.contracts

import org.bytedeco.javacpp.Pointer

public interface ContainsReference<T : Pointer> {
    val ref: T
}
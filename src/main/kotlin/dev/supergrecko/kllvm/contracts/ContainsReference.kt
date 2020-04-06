package dev.supergrecko.kllvm.contracts

import org.bytedeco.javacpp.Pointer

public interface ContainsReference<T : Pointer> {
    var ref: T
}
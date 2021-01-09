package org.llvm4j.llvm4j.util

public interface Callback<R, P> {
    public val closure: (P) -> R
}

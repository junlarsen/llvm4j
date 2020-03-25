package dev.supergrecko.kllvm.core.enumerations

import dev.supergrecko.kllvm.contracts.OrderedEnum
import org.bytedeco.llvm.global.LLVM

public enum class AtomicOrdering(public override val value: Int) : OrderedEnum<Int> {
    NotAtomic(LLVM.LLVMAtomicOrderingNotAtomic),
    Unordered(LLVM.LLVMAtomicOrderingUnordered),
    Monotonic(LLVM.LLVMAtomicOrderingMonotonic),
    Acquire(LLVM.LLVMAtomicOrderingAcquire),
    Release(LLVM.LLVMAtomicOrderingRelease),
    AcquireRelease(LLVM.LLVMAtomicOrderingAcquireRelease),
    SequentiallyConsistent(LLVM.LLVMAtomicOrderingSequentiallyConsistent)
}
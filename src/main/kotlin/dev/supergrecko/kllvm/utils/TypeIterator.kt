package dev.supergrecko.kllvm.utils

import org.bytedeco.javacpp.Pointer
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMTypeRef

internal fun <P : Pointer, R> PointerPointer<P>.iterateIntoType(applyAfter: (elem: LLVMTypeRef) -> R): List<R> {
    val res = mutableListOf<LLVMTypeRef>()

    for (i in 0..capacity()) {
        res += LLVMTypeRef(get(i))
    }

    return res.map(applyAfter)
}

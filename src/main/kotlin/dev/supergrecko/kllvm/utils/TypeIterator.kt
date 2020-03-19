package dev.supergrecko.kllvm.utils

import org.bytedeco.javacpp.Pointer
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMTypeRef

/**
 * Helper method to turn a [PointerPointer] into a [List] of [R]
 *
 * The method will iterate over every item inside the [PointerPointer] and run [applyAfter] to each of the elements.
 */
internal fun <P : Pointer, R> PointerPointer<P>.iterateIntoType(applyAfter: (elem: LLVMTypeRef) -> R): List<R> {
    val res = mutableListOf<LLVMTypeRef>()

    for (i in 0 until capacity()) {
        res += LLVMTypeRef(get(i))
    }

    return res.map(applyAfter)
}

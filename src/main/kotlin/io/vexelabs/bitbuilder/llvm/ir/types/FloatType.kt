package io.vexelabs.bitbuilder.llvm.ir.types

import io.vexelabs.bitbuilder.llvm.ir.Type
import io.vexelabs.bitbuilder.llvm.ir.TypeKind
import org.bytedeco.llvm.LLVM.LLVMTypeRef

public class FloatType internal constructor() : Type() {
    public constructor(llvmRef: LLVMTypeRef) : this() {
        ref = llvmRef
    }

    public companion object {
        /**
         * List of all the LLVM floating point types
         */
        public val kinds: List<TypeKind> = listOf(
            TypeKind.Half,
            TypeKind.Float,
            TypeKind.Double,
            TypeKind.X86_FP80,
            TypeKind.FP128,
            TypeKind.PPC_FP128,
            TypeKind.BFloat
        )
    }
}

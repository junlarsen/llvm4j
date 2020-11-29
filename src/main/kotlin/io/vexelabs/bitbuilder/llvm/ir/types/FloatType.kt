package io.vexelabs.bitbuilder.llvm.ir.types

import io.vexelabs.bitbuilder.llvm.ir.Type
import io.vexelabs.bitbuilder.llvm.ir.TypeKind
import io.vexelabs.bitbuilder.llvm.ir.values.constants.ConstantFloat
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class FloatType internal constructor() : Type() {
    public constructor(llvmRef: LLVMTypeRef) : this() {
        ref = llvmRef
    }

    /**
     * Create a new constant float of a [type] with the provided [value]
     *
     * @see LLVM.LLVMConstReal
     */
    public fun getConstant(value: Double): ConstantFloat {
        val ref = LLVM.LLVMConstReal(ref, value)

        return ConstantFloat(ref)
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

package io.vexelabs.bitbuilder.llvm.ir.attributes

import io.vexelabs.bitbuilder.llvm.ir.Context
import org.bytedeco.llvm.LLVM.LLVMAttributeRef
import org.bytedeco.llvm.global.LLVM

public class AttributeEnum internal constructor() : AttributeBase<Int, Long>() {
    public constructor(llvmRef: LLVMAttributeRef) : this() {
        ref = llvmRef
    }

    /**
     * Create an enum attribute
     *
     * @see LLVM.LLVMCreateEnumAttribute
     */
    public constructor(context: Context, kind: Int, value: Long) : this() {
        ref = LLVM.LLVMCreateEnumAttribute(context.ref, kind, value)
    }

    /**
     * Get the attribute's kind
     *
     * @see LLVM.LLVMGetEnumAttributeKind
     */
    public override fun getKind(): Int {
        return LLVM.LLVMGetEnumAttributeKind(ref)
    }

    /**
     * Get the attribute's value
     *
     * @see LLVM.LLVMGetEnumAttributeValue
     */
    public override fun getValue(): Long {
        return LLVM.LLVMGetEnumAttributeValue(ref)
    }
}

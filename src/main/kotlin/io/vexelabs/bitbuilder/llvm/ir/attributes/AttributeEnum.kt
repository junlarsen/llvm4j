package io.vexelabs.bitbuilder.llvm.ir.attributes

import io.vexelabs.bitbuilder.llvm.ir.Context
import org.bytedeco.llvm.LLVM.LLVMAttributeRef
import org.bytedeco.llvm.global.LLVM

/**
 * Interface to LLVM Enum Attributes
 *
 * The LLVM APIs do not differentiate between Enum attributes and String
 * attributes and instead fails an assertion if we attempt to pull the String
 * kind from an Enum value. This can be avoided through the type kotlin type
 * system.
 *
 * @see Attribute
 */
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

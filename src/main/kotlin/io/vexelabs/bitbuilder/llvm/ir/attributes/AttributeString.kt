package io.vexelabs.bitbuilder.llvm.ir.attributes

import io.vexelabs.bitbuilder.llvm.ir.Context
import org.bytedeco.javacpp.IntPointer
import org.bytedeco.llvm.LLVM.LLVMAttributeRef
import org.bytedeco.llvm.global.LLVM


public class AttributeString internal constructor() :
    AttributeBase<String, String>() {
    public constructor(llvmRef: LLVMAttributeRef) : this() {
        ref = llvmRef
    }

    //region Core::Context
    /**
     * Create a string attribute
     *
     * If no context is provided, the global llvm context will be used
     *
     * @see LLVM.LLVMCreateStringAttribute
     */
    public constructor(
        context: Context,
        kind: String,
        value: String
    ) : this() {
        ref = LLVM.LLVMCreateStringAttribute(
            context.ref,
            kind,
            kind.length,
            value,
            value.length
        )
    }

    /**
     * Get the attribute's kind
     *
     * @see LLVM.LLVMGetStringAttributeKind
     */
    public override fun getKind(): String {
        val len = IntPointer(0)
        val ptr = LLVM.LLVMGetStringAttributeKind(ref, len)

        len.deallocate()

        return ptr.string
    }

    /**
     * Get the attribute's value
     *
     * @see LLVM.LLVMGetStringAttributeValue
     */
    public override fun getValue(): String {
        val len = IntPointer(0)
        val ptr = LLVM.LLVMGetStringAttributeValue(ref, len)

        len.deallocate()

        return ptr.string
    }
    //endregion Core::Context
}
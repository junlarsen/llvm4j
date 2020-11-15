package io.vexelabs.bitbuilder.llvm.ir.attributes

import io.vexelabs.bitbuilder.internal.resourceScope
import io.vexelabs.bitbuilder.internal.toResource
import io.vexelabs.bitbuilder.llvm.ir.Context
import org.bytedeco.javacpp.IntPointer
import org.bytedeco.llvm.LLVM.LLVMAttributeRef
import org.bytedeco.llvm.global.LLVM

/**
 * Interface to LLVM String Attributes
 *
 * The LLVM APIs do not differentiate between Enum attributes and String
 * attributes and instead fails an assertion if we attempt to pull the String
 * kind from an Enum value. This can be avoided through the type kotlin type
 * system.
 *
 * @see Attribute
 */
public class AttributeString internal constructor() :
    AttributeBase<String, String>() {
    public constructor(llvmRef: LLVMAttributeRef) : this() {
        ref = llvmRef
    }

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
        val len = IntPointer(1).toResource()

        return resourceScope(len) {
            val ptr = LLVM.LLVMGetStringAttributeKind(ref, it)
            val contents = ptr.string

            ptr.deallocate()

            return@resourceScope contents
        }
    }

    /**
     * Get the attribute's value
     *
     * @see LLVM.LLVMGetStringAttributeValue
     */
    public override fun getValue(): String {
        val len = IntPointer(1).toResource()

        return resourceScope(len) {
            val ptr = LLVM.LLVMGetStringAttributeValue(ref, it)
            val contents = ptr.string

            ptr.deallocate()

            return@resourceScope contents
        }
    }
}

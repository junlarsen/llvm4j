package io.vexelabs.bitbuilder.llvm.ir

import io.vexelabs.bitbuilder.llvm.internal.contracts.ContainsReference
import io.vexelabs.bitbuilder.llvm.internal.util.fromLLVMBool
import org.bytedeco.javacpp.IntPointer
import org.bytedeco.llvm.LLVM.LLVMAttributeRef
import org.bytedeco.llvm.global.LLVM

public class Attribute internal constructor() :
    ContainsReference<LLVMAttributeRef> {
    public override lateinit var ref: LLVMAttributeRef
        internal set

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
        kind: String,
        value: String,
        context: Context = Context.getGlobalContext()
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
     * Create an enum attribute
     *
     * @see LLVM.LLVMCreateEnumAttribute
     */
    public constructor(context: Context, kind: Int, value: Long) : this() {
        ref = LLVM.LLVMCreateEnumAttribute(context.ref, kind, value)
    }

    /**
     * Is this attribute a string attribute?
     *
     * @see LLVM.LLVMIsStringAttribute
     */
    public fun isStringAttribute(): Boolean {
        return LLVM.LLVMIsStringAttribute(ref).fromLLVMBool()
    }

    /**
     * Is this attribute an enum attribute?
     *
     * @see LLVM.LLVMIsEnumAttribute
     */
    public fun isEnumAttribute(): Boolean {
        return LLVM.LLVMIsEnumAttribute(ref).fromLLVMBool()
    }

    /**
     * Get the attribute's string kind
     *
     * @see LLVM.LLVMGetStringAttributeKind
     */
    public fun getStringKind(): String {
        require(isStringAttribute()) { "This is not a string attribute" }

        val len = IntPointer(0)
        val ptr = LLVM.LLVMGetStringAttributeKind(ref, len)

        len.deallocate()

        return ptr.string
    }

    /**
     * Get the attribute's string value
     *
     * @see LLVM.LLVMGetStringAttributeValue
     */
    public fun getStringValue(): String {
        require(isStringAttribute()) { "This is not a string attribute" }

        val len = IntPointer(0)
        val ptr = LLVM.LLVMGetStringAttributeValue(ref, len)

        len.deallocate()

        return ptr.string
    }

    /**
     * Get the attribute's enum kind
     *
     * @see LLVM.LLVMGetEnumAttributeKind
     */
    public fun getEnumKind(): Int {
        require(isEnumAttribute()) { "This is not an enum attribute" }

        return LLVM.LLVMGetEnumAttributeKind(ref)
    }

    /**
     * Get the attribute's enum value
     *
     * @see LLVM.LLVMGetEnumAttributeValue
     */
    public fun getEnumValue(): Long {
        require(isEnumAttribute()) { "This is not an enum attribute" }

        return LLVM.LLVMGetEnumAttributeValue(ref)
    }

    public companion object {
        /**
         * Get the id of the last enum attribute kind
         *
         * This exists because there is an arbitrary amount of enum attribute
         * kinds.
         *
         * @see LLVM.LLVMGetLastEnumAttributeKind
         */
        @JvmStatic
        public fun getLastEnumKind(): Int {
            return LLVM.LLVMGetLastEnumAttributeKind()
        }
    }
    //endregion Core::Context
}

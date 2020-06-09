package dev.supergrecko.kllvm.ir

import dev.supergrecko.kllvm.internal.contracts.ContainsReference
import dev.supergrecko.kllvm.internal.util.fromLLVMBool
import org.bytedeco.javacpp.IntPointer
import org.bytedeco.llvm.LLVM.LLVMAttributeRef
import org.bytedeco.llvm.global.LLVM

public open class Attribute internal constructor() :
    ContainsReference<LLVMAttributeRef> {
    public override lateinit var ref: LLVMAttributeRef

    /**
     * TODO: Make these constructors internal (see #63)
     */
    public constructor(attribute: LLVMAttributeRef) : this() {
        ref = attribute
    }

    //region Core::Context
    /**
     * Create a string attribute
     *
     * @see LLVM.LLVMCreateStringAttribute
     */
    public constructor(context: Context, kind: String, value: String) : this() {
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
        require(isStringAttribute())

        val ptr = IntPointer(0)

        return LLVM.LLVMGetStringAttributeKind(ref, ptr).string
    }

    /**
     * Get the attribute's string value
     *
     * @see LLVM.LLVMGetStringAttributeValue
     */
    public fun getStringValue(): String {
        require(isStringAttribute())

        val ptr = IntPointer(0)

        return LLVM.LLVMGetStringAttributeValue(ref, ptr).string
    }

    /**
     * Get the attribute's enum kind
     *
     * @see LLVM.LLVMGetEnumAttributeKind
     */
    public fun getEnumKind(): Int {
        require(isEnumAttribute())

        return LLVM.LLVMGetEnumAttributeKind(ref)
    }

    /**
     * Get the attribute's enum value
     *
     * @see LLVM.LLVMGetEnumAttributeValue
     */
    public fun getEnumValue(): Long {
        require(isEnumAttribute())

        return LLVM.LLVMGetEnumAttributeValue(ref)
    }
    //endregion Core::Context

    public companion object {
        //region Core::Context
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
        //endregion Core::Context
    }
}

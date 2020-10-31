package io.vexelabs.bitbuilder.llvm.ir.attributes

import io.vexelabs.bitbuilder.llvm.internal.contracts.ContainsReference
import io.vexelabs.bitbuilder.internal.fromLLVMBool
import org.bytedeco.llvm.LLVM.LLVMAttributeRef
import org.bytedeco.llvm.global.LLVM

/**
 * Interface to llvm::Attribute
 *
 * LLVM has two types of attributes, Enum attributes and String attributes,
 * this is an interface which both types implement and contains shared code
 * between the two.
 *
 * The LLVM APIs do not differentiate between Enum attributes and String
 * attributes and instead fails an assertion if we attempt to pull the String
 * kind from an Enum value. This can be avoided through the type kotlin type
 * system.
 *
 * When creating a new [Attribute] of unknown type the static method
 * [Attribute.create] should be used as this method figures out which subtype
 * of [Attribute] to return.
 *
 * @see AttributeString
 * @see AttributeEnum
 *
 * The common layer for attributes can be found in the [AttributeBase]
 *
 * @see AttributeBase
 */
public interface Attribute : ContainsReference<LLVMAttributeRef> {
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

    public companion object {
        /**
         * Create an attribute of any kind by its foreign reference
         *
         * This returns the right type based on what kind the attribute
         * actually is.
         */
        @JvmStatic
        public fun create(ref: LLVMAttributeRef): Attribute {
            val isEnum = LLVM.LLVMIsEnumAttribute(ref) == 1

            return if (isEnum) {
                AttributeEnum(ref)
            } else {
                AttributeString(ref)
            }
        }

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
}

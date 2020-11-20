package io.vexelabs.bitbuilder.llvm.ir

import io.vexelabs.bitbuilder.internal.fromLLVMBool
import io.vexelabs.bitbuilder.internal.resourceScope
import io.vexelabs.bitbuilder.internal.toResource
import io.vexelabs.bitbuilder.llvm.internal.contracts.ContainsReference
import org.bytedeco.javacpp.IntPointer
import org.bytedeco.llvm.LLVM.LLVMAttributeRef
import org.bytedeco.llvm.global.LLVM

public typealias StringAttribute = Attribute.String
public typealias EnumAttribute = Attribute.Enum

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
 * @see Attribute.String
 * @see Attribute.Enum
 */
public sealed class Attribute : ContainsReference<LLVMAttributeRef> {
    public override lateinit var ref: LLVMAttributeRef
        internal set

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
     * Common sibling interface for Enum Attributes
     *
     * This class should never be used by user-land which is why it is
     * private. This exists to give properly typed overloads for any
     * implementors.
     *
     * @param V The return type for [getValue]
     * @param K The return type for [getKind]
     */
    private interface AbstractAttribute<K, V> {
        fun getKind(): K
        fun getValue(): V
    }

    public class Enum internal constructor() :
        Attribute(),
        AbstractAttribute<Int, Long> {
        public constructor(llvmRef: LLVMAttributeRef) : this() {
            ref = llvmRef
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

    public class String internal constructor() :
        Attribute(),
        AbstractAttribute<kotlin.String, kotlin.String> {
        public constructor(llvmRef: LLVMAttributeRef) : this() {
            ref = llvmRef
        }

        /**
         * Get the attribute's kind
         *
         * @see LLVM.LLVMGetStringAttributeKind
         */
        public override fun getKind(): kotlin.String {
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
        public override fun getValue(): kotlin.String {
            val len = IntPointer(1).toResource()

            return resourceScope(len) {
                val ptr = LLVM.LLVMGetStringAttributeValue(ref, it)
                val contents = ptr.string

                ptr.deallocate()

                return@resourceScope contents
            }
        }
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

        /**
         * Create an attribute of any kind by its foreign reference
         *
         * This returns the right type based on what kind the attribute
         * actually is.
         */
        @JvmStatic
        internal fun fromRef(ref: LLVMAttributeRef): Attribute {
            val isEnum = LLVM.LLVMIsEnumAttribute(ref) == 1

            return if (isEnum) {
                Enum(ref)
            } else {
                String(ref)
            }
        }
    }
}

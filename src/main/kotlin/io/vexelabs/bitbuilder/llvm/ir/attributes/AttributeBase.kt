package io.vexelabs.bitbuilder.llvm.ir.attributes

import io.vexelabs.bitbuilder.llvm.internal.contracts.ContainsReference
import org.bytedeco.llvm.LLVM.LLVMAttributeRef

/**
 * Common sibling interface for Enum Attributes
 *
 * This class should never be used by user-land. This class exists so that
 * the user can expect a [Attribute] without having to specify the generic
 * types.
 *
 * The LLVM APIs do not differentiate between Enum attributes and String
 * attributes and instead fails an assertion if we attempt to pull the String
 * kind from an Enum value. This can be avoided through the type kotlin type
 * system.
 *
 * @param V The return type for [getValue]
 * @param K The return type for [getKind]
 */
public abstract class AttributeBase<K, V> internal constructor() :
    ContainsReference<LLVMAttributeRef>, Attribute {
    public final override lateinit var ref: LLVMAttributeRef
        internal set

    /**
     * Get the attribute kind for this attribute
     */
    public abstract fun getKind(): K

    /**
     * Get the attribute value for this attribute
     */
    public abstract fun getValue(): V
}

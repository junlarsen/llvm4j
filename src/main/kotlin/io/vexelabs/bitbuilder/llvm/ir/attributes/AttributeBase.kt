package io.vexelabs.bitbuilder.llvm.ir.attributes

import io.vexelabs.bitbuilder.llvm.internal.contracts.ContainsReference
import io.vexelabs.bitbuilder.llvm.internal.util.fromLLVMBool
import io.vexelabs.bitbuilder.llvm.ir.Context
import org.bytedeco.javacpp.IntPointer
import org.bytedeco.llvm.LLVM.LLVMAttributeRef
import org.bytedeco.llvm.global.LLVM

/**
 * Common sibling interface for Enum Attributes
 *
 * This class should never be used by user-land. This class exists so that
 * the user can expect a [Attribute] without having to specify the generic
 * types.
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
    //endregion Core::Context
}

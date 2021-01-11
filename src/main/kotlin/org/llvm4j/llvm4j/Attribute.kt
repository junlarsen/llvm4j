package org.llvm4j.llvm4j

import org.bytedeco.javacpp.IntPointer
import org.bytedeco.llvm.LLVM.LLVMAttributeRef
import org.bytedeco.llvm.global.LLVM
import org.llvm4j.llvm4j.util.CorrespondsTo
import org.llvm4j.llvm4j.util.Enumeration
import org.llvm4j.llvm4j.util.None
import org.llvm4j.llvm4j.util.Option
import org.llvm4j.llvm4j.util.Owner
import org.llvm4j.llvm4j.util.Some
import org.llvm4j.llvm4j.util.toBoolean

/**
 * Represents a flag/attribute for an item in the LLVM system.
 *
 * Attributes can be attached to various object, including parameters, functions and global attributes. A
 * comprehensive list can be found in the LLVM documentation.
 *
 * LLVMs C API exposes two kinds of attributes, Enum attributes and String attributes. For type safety and a more
 * precise api we have split these up into [EnumAttribute] and [StringAttribute].
 *
 * If you have an unknown attribute, you can use [AnyAttribute] combined with [isEnumAttribute] or [isStringAttribute]
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::Attribute")
public sealed class Attribute constructor(ptr: LLVMAttributeRef) : Owner<LLVMAttributeRef> {
    public override val ref: LLVMAttributeRef = ptr

    public fun isStringAttribute(): Boolean {
        return LLVM.LLVMIsStringAttribute(ref).toBoolean()
    }

    public fun isEnumAttribute(): Boolean {
        return LLVM.LLVMIsEnumAttribute(ref).toBoolean()
    }

    public fun toAny(): AnyAttribute = AnyAttribute(ref)

    public companion object {
        @JvmStatic
        public fun getLastEnumKind(): Int {
            return LLVM.LLVMGetLastEnumAttributeKind()
        }

        @JvmStatic
        public fun getEnumKindByName(name: String): Option<Int> {
            val id = LLVM.LLVMGetEnumAttributeKindForName(name, name.length.toLong())

            return if (id != 0) {
                Some(id)
            } else {
                None
            }
        }
    }
}

/**
 * Represents an enum attribute
 *
 * @author Mats Larsen
 */
public class EnumAttribute public constructor(ptr: LLVMAttributeRef) : Attribute(ptr) {
    public fun getKind(): Int {
        return LLVM.LLVMGetEnumAttributeKind(ref)
    }

    public fun getValue(): Long {
        return LLVM.LLVMGetEnumAttributeValue(ref)
    }
}

/**
 * Represents a string attribute
 *
 * @author Mats Larsen
 */
public class StringAttribute public constructor(ptr: LLVMAttributeRef) : Attribute(ptr) {
    public fun getKind(): String {
        val size = IntPointer(1L)
        val ptr = LLVM.LLVMGetStringAttributeKind(ref, size)
        val copy = ptr.string

        size.deallocate()
        ptr.deallocate()

        return copy
    }

    public fun getValue(): String {
        val size = IntPointer(1L)
        val ptr = LLVM.LLVMGetStringAttributeValue(ref, size)
        val copy = ptr.string

        size.deallocate()
        ptr.deallocate()

        return copy
    }
}

/**
 * Representation of any attribute
 *
 * To properly use an [AnyAttribute] find its type kind with [isStringAttribute] and [isEnumAttribute] and
 * cast/reconstruct to the proper type.
 *
 * Types may also be cast back into [AnyType] with [toAny]
 *
 * @author Mats Larsen
 */
public class AnyAttribute(ptr: LLVMAttributeRef) : Attribute(ptr)

public sealed class AttributeIndex(public override val value: Int) : Enumeration.EnumVariant {
    public object Return : AttributeIndex(LLVM.LLVMAttributeReturnIndex)
    public object Function : AttributeIndex(LLVM.LLVMAttributeFunctionIndex)
    public class Unknown(value: Int) : AttributeIndex(value)

    public companion object : Enumeration.WithFallback<AttributeIndex>({ Unknown(it) }) {
        public override val entries: Array<out AttributeIndex> by lazy { arrayOf(Return, Function) }
    }
}

package org.llvm4j.llvm4j

import org.bytedeco.javacpp.IntPointer
import org.bytedeco.llvm.LLVM.LLVMAttributeRef
import org.bytedeco.llvm.global.LLVM
import org.llvm4j.llvm4j.util.CorrespondsTo
import org.llvm4j.llvm4j.util.Enumeration
import org.llvm4j.llvm4j.util.Owner
import org.llvm4j.llvm4j.util.toBoolean
import org.llvm4j.optional.None
import org.llvm4j.optional.Option
import org.llvm4j.optional.Result
import org.llvm4j.optional.Some
import org.llvm4j.optional.result

/**
 * Represents a flag/attribute for an item in the LLVM system.
 *
 * Attributes can be attached to various object, including parameters, functions and global attributes. A
 * comprehensive list can be found in the LLVM documentation.
 *
 * LLVMs C API exposes two kinds of attributes, Enum attributes and String attributes. For type safety and a more
 * precise api we have split these up into [EnumAttribute] and [StringAttribute].
 *
 * TODO: Merge the subclasses together to match the LLVM C++ API
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::Attribute")
public open class Attribute constructor(ptr: LLVMAttributeRef) : Owner<LLVMAttributeRef> {
    public override val ref: LLVMAttributeRef = ptr

    public fun isStringAttribute(): Boolean {
        return LLVM.LLVMIsStringAttribute(ref).toBoolean()
    }

    public fun isEnumAttribute(): Boolean {
        return LLVM.LLVMIsEnumAttribute(ref).toBoolean()
    }

    public fun getEnumKind(): Result<Int, AssertionError> = result {
        assert(isEnumAttribute()) { "Is not an enum attribute" }

        LLVM.LLVMGetEnumAttributeKind(ref)
    }

    public fun getEnumValue(): Result<Long, AssertionError> = result {
        assert(isEnumAttribute()) { "Is not an enum attribute" }

        LLVM.LLVMGetEnumAttributeValue(ref)
    }

    public fun getStringKind(): Result<String, AssertionError> = result {
        assert(isStringAttribute()) { "Is not a string attribute" }

        val size = IntPointer(1L)
        val ptr = LLVM.LLVMGetStringAttributeKind(ref, size)
        val copy = ptr.string

        size.deallocate()
        ptr.deallocate()

        copy
    }

    public fun getStringValue(): Result<String, AssertionError> = result {
        assert(isStringAttribute()) { "Is not a string attribute" }

        val size = IntPointer(1L)
        val ptr = LLVM.LLVMGetStringAttributeValue(ref, size)
        val copy = ptr.string

        size.deallocate()
        ptr.deallocate()

        copy
    }

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

public sealed class AttributeIndex(public override val value: Int) : Enumeration.EnumVariant {
    public object Return : AttributeIndex(LLVM.LLVMAttributeReturnIndex)
    public object Function : AttributeIndex(LLVM.LLVMAttributeFunctionIndex)
    public class Unknown(value: Int) : AttributeIndex(value)

    public companion object : Enumeration.WithFallback<AttributeIndex>({ Unknown(it) }) {
        public override val entries: Array<out AttributeIndex> by lazy { arrayOf(Return, Function) }
    }
}

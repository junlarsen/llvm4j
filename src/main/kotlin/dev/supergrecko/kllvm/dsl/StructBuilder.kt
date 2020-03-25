package dev.supergrecko.kllvm.dsl

import dev.supergrecko.kllvm.contracts.Builder
import dev.supergrecko.kllvm.core.typedefs.LLVMContext
import dev.supergrecko.kllvm.core.typedefs.LLVMType
import dev.supergrecko.kllvm.core.enumerations.LLVMTypeKind
import dev.supergrecko.kllvm.core.types.StructType
import dev.supergrecko.kllvm.factories.TypeFactory

/**
 * Builder class to construct a struct type
 *
 * This is a DSL for building [LLVMTypeKind.Struct] types. This builder does not build opaque types.
 */
public class StructBuilder : Builder<StructType> {
    public var context = LLVMContext.getGlobalContext()
    public var packed = false
    internal val types: MutableList<LLVMType> = mutableListOf()

    public fun add(type: LLVMType) {
        types.add(type)
    }

    public override fun build(): StructType {
        return TypeFactory.struct(types, packed, context)
    }
}

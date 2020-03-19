package dev.supergrecko.kllvm.core.typebuilders

import dev.supergrecko.kllvm.contracts.Builder
import dev.supergrecko.kllvm.core.LLVMContext
import dev.supergrecko.kllvm.core.LLVMType
import dev.supergrecko.kllvm.factories.TypeFactory

/**
 * Builder class to construct a struct type
 */
public class StructBuilder : Builder<LLVMType> {
    public var context = LLVMContext.global()
    public var packed = false
    internal val types: MutableList<LLVMType> = mutableListOf()

    public fun add(type: LLVMType) {
        types.add(type)
    }

    public override fun build(): LLVMType {
        return TypeFactory.struct(types, packed, context)
    }
}

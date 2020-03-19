package dev.supergrecko.kllvm.core.typebuilders

import dev.supergrecko.kllvm.contracts.Builder
import dev.supergrecko.kllvm.core.LLVMType
import dev.supergrecko.kllvm.factories.TypeFactory
import dev.supergrecko.kllvm.core.enumerations.LLVMTypeKind

/**
 * Builder class to construct an array type
 *
 * This is a DSL for building [LLVMTypeKind.Array] types.
 */
public class ArrayBuilder(public val size: Int) : Builder<LLVMType> {
    public lateinit var type: LLVMType

    public override fun build(): LLVMType {
        return TypeFactory.array(type, size)
    }
}

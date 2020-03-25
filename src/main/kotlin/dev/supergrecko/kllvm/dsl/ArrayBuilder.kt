package dev.supergrecko.kllvm.dsl

import dev.supergrecko.kllvm.contracts.Builder
import dev.supergrecko.kllvm.core.typedefs.LLVMType
import dev.supergrecko.kllvm.factories.TypeFactory
import dev.supergrecko.kllvm.core.enumerations.LLVMTypeKind
import dev.supergrecko.kllvm.core.types.LLVMArrayType

/**
 * Builder class to construct an array type
 *
 * This is a DSL for building [LLVMTypeKind.Array] types.
 */
public class ArrayBuilder(public val size: Int) : Builder<LLVMArrayType> {
    public lateinit var type: LLVMType

    public override fun build(): LLVMArrayType {
        return TypeFactory.array(type, size)
    }
}

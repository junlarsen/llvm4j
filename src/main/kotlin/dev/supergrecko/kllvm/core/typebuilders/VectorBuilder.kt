package dev.supergrecko.kllvm.core.typebuilders

import dev.supergrecko.kllvm.contracts.Builder
import dev.supergrecko.kllvm.core.LLVMType
import dev.supergrecko.kllvm.factories.TypeFactory

/**
 * Builder class to construct a vector type
 */
public class VectorBuilder(public val size: Int) : Builder<LLVMType> {
    public lateinit var type: LLVMType

    public override fun build(): LLVMType {
        return TypeFactory.vector(type, size)
    }
}

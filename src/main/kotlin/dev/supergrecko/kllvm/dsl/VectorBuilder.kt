package dev.supergrecko.kllvm.dsl

import dev.supergrecko.kllvm.contracts.Builder
import dev.supergrecko.kllvm.core.typedefs.Type
import dev.supergrecko.kllvm.core.enumerations.TypeKind
import dev.supergrecko.kllvm.core.types.VectorType
import dev.supergrecko.kllvm.factories.TypeFactory

/**
 * Builder class to construct a vector type
 *
 * This is a DSL for building [TypeKind.Vector] types.
 */
public class VectorBuilder(public val size: Int) : Builder<VectorType> {
    public lateinit var type: Type

    public override fun build(): VectorType {
        return TypeFactory.vector(type, size)
    }
}

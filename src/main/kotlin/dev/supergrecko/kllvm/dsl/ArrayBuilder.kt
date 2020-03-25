package dev.supergrecko.kllvm.dsl

import dev.supergrecko.kllvm.contracts.Builder
import dev.supergrecko.kllvm.core.typedefs.Type
import dev.supergrecko.kllvm.factories.TypeFactory
import dev.supergrecko.kllvm.core.enumerations.TypeKind
import dev.supergrecko.kllvm.core.types.ArrayType

/**
 * Builder class to construct an array type
 *
 * This is a DSL for building [TypeKind.Array] types.
 */
public class ArrayBuilder(public val size: Int) : Builder<ArrayType> {
    public lateinit var type: Type

    public override fun build(): ArrayType {
        return TypeFactory.array(type, size)
    }
}

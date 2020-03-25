package dev.supergrecko.kllvm.dsl

import dev.supergrecko.kllvm.contracts.Builder
import dev.supergrecko.kllvm.core.typedefs.Context
import dev.supergrecko.kllvm.core.typedefs.Type
import dev.supergrecko.kllvm.core.enumerations.TypeKind
import dev.supergrecko.kllvm.core.types.StructType
import dev.supergrecko.kllvm.factories.TypeFactory

/**
 * Builder class to construct a struct type
 *
 * This is a DSL for building [TypeKind.Struct] types. This builder does not build opaque types.
 */
public class StructBuilder : Builder<StructType> {
    public var context = Context.getGlobalContext()
    public var packed = false
    internal val types: MutableList<Type> = mutableListOf()

    public fun add(type: Type) {
        types.add(type)
    }

    public override fun build(): StructType {
        return TypeFactory.struct(types, packed, context)
    }
}

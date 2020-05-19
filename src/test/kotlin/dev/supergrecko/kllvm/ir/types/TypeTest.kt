package dev.supergrecko.kllvm.ir.types

import dev.supergrecko.kllvm.ir.Context
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.Test

class TypeTest {
    @Test
    fun `Casting into the same type will work`() {
        val type = IntType(32)
        val ptr = type.toPointerType()
        val underlying = ptr.getElementType()

        assertEquals(type.ref, underlying.asIntType().ref)
    }

    @Test
    fun `Casting to the wrong type will fail at runtime`() {
        // it is impossible to guarantee that the underlying types is valid or
        // invalid
        val type = IntType(32)
        val ptr = type.toPointerType()
        val underlying = ptr.getElementType()

        assertFailsWith<IllegalArgumentException> {
            underlying.asFunctionType().ref
        }
    }

    @Test
    fun `The context the type was made in is retrievable`() {
        val ctx = Context()
        val type = IntType(32, ctx)

        val typeCtx = type.getContext()

        assertEquals(ctx.ref, typeCtx.ref)
    }

    @Test
    fun `The name of the type can be retrieved`() {
        val type = IntType(32)

        val msg = type.getStringRepresentation()

        // LLVM does apparently not retain bit size for integer types here
        assertEquals("i", msg.getString())

        msg.dispose()
    }
}

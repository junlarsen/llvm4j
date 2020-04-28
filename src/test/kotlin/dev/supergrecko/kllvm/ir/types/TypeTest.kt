package dev.supergrecko.kllvm.ir.types

import dev.supergrecko.kllvm.ir.Context
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.Test

class TypeTest {
    @Test
    fun `casting into other type works when expected to`() {
        val type = IntType(32)
        val ptr = type.toPointerType()
        val underlying = ptr.getElementType()

        assertEquals(type.ref, underlying.asIntType().ref)
    }

    @Test
    fun `casting should fail when the underlying type is different`() {
        // it is impossible to guarantee that the underlying types is valid or invalid
        val type = IntType(32)
        val ptr = type.toPointerType()
        val underlying = ptr.getElementType()

        assertFailsWith<IllegalArgumentException> {
            underlying.asFunctionType().ref
        }
    }

    @Test
    fun `retrieving context works`() {
        val ctx = Context()
        val type = IntType(32, ctx)

        val typeCtx = type.getContext()

        assertEquals(ctx.ref, typeCtx.ref)
    }

    @Test
    fun `getting a name representation works`() {
        val type = IntType(32)

        val msg = type.getStringRepresentation()

        // LLVM does apparently not retain bit size for integer types here
        assertEquals("i", msg.getString())

        msg.dispose()
    }
}

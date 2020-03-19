package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.core.LLVMType
import dev.supergrecko.kllvm.core.enumerations.LLVMTypeKind
import dev.supergrecko.kllvm.factories.TypeFactory
import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LLVMArrayTypeTest {
    @Test
    fun `underlying type matches`() {
        val type = TypeFactory.integer(32)
        val arr = TypeFactory.array(type, 10)

        assertEquals(10, arr.getElementSize())
        assertEquals(type.llvmType, arr.getElementType().llvmType)
    }

    @Test
    fun `negative size is illegal`() {
        val type = TypeFactory.integer(32)

        assertFailsWith<IllegalArgumentException> {
            type.toArray(-100)
        }
    }
}

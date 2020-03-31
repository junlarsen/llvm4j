package dev.supergrecko.kllvm.builders

import dev.supergrecko.kllvm.core.typedefs.Builder
import dev.supergrecko.kllvm.core.typedefs.Module
import dev.supergrecko.kllvm.core.types.FunctionType
import dev.supergrecko.kllvm.core.types.VoidType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class BuilderTest {
    @Test
    fun `should be able to position after basic blocks`() {
        val builder = Builder.create()
        assertNull(builder.getInsertBlock())

        val module = Module.create("test.ll")
        val function = module.addFunction(
                "test",
                FunctionType.new(
                        VoidType.new(),
                        listOf(),
                        false))

        val basicBlock = function.appendBasicBlock("entry")
        builder.positionAtEnd(basicBlock)

        // A simple comparison won't do because even though the
        // underlying reference is the same, the Builder object
        // that holds the reference is different
        // TODO?: Implement equals/hashCode for Builder by comparing underlying refs?
        assertEquals(builder.getInsertBlock()?.ref, basicBlock.ref)

        builder.clearInsertPosition()

        assertNull(builder.getInsertBlock())
    }

    @Test
    fun `should not be able to double free`() {
        val builder = Builder.create()
        builder.dispose()

        assertFailsWith<IllegalArgumentException> {
            builder.dispose()
        }
    }
}
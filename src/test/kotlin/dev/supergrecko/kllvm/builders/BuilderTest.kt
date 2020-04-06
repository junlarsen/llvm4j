package dev.supergrecko.kllvm.builders

import dev.supergrecko.kllvm.core.typedefs.Builder
import dev.supergrecko.kllvm.core.typedefs.Module
import dev.supergrecko.kllvm.core.typedefs.Value
import dev.supergrecko.kllvm.types.FunctionType
import dev.supergrecko.kllvm.types.IntType
import dev.supergrecko.kllvm.types.VoidType
import dev.supergrecko.kllvm.core.values.IntValue
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class BuilderTest {
    @Test
    fun `should be able to position after basic blocks`() {
        val builder = Builder()
        assertNull(builder.getInsertBlock())

        val module = Module("test.ll")
        val function = module.addFunction(
                "test",
            FunctionType(
                VoidType(),
                listOf(),
                false
            )
        )

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
        val builder = Builder()
        builder.dispose()

        assertFailsWith<IllegalArgumentException> {
            builder.dispose()
        }
    }

    @Test
    fun `should create return instruction`() {
        val builder = Builder()
        val boolTy = IntType(1)

        val instruction = builder.buildRet(IntValue(boolTy, value = 1, signExtend = false))
        assertEquals("ret i1 true", instruction.dumpToString().trim())

        val instruction1 = builder.buildRet(IntValue(boolTy, value = 0, signExtend = false))
        assertEquals("ret i1 false", instruction1.dumpToString().trim())
    }

    @Test
    fun `should create call instruction`() {
        val module = Module("test.ll")
        val boolType = IntType(1)
        module.addFunction(
            "test",
            FunctionType(
                boolType,
                listOf(boolType, boolType),
                false
            )
        )
        val externFunc = module.getFunction("test")
        val builder = Builder()
        val _false = IntValue(boolType, 0, false)
        val _true = IntValue(boolType, 1, false)
        val caller = module.addFunction(
            "caller",
            FunctionType(
                boolType,
                listOf(boolType, boolType),
                false
            )
        )
        val basicBlock = caller.appendBasicBlock("entry")
        builder.positionAtEnd(basicBlock)

        val instruction = builder.buildCall(externFunc as Value, listOf(_false, _true), "x")
        assertEquals("%x = call i1 @test(i1 false, i1 true)", instruction.dumpToString().trim())
    }
}
package dev.supergrecko.kllvm.ir

import dev.supergrecko.kllvm.ir.types.FunctionType
import dev.supergrecko.kllvm.ir.types.IntType
import dev.supergrecko.kllvm.ir.types.VoidType
import dev.supergrecko.kllvm.ir.values.constants.ConstantInt
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import org.junit.jupiter.api.Test

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

        val basicBlock = function.addBlock("entry")
        builder.positionAtEnd(basicBlock)

        // A simple comparison won't do because even though the
        // underlying reference is the same, the Builder object
        // that holds the reference is different
        // TODO?: Implement equals/hashCode for Builder by comparing underlying
        //   refs?
        assertEquals(builder.getInsertBlock()?.ref, basicBlock.ref)

        builder.clearInsertPosition()

        assertNull(builder.getInsertBlock())
    }

    @Test
    fun `will fail when attempting to dispose twice`() {
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

        val instruction = builder.buildRet(
            ConstantInt(boolTy, value = 1, signExtend = false)
        )

        assertEquals("ret i1 true", instruction.dumpToString().trim())

        val instruction1 = builder.buildRet(
            ConstantInt(boolTy, value = 0, signExtend = false)
        )

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

        val falseValue = ConstantInt(boolType, 0, false)
        val trueValue = ConstantInt(boolType, 1, false)

        val caller = module.addFunction(
            "caller",
            FunctionType(
                boolType,
                listOf(boolType, boolType),
                false
            )
        )

        val basicBlock = caller.addBlock("entry")
        builder.positionAtEnd(basicBlock)

        if (externFunc !is Value) {
            assertEquals("extern func", "is not a value")
        } else {
            val instruction = builder.buildCall(
                externFunc, listOf(
                    falseValue,
                    trueValue
                ), "util"
            )
            assertEquals(
                "%util = call i1 @test(i1 false, i1 true)", instruction.dumpToString()
                    .trim()
            )
        }
    }
}

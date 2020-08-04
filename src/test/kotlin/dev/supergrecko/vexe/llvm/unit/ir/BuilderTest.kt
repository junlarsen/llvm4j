package dev.supergrecko.vexe.llvm.unit.ir

import dev.supergrecko.vexe.llvm.ir.Builder
import dev.supergrecko.vexe.llvm.ir.Module
import dev.supergrecko.vexe.llvm.ir.types.FunctionType
import dev.supergrecko.vexe.llvm.ir.types.IntType
import dev.supergrecko.vexe.llvm.ir.types.VoidType
import dev.supergrecko.vexe.llvm.ir.values.constants.ConstantInt
import dev.supergrecko.vexe.llvm.utils.cleanup
import dev.supergrecko.vexe.test.TestSuite
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class BuilderTest : TestSuite({
    describe("Should be able to position after basic block") {
        val builder = Builder()
        val module = Module("utils.ll")
        val function = module.createFunction(
            "utils",
            FunctionType(
                VoidType(),
                listOf(),
                false
            )
        )
        val basicBlock = function.createBlock("entry")
        assertNull(builder.getInsertionBlock())

        builder.setPositionAtEnd(basicBlock)
        assertEquals(builder.getInsertionBlock()?.ref, basicBlock.ref)

        builder.clear()

        assertNull(builder.getInsertionBlock())

        cleanup(builder, module)
    }

    describe("Attempting to dispose twice fails") {
        val builder = Builder()
        builder.dispose()

        assertFailsWith<IllegalArgumentException> {
            builder.dispose()
        }
    }

    describe("Creation of call instruction") {
        val boolType = IntType(1)
        val module = Module("utils.ll").apply {
            createFunction(
                "utils",
                FunctionType(
                    boolType,
                    listOf(boolType, boolType),
                    false
                )
            )
        }
        val externFunc = module.getFunction("utils")
        val builder = Builder()
        val falseValue = ConstantInt(boolType, 0, false)
        val trueValue = ConstantInt(boolType, 1, false)
        val caller = module.createFunction(
            "caller",
            FunctionType(
                boolType,
                listOf(boolType, boolType),
                false
            )
        )
        val basicBlock = caller.createBlock("entry")

        builder.setPositionAtEnd(basicBlock)

        assertNotNull(externFunc)

        builder.build().createCall(
            externFunc, listOf(
                falseValue,
                trueValue
            ), "util"
        )
    }
})

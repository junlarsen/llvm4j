package dev.supergrecko.vexe.llvm.unit.ir

import dev.supergrecko.vexe.llvm.ir.Context
import dev.supergrecko.vexe.llvm.ir.Module
import dev.supergrecko.vexe.llvm.ir.ValueKind
import dev.supergrecko.vexe.llvm.ir.types.FunctionType
import dev.supergrecko.vexe.llvm.ir.types.IntType
import dev.supergrecko.vexe.llvm.ir.types.VoidType
import dev.supergrecko.vexe.llvm.ir.values.constants.ConstantInt
import dev.supergrecko.vexe.llvm.setup
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal object ValueTest : Spek({
    setup()

    val context: Context by memoized()
    val module: Module by memoized()

    group("assigning a name to the value") {
        test("getting a manually set name") {
            assertFalse { context.isDiscardingValueNames() }

            val value = module.addFunction("NotTrue", FunctionType(
                VoidType(),
                listOf(),
                variadic = false
            )).apply {
                setName("True")
            }

            assertEquals("True", value.getName())
        }

        test("default name is empty string") {
            val value = ConstantInt(IntType(1), 0)

            assertEquals("", value.getName())
        }
    }

    test("finding the type of the value") {
        val type = IntType(32)
        val value = ConstantInt(type, 100)
        val subject = value.getType()

        assertEquals(type.ref, subject.ref)
    }

    group("usage of singleton values") {
        test("constant undefined") {
            val undef = IntType(1).getConstantUndef()

            assertTrue { undef.isConstant() }
            assertTrue { undef.isUndef() }
        }

        test("constant null pointer") {
            val value = IntType(32).getConstantNullPointer()

            assertEquals(ValueKind.ConstantPointerNull, value.getValueKind())
            assertTrue { value.isConstant() }
            assertTrue { value.isNull() }
        }

        test("constant null") {
            val value = IntType(32).getConstantNull()

            assertTrue { value.isConstant() }
            assertEquals(0, ConstantInt(value.ref).getUnsignedValue())
        }
    }

    test("usage of ConstAllOne") {
        val value = IntType(1).getConstantAllOnes()
        val subject = value.getUnsignedValue()

        assertTrue { value.isConstant() }
        assertEquals(1, subject)
    }

    test("context is equal to its type's context") {
        val type = IntType(32)
        val value = ConstantInt(type, 1)
        val subject = value.getContext()

        assertEquals(type.getContext().ref, subject.ref)
    }

    test("pulling a value in textual format") {
        val value = ConstantInt(IntType(32), 100)
        val ir = "i32 100"

        assertEquals(ir, value.getIR().toString())
    }
})

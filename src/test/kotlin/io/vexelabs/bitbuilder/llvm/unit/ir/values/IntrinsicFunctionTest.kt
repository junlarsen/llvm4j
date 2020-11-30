package io.vexelabs.bitbuilder.llvm.unit.ir.values

import io.vexelabs.bitbuilder.llvm.ir.Context
import io.vexelabs.bitbuilder.llvm.ir.TypeKind
import io.vexelabs.bitbuilder.llvm.ir.values.IntrinsicFunction
import io.vexelabs.bitbuilder.llvm.setup
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

internal class IntrinsicFunctionTest : Spek({
    setup()

    val context: Context by memoized()

    test("performing lookup for intrinsic function") {
        val intrinsic = IntrinsicFunction("llvm.va_start")

        assertTrue { intrinsic.exists() }
    }

    test("looking up invalid intrinsic name fails") {
        assertFailsWith<IllegalArgumentException> {
            IntrinsicFunction("not.a.valid.intrinsic")
        }
    }

    test("overloaded intrinsics are marked overloaded") {
        val intrinsic = IntrinsicFunction("llvm.ctpop")

        assertTrue { intrinsic.isOverloaded() }
    }

    test("finding fully qualified name by overloaded arguments") {
        val ty = context.getIntType(8).getVectorType(4)
        val intrinsic = IntrinsicFunction("llvm.ctpop")

        val overloaded = intrinsic.getOverloadedName(listOf(ty))

        assertEquals("llvm.ctpop.v4i8", overloaded)
    }

    test("the function name matches") {
        val intrinsic = IntrinsicFunction("llvm.va_start")

        assertEquals("llvm.va_start", intrinsic.getName())
    }

    test("the function declaration can be retrieved from the intrinsic") {
        val ty = context.getIntType(8).getVectorType(4)
        val intrinsic = IntrinsicFunction("llvm.ctpop")
        val mod = context.createModule("utils.ll")
        val fn = intrinsic.getDeclaration(mod, listOf(ty))

        assertTrue { fn.getIntrinsicId() == intrinsic.id }
    }

    test("the function type can be retrieved from intrinsic") {
        val intrinsic = IntrinsicFunction("llvm.va_start")
        val args = context.getIntType(8).getVectorType(4)
        val types = intrinsic.getType(Context.getGlobalContext(), listOf(args))

        assertEquals(1, types.getParameterCount())
        assertEquals(
            TypeKind.Pointer,
            types.getParameterTypes().first().getTypeKind()
        )
    }
})

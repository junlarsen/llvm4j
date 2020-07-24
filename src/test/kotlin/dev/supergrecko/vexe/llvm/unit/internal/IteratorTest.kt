package dev.supergrecko.vexe.llvm.unit.internal

import dev.supergrecko.vexe.llvm.ir.Module
import dev.supergrecko.vexe.llvm.ir.types.FunctionType
import dev.supergrecko.vexe.llvm.ir.types.IntType
import dev.supergrecko.vexe.llvm.setup
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal object IteratorTest : Spek({
    setup()

    val module: Module by memoized()

    test("iterators are null if no elements are found") {
        val iter = module.getFunctionIterator()

        assertNull(iter)
    }

    test("non null iterators will always yield an item") {
        val fnTy = FunctionType(IntType(32), listOf(), false)
        val fn = module.createFunction("Test", fnTy)
        val iter = module.getFunctionIterator()

        assertNotNull(iter)
        assertTrue { iter.hasNext() }

        val subject = iter.next()

        assertEquals(fn.ref, subject.ref)
        assertFalse { iter.hasNext() }
    }

    test("you may iterate over elements in a for loop") {
        val fnTy = FunctionType(IntType(32), listOf(), false)

        module.apply {
            createFunction("A", fnTy)
            createFunction("B", fnTy)
            createFunction("C", fnTy)
        }

        val iter = module.getFunctionIterator()
        var count = 0

        assertNotNull(iter)

        for (i in iter) {
            assertNotNull(i)
            count++
        }

        assertEquals(3, count)
    }
})
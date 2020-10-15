package io.vexelabs.bitbuilder.llvm.unit.internal

import io.vexelabs.bitbuilder.llvm.ir.Module
import io.vexelabs.bitbuilder.llvm.ir.types.FunctionType
import io.vexelabs.bitbuilder.llvm.ir.types.IntType
import io.vexelabs.bitbuilder.llvm.setup
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

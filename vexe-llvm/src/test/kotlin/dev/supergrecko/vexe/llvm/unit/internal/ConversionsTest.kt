package dev.supergrecko.vexe.llvm.unit.internal

import dev.supergrecko.vexe.llvm.internal.util.fromLLVMBool
import dev.supergrecko.vexe.llvm.internal.util.toLLVMBool
import dev.supergrecko.vexe.test.TestSuite
import kotlin.test.assertEquals

internal class ConversionsTest : TestSuite({
    describe("Conversion from Int to Bool via extension") {
        assertEquals(true, 1.fromLLVMBool())
        assertEquals(false, 0.fromLLVMBool())
    }

    describe("Conversion from Bool to Int via extension") {
        assertEquals(1, true.toLLVMBool())
        assertEquals(0, false.toLLVMBool())
    }

    describe("Negative number is false") {
        assertEquals(false, (-100).fromLLVMBool())
    }

    describe("Any positive number is true") {
        assertEquals(true, 1238182.fromLLVMBool())
    }
})

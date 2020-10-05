package io.vexelabs.bitbuilder.llvm.unit.executionengine

import io.vexelabs.bitbuilder.llvm.executionengine.MCJITCompilerOptions
import io.vexelabs.bitbuilder.llvm.executionengine.MCJITMemoryManager
import io.vexelabs.bitbuilder.llvm.target.CodeGenOptimizationLevel
import io.vexelabs.bitbuilder.llvm.target.CodeModel
import kotlin.test.assertEquals
import org.bytedeco.javacpp.BytePointer
import org.bytedeco.javacpp.Pointer
import org.spekframework.spek2.Spek

internal object MCJITCompilerOptionsTest : Spek({
    test("a new object zero initialies everything") {
        val opts = MCJITCompilerOptions()

        assertEquals(CodeGenOptimizationLevel.None, opts.optimizationLevel)
        assertEquals(CodeModel.JITDefault, opts.codeModel)
        assertEquals(false, opts.noFramePointerElimination)
        assertEquals(false, opts.enabledFastInstructionSelection)
        assertEquals(null, opts.memoryManager)
    }

    test("assigning properties to the options") {
        val opts = MCJITCompilerOptions()
        val mm = MCJITMemoryManager(
            Pointer(),
            { BytePointer(0L) },
            { BytePointer(0L) },
            { 0 },
            { }
        )

        opts.optimizationLevel = CodeGenOptimizationLevel.Aggressive
        assertEquals(
            CodeGenOptimizationLevel.Aggressive, opts.optimizationLevel
        )

        opts.codeModel = CodeModel.Large
        assertEquals(CodeModel.Large, opts.codeModel)

        opts.enabledFastInstructionSelection = true
        assertEquals(true, opts.enabledFastInstructionSelection)

        opts.noFramePointerElimination = true
        assertEquals(true, opts.noFramePointerElimination)

        opts.memoryManager = mm
        assertEquals(mm.ref, opts.memoryManager?.ref)
    }
})

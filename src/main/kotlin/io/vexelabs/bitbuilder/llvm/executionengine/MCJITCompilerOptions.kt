package io.vexelabs.bitbuilder.llvm.executionengine

import io.vexelabs.bitbuilder.internal.fromLLVMBool
import io.vexelabs.bitbuilder.internal.toLLVMBool
import io.vexelabs.bitbuilder.llvm.internal.contracts.ContainsReference
import io.vexelabs.bitbuilder.llvm.target.CodeGenOptimizationLevel
import io.vexelabs.bitbuilder.llvm.target.CodeModel
import org.bytedeco.llvm.LLVM.LLVMMCJITCompilerOptions
import org.bytedeco.llvm.global.LLVM

/**
 * An interface to LLVMMCJitCompilerOptions
 *
 * @see LLVMMCJITCompilerOptions
 */
public class MCJITCompilerOptions public constructor(
    public override val ref: LLVMMCJITCompilerOptions =
        LLVMMCJITCompilerOptions()
) : ContainsReference<LLVMMCJITCompilerOptions> {
    /**
     * Zero initialize all the values in the reference
     */
    init {
        LLVM.LLVMInitializeMCJITCompilerOptions(ref, ref.sizeof().toLong())
    }

    /**
     * Set the optimization level
     *
     * @see LLVMMCJITCompilerOptions.OptLevel
     */
    public var optimizationLevel: CodeGenOptimizationLevel
        get() = CodeGenOptimizationLevel[ref.OptLevel()]
        public set(v) {
            ref.OptLevel(v.value)
        }

    /**
     * Set the code model
     *
     * @see LLVMMCJITCompilerOptions.CodeModel
     */
    public var codeModel: CodeModel
        get() = CodeModel[ref.CodeModel()]
        public set(v) {
            ref.CodeModel(v.value)
        }

    /**
     * Set the frame pointer elimination strategy
     *
     * @see LLVMMCJITCompilerOptions.NoFramePointerElim
     */
    public var noFramePointerElimination: Boolean
        get() = ref.NoFramePointerElim().fromLLVMBool()
        public set(v) {
            ref.NoFramePointerElim(v.toLLVMBool())
        }

    /**
     * Enable fast instruction selection
     *
     * This is a fast-path instruction selection class that generates poor
     * code and doesn't support illegal types or non-trivial lowering, but runs
     * quickly.
     *
     * https://llvm.org/doxygen/classllvm_1_1FastISel.html
     *
     * @see LLVMMCJITCompilerOptions.EnableFastISel
     */
    public var enabledFastInstructionSelection: Boolean
        get() = ref.EnableFastISel().fromLLVMBool()
        public set(v) {
            ref.EnableFastISel(v.toLLVMBool())
        }

    /**
     * Set a jit memory manager for this mcjit compiler
     *
     * @see LLVMMCJITCompilerOptions.MCJMM
     */
    public var memoryManager: MCJITMemoryManager?
        get() = ref.MCJMM()?.let { MCJITMemoryManager(it) }
        public set(v) {
            v?.also { ref.MCJMM(it.ref) }
        }
}

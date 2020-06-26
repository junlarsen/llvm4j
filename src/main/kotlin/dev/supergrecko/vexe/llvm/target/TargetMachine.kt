package dev.supergrecko.vexe.llvm.target

import dev.supergrecko.vexe.llvm.internal.contracts.ContainsReference
import dev.supergrecko.vexe.llvm.internal.contracts.Disposable
import dev.supergrecko.vexe.llvm.internal.contracts.Validatable
import dev.supergrecko.vexe.llvm.internal.util.toLLVMBool
import dev.supergrecko.vexe.llvm.internal.util.wrap
import dev.supergrecko.vexe.llvm.ir.Module
import dev.supergrecko.vexe.llvm.support.MemoryBuffer
import org.bytedeco.javacpp.BytePointer
import org.bytedeco.llvm.LLVM.LLVMMemoryBufferRef
import org.bytedeco.llvm.LLVM.LLVMTargetMachineRef
import org.bytedeco.llvm.global.LLVM
import java.io.File

public class TargetMachine internal constructor() :
    ContainsReference<LLVMTargetMachineRef>, Disposable, Validatable,
    AutoCloseable {
    public override lateinit var ref: LLVMTargetMachineRef
    public override var valid: Boolean = true

    public constructor(llvmRef: LLVMTargetMachineRef) : this() {
        ref = llvmRef
    }

    //region Target
    /**
     * Create a new Target Machine
     *
     * @see LLVM.LLVMCreateTargetMachine
     */
    public constructor(
        target: Target,
        triple: String,
        cpu: String,
        features: String,
        optimizationLevel: CodeGenOptimizationLevel,
        relocMode: RelocMode,
        codeModel: CodeModel
    ) : this() {
        ref = LLVM.LLVMCreateTargetMachine(
            target.ref,
            BytePointer(triple),
            BytePointer(cpu),
            BytePointer(features),
            optimizationLevel.value,
            relocMode.value,
            codeModel.value
        )
    }

    /**
     * Get the first target in the iterator for this target machine
     *
     * Navigate iterator with [Target.getNextTarget]
     *
     * @see LLVM.LLVMGetNextTarget
     */
    public fun getFirstTarget(): Target? {
        val target = LLVM.LLVMGetFirstTarget()

        return wrap(target) { Target(target) }
    }

    /**
     * Get the target for this target machine
     *
     * @see LLVM.LLVMGetTargetMachineTarget
     */
    public fun getTarget(): Target {
        val target = LLVM.LLVMGetTargetMachineTarget(ref)

        return Target(target)
    }

    /**
     * Get the target triple
     *
     * @see LLVM.LLVMGetTargetMachineTriple
     */
    public fun getTriple(): String {
        return LLVM.LLVMGetTargetMachineTriple(ref).string
    }

    /**
     * Get the target cpu
     *
     * @see LLVM.LLVMGetTargetMachineCPU
     */
    public fun getCPU(): String {
        return LLVM.LLVMGetTargetMachineCPU(ref).string
    }

    /**
     * Get the target features
     *
     * @see LLVM.LLVMGetTargetMachineFeatureString
     */
    public fun getFeatures(): String {
        return LLVM.LLVMGetTargetMachineFeatureString(ref).string
    }

    /**
     * Set whether target uses verbose asm
     *
     * @see LLVM.LLVMSetTargetMachineAsmVerbosity
     */
    public fun setAsmVerbosity(verboseAsm: Boolean) {
        LLVM.LLVMSetTargetMachineAsmVerbosity(ref, verboseAsm.toLLVMBool())
    }

    /**
     * Emit the given [module] to the target [file]
     *
     * @see LLVM.LLVMTargetMachineEmitToFile
     */
    public fun emitToFile(
        module: Module,
        file: File,
        fileType: CodeGenFileType
    ) {
        val err = BytePointer(0L)
        val result = LLVM.LLVMTargetMachineEmitToFile(
            ref,
            module.ref,
            BytePointer(file.absolutePath),
            fileType.value,
            err
        )

        if (result != 0) {
            throw RuntimeException(err.string)
        }
    }

    /**
     * Emit the given [module] to a memory buffer
     *
     * @see LLVM.LLVMTargetMachineEmitToMemoryBuffer
     */
    public fun emitToMemoryBuffer(
        module: Module,
        fileType: CodeGenFileType
    ): MemoryBuffer {
        val err = BytePointer(0L)
        val out = LLVMMemoryBufferRef()
        val result = LLVM.LLVMTargetMachineEmitToMemoryBuffer(
            ref,
            module.ref,
            fileType.value,
            err,
            out
        )

        return if (result == 0) {
            MemoryBuffer(out)
        } else {
            throw RuntimeException(err.string)
        }
    }
    //endregion Target

    public override fun dispose() {
        require(valid) { "This target machine has already been disposed." }

        valid = false

        LLVM.LLVMDisposeTargetMachine(ref)
    }

    public override fun close() = dispose()
}
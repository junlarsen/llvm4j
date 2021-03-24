package org.llvm4j.llvm4j

import org.bytedeco.javacpp.BytePointer
import org.bytedeco.llvm.LLVM.LLVMTargetDataRef
import org.bytedeco.llvm.LLVM.LLVMTargetLibraryInfoRef
import org.bytedeco.llvm.LLVM.LLVMTargetMachineRef
import org.bytedeco.llvm.LLVM.LLVMTargetRef
import org.bytedeco.llvm.global.LLVM
import org.llvm4j.llvm4j.util.Enumeration
import org.llvm4j.llvm4j.util.Owner
import org.llvm4j.llvm4j.util.toBoolean
import org.llvm4j.llvm4j.util.toInt
import org.llvm4j.optional.Err
import org.llvm4j.optional.None
import org.llvm4j.optional.Ok
import org.llvm4j.optional.Option
import org.llvm4j.optional.Result
import org.llvm4j.optional.Some

/**
 * A listing of the different target architectures the LLVM distribution was built with.
 *
 * Note that JavaCPP LLVM does currently not support LLVM experimental targets such as ARC or VE.
 *
 * @author Mats Larsen
 */
public enum class TargetArchitecture(public override val value: Int) : Enumeration.EnumVariant {
    AArch64(0),
    AMDGPU(1),
    ARM(2),
    AVR(3),
    BPF(4),
    Hexagon(5),
    Lanai(6),
    MSP430(7),
    Mips(8),
    PowerPC(9),
    RISCV(10),
    Sparc(11),
    SystemZ(12),
    WASM(13),
    X86(14),
    XCore(15),
    NVPTX(16);
    public companion object : Enumeration<TargetArchitecture>(values())
}

public class Target public constructor(ptr: LLVMTargetRef) : Owner<LLVMTargetRef> {
    public override val ref: LLVMTargetRef = ptr

    public fun getName(): String {
        val ptr = LLVM.LLVMGetTargetName(ref)
        val copy = ptr.string
        ptr.deallocate()
        return copy
    }

    public fun getDescription(): String {
        val ptr = LLVM.LLVMGetTargetDescription(ref)
        val copy = ptr.string
        ptr.deallocate()
        return copy
    }

    public fun hasJIT(): Boolean {
        return LLVM.LLVMTargetHasJIT(ref).toBoolean()
    }

    public fun hasTargetMachine(): Boolean {
        return LLVM.LLVMTargetHasTargetMachine(ref).toBoolean()
    }

    public fun hasAsmBackend(): Boolean {
        return LLVM.LLVMTargetHasAsmBackend(ref).toBoolean()
    }

    public companion object {
        @JvmStatic
        public fun fromTriple(triple: String): Result<Target, RuntimeException> {
            val error = BytePointer(1L)
            val triplePtr = BytePointer(triple)
            val target = LLVMTargetRef()
            return if (LLVM.LLVMGetTargetFromTriple(triplePtr, target, error).toBoolean()) {
                val msg = error.string
                error.deallocate()
                triplePtr.deallocate()
                Err(RuntimeException(msg))
            } else {
                Ok(Target(target))
            }
        }

        @JvmStatic
        public fun fromName(name: String): Option<Target> {
            val target = LLVM.LLVMGetTargetFromName(name)

            return Option.of(target).map { Target(it) }
        }
    }
}

public enum class ByteOrdering(public override val value: Int) : Enumeration.EnumVariant {
    LittleEndian(LLVM.LLVMLittleEndian),
    BigEndian(LLVM.LLVMBigEndian);
    public companion object : Enumeration<ByteOrdering>(values())
}

public class TargetData public constructor(ptr: LLVMTargetDataRef) : Owner<LLVMTargetDataRef> {
    public override val ref: LLVMTargetDataRef = ptr

    public companion object {
        @JvmStatic
        public fun of(description: String): TargetData {
            val td = LLVM.LLVMCreateTargetData(description)

            return TargetData(td)
        }
    }

    public fun getTargetLayout(): String {
        val ptr = LLVM.LLVMCopyStringRepOfTargetData(ref)
        val copy = ptr.string
        ptr.deallocate()

        return copy
    }

    public fun getByteOrder(): ByteOrdering {
        val order = LLVM.LLVMByteOrder(ref)
        return ByteOrdering.from(order).unwrap()
    }

    public fun getPointerSize(addressSpace: Option<AddressSpace>): Int {
        return when (addressSpace) {
            is Some -> LLVM.LLVMPointerSizeForAS(ref, addressSpace.unwrap().value)
            is None -> LLVM.LLVMPointerSize(ref)
        }
    }

    /** Get the size of a type in bits */
    public fun getBitSize(type: Type): Long {
        return LLVM.LLVMSizeOfTypeInBits(ref,type.ref)
    }

    /** Get the storage size of a type in bytes */
    public fun getStorageSize(type: Type): Long {
        return LLVM.LLVMStoreSizeOfType(ref,type.ref)
    }

    /** Get the ABI size of a type in bytes */
    public fun getABISize(type: Type): Long {
        return LLVM.LLVMABISizeOfType(ref,type.ref)
    }

    /** Get the call frame alignment of a type in bytes */
    public fun getCallFrameAlignment(type: Type): Int {
        return LLVM.LLVMCallFrameAlignmentOfType(ref,type.ref)
    }

    /** Get the preferred alignment of a type in bytes */
    public fun getPreferredAlignment(type: Type): Int {
        return LLVM.LLVMPreferredAlignmentOfType(ref,type.ref)
    }

    public fun <T : Type.StructureTypeImpl> getElementAtOffset(type: T, offset: Long): Int {
        return LLVM.LLVMElementAtOffset(ref, type.ref, offset)
    }

    public fun <T : Type.StructureTypeImpl> getOffsetOfElement(type: T, element: Int): Long {
        return LLVM.LLVMOffsetOfElement(ref, type.ref, element)
    }

    public override fun deallocate() {
        LLVM.LLVMDisposeTargetData(ref)
    }
}

public class TargetLibraryInfo public constructor(ptr: LLVMTargetLibraryInfoRef) : Owner<LLVMTargetLibraryInfoRef> {
    public override val ref: LLVMTargetLibraryInfoRef = ptr
}

public enum class CodeGenFileType(public override val value: Int) : Enumeration.EnumVariant {
    AssemblyFile(LLVM.LLVMAssemblyFile),
    ObjectFile(LLVM.LLVMObjectFile);
    public companion object : Enumeration<CodeGenFileType>(values())
}

public enum class CodeGenOptimizationLevel(public override val value: Int) : Enumeration.EnumVariant {
    None(LLVM.LLVMCodeGenLevelNone),
    Less(LLVM.LLVMCodeGenLevelLess),
    Default(LLVM.LLVMCodeGenLevelDefault),
    Aggressive(LLVM.LLVMCodeGenLevelAggressive);
    public companion object : Enumeration<CodeGenOptimizationLevel>(values())
}

public enum class CodeModel(public override val value: Int) : Enumeration.EnumVariant {
    Default(LLVM.LLVMCodeModelDefault),
    JITDefault(LLVM.LLVMCodeModelJITDefault),
    Tiny(LLVM.LLVMCodeModelTiny),
    Small(LLVM.LLVMCodeModelSmall),
    Kernel(LLVM.LLVMCodeModelKernel),
    Medium(LLVM.LLVMCodeModelMedium),
    Large(LLVM.LLVMCodeModelLarge);
    public companion object : Enumeration<CodeModel>(values())
}

public enum class RelocMode(public override val value: Int) : Enumeration.EnumVariant {
    Default(LLVM.LLVMRelocDefault),
    Static(LLVM.LLVMRelocStatic),
    PositionIndependentCode(LLVM.LLVMRelocPIC),
    DynamicNoPositionIndependentCode(LLVM.LLVMRelocDynamicNoPic),
    ROPI(LLVM.LLVMRelocROPI),
    RWPI(LLVM.LLVMRelocRWPI),
    ROPIRWPI(LLVM.LLVMRelocROPI_RWPI);
    public companion object : Enumeration<RelocMode>(values())
}

public class TargetMachine public constructor(ptr: LLVMTargetMachineRef) : Owner<LLVMTargetMachineRef> {
    public override val ref: LLVMTargetMachineRef = ptr

    public fun getTarget(): Target {
        val target = LLVM.LLVMGetTargetMachineTarget(ref)

        return Target(target)
    }

    public fun getTargetTriple(): String {
        val ptr = LLVM.LLVMGetTargetMachineTriple(ref)
        val copy = ptr.string
        ptr.deallocate()
        return copy
    }

    public fun getProcessorName(): String {
        val ptr = LLVM.LLVMGetTargetMachineCPU(ref)
        val copy = ptr.string
        ptr.deallocate()
        return copy
    }

    public fun getProcessorFeatures(): String {
        val ptr = LLVM.LLVMGetTargetMachineFeatureString(ref)
        val copy = ptr.string
        ptr.deallocate()
        return copy
    }

    public fun getTargetLayout(): TargetData {
        val layout = LLVM.LLVMCreateTargetDataLayout(ref)

        return TargetData(layout)
    }

    public fun setAsmVerbosity(verbose: Boolean) {
        LLVM.LLVMSetTargetMachineAsmVerbosity(ref, verbose.toInt())
    }

    public companion object {
        /**
         * Create a target machine for the given [target]
         *
         * If you do not know which values to pass here you can use the following for the machine the system is
         * currently running on.
         *
         * @param triple            target triple ([LLVMSystem.getHostTargetTriple])
         * @param cpu               target cpu ([LLVMSystem.getHostProcessorName])
         * @param features          target cpu features ([LLVMSystem.getHostProcessorFeatures])
         * @param optimizationLevel optimization level to use for code generation
         * @param codeModel
         * @param relocMode
         */
        @JvmStatic
        public fun of(
            target: Target,
            triple: String,
            cpu: String,
            features: String,
            optimizationLevel: CodeGenOptimizationLevel,
            codeModel: CodeModel,
            relocMode: RelocMode
        ): TargetMachine {
            val machine = LLVM.LLVMCreateTargetMachine(
                target.ref, triple, cpu, features,
                optimizationLevel.value, codeModel.value, relocMode.value
            )

            return TargetMachine(machine)
        }
    }

    public override fun deallocate() {
        LLVM.LLVMDisposeTargetMachine(ref)
    }
}

package org.llvm4j.llvm4j

import org.bytedeco.llvm.LLVM.LLVMTargetDataRef
import org.bytedeco.llvm.LLVM.LLVMTargetLibraryInfoRef
import org.bytedeco.llvm.LLVM.LLVMTargetMachineRef
import org.bytedeco.llvm.LLVM.LLVMTargetRef
import org.llvm4j.llvm4j.util.Enumeration
import org.llvm4j.llvm4j.util.Owner

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
}

public class TargetData public constructor(ptr: LLVMTargetDataRef) : Owner<LLVMTargetDataRef> {
    public override val ref: LLVMTargetDataRef = ptr
}

public class TargetLibraryInfo public constructor(ptr: LLVMTargetLibraryInfoRef) : Owner<LLVMTargetLibraryInfoRef> {
    public override val ref: LLVMTargetLibraryInfoRef = ptr
}

public class TargetMachine public constructor(ptr: LLVMTargetMachineRef) : Owner<LLVMTargetMachineRef> {
    public override val ref: LLVMTargetMachineRef = ptr
}

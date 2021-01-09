package org.llvm4j.llvm4j

import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.llvm4j.llvm4j.util.Owner

public sealed class Instruction constructor(ptr: LLVMValueRef) : User {
    public override val ref: LLVMValueRef = ptr

    public interface Atomic : Owner<LLVMValueRef>
    public interface CallBase : Owner<LLVMValueRef>
    public interface FuncletPad : Owner<LLVMValueRef>
    public interface MemoryAccessor : Owner<LLVMValueRef>
    public interface Terminator : Owner<LLVMValueRef>
}

public class AllocaInstruction
public class AtomicCmpXchgInstruction
public class AtomicRMWInstruction
public class BrInstruction
public class CallBrInstruction
public class CatchPadInstruction
public class CatchRetInstruction
public class CatchSwitchInstruction
public class CleanupPadInstruction
public class CleanupRetInstruction
public class FenceInstruction
public class IndirectBrInstruction
public class InvokeInstruction
public class LandingPadInstruction
public class LoadInstruction
public class PhiInstruction
public class ResumeInstruction
public class RetInstruction
public class SelectInstruction
public class StoreInstruction
public class SwitchInstruction
public class UnreachableInstruction
public class VAArgInstruction

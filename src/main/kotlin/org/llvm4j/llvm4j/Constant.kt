package org.llvm4j.llvm4j

import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.llvm4j.llvm4j.util.CorrespondsTo
import org.llvm4j.llvm4j.util.InternalApi
import org.llvm4j.llvm4j.util.Owner

public sealed class Constant constructor(ptr: LLVMValueRef) : Owner<LLVMValueRef>, User {
    public override val ref: LLVMValueRef = ptr

    @InternalApi
    public interface Aggregate : Owner<LLVMValueRef>

    @CorrespondsTo("llvm::GlobalValue")
    public interface GlobalValue : Owner<LLVMValueRef>

    @InternalApi
    public interface ConstantData : Owner<LLVMValueRef>
}

public class ConstantArray public constructor(ptr: LLVMValueRef) : Constant(ptr), Constant.Aggregate
public class ConstantVector public constructor(ptr: LLVMValueRef) : Constant(ptr), Constant.Aggregate
public class ConstantStruct public constructor(ptr: LLVMValueRef) : Constant(ptr), Constant.Aggregate

public class ConstantInt public constructor(ptr: LLVMValueRef) : Constant(ptr), Constant.ConstantData
public class ConstantFloat public constructor(ptr: LLVMValueRef) : Constant(ptr), Constant.ConstantData
public class ConstantAggregateZero public constructor(ptr: LLVMValueRef) : Constant(ptr), Constant.ConstantData
public class ConstantPointerNull public constructor(ptr: LLVMValueRef) : Constant(ptr), Constant.ConstantData
public class ConstantTokenNone public constructor(ptr: LLVMValueRef) : Constant(ptr), Constant.ConstantData
public class UndefValue public constructor(ptr: LLVMValueRef) : Constant(ptr), Constant.ConstantData

public class BlockAddress public constructor(ptr: LLVMValueRef) : Constant(ptr)

public class Function public constructor(ptr: LLVMValueRef) : Constant(ptr), Constant.GlobalValue, Value.HasDebugLocation
public class GlobalIndirectFunction public constructor(ptr: LLVMValueRef) : Constant(ptr), Constant.GlobalValue
public class GlobalAlias public constructor(ptr: LLVMValueRef) : Constant(ptr), Constant.GlobalValue
public class GlobalVariable public constructor(ptr: LLVMValueRef) : Constant(ptr), Constant.GlobalValue, Value.HasDebugLocation

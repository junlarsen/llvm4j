package org.llvm4j.llvm4j

import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.llvm4j.llvm4j.util.Owner

public sealed class Type constructor(ptr: LLVMTypeRef) : Owner<LLVMTypeRef> {
    public override val ref: LLVMTypeRef = ptr

    public interface Composite : Owner<LLVMTypeRef>
    public interface Sequential : Composite
}

public class IntType public constructor(ptr: LLVMTypeRef) : Type(ptr)
public class FloatType public constructor(ptr: LLVMTypeRef) : Type(ptr)
public class VoidType public constructor(ptr: LLVMTypeRef) : Type(ptr)
public class ArrayType public constructor(ptr: LLVMTypeRef) : Type(ptr), Type.Sequential
public class VectorType public constructor(ptr: LLVMTypeRef) : Type(ptr), Type.Sequential
public class PointerType public constructor(ptr: LLVMTypeRef) : Type(ptr), Type.Sequential
public class StructType public constructor(ptr: LLVMTypeRef) : Type(ptr), Type.Composite
public class NamedStructType public constructor(ptr: LLVMTypeRef) : Type(ptr)
public class FunctionType public constructor(ptr: LLVMTypeRef) : Type(ptr)
public class LabelType public constructor(ptr: LLVMTypeRef) : Type(ptr)
public class MetadataType public constructor(ptr: LLVMTypeRef) : Type(ptr)
public class TokenType public constructor(ptr: LLVMTypeRef) : Type(ptr)
public class X86MMXType public constructor(ptr: LLVMTypeRef) : Type(ptr)

package org.llvm4j.llvm4j

import org.bytedeco.llvm.LLVM.LLVMAttributeRef
import org.bytedeco.llvm.global.LLVM
import org.llvm4j.llvm4j.util.Enumeration
import org.llvm4j.llvm4j.util.Owner

public sealed class Attribute constructor(ptr: LLVMAttributeRef) : Owner<LLVMAttributeRef> {
    public override val ref: LLVMAttributeRef = ptr
}

public class EnumAttribute public constructor(ptr: LLVMAttributeRef) : Attribute(ptr)
public class StringAttribute public constructor(ptr: LLVMAttributeRef) : Attribute(ptr)

public sealed class AttributeIndex(public override val value: Int) : Enumeration.EnumVariant {
    public object Return : AttributeIndex(LLVM.LLVMAttributeReturnIndex)
    public object Function : AttributeIndex(LLVM.LLVMAttributeFunctionIndex)
    public class Unknown(value: Int) : AttributeIndex(value)

    public companion object : Enumeration.WithFallback<AttributeIndex>({ Unknown(it) }) {
        public override val entries: Array<out AttributeIndex> by lazy { arrayOf(Return, Function) }
    }
}

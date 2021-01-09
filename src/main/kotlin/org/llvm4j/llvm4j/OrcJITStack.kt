package org.llvm4j.llvm4j

import org.bytedeco.javacpp.BytePointer
import org.bytedeco.javacpp.Pointer
import org.bytedeco.llvm.LLVM.LLVMErrorRef
import org.bytedeco.llvm.LLVM.LLVMErrorTypeId
import org.bytedeco.llvm.LLVM.LLVMJITEventListenerRef
import org.bytedeco.llvm.LLVM.LLVMOrcJITStackRef
import org.bytedeco.llvm.LLVM.LLVMOrcLazyCompileCallbackFn
import org.bytedeco.llvm.LLVM.LLVMOrcSymbolResolverFn
import org.llvm4j.llvm4j.util.Callback
import org.llvm4j.llvm4j.util.None
import org.llvm4j.llvm4j.util.Option
import org.llvm4j.llvm4j.util.Owner
import org.llvm4j.llvm4j.util.Some

public class OrcJITStack public constructor(ptr: LLVMOrcJITStackRef) : Owner<LLVMOrcJITStackRef> {
    public override val ref: LLVMOrcJITStackRef = ptr

    public class Error public constructor(ptr: LLVMErrorRef) : Owner<LLVMErrorRef> {
        public override val ref: LLVMErrorRef = ptr
    }

    public class ErrorTypeId public constructor(ptr: LLVMErrorTypeId) : Owner<LLVMErrorTypeId> {
        public override val ref: LLVMErrorTypeId = ptr
    }

    public class JITEventListener public constructor(ptr: LLVMJITEventListenerRef) : Owner<LLVMJITEventListenerRef> {
        public override val ref: LLVMJITEventListenerRef = ptr
    }

    public class LazyCompileCallback(public override val closure: (Payload) -> Long) :
        LLVMOrcLazyCompileCallbackFn(),
        Callback<Long, LazyCompileCallback.Payload> {
        public override fun call(p0: LLVMOrcJITStackRef, p1: Pointer?): Long {
            val jitStack = OrcJITStack(p0)
            val payload = p1?.let { Some(it) } ?: None
            val data = Payload(jitStack, payload)

            return closure.invoke(data)
        }

        public data class Payload(
            public val jitStack: OrcJITStack,
            public val payload: Option<Pointer>
        )
    }

    public class SymbolResolver(public override val closure: (Payload) -> Long) :
        LLVMOrcSymbolResolverFn(),
        Callback<Long, SymbolResolver.Payload> {
        public override fun call(p0: BytePointer, p1: Pointer?): Long {
            val name = p0.string
            val payload = p1?.let { Some(it) } ?: None
            val data = Payload(name, payload)

            p0.deallocate()

            return closure.invoke(data)
        }

        public data class Payload(
            public val name: String,
            public val payload: Option<Pointer>
        )
    }
}

package org.llvm4j.llvm4j

import org.bytedeco.javacpp.Pointer
import org.bytedeco.llvm.LLVM.LLVMContextRef
import org.bytedeco.llvm.LLVM.LLVMDiagnosticHandler
import org.bytedeco.llvm.LLVM.LLVMDiagnosticInfoRef
import org.bytedeco.llvm.LLVM.LLVMYieldCallback
import org.bytedeco.llvm.global.LLVM
import org.llvm4j.llvm4j.util.Callback
import org.llvm4j.llvm4j.util.CorrespondsTo
import org.llvm4j.llvm4j.util.None
import org.llvm4j.llvm4j.util.Option
import org.llvm4j.llvm4j.util.Owner
import org.llvm4j.llvm4j.util.Result
import org.llvm4j.llvm4j.util.Some
import org.llvm4j.llvm4j.util.tryWith

/**
 * A context keeping the state a LLVM system requires.
 *
 * The context's data is local to a single thread and the context is not thread-safe. If compilation using multiple
 * threads is required, create a [Context] for each thread.
 *
 * The context is also a "super object" of sorts. Most state you can build in the LLVM system is built from this
 * object and these constructors are available on the [Context] class as methods.
 *
 * TODO: LLVM 12.x - getScalableVectorType
 */
@CorrespondsTo("llvm::LLVMContext")
public class Context public constructor(
    ptr: LLVMContextRef = LLVM.LLVMContextCreate()
) : Owner<LLVMContextRef> {
    public override val ref: LLVMContextRef = ptr

    public fun getArrayType(of: Type, size: Int): Result<ArrayType> = tryWith {
        assert(size > 0) { "Element count must be greater than 0" }
        assert(of.isValidArrayElementType()) { "Invalid type for array element" }

        val arrayTy = LLVM.LLVMArrayType(of.ref, size)
        ArrayType(arrayTy)
    }

    public fun getVectorType(of: Type, size: Int): Result<VectorType> = tryWith {
        assert(size > 0) { "Element count must be greater than 0" }
        assert(of.isValidVectorElementType()) { "Invalid type for vector element" }

        val vecTy = LLVM.LLVMVectorType(of.ref, size)
        VectorType(vecTy)
    }

    public fun getPointerType(of: Type, addressSpace: Option<Int> = None): Result<PointerType> = tryWith {
        if (addressSpace.isDefined()) {
            assert(addressSpace.get() >= 0) { "Address space must be positive or null" }
        }
        assert(of.isValidPointerElementType()) { "Invalid type for pointer element" }

        val address = when (addressSpace) {
            is None -> 0
            is Some -> addressSpace.get()
        }
        val ptrTy = LLVM.LLVMPointerType(of.ref, address)
        PointerType(ptrTy)
    }

    public class DiagnosticHandler(public override val closure: (Payload) -> Unit) :
        LLVMDiagnosticHandler(),
        Callback<Unit, DiagnosticHandler.Payload> {
        public override fun call(p0: LLVMDiagnosticInfoRef, p1: Pointer?) {
            val info = DiagnosticInfo(p0)
            val payload = p1?.let { Some(it) } ?: None
            val data = Payload(info, payload)

            return closure.invoke(data)
        }

        public data class Payload(
            public val info: DiagnosticInfo,
            public val payload: Option<Pointer>
        )
    }

    public class YieldCallback(public override val closure: (Payload) -> Unit) :
        LLVMYieldCallback(),
        Callback<Unit, YieldCallback.Payload> {
        public override fun call(p0: LLVMContextRef, p1: Pointer?) {
            val context = Context(p0)
            val payload = p1?.let { Some(it) } ?: None
            val data = Payload(context, payload)

            return closure.invoke(data)
        }

        public data class Payload(
            public val context: Context,
            public val payload: Option<Pointer>
        )
    }
}

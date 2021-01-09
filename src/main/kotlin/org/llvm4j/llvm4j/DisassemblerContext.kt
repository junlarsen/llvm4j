package org.llvm4j.llvm4j

import org.bytedeco.javacpp.BytePointer
import org.bytedeco.javacpp.LongPointer
import org.bytedeco.javacpp.Pointer
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMDisasmContextRef
import org.bytedeco.llvm.LLVM.LLVMOpInfoCallback
import org.bytedeco.llvm.LLVM.LLVMSymbolLookupCallback
import org.llvm4j.llvm4j.util.Callback
import org.llvm4j.llvm4j.util.Owner

public class DisassemblerContext public constructor(ptr: LLVMDisasmContextRef) : Owner<LLVMDisasmContextRef> {
    public override val ref: LLVMDisasmContextRef = ptr

    public class OpInfoCallback(public override val closure: (Payload) -> Int) : LLVMOpInfoCallback(),
        Callback<Int, OpInfoCallback.Payload> {
        public override fun call(p0: Pointer, p1: Long, p2: Long, p3: Long, p4: Int, p5: Pointer): Int {
            val data = Payload(p0, p1, p2, p3, p4, p5)

            return closure.invoke(data)
        }

        public data class Payload(
            public val disassemblerInfo: Pointer,
            public val pc: Long,
            public val offset: Long,
            public val size: Long,
            public val tagType: Int,
            public val tagBuf: Pointer
        )
    }

    public class SymbolLookupCallback(public override val closure: (Payload) -> BytePointer) : LLVMSymbolLookupCallback(),
        Callback<BytePointer, SymbolLookupCallback.Payload> {
        public override fun call(
            p0: Pointer,
            p1: Long,
            p2: LongPointer,
            p3: Long,
            p4: PointerPointer<*>
        ): BytePointer {
            val referenceType = p2.get(0)
            val referenceName = p4.getString(0)
            val data = Payload(p0, p1, referenceType, p3, referenceName)

            p4.deallocate()

            return closure.invoke(data)
        }

        public data class Payload(
            public val disassemblerInfo: Pointer,
            public val referenceValue: Long,
            // TODO: is this the correct type?
            public val referenceType: Long,
            public val referencePc: Long,
            // TODO: is this the correct type?
            public val referenceName: String
        )
    }
}
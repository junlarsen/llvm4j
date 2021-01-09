package org.llvm4j.llvm4j

import org.bytedeco.llvm.LLVM.LTOObjectBuffer
import org.bytedeco.llvm.LLVM.thinlto_code_gen_t
import org.llvm4j.llvm4j.util.Owner

public typealias ThinLTOCodeGenRef = thinlto_code_gen_t

public class ThinLTOCodeGen public constructor(ptr: ThinLTOCodeGenRef) : Owner<ThinLTOCodeGenRef> {
    public override val ref: ThinLTOCodeGenRef = ptr
}

public class ThinLTOObjectBuffer public constructor(ptr: LTOObjectBuffer) : Owner<LTOObjectBuffer> {
    public override val ref: LTOObjectBuffer = ptr
}

package org.llvm4j.llvm4j

import org.bytedeco.llvm.LLVM.Pass
import org.llvm4j.llvm4j.util.Owner

public class PollyPass public constructor(ptr: Pass) : Owner<Pass> {
    public override val ref: Pass = ptr
}

package dev.supergrecko.kllvm.ir.values

import dev.supergrecko.kllvm.internal.contracts.ContainsReference
import dev.supergrecko.kllvm.internal.contracts.Unreachable
import dev.supergrecko.kllvm.internal.util.fromLLVMBool
import dev.supergrecko.kllvm.ir.Type
import dev.supergrecko.kllvm.ir.Value
import dev.supergrecko.kllvm.ir.instructions.Opcode
import dev.supergrecko.kllvm.ir.types.PointerType
import dev.supergrecko.kllvm.ir.values.constants.ConstantPointer
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

/**
 * Type to which acts like a supertype for all constant LLVM values
 */
public interface ConstantValue : ContainsReference<LLVMValueRef> {
    //region Core::Values::Constants::ConstantExpressions
    /**
     * Get the opcode for a constant value
     *
     * TODO: Move this, presumably to instructions?
     *
     * @see LLVM.LLVMGetConstOpcode
     */
    public fun getOpcode(): Opcode {
        val isConst = LLVM.LLVMIsConstant(ref).fromLLVMBool()
        require(isConst)

        val int = LLVM.LLVMGetConstOpcode(ref)

        return Opcode.values()
            .firstOrNull { it.value == int }
            ?: throw Unreachable()
    }

    /**
     * Perform a cast without changing any bits
     *
     * This requires both this and the destination type to be non-aggregate,
     * first-class types.
     *
     * @see LLVM.LLVMConstBitCast
     *
     * TODO: Determine that this is not an aggregate type
     */
    public fun bitcast(type: Type): Value {
        val ref = LLVM.LLVMConstBitCast(ref, type.ref)

        return Value(ref)
    }

    /**
     * Attempt to convert using extension, default to bitcast
     *
     * This is an LLVM-C/C++ specific API. It is not a part of the
     * instruction set.
     *
     * @see LLVM.LLVMConstSExtOrBitCast
     * @see LLVM.LLVMConstZExtOrBitCast
     *
     * TODO: Find out which types are compatible here, int?
     */
    public fun extOrBitcast(type: Type, signExtend: Boolean): Value {
        val ref = if(signExtend) {
            LLVM.LLVMConstSExtOrBitCast(ref, type.ref)
        } else {
            LLVM.LLVMConstZExtOrBitCast(ref, type.ref)
        }

        return Value(ref)
    }

    /**
     * Attempt to convert using zero extension, default to bitcast
     *
     * @see LLVM.LLVMConstZExtOrBitCast
     */
    public fun zextOrBitcast(type: Type): Value {
        return extOrBitcast(type, false)
    }

    /**
     * Attempt to convert using sign extension, default to bitcast
     *
     * @see LLVM.LLVMConstSExtOrBitCast
     */
    public fun sextOrBitcast(type: Type): Value {
        return extOrBitcast(type, true)
    }

    /**
     * Attempt to truncate, default to bitcast
     *
     * @see LLVM.LLVMConstTruncOrBitCast
     *
     * TODO: Find out which types are compatible here, int?
     */
    public fun truncOrBitcast(type: Type): Value {
        val ref = LLVM.LLVMConstTruncOrBitCast(ref, type.ref)

        return Value(ref)
    }
    //endregion Core::Values::Constants::ConstantExpressions
}
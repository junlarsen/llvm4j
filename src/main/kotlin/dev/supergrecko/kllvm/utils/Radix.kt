package dev.supergrecko.kllvm.utils

import dev.supergrecko.kllvm.contracts.OrderedEnum

public enum class Radix(public override val value: Int) : OrderedEnum<Int> {
    Binary(2),
    Octal(8),
    Decimal(10),
    Hex(16),
    Alphanumeric(36)
}

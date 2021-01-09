package org.llvm4j.llvm4j.util

/**
 * Common trait for any enum with an out-of-order ordinal (like most of LLVMs C++ enums)
 *
 * This allows conversion between the C++ integer and the Kotlin enum.
 */
public abstract class Enumeration<E : Enumeration.EnumVariant>(entries: Array<E>) {
    private val associated: Map<Int, E> = entries.associateBy { it.value }
    private val reverse: Map<E, Int> by lazy { associated.entries.associate { Pair(it.value, it.key) } }
    /**
     * Turn an integer value (from the C++ enum) into the Kotlin equivalent
     *
     * @return [Some] if enum variant was found, otherwise [None]
     */
    public fun from(id: Int): Option<E> = associated[id]?.let { Some(it) } ?: None

    /**
     * Turn an enum variant into the C++ integer form
     *
     * @return [Some] if enum variant was found, otherwise [None]
     */
    public fun into(id: E): Option<Int> = reverse[id]?.let { Some(it) } ?: None

    /**
     * Trait allowing an enum to have a set of defined variants or a fallback variant which may catch all cases which
     * otherwise would result in [None]
     *
     * @property catch lambda responsible for producing the wildcard [E] given [Int]
     * @property entries all valid entries for this enum
     */
    public abstract class Extendable<E : EnumVariant>(
        private val catch: (Int) -> E,
        private vararg val entries: E
    ) {
        /**
         * Turn an integer value (from the C++ enum) into the Kotlin equivalent
         *
         * @return the enum variant associated with the integer
         */
        public fun from(id: Int): E = entries.firstOrNull { it.value == id }
            ?: catch(id)

        /**
         * Turn an enum variant into the C++ integer form
         *
         * @return the integer value of the enum variant
         */
        public fun into(id: E): Int = entries.first { it == id }.value
    }

    /**
     * Anything which shall represent an extendable enum must have a value for the catch-all kind.
     */
    public interface EnumVariant {
        public val value: Int
    }
}

# Structures

Structures are top-level declarations which declare a new data type. A struct contains a set of members each with their own type.

Structures may only be declared at the top-level scope, and they cannot be nested.

The grammar for a struct (struct-decl) is as following:

## Structure Members

A struct may define an arbitrary amount of members. Members are declared inside the struct body. Member declarations are comma separated.

**Example struct declaring three members**

```
struct Triple {
    x: s32,
    y: s32,
    z: s32
}
```

## Structure Attributes

A struct may have certain properties/attributes such as declaring itself as external. These attributes come after the `struct` keyword.

**Example struct declared as external**

```
struct(ffi) Vec3 {
    x: s32,
    y: s32,
    z: s32
}
```

In the case of structures, this means that they are imported via a foreign function interface.
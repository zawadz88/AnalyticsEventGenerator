package dev.zawadzki.analyticseventgenerator.plugin.internal

import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.DOUBLE
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.LONG
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeName

internal fun Attribute.getPoetTypeName(): TypeName = when (this.type) {
    Type.Primitive.STRING -> STRING
    Type.Primitive.NULLABLE_STRING -> STRING.copy(nullable = true)
    Type.Primitive.INTEGER -> INT
    Type.Primitive.NULLABLE_INTEGER -> INT.copy(nullable = true)
    Type.Primitive.DOUBLE -> DOUBLE
    Type.Primitive.NULLABLE_DOUBLE -> DOUBLE.copy(nullable = true)
    Type.Primitive.BOOLEAN -> BOOLEAN
    Type.Primitive.NULLABLE_BOOLEAN -> BOOLEAN.copy(nullable = true)
    Type.Primitive.LONG -> LONG
    Type.Primitive.NULLABLE_LONG -> LONG.copy(nullable = true)
    is Type.Enum -> ClassName("", this.name.toClassName())
}

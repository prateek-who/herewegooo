package com.example.herewegooo


import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import android.graphics.Color as AndroidColor

object ColorSerializer : KSerializer<Color> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(
            "Color", PrimitiveKind.STRING
        )

    override fun serialize(encoder: Encoder, value: Color) {
        // Convert the Compose Color to an ARGB hex string.
        val hex = String.format("#%08X", value.toArgb())
        encoder.encodeString(hex)
    }

    override fun deserialize(decoder: Decoder): Color {
        val hex = decoder.decodeString()
        return Color(AndroidColor.parseColor("#$hex"))
    }
}
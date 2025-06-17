package com.example.blockchainproject.ui.compose.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

val SamsungColorScheme = darkColorScheme(
    primary = Color(0xFF38BDF8),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF0EA5E9),
    background = Color(0xFF000000),
    surface = Color(0xFF1F2937),
    onBackground = Color(0xFFF3F4F6),
    onSurface = Color(0xFFE5E7EB),
    error = Color(0xFFF87171),
    onError = Color.Black
)
@Composable
fun GlassRedButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    loading: Boolean = false,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)

    Box(
        modifier = modifier
            .graphicsLayer {
                alpha = if (enabled) 1f else 0.5f
            }
            .clip(shape)
            .background(Color.Transparent)
            .border(
                width = 2.dp,
                color = SamsungColorScheme.primary,
                shape = shape
            )
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(shape)
                .blur(10.dp)
                .background(SamsungColorScheme.onSurface.copy(alpha = 0.15f))
                .clickable(
                    enabled = enabled,
                    onClick = onClick,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                )
            )

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(vertical = 12.dp, horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = SamsungColorScheme.primaryContainer,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = text,
                    color = SamsungColorScheme.primary,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
fun GlassOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean,
    isValid: Boolean,
    supportingText: String?,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)

    val focusedBorderColor = when {
        isError -> SamsungColorScheme.error
        isValid -> Color(0xFF4CAF50)
        value.isEmpty() -> SamsungColorScheme.primary
        else -> MaterialTheme.colorScheme.primary
    }

    val unfocusedBorderColor = when {
        isError -> SamsungColorScheme.error
        isValid -> Color(0xFF81C784)
        value.isEmpty() -> SamsungColorScheme.primary
        else -> MaterialTheme.colorScheme.outline
    }
    Box(
        modifier = Modifier
            .clip(shape)
            .blur(10.dp)
            .background(Color.White.copy(alpha = 0.15f))
    )

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = isError,
        supportingText = {
            if (isError && supportingText != null) {
                Text(supportingText)
            }
        },
        textStyle = TextStyle(color = SamsungColorScheme.primary),
        modifier = Modifier
            .fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = focusedBorderColor,
            unfocusedBorderColor = unfocusedBorderColor,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            errorBorderColor = SamsungColorScheme.error,
            errorContainerColor = Color.Transparent,
            focusedLabelColor = SamsungColorScheme.primary,
            unfocusedLabelColor = SamsungColorScheme.primary,
        ),
        shape = shape
    )
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .background(Color.Transparent)
            .border(
                width = 1.dp,
                color = SamsungColorScheme.primary,
                shape = shape
            )
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(shape)
                .blur(12.dp)
                .background(SamsungColorScheme.onBackground.copy(alpha = 0.07f))
        )
        Box(modifier = Modifier.padding(16.dp))
        CompositionLocalProvider(
            LocalContentColor provides SamsungColorScheme.primary
        ){
            content()
        }
    }
}

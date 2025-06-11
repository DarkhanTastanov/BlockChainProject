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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp


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
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFF4444),
                        Color(0xFFFF8888),
                        Color(0xFFFF4444)
                    )
                ),
                shape = shape
            )
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(shape)
                .blur(10.dp)
                .background(Color.Red.copy(alpha = 0.15f))
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
                    color = Color(0xFFFF4444),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = text,
                    color = Color(0xFFFF4444),
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
        isError -> Color(0xFFFF4444)
        isValid -> Color(0xFF4CAF50)
        value.isEmpty() -> Color.White
        else -> MaterialTheme.colorScheme.primary
    }

    val unfocusedBorderColor = when {
        isError -> Color(0xFFFF8888)
        isValid -> Color(0xFF81C784)
        value.isEmpty() -> Color.White
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
        modifier = Modifier
            .fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = focusedBorderColor,
            unfocusedBorderColor = unfocusedBorderColor,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            errorBorderColor = Color(0xFFFF4444),
            errorContainerColor = Color.Transparent,
            focusedLabelColor = Color.Black,
            unfocusedLabelColor = Color.Black,
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
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFF4444),
                        Color(0xFFFF8888)
                    )
                ),
                shape = shape
            )
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(shape)
                .blur(12.dp)
                .background(Color.Red.copy(alpha = 0.07f))
        )
        Box(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

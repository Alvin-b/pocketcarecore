package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CareData
import com.example.data.model.Doctor
import com.example.ui.theme.TeleAccent
import com.example.ui.theme.TeleBorder
import com.example.ui.theme.TeleMuted
import com.example.ui.theme.TelePrimary
import com.example.ui.theme.TelePrimarySoft

enum class BtnVariant {
    PRIMARY,
    SOFT,
    OUTLINE,
    ACCENT,
    GHOST
}

@Composable
fun CareButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: BtnVariant = BtnVariant.PRIMARY,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val shape = RoundedCornerShape(16.dp)

    when (variant) {
        BtnVariant.PRIMARY -> {
            Button(
                onClick = onClick,
                enabled = enabled,
                shape = shape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = TelePrimary,
                    contentColor = Color.White
                ),
                modifier = modifier.height(48.dp)
            ) {
                if (leadingIcon != null) {
                    leadingIcon()
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(text = text, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
        BtnVariant.ACCENT -> {
            Button(
                onClick = onClick,
                enabled = enabled,
                shape = shape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = TeleAccent,
                    contentColor = Color.White
                ),
                modifier = modifier.height(48.dp)
            ) {
                if (leadingIcon != null) {
                    leadingIcon()
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(text = text, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
        BtnVariant.SOFT -> {
            Button(
                onClick = onClick,
                enabled = enabled,
                shape = shape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = TelePrimarySoft,
                    contentColor = TelePrimary
                ),
                modifier = modifier.height(48.dp)
            ) {
                if (leadingIcon != null) {
                    leadingIcon()
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(text = text, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TelePrimary)
            }
        }
        BtnVariant.OUTLINE -> {
            OutlinedButton(
                onClick = onClick,
                enabled = enabled,
                shape = shape,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, TeleBorder),
                modifier = modifier.height(48.dp)
            ) {
                if (leadingIcon != null) {
                    leadingIcon()
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(text = text, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
        BtnVariant.GHOST -> {
            Box(
                modifier = modifier
                    .clickable(enabled = enabled, onClick = onClick)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = text, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TelePrimary)
            }
        }
    }
}

@Composable
fun CareHeader(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    right: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, TeleBorder.copy(alpha = 0.5f), CircleShape)
                .testTag("care_header_back")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier.weight(1f),
            maxLines = 1
        )

        if (right != null) {
            right()
        }
    }
}

@Composable
fun CareAvatar(
    name: String,
    photoRes: Int? = null,
    size: Dp = 48.dp,
    modifier: Modifier = Modifier
) {
    if (photoRes != null) {
        Image(
            painter = painterResource(id = photoRes),
            contentDescription = name,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(size)
                .clip(RoundedCornerShape(14.dp))
        )
    } else {
        Box(
            modifier = modifier
                .size(size)
                .clip(RoundedCornerShape(14.dp))
                .background(TelePrimarySoft),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = CareData.initials(name),
                color = TelePrimary,
                fontWeight = FontWeight.Bold,
                fontSize = (size.value / 3).sp
            )
        }
    }
}

@Composable
fun CareStars(
    value: Float,
    modifier: Modifier = Modifier,
    size: Dp = 14.dp,
    onChange: ((Int) -> Unit)? = null
) {
    Row(modifier = modifier) {
        for (i in 1..5) {
            val filled = i <= Math.round(value)
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "$i stars",
                tint = if (filled) TeleAccent else TeleBorder,
                modifier = Modifier
                    .size(size)
                    .then(if (onChange != null) Modifier.clickable { onChange(i) } else Modifier)
            )
        }
    }
}

@Composable
fun CareCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = TeleBorder.copy(alpha = 0.5f),
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            content()
        }
    }
}

@Composable
fun CareField(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        content()
    }
}

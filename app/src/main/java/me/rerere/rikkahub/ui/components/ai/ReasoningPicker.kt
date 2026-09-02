package me.rerere.rikkahub.ui.components.ai

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.rerere.ai.core.ReasoningLevel
import me.rerere.hugeicons.HugeIcons
import me.rerere.hugeicons.stroke.ArrowRight01
import me.rerere.hugeicons.stroke.Idea
import me.rerere.hugeicons.stroke.Idea01
import me.rerere.rikkahub.R
import me.rerere.rikkahub.ui.components.ui.ToggleSurface
import me.rerere.rikkahub.ui.components.ui.icons.ReasoningHigh
import me.rerere.rikkahub.ui.components.ui.icons.ReasoningLow
import me.rerere.rikkahub.ui.components.ui.icons.ReasoningMedium
import kotlin.math.roundToInt

private val levels = ReasoningLevel.entries
private val levelCount = levels.size

@Composable
fun ReasoningButton(
    modifier: Modifier = Modifier,
    onlyIcon: Boolean = false,
    reasoningLevel: ReasoningLevel,
    onUpdateReasoningLevel: (ReasoningLevel) -> Unit,
) {
    var showPicker by remember { mutableStateOf(false) }

    if (showPicker) {
        ReasoningPicker(
            reasoningLevel = reasoningLevel,
            onDismissRequest = { showPicker = false },
            onUpdateReasoningLevel = onUpdateReasoningLevel
        )
    }

    ToggleSurface(
        checked = reasoningLevel.isEnabled,
        onClick = { showPicker = true },
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier.size(18.dp),
                contentAlignment = Alignment.Center
            ) {
                ReasoningIcon(reasoningLevel)
            }
            if (!onlyIcon) {
                Text(stringResource(R.string.setting_provider_page_reasoning))
            } else {
                Text(
                    text = reasoningLevel.label(),
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1
                )
            }
            // tombol kecil ⌄ seperti Grok pill
            Icon(
                imageVector = HugeIcons.ArrowRight01,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ReasoningPicker(
    reasoningLevel: ReasoningLevel,
    onDismissRequest: () -> Unit = {},
    onUpdateReasoningLevel: (ReasoningLevel) -> Unit,
) {
    // PopUp kecil seperti Grok PopUp Menu.jpg - 5 visible scroll, pill 1 lengkap cek instruksi
    androidx.compose.material3.DropdownMenu(
        expanded = true,
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        androidx.compose.foundation.lazy.LazyColumn(
            modifier = Modifier.heightIn(max = 280.dp)
        ) {
            items(levels.size) { idx ->
                val level = levels[idx]
                val selected = level == reasoningLevel
                androidx.compose.material3.DropdownMenuItem(
                    text = {
                        androidx.compose.foundation.layout.Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                                ReasoningIcon(level)
                            }
                            androidx.compose.foundation.layout.Column(modifier = Modifier.weight(1f)) {
                                Text(text = level.label(), style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    text = when (level) {
                                        ReasoningLevel.OFF -> stringResource(R.string.reasoning_off_desc)
                                        ReasoningLevel.AUTO -> stringResource(R.string.reasoning_auto_desc)
                                        ReasoningLevel.LOW -> stringResource(R.string.reasoning_light_desc)
                                        ReasoningLevel.MEDIUM -> stringResource(R.string.reasoning_medium_desc)
                                        ReasoningLevel.HIGH -> stringResource(R.string.reasoning_heavy_desc)
                                        ReasoningLevel.XHIGH -> "Extra high reasoning"
                                        ReasoningLevel.MAX -> "Maximum reasoning"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                            }
                            if (selected) Icon(HugeIcons.Tick01, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        }
                    },
                    onClick = {
                        onUpdateReasoningLevel(level)
                        onDismissRequest()
                    },
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun ReasoningIcon(level: ReasoningLevel) {
    when (level) {
        ReasoningLevel.OFF -> Icon(HugeIcons.Idea, null)
        ReasoningLevel.AUTO -> Icon(HugeIcons.Idea01, null)
        ReasoningLevel.LOW -> Icon(ReasoningLow, null)
        ReasoningLevel.MEDIUM -> Icon(ReasoningMedium, null)
        ReasoningLevel.HIGH -> Icon(ReasoningHigh, null)
        ReasoningLevel.XHIGH -> Icon(ReasoningHigh, null)
        ReasoningLevel.MAX -> Icon(ReasoningHigh, null)
    }
}

@Composable
private fun ReasoningLevel.label(): String = when (this) {
    ReasoningLevel.OFF -> stringResource(R.string.reasoning_off)
    ReasoningLevel.AUTO -> stringResource(R.string.reasoning_auto)
    ReasoningLevel.LOW -> stringResource(R.string.reasoning_light)
    ReasoningLevel.MEDIUM -> stringResource(R.string.reasoning_medium)
    ReasoningLevel.HIGH -> stringResource(R.string.reasoning_heavy)
    ReasoningLevel.XHIGH -> stringResource(R.string.reasoning_xhigh)
    ReasoningLevel.MAX -> stringResource(R.string.reasoning_max)
}

@Composable
@Preview(showBackground = true)
private fun ReasoningPickerPreview() {
    MaterialTheme {
        var level by remember { mutableStateOf(ReasoningLevel.AUTO) }
        ReasoningPicker(
            reasoningLevel = level,
            onUpdateReasoningLevel = { level = it }
        )
    }
}

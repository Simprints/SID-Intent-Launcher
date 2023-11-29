package com.simprints.simprintsidtester.fragments.integration.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.simprints.simprintsidtester.R
import com.simprints.simprintsidtester.fragments.integration.IntegrationViewModel
import com.simprints.simprintsidtester.compose.PropertyField
import com.simprints.simprintsidtester.fragments.integration.model.CoSyncItem
import com.simprints.simprintsidtester.fragments.integration.model.CoSyncTemplateType
import com.simprints.simprintsidtester.model.domain.CoSyncTemplate


@Composable
fun IntegrationFormScreen(viewModel: IntegrationViewModel) {
    IntegrationForm(
        projectId = viewModel.projectId,
        onProjectIdChange = viewModel::updateProjectId,
        userId = viewModel.userId,
        onUserIdChange = viewModel::updateUserId,
        moduleId = viewModel.moduleId,
        onModuleIdChange = viewModel::updateModuleId,
        guid = viewModel.guid,
        onGuidChange = viewModel::updateGuid,
        onEnroll = viewModel::enroll,
        onIdentify = viewModel::identify,
        onVerify = viewModel::verify,
        result = viewModel.result,
        templates = viewModel.templates,
        onTemplateSelectionChanged = viewModel::onTemplateSelectionChanged,
    )
}

@Preview(widthDp = 480, heightDp = 600)
@Composable
private fun IntegrationForm(
    projectId: String = "",
    onProjectIdChange: (String) -> Unit = {},
    userId: String = "",
    onUserIdChange: (String) -> Unit = {},
    moduleId: String = "",
    onModuleIdChange: (String) -> Unit = {},
    guid: String = "",
    onGuidChange: (String) -> Unit = {},
    onEnroll: () -> Unit = {},
    onIdentify: () -> Unit = {},
    onVerify: () -> Unit = {},
    result: String = "",
    templates: List<CoSyncItem> = emptyList(),
    onTemplateSelectionChanged: (CoSyncItem, Boolean) -> Unit = { _, _ -> }
) {
    val resultScroll = rememberScrollState()
    val focusManager = LocalFocusManager.current

    Column(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .background(Color.White)
            .fillMaxWidth(),
    ) {
        PropertyField(
            "Project ID",
            projectId,
            focusManager,
            onProjectIdChange,
            modifier = Modifier.fillMaxWidth(),
        )

        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            PropertyField(
                "User ID",
                userId,
                focusManager,
                onUserIdChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )
            PropertyField(
                "Module ID",
                moduleId,
                focusManager,
                onModuleIdChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )
        }

        PropertyField(
            "GUID (for verification only)",
            guid,
            focusManager,
            onGuidChange,
            modifier = Modifier.fillMaxWidth(),
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp, 4.dp)
        ) {
            Button(
                onClick = onEnroll,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .weight(1f),
            ) { Text("Enroll") }
            Button(
                onClick = onIdentify,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .weight(1f),
            ) { Text("Identify") }
            Button(
                onClick = onVerify,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .weight(1f),
            ) { Text("Verify") }
        }
        templates.takeUnless { it.isEmpty() }?.let {
            TemplatesRow(templates, onTemplateSelectionChanged)
        }
        SelectionContainer {
            Text(
                text = result,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(4.dp)
                    .background(Color.LightGray.copy(alpha = 0.2f))
                    .padding(4.dp)
                    .verticalScroll(resultScroll)
            )
        }
    }
}

@Composable
fun TemplatesRow(
    templates: List<CoSyncItem>,
    onTemplateSelectionChanged: (CoSyncItem, Boolean) -> Unit
) {
    val horizontalPadding = 12.dp
    val facesSelected = templates.count { it.type == CoSyncTemplateType.Face && it.isSelected }
    val fingerprintsSelected =
        templates.count { it.type == CoSyncTemplateType.Fingerprint && it.isSelected }
    val currentSelectionText = when {
        facesSelected == 0 && fingerprintsSelected == 0 -> "None"
        fingerprintsSelected == 0 -> "Faces: $facesSelected"
        facesSelected == 0 -> "Fingerprints: $fingerprintsSelected"
        else -> "Faces: $facesSelected; Fingerprints: $fingerprintsSelected"
    }
    Column {
        Text(
            modifier = Modifier.padding(horizontal = horizontalPadding),
            style = MaterialTheme.typography.body1,
            text = "Captured templates"
        )
        Text(
            modifier = Modifier.padding(horizontal = horizontalPadding,),
            style = MaterialTheme.typography.caption,
            text = "Select at least one template to use the 1:1 CoSync verification"
        )
        Text(
            modifier = Modifier.padding(
                start = horizontalPadding,
                end = horizontalPadding,
                top = 8.dp,
                bottom = 4.dp
            ),
            style = MaterialTheme.typography.caption,
            text = "Current selection: $currentSelectionText"
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = horizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            content = {
                items(templates.size) { i ->
                    TemplateWithCheckbox(templates[i], onTemplateSelectionChanged)
                }
            }
        )
    }
}

@Composable
fun TemplateWithCheckbox(
    item: CoSyncItem,
    onTemplateSelectionChanged: (CoSyncItem, Boolean) -> Unit
) {
    val (title, iconRes) = when (val template = item.payload.template) {
        is CoSyncTemplate.Face -> "Face ${item.faceId}" to R.drawable.face_24
        is CoSyncTemplate.Fingerprint -> template.finger.toString() to R.drawable.fingerprint_24
    }
    val bgAlpha = if (item.isSelected) 1f else 0.5f
    Row(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colors.primary.copy(alpha = bgAlpha))
            .padding(end = 8.dp)
            .clickable {
                onTemplateSelectionChanged(item, !item.isSelected)
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = item.isSelected,
            onCheckedChange = { isSelected -> onTemplateSelectionChanged(item, isSelected) },
            colors = CheckboxDefaults.colors(
                uncheckedColor = Color.White
            )
        )
        Icon(
            modifier = Modifier.padding(end = 4.dp),
            painter = painterResource(id = iconRes),
            contentDescription = "",
            tint = Color.White
        )
        Text(
            text = title,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}
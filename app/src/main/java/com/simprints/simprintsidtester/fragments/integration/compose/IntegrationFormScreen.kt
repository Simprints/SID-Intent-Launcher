package com.simprints.simprintsidtester.fragments.integration.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.simprints.simprintsidtester.fragments.integration.IntegrationViewModel
import com.simprints.simprintsidtester.compose.PropertyField


@Composable
fun IntegrationFormScreen(viewModel: IntegrationViewModel) {
    IntegrationForm(
        viewModel.projectId,
        viewModel::updateProjectId,
        viewModel.userId,
        viewModel::updateUserId,
        viewModel.moduleId,
        viewModel::updateModuleId,
        viewModel.guid,
        viewModel::updateGuid,
        viewModel::enroll,
        viewModel::identify,
        viewModel::verify,
        viewModel.result,
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

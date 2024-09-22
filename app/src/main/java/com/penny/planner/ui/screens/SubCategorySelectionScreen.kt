package com.penny.planner.ui.screens

import android.widget.Toast
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.penny.planner.R
import com.penny.planner.data.db.subcategory.SubCategoryEntity
import com.penny.planner.ui.components.SubCategoryAddPage
import com.penny.planner.viewmodels.CategoryViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubCategorySelectionScreen (
    viewModel: CategoryViewModel,
    enabled: Boolean,
    onDismiss: () -> Unit
) {
    val category = viewModel.getSelectedCategory()!!.name
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var savedList by remember {
        mutableStateOf(mapOf<String, String>())
    }
    val recommendedSubCategories = viewModel.getAllRecommendedSubCategories(category)
    var selectedSubCategory by remember {
        mutableStateOf(viewModel.getSelectedSubCategory())
    }

    var add by remember {
        mutableStateOf(false)
    }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    if (sheetState.isVisible && !enabled) {
        LaunchedEffect(key1 = "") {
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) {
                    onDismiss.invoke()
                }
            }
        }
    } else if (enabled) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState
        ) {
            if (add) {
                SubCategoryAddPage(
                    emojis = viewModel.getAllEmojis(),
                    onBack = {
                        add = false
                    }
                ) { name, icon ->
                    if (savedList.filter { it.key.lowercase() == name.lowercase() }.isNotEmpty()
                        || recommendedSubCategories.filter { it.key.lowercase() == name.lowercase() }.isNotEmpty()) {
                        Toast.makeText(context, context.resources.getString(R.string.name_match_error), Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.setSelectedSubCategory(
                            SubCategoryEntity(
                                name = name,
                                category = category,
                                icon = icon
                            )
                        )
                        onDismiss.invoke()
                    }
                }
            } else {
                LaunchedEffect(keys = emptyArray()) {
                    scope.launch {
                        savedList = viewModel.getAllSavedSubCategories(category).associate { it.name to it.icon }
                    }
                }

                SelectedSavedAndRecommendedListComponent(
                    addNeeded = selectedSubCategory == null,
                    title = stringResource(id = R.string.select_a_subcategory),
                    selectedItem = if (selectedSubCategory == null) Pair("", "") else  Pair(
                        selectedSubCategory!!.name, selectedSubCategory!!.icon),
                    recommendedList = recommendedSubCategories.filter { !savedList.containsKey(it.key) },
                    savedList = savedList,
                    selectedItemDeleted = {
                        selectedSubCategory = null
                        viewModel.deleteSelectedSubCategory()
                    },
                    editNeeded = viewModel.getSubCategoryEditable(),
                    editClicked = {
                        add = true
                    },
                    savedItemClicked = {
                        viewModel.setSelectedSubCategory(
                            SubCategoryEntity(
                                name = it,
                                icon = savedList[it]!!,
                                category = category
                            )
                        )
                        viewModel.setSubCategoryEditable(false)
                        onDismiss.invoke()
                    },
                    recommendedItemClicked = {
                        viewModel.setSelectedSubCategory(
                            SubCategoryEntity(
                                name = it,
                                icon = recommendedSubCategories[it]!!,
                                category = category
                            )
                        )
                        viewModel.setSubCategoryEditable(false)
                        onDismiss.invoke()
                    },
                    onAddClicked = {
                        viewModel.setSubCategoryEditable(true)
                        add = true
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewSubCategoryScreen() {
    SubCategorySelectionScreen(
        hiltViewModel<CategoryViewModel>(),
        true
    ) {}
}
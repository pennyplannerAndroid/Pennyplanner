package com.penny.planner.ui.screens

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.penny.planner.R
import com.penny.planner.data.db.subcategory.SubCategoryEntity
import com.penny.planner.models.NameIconPairWithKeyModel
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
    val scope = rememberCoroutineScope()
    var savedList by remember {
        mutableStateOf(listOf<NameIconPairWithKeyModel>())
    }
    var savedSubCategories by remember {
        mutableStateOf(setOf<String>())
    }

    LaunchedEffect(keys = emptyArray()) {
        scope.launch {
            val list = viewModel.getAllSavedSubCategories(category)
            savedSubCategories = list.map { it.searchKey }.toSet()
            savedList = list.map { NameIconPairWithKeyModel(name = it.name, icon = it.icon) }
        }
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
                    selectedSubCategory = selectedSubCategory,
                    savedList = savedSubCategories,
                    recommendedList = recommendedSubCategories,
                    onBack = {
                        add = false
                    }
                ) { name, icon, isEditable ->
                    viewModel.setSelectedSubCategory(
                        SubCategoryEntity(
                            name = name,
                            category = category,
                            icon = icon
                        )
                    )
                    viewModel.setSubCategoryEditable(isEditable)
                    onDismiss.invoke()
                }
            } else {
                SelectedSavedAndRecommendedListComponent(
                    addNeeded = selectedSubCategory == null,
                    title = stringResource(id = R.string.select_a_subcategory),
                    selectedItem = if (selectedSubCategory == null) Pair("", "") else  Pair(
                        selectedSubCategory!!.name, selectedSubCategory!!.icon),
                    recommendedList = recommendedSubCategories.filter { !savedSubCategories.contains(it.searchKey) }.filter { it.searchKey != selectedSubCategory?.name?.lowercase() },
                    savedList = savedList,
                    selectedItemDeleted = {
                        selectedSubCategory = null
                        viewModel.deleteSelectedSubCategory()
                    },
                    editNeeded = viewModel.getSubCategoryEditable(),
                    editClicked = {
                        add = true
                    },
                    savedItemClicked = { name, icon ->
                        viewModel.setSelectedSubCategory(
                            SubCategoryEntity(
                                name = name,
                                icon = icon,
                                category = category
                            )
                        )
                        viewModel.setSubCategoryEditable(false)
                        onDismiss.invoke()
                    },
                    recommendedItemClicked = { name, icon ->
                        viewModel.setSelectedSubCategory(
                            SubCategoryEntity(
                                name = name,
                                icon = icon,
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
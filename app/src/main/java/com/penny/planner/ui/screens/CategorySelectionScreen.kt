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
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.penny.planner.R
import com.penny.planner.data.db.category.CategoryEntity
import com.penny.planner.ui.components.CategoryAddPage
import com.penny.planner.viewmodels.CategoryViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelectionScreen (
    viewModel: CategoryViewModel,
    enabled: Boolean,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var savedList by remember {
        mutableStateOf(listOf<CategoryEntity>())
    }
    var savedCategories by remember {
        mutableStateOf(mapOf<String, String>())
    }
    val recommendedCategories = viewModel.getAllRecommendedCategories()
    var selectedCategory by remember {
        mutableStateOf(viewModel.getSelectedCategory())
    }
    val lifecycleOwner = LocalLifecycleOwner.current

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
                CategoryAddPage(
                    canEdit = viewModel.getCategoryEditable(),
                    emojis = viewModel.getAllEmojis(),
                    selectedCategory = selectedCategory,
                    limit = viewModel.limit,
                    savedList = savedCategories,
                    recommendedList = recommendedCategories,
                    onBack = {
                        if (!viewModel.getCategoryEditable()) {
                            selectedCategory = viewModel.getSelectedCategory()
                        }
                        add = false
                    }
                ) { name, limit, icon, isEditable ->
                    viewModel.setCategoryEditable(isEditable)
                    viewModel.setSelectedCategory(CategoryEntity(name, icon))
                    viewModel.limit = limit
                    onDismiss.invoke()
                }
            } else {
                LaunchedEffect(keys = emptyArray()) {
                    scope.launch {
                        viewModel.getAllSavedCategories().observe(lifecycleOwner) { list ->
                            savedList = list
                            savedCategories = list.associate { it.name to it.icon }
                        }
                    }
                }
                SelectedSavedAndRecommendedListComponent(
                    addNeeded = selectedCategory == null,
                    title = stringResource(id = R.string.select_a_category),
                    selectedItem = if (selectedCategory == null) Pair("", "") else  Pair(
                        selectedCategory!!.name, selectedCategory!!.icon),
                    recommendedList = recommendedCategories.filter { !savedCategories.containsKey(it.key) }.filter { it.key.lowercase() != selectedCategory?.name?.lowercase() },
                    savedList = savedCategories,
                    selectedItemDeleted = {
                        selectedCategory = null
                        viewModel.deleteSelectedCategory()
                    },
                    editNeeded = viewModel.getCategoryEditable(),
                    editClicked = {
                        add = true
                    },
                    savedItemClicked = {
                        viewModel.addCategoryToDb = false
                        var savedItem: CategoryEntity? = null
                        for (item in savedList) {
                            if (item.name == it) {
                                savedItem = item
                                break
                            }
                        }
                        if (savedItem != null) {
                            scope.launch {
                                viewModel.setSelectedCategory(CategoryEntity(name = savedItem.name, icon = savedItem.icon))
                                // check in budget table for limit set or not and code accordingly
                                viewModel.setCategoryEditable(false)
                                onDismiss.invoke()
                            }
                        } else {
                            viewModel.setCategoryEditable(false)
                            onDismiss.invoke()
                        }
                    },
                    recommendedItemClicked = {
                        viewModel.addCategoryToDb = true
                        selectedCategory = CategoryEntity(name = it, icon = recommendedCategories[it]!!)
                        viewModel.setCategoryEditable(false)
                        add = true
                    },
                    onAddClicked = {
                        viewModel.addCategoryToDb = true
                        viewModel.setCategoryEditable(true)
                        add = true
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewBottomSheetWithList() {
    CategorySelectionScreen(
        hiltViewModel<CategoryViewModel>(),
        true
    ) {}
}
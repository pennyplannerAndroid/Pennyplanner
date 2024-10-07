package com.penny.planner.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.penny.planner.R
import com.penny.planner.data.db.category.CategoryEntity
import com.penny.planner.data.db.expense.ExpenseEntity
import com.penny.planner.data.db.subcategory.SubCategoryEntity
import com.penny.planner.helpers.Utils
import com.penny.planner.ui.components.PaymentSelectionPage
import com.penny.planner.ui.components.PrimaryButton
import com.penny.planner.ui.components.TextFieldWithTrailingIcon
import com.penny.planner.viewmodels.CategoryViewModel

const val NONE = 0
const val CATEGORY = 1
const val SUBCATEGORY = 2
const val PAYMENT = 3

@Composable
fun AddExpenseScreen(
    onDismiss: () -> Unit,
    addExpense: (ExpenseEntity) -> Unit
) {
    val categoryViewModel = hiltViewModel<CategoryViewModel>()
    BackHandler(
        onBack = {
            categoryViewModel.deleteSelectedCategory()
            categoryViewModel.deleteSelectedSubCategory()
            onDismiss.invoke()
        }
    )
    var amount by remember {
        mutableStateOf("")
    }
    var details by remember {
        mutableStateOf("")
    }

    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            alpha.animateTo(1f, animationSpec = tween(durationMillis = 500))
            alpha.animateTo(0f, animationSpec = tween(durationMillis = 500))
        }
    }

    var selectedCategory by remember {
        mutableStateOf<CategoryEntity?>(null)
    }

    var selectedSubCategory by remember {
        mutableStateOf<SubCategoryEntity?>(null)
    }
    var payment by remember {
        mutableStateOf("")
    }


    var showDialog by remember {
        androidx.compose.runtime.mutableIntStateOf(0)
    }

    Scaffold(
        modifier = Modifier.background(color = Color.Red),
    ) { contentPadding ->
        Column(
            modifier = Modifier.background(color = Color.Red).padding(contentPadding)
        ) {
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = stringResource(id = R.string.add_expense),
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
            Text(
                modifier = Modifier
                    .padding(start = 24.dp, top = 64.dp)
                    .alpha(0.64f),
                text = stringResource(id = R.string.how_much),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorResource(id = R.color.text_header_color)
            )
            Row(
                modifier = Modifier
                    .padding(start = 24.dp, top = 8.dp, bottom = 8.dp, end = 24.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.rupee_icon),
                    fontSize = 64.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                BasicTextField(
                    value = amount,
                    onValueChange = {
                        if (it.length < Utils.PRICE_LIMIT) {
                            amount = it
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .height(64.dp)
                        .weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = LocalTextStyle.current.copy(fontSize = 64.sp, color = Color.White),
                    cursorBrush = if (amount.isEmpty()) SolidColor(Color.Transparent) else SolidColor(Color.White),
                    decorationBox = { innerTextField ->
                        Box(
                            contentAlignment = Alignment.CenterStart,
                            modifier = Modifier.padding(start = 6.dp)
                        ) {
                            if (amount.isEmpty()) {
                                Box(modifier = Modifier
                                    .width(2.dp)
                                    .height(64.dp)
                                    .graphicsLayer(alpha = alpha.value)
                                    .background(color = Color.White)
                                )
                                Text(
                                    modifier = Modifier
                                        .padding(start = 4.dp),
                                            text = "0",
                                    color = Color.Black,
                                    fontSize = 64.sp
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }
            Column(
                modifier = Modifier
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    )
            ) {
                TextFieldWithTrailingIcon(
                    value = if (selectedCategory == null) "" else "${selectedCategory!!.icon} ${selectedCategory!!.name}",
                    title = R.string.category
                ) {
                    showDialog = CATEGORY
                }
                TextFieldWithTrailingIcon(
                    value = if (selectedSubCategory == null) "" else "${selectedSubCategory!!.icon} ${selectedSubCategory!!.name}",
                    title = R.string.sub_category
                ) {
                    if (selectedCategory?.name?.isNotEmpty() == true)
                        showDialog = SUBCATEGORY
                }
                TextFieldWithTrailingIcon(
                    value = payment,
                    title = R.string.payment
                ) {
                    showDialog = PAYMENT
                }
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = colorResource(id = R.color.textField_border),
                        focusedBorderColor = colorResource(id = R.color.loginText)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    value = details,
                    onValueChange = { details = it },
                    label = {
                        Text(stringResource(id = R.string.description))
                    },
                    supportingText = {
                        Text(
                            text = "${amount.length}/${Utils.PRICE_LIMIT}",
                        )
                    }
                )
                PrimaryButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 30.dp, bottom = 36.dp)
                        .size(48.dp),
                    textRes = R.string.add,
                    onClick = {
                        if (categoryViewModel.addCategoryToDb)
                            categoryViewModel.addCategory(selectedCategory!!)
                        if (selectedSubCategory != null)
                            categoryViewModel.addSubCategory(selectedSubCategory!!)
                        if (categoryViewModel.addBudget)
                            categoryViewModel.addBudget(selectedCategory!!)
                        categoryViewModel.deleteSelectedSubCategory()
                        categoryViewModel.deleteSelectedCategory()
                        categoryViewModel.limit = ""
                        categoryViewModel.addCategoryToDb = false
                        categoryViewModel.addBudget = true
                        addExpense(
                            ExpenseEntity(
                            content = details,
                            category = selectedCategory?.name ?: "",
                            subCategory = selectedSubCategory?.name ?: Utils.DEFAULT,
                            price = amount.toDouble(),
                            paymentType = payment,
                            icon = if (selectedSubCategory != null && selectedSubCategory!!.name.isNotEmpty()) selectedSubCategory!!.icon else selectedCategory?.icon ?: Utils.DEFAULT_ICON
                            )
                        )
                    },
                    enabled = (amount.isNotEmpty() && selectedCategory?.name?.isNotEmpty() == true && payment.isNotEmpty())
                )
            }
            when (showDialog) {
                CATEGORY -> CategorySelectionScreen(
                    viewModel = categoryViewModel,
                    enabled = true,
                    onDismiss = {
                        showDialog = NONE
                        val updatedCategory = categoryViewModel.getSelectedCategory()
                        if (selectedCategory == null || updatedCategory == null ||
                            selectedCategory!!.name != updatedCategory.name) {
                            categoryViewModel.setSelectedSubCategory(null)
                            selectedSubCategory = null
                        }
                        selectedCategory = updatedCategory
                    }
                )

                SUBCATEGORY -> SubCategorySelectionScreen(
                    viewModel = categoryViewModel,
                    enabled = true,
                    onDismiss = {
                        showDialog = NONE
                        selectedSubCategory = categoryViewModel.getSelectedSubCategory()
                    }
                )
                PAYMENT -> PaymentSelectionPage(
                    title = stringResource(id = R.string.payment),
                    onDismiss = { showDialog = NONE },
                    enabled = true
                ) {
                    payment = it
                    showDialog = NONE
                }
                else -> {}
            }
        }
    }
}

@Preview
@Composable
fun PreviewAddExpenseScreen() {
    AddExpenseScreen({}) {}
}
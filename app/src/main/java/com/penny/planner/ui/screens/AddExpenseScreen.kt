package com.penny.planner.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.penny.planner.R
import com.penny.planner.data.db.category.CategoryEntity
import com.penny.planner.data.db.expense.ExpenseEntity
import com.penny.planner.data.db.subcategory.SubCategoryEntity
import com.penny.planner.helpers.enums.PaymentType
import com.penny.planner.ui.components.BottomSheetWithList
import com.penny.planner.ui.components.BottomSheetWithName
import com.penny.planner.ui.components.PrimaryButton
import com.penny.planner.ui.components.TextFieldWithTrailingIcon
import com.penny.planner.viewmodels.ExpenseAndCategoryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val NONE = 0
const val CATEGORY = 1
const val SUBCATEGORY = 2
const val PAYMENT = 3

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    viewModel: ExpenseAndCategoryViewModel,
    onDismiss: () -> Unit
) {
    var categoryList: List<CategoryEntity>? = null
    var subCategoryList: List<String>? = null
    val scope = rememberCoroutineScope()
    val state = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var amount by remember {
        mutableStateOf("")
    }
    var details by remember {
        mutableStateOf("")
    }

    val category by remember {
        mutableStateOf(CategoryEntity())
    }

    val subCategory by remember {
        mutableStateOf(SubCategoryEntity())
    }
    var payment by remember {
        mutableStateOf(PaymentType.CASH)
    }


    var showDialog by remember {
        androidx.compose.runtime.mutableIntStateOf(0)
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(keys = emptyArray()) {
        scope.launch {
            viewModel.getAllCategories().observe(lifecycleOwner) { list ->
                categoryList = list
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.Red,
        sheetState = state
    ) {
        Column(
            modifier = Modifier.align(Alignment.End)
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
                    modifier = Modifier
                        .padding(start = 8.dp),
                    value = amount,
                    onValueChange = { amount= it },
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(fontSize = 64.sp, color = Color.White),
                    cursorBrush = SolidColor(Color.White)
                )
            }
            Column(
                modifier = Modifier
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    )
            ) {
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
                    }
                )
                TextFieldWithTrailingIcon(
                    value = category.name,
                    title = R.string.category
                ) {
                    showDialog = CATEGORY
                }
                TextFieldWithTrailingIcon(
                    value = subCategory.name,
                    title = R.string.sub_category
                ) {
                    if (category.name.isNotEmpty()) {
                        scope.launch(Dispatchers.IO) {
                            subCategoryList = viewModel.getSubCategories(category.name)
                        }
                        showDialog = SUBCATEGORY
                    }
                }
                TextFieldWithTrailingIcon(
                    value = payment.toString(),
                    title = R.string.payment
                ) {
                    showDialog = PAYMENT
                }
                PrimaryButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 30.dp, bottom = 36.dp)
                        .size(48.dp),
                    textRes = R.string.add,
                    onClick = {
                        viewModel.addExpense(ExpenseEntity(
                            content = details,
                            category = category.name,
                            subCategory = subCategory.name,
                            price = amount,
                            paymentType = payment.toString()
                        ))
                        onDismiss.invoke()
                    },
                    enabled = (amount.isNotEmpty() && category.name.isNotEmpty())
                )
            }
            when (showDialog) {
                CATEGORY -> BottomSheetWithList(
                    stringResource(id = R.string.category),
                    categoryList,
                    true,
                    { showDialog = NONE },
                    true
                ) { name, limit ->
                    category.name = name
                    category.limit = limit
                    viewModel.addCategory(category)
                    showDialog = NONE
                }

                SUBCATEGORY -> BottomSheetWithName(
                    title = stringResource(id = R.string.sub_category),
                    list = subCategoryList,
                    onDismiss = { showDialog = NONE },
                    showSheet = true
                ) {
                    subCategory.name = it
                    subCategory.category = category.name
                    viewModel.addSubCategory(subCategory)
                    showDialog = NONE
                }
                PAYMENT -> BottomSheetWithName(
                    title = stringResource(id = R.string.sub_category),
                    list =  PaymentType.entries.map { it.toString() },
                    onDismiss = { showDialog = NONE },
                    showSheet = true
                ) {
                    payment = enumValueOf<PaymentType>(it)
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
//    AddExpenseScreen {}
}
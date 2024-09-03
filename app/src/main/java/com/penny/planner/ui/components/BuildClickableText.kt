package com.penny.planner.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import com.penny.planner.R
import com.penny.planner.Utils

@Composable
fun buildText(start : Int, end: Int) : AnnotatedString {
    return buildAnnotatedString {
        append(stringResource(id = start).plus(" "))
        pushStringAnnotation(
            tag = Utils.CLICK_TAG,
            annotation = stringResource(id = end)
        )
        withStyle(
            style = SpanStyle(
                color = colorResource(id = R.color.loginText),
                textDecoration = TextDecoration.Underline,
            )
        ) {
            append(
                stringResource(id = end)
            )
        }
    }
}
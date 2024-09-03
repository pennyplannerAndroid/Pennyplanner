package com.penny.planner.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penny.planner.data.repositories.UserAndExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val repository: UserAndExpenseRepository
) : ViewModel() {

    private val _validateEmailResult = MutableLiveData<Result<Boolean>>()
    val validateEmailResult: LiveData<Result<Boolean>> = _validateEmailResult

    private val _newGroupResult = MutableLiveData<Result<Boolean>>()
    val newGroupResult: LiveData<Result<Boolean>> = _newGroupResult

    fun getName(): String {
        return repository.getUserName()
    }

    fun newGroup(email: String) {
        viewModelScope.launch {
            _newGroupResult.value = repository.newGroup(email)
        }
    }
}
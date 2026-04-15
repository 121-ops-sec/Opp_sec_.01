package com.opsec.fieldintelligence.ui.frequency

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.opsec.fieldintelligence.data.db.AppDatabase
import com.opsec.fieldintelligence.data.model.FrequencyLog
import com.opsec.fieldintelligence.data.repository.FrequencyRepository
import kotlinx.coroutines.launch

class FrequencyListViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = FrequencyRepository(AppDatabase.getInstance(application).frequencyLogDao())

    val allLogs: LiveData<List<FrequencyLog>> = repo.allLogs.asLiveData()

    fun deleteLog(log: FrequencyLog) = viewModelScope.launch { repo.deleteLog(log) }
}

package com.opsec.fieldintelligence.ui.hazard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.opsec.fieldintelligence.data.db.AppDatabase
import com.opsec.fieldintelligence.data.model.Hazard
import com.opsec.fieldintelligence.data.model.HazardSeverity
import com.opsec.fieldintelligence.data.repository.HazardRepository
import kotlinx.coroutines.launch

class HazardListViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = HazardRepository(AppDatabase.getInstance(application).hazardDao())

    val activeHazards: LiveData<List<Hazard>> = repo.activeHazards.asLiveData()

    fun bySeverity(severity: HazardSeverity): LiveData<List<Hazard>> =
        repo.hazardsBySeverity(severity).asLiveData()

    fun deleteHazard(hazard: Hazard) = viewModelScope.launch { repo.deleteHazard(hazard) }
}

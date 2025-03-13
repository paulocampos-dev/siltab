package com.prototype.silver_tab.viewmodels

import androidx.lifecycle.ViewModel
import com.prototype.silver_tab.data.models.ImageDTO
import com.prototype.silver_tab.data.repository.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InspectionInfoViewModel @Inject constructor(
    private val imageRepository: ImageRepository
) : ViewModel() {
    suspend fun getPdiImages(pdiId: Int): List<ImageDTO> {
        return imageRepository.getAllPdiImages(pdiId) ?: emptyList()
    }
}

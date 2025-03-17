package com.prototype.silver_tab.data.models

import java.time.LocalDateTime

data class ImageDTO(
    val imageId: Int? = null,
    val pdiId: Int? = null,
    val pdiImageType: String? = null,
    val imageData: String? = null,  // Base64 encoded image
    val fileName: String? = null,
    val filePath: String? = null,   // Storage path on server
    val createDate: String? = null
)

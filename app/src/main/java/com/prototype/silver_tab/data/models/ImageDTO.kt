package com.prototype.silver_tab.data.models

import java.time.LocalDateTime

data class ImageDTO(
    val imageId: Int? = null,
    val pdiId: Int? = null,
    val imageTypeName: String? = null,
    val imageData: String? = null,
    val fileName: String? = null,
    val filePath: String? = null,
    val createDate: String? = null
)


package com.prototype.silver_tab.data.models

import java.time.LocalDateTime

data class ImageDTO(
    var imageId: Int? = null,
    var dealerCode: String? = null,
    var pdiId: Int? = null,
    var imageTypeName: String? = null,
    var fileName: String? = null,
    var filePath: String? = null,
    var uploadDate: String? = null,
    var uploadedByUserId: Int? = null,
    var imageData: String? = null,
    var mimeType: String? = null
) {
    val description: String? = null
}

package com.prototype.silver_tab.ui.camera

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import android.Manifest
import com.prototype.silver_tab.data.models.mockProfile
import com.prototype.silver_tab.ui.components.ProfileModal
import com.prototype.silver_tab.utils.CameraUtils

class CameraManager(
    private val context: Context,
    private val cameraUtils: CameraUtils,
) {
    private var currentImageType: ImageType? = null
    private var tempPhotoUri: Uri? = null

    sealed class CameraResult {
        data class Success(val uri: Uri) : CameraResult()
        data class Error(val message: String) : CameraResult()
        object Cancelled : CameraResult()
        object PermissionDenied : CameraResult()
    }

    fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun createTempUri(): Uri? {
        return try {
            val photoFile = cameraUtils.createImageFile()
            cameraUtils.getUriForFile(photoFile)
        } catch (ex: Exception) {
            null
        }
    }
}

@Composable
fun rememberCameraManager(
    context: Context,
    cameraUtils: CameraUtils,
    onImageCaptured: (ImageType, Uri) -> Unit
): CameraState {
    val cameraManager = remember { CameraManager(context, cameraUtils) }
    var currentImageType by remember { mutableStateOf<ImageType?>(null) }
    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }
    // Keep track of which image we're currently handling
    var tempImageType by remember { mutableStateOf<ImageType?>(null) }



    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempPhotoUri?.let { uri ->
                currentImageType?.let { type ->
                    onImageCaptured(type, uri)
                }
            }
        }
    }


    // Function to actually launch the camera
    fun launchCameraWithPermission(imageType: ImageType) {
        try {
            currentImageType = imageType
            val photoFile = cameraUtils.createImageFile()
            tempPhotoUri = cameraUtils.getUriForFile(photoFile)
            cameraLauncher.launch(tempPhotoUri)
        } catch (ex: Exception) {
            // Handle error
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            tempImageType?.let { launchCameraWithPermission(it) }
        } else {
            // Show error message
        }
    }

    // Function to check permission and launch camera
    fun launchCamera(imageType: ImageType) {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) -> {
                launchCameraWithPermission(imageType)
            }
            else -> {
                tempImageType = imageType
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }


    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            currentImageType?.let { type ->
                onImageCaptured(type, it)
            }
        }
    }

    fun launchGallery(imageType: ImageType) {
        currentImageType = imageType
        galleryLauncher.launch("image/*")
    }

    return remember(cameraManager) {
        CameraState(
            launchCamera = ::launchCamera,
            launchGallery = ::launchGallery
        )
    }
}

data class CameraState(
    val launchCamera: (ImageType) -> Unit,
    val launchGallery: (ImageType) -> Unit
)

enum class ImageType {
    CHASSIS,
    BATTERY,
    VOLTAGE,
    TIRE_PRESSURE,
    CAR_STARTED
}
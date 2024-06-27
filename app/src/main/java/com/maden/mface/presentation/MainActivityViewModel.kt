package com.maden.mface.presentation

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.activity.result.ActivityResult
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

typealias photoName = String
typealias photo = Bitmap

class MainActivityViewModel : ViewModel() {

    private val _galleryPhotoMutableLive = MutableLiveData<Pair<photo, photoName>>()
    val galleryPhotoLiveData: LiveData<Pair<photo, photoName>> = _galleryPhotoMutableLive

    private val _uiStateMutableLive = MutableLiveData<MainActivityUIState>()
    val uiStateLiveData: LiveData<MainActivityUIState> = _uiStateMutableLive

    var photoName = ""

    fun setUiState(uiState: MainActivityUIState) {
        _uiStateMutableLive.postValue(uiState)
    }

    fun uriToBitmap(activityResult: ActivityResult, context: Context) {
        try {
            val imageUri: Uri? = activityResult.data?.data

            imageUri?.let {
                val inputStream = context.contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                val rotatedBitmap = rotateImageIfRequired(bitmap, it, context)
                _galleryPhotoMutableLive.postValue(
                    Pair(
                        rotatedBitmap,
                        photoName
                    )
                )
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun rotateImageIfRequired(bitmap: Bitmap, uri: Uri, context: Context): Bitmap {
        val input: InputStream? = context.contentResolver.openInputStream(uri)
        val ei: ExifInterface
        try {
            ei = ExifInterface(input!!)
        } catch (e: IOException) {
            e.printStackTrace()
            return bitmap
        }

        return when (ei.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270f)
            ExifInterface.ORIENTATION_NORMAL -> bitmap
            else -> bitmap
        }
    }

    private fun rotateImage(bitmap: Bitmap, degree: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree)
        val rotatedImg =
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        bitmap.recycle()
        return rotatedImg
    }
}
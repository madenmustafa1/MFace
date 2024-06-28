package com.maden.mface.presentation.face_detector

import android.graphics.Bitmap
import com.maden.mface.presentation.MFaceUIState

interface MDetectorListener {
    fun faceDetectorUIState(mFaceUiState: MFaceUIState) { }
    fun onFaceDetected(face: Bitmap)
    fun onDetectorError(error: String)
}
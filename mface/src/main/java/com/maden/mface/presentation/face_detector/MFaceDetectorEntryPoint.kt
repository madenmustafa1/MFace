package com.maden.mface.presentation.face_detector

import android.graphics.Bitmap
import com.maden.mface.core.face_detector.MFaceDetector
import com.maden.mface.presentation.MFaceUIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MFaceDetectorEntryPoint(
    private val _listener: MDetectorListener
) {

    private val _mFaceDetector: MFaceDetector = MFaceDetector(_listener)

    suspend fun execute(bitmap: Bitmap) = withContext(Dispatchers.IO) {
        _listener.faceDetectorUIState(MFaceUIState.LOADING)

        val result = runCatching {
            _mFaceDetector.detectFaces(bitmap)
        }

        if (result.isFailure) {
            _listener.onDetectorError(result.exceptionOrNull()?.message ?: "Unknown sdk error | FaceDetector | #17")
        }

        _listener.faceDetectorUIState(MFaceUIState.FINISH)
    }
}
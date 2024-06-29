package com.maden.mface.presentation.face_detector

import android.graphics.Bitmap
import com.maden.mface.common.errorLog
import com.maden.mface.core.face_detector.MFaceDetector
import com.maden.mface.data.face_match.model.FaceDetectorRequest
import com.maden.mface.presentation.MFaceUIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MFaceDetectorEntryPoint(
    private val _requestModel: FaceDetectorRequest = FaceDetectorRequest(),
    private val _listener: MDetectorListener
) {

    //Initialize the face detector.
    private val _mFaceDetector: MFaceDetector =
        MFaceDetector(
            _requestModel = _requestModel, _listener = _listener
        )

    /**
     * @description -> Detect face from bitmap.
     * @description -> Only one face will return
     *
     * @param bitmap: Bitmap -> Image to detect face.
     *
     * @return onFaceDetected(face: Bitmap) -> captured face from bitmap.
     * @return onDetectorError(error: String) -> error message.
     *
     * @exception Exception -> Corrupted bitmap
     * @exception IllegalArgumentException -> Null or empty bitmap.
     */
    suspend fun execute(bitmap: Bitmap) = withContext(Dispatchers.IO) {
        //Set ui state to loading
        _listener.faceDetectorUIState(MFaceUIState.LOADING)

        //Detect face
        val result = runCatching {
            _mFaceDetector.detectFaces(bitmap)
        }

        //Check result
        if (result.isFailure) {
            val errorMessage =
                result.exceptionOrNull()?.message ?: "Unknown sdk error | FaceDetector | #17"
            errorMessage.errorLog()

            _listener.onDetectorError(
                result.exceptionOrNull()?.message ?: "Unknown sdk error | FaceDetector | #17"
            )
        }

        //Set ui state to finish
        _listener.faceDetectorUIState(MFaceUIState.FINISH)
    }
}
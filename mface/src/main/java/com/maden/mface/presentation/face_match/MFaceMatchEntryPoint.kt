package com.maden.mface.presentation.face_match

import android.content.Context
import android.graphics.Bitmap
import com.maden.mface.core.face_match.MFaceMatch
import com.maden.mface.data.face_match.model.FaceMatchRequestModel
import com.maden.mface.presentation.MFaceUIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MFaceMatchEntryPoint(
    private val _context: Context,
    private val _requestModel: FaceMatchRequestModel = FaceMatchRequestModel(),
    private val _listener: MFaceMatchLister
) {

    private val _mFaceMatch: MFaceMatch =
        MFaceMatch(
            _context = _context,
            _requestModel = _requestModel,
            _listener = _listener
        )

    suspend fun addFace(name: String, face: Bitmap) = withContext(Dispatchers.IO) {
        _listener.faceMatchUIState(MFaceUIState.LOADING)

        val result = runCatching {
            _mFaceMatch.addFace(name, face)
        }

        if (result.isFailure) {
            _listener.onFaceMatchError(result.exceptionOrNull()?.message ?: "Unknown sdk error | addFace | #29")
        }

        _listener.faceMatchUIState(MFaceUIState.FINISH)
    }

    suspend fun recognizeFace(face: Bitmap) = withContext(Dispatchers.IO) {
        _listener.faceMatchUIState(MFaceUIState.LOADING)

        val result = runCatching {
            _mFaceMatch.recognizeImage(face)
        }

        if (result.isFailure) {
            _listener.onFaceMatchError(result.exceptionOrNull()?.message ?: "Unknown sdk error | recognizeFace | #39")
        }

        _listener.faceMatchUIState(MFaceUIState.FINISH)
    }

}
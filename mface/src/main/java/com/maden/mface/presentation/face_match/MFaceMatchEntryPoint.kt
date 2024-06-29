package com.maden.mface.presentation.face_match

import android.content.Context
import android.graphics.Bitmap
import com.maden.mface.common.errorLog
import com.maden.mface.core.face_match.MFaceMatch
import com.maden.mface.data.face_match.model.FaceMatchRequest
import com.maden.mface.presentation.MFaceUIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MFaceMatchEntryPoint(
    private val _context: Context,
    private val _requestModel: FaceMatchRequest = FaceMatchRequest(),
    private val _listener: MFaceMatchLister
) {

    //Initialize the face match.
    private val _mFaceMatch: MFaceMatch =
        MFaceMatch(
            _context = _context,
            _requestModel = _requestModel,
            _listener = _listener
        )

    /**
     * @description -> Add face to the list.
     * @description -> Only one face result.
     *
     * @param name: String -> Name of the face.
     * @param face: Bitmap -> Cropped face.
     *
     * @return onFaceMatchResult(result: Boolean) -> if face added successfully.
     * @return onFaceMatchError(error: String) -> if face not added.
     *
     * @exception Exception -> Corrupted bitmap.
     * @exception IllegalArgumentException -> Unsupported width or height.
     * @exception IllegalArgumentException -> Null or empty bitmap.
     */
    suspend fun addFace(name: String, face: Bitmap) = withContext(Dispatchers.IO) {
        //Set ui state to loading
        _listener.faceMatchUIState(MFaceUIState.LOADING)

        //Add face
        val result = runCatching {
            _mFaceMatch.addFace(name, face)
        }

        //Check result
        if (result.isFailure) {
            val errorMessage = result.exceptionOrNull()?.message ?: "Unknown sdk error | addFace | #29"
            errorMessage.errorLog()
            _listener.onFaceMatchError(errorMessage)
        }

        //Set ui state to finish
        _listener.faceMatchUIState(MFaceUIState.FINISH)
    }

    /**
     * @description: Recognize face from the list.
     * @description: Only one face result.
     *
     * @param face: Bitmap -> Cropped face.
     *
     * @return onRecognizeFace(result: Boolean, name: String) -> if face recognized successfully.
     * @return onFaceMatchError(error: String) -> if face not recognized.
     *
     * @exception Exception -> Corrupted bitmap.
     * @exception IllegalArgumentException -> Unsupported width or height.
     * @exception IllegalArgumentException -> Null or empty bitmap.
     */
    suspend fun recognizeFace(face: Bitmap) = withContext(Dispatchers.IO) {
        //Set ui state to loading
        _listener.faceMatchUIState(MFaceUIState.LOADING)

        //Recognize face
        val result = runCatching {
            _mFaceMatch.recognizeImage(face)
        }

        //Check result
        if (result.isFailure) {
            val errorMessage = result.exceptionOrNull()?.message ?: "Unknown sdk error | recognizeFace | #39"
            errorMessage.errorLog()
            _listener.onFaceMatchError(errorMessage)
        }

        //Set ui state to finish
        _listener.faceMatchUIState(MFaceUIState.FINISH)
    }

}
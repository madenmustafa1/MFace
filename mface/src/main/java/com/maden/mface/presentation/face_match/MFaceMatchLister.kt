package com.maden.mface.presentation.face_match

import com.maden.mface.presentation.MFaceUIState

interface MFaceMatchLister {
    fun addFaceResult(result: Boolean) { }
    fun faceMatchUIState(mFaceUiState: MFaceUIState) { }
    fun onRecognizeFace(result: Boolean, name: String)
    fun onFaceMatchError(error: String)
}
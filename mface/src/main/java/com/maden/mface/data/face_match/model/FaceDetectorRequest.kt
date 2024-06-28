package com.maden.mface.data.face_match.model

import com.maden.mface.core.face_detector.FaceDetectorConstants

data class FaceDetectorRequest(
    val width: Int = FaceDetectorConstants.CROPPED_FACE_WIDTH,
    val height: Int = FaceDetectorConstants.CROPPED_FACE_HEIGHT,
)
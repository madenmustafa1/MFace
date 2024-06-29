package com.maden.mface.data.face_match.model

import com.maden.mface.core.face_detector.FaceDetectorConstants

/**
 * @description -> Do not change these parameters unless you are going to use your own TFLite model.
 * @param width -> Face width.
 * @param height -> Face height.
 */
data class FaceDetectorRequest(
    val width: Int = FaceDetectorConstants.CROPPED_FACE_WIDTH,
    val height: Int = FaceDetectorConstants.CROPPED_FACE_HEIGHT,
)
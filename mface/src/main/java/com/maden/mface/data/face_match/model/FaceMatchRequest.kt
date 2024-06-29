package com.maden.mface.data.face_match.model

import com.maden.mface.core.face_match.FaceMatchConstants

/**
 * @description -> Do not change these parameters unless you are going to use your own TFLite model.
 *
 * @param imageMean: Float -> Mean value of the bitmap.
 * @param imageStd: Float -> Standard deviation value of the bitmap.
 * @param inputSize: Int -> Input size of the model.
 * @param modelFile: String -> TFLite Model file name. (Asset)
 * @param distance: Float -> Distance between faces.
 * @param outputSize: Int -> Output size of the model.
 * @param isModelQuantized: Boolean -> Is model quantized.
 */
data class FaceMatchRequest(
    var imageMean: Float = FaceMatchConstants.IMAGE_MEAN,
    var imageStd: Float = FaceMatchConstants.IMAGE_STD,
    var inputSize: Int = FaceMatchConstants.INPUT_SIZE,
    var modelFile: String = FaceMatchConstants.MODEL_FILE,
    var distance: Float = FaceMatchConstants.DISTANCE,
    var outputSize: Int = FaceMatchConstants.OUTPUT_SIZE,
    var isModelQuantized: Boolean = FaceMatchConstants.IS_MODEL_QUANTIZED
)

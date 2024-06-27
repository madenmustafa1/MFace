package com.maden.mface.data.face_match.model

import com.maden.mface.core.face_match.FaceMatchConstants

data class FaceMatchRequestModel(
    var imageMean: Float = FaceMatchConstants.IMAGE_MEAN,
    var imageStd: Float = FaceMatchConstants.IMAGE_STD,
    var inputSize: Int = FaceMatchConstants.INPUT_SIZE,
    var modelFile: String = FaceMatchConstants.MODEL_FILE,
    var distance: Float = FaceMatchConstants.DISTANCE,
    var outputSize: Int = FaceMatchConstants.OUTPUT_SIZE,
    var isModelQuantized: Boolean = FaceMatchConstants.IS_MODEL_QUANTIZED
)

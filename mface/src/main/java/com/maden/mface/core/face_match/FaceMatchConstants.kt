package com.maden.mface.core.face_match

internal object FaceMatchConstants {
     var IMAGE_MEAN: Float = 128.0f
     var IMAGE_STD: Float = 128.0f
     var OUTPUT_SIZE: Int = 192 //Output size of mode
     var INPUT_SIZE: Int = 112
     var DISTANCE: Float = 1.0f
     var MODEL_FILE: String = "mobile_face_net.tflite"
     var IS_MODEL_QUANTIZED: Boolean = false
}
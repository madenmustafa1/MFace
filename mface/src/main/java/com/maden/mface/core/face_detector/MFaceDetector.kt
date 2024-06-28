package com.maden.mface.core.face_detector

import android.graphics.Bitmap
import android.graphics.RectF
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.maden.mface.common.errorLog
import com.maden.mface.common.log
import com.maden.mface.core.bitmap.getCropBitmapByCPU
import com.maden.mface.core.bitmap.getResizedBitmap
import com.maden.mface.core.bitmap.rotateBitmap
import com.maden.mface.data.face_match.model.FaceDetectorRequest
import com.maden.mface.presentation.face_detector.MDetectorListener

internal class MFaceDetector(
    private val _requestModel: FaceDetectorRequest,
    private val _listener: MDetectorListener
) {

    private val _detector: FaceDetector by lazy {
        FaceDetection.getClient(
            FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .build()
        )}

    suspend fun detectFaces(bitmap: Bitmap) {
        _detector.process(InputImage.fromBitmap(bitmap, 0))
            .addOnSuccessListener { faces ->
                if (faces.isNotEmpty()) {
                    val face: Face = faces[0]
                    val frameBitmap = bitmap.rotateBitmap(0f, false, false)

                    val boundingBox = RectF(face.boundingBox)
                    val croppedFace = frameBitmap.getCropBitmapByCPU(boundingBox)

                    val scaled = croppedFace.getResizedBitmap(_requestModel.width, _requestModel.height)

                    "Face Detected".log()

                    _listener.onFaceDetected(scaled)
                } else {
                    val message = "No faces detected"
                    _listener.onDetectorError("No faces detected")
                    message.errorLog()
                }
            }
            .addOnFailureListener { e ->
                _listener.onDetectorError(e.localizedMessage ?: "Unknown sdk error #58")
                e.log()
            }
    }
}
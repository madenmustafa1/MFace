package com.maden.mface.core.face_match

import android.content.Context
import android.graphics.Bitmap
import com.maden.mface.common.log
import com.maden.mface.core.face_match.SimilarityClassifier.Recognition
import com.maden.mface.data.face_match.model.FaceMatchRequest
import com.maden.mface.presentation.face_match.MFaceMatchLister
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.sqrt

internal class MFaceMatch(
    private val _context: Context,
    private val _requestModel: FaceMatchRequest,
    private val _listener: MFaceMatchLister
) {
    private var _tfLite: Interpreter? = null

    private lateinit var _embeedings: Array<FloatArray>

    private val _registeredFaces = HashMap<String, Recognition>()
    private lateinit var _intValues: IntArray

    init {
        _tfLite = Interpreter(loadModelFile())
    }

    private fun loadModelFile(): MappedByteBuffer {
        val fileDescriptor = _context.assets.openFd(_requestModel.modelFile)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    suspend fun addFace(name: String, face: Bitmap) {
        recognizeImage(bitmap = face)
        val result = Recognition(
            "0",
            "",
            -1f
        )

        result.extra = _embeedings
        _registeredFaces[name] = result

        _listener.addFaceResult(true)
    }

    suspend fun recognizeImage(bitmap: Bitmap) {
        //Create ByteBuffer to store normalized image
        val imgData =
            ByteBuffer.allocateDirect(1 * _requestModel.inputSize * _requestModel.inputSize * 3 * 4)

        imgData.order(ByteOrder.nativeOrder())

        _intValues = IntArray(_requestModel.inputSize * _requestModel.inputSize)

        //get pixel values from Bitmap to normalize
        bitmap.getPixels(_intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        imgData.rewind()

        for (i in 0 until _requestModel.inputSize) {
            for (j in 0 until _requestModel.inputSize) {
                val pixelValue: Int = _intValues[i * _requestModel.inputSize + j]
                if (_requestModel.isModelQuantized) {
                    // Quantized model
                    imgData.put(((pixelValue shr 16) and 0xFF).toByte())
                    imgData.put(((pixelValue shr 8) and 0xFF).toByte())
                    imgData.put((pixelValue and 0xFF).toByte())
                } else {
                    // Float model
                    imgData.putFloat((((pixelValue shr 16) and 0xFF) - _requestModel.imageMean) / _requestModel.imageStd)
                    imgData.putFloat((((pixelValue shr 8) and 0xFF) - _requestModel.imageMean) / _requestModel.imageStd)
                    imgData.putFloat(((pixelValue and 0xFF) - _requestModel.imageMean) / _requestModel.imageStd)
                }
            }
        }
        //imgData is input to our model
        val inputArray = arrayOf<Any>(imgData)

        val outputMap: MutableMap<Int, Any> = HashMap()

        _embeedings =
            Array(1) { FloatArray(_requestModel.outputSize) } //output of model will be stored in this variable

        outputMap[0] = _embeedings

        _tfLite!!.runForMultipleInputsOutputs(inputArray, outputMap) //Run model

        var distanceLocal = Float.MAX_VALUE

        //Compare new face with saved Faces.
        if (_registeredFaces.size > 0) {
            val nearest = findNearest(_embeedings[0]) //Find 2 closest matching face

            if (nearest[0] != null) {
                val name = nearest[0]!!.first //get name and distance of closest matching face
                distanceLocal = nearest[0]!!.second

                //If distance between Closest found face is more than 1.000 ,then output UNKNOWN face.
                if (distanceLocal < _requestModel.distance) {
                    _listener.onRecognizeFace(true, name)
                } else {
                    val message = "Unknown face"
                    message.log()
                    _listener.onRecognizeFace(false, "Unknown face")
                }
            }
        }
    }

    //Compare Faces by distance between face embeddings
    private fun findNearest(emb: FloatArray): List<Pair<String, Float>?> {
        val neighbourList: MutableList<Pair<String, Float>?> = ArrayList()
        var ret: Pair<String, Float>? = null //to get closest match
        var prevRet: Pair<String, Float>? = null //to get second closest match
        for ((name, value) in _registeredFaces) {
            val knownEmb = (value.extra as Array<FloatArray>)[0]

            var distance = 0f
            for (i in emb.indices) {
                val diff = emb[i] - knownEmb[i]
                distance += diff * diff
            }
            distance = sqrt(distance.toDouble()).toFloat()
            if (ret == null || distance < ret.second) {
                prevRet = ret
                ret = Pair(name, distance)
            }
        }
        if (prevRet == null) prevRet = ret
        neighbourList.add(ret)
        neighbourList.add(prevRet)

        return neighbourList
    }

}
package com.maden.mface.core.bitmap

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.YuvImage
import android.media.Image
import java.io.ByteArrayOutputStream
import java.nio.ReadOnlyBufferException
import kotlin.experimental.inv

internal fun Bitmap.getResizedBitmap(newWidth: Int, newHeight: Int): Bitmap {
    val width = this.width
    val height = this.height
    val scaleWidth = (newWidth.toFloat()) / width
    val scaleHeight = (newHeight.toFloat()) / height
    // CREATE A MATRIX FOR THE MANIPULATION
    val matrix: Matrix = Matrix()
    // RESIZE THE BIT MAP
    matrix.postScale(scaleWidth, scaleHeight)

    // "RECREATE" THE NEW BITMAP
    val resizedBitmap = Bitmap.createBitmap(
        this, 0, 0, width, height, matrix, false
    )

    return resizedBitmap
}

internal fun Bitmap.getCropBitmapByCPU(cropRectF: RectF): Bitmap {
    val resultBitmap = Bitmap.createBitmap(
        cropRectF.width().toInt(),
        cropRectF.height().toInt(), Bitmap.Config.ARGB_8888
    )
    val cavas = Canvas(resultBitmap)

    // draw background
    val paint = Paint(Paint.FILTER_BITMAP_FLAG)
    paint.color = Color.WHITE
    cavas.drawRect(
        RectF(0f, 0f, cropRectF.width(), cropRectF.height()),
        paint
    )

    val matrix: Matrix = Matrix()
    matrix.postTranslate(-cropRectF.left, -cropRectF.top)

    cavas.drawBitmap(this, matrix, paint)

    return resultBitmap
}

internal fun Bitmap.rotateBitmap(
    rotationDegrees: Float, flipX: Boolean, flipY: Boolean
): Bitmap {
    val matrix: Matrix = Matrix()

    // Rotate the image back to straight.
    matrix.postRotate(rotationDegrees)

    // Mirror the image along the X or Y axis.
    matrix.postScale(if (flipX) -1.0f else 1.0f, if (flipY) -1.0f else 1.0f)
    val rotatedBitmap =
        Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, true)

    return rotatedBitmap
}


package com.lovestory.lovestory.module

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.TypedValue
import android.view.View
import androidx.core.view.drawToBitmap

fun View.toBitmap(width: Int, height: Int): Bitmap {
    measure(
        View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
        View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
    )

    layout(0, 0, width, height)

    return drawToBitmap()
}

@SuppressLint("ViewConstructor")
class ClusterView(context: Context, private val bitmap: Bitmap, private val value: Int) : View(context) {
    private val textPaint: Paint = Paint().apply {
        textSize = 16f.spToPx(context).toFloat() // Adjust the text size as needed
        color = Color.WHITE // Set the text color
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }


    private val backgroundPaint: Paint = Paint().apply {
        color = Color.WHITE // Set the background color
        style = Paint.Style.FILL
    }

    private val borderPaint: Paint = Paint().apply {
        color = Color.WHITE // Set the border color
        style = Paint.Style.STROKE
        strokeWidth = 1.dpToPx(context).toFloat() // Set the border width (1dp converted to pixels)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val bitmapSize = 50.dpToPx(context).toFloat()

        // Draw the background
        val backgroundRect = RectF(0f, 0f, width.toFloat(), height.toFloat())
        canvas.drawRect(backgroundRect, backgroundPaint)

        // Draw the bitmap in the center
        val bitmapRect = RectF(
            centerX - bitmapSize / 2f,
            centerY - bitmapSize / 2f,
            centerX + bitmapSize / 2f,
            centerY + bitmapSize / 2f
        )
        canvas.drawBitmap(bitmap, null, bitmapRect, null)

        // Draw the text in the center
        val textBounds = Rect()
        textPaint.getTextBounds(value.toString(), 0, value.toString().length, textBounds)

        val textX = centerX
        val textY = centerY + textBounds.height() / 2f

        canvas.drawText(value.toString(), textX, textY, textPaint)

        // Draw the border
        canvas.drawRect(backgroundRect, borderPaint)
    }

    /*
    private val textPaint: Paint = Paint().apply {
        textSize = 48f // Adjust the text size as needed
        color = Color.WHITE // Set the text color
        textAlign = Paint.Align.CENTER
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val bitmapSize = 100.toPx().toFloat()
        val borderWidth = 50.toPx().toFloat() // Border width in pixels

        val bitmapRect = RectF(centerX - bitmapSize / 2f, centerY - bitmapSize / 2f, centerX + bitmapSize / 2f, centerY + bitmapSize / 2f)
        canvas.drawBitmap(bitmap, null, bitmapRect, null)

        val textBounds = Rect()
        textPaint.getTextBounds(value.toString(), 0, value.toString().length, textBounds)

        val textX = centerX
        val textY = centerY + textBounds.height() / 2f

        canvas.drawText(value.toString(), textX, textY, textPaint)
    }
     */
}

fun Int.dpToPx(context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        context.resources.displayMetrics
    ).toInt()
}

fun Float.spToPx(context: Context): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this,
        context.resources.displayMetrics
    )
}

fun Float.dpToPx(context: Context): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        context.resources.displayMetrics
    )
}

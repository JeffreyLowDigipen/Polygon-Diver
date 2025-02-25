package com.example.projectpolygondiver.Project_Files.Util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.projectpolygondiver.GameObjects.Skills
import com.example.projectpolygondiver.R

/*
class ArrowDrawView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private var arrowSpacing: Float = 50f // Default spacing
    private var arrowSize: Float = 150f   // Default size
    private var arrowTopMargin: Float = 100f // Default top margin

    private lateinit var skills: Skills
    private val arrowSequence = mutableListOf<String>()

    private val arrowPaint = Paint().apply {
        color = Color.GRAY
        strokeWidth = 10f
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val highlightPaint = Paint().apply {
        color = Color.GREEN
        strokeWidth = 10f
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.ArrowDrawView, 0, 0).apply {
            try {
                arrowSpacing = getDimension(R.styleable.ArrowDrawView_arrowSpacing, 50f)
                arrowSize = getDimension(R.styleable.ArrowDrawView_arrowSize, 150f)
                arrowTopMargin = getDimension(R.styleable.ArrowDrawView_arrowTopMargin, 100f)
            } finally {
                recycle()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (arrowSequence.isEmpty()) return // If no sequence, don't draw

        val totalWidth = (arrowSize * arrowSequence.size) + (arrowSpacing * (arrowSequence.size - 1))
        val startX = (width - totalWidth) / 2f // Center the sequence horizontally
        val yPosition = arrowTopMargin // Draw from the top margin

        // ✅ Draw arrows based on the generated sequence
        arrowSequence.forEachIndexed { index, direction ->
            val xPosition = startX + index * (arrowSize + arrowSpacing)
            drawArrow(canvas, xPosition, yPosition, direction, arrowSize)
        }
    }

    private fun drawArrow(canvas: Canvas, x: Float, y: Float, direction: String, size: Float) {
        val paint = if (highlightedArrows.contains(direction)) highlightPaint else arrowPaint

        when (direction) {
            "up" -> {
                canvas.drawLine(x, y, x, y - size, paint)
                canvas.drawLine(x, y - size, x - size / 4, y - size / 1.5f, paint)
                canvas.drawLine(x, y - size, x + size / 4, y - size / 1.5f, paint)
            }
            "down" -> {
                canvas.drawLine(x, y, x, y + size, paint)
                canvas.drawLine(x, y + size, x - size / 4, y + size / 1.5f, paint)
                canvas.drawLine(x, y + size, x + size / 4, y + size / 1.5f, paint)
            }
            "left" -> {
                canvas.drawLine(x, y, x - size, y, paint)
                canvas.drawLine(x - size, y, x - size / 1.5f, y - size / 4, paint)
                canvas.drawLine(x - size, y, x - size / 1.5f, y + size / 4, paint)
            }
            "right" -> {
                canvas.drawLine(x, y, x + size, y, paint)
                canvas.drawLine(x + size, y, x + size / 1.5f, y - size / 4, paint)
                canvas.drawLine(x + size, y, x + size / 1.5f, y + size / 4, paint)
            }
        }
    }
}
*/

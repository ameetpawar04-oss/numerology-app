package com.amietppawar.numerology.ui.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import androidx.core.content.FileProvider
import com.amietppawar.numerology.AppConfig
import java.io.File
import java.io.FileOutputStream
import kotlin.math.cos
import kotlin.math.sin

/**
 * Creates a shareable image of a name result, entirely on the device.
 * Only the name and numbers the user chose to share appear on it.
 */
object ShareUtils {

    private val VIOLET = 0xFF7B5CFF.toInt()
    private val CYAN = 0xFF3EE6FF.toInt()
    private val GOLD = 0xFFD9B45A.toInt()
    private val INK = 0xFF07060F.toInt()
    private val PANEL = 0xFF1A1140.toInt()
    private val PALE = 0xFFECE9F7.toInt()
    private val MUTED = 0xFFC3BDD9.toInt()

    fun createResultCard(
        name: String,
        compound: Int,
        single: Int,
        letterValues: List<Int>
    ): Bitmap {
        val width = 1080
        val height = 1350
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(INK)

        val panelPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        panelPaint.color = PANEL
        canvas.drawRoundRect(60f, 60f, width - 60f, height - 60f, 48f, 48f, panelPaint)

        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = 3f
        borderPaint.color = GOLD
        canvas.drawRoundRect(60f, 60f, width - 60f, height - 60f, 48f, 48f, borderPaint)

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        textPaint.textAlign = Paint.Align.CENTER

        // Name, shrunk to fit if it is long
        textPaint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        textPaint.color = PALE
        var nameSize = 76f
        textPaint.textSize = nameSize
        val shownName = name.trim().uppercase()
        while (nameSize > 30f && textPaint.measureText(shownName) > width - 200f) {
            nameSize -= 4f
            textPaint.textSize = nameSize
        }
        canvas.drawText(shownName, width / 2f, 220f, textPaint)

        // Sigil
        val centerX = width / 2f
        val centerY = 590f
        val radius = 230f
        val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        ringPaint.style = Paint.Style.STROKE
        ringPaint.strokeWidth = 4f
        ringPaint.color = GOLD
        canvas.drawCircle(centerX, centerY, radius * 1.12f, ringPaint)

        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        linePaint.style = Paint.Style.STROKE
        linePaint.strokeWidth = 7f
        linePaint.strokeCap = Paint.Cap.ROUND
        linePaint.color = CYAN

        val count = letterValues.size
        var lastX = 0f
        var lastY = 0f
        letterValues.forEachIndexed { index, value ->
            val shrink = 1f - 0.6f * index.toFloat() / count.toFloat()
            val angle = Math.toRadians(-90.0 + (value - 1) * 45.0)
            val pointX = centerX + radius * shrink * cos(angle).toFloat()
            val pointY = centerY + radius * shrink * sin(angle).toFloat()
            if (index > 0) {
                canvas.drawLine(lastX, lastY, pointX, pointY, linePaint)
            } else {
                val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG)
                dotPaint.color = GOLD
                canvas.drawCircle(pointX, pointY, 14f, dotPaint)
            }
            lastX = pointX
            lastY = pointY
        }

        // Numbers
        textPaint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        textPaint.textSize = 38f
        textPaint.color = MUTED
        canvas.drawText("COMPOUND", width * 0.30f, 960f, textPaint)
        canvas.drawText("SINGLE", width * 0.70f, 960f, textPaint)

        textPaint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        textPaint.textSize = 150f
        textPaint.color = CYAN
        canvas.drawText(compound.toString(), width * 0.30f, 1110f, textPaint)
        textPaint.color = GOLD
        canvas.drawText(single.toString(), width * 0.70f, 1110f, textPaint)

        // Footer
        textPaint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        textPaint.textSize = 34f
        textPaint.color = VIOLET
        canvas.drawText("Chaldean name number", width / 2f, 1200f, textPaint)
        textPaint.color = MUTED
        canvas.drawText(AppConfig.PRACTICE_NAME, width / 2f, 1250f, textPaint)

        return bitmap
    }

    /** Opens the system share sheet with the image. Returns false if it could not. */
    fun shareBitmap(context: Context, bitmap: Bitmap): Boolean {
        return try {
            val folder = File(context.cacheDir, "share")
            folder.mkdirs()
            val file = File(folder, "numerology_result.png")
            FileOutputStream(file).use { stream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            }
            val uri = FileProvider.getUriForFile(
                context,
                context.packageName + ".fileprovider",
                file
            )
            val send = Intent(Intent.ACTION_SEND)
            send.type = "image/png"
            send.putExtra(Intent.EXTRA_STREAM, uri)
            send.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val chooser = Intent.createChooser(send, "Share result")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
            true
        } catch (e: Exception) {
            false
        }
    }
}

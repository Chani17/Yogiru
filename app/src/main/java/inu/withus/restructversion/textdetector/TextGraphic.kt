package inu.withus.restructversion.textdetector


import android.content.Intent
import android.content.Intent.*
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.Log
import inu.withus.restructversion.GraphicOverlay.Graphic
import com.google.mlkit.vision.text.Text
import inu.withus.restructversion.GraphicOverlay
import inu.withus.restructversion.RegisterFoodActivity
import kotlin.collections.HashMap
import kotlin.math.max
import kotlin.math.min
import kotlin.math.tan

/**
 * Graphic instance for rendering TextBlock position, size, and ID within an associated graphic
 * overlay view.
 */
class TextGraphic
constructor(
    overlay: GraphicOverlay?,
    private val text: Text,
//    private val shouldGroupTextInBlocks: Boolean,
    private val showLanguageTag: Boolean,
    private var expireDateCount: HashMap<String, Int>,
    private var foodName: HashMap<String, Float>,
) : Graphic(overlay) {

    private val rectPaint: Paint = Paint()
    private val textPaint: Paint
    private val labelPaint: Paint
    private var maxCount: String? = ""
    private var maxHeight: String? = ""
    private var info: String = ""
    private val datePattern =
        Regex("^([0-9]*(.|,|-|년))*\\s*(0[1-9]|1[012])(.|,|-|월)\\s*(0[1-9]|[12][0-9]|3[01])(.|,|-|일)?(\\S[가-힣0-9a-zA-Z])?$")
    private val namePattern = Regex("^[가-힣]*$")
    val intent = Intent(applicationContext, RegisterFoodActivity::class.java)

    init {
        rectPaint.color = MARKER_COLOR
        rectPaint.style = Paint.Style.STROKE
        rectPaint.strokeWidth = STROKE_WIDTH
        textPaint = Paint()
        textPaint.color = TEXT_COLOR
        textPaint.textSize = TEXT_SIZE
        labelPaint = Paint()
        labelPaint.color = MARKER_COLOR
        labelPaint.style = Paint.Style.FILL
        // Redraw the overlay, as this graphic has been added.
        postInvalidate()
    }

    /** Draws the text block annotations for position, size, and raw value on the supplied canvas. */
    override fun draw(canvas: Canvas?) {
        for (textBlock in text.textBlocks) { // Renders the text at the bottom of the box.
            Log.d(TAG, "Textblock text is: " + textBlock.text)

            // 유통기한 필요한 부분만 가져오기
            if (datePattern.matches(textBlock.text)) {

                info = if (!textBlock.text[0].isDigit()) {
                    // 인식된 유통기한의 첫글자가 숫자가 아니라면
                    textBlock.text.substring(6, textBlock.text.length)
                } else {
                    // 인식된 유통기한의 첫글자가 숫자라면
                    textBlock.text
                }
                // 공백 제거
                info = info.replace(" ", "")
                // 인식된 유통기한 필요한 부분만 가져오기
                val expireDate = getExpireDate(info)

                // HashMap에 저장 단계
                if (expireDateCount.containsKey(expireDate)) {
                    // 유통기한이 이미 HashMap에 있다면 +1
                    expireDateCount[expireDate] =
                        (expireDateCount[expireDate] ?: 0) + 1
                } else {
                    // 유통기한이 이미 HashMap에 없다면 추가
                    expireDateCount[expireDate] = 1
                }
            } else if (namePattern.matches(textBlock.text)) {
                // 이 부분 변경 필요성
                // theta값으로 식품명 가져오기
                val theta = drawText(
                    getFormattedText(textBlock.text, textBlock.recognizedLanguage),
                    RectF(textBlock.boundingBox),
                    TEXT_SIZE * textBlock.lines.size + 2 * STROKE_WIDTH,
                    canvas!!
                )
                Log.d(TAG, "theta : $theta")

                // theta값이 HashMap에 없다면 추가해라!
                if (!(foodName.containsValue(theta))) {
                    foodName[textBlock.text] = theta
                    Log.d(TAG, "foodName text : " + textBlock.text)
                    Log.d(TAG, "textBlock lines size : " + textBlock.lines.size)
                }
            }
        }
        // 저장된 유통기한 map들 중 가장 많은 count를 가져온다.
        maxCount = expireDateCount.maxByOrNull { it.value }?.key
        Log.d(TAG, "maxCount : $maxCount")
        intent.putExtra("expireDate", maxCount)
        Log.d(TAG, "유통기한 : $expireDateCount")

        // 저장된 식품명 인식된 map들 중 theta값이 가장 큰 값을 가져온다.
        maxHeight = foodName.maxByOrNull { it.value }?.key
        Log.d(TAG, "maxHeight : $maxHeight")
        intent.putExtra("foodName", maxHeight)
        Log.d(TAG, "식품명 : $foodName")

        if ((maxCount != null) && (maxHeight != null)) {
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            Log.d(TAG, "StartActivity로 들어갈거임")
            applicationContext.startActivity(
                intent.addFlags(
                    FLAG_ACTIVITY_NEW_TASK
                )
            )
        }
    }

    // 인식된 유통기한 필요한 부분만 가져오기 위한 중간단계
    private fun getExpireDate(text: String): String {
        Log.d(TAG, "text = $text")
        try {
            when {
                text.length == 8 -> {
                    // 유통기한이 22.04.24와 같은 형식으로 들어올 경우
                    info = "20$text"
                }
                text[0] == '2' -> {
                    // 유통기한 앞글자가 2로 시작한다면
                    info = text.substring(0, 10)
                }
            }
        } catch (e : StringIndexOutOfBoundsException) {

        }

        // 날짜 format을 YY-MM-DD형식으로 변환
        info = info.replace("." , "-")
        info = info.replace("," , "-")
        return info
    }

    private fun getFormattedText(text: String, languageTag: String): String {
        if (showLanguageTag) {
            return String.format(
                TEXT_WITH_LANGUAGE_TAG_FORMAT,
                languageTag,
                text
            )
        }
        return text
    }

    private fun drawText(text: String, rect: RectF, textHeight: Float, canvas: Canvas): Float {
        // If the image is flipped, the left will be translated to right, and the right to left.
        val x0 = translateX(rect.left)
        val x1 = translateX(rect.right)
        rect.left = min(x0, x1)
        rect.right = max(x0, x1)
        rect.top = translateY(rect.top)
        rect.bottom = translateY(rect.bottom)
//        canvas.drawRect(rect, rectPaint)
        val textWidth = textPaint.measureText(text)
//        canvas.drawRect(
//            rect.left - STROKE_WIDTH,
//            rect.top - textHeight,
//            rect.left + textWidth + 2 * STROKE_WIDTH,
//            rect.top,
//            labelPaint
//        )
        return textHeight
        // Renders the text at the bottom of the box.
//        canvas.drawText(text, rect.left, rect.top - STROKE_WIDTH, textPaint)
    }

    companion object {
        private const val TAG = "TextGraphic"
        private const val TEXT_WITH_LANGUAGE_TAG_FORMAT = "%s:%s"
        private const val TEXT_COLOR = Color.BLACK
        private const val MARKER_COLOR = Color.WHITE
        private const val TEXT_SIZE = 54.0f
        private const val STROKE_WIDTH = 4.0f

    }

}

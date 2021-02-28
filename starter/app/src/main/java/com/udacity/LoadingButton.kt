package com.udacity

import android.animation.AnimatorInflater
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.withStyledAttributes
import java.util.*
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 100
    private var heightSize = 50
    private var bgColor: Int = 0
    private var bgColorLoading: Int = 0
    private var bgColorDisabled: Int = 0
    private var buttonDisabled: Boolean = false
    private var textColor: Int = Color.WHITE
    private var valueAnimator = ValueAnimator()
    @Volatile
    private var progress: Double = 0.0
    private val updateListener = ValueAnimator.AnimatorUpdateListener {
        progress = (it.animatedValue as Float).toDouble()

        invalidate()
        requestLayout()
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }


    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

    }


    init {
    isClickable = true

        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            bgColor = getColor(R.styleable.LoadingButton_button_color, 0)
            bgColorDisabled = getColor(R.styleable.LoadingButton_button_disable_color, 0)
            bgColorLoading = getColor(R.styleable.LoadingButton_button_loading_color, 0)
        }
        valueAnimator = AnimatorInflater.loadAnimator(context, R.animator.animation) as ValueAnimator
        valueAnimator.addUpdateListener(updateListener)

    }

    private val rect = RectF(
        740f,
        50f,
        810f,
        110f
    )


    @SuppressLint("ResourceAsColor")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //Drawing Button
        val width = width
        val height = height
        paint.color =
            if (buttonDisabled)
                bgColorDisabled
             else
            bgColor
        if (buttonState === ButtonState.Loading) {
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            paint.color = bgColorLoading
            canvas.drawRect(
                0f, 0f,
                (width * (progress / 100)).toFloat(), height.toFloat(), paint
            )
            paint.color = resources.getColor(R.color.colorAccent)
            canvas.drawArc(rect, 0f, (360 * (progress / 100)).toFloat(), true, paint)
        } else {
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        }

        // Drawing Text
        paint.color = textColor
        val textHeight = (height / 2 - (paint.descent() + paint.ascent()) / 2)
        val buttonLabel =
            if (buttonState === ButtonState.Loading)
                resources.getString(R.string.button_label_loading)
        else
                resources.getString(R.string.button_label)
        canvas.drawText(
            buttonLabel.toUpperCase(Locale.ROOT),
            (width / 2).toFloat(),
            textHeight,
            paint
        )

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    private fun buttonAnimation() {
        valueAnimator.start()
    }

    override fun performClick(): Boolean {
        super.performClick()
        if (buttonState === ButtonState.Completed) buttonState = ButtonState.Loading
        buttonAnimation()
        invalidate()
        return true
    }

    fun downloadCompleted() {
        valueAnimator.cancel()
        buttonState =  ButtonState.Completed
        invalidate()
        requestLayout()
    }

}
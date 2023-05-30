package com.dicoding.storyapp.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.dicoding.storyapp.R

class MyEdPassword : AppCompatEditText, View.OnTouchListener {
    private lateinit var visibleButtonImage: Drawable
    private var isVisible: Boolean = true

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        background = ContextCompat.getDrawable(context, R.drawable.ed_bg)

        maxLines = 1

        transformationMethod =
            if (isVisible) PasswordTransformationMethod.getInstance() else HideReturnsTransformationMethod.getInstance()

        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    private fun init() {
        visibleButtonImage =
            ContextCompat.getDrawable(context, R.drawable.ic_baseline_visibility_24) as Drawable

        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                if (s.toString().isNotEmpty()) {
                    hideVisibleButton()
                    if (s.length < 8) {
                        error = context.getString(R.string.valid_password)

                    } else showVisibleButton()
                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        })
    }

    private fun showVisibleButton() {
        setButtonDrawables(endOfTheText = visibleButtonImage)
    }

    private fun hideVisibleButton() {
        setButtonDrawables()
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val visibleButtonStart: Float
            val visibleButtonEnd: Float
            var isVisibleButtonClicked = false

            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                visibleButtonEnd = (visibleButtonImage.intrinsicWidth + paddingStart).toFloat()
                when {
                    event.x < visibleButtonEnd -> isVisibleButtonClicked = true
                }
            } else {
                visibleButtonStart =
                    (width - paddingEnd - visibleButtonImage.intrinsicWidth).toFloat()
                when {
                    event.x > visibleButtonStart -> isVisibleButtonClicked = true
                }
            }
            if (isVisibleButtonClicked) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        visibleButtonImage = ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_baseline_visibility_off_24
                        ) as Drawable
                        isVisible = false
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        visibleButtonImage = ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_baseline_visibility_24
                        ) as Drawable
                        isVisible = true
                        return true
                    }
                    else -> return false
                }
            } else return false
        }
        return false
    }
}
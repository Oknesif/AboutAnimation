package com.zzzombiecoder.aboutanimation.collapsing.layout

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.constraint.Guideline
import android.support.design.widget.FloatingActionButton
import android.support.v4.view.GestureDetectorCompat
import android.support.v4.view.ViewCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.Scroller
import android.widget.TextView
import com.zzzombiecoder.aboutanimation.R
import java.lang.Exception

class CollapsingConstraintLayout @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val scroller = Scroller(context)

    private lateinit var recyclerView: RecyclerView
    private lateinit var imageView: ImageView
    private lateinit var titleView: TextView
    private lateinit var subTitleView: TextView
    private lateinit var floatingActionButton: FloatingActionButton

    private lateinit var mainGuideline: Guideline
    private lateinit var imageGuideline: Guideline
    private lateinit var recyclerGuideline: Guideline

    fun setUp(
            mainInfoItem: InfoItem,
            infoItems: List<InfoItem>,
            onPhoneClickListener: (view: View) -> Unit,
            onInfoItemClickListener: (infoItem: InfoItem) -> Unit) {
        try {
            recyclerView = findViewById(R.id.recycler_view)
            titleView = findViewById(R.id.title_view)
            subTitleView = findViewById(R.id.subtitle_view)
            mainGuideline = findViewById(R.id.main_guideline)
            imageGuideline = findViewById(R.id.image_horizontal_guideline)
            recyclerGuideline = findViewById(R.id.recycler_guideline)

            imageView = findViewById(R.id.image_view)
            floatingActionButton = findViewById(R.id.fab)
            imageView.setImageURI(mainInfoItem.imageUri)
            titleView.text = mainInfoItem.text
            subTitleView.text = mainInfoItem.additionalText
            onPhoneClickListener.apply {
                floatingActionButton.setOnClickListener(this)
            }
            initAdapter(infoItems, onInfoItemClickListener)
        } catch (ex: Exception) {
            throw IllegalArgumentException("Probably wrong layout: ${Log.getStackTraceString(ex)}")
        }
        setOnTouchListener(touchListener)
    }

    private val touchListener: View.OnTouchListener = object : View.OnTouchListener {
        private val onGestureListener =
                object : GestureDetector.SimpleOnGestureListener() {
                    override fun onFling(e1: MotionEvent?, e2: MotionEvent?,
                                         velocityX: Float, velocityY: Float
                    ): Boolean {
                        scroller.forceFinished(true)
                        val isCollapsing: Boolean = velocityY < 0
                        if (shouldUpdateGuidelines(isCollapsing)) {
                            animateRecyclerItems(isCollapsing)
                            val fromValue = if (isCollapsing) scrollerDistance else 0
                            val dX = if (isCollapsing) -scrollerDistance else scrollerDistance
                            scroller.startScroll(0, fromValue, 0, dX, ANIMATION_DURATION)
                            ViewCompat.postInvalidateOnAnimation(this@CollapsingConstraintLayout)
                        }
                        return true
                    }

                    override fun onDown(e: MotionEvent?): Boolean {
                        scroller.forceFinished(true)
                        return true
                    }
                }

        private val gestureDetector = GestureDetectorCompat(context, onGestureListener)

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            return gestureDetector.onTouchEvent(event)
        }
    }

    override fun invalidate() {
        if (scroller.isFinished.not()) {
            scroller.computeScrollOffset()
            setConstraintsByScroller(scroller.currY)
            ViewCompat.postInvalidateOnAnimation(this)
        }
        super.invalidate()
    }

    private fun shouldUpdateGuidelines(isCollapsing: Boolean): Boolean {
        val fault = 0.1f
        val layoutParams = mainGuideline.layoutParams as ConstraintLayout.LayoutParams
        return layoutParams.guidePercent > collapsedMainGuidelineValue + fault && isCollapsing
                || layoutParams.guidePercent < expandGuidelineValue - fault && isCollapsing.not()
    }

    private fun setConstraintsByScroller(partialValue: Int) {
        val mainGuidelineValue = collapsedMainGuidelineValue +
                ((expandGuidelineValue - collapsedMainGuidelineValue) * partialValue / scrollerDistance)
        val imageGuidelineValue = collapsedImageGuidelineValue +
                ((expandGuidelineValue - collapsedImageGuidelineValue) * partialValue / scrollerDistance)
        val recyclerGuidelineValue = collapsedRecyclerGuidelineValue +
                ((expandRecyclerGuidelineValue - collapsedRecyclerGuidelineValue) * partialValue / scrollerDistance)
        mainGuideline.setPercentage(mainGuidelineValue)
        imageGuideline.setPercentage(imageGuidelineValue)
        recyclerGuideline.setPercentage(recyclerGuidelineValue)

        val biasValue = 0.5f * partialValue / scrollerDistance
        subTitleView.setHorizontalBias(biasValue)
    }

    private fun View.setHorizontalBias(percentage: Float) {
        val layoutParams = this.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.horizontalBias = percentage
        this.layoutParams = layoutParams
    }

    private fun Guideline.setPercentage(percentage: Float) {
        val layoutParams = this.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.guidePercent = percentage
        this.layoutParams = layoutParams
    }

    private fun initAdapter(
            list: List<InfoItem>,
            onInfoItemClickListener: (infoItem: InfoItem) -> Unit) {
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = InfoItemsAdapter(
                list = list,
                context = context,
                onItemClickListener = onInfoItemClickListener)
    }

    private fun animateRecyclerItems(isCollapsing: Boolean) {
        val adapter = recyclerView.adapter as InfoItemsAdapter
        adapter.animateItems(isCollapsing)
    }
}

const val ANIMATION_DURATION = 1000

private val scrollerDistance = 1000
private val collapsedMainGuidelineValue: Float = 0.35f
private val collapsedRecyclerGuidelineValue: Float = 0.35f
private val expandRecyclerGuidelineValue: Float = 0.8f
private val expandGuidelineValue: Float = 1f
private val collapsedImageGuidelineValue: Float = 0.7f
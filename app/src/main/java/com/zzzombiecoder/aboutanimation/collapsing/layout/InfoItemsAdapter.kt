package com.zzzombiecoder.aboutanimation.collapsing.layout

import android.animation.ValueAnimator
import android.content.Context
import android.support.v4.view.ViewCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.zzzombiecoder.aboutanimation.R

class InfoItemsAdapter(
        context: Context,
        private val list: List<InfoItem>,
        onItemClickListener: (infoItem: InfoItem) -> Unit
) : RecyclerView.Adapter<ItemViewHolder>() {

    private val clickListener = { position: Int ->
        onItemClickListener(list[position])
    }

    private var currentAnimationFraction: Float = 0f
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    private val margin: Int by lazy { context.resources.getDimensionPixelSize(R.dimen.card_view_bottom_margin) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = layoutInflater.inflate(R.layout.info_item_view, parent, false)
        return ItemViewHolder(view, clickListener)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = list[position]
        holder.imageView.setImageURI(item.imageUri)
        holder.titleView.text = item.text
        holder.contentView.text = item.additionalText
        val bottomMargin = getBottomMarginByFraction(currentAnimationFraction)
        val rightMargin = if (position == list.lastIndex) margin else 0
        holder.cardView.setBottomMargin(bottomMargin)
        holder.cardView.setRightMargin(rightMargin)
        ViewCompat.postInvalidateOnAnimation(holder.cardView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            animateViewHolder(holder)
        }
        super.onBindViewHolder(holder, position, payloads)
    }

    private fun animateViewHolder(holder: ItemViewHolder) {
        val bottomMargin = getBottomMarginByFraction(currentAnimationFraction)
        Log.d("bottomMargin", "It is: $bottomMargin")
        holder.cardView.setBottomMargin(bottomMargin)
        ViewCompat.postInvalidateOnAnimation(holder.cardView)
    }

    private fun getBottomMarginByFraction(fraction: Float): Int {
        return (margin * 7 * fraction - 6 * margin).toInt()
    }

    private fun CardView.setRightMargin(margin: Int) {
        val cardLayoutParams = this.layoutParams as FrameLayout.LayoutParams
        cardLayoutParams.rightMargin = margin
        this.layoutParams = cardLayoutParams
    }

    private fun CardView.setBottomMargin(margin: Int) {
        val cardLayoutParams = this.layoutParams as FrameLayout.LayoutParams
        cardLayoutParams.bottomMargin = margin
        this.layoutParams = cardLayoutParams
    }

    fun animateItems(isCollapsing: Boolean) {
        val toValue = if (isCollapsing) 1f else 0f
        ValueAnimator.ofFloat(currentAnimationFraction, toValue).apply {
            duration = ANIMATION_DURATION.toLong() / 2
            addUpdateListener {
                val value = it.animatedValue as Float
                currentAnimationFraction = value
                for (i in 0 until itemCount) {
                    notifyItemChanged(i, value)
                }
            }
        }.start()
    }
}

class ItemViewHolder(
        view: View,
        clickListener: (position: Int) -> Unit
) : RecyclerView.ViewHolder(view) {
    val cardView: CardView = view.findViewById(R.id.cart_view)
    val imageView: ImageView = view.findViewById(R.id.image_view)
    val titleView: TextView = view.findViewById(R.id.title_view)
    val contentView: TextView = view.findViewById(R.id.content_view)

    init {
        view.findViewById<View>(R.id.container_layout).setOnClickListener {
            clickListener(adapterPosition)
        }
    }
}
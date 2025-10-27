package com.arrive.terminal.core.ui.view.recyclerview.decorator;

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.arrive.terminal.core.ui.utils.safe

class SpaceDividerDecorator(
    private val divider: Drawable? = null,
    private val dividerPaddingLeft: Int = 0,
    private val dividerPaddingRight: Int = 0,
    private val drawZero: Boolean = false,
    private val drawLast: Boolean = false,
    private val onDrawDivider: (previous: Any?, current: Any?, next: Any?) -> Boolean = { _, _, _ -> divider != null },
    private val onDrawSpace: (previous: Any?, current: Any?, next: Any?) -> SpaceConfig = { _, _, _ -> SpaceConfig.EMPTY }
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        parent.adapter?.let { notNullAdapter ->
            val currentViewHolder = parent.getChildViewHolder(view)
            val (previous, current, next) = Triple(
                notNullAdapter.tryToGetItem(currentViewHolder.bindingAdapterPosition - 1),
                notNullAdapter.tryToGetItem(currentViewHolder.bindingAdapterPosition),
                notNullAdapter.tryToGetItem(currentViewHolder.bindingAdapterPosition + 1)
            )

            val spaceConfig = onDrawSpace(previous, current, next)
            outRect.top = spaceConfig.topSpace
            outRect.bottom = spaceConfig.bottomSpace
            outRect.left = spaceConfig.leftSpace
            outRect.right = spaceConfig.rightSpace
        }
    }

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(canvas, parent, state)
        parent.children.forEachIndexed { index, view ->
            parent.adapter?.let { notNullAdapter ->
                if (index < parent.childCount - 1 || drawLast) {
                    draw(canvas, view, parent, notNullAdapter)
                }
            }
        }
    }

    private fun draw(canvas: Canvas, view: View, parent: RecyclerView, adapter: RecyclerView.Adapter<*>) {
        val currentViewHolder = parent.getChildViewHolder(view)
        val (previous, current, next) = Triple(
            adapter.tryToGetItem(currentViewHolder.bindingAdapterPosition - 1),
            adapter.tryToGetItem(currentViewHolder.bindingAdapterPosition),
            adapter.tryToGetItem(currentViewHolder.bindingAdapterPosition + 1)
        )

        val spaceConfig = onDrawSpace.invoke(previous, current, next)
        when {
            drawZero && previous == null && currentViewHolder != null -> {
                drawDivider(canvas, getDrawableRectForTop(view, parent, spaceConfig))
            }
            drawLast && current != null && next == null -> {
                drawDivider(canvas, getDrawableRectForBottom(view, parent, spaceConfig))
            }
        }

        if (onDrawDivider.invoke(previous, current, next)) {
            drawDivider(canvas, getDrawableRectForBottom(view, parent, spaceConfig))
        }
    }

    private fun drawDivider(canvas: Canvas, rect: Rect) {
        divider?.setBounds(rect.left, rect.top, rect.right, rect.bottom)
        divider?.draw(canvas)
    }

    private fun getDrawableRectForTop(
        view: View,
        parent: RecyclerView,
        spaceConfig: SpaceConfig,
    ): Rect {
        val params = view.layoutParams as RecyclerView.LayoutParams
        val top = view.top - params.topMargin - spaceConfig.topSpace
        return Rect(
            /* left = */ parent.paddingLeft + dividerPaddingLeft,
            /* top = */ top,
            /* right = */ parent.width - parent.paddingRight - dividerPaddingRight,
            /* bottom = */ top + (divider?.intrinsicHeight ?: 0)
        )
    }

    private fun getDrawableRectForBottom(
        view: View,
        parent: RecyclerView,
        spaceConfig: SpaceConfig,
    ): Rect {
        val params = view.layoutParams as RecyclerView.LayoutParams
        val top = view.bottom + params.bottomMargin + spaceConfig.bottomSpace
        return Rect(
            /* left = */ parent.paddingLeft + dividerPaddingLeft,
            /* top = */ top,
            /* right = */ parent.width - parent.paddingRight - dividerPaddingRight,
            /* bottom = */ top + (divider?.intrinsicHeight ?: 0)
        )
    }

    private fun RecyclerView.Adapter<*>.tryToGetItem(position: Int) = safe {
        val adapter = this as? AsyncListDifferDelegationAdapter<*>
        adapter?.items?.getOrNull(position)
    }
}
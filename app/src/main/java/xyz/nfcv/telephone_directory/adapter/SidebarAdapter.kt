package xyz.nfcv.telephone_directory.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import xyz.nfcv.telephone_directory.databinding.SidebarItemBinding
import xyz.nfcv.telephone_directory.model.Header
import android.view.*
import androidx.core.view.children
import androidx.core.view.forEachIndexed
import androidx.core.view.iterator
import xyz.nfcv.telephone_directory.adapter.SidebarAdapter.Companion.inTouchPoint

@SuppressLint("ClickableViewAccessibility")
class SidebarAdapter(
    private val recycler: RecyclerView,
    private val listener: (Int, Header) -> Unit
) : RecyclerView.Adapter<SidebarAdapter.ViewHolder>() {

    private val data = Header.values()
    private var point = Pair<Float, Float>(0f, 0f)

    private var selected = 0
        set(value) {
            val index = when {
                value < 0 -> 0
                value > data.lastIndex -> data.lastIndex
                else -> value
            }

            field = index

            recycler.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)

            notifyItemRangeChanged(0, itemCount)
        }

    init {
        recycler.adapter = this

        recycler.layoutManager = LinearLayoutManager(recycler.context).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
        recycler.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    point = event.rawX to event.rawY
                }

                MotionEvent.ACTION_MOVE -> {
                    val p = event.rawX to event.rawY
                    if (point.second - p.second > 20) {
                        selected--
                        point = event.rawX to event.rawY
                    } else if (p.second - point.second > 20) {
                        selected++
                        point = event.rawX to event.rawY
                    }
                }

                MotionEvent.ACTION_UP -> {
                    point = 0f to 0f
                }
            }
            return@setOnTouchListener false
        }

    }

    inner class ViewHolder(binding: SidebarItemBinding, listener: (Int, Header) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        val item = binding.sidebarItem

        init {
            binding.root.setOnClickListener {
                listener(layoutPosition, data[layoutPosition])
                selected = layoutPosition
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            SidebarItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), listener = listener
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val header = data[position]
        if (header == Header.LIKE) {
            holder.item.text = "♡"
        } else {
            holder.item.text = data[position].value
        }
        if (position == selected) {
            holder.item.setTextColor(Color.BLUE)
        } else {
            holder.item.setTextColor(Color.GRAY)
        }
    }

    override fun getItemCount(): Int = data.size

    companion object {
        private fun View.inTouchPoint(x: Int, y: Int): Boolean {
            val location = IntArray(2)
            this.getLocationOnScreen(location)
            val left = location[0]
            val top = location[1]
            val right: Int = left + this.measuredWidth
            val bottom: Int = top + this.measuredHeight
            return y in top..bottom && x in left..right
        }
    }
}
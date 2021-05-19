package xyz.nfcv.telephone_directory.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEachIndexed
import androidx.core.view.iterator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import xyz.nfcv.telephone_directory.databinding.SidebarItemBinding
import xyz.nfcv.telephone_directory.model.Header

class SidebarAdapter(
    private val recycler: RecyclerView,
    private val listener: (Int, Header) -> Unit
) : RecyclerView.Adapter<SidebarAdapter.ViewHolder>() {

    private val data = Header.values()

    private var selected = 0
        set(value) {
            field = value
            notifyItemRangeChanged(0, itemCount)
        }

    init {
        recycler.adapter = this

        recycler.layoutManager = LinearLayoutManager(recycler.context).apply {
            orientation = LinearLayoutManager.VERTICAL
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
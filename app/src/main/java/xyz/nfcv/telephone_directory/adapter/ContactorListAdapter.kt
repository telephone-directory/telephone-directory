package xyz.nfcv.telephone_directory.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.ImageView
import android.widget.TextView
import xyz.nfcv.telephone_directory.databinding.ContactChildPeopleBinding
import xyz.nfcv.telephone_directory.databinding.ContactGroupHeaderBinding
import xyz.nfcv.telephone_directory.model.Person
import xyz.nfcv.widget.Header
import xyz.nfcv.widget.RoundImageView
import java.util.*
import kotlin.math.abs


class ContactorListAdapter(
    private var context: Context,
    private val list: ExpandableListView
) : BaseExpandableListAdapter() {
    private val data = ArrayList<PeopleGroup>()

    fun update(data: Collection<PeopleGroup>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()

        for (i in 0 until groupCount) {
            list.expandGroup(i)
        }
    }

    init {
        list.setAdapter(this)
        list.setOnGroupClickListener { _, _, _, _ -> true }

        for (i in 0 until groupCount) {
            list.expandGroup(i)
        }
    }

    companion object {
        data class PeopleGroup(val header: Header, val people: List<Person>) {
            operator fun minus(other: PeopleGroup): Int {
                return header - other.header
            }
        }

        class GroupViewHolder {
            lateinit var header: TextView
        }

        class ChildViewHolder {
            lateinit var avatar: RoundImageView
            lateinit var name: TextView
            lateinit var info: ImageView
        }

        fun String.ofBitmap(size: Int = 1000): Bitmap {
            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.drawBitmap(bitmap, 0f, 0f, null)
            canvas.drawColor(Color.rgb(0xdd, 0xdd, 0xdd))

            val paint = TextPaint(TextPaint.ANTI_ALIAS_FLAG).apply {
                textAlign = Paint.Align.CENTER
                textSize = size * 0.5f
                color = Color.WHITE
            }

            val offset = abs(paint.descent() + paint.ascent()) + size * 3 / 10f

            canvas.drawText(this, size / 2f, offset, paint)
            return bitmap
        }
    }

    override fun getGroupCount(): Int = data.size

    override fun getChildrenCount(groupPosition: Int): Int = data[groupPosition].people.size

    override fun getGroup(groupPosition: Int): PeopleGroup = data[groupPosition]

    override fun getChild(groupPosition: Int, childPosition: Int): Person =
        getGroup(groupPosition).people[childPosition]

    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()

    override fun hasStableIds(): Boolean = false

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val holder: GroupViewHolder
        val view: View
        if (convertView == null) {
            val binding =
                ContactGroupHeaderBinding.inflate(LayoutInflater.from(context), parent, false)
            view = binding.root
            holder = GroupViewHolder()
            holder.header = binding.contactGroupHeaderText
            view.tag = holder
        } else {
            view = convertView
            holder = convertView.tag as GroupViewHolder
        }

        holder.header.text = getGroup(groupPosition).header.value

        return view
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val view: View
        val holder: ChildViewHolder

        if (convertView == null) {
            val binding =
                ContactChildPeopleBinding.inflate(LayoutInflater.from(context), parent, false)
            view = binding.root
            holder = ChildViewHolder()
            holder.avatar = binding.contactChildPeopleAvatar
            holder.name = binding.contactChildPeopleName
            holder.info = binding.contactChildPeopleInfo
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ChildViewHolder
        }

        val person = getChild(groupPosition, childPosition)
        holder.name.text = person.name
        holder.avatar.setImageBitmap(person.last.ofBitmap())
        holder.info.visibility = View.GONE
        return view
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true

    fun contains(header: Header): Boolean {
        return data.count { it.header == header } > 0
    }

    fun near(header: Header): Header? {
        if (contains(header)) return header
        val before = data.lastOrNull { it.header.index < header.index }?.header
        val after = data.firstOrNull { it.header.index > header.index }?.header
        return if (after != null && before != null) {
            val diffBefore = abs(header.index - before.index)
            val diffAfter = abs(header.index - after.index)
            if (diffBefore > diffAfter) after else before
        } else if (after != null && before == null) {
            after
        } else if (after == null && before != null) {
            before
        } else {
            Log.wtf(javaClass.name, "怎么可以发生这样的事情！！！")
            data.firstOrNull()?.header
        }
    }

    fun scroll(header: Header) {
        val group = data.indexOfFirst { it.header == header }

        if (group >= 0) {
            list.setSelectedGroup(group)
        }
    }

    fun first(start: Int, last: Int): Header? {
        var position = 0
        data.forEach { group ->
            position += 1 + group.people.size
            val range = 0 until position
            if (start in range) {
                return group.header
            }
        }

        return null
    }

    private fun visible(start: Int, last: Int): ArrayList<Header> {
        var position = 0
        val range = start..last
        val headers = ArrayList<Header>()
        data.forEach { group ->
            if (position in range) {
                headers.add(group.header)
            }
            position += 1 + group.people.size
        }
        return headers
    }
}
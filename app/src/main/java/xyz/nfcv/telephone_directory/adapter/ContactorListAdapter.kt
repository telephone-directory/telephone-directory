package xyz.nfcv.telephone_directory.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import xyz.nfcv.telephone_directory.databinding.ContactChildPeopleBinding
import xyz.nfcv.telephone_directory.databinding.ContactGroupHeaderBinding
import xyz.nfcv.telephone_directory.model.Header
import xyz.nfcv.telephone_directory.model.Person
import xyz.nfcv.telephone_directory.widget.RoundAngleImageView
import java.util.*
import kotlin.math.abs


class ContactorListAdapter(
    private var context: Context,
    private val list: ExpandableListView
) :
    BaseExpandableListAdapter() {
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
            lateinit var avatar: RoundAngleImageView
            lateinit var name: TextView
            lateinit var info: ImageView
        }

        fun getBitmapByChar(text: Char, size: Int = 1000): Bitmap {
            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.drawBitmap(bitmap, 0f, 0f, null)
            canvas.drawColor(Color.rgb(0xdd, 0xdd, 0xdd))

            val paint = TextPaint(TextPaint.ANTI_ALIAS_FLAG).apply {
                textSize = size * 0.5f
                color = Color.WHITE
            }

            val layout = StaticLayout.Builder
                .obtain("$text", 0, 1, paint, size)
                .setAlignment(Layout.Alignment.ALIGN_CENTER)
                .build()

            canvas.translate(0f, abs(paint.descent() + paint.ascent()) * 0.5f - size / 25f)
            layout.draw(canvas)
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
        holder.avatar.setImageBitmap(getBitmapByChar(person.name.last()))
        holder.info.visibility = View.GONE
        return view
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true
}
package xyz.nfcv.telephone_directory.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import xyz.nfcv.telephone_directory.adapter.ContactorListAdapter.Companion.PeopleGroup
import xyz.nfcv.telephone_directory.databinding.ContactChildPeopleBinding
import xyz.nfcv.telephone_directory.model.PeopleGroupData
import xyz.nfcv.telephone_directory.widget.RoundAngleImageView

class ContactorRecyclerAdapter(private val recycler: RecyclerView) :
    RecyclerView.Adapter<ContactorRecyclerAdapter.ViewHolder>() {

    init {
        recycler.layoutManager = LinearLayoutManager(recycler.context).apply {
            orientation = LinearLayoutManager.VERTICAL
        }

        recycler.addItemDecoration(ContactorHeaderDecoration(this))
        recycler.adapter = this
    }

    val data = PeopleGroupData(listOf())

    @SuppressLint("NotifyDataSetChanged")
    fun update(data: Collection<PeopleGroup>) {
        this.data.update(data.toList())
        notifyDataSetChanged()
    }

    inner class ViewHolder(binding: ContactChildPeopleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val avatar: RoundAngleImageView = binding.contactChildPeopleAvatar
        val name: TextView = binding.contactChildPeopleName
        val info: ImageView = binding.contactChildPeopleInfo
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ContactChildPeopleBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val person = data[position]
        holder.name.text = person.name
        holder.avatar.setImageBitmap(ContactorListAdapter.getBitmapByChar(person.name.last()))
        holder.info.visibility = View.GONE
    }

    override fun getItemCount(): Int {
        return data.size
    }
}
package xyz.nfcv.telephone_directory.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import xyz.nfcv.telephone_directory.adapter.ContactorListAdapter.Companion.ofBitmap
import xyz.nfcv.telephone_directory.adapter.SearchContactorAdapter.ViewHolder
import xyz.nfcv.telephone_directory.databinding.ContactChildPeopleBinding
import xyz.nfcv.telephone_directory.model.Person

class SearchContactorAdapter(private val context: Context, private val recycler: RecyclerView, private val listener: (Int, Person) -> Unit) : RecyclerView.Adapter<ViewHolder>() {
    private val people = ArrayList<Person>()
    init {
        recycler.layoutManager = LinearLayoutManager(context).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
        recycler.adapter = this
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update(keyword: String) {
        people.clear()
        people.addAll(Person.likeName(context, keyword))
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ContactChildPeopleBinding.inflate(inflater, parent, false)
        return ViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val person = people[position]
        holder.avatar.setImageBitmap(person.last.ofBitmap())
        holder.name.text = person.name
    }

    override fun getItemCount(): Int = people.size

    inner class ViewHolder(
        binding: ContactChildPeopleBinding,
        listener: (Int, Person) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        val avatar = binding.contactChildPeopleAvatar
        val name = binding.contactChildPeopleName
        private val info = binding.contactChildPeopleInfo

        init {
            info.visibility = View.GONE
            binding.root.setOnClickListener { listener(layoutPosition, people[layoutPosition]) }
        }
    }
}
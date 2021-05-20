package xyz.nfcv.telephone_directory.model

import xyz.nfcv.telephone_directory.adapter.ContactorListAdapter.Companion.PeopleGroup

class PeopleGroupData(var groups: List<PeopleGroup>) {
    operator fun get(index: Int): Person {
        var size = 0
        for (group in groups) {
            size += group.people.size
            if (size > index) {
                return group.people[size - index - 1]
            }
        }

        throw ArrayIndexOutOfBoundsException()
    }

    fun update(data: List<PeopleGroup>) {
        this.groups = data
    }

    fun first(person: Person): Boolean {
        for (group in groups) {
            if (person == group.people.firstOrNull()) {
                return true
            }
        }
        return false
    }

    fun last(person: Person): Boolean {
        for (group in groups) {
            if (person == group.people.lastOrNull()) {
                return true
            }
        }
        return false
    }

    fun first(index: Int): Boolean {
        var size = 0
        for (group in groups) {
            if (group.people.isNotEmpty() && index == size) {
                return true
            }
            size += group.people.size
        }

        return false
    }

    fun last(index: Int): Boolean {
        var size = 0
        for (group in groups) {
            if (group.people.isNotEmpty() && index == size + group.people.size - 1) {
                return true
            }
            size += group.people.size
        }

        return false
    }

    fun group(index: Int): PeopleGroup? {
        var size = 0
        for (group in groups) {
            if (index in size until group.people.size) {
                return group
            }
            size += group.people.size
        }
        return null
    }

    val size = groups.sumOf { it.people.size }
}
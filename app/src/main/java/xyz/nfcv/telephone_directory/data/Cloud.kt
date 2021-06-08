package xyz.nfcv.telephone_directory.data

import android.content.Context
import xyz.nfcv.telephone_directory.model.Person

class Cloud {
    enum class Operation {
        ADD, DELETE, UPDATE
    }

    data class Record(val person: Person, val operation: Operation)

    companion object {
        fun invalidate(context: Context, cloud: List<Record>) {
            for (record in cloud) {
                when (record.operation) {
                    Operation.ADD -> {
                        Person.insertCloud(context, record.person)
                    }
                    Operation.DELETE -> {
                        Person.deleteCloud(context, record.person)
                    }
                    Operation.UPDATE -> {
                        Person.updateCloud(context, record.person)
                    }
                }
            }

        }
    }
}
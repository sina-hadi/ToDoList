package com.codinginflow.todolist.fragments

import android.app.Application
import android.view.View
import android.widget.Spinner
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.codinginflow.todolist.R
import com.codinginflow.todolist.data.models.Priority
import com.codinginflow.todolist.data.models.ToDoData
import com.codinginflow.todolist.fragments.list.ListFragmentDirections
import com.google.android.material.floatingactionbutton.FloatingActionButton

class BindingAdapters {

    companion object {

        @BindingAdapter("android:navigateToAddFragment")
        @JvmStatic
        fun navigateToAddFragment(view: FloatingActionButton, navigate: Boolean) {
            view.setOnClickListener {
                if (navigate) {
                    view.findNavController().navigate(R.id.action_listFragment_to_addFragment)
                }
            }
        }

        @BindingAdapter("android:emptyDatabase")
        @JvmStatic
        fun emptyDatabase(view: View , emptyDatabase: MutableLiveData<Boolean>){
            when(emptyDatabase.value){
                true -> view.visibility = View.VISIBLE
                false -> view.visibility = View.INVISIBLE
                else -> {}
            }
        }

        @BindingAdapter("android:parsePriorityToInt")
        @JvmStatic
        fun parsePriorityToInt(view: Spinner , priority: Priority) {
            when (priority) {
                Priority.HIGH -> {view.setSelection(0)}
                Priority.MEDIUM -> {view.setSelection(1)}
                Priority.LOW -> {view.setSelection(2)}
            }
        }

        @BindingAdapter("android:parsePriorityColor")
        @JvmStatic
        fun parsePriorityColor(cardView: CardView , priority: Priority) {
            when (priority) {
                Priority.HIGH -> {cardView.setCardBackgroundColor(cardView.context.getColor(R.color.red))}
                Priority.MEDIUM -> {cardView.setCardBackgroundColor(cardView.context.getColor(R.color.yellow))}
                Priority.LOW -> {cardView.setCardBackgroundColor(cardView.context.getColor(R.color.green))}
            }
        }

        @BindingAdapter("android:sendDataToUpdateFragment")
        @JvmStatic
        fun sendDataToUpdateFragment(view: ConstraintLayout , currentItem: ToDoData) {
            view.setOnClickListener {
                val action = ListFragmentDirections.actionListFragmentToUpdateFragment(currentItem)
                view.findNavController().navigate(action)
            }
        }
    }
}
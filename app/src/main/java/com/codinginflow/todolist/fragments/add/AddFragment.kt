package com.codinginflow.todolist.fragments.add

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.codinginflow.todolist.R
import com.codinginflow.todolist.data.models.ToDoData
import com.codinginflow.todolist.data.viewmodel.ToDoViewModel
import com.codinginflow.todolist.fragments.SharedViewModel

class AddFragment : Fragment() {

    private val mToDoViewModel: ToDoViewModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_add, container, false)

        view.findViewById<Spinner>(R.id.spinner).onItemSelectedListener = mSharedViewModel.listener

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireHost() as MenuHost
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                super.onMenuClosed(menu)
                super.onPrepareMenu(menu)
                menuInflater.inflate(R.menu.add_fragment_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    android.R.id.home -> {
                        return false
                    }
                    R.id.menu_add -> {
                        insertData()
                    }
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun insertData() {
        val mTitle = view?.findViewById<EditText>(R.id.etTitle)?.text.toString()
        val mPriority = view?.findViewById<Spinner>(R.id.spinner)?.selectedItem.toString()
        val mDescription = view?.findViewById<EditText>(R.id.etDescription)?.text.toString()

        val validation = mSharedViewModel.verifyDataFromUser(mTitle, mDescription)

        if (validation) {
            val myData = ToDoData(
                title = mTitle,
                priority = mSharedViewModel.parsePriority(mPriority),
                description = mDescription
            )
            mToDoViewModel.insertData(myData)
            findNavController().navigate(R.id.action_addFragment_to_listFragment)
        }
    }
}
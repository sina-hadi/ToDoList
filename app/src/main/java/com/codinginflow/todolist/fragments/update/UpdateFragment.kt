package com.codinginflow.todolist.fragments.update

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.codinginflow.todolist.R
import com.codinginflow.todolist.data.models.ToDoData
import com.codinginflow.todolist.data.viewmodel.ToDoViewModel
import com.codinginflow.todolist.databinding.FragmentListBinding
import com.codinginflow.todolist.databinding.FragmentUpdateBinding
import com.codinginflow.todolist.fragments.SharedViewModel

class UpdateFragment : Fragment() {

    private val args by navArgs<UpdateFragmentArgs>()
    private val mySharedViewModel: SharedViewModel by viewModels()
    private val mToDoViewModel: ToDoViewModel by viewModels()

    private var _binding: FragmentUpdateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateBinding.inflate(inflater, container, false)
        binding.args = args

        binding.uspinner.onItemSelectedListener = mySharedViewModel.listener

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireHost() as MenuHost
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                super.onMenuClosed(menu)
                super.onPrepareMenu(menu)
                menuInflater.inflate(R.menu.update_fragment_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    android.R.id.home -> {
                        return false
                    }
                    R.id.menu_update -> {
                        updateData()
                    }
                    R.id.menu_delete -> {
                        deleteItem()
                    }
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun updateData() {
        val mTitle = binding.uetTitle.text.toString()
        val mPriority = binding.uspinner.selectedItem.toString()
        val mDescription = binding.uetDescription.text.toString()

        val validation = mySharedViewModel.verifyDataFromUser(mTitle, mDescription)

        if (validation) {
            val myData = ToDoData(
                id = args.currentitem.id,
                title = mTitle,
                priority = mySharedViewModel.parsePriority(mPriority),
                description = mDescription
            )
            mToDoViewModel.updateData(myData)
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        }
    }

    private fun deleteItem() {
        mToDoViewModel.deleteItem(args.currentitem)
        findNavController().navigate(R.id.action_updateFragment_to_listFragment)
    }
}
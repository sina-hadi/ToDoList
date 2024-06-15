package com.codinginflow.todolist.fragments.list

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.codinginflow.todolist.R
import com.codinginflow.todolist.data.models.ToDoData
import com.codinginflow.todolist.data.viewmodel.ToDoViewModel
import com.codinginflow.todolist.databinding.FragmentListBinding
import com.codinginflow.todolist.fragments.SharedViewModel
import com.codinginflow.todolist.fragments.list.adapter.ListAdapter
import com.codinginflow.todolist.utils.observeOnce
import com.google.android.material.snackbar.Snackbar
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator

class ListFragment : Fragment(), SearchView.OnQueryTextListener {

    private val mToDoViewModel: ToDoViewModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()
    private val adapter: ListAdapter by lazy { ListAdapter() }

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.args = mSharedViewModel

        mToDoViewModel.getAllData.observe(viewLifecycleOwner, Observer { data ->
            adapter.setData(data)
            mSharedViewModel.checkIfDatabaseEmpty(data)
        })

        setupRecyclerView()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        setupAnimatorRecyclerView()

        swipeToDelete(binding.recyclerView)
    }

    private fun setupAnimatorRecyclerView() {
        binding.recyclerView.itemAnimator = SlideInUpAnimator().apply {
            addDuration = 100
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireHost() as MenuHost
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.list_fragment_menu, menu)

                val search = menu.findItem(R.id.menu_search)
                val searchView = search.actionView as? SearchView
                searchView?.isSubmitButtonEnabled = true
                searchView?.setOnQueryTextListener(this@ListFragment)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.menu_high ->
                        mToDoViewModel.sortByHighPriority.observe(viewLifecycleOwner) {
                            adapter.setData(it)
                        }
                    R.id.menu_low ->
                        mToDoViewModel.sortByLowPriority.observe(viewLifecycleOwner) {
                            adapter.setData(it)
                        }
                    R.id.menu_delete_all -> {
                        confirmRemoval()
                    }
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun deleteAll() {
        mToDoViewModel.deleteAll()
    }

    private fun confirmRemoval() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _, _ ->
            deleteAll()
            Toast.makeText(
                requireContext(),
                "Successfully Removed Everything!",
                Toast.LENGTH_SHORT
            ).show()
        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.setTitle("Delete everything?")
        builder.setMessage("Are you sure you want to remove everything?")
        builder.create().show()
    }

    private fun swipeToDelete(recyclerView: RecyclerView) {
        val swipeToDeleteCallback = object : SwipeToDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val itemToDelete = adapter.dataList[viewHolder.adapterPosition]
                mToDoViewModel.deleteItem(itemToDelete)
                restoreDeletedData(viewHolder.itemView, itemToDelete)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    @SuppressLint("ShowToast")
    private fun restoreDeletedData(view: View, deletedItem: ToDoData) {
        val snackBar = Snackbar.make(
            view, "Deleted ${deletedItem.title}",
            Snackbar.LENGTH_LONG
        )
        snackBar.setAction("Undo") {
            mToDoViewModel.insertData(deletedItem)
        }
        snackBar.show()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            searchThroughDatabase(query)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            searchThroughDatabase(newText)
        }
        return true
    }

    private fun searchThroughDatabase(query: String) {
        var searchQuery = query
        searchQuery = "%$searchQuery%"

        mToDoViewModel.searchDatabase(searchQuery)
            .observeOnce(viewLifecycleOwner, Observer { list ->
                list.let {
                    adapter.setData(it)
                }
            })
    }

}
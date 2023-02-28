package com.flysolo.myapplication.views.teacher.nav

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.flysolo.myapplication.R
import com.flysolo.myapplication.databinding.TeacherHomeNavBinding
import com.flysolo.myapplication.services.classroom.ClassroomServiceImpl
import com.flysolo.myapplication.utils.LoadingDialog
import com.flysolo.myapplication.utils.UiState
import com.flysolo.myapplication.views.teacher.adapters.ClassroomAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


class TeacherHomeNav : Fragment() {

    private lateinit var binding : TeacherHomeNavBinding
    private val classroomService = ClassroomServiceImpl(FirebaseFirestore.getInstance(),
        FirebaseStorage.getInstance())
    private lateinit var loadingDialog : LoadingDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = TeacherHomeNavBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingDialog = LoadingDialog(view.context)
        binding.buttonCreateClass.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_createClassroom)
        }
        FirebaseAuth.getInstance().currentUser?.let {
            getAllClasses(it.uid)
        }
    }
    private fun getAllClasses(id : String) {
        classroomService.getAllClassroom(id) {
            when(it) {
                is UiState.Failed -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(binding.root.context,it.message, Toast.LENGTH_SHORT).show()
                }
                UiState.Loading -> {
                    loadingDialog.showLoadingDialog("Getting All Classes....")
                }
                is UiState.Successful -> {
                    loadingDialog.stopLoading()
                    if (it.data.isEmpty()) {
                        binding.textNoClass.visibility = View.GONE
                    } else {
                        binding.textNoClass.visibility = View.VISIBLE
                    }
                    binding.recyclerviewClasses.apply {
                        layoutManager = LinearLayoutManager(binding.root.context,LinearLayoutManager.HORIZONTAL,false)
                        adapter = ClassroomAdapter(binding.root.context,it.data)
                    }
                }
            }
        }
    }


}
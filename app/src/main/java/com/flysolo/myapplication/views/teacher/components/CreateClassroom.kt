package com.flysolo.myapplication.views.teacher.components

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.flysolo.myapplication.R
import com.flysolo.myapplication.databinding.FragmentCreateClassroomBinding
import com.flysolo.myapplication.models.Classroom
import com.flysolo.myapplication.services.classroom.ClassroomServiceImpl
import com.flysolo.myapplication.utils.LoadingDialog
import com.flysolo.myapplication.utils.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException


class CreateClassroom : Fragment() {
    private lateinit var binding : FragmentCreateClassroomBinding
    private val classroomService = ClassroomServiceImpl(FirebaseFirestore.getInstance(),
        FirebaseStorage.getInstance())

    private var imageUri : Uri? = null
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var loadingDialog : LoadingDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCreateClassroomBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingDialog = LoadingDialog(view.context)
        galleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                val data = result.data
                try {
                    if (data?.data != null) {
                        data.data?.let {
                            imageUri= it
                            binding.imageClassBackground.setImageURI(imageUri)
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        binding.buttonAddImage.setOnClickListener {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryLauncher.launch(galleryIntent)
        }
        binding.buttonCreateClass.setOnClickListener {
            val name = binding.inputName.text.toString()
            if (name.isEmpty()) {
                binding.layoutName.error = "This field is required!"
            } else {
                FirebaseAuth.getInstance().currentUser?.let {
                    val classroom = Classroom("",it.uid,"",name,false, "", mutableListOf(),System.currentTimeMillis())
                    if (imageUri != null) {
                        uploadImage(it.uid, imageUri!!,classroom)
                    } else {
                        saveClassroom(classroom)
                    }
                }

            }
        }
    }

    private fun uploadImage(uid : String,uri: Uri,classroom: Classroom) {
        classroomService.uploadClassroomBackground(uid,uri)
        {
            when(it) {
                is UiState.Failed -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(binding.root.context,it.message, Toast.LENGTH_LONG).show()
                }
                is UiState.Loading -> {
                    loadingDialog.showLoadingDialog("Uploading image...")
                }
                is UiState.Successful -> {
                    loadingDialog.stopLoading()
                    classroom.background = it.data
                    saveClassroom(classroom)
                }
            }
        }
    }
    private fun saveClassroom(classroom : Classroom) {
        classroomService.createClassroom(classroom) {
            when(it) {
                is UiState.Failed -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(binding.root.context,it.message,Toast.LENGTH_SHORT).show()
                }
                is UiState.Loading -> {
                    loadingDialog.showLoadingDialog("Saving classroom.....")
                }
                is UiState.Successful -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(binding.root.context,it.data,Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
            }
        }
    }

}
package com.maden.mface.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.maden.mface.databinding.ActivityMainBinding
import com.maden.mface.presentation.face_detector.MDetectorListener
import com.maden.mface.presentation.face_detector.MFaceDetectorEntryPoint
import com.maden.mface.presentation.face_match.MFaceMatchEntryPoint
import com.maden.mface.presentation.face_match.MFaceMatchLister
import com.maden.mface.util.showInputDialog
import kotlinx.coroutines.launch

@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity(), MDetectorListener, MFaceMatchLister {

    private val faceDetections = MFaceDetectorEntryPoint(_listener = this@MainActivity)
    private var faceMatch: MFaceMatchEntryPoint? = null

    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        faceMatch = MFaceMatchEntryPoint(
            _context = this@MainActivity,
            _listener = this@MainActivity
        )

        setViews()
        observeData()
    }

    private fun observeData() {
        viewModel.galleryPhotoLiveData.observe(this) {
            binding.faceImageView.setImageBitmap(it.first)
            binding.mFaceButtons.visibility = View.VISIBLE
        }

        viewModel.uiStateLiveData.observe(this) { uiState ->
            if (uiState == null) return@observe

            when (uiState) {
                MainActivityUIState.ADD_FACE, MainActivityUIState.RECOGNIZE_FACE -> {
                    lifecycleScope.launch {
                        faceDetections.execute(
                            viewModel.galleryPhotoLiveData.value?.first ?: return@launch
                        )
                    }
                }

                MainActivityUIState.LOADING -> {}
                MainActivityUIState.FINISH -> {}
                MainActivityUIState.ERROR -> {}
            }
        }
    }

    private fun setViews() {
        binding.pickImageButton.setOnClickListener {
            showInputDialog {
                var photoName = it
                if (photoName.trim().isEmpty()) {
                    photoName = "Photo: " + System.currentTimeMillis()
                }

                viewModel.photoName = photoName
                pickImage()
            }
        }

        binding.addFaceButton.setOnClickListener {
            viewModel.setUiState(uiState = MainActivityUIState.ADD_FACE)
        }

        binding.recognizeFaceButton.setOnClickListener {
            viewModel.setUiState(uiState = MainActivityUIState.RECOGNIZE_FACE)
        }
    }

    private var mainActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                lifecycleScope.launch {
                    viewModel.uriToBitmap(result, this@MainActivity)
                }
            }
        }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        mainActivityResultLauncher.launch(intent)
    }


    override fun onFaceDetected(face: Bitmap) {
        lifecycleScope.launch {
            if (viewModel.uiStateLiveData.value == MainActivityUIState.ADD_FACE) {
                faceMatch!!.addFace(name = viewModel.photoName, face = face)
            }

            if (viewModel.uiStateLiveData.value == MainActivityUIState.RECOGNIZE_FACE) {
                faceMatch!!.recognizeFace(face)
            }
        }
    }

    override fun onDetectorError(error: String) {
        binding.resultTextView.text = "onDetectorError: $error"
    }


    override fun onRecognizeFace(result: Boolean, name: String) {
        binding.resultTextView.text = "Result: $result | Name: $name"
    }

    override fun onFaceMatchError(error: String) {
        binding.resultTextView.text = "onFaceMatchError: $error"
    }

    //Optional
    override fun addFaceResult(result: Boolean) {
        super.addFaceResult(result)
    }

    //Optional
    override fun faceDetectorUIState(mFaceUiState: MFaceUIState) {
        super.faceDetectorUIState(mFaceUiState)
    }

    //Optional
    override fun faceMatchUIState(mFaceUiState: MFaceUIState) {
        super.faceMatchUIState(mFaceUiState)
    }
}
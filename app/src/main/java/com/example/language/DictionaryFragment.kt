package com.example.language

import com.example.dictionarycore.DictionaryEngine
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class DictionaryFragment : Fragment() {
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                recognizeTextFromImage(bitmap)
            }
        }


    private val engine = DictionaryEngine()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            Toast.makeText(
                context,
                "Camera permission is required for AI features",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dictionary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageButton>(R.id.camera_button).setOnClickListener {
            if (androidx.core.content.ContextCompat.checkSelfPermission(
                    requireContext(), android.Manifest.permission.CAMERA
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                cameraLauncher.launch(null)
            } else {
                requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
        }

        val rvLanguages = view.findViewById<RecyclerView>(R.id.rv_languages)
        rvLanguages.layoutManager = GridLayoutManager(context, 3)
        rvLanguages.adapter = TileAdapter(getInstalledLanguages()) { item ->
            Toast.makeText(context, "Clicked: ${item.title}", Toast.LENGTH_SHORT).show()
        }

        val rvTrending = view.findViewById<RecyclerView>(R.id.rv_ai_trending)
        rvTrending.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvTrending.adapter = TileAdapter(getTrendingLibraries()) { item ->
            Toast.makeText(context, "AI Loading: ${item.title}", Toast.LENGTH_SHORT).show()
        }

        val rvPlaylists = view.findViewById<RecyclerView>(R.id.rv_playlists)
        rvPlaylists.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvPlaylists.adapter = TileAdapter(getPlaylists()) { item ->
            Toast.makeText(context, "Opening Playlist: ${item.title}", Toast.LENGTH_SHORT).show()
        }

        val rvDownloadable = view.findViewById<RecyclerView>(R.id.rv_downloadable)
        rvDownloadable.layoutManager = GridLayoutManager(context, 3)
        rvDownloadable.adapter = TileAdapter(getDownloadableLanguages()) { item ->
            Toast.makeText(context, "Download started: ${item.title}", Toast.LENGTH_SHORT).show()
        }

        val searchBar = view.findViewById<EditText>(R.id.search_bar)
        val definitionView = view.findViewById<android.widget.TextView>(R.id.tv_definition)

        searchBar.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {

                val query = searchBar.text.toString().trim()
                val result = engine.search(query)

                // Show result
                definitionView.text = result ?: "No definition found"

                // Hide keyboard
                val imm = context?.getSystemService(android.content.Context.INPUT_METHOD_SERVICE)
                        as? android.view.inputmethod.InputMethodManager
                imm?.hideSoftInputFromWindow(v.windowToken, 0)

                true
            } else {
                false
            }
        }



        searchBar.addTextChangedListener(object : android.text.TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()

                if (query.isEmpty()) {
                    rvLanguages.adapter = TileAdapter(emptyList()) { }
                    return
                }

                val suggestions = engine.suggest(query)

                val tiles = suggestions.map { word ->
                    TileItem(word, "WORD", "RESULT")
                }

                rvLanguages.adapter = TileAdapter(tiles) { item ->
                    searchBar.setText(item.title)
                    searchBar.setSelection(item.title.length)
                }
            }

            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    private fun getInstalledLanguages(): List<TileItem> {
        return listOf(
            TileItem("Python", "PYTHON", "LANGUAGE"),
            TileItem("Java", "JAVA", "LANGUAGE"),
            TileItem("C++", "CPP", "LANGUAGE"),
            TileItem("Dart", "DART", "LANGUAGE"),
            TileItem("C", "C", "LANGUAGE")
        )
    }

    private fun getTrendingLibraries(): List<TileItem> {
        return listOf(
            TileItem("Pandas", "PYTHON", "LIBRARY"),
            TileItem("Retrofit", "JAVA", "LIBRARY"),
            TileItem("TensorFlow", "PYTHON", "LIBRARY"),
            TileItem("React", "JS", "LIBRARY")
        )
    }

    private fun getPlaylists(): List<TileItem> {
        return listOf(
            TileItem("My Favorites", "ALL", "PLAYLIST"),
            TileItem("Exam Prep", "CPP", "PLAYLIST"),
            TileItem("Game Dev", "C#", "PLAYLIST")
        )
    }

    private fun getDownloadableLanguages(): List<TileItem> {
        return listOf(
            TileItem("Rust", "RUST", "LANGUAGE"),
            TileItem("Go", "GO", "LANGUAGE"),
            TileItem("Ruby", "RUBY", "LANGUAGE")
        )
    }

    private fun recognizeTextFromImage(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val resultText = visionText.text
                if (resultText.isNotEmpty()) {
                    view?.findViewById<EditText>(R.id.search_bar)?.setText(resultText)
                    Toast.makeText(context, "Recognized: $resultText", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


}

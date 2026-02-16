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
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            recognizeTextFromImage(bitmap)
        }
    }

    private val  engine = DictionaryEngine()


    // New: Launcher to handle the camera permission request
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            Toast.makeText(context, "Camera permission is required for AI features", Toast.LENGTH_SHORT).show()
        }
    }



        override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dictionary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Updated: Camera button now checks for permission before opening
        view.findViewById<ImageButton>(R.id.camera_button).setOnClickListener {
            if (androidx.core.content.ContextCompat.checkSelfPermission(
                    requireContext(), android.Manifest.permission.CAMERA
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                cameraLauncher.launch(null)
            } else {
                requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
        }

        // 1. Setup "Installed Languages" (Grid Layout)
        val rvLanguages = view.findViewById<RecyclerView>(R.id.rv_languages)
        rvLanguages.layoutManager = GridLayoutManager(context, 3) // 3 tiles wide
        rvLanguages.adapter = TileAdapter(getInstalledLanguages()) { item ->
            // What happens when you click a language
            Toast.makeText(context, "Clicked: ${item.title}", Toast.LENGTH_SHORT).show()
        }

        // 2. Setup "Trending Libraries" (Horizontal Scroll)
        // Note: This is currently hidden in XML until we add the Internet Logic later
        val rvTrending = view.findViewById<RecyclerView>(R.id.rv_ai_trending)
        rvTrending.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvTrending.adapter = TileAdapter(getTrendingLibraries()) { item ->
            Toast.makeText(context, "AI Loading: ${item.title}", Toast.LENGTH_SHORT).show()
        }

        // 3. Setup "Playlists & Favorites" (Horizontal or Grid)
        val rvPlaylists = view.findViewById<RecyclerView>(R.id.rv_playlists)
        rvPlaylists.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvPlaylists.adapter = TileAdapter(getPlaylists()) { item ->
            Toast.makeText(context, "Opening Playlist: ${item.title}", Toast.LENGTH_SHORT).show()
        }

        // 4. Setup "Downloadable" (Grid)
        val rvDownloadable = view.findViewById<RecyclerView>(R.id.rv_downloadable)
        rvDownloadable.layoutManager = GridLayoutManager(context, 3)
        rvDownloadable.adapter = TileAdapter(getDownloadableLanguages()) { item ->
            Toast.makeText(context, "Download started: ${item.title}", Toast.LENGTH_SHORT).show()
        }

        // ... (Your RecyclerView code is here) ...

        // 5. Setup Search Bar Logic
        val searchBar = view.findViewById<EditText>(R.id.search_bar)

        searchBar.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                val query = searchBar.text.toString()

                val result = engine.search(query.trim())

                if (result != null) {
                    Toast.makeText(context, result, Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "No definition found", Toast.LENGTH_SHORT).show()
                }



                // Hide the keyboard after searching
                val imm = context?.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as? android.view.inputmethod.InputMethodManager
                imm?.hideSoftInputFromWindow(v.windowToken, 0)

                true // Return true to say "we handled it"
            } else {
                false
            }
        }

        // 6. Real-time Filtering Logic
        searchBar.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().lowercase()
                val filteredList = getInstalledLanguages().filter {
                    it.title.lowercase().contains(query)
                }

                // Update the adapter
                rvLanguages.adapter = TileAdapter(filteredList) { item ->
                    Toast.makeText(context, "Clicked: ${item.title}", Toast.LENGTH_SHORT).show()
                }

                // IMPROVED: Safely find the view from the fragment's main view
                val noResultsView = getView()?.findViewById<android.widget.TextView>(R.id.tv_no_results)

                if (filteredList.isEmpty() && query.isNotEmpty()) {
                    noResultsView?.visibility = View.VISIBLE
                    noResultsView?.text = getString(R.string.no_results_found, query)
                } else {
                    noResultsView?.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    // --- FAKE DATA GENERATORS (Updated with your 5 core languages) ---

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
                // This is the text the AI "read" from the photo
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
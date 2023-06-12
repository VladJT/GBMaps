package jt.projects.gbmaps.ui.markers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import jt.projects.gbmaps.databinding.FragmentMarkersBinding
import jt.projects.gbmaps.model.MapMarker
import jt.projects.gbmaps.utils.ViewModelNotInitException
import jt.projects.gbmaps.utils.showSnackbar
import jt.projects.gbmaps.viewmodel.MapViewModel

class MarkersFragment : Fragment() {

    private var _binding: FragmentMarkersBinding? = null
    private val binding get() = _binding!!

    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[MapViewModel::class.java] // переживает создание активити
    }

    private val markersAdapter: MarkersAdapter by lazy {
        MarkersAdapter(
            ::onSaveClick,
            ::onDeleteClick
        )
    }

    private fun onSaveClick(data: MapMarker, newTitle: String, newComment: String) {
        viewModel.editMarker(data, newTitle, newComment)
        showSnackbar("Данные сохранены")
    }

    private fun onDeleteClick(data: MapMarker) {
        viewModel.removeMarker(data.markerData.position)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMarkersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        initRecView()
    }

    private fun initViewModel() {
        // Убедимся, что модель инициализируется раньше View
        if (binding.recyclerviewMarkers.adapter != null) {
            throw ViewModelNotInitException
        }

        viewModel.liveDataToObserve.observe(viewLifecycleOwner) {
            markersAdapter.setData(it)
        }
    }

    private fun initRecView() {
        binding.recyclerviewMarkers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = markersAdapter
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
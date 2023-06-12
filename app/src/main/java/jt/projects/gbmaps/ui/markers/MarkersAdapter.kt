package jt.projects.gbmaps.ui.markers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import jt.projects.gbmaps.databinding.ItemMarkerBinding
import jt.projects.gbmaps.model.MapMarker

class MarkersAdapter(
    private var onSaveClicked: ((MapMarker, String, String) -> Unit),
    private var onDeleteClicked: ((MapMarker) -> Unit),
) :
    RecyclerView.Adapter<MarkersAdapter.MarkerViewHolder>() {

    private var data: List<MapMarker> = arrayListOf()

    // Метод передачи данных в адаптер
    fun setData(data: List<MapMarker>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarkerViewHolder {
        val binding = ItemMarkerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MarkerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MarkerViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount() = data.size


    inner class MarkerViewHolder(private val binding: ItemMarkerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: MapMarker) {
            if (layoutPosition != RecyclerView.NO_POSITION) {
                with(binding) {
                    tvLatitude.text = data.markerData.position.latitude.toString()
                    tvLongitude.text = data.markerData.position.longitude.toString()

                    etMarkerTitle.setText(data.markerData.title)
                    etMarkerComment.setText(data.comment)

                    btnSave.setOnClickListener {
                        onSaveClicked(
                            data,
                            etMarkerTitle.text.toString(),
                            etMarkerComment.text.toString()
                        )
                    }

                    btnDelete.setOnClickListener {
                        onDeleteClicked(data)
                    }
                }

            }
        }
    }
}
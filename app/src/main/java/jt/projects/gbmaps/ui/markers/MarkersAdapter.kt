package jt.projects.gbmaps.ui.markers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.MarkerOptions
import jt.projects.gbmaps.databinding.ItemMarkerBinding

class MarkersAdapter(
    private var onSaveClicked: ((MarkerOptions, String) -> Unit),
    private var onDeleteClicked: ((MarkerOptions) -> Unit),
) :
    RecyclerView.Adapter<MarkersAdapter.MarkerViewHolder>() {

    private var data: List<MarkerOptions> = arrayListOf()

    // Метод передачи данных в адаптер
    fun setData(data: List<MarkerOptions>) {
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
        fun bind(data: MarkerOptions) {
            if (layoutPosition != RecyclerView.NO_POSITION) {
                with(binding) {
                    tvLatitude.text = data.position.latitude.toString()
                    tvLongitude.text = data.position.longitude.toString()

                    etMarkerTitle.setText(data.title)

                    btnSave.setOnClickListener {
                        onSaveClicked(data, etMarkerTitle.text.toString())
                    }

                    btnDelete.setOnClickListener {
                        onDeleteClicked(data)
                    }
                }

            }
        }
    }
}
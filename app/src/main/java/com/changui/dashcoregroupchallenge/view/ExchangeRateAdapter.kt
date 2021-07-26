package com.changui.dashcoregroupchallenge.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.changui.dashcoregroupchallenge.databinding.CryptoCurrencyExchangeRateItemBinding
import com.changui.dashcoregroupchallenge.domain.entity.ExchangeRateModel

class ExchangeRateAdapter(private val items: MutableList<ExchangeRateModel>):
    RecyclerView.Adapter<ExchangeRateViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExchangeRateViewHolder {
        return ExchangeRateViewHolder(CryptoCurrencyExchangeRateItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ExchangeRateViewHolder, position: Int) {
        val item = items[position]
        holder.itemBinding.name.text = item.name
        holder.itemBinding.rate.text = item.rate.toString()
        if (position == itemCount - 1) {
            holder.itemBinding.divider.visibility = View.GONE
        } else holder.itemBinding.divider.visibility = View.VISIBLE
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setData(data: MutableList<ExchangeRateModel>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }
}

class ExchangeRateViewHolder(val itemBinding: CryptoCurrencyExchangeRateItemBinding) : RecyclerView.ViewHolder(itemBinding.root)

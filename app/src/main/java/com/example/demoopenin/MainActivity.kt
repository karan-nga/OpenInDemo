package com.example.demoopenin

import com.example.demoopenin.adapter.CardAdapter
import DashBoardModel
import RecentLink
import TopLink
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.demoopenin.adapter.LinkAdapter
import com.example.demoopenin.databinding.ActivityMainBinding
import com.example.demoopenin.networking.ApiResponse
import com.example.demoopenin.util.ProgressLoadingDialog
import com.example.demoopenin.viewmodel.DashboardViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: DashboardViewModel
    private lateinit var chart: BarChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[DashboardViewModel::class.java]
        val token = getString(R.string.token)
        val progressDialog = ProgressLoadingDialog(this, "Loading")
        viewModel.dashboardData.observe(this) { response ->
            when (response) {
                is ApiResponse.Loading -> {
                progressDialog.show()
                }
                is ApiResponse.Success -> {
                    progressDialog.dismiss()
                    binding = ActivityMainBinding.inflate(layoutInflater)
                    setContentView(binding.root)
                    chart=binding.chart
                    binding.currentTime.text= getGreetingMessage()
                    binding.userName.text=getString(R.string.user_name)
                    topAndRecentLinks(response.data)
                    buildGraph(response.data.data.overall_url_chart)
                }
                is ApiResponse.Error -> {
                    progressDialog.dismiss()
                   Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.getDashboardData(token)
    }

    private fun buildGraph(overallUrlChart: Map<String, Int>) {
        val barEntries = mutableListOf<BarEntry>()
        val monthNames = mutableListOf<String>()
        var i = 0
        for ((date, count) in overallUrlChart) {
            val month = date.substring(5, 7) // Extract the month from the date
            barEntries.add(BarEntry(i.toFloat(), count.toFloat()))
            monthNames.add(month)
            i++
        }

        val barDataSet = BarDataSet(barEntries, "URL Clicks")
        barDataSet.color = Color.argb(255, 14, 111, 255)

        val barData = BarData(barDataSet)

        chart.data = barData
        chart.setBackgroundColor(Color.WHITE)
        chart.xAxis.labelRotationAngle = -45f
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.axisLeft.axisMinimum = 0f
        chart.axisRight.isEnabled = false

        chart.xAxis.valueFormatter = IndexAxisValueFormatter(monthNames)
        chart.xAxis.granularity = 1f
        chart.xAxis.setCenterAxisLabels(true)
        chart.xAxis.axisMinimum = -0.5f
        chart.xAxis.axisMaximum = barEntries.size - 0.5f

        chart.invalidate()
    }
    private fun topAndRecentLinks(dashboardData: DashBoardModel) {
        val tagRecyclerView: RecyclerView = binding.tagRecyclerView
        val tagLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        tagRecyclerView.layoutManager = tagLayoutManager
        val tags = listOf("Recent","Top Links")
        val recyclerView: RecyclerView =binding.recyclerView
        val layoutManager = LinearLayoutManager(this@MainActivity)
        recyclerView.layoutManager = layoutManager
        val carAdapter = CardAdapter(tags) { selectedTag ->
            if (selectedTag.isNotEmpty()) {

                if(selectedTag==tags[0]){
                    val topLinks: List<TopLink> = dashboardData.data.top_links// Your topLinks data
                    val adapter = LinkAdapter(topLinks, R.layout.item_layout) { view, topLink ->
                        val linkNameTextView = view.findViewById<TextView>(R.id.linkName)
                        val logoImageView = view.findViewById<ImageView>(R.id.logo)
                        val dateTextView = view.findViewById<TextView>(R.id.date)
                        val numberOfClicksTextView = view.findViewById<TextView>(R.id.numberOfClicks)
                        val linkTextView = view.findViewById<TextView>(R.id.link)
                        val copyButton = view.findViewById<ImageButton>(R.id.copyButton)

                        // Bind data to the views
                        Glide.with(logoImageView)
                            .load(topLink.original_image)
                            .into(logoImageView)
                        linkNameTextView.text = topLink.title
                        dateTextView.text = topLink.times_ago
                        numberOfClicksTextView.text = topLink.total_clicks.toString()
                        linkTextView.text = topLink.web_link

                        // Set click listener for the copy button if needed
                        copyButton.setOnClickListener {
                           linkCopy(linkTextView)
                        }
                    }
                    recyclerView.adapter = adapter
                }
                else{
                    val topLinks: List<RecentLink> = dashboardData.data.recent_links// Your topLinks data
                    val adapter = LinkAdapter(topLinks, R.layout.item_layout) { view, topLink ->
                        val linkNameTextView = view.findViewById<TextView>(R.id.linkName)
                        val logoImageView = view.findViewById<ImageView>(R.id.logo)
                        val dateTextView = view.findViewById<TextView>(R.id.date)
                        val numberOfClicksTextView = view.findViewById<TextView>(R.id.numberOfClicks)
                        val linkTextView = view.findViewById<TextView>(R.id.link)
                        val copyButton = view.findViewById<ImageButton>(R.id.copyButton)

                        // Bind data to the views
                        Glide.with(logoImageView)
                            .load(topLink.original_image)
                            .placeholder(R.drawable.baseline_broken_image_24) // Placeholder image resource
                            .into(logoImageView)

                        linkNameTextView.text = topLink.title
                        dateTextView.text = topLink.times_ago
                        numberOfClicksTextView.text = topLink.total_clicks.toString()
                        linkTextView.text = topLink.web_link

                        // Set click listener for the copy button if needed
                        copyButton.setOnClickListener {
                            linkCopy(linkTextView)
                        }
                    }
                    recyclerView.adapter = adapter
                }

            } else {
                // Handle the empty selection
            }
        }
        tagRecyclerView.adapter = carAdapter
        carAdapter.setSelectedIndex(0)
    }
    private fun getGreetingMessage(): String {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)

        return if (currentHour in 0..11) {
            "Good Morning"
        } else {
            "Good Evening"
        }
    }
    private fun linkCopy(linkTextView: TextView) {
        val textToCopy = linkTextView.text.toString()

        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied Text", textToCopy)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(this, "Link copied to clipboard", Toast.LENGTH_SHORT).show()
    }

}
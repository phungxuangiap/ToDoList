package com.example.todolist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class chartView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart_view)
        var numDoing = intent.getStringExtra(Constaints.numDoing)
        var numHaventFinished = intent.getStringExtra(Constaints.numHaventFinish)
        var numFinished = intent.getStringExtra(Constaints.numFinish)
        var numCancle = intent.getStringExtra(Constaints.numCancle)
        var numLateTime = intent.getStringExtra(Constaints.numLateTime)

        var doingView:LinearLayout = findViewById(R.id.doing)
        var doingTitle:TextView = findViewById(R.id.doing_title)
        var haventFinishView:LinearLayout = findViewById(R.id.havent_finished)
        var haventFinishTitle:TextView = findViewById(R.id.havent_finished_title)
        var finishedView:LinearLayout = findViewById(R.id.finished)
        var finishTitle:TextView = findViewById(R.id.finished_title)
        var cancleView:LinearLayout = findViewById(R.id.cancle)
        var cancleTitle:TextView = findViewById(R.id.cancle_title)
        var lateTimeView:LinearLayout = findViewById(R.id.late_time)
        var lateTimeTitle:TextView = findViewById(R.id.late_time_title)
        var returnFilter: Button = findViewById(R.id.returnFilter)

        doingView.layoutParams.height = 70+100*numDoing!!.toInt()
        haventFinishView.layoutParams.height = 70+100*numHaventFinished!!.toInt()
        finishedView.layoutParams.height = 70+100*numFinished!!.toInt()
        cancleView.layoutParams.height = 70+100*numCancle!!.toInt()
        lateTimeView.layoutParams.height = 70+100*numLateTime!!.toInt()

        doingTitle.setText(numDoing)
        haventFinishTitle.setText(numHaventFinished)
        finishTitle.setText(numFinished)
        cancleTitle.setText(numCancle)
        lateTimeTitle.setText(numLateTime)
        returnFilter.setOnClickListener(){
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
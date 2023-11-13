package com.example.todolist

import android.app.DatePickerDialog
import android.content.ClipData.Item
import android.content.Context
import java.time.LocalDateTime
import java.util.*
import java.time.format.DateTimeFormatter
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setMargins
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputEditText
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.time.LocalDate
import kotlin.collections.ArrayList
import kotlin.random.Random


class MainActivity : AppCompatActivity() {
    private var listTaskObject = arrayListOf<Task>()
    private var listIDTask = ArrayList<String>()
    private var listProperty = listOf<String>()
    private var listIDCheckBox = arrayListOf<Int>()
    private var fileStoreIdOftask:String = "FileIdTask"
    private var IdOfTaskAfterFilters:String = "FileFilter"
    private var startTime:String = ""

    private var taskView:LinearLayout? = null
    private var tast:LinearLayout? = null
    private var layout:LinearLayout? = null
    private var btnPlus: TextView? = null
    private var popUpView:LinearLayout? = null
    private var listView:ScrollView? = null
    private var inputName:TextInputEditText? = null
    private var inputStart:TextInputEditText? = null
    private var task:LinearLayout? = null
    private var inputDeadline:TextView? = null
    private var inputDescription:TextInputEditText? = null
    private var priorityText:TextView? = null
    private var createBtn:Button? =null
    private var inputStatus:TextView? = null
    private var btnPopup:Button? = null
    private var statusText:TextView? = null
    private var btnPopupPriority:Button? = null
    private var deadlineBtn:Button? = null
    private var SearchTask:TextInputEditText? = null
    private var FilterView:LinearLayout? = null
    private var FilterBtn:TextView? = null
    private var ChartBtn:TextView? = null
    private var currentDateTime = 0
    private var currentMonthTime = 0
    private var currentYearTime = 0
//Create calendar and get input day
    private fun handleCalendar(){
        val myCalendar = Calendar.getInstance()
        val birthDate = Calendar.getInstance()
        val year = myCalendar.get(Calendar.YEAR)
        val month = myCalendar.get(Calendar.MONTH)
        val day = myCalendar.get(Calendar.DAY_OF_MONTH)
        val dpd = DatePickerDialog(this,
            { _, years, months, dayOfMonth->
                birthDate.set(years, months, dayOfMonth, 0 ,0)
                inputDeadline?.setText("$dayOfMonth/${months+1}/$years")
            },
            year,
            month,
            day
        )
        dpd.datePicker.minDate = System.currentTimeMillis()
        dpd.show()
    }
//handle store and read data from internal storage
    fun openFileInputAndRead(context:Context, filename:String):Task{
        try {
            context.openFileInput(filename).use {
                val buffer = ByteArray(it.available())
                it.read(buffer)
                val fileContents = String(buffer)
                return Gson().fromJson(fileContents, Task::class.java)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return Task("", "", "", "", "", "")
    }
    fun storeDataToPackage(context: Context, filename:String, data:Task){
        val fileContents = Gson().toJson(data)
        try {
            context.openFileOutput(filename, MODE_PRIVATE).use {
                it.write(fileContents.toByteArray())
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
    fun storeKeyToPackage(context:Context, filename: String, data: ArrayList<String>){
        val fileContents = Gson().toJson(data)
        try {
            context.openFileOutput(filename, Context.MODE_PRIVATE).use {
                it.write(fileContents.toByteArray())
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    fun openFileKeyInputAndRead(context:Context, filename:String):ArrayList<String>{
        var results = arrayListOf<String>()
        try {
            context.openFileInput(filename).use {
                val buffer = ByteArray(it.available())
                it.read(buffer)
                val fileContents = String(buffer)
                results = Gson().fromJson(fileContents, object : TypeToken<ArrayList<String>>() {}.type)
                return results
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return results
    }
//    ----------------------------------------------------------------------------------------------
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        layout = findViewById<LinearLayout>(R.id.main_activity)
        btnPlus = findViewById(R.id.btn_plus)
        popUpView = findViewById(R.id.inputTask)
        listView = findViewById(R.id.listView)
        createBtn = findViewById(R.id.createBtn)
        taskView = findViewById(R.id.listTask)
        btnPopup = findViewById(R.id.popupBtn)
        btnPopupPriority = findViewById(R.id.popupBtnPriority)
        statusText = findViewById(R.id.status)
        priorityText = findViewById(R.id.priority)
        deadlineBtn = findViewById(R.id.popupBtnDeadline)

        inputName = findViewById(R.id.todoNameText)
        inputStart = findViewById(R.id.todoStartTimeText)
        inputDeadline = findViewById(R.id.deadline)
        inputDescription = findViewById(R.id.todoDescriptionText)
        inputStatus = findViewById(R.id.status) //change later
        SearchTask = findViewById(R.id.searchBar)
        FilterView = findViewById(R.id.filterView)
        FilterBtn = findViewById(R.id.filterBtn)
        ChartBtn = findViewById(R.id.chartBtn)

        currentDateTime = Calendar.getInstance().time.date
        currentMonthTime = Calendar.getInstance().time.month
        currentYearTime = LocalDateTime.now().year
        startTime = "$currentDateTime/${currentMonthTime+1}/$currentYearTime"

//Handle show the input view
        btnPlus?.setOnClickListener{
            if (popUpView?.visibility != View.VISIBLE){
                popUpView?.visibility = View.VISIBLE
                listView?.visibility = View.GONE
                btnPlus?.text ="-"
            } else{
                popUpView?.visibility = View.GONE
                listView?.visibility = View.VISIBLE
                btnPlus?.text ="+"
            }

        }
//Handle Filter
//    Search
        SearchTask?.doOnTextChanged { text, start, before, count ->
            FilterView?.removeAllViews()
            var IdTask = openFileKeyInputAndRead(this, fileStoreIdOftask)
            for (i in IdTask){
                var Object = openFileInputAndRead(this, i)
                if (text.toString().isNotEmpty() && text.toString() in Object.title){
                    var textTrash = TextView(this)
                    var textTrashParams = LinearLayout.LayoutParams(56,56)
                    textTrashParams.setMargins(12, 12, 16 ,12)
                    textTrash.layoutParams = textTrashParams
                    textTrash.setId(R.id.trash)

                    var textCancle = TextView(this)
                    var textCancleParams = LinearLayout.LayoutParams(56, 56)
                    textCancleParams.setMargins(12, 12, 12, 12)
                    textCancle.layoutParams = textCancleParams
                    textCancle.setBackgroundResource(R.drawable.cancle)

                    var textTitleView = TextView(this)
                    var textTitleParams = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    textTitleView.layoutParams = textTitleParams
                    textTitleView.textSize = 20f
                    textTitleView.setTypeface(null, Typeface.BOLD)

                    var textTimeView = TextView(this)
                    var textTimeParams = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    textTimeView.layoutParams = textTimeParams
                    textTimeView.setTypeface(null, Typeface.ITALIC)

                    textTimeView.textSize = 18f

                    var linearLayoutChild = LinearLayout(this)
                    linearLayoutChild.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1F)
                    linearLayoutChild.setPadding(32, 0, 0, 0)
                    linearLayoutChild.orientation = LinearLayout.VERTICAL


                    var checkBoxView = CheckBox(this)
                    var checkBoxParams = LinearLayout.LayoutParams(70, 48)
                    checkBoxParams.setMargins(12, 12, 32, 12)
                    checkBoxView.layoutParams = checkBoxParams

                    checkBoxView.layoutParams = checkBoxParams
                    checkBoxView.id = i.toInt()+1000
                    listIDCheckBox.add(checkBoxView.id)

                    var linearLayout = LinearLayout(this)

                    linearLayout.orientation = LinearLayout.HORIZONTAL
                    val layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    linearLayout.setPadding(0, 20, 0, 20)


                    layoutParams.setMargins(54, 0, 54, 36)
                    linearLayout.gravity = Gravity.CENTER
                    linearLayout.setBackgroundResource(R.drawable.borderline)
                    linearLayout.id = i.toInt()
                    linearLayout.layoutParams = layoutParams
                    linearLayout.setBackgroundResource(R.drawable.active_background)
                    var element = openFileInputAndRead(this, i)
                    var dayListBody = element.deadline.split("/")
                    var date1 = LocalDate.of(currentYearTime, currentMonthTime, currentDateTime)
                    var dayTask = dayListBody[0].toInt()
                    var monthTask = dayListBody[1].toInt()-1
                    var yearTask = dayListBody[2].toInt()
                    var date2 = LocalDate.of(yearTask, monthTask, dayTask)
                    if (date1.isAfter(date2) && element.status !="Finish" && element.status != "Cancle") {
                        var updateObject = Task(element.title, "Late Time", element.importantOrder, element.startTime, element.deadline, element.description)
                        storeDataToPackage(this, i, updateObject)
                    }
                    if (element.status == "Finish"){
                        checkBoxView.isChecked = true
                        checkBoxView.isEnabled = false
                        linearLayout.setBackgroundResource(R.drawable.finish_background)
                    } else if (element.status == "Late Time" || element.status == "Cancle"){
                        checkBoxView.isEnabled = false
                        linearLayout.setBackgroundResource(R.drawable.cancle_background)
                    }
                    element = openFileInputAndRead(this, i)
                    textTitleView.setText(element.title)
                    textTimeView.setText(element.deadline)
                    textTrash.setBackgroundResource(R.drawable.trash)

                    FilterView?.addView(linearLayout)
                    linearLayoutChild.addView(textTitleView)
                    linearLayoutChild.addView(textTimeView)
                    linearLayout.addView(linearLayoutChild)
                    linearLayout.addView(checkBoxView)
                    linearLayout.addView(textCancle)
                    linearLayout.addView(textTrash)
                    checkBoxView.setOnCheckedChangeListener{buttonView, isChecked ->
                        if(isChecked){
                            var updateObject = Task(element.title, "Finish", element.importantOrder, element.startTime, element.deadline, element.description)
                            storeDataToPackage(this, i, updateObject)
                            var childTask = taskView?.childCount as Int
                            linearLayout.setBackgroundResource(R.drawable.finish_background)
                            taskView?.removeViews(3, childTask-3)
                            initial()
                        }
                    }
                    linearLayoutChild.setOnClickListener(){
                        var intent= Intent(this, inputTask::class.java)
                        intent.putExtra(Constaints.title, element.title)
                        intent.putExtra(Constaints.status, element.status)
                        intent.putExtra(Constaints.importantOrder, element.importantOrder)
                        intent.putExtra(Constaints.startTime, element.startTime)
                        intent.putExtra(Constaints.deadline, element.deadline)
                        intent.putExtra(Constaints.description, element.description)
                        intent.putExtra(Constaints.id, i)
                        startActivity(intent)
                    }
                    textTrash.setOnClickListener(){
                        var arrayId = openFileKeyInputAndRead(this, fileStoreIdOftask)
                        arrayId.remove(linearLayout.id.toString())
                        storeKeyToPackage(this, fileStoreIdOftask, arrayId)
                        taskView?.removeViews(3, taskView?.childCount as Int - 3)
                        FilterView?.removeView(linearLayout)
                        initial()
                    }
                    textCancle.setOnClickListener(){
                        if (checkBoxView.isChecked == false){
                            var updateObject = Task(element.title, "Cancle", element.importantOrder, element.startTime, element.deadline, element.description)
                            storeDataToPackage(this, linearLayout.id.toString(), updateObject)
                            var childTask = taskView?.childCount as Int
                            linearLayout.setBackgroundResource(R.drawable.cancle_background)
                            taskView?.removeViews(3, childTask-3)
                            initial()
                        }
                    }
                }
            }
        }
//    Handle filter task
        btnPopup?.setOnClickListener(){view->
            val popupMenu = PopupMenu(this, view)
            popupMenu.inflate(R.menu.menu_popup)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_item1 -> {
                        statusText?.setText("Doing")
                        true
                    }
                    R.id.menu_item2 -> {
                        statusText?.setText("Haven't Finished")
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
        btnPopupPriority?.setOnClickListener(){view->
            val popupMenu = PopupMenu(this, view)
            popupMenu.inflate(R.menu.menu_popup_priority)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_item1 -> {
                        priorityText?.setText("Finish in day")
                        true
                    }
                    R.id.menu_item2 -> {
                        priorityText?.setText("Finish in 2 days")
                        true
                    }
                    R.id.menu_item3 -> {
                        priorityText?.setText("Finish in week")
                        true
                    }
                    R.id.menu_item4 -> {
                        priorityText?.setText("Finish in 2 weeks")
                        true
                    }
                    R.id.menu_item5 -> {
                        priorityText?.setText("Finish in month")
                        true
                    }

                    else -> false
                }
            }
            popupMenu.show()
        }
//    Handle chart
        ChartBtn?.setOnClickListener(){
            var intent= Intent(this, chartView::class.java)
            var keys = openFileKeyInputAndRead(this, fileStoreIdOftask)
            var arrayStoreNumStatus = ArrayList<Int>()
            arrayStoreNumStatus.add(0)
            arrayStoreNumStatus.add(0)
            arrayStoreNumStatus.add(0)
            arrayStoreNumStatus.add(0)
            arrayStoreNumStatus.add(0)
            for (i in keys){
                var item = openFileInputAndRead(this, i)
                if (item.status == "Doing"){
                    arrayStoreNumStatus[0]++
                }else if (item.status == "Haven't Finished"){
                    arrayStoreNumStatus[1]++
                }else if (item.status == "Finish"){
                    arrayStoreNumStatus[2]++
                }else if (item.status == "Cancle"){
                    arrayStoreNumStatus[3]++
                }else if (item.status == "Late Time"){
                    arrayStoreNumStatus[4]++
                }
            }
            intent.putExtra(Constaints.numDoing, arrayStoreNumStatus[0].toString())
            intent.putExtra(Constaints.numHaventFinish, arrayStoreNumStatus[1].toString())
            intent.putExtra(Constaints.numFinish, arrayStoreNumStatus[2].toString())
            intent.putExtra(Constaints.numCancle, arrayStoreNumStatus[3].toString())
            intent.putExtra(Constaints.numLateTime, arrayStoreNumStatus[4].toString())
            startActivity(intent)
        }
        fun sortAndHandle(type:String){
            FilterView?.removeAllViews()
            var idItemSort = openFileKeyInputAndRead(this, fileStoreIdOftask)
            var arrayPriority = ArrayList<Int>()
            for (id in idItemSort){
                var item = openFileInputAndRead(this, id)
                if (item.importantOrder == "Finish in day"){
                    arrayPriority.add(1)
                } else if (item.importantOrder == "Finish in 2 days"){
                    arrayPriority.add(2)
                } else if (item.importantOrder == "Finish in week"){
                    arrayPriority.add(3)
                } else if (item.importantOrder == "Finish in 2 weeks"){
                    arrayPriority.add(4)
                } else if (item.importantOrder == "Finish in month"){
                    arrayPriority.add(5)
                }
            }
            if (type == "Increase"){
                for (i in 0..idItemSort.size-2){
                    for (j in i+1..idItemSort.size-1){
                        if (arrayPriority[i]>arrayPriority[j]){
                            var tg1 = arrayPriority[i]
                            var tg2 = idItemSort[i]
                            arrayPriority[i] = arrayPriority[j]
                            idItemSort[i] = idItemSort[j]
                            arrayPriority[j] = tg1
                            idItemSort[j] = tg2
                        }
                    }
                }

            } else {
                for (i in 0..idItemSort.size-2){
                    for (j in i+1..idItemSort.size-1){
                        if (arrayPriority[i]<arrayPriority[j]){
                            var tg1 = arrayPriority[i]
                            var tg2 = idItemSort[i]
                            arrayPriority[i] = arrayPriority[j]
                            idItemSort[i] = idItemSort[j]
                            arrayPriority[j] = tg1
                            idItemSort[j] = tg2
                        }
                    }
                }
            }
            storeKeyToPackage(this, fileStoreIdOftask, idItemSort)
            FilterView?.removeAllViews()
            taskView?.removeViews(3, taskView?.childCount as Int - 3)
            initial()
        }
        fun filterStatusTask(type:String){
            var key = openFileKeyInputAndRead(this, fileStoreIdOftask)
            FilterView?.removeAllViews()
            for (i in key){
                var item = openFileInputAndRead(this, i)
                if (item.status == type){
                    var textTrash = TextView(this)
                    var textTrashParams = LinearLayout.LayoutParams(56,56)
                    textTrashParams.setMargins(12, 12, 16 ,12)
                    textTrash.layoutParams = textTrashParams
                    textTrash.setId(R.id.trash)

                    var textCancle = TextView(this)
                    var textCancleParams = LinearLayout.LayoutParams(56, 56)
                    textCancleParams.setMargins(12, 12, 12, 12)
                    textCancle.layoutParams = textCancleParams
                    textCancle.setBackgroundResource(R.drawable.cancle)

                    var textTitleView = TextView(this)
                    var textTitleParams = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    textTitleView.layoutParams = textTitleParams
                    textTitleView.textSize = 20f
                    textTitleView.setTypeface(null, Typeface.BOLD)

                    var textTimeView = TextView(this)
                    var textTimeParams = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    textTimeView.layoutParams = textTimeParams
                    textTimeView.setTypeface(null, Typeface.ITALIC)

                    textTimeView.textSize = 18f

                    var linearLayoutChild = LinearLayout(this)
                    linearLayoutChild.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1F)
                    linearLayoutChild.setPadding(32, 0, 0, 0)
                    linearLayoutChild.orientation = LinearLayout.VERTICAL


                    var checkBoxView = CheckBox(this)
                    var checkBoxParams = LinearLayout.LayoutParams(70, 48)
                    checkBoxParams.setMargins(12, 12, 32, 12)
                    checkBoxView.layoutParams = checkBoxParams

                    checkBoxView.layoutParams = checkBoxParams
//                checkBoxView.setPadding(120, 120, 120, 120)
                    checkBoxView.id = i.toInt()+1000
                    listIDCheckBox.add(checkBoxView.id)

                    var linearLayout = LinearLayout(this)

                    linearLayout.orientation = LinearLayout.HORIZONTAL
                    val layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    linearLayout.setPadding(0, 20, 0, 20)


                    layoutParams.setMargins(54, 0, 54, 36)
                    linearLayout.gravity = Gravity.CENTER
                    linearLayout.setBackgroundResource(R.drawable.borderline)
                    linearLayout.id = i.toInt()
                    linearLayout.layoutParams = layoutParams
                    linearLayout.setBackgroundResource(R.drawable.active_background)
                    var element = openFileInputAndRead(this, i)
                    var dayListBody = element.deadline.split("/")
                    var date1 = LocalDate.of(currentYearTime, currentMonthTime, currentDateTime)
                    var dayTask = dayListBody[0].toInt()
                    var monthTask = dayListBody[1].toInt()-1
                    var yearTask = dayListBody[2].toInt()
                    var date2 = LocalDate.of(yearTask, monthTask, dayTask)
                    if (date1.isAfter(date2) && element.status !="Finish" && element.status != "Cancle") {
                        var updateObject = Task(element.title, "Late Time", element.importantOrder, element.startTime, element.deadline, element.description)
                        storeDataToPackage(this, i, updateObject)
                    }
                    if (element.status == "Finish"){
                        checkBoxView.isChecked = true
                        checkBoxView.isEnabled = false
                        linearLayout.setBackgroundResource(R.drawable.finish_background)
                    } else if (element.status == "Late Time" || element.status == "Cancle"){
                        checkBoxView.isEnabled = false
                        linearLayout.setBackgroundResource(R.drawable.cancle_background)
                    }
                    element = openFileInputAndRead(this, i)
                    textTitleView.setText(element.title)
                    textTimeView.setText(element.deadline)
                    textTrash.setBackgroundResource(R.drawable.trash)

                    FilterView?.addView(linearLayout)
                    linearLayoutChild.addView(textTitleView)
                    linearLayoutChild.addView(textTimeView)
                    linearLayout.addView(linearLayoutChild)
                    linearLayout.addView(checkBoxView)
                    linearLayout.addView(textCancle)
                    linearLayout.addView(textTrash)
                    checkBoxView.setOnCheckedChangeListener{buttonView, isChecked ->
                        if(isChecked){
                            var updateObject = Task(element.title, "Finish", element.importantOrder, element.startTime, element.deadline, element.description)
                            storeDataToPackage(this, i, updateObject)
                            var childTask = taskView?.childCount as Int
                            linearLayout.setBackgroundResource(R.drawable.finish_background)
                            taskView?.removeViews(3, childTask-3)
                            initial()
                        }
                    }
                    linearLayoutChild.setOnClickListener(){
                        var intent= Intent(this, inputTask::class.java)
                        intent.putExtra(Constaints.title, element.title)
                        intent.putExtra(Constaints.status, element.status)
                        intent.putExtra(Constaints.importantOrder, element.importantOrder)
                        intent.putExtra(Constaints.startTime, element.startTime)
                        intent.putExtra(Constaints.deadline, element.deadline)
                        intent.putExtra(Constaints.description, element.description)
                        intent.putExtra(Constaints.id, i)
                        startActivity(intent)
                    }
                    textTrash.setOnClickListener(){
                        var arrayId = openFileKeyInputAndRead(this, fileStoreIdOftask)
                        arrayId.remove(linearLayout.id.toString())
                        storeKeyToPackage(this, fileStoreIdOftask, arrayId)
                        taskView?.removeViews(3, taskView?.childCount as Int - 3)
                        FilterView?.removeView(linearLayout)
                        initial()
                    }
                    textCancle.setOnClickListener(){
                        if (checkBoxView.isChecked == false){
                            var updateObject = Task(element.title, "Cancle", element.importantOrder, element.startTime, element.deadline, element.description)
                            storeDataToPackage(this, linearLayout.id.toString(), updateObject)
                            var childTask = taskView?.childCount as Int
                            linearLayout.setBackgroundResource(R.drawable.cancle_background)
                            taskView?.removeViews(3, childTask-3)
                            initial()
                        }
                    }
                }
            }
        }
        fun filterPriorityTask(type:String){
        var key = openFileKeyInputAndRead(this, fileStoreIdOftask)
        FilterView?.removeAllViews()
        for (i in key){
            var item = openFileInputAndRead(this, i)
            if (item.importantOrder == type){
                var textTrash = TextView(this)
                var textTrashParams = LinearLayout.LayoutParams(56,56)
                textTrashParams.setMargins(12, 12, 16 ,12)
                textTrash.layoutParams = textTrashParams
                textTrash.setId(R.id.trash)

                var textCancle = TextView(this)
                var textCancleParams = LinearLayout.LayoutParams(56, 56)
                textCancleParams.setMargins(12, 12, 12, 12)
                textCancle.layoutParams = textCancleParams
                textCancle.setBackgroundResource(R.drawable.cancle)

                var textTitleView = TextView(this)
                var textTitleParams = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                textTitleView.layoutParams = textTitleParams
                textTitleView.textSize = 20f
                textTitleView.setTypeface(null, Typeface.BOLD)

                var textTimeView = TextView(this)
                var textTimeParams = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                textTimeView.layoutParams = textTimeParams
                textTimeView.setTypeface(null, Typeface.ITALIC)

                textTimeView.textSize = 18f

                var linearLayoutChild = LinearLayout(this)
                linearLayoutChild.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1F)
                linearLayoutChild.setPadding(32, 0, 0, 0)
                linearLayoutChild.orientation = LinearLayout.VERTICAL


                var checkBoxView = CheckBox(this)
                var checkBoxParams = LinearLayout.LayoutParams(70, 48)
                checkBoxParams.setMargins(12, 12, 32, 12)
                checkBoxView.layoutParams = checkBoxParams

                checkBoxView.layoutParams = checkBoxParams
//                checkBoxView.setPadding(120, 120, 120, 120)
                checkBoxView.id = i.toInt()+1000
                listIDCheckBox.add(checkBoxView.id)

                var linearLayout = LinearLayout(this)

                linearLayout.orientation = LinearLayout.HORIZONTAL
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                linearLayout.setPadding(0, 20, 0, 20)


                layoutParams.setMargins(54, 0, 54, 36)
                linearLayout.gravity = Gravity.CENTER
                linearLayout.setBackgroundResource(R.drawable.borderline)
                linearLayout.id = i.toInt()
                linearLayout.layoutParams = layoutParams
                linearLayout.setBackgroundResource(R.drawable.active_background)
                var element = openFileInputAndRead(this, i)
                var dayListBody = element.deadline.split("/")
                var date1 = LocalDate.of(currentYearTime, currentMonthTime, currentDateTime)
                var dayTask = dayListBody[0].toInt()
                var monthTask = dayListBody[1].toInt()-1
                var yearTask = dayListBody[2].toInt()
                var date2 = LocalDate.of(yearTask, monthTask, dayTask)
                if (date1.isAfter(date2) && element.status !="Finish" && element.status != "Cancle") {
                    var updateObject = Task(element.title, "Late Time", element.importantOrder, element.startTime, element.deadline, element.description)
                    storeDataToPackage(this, i, updateObject)
                }
                if (element.status == "Finish"){
                    checkBoxView.isChecked = true
                    checkBoxView.isEnabled = false
                    linearLayout.setBackgroundResource(R.drawable.finish_background)
                } else if (element.status == "Late Time" || element.status == "Cancle"){
                    checkBoxView.isEnabled = false
                    linearLayout.setBackgroundResource(R.drawable.cancle_background)
                }
                element = openFileInputAndRead(this, i)
                textTitleView.setText(element.title)
                textTimeView.setText(element.deadline)
                textTrash.setBackgroundResource(R.drawable.trash)

                FilterView?.addView(linearLayout)
                linearLayoutChild.addView(textTitleView)
                linearLayoutChild.addView(textTimeView)
                linearLayout.addView(linearLayoutChild)
                linearLayout.addView(checkBoxView)
                linearLayout.addView(textCancle)
                linearLayout.addView(textTrash)
                checkBoxView.setOnCheckedChangeListener{buttonView, isChecked ->
                    if(isChecked){
                        var updateObject = Task(element.title, "Finish", element.importantOrder, element.startTime, element.deadline, element.description)
                        storeDataToPackage(this, i, updateObject)
                        var childTask = taskView?.childCount as Int
                        linearLayout.setBackgroundResource(R.drawable.finish_background)
                        taskView?.removeViews(3, childTask-3)
                        initial()
                    }
                }
                linearLayoutChild.setOnClickListener(){
                    var intent= Intent(this, inputTask::class.java)
                    intent.putExtra(Constaints.title, element.title)
                    intent.putExtra(Constaints.status, element.status)
                    intent.putExtra(Constaints.importantOrder, element.importantOrder)
                    intent.putExtra(Constaints.startTime, element.startTime)
                    intent.putExtra(Constaints.deadline, element.deadline)
                    intent.putExtra(Constaints.description, element.description)
                    intent.putExtra(Constaints.id, i)
                    startActivity(intent)
                }
                textTrash.setOnClickListener(){
                    var arrayId = openFileKeyInputAndRead(this, fileStoreIdOftask)
                    arrayId.remove(linearLayout.id.toString())
                    storeKeyToPackage(this, fileStoreIdOftask, arrayId)
                    taskView?.removeViews(3, taskView?.childCount as Int - 3)
                    FilterView?.removeView(linearLayout)
                    initial()
                }
                textCancle.setOnClickListener(){
                    if (checkBoxView.isChecked == false){
                        var updateObject = Task(element.title, "Cancle", element.importantOrder, element.startTime, element.deadline, element.description)
                        storeDataToPackage(this, linearLayout.id.toString(), updateObject)
                        var childTask = taskView?.childCount as Int
                        linearLayout.setBackgroundResource(R.drawable.cancle_background)
                        taskView?.removeViews(3, childTask-3)
                        initial()
                    }
                }
            }
        }
    }
        fun filterDayTask(day:String){
            var key = openFileKeyInputAndRead(this, fileStoreIdOftask)
            FilterView?.removeAllViews()
            for (i in key){
                var item = openFileInputAndRead(this, i)
                if (item.deadline == day){
                    var textTrash = TextView(this)
                    var textTrashParams = LinearLayout.LayoutParams(56,56)
                    textTrashParams.setMargins(12, 12, 16 ,12)
                    textTrash.layoutParams = textTrashParams
                    textTrash.setId(R.id.trash)

                    var textCancle = TextView(this)
                    var textCancleParams = LinearLayout.LayoutParams(56, 56)
                    textCancleParams.setMargins(12, 12, 12, 12)
                    textCancle.layoutParams = textCancleParams
                    textCancle.setBackgroundResource(R.drawable.cancle)

                    var textTitleView = TextView(this)
                    var textTitleParams = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    textTitleView.layoutParams = textTitleParams
                    textTitleView.textSize = 20f
                    textTitleView.setTypeface(null, Typeface.BOLD)

                    var textTimeView = TextView(this)
                    var textTimeParams = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    textTimeView.layoutParams = textTimeParams
                    textTimeView.setTypeface(null, Typeface.ITALIC)

                    textTimeView.textSize = 18f

                    var linearLayoutChild = LinearLayout(this)
                    linearLayoutChild.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1F)
                    linearLayoutChild.setPadding(32, 0, 0, 0)
                    linearLayoutChild.orientation = LinearLayout.VERTICAL


                    var checkBoxView = CheckBox(this)
                    var checkBoxParams = LinearLayout.LayoutParams(70, 48)
                    checkBoxParams.setMargins(12, 12, 32, 12)
                    checkBoxView.layoutParams = checkBoxParams

                    checkBoxView.layoutParams = checkBoxParams
//                checkBoxView.setPadding(120, 120, 120, 120)
                    checkBoxView.id = i.toInt()+1000
                    listIDCheckBox.add(checkBoxView.id)

                    var linearLayout = LinearLayout(this)

                    linearLayout.orientation = LinearLayout.HORIZONTAL
                    val layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    linearLayout.setPadding(0, 20, 0, 20)


                    layoutParams.setMargins(54, 0, 54, 36)
                    linearLayout.gravity = Gravity.CENTER
                    linearLayout.setBackgroundResource(R.drawable.borderline)
                    linearLayout.id = i.toInt()
                    linearLayout.layoutParams = layoutParams
                    linearLayout.setBackgroundResource(R.drawable.active_background)
                    var element = openFileInputAndRead(this, i)
                    var dayListBody = element.deadline.split("/")
                    var date1 = LocalDate.of(currentYearTime, currentMonthTime, currentDateTime)
                    var dayTask = dayListBody[0].toInt()
                    var monthTask = dayListBody[1].toInt()-1
                    var yearTask = dayListBody[2].toInt()
                    var date2 = LocalDate.of(yearTask, monthTask, dayTask)
                    if (date1.isAfter(date2) && element.status !="Finish" && element.status != "Cancle") {
                        var updateObject = Task(element.title, "Late Time", element.importantOrder, element.startTime, element.deadline, element.description)
                        storeDataToPackage(this, i, updateObject)
                    }
                    if (element.status == "Finish"){
                        checkBoxView.isChecked = true
                        checkBoxView.isEnabled = false
                        linearLayout.setBackgroundResource(R.drawable.finish_background)
                    } else if (element.status == "Late Time" || element.status == "Cancle"){
                        checkBoxView.isEnabled = false
                        linearLayout.setBackgroundResource(R.drawable.cancle_background)
                    }
                    element = openFileInputAndRead(this, i)
                    textTitleView.setText(element.title)
                    textTimeView.setText(element.deadline)
                    textTrash.setBackgroundResource(R.drawable.trash)

                    FilterView?.addView(linearLayout)
                    linearLayoutChild.addView(textTitleView)
                    linearLayoutChild.addView(textTimeView)
                    linearLayout.addView(linearLayoutChild)
                    linearLayout.addView(checkBoxView)
                    linearLayout.addView(textCancle)
                    linearLayout.addView(textTrash)
                    checkBoxView.setOnCheckedChangeListener{buttonView, isChecked ->
                        if(isChecked){
                            var updateObject = Task(element.title, "Finish", element.importantOrder, element.startTime, element.deadline, element.description)
                            storeDataToPackage(this, i, updateObject)
                            var childTask = taskView?.childCount as Int
                            linearLayout.setBackgroundResource(R.drawable.finish_background)
                            taskView?.removeViews(3, childTask-3)
                            initial()
                        }
                    }
                    linearLayoutChild.setOnClickListener(){
                        var intent= Intent(this, inputTask::class.java)
                        intent.putExtra(Constaints.title, element.title)
                        intent.putExtra(Constaints.status, element.status)
                        intent.putExtra(Constaints.importantOrder, element.importantOrder)
                        intent.putExtra(Constaints.startTime, element.startTime)
                        intent.putExtra(Constaints.deadline, element.deadline)
                        intent.putExtra(Constaints.description, element.description)
                        intent.putExtra(Constaints.id, i)
                        startActivity(intent)
                    }
                    textTrash.setOnClickListener(){
                        var arrayId = openFileKeyInputAndRead(this, fileStoreIdOftask)
                        arrayId.remove(linearLayout.id.toString())
                        storeKeyToPackage(this, fileStoreIdOftask, arrayId)
                        taskView?.removeViews(3, taskView?.childCount as Int - 3)
                        FilterView?.removeView(linearLayout)
                        initial()
                    }
                    textCancle.setOnClickListener(){
                        if (checkBoxView.isChecked == false){
                            var updateObject = Task(element.title, "Cancle", element.importantOrder, element.startTime, element.deadline, element.description)
                            storeDataToPackage(this, linearLayout.id.toString(), updateObject)
                            var childTask = taskView?.childCount as Int
                            linearLayout.setBackgroundResource(R.drawable.cancle_background)
                            taskView?.removeViews(3, childTask-3)
                            initial()
                        }
                    }
                }
            }
        }
        FilterBtn?.setOnClickListener(){view->
            val popupMenu = PopupMenu(this, view)
            popupMenu.inflate(R.menu.filter_popup)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_item1 -> {
                        FilterView?.removeAllViews()
                        taskView?.removeViews(3, taskView?.childCount as Int - 3)
                        initial()
                        true
                    }
                    R.id.menu_item2 -> {
                        var popupMenuSort = PopupMenu(this, view)
                        popupMenuSort.inflate(R.menu.sort_menu)
                        popupMenuSort.setOnMenuItemClickListener { item2 ->
                            when (item2.itemId) {
                                R.id.menu_sort_item1 -> {
                                    sortAndHandle("Increase")
                                    true
                                }
                                R.id.menu_sort_item2 -> {
                                    sortAndHandle("Decrease")
                                    true
                                }
                                else -> false
                            }
                        }
                        popupMenuSort.show()
                        true
                    }
                    R.id.menu_item3 -> {
                        var popupMenuSort = PopupMenu(this, view)
                        popupMenuSort.inflate(R.menu.status_menu)
                        popupMenuSort.setOnMenuItemClickListener { item3 ->
                            when (item3.itemId) {
                                R.id.menu_item3_option1 -> {
                                    filterStatusTask("Doing")
                                    true
                                }
                                R.id.menu_item3_option2 -> {
                                    filterStatusTask("Haven't Finished")
                                    true
                                }
                                R.id.menu_item3_option3 -> {
                                    filterStatusTask("Finish")
                                    true
                                }
                                R.id.menu_item3_option4 -> {
                                    filterStatusTask("Cancle")
                                    true
                                }
                                R.id.menu_item3_option5 -> {
                                    filterStatusTask("Late Time")
                                    true
                                }
                                else -> false
                            }
                        }
                        popupMenuSort.show()
                        true
                    }
                    R.id.menu_item4 -> {
                        var popupMenuSort = PopupMenu(this, view)
                        popupMenuSort.inflate(R.menu.priority_menu)
                        popupMenuSort.setOnMenuItemClickListener { item4 ->
                            when (item4.itemId) {
                                R.id.menu_item4_option1 -> {
                                    filterPriorityTask("Finish in day")
                                    true
                                }
                                R.id.menu_item4_option2 -> {
                                    filterPriorityTask("Finish in 2 days")
                                    true
                                }
                                R.id.menu_item4_option3 -> {
                                    filterPriorityTask("Finish in week")
                                    true
                                }
                                R.id.menu_item4_option4 -> {
                                    filterPriorityTask("Finish in 2 weeks")
                                    true
                                }
                                R.id.menu_item4_option5 -> {
                                    filterPriorityTask("Finish in month")
                                    true
                                }
                                else -> false
                            }
                        }
                        popupMenuSort.show()
                        true
                    }
                    R.id.menu_item5 -> {
                        val calen = Calendar.getInstance()
                        val birday = Calendar.getInstance()
                        val yearz = calen.get(Calendar.YEAR)
                        val monthz = calen.get(Calendar.MONTH)
                        val dayz = calen.get(Calendar.DAY_OF_MONTH)
                        val dpd = DatePickerDialog(this,
                            { _, years, months, dayOfMonth->
                                birday.set(years, months, dayOfMonth, 0 ,0)
                                var checkDay = "$dayOfMonth/${months+1}/$years"
                                filterDayTask(checkDay)
                            },
                            yearz,
                            monthz,
                            dayz
                        )
                        dpd.show()
                        true
                    }


                    else -> false
                }
            }
            popupMenu.show()
        }
        deadlineBtn?.setOnClickListener(){
            handleCalendar()
        }
//Reset dataBase (call funtion to reset)
        fun deleteInternalStorageFiles() {
            val internalStorageDir = File("/data/data/com.example.todolist/files")

            if (internalStorageDir.exists() && internalStorageDir.isDirectory) {
                internalStorageDir.listFiles()?.forEach { file ->
                    file.delete()
                }
            }
        }
//        deleteInternalStorageFiles()
        initial()
//    Handle create new task
        createBtn?.setOnClickListener{
            FilterView?.removeAllViews()
            if (inputName?.text.toString().isNotEmpty() &&
                inputStatus?.text.toString().isNotEmpty() &&
                priorityText?.text.toString().isNotEmpty() &&
//                inputStart?.text.toString().isNotEmpty() &&
                inputDeadline?.text.toString().isNotEmpty()
                && inputDescription?.text.toString().isNotEmpty()){

                var textCancle = TextView(this)
                var textCancleParams = LinearLayout.LayoutParams(56, 56)
                textCancleParams.setMargins(12, 12, 12, 12)
                textCancle.layoutParams = textCancleParams
                textCancle.setBackgroundResource(R.drawable.cancle)

                var textTrash = TextView(this)
                var textTrashParams = LinearLayout.LayoutParams(56,56)
                textTrashParams.setMargins(12, 12, 16, 12)
                textTrash.layoutParams = textTrashParams
                textTrash.setId(R.id.trash)

                var textTitleView = TextView(this)
                var textTitleParams = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                textTitleView.layoutParams = textTitleParams
                textTitleView.setTypeface(null, Typeface.BOLD)
                textTitleView.textSize = 20f

                var textTimeView = TextView(this)
                var textTimeParams = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                textTimeView.layoutParams = textTimeParams
                textTimeView.setTypeface(null, Typeface.ITALIC)

                textTimeView.textSize = 18f

                var linearLayoutChild = LinearLayout(this)
                var linearLayoutChildParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1F)
                linearLayoutChild.setPadding(32, 0, 0, 0)
                linearLayoutChild.layoutParams = linearLayoutChildParams
                linearLayoutChild.orientation = LinearLayout.VERTICAL

                var checkBoxView = CheckBox(this)
                var checkBoxParams = LinearLayout.LayoutParams(70, 48)
                checkBoxParams.setMargins(12, 12, 32, 12)
                checkBoxView.layoutParams = checkBoxParams
                checkBoxView.id = Random.nextInt()
                listIDCheckBox.add(checkBoxView.id)


                var linearLayout = LinearLayout(this)

                linearLayout.orientation = LinearLayout.HORIZONTAL
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams.setMargins(54, 0, 54, 36)
                linearLayout.setPadding(1,1,1,1)
                linearLayout.gravity = Gravity.CENTER
                linearLayout.setBackgroundResource(R.drawable.borderline)
                linearLayout.setPadding(0, 20, 0, 20)
                linearLayout.setBackgroundResource(R.drawable.active_background)
                textTrash.setBackgroundResource(R.drawable.trash)
                linearLayout.id = Random.nextInt()
                linearLayout.layoutParams = layoutParams

                var newTask = Task(inputName?.text.toString(), inputStatus?.text.toString(), priorityText?.text.toString(), startTime, inputDeadline?.text.toString(), inputDescription?.text.toString())
                storeDataToPackage(this, linearLayout.id.toString(), newTask)
                var arrayKey = openFileKeyInputAndRead(this, fileStoreIdOftask)
                arrayKey.add(linearLayout.id.toString())
                storeKeyToPackage(this, fileStoreIdOftask, arrayKey)
                var out = openFileInputAndRead(this, linearLayout.id.toString())
                textTitleView.setText(out.title.toString())
                textTimeView.setText(out.deadline.toString())

                taskView?.addView(linearLayout)
                linearLayoutChild.addView(textTitleView)
                linearLayoutChild.addView(textTimeView)
                linearLayout.addView(linearLayoutChild)
                linearLayout.addView(checkBoxView)
                linearLayout.addView(textCancle)
                linearLayout.addView(textTrash)
                textCancle.setOnClickListener(){
                    if (checkBoxView.isChecked == false){
                        var updateObject = Task(out.title, "Cancle", out.importantOrder, out.startTime, out.deadline, out.description)
                        storeDataToPackage(this, linearLayout.id.toString(), updateObject)
                        var childTask = taskView?.childCount as Int
                        taskView?.removeViews(3, childTask-3)
                        initial()
                    }
                }
                checkBoxView.setOnCheckedChangeListener{buttonView, isChecked ->
                    if(isChecked){
                        var updateObject = Task(out.title, "Finish", out.importantOrder, out.startTime, out.deadline, out.description)
                        storeDataToPackage(this, linearLayout.id.toString(), updateObject)
                        var childTask = taskView?.childCount as Int
                        taskView?.removeViews(3, childTask-3)
                        initial()
                    }
                }
                linearLayoutChild.setOnClickListener(){
                    var intent= Intent(this, inputTask::class.java)
                    intent.putExtra(Constaints.title, out.title)
                    intent.putExtra(Constaints.status, out.status)
                    intent.putExtra(Constaints.importantOrder, out.importantOrder)
                    intent.putExtra(Constaints.startTime, out.startTime)
                    intent.putExtra(Constaints.deadline, out.deadline)
                    intent.putExtra(Constaints.description, out.description)
                    intent.putExtra(Constaints.id, linearLayout.id.toString())
                    startActivity(intent)
                }
                textTrash.setOnClickListener(){
                    var arrayId = openFileKeyInputAndRead(this, fileStoreIdOftask)
                    arrayId.remove(linearLayout.id.toString())
                    storeKeyToPackage(this, fileStoreIdOftask, arrayId)
                    taskView?.removeView(linearLayout)
                }

                if (popUpView?.visibility != View.VISIBLE){
                    popUpView?.visibility = View.VISIBLE
                    listView?.visibility = View.GONE
                    btnPlus?.text ="-"
                } else{
                    popUpView?.visibility = View.GONE
                    listView?.visibility = View.VISIBLE
                    btnPlus?.text ="+"
                }

            } else{
                Toast.makeText(this, "Please provide more infomation to make a task", Toast.LENGTH_SHORT).show()
            }
        }
        task?.setOnClickListener(){
            var intent= Intent(this, inputTask::class.java)
            startActivity(intent)
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
//Initial avaiable task when open app
    fun initial() {
        val file = File(filesDir, fileStoreIdOftask)
        if (!file.exists()) {
            storeKeyToPackage(this, fileStoreIdOftask, arrayListOf())
        } else{
            listIDTask = openFileKeyInputAndRead(this, fileStoreIdOftask)
            for (i in listIDTask){

                var textTrash = TextView(this)
                var textTrashParams = LinearLayout.LayoutParams(56,56)
                textTrashParams.setMargins(12, 12, 16 ,12)
                textTrash.layoutParams = textTrashParams
                textTrash.setId(R.id.trash)

                var textCancle = TextView(this)
                var textCancleParams = LinearLayout.LayoutParams(56, 56)
                textCancleParams.setMargins(12, 12, 12, 12)
                textCancle.layoutParams = textCancleParams
                textCancle.setBackgroundResource(R.drawable.cancle)

                var textTitleView = TextView(this)
                var textTitleParams = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                textTitleView.layoutParams = textTitleParams
                textTitleView.textSize = 20f
                textTitleView.setTypeface(null, Typeface.BOLD)

                var textTimeView = TextView(this)
                var textTimeParams = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                textTimeView.layoutParams = textTimeParams
                textTimeView.setTypeface(null, Typeface.ITALIC)

                textTimeView.textSize = 18f

                var linearLayoutChild = LinearLayout(this)
                linearLayoutChild.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1F)
                linearLayoutChild.setPadding(32, 0, 0, 0)
                linearLayoutChild.orientation = LinearLayout.VERTICAL


                var checkBoxView = CheckBox(this)
                var checkBoxParams = LinearLayout.LayoutParams(70, 48)
                checkBoxParams.setMargins(12, 12, 32, 12)
                checkBoxView.layoutParams = checkBoxParams

                checkBoxView.layoutParams = checkBoxParams
                checkBoxView.id = i.toInt()+1000
                listIDCheckBox.add(checkBoxView.id)

                var linearLayout = LinearLayout(this)

                linearLayout.orientation = LinearLayout.HORIZONTAL
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                linearLayout.setPadding(0, 20, 0, 20)


                layoutParams.setMargins(54, 0, 54, 36)
                linearLayout.gravity = Gravity.CENTER
                linearLayout.setBackgroundResource(R.drawable.borderline)
                linearLayout.id = i.toInt()
                linearLayout.layoutParams = layoutParams
                linearLayout.setBackgroundResource(R.drawable.active_background)
                var element = openFileInputAndRead(this, i)
                var dayListBody = element.deadline.split("/")
                var date1 = LocalDate.of(currentYearTime, currentMonthTime, currentDateTime)
                var dayTask = dayListBody[0].toInt()
                var monthTask = dayListBody[1].toInt()-1
                var yearTask = dayListBody[2].toInt()
                var date2 = LocalDate.of(yearTask, monthTask, dayTask)
                if (date1.isAfter(date2) && element.status !="Finish" && element.status != "Cancle") {
                    var updateObject = Task(element.title, "Late Time", element.importantOrder, element.startTime, element.deadline, element.description)
                    storeDataToPackage(this, i, updateObject)
                }
                if (element.status == "Finish"){
                    checkBoxView.isChecked = true
                    checkBoxView.isEnabled = false
                    linearLayout.setBackgroundResource(R.drawable.finish_background)
                } else if (element.status == "Late Time" || element.status == "Cancle"){
                    checkBoxView.isEnabled = false
                    linearLayout.setBackgroundResource(R.drawable.cancle_background)
                }
                element = openFileInputAndRead(this, i)
                textTitleView.setText(element.title)
                textTimeView.setText(element.deadline)
                textTrash.setBackgroundResource(R.drawable.trash)

                taskView?.addView(linearLayout)
                linearLayoutChild.addView(textTitleView)
                linearLayoutChild.addView(textTimeView)
                linearLayout.addView(linearLayoutChild)
                linearLayout.addView(checkBoxView)
                linearLayout.addView(textCancle)
                linearLayout.addView(textTrash)
                checkBoxView.setOnCheckedChangeListener{buttonView, isChecked ->
                    if(isChecked){
                        var updateObject = Task(element.title, "Finish", element.importantOrder, element.startTime, element.deadline, element.description)
                        storeDataToPackage(this, i, updateObject)
                        var childTask = taskView?.childCount as Int
                        taskView?.removeViews(3, childTask-3)
                        initial()
                    }
                }
                linearLayoutChild.setOnClickListener(){
                    var intent= Intent(this, inputTask::class.java)
                    intent.putExtra(Constaints.title, element.title)
                    intent.putExtra(Constaints.status, element.status)
                    intent.putExtra(Constaints.importantOrder, element.importantOrder)
                    intent.putExtra(Constaints.startTime, element.startTime)
                    intent.putExtra(Constaints.deadline, element.deadline)
                    intent.putExtra(Constaints.description, element.description)
                    intent.putExtra(Constaints.id, i)
                    startActivity(intent)
                }
                textTrash.setOnClickListener(){
                    var arrayId = openFileKeyInputAndRead(this, fileStoreIdOftask)
                    arrayId.remove(linearLayout.id.toString())
                    storeKeyToPackage(this, fileStoreIdOftask, arrayId)
                    taskView?.removeView(linearLayout)
                }
                textCancle.setOnClickListener(){
                    if (checkBoxView.isChecked == false){
                        var updateObject = Task(element.title, "Cancle", element.importantOrder, element.startTime, element.deadline, element.description)
                        storeDataToPackage(this, linearLayout.id.toString(), updateObject)
                        var childTask = taskView?.childCount as Int
                        taskView?.removeViews(3, childTask-3)
                        initial()
                    }
                }

            }
        }
    }
}

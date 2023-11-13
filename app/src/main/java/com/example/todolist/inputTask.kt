package com.example.todolist

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.setMargins
import java.io.IOException
import java.util.Calendar

class inputTask : AppCompatActivity() {
    private var btnBackFromInfoView: Button? = null
    private var titleInfoTask: TextView? = null
    private var deadlineTime: TextView? = null
    private var descriptionText: TextView? = null
    private var statusText: TextView? = null
    private var orderText: TextView? = null
    private var startTimeText: TextView? = null
    private var mainview:LinearLayout? = null
    private var inputView:LinearLayout? = null
    private var editBtn:Button? = null
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
    private fun handleCalendar(view:TextView?){
        val myCalendar = Calendar.getInstance()
        val birthDate = Calendar.getInstance()
        val year = myCalendar.get(Calendar.YEAR)
        val month = myCalendar.get(Calendar.MONTH)
        val day = myCalendar.get(Calendar.DAY_OF_MONTH)
        val dpd = DatePickerDialog(this,
            { _, years, months, dayOfMonth->
                birthDate.set(years, months, dayOfMonth, 0 ,0)
                view?.setText("$dayOfMonth/${months+1}/$years")
            },
            year,
            month,
            day
        )
        dpd.datePicker.minDate = System.currentTimeMillis()
        dpd.show()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_task2)
        var title = intent.getStringExtra(Constaints.title)
        var status = intent.getStringExtra(Constaints.status)
        var importantOrder = intent.getStringExtra(Constaints.importantOrder)
        var startTime = intent.getStringExtra(Constaints.startTime)
        var deadline = intent.getStringExtra(Constaints.deadline)
        var description = intent.getStringExtra(Constaints.description)
        var id = intent.getStringExtra(Constaints.id).toString()
        titleInfoTask = findViewById(R.id.titleInfoTask)
        startTimeText = findViewById(R.id.startTime)
        deadlineTime = findViewById(R.id.deadlineTime)
        descriptionText = findViewById(R.id.descriptionText)
        statusText = findViewById(R.id.statusText)
        orderText = findViewById(R.id.orderText)
        mainview = findViewById(R.id.mainOfInputView)
        inputView = findViewById(R.id.inputView)
        editBtn = findViewById(R.id.editBtn)
        if (status =="Finish"){
            inputView?.setBackgroundResource(R.drawable.finish_background)
            inputView?.removeView(editBtn)
        } else if (status =="Cancle" || status == "Late Time"){
            inputView?.setBackgroundResource(R.drawable.cancle_background)
            inputView?.removeView(editBtn)

        }
        titleInfoTask?.setText(title)
        startTimeText?.setText(startTime)
        deadlineTime?.setText(deadline)
        descriptionText?.setText(description)
        statusText?.setText(status)
        orderText?.setText(importantOrder)

//        make new display-----------------------------------------------------------
        var titleEdit:TextView = TextView(this)
        var titleEditParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        titleEdit.setTypeface(null, Typeface.BOLD)
        titleEdit.setText("EDIT")
        titleEdit.gravity = Gravity.CENTER
        titleEdit.textSize = 36F
        titleEdit.layoutParams = titleEditParams

        var btnDone:Button = Button(this)
        var btnDoneParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        btnDone.textSize = 36F
        btnDone.setText("DONE")
        btnDone.setBackgroundResource(R.color.btnColor)
        btnDone.layoutParams = btnDoneParams


//        Container Content
        var wrapInfo:LinearLayout = LinearLayout(this)
        var wrapInfoParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        wrapInfoParams.setMargins(24, 24, 24, 24)
        wrapInfo.orientation = LinearLayout.VERTICAL
        wrapInfo.gravity = Gravity.CENTER
        wrapInfo.layoutParams = wrapInfoParams
        wrapInfo.setBackgroundResource(R.drawable.borderline)
        wrapInfo.setPadding(24, 24, 24, 24)
//        Title
        var cardTitleViewContainer:androidx.cardview.widget.CardView = androidx.cardview.widget.CardView(this)
        var cardTitleViewContainerParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        cardTitleViewContainer.layoutParams = cardTitleViewContainerParams
        var cardTitleView:com.google.android.material.textfield.TextInputEditText = com.google.android.material.textfield.TextInputEditText(this)
        var cardTitleViewParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        cardTitleView.layoutParams = cardTitleViewParams
        cardTitleView.setText(title)
        cardTitleView.textSize = 48F
        cardTitleView.gravity = Gravity.CENTER
        cardTitleViewContainer.addView(cardTitleView)
        wrapInfo.addView(cardTitleViewContainer)

//        Start Time
        var cardStartTimeContainer:LinearLayout = LinearLayout(this)
        cardStartTimeContainer.setPadding(0, 24, 0, 24)
        var cardStartTimeContainerParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        cardStartTimeContainer.layoutParams = cardStartTimeContainerParams
        cardStartTimeContainer.orientation = LinearLayout.HORIZONTAL
        cardStartTimeContainer.gravity = Gravity.CENTER
        var cardStartTimeTitle:TextView = TextView(this)
        var cardStartTimeTitleParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        cardStartTimeTitleParam.weight = 1f
        cardStartTimeTitle.layoutParams = cardStartTimeTitleParam
        cardStartTimeTitle.gravity = Gravity.LEFT
        cardStartTimeTitle.setText("START: ")
        cardStartTimeTitle.textSize = 16F
        cardStartTimeTitle.setTypeface(null, Typeface.BOLD)
        var cardStartTimeContent:TextView = TextView(this)
        var cardStartTimeContentParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        cardStartTimeContentParam.weight = 3f
        cardStartTimeContent.layoutParams = cardStartTimeContentParam
        cardStartTimeContent.gravity = Gravity.CENTER
        cardStartTimeContent.setText(startTime)
        cardStartTimeContainer.addView(cardStartTimeTitle)
        cardStartTimeContainer.addView(cardStartTimeContent)
        wrapInfo.addView(cardStartTimeContainer)

//        Description
        var cardDescriptionContainer:LinearLayout = LinearLayout(this)
        var cardDescriptionContainerParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        cardDescriptionContainer.layoutParams = cardDescriptionContainerParams
        cardDescriptionContainer.orientation = LinearLayout.HORIZONTAL
        cardDescriptionContainer.gravity = Gravity.CENTER
        var cardDescriptionTitle:TextView = TextView(this)
        var cardDescriptionTitleParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        cardDescriptionTitleParam.weight = 5f
        cardDescriptionTitle.layoutParams = cardDescriptionTitleParam
        cardDescriptionTitle.setText("Description: ")
        cardDescriptionTitle.textSize = 16F
        cardDescriptionTitle.setTypeface(null, Typeface.BOLD)
        cardDescriptionTitle.gravity = Gravity.LEFT
        var cardDescriptionContentContainer:androidx.cardview.widget.CardView = androidx.cardview.widget.CardView(this)
        var cardDescriptionContentContainerParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        cardDescriptionContentContainerParams.weight = 2f
        cardDescriptionContentContainer.layoutParams = cardDescriptionContentContainerParams
        var cardDescriptionContent:com.google.android.material.textfield.TextInputEditText = com.google.android.material.textfield.TextInputEditText(this)
        var cardDescriptionContentParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        cardDescriptionContent.layoutParams = cardDescriptionContentParams
        cardDescriptionContent.setText(description)
        cardDescriptionContent.gravity = Gravity.CENTER
        cardDescriptionContentContainer.addView(cardDescriptionContent)
        cardDescriptionContainer.addView(cardDescriptionTitle)
        cardDescriptionContainer.addView(cardDescriptionContentContainer)
        wrapInfo.addView(cardDescriptionContainer)
//        Deadline
        var cardDeadlineForm:LinearLayout = LinearLayout(this)
        cardDeadlineForm.orientation = LinearLayout.HORIZONTAL
        var cardDeadlineFormParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        cardDeadlineForm.layoutParams = cardDeadlineFormParams
        var cardDeadlineTitle:TextView = TextView(this)
        var cardDeadlineTitleParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
        cardDeadlineTitleParams.weight = 2f
        cardDeadlineTitle.layoutParams = cardDeadlineTitleParams
        cardDeadlineTitle.setText("Deadline: ")
        cardDeadlineTitle.textSize = 16F
        cardDeadlineTitle.setTypeface(null, Typeface.BOLD)
        var cardDeadlineContent:TextView = TextView(this)
        var cardDeadlineContentParam = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
        cardDeadlineContentParam.weight = 3f
        cardDeadlineContent.setText(deadline)
        cardDeadlineContent.layoutParams = cardDeadlineContentParam
        var DeadlineBtn:Button = Button(this)
        var DeadlineBtnParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        DeadlineBtn.setText("Choose")
        DeadlineBtn.layoutParams = DeadlineBtnParam
        cardDeadlineForm.addView(cardDeadlineTitle)
        cardDeadlineForm.addView(cardDeadlineContent)
        cardDeadlineForm.addView(DeadlineBtn)
        wrapInfo.addView(cardDeadlineForm)


//        Status
        var statusForm:LinearLayout = LinearLayout(this)
        statusForm.orientation = LinearLayout.HORIZONTAL
        var statusFormParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        statusForm.layoutParams = statusFormParams
        var statusTitle:TextView = TextView(this)
        var statusTitleParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
        statusTitleParams.weight = 2f
        statusTitle.layoutParams = statusTitleParams
        statusTitle.setText("Status:")
        statusTitle.textSize = 16F
        statusTitle.setTypeface(null, Typeface.BOLD)
        var cardStatusContent:TextView = TextView(this)
        var cardStatusContentParam = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
        cardStatusContentParam.weight = 3f
        cardStatusContent.setText(status)
        cardStatusContent.layoutParams = cardStatusContentParam
        var statusBtn:Button = Button(this)
        var statusBtnParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        statusBtn.setText("Choose")
        statusBtn.layoutParams = statusBtnParam
        statusForm.addView(statusTitle)
        statusForm.addView(cardStatusContent)
        statusForm.addView(statusBtn)
        wrapInfo.addView(statusForm)

//        Order
        var cardOrderForm:LinearLayout = LinearLayout(this)
        cardOrderForm.orientation = LinearLayout.HORIZONTAL
        var cardOrderFormParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        cardOrderForm.layoutParams = cardOrderFormParams
        var cardOrderTitle:TextView = TextView(this)
        var cardOrderTitleParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
        cardOrderTitleParams.weight = 2f
        cardOrderTitle.layoutParams = cardOrderTitleParams
        cardOrderTitle.setText("Priority: ")
        cardOrderTitle.textSize = 16F
        cardOrderTitle.setTypeface(null, Typeface.BOLD)
        var cardOrderContent:TextView = TextView(this)
        var cardOrderContentParam = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
        cardOrderContentParam.weight = 3f
        cardOrderContent.setText(importantOrder)
        cardOrderContent.layoutParams = cardOrderContentParam
        var OrderBtn:Button = Button(this)
        var OrderBtnParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        OrderBtn.setText("Choose")
        OrderBtn.layoutParams = OrderBtnParam
        cardOrderForm.addView(cardOrderTitle)
        cardOrderForm.addView(cardOrderContent)
        cardOrderForm.addView(OrderBtn)
        wrapInfo.addView(cardOrderForm)
//        handle back from input task view
        btnBackFromInfoView = findViewById(R.id.returnFromInfoPage)
        btnBackFromInfoView?.setOnClickListener(){
            var intent= Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
//        handle edit task
        editBtn?.setOnClickListener(){
            mainview?.removeView(inputView)
            mainview?.removeView(btnBackFromInfoView)
            mainview?.addView(titleEdit)
            mainview?.addView(wrapInfo)
            mainview?.addView(btnDone)


        }
        btnDone.setOnClickListener(){
            storeDataToPackage(this, id, Task(cardTitleView.text.toString(), cardStatusContent.text.toString(), cardOrderContent.text.toString(), cardStartTimeContent.text.toString(), cardDeadlineContent.text.toString(), cardDescriptionContent.text.toString()))
            var intent= Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
//        handle choose option of status in edit mode
        statusBtn.setOnClickListener(){view->
            val popupMenu = PopupMenu(this, view)
            popupMenu.inflate(R.menu.menu_popup)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_item1 -> {
                        cardStatusContent.setText("Doing")
                        true
                    }
                    R.id.menu_item2 -> {
                        cardStatusContent.setText("Haven't finished")
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
        OrderBtn.setOnClickListener(){view->
            val popupMenu = PopupMenu(this, view)
            popupMenu.inflate(R.menu.menu_popup_priority)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_item1 -> {
                        cardOrderContent.setText("Finish in day")
                        true
                    }
                    R.id.menu_item2 -> {
                        cardOrderContent.setText("Finish in 2 days")
                        true
                    }
                    R.id.menu_item3 -> {
                        cardOrderContent.setText("Finish in week")
                        true
                    }
                    R.id.menu_item4 -> {
                        cardOrderContent.setText("Finish in 2 weeks")
                        true
                    }
                    R.id.menu_item5 -> {
                        cardOrderContent.setText("Finish in month")
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
        DeadlineBtn.setOnClickListener(){
            handleCalendar(cardDeadlineContent)
        }
    }
}
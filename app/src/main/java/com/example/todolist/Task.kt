package com.example.todolist

import android.icu.text.CaseMap.Title

data class Task(
    var title:String,
    var status:String,
    var importantOrder:String,
    var startTime:String,
    var deadline:String,
    var description:String)

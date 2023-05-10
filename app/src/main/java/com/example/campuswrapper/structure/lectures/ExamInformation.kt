package com.example.campuswrapper.structure.lectures

data class ExamInformation(
    val strategy: String,
    val contents: String,
    val gradingInfos: String,
    val gradingDetails: String?
) {}
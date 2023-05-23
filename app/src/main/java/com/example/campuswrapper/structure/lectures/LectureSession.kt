package com.example.campuswrapper.structure.lectures

import java.util.*

enum class LectureSessionType {
    WEEKLY, REPLACEMENT, BLOCK, VORBESPRECHUNG
}


data class LectureSession (val start: Date, val end: Date, val type: LectureSessionType, val onCampus: Boolean){}
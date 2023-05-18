package com.example.campuswrapper.structure.fetch

enum class SemesterType {
    SUMMER,
    WINTER
}


data class SearchCriteria(val year: Int, val semester: SemesterType, val studyID: Int)


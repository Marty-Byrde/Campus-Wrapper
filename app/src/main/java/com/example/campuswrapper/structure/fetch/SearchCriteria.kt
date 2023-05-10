package com.example.campuswrapper.structure.fetch

enum class SemesterType {
    SUMMER,
    WINTER
}

data class SearchCriteria(val year: Int, val type: SemesterType, val study: Int)

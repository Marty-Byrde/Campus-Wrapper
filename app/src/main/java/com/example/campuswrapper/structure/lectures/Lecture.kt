package com.example.campuswrapper.structure.lectures

import com.example.campuswrapper.LectureContributor
import java.security.InvalidParameterException
import java.util.*

class Lecture {
    var id: Int
        private set
    var name: String
        private set
    var type: Type
        private set
    var contributors: ArrayList<LectureContributor>? = null
        private set
    var ects: Double? = null
        private set
    var estimatedEffort: Double? = null
        private set
    var registrations = 0
        private set
    var registrationStart: Date? = null
        private set
    var registrationEnd: Date? = null
        private set
    var sessions: ArrayList<Date>? = null
        private set
    var description: Description? = null
        private set
    var examInformation: ExamInformation? = null
        private set

    constructor(id: Int, name: String, type: Type, ects: Double?, estimatedEffort: Double?) {
        this.id = id
        this.name = name
        this.type = type
        this.ects = ects
        this.estimatedEffort = estimatedEffort
    }

    constructor(id: Int, name: String, type: Type, contributors: ArrayList<LectureContributor>?) {
        this.id = id
        this.name = name
        this.type = type
        this.contributors = contributors
    }

    constructor(
        id: Int,
        name: String,
        type: Type,
        contributors: ArrayList<LectureContributor>?,
        ects: Double?,
        estimatedEffort: Double?,
        registrations: Int,
        registrationStart: Date?,
        registrationEnd: Date?,
        sessions: ArrayList<Date>?,
        description: Description?,
        examInformation: ExamInformation?
    ) {
        this.id = id
        this.name = name
        this.type = type
        this.contributors = contributors
        this.ects = ects
        this.estimatedEffort = estimatedEffort
        this.registrations = registrations
        this.registrationStart = registrationStart
        this.registrationEnd = registrationEnd
        this.sessions = sessions
        this.description = description
        this.examInformation = examInformation
    }

    /**
     * This method will look up more information regarding this lecture based on its id.
     */
    @Throws(Exception::class)
    fun lookUp() {
        if (id == 0) throw InvalidParameterException("Missing a lecture id!")

        // fetch ..
        ects = 123.1
        //* ... more
    }

    companion object {
        fun parseHTML(html: String?): _Lecture? {
            //* Parsing the HTML Contents
            return null
        }
    }
}
package com.example.campuswrapper.structure.lectures

import com.example.campuswrapper.LectureContributor
import com.example.campuswrapper.structure.exam.Exam
import java.security.InvalidParameterException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Lecture {
    var id: String
        private set
    var name: String
        private set
    var type: Type
        private set
    var contributors: ArrayList<LectureContributor>
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
    var sessions: ArrayList<LectureSession>? = null
        private set
    var description: HashMap<String, ArrayList<String>>? = null
        private set
    var examInformation: HashMap<String, ArrayList<String>>? = null
        private set

    var curricularPositions: ArrayList<Curricular>? = null
        private set
    var exams: ArrayList<Exam>? = null
        private set

    var href: String? = null
        private set

    var rawBasicValues: HashMap<String, Any>? = null



    constructor(id: String, name: String, type: Type, contributors: ArrayList<LectureContributor>, href: String) {
        this.id = id
        this.name = name
        this.type = type
        this.contributors = contributors
        this.href = href
    }

    constructor(id: String, name: String, type: Type, contributors: ArrayList<LectureContributor>, ects: Double?, estimatedEffort: Double?, registrations: Int, registrationStart: Date?, registrationEnd: Date?, sessions: ArrayList<LectureSession>, description: HashMap<String, ArrayList<String>>?, examInformation: HashMap<String, ArrayList<String>>?, curricularPositions: ArrayList<Curricular>, exams: ArrayList<Exam>?, href: String?) {
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
        this.curricularPositions = curricularPositions;
        this.exams = exams
        this.href = href
    }

    /**
     * This method will look up more information regarding this lecture based on its id.
     */
    @Throws(Exception::class)
    fun lookUp() {
        if (id.isBlank()) throw InvalidParameterException("Missing a lecture id!")

        // fetch ..
        ects = 123.1
        //* ... more
    }

    override fun toString(): String {
        return "Lecture(id='$id', name='$name', type=$type, contributors=$contributors, ects=$ects, estimatedEffort=$estimatedEffort, registrations=$registrations, registrationStart=$registrationStart, registrationEnd=$registrationEnd, sessions=$sessions, description=$description, examInformation=$examInformation, exams=$exams, href=$href)"
    }
}
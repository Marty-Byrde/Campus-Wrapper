package com.example.campuswrapper.structure.fetch

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.example.campuswrapper.LectureContributor
import com.example.campuswrapper.structure.exam.Exam
import com.example.campuswrapper.structure.exam.ExamMode
import com.example.campuswrapper.structure.exam.ExamNotes
import com.example.campuswrapper.structure.lectures.*
import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object Handler {
    private val TAG: String = "FETCH-Campus"
    private val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm")
    fun fetchLectures(filters: SearchCriteria): ArrayList<Lecture>? {
        //* trims year down to two digits.
        if (filters.year.toString().length <= 1) throw Error("Invalid year provided")
        val year = if (filters.year.toString().length > 2) filters.year.toString().substring(filters.year.toString().length - 2) else filters.year

        val semester = if (filters.semester == SemesterType.SUMMER) "S" else "W"
        val document: Document;
        try {
            document = Jsoup.connect("https://campus.aau.at/studien/lvliste.jsp?semester=${year}${semester}&stpkey=${filters.studyID}").get() ?: return null
        }catch (err: java.lang.Exception){
            Log.e("Fetch-Campus", "Couldnt fetch lectures because: ${err.message}")
            return null
        }

        Log.i(TAG, "Document has been fetched. (https://campus.aau.at/studien/lvliste.jsp?semester=${22}${semester}&stpkey=${filters.studyID})")

        val verbund: Element = document.getElementById("verbundInfos") ?: return null

        val tableParent = verbund.parent() ?: return null
        val table = tableParent.child(0) ?: return null
        val tbody = table.child(0) ?: return null

        val rows = tbody.children()
        rows.remove(rows[0]) //! empty row
        rows.remove(rows[0]) //! header row

        Log.d(TAG, "There are ${rows.size} lectures on this page!")

        val lectures: ArrayList<Lecture> = ArrayList()

        rows.forEach { row ->
            val lecture = parseRow(row)
            if (lecture != null) lectures.add(lecture)
        }

        return lectures
    }

    @SuppressLint("SimpleDateFormat")
    fun fetchExams(filters: SearchCriteria): ArrayList<Exam> {
        if (filters.year.toString().length <= 1) throw Error("Invalid year provided")

        //* trims year down to two digits.
        val year = if (filters.year.toString().length > 2) filters.year.toString().substring(filters.year.toString().length - 2) else filters.year
        val semester = if (filters.semester == SemesterType.SUMMER) "S" else "W"

        val document: Document = Jsoup.connect("https://campus.aau.at/studien/prliste.jsp?semester=${year}${semester}&stpkey=${filters.studyID}").get() ?: throw Error("Fetching exam-html failed!")

        val table: Element = document.getElementsByClass("nice")[0] ?: throw Error("Exams-rable not found!")
        val tbody: Element = table.child(0) ?: throw Error("Exams-tbody not found!")

        val rows = tbody.children();
        rows.remove(rows[0]) //* Headers

        val exams = ArrayList<Exam>()

        for (row in rows) {
            val idContainer = row.child(0)
            val id = idContainer.text();
            val href = idContainer.getElementsByTag("a")[0].attr("href")

            val name = row.child(1).text()
            val dates = retrieveDate(row)
            val start = dates.get("start") as Date
            val end = dates.get("end") as Date

            val location: String = when (row.children()[row.children().size - 4].text().trim()) {
                "RemoteOnlineProctoredExam" -> "ROPE"
                "RemoteOnlineProctoredExam (tatsächliche Prüfungszeit 80 min)" -> "ROPE 80min"
                "RemoteOnlineProctoredExam (tatsächliche Prüfungszeit 90 min)" -> "ROPE 90min"
                "RemoteOnlineProctoredExam (tatsächliche Prüfungszeit 100 min)" -> "ROPE 100min"
                "Open-Book-Online-Prüfung" -> "OpenBook"


                else -> row.children()[row.children().size - 4].text().trim()
            }
            val notes: ExamNotes = when (row.children()[row.children().size - 3].text()) {
                "ohne" -> ExamNotes.NONE
                "teilweise" -> ExamNotes.SOME
                "mit" -> ExamNotes.ALLOWED

                else -> ExamNotes.UNKNOWN
            }
            val mode: ExamMode = when (row.children()[row.children().size - 2].text()) {
                "online" -> ExamMode.DIGITAL
                "schriftlich" -> ExamMode.PEN_PAPER
                "schriftlich und mündlich" -> ExamMode.VERBAL_PAPER
                else -> ExamMode.UNKNOWN
            }


            exams.add(Exam(id, name, start, end, location, notes, mode, href))
        }


        return exams
    } //    public static ArrayList<>

    @SuppressLint("SimpleDateFormat")
    private fun retrieveDate(row: Element): JSONObject {
        val obj = JSONObject()
        var start: Date
        var end: Date
        var formatter: SimpleDateFormat

        if (row.child(2).text().contains("Online Slot")) {
            var value: String = row.child(2).text()
            value = value.replace("Online Slot-Prüfung vom", "").trim()

            formatter = SimpleDateFormat("dd.MM.yyyy")
            start = formatter.parse(value.split("bis")[0].trim()) ?: Date()
            end = formatter.parse(value.split("bis")[1].trim()) ?: Date()
        } else {
            val baseDate: String = row.child(2).text();
            val baseStart: String = row.child(3).text();
            val baseEnd: String = row.child(4).text();

            formatter = SimpleDateFormat("dd.MM.yyyy HH:mm")
            start = formatter.parse("$baseDate $baseStart") ?: Date()
            end = formatter.parse("$baseDate $baseEnd") ?: Date()
        }

        obj.put("start", start)
        obj.put("end", end)

        return obj;
    }

    private fun parseRow(row: Element): Lecture? {
        val idContainer: Element = row.child(0)
        val typeContainer: Element = row.child(1)
        val nameContainer: Element = row.child(2)
        val authorContainer: Element = row.child(4)

        val id = idContainer.text()
        val type = typeContainer.text()
        val href = idContainer.getElementsByTag("a")[0]?.attr("href")?.split(";")?.get(0)
        val name = nameContainer.text()
        val authors: ArrayList<LectureContributor> = ArrayList()

        //* Adding authors
        for (author in authorContainer.children()) authors.add(LectureContributor(lastName = author.text().split(" ")[0], firstName = author.text().split(" ")[1]))

        return Lecture(id, name, Type.valueOf(type), authors, href.toString())
    }


    fun retrieveLectureDetails(context: Context, baseLecture: Lecture): Lecture? {
        if (baseLecture.href?.isBlank() == true) throw Error("No web-reference provided!")

        val id = baseLecture.href!!.split("/").last()
        val fetchRef = "http://10.0.2.2/course?id=$id";

        Log.d("Fetch-Campus", "Fetching lecture details from $fetchRef")
        val document: Document;
        try {
            document = Jsoup.connect(fetchRef).get() ?: throw Error("Fetching lecture details failed!")
        } catch (e: Exception) {
            Log.e("Fetch-Campus", "Fetching lecture details failed!")
            return null
        }


        val infoContainer = document.getElementById("uebersicht") ?: throw Error("Info-Container of course page is missing!")
        val table: Element = infoContainer.getElementsByClass("taglib-dl")[0]

        val imagesScript = document.getElementById("contributor-images") ?: Element("div")
        val images = JSONArray(imagesScript.data())

        val values = HashMap<String, Any>()
        for (i in 0 until table.childrenSize() step 2) {
            val fieldName = table.child(i)
            val fieldValue: Element = table.child(i + 1)

            var value: Any = fieldValue.text().replace(fieldName.text(), "")
            val key: String = when (fieldName.text().lowercase()) {
                "lehrende/r" -> "contributors"
                "tutor/in/innen" -> "contributors"
                "lv-titel englisch" -> "titleEN"
                "lv-art" -> "type"
                "lv-modell" -> "model"
                "semesterstunde/n" -> "estimatedEffort"
                "ects-anrechnungspunkte" -> "ects"
                "anmeldungen" -> "registrations"
                "organisationseinheit" -> "organizedBy"
                "unterrichtssprache" -> "language"
                "lv-beginn" -> "start"
                "elearning" -> "moodle"
                "mögliche sprache/n der leistungserbringung" -> "subLanguages"
                "anmerkungen" -> "notes"
                "online-anteil" -> "onlineRatio"
                "Studienberechtigungsprüfung" -> "examRequirement"

                else -> {
                    fieldName.text()
                }
            }

            if (key == "registrations") value = value.toString().trim().split(" ")[0]
            if (key == "contributors") {
                val authors = fieldValue.getElementsByTag("ul")[0]
                val contributors = ArrayList<LectureContributor>()

                var index = 0;
                for (author in authors.children()) {
                    val name = author.text();
                    contributors.add(LectureContributor(null, name, "", null, null, null, null, if (images.length() - 1 < index) "images/card/keinbild.jpg" else images.getString(index++)))
                }

                value = contributors
            }

            values[key] = value
        }

        Log.d("Fetch-Campus", "Parsed basic details, such as contributors and title")

        //? Lecture Schedule
        val sessions = ArrayList<LectureSession>()
        val scheduleContainer = document.getElementById("weeklyEventsSparse") ?: throw Error("Schedule-Container of course page is missing!")

        for (schedule in scheduleContainer.children()) {
            val internalContainer: Element = schedule.getElementsByClass("date-time-child")[0]
            val baseDate = internalContainer.child(0).text().split("-")[1]
            val baseTimes = internalContainer.child(1).text()

            val type = internalContainer.child(0).getElementsByTag("span")[0].attr("title")
            val start: Date = formatter.parse("$baseDate ${baseTimes.split("-")[0]}") ?: Date()
            val end: Date = formatter.parse("$baseDate ${baseTimes.split("-")[1]}") ?: Date()

            val onCampus = internalContainer.child(2).getElementsByClass("label")[0]
            val room = internalContainer.child(2).child(0)

            val noteContainer = internalContainer.child(3)
            val notes = if (noteContainer.getElementsByClass("emptyNote").size == 0) null else noteContainer.text()

            if (type.uppercase().trim() == "STORNIERT") continue;

            val lectureType = when (type.uppercase()) {
                "WÖCHENTLICH" -> LectureSessionType.WEEKLY
                "VORBESPRECHUNG" -> LectureSessionType.VORBESPRECHUNG
                "BLOCK" -> LectureSessionType.BLOCK
                "ERSATZ" -> LectureSessionType.REPLACEMENT

                else -> LectureSessionType.VORBESPRECHUNG
            }

            val session = LectureSession(start, end, lectureType, onCampus.text().lowercase() == "on campus", room.text(), notes)
            sessions.add(session)
        }
        Log.d("Fetch-Campus", "Parsed schedule of lecture, there are ${sessions.size} sessions!")

        var elementContainer: Element;

        //? Lecture Description
        val descriptionContainer: Element = document.getElementById("lzk-lang-tabs") ?: throw Error("Description-Container of course page is missing!")
        elementContainer = if (descriptionContainer.childrenSize() > 0) descriptionContainer.child(0) else Element("div")
        val lectureDescription = parseContainer(elementContainer, "h2")
        Log.i("Fetch-Campus", "Parsed description of lecture, there are ${lectureDescription.size} sections!")


        //? Exam Information
        val examInfoContainer: Element = document.getElementById("exam-lang-tabs") ?: throw Error("Exam-Information-Container of course page is missing!")
        elementContainer = if (examInfoContainer.childrenSize() > 0) examInfoContainer.child(0) else Element("div")
        val examInformations = parseContainer(elementContainer, "h3")

        Log.d("Fetch-Campus", "Parsed Exam Information of lecture, there are ${examInformations.size} sections!")

        //? Curriculum position
        val curriculumContainer: Element = document.getElementById("card-content-stp-stellung-header") ?: throw Error("Curricular-Container of course page is missing!")
        val curriculars = ArrayList<Curricular>()

        for (curriculum in curriculumContainer.children()) {
            if (curriculum.tagName() != "ul") continue;

            val entries = parseCurricularGroup(curriculum, ArrayList<String>())
            val curricular = Curricular(
                entries[0],
                entries[1],
                entries[2],
                entries[3].split(" ")[0],
                if (entries.size > 4) entries[4] else null,
            )

            curriculars.add(curricular)
        }
        Log.w("Fetch-Campus", "Parsed ${curriculars.size} curricular entries!")


        Log.d("Fetch-Campus", "Finished parsing lecture details!")
        Log.d("Fetch-Campus", "")

        val lecture = Lecture(
            id = baseLecture.id,
            name = baseLecture.name,
            type = baseLecture.type,
            contributors = values["contributors"] as ArrayList<LectureContributor>,
            ects = values["ects"]?.toString()?.toDouble() ?: -1.0,
            estimatedEffort = values["estimatedEffort"]?.toString()?.toDouble() ?: -1.0,
            registrations = values["registrations"]?.toString()?.toInt() ?: -1,
            registrationStart = values["registrationStart"] as Date?,
            registrationEnd = values["registrationEnd"] as Date?,
            sessions = sessions,
            description = lectureDescription,
            curricularPositions = curriculars,
            examInformation = examInformations,
            exams = null,
            href = "https://campus.aau.at/${baseLecture.href}",
        )
        lecture.rawBasicValues = values

        return lecture
    }

    /**
     * This function will parse a given container by setting each heading as a key and all the elements between headings as values of the previous heading.
     */
    private fun parseContainer(container: Element, headingTag: String): HashMap<String, ArrayList<String>> {
        val map = HashMap<String, ArrayList<String>>()

        var lastHeading: String = "";
        for (element in container.children()) {
            if (element.text().isBlank()) continue
            var list = ArrayList<String>()

            //* The first element does not belong to any heading
            if (map.keys.size == 0 && element.tagName() != headingTag) {
                map[element.text()] = list
                continue
            }

            //* Adding a heading as a category
            if (element.tagName() == headingTag) {
                map[element.text()] = list
                lastHeading = element.text()
                continue
            }

            // Add text element that to a heading (=category)
            list = map[lastHeading] ?: ArrayList()
            list.add(element.text())
            map[lastHeading] = list
        }

        return map
    }

    /**
     * This function parses a curricular entry ([group]) to [entries], which are stored as Strings in an [ArrayList]. This function goes through a curricular entry recursively.
     */
    private fun parseCurricularGroup(group: Element, entries: ArrayList<String>): ArrayList<String> {
        val subGroups = group.getElementsByClass("list-group")
        if (subGroups.size == 1) {
            entries.add(group.text().trim())
            return entries
        }

        val layerText = group.text().split(subGroups[1].text())[0].trim()
        entries.add(layerText)

        return parseCurricularGroup(subGroups[1], entries)
    }
}

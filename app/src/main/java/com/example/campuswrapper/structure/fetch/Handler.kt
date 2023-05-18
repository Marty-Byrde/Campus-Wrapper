package com.example.campuswrapper.structure.fetch
import android.annotation.SuppressLint
import android.util.Log
import com.example.campuswrapper.LectureContributor
import com.example.campuswrapper.structure.exam.Exam
import com.example.campuswrapper.structure.exam.ExamMode
import com.example.campuswrapper.structure.exam.ExamNotes
import com.example.campuswrapper.structure.lectures.Lecture
import com.example.campuswrapper.structure.lectures.Type
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.text.SimpleDateFormat
import java.util.*

object Handler {
    private val TAG: String = "FETCH-Campus"
    fun fetchLectures(filters: SearchCriteria): ArrayList<Lecture>? {
        //* trims year down to two digits.
        if (filters.year.toString().length <= 1) throw Error("Invalid year provided")
        val year = if (filters.year.toString().length > 2) filters.year.toString()
            .substring(filters.year.toString().length - 2) else filters.year

        val semester = if (filters.semester == SemesterType.SUMMER) "S" else "W"
        val document: Document =
            Jsoup.connect("https://campus.aau.at/studien/lvliste.jsp?semester=${22}${semester}&stpkey=${filters.studyID}")
                .get() ?: return null

        Log.i(
            TAG,
            "Document has been fetched. (https://campus.aau.at/studien/lvliste.jsp?semester=${22}${semester}&stpkey=${filters.studyID})"
        )

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
            if(lecture != null) lectures.add(lecture)
        }

        return lectures
    }

    @SuppressLint("SimpleDateFormat")
    fun fetchExams(filters: SearchCriteria): ArrayList<Exam> {
        if (filters.year.toString().length <= 1) throw Error("Invalid year provided")

        //* trims year down to two digits.
        val year = if (filters.year.toString().length > 2) filters.year.toString().substring(filters.year.toString().length - 2) else filters.year
        val semester = if (filters.semester == SemesterType.SUMMER) "S" else "W"

        val document: Document = Jsoup.connect("https://campus.aau.at/studien/prliste.jsp?semester=${22}${semester}&stpkey=${filters.studyID}").get() ?: throw Error("Fetching exam-html failed!")

        val table: Element = document.getElementsByClass("nice")[0] ?: throw Error("Exams-rable not found!")
        val tbody: Element = table.child(0) ?: throw Error("Exams-tbody not found!")

        val rows = tbody.children();
        rows.remove(rows[0]) //* Headers

        val exams = ArrayList<Exam>()

        for(row in rows){
            val idContainer = row.child(0)
            val id = idContainer.text();
            val href = idContainer.getElementsByTag("a")[0].attr("href")

            val name = row.child(1).text()
            val dates = retrieveDate(row)
            val start = dates.get("start") as Date
            val end = dates.get("end") as Date

            val location: String = row.children()[row.children().size-3].text()
            val notes: ExamNotes = when(row.children()[row.children().size-2].text()){
                "ohne" -> ExamNotes.NONE
                "teilweise" -> ExamNotes.SOME
                "mit" -> ExamNotes.ALLOWED

                else -> ExamNotes.UNKNOWN
            }
            val mode: ExamMode = when(row.children()[row.children().size-1].text()){
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
    private fun retrieveDate(row: Element) : JSONObject {
        val obj = JSONObject()
        var start = Date()
        var end = Date()
        var formatter = SimpleDateFormat("dd.MM.yyyy HH:mm")

        if(row.child(2).text().contains("Online Slot")){
            var value: String = row.child(2).text()
            value = value.replace("Online Slot-Prüfung vom", "").trim()

            formatter = SimpleDateFormat("dd.MM.yyyy")
            start = formatter.parse(value.split("bis")[0].trim()) ?: Date()
            end = formatter.parse(value.split("bis")[1].trim()) ?: Date()
        }else{
            val baseDate: String = row.child(2).text();
            val baseStart: String = row.child(3).text();
            val baseEnd: String = row.child(4).text();

            val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm")
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
        val authorContainer : Element = row.child(4)

        val id = idContainer.text()
        val type = typeContainer.text()
        val href = idContainer.getElementsByTag("a")[0]?.attr("href")?.split(";")?.get(0)
        val name = nameContainer.text()
        val authors: ArrayList<LectureContributor> = ArrayList()

        //* Adding authors
        for(author in authorContainer.children()) authors.add(LectureContributor(lastName = author.text().split(" ")[0], firstName = author.text().split(" ")[1]))

        return Lecture(id, name, Type.valueOf(type), authors, href.toString())
    }
}

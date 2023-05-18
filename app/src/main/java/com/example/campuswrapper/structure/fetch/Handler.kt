package com.example.campuswrapper.structure.fetch
import android.util.Log
import com.example.campuswrapper.LectureContributor
import com.example.campuswrapper.structure.lectures.Lecture
import com.example.campuswrapper.structure.lectures.Type
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

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

    fun fetchExams(filters: SearchCriteria?): ArrayList<Lecture>? {
        return null
    } //    public static ArrayList<>

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

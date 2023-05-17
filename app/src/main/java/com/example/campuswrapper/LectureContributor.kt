package com.example.campuswrapper

class LectureContributor (val title: String?, val firstName : String, val lastName: String, val email: String?, val phone: String?, val office: String?, val profile: String?){
    constructor(firstName: String, lastName: String) : this(null, firstName, lastName, null, null, null, null)

    override fun toString(): String {
        return "LectureContributor(title=$title, firstName='$firstName', lastName='$lastName', email=$email, phone=$phone, office=$office, profile=$profile)"
    }

}
package com.example


import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}



fun Application.init() {
    routing {
        get("/") {
            call.respondText("Hello, world!")
        }
        get("/greeting") {
            call.respondText("This is my greeting G'Day mate")
        }
    }
}
---((CRAD))---
package com.example


import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

var people = mutableListOf<Person>()
var nextId = 1

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.init() {

    people.add(Person(nextId++,"Fred","Cole"))
    people.add(Person(nextId++,"Sue", "Stojic"))
    people.add(Person(nextId++,"John", "Doe"))

    install(ContentNegotiation){
        json()
    }

    routing {
        get("/people") {

            call.respond(people)
        }

        get("/people/{id}"){

            val id = call.parameters["id"]?.toInt()
            var person = people.find{ it.id == id }

            if(person != null){
                call.respond(person)
            }
            else {
                call.respondText("No person with the id $id", status = HttpStatusCode.NotFound)
            }
        }

        post("/people"){
            //getting the data that was posted to the server
            var person = call.receive<Person>()
            //adding the new person to the people collection
            person.id = nextId++

            people.add(person)
            //send a response back to the client
            call.respondText("Person Create",status = HttpStatusCode.Created)
        }
        delete("/people/{id}"){

            val id = call.parameters["id"]?.toInt()
            val isDeleted = people.removeIf{ it.id == id }
            if(isDeleted){
                call.respondText("Person with ID: $id has been removed",status = HttpStatusCode.NoContent)
            }
            else{
                call.respondText("No person with the id $id", status = HttpStatusCode.NotFound)
            }
        }

        put("/people/{id}"){

            val id = call.parameters["id"]?.toInt()
            var original = people.find{ it.id == id }

            if(original != null){
                var updated = call.receive<Person>()
                original.firstName = updated.firstName
                original.lastName = updated.lastName
                call.respond(original)
            }
            else{
                call.respondText("No person with the id $id", status = HttpStatusCode.NotFound)
            }
        }
    }
}

@Serializable

class Person(var firstName: String, var lastName: String) {

    constructor(id: Int, firstName: String, lastName: String) : this(firstName, lastName) {
        this.id = id
    }
    var id: Int = 0
    get
    set
}

package app

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
open class QueueApp {
}

fun main() {
    SpringApplication.run(QueueApp::class.java)
}
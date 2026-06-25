package com.schoolfinder

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class SchoolFinderApplication

fun main(args: Array<String>) {
    runApplication<SchoolFinderApplication>(*args)
}

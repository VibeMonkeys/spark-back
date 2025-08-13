package com.monkeys.spark

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SparkApplication

fun main(args: Array<String>) {
	runApplication<SparkApplication>(*args)
}

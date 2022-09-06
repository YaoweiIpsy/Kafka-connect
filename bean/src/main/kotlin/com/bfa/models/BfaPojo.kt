package com.bfa.models

@Target(allowedTargets = arrayOf(AnnotationTarget.CLASS))
annotation class BfaPojo(val name: String, val group: String, val version:String)


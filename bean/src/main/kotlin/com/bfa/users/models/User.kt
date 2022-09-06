package com.bfa.users.models

import com.bfa.models.BfaPojo

@BfaPojo(name="user", group="com.bfa.users", version="1.0.0")
data class User(val name:String, val lastUpdated:Long = System.currentTimeMillis())

@BfaPojo(name="user", group = "com.bfa.users", version="2.0.0")
data class UserWithFLName(val firstName:String, val lastName:String, val lastUpdated:Long = System.currentTimeMillis())

@BfaPojo(name="user", group = "com.bfa.users", version = "2.1.0")
data class UserWithAge(val firstName: String, val lastName: String, val lastUpdated:Long = System.currentTimeMillis(), val age:Int = 0)

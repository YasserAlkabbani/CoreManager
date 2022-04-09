package com.yasser.coremanager.manager

fun String.asTextManager() =TextManager.StringText(this)
fun Int.asTextManager() =TextManager.ResourceText(this)
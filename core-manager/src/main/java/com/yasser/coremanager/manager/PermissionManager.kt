package com.yasser.coremanager.manager

sealed class PermissionManager {
    abstract val taskToDoWhenPermissionGranted:()->Unit
    abstract val taskToDoWhenPermissionDeclined:()->Unit
    abstract val showRequestPermissionRationale:()->Unit
    class ReadExternalStorage(
        override val taskToDoWhenPermissionGranted:()->Unit,
        override val taskToDoWhenPermissionDeclined:()->Unit,
        override val showRequestPermissionRationale:()->Unit
    ):PermissionManager()
    class RecordAudio(
        override val taskToDoWhenPermissionGranted:()->Unit,
        override val taskToDoWhenPermissionDeclined:()->Unit,
        override val showRequestPermissionRationale:()->Unit
    ):PermissionManager()
    class Camera(
        override val taskToDoWhenPermissionGranted:()->Unit,
        override val taskToDoWhenPermissionDeclined:()->Unit,
        override val showRequestPermissionRationale:()->Unit
    ):PermissionManager()
    class CallPhone(
        override val taskToDoWhenPermissionGranted:()->Unit,
        override val taskToDoWhenPermissionDeclined:()->Unit,
        override val showRequestPermissionRationale:()->Unit
    ):PermissionManager()
    class SendSMSPermission(
        override val taskToDoWhenPermissionGranted:()->Unit,
        override val taskToDoWhenPermissionDeclined:()->Unit,
        override val showRequestPermissionRationale:()->Unit,
    ):PermissionManager()
    class ReadCallLogPermission(
        override val taskToDoWhenPermissionGranted:()->Unit,
        override val taskToDoWhenPermissionDeclined:()->Unit,
        override val showRequestPermissionRationale:()->Unit,
    ):PermissionManager()
    class WriteCallLogPermission(
        override val taskToDoWhenPermissionGranted:()->Unit,
        override val taskToDoWhenPermissionDeclined:()->Unit,
        override val showRequestPermissionRationale:()->Unit,
    ):PermissionManager()
    class ReadPhoneStatePermission(
        override val taskToDoWhenPermissionGranted:()->Unit,
        override val taskToDoWhenPermissionDeclined:()->Unit,
        override val showRequestPermissionRationale:()->Unit,
    ):PermissionManager()
    class LocationPermission(
        override val taskToDoWhenPermissionGranted:()->Unit,
        override val taskToDoWhenPermissionDeclined:()->Unit,
        override val showRequestPermissionRationale:()->Unit,
    ):PermissionManager()
    class CustomPermission(
        val permission:String,
        override val taskToDoWhenPermissionGranted:()->Unit,
        override val taskToDoWhenPermissionDeclined:()->Unit,
        override val showRequestPermissionRationale:()->Unit,
    ):PermissionManager()
}
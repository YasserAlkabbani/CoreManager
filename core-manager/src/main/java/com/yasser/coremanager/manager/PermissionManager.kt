package com.yasser.coremanager.manager

sealed class PermissionManager {
    abstract val taskToDoWhenPermissionDeclined:()->Unit
    abstract val showRequestPermissionRationale:()->Unit
    abstract val taskToDoWhenPermissionGranted:()->Unit
    class ReadExternalStorage(
        override val taskToDoWhenPermissionDeclined:()->Unit={},
        override val showRequestPermissionRationale:()->Unit={},
        override val taskToDoWhenPermissionGranted:()->Unit,
    ):PermissionManager()
    class RecordAudio(
        override val taskToDoWhenPermissionDeclined:()->Unit={},
        override val showRequestPermissionRationale:()->Unit={},
        override val taskToDoWhenPermissionGranted:()->Unit,
    ):PermissionManager()
    class Camera(
        override val taskToDoWhenPermissionDeclined:()->Unit={},
        override val showRequestPermissionRationale:()->Unit={},
        override val taskToDoWhenPermissionGranted:()->Unit,
    ):PermissionManager()
    class CallPhone(
        override val taskToDoWhenPermissionDeclined:()->Unit={},
        override val showRequestPermissionRationale:()->Unit={},
        override val taskToDoWhenPermissionGranted:()->Unit,
    ):PermissionManager()
    class SendSMSPermission(
        override val taskToDoWhenPermissionDeclined:()->Unit={},
        override val showRequestPermissionRationale:()->Unit={},
        override val taskToDoWhenPermissionGranted:()->Unit,
    ):PermissionManager()
    class ReadCallLogPermission(
        override val taskToDoWhenPermissionDeclined:()->Unit={},
        override val showRequestPermissionRationale:()->Unit={},
        override val taskToDoWhenPermissionGranted:()->Unit,
    ):PermissionManager()
    class WriteCallLogPermission(
        override val taskToDoWhenPermissionDeclined:()->Unit={},
        override val showRequestPermissionRationale:()->Unit={},
        override val taskToDoWhenPermissionGranted:()->Unit,
    ):PermissionManager()
    class ReadPhoneStatePermission(
        override val taskToDoWhenPermissionDeclined:()->Unit={},
        override val showRequestPermissionRationale:()->Unit={},
        override val taskToDoWhenPermissionGranted:()->Unit,
    ):PermissionManager()
    class LocationPermission(
        override val taskToDoWhenPermissionDeclined:()->Unit={},
        override val showRequestPermissionRationale:()->Unit={},
        override val taskToDoWhenPermissionGranted:()->Unit,
    ):PermissionManager()
    class CustomPermission(
        val permission:String,
        override val taskToDoWhenPermissionDeclined:()->Unit={},
        override val showRequestPermissionRationale:()->Unit={},
        override val taskToDoWhenPermissionGranted:()->Unit,
    ):PermissionManager()
}
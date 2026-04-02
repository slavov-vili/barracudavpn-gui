package de.cas.barracudavpn_gui

const val VPN_CMD = "barracudavpn"

class VPNActions {

    companion object {
        fun status(): String {
            return execute("--status")
        }

        fun start(username: String, password: String, oneTimePassword: String): String {
            return execute("--start", "--login", username, "--serverpwd", password, "--onetimepwd", oneTimePassword)
        }

        fun stop(): String {
            return execute("--stop")
        }

        fun execute(vararg args: String): String {
            val command = listOf(VPN_CMD) + args

            val pb = ProcessBuilder(command)
            pb.redirectErrorStream()

            val process = pb.start()
            return process.inputStream.bufferedReader().readText()
        }
    }
}
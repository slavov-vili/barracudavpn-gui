package de.cas.barracudavpn_gui

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

const val VPN_CMD = "barracudavpn"

class VPNActions {

    companion object {
        fun status(): Flow<String> {
            return execute("--status")
        }

        fun start(username: String, password: String, oneTimePassword: String): Flow<String> {
            return execute("--start", "--login", username, "--serverpwd", password, "--onetimepwd", oneTimePassword)
        }

        fun stop(): Flow<String> {
            return execute("--stop")
        }

        fun execute(vararg args: String): Flow<String> = flow {
            val command = listOf(VPN_CMD) + args

            val pb = ProcessBuilder(command)
            pb.redirectErrorStream()

            val process = pb.start()
            val reader = process.inputStream.bufferedReader()
            reader.useLines { lines ->
                lines.forEach { line ->
                    emit(line)
                }
            }
            process.waitFor()
        }
            .flowOn(Dispatchers.IO)
    }
}
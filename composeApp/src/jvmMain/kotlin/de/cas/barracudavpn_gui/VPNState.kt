package de.cas.barracudavpn_gui

import kotlin.text.RegexOption.IGNORE_CASE

data class VPNState(val status: VPNStatus, val content: String? = null) {
    companion object {
        val PATTERN_STATUS = Regex("""(Status|State): *([a-z]+)""", option = IGNORE_CASE)

        fun unknown(content: String? = null): VPNState {
            return VPNState(VPNStatus.UNKNOWN, content)
        }

        fun fromString(str: String): VPNState {
            var status: VPNStatus = VPNStatus.UNKNOWN
            var content: String = ""

            for (line in str.lines()) {
                val match = PATTERN_STATUS.find(line)
                if (match != null) {
                    status = VPNStatus.fromString(match.groupValues[2])
                } else {
                    // TODO: skip leading and trailing empty lines
                    content = "${content}${line}\n"
                }
            }

            return VPNState(status, content)
        }
    }
}

enum class VPNStatus {
    CONNECTED,
    RECONNECTING,
    DISCONNECTED,
    UNKNOWN;

    companion object {
        fun fromString(str: String?): VPNStatus {
            return entries.find { it.name.equals(str, ignoreCase = true) }
                ?: UNKNOWN
        }
    }
}
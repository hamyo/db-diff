import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy
import java.nio.charset.Charset

import static ch.qos.logback.classic.Level.INFO

appender("file", RollingFileAppender) {
    file = "dbdiff.log"
    rollingPolicy(FixedWindowRollingPolicy) {
        fileNamePattern = "logs/dbdiff-%i.log.zip"
        minIndex = 1
        maxIndex = 20
    }
    triggeringPolicy(SizeBasedTriggeringPolicy) {
        maxFileSize = "50MB"
    }
    encoder(PatternLayoutEncoder) {
        charset = Charset.forName("UTF-8")
        pattern = "%date %level [%thread] [%logger{10}] %msg%ex - %caller{1}"
    }
}
appender("console", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        charset = Charset.forName("UTF-8")
        pattern = "%date %-5level [%thread] [%logger{10}] %msg%ex - %caller{1}"
    }
}
root(INFO, ["file", "console"])
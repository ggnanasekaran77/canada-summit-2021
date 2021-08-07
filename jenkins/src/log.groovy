import java.util.logging.Level
import java.util.logging.Logger

Logger.getLogger("").setLevel(Level.WARNING)
Logger.getLogger("org.apache.sshd").setLevel(Level.WARNING)
Logger.getLogger("winstone").setLevel(Level.WARNING)

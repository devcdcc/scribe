package scribe.writer.action

import java.nio.file.{Files, Path}

import scribe.util.Time
import scribe.writer.FileWriter
import scribe.writer.file.LogFile

case class UpdatePathAction(path: Long => Path, gzip: Boolean, checkRate: Long) extends UpdateLogFileAction {
  override def update(current: LogFile): LogFile = rateDelayed(checkRate, current) {
    val newPath = path(Time())
    if (FileWriter.samePath(current.path, newPath)) {
      current
    } else {
      val replacement = current.replace(path = newPath)
      if (gzip && Files.exists(current.path) && Files.size(current.path) > 0L) {
        current.gzip()
      }
      replacement
    }
  }
}

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import java.io.File



class FileApi(private val host: String) {
  val client = HttpClient(CIO) {
    install(JsonFeature) {
      serializer = GsonSerializer()
    }
  }

  suspend fun getFiles(): FileInfo {
    try {
      return client.get<GetFilesResponse>("$host/files").files
    } catch (e: Exception) {
      e.printStackTrace()
    }
    return emptyMap()
  }

  suspend fun upload(file: File, onUpload: suspend (Long, Long) -> Unit = { _, _ -> }): Boolean {
    if (file.exists()) {
      upload(file.readBytes(), file.name, onUpload)
    }
    return false
  }

  suspend fun upload(data: ByteArray, name: String, onUpload: suspend (Long, Long) -> Unit = { _, _ -> }): Boolean {
    val response: HttpResponse = client.submitFormWithBinaryData(
      url = "$host/files",
      formData = formData {
        append("file", data, Headers.build {
          append(HttpHeaders.ContentDisposition, "filename=$name")
        })
      }
    ) {
      onUpload(onUpload)
    }
    return response.status == HttpStatusCode.OK
  }

  suspend fun download(filename: String, onDownload: suspend (Long, Long) -> Unit = { _, _ -> }): ByteArray? {
    val httpResponse: HttpResponse = client.get("$host/files/$filename") {
      onDownload(onDownload)
    }
    return if (httpResponse.status == HttpStatusCode.OK) httpResponse.receive() else null
  }

  suspend fun delete(filename: String): Boolean {
    val response: HttpResponse = client.delete("$host/files/$filename")
    return response.status == HttpStatusCode.OK
  }
}

typealias FileInfo = Map<String, String>
data class GetFilesResponse(val files: Map<String, String>)
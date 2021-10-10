# web-file-client

```
  val api = FileApi("http://localhost:8080")
  val file = File("test.txt")
  api.upload(file) { bytesSentTotal, contentLength ->
    println("Uploaded $bytesSentTotal bytes from $contentLength")
  }
  // or
  api.upload("text".toByteArray(), "text.txt") { bytesSentTotal, contentLength ->
    println("Uploaded $bytesSentTotal bytes from $contentLength")
  }
  val fileMap = api.getFiles()
  val bytes = api.download("test.txt") { bytesSentTotal, contentLength ->
    println("Received $bytesSentTotal bytes from $contentLength")
  }
  api.delete("test.txt")
```

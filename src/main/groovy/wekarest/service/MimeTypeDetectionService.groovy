package wekarest.service

import groovy.util.logging.Log4j
import org.apache.tika.Tika
import org.springframework.stereotype.Service
import wekarest.model.ClassificationResult
import wekarest.model.JobInfo

import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future

import static wekarest.model.JobInfo.JobStatus.*

@Service
@Log4j
class MimeTypeDetectionService {

    String getMimeType(String fileName, byte[] content) {
        def tika = new Tika()
        return tika.detect(content, fileName)
    }

}

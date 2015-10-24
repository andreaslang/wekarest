package wekarest.controller

import groovy.json.JsonOutput
import groovy.util.logging.Log4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.multipart.MultipartFile
import wekarest.model.Data
import wekarest.model.FileMetaData
import wekarest.model.UploadResult
import wekarest.mongodb.FileRepository
import wekarest.service.ConfigurationService
import wekarest.service.DataAccessService
import wekarest.service.MimeTypeDetectionService

import static groovy.json.JsonOutput.toJson

@Log4j
@Controller
class FileUploadController {

    @Autowired
    FileRepository repository

    @Autowired
    ConfigurationService configurationService

    @Autowired
    DataAccessService dataAccessService

    @Autowired
    MimeTypeDetectionService mimeTypeDetectionService

    @RequestMapping(value='/upload', method=RequestMethod.GET)
    @ResponseBody String provideUploadInfo() {
        return 'You can upload a file by posting to this same URL.'
    }

    @RequestMapping(value="/upload", method=RequestMethod.POST)
    @ResponseBody String handleFileUpload(
            @RequestParam(value = 'name', required = false, defaultValue = 'file.csv') String name,
            @RequestParam('file') MultipartFile file) {
        def response
        if (!file.isEmpty()) {
            def bytes = file.bytes
            Data data = dataAccessService.store(bytes)
            def type = mimeTypeDetectionService.getMimeType(name, bytes)
            checkIfLoaderForFileTypeExists(type)
            def fileMetaData = new FileMetaData(name:  name, dataHash: data.hash, type: type)
            def metaDataJson = toJson(fileMetaData)
            def metaData = dataAccessService.store(metaDataJson)
            response = new UploadResult(
                    status: 'SUCCESS', message: "File uploaded! Hash: ${metaData.hash}"
            )
        } else {
            response = new UploadResult(
                    status: 'FAILURE', message: 'You failed to upload $name because the file was empty.'
            )
        }
        return toJson(response)
    }

    private void checkIfLoaderForFileTypeExists(String type) {
        def loaderName = configurationService.getInstanceLoaderClassName(type)
        if (!loaderName)
            throw new RuntimeException("Unknwon file type $type")
    }

}
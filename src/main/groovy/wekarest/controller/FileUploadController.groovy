package wekarest.controller

import groovy.util.logging.Log4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.multipart.MultipartFile
import wekarest.model.Data
import wekarest.model.UploadResult
import wekarest.mongodb.FileRepository
import wekarest.service.DataAccessService

import static groovy.json.JsonOutput.toJson

@Log4j
@Controller
class FileUploadController {

    @Autowired
    FileRepository repository;

    @Autowired
    DataAccessService dataAccessService;

    @RequestMapping(value='/upload', method=RequestMethod.GET)
    @ResponseBody String provideUploadInfo() {
        return 'You can upload a file by posting to this same URL.'
    }

    @RequestMapping(value="/upload", method=RequestMethod.POST)
    @ResponseBody String handleFileUpload(@RequestParam('file') MultipartFile file) {
        def response
        if (!file.isEmpty()) {
            Data dataFile = dataAccessService.store(file.bytes)
            response = new UploadResult(
                    status: 'SUCCESS', message: "File uploaded! Hash: ${dataFile.hash}"
            )
        } else {
            response = new UploadResult(
                    status: 'FAILURE', message: 'You failed to upload $name because the file was empty.'
            )
        }
        return toJson(response)
    }

}
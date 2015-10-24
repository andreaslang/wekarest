package wekarest.controller

import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import wekarest.model.ClassificationOptions
import wekarest.model.ClassificationResult
import wekarest.model.FileMetaData
import wekarest.service.ClassificationService
import wekarest.service.DataAccessService
import wekarest.service.JobService

import static groovy.json.JsonOutput.toJson

@RestController
@RequestMapping('/classification')
class ClassificationController {

    @Autowired
    JobService jobService

    @Autowired
    DataAccessService dataAccessService

    @Autowired
    ClassificationService classificationService

    @RequestMapping(method=RequestMethod.POST)
    String createClassification(@RequestBody ClassificationOptions options) {
        def fileHash = options.fileHash
        def optionHash = toJson(options).asMD5()
        def classificationHash = "${optionHash}${fileHash}".asMD5()
        jobService.createJob(classificationHash) {
            def metaData = dataAccessService.load(fileHash)
            def slurper = new JsonSlurper()
            def content = metaData.zippedContent.unzip()
            def fileMetaData = slurper.parse(content) as FileMetaData
            def dataSet = dataAccessService.loadDataSet(fileMetaData, options)
            return classificationService.classify(options, dataSet)
        }
        return classificationHash
    }

    @RequestMapping(value='/{classificationHash}', method=RequestMethod.GET)
    ClassificationResult supportVectorMachineClassification(@PathVariable('classificationHash') String classificationHash) {
        return jobService.retrieveJobResult(classificationHash)
    }

}
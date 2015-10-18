package wekarest.controller

import groovy.json.JsonOutput
import org.springframework.web.bind.annotation.RequestBody
import wekarest.model.ClassificationOptions
import wekarest.model.ClassificationResult
import wekarest.service.ClassificationService
import wekarest.service.DataAccessService
import wekarest.service.JobService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

import static groovy.json.JsonOutput.toJson

@RestController
@RequestMapping('/classification')
class ClassificationController {

    @Autowired
    JobService jobService;

    @Autowired
    DataAccessService dataAccessService;

    @Autowired
    ClassificationService classificationService;

    @RequestMapping(value='/{hash}', method=RequestMethod.PUT)
    String createClassification(@PathVariable('hash') String fileHash,
                                   @RequestBody ClassificationOptions options) {
        def optionHash = toJson(options).asMD5()
        def jobId = "${optionHash}_${fileHash}"
        jobService.createJob(jobId) {
            def dataSet = dataAccessService.loadDataSet(fileHash)
            return classificationService.classify(options.classifier, dataSet)
        }
        return jobId
    }

    @RequestMapping(value='/{jobId}', method=RequestMethod.GET)
    ClassificationResult supportVectorMachineClassification(@PathVariable('jobId') String jobId) {
        return jobService.retrieveJobResult(jobId)
    }

}
package wekarest.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import wekarest.model.ClassificationOptions
import wekarest.model.ClassificationResult
import wekarest.service.ClassificationService
import wekarest.service.DataAccessService
import wekarest.service.JobService

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

    @RequestMapping(method=RequestMethod.POST)
    String createClassification(@RequestBody ClassificationOptions options) {
        def fileHash = options.fileHash
        def optionHash = toJson(options).asMD5()
        def classificationHash = "${optionHash}${fileHash}".asMD5()
        jobService.createJob(classificationHash) {
            def dataSet = dataAccessService.loadDataSet(fileHash)
            return classificationService.classify(options.classifier, dataSet)
        }
        return classificationHash
    }

    @RequestMapping(value='/{classificationHash}', method=RequestMethod.GET)
    ClassificationResult supportVectorMachineClassification(@PathVariable('classificationHash') String classificationHash) {
        return jobService.retrieveJobResult(classificationHash)
    }

}
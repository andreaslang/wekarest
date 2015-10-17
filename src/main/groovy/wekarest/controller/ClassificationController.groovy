package wekarest.controller

import wekarest.service.DataAccessService
import wekarest.service.JobService
import wekarest.service.LogisticRegressionService
import wekarest.service.SMOService
import groovy.util.logging.Log4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping('/classification')
class ClassificationController {

    @Autowired
    JobService jobService;

    @Autowired
    DataAccessService dataAccessService;

    @Autowired
    SMOService smoService;


    @Autowired
    LogisticRegressionService logisticRegressionService;

    @RequestMapping(value='/smo/{hash}', method=RequestMethod.GET)
    String supportVectorMachineClassification(@PathVariable('hash') String hash) {
        def jobId = "smo$hash"
        jobService.createJob(jobId) {
            def dataSet = dataAccessService.loadDataSet(hash)
            return smoService.classify(dataSet)
        }
        return jobId
    }

    @RequestMapping(value='/logistic/{hash}', method=RequestMethod.GET)
    String logisticRegression(@PathVariable('hash') String hash) {
        def jobId = "logistic$hash"
        jobService.createJob(jobId) {
            def dataSet = dataAccessService.loadDataSet(hash)
            return logisticRegressionService.classify(dataSet)
        }
        return jobId
    }

}
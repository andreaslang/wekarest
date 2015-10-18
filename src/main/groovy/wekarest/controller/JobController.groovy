package wekarest.controller

import wekarest.model.ClassificationResult
import wekarest.model.JobInfo
import wekarest.service.JobService

import groovy.util.logging.Log4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@Log4j
@RestController
@RequestMapping('/job')
class JobController {

    @Autowired
    JobService jobService;

    @RequestMapping(method=RequestMethod.GET)
    List<JobInfo> listJobs() {
        return jobService.listJobs()
    }

    @RequestMapping(value='/{id}', method=RequestMethod.GET)
    ClassificationResult retrieveJob(@PathVariable('id') String id) {
        return jobService.retrieveJobResult(id)
    }

}
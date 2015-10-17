package wekarest.service

import groovy.util.logging.Log4j
import wekarest.model.ClassificationResult
import org.springframework.stereotype.Service
import wekarest.model.JobInfo

import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future

import static wekarest.model.JobInfo.JobStatus.*

@Service
@Log4j
class JobService {

    private static final STORE_LOCK = new Object()

    def service = Executors.newFixedThreadPool(4)
    def store = [:] as Map<String, Map>

    List<JobInfo> listJobs() {
        def jobs
        synchronized (STORE_LOCK) {
            jobs = store.values().jobInfo
        }
        return jobs
    }

    ClassificationResult retrieveJobResult(String name) {
        def future
        synchronized (STORE_LOCK) {
            if (store.containsKey(name))
                future = store[name].future
            else
                future = { new ClassificationResult(details: 'n/a', summary: 'n/a') } as Future<ClassificationResult>
        }
        return future.get()
    }

    void createJob(String name, Closure classificationJob) {
        log.error("Creating classification job $name")
        synchronized (STORE_LOCK) {
            if (!store.containsKey(name)) {
                def jobInfo = new JobInfo(status: RUNNING, started: new Date())
                def future = service.submit(createJobCallable(jobInfo, classificationJob))
                store[name] = [future: future, jobInfo: jobInfo]
            } else {
                def job = store[name]
                def info = job.jobInfo
                if (info.status != SUCCESS) {
                    store.remove(name)
                    createJob(name, classificationJob)
                }
            }
        }
    }

    private static Callable<ClassificationResult> createJobCallable(JobInfo info, Closure classificationJob) {
        return { ->
            def result
            try {
                result = classificationJob()
                info.status = SUCCESS
                info.finished = new Date()
            } catch (Exception ex) {
                info.status = FAILURE
                result = new ClassificationResult(details: ex.getMessage(), summary: 'Classification failed')
            }
            return result
        } as Callable<ClassificationResult>
    }

}
